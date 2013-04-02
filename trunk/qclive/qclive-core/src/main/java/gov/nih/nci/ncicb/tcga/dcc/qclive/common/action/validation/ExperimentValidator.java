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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractListProcessor;

import java.util.List;

/**
 * Validates all uploaded archives for an experiment.  Main function is to run all listProcessors (which should do
 * whatever validations are necessary for the experiment; some possible validators are listed below) and then to check
 * if all archives validated, and set the status accordingly.
 *
 * Possible configuration:
 *
 * Input validators:
 * BcrExperimentValidator
 * CgccExperimentValidator
 *
 * List-steps:
 * - ManifestValidator
 * - SdrfValidator
 * - IdfValidator
 *
 * - MafFileValidator
 * - TraceFileValidator
 * - ClinicalXmlValidator
 * - ArchiveSaver
 *
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ExperimentValidator extends AbstractListProcessor<Experiment, Boolean, Archive, Boolean> {

    private ArchiveQueries archiveQueries;

    public List<Archive> getWorkList( final Experiment experiment, final QcContext context ) throws ProcessorException {
        context.setExperiment( experiment );
        return experiment.getArchivesForStatus( Archive.STATUS_UPLOADED );
    }

    @Override
    protected void updateItemWorkStatus(final Archive archive, final QcContext context){
        if(context.getErrorsByArchiveName(archive).size()> 0){
            archive.setDeployStatus(Archive.STATUS_INVALID);
        }

    }

    protected Boolean afterWork( final Experiment experiment, final List<Boolean> archiveValidatorOutputs,
                                 final QcContext context ) throws ProcessorException {
        // for all archives in the experiment...
        for(final Archive archive : experiment.getArchives()) {
            // if the archive has been set to invalid by one of the validators
            if(archive.getDeployStatus().equals( Archive.STATUS_INVALID )) {
                // then set the experiment to failed
                experiment.setStatus( Experiment.STATUS_FAILED );
            } else if(archive.getDeployStatus().equals( Archive.STATUS_UPLOADED )) {
                // otherwise, the archive passed all validations, so set it to Valid
                archive.setDeployStatus( Archive.STATUS_VALIDATED );
                // if have DAO handle, update the status in the DB
                if(archiveQueries != null) {
                    archiveQueries.updateArchiveStatus( archive );
                }
            }
        }
        if(!Experiment.STATUS_FAILED.equals( experiment.getStatus() ) && !archiveValidatorOutputs.contains( false )) {
            experiment.setStatus( Experiment.STATUS_VALID );
            return true;
        } else {
            experiment.setStatus( Experiment.STATUS_FAILED );
            return false;
        }
    }

    public String getName() {
        return "experiment validation";
    }

    public String getDescription( final Experiment e ) {
        return new StringBuilder().append( "validation of " ).append( e.getName() ).append( " archives" ).toString();
    }

    public void setArchiveQueries( final ArchiveQueries archiveQueries ) {
        this.archiveQueries = archiveQueries;
    }
}
