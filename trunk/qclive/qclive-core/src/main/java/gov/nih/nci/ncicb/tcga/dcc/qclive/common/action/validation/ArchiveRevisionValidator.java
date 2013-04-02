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
 * Validator that makes sure the submitted archive does not have a revision that is actually
 * lower than an existing Available archive.  Fails if the archive revision is less than the
 * max available archive revision.  Ignores non-Available archives, so invalid archives can be replaced.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveRevisionValidator extends AbstractProcessor<Archive, Boolean> {
    private ArchiveQueries archiveQueries;

    @Override
    protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {        
        try {
            final Long archiveRevision = Long.valueOf(archive.getRevision());
            final Long maxRevision = archiveQueries.getMaxRevisionForArchive(archive, true);
            if (maxRevision > archiveRevision) {
            	context.addError(MessageFormat.format(
            			MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
            			archive.getArchiveFile(), 
            			"The next revision for this serial index should be " + (maxRevision + 1) + " or greater (revision " + maxRevision + " already exists)"));
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException e) {
            // this should not happen assuming the ArchiveNameValidator has been run, but just in case...
            throw new ProcessorException("Archive revision must be a number");
        }        
    }

    @Override
    public String getName() {
        return "archive revision validation";
    }

    public void setArchiveQueries(final ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }
}
