/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveDeletionBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;
import junit.framework.Assert;
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
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

/**
 * Test for FilePackagerEnqueuer
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class FilePackagerEnqueuerFastTest {

    private static final String appContextFile = "samples/applicationContext-unittest.xml";
    private Mockery context = new JUnit4Mockery();
    private FilePackagerEnqueuer filePackagerEnqueuer;
    private ApplicationContext appContext;
    private Scheduler bigJobQueue;
    private Scheduler smallJobQueue;
    private QuartzJobHistoryService mockJobHistoryService;

    @Before
    public void setup() {

        appContext = new ClassPathXmlApplicationContext(appContextFile);
        bigJobQueue = context.mock(Scheduler.class, "big");
        smallJobQueue = context.mock(Scheduler.class, "small");
        mockJobHistoryService = context.mock(QuartzJobHistoryService.class);

        filePackagerEnqueuer = (FilePackagerEnqueuer) appContext.getBean("filePackagerEnqueuer");

        filePackagerEnqueuer.setSmalljobScheduler(smallJobQueue);
        filePackagerEnqueuer.setBigjobScheduler(bigJobQueue);

        filePackagerEnqueuer.setSmallJobQueueMaxBytes(1000);

        filePackagerEnqueuer.setQuartzJobHistoryService(mockJobHistoryService);
    }

    @Test
    public void testQueueFilePackagerSmallJob() throws Exception {

        final FilePackagerBean filePackagerBean = getTestFilePackagerBean();
        filePackagerBean.setPriorityAdjustedEstimatedUncompressedSize(10L);

        context.checking(new Expectations() {{
            one(smallJobQueue).scheduleJob(with(getJobDetail(JobType.FilePackagerJob)), with(getTrigger(JobType.FilePackagerJob)));
            one(mockJobHistoryService).persist(with(any(QuartzJobHistory.class)));
        }});

        filePackagerEnqueuer.queueFilePackagerJob(filePackagerBean);
        checkQuartzJobHistory(filePackagerBean.getQuartzJobHistory(), QuartzJobStatus.Queued, FilePackagerEnqueuer.QUEUE_NAME_SMALL_JOB);
        assertNotNull(filePackagerBean.getQuartzJobHistory().getEnqueueDate());
    }

    @Test
    public void testQueueFilePackagerBigJob() throws SchedulerException {

        final FilePackagerBean filePackagerBean = getTestFilePackagerBean();
        filePackagerBean.setPriorityAdjustedEstimatedUncompressedSize(1000000L);

        context.checking(new Expectations() {{
            one(bigJobQueue).scheduleJob(with(getJobDetail(JobType.FilePackagerJob)), with(getTrigger(JobType.FilePackagerJob)));
            one(mockJobHistoryService).persist(with(any(QuartzJobHistory.class)));
        }});

        filePackagerEnqueuer.queueFilePackagerJob(filePackagerBean);
        checkQuartzJobHistory(filePackagerBean.getQuartzJobHistory(), QuartzJobStatus.Queued, FilePackagerEnqueuer.QUEUE_NAME_BIG_JOB);
        assertNotNull(filePackagerBean.getQuartzJobHistory().getEnqueueDate());
    }

    @Test
    public void testQueueArchiveDeletionJob() throws SchedulerException {

        final ArchiveDeletionBean archiveDeletionBean = new ArchiveDeletionBean();
        archiveDeletionBean.setArchiveName("archive");

        context.checking(new Expectations() {{
            one(smallJobQueue).scheduleJob(with(getJobDetail(JobType.ArchiveDeletionJob)), with(getTrigger(JobType.ArchiveDeletionJob)));
        }});

        final Date beforeEnqueueingDate = new Date();
        final Date archiveDeletionTriggerDate = filePackagerEnqueuer.queueArchiveDeletionJob("archive", false);

        assertFalse(new StringBuilder("Archive deletion trigger date is earlier than expected: ")
                .append(archiveDeletionTriggerDate.getTime())
                .append(" < ")
                .append(beforeEnqueueingDate.getTime())
                .toString(),
                archiveDeletionTriggerDate.before(beforeEnqueueingDate));
    }

    @Test
    public void testQueueQuartzJobHistoryDeletionJob() throws SchedulerException {

        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
        final Date triggerDate = new Date();

        context.checking(new Expectations() {{
            one(smallJobQueue).scheduleJob(with(getJobDetail(JobType.QuartzJobHistoryDeletionJob)), with(getTrigger(JobType.QuartzJobHistoryDeletionJob)));
        }});

        filePackagerEnqueuer.queueQuartzJobHistoryDeletionJob(quartzJobHistory, triggerDate);
    }

    @Test
    public void testQueueSmallJobWithSchedulerException() throws SchedulerException {
        final FilePackagerBean filePackagerBean = getTestFilePackagerBean();
        filePackagerBean.setPriorityAdjustedEstimatedUncompressedSize(10L);

        context.checking(new Expectations() {{
            one(smallJobQueue).scheduleJob(with(getJobDetail(JobType.FilePackagerJob)), with(getTrigger(JobType.FilePackagerJob)));
            will(throwException(new SchedulerException("fake exception")));
            one(mockJobHistoryService).persist(with(any(QuartzJobHistory.class)));
        }});

        try {
            filePackagerEnqueuer.queueFilePackagerJob(filePackagerBean);
            fail("exception should have been thrown");
        } catch (SchedulerException e) {
            // expected since scheduler threw it, but quartz history should still have correct status
            checkQuartzJobHistory(filePackagerBean.getQuartzJobHistory(), QuartzJobStatus.Failed, null);
            assertEquals("There was an error scheduling the job: fake exception", filePackagerBean.getQuartzJobHistory().getLinkText());
        }

    }

    private void checkQuartzJobHistory(final QuartzJobHistory quartzJobHistory, final QuartzJobStatus expectedStatus,
                                       final String expectedQueueName) {

        assertEquals(expectedStatus, quartzJobHistory.getStatus());
        assertEquals(expectedQueueName, quartzJobHistory.getQueueName());
    }

    /**
     * Return a <code>FilePackagerBean</code> for this test
     *
     * @return a <code>FilePackagerBean</code> for this test
     */
    private FilePackagerBean getTestFilePackagerBean() {

        final FilePackagerBean result =  new FilePackagerBean();
        result.setArchivePhysicalName("archive");
        result.setKey(UUID.randomUUID());

        return result;
    }

    /**
     * Return a Matcher<JobDetail> for the given <code>JobType</code>
     *
     * @param jobType the <code>JobType</code>
     * @return a Matcher<JobDetail> for the given <code>JobType</code>
     */
    private Matcher<JobDetail> getJobDetail(final JobType jobType) {

        return new TypeSafeMatcher<JobDetail>() {

            @Override
            public boolean matchesSafely(final JobDetail jobDetail) {

                boolean result = false;

                switch(jobType) {
                    case FilePackagerJob:
                        result = isValidJobDetailForFilePackagerJob(jobDetail);
                        break;
                    case ArchiveDeletionJob:
                        result = isValidJobDetailForArchiveDeletionJob(jobDetail);
                        break;
                    case QuartzJobHistoryDeletionJob:
                        result = isValidJobDetailForQuartzJobHistoryDeletionJob(jobDetail);
                        break;
                }

                return result;
            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    /**
     * Return a Matcher<Trigger> for the given <code>JobType</code>
     *
     * @param jobType the <code>JobType</code>
     * @return a Matcher<Trigger> for the given <code>JobType</code>
     */
    private Matcher<Trigger> getTrigger(final JobType jobType) {
        return new TypeSafeMatcher<Trigger>() {

            @Override
            public boolean matchesSafely(final Trigger trigger) {

                boolean result = isValidTrigger(trigger, jobType);

                switch (jobType) {
                    case ArchiveDeletionJob:
                    case QuartzJobHistoryDeletionJob:
                        result &= trigger.getPriority() == FilePackagerEnqueuer.PRIORITY_DELETION;
                        break;
                }

                return result;
            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    /**
     * Return <code>true</code> if the given <code>JobDetail</code> is valid for a <code>FilePackagerJob</code>, <code>false</code> otherwise
     *
     * @param jobDetail the <code>JobDetail</code> to check
     * @return <code>true</code> if the given <code>JobDetail</code> is valid for a <code>FilePackagerJob</code>, <code>false</code> otherwise
     */
    private boolean isValidJobDetailForFilePackagerJob(final JobDetail jobDetail) {

        return jobDetail.getJobDataMap().get(JobDelegate.DATA_BEAN) instanceof FilePackagerBean
                && FilePackagerEnqueuer.SPRING_BEAN_NAME_FOR_FILEPACKAGER_JOB.equals((String) jobDetail.getJobDataMap().get(JobDelegate.JOB_BEAN_NAME))
                && FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER.equals(jobDetail.getGroup())
                && gov.nih.nci.ncicb.tcga.dcc.dam.service.JobDelegate.class.equals(jobDetail.getJobClass());
    }

    /**
     * Return <code>true</code> if the given <code>JobDetail</code> is valid for a <code>ArchiveDeletionJob</code>, <code>false</code> otherwise
     *
     * @param jobDetail the <code>JobDetail</code> to check
     * @return <code>true</code> if the given <code>JobDetail</code> is valid for a <code>ArchiveDeletionJob</code>, <code>false</code> otherwise
     */
    private boolean isValidJobDetailForArchiveDeletionJob(final JobDetail jobDetail) {

        return jobDetail.getJobDataMap().get(JobDelegate.DATA_BEAN) instanceof ArchiveDeletionBean
                && FilePackagerEnqueuer.SPRING_BEAN_NAME_FOR_ARCHIVE_DELETION_JOB.equals((String) jobDetail.getJobDataMap().get(JobDelegate.JOB_BEAN_NAME))
                && FilePackagerEnqueuer.JOB_GROUP_ARCHIVE_DELETION.equals(jobDetail.getGroup())
                && gov.nih.nci.ncicb.tcga.dcc.dam.service.JobDelegate.class.equals(jobDetail.getJobClass());
    }

    /**
     * Return <code>true</code> if the given <code>JobDetail</code> is valid for a <code>QuartzJobHistoryDeletionJob</code>, <code>false</code> otherwise
     *
     * @param jobDetail the <code>JobDetail</code> to check
     * @return <code>true</code> if the given <code>JobDetail</code> is valid for a <code>QuartzJobHistoryDeletionJob</code>, <code>false</code> otherwise
     */
    private boolean isValidJobDetailForQuartzJobHistoryDeletionJob(final JobDetail jobDetail) {

        return jobDetail.getJobDataMap().get(JobDelegate.DATA_BEAN) instanceof QuartzJobHistory
                && FilePackagerEnqueuer.SPRING_BEAN_NAME_FOR_QUARTZ_JOB_HISTORY_DELETION_JOB.equals((String) jobDetail.getJobDataMap().get(JobDelegate.JOB_BEAN_NAME))
                && FilePackagerEnqueuer.JOB_GROUP_QUARTZ_JOB_HISTORY_DELETION.equals(jobDetail.getGroup())
                && gov.nih.nci.ncicb.tcga.dcc.dam.service.JobDelegate.class.equals(jobDetail.getJobClass());
    }


    /**
     * Return <code>true</code> if the trigger is valid for the given <code>JobType</code>, <code>true</code> otherwise
     *
     * @param trigger the <code>Trigger</code> to check
     * @param jobType the <code>JobType</code> this trigger is for
     * @return <code>true</code> if the trigger is valid for the given <code>JobType</code>, <code>true</code> otherwise
     */
    private boolean isValidTrigger(final Trigger trigger, final JobType jobType) {

        boolean result = false;

        final SimpleTriggerBean simpleTrigger = (SimpleTriggerBean) trigger;
        final JobDetail jobDetail = simpleTrigger.getJobDetail();

        switch (jobType) {
            case FilePackagerJob:
                result = isValidJobDetailForFilePackagerJob(jobDetail);
                break;
            case ArchiveDeletionJob:
                result = isValidJobDetailForArchiveDeletionJob(jobDetail);
                break;
            case QuartzJobHistoryDeletionJob:
                result = isValidJobDetailForQuartzJobHistoryDeletionJob(jobDetail);
                break;
        }

        result &= simpleTrigger.getName().equals(jobDetail.getName())
                && simpleTrigger.getGroup().equals(jobDetail.getGroup())
                && simpleTrigger.getJobGroup().equals(jobDetail.getGroup());

        return result;
    }

    /**
     * An enumeration of possible job types
     */
    private enum JobType {

        FilePackagerJob,
        ArchiveDeletionJob,
        QuartzJobHistoryDeletionJob
    }
}
