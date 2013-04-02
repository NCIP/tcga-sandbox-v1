/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ArchiveBase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for LiveScheduler
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class LiveSchedulerFastTest {
    private static final String TEST_ARCHIVE = "testarchive.tar.gz";

    private final Mockery context = new JUnit4Mockery();
    private final Scheduler mockScheduler = context.mock(Scheduler.class, "live_scheduler");
    private final Scheduler mockLoaderScheduler = context.mock(Scheduler.class, "loader_scheduler");
    private Calendar now;
    private LiveScheduler liveScheduler;
    private final JobDetail cleanupJob = new JobDetail();
    private final Trigger cleanupTrigger = new SimpleTrigger();
    private static final String TEST_GROUP = "TEST";
    private static final String EXPERIMENT_1 = "experiment1";
    private static final String ARCHIVE_PATH = "location";
    private final Archive archive = new Archive();
    private final JobDetail clinicalJobDetail = new JobDetail();    
    private List<JobDetail> createdJobDetails;
    private List<Trigger> createdTriggers;
    private QcLiveStateBean stateContext;
    @Before
    public void setup() throws Exception {
        createdJobDetails = new ArrayList<JobDetail>();
        createdTriggers = new ArrayList<Trigger>();

        now = Calendar.getInstance();
        archive.setDepositLocation(ARCHIVE_PATH);
        liveScheduler = new LiveScheduler() {
            @Override
            public JobDetail getClinicalLoadJobDetail() {
                return makeEmptyJob();
            }

            @Override
            public Trigger getClinicalLoaderTrigger() {
                return makeEmptyTrigger();
            }

            @Override
            public SimpleTriggerBean getExperimentTrigger() {
                return makeEmptyTrigger();
            }

            @Override
            public SimpleTriggerBean getUploadTrigger() {
                return makeEmptyTrigger();
            }

            public JobDetail getExperimentJobDetail() {
                return makeEmptyJob();
            }

            @Override
            public JobDetail getUploadJobDetail() {
                return makeEmptyJob();
            }			
					
        };
        liveScheduler.setScheduler(mockScheduler);        
        liveScheduler.setUploadScheduler(mockScheduler);
        liveScheduler.setCleanupArchiveJobDetail(cleanupJob);
        liveScheduler.setCleanupArchiveTrigger(cleanupTrigger);
        liveScheduler.setLoaderScheduler(mockLoaderScheduler);
        stateContext = new QcLiveStateBean();
        stateContext.setTransactionId(1l);
    }

    private JobDetail makeEmptyJob() {
        JobDetail job = new JobDetail();
        job.setGroup(TEST_GROUP);
        createdJobDetails.add(job);
        return job;
    }

    private SimpleTriggerBean makeEmptyTrigger() {
        SimpleTriggerBean trigger = new SimpleTriggerBean();
        trigger.setGroup(TEST_GROUP);
        createdTriggers.add(trigger);
        return trigger;
    }

    @Test
    public void testScheduleFirstCheck() throws SchedulerException {
        context.checking(new Expectations() {{
            atMost(2).of(mockScheduler).getJobDetail(EXPERIMENT_1, EXPERIMENT_1);
            will(returnValue(null));
            one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
        }});


        liveScheduler.scheduleExperimentCheck(EXPERIMENT_1, "CGCC", TEST_ARCHIVE, now,stateContext,EXPERIMENT_1);

        assertEquals(1, createdJobDetails.size());
        assertEquals(1, createdTriggers.size());
        JobDetail job = createdJobDetails.get(0);
        Trigger trigger = createdTriggers.get(0);

        // job data map should contain a runner and the right variables
        assertEquals(EXPERIMENT_1, job.getName());
        assertTrue(trigger.getName().contains(EXPERIMENT_1));
        assertEquals(EXPERIMENT_1, trigger.getJobName());
        assertEquals(EXPERIMENT_1, createdJobDetails.get(0).getJobDataMap().get("experimentName"));
        assertEquals("CGCC", createdJobDetails.get(0).getJobDataMap().get("experimentType"));
        assertEquals(new Long(1),((QcLiveStateBean)(createdJobDetails.get(0).getJobDataMap().get(LiveScheduler.STATE_CONTEXT))).getTransactionId());
    }


     @Test
    public void testScheduleCheckWithExistingJob() throws SchedulerException {
        final JobDetail existingJob = new JobDetail();
        context.checking(new Expectations() {{
            one(mockScheduler).getJobDetail(EXPERIMENT_1, EXPERIMENT_1);
            will(returnValue(existingJob));    
            one(mockScheduler).scheduleJob(with(any(Trigger.class)));       
        }});
        liveScheduler.scheduleExperimentCheck(EXPERIMENT_1, "CGCC", TEST_ARCHIVE, now,stateContext,EXPERIMENT_1);
        assertEquals(1, createdTriggers.size());
        assertTrue(createdTriggers.get(0).getName().contains(EXPERIMENT_1));
        assertEquals(EXPERIMENT_1, createdTriggers.get(0).getJobName());
        assertEquals(new Long(1),((QcLiveStateBean)(createdJobDetails.get(0).getJobDataMap().get(LiveScheduler.STATE_CONTEXT))).getTransactionId());
    }

    @Test
    public void testScheduleCheckFails() {
        try {
            context.checking(new Expectations() {{
                atMost(2).of(mockScheduler).getJobDetail(EXPERIMENT_1, EXPERIMENT_1);
                will(returnValue(null));
                one(mockScheduler).scheduleJob(with(jobInCreatedList()), with(triggerInCreatedList()));
                //noinspection ThrowableInstanceNeverThrown
                will(throwException(new SchedulerException()));
            }});

            liveScheduler.scheduleExperimentCheck(EXPERIMENT_1, "CGCC", TEST_ARCHIVE, now,stateContext,EXPERIMENT_1);
            fail();
        } catch (SchedulerException e) {
            // expected
        }
    }

    @Test
    public void testScheduleClinicalLoader() {
        try {
            List<Archive> archiveList = new ArrayList<Archive>();
            archiveList.add(archive);
            JobDataMap map = new JobDataMap();
            map.put("clinicalArchives", archiveList);
            clinicalJobDetail.setJobDataMap(map);

            context.checking(new Expectations() {{
                allowing(mockLoaderScheduler).getJobDetail("CLINICAL_LOADER-someExperimentName", "CLINICAL_LOADER-someExperimentName");
                will(returnValue(clinicalJobDetail));      
                one(mockLoaderScheduler).scheduleJob(with(any(SimpleTrigger.class)));          
            }});

            liveScheduler.scheduleClinicalLoader(archiveList, Calendar.getInstance(), "someExperimentName",stateContext);
        } catch (SchedulerException e) {
            fail();
        }

    }
    
   

    @Test
    public void testScheduleCleanup() throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockScheduler).scheduleJob(cleanupJob, cleanupTrigger);
        }});
        liveScheduler.scheduleArchiveCleanup(archive, true);
        assertTrue((Boolean) cleanupJob.getJobDataMap().get(ArchiveCleanupJob.ARCHIVE_FAILED));
        assertEquals(archive.getDepositLocation(), ((ArchiveBase) cleanupJob.getJobDataMap().get(ArchiveCleanupJob.ARCHIVE)).getDepositLocation());
    }

    @Test
    public void testScheduleCleanupFails() {

        try {
            context.checking(new Expectations() {{
                one(mockScheduler).scheduleJob(cleanupJob, cleanupTrigger);
                //noinspection ThrowableInstanceNeverThrown
                will(throwException(new SchedulerException()));
            }});
            liveScheduler.scheduleArchiveCleanup(archive, true);
            fail();
        } catch (SchedulerException e) {
            // expected
        }
    }

    @Test
    public void testScheduleArchiveCleanupWhenJobWithSameNameAlreadyExists() throws SchedulerException, InterruptedException {

        final Set<String> jobNames = new HashSet<String>();
        final Set<String> triggerNames = new HashSet<String>();

        context.checking(new Expectations() {{
            one(mockScheduler).scheduleJob(with(expectedCleanupJobDetail(jobNames)), with(expectedCleanupTrigger(triggerNames)));
            one(mockScheduler).scheduleJob(with(expectedCleanupJobDetail(jobNames)), with(expectedCleanupTrigger(triggerNames)));
        }});

        liveScheduler.scheduleArchiveCleanup(archive, false);
        Thread.sleep(1);
        liveScheduler.scheduleArchiveCleanup(archive, false);
    }

    /**
     * Return a matcher that matches any {@link JobDetail} which name does not already exist.
     *
     * @param jobNames the existing job names
     * @return a matcher that matches any {@link JobDetail} which name does not already exist
     */
    private static TypeSafeMatcher<JobDetail> expectedCleanupJobDetail(final Set<String> jobNames) {

        return new TypeSafeMatcher<JobDetail>() {

            @Override
            public boolean matchesSafely(final JobDetail jobDetail) {

                assertNotNull(jobDetail);
                final String jobName = jobDetail.getName();
                return jobNames.add(jobName);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Cleanup JobDetail matches expectations");
            }
        };
    }

    /**
     * Return a matcher that matches any {@link Trigger} which name does not already exist.
     *
     * @param triggerNames the existing trigger names
     * @return a matcher that matches any {@link Trigger} which name does not already exist
     */
    private static TypeSafeMatcher<Trigger> expectedCleanupTrigger(final Set<String> triggerNames) {

        return new TypeSafeMatcher<Trigger>() {

            @Override
            public boolean matchesSafely(final Trigger trigger) {

                assertNotNull(trigger);
                final String triggerName = trigger.getName();
                return triggerNames.add(triggerName);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Cleanup Trigger matches expectations");
            }
        };
    }

    @Test
    public void testScheduleUploadCheck() throws SchedulerException, IOException {
        final File uploadFile = new File("path" + File.separator + "test");
        context.checking(new Expectations() {{

            one(mockScheduler).getJobDetail(uploadFile.getCanonicalPath(), TEST_GROUP);
            will(returnValue(null));
            // because no above calls returned null, should use created trigger and job
            one(mockScheduler).scheduleJob(with(jobInCreatedList()), with(triggerInCreatedList()));
        }});

        liveScheduler.scheduleUploadCheck(uploadFile, Calendar.getInstance(),stateContext);
        assertEquals(1, createdTriggers.size());
        assertEquals(1, createdJobDetails.size());
        assertEquals(uploadFile.getCanonicalPath(), createdJobDetails.get(0).getName());
        assertTrue(createdTriggers.get(0).getName().contains(uploadFile.getCanonicalPath()));
        assertEquals(uploadFile.getCanonicalPath(), createdTriggers.get(0).getJobName());
        assertEquals(new Long(1),((QcLiveStateBean)(createdJobDetails.get(0).getJobDataMap().get(LiveScheduler.STATE_CONTEXT))).getTransactionId());
    }

    @Test
    public void testSynchronizedScheduleCalls() throws Exception {

        // these are Actions that JMock will run
        final RecordTime stallJob2TenMilliseconds = new RecordTime(10); // stall 10 milliseconds
        final RecordTime stallJob1OneSecond = new RecordTime(1000); // stall 1 second

        final Trigger trigger1 = new SimpleTrigger("test1", TEST_GROUP);
        final Trigger trigger2 = new SimpleTrigger("test2", TEST_GROUP);
        final File uploadFile1 = new File("test1");
        final File uploadFile2 = new File("test2");

        context.checking(new Expectations() {{
            // first job will stall 1000 milliseconds
            one(mockScheduler).getJobDetail(uploadFile1.getCanonicalPath(), TEST_GROUP);
            will(returnValue(null));
            one(mockScheduler).scheduleJob(with(jobInCreatedList()), with(getTrigger(uploadFile1.getCanonicalPath())));
            will(stallJob1OneSecond);

            // second job will stall 10 milliseconds
            one(mockScheduler).getJobDetail(uploadFile2.getCanonicalPath(), TEST_GROUP);
            will(returnValue(null));
            one(mockScheduler).scheduleJob(with(jobInCreatedList()), with(getTrigger(uploadFile2.getCanonicalPath())));
            will(stallJob2TenMilliseconds);
        }});

        // start 1 and 2 at nearly the same time
        UploadSchedulerThread scheduler1 = new UploadSchedulerThread(uploadFile1);
        UploadSchedulerThread scheduler2 = new UploadSchedulerThread(uploadFile2);
        Thread thread1 = new Thread(scheduler1);
        Thread thread2 = new Thread(scheduler2);
        thread1.start();
        Thread.sleep(5 * 1000);
        thread2.start();

        while (thread1.getState() != Thread.State.TERMINATED || thread2.getState() != Thread.State.TERMINATED) {
            Thread.sleep(1000);
        }

        // I don't understand why I need to put this here when I have @RunWith(JMock.class)...
        context.assertIsSatisfied();

        // check that the 2nd job started after the 1st one finished
        assertTrue("2nd job not invoked after 1st! (2nd invoked at " +
                stallJob2TenMilliseconds.getInvocationTime() + ", 1st at " + stallJob1OneSecond.getInvocationTime() + ")",
                stallJob2TenMilliseconds.getInvocationTime() >= stallJob1OneSecond.getInvocationTime());
        //noinspection ThrowableResultOfMethodCallIgnored
        assertNull("Scheduler 1 threw exception: " + scheduler1.getRecordedException(),
                scheduler1.getRecordedException());
        //noinspection ThrowableResultOfMethodCallIgnored
        assertNull("Scheduler 2 threw exception: " + scheduler2.getRecordedException(),
                scheduler2.getRecordedException());

    }

    // thread that calls scheduleUploadCheck

    class UploadSchedulerThread implements Runnable {
        private File uploadFile;
        private Throwable exception = null;

        UploadSchedulerThread(final File file) {
            uploadFile = file;
        }

        public void run() {
            try {
                liveScheduler.scheduleUploadCheck(uploadFile, Calendar.getInstance(),stateContext);
            } catch (Throwable e) {
                exception = e;
            }
        }

        Throwable getRecordedException() {
            return exception;
        }
    }

    // a class that implements Action... records the time it was invoked, after stalling the specified number of milliseconds

    class RecordTime implements Action {
        private long invocationTime = 0;
        private int stallLength; // milliseconds

        public RecordTime(final int stall) {
            stallLength = stall;
        }

        public void describeTo(final Description description) {
            description.appendText("records the time of invocation");
        }

        public Object invoke(final Invocation invocation) throws Throwable {
            Thread.sleep(stallLength);
            invocationTime = System.currentTimeMillis();
            return null;
        }

        long getInvocationTime() {
            return invocationTime;
        }
    }

    public class TriggerInCreatedList extends TypeSafeMatcher<Trigger> {
        @Override
        public boolean matchesSafely(final Trigger t) {
            return createdTriggers.contains(t);
        }

        public void describeTo(final Description description) {
            description.appendText("a trigger in the created list ");
        }
    }

    public Matcher<Trigger> triggerInCreatedList() {
        return new TriggerInCreatedList();
    }

    private Matcher<Trigger> getTrigger(final String jobName) {
        return new org.junit.internal.matchers.TypeSafeMatcher<Trigger>() {

            @Override
            public boolean matchesSafely(final Trigger trigger) {

                boolean result = trigger.getName().contains(jobName);
                return result;
            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    public class JobInCreatedList extends TypeSafeMatcher<JobDetail> {
        @Override
        public boolean matchesSafely(final JobDetail jobDetail) {
            return createdJobDetails.contains(jobDetail);
        }

        public void describeTo(final Description description) {
            description.appendText("job in the created list");
        }
    }

    public Matcher<JobDetail> jobInCreatedList() {
        return new JobInCreatedList();
    }


}
