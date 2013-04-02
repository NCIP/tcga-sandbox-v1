/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.StringTokenizerThatCanReturnNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DataFile bean for autoloader.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataFile {

    public static final String FILETYPE_UNKNOWN = "--unknown--";
    private File file;
    private int level;
    private String fileType;
    private List<String> columnNames, constantNames;

    public DataFile( final File file, final String fileType, final int level) throws LoaderException {
        this.file = file;
        this.fileType = fileType;
        this.level = level;
        introspectColumnAndConstantNames();
    }

    public String getFileType() throws LoaderException {
        return fileType;
    }

    public int getLevel() throws LoaderException {
        return level;
    }

    public String getName() {
        return file.getName();
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<String> getConstantNames() {
        return constantNames;
    }

    public File getFile() {
        return file;
    }

    final void introspectColumnAndConstantNames() throws LoaderException {
        if(!file.exists()) {
            throw new LoaderException( "File does not exist: " + file.getName() );
        }
        columnNames = new ArrayList<String>();
        constantNames = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            //read first two lines
            reader = new BufferedReader( new FileReader( file ) );
            String firstLine = reader.readLine();
            String secondLine = reader.readLine();
            StringTokenizerThatCanReturnNull st1 = new StringTokenizerThatCanReturnNull( firstLine, "\t" );
            StringTokenizerThatCanReturnNull st2 = new StringTokenizerThatCanReturnNull( secondLine, "\t" );
            //skip first column - it's the CE
            st1.nextToken();
            st2.nextToken();
            while(st1.hasMoreTokens()) {
                if(!st2.hasMoreTokens()) {
                    throw new LoaderException( "File not well formed: misaligned header: " + file.getName() );
                }
                String token1 = st1.nextToken();
                String token2 = st2.nextToken();
                if(token1 == null || token1.trim().length() == 0) {
                    //no first-line header for column: must be a constant
                    constantNames.add( token2 );
                } else {
                    //check for dupe before inserting
                    boolean dupe = false;
                    for(final String col : columnNames) {
                        if(col.equals( token2 )) {
                            dupe = true;
                            break;
                        }
                    }
                    if(!dupe) {
                        columnNames.add( token2 );
                    }
                }
            }
        }
        catch(IOException e) {
            throw new LoaderException( e );
        }
        finally {
            try {
                if(reader != null) {
                    reader.close();
                }
            }
            catch(IOException e) {
            }
        }
    }

    public List<String> getHybrefs() throws LoaderException {
        //read first line of file, extract the hybrefs
        BufferedReader reader = null;
        List<String> hybrefs = new ArrayList<String>();
        try {
            reader = new BufferedReader( new FileReader( file ) );
            String line = reader.readLine();
            String[] tokens = line.split("\t");
            if (tokens.length <= 1) {
                throw new LoaderException(String.format("Data file %s not well formed: no hybridization ref", getName()));
            }
            for (int i=1; i<tokens.length; i++) {
                hybrefs.add(tokens[i]);
            }
        } catch (IOException e) {
            throw new LoaderException(e);
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException e) {}
            }
        }
        return hybrefs;
    }
}
