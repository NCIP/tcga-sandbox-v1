/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.QcLiveJobInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.Live;
import gov.nih.nci.ncicb.tcga.dcc.qclive.service.LoaderJob;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Test for LiveMonitorImpl
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class LiveMonitorImplFastTest {
    private LiveMonitorImpl liveMonitor;
    private Mockery context;
    private Scheduler mockQcLiveScheduler, mockLoaderScheduler;
    private List<JobExecutionContext> currentlyExecutingQcLiveJobs, currentlyExecutingLoaderJobs;
    private Date defaultFireTime;
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() throws Exception {
        context = new JUnit4Mockery();
        liveMonitor = new LiveMonitorImpl();
        mockQcLiveScheduler = context.mock(Scheduler.class, "qcLiveScheduler");
        mockLoaderScheduler = context.mock(Scheduler.class, "loaderScheduler");
        final Map<Scheduler, String> schedulers = new HashMap<Scheduler, String>();
        schedulers.put(mockQcLiveScheduler, Live.QUARTZ_JOB_GROUP);
        schedulers.put(mockLoaderScheduler, "loaderGroup");
        liveMonitor.setSchedulers(schedulers);
        currentlyExecutingQcLiveJobs = new ArrayList<JobExecutionContext>();
        currentlyExecutingLoaderJobs = new ArrayList<JobExecutionContext>(); 
        defaultFireTime = new Date();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    }

    private void setUpForRunningJobs() throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockQcLiveScheduler).getCurrentlyExecutingJobs();
            will(returnValue(currentlyExecutingQcLiveJobs));

            one(mockLoaderScheduler).getCurrentlyExecutingJobs();
            will(returnValue(currentlyExecutingLoaderJobs));
        }});
    }

    private void setUpForScheduledQcLiveJobs(final String... jobNames) throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockQcLiveScheduler).getJobNames(Live.QUARTZ_JOB_GROUP);
            will(returnValue(jobNames));
        }});
    }

    private void setUpForScheduledLoaderJobs(final String... jobNames) throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockLoaderScheduler).getJobNames("loaderGroup");
            will(returnValue(jobNames));
        }});
    }

    @Test
    public void testGetRunningJobsNone() throws SchedulerException {
        setUpForRunningJobs();
        // don't add any jobs for the scheduler to return
        final List<QcLiveJobInfo> jobs = liveMonitor.getRunningJobs();
        assertNotNull(jobs);
        assertEquals(0, jobs.size());
    }

    @Test
    public void testGetRunningJobsOneUpload() throws SchedulerException {
        setUpForRunningJobs();
        final UploadCheckerJob uploadCheckerJob = makeUploadCheckerJob();
        currentlyExecutingQcLiveJobs.add(makeJobExecutionContext("archive_name.tar.gz", uploadCheckerJob, defaultFireTime));
        final List<QcLiveJobInfo> jobs = liveMonitor.getRunningJobs();
        assertEquals(1, jobs.size());
        checkJobInfo(jobs.get(0), "archive_name.tar.gz", "TestableUploadCheckerJob", defaultFireTime);
    }

    @Test
    public void testGetRunningJobsOneExperiment() throws SchedulerException {
        setUpForRunningJobs();
        final ExperimentCheckerJob experimentCheckerJob = makeExperimentCheckerJob();
        currentlyExecutingQcLiveJobs.add(makeJobExecutionContext("experimentName", experimentCheckerJob, defaultFireTime));
        final List<QcLiveJobInfo> jobs = liveMonitor.getRunningJobs();
        assertEquals(1, jobs.size());
        checkJobInfo(jobs.get(0), "experimentName", "TestableExperimentCheckerJob", defaultFireTime);
    }

    @Test
    public void testGetRunningJobsOneCleanup() throws SchedulerException {
        setUpForRunningJobs();
        currentlyExecutingQcLiveJobs.add(makeJobExecutionContext("archiveName", new ArchiveCleanupJob(), defaultFireTime));
        final List<QcLiveJobInfo> jobs = liveMonitor.getRunningJobs();
        assertEquals(1, jobs.size());
        checkJobInfo(jobs.get(0), "archiveName", "ArchiveCleanupJob", defaultFireTime);
    }

    @Test
    public void testGetRunningJobsOneLoader() throws SchedulerException {
        setUpForRunningJobs();
        currentlyExecutingLoaderJobs.add(makeJobExecutionContext("archiveToLoad", new TestableLoaderJob(), defaultFireTime));
        final List<QcLiveJobInfo> jobs = liveMonitor.getRunningJobs();
        assertEquals(1, jobs.size());
        checkJobInfo(jobs.get(0), "archiveToLoad", "TestableLoaderJob", defaultFireTime);
    }

    @Test
    public void testGetRunningJobs() throws SchedulerException, ParseException {
        setUpForRunningJobs();
        final Date upload1Start = dateFormat.parse("10/01/2010 08:00:00");
        final Date upload2Start = dateFormat.parse("10/01/2010 12:00:00");
        final Date experiment1Start = dateFormat.parse("10/02/2010 6:30:00");
        final Date experiment2Start = dateFormat.parse("10/01/2010 09:00:00");
        final Date experiment3Start = dateFormat.parse("10/01/2010 22:00:00");
        final Date cleanupStart = dateFormat.parse("10/02/2010 6:29:00");
        final Date loaderStart = dateFormat.parse("11/01/2010 8:00:00");

        currentlyExecutingQcLiveJobs.add(makeJobExecutionContext("uploaded_archive1.tar.gz", makeUploadCheckerJob(), upload1Start));
        currentlyExecutingQcLiveJobs.add(makeJobExecutionContext("uploaded_archive2.tar.gz", makeUploadCheckerJob(), upload2Start));
        currentlyExecutingQcLiveJobs.add(makeJobExecutionContext("Experiment1", makeExperimentCheckerJob(), experiment1Start));
        currentlyExecutingQcLiveJobs.add(makeJobExecutionContext("Experiment2", makeExperimentCheckerJob(), experiment2Start));
        currentlyExecutingQcLiveJobs.add(makeJobExecutionContext("Experiment3", makeExperimentCheckerJob(), experiment3Start));
        currentlyExecutingQcLiveJobs.add(makeJobExecutionContext("done_archive", new ArchiveCleanupJob(), cleanupStart));
        currentlyExecutingLoaderJobs.add(makeJobExecutionContext("anArchiveToLoad", new TestableLoaderJob(), loaderStart));

        final List<QcLiveJobInfo> jobs = liveMonitor.getRunningJobs();
        assertEquals(7, jobs.size());
        // expect list to be sorted by start time
        checkJobInfo(jobs.get(0), "uploaded_archive1.tar.gz", "TestableUploadCheckerJob", upload1Start);
        checkJobInfo(jobs.get(1), "Experiment2", "TestableExperimentCheckerJob", experiment2Start);
        checkJobInfo(jobs.get(2), "uploaded_archive2.tar.gz", "TestableUploadCheckerJob", upload2Start);
        checkJobInfo(jobs.get(3), "Experiment3", "TestableExperimentCheckerJob", experiment3Start);
        checkJobInfo(jobs.get(4), "done_archive", "ArchiveCleanupJob", cleanupStart);
        checkJobInfo(jobs.get(5), "Experiment1", "TestableExperimentCheckerJob", experiment1Start);
        checkJobInfo(jobs.get(6), "anArchiveToLoad", "TestableLoaderJob", loaderStart);
    }

    @Test
    public void testGetScheduledJobsNone() throws SchedulerException {
        setUpForScheduledQcLiveJobs();
        setUpForScheduledLoaderJobs();
        final List<QcLiveJobInfo> scheduledJobs = liveMonitor.getScheduledJobs();
        assertNotNull(scheduledJobs);
        assertEquals(0, scheduledJobs.size());
    }

    @Test
    public void testGetScheduledJobsOne() throws SchedulerException {
        setUpForScheduledQcLiveJobs("file_name_for_upload.tar.gz");
        setUpForScheduledLoaderJobs(); // none
        final Date inTwoHours = addQcLiveExpectation("file_name_for_upload.tar.gz", UploadCheckerJob.class, 2);

        final List<QcLiveJobInfo> scheduledJobs = liveMonitor.getScheduledJobs();
        assertEquals(1, scheduledJobs.size());
        assertEquals("UploadCheckerJob", scheduledJobs.get(0).getJobType());
        assertEquals("file_name_for_upload.tar.gz", scheduledJobs.get(0).getJobName());
        assertEquals(inTwoHours, scheduledJobs.get(0).getStartTime());
    }

    @Test
    public void testGetScheduledJobsOneLoader() throws SchedulerException {
        setUpForScheduledQcLiveJobs(); // no qclive jobs
        setUpForScheduledLoaderJobs("archive_to_load");

        final Date inThreeHours = addLoaderExpectation("archive_to_load", LoaderJob.class, 3);
        final List<QcLiveJobInfo> scheduledJobs = liveMonitor.getScheduledJobs();
        assertEquals(1, scheduledJobs.size());
        checkJobInfo(scheduledJobs.get(0), "archive_to_load", "LoaderJob", inThreeHours);
    }

    @Test
    public void testGetScheduledJobs() throws SchedulerException {
        setUpForScheduledQcLiveJobs("running_experiment", "waiting_upload", "done_upload", "waiting_cleanup", "waiting_experiment");
        setUpForScheduledLoaderJobs("waiting_to_load_1", "waiting_to_load_2", "running_load");
        addQcLiveExpectation("running_experiment", ExperimentCheckerJob.class, -4);
        final Date waitingUploadTime = addQcLiveExpectation("waiting_upload", UploadCheckerJob.class, 2);
        addQcLiveExpectation("done_upload", UploadCheckerJob.class, -1);
        final Date waitingCleanupTime = addQcLiveExpectation("waiting_cleanup", ArchiveCleanupJob.class, 1);
        final Date waitingExperimentTime = addQcLiveExpectation("waiting_experiment", ExperimentCheckerJob.class, 6);
        final Date waitingLoader1Time = addLoaderExpectation("waiting_to_load_1", LoaderJob.class, 3);
        final Date waitingLoader2Time = addLoaderExpectation("waiting_to_load_2", LoaderJob.class, 7);
        addLoaderExpectation("running_load", LoaderJob.class, -6);

        final List<QcLiveJobInfo> scheduledJobs = liveMonitor.getScheduledJobs();
        assertEquals(5, scheduledJobs.size());
        final QcLiveJobInfo cleanupJobInfo = scheduledJobs.get(0);
        final QcLiveJobInfo uploadJobInfo = scheduledJobs.get(1);
        final QcLiveJobInfo loaderJob1Info = scheduledJobs.get(2);
        final QcLiveJobInfo experimentJobInfo = scheduledJobs.get(3);
        final QcLiveJobInfo loaderJob2Info = scheduledJobs.get(4);
        checkJobInfo(loaderJob1Info, "waiting_to_load_1", "LoaderJob", waitingLoader1Time);
        checkJobInfo(loaderJob2Info, "waiting_to_load_2", "LoaderJob", waitingLoader2Time);
        checkJobInfo(cleanupJobInfo, "waiting_cleanup", "ArchiveCleanupJob", waitingCleanupTime);
        checkJobInfo(uploadJobInfo, "waiting_upload", "UploadCheckerJob", waitingUploadTime);
        checkJobInfo(experimentJobInfo, "waiting_experiment", "ExperimentCheckerJob", waitingExperimentTime);
        
    }

    @Test
    public void testSuspendScheduler() throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockQcLiveScheduler).standby();
            one(mockLoaderScheduler).standby();
        }});
        liveMonitor.suspendScheduler();        
    }

    @Test
    public void testResumeScheduler() throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockQcLiveScheduler).start();
            one(mockLoaderScheduler).start();
        }});
        liveMonitor.resumeScheduler();
    }

    @Test
    public void testIsSchedulerRunningNo() throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockQcLiveScheduler).isInStandbyMode();
            will(returnValue(true));
            one(mockLoaderScheduler).isInStandbyMode();
            will(returnValue(true));
        }});
        assertFalse(liveMonitor.isSchedulerRunning());
    }

    @Test
    public void testIsSchedulerRunningYes() throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockQcLiveScheduler).isInStandbyMode();
            will(returnValue(false));
            one(mockLoaderScheduler).isInStandbyMode();
            will(returnValue(false));
        }});
        assertTrue(liveMonitor.isSchedulerRunning());
    }

    @Test
    public void testIsSchedulerRunningOneIs() throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockQcLiveScheduler).isInStandbyMode();
            will(returnValue(true));
            one(mockLoaderScheduler).isInStandbyMode();
            will(returnValue(false));
        }});
        assertFalse(liveMonitor.isSchedulerRunning());
    }


    private Date addQcLiveExpectation(final String jobName, final Class jobClass, final int startTimeOffsetFromNowInHours) throws SchedulerException {
        return addExpectation(mockQcLiveScheduler, Live.QUARTZ_JOB_GROUP, jobName, jobClass, startTimeOffsetFromNowInHours);
    }

    private Date addLoaderExpectation(final String jobName, final Class jobClass,
                                      final int startTimeOffsetFromNowInHours) throws SchedulerException {
        return addExpectation(mockLoaderScheduler, "loaderGroup", jobName, jobClass, startTimeOffsetFromNowInHours);
    }

    private Date addExpectation(final Scheduler scheduler, final String groupName, final String jobName, final Class jobClass,
                                final int startTimeOffsetFromNowInHours) throws SchedulerException {
        final Calendar start = Calendar.getInstance();
        start.add(Calendar.HOUR, startTimeOffsetFromNowInHours);
        final JobDetail jobDetail = new JobDetail(jobName, jobClass);
        final SimpleTrigger jobTrigger = new SimpleTrigger(jobName, groupName, start.getTime());
        if (startTimeOffsetFromNowInHours > 0) {
            jobTrigger.setNextFireTime(start.getTime());
        }

        context.checking(new Expectations() {{
            one(scheduler).getJobDetail(jobName, groupName);
            will(returnValue(jobDetail));
            one(scheduler).getTriggersOfJob(jobName, groupName);
            will(returnValue(new Trigger[]{jobTrigger}));
        }});
        return start.getTime();
    }



    private void checkJobInfo(final QcLiveJobInfo jobInfo, final String expectedName, final String expectedType, final Date fireTime) {
        assertEquals(expectedName, jobInfo.getJobName());
        assertEquals(expectedType, jobInfo.getJobType());
        assertEquals(fireTime, jobInfo.getStartTime());
    }

    private JobExecutionContext makeJobExecutionContext(final String jobName, final Job job, final Date fireTime) {
        final JobDetail jobDetail = new JobDetail(jobName, Live.QUARTZ_JOB_GROUP, job.getClass());
        final Trigger trigger = new SimpleTrigger(Live.QUARTZ_JOB_GROUP);
        final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(jobDetail, trigger, new BaseCalendar(), false, fireTime, null, null, null);
        return new JobExecutionContext(mockQcLiveScheduler, triggerFiredBundle, job);
    }

    private UploadCheckerJob makeUploadCheckerJob() {
        return new TestableUploadCheckerJob();
    }

    private ExperimentCheckerJob makeExperimentCheckerJob() {
        return new TestableExperimentCheckerJob();
    }

    class TestableUploadCheckerJob extends UploadCheckerJob {
        @Override
        protected void initFields() {
            // overridden to do nothing
        }
    }

    class TestableExperimentCheckerJob extends ExperimentCheckerJob {
        @Override
        protected void initFields() {
            // overridden to do nothing
        }
    }

    class TestableLoaderJob extends LoaderJob {
        @Override
        protected void initFields() {
            // overridden to do nothing
        }
    }
}
