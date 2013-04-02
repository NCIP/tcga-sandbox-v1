/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzQueueJobDetails;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerFactory;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.FilePackagerEnqueuer;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.QuartzJobHistoryService;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.QuartzQueueJobDetailsService;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * WSFilePackagerFactory unit test
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class WSFilePackagerFactoryFastTest {

    private static final String APP_CONTEXT_FILE = "samples/applicationContext-unittest.xml";
    private static final String WS_FILE_PACKAGER_FACTORY_APP_CONTEXT_BEAN_NAME = "wsFpFactory";
    private static final int TEST_QUARTZ_JOB_HISTORY_LIST_SIZE = 4;
    private static final String UUID_AS_STRING = "067e6162-3b6f-4ae2-a171-2470b63dff00";

    private Mockery mockery = new JUnit4Mockery();
    private WSFilePackagerFactory wsFilePackagerFactory;
    private Scheduler mockSmalljobScheduler;
    private Scheduler mockBigjobScheduler;
    private QuartzJobHistoryService mockQuartzJobHistoryService;
    private QuartzQueueJobDetailsService mockQuartzQueueJobDetailsService;

    @Before
    public void setUp() {

        mockSmalljobScheduler = mockery.mock(Scheduler.class, "mockSmalljobScheduler");
        mockBigjobScheduler = mockery.mock(Scheduler.class, "mockBigjobScheduler");
        mockQuartzJobHistoryService = mockery.mock(QuartzJobHistoryService.class);
        mockQuartzQueueJobDetailsService = mockery.mock(QuartzQueueJobDetailsService.class);

        final ApplicationContext appContext = new ClassPathXmlApplicationContext(APP_CONTEXT_FILE);
        wsFilePackagerFactory = (WSFilePackagerFactory) appContext.getBean(WS_FILE_PACKAGER_FACTORY_APP_CONTEXT_BEAN_NAME);
        wsFilePackagerFactory.setSmalljobScheduler(mockSmalljobScheduler);
        wsFilePackagerFactory.setBigjobScheduler(mockBigjobScheduler);
        wsFilePackagerFactory.setQuartzJobHistoryService(mockQuartzJobHistoryService);
        wsFilePackagerFactory.setQuartzQueueJobDetailsService(mockQuartzQueueJobDetailsService);
    }

    @Test
    public void testGetQuartzJobHistoryWhenJobFinished() {

        final UUID uuid = UUID.fromString(UUID_AS_STRING);
        final QuartzJobHistory pretendFinishedQuartzJobHistory = getPretendFinishedQuartzJobHistory(uuid);

        mockery.checking(new Expectations() {{

            one(mockQuartzQueueJobDetailsService).getQuartzSmallOrBigQueueJobDetails(uuid.toString(), FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
            will(returnValue(null));
            
            one(mockQuartzJobHistoryService).getQuartzJobHistory(uuid.toString(), FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
            will(returnValue(pretendFinishedQuartzJobHistory));
        }});

        final QuartzJobHistory quartzJobHistory = wsFilePackagerFactory.getQuartzJobHistory(uuid);

        assertSame("Unexpected QuartzJobHistory", quartzJobHistory, pretendFinishedQuartzJobHistory);
    }

    @Test
    public void testGetQuartzJobHistoryWhenDoesNotExist() {

        final UUID uuid = UUID.fromString(UUID_AS_STRING);

        mockery.checking(new Expectations() {{

            one(mockQuartzQueueJobDetailsService).getQuartzSmallOrBigQueueJobDetails(uuid.toString(), FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
            will(returnValue(null));

            one(mockQuartzJobHistoryService).getQuartzJobHistory(uuid.toString(), FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
            will(returnValue(null));
        }});

        final QuartzJobHistory quartzJobHistory = wsFilePackagerFactory.getQuartzJobHistory(uuid);

        assertNull(quartzJobHistory);
    }

    @Test
    public void testGetQuartzQueueJobDetailsExists() {

        final UUID uuid = UUID.fromString(UUID_AS_STRING);
        final QuartzQueueJobDetails pretendQuartzQueueJobDetails = getPretendQuartzQueueJobDetails(uuid);

        mockery.checking(new Expectations() {{

            one(mockQuartzQueueJobDetailsService).getQuartzSmallOrBigQueueJobDetails(uuid.toString(), FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
            will(returnValue(pretendQuartzQueueJobDetails));
        }});

        final QuartzQueueJobDetails quartzQueueJobDetails = wsFilePackagerFactory.getQuartzQueueJobDetails(uuid);

        assertSame("Unexpected QuartzQueueJobDetails", pretendQuartzQueueJobDetails, quartzQueueJobDetails);
    }

    @Test
    public void testGetQuartzQueueJobDetailsDoesNotExist() {

        final UUID uuid = UUID.fromString(UUID_AS_STRING);

        mockery.checking(new Expectations() {{

            one(mockQuartzQueueJobDetailsService).getQuartzSmallOrBigQueueJobDetails(uuid.toString(), FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
            will(returnValue(null));
        }});

        final QuartzQueueJobDetails quartzQueueJobDetails = wsFilePackagerFactory.getQuartzQueueJobDetails(uuid);

        assertNull(quartzQueueJobDetails);
    }

    @Test
    public void testInit() throws SchedulerException, NoSuchFieldException, IllegalAccessException {

        final Sequence sequence = mockery.sequence("init sequence");
        final Trigger[] nonEmptyTriggerArray = new Trigger[]{new SimpleTrigger()};

        mockery.checking(new Expectations() {{

            // restoreLivePackagersFromScheduler() for the 2 small jobs
            one(mockSmalljobScheduler).getJobNames(FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(returnValue(getJobNamesForSmalljobScheduler()));
            one(mockSmalljobScheduler).getTriggersOfJob(getJobNamesForSmalljobScheduler()[0], FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(returnValue(nonEmptyTriggerArray));
            one(mockSmalljobScheduler).getTriggersOfJob(getJobNamesForSmalljobScheduler()[1], FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(returnValue(nonEmptyTriggerArray));
            one(mockSmalljobScheduler).getJobDetail(getJobNamesForSmalljobScheduler()[0], FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(getActionForScheduler());
            one(mockSmalljobScheduler).getJobDetail(getJobNamesForSmalljobScheduler()[1], FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(getActionForScheduler());

            // restoreLivePackagersFromScheduler() for the 3 big jobs
            one(mockBigjobScheduler).getJobNames(FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(returnValue(getJobNamesForBigjobScheduler()));
            one(mockBigjobScheduler).getTriggersOfJob(getJobNamesForBigjobScheduler()[0], FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(returnValue(nonEmptyTriggerArray));
            one(mockBigjobScheduler).getTriggersOfJob(getJobNamesForBigjobScheduler()[1], FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(returnValue(nonEmptyTriggerArray));
            one(mockBigjobScheduler).getTriggersOfJob(getJobNamesForBigjobScheduler()[2], FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(returnValue(nonEmptyTriggerArray));
            one(mockBigjobScheduler).getJobDetail(getJobNamesForBigjobScheduler()[0], FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(getActionForScheduler());
            one(mockBigjobScheduler).getJobDetail(getJobNamesForBigjobScheduler()[1], FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(getActionForScheduler());
            one(mockBigjobScheduler).getJobDetail(getJobNamesForBigjobScheduler()[2], FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER); inSequence(sequence);
            will(getActionForScheduler());

            // restoreLivePackagersFromQuartzJobHistory()
            one(mockQuartzJobHistoryService).getAllQuartzJobHistory(); inSequence(sequence);
            will(returnValue(getTestQuartzJobHistoryList()));
        }});

        // Let's make the Map directly accessible for this test
        final Map<UUID, QuartzJobHistory> testLivePackagers = new HashMap<UUID, QuartzJobHistory>();
        final Field livePackagersField = FilePackagerFactory.class.getDeclaredField("livePackagers");
        livePackagersField.setAccessible(true);
        livePackagersField.set(wsFilePackagerFactory, testLivePackagers);

        // Run the method to unit test
        assertEquals("Unexpected size for Map of QuartzJobHistory", 0, testLivePackagers.size());
        wsFilePackagerFactory.init();
        assertEquals("Unexpected size for Map of QuartzJobHistory",
                // In this test, we are inserting 1 new QuartzJobHistory in the WSFilePackagerFactory livePackagers Map for each job names found,
                // as well as as many <code>QuartzJobHistory</code> found in the test List so we can expect as much here:
                getJobNamesForSmalljobScheduler().length + getJobNamesForBigjobScheduler().length + TEST_QUARTZ_JOB_HISTORY_LIST_SIZE, 
                testLivePackagers.size());
    }

    /**
     * Return a <code>List</code> of <code>QuartzJobHistory</code> for this test
     *
     * @return a <code>List</code> of <code>QuartzJobHistory</code> for this test
     */
    private List<QuartzJobHistory> getTestQuartzJobHistoryList() {

        final List<QuartzJobHistory> result = new LinkedList<QuartzJobHistory>();
        QuartzJobHistory quartzJobHistory;
        for(int i=0; i< TEST_QUARTZ_JOB_HISTORY_LIST_SIZE; i++) {

            quartzJobHistory = new QuartzJobHistory();
            quartzJobHistory.setJobName(UUID.randomUUID().toString());
            
            result.add(quartzJobHistory);
        }

        return result;
    }

    /**
     * Return the <code>Action</code> for the mock <code>Scheduler</code>
     *
     * @return the <code>Action</code> for the mock <code>Scheduler</code>
     */
    private Action getActionForScheduler() {

        return new CustomAction("actionForScheduler") {

            /**
             * Put a new <code>QuartzJobHistory</code> in the </code>WSFilePackagerFactory</code> livePackagers </code>Map</code>
             * and return a <code>JobDetail</code>
             *
             * @param invocation the <code>Invocation</code>
             * @return a <code>JobDetail</code>
             * @throws Throwable
             */
            @Override
            public Object invoke(final Invocation invocation) throws Throwable {

                wsFilePackagerFactory.putQuartzJobHistory(UUID.randomUUID(), new QuartzJobHistory());
                return new JobDetail();
            }
        };
    }

    /**
     * Return 2 job names for the mock small job <code>Scheduler</code>
     *
     * @return 2 job names for the mock small job <code>Scheduler</code>
     */
    private String[] getJobNamesForSmalljobScheduler() {
        return new String[]{"smallJob1", "smallJob2"};
    }

    /**
     * Return 3 job names for the mock big job <code>Scheduler</code>
     *
     * @return 3 job names for the mock big job <code>Scheduler</code>
     */
    private String[] getJobNamesForBigjobScheduler() {
        return new String[]{"bigJob1", "bigJob2", "bigJob3"};
    }

    /**
     * Return a dummy <code>QuartzJobHistory</code> with status 'Succeeded' and the given uuid as job name (filePackager group)
     *
     * @param uuid the <code>QuartzJobHistory</code> job name
     * @return a dummy <code>QuartzJobHistory</code> with status 'Succeeded' and the given uuid as job name (filePackager group)
     */
    private QuartzJobHistory getPretendFinishedQuartzJobHistory(final UUID uuid) {

        final QuartzJobHistory result = new QuartzJobHistory();
        result.setJobName(uuid.toString());
        result.setJobGroup(FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
        result.setStatus(QuartzJobStatus.Succeeded);

        return result;
    }

    private QuartzQueueJobDetails getPretendQuartzQueueJobDetails(final UUID uuid) {

        final String jobName = uuid.toString();
        final String jobGroup = "jobGroup";
        final String description = "decription";
        final String jobClassName = "job.class.name";
        final boolean isDurable = true;
        final boolean isVolatile = true;
        final boolean isStateFul = true;
        final boolean requestsRecovery = true;
        final Blob jobData = null;

        final QuartzQueueJobDetails result = new QuartzQueueJobDetails(
                jobName,
                jobGroup,
                description,
                jobClassName,
                isDurable,
                isVolatile,
                isStateFul,
                requestsRecovery,
                jobData
        );

        return result;
    }

}//End of Class
