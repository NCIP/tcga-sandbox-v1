/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.LoggerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.StdoutLoggerDestination;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Calls L3 loader to load L3 data into the db
 * This process takes a list of archive names to load as
 * command line arguments
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LevelThreeLoaderCaller {

    private static Logger logger = new LoggerImpl();

    static {
        logger.addDestination(new StdoutLoggerDestination());
    }

    private ApplicationContext ctx = null;
    private static LevelThreeLoader loader = null;

    /**
     * Main entry point for the loader
     *
     * @param args command line arguments to the loader
     */
    public static void main(final String[] args) {

        final LevelThreeLoaderCaller l3LoaderCaller = new LevelThreeLoaderCaller();
        try {
            l3LoaderCaller.init("application-context-level-three-standalone.xml");
        }
        catch (LoaderException e) {
            logger.log(Level.FATAL, " Error when initializing standalone level three loader. exiting.. ");
            System.exit(-1);
        }
        
        // process command line arguments
        if (args == null || args.length <= 0) {
            logger.log(Level.FATAL, " Invalid number of command line arguments. Level Three loader" +
                    " must have at least one archive name to load. ");
            System.exit(-1);
        }
        
        // proceed without else since there is a System.exit
        List<String> archivesToLoad = new ArrayList<String>();
        String exclusionFile = null;
        exclusionFile = processFileList(args, archivesToLoad);
        
        loader.setExcludedFiles(listExclusionFileNames(exclusionFile));
        
        // load archives
        for (final String archiveName : archivesToLoad) {
            try {
                loader.loadArchiveByName(archiveName);
            }
            catch (LoaderException e) {
                logger.log(Level.ERROR, " Error while attempting to load archive " + archiveName);
                logger.log(e);
            }
        }
    }
    
    /**
     * This method is used to initialize the standalone loader.
     *
     * @param applicationContextFile path to applicationContext configuration files
     * @throws LoaderException if initialization failed
     */
    public void init(String applicationContextFile) throws LoaderException {

        logger.log(Level.INFO, " initializing standalone level 3 loader");
        ctx = new ClassPathXmlApplicationContext(applicationContextFile);

        Object obj = ctx.getBean("lThreeLoader");
        if (!(obj instanceof LevelThreeLoader)) {
            throw new LoaderException("Unable to lookup level three loader in Spring configuration ");
        } 
        else {
            loader = (LevelThreeLoader) obj;
        }
        loader.setPatterns(loadPatterns(loader.getPatternFile()));
    }

    /**
     * Loads the pattern file , the pattern format should be :
     * [center]\t[platform]\t[patern1,pattern2,pattern3...]
     *
     * @param patternFilePath
     * @return a list of CenterPlatformPattern objects
     * @throws LoaderException if loading of patterns failed
     */
    protected List<CenterPlatformPattern> loadPatterns(String patternFilePath) throws LoaderException {
        List<CenterPlatformPattern> patternList = new ArrayList<CenterPlatformPattern>();
        InputStreamReader streamReader = null;
        BufferedReader reader = null;
        try {
            streamReader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(patternFilePath));
            reader = new BufferedReader(streamReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                if (st.countTokens() != 3) {
                    throw new LoaderException(" The pattern fle must contain three tokens");
                } 
                else {
                    // should be exactly three tokens
                    while (st.hasMoreTokens()) {
                        String center = st.nextToken();
                        String platform = st.nextToken();
                        String pattern = st.nextToken();

                        // more then one pattern
                        String[] patterns = pattern.split(",", -1);
                        patternList.add(new CenterPlatformPattern(center, platform, Arrays.asList(patterns)));
                    }
                }
            }
        } 
        catch (Exception e) {
            logger.log(e);
            throw new LoaderException(e);
        } 
        finally {
            //Close the BufferedReader
            try {
                if (reader != null) {
                    reader.close();
                }
                if (streamReader != null) {
                    streamReader.close();
                }
            } 
            catch (IOException ex) {
                logger.log(ex);
            }
        }
        return patternList;
    }

    /**
     * Simple helper method to retrieve the set of files to be
     * excluded from the archive load process. Main purpose of the
     * method is to avoid read from file every time an archive
     * is loaded from the argument list.
     * Made protected for unit testing
     * @param exclusionFile the filename of the file that contains a list of files to be excluded
     * @return List<String> A list of filenames that should be excluded from the load process.
     * Returns an empty list if either no file is passed
     * If an invalid file is passed system exits
     */
    protected static List<String> listExclusionFileNames(String exclusionFile) {
        boolean invalidFile = false;
        List<String> excludedFiles = new ArrayList<String>();
        if(exclusionFile != null) {
            BufferedReader br = null;
            String fileName = "";
            try {
                br = new BufferedReader(new FileReader(exclusionFile));
                while((fileName = br.readLine()) != null) {
                    excludedFiles.add(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                invalidFile = true;
            } finally {
                if(br != null) try {
                    br.close();
                } catch (IOException e) {
                    // nothing to do here
                }
            }
        }
        if(invalidFile) {
            // prefer this to system.exit() because it allows me to unit test this case
            throw new RuntimeException("Hey, if you give me a exclusion file, make sure it is valid!");
        }
        return excludedFiles;
    }

    protected static String processFileList(String[] args, List<String> archivesToLoad) {
        String exclusionFile = null;
        for(String arg : args) {
            if(arg.substring(0,2).equals("-E")) {
                exclusionFile = arg.substring(2);
            } else {
                archivesToLoad.add(arg);
            }
        }
        return exclusionFile;
    }
}
