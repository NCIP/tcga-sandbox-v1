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

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * This class checks an uploaded archive for certain basic things.  After it has run successfully, the archive
 * will be represented in the database with a deploy_status of "Uploaded".
 * <p/>
 * The UploadChecker should be configured with the following steps:
 * inputValidator = MD5Validator
 * outputValidators = ArchiveNameValidator, (and if running with database: ArchiveTypeValidator, DomainNameValidator, PlatformValidator)
 * postSteps = ArchiveExpander, ArchiveSaver
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class UploadChecker extends AbstractProcessor<File, Archive> {

    /**
     * @param archiveFile the archive's file
     * @return the Archive object
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an error
     */
    protected Archive doWork( final File archiveFile, final QcContext context ) throws ProcessorException {
        if(!archiveFile.exists()) {
            throw new ProcessorException( new StringBuilder().append( "Archive file '" ).append( archiveFile.getName() ).append( "' could not be found" ).toString() );
        }
        if(!archiveFile.canRead()) {
            throw new ProcessorException( new StringBuilder().append( "Archive file '" ).append( archiveFile.getName() ).append( "' is not readable" ).toString() );
        }
        final Archive archive = makeArchive( archiveFile );
        context.setArchive(archive);
        try {
            // for now, deploy location is the uploaded location.  once deployment happens this will change
            // to the final FTP site location
            archive.setDeployLocation( archiveFile.getCanonicalPath() );
            archive.setDepositLocation( archive.getDeployLocation() );
            // set status to Uploaded, and date added to now
            // these will both be updated by later action
            archive.setDeployStatus( Archive.STATUS_UPLOADED );
            archive.setDateAdded( new Date( System.currentTimeMillis() ) );
        }
        catch(IOException e) {
            archive.setDeployStatus( Archive.STATUS_INVALID );
            throw new ProcessorException( new StringBuilder().append( "I/O error getting path of uploaded archive '" ).append( archiveFile.getName() ).append( "': " ).append( e.getMessage() ).toString(), e );
        }
        return archive;
    }

    @Override
    protected String getSuccessEmailBody(File input) {
        return "Archive " + input.getName() + " was uploaded successfully.  MD5 and archive name checks passed.  You will receive another email when processing is complete.";
    }

    public String getName() {
        return "upload checker";
    }

    public String getDescription( File file ) {
        return new StringBuilder().append( file.getName() ).append( " upload check" ).toString();
    }

    protected Archive makeArchive( final File file ) throws ProcessorException {
        final Archive archive = new Archive();
        archive.setArchiveFile( file );
        return archive;
    }
}
