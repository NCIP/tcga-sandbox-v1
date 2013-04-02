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
import gov.nih.nci.ncicb.tcga.dcc.common.util.DataLevel;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MiRNASeqDataFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqDataFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentQueries;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Experiment checker.  Input is the name of the experiment (domain_disease.platform) and the output is the Experiment
 * object.  The experiment status will indicate whether the check passed or not.  A status of "Checked" means it is
 * good to go, while "Pending" means we need to wait longer, and "Failed" means there was an error so it has failed.
 * <p/>
 * Post-steps:
 * CgccExperimentChecker
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ExperimentChecker extends AbstractProcessor<String, Experiment> {

    private ExperimentQueries experimentQueries;
    private int timeLimitInHours = 24;  // this is default, can also set via setter / injection
    private final ExperimentStatusSetter experimentStatusSetter;
    private String firstGenSequencingPlatform;

    /**
     * Names of the platforms that are known to be for Level 1 data only
     */
    private static final List<String> LEVEL_1_ONLY_PLATFORM_NAMES = Arrays.asList("bio", "diagnostic_images", "pathology_reports", "tissue_images");

    public ExperimentChecker() {
        this.experimentStatusSetter = new ExperimentStatusSetter();
    }

    /*
     * This is overridden to run the experiment status setter last.
     */
    protected void runPostProcessors( final Experiment experiment, final QcContext context ) throws ProcessorException {
        super.runPostProcessors( experiment, context );
        experimentStatusSetter.execute( experiment, context );
    }

    /**
     * This does the main work of the step.
     *
     * @param experimentName the name of the experiment to check
     * @return the Experiment object for the given name, or null if the check failed.
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there was an error during checking.
     */
    protected Experiment doWork( final String experimentName, final QcContext context ) throws ProcessorException {
        // 1. get experiment.  should throw exception if something goes wrong.
        final Experiment experiment;
        if(context.getExperimentArchiveNameFilter() != null) {
            experiment = experimentQueries.getExperimentForSingleArchive(context.getExperimentArchiveNameFilter());
        } else {
            experiment = experimentQueries.getExperiment(experimentName);
        }

        if(experiment == null) {
            throw new ProcessorException( new StringBuilder().append( "Could not find experiment '" ).append( experimentName ).append( "'" ).toString() );
        } else {
            context.setExperiment( experiment );
            context.setCenterName( experiment.getCenterName() );
        }
        // make sure there is at least 1 uploaded archive -- if not, just bail
        if (experiment.getArchivesForStatus( Archive.STATUS_UPLOADED).size() == 0) {
            experiment.setStatus(Experiment.STATUS_UP_TO_DATE);
            throw new ProcessorException(new StringBuilder().append("Experiment '").append(experimentName).append("' does not have any uploaded archives to process").toString());
        }

        final String platformName = experiment.getPlatformName();

        if(LEVEL_1_ONLY_PLATFORM_NAMES.contains(platformName)) {

            // Expecting Level 1 data only
            for(final Archive archive : experiment.getArchives()) {

                final Integer archiveLevel = archive.getDataLevel();

                if(archiveLevel != DataLevel.Level1.getLevel()) {

                    experiment.setStatus( Experiment.STATUS_FAILED );
                    throw new ProcessorException(new StringBuilder("The DCC is only accepting level 1 for ")
                            .append(platformName)
                            .append(" archives, but found level ")
                            .append(archiveLevel)
                            .toString());
                }
            }

        } else {

            // check that if there is a level X archive, there is also a level X-1 archive, if X > 1.
            final Map<Integer, Boolean> levels = new HashMap<Integer, Boolean>();
            for(final Archive archive : experiment.getArchives()) {
                if(archive.getDataLevel() != null) {
                    final String archivePlatform = archive.getPlatform();
                    final Integer archiveLevel = archive.getDataLevel();

                    if (archivePlatform.contains(MiRNASeqDataFileValidator.MIRNASEQ) && (archiveLevel == 1 || archiveLevel == 2)) {
                        // set to failed because this isn't recoverable
                        experiment.setStatus( Experiment.STATUS_FAILED );
                        throw new ProcessorException("The DCC is not yet accepting level 1 or 2 " + platformName + " archives");
                    } else if(archivePlatform.contains(RNASeqDataFileValidator.RNASEQ) && archiveLevel == 1) {
                        // set to failed because this isn't recoverable
                        experiment.setStatus( Experiment.STATUS_FAILED );
                        throw new ProcessorException("The DCC is not yet accepting level 1 " + platformName + " archives");
                    }

                    levels.put( archive.getDataLevel(), true );
                }
            }

            for(final Integer level : levels.keySet()) {
                if(level > 1) {
                    if(levels.get( level - 1 ) == null) {
                        if (levelIsRequired(level - 1, experiment, context)) {
                            context.addError(MessageFormat.format(
                                    MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
                                    experiment,
                                    new StringBuilder().append("Contains a level ").append(String.valueOf(level)).
                                            append(" archive but no level ").append(String.valueOf(level - 1)).append(" archive").toString()));
                        }
                    }
                }
            }
        }

        return experiment;
    }

    private boolean levelIsRequired(final int level, final Experiment experiment, final QcContext context) {
        boolean isRequired = true;
        if (experiment.getType().equals(Experiment.TYPE_GDAC)) {
            // no level is required for GDAC experiments
            return false;
        } else if (experiment.getType().equals(Experiment.TYPE_GSC) && !experiment.getPlatformName().equals(firstGenSequencingPlatform) && level == 1) {
            context.addWarning("Please make sure your Level 1 data has been submitted to the appropriate repository.");
            isRequired = false;
        } else if (experiment.getPlatformName().contains(MiRNASeqDataFileValidator.MIRNASEQ)) {
            // miRNASeq we only expect level 3, so this is ok
            isRequired = false;
        } else if (experiment.getPlatformName().contains(RNASeqDataFileValidator.RNASEQ)) {
            // RNASeq we only expect level 3, so this is ok
            isRequired = false;
        } else if (experiment.getPlatformName().equals("IlluminaHiSeq_DNASeqC")) {
            isRequired = false;
        }

        return isRequired;
    }

    private void notComplete( final Experiment experiment, final QcContext context ) {
        // check if the initial date is more than MAX TIME ago
        final Calendar deadline = Calendar.getInstance();
        deadline.setTime( experiment.getUploadStartDate() );
        deadline.add( Calendar.HOUR_OF_DAY, getTimeLimitInHours() );
        final Calendar now = Calendar.getInstance();
        if(now.after( deadline )) {
            experiment.setStatus( Experiment.STATUS_FAILED );
            context.addError(MessageFormat.format(
            		MessagePropertyType.EXPERIMENT_PROCESSING_ERROR, 
            		experiment, 
            		new StringBuilder().append("Experiment archives were not all uploaded within ").append(getTimeLimitInHours()).append(" hours of first upload (").
            		append(experiment.getUploadStartDate()).append("), ").append("so the processing of new archives for ").
            		append(experiment.getName()).append(" has failed").toString()));
        } else {
            // whatever called this step will know what to do about this
            SimpleDateFormat sdf = new SimpleDateFormat( "EEE, d MMM yyyy hh:mm aa z" );
            context.addError(MessageFormat.format(
            		MessagePropertyType.EXPERIMENT_PROCESSING_ERROR, 
            		experiment, 
            		new StringBuilder().append("All missing archives must be uploaded by ").append(sdf.format(deadline.getTime())).append(" or processing will fail.").toString()));
            experiment.setStatus( Experiment.STATUS_PENDING );
        }
    }

    public String getName() {
        return "experiment checker";
    }

    public void setExperimentQueries( final ExperimentQueries experimentQueries ) {
        this.experimentQueries = experimentQueries;
    }

    public int getTimeLimitInHours() {
        return timeLimitInHours;
    }

    public void setTimeLimitInHours( final int timeLimitInHours ) {
        this.timeLimitInHours = timeLimitInHours;
    }

    /**
     * Sets the name of the platform that is "first gen" (not next gen).
     * @param firstGenSequencingPlatform platform name, such as ABI
     */
    public void setFirstGenSequencingPlatform(final String firstGenSequencingPlatform) {
        this.firstGenSequencingPlatform = firstGenSequencingPlatform;
    }

    /**
     * Private inner class for setting status of experiment.  Is called after all other post-processing steps.
     */
    private class ExperimentStatusSetter extends AbstractProcessor<Experiment, Experiment> {

        protected Experiment doWork( final Experiment experiment, final QcContext context ) throws ProcessorException {
            if(context.getErrorCount() > 0) {
                notComplete( experiment, context );
            } else {
                // all checks passed, the experiment is complete
                experiment.setStatus( Experiment.STATUS_CHECKED );
            }
            return experiment;
        }

        public String getName() {
            return "experiment checker";
        }
    }
}
