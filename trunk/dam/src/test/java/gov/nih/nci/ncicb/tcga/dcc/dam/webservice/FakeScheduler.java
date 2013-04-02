/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.quartz.Calendar;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.UnableToInterruptJobException;
import org.quartz.spi.JobFactory;

/**
 * In this case, easier to use a fake than a mock, because of the number of methods
 *
 * @author David Nassau
 * @version $Rev$
 */
public class FakeScheduler implements Scheduler {
    public String getSchedulerName() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getSchedulerInstanceId() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SchedulerContext getContext() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void start() throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void startDelayed(final int i) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isStarted() throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void standby() throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void pause() throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isInStandbyMode() throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isPaused() throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void shutdown() throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void shutdown(final boolean b) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isShutdown() throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SchedulerMetaData getMetaData() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List getCurrentlyExecutingJobs() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setJobFactory(final JobFactory jobFactory) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Date scheduleJob(final JobDetail jobDetail, final Trigger trigger) throws SchedulerException {
        System.out.println("Job Scheduled");
        return new Date();
    }

    public Date scheduleJob(final Trigger trigger) throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean unscheduleJob(final String s, final String s1) throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Date rescheduleJob(final String s, final String s1, final Trigger trigger) throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addJob(final JobDetail jobDetail, final boolean b) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean deleteJob(final String s, final String s1) throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void triggerJob(final String s, final String s1) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void triggerJobWithVolatileTrigger(final String s, final String s1) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void triggerJob(final String s, final String s1, final JobDataMap jobDataMap) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void triggerJobWithVolatileTrigger(final String s, final String s1, final JobDataMap jobDataMap)
            throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void pauseJob(final String s, final String s1) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void pauseJobGroup(final String s) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void pauseTrigger(final String s, final String s1) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void pauseTriggerGroup(final String s) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resumeJob(final String s, final String s1) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resumeJobGroup(final String s) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resumeTrigger(final String s, final String s1) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resumeTriggerGroup(final String s) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void pauseAll() throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resumeAll() throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] getJobGroupNames() throws SchedulerException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] getJobNames(final String s) throws SchedulerException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Trigger[] getTriggersOfJob(final String s, final String s1) throws SchedulerException {
        return new Trigger[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] getTriggerGroupNames() throws SchedulerException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] getTriggerNames(final String s) throws SchedulerException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set getPausedTriggerGroups() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public JobDetail getJobDetail(final String s, final String s1) throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Trigger getTrigger(final String s, final String s1) throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getTriggerState(final String s, final String s1) throws SchedulerException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addCalendar(final String s, final Calendar calendar, final boolean b, final boolean b1)
            throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean deleteCalendar(final String s) throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Calendar getCalendar(final String s) throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] getCalendarNames() throws SchedulerException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean interrupt(final String s, final String s1) throws UnableToInterruptJobException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addGlobalJobListener(final JobListener jobListener) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addJobListener(final JobListener jobListener) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean removeGlobalJobListener(final JobListener jobListener) throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean removeGlobalJobListener(final String s) throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean removeJobListener(final String s) throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List getGlobalJobListeners() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set getJobListenerNames() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public JobListener getGlobalJobListener(final String s) throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public JobListener getJobListener(final String s) throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addGlobalTriggerListener(final TriggerListener triggerListener) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addTriggerListener(final TriggerListener triggerListener) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean removeGlobalTriggerListener(final TriggerListener triggerListener) throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean removeGlobalTriggerListener(final String s) throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean removeTriggerListener(final String s) throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List getGlobalTriggerListeners() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set getTriggerListenerNames() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public TriggerListener getGlobalTriggerListener(final String s) throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public TriggerListener getTriggerListener(final String s) throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addSchedulerListener(final SchedulerListener schedulerListener) throws SchedulerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean removeSchedulerListener(final SchedulerListener schedulerListener) throws SchedulerException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List getSchedulerListeners() throws SchedulerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
