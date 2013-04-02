/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ArchiveLoader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.Live;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Enqueuer for qc live.  Handles scheduling details.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public abstract class LiveScheduler implements JobScheduler {
    // locks are static, so if there are multiple instances of LiveScheduler, they still will be synchronized
    private static final String EXPERIMENT_CHECK_LOCK = "experimentCheckSync";
    private static final String UPLOAD_CHECK_LOCK = "uploadCheckSync";
    public static final String ARCHIVE_NAME = "archiveName";
    public static final String EXPERIMENT_NAME = "experimentName";
    public static final String EXPERIMENT_TYPE = "experimentType";
    public static final String STATE_CONTEXT = "stateContext";
        
    private Scheduler scheduler;
    private Scheduler uploadScheduler;    

	private Scheduler loaderScheduler;
    private Trigger cleanupArchiveTrigger;
    private JobDetail cleanupArchiveJobDetail;
    private final Log logger = LogFactory.getLog(getClass());
    private static final Object loaderLock = new Object();
    
	/**
     * Schedules an experiment check for the given experiment, to run at the given time.
     *
     * @param experimentName the name of the experiment (center_platform.disease)
     * @param experimentType the type of the experiment (CGCC, BCR, GSC)
     * @param whenToRun      when the check should be scheduled for
     * @throws SchedulerException if there is an error scheduling the job with the Quartz scheduler
     */
    public void scheduleExperimentCheck(
            final String experimentName,
            final String experimentType,
            final String archive,
            final Calendar whenToRun,
            final QcLiveStateBean stateContext,
            final String groupName) throws SchedulerException {

        final JobDetail jobDetail = makeJobDetailForExperiment(experimentName, experimentType, archive,stateContext,groupName);        
        final Trigger jobTrigger = makeTriggerForExperiment(jobDetail);
        jobTrigger.setStartTime(whenToRun.getTime());

        logger.info("ExperimentCheckerJob for " + experimentName + "-" + archive + " scheduled. ");
        // only one thread should schedule an experiment check at a time
        synchronized (EXPERIMENT_CHECK_LOCK) {
            schedule(jobTrigger, jobDetail, scheduler);
        }
    }


    /**
     * Schedules a call to cleanup the given archive after processing is complete (be it successfully or
     * unsuccessfully.) Is synchronized because uses shared beans to do the scheduling. (Which are serialized after each
     * call to schedule, so reuse is fine.)
     *
     * @param archive       archive object
     * @param archiveFailed did the archive processing fail or not
     */
    public synchronized void scheduleArchiveCleanup(final Archive archive, final boolean archiveFailed)
            throws SchedulerException {
        final String archiveDepositLocation = archive.getDepositLocation();
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        final String name = "cleanup " + archiveDepositLocation + "-" + timeStamp;

        cleanupArchiveJobDetail.setName(name);
        cleanupArchiveJobDetail.getJobDataMap().put(ArchiveCleanupJob.ARCHIVE, archive.getArchiveBase());
        cleanupArchiveJobDetail.getJobDataMap().put(ArchiveCleanupJob.ARCHIVE_FAILED, archiveFailed);
        cleanupArchiveTrigger.setGroup(Live.QUARTZ_JOB_GROUP);
        cleanupArchiveTrigger.setJobName(name);
        cleanupArchiveTrigger.setJobGroup(Live.QUARTZ_JOB_GROUP);
        cleanupArchiveTrigger.setName(name);
        cleanupArchiveTrigger.setJobDataMap(cleanupArchiveJobDetail.getJobDataMap());
        final Calendar whenToRun = Calendar.getInstance();
        whenToRun.add(Calendar.MINUTE, 1);
        cleanupArchiveTrigger.setStartTime(whenToRun.getTime());
        cleanupArchiveTrigger.setPriority(Trigger.DEFAULT_PRIORITY);
        scheduler.scheduleJob(cleanupArchiveJobDetail, cleanupArchiveTrigger);
    }

    /**
     * Schedules the given job and trigger. If the job already exists for this name and group, delete the job and schedule new
     * job .
     *
     * @param trigger   the trigger for the job
     * @param jobDetail the job to run
     * @throws SchedulerException if the job could not be scheduled
     */
    private void schedule(final Trigger trigger, final JobDetail jobDetail, final Scheduler scheduler) throws SchedulerException {
        // check if this job is already in the system
        final JobDetail existingJobDetail = scheduler.getJobDetail(jobDetail.getName(), jobDetail.getGroup());
        if (existingJobDetail != null) {
            // add the jobdatamap from jobDetail to trigger
            trigger.setJobDataMap(jobDetail.getJobDataMap());
            // Just add a trigger
            scheduler.scheduleJob(trigger);
        } else {
            // Add both job and trigger
            scheduler.scheduleJob(jobDetail, trigger);
        }

    }

    

    /**
     * Schedules a call to check an uploaded file.
     *
     * @param file      the uploaded file
     * @param whenToRun when the check should occur
     * @param stateContext used to carry stateful information across scheduler executions
     * @throws SchedulerException if the check could not be scheduled
     * @throws IOException        if the file path cannot be determined
     */
    public void scheduleUploadCheck(final File file, final Calendar whenToRun,QcLiveStateBean stateContext) throws SchedulerException, IOException {
        scheduleUploadCheck(file.getCanonicalPath(), whenToRun, 1,stateContext);
    }

    /**
     * Re-Schedules a call to check md5 for the given file name
     *
     * @param file               uploaded file
     * @param whenToRun          when the check should occur
     * @param stateContext used to carry stateful information across scheduler executions
     * @param validationAttempts number of validation attempts already made
     * @throws SchedulerException scheduler exception
     */
    public void scheduleUploadCheck(final String file, final Calendar whenToRun,final Integer validationAttempts,QcLiveStateBean stateContext) throws SchedulerException {
        final JobDetail jobDetail = makeJobDetailForFile(file, validationAttempts,stateContext);
        final Trigger jobTrigger = makeTriggerForFile(jobDetail);
        jobTrigger.setStartTime(whenToRun.getTime());
        synchronized (UPLOAD_CHECK_LOCK) {
            schedule(jobTrigger, jobDetail, uploadScheduler);
        }
    }
  
    
    /**
     * Schedules clinical loader
     * if the jobDetail already exists for the same clinical experiment, new triggers will be created
     *
     * @param archivesToLoad archives to load
     * @param whenToRun      when the loading should occur
     * @throws SchedulerException scheduler exception
     */
    public void scheduleClinicalLoader(List<Archive> archivesToLoad, 
    		Calendar whenToRun, 
    		String experimentName,
    		QcLiveStateBean stateContext) throws SchedulerException {

        String jobName = ArchiveLoader.ArchiveLoaderType.CLINICAL_LOADER.toString() + "-" + experimentName;

        final JobDetail jobDetail = getClinicalLoadJobDetail();
        jobDetail.getJobDataMap().put("clinicalArchives", archivesToLoad);
        jobDetail.getJobDataMap().put(STATE_CONTEXT, stateContext);
        jobDetail.setName(jobName);
        jobDetail.setDescription("Loads clinical data");
        // JobGroupName to identify the clinical loader jobs belongs to the same experiment.
        // It is based on experiment name. Do not change the group name.
        jobDetail.setGroup(jobName);

        final Trigger jobTrigger = getClinicalLoaderTrigger();
        jobTrigger.setJobName(jobDetail.getName());
        jobTrigger.setStartTime(whenToRun.getTime());
        jobTrigger.setName(jobDetail.getName() + "_" + UUID.randomUUID());
        jobTrigger.setGroup(jobDetail.getGroup());
        jobTrigger.setJobGroup(jobDetail.getGroup());

        synchronized (loaderLock) {
            logger.info("Scheduled clinical loader job " + jobDetail.getGroup() + "/" + jobDetail.getName());
            schedule(jobTrigger, jobDetail, loaderScheduler);
        }
    }


    protected JobDetail makeJobDetailForFile(final String file, final Integer md5ValidationAttempts,final QcLiveStateBean stateContext) {
        final JobDetail jobDetail = getUploadJobDetail();
        jobDetail.setName(file);
        jobDetail.setDescription("Runs upload check for " + file);
        jobDetail.getJobDataMap().put("file", file);
        jobDetail.getJobDataMap().put("md5ValidationAttempts", md5ValidationAttempts);
        jobDetail.getJobDataMap().put(STATE_CONTEXT,stateContext);
        return jobDetail;
    }

    protected Trigger makeTriggerForFile(final JobDetail jobDetail) {
        final SimpleTriggerBean trigger = getUploadTrigger();
        trigger.setName(getUniqueTriggerName(jobDetail.getName()));
        updateTriggerWithJobDetails(trigger, jobDetail);
        return trigger;
    }

    protected Trigger makeTriggerForExperiment(final JobDetail jobDetail) {
        final SimpleTriggerBean trigger = getExperimentTrigger();
        trigger.setName(getUniqueTriggerName(jobDetail.getName()));
        updateTriggerWithJobDetails(trigger, jobDetail);
        return trigger;
    }

    private void updateTriggerWithJobDetails(final SimpleTriggerBean trigger, final JobDetail jobDetail) {
        trigger.setJobName(jobDetail.getName());
        trigger.setJobDetail(jobDetail);
        trigger.setGroup(jobDetail.getGroup());
        trigger.setJobGroup(jobDetail.getGroup());
    }

    private String getUniqueTriggerName(final String triggerName) {
        return triggerName + "-" + UUID.randomUUID();
    }

    protected JobDetail makeJobDetailForExperiment(
            final String experimentName,
            final String experimentType,
            final String archive,
            final QcLiveStateBean stateContext,
            final String groupName) {
        final JobDetail jobDetail = getExperimentJobDetail();
        // if the experiment type is BCR then group name should be archive name
        jobDetail.setGroup(groupName);
        jobDetail.setName(experimentName);
        jobDetail.getJobDataMap().put(EXPERIMENT_NAME, experimentName);
        jobDetail.getJobDataMap().put(EXPERIMENT_TYPE, experimentType);
        jobDetail.getJobDataMap().put(ARCHIVE_NAME, archive);
        jobDetail.setDescription("Runs experiment check for " + experimentName);
        jobDetail.getJobDataMap().put(STATE_CONTEXT, stateContext);
        return jobDetail;
    }

    /**
     * Should be implemented in Spring via method injection to return new copy of trigger as setup in XML.
     *
     * @return an experiment checker trigger object
     */
    public abstract SimpleTriggerBean getExperimentTrigger();

    /**
     * Should be implemented in Spring via method injection to return new copy of trigger.
     *
     * @return an upload checker trigger object
     */
    public abstract SimpleTriggerBean getUploadTrigger();

    public void setScheduler(final Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Should be implemented in Spring via method injection.
     *
     * @return an experiment checker job detail object
     */
    public abstract JobDetail getExperimentJobDetail();

    /**
     * Should be implemented in Spring via method injection.
     *
     * @return an upload checker job detail object
     */
    public abstract JobDetail getUploadJobDetail();

    /**
     * Should be implemented in Spring via method injection.
     *
     * @return a loader job detail object
     */
    public abstract JobDetail getClinicalLoadJobDetail();
   
    /**
     * Simple trigger for loading clinical data
     *
     * @return
     */
    public abstract Trigger getClinicalLoaderTrigger();

    public void setCleanupArchiveTrigger(final Trigger cleanupArchiveTrigger) {
        this.cleanupArchiveTrigger = cleanupArchiveTrigger;
    }

    public void setCleanupArchiveJobDetail(final JobDetail cleanupArchiveJobDetail) {
        this.cleanupArchiveJobDetail = cleanupArchiveJobDetail;
    }

    public Scheduler getLoaderScheduler() {
        return loaderScheduler;
    }

    public void setLoaderScheduler(Scheduler loaderScheduler) {
        this.loaderScheduler = loaderScheduler;
    }   
    public void setUploadScheduler(Scheduler uploadScheduler) {
		this.uploadScheduler = uploadScheduler;
	}

}
