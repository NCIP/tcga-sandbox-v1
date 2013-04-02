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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

/**
 * Validates the archive's tumor type against the database.
 *
 * @author Jessica Chen
 *         Last updated by: nichollsmc
 * @version $Rev: 3419 $
 */
public class DiseaseTypeValidator extends AbstractProcessor<Archive, Boolean> {

    private TumorQueries diseaseQueries;

    protected Boolean doWork( final Archive archive, final QcContext context ) throws ProcessorException {
        context.setArchive( archive );
        archive.setTheTumor(diseaseQueries.getTumorForName( archive.getTumorType() ) );
        if(archive.getTheTumor() == null) {
            archive.setDeployStatus( Archive.STATUS_INVALID );
            context.addError(MessageFormat.format(
                MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archive,
                	"Disease type '" + archive.getTumorType() + "' is not in the database"));
            return false;
        }
         return true;
    }

    public String getName() {
        return "tumor type validation";
    }

    public void setDiseaseQueries( final TumorQueries diseaseQueries ) {
        this.diseaseQueries = diseaseQueries;
    }
}
