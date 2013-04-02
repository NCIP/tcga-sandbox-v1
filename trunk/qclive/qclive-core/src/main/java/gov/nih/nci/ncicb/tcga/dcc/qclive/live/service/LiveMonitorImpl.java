/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.QcLiveJobInfo;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * QcLive job monitor.  Queries a list of Scheduler to find running jobs and scheduled jobs.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LiveMonitorImpl implements LiveMonitor {
    // key = Scheduler object, value = group name to monitor
    private Map<Scheduler, String> schedulers;

    /**
     * Queries the quartz scheduler to find what jobs are currently executing.
     * @return a list of QcLiveJobInfo objects
     * @throws SchedulerException
     */
    public List<QcLiveJobInfo> getRunningJobs() throws SchedulerException {
        final List<QcLiveJobInfo> jobs = new ArrayList<QcLiveJobInfo>();
        for (final Scheduler scheduler : schedulers.keySet()) {
            final List executingJobs = scheduler.getCurrentlyExecutingJobs();
            for (final Object o : executingJobs) {
                final JobExecutionContext jobExecutionContext = (JobExecutionContext) o;
                final QcLiveJobInfo qcLiveJobInfo = new QcLiveJobInfo();
                qcLiveJobInfo.setStartTime(jobExecutionContext.getFireTime());
                qcLiveJobInfo.setJobName(jobExecutionContext.getJobDetail().getName());
                setJobType(qcLiveJobInfo, jobExecutionContext.getJobDetail().getJobClass());
                jobs.add(qcLiveJobInfo);
            }
        }
        sortJobList(jobs);
        return jobs;
    }

    /**
     * Queries the quartz scheduler to find what jobs are pending -- are scheduled to fire but haven't fired yet.
     * Note: if a job has multiple triggers, only the first one will be looked at.
     * @return a list of QcLiveJobInfo objects
     * @throws SchedulerException
     */
    public List<QcLiveJobInfo> getScheduledJobs() throws SchedulerException {        
        final List<QcLiveJobInfo> scheduledJobs = new ArrayList<QcLiveJobInfo>();
        for (final Scheduler scheduler : schedulers.keySet()) {
            final String[] jobNames = scheduler.getJobNames(schedulers.get(scheduler));
            for (final String jobName : jobNames) {
                final JobDetail jobDetail = scheduler.getJobDetail(jobName, schedulers.get(scheduler));
                final Trigger[] triggers = scheduler.getTriggersOfJob(jobName, schedulers.get(scheduler));
                if (triggers.length > 0) {
                    final QcLiveJobInfo jobInfo = new QcLiveJobInfo();
                    final Date nextFireTime = triggers[0].getNextFireTime();
                    if (nextFireTime != null) {
                        jobInfo.setStartTime(nextFireTime);
                        jobInfo.setJobName(jobName);
                        setJobType(jobInfo, jobDetail.getJobClass());
                        scheduledJobs.add(jobInfo);
                    }
                }
            }
        }
        sortJobList(scheduledJobs);
        return scheduledJobs;
    }

    private void sortJobList(final List<QcLiveJobInfo> jobList) {
        Collections.sort(jobList, new Comparator<QcLiveJobInfo>() {
            @Override
            public int compare(final QcLiveJobInfo j1, final QcLiveJobInfo j2) {
                return j1.getStartTime().compareTo(j2.getStartTime());
            }
        });
    }

    private void setJobType(final QcLiveJobInfo qcLiveJobInfo, final Class jobClass) {
        qcLiveJobInfo.setJobType(jobClass.getSimpleName());
    }

    /**
     * Suspends the scheduler by calling standby().       
     * @throws SchedulerException
     */
    public synchronized void suspendScheduler() throws SchedulerException {
        for (final Scheduler scheduler : schedulers.keySet()) {
            scheduler.standby();
        }
    }

    /**
     * Resumes the scheduler by calling start(). The quartz API notes that this will
     * not pay attention to misfire instructions.
     * @throws SchedulerException
     */
    public synchronized void resumeScheduler() throws SchedulerException {
        for (final Scheduler scheduler : schedulers.keySet()) {
            scheduler.start();
        }
    }

    /**
     * Checks if the any schedulers are in standby, and returns true only if none are in standby.
     * @return true if all schedulers are started, false if any are in standby
     * @throws SchedulerException if the status can't be acquired
     */
    public synchronized boolean isSchedulerRunning() throws SchedulerException {
        boolean inStandby = false;
        for (final Scheduler scheduler : schedulers.keySet()) {
            if (scheduler.isInStandbyMode()) {
                inStandby = true;
            }
        }
        return !inStandby;
    }

    /**
     * Set the map of schedulers to monitor, where the key is the Scheduler object and the
     * value is the name of the job group to monitor in that Scheduler.
     * @param schedulers the map of schedulers and group names to monitor
     */
    public void setSchedulers(final Map<Scheduler, String> schedulers) {
        this.schedulers = schedulers;
    }
}
