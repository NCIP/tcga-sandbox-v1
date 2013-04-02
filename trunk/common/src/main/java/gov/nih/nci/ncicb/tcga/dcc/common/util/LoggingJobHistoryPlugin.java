/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.spi.SchedulerPlugin;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Quartz plugin to log started, vetoed and completed (failed or successful) jobs.
 *
 * This is based on the LoggingJobHistoryPlugin provided by Quartz.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LoggingJobHistoryPlugin implements SchedulerPlugin, JobListener {

    /**
     * The date format to use for logging.
     */
    protected static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss S";

    private Log log = LogFactory.getLog(getClass());

    /**
     * The identifier for this quartz plugin
     */
    protected String name;

    private String jobToBeFiredMessage = "Job {1}.{0} fired (by trigger {4}.{3}) at: {2, date," + DATE_FORMAT + "}";

    private String jobSuccessMessage = "Job {1}.{0} execution complete at {2, date," + DATE_FORMAT + "} and reports: {3}";

    private String jobFailedMessage = "Job {1}.{0} execution failed at {2, date," + DATE_FORMAT + "} and reports: {3}";

    private String jobWasVetoedMessage = "Job {1}.{0} was vetoed. It was to be fired (by trigger {4}.{3}) at: {2, date," + DATE_FORMAT + "}";

    /*
     * SchedulerPlugin Interface
     */

    @Override
    public void initialize(final String name, final Scheduler scheduler) throws SchedulerException {

        this.name = name;
        scheduler.addGlobalJobListener(this);
    }

    @Override
    public void start() {
        //do nothing
    }

    @Override
    public void shutdown() {
        //do nothing
    }

    /*
     * JobListener Interface
     */

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Log info from the job that is about to be executed
     *
     * @param jobExecutionContext the <code>JobExecutionContext</code> for the job being executed
     */
    @Override
    public void jobToBeExecuted(final JobExecutionContext jobExecutionContext) {

        if (!getLog().isInfoEnabled()) {
            return;
        }

        final Trigger trigger = jobExecutionContext.getTrigger();
        final JobDetail jobDetail = jobExecutionContext.getJobDetail();

        final Object[] args = {
                jobDetail.getName(),
                jobDetail.getGroup(),
                trigger.getStartTime(),
                trigger.getName(),
                trigger.getGroup()
        };

        getLog().info(MessageFormat.format(getJobToBeFiredMessage(), args));
    }

    /**
     * Log info from the job that has been vetoed.
     *
     * @param jobExecutionContext the <code>JobExecutionContext</code> for the job being executed
     */
    @Override
    public void jobExecutionVetoed(final JobExecutionContext jobExecutionContext) {

        if (!getLog().isInfoEnabled()) {
            return;
        }

        final Trigger trigger = jobExecutionContext.getTrigger();
        final JobDetail jobDetail = jobExecutionContext.getJobDetail();

        final Object[] args = {
                jobDetail.getName(),
                jobDetail.getGroup(),
                trigger.getStartTime(),
                trigger.getName(),
                trigger.getGroup()
        };

        getLog().info(MessageFormat.format(getJobWasVetoedMessage(), args));
    }

    /**
     * Log info from the job that was executed
     *
     * @param jobExecutionContext the <code>JobExecutionContext</code> for the job being executed
     * @param jobException the <code>JobExecutionException</code> for the job being executed
     */
    @Override
    public void jobWasExecuted(final JobExecutionContext jobExecutionContext, final JobExecutionException jobException) {

        final JobDetail jobDetail = jobExecutionContext.getJobDetail();

        Object[] args = null;

        if (jobException != null) {

            final String errMsg = jobException.getMessage();
            args = new Object[] {
                    jobDetail.getName(),
                    jobDetail.getGroup(),
                    new Date(),
                    errMsg
            };

            getLog().warn(MessageFormat.format(getJobFailedMessage(), args), jobException);

        } else {

            final String result = String.valueOf(jobExecutionContext.getResult());
            args = new Object[] {
                    jobExecutionContext.getJobDetail().getName(),
                    jobExecutionContext.getJobDetail().getGroup(),
                    new Date(),
                    result
            };

            getLog().info(MessageFormat.format(getJobSuccessMessage(), args));
        }
    }

    /*
     * Getter / Setter
     */

    public Log getLog() {
        return log;
    }

    public void setLog(final Log log) {
        this.log = log;
    }

    public String getJobToBeFiredMessage() {
        return jobToBeFiredMessage;
    }

    public void setJobToBeFiredMessage(final String jobToBeFiredMessage) {
        this.jobToBeFiredMessage = jobToBeFiredMessage;
    }

    public String getJobSuccessMessage() {
        return jobSuccessMessage;
    }

    public void setJobSuccessMessage(final String jobSuccessMessage) {
        this.jobSuccessMessage = jobSuccessMessage;
    }

    public String getJobFailedMessage() {
        return jobFailedMessage;
    }

    public void setJobFailedMessage(final String jobFailedMessage) {
        this.jobFailedMessage = jobFailedMessage;
    }

    public String getJobWasVetoedMessage() {
        return jobWasVetoedMessage;
    }

    public void setJobWasVetoedMessage(final String jobWasVetoedMessage) {
        this.jobWasVetoedMessage = jobWasVetoedMessage;
    }
}
