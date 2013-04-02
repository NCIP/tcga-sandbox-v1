/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MiRNASeqDataFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqDataFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentQueries;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Test class for ExperimentChecker
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ExperimentCheckerFastTest {

    private QcContext qcContext;

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    private static final String EXPERIMENT_CHECKER_DIR = SAMPLES_DIR + "qclive"
            + File.separator + "experimentChecker";

    private static final String LEVEL_1_ARCHIVE = "domain_disease.platform.Level_1.1.0.0";
    private static final String LEVEL_2_ARCHIVE = "domain_disease.platform.Level_2.1.1.0";
    private static final String LEVEL_3_ARCHIVE = "domain_disease.platform.Level_3.1.0.0";
    private ExperimentChecker checkerForCgcc = new ExperimentChecker();
    private ExperimentChecker checkerForNonCgcc = new ExperimentChecker();
    private MageTabExperimentChecker cgccChecker = new MageTabExperimentChecker();
    private Mockery context = new JUnit4Mockery();
    private ExperimentQueries mockExperimentQueries = context
            .mock(ExperimentQueries.class);
    private ArchiveQueries mockArchiveQueries = context
            .mock(ArchiveQueries.class);
    private CenterQueries mockCenterQueries;

    private Experiment experiment;
    private Archive mageTabArchive, availableLevel1Archive,
            uploadedLevel1Archive, uploadedLevel2Archive,
            availableLevel3Archive, uploadedLevel3RnaSeqArchive,
            uploadedLevel3MiRnaSeqArchive, auxArchive, availableMageTabArchive,
            uploadedLevel3DNASeqCArchive;
    private File sdrfFile = new File(
            SAMPLES_DIR
                    + "qclive/experimentChecker/domain_disease.platform_mage-tab.1.3.0/test.sdrf.txt");

    private String experimentName = "center_disease.platform";

    @Before
    public void setup() {
        experiment = new TestableExperiment();
        qcContext = new QcContext();
        qcContext.setExperiment(experiment);
        experiment.setName(experimentName);
        experiment.setType("CGCC");
        checkerForCgcc.setExperimentQueries(mockExperimentQueries);
        checkerForCgcc.addOutputValidator(cgccChecker);
        checkerForNonCgcc.setExperimentQueries(mockExperimentQueries);
        mageTabArchive = new Archive();
        availableMageTabArchive = new Archive();
        availableMageTabArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
        availableMageTabArchive.setDeployStatus(Archive.STATUS_AVAILABLE);
        availableMageTabArchive.setPlatform("platform");

        uploadedLevel1Archive = new Archive();
        uploadedLevel1Archive.setRealName(LEVEL_1_ARCHIVE);
        uploadedLevel1Archive.setDeployStatus(Archive.STATUS_UPLOADED);
        uploadedLevel1Archive.setArchiveType(Archive.TYPE_LEVEL_1);
        uploadedLevel1Archive.setPlatform("platform");

        uploadedLevel2Archive = new Archive();
        uploadedLevel2Archive.setRealName(LEVEL_2_ARCHIVE);
        uploadedLevel2Archive.setDeployStatus(Archive.STATUS_UPLOADED);
        uploadedLevel2Archive.setArchiveType(Archive.TYPE_LEVEL_2);
        uploadedLevel2Archive.setPlatform("platform");

        availableLevel1Archive = new Archive();
        availableLevel1Archive.setRealName(LEVEL_1_ARCHIVE);
        availableLevel1Archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        availableLevel1Archive.setArchiveType(Archive.TYPE_LEVEL_1);
        availableLevel1Archive.setPlatform("platform");

        availableLevel3Archive = new Archive();
        availableLevel3Archive.setRealName(LEVEL_3_ARCHIVE);
        availableLevel3Archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        availableLevel3Archive.setArchiveType(Archive.TYPE_LEVEL_3);
        availableLevel3Archive.setPlatform("platform");

        uploadedLevel3RnaSeqArchive = new Archive();
        uploadedLevel3RnaSeqArchive
                .setRealName("domain_disease.RNASeqPlatform.Level_3.1.0.0");
        uploadedLevel3RnaSeqArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        uploadedLevel3RnaSeqArchive.setArchiveType(Archive.TYPE_LEVEL_3);
        uploadedLevel3RnaSeqArchive.setPlatform("RNASeqPlatform");

        uploadedLevel3MiRnaSeqArchive = new Archive();
        uploadedLevel3MiRnaSeqArchive
                .setRealName("domain_disease.miRNASeqPlatform.Level_3.1.0.0");
        uploadedLevel3MiRnaSeqArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        uploadedLevel3MiRnaSeqArchive.setArchiveType(Archive.TYPE_LEVEL_3);
        uploadedLevel3MiRnaSeqArchive.setPlatform("miRNASeqPlatform");

        uploadedLevel3DNASeqCArchive = new Archive();
        uploadedLevel3DNASeqCArchive.setRealName("domain_disease.IlluminaHiSeq_DNASeqC.Level_3.10.0.0");
        uploadedLevel3DNASeqCArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        uploadedLevel3DNASeqCArchive.setArchiveType(Archive.TYPE_LEVEL_3);
        uploadedLevel3DNASeqCArchive.setPlatform("IlluminaHiSeq_DNASeqC");


        auxArchive = new Archive();
        auxArchive.setArchiveType(Archive.TYPE_AUX);
        auxArchive.setDeployStatus(Archive.STATUS_UPLOADED);

        cgccChecker.setArchiveQueries(mockArchiveQueries);

        mockCenterQueries = context.mock(CenterQueries.class);
        cgccChecker.setCenterQueries(mockCenterQueries);
        context.checking(new Expectations() {{
            allowing(mockCenterQueries).doesCenterRequireMageTab(with(any(String.class)), with(Experiment.TYPE_CGCC));
            will(returnValue(true));
        }});

    }

    private void stillTime() {
        // set start time to now, so deadline hasn't elapsed
        experiment.setUploadStartDate(Calendar.getInstance().getTime());
    }

    private void noTime() {
        // set start time to 48 hours ago
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY, -48);
        experiment.setUploadStartDate(now.getTime());
    }

    private void addArraySDRF() {
        mageTabArchive.setRealName("domain_disease.platform_mage-tab.1.3.0");
        mageTabArchive
                .setDeployLocation(SAMPLES_DIR
                        + "qclive/experimentChecker/domain_disease.platform_mage-tab.1.3.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        mageTabArchive.setArchiveFile(new File(mageTabArchive
                .getDeployLocation()));
        mageTabArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
        mageTabArchive.setDeployStatus("Uploaded");
        experiment.addArchive(mageTabArchive);
    }

    private void addRnaSeqSDRF() {
        mageTabArchive
                .setRealName("domain_disease.RNASeqPlatform.mage-tab.1.0.0");
        mageTabArchive
                .setDeployLocation(SAMPLES_DIR
                        + "qclive/experimentChecker/domain_disease.RNASeqPlatform.mage-tab.1.0.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        mageTabArchive.setArchiveFile(new File(mageTabArchive
                .getDeployLocation()));
        mageTabArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
        mageTabArchive.setDeployStatus("Uploaded");
        experiment.addArchive(mageTabArchive);
    }

    /*
      * Add an SDRF file for miRNASeq archives
      */
    private void addMageTabArchive(final String mageTabArchiveName) {

        //final String archiveName = "domain_disease.miRNASeqPlatform.mage-tab.1.0.0";

        mageTabArchive.setRealName(mageTabArchiveName);
        mageTabArchive.setDeployLocation(EXPERIMENT_CHECKER_DIR
                + File.separator + mageTabArchiveName
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        mageTabArchive.setArchiveFile(new File(mageTabArchive
                .getDeployLocation()));
        mageTabArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
        mageTabArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        experiment.addArchive(mageTabArchive);
    }

    @Test
    public void testNoSdrf() throws Processor.ProcessorException {
        // make sure expt is set to Pending if not complete but still time left
        stillTime();
        experiment.addArchive(uploadedLevel2Archive);
        qcContext.setArchive(uploadedLevel2Archive);
        addExperimentQueriesExpectations(experimentName);
        Experiment returnVal = checkerForCgcc
                .execute(experimentName, qcContext);
        assertNotNull(returnVal);
        assertEquals(Experiment.STATUS_PENDING, returnVal.getStatus());
        assertTrue(
                qcContext.getErrors().toString(),
                qcContext
                        .getErrors()
                        .contains(
                                "An error occurred while processing experiment 'center_disease.platform': MAGE-TAB archive not found	[archive domain_disease.platform.Level_2.1.1.0]"));
        assertTrue(
                qcContext.getErrors().toString(),
                qcContext
                        .getErrors()
                        .contains(
                                "An error occurred while processing experiment 'center_disease.platform': Contains a level 2 archive but no level 1 archive	[archive domain_disease.platform.Level_2.1.1.0]"));
    }

    @Test
    public void testIncomplete() throws Processor.ProcessorException {
        stillTime();
        addArraySDRF();
        // level 1 is not the latest, level 2 and level 3 archives are not yet
        // uploaded
        context.checking(new Expectations() {
            {
                one(mockArchiveQueries).getArchiveIdByName(LEVEL_1_ARCHIVE);
                will(returnValue(100L));
                one(mockArchiveQueries).getArchiveIdByName(LEVEL_2_ARCHIVE);
                will(returnValue(-1L));
                one(mockArchiveQueries).getArchiveIdByName(LEVEL_3_ARCHIVE);
                will(returnValue(-1L));
            }
        });

        addExperimentQueriesExpectations(experimentName);
        Experiment exp = checkerForCgcc.execute(experimentName, qcContext);
        assertNotNull(exp);
        assertEquals(Experiment.STATUS_PENDING, exp.getStatus());
        assertTrue(qcContext
                .getErrors()
                .contains(
                        "An error occurred while processing experiment 'center_disease.platform': Archive '"
                                + LEVEL_1_ARCHIVE
                                + "' is listed in the SDRF but is not the latest available archive for that type and serial index	[archive domain_disease.platform_mage-tab.1.3.0]"));
        assertTrue(qcContext
                .getErrors()
                .contains(
                        "An error occurred while processing experiment 'center_disease.platform': Archive '"
                                + LEVEL_2_ARCHIVE
                                + "' is listed in the SDRF but has not yet been uploaded	[archive domain_disease.platform_mage-tab.1.3.0]"));
        assertTrue(qcContext
                .getErrors()
                .contains(
                        "An error occurred while processing experiment 'center_disease.platform': Archive '"
                                + LEVEL_3_ARCHIVE
                                + "' is listed in the SDRF but has not yet been uploaded	[archive domain_disease.platform_mage-tab.1.3.0]"));
    }

    @Test
    public void testFailed() throws Processor.ProcessorException {
        // test that check fails if the experiment is not complete and time has
        // run out
        noTime();
        experiment.addArchive(uploadedLevel2Archive);
        addExperimentQueriesExpectations(experimentName);
        checkerForCgcc.execute(experimentName, qcContext);
        assertTrue("Checker should have had errors recorded",
                qcContext.getErrorCount() > 0);
        assertEquals(Experiment.STATUS_FAILED, experiment.getStatus());
    }

    @Test
    public void testCorrect() throws Processor.ProcessorException {
        stillTime();
        // test correct setup
        addArraySDRF();
        experiment.addArchive(availableLevel1Archive);
        experiment.addArchive(uploadedLevel2Archive);
        experiment.addArchive(availableLevel3Archive);
        qcContext.setExperiment(experiment);
        addExperimentQueriesExpectations(experimentName);
        Experiment returnVal = checkerForCgcc
                .execute(experimentName, qcContext);
        assertNotNull(returnVal);
        assertEquals("Experiment name should be set", experimentName,
                experiment.getName());
        assertEquals("Checker had errors: " + qcContext.getErrors().toString(),
                0, qcContext.getErrorCount());
        assertEquals(Experiment.STATUS_CHECKED, experiment.getStatus());
        assertNotNull(experiment.getSdrfFile());
    }

    @Test
    public void testAuxOnly() throws Processor.ProcessorException {
        experiment.addArchive(auxArchive);
        addExperimentQueriesExpectations(experimentName);
        Experiment returnVal = checkerForCgcc
                .execute(experimentName, qcContext);
        assertNotNull(returnVal);
        assertEquals(Experiment.STATUS_CHECKED, experiment.getStatus());
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testWithAux() throws Processor.ProcessorException {
        stillTime();
        // make sure if there is an aux archive plus another archive, it still
        // checks for completeness wrt SDRF/mage-tab archive
        experiment.addArchive(auxArchive);
        experiment.addArchive(uploadedLevel2Archive);
        addExperimentQueriesExpectations(experimentName);
        Experiment returnVal = checkerForCgcc
                .execute(experimentName, qcContext);
        assertNotNull(returnVal);
        // status should be pending because there is still time but there is no
        // mage-tab (plus there is a non-aux archive)
        assertEquals(Experiment.STATUS_PENDING, experiment.getStatus());
        assertTrue(qcContext.getErrorCount() > 0);
    }

    @Test
    public void testGscExperiment() throws Processor.ProcessorException {

        experiment.addArchive(availableLevel1Archive);
        experiment.addArchive(uploadedLevel2Archive);
        experiment.setType(Experiment.TYPE_GSC);

        addExperimentQueriesExpectations(experimentName);

        checkerForNonCgcc.execute(experimentName, qcContext);
        assertEquals(Experiment.STATUS_CHECKED, experiment.getStatus());
        assertTrue(qcContext.getErrorCount() == 0);
    }

    @Test
    public void testBcrExperiment() throws ProcessorException {

        final String experimentArchiveNameFilter = "center_disease.platform.level.serialIndex.revision.series";
        experiment.addArchive(uploadedLevel1Archive);
        experiment.setType(Experiment.TYPE_BCR);
        qcContext.setExperimentArchiveNameFilter(experimentArchiveNameFilter);

        context.checking(new Expectations() {
            {
                one(mockExperimentQueries).getExperimentForSingleArchive(experimentArchiveNameFilter);
                will(returnValue(experiment));
            }
        });

        checkerForNonCgcc.execute(experimentName, qcContext);

        assertEquals(Experiment.STATUS_CHECKED, experiment.getStatus());
        assertTrue(qcContext.getErrorCount() == 0);
    }

    @Test
    public void testGscABI() throws Processor.ProcessorException {
        noTime();
        experiment.setType(Experiment.TYPE_GSC);
        // the "first gen" sequencing platform is this one, so level 1 cannot be
        // skipped
        checkerForNonCgcc.setFirstGenSequencingPlatform("platform");
        experiment.addArchive(uploadedLevel2Archive);
        addExperimentQueriesExpectations(experimentName);
        checkerForNonCgcc.execute(experimentName, qcContext);
        assertEquals(Experiment.STATUS_FAILED, experiment.getStatus());
        assertTrue(qcContext.getErrorCount() > 0);
    }

    @Test
    public void testGscNextGen() throws Processor.ProcessorException {
        noTime();
        experiment.setType(Experiment.TYPE_GSC);
        // the "first gen" sequencing platform is something other than this
        // experiment's platform, so level 1 can be skipped
        checkerForNonCgcc.setFirstGenSequencingPlatform("ABI");
        experiment.addArchive(uploadedLevel2Archive);
        addExperimentQueriesExpectations(experimentName);
        checkerForNonCgcc.execute(experimentName, qcContext);
        assertEquals(Experiment.STATUS_CHECKED, experiment.getStatus());
        assertTrue(qcContext.getErrorCount() == 0);
        assertTrue(qcContext.getWarningCount() > 0);
    }

    @Test
    public void testCgccRNASeq() throws Processor.ProcessorException {

        final String experimentName = "domain_disease.RNASeqPlatform";
        noTime();
        addRnaSeqSDRF();
        experiment.setName("domain_disease.RNASeqPlatform");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(uploadedLevel3RnaSeqArchive);
        addExperimentQueriesExpectations(experimentName);
        checkerForCgcc.execute(experimentName, qcContext);
        assertEquals(Experiment.STATUS_CHECKED, experiment.getStatus());
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testCgccMiRNASeq() throws Processor.ProcessorException {

        final String experimentName = "domain_disease.miRNASeqPlatform";
        noTime();
        addMageTabArchive("domain_disease.miRNASeqPlatform.mage-tab.1.0.0");
        experiment.setName(experimentName);
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(uploadedLevel3MiRnaSeqArchive);
        addExperimentQueriesExpectations(experimentName);
        checkerForCgcc.execute(experimentName, qcContext);
        assertEquals(Experiment.STATUS_CHECKED, experiment.getStatus());
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testIlluminaHiSeqC() throws ProcessorException {
        final String experimentName = "domain_disease.IlluminaHiSeq_DNASeqC";
        noTime();
        addMageTabArchive(experimentName + ".mage-tab.1.0.0");
        experiment.setName(experimentName);
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(uploadedLevel3DNASeqCArchive);
        addExperimentQueriesExpectations(experimentName);
        checkerForNonCgcc.execute(experimentName, qcContext);
        assertEquals(qcContext.getErrors().toString(), Experiment.STATUS_CHECKED, experiment.getStatus());
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testAvailableMageTab() {
        // this experiment has a mage-tab archive that is Available, and there
        // is an Uploaded mage-tab
        // archive for a different serial index. Should fail b/c only one
        // mage-tab allowed
        Archive availableMageTab = new Archive();
        availableMageTab.setArchiveType(Archive.TYPE_MAGE_TAB);
        availableMageTab.setDeployStatus(Archive.STATUS_AVAILABLE);
        availableMageTab.setRealName("available_mage_tab");
        experiment.addArchive(availableMageTab);
        addArraySDRF(); // adds uploaded mage-tab
        stillTime(); // doesn't matter, this is a fatal error
        addExperimentQueriesExpectations(experimentName);

        try {
            checkerForCgcc.execute(experimentName, qcContext);
            fail("exception was not thrown with 2 mage-tabs");
        } catch (Processor.ProcessorException e) {
            assertEquals(Experiment.STATUS_FAILED, experiment.getStatus());
        }
    }

    @Test
    public void testOneNonUploadedMageTabArchive()
            throws Processor.ProcessorException {
        // this experiment has only one mage-tab archive that is not uploaded,
        // Should fail b/c the only one mage-tab should be an uploaded one

        mageTabArchive.setRealName("domain_disease.platform_mage-tab.1.3.0");
        mageTabArchive
                .setDeployLocation(SAMPLES_DIR
                        + "qclive/experimentChecker/domain_disease.platform_mage-tab.1.3.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        mageTabArchive.setArchiveFile(new File(mageTabArchive
                .getDeployLocation()));
        mageTabArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
        mageTabArchive.setDeployStatus(Archive.STATUS_AVAILABLE);
        experiment.addArchive(mageTabArchive);
        experiment.addArchive(uploadedLevel2Archive);
        qcContext.setArchive(uploadedLevel2Archive);
        stillTime();
        addExperimentQueriesExpectations(experimentName);

        checkerForCgcc.execute(experimentName, qcContext);
        assertEquals(Experiment.STATUS_PENDING, experiment.getStatus());
        assertTrue(qcContext.getErrorCount() > 0);
        assertEquals(
                true,
                qcContext
                        .getErrors()
                        .contains(
                                "An error occurred while processing experiment 'center_disease.platform': Updated mage-tab archive has not been uploaded yet.	[archive domain_disease.platform.Level_2.1.1.0]"));
    }

    @Test
    public void testNoUploadedArchives() throws Processor.ProcessorException {
        // experiment has only Available archives
        experiment.addArchive(availableLevel1Archive);
        experiment.addArchive(availableLevel3Archive);
        noTime();
        addExperimentQueriesExpectations(experimentName);

        try {
            checkerForCgcc.execute(experimentName, qcContext);
            fail("Exception was not thrown");
        } catch (Processor.ProcessorException e) {
            assertEquals(Experiment.STATUS_UP_TO_DATE, qcContext
                    .getExperiment().getStatus());
        }
    }

    @Test
    public void testNoUploadedArchivesWithErrors()
            throws Processor.ProcessorException {
        experiment.addArchive(availableLevel1Archive);
        experiment.addArchive(availableMageTabArchive);
        addExperimentQueriesExpectations(experimentName);

        try {
            checkerForCgcc.execute(experimentName, qcContext);
            fail("ProcessorException was not thrown");
        } catch (Processor.ProcessorException e) {
            assertEquals(Experiment.STATUS_UP_TO_DATE, qcContext
                    .getExperiment().getStatus());
            assertEquals(1, qcContext.getErrorCount());
        }
    }

    @Test
    public void testLevel2MiRna() throws ProcessorException {
        uploadedLevel3MiRnaSeqArchive.setArchiveType(Archive.TYPE_LEVEL_2);
        experiment.addArchive(uploadedLevel3MiRnaSeqArchive);
        stillTime();
        final String experimentName = "center_disease." + MiRNASeqDataFileValidator.MIRNASEQ;
        experiment.setName(experimentName);
        addExperimentQueriesExpectations(experimentName);

        try {
            checkerForCgcc.execute(experimentName, qcContext);
            fail("Exception was expected to be thrown");
        } catch (ProcessorException e) {
            assertEquals(Experiment.STATUS_FAILED, experiment.getStatus());
        }
    }

    @Test
    public void testLevel1MiRna() throws ProcessorException {
        uploadedLevel3MiRnaSeqArchive.setArchiveType(Archive.TYPE_LEVEL_1);
        experiment.addArchive(uploadedLevel3MiRnaSeqArchive);
        stillTime();
        final String experimentName = "center_disease." + MiRNASeqDataFileValidator.MIRNASEQ;
        experiment.setName(experimentName);
        addExperimentQueriesExpectations(experimentName);

        try {
            checkerForCgcc.execute(experimentName, qcContext);
            fail("Exception was expected to be thrown");
        } catch (ProcessorException e) {
            assertEquals(Experiment.STATUS_FAILED, experiment.getStatus());
        }
    }

    @Test
    public void testLevel1RNASeq() throws ProcessorException {
        uploadedLevel3RnaSeqArchive.setArchiveType(Archive.TYPE_LEVEL_1);
        experiment.addArchive(uploadedLevel3RnaSeqArchive);
        stillTime();
        final String experimentName = "center_disease." + RNASeqDataFileValidator.RNASEQ;
        experiment.setName(experimentName);
        addExperimentQueriesExpectations(experimentName);

        try {
            checkerForCgcc.execute(experimentName, qcContext);
            fail("Exception was expected to be thrown");
        } catch (ProcessorException e) {
            assertEquals(Experiment.STATUS_FAILED, experiment.getStatus());
        }
    }

    @Test
    public void testLevel2RNASeq() throws ProcessorException {
        uploadedLevel3RnaSeqArchive.setArchiveType(Archive.TYPE_LEVEL_2);
        experiment.addArchive(uploadedLevel3RnaSeqArchive);
        stillTime();
        final String experimentName = "domain_disease.RNASeqPlatform";
        addMageTabArchive(experimentName + ".mage-tab.1.0.0");
        experiment.setName(experimentName);
        addExperimentQueriesExpectations(experimentName);

        checkerForCgcc.execute(experimentName, qcContext);
        assertEquals(Experiment.STATUS_CHECKED, experiment.getStatus());
        assertEquals(qcContext.getErrors().toString(), 0, qcContext.getErrorCount());
    }

    @Test
    public void testGDAC() throws ProcessorException {
        Archive gdacArchive = new Archive();
        gdacArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        gdacArchive.setArchiveType(Archive.TYPE_LEVEL_4);
        gdacArchive.setPlatform("fh_reports");
        experiment.setType(Experiment.TYPE_GDAC);
        experiment.addArchive(gdacArchive);
        experiment.setName("broad.mit.edu_TEST.fh_reports");
        stillTime();
        addExperimentQueriesExpectations(experiment.getName());

        checkerForNonCgcc.execute(experiment.getName(), qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(Experiment.STATUS_CHECKED, experiment.getStatus());
    }

    @Test
    public void testExpectingLevel1WhenLevel2() {

        final Archive bioArchive = new Archive();
        bioArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        bioArchive.setDataLevel(2);

        experiment.setName("intgen.org_CNTL.bio.Level_2.0.4.0");
        experiment.addArchive(bioArchive);
        
        addExperimentQueriesExpectations(experiment.getName());

        try {
            checkerForNonCgcc.execute(experiment.getName(), qcContext);
            
        } catch (final ProcessorException e) {

            final String expectedErrorMessage = "The DCC is only accepting level 1 for bio archives, but found level 2";
            assertEquals(expectedErrorMessage, e.getMessage());
            assertEquals(1, qcContext.getErrorCount());
            
            final List<String> errors = qcContext.getErrors();
            assertNotNull(errors);
            assertEquals(1, errors.size());

            final String errorMessage = errors.get(0);
            assertEquals(expectedErrorMessage, errorMessage);
        }
    }

    @Test
    public void testExpectingLevel1WhenLevel1() throws ProcessorException {

        final Archive bioArchive = new Archive();
        bioArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        bioArchive.setDataLevel(1);

        experiment.setName("intgen.org_CNTL.bio.Level_1.0.4.0");
        experiment.addArchive(bioArchive);

        addExperimentQueriesExpectations(experiment.getName());
        checkerForNonCgcc.execute(experiment.getName(), qcContext);

        assertEquals(0, qcContext.getErrorCount());
    }

    /**
     * Add an expectation for the mock ExperimentQueries
     *
     * @param experimentName the experiment name
     */
    private void addExperimentQueriesExpectations(final String experimentName) {

        context.checking(new Expectations() {
            {
                one(mockExperimentQueries).getExperiment(with(experimentName));
                will(returnValue(experiment));
            }
        });
    }

    public class TestableExperiment extends Experiment {

        public File getSdrfFile() {
            return sdrfFile;
        }
    }
}
