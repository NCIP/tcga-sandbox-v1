package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import static org.junit.Assert.fail;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLoggerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerI;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Level;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test class for FilePackagerJob
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FilePackagerJobFastTest {

    private static final String appContextFile = "samples/applicationContext-unittest.xml";
    private final ApplicationContext appContext = new ClassPathXmlApplicationContext(appContextFile);

    private Mockery mocker = new JUnit4Mockery();
    private FilePackagerEnqueuerI mockFilePackagerEnqueuer;
    private FilePackagerI mockFilePackager;
    private FilePackagerFactoryI mockFilePackagerFactory;
    private FilePackagerFactoryI mockwsFilePackagerFactory;
    private ProcessLoggerI mockLogger;
    private FilePackagerJob filePackagerJob;

    @Before
    public void setup() {
        filePackagerJob = (FilePackagerJob) appContext.getBean("filePackagerJob");
        mockFilePackagerEnqueuer = mocker.mock(FilePackagerEnqueuerI.class);
        mockFilePackager = mocker.mock(FilePackagerI.class);
        mockFilePackagerFactory = mocker.mock(FilePackagerFactoryI.class, "FilePackagerFactory");
        mockwsFilePackagerFactory = mocker.mock(FilePackagerFactoryI.class, "WSFilePackagerFactory");
        mockLogger = mocker.mock(ProcessLoggerI.class, "logger");
        filePackagerJob.setFilePackagerEnqueuer(mockFilePackagerEnqueuer);
        filePackagerJob.setFilePackagerFactory(mockFilePackagerFactory);
        filePackagerJob.setWsFilePackagerFactory(mockwsFilePackagerFactory);
        filePackagerJob.setFilePackager(mockFilePackager);
        filePackagerJob.setLogger(mockLogger);
    }

    @Test
    public void runValidJob() throws Exception {

        final Date now = new Date();
        final FilePackagerBean filePackagerBean = getTestFilePackagerBean(now);

        final Sequence sequence = mocker.sequence("runJobSequence");

        mocker.checking(new Expectations() {{
            one(mockLogger).logToLogger(with(Level.INFO), with(getExpectedValidLog())); inSequence(sequence);
            one(mockFilePackagerFactory).putQuartzJobHistory(filePackagerBean.getKey(), filePackagerBean.getUpdatedQuartzJobHistory()); inSequence(sequence);
            one(mockwsFilePackagerFactory).putQuartzJobHistory(filePackagerBean.getKey(), filePackagerBean.getUpdatedQuartzJobHistory()); inSequence(sequence);
            one(mockFilePackager).runJob(filePackagerBean); inSequence(sequence);
            one(mockFilePackagerFactory).putQuartzJobHistory(filePackagerBean.getKey(), filePackagerBean.getUpdatedQuartzJobHistory()); inSequence(sequence);
            one(mockwsFilePackagerFactory).putQuartzJobHistory(filePackagerBean.getKey(), filePackagerBean.getUpdatedQuartzJobHistory()); inSequence(sequence);
            one(mockFilePackagerEnqueuer).queueArchiveDeletionJob(
                    File.separator + filePackagerBean.getArchivePhysicalPathPrefix() + filePackagerBean.getArchivePhysicalName() + ".tar.gz",
                    filePackagerBean.isFailed());
            will(returnValue(now)); inSequence(sequence);
            one(mockFilePackagerEnqueuer).queueQuartzJobHistoryDeletionJob(filePackagerBean.getUpdatedQuartzJobHistory(), now); inSequence(sequence);
            one(mockLogger).logToLogger(with(Level.INFO), with(getExpectedValidLog())); inSequence(sequence);
        }});

        try {
            filePackagerJob.run(filePackagerBean);

        } catch (final Exception unexpected) {

            fail("Unexpected exception was raised: " + unexpected.getMessage());

            //re-throw exception
            throw unexpected;
        }
    }

    @Test
    public void runInvalidJob() {

        mocker.checking(new Expectations() {{
            one(mockLogger).logToLogger(with(Level.ERROR), with(getExpectedExceptionLog()));

        }});

        try {
            filePackagerJob.run(null);
            fail("Expected exception wasn't raised");

        } catch (final Exception expected) {
        }
    }

    /**
     * Return a <code>FilePackagerBean</code> for this test
     *
     * @param archiveDeletionJobTriggerDate the <code>ArchiveDeletionJob</code> trigger date
     * @return a <code>FilePackagerBean</code> for this test
     */
    private FilePackagerBean getTestFilePackagerBean(final Date archiveDeletionJobTriggerDate) {

        final FilePackagerBean filePackagerBean = new FilePackagerBean();
        filePackagerBean.setKey(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff01"));
        filePackagerBean.setArchivePhysicalPathPrefix("/path/");
        filePackagerBean.setArchiveLinkSite("https://tcga-data.nci.nih.gov/tcga/blah/blahblah/");
        filePackagerBean.setArchivePhysicalName("test-archive");
        filePackagerBean.setJobWSSubmissionDate(new Date());
        filePackagerBean.setEstimatedUncompressedSize(999L);

        //Initialize the FilePackagerBean's QuartzJobHistory
        filePackagerBean.createQuartzJobHistory(getTestJobDetail(), getTestSimpleTrigger(archiveDeletionJobTriggerDate));

        return filePackagerBean;
    }

    /**
     * Return a <code>SimpleTrigger</code> for this test
     *
     * @param archiveDeletionJobTriggerDate the <code>ArchiveDeletionJob</code> trigger date
     * @return a <code>SimpleTrigger</code> for this test
     */
    private SimpleTrigger getTestSimpleTrigger(final Date archiveDeletionJobTriggerDate) {

        final SimpleTrigger result =  new SimpleTrigger();
        result.setStartTime(archiveDeletionJobTriggerDate);

        return result;
    }

    /**
     * Return a <code>JobDetail</code> for this test
     *
     * @return a <code>JobDetail</code> for this test
     */
    private JobDetail getTestJobDetail() {

        final JobDetail result = new JobDetail();
        result.setName("test job name");
        result.setGroup("test job group");

        return result;
    }

    private Matcher<String> getExpectedValidLog() {
        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String s) {
                return s.contains("Started Job") ||
                        s.contains("Completed Job");
            }

            public void describeTo(final Description description) {
                description.appendText("Expected log");

            }
        };
    }

    private Matcher<String> getExpectedExceptionLog() {
        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String s) {
                return s.contains("Job Failed");
            }

            public void describeTo(final Description description) {
                description.appendText("Expected log");
            }
        };
    }

}
