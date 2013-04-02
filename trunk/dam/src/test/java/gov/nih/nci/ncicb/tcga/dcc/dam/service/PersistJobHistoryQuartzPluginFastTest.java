/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;

import java.text.MessageFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;

/**
 * PersistJobHistoryQuartzPlugin unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class PersistJobHistoryQuartzPluginFastTest {

    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss S";
    private static final Date TEST_TRIGGER_START_DATE = new Date(0);
    public static final String TEST_TRIGGER_NAME = "testTriggerName";
    public static final String TEST_TRIGGER_GROUP = "testTriggerGroup";
    public static final String TEST_JOB_NAME = UUID.fromString("6f4f1ce7-ca7e-4210-b16e-aaac5c5e2b36").toString();
    public static final String TEST_JOB_GROUP = FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER;
    private static final String TEST_STATUS = "Succeeded";
    private static final long TEST_ESTIMATED_UNCOMPRESSED_SIZE = 200L;
    private static final Date TEST_JOB_WS_SUBMISSION_DATE = new Date(1);
    private static final String TEST_ARCHIVE_LINK_SITE = "http://localhost/";
    private static final String TEST_ARCHIVE_LOGICAL_NAME = "testArchive";
    private static final String TEST_RESULT = "The test reports output";

    // Mocks
    private Mockery mockery = new JUnit4Mockery();
    private Log mockLog;
    private Scheduler mockScheduler;
    private Job mockJob;
    private org.quartz.Calendar mockCalendar;
    private QuartzJobHistoryService mockQuartzJobHistoryService;

    // Fakes
    private JobExecutionContext fakeJobExecutionContext;

    private PersistJobHistoryQuartzPlugin persistJobHistoryQuartzPlugin;

    @Before
    public void setUp() {

        // Mocks
        mockScheduler = mockery.mock(Scheduler.class);
        mockJob = mockery.mock(Job.class);
        mockCalendar = mockery.mock(org.quartz.Calendar.class);
        mockLog = mockery.mock(Log.class);
        mockQuartzJobHistoryService = mockery.mock(QuartzJobHistoryService.class);

        // Fakes
        fakeJobExecutionContext = new FakeJobExecutionContext(mockScheduler, getTestTriggerFiredBundle(), mockJob);

        persistJobHistoryQuartzPlugin = new PersistJobHistoryQuartzPlugin() {

            @Override
            public void initialize(final String name, final Scheduler scheduler) throws SchedulerException {

                this.name = name;
                scheduler.addGlobalJobListener(this);
                // This is what's different from the overridden method: use a mock instead of a static:
                this.quartzJobHistoryService = mockQuartzJobHistoryService;
            }
        };

        persistJobHistoryQuartzPlugin.setLog(mockLog);

        // set quartzJobHistoryService, since initialize() is only called when an actual PersistJobHistoryQuartzPlugin is created by Quartz
        persistJobHistoryQuartzPlugin.setQuartzJobHistoryService(mockQuartzJobHistoryService);
    }

    @Test
    public void testInit() throws SchedulerException {

        mockery.checking(new Expectations() {{
            one(mockScheduler).addGlobalJobListener(persistJobHistoryQuartzPlugin);
        }});

        final String expectedPluginName = "test plugin";
        persistJobHistoryQuartzPlugin.initialize(expectedPluginName, mockScheduler);

        assertEquals("Unexpected plugin name:", expectedPluginName, persistJobHistoryQuartzPlugin.getName());
        assertNotNull(persistJobHistoryQuartzPlugin.getQuartzJobHistoryService());
    }

    @Test
    public void testJobToBeExecutedInfoLogDisabled() throws SchedulerException {

        mockery.checking(new Expectations() {{
            one(mockLog).isInfoEnabled();
            will(returnValue(false));
            one(mockLog).isInfoEnabled();
            will(returnValue(false));
            one(mockQuartzJobHistoryService).persist(with(any(QuartzJobHistory.class)));
        }});

        persistJobHistoryQuartzPlugin.jobToBeExecuted(fakeJobExecutionContext);
    }

    @Test
    public void testJobToBeExecutedInfoLogEnabled() throws SchedulerException {

        final String expectedLog = new StringBuilder("Job ")
                .append(TEST_JOB_GROUP)
                .append(".")
                .append(TEST_JOB_NAME)
                .append(" fired (by trigger ")
                .append(TEST_TRIGGER_GROUP)
                .append(".")
                .append(TEST_TRIGGER_NAME)
                .append(") at: ")
                .append(getFormattedDate(TEST_TRIGGER_START_DATE))
                .toString();

        mockery.checking(new Expectations() {{

            allowing(mockLog).isInfoEnabled();
            will(returnValue(true));

            one(mockLog).info(expectedLog);
            one(mockLog).info(with(getQuartzJobHistoryValidLog()));
            one(mockQuartzJobHistoryService).persist(with(any(QuartzJobHistory.class)));
        }});

        persistJobHistoryQuartzPlugin.jobToBeExecuted(fakeJobExecutionContext);
    }

    @Test
    public void testJobExecutionVetoedInfoLogDisabled() {

        mockery.checking(new Expectations() {{
            one(mockLog).isInfoEnabled();
            will(returnValue(false));
        }});

        persistJobHistoryQuartzPlugin.jobExecutionVetoed(fakeJobExecutionContext);
    }

    @Test
    public void testJobExecutionVetoedInfoLogEnabled() {

        final String expectedLog = new StringBuilder("Job ")
                .append(TEST_JOB_GROUP)
                .append(".")
                .append(TEST_JOB_NAME)
                .append(" was vetoed. It was to be fired (by trigger ")
                .append(TEST_TRIGGER_GROUP)
                .append(".")
                .append(TEST_TRIGGER_NAME)
                .append(") at: ")
                .append(getFormattedDate(TEST_TRIGGER_START_DATE))
                .toString();

        mockery.checking(new Expectations() {{

            one(mockLog).isInfoEnabled();
            will(returnValue(true));

            one(mockLog).info(expectedLog);
        }});

        persistJobHistoryQuartzPlugin.jobExecutionVetoed(fakeJobExecutionContext);
    }

    @Test
    public void testJobWasExecuted() throws SchedulerException {

        mockery.checking(new Expectations() {{

            allowing(mockLog).isInfoEnabled();
            will(returnValue(true));

            one(mockLog).info(with(getJobWasExecutedExpectedValidLog()));
            one(mockLog).info(with(getQuartzJobHistoryValidLog()));

            one(mockQuartzJobHistoryService).persist(with(any(QuartzJobHistory.class)));
            will(returnValue(1));
        }});

        persistJobHistoryQuartzPlugin.jobWasExecuted(fakeJobExecutionContext, null);
    }

    /**
     * A JobExecutionContext fake for testing, since it can't be mocked
     */
    private class FakeJobExecutionContext extends JobExecutionContext {

        /**
         * Call super()
         *
         * @param scheduler a <code>Scheduler</code>
         * @param firedBundle a <code>TriggerFiredBundle</code>
         * @param job a <code>Job</code>
         */
        public FakeJobExecutionContext(final Scheduler scheduler, final TriggerFiredBundle firedBundle, final Job job) {
            super(scheduler, firedBundle, job);
        }

        @Override
        public Trigger getTrigger() {

            final Trigger result =  new SimpleTrigger();
            result.setName(TEST_TRIGGER_NAME);
            result.setGroup(TEST_TRIGGER_GROUP);
            result.setStartTime(TEST_TRIGGER_START_DATE);

            return result;
        }

        @Override
        public JobDetail getJobDetail() {

            final JobDetail result = new JobDetail();

            final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
            quartzJobHistory.setJobName(TEST_JOB_NAME);
            quartzJobHistory.setJobGroup(TEST_JOB_GROUP);
            quartzJobHistory.setFireTime(TEST_TRIGGER_START_DATE);

            final FilePackagerBean filePackagerBean = new FilePackagerBean();
            filePackagerBean.setQuartzJobHistory(quartzJobHistory);
            filePackagerBean.setStatus(QuartzJobStatus.valueOf(TEST_STATUS));
            filePackagerBean.setEstimatedUncompressedSize(TEST_ESTIMATED_UNCOMPRESSED_SIZE);
            filePackagerBean.setJobWSSubmissionDate(TEST_JOB_WS_SUBMISSION_DATE);
            filePackagerBean.setArchiveLinkSite(TEST_ARCHIVE_LINK_SITE);
            filePackagerBean.setArchiveLogicalName(TEST_ARCHIVE_LOGICAL_NAME);
            filePackagerBean.setJobWSSubmissionDate(TEST_JOB_WS_SUBMISSION_DATE);
            
            final JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(JobDelegate.DATA_BEAN, filePackagerBean);

            result.setName(TEST_JOB_NAME);
            result.setGroup(TEST_JOB_GROUP);
            result.setJobDataMap(jobDataMap);
            
            return result;
        }

        @Override
        public Object getResult() {
            return TEST_RESULT;
        }
    }

    /**
     * Return a test <code>TriggerFiredBundle</code>
     *
     * @return a test <code>TriggerFiredBundle</code>
     */
    private TriggerFiredBundle getTestTriggerFiredBundle() {

        final Date now = new Date();
        return new TriggerFiredBundle(new JobDetail(), new SimpleTrigger(), mockCalendar, true, now, now, now, now);
    }

    /**
     * Return the given <code>Date</code>, formatted as text
     *
     * @param date the <code>Date</code> to format
     * @return the given <code>Date</code>, formatted as text
     */
    private String getFormattedDate(final Date date) {
        return MessageFormat.format("{0, date," + DATE_FORMAT + "}", date);
    }

    /**
     * Return a <code>Matcher</code> for the expected log for QuartzJobHistory
     *
     * @return a <code>Matcher</code> for the expected log for QuartzJobHistory
     */
    private Matcher<String> getQuartzJobHistoryValidLog() {

        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String input) {

                final StringBuilder stringBuilder1 = new StringBuilder("QuartzJobHistory: [Job Name: ")
                        .append(TEST_JOB_NAME)
                        .append("], [Job Group: ")
                        .append(TEST_JOB_GROUP)
                        .append("], [Fire Time: ")
                        .append(getFormattedDate(TEST_TRIGGER_START_DATE))
                        .append("], [Status: ")
                        .append(TEST_STATUS)
                        .append("], [Last Updated: ");// Intentionally leaving the date value out since it is calculated at run time and can't be predicted

                final StringBuilder stringBuilder2 = new StringBuilder("], [Job Data: <jobData><linkText>")
                        .append(TEST_ARCHIVE_LINK_SITE)
                        .append(TEST_ARCHIVE_LOGICAL_NAME)
                        .append(".tar.gz")
                        .append("</linkText><estimatedUncompressedSize>")
                        .append(TEST_ESTIMATED_UNCOMPRESSED_SIZE)
                        .append("</estimatedUncompressedSize><jobWSSubmissionDate>")
                        .append(TEST_JOB_WS_SUBMISSION_DATE.getTime())
                        .append("</jobWSSubmissionDate></jobData>]");

                return input.contains(stringBuilder1) && input.contains(stringBuilder2);
            }

            public void describeTo(final Description description) {
                description.appendText("QuartzJobHistory Expected log");
            }
        };
    }

    /**
     * Return a <code>Matcher</code> for the expected log for jobWasExecuted()
     *
     * @return a <code>Matcher</code> for the expected log for jobWasExecuted()
     */
    private Matcher<String> getJobWasExecutedExpectedValidLog() {

        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String input) {

                final StringBuilder stringBuilder1 = new StringBuilder("Job ")
                        .append(TEST_JOB_GROUP)
                        .append(".")
                        .append(TEST_JOB_NAME)
                        .append(" execution complete at ")
                        ;// Intentionally leaving the date value out since it is calculated at run time and can't be predicted

                final StringBuilder stringBuilder2 = new StringBuilder(" and reports: ")
                        .append(TEST_RESULT);

                return input.contains(stringBuilder1) && input.contains(stringBuilder2);
            }

            public void describeTo(final Description description) {
                description.appendText("Job Was Executed Expected log");
            }
        };
    }
}
