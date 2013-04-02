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
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ArchiveUtilImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for ClinicalDateObscurer.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ClinicalDateObscurerFastTest {

    private ClinicalDateObscurer dateObscurer;
    private Archive archive;
    private ArchiveUtilImpl archiveUtilImpl;

    private final Mockery mockery = new JUnit4Mockery();
    private BCRUtils mockBcrUtils;

    private static final String FILE_SEPARATOR = System
            .getProperty("file.separator");

    private static final String SAMPLE_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples/qclive").getPath()
            + FILE_SEPARATOR;

    private static final String CLINICAL_DATE_OBSCURER_TEST_DIR = SAMPLE_DIR
            + "clinicalDateObscurer" + FILE_SEPARATOR;

    private static final String CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR = CLINICAL_DATE_OBSCURER_TEST_DIR
            + "clinical" + FILE_SEPARATOR;
    private static final String CLINICAL_DATE_OBSCURER_BIOSPECIMEN_TEST_DIR = CLINICAL_DATE_OBSCURER_TEST_DIR
            + "biospecimen" + FILE_SEPARATOR;
    private static final String CLINICAL_DATE_OBSCURER_OTHER_TEST_DIR = CLINICAL_DATE_OBSCURER_TEST_DIR
            + "other" + FILE_SEPARATOR;

    private static final String DUMMY_ARCHIVE_FILE = SAMPLE_DIR + "dummy"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;

    private static final String TEST_DIR = SAMPLE_DIR + "archiveUtilImpl"
            + FILE_SEPARATOR;

    private static final String MANIFEST_DEPLOY_DIR = TEST_DIR + "manifest";

    private static final String MANIFEST_DEPLOY_LOCATION = MANIFEST_DEPLOY_DIR
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;

    private static final String TEST_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "test-clinical.xml";
    private static final String EXPECTED_TEST_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "test-clinical-Expected.xml";

    private static final String MISSING_BASIS_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "missingBasis-clinical.xml";
    private static final String EXPECTED_MISSING_BASIS_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "missingBasis-clinical-Expected.xml";

    private static final String MISSING_DATES_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "missingDates-clinical.xml";
    private static final String EXPECTED_MISSING_DATES_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "missingDates-clinical-Expected.xml";

    private static final String MISSING_MONTH_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "missingBirthMonth-clinical.xml";
    private static final String EXPECTED_MISSING_MONTH_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "missingBirthMonth-clinical-Expected.xml";

    private static final String REAL_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "real-clinical.xml";
    private static final String EXPECTED_REAL_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "real-clinical-Expected.xml";

    private static final String COMPLETE_SAMPLE_ATTRIBUTES_ON_SEVERAL_LINES_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "completeSampleAttributesOnSeveralLines-clinical.xml";
    private static final String EXPECTED_COMPLETE_SAMPLE_ATTRIBUTES_ON_SEVERAL_LINES_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "completeSampleAttributesOnSeveralLines-clinical-Expected.xml";

    private static final String AGE_AT_INITIAL_DIAGNOSIS_EQUALS_90_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "ageAtInitialDiagnosisEquals90-clinical.xml";
    private static final String EXPECTED_AGE_AT_INITIAL_DIAGNOSIS_EQUALS_90_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "ageAtInitialDiagnosisEquals90-clinical-Expected.xml";

    private static final String AGE_AT_INITIAL_DIAGNOSIS_UNDER_90_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "ageAtInitialDiagnosisUnder90-clinical.xml";
    private static final String EXPECTED_AGE_AT_INITIAL_DIAGNOSIS_UNDER_90_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "ageAtInitialDiagnosisUnder90-clinical-Expected.xml";

    private static final String AGE_AT_INITIAL_DIAGNOSIS_90_PLUS_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "ageAtInitialDiagnosis90Plus-clinical.xml";
    private static final String EXPECTED_AGE_AT_INITIAL_DIAGNOSIS_90_PLUS_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "ageAtInitialDiagnosis90Plus-clinical-Expected.xml";

    private static final String REAL_LOWERCASE_AGE_AT_INITIAL_DIAGNOSIS_90_PLUS_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "realLowerCasePatientOver90-clinical.xml";
    private static final String EXPECTED_REAL_LOWERCASE_AGE_AT_INITIAL_DIAGNOSIS_90_PLUS_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "realLowerCasePatientOver90-clinical-Expected.xml";

    private static final String YEAR_ONLY_DATE_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "yearOnlyDate-clinical.xml";
    private static final String EXPECTED_YEAR_ONLY_DATE_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "yearOnlyDate-clinical-Expected.xml";

    private static final String NO_PROCUREMENT_STATUS_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "noProcurementStatus-clinical.xml";
    private static final String EXPECTED_NO_PROCUREMENT_STATUS_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "noProcurementStatus-clinical-Expected.xml";

    private static final String DIFFERENT_NAMESPACE_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "differentNamespace-clinical.xml";
    private static final String EXPECTED_DIFFERENT_NAMESPACE_FILE = CLINICAL_DATE_OBSCURER_CLINICAL_TEST_DIR
            + "differentNamespace-clinical-Expected.xml";

    private static final String BIOSPECIMEN_FILE = CLINICAL_DATE_OBSCURER_BIOSPECIMEN_TEST_DIR
            + "biospecimen.xml";
    private static final String EXPECTED_BIOSPECIMEN_FILE = CLINICAL_DATE_OBSCURER_BIOSPECIMEN_TEST_DIR
            + "biospecimen-Expected.xml";

    private static final String BIOSPECIMEN_MONTH_PRECISION_FILE = CLINICAL_DATE_OBSCURER_BIOSPECIMEN_TEST_DIR
            + "biospecimenMonthPrecision.xml";
    private static final String EXPECTED_BIOSPECIMEN_MONTH_PRECISION_FILE = CLINICAL_DATE_OBSCURER_BIOSPECIMEN_TEST_DIR
            + "biospecimenMonthPrecision-Expected.xml";

    private static final String BIOSPECIMEN_YEAR_ONLY_FILE = CLINICAL_DATE_OBSCURER_BIOSPECIMEN_TEST_DIR
            + "biospecimenYearOnly.xml";
    private static final String EXPECTED_BIOSPECIMEN_YEAR_ONLY_FILE = CLINICAL_DATE_OBSCURER_BIOSPECIMEN_TEST_DIR
            + "biospecimenYearOnly-Expected.xml";

    private static final String BIOSPECIMEN_DIFFERENT_NAMESPACE_FILE = CLINICAL_DATE_OBSCURER_BIOSPECIMEN_TEST_DIR
            + "differentNamespace-biospecimen.xml";
    private static final String EXPECTED_BIOSPECIMEN_DIFFERENT_NAMESPACE_FILE = CLINICAL_DATE_OBSCURER_BIOSPECIMEN_TEST_DIR
            + "differentNamespace-biospecimen-Expected.xml";

    private static final String OTHER_FILE = CLINICAL_DATE_OBSCURER_OTHER_TEST_DIR
            + "other.xml";

    private static final String EMPTY_README_MD5 = "d41d8cd98f00b204e9800998ecf8427e";
    private static final String NON_EMPTY_README_MD5 = "e7df7cd2ca07f4f1ab415d457a6e1c13";
    private static final String REAL_LOWERCASE_PATIENT_OVER_90_NON_EMPTY_README_MD5 = "6360f35900b4ff10ef08cfec0691ce58";

    private static final String README_FILENAME = "README_HIPAA_AGES.txt";

    @Before
    public void setUp() {

        archive = new Archive();
        archive.setArchiveFile(new File(DUMMY_ARCHIVE_FILE));
        archive.setId(1L);
        archive.setDeployLocation(MANIFEST_DEPLOY_LOCATION);

        archive.setExperimentType(Experiment.TYPE_BCR);

        archiveUtilImpl = new ArchiveUtilImpl();
        archiveUtilImpl.setManifestParser(new ManifestParserImpl());

        mockBcrUtils = mockery.mock(BCRUtils.class);
    }

    @After
    public void tearDown() {

        // Reset the sample Manifest
        final File manifest = new File(MANIFEST_DEPLOY_DIR,
                ManifestValidator.MANIFEST_FILE);
        try {

            if (manifest.exists()) {
                manifest.delete();
            }
            manifest.createNewFile();

            assertTrue("The manifest doe not exist: ", manifest.exists());

        } catch (IOException e) {
            fail("The file '" + manifest.getPath() + manifest.getName()
                    + " could not be created: " + e.getMessage());
        }

        // Delete the README
        final File readme = new File(MANIFEST_DEPLOY_DIR,
                ClinicalDateObscurer.README_FILENAME);
        if (readme.exists()) {
            readme.delete();
        }
    }

    private void createDateObscurer(final String file) {
        dateObscurer = new ClinicalDateObscurer() {
            @Override
            protected File[] getFilesForExtension(final Archive archive) {
                return new File[]{new File(file)};
            }
        };

        dateObscurer.setBcrUtils(mockBcrUtils);
    }

    /**
     * Setup the dateObscurer
     *
     * @param useLowerCase                <code>true</code> if the XML schema uses the latest schema
     *                                    (with lower case eement names), <code>false</code> otherwise
     * @param addComprehensiveCdeForDatesToObscure
     *                                    <code>true</code> if the setup should add a comprehensive list
     *                                    of the CDE for dates to obscure, <code>false</code> otherwise
     * @param addFakeCdeForDatesToObscure <code>true</code> if the setup should add fake CDE for dates
     *                                    to obscure, <code>false</code> otherwise
     * @param useLatestBasisDateName      <code>true</code> if the setup should use
     *                                    <code>INITIALPATHOLOGICDIAGNOSIS</code> and
     *                                    <code>initial_pathologic_diagnosis</code> for the basis date
     *                                    name, <code>false</code> otherwise
     */
    private void setupDateObscurer(final boolean useLowerCase,
                                   final boolean addComprehensiveCdeForDatesToObscure,
                                   final boolean addFakeCdeForDatesToObscure,
                                   final boolean useLatestBasisDateName) {

        final String elapsedElementBase;
        final String basisDateName;
        final String birthDateName;
        final String ageAtBasisDateCDE;
        final String ageAtPrefix;
        final String dayOfPrefix;
        final String monthOfPrefix;
        final String yearOfPrefix;
        String cdeForDatesToObscureString;
        final String datesNotToObscureString;
        final Integer cutoffAgeAtInitialDiagnosis;
        final String bcrPatientBarcodeElementName;
        final Integer daysToBirthLowerBound;

        if (useLowerCase) {

            if (useLatestBasisDateName) {
                basisDateName = "initial_pathologic_diagnosis";
            } else {
                basisDateName = "initial_diagnosis";
            }

            elapsedElementBase = "days_to_";
            birthDateName = "birth";
            ageAtBasisDateCDE = "000";
            ageAtPrefix = "age_at_";
            dayOfPrefix = "day_of_";
            monthOfPrefix = "month_of_";
            yearOfPrefix = "year_of_";
            cutoffAgeAtInitialDiagnosis = 90;
            bcrPatientBarcodeElementName = "bcr_patient_barcode";
            daysToBirthLowerBound = -32872;
            cdeForDatesToObscureString = "birth#1234,death#5678,collection,"
                    + basisDateName;
            datesNotToObscureString = "creation,shipment,dcc_upload";

            if (addComprehensiveCdeForDatesToObscure) {
                cdeForDatesToObscureString += ",last_followup,tumor_recurrence,tumor_progression,drug_treatment_start,drug_treatment_end,radiation_treatment_start,radiation_treatment_end,procedure";
            }

            if (addFakeCdeForDatesToObscure) {
                cdeForDatesToObscureString += ",squirrel,chipmunk";
            }

        } else {

            if (useLatestBasisDateName) {
                basisDateName = "INITIALPATHOLOGICDIAGNOSIS";
            } else {
                basisDateName = "INITIALDIAGNOSIS";
            }

            elapsedElementBase = "DAYSTO";
            birthDateName = "BIRTH";
            ageAtBasisDateCDE = "000";
            ageAtPrefix = "AGEAT";
            dayOfPrefix = "DAYOF";
            monthOfPrefix = "MONTHOF";
            yearOfPrefix = "YEAROF";
            cutoffAgeAtInitialDiagnosis = 90;
            bcrPatientBarcodeElementName = "BARCODE";
            daysToBirthLowerBound = -32872;
            cdeForDatesToObscureString = "BIRTH#1234,DEATH#5678,COLLECTION,"
                    + basisDateName;
            datesNotToObscureString = "CREATION,SHIPMENT,DCC_UPLOAD";

            if (addComprehensiveCdeForDatesToObscure) {
                cdeForDatesToObscureString += ",LASTFOLLOWUP,TUMORRECURRENCE,TUMORPROGRESSION,DRUGTREATMENTSTART,DRUGTREATMENTEND,RADIATIONTREATMENTSTART,RADIATIONTREATMENTEND,PROCEDURE";
            }

            if (addFakeCdeForDatesToObscure) {
                cdeForDatesToObscureString += ",SQUIRREL,CHIPMUNK";
            }
        }

        dateObscurer.setElapsedElementBase(elapsedElementBase);
        dateObscurer.setBasisDateNameForClinical(basisDateName);
        dateObscurer.setBasisDateNameForBiospecimen("index");
        dateObscurer.setBirthDateName(birthDateName);
        dateObscurer.setAgeAtBasisDateCDE(ageAtBasisDateCDE);
        dateObscurer.setAgeAtPrefix(ageAtPrefix);
        dateObscurer.setDayOfPrefix(dayOfPrefix);
        dateObscurer.setMonthOfPrefix(monthOfPrefix);
        dateObscurer.setYearOfPrefix(yearOfPrefix);
        dateObscurer.setCdeForDatesToObscureString(cdeForDatesToObscureString);
        dateObscurer.setDatesNotToObscureString(datesNotToObscureString);
        dateObscurer
                .setCutoffAgeAtInitialDiagnosis(cutoffAgeAtInitialDiagnosis);
        dateObscurer
                .setBcrPatientBarcodeElementName(bcrPatientBarcodeElementName);
        dateObscurer.setDaysToBirthLowerBound(daysToBirthLowerBound);

        dateObscurer.setArchiveUtilImpl(archiveUtilImpl);
    }

    @Test
    public void testSetCdeForDatesToObscureString() {

        dateObscurer = new ClinicalDateObscurer();
        dateObscurer.setBcrUtils(mockBcrUtils);

        // string is comma-separated, and key-value pairs are separated by pound
        // if no value, can omit colon
        dateObscurer.setCdeForDatesToObscureString("a#1,b#2,c,d#,e#hello");

        assertEquals("1", dateObscurer.getCdeForDatesToObscure().get("a"));
        assertEquals("2", dateObscurer.getCdeForDatesToObscure().get("b"));
        assertEquals("", dateObscurer.getCdeForDatesToObscure().get("c"));
        assertEquals("", dateObscurer.getCdeForDatesToObscure().get("d"));
        assertEquals("hello", dateObscurer.getCdeForDatesToObscure().get("e"));
    }

    @Test
    public void testExecute() throws IOException, Processor.ProcessorException {

        addMockBcrUtilsExpectation(true, null, null, null);
        testFile(TEST_FILE, EXPECTED_TEST_FILE, EMPTY_README_MD5, "", false,
                false, true, false);
    }

    @Test
    public void testExecuteWithMissingBasisDate()
            throws Processor.ProcessorException, IOException {

        addMockBcrUtilsExpectation(true, null, null, null);
        testFile(MISSING_BASIS_FILE, EXPECTED_MISSING_BASIS_FILE,
                EMPTY_README_MD5, "", false, false, true, false);
    }

    @Test
    public void testExecuteWithEmptyDates()
            throws Processor.ProcessorException, IOException {

        addMockBcrUtilsExpectation(true, null, null, null);
        testFile(MISSING_DATES_FILE, EXPECTED_MISSING_DATES_FILE,
                EMPTY_README_MD5, "", false, false, true, false);
    }

    /**
     * Add an expectation for the call of methods on mockBcrUtils
     *
     * @param isClinicalFileExpectedReturnValue
     *         the return value for isClinicalFile() call
     * @param isBiospecimenFileExpectedReturnValue
     *         the return value for isBiospecimenFile() call
     * @param isAuxiliaryFileExpectedReturnValue
     *         the return value for isAuxiliaryFile() call
     */
    private void addMockBcrUtilsExpectation(final Boolean isClinicalFileExpectedReturnValue,
                                            final Boolean isBiospecimenFileExpectedReturnValue,
                                            final Boolean isAuxiliaryFileExpectedReturnValue,
                                            final Boolean isControlFileExpectedReturnValue) {

        mockery.checking(new Expectations() {{

            if (isClinicalFileExpectedReturnValue != null) {
                allowing(mockBcrUtils).isClinicalFile(with(any(File.class)));
                will(returnValue(isClinicalFileExpectedReturnValue));
            }

            if (isBiospecimenFileExpectedReturnValue != null) {
                allowing(mockBcrUtils).isBiospecimenFile(with(any(File.class)));
                will(returnValue(isBiospecimenFileExpectedReturnValue));
            }

            if (isAuxiliaryFileExpectedReturnValue != null) {
                allowing(mockBcrUtils).isAuxiliaryFile(with(any(File.class)));
                will(returnValue(isAuxiliaryFileExpectedReturnValue));
            }

            if (isControlFileExpectedReturnValue != null) {
                allowing(mockBcrUtils).isControlFile(with(any(File.class)));
                will(returnValue(isControlFileExpectedReturnValue));
            }

        }});
    }

    /**
     * @param testFile                       the name of the file to test
     * @param expectedFile                   the name of the expected file
     * @param expectedReadmeMD5              the expected MD5 of the README (created for containing
     *                                       barcodes of patients over 90 years old)
     * @param expectedReadmeContent          the expected content of the README (created for containing
     *                                       barcodes of patients over 90 years old)
     * @param useLowerCase                   <code>true</code> if the XML schema uses the latest schema
     *                                       (with lower case eement names), <code>false</code> otherwise
     * @param addComprehensiveDatesToObscure <code>true</code> if the setup should add a comprehensive list
     *                                       of the dates to obscure, <code>false</code> otherwise
     * @param addFakeDatesToObscure          <code>true</code> if the setup should add fake dates to
     *                                       obscure, <code>false</code> otherwise
     * @param useLatestBasisDateName         <code>true</code> if the setup should use
     *                                       <code>INITIALPATHOLOGICDIAGNOSIS</code> and
     *                                       <code>initial_pathologic_diagnosis</code> for the basis date
     *                                       name, <code>false</code> otherwise
     * @throws IOException
     * @throws Processor.ProcessorException
     */
    private void testFile(final String testFile, final String expectedFile,
                          final String expectedReadmeMD5, final String expectedReadmeContent,
                          final boolean useLowerCase,
                          final boolean addComprehensiveDatesToObscure,
                          final boolean addFakeDatesToObscure,
                          final boolean useLatestBasisDateName) throws IOException,
            Processor.ProcessorException {

        createDateObscurer(testFile);
        setupDateObscurer(useLowerCase, addComprehensiveDatesToObscure,
                addFakeDatesToObscure, useLatestBasisDateName);
        runTest(testFile, expectedFile, expectedReadmeMD5,
                expectedReadmeContent);
    }

    private void runTest(final String testFile, final String expectedFile,
                         final String expectedReadmeMD5, final String expectedReadmeContent)
            throws IOException, Processor.ProcessorException {
        File fileToTest = new File(testFile);
        final String initialContents = FileUtil.readFile(fileToTest, true);
        try {
            QcContext qcContext = new QcContext();
            dateObscurer.execute(archive, qcContext);
            final String expectedContent = FileUtil.readFile(new File(
                    expectedFile), true);
            assertTrue(qcContext.getAlteredFiles().containsKey(
                    fileToTest.getName()));

            checkFileContent(fileToTest, expectedContent);

            if (expectedReadmeMD5 != null) {
                checkFileContent(new File(archive.getDeployDirectory(),
                        ManifestValidator.MANIFEST_FILE), expectedReadmeMD5
                        + "  " + README_FILENAME + "\n");
            }

            if (expectedReadmeContent != null) {
                checkFileContent(new File(archive.getDeployDirectory(),
                        README_FILENAME), expectedReadmeContent);
            }

        } finally {
            if (initialContents != null && initialContents.length() > 0) {
                final PrintWriter out = new PrintWriter(new BufferedWriter(
                        new FileWriter(testFile)));
                out.write(initialContents);
                out.close();
            }
        }
    }

    /**
     * Check that the content of a given file matches with the expected content.
     *
     * @param file            the file of which to verify the content
     * @param expectedContent the expected content
     * @throws IOException
     */
    private void checkFileContent(final File file, final String expectedContent)
            throws IOException {

        assertNotNull(file);
        String actualContent = FileUtil.readFile(file, true);
        assertEquals("Contents don't match: ", expectedContent, actualContent);
    }

    @Test
    public void testWithRealXml() throws Processor.ProcessorException,
            IOException {
        // the "real" XML based on a real clinical file has different settings
        // for the Obscurer
        // note this is the same as the setup in the Spring configuration
        createDateObscurer(REAL_FILE);
        setupDateObscurer(false, true, false, true);
        addMockBcrUtilsExpectation(true, null, null, null);
        runTest(REAL_FILE, EXPECTED_REAL_FILE, EMPTY_README_MD5, "");
    }

    /**
     * This tests an complete XML file in which some attributes are spread on
     * several lines
     *
     * @throws Processor.ProcessorException
     * @throws IOException
     */
    @Test
    public void testWithCompleteXmlSampleWithAttributesOnSeveralLines()
            throws Processor.ProcessorException, IOException {
        // the "real" XML based on a real clinical file has different settings
        // for the Obscurer
        // note this is the same as the setup in the Spring configuration
        createDateObscurer(COMPLETE_SAMPLE_ATTRIBUTES_ON_SEVERAL_LINES_FILE);
        setupDateObscurer(false, true, false, true);
        addMockBcrUtilsExpectation(true, null, null, null);
        runTest(COMPLETE_SAMPLE_ATTRIBUTES_ON_SEVERAL_LINES_FILE,
                EXPECTED_COMPLETE_SAMPLE_ATTRIBUTES_ON_SEVERAL_LINES_FILE,
                EMPTY_README_MD5, "");
    }

    @Test
    public void testWithMissingMonth() throws Processor.ProcessorException,
            IOException {

        addMockBcrUtilsExpectation(true, null, null, null);
        testFile(MISSING_MONTH_FILE, EXPECTED_MISSING_MONTH_FILE,
                EMPTY_README_MD5, "", false, false, true, false);
    }

    @Test
    public void testWithYearOnlyDate() throws Processor.ProcessorException,
            IOException {
        addMockBcrUtilsExpectation(true, null, null, null);
        testFile(YEAR_ONLY_DATE_FILE, EXPECTED_YEAR_ONLY_DATE_FILE,
                EMPTY_README_MD5, "", true, false, false, true);
    }

    @Test
    public void testCalculateAgeAtBasisDate() {
        createDateObscurer(REAL_FILE);
        assertEquals(50, dateObscurer.calculateAgeAtBasisDate("2000", "9",
                "15", "1950", "6", "15", "day"));
        assertEquals(50, dateObscurer.calculateAgeAtBasisDate("2000", "6",
                "15", "1950", "6", "15", "day"));
        assertEquals(49, dateObscurer.calculateAgeAtBasisDate("2000", "3",
                "15", "1950", "6", "15", "day"));
        assertEquals(49, dateObscurer.calculateAgeAtBasisDate("2000", "6",
                "10", "1950", "6", "15", "day"));
    }

    @Test
    public void testYearAtInitialDiagnosisEquals90() throws IOException,
            Processor.ProcessorException {
        addMockBcrUtilsExpectation(true, null, null, null);
        testFile(AGE_AT_INITIAL_DIAGNOSIS_EQUALS_90_FILE,
                EXPECTED_AGE_AT_INITIAL_DIAGNOSIS_EQUALS_90_FILE,
                EMPTY_README_MD5, "", false, false, true, false);
    }

    @Test
    public void testYearAtInitialDiagnosisUnder90() throws IOException,
            Processor.ProcessorException {
        addMockBcrUtilsExpectation(true, null, null, null);
        testFile(AGE_AT_INITIAL_DIAGNOSIS_UNDER_90_FILE,
                EXPECTED_AGE_AT_INITIAL_DIAGNOSIS_UNDER_90_FILE,
                EMPTY_README_MD5, "", false, false, true, false);
    }

    @Test
    public void testYearAtInitialDiagnosis90Plus() throws IOException,
            Processor.ProcessorException {
        addMockBcrUtilsExpectation(true, null, null, null);
        testFile(AGE_AT_INITIAL_DIAGNOSIS_90_PLUS_FILE,
                EXPECTED_AGE_AT_INITIAL_DIAGNOSIS_90_PLUS_FILE,
                NON_EMPTY_README_MD5, "1234\n", false, false, true, false);
    }

    @Test
    public void testRealLowerCasePatientOver90() throws IOException,
            Processor.ProcessorException {
        addMockBcrUtilsExpectation(true, null, null, null);
        testFile(REAL_LOWERCASE_AGE_AT_INITIAL_DIAGNOSIS_90_PLUS_FILE,
                EXPECTED_REAL_LOWERCASE_AGE_AT_INITIAL_DIAGNOSIS_90_PLUS_FILE,
                REAL_LOWERCASE_PATIENT_OVER_90_NON_EMPTY_README_MD5,
                "TCGA-12-3644\n", true, true, false, true);
    }

    @Test
    public void testBiospecimenFile() throws Processor.ProcessorException,
            IOException {
        addMockBcrUtilsExpectation(false, true, null, null);
        testFile(BIOSPECIMEN_FILE, EXPECTED_BIOSPECIMEN_FILE, null, null, true,
                false, false, true);
    }

    @Test
    public void testBiospecimenMonthPrecisionFile()
            throws Processor.ProcessorException, IOException {
        addMockBcrUtilsExpectation(false, true, null, null);
        testFile(BIOSPECIMEN_MONTH_PRECISION_FILE,
                EXPECTED_BIOSPECIMEN_MONTH_PRECISION_FILE, null, null, true,
                false, false, true);
    }

    @Test
    public void testBiospecimenYearOnlyFile()
            throws Processor.ProcessorException, IOException {
        addMockBcrUtilsExpectation(false, true, null, null);
        testFile(BIOSPECIMEN_YEAR_ONLY_FILE,
                EXPECTED_BIOSPECIMEN_YEAR_ONLY_FILE, null, null, true, false,
                false, true);
    }

    @Test
    public void testBiospecimenDifferentNamespaceFile()
            throws Processor.ProcessorException, IOException {
        addMockBcrUtilsExpectation(false, true, null, null);
        testFile(BIOSPECIMEN_DIFFERENT_NAMESPACE_FILE,
                EXPECTED_BIOSPECIMEN_DIFFERENT_NAMESPACE_FILE, null, null,
                true, false, false, true);
    }

    @Test()
    public void testNeitherClinicalNeitherBiospecimenNeitherAuxiliaryNeitherControl() {

        createDateObscurer(OTHER_FILE);
        final QcContext qcContext = new QcContext();
        addMockBcrUtilsExpectation(false, false, false, false);
        try {
            dateObscurer.execute(archive, qcContext);
            fail("ProcessorException should have been thrown");

        } catch (final Processor.ProcessorException e) {
            Assert.assertEquals("Unsupported XML file 'other.xml'",
                    e.getMessage());
        }
    }

    @Test()
    public void testAuxiliary() throws Processor.ProcessorException {

        createDateObscurer(OTHER_FILE);
        final QcContext qcContext = new QcContext();
        addMockBcrUtilsExpectation(false, false, true, false);
        final Archive result = dateObscurer.execute(archive, qcContext);

        assertSame(archive, result);
    }

    @Test()
    public void testControl() throws Processor.ProcessorException {

        createDateObscurer(OTHER_FILE);
        final QcContext qcContext = new QcContext();
        addMockBcrUtilsExpectation(false, false, false, true);
        final Archive result = dateObscurer.execute(archive, qcContext);

        assertSame(archive, result);
    }

    @Test
    public void testDateWithoutProcurementStatusOrOwner()
            throws Processor.ProcessorException, IOException {
        addMockBcrUtilsExpectation(true, false, false, false);
        testFile(NO_PROCUREMENT_STATUS_FILE,
                EXPECTED_NO_PROCUREMENT_STATUS_FILE, EMPTY_README_MD5, "",
                true, false, false, true);
    }

    @Test
    public void testDifferentNamespace() throws IOException,
            Processor.ProcessorException {
        addMockBcrUtilsExpectation(true, false, false, false);
        testFile(DIFFERENT_NAMESPACE_FILE, EXPECTED_DIFFERENT_NAMESPACE_FILE,
                EMPTY_README_MD5, "", false, false, true, false);
    }
}
