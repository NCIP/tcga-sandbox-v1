package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.common.service.StatusCallback;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.DummyFileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.Loader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderQueries;

import java.io.File;

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
import org.quartz.Trigger;

/**
 * Class which tests LoaderEnqueuer
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class LoaderEnqueuerFastTest implements StatusCallback {
	private static final String SAMPLES_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private Mockery context = new JUnit4Mockery();
    private Scheduler mockScheduler;
    private LoaderEnqueuer loaderEnqueuer;
    protected LoaderQueries mockLoaderQueries;
    protected ArchiveQueries mockCommonArchiveQueries;
    protected ArchiveQueries mockDiseaseArchiveQueries;
    protected UUIDDAO mockUuidDAO;
    protected MailSender mockMailSender;
    protected JobDetail scheduledJobDetail;

    @Before
    public void setup() throws Exception {
        mockScheduler = context.mock(Scheduler.class, "Scheduler");
        mockLoaderQueries = context.mock(LoaderQueries.class, "LoaderQueries");
        mockCommonArchiveQueries = context.mock(ArchiveQueries.class, "CommonArchiveQueries");
        mockDiseaseArchiveQueries = context.mock(ArchiveQueries.class, "DiseaseArchiveQueries");
        mockUuidDAO = context.mock(UUIDDAO.class, "UUIDDAO");
        mockMailSender = context.mock(MailSender.class, "MailSender");

        loaderEnqueuer = LoaderEnqueuer.getLoaderEnqueuer();
        loaderEnqueuer.setQuartzScheduler(mockScheduler);
        loaderEnqueuer.setLoaderQueries(mockLoaderQueries);
        loaderEnqueuer.setCommonArchiveQueries(mockCommonArchiveQueries);
        loaderEnqueuer.setDiseaseArchiveQueries(mockDiseaseArchiveQueries);
        loaderEnqueuer.setUuidDAO(mockUuidDAO);
        loaderEnqueuer.setMailSender(mockMailSender);
        loaderEnqueuer.setMailTo("testGroup");


    }


    @Test
    public void testQueueLoaderJob() throws Exception {
        final File file = new File(SAMPLES_DIR + 
                "autoloader" +
                File.separator +
                "loaderTest" +
                File.separator +
                "center_DIS.platform.1.0.0");
        final String loadDirectory = SAMPLES_DIR +
                "autoloader" +
                File.separator +
                "loaderTest" +
                File.separator +
                "center_DIS.platform.1.0.0";

        final String magetabDirectory = loadDirectory;
        final FileTypeLookup ftLookup = new DummyFileTypeLookup();
        final String experimentName = "experiment-1";
        final String jobGroupName = loaderEnqueuer.getLoaderJobGroupName(experimentName);
        context.checking(new Expectations() {{
            one(mockScheduler).scheduleJob(with(validateJobDetail(jobGroupName, loadDirectory, magetabDirectory)), with(validateTrigger(jobGroupName)));
        }});


        loaderEnqueuer.queueLoaderJob(loadDirectory,
                magetabDirectory,
                ftLookup,
                this,
                experimentName);
    }

    /**
     * Status call back API
     *
     * @param status
     */
    public void sendStatus(Status status) {
        // dummy method
    }

    private Matcher<JobDetail> validateJobDetail(final String jobGroupName,
                                                 final String loaderDirectory,
                                                 final String magetabDirectory) {

        return new TypeSafeMatcher<JobDetail>() {

            @Override
            public boolean matchesSafely(final JobDetail jobDetail) {
                scheduledJobDetail = jobDetail;
                final Loader loader = ((LoaderRunner) jobDetail.getJobDataMap().get(LoaderEnqueuer.JOBRUNNER_MAPNAME)).getLoader();

                return jobGroupName.equals(jobDetail.getGroup()) &&
                        gov.nih.nci.ncicb.tcga.dcc.qclive.service.LoaderJob.class.equals(jobDetail.getJobClass()) &&
                        loader != null &&
                        loaderDirectory.equals(loader.getLoadDirectory()) &&
                        magetabDirectory.equals(loader.getMagetabDirectory());
            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }


    private Matcher<Trigger> validateTrigger(final String jobGroupName) {

        return new TypeSafeMatcher<Trigger>() {

            @Override
            public boolean matchesSafely(final Trigger trigger) {

                return scheduledJobDetail.getGroup().equals(trigger.getJobGroup()) &&
                        scheduledJobDetail.getGroup().equals(trigger.getGroup()) &&
                        scheduledJobDetail.getName().equals(trigger.getName()) &&
                        scheduledJobDetail.getName().equals(trigger.getJobName());
            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }


}
