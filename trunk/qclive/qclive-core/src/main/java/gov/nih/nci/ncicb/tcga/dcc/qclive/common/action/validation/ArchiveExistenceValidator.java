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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

/**
 * Validator which checks to make sure that the archive we're about to process doesn't already exist in the database.
 * The check is done against archives whose status is Available, and for the same full version.
 *
 * The execute method will return true if the archive does not exist with status 'Available' and false if it does.
 *
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveExistenceValidator extends AbstractProcessor<Archive, Boolean> {
    
    private ArchiveQueries archiveQueries;

    public String getName() {
        return "checking for existence of an Available archive with same version in db";
    }

    public void setArchiveQueries(final ArchiveQueries queries) {
        archiveQueries = queries;
    }

    @Override
    protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {
        if (archive.getArchiveFile() == null) {
            // should not happen, but if it does, can't get the archive name so bail
            throw new ProcessorException("Archive file not specified");
        }

        boolean archiveExistsAndIsAvailable = false;
        long archiveId = archiveQueries.getArchiveIdByName(archive.getArchiveName());
        if (archiveId > 0) {
            Archive searchedArchive = archiveQueries.getArchive(archiveId);
            // if searchedArchive is null, the archive wasn't added correctly to the db -- means the datatype_to_archives
            // entry is missing, so archive is probably messed up and thus it is okay to resubmit it
            if (searchedArchive != null) {
                archiveExistsAndIsAvailable = Archive.STATUS_AVAILABLE.equals(searchedArchive.getDeployStatus());
            }
        }

        if (archiveExistsAndIsAvailable) {
        	context.addError(MessageFormat.format(
        			MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
        			archive, 
        			"Archive already exists and is available, so cannot be resubmitted."));
        }

        // validators should return true if they pass, and false if not.  in this case, an archive being available already
        // is considered a failure, so return the opposite
        return !archiveExistsAndIsAvailable;
    }
}
