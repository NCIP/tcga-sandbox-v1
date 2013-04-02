/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calls the correct MafFileValidator based on the version information in the file.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public abstract class AbstractMafFileVersionDispatcher<O> extends AbstractArchiveFileProcessor<O> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("#version (.+)$");
    private String defaultSpecVersion;
    private Map<String, Processor<File, O>> mafHandlers = new HashMap<String, Processor<File, O>>();


    @Override
    protected O processFile(final File file, final QcContext context) throws ProcessorException {
        Processor<File, O> mafHandler = mafHandlers.get(defaultSpecVersion);
        
        // read first line of file, look for #version
        BufferedReader in = null;
        String firstLine = null;
        try {
            in = new BufferedReader( new FileReader( file ) );
            firstLine = in.readLine();
            Matcher versionMatcher = VERSION_PATTERN.matcher(firstLine);
            if(versionMatcher.matches()) {
                String version = versionMatcher.group(1);
                mafHandler = mafHandlers.get(version);
                if(mafHandler == null) {
                    throw new ProcessorException("MAF spec version '" + version + "' is not supported");
                }
            } else if (firstLine.startsWith("#")) {
                throw new ProcessorException("MAF spec version must be specified in the first line of the file with the format '#version X' where X is the version designation");
            } else {
                context.addWarning(new StringBuilder().append("No MAF version header found; validating against ").append(defaultSpecVersion).append(" spec").toString());
            }
        } 
        catch (FileNotFoundException e) {
            throw new ProcessorException(e.getMessage(), e);
        } 
        catch (IOException e) {
            throw new ProcessorException(e.getMessage(), e);
        }
        finally {
        	if(in != null) {
        		try {
					in.close();
				} 
        		catch (IOException e) {
        			throw new ProcessorException("Unable to close file reader for file '" + file.getName() + "'");
				}
        	}
        }

        return mafHandler.execute(file, context);
    }

    @Override
    protected String getFileExtension() {
        return AbstractMafFileHandler.MAF_EXTENSION;
    }

    @Override
     protected boolean isCorrectArchiveType(final Archive archive) throws ProcessorException {
        if (Experiment.TYPE_GSC.equals(archive.getExperimentType())) {
            boolean hasMafs = DirectoryListerImpl.getFilesByExtension(archive.getDeployDirectory(), getFileExtension()).length > 0;
            if (Archive.TYPE_LEVEL_2.equals(archive.getArchiveType()) || Archive.TYPE_LEVEL_3.equals(archive.getArchiveType())) {
                return true; // GSC level 2 or 3
            } else if (hasMafs) {
                // not GSC level 2 or 3 but maf files found... this is an error
                throw new ProcessorException(new StringBuilder().append("Only ").append(Archive.TYPE_LEVEL_2).
                        append(" and ").append(Archive.TYPE_LEVEL_3).append(" ").append(Experiment.TYPE_GSC).
                        append(" archives may contain ").
                        append(AbstractMafFileHandler.MAF_EXTENSION).append(" files").toString());
            }
        }
        return false;
    }    

    public void addMafHandler(final Processor<File, O> validator, final String version) {
        mafHandlers.put(version, validator);
    }

    public void setMafHandlers(final Map<String, Processor<File, O>> handlers) {
        this.mafHandlers = handlers;
    }

    public void setDefaultSpecVersion(final String defaultSpecVersion) {
        this.defaultSpecVersion = defaultSpecVersion;
    }
}
