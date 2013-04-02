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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;

import java.io.File;

/**
 * Validate a BCR experiment.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class BcrExperimentValidator extends AbstractProcessor<Experiment, Boolean> {
    private String clinicalPlatform;

    /**
     * Checks BCR archives of platform type "bio" to verify XML files against the XSD.
     *
     * @param experiment the experiment to validate
     * @param context the context for this QC call
     * @return true if the experiment is valid, false if not
     * @throws ProcessorException if the validation cannot complete
     */
    protected Boolean doWork( final Experiment experiment, final QcContext context ) throws ProcessorException {

        context.setExperiment( experiment );
        if(!Experiment.TYPE_BCR.equals( experiment.getType() )) {
            // only checks BCR archives
            return true;
        }
        boolean passed = true;
        // if platform is "bio" then check for XSDs
        if(experiment.getPlatformName().equals(clinicalPlatform)) {
            for(final Archive archive : experiment.getArchivesForStatus(Archive.STATUS_UPLOADED)) {
                context.setArchive( archive );
                context.setArchiveInProgress(archive);
                final File[] xsdFiles = DirectoryListerImpl.getFilesByExtension( archive.getDeployDirectory(), ClinicalXmlValidator.XSD_EXTENSION );
                if(xsdFiles != null && xsdFiles.length != 0) {
                    context.addError(MessageFormat.format(
                                MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
                                experiment,
                                new StringBuilder().append("Archive '").append(archive.getRealName()).append("' contains local XSD file(s)").toString()));

                    passed = false;
                }
                if(context.getErrorsByArchiveName(archive).size() > 0){
                    archive.setDeployStatus(Archive.STATUS_INVALID);
                }
            }
        }
        return passed;
    }

    /**
     * @return descriptive name for this validator
     */
    public String getName() {
        return "BCR experiment validation";
    }

    public void setClinicalPlatform(final String clinicalPlatform) {
        this.clinicalPlatform = clinicalPlatform;
    }
}
