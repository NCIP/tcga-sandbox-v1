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
import org.quartz.SchedulerException;

import java.util.List;

/**
 * Interface for QcLive monitor
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface LiveMonitor {
    /**
     * Gets a list of currently running jobs.
     * @return a list of QcLiveJobInfo objects representing running jobs
     * @throws SchedulerException if there is an error getting the running jobs list
     */
    public List<QcLiveJobInfo> getRunningJobs() throws SchedulerException;

    /**
     * Gets a list of scheduled jobs that have not yet been triggered.
     * @return a list of QcLiveJobInfo objects representing pending jobs
     * @throws SchedulerException if there is an error getting the scheduled jobs list
     */
    public List<QcLiveJobInfo> getScheduledJobs() throws SchedulerException;

    /**
     * Suspends all currently scheduled jobs, so none will be triggered until resume is called.
     * @throws SchedulerException if there is an error suspending the scheduler
     */
    public void suspendScheduler() throws SchedulerException;

    /**
     * Resumes all suspended jobs, so any that have missed or are at their fire time will be triggered.
     * @throws SchedulerException if there is an error resuming the scheduler
     */
    public void resumeScheduler() throws SchedulerException;

    /**
     * Gets the status of the scheduler
     * @return true if the scheduler is running normally, false if it is paused
     * @throws SchedulerException if there is an error getting the scheduler status
     */
    public boolean isSchedulerRunning() throws SchedulerException;
}
