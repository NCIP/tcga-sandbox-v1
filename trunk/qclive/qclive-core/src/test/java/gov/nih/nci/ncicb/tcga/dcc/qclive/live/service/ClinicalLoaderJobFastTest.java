package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.service.CacheFileGenerator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.LiveI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Test class for clinical loader job
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ClinicalLoaderJobFastTest {
    public static final int TO = 1;
    public static final int BCC = 2;
    public static final int SUBJECT = 3;
    public static final int BODY = 4;

    private static final String SAMPLE_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples/qclive").getPath() + File.separator;
    private final String ARCHIVE = SAMPLE_DIR + "test.archive_GBM.bio.1.3.0.tar.gz";

    private final Mockery context = new JUnit4Mockery();
    private final LiveI mockLive = context.mock(LiveI.class);
    private final MailSender mockMailSender = context.mock(MailSender.class);
    private final MailErrorHelper mockMailErrorHelper = context.mock(MailErrorHelper.class);
    private final CacheFileGenerator mockCacheFileGenerator = context.mock(CacheFileGenerator.class);
    private final Scheduler mockScheduler = context.mock(Scheduler.class);
    private final ArchiveQueries mockDiseaseArchiveQueries = context.mock(ArchiveQueries.class, "disease");
    private final ArchiveQueries mockCommonArchiveQueries = context.mock(ArchiveQueries.class, "common");
    private final PlatformQueries mockPlatformQueries = context.mock(PlatformQueries.class);
    private final CenterQueries mockCenterQueries = context.mock(CenterQueries.class);

    private final Job job = context.mock(Job.class);
    private ClinicalLoaderJob clinicalLoaderJob;
    private JobExecutionContext jobExecutionContext;

    private QcLiveStateBean stateContext;
    
    @Before
    public void setup() throws Exception {
        clinicalLoaderJob = new ClinicalLoaderJob() {
            protected MailErrorHelper getErrorMailSender() {
                return mockMailErrorHelper;
            }

            protected LiveI getLive() {
                return mockLive;
            }

            protected MailSender getMailSender() {
                return mockMailSender;
            }

            protected CacheFileGenerator getCacheFileGenerator() {
                return mockCacheFileGenerator;
            }

            protected ArchiveQueries getCommonArchiveQueries() {
                return mockCommonArchiveQueries;
            }

            protected ArchiveQueries getDiseaseArchiveQueries() {
                return mockDiseaseArchiveQueries;
            }

            protected PlatformQueries getPlatformQueries() {
                return mockPlatformQueries;
            }

            protected CenterQueries getCenterQueries() {
                return mockCenterQueries;
            }

        };

        jobExecutionContext = getJobExecutionContext();
        stateContext = new QcLiveStateBean();
        stateContext.setTransactionId(1l);
    }

    private JobExecutionContext getJobExecutionContext() {
        final JobDetail jobDetail = new JobDetail();
        final Trigger trigger = new SimpleTrigger();
        final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(jobDetail, trigger, null, false, null, null, null, null);
        return new JobExecutionContext(mockScheduler, triggerFiredBundle, job);
    }


    @Test
    public void testClinicalLoaderAndCacheGenerator() throws Exception {

        final List<Archive> archiveList = new ArrayList<Archive>();
        final List<Archive> processedArchiveList = new ArrayList<Archive>();
        final Archive archive = getArchive();
        archiveList.add(archive);

        final String[] triggerNames = new String[]{
                "trigger1"
        };

        processedArchiveList.add(archive);
        final List<String> generatedCacheFileNames = new ArrayList<String>();
        generatedCacheFileNames.add("clinical_patient_all_GBM.txt");
        jobExecutionContext.getMergedJobDataMap().put(ClinicalLoaderJob.CLINICAL_ARCHIVES, archiveList);
        jobExecutionContext.getJobDetail().getJobDataMap().put(ClinicalLoaderJob.CLINICAL_PROCESSED_ARCHIVES, processedArchiveList);

        context.checking(new Expectations() {{
            one(mockLive).loadClinicalData(archiveList,null);
        }});

        clinicalLoaderJob.execute(jobExecutionContext);
    }


    @Test
    public void testClinicalLoader() throws Exception {

        final List<Archive> archiveList = new ArrayList<Archive>();
        final Archive archive = getArchive();
        archiveList.add(archive);
        final String[] triggerNames = new String[]{
                "trigger1",
                "trigger2"
        };

        jobExecutionContext.getMergedJobDataMap().put(ClinicalLoaderJob.CLINICAL_ARCHIVES, archiveList);


        context.checking(new Expectations() {{
            one(mockLive).loadClinicalData(archiveList,null);


        }});

        clinicalLoaderJob.execute(jobExecutionContext);
    }

    @Test
    public void testClinicalLoaderFailure() throws Exception {

        final List<Archive> archiveList = new ArrayList<Archive>();
        final Tumor tumor = new Tumor();
        tumor.setTumorName("GBM");
        final Archive archive = new Archive(ARCHIVE);
        archive.setTheTumor(tumor);
        archiveList.add(archive);
        final String[] triggerNames = new String[]{
                "trigger1",
                "trigger2"
        };

        jobExecutionContext.getMergedJobDataMap().put(ClinicalLoaderJob.CLINICAL_ARCHIVES, archiveList);


        context.checking(new Expectations() {{
            one(mockLive).loadClinicalData(archiveList,null);
            will(throwException(new Exception()));
            one(mockMailErrorHelper).send(with(validateErrorMessage(false)), with(any(String.class)));

        }});

        clinicalLoaderJob.execute(jobExecutionContext);
    }

    private Archive getArchive() {
        final String[] emailList = new String[]{
                "center1",
                "center2"
        };
        final Center center = new Center();
        center.setEmailList(Arrays.asList(emailList));
        final Tumor tumor = new Tumor();
        tumor.setTumorName("GBM");
        final Platform platform = new Platform();
        platform.setPlatformName("platform");
        final Archive archive = new Archive(ARCHIVE);
        archive.setTheTumor(tumor);
        archive.setTheCenter(center);
        archive.setThePlatform(platform);
        archive.setDeployLocation("/test");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);

        return archive;
    }

    private Matcher<String> validateParameter(final int parameterIndex,
                                              final boolean isCacheGenerator) {

        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String parameter) {
                switch (parameterIndex) {
                    case TO:
                        return (isCacheGenerator) ? "test".equals(parameter) :
                                "center1,center2".equals(parameter);
                    case SUBJECT:
                        return (isCacheGenerator) ? parameter.startsWith("Generated clinical bio-tab cache files") :
                                parameter.startsWith("New Archive Available") ||
                                        parameter.startsWith("Completed processing of");
                    case BODY:
                        return (isCacheGenerator) ?
                                parameter.startsWith("Generated following bio-tab cache files") &&
                                        parameter.contains("clinical_patient_all_GBM.txt") :

                                (parameter.startsWith("Archive Name") &&
                                        parameter.contains("test.archive_GBM.bio.1.3.0")) ||
                                        (parameter.startsWith("Processing of your submission for") &&
                                                parameter.contains("test.archive_GBM.bio.1.3.0"));

                    default:
                        return false;

                }
            }

            public void describeTo(final Description description) {
                description.appendText("Expected log");

            }
        };
    }

    private Matcher<String> validateErrorMessage(final boolean isCacheGenerator) {

        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String message) {
                if (isCacheGenerator) {
                    return ("Failed generating clinical bio-tab cache files for GBM".equals(message));
                } else {
                    return message.startsWith("Failed loading clinical archives");
                }

            }

            public void describeTo(final Description description) {
                description.appendText("Expected log");

            }
        };
    }
}
