/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.common.util.LoggingJobHistoryPlugin;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Quartz plugin to log and persist completed (failed or successful) jobs.
 * Also log, without persisting, the started and vetoed jobs.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class PersistJobHistoryQuartzPlugin extends LoggingJobHistoryPlugin {

    /**
     * The bean name for the <code>JobHistoryService</code> bean in the Spring application context.
     */
    private static final String QUARTZ_JOB_HISTORY_SERVICE_BEAN_NAME = "quartzJobHistoryService";

    protected QuartzJobHistoryService quartzJobHistoryService;

    private String quartzJobHistoryMessage = "QuartzJobHistory: " +
            "[Job Name: {0}], " +
            "[Job Group: {1}], " +
            "[Fire Time: {2, date," + DATE_FORMAT + "}], " +
            "[Status: {3}], " +
            "[Last Updated: {4, date," + DATE_FORMAT + "}], " +
            "[Job Data: {5}]";
    /*
     * SchedulerPlugin Interface
     */

    @Override
    public void initialize(final String name, final Scheduler scheduler) throws SchedulerException {

        super.initialize(name, scheduler);
        this.quartzJobHistoryService = (QuartzJobHistoryService) SpringApplicationContext.getObject(QUARTZ_JOB_HISTORY_SERVICE_BEAN_NAME);
    }

    /*
     * JobListener Interface
     */

    /**
     * Log info from the job that is about to be executed, as well as info from its associated <code>QuartzJobHistory</code>.
     *
     * Note: <code>QuartzJobHistory</code> does not need to be persisted: Jobs that are started but not finished before the application crashes or restarts
     * will be retrieved by Quartz.
     *
     * @param jobExecutionContext the <code>JobExecutionContext</code> for the job being executed
     */
    @Override
    public void jobToBeExecuted(final JobExecutionContext jobExecutionContext) {

        super.jobToBeExecuted(jobExecutionContext);

        // Log the QuartzJobHistory if the job belongs to the JOB_GROUP_FILE_PACKAGER group
        final JobDetail jobDetail = jobExecutionContext.getJobDetail();
        if(FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER.equals(jobDetail.getGroup())) {

            final FilePackagerBean filePackagerBean = getFilePackagerBean(jobDetail);
            final QuartzJobHistory quartzJobHistory = filePackagerBean.getUpdatedQuartzJobHistory();

            logQuartzJobHistory(quartzJobHistory);
            persist(quartzJobHistory);
        }
    }

    /**
     * Log info from the job that was executed, as well as info from its associated <code>QuartzJobHistory</code>
     * and persist the <code>QuartzJobHistory</code>.
     *
     * @param jobExecutionContext the <code>JobExecutionContext</code> for the job being executed
     * @param jobException the <code>JobExecutionException</code> for the job being executed
     */
    @Override
    public void jobWasExecuted(final JobExecutionContext jobExecutionContext, final JobExecutionException jobException) {

        super.jobWasExecuted(jobExecutionContext, jobException);

        // Log and persist the QuartzJobHistory if the job belongs to the JOB_GROUP_FILE_PACKAGER group
        final JobDetail jobDetail = jobExecutionContext.getJobDetail();
        if(FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER.equals(jobDetail.getGroup())) {

            final FilePackagerBean filePackagerBean = getFilePackagerBean(jobDetail);
            final QuartzJobHistory quartzJobHistory = filePackagerBean.getUpdatedQuartzJobHistory();

            logQuartzJobHistory(quartzJobHistory);
            persist(quartzJobHistory);
        }
    }

    /**
     * Persist the given <code>QuartzJobHistory</code>
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to persist
     */
    private void persist(final QuartzJobHistory quartzJobHistory) {
        getQuartzJobHistoryService().persist(quartzJobHistory);
    }

    /**
     * Log a given <code>QuartzJobHistory</code>
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to log
     */
    private void logQuartzJobHistory(final QuartzJobHistory quartzJobHistory) {

        if (!getLog().isInfoEnabled()) {
            return;
        }

        final Object[] args = new Object[] {
                quartzJobHistory.getJobName(),
                quartzJobHistory.getJobGroup(),
                quartzJobHistory.getFireTime(),
                quartzJobHistory.getStatusAsString(),
                quartzJobHistory.getLastUpdated(),
                quartzJobHistory.getJobData()
        };

        getLog().info(MessageFormat.format(getQuartzJobHistoryMessage(), args));
    }

    /**
     * Return the <code>FilePackagerBean</code> associated with the given <code>JobDetail</code>
     * if the job belongs to the JOB_GROUP_FILE_PACKAGER group, <code>false</code> otherwise
     *
     * @param jobDetail the <code>JobDetail</code> from which to retrieve the <code>FilePackagerBean</code>
     * @return the <code>FilePackagerBean</code> associated with the given <code>JobDetail</code>
     * if the job belongs to the JOB_GROUP_FILE_PACKAGER group, <code>false</code> otherwise
     */
    private FilePackagerBean getFilePackagerBean(final JobDetail jobDetail) {

        FilePackagerBean result = null;

        final Object object = jobDetail.getJobDataMap().get(JobDelegate.DATA_BEAN);

        if(object instanceof FilePackagerBean) {
            result = (FilePackagerBean)object;
        }

        return result;
    }

    /*
     * Getter / Setter
     */

    public QuartzJobHistoryService getQuartzJobHistoryService() {
        return quartzJobHistoryService;
    }

    public void setQuartzJobHistoryService(final QuartzJobHistoryService quartzJobHistoryService) {
        this.quartzJobHistoryService = quartzJobHistoryService;
    }

    public String getQuartzJobHistoryMessage() {
        return quartzJobHistoryMessage;
    }

    public void setQuartzJobHistoryMessage(final String quartzJobHistoryMessage) {
        this.quartzJobHistoryMessage = quartzJobHistoryMessage;
    }
}
