/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveDeletionBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Singleton class which schedules file packaging and archive deletion jobs.
 * Spring uses getInstance method to create this object
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class FilePackagerEnqueuer implements FilePackagerEnqueuerI {

    public static final String JOB_GROUP_FILE_PACKAGER = "filePackagerGroup";
    public static final String JOB_GROUP_ARCHIVE_DELETION = "archiveDeletionGroup";
    public static final String JOB_GROUP_QUARTZ_JOB_HISTORY_DELETION = "quartzJobHistoryDeletionGroup";
    public static final int PRIORITY_DELETION = 150;
    public static final String SPRING_BEAN_NAME_FOR_FILEPACKAGER_JOB = "filePackagerJob";
    public static final String SPRING_BEAN_NAME_FOR_ARCHIVE_DELETION_JOB = "archiveDeletionJob";
    public static final String SPRING_BEAN_NAME_FOR_QUARTZ_JOB_HISTORY_DELETION_JOB = "quartzJobHistoryDeletionJob";
    public static final String QUEUE_NAME_SMALL_JOB = "small";
    public static final String QUEUE_NAME_BIG_JOB = "big";

    private static final Object smallQueueLock = new Object();
    private static final Object bigQueueLock = new Object();

    private Scheduler bigjobScheduler, smalljobScheduler;
    private int hoursTillDeletion;
    private long smallJobQueueMaxBytes = 52428800; //default to 50MB
    private QuartzJobHistoryService quartzJobHistoryService;

    protected final Log log = LogFactory.getLog(getClass());


    public void setBigjobScheduler(final Scheduler bigjobScheduler) {
        this.bigjobScheduler = bigjobScheduler;
    }

    public void setSmalljobScheduler(final Scheduler smalljobScheduler) {
        this.smalljobScheduler = smalljobScheduler;
    }

    public void setHoursTillDeletion(final int hoursTillDeletion) {
        this.hoursTillDeletion = hoursTillDeletion;
    }

    public int getHoursTillDeletion() {
        return hoursTillDeletion;
    }

    public void setSmallJobQueueMaxBytes(final long fastLaneMaxBytes) {
        this.smallJobQueueMaxBytes = fastLaneMaxBytes;
    }

    protected abstract JobDetail getJobDetail();

    protected abstract SimpleTriggerBean getTrigger();

    protected Trigger getTrigger(final JobDetail jobDetail, final Date scheduleTime) {
        SimpleTriggerBean trigger = getTrigger();
        trigger.setName(jobDetail.getName());
        trigger.setJobName(jobDetail.getName());
        trigger.setJobDetail(jobDetail);
        trigger.setGroup(jobDetail.getGroup());
        trigger.setStartTime(scheduleTime);
        trigger.setJobDataMap(jobDetail.getJobDataMap());
        trigger.setJobGroup(jobDetail.getGroup());
        return trigger;
    }

    /**
     * Schedule a FilePackager job with the given <code>FilePackagerBean</code>
     *
     * @param filePackagerBean the <code>FilePackagerBean</code> to run the FilePackager job with
     * @throws SchedulerException
     */
    public void queueFilePackagerJob(final FilePackagerBean filePackagerBean) throws SchedulerException {

        String name = filePackagerBean.getKey().toString();
        JobDetail jobDetail = getJobDetail();
        jobDetail.setName(name);
        jobDetail.getJobDataMap().put(JobDelegate.DATA_BEAN, filePackagerBean);
        jobDetail.getJobDataMap().put(JobDelegate.JOB_BEAN_NAME, SPRING_BEAN_NAME_FOR_FILEPACKAGER_JOB);

        final Trigger trigger = getTrigger(jobDetail, new Date());

        final QuartzJobHistory quartzJobHistory = filePackagerBean.createQuartzJobHistory(jobDetail, trigger);

        long packageSize = filePackagerBean.getPriorityAdjustedEstimatedUncompressedSize();

        try {
            if (packageSize <= smallJobQueueMaxBytes) {

                log.info(new StringBuilder("[SMALL QUEUE] Scheduled ")
                        .append(filePackagerBean.getArchivePhysicalPathPrefix())
                        .append(filePackagerBean.getArchivePhysicalName())
                        .append("[")
                        .append(name)
                        .append("]")
                        .append(" Start Time: ")
                        .append(trigger.getStartTime())
                );
                scheduleSmallJobs(jobDetail, trigger);
                quartzJobHistory.setQueueName(QUEUE_NAME_SMALL_JOB);

            } else {

                log.info(new StringBuilder("[BIG QUEUE] Scheduled ")
                        .append(filePackagerBean.getArchivePhysicalPathPrefix())
                        .append(filePackagerBean.getArchivePhysicalName())
                        .append("[")
                        .append(name)
                        .append("]")
                        .append(" Time: ")
                        .append(trigger.getStartTime())
                );
                scheduleBigJobs(jobDetail, trigger);
                quartzJobHistory.setQueueName(QUEUE_NAME_BIG_JOB);

            }
            // once we get here, the job is in the queue
            quartzJobHistory.setEnqueueDate(new Date());
            quartzJobHistory.setStatus(QuartzJobStatus.Queued);

        } catch (SchedulerException e) {
            quartzJobHistory.setStatus(QuartzJobStatus.Failed);
            quartzJobHistory.setLinkText("There was an error scheduling the job: " + e.getMessage());

            // rethrow after updating the status
            throw e;
        } finally {
            quartzJobHistoryService.persist(quartzJobHistory);
        }
    }

    private void scheduleSmallJobs(final JobDetail jobDetail, final Trigger trigger) throws SchedulerException {
        synchronized (smallQueueLock) {
            smalljobScheduler.scheduleJob(jobDetail, trigger);
        }
    }

    private void scheduleBigJobs(final JobDetail jobDetail, final Trigger trigger) throws SchedulerException {
        synchronized (bigQueueLock) {
            bigjobScheduler.scheduleJob(jobDetail, trigger);
        }

    }



    /**
     * Schedule a job to delete the given archive
     *
     * @param archiveName the archive name
     * @param immediate <code>true</code> if it should be scheduled immediately, <code>false</code> otherwise
     * @return the date at which the trigger will fire
     * @throws SchedulerException
     */
    public Date queueArchiveDeletionJob(final String archiveName, final boolean immediate) throws SchedulerException {

        String name = UUID.randomUUID().toString();
        JobDetail jobDetail = getJobDetail();
        jobDetail.setName(name);
        jobDetail.setGroup(JOB_GROUP_ARCHIVE_DELETION);
        ArchiveDeletionBean archiveBean = new ArchiveDeletionBean();
        archiveBean.setArchiveName(archiveName);
        jobDetail.getJobDataMap().put(JobDelegate.DATA_BEAN, archiveBean);
        jobDetail.getJobDataMap().put(JobDelegate.JOB_BEAN_NAME, SPRING_BEAN_NAME_FOR_ARCHIVE_DELETION_JOB);

        final Date now = getDateForArchiveDeletionSchedule(immediate);
        Trigger trigger = getTrigger(jobDetail, now);
        trigger.setPriority(PRIORITY_DELETION);
        log.info("Scheduling deletion of " + archiveName + getJobDetailString(jobDetail, trigger) + "at " + now.toString());
        scheduleSmallJobs(jobDetail, trigger);

        return now;
    }

    /**
     * Return the <code>Date</code> at which the archive should be scheduled for deletion
     *
     * @param immediate <code>true</code> if it should be scheduled immediately, <code>false<code> otherwise
     * @return the <code>Date</code> at which the archive should be scheduled for deletion
     */
    private Date getDateForArchiveDeletionSchedule(boolean immediate) {

        final Calendar now = Calendar.getInstance();

        if (!immediate) {
            now.add(Calendar.HOUR, getHoursTillDeletion());
        }

        return now.getTime();
    }

    private String getJobDetailString(final JobDetail jobDetail, final Trigger trigger) {
        final StringBuilder sb = new StringBuilder();
        sb.append(" [ JobDetail: ")
                .append(jobDetail.getName())
                .append("/")
                .append(jobDetail.getGroup())
                .append("Trigger:")
                .append(trigger.getName())
                .append("/")
                .append(trigger.getJobName())
                .append("/")
                .append(trigger.getGroup())
                .append("]");

        return sb.toString();
    }

    /**
     * Schedule a job to delete the given <code>QuartzJobHistory</code>.
     * It should be scheduled at the same time the <code>FilePackagerBean</code> archive is scheduled for deletion.
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to delete
     * @param dateOfTrigger the Date at which the trigger should fire
     * @throws SchedulerException
     */
    @Override
    public void queueQuartzJobHistoryDeletionJob(final QuartzJobHistory quartzJobHistory, final Date dateOfTrigger) throws SchedulerException {

        final String jobName = UUID.randomUUID().toString();
        final JobDetail jobDetail = getJobDetail();
        jobDetail.setName(jobName);
        jobDetail.setGroup(JOB_GROUP_QUARTZ_JOB_HISTORY_DELETION);
        jobDetail.getJobDataMap().put(JobDelegate.DATA_BEAN, quartzJobHistory);
        jobDetail.getJobDataMap().put(JobDelegate.JOB_BEAN_NAME, SPRING_BEAN_NAME_FOR_QUARTZ_JOB_HISTORY_DELETION_JOB);

        final Trigger trigger = getTrigger(jobDetail, dateOfTrigger);
        trigger.setPriority(PRIORITY_DELETION);

        log.info(new StringBuilder("Scheduling deletion of quartzJobHistory ")
                .append(quartzJobHistory.getJobName())
                .append(".")
                .append(quartzJobHistory.getJobGroup())
                .append(" ")
                .append(getJobDetailString(jobDetail, trigger))
                .append(" at ")
                .append(dateOfTrigger.toString())
        );

        scheduleSmallJobs(jobDetail, trigger);
    }

    public void setQuartzJobHistoryService(final QuartzJobHistoryService quartzJobHistoryService) {
        this.quartzJobHistoryService = quartzJobHistoryService;
    }
}
