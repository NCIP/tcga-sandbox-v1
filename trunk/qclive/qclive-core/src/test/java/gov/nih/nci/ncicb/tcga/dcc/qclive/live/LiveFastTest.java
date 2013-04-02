/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ArchiveLoader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MD5Validator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.ArchiveLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentDAO;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.service.JobScheduler;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalLoaderException;
import org.apache.log4j.Level;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for Live.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class LiveFastTest {
    private static final String SAMPLES_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private Mockery context = new JUnit4Mockery();
    private File testFile = new File("domain_tumor.platform.test.tar.gz");
    private QcContext qcContext = new QcContext();
    private Logger mockLogger = context.mock(Logger.class);
    private ArchiveLogger mockArchiveLogger = context.mock(ArchiveLogger.class);
    final private Archive archive = new Archive("domain_tumor.platform.test.tar.gz");
    final private Archive archiveTar = new Archive("domain_tumor.platform.test.tar");
    final private Experiment experiment = new Experiment(
            "domain_tumor.platform");
    final Center center = new Center();
    private Live live = new TestableLive();
    private QcLiveStateBean stateContext = null;
    private static final String SAMPLE_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    @SuppressWarnings("unchecked")
    private Processor<File, Archive> mockUploadChecker = (Processor<File, Archive>) context
            .mock(Processor.class, "uploadChecker");

    @SuppressWarnings("unchecked")
    private Processor<String, Experiment> mockCgccExperimentChecker = (Processor<String, Experiment>) context
            .mock(Processor.class, "cgccExperimentChecker");
    @SuppressWarnings("unchecked")
    private Processor<String, Experiment> mockBcrExperimentChecker = (Processor<String, Experiment>) context
            .mock(Processor.class, "bcrExperimentChecker");
    @SuppressWarnings("unchecked")
    private Processor<String, Experiment> mockGscExperimentChecker = (Processor<String, Experiment>) context
            .mock(Processor.class, "gscExperimentChecker");

    @SuppressWarnings("unchecked")
    private Processor<Experiment, Boolean> mockCgccExperimentValidator = (Processor<Experiment, Boolean>) context
            .mock(Processor.class, "cgccExperimentValidator");
    @SuppressWarnings("unchecked")
    private Processor<Experiment, Boolean> mockGscExperimentValidator = (Processor<Experiment, Boolean>) context
            .mock(Processor.class, "gscExperimentValidator");
    @SuppressWarnings("unchecked")
    private Processor<Experiment, Boolean> mockBcrExperimentValidator = (Processor<Experiment, Boolean>) context
            .mock(Processor.class, "bcrExperimentValidator");

    @SuppressWarnings("unchecked")
    private Processor<Experiment, List<Archive>> mockCgccExperimentDeployer = (Processor<Experiment, List<Archive>>) context
            .mock(Processor.class, "cgccExperimentDeployer");
    @SuppressWarnings("unchecked")
    private Processor<Experiment, List<Archive>> mockBcrExperimentDeployer = (Processor<Experiment, List<Archive>>) context
            .mock(Processor.class, "bcrExperimentDeployer");
    @SuppressWarnings("unchecked")
    private Processor<Experiment, List<Archive>> mockGscExperimentDeployer = (Processor<Experiment, List<Archive>>) context
            .mock(Processor.class, "gscExperimentDeployer");

    private ArchiveLoader mockClinicalLoaderCaller = (ArchiveLoader) context
            .mock(ArchiveLoader.class, "clinicalLoaderCaller");

    private MailErrorHelper mockMailErrorHelper = context
            .mock(MailErrorHelper.class);
    private JobScheduler mockLiveScheduler = context.mock(JobScheduler.class);
    private CenterQueries mockCenterQueries = context.mock(CenterQueries.class);
    private PlatformQueries mockPlatformQueries = context
            .mock(PlatformQueries.class);
    private MailSender mockMailSender = context.mock(MailSender.class);
    private ArchiveQueries mockArchiveQueries = context.mock(
            ArchiveQueries.class, "archiveQueries_1");
    private ArchiveQueries mockDiseaseArchiveQueries = context.mock(
            ArchiveQueries.class, "archiveQueries_2");
    private final Platform platForm = new Platform();
    private String failedArchiveRootPath = "";
    private String experimentArchiveNameFilter = "center_disease.platform.level.serialIndex.revision.series";
    private ExperimentDAO mockExperimentDAO = context.mock(
            ExperimentDAO.class, "experiment");

    @Before
    public void setup() {
        live.setUploadChecker(mockUploadChecker);
        live.setJobScheduler(mockLiveScheduler);
        live.setServerUrl("http://test-url");
        live.setArchiveLogger(mockArchiveLogger);
        live.setExperimentDAO(mockExperimentDAO);
        Map<String, Processor<String, Experiment>> experimentCheckers = new HashMap<String, Processor<String, Experiment>>();
        experimentCheckers.put("CGCC", mockCgccExperimentChecker);
        experimentCheckers.put("GSC", mockGscExperimentChecker);
        experimentCheckers.put("BCR", mockBcrExperimentChecker);
        live.setExperimentCheckers(experimentCheckers);

        Map<String, Processor<Experiment, Boolean>> experimentValidators = new HashMap<String, Processor<Experiment, Boolean>>();
        experimentValidators.put("CGCC", mockCgccExperimentValidator);
        experimentValidators.put("GSC", mockGscExperimentValidator);
        experimentValidators.put("BCR", mockBcrExperimentValidator);
        live.setExperimentValidators(experimentValidators);

        Map<String, Processor<Experiment, List<Archive>>> experimentDeployers = new HashMap<String, Processor<Experiment, List<Archive>>>();
        experimentDeployers.put("CGCC", mockCgccExperimentDeployer);
        experimentDeployers.put("GSC", mockGscExperimentDeployer);
        experimentDeployers.put("BCR", mockBcrExperimentDeployer);
        live.setExperimentDeployers(experimentDeployers);

        // clinical loader related configuration
        live.setInitialWaitMinutes(1);
        live.setClinicalLoaderCaller(mockClinicalLoaderCaller);

        live.setLogger(mockLogger);
        live.setErrorMailSender(mockMailErrorHelper);
        live.setCenterQueries(mockCenterQueries);
        live.setPlatformQueries(mockPlatformQueries);
        live.setMailSender(mockMailSender);
        live.setCommonArchiveQueries(mockArchiveQueries);
        live.setDiseaseArchiveQueries(mockDiseaseArchiveQueries);
        live.setInitialWaitMinutes(30);
        live.setValidClinicalPlatforms("bio,clinical");
        live.setEmailBcc("newArchiveList");

        platForm.setCenterType("CGCC");

        archive.setDomainName("domain");
        archive.setPlatform("platform");
        archive.setArchiveType(Archive.TYPE_LEVEL_1);
        archive.setTumorType("tumor");
        archive.setRealName("domain_tumor.platform.test.tar.gz");
        archive.setDepositLocation("/tcgafiles/depositlocation/received/domain_tumor.platform.test.tar.gz");
        archive.setExperimentType("CGCC");
        archiveTar.setDomainName("domain");
        archiveTar.setPlatform("platform");
        archiveTar.setArchiveType(Archive.TYPE_LEVEL_1);
        archiveTar.setTumorType("tumor");
        archiveTar.setRealName("domain_tumor.platform.test.tar");
        archiveTar.setDepositLocation("/tcgafiles/depositlocation/received/domain_tumor.platform.test.tar");
        archiveTar.setExperimentType("CGCC");
        List<String> emailList = new ArrayList<String>();
        emailList.add("email");
        center.setEmailList(emailList);
        archive.setTheCenter(center);
        archiveTar.setTheCenter(center);
        // failed root dir
        failedArchiveRootPath = getFailedArchiveRootPath();
        live.setFailedArchiveRootPath(failedArchiveRootPath);

        stateContext = new QcLiveStateBean();
        stateContext.setTransactionId(1l);
        qcContext.setStateContext(stateContext);
    }

    @Test
    public void testEmailCenter() {
        final Platform platform = new Platform();
        platform.setCenterType("GSC");
        final Center center = new Center();
        center.setEmailList(Arrays.asList("test@test"));

        context.checking(new Expectations() {
            {
                one(mockPlatformQueries).getPlatformForName("aPlatform");
                will(returnValue(platform));
                one(mockCenterQueries).findCenterId("foo", "GSC");
                will(returnValue(123));
                one(mockCenterQueries).getCenterById(123);
                will(returnValue(center));
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                one(mockMailSender).send("test@test", null, "test subject",
                        "test body", false);
            }
        });

        live.emailCenter("foo", "aPlatform", "test subject", "test body");
    }

    @Test
    public void testEmailCenterNoPlatform() {
        final Platform platform = new Platform();
        platform.setCenterType("GSC");
        final Center center = new Center();
        center.setEmailList(Arrays.asList("test@test"));

        context.checking(new Expectations() {
            {
                one(mockMailErrorHelper)
                        .send("Unable to get email for center foo and platform null.  Message to be sent was:\n\ntest body",
                                "");
            }
        });

        live.emailCenter("foo", null, "test subject", "test body");

    }

    @Test
    public void testProcessUpload() throws Processor.ProcessorException,
            SchedulerException {
        context.checking(new Expectations() {
            {
                one(mockUploadChecker).execute(testFile, qcContext);
                will(uploadArchive(archive));
                allowing(mockLogger).log(with(any(Level.class)),
                        with(any(String.class)));
                one(mockLiveScheduler).scheduleExperimentCheck(
                        with(any(String.class)), with(any(String.class)),
                        with(any(String.class)), with(any(Calendar.class)),
                        with(any(QcLiveStateBean.class)),
                        with(any(String.class)));
            }
        });

        live.processUpload("test", 1, stateContext);
        assertEquals(Archive.STATUS_UPLOADED, archive.getDeployStatus());
    }

    @Test
    public void testProcessUploadFails() throws Processor.ProcessorException,
            SchedulerException {

        final String errorMesssage = "Processing failed for domain_tumor.platform.test.\n\n"
                + "Please address any errors shown below before resubmitting the archive(s).  "
                + "If you have any questions, please contact the DCC team at TCGA-DCC-BINF-L@LIST.NIH.GOV.\n\n"
                + "Error(s) recorded:\n- archive forced to fail for testing\n\n";

        context.checking(new Expectations() {
            {
                one(mockUploadChecker).execute(testFile, qcContext);
                will(failArchive(archive, qcContext));
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockCenterQueries).findCenterId("domain", "CGCC");
                will(returnValue(1));
                one(mockCenterQueries).getCenterById(1);
                will(returnValue(center));
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                one(mockCenterQueries).isCenterConvertedToUUID("domain", "CGCC");
                will(returnValue(false));
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockMailSender).send("email", null,
                        "Processing failed for domain_tumor.platform.test",
                        errorMesssage, false);
                one(mockLogger).log(
                        Level.INFO,
                        "ProcessUpload called for "
                                + archive.getDepositLocation());
                one(mockLogger).log(with(Level.WARN),
                        with(getExpectedValidLog()));
                // expect job for moving to failed to be scheduled
                one(mockLiveScheduler).scheduleArchiveCleanup(archive, true);
                one(mockArchiveLogger).addErrorMessage(1l, "domain_tumor.platform.test",
                        errorMesssage);
                one(mockArchiveLogger).endTransaction(1l, false);
            }
        });
        live.processUpload(archive.getDepositLocation(), 1, stateContext);
        assertEquals(1, qcContext.getErrorCount());
    }

    @Test
    public void testBarcodeValidationRetry()
            throws Processor.ProcessorException, SchedulerException {
        qcContext.setMd5ValidationStatus(MD5Validator.STATUS_PENDING);
        live.setMd5RetryAttempts(3);
        live.setMd5ValidationRetryPeriod(60);

        context.checking(new Expectations() {
            {
                one(mockUploadChecker).execute(testFile, qcContext);
                will(failArchive(archive, qcContext));
                one(mockLogger).log(
                        Level.INFO,
                        "ProcessUpload called for "
                                + archive.getDepositLocation());
                one(mockCenterQueries).isCenterConvertedToUUID("domain", "CGCC");
                will(returnValue(false));
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));

                // expect the upload check job to be scheduled again
                one(mockLiveScheduler).scheduleUploadCheck(
                        with(any(String.class)), with(any(Calendar.class)),
                        with(any(Integer.class)),
                        with(any(QcLiveStateBean.class)));
                one(mockLogger).log(Level.INFO, "Retrying MD5 Check again.");
            }
        });
        live.processUpload(archive.getDepositLocation(), 1, stateContext);
    }

    @Test
    public void testProcessUploadFailMaximumRetrySAttempts()
            throws Processor.ProcessorException, SchedulerException {
        qcContext.setMd5ValidationStatus(MD5Validator.STATUS_PENDING);
        live.setMd5RetryAttempts(3);
        live.setMd5ValidationRetryPeriod(60);
        final String errorMessage = "Processing failed for domain_tumor.platform.test.\n\nPlease"
                + " address any errors shown below before resubmitting the archive(s).  "
                + "If you have any questions, please contact the DCC team at TCGA-DCC-BINF-L@LIST.NIH.GOV."
                + "\n\nError(s) recorded:\n- archive forced to fail for testing\n\n";

        context.checking(new Expectations() {
            {
                one(mockUploadChecker).execute(testFile, qcContext);
                will(failArchive(archive, qcContext));
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockCenterQueries).isCenterConvertedToUUID("domain", "CGCC");
                will(returnValue(true));

                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockLogger).log(
                        Level.INFO,
                        "ProcessUpload called for "
                                + archive.getDepositLocation());
                one(mockLogger).log(with(Level.WARN),
                        with(getExpectedValidLog()));
                one(mockCenterQueries).findCenterId("domain", "CGCC");
                will(returnValue(1));
                one(mockCenterQueries).getCenterById(1);
                will(returnValue(center));
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                one(mockMailSender).send(with(any(String.class)),
                        with(any(String.class)), with(any(String.class)),
                        with(any(String.class)), with(any(Boolean.class)));
                // expect job for moving to failed to be scheduled
                one(mockLiveScheduler).scheduleArchiveCleanup(archive, true);
                one(mockArchiveLogger)
                        .addErrorMessage(1l, "domain_tumor.platform.test", errorMessage);
                one(mockArchiveLogger).endTransaction(1l, false);
            }
        });

        live.processUpload(archive.getDepositLocation(), 4, stateContext);
    }

    @Test
    public void testProcessUploadThrowsException()
            throws Processor.ProcessorException, SchedulerException {
        final String expectedEmailBody = "Processing failed for domain_tumor.platform.test.\n\nPlease address any "
                + "errors shown below before resubmitting the archive(s).  "
                + "If you have any questions, please contact the DCC team at "
                + "TCGA-DCC-BINF-L@LIST.NIH.GOV.\n\nError(s) recorded:\n- Uh-oh Spaghettios!\n\n";
        qcContext.setCenterName("theCenter");
        qcContext.setPlatformName("thePlatform");
        // noinspection ThrowableInstanceNeverThrown
        final Processor.ProcessorException fakeException = new Processor.ProcessorException(
                "Uh-oh Spaghettios!");
        context.checking(new Expectations() {
            {
                one(mockLogger).log(Level.INFO,
                        "ProcessUpload called for /tcgafiles/depositlocation/received/domain_tumor.platform.test.tar.gz");
                one(mockUploadChecker).execute(testFile, qcContext);
                will(throwException(fakeException));
                one(mockLogger).log(fakeException);
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockCenterQueries).isCenterConvertedToUUID("domain", "CGCC");
                will(returnValue(true));

                one(mockLiveScheduler).scheduleArchiveCleanup(
                        with(archiveMatchingDepositLocation(archive
                                .getDepositLocation())), with(true));
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockCenterQueries).findCenterId("domain", "CGCC");
                will(returnValue(1));
                one(mockCenterQueries).getCenterById(1);
                will(returnValue(center));
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                one(mockMailSender).send("email", null,
                        "Processing failed for domain_tumor.platform.test",
                        expectedEmailBody, false);
                one(mockLogger).log(
                        Level.WARN,
                        "domain platform /tcgafiles/depositlocation/received/domain_tumor.platform.test.tar.gz: "
                                + expectedEmailBody);
                one(mockArchiveLogger).addErrorMessage(1l, "domain_tumor.platform.test",
                        expectedEmailBody);
                one(mockArchiveLogger).endTransaction(1l, false);
            }
        });

        live.processUpload(archive.getDepositLocation(), 1, stateContext);
    }

    @Test
    public void testCheckCgccExperiment() throws SchedulerException,
            Processor.ProcessorException {
        experiment.setType(Experiment.TYPE_CGCC);
        testCheckExperiment(Experiment.TYPE_CGCC);
    }

    @Test
    public void testCheckGscExperiment() throws SchedulerException,
            Processor.ProcessorException {
        experiment.setType(Experiment.TYPE_GSC);
        testCheckExperiment(Experiment.TYPE_GSC);
    }

    @Test
    public void testCheckBcrExperiment() throws SchedulerException,
            Processor.ProcessorException {
        experiment.setType(Experiment.TYPE_BCR);
        final List<Archive> deployedArchives = new ArrayList<Archive>();
        archive.setPlatform("bio");
        deployedArchives.add(archive);
        final Platform platformMock = new Platform();

        context.checking(new Expectations() {
            {
                one(mockBcrExperimentChecker).execute("domain_tumor.platform",
                        qcContext);
                will(checkExperiment(experiment));
                one(mockBcrExperimentValidator).execute(experiment, qcContext);
                will(returnValue(true));
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockCenterQueries).isCenterConvertedToUUID("domain", "CGCC");
                will(returnValue(true));

                one(mockBcrExperimentDeployer).execute(experiment, qcContext);
                will(deployArchives(experiment, deployedArchives));
                one(mockArchiveQueries).updateArchiveStatus(archive);
                one(mockDiseaseArchiveQueries).updateArchiveStatus(archive);
                one(mockArchiveQueries).setToLatest(archive);
                one(mockDiseaseArchiveQueries).setToLatest(archive);
                one(mockArchiveQueries).getArchiveSize(with(any(Long.class)));
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                oneOf(mockMailSender).send(with(any(String.class)),
                        with(any(String.class)), with(any(String.class)),
                        with(any(String.class)), with(any(Boolean.class)));
                oneOf(mockLiveScheduler).scheduleClinicalLoader(
                        with(any(List.class)), with(any(Calendar.class)),
                        with(any(String.class)),
                        with(any(QcLiveStateBean.class)));
                will(throwException(new SchedulerException("boom")));
                allowing(mockLogger).log(Level.ERROR,
                        "Unable to schedule clinical data loading job boom");
                allowing(mockMailErrorHelper).send(with(any(String.class)),
                        with(any(String.class)));
                one(mockArchiveLogger).endTransaction("domain_tumor.platform.test", true);
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platformMock));
                one(mockCenterQueries).findCenterId("domain", null);
                will(returnValue(null));
                one(mockCenterQueries).getCenterById(null);
                will(returnValue(null));
                allowing(mockLiveScheduler).scheduleArchiveCleanup(archive,
                        false);
                one(mockArchiveLogger).endTransaction(1l, false);
            }
        });

        live.checkExperiment("domain_tumor.platform", Experiment.TYPE_BCR,
                experimentArchiveNameFilter + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION,
                stateContext);
        assertEquals(experimentArchiveNameFilter,
                qcContext.getExperimentArchiveNameFilter());

        context.assertIsSatisfied();

    }

    @Test
    public void testCheckUpToDateExperiment()
            throws Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                one(mockCgccExperimentChecker).execute("domain_tumor.platform",
                        qcContext);
                will(throwExceptionAndSetStatusToUpToDate(experiment, qcContext));
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockCenterQueries).isCenterConvertedToUUID("domain", "CGCC");
                will(returnValue(true));

                one(mockLogger).log(
                        Level.INFO,
                        "Experiment " + experiment.getName()
                                + " has no new uploaded archives");
            }
        });
        live.checkExperiment("domain_tumor.platform", Experiment.TYPE_CGCC,
                stateContext);
    }

    private Matcher<String> getExpectedValidLog() {
        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String s) {
                return s.contains("Processing failed");
            }

            public void describeTo(final Description description) {
                description.appendText("Expected log");

            }
        };
    }

    private void testCheckExperiment(final String type)
            throws Processor.ProcessorException, SchedulerException {
        final List<Archive> deployedArchives = new ArrayList<Archive>();
        archive.setPlatform("platform");
        deployedArchives.add(archive);
        final Platform platformMock = new Platform();
        platformMock.setCenterType(type);
        context.checking(new Expectations() {
            {

                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platformMock));
                one(mockCenterQueries).isCenterConvertedToUUID("domain", platformMock.getCenterType());
                will(returnValue(true));

                if (type.equals(Experiment.TYPE_CGCC)) {
                    one(mockCgccExperimentChecker).execute(
                            "domain_tumor.platform", qcContext);
                    will(checkExperiment(experiment));
                    one(mockCgccExperimentValidator).execute(experiment,
                            qcContext);
                    will(returnValue(true));
                    one(mockCgccExperimentDeployer).execute(experiment,
                            qcContext);
                    will(deployArchives(experiment, deployedArchives));
                    one(mockArchiveLogger).endTransaction("domain_tumor.platform.test", true);
                } else if (type.equals(Experiment.TYPE_GSC)) {
                    one(mockGscExperimentChecker).execute(
                            "domain_tumor.platform", qcContext);
                    will(checkExperiment(experiment));
                    one(mockGscExperimentValidator).execute(experiment,
                            qcContext);
                    will(returnValue(true));
                    one(mockGscExperimentDeployer).execute(experiment,
                            qcContext);
                    will(deployArchives(experiment, deployedArchives));
                    one(mockArchiveLogger).endTransaction("domain_tumor.platform.test", true);
                } else {
                    throw new IllegalArgumentException(
                            "Unknown experiment type " + type);
                }
                one(mockArchiveQueries).getArchiveSize(with(any(Long.class)));
                will(returnValue(6291456l));
                one(mockArchiveQueries).updateArchiveStatus(archive);
                one(mockDiseaseArchiveQueries).updateArchiveStatus(archive);
                one(mockArchiveQueries).setToLatest(archive);
                one(mockDiseaseArchiveQueries).setToLatest(archive);
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                one(mockMailSender)
                        .send("email",
                                "newArchiveList",
                                "New Archive Available - domain_tumor.platform.test",
                                "A new archive is available.\n\nArchive Name\tdomain_tumor.platform.test\n\nArchive Size\t6.0 GiB\n\nBrowse Contents\thttp://test-url/tcga/showFiles.htm?archiveId=0\n\nDownload\thttp://test-url/path/to/archive/test.tar.gz\n\n#This is a tab delimited message",
                                false);

                allowing(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platformMock));

                one(mockCenterQueries).findCenterId("domain", type);
                will(returnValue(1));
                one(mockCenterQueries).getCenterById(1);
                will(returnValue(center));
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                one(mockMailSender)
                        .send("email",
                                null,
                                "Processing of domain_tumor.platform was successful",
                                "Processing of your submission for 'domain_tumor.platform' has completed successfully.  The following archive(s) were deployed:\n\n\tdomain_tumor.platform.test\n",
                                false);
                one(mockLiveScheduler).scheduleArchiveCleanup(archive, false);
            }
        });

        if (Experiment.TYPE_BCR.equals(type)) {
            live.checkExperiment("domain_tumor.platform", type,
                    experimentArchiveNameFilter
                            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION, stateContext);
            assertEquals(experimentArchiveNameFilter,
                    qcContext.getExperimentArchiveNameFilter());
        } else {
            live.checkExperiment("domain_tumor.platform", type, stateContext);
            assertNull(qcContext.getExperimentArchiveNameFilter());
        }

        context.assertIsSatisfied();
    }

    /**
     * Test Experiment failure
     *
     * @throws Processor.ProcessorException
     * @throws SchedulerException
     */
    @Test
    public void testCheckExperimentFailed()
            throws Processor.ProcessorException, SchedulerException {

        final String centerType = "CGCC";
        final String platform = "domain_tumor.platform";
        final Platform platformMock = new Platform();
        platformMock.setCenterType(centerType);
        final String errorMessage = "Processing failed for domain_tumor.platform.\n\nPlease address any"
                + " errors shown below before resubmitting the archive(s).  If you have"
                + " any questions, please contact the DCC team at "
                + "TCGA-DCC-BINF-L@LIST.NIH.GOV.\n\nNo specific errors were recorded.  Please contact the DCC "
                + "for assistance.The following archives were part of this submission:\n\t- domain_tumor.platform.test.tar.gz\n";
        context.checking(new Expectations() {
            {
                one(mockCgccExperimentChecker).execute(platform, qcContext);
                will(checkExperimentAndFailIt(experiment, archive));
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockCenterQueries).isCenterConvertedToUUID("domain", "CGCC");
                will(returnValue(true));

                allowing(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platformMock));
                one(mockCenterQueries).findCenterId("domain", centerType);
                will(returnValue(1));
                one(mockCenterQueries).getCenterById(1);
                will(returnValue(center));
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                one(mockArchiveQueries).updateArchiveStatus(archive);
                one(mockDiseaseArchiveQueries).updateArchiveStatus(archive);
                one(mockMailSender).send("email", null,
                        "Processing failed for domain_tumor.platform",
                        errorMessage, false);
                one(mockLogger).log(with(Level.WARN),
                        with(getExpectedValidLog()));
                one(mockLiveScheduler).scheduleArchiveCleanup(archive, true);
                one(mockArchiveLogger).addErrorMessage(1l, "domain_tumor.platform",
                        errorMessage);
                one(mockArchiveLogger).endTransaction("domain_tumor.platform.test", false);
                one(mockArchiveLogger).endTransaction(1l, false);
            }
        });
        live.checkExperiment(platform, centerType, stateContext);
        context.assertIsSatisfied();
    }

    @Test
    public void testCheckExperimentInvalid()
            throws Processor.ProcessorException, SchedulerException {
        // same as above but sets archive to invalid, not uploaded
        final String centerType = "CGCC";
        final String platform = "domain_tumor.platform";
        final Platform platformMock = new Platform();
        platformMock.setCenterType(centerType);
        final String errorMessage = "Processing failed for domain_tumor.platform.\n\nPlease address any errors shown below before resubmitting the archive(s).  If you have any questions, please contact the DCC team at TCGA-DCC-BINF-L@LIST.NIH.GOV.\n\nNo specific errors were recorded.  Please contact the DCC for assistance.The following archives were part of this submission:\n\t- domain_tumor.platform.test.tar.gz\n";
        context.checking(new Expectations() {
            {
                one(mockCgccExperimentChecker).execute(platform, qcContext);
                will(checkExperimentAndFailArchive(experiment, archive));
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockCenterQueries).isCenterConvertedToUUID("domain", "CGCC");
                will(returnValue(true));

                allowing(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platformMock));
                one(mockCenterQueries).findCenterId("domain", centerType);
                will(returnValue(1));
                one(mockCenterQueries).getCenterById(1);
                will(returnValue(center));
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                one(mockArchiveQueries).updateArchiveStatus(archive);
                one(mockDiseaseArchiveQueries).updateArchiveStatus(archive);
                one(mockMailSender).send("email", null,
                        "Processing failed for domain_tumor.platform",
                        errorMessage, false);
                one(mockLogger).log(with(Level.WARN),
                        with(getExpectedValidLog()));
                one(mockLiveScheduler).scheduleArchiveCleanup(archive, true);
                one(mockArchiveLogger).addErrorMessage(1l, "domain_tumor.platform",
                        errorMessage);
                one(mockArchiveLogger).endTransaction(1l, false);
                one(mockArchiveLogger).endTransaction("domain_tumor.platform.test", false);

            }
        });
        live.checkExperiment(platform, centerType, stateContext);
        context.assertIsSatisfied();
    }

    @Test
    public void testCleanupFailedArchive() {
        String containingDir = SAMPLE_DIR + "qclive" + File.separator + "live";
        String testFile = containingDir + File.separator + "test.tar.gz";
        final String originalArchivePath = archive.getDepositLocation();
        File testDirectory = new File(containingDir + File.separator + "test");
        File failedDir = new File(failedArchiveRootPath);

        final String expectedMovedFilename = failedDir + File.separator
                + archive.getDomainName() + File.separator
                + archive.getExperimentType() + File.separator + "test.tar.gz";
        File expectedMovedFile = new File(expectedMovedFilename);

        try {
            context.checking(new Expectations() {
                {
                    allowing(mockLogger).log(with(any(Level.class)),
                            with(any(String.class)));
                }
            });
            archive.setDepositLocation(testFile);
            live.cleanupArchive(archive, true);
            assertTrue(failedDir.exists());
            assertTrue(expectedMovedFile.exists());
            assertFalse("archive directory should have been deleted",
                    testDirectory.exists());
        } finally {
            // clean up: move file back and delete failed dir, recreate test dir
            // noinspection ResultOfMethodCallIgnored
            expectedMovedFile.renameTo(new File(testFile));
            FileUtil.deleteDir(failedDir);
            archive.setDepositLocation(originalArchivePath);
        }
    }

    @Test
    public void testCleanupFailedArchiveBadFile() {
        final String originalArchivePath = archive.getDepositLocation();
        context.checking(new Expectations() {{
            one(mockLogger).log(Level.WARN, "Archive cleanup could not complete, " +
                    "because archive file 'not a file' not found");
        }});
        // test when file does not exist
        archive.setDepositLocation("not a file");
        live.cleanupArchive(archive, true);
        context.assertIsSatisfied();
        archive.setDepositLocation(originalArchivePath);
    }

    @Test
    public void testCleanupFailedArchiveNull() {
        // make sure things don't break if the location is null
        // just no exceptions thrown, that is all that is expected
        live.cleanupArchive(null, true);
    }

    @Test
    public void testFailsEarly() throws Processor.ProcessorException,
            SchedulerException {
        final Platform platformMock = new Platform();
        platformMock.setCenterType("centerType");
        final String originalArchivePath = archive.getDepositLocation();
        final Processor.ProcessorException processorException = new Processor.ProcessorException(
                "fail");
        archive.setDepositLocation("center_disease.platform.type.0.0.0.tar.gz");
        context.checking(new Expectations() {
            {
                one(mockUploadChecker).execute(testFile, qcContext);
                // noinspection ThrowableInstanceNeverThrown
                will(throwException(processorException));
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockCenterQueries).isCenterConvertedToUUID("center", "CGCC");
                will(returnValue(false));

                one(mockLogger).log(processorException);
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platformMock));
                one(mockCenterQueries).findCenterId("center", "centerType");
                will(returnValue(1));
                one(mockCenterQueries).getCenterById(1);
                will(returnValue(center));
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                one(mockMailSender).send(with(any(String.class)),
                        with(any(String.class)), with(any(String.class)),
                        with(any(String.class)), with(any(Boolean.class)));
                one(mockLogger)
                        .log(Level.INFO,
                                "ProcessUpload called for center_disease.platform.type.0.0.0.tar.gz");
                one(mockLogger).log(with(Level.WARN),
                        with(getExpectedValidLog()));
                // expect job for moving to failed to be scheduled
                one(mockLiveScheduler).scheduleArchiveCleanup(
                        with(any(Archive.class)), with(any(Boolean.class)));
                one(mockArchiveLogger).addErrorMessage(with(any(Long.class)),
                        with(any(String.class)), with(any(String.class)));
                one(mockArchiveLogger).endTransaction(1l, false);
            }
        });

        live.processUpload("center_disease.platform.type.0.0.0.tar.gz", 1,
                stateContext);
        assertEquals("center", qcContext.getCenterName());
        archive.setDepositLocation(originalArchivePath);
    }

    @Test
    public void testCleanupArchive() {
        final String rootDir = SAMPLES_DIR + "TCGAQCLiveTest";
        // offline root dir
        final String testOfflineRootDir = rootDir + File.separator + "offline";
        // archive dir
        final String testArchiveDir = rootDir + File.separator + "test"
                + File.separator + "tar";
        final String filename = "test.tar.gz";
        final String fileMD5 = filename + ".md5";
        final String originalArchivePath = archive.getDepositLocation();
        final File testArchive = new File(testArchiveDir + File.separator
                + filename);
        final File testArchiveMD5 = new File(testArchiveDir + File.separator
                + fileMD5);

        try {
            context.checking(new Expectations() {
                {
                    one(mockLogger).log(
                            Level.DEBUG,
                            "Moved Archives " + testArchive.getCanonicalPath()
                                    + "," + testArchiveMD5.getCanonicalPath()
                                    + " to: " + testOfflineRootDir);
                }
            });

            new File(testArchiveDir).mkdirs();
            if (!testArchive.exists()) {
                testArchive.createNewFile();
            }

            if (!testArchiveMD5.exists()) {
                testArchiveMD5.createNewFile();
            }

            archive.setDepositLocation(testArchive.getCanonicalPath());

            live.setArchiveOfflineRootPath(testOfflineRootDir);
            live.cleanupArchive(archive, false);
            // offline archive test file
            final String offlinePath = testOfflineRootDir + File.separator
                    + archive.getDomainName() + File.separator
                    + archive.getExperimentType();
            final File testOfflineArchive = new File(offlinePath
                    + File.separator + filename);
            final File testOfflineArchiveMD5 = new File(offlinePath
                    + File.separator + fileMD5);

            assertTrue(testOfflineArchive.exists());
            assertTrue(!new File(archive.getDepositLocation()).exists());
            assertTrue(testOfflineArchiveMD5.exists());
            assertTrue(!new File(testArchiveDir + File.separator + fileMD5)
                    .exists());

        } catch (Exception e) {
            assertTrue(false);
        } finally {
            archive.setDepositLocation(originalArchivePath);
            FileUtil.deleteDir(new File(rootDir));
        }
    }

    @Test
    public void testCleanupArchiveTar() {
        final String rootDir = SAMPLES_DIR + "TCGAQCLiveTest";
        // offline root dir
        final String testOfflineRootDir = rootDir + File.separator + "offline";
        // archive dir
        final String testArchiveDir = rootDir + File.separator + "test"
                + File.separator + "tar";
        final String filename = "test.tar";
        final String fileMD5 = filename + ".md5";
        final String originalArchivePath = archiveTar.getDepositLocation();
        final File testArchive = new File(testArchiveDir + File.separator
                + filename);
        final File testArchiveMD5 = new File(testArchiveDir + File.separator
                + fileMD5);
        try {
            context.checking(new Expectations() {{
                one(mockLogger).log(Level.DEBUG,
                        "Moved Archives " + testArchive.getCanonicalPath()
                                + "," + testArchiveMD5.getCanonicalPath()
                                + " to: " + testOfflineRootDir);
            }});

            new File(testArchiveDir).mkdirs();
            if (!testArchive.exists()) {
                testArchive.createNewFile();
            }

            if (!testArchiveMD5.exists()) {
                testArchiveMD5.createNewFile();
            }

            archiveTar.setDepositLocation(testArchive.getCanonicalPath());

            live.setArchiveOfflineRootPath(testOfflineRootDir);
            live.cleanupArchive(archiveTar, false);
            // offline archive test file
            final String offlinePath = testOfflineRootDir + File.separator
                    + archiveTar.getDomainName() + File.separator
                    + archiveTar.getExperimentType();
            final File testOfflineArchive = new File(offlinePath
                    + File.separator + filename);
            final File testOfflineArchiveMD5 = new File(offlinePath
                    + File.separator + fileMD5);

            assertTrue(testOfflineArchive.exists());
            assertTrue(!new File(archiveTar.getDepositLocation()).exists());
            assertTrue(testOfflineArchiveMD5.exists());
            assertTrue(!new File(testArchiveDir + File.separator + fileMD5)
                    .exists());

        } catch (Exception e) {
            assertTrue(false);
        } finally {
            archiveTar.setDepositLocation(originalArchivePath);
            FileUtil.deleteDir(new File(rootDir));
        }
    }

    @Test
    public void testLoadClinicalData() throws ClinicalLoaderException {
        final List<Archive> deployedArchives = new ArrayList<Archive>();
        // bio is needed for clinical loader
        archive.setPlatform("bio");
        archive.setRealName("realArchiveName");
        deployedArchives.add(archive);
        try {
            context.checking(new Expectations() {
                {
                    one(mockClinicalLoaderCaller).load(deployedArchives,
                            stateContext);
                }
            });
            live.loadClinicalData(deployedArchives, stateContext);
        } catch (ClinicalLoaderException e) {
            assertEquals(
                    "Archive realArchiveName is not a clinical platform archive so can't be loaded",
                    e.getMessage());
        }
    }

    @Test
    public void testBadClinicalPlatform() throws ClinicalLoaderException {
        final List<Archive> deployedArchives = new ArrayList<Archive>();
        // bio is needed for clinical loader
        archive.setPlatform("badPlatform");
        deployedArchives.add(archive);
        context.checking(new Expectations() {
            {
                one(mockClinicalLoaderCaller).load(deployedArchives,
                        stateContext);
            }
        });
        live.loadClinicalData(deployedArchives, stateContext);
        context.assertIsSatisfied();
    }

    @Test
    public void testFailExperiment() throws SchedulerException {
        // one Available archive, one Uploaded archive, one Validated archive
        final Archive availableArchive = new Archive("available.tar.gz");
        availableArchive.setDeployStatus(Archive.STATUS_AVAILABLE);
        availableArchive.setRealName("availableArchive");
        final Archive uploadedArchive = new Archive("uploaded.tar.gz");
        uploadedArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        uploadedArchive.setRealName("uploadedArchive");
        final Archive validatedArchive = new Archive("validated.tar.gz");
        validatedArchive.setDeployStatus(Archive.STATUS_VALIDATED);
        validatedArchive.setRealName("validatedArchive");
        experiment.addArchive(availableArchive);
        experiment.addArchive(uploadedArchive);
        experiment.addArchive(validatedArchive);

        qcContext.getErrors().clear();
        qcContext.addError("error1");
        qcContext.addError("error2");

        final String expectedEmailBody = "Processing failed for domain_tumor.platform.\n\n"
                + "Please address any errors shown below before resubmitting the archive(s).  "
                + "If you have any questions, please contact the DCC team at TCGA-DCC-BINF-L@LIST.NIH.GOV.\n\n"
                + "Error(s) recorded:\n- error1\n\n- error2\n\n"
                + "The following archives were part of this submission:\n"
                + "\t- uploadedArchive\n" + "\t- validatedArchive\n";

        context.checking(new Expectations() {
            {
                one(mockArchiveQueries).updateArchiveStatus(availableArchive);
                one(mockDiseaseArchiveQueries).updateArchiveStatus(
                        availableArchive);
                one(mockArchiveQueries).updateArchiveStatus(uploadedArchive);
                one(mockDiseaseArchiveQueries).updateArchiveStatus(
                        uploadedArchive);
                one(mockArchiveQueries).updateArchiveStatus(validatedArchive);
                one(mockDiseaseArchiveQueries).updateArchiveStatus(
                        validatedArchive);

                one(mockLiveScheduler).scheduleArchiveCleanup(uploadedArchive,
                        true);
                one(mockLiveScheduler).scheduleArchiveCleanup(validatedArchive,
                        true);

                one(mockPlatformQueries).getPlatformForName("testPlatform");
                will(returnValue(platForm));
                one(mockCenterQueries).findCenterId("testCenter",
                        platForm.getCenterType());
                will(returnValue(123));
                one(mockCenterQueries).getCenterById(123);
                will(returnValue(center));

                one(mockMailSender).isMailEnabled();
                will(returnValue(true));

                one(mockMailSender).send("email", null,
                        "Processing failed for domain_tumor.platform",
                        expectedEmailBody, false);
                one(mockLogger).log(
                        Level.WARN,
                        "testCenter testPlatform domain_tumor.platform: "
                                + expectedEmailBody);

                one(mockArchiveLogger).addErrorMessage(1l, "domain_tumor.platform",
                        expectedEmailBody);
                one(mockArchiveLogger).endTransaction("uploaded", false);
                one(mockArchiveLogger).endTransaction("validated", false);
                one(mockArchiveLogger).endTransaction(1L, false);

            }
        });
        qcContext.setCenterName("testCenter");
        qcContext.setPlatformName("testPlatform");
        live.failExperiment(experiment, qcContext);

        assertEquals("Available archive status was changed",
                Archive.STATUS_AVAILABLE, availableArchive.getDeployStatus());
        assertEquals("Uploaded archive status was not changed to invalid",
                Archive.STATUS_INVALID, uploadedArchive.getDeployStatus());
        assertEquals("Validated archive status was not changed to invalid",
                Archive.STATUS_INVALID, validatedArchive.getDeployStatus());
    }


    @Test
    public void testCleanupExperiment() throws SchedulerException {
        // one Available archive, one Uploaded archive, one Validated archive
        final Archive archive = new Archive("deployed.tar.gz");
        archive.setDeployStatus(Archive.STATUS_DEPLOYED);
        archive.setRealName("deployedArchive");

        List<Archive> archives = Arrays.asList(new Archive[]{archive});
        qcContext.setArchivesToBeProcessedInTheExperiment(archives);
        context.checking(new Expectations() {
            {
                one(mockLogger).log(
                        Level.INFO,
                        "center_disease_platform experiment deployment failed. The following archives are deleted from distro dir :");
                one(mockArchiveQueries).getArchive("deployed");
                will(returnValue(archive));
                one(mockExperimentDAO).getDeployDirectoryPath(archive);
                will(returnValue(new File("deployed")));
                one(mockLogger).log(
                        Level.INFO,
                        " Archive :deployed");
            }
        });
        live.cleanupNewlyDeployedArchives("center_disease_platform", qcContext);

    }

    @Test
    public void testExperimentRuntimeError() throws SchedulerException {
        final Archive archive = new Archive("deployed.tar.gz");
        archive.setDeployStatus(Archive.STATUS_DEPLOYED);
        archive.setRealName("deployedArchive");

        List<Archive> archives = Arrays.asList(new Archive[]{archive});
        qcContext.setArchivesToBeProcessedInTheExperiment(archives);
        qcContext.setCenterName("center");
        qcContext.setPlatformName("platform");
        platForm.setCenterType(Experiment.TYPE_BCR);
        context.checking(new Expectations() {
            {
                one(mockLogger).log(with(any(Exception.class)));
                one(mockMailErrorHelper).send("Unexpected error during checking of experiment center_disease_platform", "java.lang.NullPointerException");
                one(mockPlatformQueries).getPlatformForName("platform");
                will(returnValue(platForm));
                one(mockCenterQueries).findCenterId("center", "BCR");
                will(returnValue(0));
                one(mockCenterQueries).getCenterById(0);
                will(returnValue(center));
                one(mockMailSender).isMailEnabled();
                will(returnValue(true));
                one(mockMailSender).send("email", null, "Processing failed for center_disease_platform", "Processing failed for center_disease_platform.\n\nPlease address any errors shown below before resubmitting the archive(s).  If you have any questions, please contact the DCC team at TCGA-DCC-BINF-L@LIST.NIH.GOV.\n\nNo specific errors were recorded.  Please contact the DCC for assistance.", false);
                one(mockLogger).log(Level.WARN, "center platform center_disease_platform: Processing failed for center_disease_platform.\n\nPlease address any errors shown below before resubmitting the archive(s).  If you have any questions, please contact the DCC team at TCGA-DCC-BINF-L@LIST.NIH.GOV.\n\nNo specific errors were recorded.  Please contact the DCC for assistance.");
                one(mockArchiveLogger).addErrorMessage(null, "center_disease_platform", "Processing failed for center_disease_platform.\n\nPlease address any errors shown below before resubmitting the archive(s).  If you have any questions, please contact the DCC team at TCGA-DCC-BINF-L@LIST.NIH.GOV.\n\nNo specific errors were recorded.  Please contact the DCC for assistance.");
                one(mockArchiveLogger).endTransaction(with(any(Long.class)), with(any(Boolean.class)));

            }
        });
        live.checkExperiment("center_disease_platform", Experiment.TYPE_BCR, new QcLiveStateBean());

    }

    class TestableLive extends Live {

        public TestableLive() {
            super();
        }

        @Override
        protected File makeFile(final String path) {
            return testFile;
        }

        protected QcContext makeContext() {
            return qcContext;
        }
    }

    public static TypeSafeMatcher<Archive> archiveMatchingDepositLocation(
            final String depositLocation) {
        return new TypeSafeMatcher<Archive>() {

            @Override
            public boolean matchesSafely(final Archive archive) {
                return archive.getDepositLocation().equals(depositLocation);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("matches archive by deposit location");
            }
        };
    }

    public static Action uploadArchive(final Archive archive) {
        return new Action() {
            public void describeTo(final Description description) {
                description.appendText("sets archive to status UPLOADED");
            }

            public Object invoke(final Invocation invocation) throws Throwable {
                archive.setDeployStatus(Archive.STATUS_UPLOADED);
                return archive;
            }
        };
    }

    public static Action failArchive(final Archive archive,
                                     final QcContext qcContext) {
        return new Action() {
            public void describeTo(final Description description) {
                description.appendText("sets archive to status INVALID");
            }

            public Object invoke(final Invocation invocation) throws Throwable {
                archive.setDeployStatus(Archive.STATUS_INVALID);
                qcContext.addError("archive forced to fail for testing");
                return archive;
            }
        };
    }

    public static Action throwExceptionAndSetStatusToUpToDate(
            final Experiment experiment, final QcContext qcContext) {
        return new Action() {
            public Object invoke(final Invocation invocation) throws Throwable {
                qcContext.setExperiment(experiment);
                experiment.setStatus(Experiment.STATUS_UP_TO_DATE);
                throw new Processor.ProcessorException(
                        "No archives to process!");
            }

            @Override
            public void describeTo(final Description description) {
                description
                        .appendText("sets archive status to Up to date and throws an exception");
            }
        };
    }

    public static Action checkExperiment(final Experiment experiment) {
        return checkExperiment(experiment, Experiment.STATUS_CHECKED);
    }

    public static Action checkExperiment(final Experiment experiment,
                                         final String experimentStatus) {
        return new Action() {
            public void describeTo(final Description description) {
                description.appendText("sets experiment status to ")
                        .appendText(experimentStatus);
            }

            public Object invoke(final Invocation invocation) throws Throwable {
                experiment.setStatus(experimentStatus);
                return experiment;
            }
        };
    }

    public static Action checkExperimentAndFailIt(final Experiment experiment,
                                                  final Archive archive) {
        return new Action() {
            public void describeTo(final Description description) {
                description.appendText("sets experiment status to FAILED");
            }

            public Object invoke(final Invocation invocation) throws Throwable {
                experiment.setStatus(Experiment.STATUS_FAILED);
                experiment.addArchive(archive);
                archive.setDeployStatus(Archive.STATUS_UPLOADED);
                return experiment;
            }
        };
    }

    public static Action checkExperimentAndFailArchive(
            final Experiment experiment, final Archive archive) {
        return new Action() {
            public void describeTo(final Description description) {
                description
                        .appendText("sets experiment status to FAILED and archive status to INVALID");
            }

            public Object invoke(final Invocation invocation) throws Throwable {
                experiment.setStatus(Experiment.STATUS_FAILED);
                experiment.addArchive(archive);
                archive.setDeployStatus(Archive.STATUS_INVALID);
                return experiment;
            }
        };
    }

    public static Action deployArchives(final Experiment experiment,
                                        final List<Archive> archives) {
        return new Action() {
            public void describeTo(final Description description) {
                description
                        .appendText("sets experiment status to deployed and archive status to available");
            }

            public Object invoke(final Invocation invocation) throws Throwable {
                experiment.setStatus(Experiment.STATUS_DEPLOYED);
                for (Archive archive : archives) {
                    archive.setDeployStatus(Archive.STATUS_AVAILABLE);
                    archive.setDeployLocation("/path/to/archive/test.tar.gz");
                }
                return archives;
            }
        };
    }

    public String getFailedArchiveRootPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(SAMPLES_DIR)
                .append("TCGAQCLiveTest")
                .append(File.separator).append("failed");

        return sb.toString();

    }

}
