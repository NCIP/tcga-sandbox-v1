package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DAMJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Fast test for DAMJobStatusServiceImpl.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DAMJobStatusServiceImplFastTest {

    private final Mockery context = new JUnit4Mockery();
    private QuartzJobHistoryService mockQuartzJobHistoryService;
    private FilePackagerEnqueuerI mockFilePackagerEnqueuer;
    private DAMJobStatusServiceImpl jobStatusService;
    private QuartzJobHistory quartzJobHistory;

    @Before
    public void setup() {
        mockQuartzJobHistoryService = context.mock(QuartzJobHistoryService.class);
        mockFilePackagerEnqueuer = context.mock(FilePackagerEnqueuerI.class);

        jobStatusService = new DAMJobStatusServiceImpl();
        jobStatusService.setQuartzJobHistoryService(mockQuartzJobHistoryService);
        jobStatusService.setFilePackagerEnqueuer(mockFilePackagerEnqueuer);
        quartzJobHistory = new QuartzJobHistory();

        jobStatusService.setSupportEmailAddress("support@test.test");
    }

    @Test
    public void testGetJobStatusQueued() throws Exception {
        quartzJobHistory.setStatus(QuartzJobStatus.Queued);
        context.checking(new Expectations() {{
            one(mockQuartzJobHistoryService).getPositionInQueue(quartzJobHistory);
            will(returnValue(5));
        }});

        runGetJobStatusTest("Queued", "There are 5 jobs in the queue ahead of you.", null);
    }

    @Test
    public void testGetJobStatusComplete() {
        quartzJobHistory.setStatus(QuartzJobStatus.Succeeded);
        quartzJobHistory.setLinkText("pretendThisIsURL");

        runGetJobStatusTest("Complete", "The archive you created is available at <a href=\"pretendThisIsURL\">pretendThisIsURL</a>",
                "pretendThisIsURL");
    }

    @Test
    public void testGetJobStatusRunning() {
        quartzJobHistory.setStatus(QuartzJobStatus.Started);

        runGetJobStatusTest("Running", "Your job is currently being processed.", null);
    }

    @Test
    public void testGetJobStatusFailed() {
        quartzJobHistory.setStatus(QuartzJobStatus.Failed);
        quartzJobHistory.setLinkText("something weird happened");

        runGetJobStatusTest("Failed", "There was an error creating your archive: something weird happened. Please contact support@test.test if you need assistance.", null);
    }

    @Test
    public void testGetJobStatusAccepted() {
        quartzJobHistory.setStatus(QuartzJobStatus.Accepted);
        runGetJobStatusTest("Submitted", "Your archive request has been submitted.", null);
    }

    @Test
    public void testGetJobStatusNoHistory() {
        context.checking(new Expectations() {{
            one(mockQuartzJobHistoryService).getQuartzJobHistory("abc123", FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
            will(returnValue(null));

            one(mockFilePackagerEnqueuer).getHoursTillDeletion();
            will(returnValue(5));
        }});

        final DAMJobStatus jobStatus = jobStatusService.getJobStatusForJobKey("abc123");
        assertEquals("Not Found", jobStatus.getStatus());
        assertEquals("The job requested was not found. Archives are removed after 5 hours from the time they're finished processing. If you have reached this status in error please contact support@test.test for more information.",
                jobStatus.getMessage());
    }

    private void runGetJobStatusTest(final String expectedStatus, final String expectedMessage, final String expectedUrl) {
        context.checking(new Expectations() {{
            one(mockQuartzJobHistoryService).getQuartzJobHistory("abc123", FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
            will(returnValue(quartzJobHistory));
        }});

        final DAMJobStatus jobStatus = jobStatusService.getJobStatusForJobKey("abc123");
        assertEquals(expectedStatus, jobStatus.getStatus());
        assertEquals(expectedMessage, jobStatus.getMessage());
        assertEquals(expectedUrl, jobStatus.getDownloadUrl());
    }

}
