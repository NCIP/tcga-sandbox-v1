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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.FileCopier;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

/**
 * This adds a file into an archive, for a given experiment and/or archive type.  The added file is also added
 * to the manifest list.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ArchiveFileAdder extends AbstractProcessor<Archive, Archive> {

    private final String experimentType;
    private final String archiveType;
    private final File fileToAdd;
    private ManifestParser manifestParser;

    public ArchiveFileAdder( final String experimentType, final String archiveType, final String filename ) {
        this.experimentType = experimentType;
        this.archiveType = archiveType;
        this.fileToAdd = new File( filename );
        if(!fileToAdd.exists() || !fileToAdd.canRead()) {
            throw new IllegalArgumentException( new StringBuilder().append( "The file '" ).append( filename ).append( "' does not exist or cannot be read" ).toString() );
        }
    }

    protected Archive doWork( final Archive archive, final QcContext context ) throws ProcessorException {
        context.setArchive( archive );
        // note if experimentType and archiveType are null, all archives will get the file added to them
        if(( this.experimentType == null || archive.getExperimentType().equals( this.experimentType ) ) &&
                ( this.archiveType == null || archive.getArchiveType().equals( this.archiveType ) )) {
            // copy the fileToAdd into the archive deploy directory, and add it to the manifest
            try {
                final File copiedFile = FileCopier.copy( fileToAdd, new File( archive.getDeployDirectory() ) );
                manifestParser.addFileToManifest(copiedFile, new File( archive.getDeployDirectory(), ManifestValidator.MANIFEST_FILE ));                
            }
            catch(IOException e) {
            	context.addError(MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archive, e.getMessage()));
            }
            catch(NoSuchAlgorithmException e) {
            	context.addError(MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archive, e.getMessage()));
            }
            catch(ParseException e) {
            	context.addError(MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archive, e.getMessage()));
            }
        }
        return archive;
    }

    public String getName() {
        return "archive file adder";
    }

    public void setManifestParser( final ManifestParser manifestParser ) {
        this.manifestParser = manifestParser;
    }
}
