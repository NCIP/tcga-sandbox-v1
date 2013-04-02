/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;

/**
 * Utility class to list directory contents.
 *
 * @author Rob Sfeir Last updated by: $Author$
 * @version $Rev$
 */
public class DirectoryListerImpl implements DirectoryLister {

    /**
     * Convenience method to statically get list of files in dir.
     * @param dirName the directory in which to look
     * @return array of File objects
     */
    public static File[] getFilesInDir( final String dirName ) {
        final File dir = new File( dirName );
        // It is also possible to filter the list of returned files.
        // This does not return any files that start with `.'.
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept( final File dir, final String name ) {
                return !name.startsWith( "." );
            }
        };
        return dir.listFiles( filter ) != null ? dir.listFiles( filter ) : null;
    }

    /**
     * Convenience method to statically get list of files in dir by extension.
     * @param dirName the directory in which to look
     * @param extension the extension the files must have
     * @return array of File objects
     */
    public static File[] getFilesByExtension( final String dirName, final String extension ) {
        final File dir = new File( dirName );
        // It is also possible to filter the list of returned files.
        // This does not return any files that start with `.'.
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept( final File dir, final String name ) {
                return name.toLowerCase().endsWith(extension.toLowerCase()) && !name.startsWith( "." );
            }
        };
        return dir.listFiles( filter ) != null ? dir.listFiles( filter ) : null;
    }
    
   /**
    * A method to get all files by a wildcard pattern 
    * @param dirName name of the directory to look
    * @param filePatternList a list of patterns
    * @return an arrays of files that match the patterns
    * @throws IOException 
    */
    public static File[] getFilesByPattern( final String dirName, 
    		final List<String> filePatternList) {
    	
    	File [] listOfFiles = null; 
    	
    	if (StringUtils.isNotEmpty(dirName)
    			&& filePatternList != null 
    			&& filePatternList.size() > 0){
    	
    		final File dir = new File( dirName );
    		String[] dataSetFiles = dir.list(new WildcardFileFilter(filePatternList));
    		if (dataSetFiles != null && dataSetFiles.length > 0){
    			listOfFiles = new File[dataSetFiles.length];
    			for (int i = 0 ; i < dataSetFiles.length; i ++){
    				try{
    					listOfFiles[i] = new File(dir.getCanonicalPath() + File.separator + dataSetFiles[i]);
    				}catch (IOException e){
    					// should never get here, but just in case we do , just return with null
    					// meaning that no files by pattern could be found
    					return null;
    				}
    			}
    		}
    		
    	}    	
    	return listOfFiles;
    }    
    
    /**
     * Gets a list of all hidden files in the given directory.
     *
     * @param dirName the directory
     * @return list of hidden files
     */
    public static File[] getHiddenFilesInDir(final String dirName) {
        final File dir = new File(dirName);
        return dir.listFiles(HIDDEN_FILES_FILTER);
    }

    /**
     * If a file is hidden, or starts with "." will match the filter
     */
    public final static FilenameFilter HIDDEN_FILES_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(final File file, final String name) {
            return name.startsWith(".") || file.isHidden();
        }
    };

    /**
     * Get all files in the directory except those that start with "." 
     * @param dirName the directory in which to look
     * @return array of File objects
     */
    public File[] getFilesInDirectory(final String dirName) {
        return DirectoryListerImpl.getFilesInDir(dirName);
    }

     /**
     * Get all files in the directory that end with the given extension, except those that start with "."
     * @param dirName the directory in which to look
     * @param extension the extension the files must have
     * @return array of File objects
     */
    public File[] getFilesInDirectoryByExtension(final String dirName, final String extension) {
        return DirectoryListerImpl.getFilesByExtension(dirName, extension);
    }
}
