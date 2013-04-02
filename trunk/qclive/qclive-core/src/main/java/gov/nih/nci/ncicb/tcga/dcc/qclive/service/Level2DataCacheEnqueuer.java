/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

import java.util.Date;

/**
 * Class which schedules jobs to generate level2data cache files.
 * Only one job should be scheduled per experiment.
 * Use <code> addJob </code> API to add job into the queue and
 * <code> scheduleTrigger</code> API to schedule the job.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class Level2DataCacheEnqueuer implements ConstantValues, Level2DataCacheEnqueuerI {
    private static final Object lockObject = new Object();

    private Scheduler scheduler;

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

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

    protected abstract JobDetail getJobDetail();

    protected abstract SimpleTriggerBean getTrigger();

    /**
     * Adds new job into the scheduler. If the job already exists, updates existing job JobDataMap with
     * new experimentId.
     * The Job will be not be scheduled for processing. Call scheduleTrigger to schedule the Job for processing.
     *
     * @param level2DataFilterBean
     * @return JobDetail for the given level2DataFileBean
     * @throws SchedulerException
     */
    public JobDetail addJob(final Level2DataFilterBean level2DataFilterBean) throws SchedulerException {
        final String jobName = getJobName(level2DataFilterBean);
        Level2DataFilterBean jobDataBean = level2DataFilterBean;
        JobDetail jobDetail  = null;

   //     JobDetail jobDetail = scheduler.getJobDetail(jobName, getJobGroupName());
        // If job already exists, update experimentId List
        if (jobDetail != null) {
            jobDataBean = (Level2DataFilterBean) jobDetail.getJobDataMap().get(DATA_BEAN);
            jobDataBean.addExperimentIds(level2DataFilterBean.getExperimentIdList());
        } else { // create new job
            jobDetail = getJobDetail();
        }
        jobDetail.setName(jobName);
        jobDetail.setGroup(getJobGroupName());
        jobDetail.getJobDataMap().put(DATA_BEAN, jobDataBean);
        jobDetail.getJobDataMap().put(JOB_BEAN_NAME, LEVEL2_CACHE_GENERATOR_SPRING_BEAN_NAME);

        // Add job into the scheduler.
   /*     synchronized (lockObject) {
            scheduler.addJob(jobDetail, true);
        } */
        return jobDetail;
    }

    /**
     * Schedules trigger for the given job. The job should be added into the
     * scheduler before calling this API. Call addJob API to add the job into the scheduler.
     *
     * @param jobDetail
     * @throws SchedulerException
     */
    public void scheduleTrigger(final JobDetail jobDetail) throws SchedulerException {
        final Trigger trigger = getTrigger(jobDetail, new Date());
       /* synchronized (lockObject) {
            scheduler.scheduleJob(trigger);
        } */
    }

    public String getJobName(Level2DataFilterBean level2DataFilterBean) {
        StringBuilder jobName = new StringBuilder(level2DataFilterBean.getDiseaseAbbreviation());
        jobName.append("_")
                .append(level2DataFilterBean.getCenterDomainName())
                .append("_")
                .append(level2DataFilterBean.getPlatformName());

        return jobName.toString();
    }

    public static String getJobGroupName() {
        return ConstantValues.LEVEL_2_CACHE_GROUP_NAME;
    }
}
