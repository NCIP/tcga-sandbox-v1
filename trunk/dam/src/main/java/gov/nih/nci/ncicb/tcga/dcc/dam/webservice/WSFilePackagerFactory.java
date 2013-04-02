/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzQueueJobDetails;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerFactory;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.FilePackagerEnqueuer;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.JobDelegate;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.QuartzJobHistoryService;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.QuartzQueueJobDetailsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Override <code>FilePackagerFactory</code> for use by the DAM Web Service:
 *
 * The front end server (UI) queries the Quartz database to get the <code>QuartzJobHistory</code
 * instead of relying on memory, since the FilePackager job is executed by a different server on the back end.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class WSFilePackagerFactory extends FilePackagerFactory {

    protected final Log log = LogFactory.getLog(getClass());

    private Scheduler smalljobScheduler;

    private Scheduler bigjobScheduler;

    private QuartzJobHistoryService quartzJobHistoryService;

    private QuartzQueueJobDetailsService quartzQueueJobDetailsService;

    /**
     * Retrieve the <code>QuartzJobHistory</code>, from the Quartz DB, with the given <code>UUID</code> key.
     * If none is found but there is a Quartz job details (small or big) with a matching job name,
     * return a dummy <code>QuartzJobHistory</code> with Queued status instead.
     *
     * @param key the map key
     * @return the <code>QuartzJobHistory</code>, from the Quartz DB, with the given <code>UUID</code> key
     */
    @Override
    public QuartzJobHistory getQuartzJobHistory(final UUID key) {

        final QuartzJobHistory result;

        // Query [SMALL|BIG]_QUE_JOB_DETAILS
        // (It has to be done before querying QRTZ_JOB_HISTORY to make sure we are not missing it as it is deleted
        // and QRTZ_JOB_HISTORY is being written to)
        final QuartzQueueJobDetails quartzQueueJobDetails = getQuartzQueueJobDetails(key);
        if(quartzQueueJobDetails != null) {

            // Create a dummy QuartzJobHistory to return, with Accepted status
            result = new QuartzJobHistory();
            result.setJobName(key.toString());
            result.setJobGroup(FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
            result.setStatus(QuartzJobStatus.Accepted);
            result.setEstimatedUncompressedSize(quartzQueueJobDetails.getEstimatedUncompressedSize());
            result.setJobWSSubmissionDate(quartzQueueJobDetails.getJobWSSubmissionDate());

        } else {
            // If the job wasn't found in [SMALL|BIG]_QUE_JOB_DETAILS, it must be in QRTZ_JOB_HISTORY
            result = getQuartzJobHistoryService().getQuartzJobHistory(key.toString(), FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
        }

        return result;
    }

    /**
     * Return the <code>QuartzQueueJobDetails</code> with the given <code>UUID</code> as the key
     *
     * @param key the Quartz job name
     * @return the <code>QuartzQueueJobDetails</code> with the given <code>UUID</code> as the key
     */
    @Override
    public QuartzQueueJobDetails getQuartzQueueJobDetails(final UUID key) {
        return getQuartzQueueJobDetailsService().getQuartzSmallOrBigQueueJobDetails(key.toString(), FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
    }

    /**
     * Populate the livePackagers with data retrieved from Quartz Schedulers for small and big jobs
     */
    public void init() {

        restoreLivePackagersFromScheduler(getSmalljobScheduler());
        restoreLivePackagersFromScheduler(getBigjobScheduler());
        restoreLivePackagersFromQuartzJobHistory();
    }

    /**
     * restore LivePackagers with the QuartzJobHistory entries found in the DB
     */
    private void restoreLivePackagersFromQuartzJobHistory() {

        final List<QuartzJobHistory> quartzJobHistoryList = getQuartzJobHistoryService().getAllQuartzJobHistory();
        for(final QuartzJobHistory quartzJobHistory : quartzJobHistoryList) {
            putQuartzJobHistory(quartzJobHistory.getKey(), quartzJobHistory);
        }
    }

    /**
     * Restore the livePackagers with <code>FilePackagerBean</code>s retrieved from the given Quartz Scheduler
     * (that is: the jobs that are Queued or Started)
     *
     * @param jobScheduler the Quartz Scheduler to retrieve the <code>FilePackagerBean</code>s from
     */
    private void restoreLivePackagersFromScheduler(final Scheduler jobScheduler) {

        try {
            final List<String> jobNames = getJobNamesFromFilePackagerGroupNoOrphan(jobScheduler);
            JobDetail jobDetail;
            Object object;
            FilePackagerBean filePackagerBean;

            for(final String jobName : jobNames) {

                jobDetail = jobScheduler.getJobDetail(jobName, FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
                object = jobDetail.getJobDataMap().get(JobDelegate.DATA_BEAN);

                if(object instanceof FilePackagerBean) {

                    filePackagerBean = (FilePackagerBean)object;

                    //Update the livePackagers
                    putQuartzJobHistory(filePackagerBean.getKey(), filePackagerBean.getUpdatedQuartzJobHistory());
                }
            }

        } catch (final SchedulerException e) {
            log.error(new StringBuilder("Error while restoring livePackagers from scheduler: ").append(e.getMessage()), e);
        }
    }

    /**
     * Return a <code>List</code> of job names associated with the given <code>Scheduler</code> if they belong to the 'filePackager' group, excluding orphans.
     *
     * @param jobScheduler the <code>Scheduler</code>
     * @return a <code>List</code> of job names associated with the given <code>Scheduler</code> if they belong to the 'filePackager' group, excluding orphans.
     * @throws SchedulerException
     */
    private List<String> getJobNamesFromFilePackagerGroupNoOrphan(final Scheduler jobScheduler) throws SchedulerException {

        final List<String> result = new LinkedList<String>();
        final String[] jobNames = jobScheduler.getJobNames(FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);

        // Remove orphan jobs (no associated triggers - this should not happen under normal conditions)
        Trigger[] triggers = null;
        for(final String jobName : jobNames) {

            triggers = jobScheduler.getTriggersOfJob(jobName, FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
            if(triggers.length > 0) { // trigger(s) found
                result.add(jobName);
            }
        }

        return result;
    }

    /*
     * Getter / Setter
     */

    public Scheduler getSmalljobScheduler() {
        return smalljobScheduler;
    }

    public void setSmalljobScheduler(final Scheduler smalljobScheduler) {
        this.smalljobScheduler = smalljobScheduler;
    }

    public Scheduler getBigjobScheduler() {
        return bigjobScheduler;
    }

    public void setBigjobScheduler(final Scheduler bigjobScheduler) {
        this.bigjobScheduler = bigjobScheduler;
    }

    public QuartzJobHistoryService getQuartzJobHistoryService() {
        return quartzJobHistoryService;
    }

    public void setQuartzJobHistoryService(final QuartzJobHistoryService quartzJobHistoryService) {
        this.quartzJobHistoryService = quartzJobHistoryService;
    }

    public QuartzQueueJobDetailsService getQuartzQueueJobDetailsService() {
        return quartzQueueJobDetailsService;
    }

    public void setQuartzQueueJobDetailsService(final QuartzQueueJobDetailsService quartzQueueJobDetailsService) {
        this.quartzQueueJobDetailsService = quartzQueueJobDetailsService;
    }
}
