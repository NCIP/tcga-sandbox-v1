/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AlteredFileListCreator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This validates a manifest file.  Given an Archive, the manifest is found and parsed, and the contents checked.
 * If the manifest exists, parses correctly, and all files listed in it are found and match their MD5s, then
 * the validation will succeed.
 * <p/>
 * If the ArchiveQueries object is set in this class, the previous version of the archive will be checked for any
 * missing files.  If the ArchiveQueries object is not set, the validator is assumed to be running standalone, and
 * missing files are reported as a warning, not an error.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ManifestValidator extends AbstractProcessor<Archive, Boolean> {

    public static final String MANIFEST_FILE = "MANIFEST.txt";
    private final ManifestParser manifestParser;
    private ArchiveQueries archiveQueries;
    private boolean doMd5Check = true;

    public ManifestValidator( final ManifestParser manifestParser ) {
        this.manifestParser = manifestParser;
    }

    /**
     * Sets flag to enable or disable MD5 check.  Used by Soundcheck when in bypass mode.  Default is true.
     *
     * @param check true if MD5s should be checked
     */
    public void setDoMd5Check( final boolean check ) {
        doMd5Check = check;
    }

    protected Boolean doWork( final Archive archive, final QcContext context ) throws ProcessorException {
        context.setArchive( archive );
        boolean valid = true;
        // 1. make sure manifest is there
        final File manifest = new File( archive.getDeployDirectory(), MANIFEST_FILE );
        if(!manifest.exists()) {
            archive.setDeployStatus( Archive.STATUS_INVALID );
            throw new ProcessorException( new StringBuilder().append( "Archive is missing its " ).append( MANIFEST_FILE ).append( " file" ).toString() );
        }
        // 2. get list of files in manifest with their MD5 checksums
        try {
            final Map<String, String> manifestEntries = manifestParser.parseManifest( manifest );
            final List<String> missingFiles = new ArrayList<String>();
            // 3. now for each file listed in the manifest, check for it in the archive's directory.
            for(final String filename : manifestEntries.keySet()) {
                // if found, verify the md5.
                final File file = new File( archive.getDeployDirectory(), filename );
                if(file.exists()) {
                    // don't check the manifest's MD5 if it is listed
                    if(!file.getName().equals( MANIFEST_FILE )) {
                        final String md5 = MD5Validator.getFileMD5( file );
                        if(doMd5Check && !md5.equals( manifestEntries.get( filename ) )) {
                            context.addError(MessageFormat.format(
                            		MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
                            		archive, 
                            		"The MD5 listed in the manifest for " + filename + " does not match the actual MD5 for the file"));
                            valid = false;
                        }
                    }
                } else {
                    missingFiles.add( filename );
                }
            }
            // look for missing files in previous archive
            if(missingFiles.size() > 0) {
                if(archiveQueries != null) {
                    // if there are missing files, need to look in previous (latest) version of this archive
                    final Archive latestArchive = archiveQueries.getLatestVersionArchive( archive );
                    Map<String, String> alteredFileMd5s = AlteredFileListCreator.getAlteredFileListMap(latestArchive);
                    final Iterator<String> it = missingFiles.iterator();
                    while(it.hasNext()) {
                        final String filename = it.next();
                        if(latestArchive != null) {
                            final File theoreticalFile = new File( latestArchive.getDeployDirectory(), filename );
                            if(theoreticalFile.exists()) {
                                final String md5 = MD5Validator.getFileMD5( theoreticalFile );
                                // remove from "missing" list if they exist in previous version of archive and md5 matches
                                // or if md5 in manifest matches original md5 in altered file list
                                if(!doMd5Check || md5.equals( manifestEntries.get( filename ) ) ||
                                        manifestEntries.get( filename ).equals( alteredFileMd5s.get(filename))) {                                    
                                    it.remove();
                                    // if this file was in altered file list in previous archive, add it this time as well
                                    if (alteredFileMd5s.containsKey(filename)) {
                                        context.getAlteredFiles().put(filename,
                                                new String[]{alteredFileMd5s.get(filename),
                                                        "Was altered during processing of archive " + latestArchive.getRealName()});
                                    }
                                } else {
                                	context.addError(MessageFormat.format(
                                			MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                                			archive, 
                                			"The MD5 listed in the manifest for " + filename + " does not match the MD5 of the same file in the previous archive: " 
                                			+ latestArchive.getRealName()));
                                    valid = false;
                                }
                            } else {
                                context.addError(MessageFormat.format(
                                		MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
                                		archive, 
                                		"The file " + filename + " listed in the manifest is not present in the archive or in the previous version of the archive"));
                                valid = false;
                            }
                        } else {
                            // there is no previous version, so everything should be in here
                        	context.addError(MessageFormat.format(
                            		MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
                            		archive, 
                            		"The file " + filename + " listed in the manifest is not present in the archive"));
                            valid = false;
                        }
                    }
                } else {
                    // if archiveQueries is null, assume that means only local files should be checked, so only add warning
                    context.addWarning( new StringBuilder().append( "The following files listed in the manifest are not present in the archive.  If these files do not " ).
                            append( "exist in the previous version of the archive, DCC processing will fail. " ).
                            append( missingFiles ).toString() );
                    // clear missing list, so it won't trigger errors
                    missingFiles.clear();
                }
            }
            // 3. Check for files that are in the archive but not listed in the manifest!
            File[] archiveFiles = new File( archive.getDeployDirectory() ).listFiles();
            for(final File file : archiveFiles) {
                // skip names that start with "." as well as the manifest itself
                if(!file.getName().startsWith( "." ) && !file.getName().equals( MANIFEST_FILE )) {
                    if(manifestEntries.get( file.getName() ) == null) {
                    	context.addError(MessageFormat.format(
                        		MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
                        		archive, 
                        		"File '" + file.getName() + "' is present in the archive but is not listed in the manifest"));
                        valid = false;
                    }
                }
            }
            if(!valid) {
                // throw an exception if we found any errors
                archive.setDeployStatus( Archive.STATUS_INVALID );
                throw new ProcessorException( "" );
            }
            return valid;
        }
        catch(IOException e) {
            archive.setDeployStatus( Archive.STATUS_INVALID );
            throw new ProcessorException( new StringBuilder().append( "There was an error reading the manifest file: " ).append( e.getMessage() ).toString(), e );
        }
        catch(ParseException e) {
            archive.setDeployStatus( Archive.STATUS_INVALID );
            throw new ProcessorException( e.getMessage() );
        }
        catch(NoSuchAlgorithmException e) {
            archive.setDeployStatus( Archive.STATUS_INVALID );
            throw new ProcessorException( new StringBuilder().append( "There was an error getting the MD5 value for a file: " ).append( e.getMessage() ).toString(), e );
        }
    }


    public String getName() {
        return "archive manifest validation";
    }

    public void setArchiveQueries( final ArchiveQueries archiveQueries ) {
        this.archiveQueries = archiveQueries;
    }
}
