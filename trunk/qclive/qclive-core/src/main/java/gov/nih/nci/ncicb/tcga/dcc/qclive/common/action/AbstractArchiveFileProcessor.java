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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Abstract handler that does something to files of a certain type in an archive.
 * Output type is generic.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public abstract class AbstractArchiveFileProcessor<O> extends AbstractProcessor<Archive, O> {

    protected O doWork( final Archive archive, final QcContext context ) throws ProcessorException {
        context.setArchive( archive );
        
        if(isCorrectArchiveType( archive )) {
            final File[] files = getFilesForExtension(archive);
            final Map<File, O> results = new HashMap<File, O>();
            
            if (files != null && files.length > 0){
	            for(final File file : files) {
	                final O result = processFile( file, context );
	                results.put( file, result );
	            }
	            return getReturnValue( results, context );
            }else{
            	return getDefaultReturnValue( archive );
            }
        } else {
            return getDefaultReturnValue( archive );
        }
    }

    /**
     * Separate method so tests can override to not need to interact with filesystem.
     * If there are multiple file extensions given to the the method, call  getFilesByPattern
     * which takes a wildcard pattern
     *
     * @param archive the archive
     * @return an array of file objects
     */
    protected File[] getFilesForExtension(Archive archive) {
    	
    	File [] returnFileList = null;    	
    	String extensionList = getFileExtension();
    	if (StringUtils.isNotEmpty(extensionList)){
    		if (extensionList.contains(",")){
    			returnFileList = DirectoryListerImpl.getFilesByPattern(archive.getDeployDirectory(),
    					Arrays.asList(extensionList.split(",")));
    		}else{
    			returnFileList = DirectoryListerImpl.getFilesByExtension( archive.getDeployDirectory(), getFileExtension() );
    		}
    	}    	    	
    	return returnFileList;
    }

    /**
     * Figures out what to return from doWork.
     *
     * @param results the results of each processFile call.  Map key = File and value = return from processFile
     * @param context the qc context
     * @return the value to return from the complete processing call
     */
    protected abstract O getReturnValue( Map<File, O> results, QcContext context );

    /**
     * Does the work of whatever the class does on this file.
     *
     * @param file    the file to process
     * @param context the qc context
     * @return the result of the processing
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an error while processing the file
     */
    protected abstract O processFile( File file, QcContext context ) throws ProcessorException;

    /**
     * Figures out what to return if this is the wrong kind of archive for this processing.
     *
     * @param archive the input archive
     * @return the return value to return from doWork if this is the wrong archive type
     */
    protected abstract O getDefaultReturnValue( Archive archive );

    /**
     * @return the file extension of the files this class processes
     */
    protected abstract String getFileExtension();

    /**
     * @param archive the input archive
     * @return true if this processor can process this archive, false if not
     * @throws ProcessorException if the archive type cannot be determined or there is some other error
     */
    protected abstract boolean isCorrectArchiveType( Archive archive ) throws ProcessorException;
}
