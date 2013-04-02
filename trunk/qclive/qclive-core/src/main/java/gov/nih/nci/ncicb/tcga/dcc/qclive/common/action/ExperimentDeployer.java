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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.util.List;

/**
 * Deploys all valid archives for an experiment.
 * <p/>
 * List-step: ArchiveDeployer
 * <p/>
 * Post-step:  ExperimentSaver
 *
 * @author Jessica Chen
 *         Last updated by: $Author: chenjw $
 * @version $Rev: 3441 $
 */
public class ExperimentDeployer extends AbstractListProcessor<Experiment, List<Archive>, Archive, Archive> {

    public List<Archive> getWorkList(final Experiment experiment, final QcContext context) throws ProcessorException {
        context.setExperiment(experiment);
        final List<Archive> archives = experiment.getArchivesForStatus(Archive.STATUS_VALIDATED);
        context.setArchivesToBeProcessedInTheExperiment(archives);
        // move meta-data archive, if any, to end of list, because it needs to be deployed last
        Archive metaDataArchive = null;
        for (Archive archive : archives) {
            if (Archive.TYPE_MAGE_TAB.equals(archive.getArchiveType())) {
                metaDataArchive = archive;
            }
        }
        if (metaDataArchive != null) {
            archives.remove(metaDataArchive);
            archives.add(metaDataArchive);
        }
        return archives;
    }

    @Override
    protected void updateItemWorkStatus(final Archive archive, final QcContext context){
        if(context.getErrorsByArchiveName(archive).size()> 0){
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
        }

    }

    // archiveList is list from getWorkList, which is validated archives that were run through archive deployer

    protected List<Archive> afterWork(final Experiment experiment, final List<Archive> archiveList,
                                      final QcContext context) throws ProcessorException {
        experiment.setStatus(Experiment.STATUS_DEPLOYED);
        for (final Archive archive : archiveList) {
            if (!archive.getDeployStatus().equals(Archive.STATUS_DEPLOYED)) {
                // if any archives did not deploy, set experiment status to failed
                experiment.setStatus(Experiment.STATUS_FAILED);
            }
        }
        // if any failed, need to set all to In Review
        if (experiment.getStatus().equals(Experiment.STATUS_FAILED)) {
            for (final Archive archive : archiveList) {
                // set all archives that were just deployed to In Review because there was a problem
                archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            }
            context.addError(MessageFormat.format(
        			MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
        			experiment.getName(),
        			new StringBuilder().append("Deployment of one or more archives in failed. Setting all to 'In Review'").toString()));
        }
        return archiveList;
    }

    public String getName() {
        return "experiment archive deployer";
    }
}
