/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ArchiveTypeQueries;

/**
 * Validates the archive type based on the available options in the database.  Also sets the archive's data level using
 * the database value for the archive type, if it is found.
 *
 * @author Jessica Chen Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ArchiveTypeValidator extends AbstractProcessor<Archive, Boolean> {

    private ArchiveTypeQueries archiveTypeQueries;

    /**
     * Validates the archive type.  Checks that the type is valid according to the DCC database.
     *
     * @param archive the archive given as input
     * @param context the qc context
     *
     * @return if the archive type is valid or not
     *
     * @throws ProcessorException if there is an unrecoverable error
     */
    protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {
        context.setArchive(archive);
        final boolean isValid = archiveTypeQueries.isValidArchiveType(archive.getArchiveType());
        if (!isValid) {
            archive.setDeployStatus(Archive.STATUS_INVALID);
            context.addError(MessageFormat.format(
            		MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                    archive,
                    "Archive type '" + archive.getArchiveType() + "' is not valid"));
        } else {
            // get the data level for this archive type
            final Integer level = archiveTypeQueries.getArchiveTypeDataLevel(archive.getArchiveType());
            if (level != null) {
                archive.setDataLevel(level);
            } else {
                archive.setDataLevel(ConstantValues.DATALEVEL_UNKNOWN);
            }
        }
        return isValid;
    }

    /** @return descriptive name for this validator */
    public String getName() {
        return "archive type validation";
    }

    /**
     * Simple setter.
     *
     * @param archiveTypeQueries the ArchiveTypeQueries object to use for validation
     */
    public void setArchiveTypeQueries(final ArchiveTypeQueries archiveTypeQueries) {
        this.archiveTypeQueries = archiveTypeQueries;
    }
}
