/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImplFastTest;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationErrors;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResult;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResults;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.BiospecimenIdWsQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.ValidationWebServiceQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl.WebServiceException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for QcLiveBarcodeAndUUIDValidatorImpl.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QcLiveBarcodeAndUUIDValidatorImplFastTest {

    /**
     * Valid codes for barcode
     */
    private static final String PROJECT_NAME = "TCGA";
    private static final String TSS_CODE = "01";
    private static final String PATIENT_CODE = "2345";
    private static final String SAMPLE_TYPE = "67";
    private static final String SAMPLE_NUMBER = "A";
    private static final String PORTION_NUMBER = "89";
    private static final String PORTION_ANALYTE = "B";
    private static final String PLATE_ID = "0123";
    private static final String BCR_CENTER_ID = "45";

    private static final String VALID_UUID = "69de087d-e31d-4ff5-a760-6be8da96b6e2";

    private final Mockery mockery = new JUnit4Mockery();
    private QcLiveBarcodeAndUUIDValidatorImpl barcodeAndUUIDValidatorImpl;
    private CodeTableQueries mockCodeTableQueries;
    private QcContext qcContext;
    private BCRIDQueries mockBcrIdQueries;

    private BiospecimenIdWsQueries mockBiospecimenIdWsQueries;
    private ValidationWebServiceQueries mockValidationWebServiceQueries;
    private QcLiveBarcodeAndUUIDValidatorImpl wsBarcodeAndUUIDValidatorImpl; // an instance for testing the webservice calls

    @Before
    public void setUp() {
        
        barcodeAndUUIDValidatorImpl = new QcLiveBarcodeAndUUIDValidatorImpl();
        mockCodeTableQueries = mockery.mock(CodeTableQueries.class);
        barcodeAndUUIDValidatorImpl.setCodeTableQueries(mockCodeTableQueries);

        mockBcrIdQueries = mockery.mock(BCRIDQueries.class);
        barcodeAndUUIDValidatorImpl.setBcrIdQueries(mockBcrIdQueries);

        mockBiospecimenIdWsQueries = mockery.mock(BiospecimenIdWsQueries.class);
        mockValidationWebServiceQueries = mockery.mock(ValidationWebServiceQueries.class);
        wsBarcodeAndUUIDValidatorImpl = new QcLiveBarcodeAndUUIDValidatorImpl();
        wsBarcodeAndUUIDValidatorImpl.setValidationWebServiceQueries(mockValidationWebServiceQueries);

        qcContext = new QcContext();
    }

    @Test
    public void testValidateValid() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = true;
        final int expectedErrorCount = 0;
        final String expectedAppendedErrorMessage = null;

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, TSS_CODE, PATIENT_CODE, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testValidateInvalidProjectName() {

        final boolean projectNameExists = false;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = false;
        final int expectedErrorCount = 1;
        final String expectedAppendedErrorMessage = " has failed validation due to following errors :\n"
                + "The project code '" + PROJECT_NAME + "' in the barcode does not exist in database\n";
        //An error occurred while validating barcode 'TCGA-01-2345-67A-89B-0123-45': The barcode 'TCGA-01-2345-67A-89B-0123-45' in file filename.txt has failed validation due to following errors :\n
        //	The project code 'TCGA' in the barcode does not exist in database
        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, TSS_CODE, PATIENT_CODE, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testValidateInvalidTssCode() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = false;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = false;
        final int expectedErrorCount = 1;
        final String expectedAppendedErrorMessage = " has failed validation due to following errors :\n"
                + "The tissue source site '" + TSS_CODE + "' in the barcode does not exist in database\n";

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, TSS_CODE, PATIENT_CODE, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testValidateInvalidSampleType() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = false;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = false;
        final int expectedErrorCount = 1;
        final String expectedAppendedErrorMessage = " has failed validation due to following errors :\n"
                + "The sample Type '" + SAMPLE_TYPE + "' in the barcode does not exist in database\n";

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, TSS_CODE, PATIENT_CODE, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testValidateInvalidPortionAnalyte() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = false;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = false;
        final int expectedErrorCount = 1;
        final String expectedAppendedErrorMessage = " has failed validation due to following errors :\n"
                + "The portion analyte '" + PORTION_ANALYTE + "' in the barcode does not exist in database\n";

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, TSS_CODE, PATIENT_CODE, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testValidateInvalidBcrCenterId() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = false;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = false;
        final int expectedErrorCount = 1;
        final String expectedAppendedErrorMessage = " has failed validation due to following errors :\n"
                + "The bcr Center '" + BCR_CENTER_ID + "' in the barcode does not exist in database\n";

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, TSS_CODE, PATIENT_CODE, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testValidateInvalidFormatBadBarcode() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = false;
        final int expectedErrorCount = 1;
        final String expectedAppendedErrorMessage = " has an invalid format";

        final String projectName = PROJECT_NAME + "-Invalid-bad-barcode";

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                projectName, TSS_CODE, PATIENT_CODE, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testAlphanumericTissueSourceSite() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = true;
        final int expectedErrorCount = 0;
        final String expectedAppendedErrorMessage = null;

        final String tssCode = "A0";

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, tssCode, PATIENT_CODE, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testValidateThirdBlockAlphaNumeric() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = true;
        final int expectedErrorCount = 0;
        final String expectedAppendedErrorMessage = null;

        final String patientCode = "0A00";

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, TSS_CODE, patientCode, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testValidateSixthBlockAlphaNumeric() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = true;
        final int expectedErrorCount = 0;
        final String expectedAppendedErrorMessage = null;

        final String plateId = "0F00";

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, TSS_CODE, PATIENT_CODE, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, plateId, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testValidateMax4AlphNumeric() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = false;
        final int expectedErrorCount = 1;
        final String expectedAppendedErrorMessage = " has an invalid format";

        final String plateId = "0FF000";

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, TSS_CODE, PATIENT_CODE, SAMPLE_TYPE, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, plateId, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testDifferentSampleType() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final boolean expectedToBeValid = true;
        final int expectedErrorCount = 0;
        final String expectedAppendedErrorMessage = null;

        final String sampleType = "20";

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                PROJECT_NAME, TSS_CODE, PATIENT_CODE, sampleType, SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage);
    }

    @Test
    public void testCheckExistence() {
        checkValidate(true, true, true, true, true,
                PROJECT_NAME, TSS_CODE, PATIENT_CODE, "01", SAMPLE_NUMBER, PORTION_NUMBER, PORTION_ANALYTE, PLATE_ID, BCR_CENTER_ID,
                "nonBcrFile.txt", true, 0, null, true, true);
    }

    @Test
    public void testValidateUuidWithInvalidFormat() {

        final String filename = "filename.txt";
        final String invalid_uuid = "uuid";
        assertFalse(barcodeAndUUIDValidatorImpl.validateUuid(invalid_uuid, qcContext, filename, true));
        assertFalse(barcodeAndUUIDValidatorImpl.validateUuid(invalid_uuid, qcContext, filename, false));
    }

    @Test
    public void testValidateUuidWithValidFormatNeedNotExistInDb() {

        final String filename = "filename.txt";
        final String valid_uuid = "2dad642f-95ac-4008-989e-b5630757c88b";
        assertTrue(barcodeAndUUIDValidatorImpl.validateUuid(valid_uuid, qcContext, filename, false));
    }

    @Test
    public void testValidateUuidWithValidFormatMustExistInDbButDoesNot() {

        final String filename = "filename.txt";
        final String valid_uuid = "2dad642f-95ac-4008-989e-b5630757c88b";
        mockery.checking(new Expectations() {{
            one(mockBcrIdQueries).uuidExists(valid_uuid);
            will(returnValue(false));
        }});
        assertFalse(barcodeAndUUIDValidatorImpl.validateUuid(valid_uuid, qcContext, filename, true));
    }

    @Test
    public void testValidateUuidWithValidFormatMustAndDoesExistInDb() {

        final String filename = "filename.txt";
        final String valid_uuid = "2dad642f-95ac-4008-989e-b5630757c88b";
        mockery.checking(new Expectations() {{
            one(mockBcrIdQueries).uuidExists(valid_uuid);
            will(returnValue(true));
        }});
        assertTrue(barcodeAndUUIDValidatorImpl.validateUuid(valid_uuid, qcContext, filename, true));
    }

    @Test
    public void testIsBarcodeExistsSoundcheck() {
        wsBarcodeAndUUIDValidatorImpl.setBiospecimenIdWsQueries(mockBiospecimenIdWsQueries);
        wsBarcodeAndUUIDValidatorImpl.setBcrIdQueries(null);
        final String validBarcode = "TCGA-02-0001";
        mockery.checking(new Expectations() {{
            one(mockBiospecimenIdWsQueries).exists(validBarcode);
            will(returnValue(true));
            never(mockBcrIdQueries).exists(validBarcode);
        }});
        assertTrue(wsBarcodeAndUUIDValidatorImpl.isBarcodeExists(validBarcode));
    }

    @Test
    public void testIsBarcodeExistsQcLive() {
        wsBarcodeAndUUIDValidatorImpl.setBiospecimenIdWsQueries(null);
        wsBarcodeAndUUIDValidatorImpl.setBcrIdQueries(mockBcrIdQueries);
        final String validBarcode = "TCGA-02-0001";
        mockery.checking(new Expectations() {{
            one(mockBcrIdQueries).exists(validBarcode);
            will(returnValue(0));
            never(mockBiospecimenIdWsQueries).exists(validBarcode);
        }});
        assertTrue(wsBarcodeAndUUIDValidatorImpl.isBarcodeExists(validBarcode));
    }

    @Test
    public void testIsBarcodeExistsNoQueries() {
        wsBarcodeAndUUIDValidatorImpl.setBiospecimenIdWsQueries(null);
        wsBarcodeAndUUIDValidatorImpl.setBcrIdQueries(null);
        final String validBarcode = "TCGA-02-0001";
        mockery.checking(new Expectations() {{
            never(mockBcrIdQueries).exists(validBarcode);
            never(mockBiospecimenIdWsQueries).exists(validBarcode);
        }});
        assertTrue(wsBarcodeAndUUIDValidatorImpl.isBarcodeExists(validBarcode));
    }

    @Test
    public void testBatchValidateReportIndividualResultsNoBarcodes() {

        final List<String> barcodes = new LinkedList<String>();
        final String filename = "testFile.txt";
        final boolean mustExist = true;
        final Map<String, Boolean> barcodeValidityMap = barcodeAndUUIDValidatorImpl.batchValidateReportIndividualResults(barcodes, qcContext, filename, mustExist);

        assertEquals(0, qcContext.getErrorCount());
        assertNotNull(barcodeValidityMap);
        assertEquals(0, barcodeValidityMap.size());
        assertFalse(barcodeValidityMap.containsValue(new Boolean(false)));
    }

    @Test
    public void testBatchValidateReportIndividualResultsInvalidBarcodeFormatAndCodes() {

        final String barcode = "invalidBarcode";

        final List<String> barcodes = new LinkedList<String>();
        barcodes.add(barcode);

        final String filename = "testFile.txt";
        final boolean mustExist = true;
        final Map<String, Boolean> barcodeValidityMap = barcodeAndUUIDValidatorImpl.batchValidateReportIndividualResults(barcodes, qcContext, filename, mustExist);

        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating barcode '" + barcode
                + "': The Aliquot barcode '" + barcode + "' in file testFile.txt has an invalid format",
                qcContext.getErrors().get(0));
        assertNotNull(barcodeValidityMap);
        assertEquals(1, barcodeValidityMap.size());
        assertTrue(barcodeValidityMap.containsKey(barcode));
        assertEquals(new Boolean(false), barcodeValidityMap.get(barcode));
    }

    @Test
    public void testBatchValidateReportIndividualResultsNonBcrSubmittedBarcode() throws WebServiceException {

        final String nonBcrSubmittedBarcode = "TCGA-06-0939-01A-89D-0080-08";
        final String bcrSubmittedBarcode = "TCGA-07-0939-01A-89D-0080-08";

        final List<String> barcodes = new LinkedList<String>();
        barcodes.add(nonBcrSubmittedBarcode);
        barcodes.add(bcrSubmittedBarcode);

        final List<String> bcrSubmittedBarcodes = new LinkedList<String>();
        bcrSubmittedBarcodes.add(bcrSubmittedBarcode);

        final String filename = "testFile.txt";
        final boolean mustExist = true;

        mockery.checking(new Expectations() {{

            exactly(2).of(mockCodeTableQueries).projectNameExists("TCGA");
            will(returnValue(true));

            one(mockCodeTableQueries).tssCodeExists("06");
            will(returnValue(true));

            one(mockCodeTableQueries).tssCodeExists("07");
            will(returnValue(true));

            exactly(2).of(mockCodeTableQueries).sampleTypeExists("01");
            will(returnValue(true));

            exactly(2).of(mockCodeTableQueries).portionAnalyteExists("D");
            will(returnValue(true));

            exactly(2).of(mockCodeTableQueries).bcrCenterIdExists("08");
            will(returnValue(true));

            one(mockBcrIdQueries).exists(nonBcrSubmittedBarcode);
            will(returnValue(-1));

            one(mockBcrIdQueries).exists(bcrSubmittedBarcode);
            will(returnValue(123));
        }});

        final Map<String, Boolean> barcodeValidityMap = barcodeAndUUIDValidatorImpl.batchValidateReportIndividualResults(barcodes, qcContext, filename, mustExist);

        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating barcode '" + nonBcrSubmittedBarcode
                + "': Barcode '" + nonBcrSubmittedBarcode + "' has not been submitted by the BCR yet, so data for it cannot be accepted",
                qcContext.getErrors().get(0));
        assertNotNull(barcodeValidityMap);
        assertEquals(2, barcodeValidityMap.size());
        assertTrue(barcodeValidityMap.containsKey(nonBcrSubmittedBarcode));
        assertTrue(barcodeValidityMap.containsKey(bcrSubmittedBarcode));
        assertEquals(new Boolean(false), barcodeValidityMap.get(nonBcrSubmittedBarcode));
        assertEquals(new Boolean(true), barcodeValidityMap.get(bcrSubmittedBarcode));
    }

    @Test
    public void testBatchValidateReportIndividualResultsNonBcrSubmittedBarcodeSoundcheck() throws WebServiceException {

        final String nonBcrSubmittedBarcode = "TCGA-06-0939-01A-89D-0080-08";
        final String bcrSubmittedBarcode = "TCGA-07-0939-01A-89D-0080-08";

        final List<String> barcodes = new LinkedList<String>();
        barcodes.add(nonBcrSubmittedBarcode);
        barcodes.add(bcrSubmittedBarcode);

        final List<String> nonBcrSubmittedBarcodes = new LinkedList<String>();
        nonBcrSubmittedBarcodes.add(nonBcrSubmittedBarcode);

        final String filename = "testFile.txt";
        final boolean mustExist = true;

        // Soundcheck setup
        barcodeAndUUIDValidatorImpl.setCodeTableQueries(null);
        barcodeAndUUIDValidatorImpl.setBcrIdQueries(null);
        barcodeAndUUIDValidatorImpl.setBiospecimenIdWsQueries(mockBiospecimenIdWsQueries);

        mockery.checking(new Expectations() {{
            one(mockBiospecimenIdWsQueries).exists(barcodes);
            will(returnValue(nonBcrSubmittedBarcodes));
        }});

        final Map<String, Boolean> barcodeValidityMap = barcodeAndUUIDValidatorImpl.batchValidateReportIndividualResults(barcodes, qcContext, filename, mustExist);

        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating barcode '" + nonBcrSubmittedBarcode
                + "': Barcode '" + nonBcrSubmittedBarcode + "' has not been submitted by the BCR yet, so data for it cannot be accepted",
                qcContext.getErrors().get(0));
        assertNotNull(barcodeValidityMap);
        assertEquals(2, barcodeValidityMap.size());
        assertTrue(barcodeValidityMap.containsKey(nonBcrSubmittedBarcode));
        assertTrue(barcodeValidityMap.containsKey(bcrSubmittedBarcode));
        assertEquals(new Boolean(false), barcodeValidityMap.get(nonBcrSubmittedBarcode));
        assertEquals(new Boolean(true), barcodeValidityMap.get(bcrSubmittedBarcode));
    }

    @Test
    public void testBatchValidateReportIndividualResultsWebServiceExceptionSoundcheck() throws WebServiceException {

        final String nonBcrSubmittedBarcode = "TCGA-06-0939-01A-89D-0080-08";
        final String bcrSubmittedBarcode = "TCGA-07-0939-01A-89D-0080-08";

        final List<String> barcodes = new LinkedList<String>();
        barcodes.add(nonBcrSubmittedBarcode);
        barcodes.add(bcrSubmittedBarcode);

        final List<String> nonBcrSubmittedBarcodes = new LinkedList<String>();
        nonBcrSubmittedBarcodes.add(nonBcrSubmittedBarcode);

        final String filename = "testFile.txt";
        final boolean mustExist = true;

        // Soundcheck setup
        barcodeAndUUIDValidatorImpl.setCodeTableQueries(null);
        barcodeAndUUIDValidatorImpl.setBcrIdQueries(null);
        barcodeAndUUIDValidatorImpl.setBiospecimenIdWsQueries(mockBiospecimenIdWsQueries);

        mockery.checking(new Expectations() {{
            one(mockBiospecimenIdWsQueries).exists(barcodes);
            will(throwException(new WebServiceException("WebServiceException occurred")));
        }});

        final Map<String, Boolean> barcodeValidityMap = barcodeAndUUIDValidatorImpl.batchValidateReportIndividualResults(barcodes, qcContext, filename, mustExist);

        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating barcode '': WebServiceException occurred", qcContext.getErrors().get(0));
        assertNotNull(barcodeValidityMap);
        assertEquals(2, barcodeValidityMap.size());
        assertTrue(barcodeValidityMap.containsKey(nonBcrSubmittedBarcode));
        assertTrue(barcodeValidityMap.containsKey(bcrSubmittedBarcode));
        assertEquals(new Boolean(false), barcodeValidityMap.get(nonBcrSubmittedBarcode));
        assertEquals(new Boolean(false), barcodeValidityMap.get(bcrSubmittedBarcode));
    }

    @Test
    public void testBatchValidateReportIndividualResultsDontNeedToExistSoundcheck() throws WebServiceException {

        final String nonBcrSubmittedBarcode = "TCGA-06-0939-01A-89D-0080-08";
        final String bcrSubmittedBarcode = "TCGA-07-0939-01A-89D-0080-08";

        final List<String> barcodes = new LinkedList<String>();
        barcodes.add(nonBcrSubmittedBarcode);
        barcodes.add(bcrSubmittedBarcode);

        final List<String> nonBcrSubmittedBarcodes = new LinkedList<String>();
        nonBcrSubmittedBarcodes.add(nonBcrSubmittedBarcode);

        final String filename = "testFile.txt";
        final boolean mustExist = false;

        // Soundcheck setup
        barcodeAndUUIDValidatorImpl.setCodeTableQueries(null);
        barcodeAndUUIDValidatorImpl.setBcrIdQueries(null);
        barcodeAndUUIDValidatorImpl.setBiospecimenIdWsQueries(mockBiospecimenIdWsQueries);

        final Map<String, Boolean> barcodeValidityMap = barcodeAndUUIDValidatorImpl.batchValidateReportIndividualResults(barcodes, qcContext, filename, mustExist);

        assertEquals(0, qcContext.getErrorCount());
        assertNotNull(barcodeValidityMap);
        assertEquals(2, barcodeValidityMap.size());
        assertTrue(barcodeValidityMap.containsKey(nonBcrSubmittedBarcode));
        assertTrue(barcodeValidityMap.containsKey(bcrSubmittedBarcode));
        assertEquals(new Boolean(true), barcodeValidityMap.get(nonBcrSubmittedBarcode));
        assertEquals(new Boolean(true), barcodeValidityMap.get(bcrSubmittedBarcode));
    }

    @Test
    public void testBatchValidateReportIndividualResultsDontNeedToExist() throws WebServiceException {

        final String nonBcrSubmittedBarcode = "TCGA-06-0939-01A-89D-0080-08";
        final String bcrSubmittedBarcode = "TCGA-07-0939-01A-89D-0080-08";

        final List<String> barcodes = new LinkedList<String>();
        barcodes.add(nonBcrSubmittedBarcode);
        barcodes.add(bcrSubmittedBarcode);

        final List<String> nonBcrSubmittedBarcodes = new LinkedList<String>();
        nonBcrSubmittedBarcodes.add(nonBcrSubmittedBarcode);

        final String filename = "testFile.txt";
        final boolean mustExist = false;

        mockery.checking(new Expectations() {{

            exactly(2).of(mockCodeTableQueries).projectNameExists("TCGA");
            will(returnValue(true));

            one(mockCodeTableQueries).tssCodeExists("06");
            will(returnValue(true));

            one(mockCodeTableQueries).tssCodeExists("07");
            will(returnValue(true));

            exactly(2).of(mockCodeTableQueries).sampleTypeExists("01");
            will(returnValue(true));

            exactly(2).of(mockCodeTableQueries).portionAnalyteExists("D");
            will(returnValue(true));

            exactly(2).of(mockCodeTableQueries).bcrCenterIdExists("08");
            will(returnValue(true));

            exactly(2).of(mockBcrIdQueries).exists(nonBcrSubmittedBarcode);
            will(returnValue(123));
        }});

        final Map<String, Boolean> barcodeValidityMap = barcodeAndUUIDValidatorImpl.batchValidateReportIndividualResults(barcodes, qcContext, filename, mustExist);

        assertEquals(0, qcContext.getErrorCount());
        assertNotNull(barcodeValidityMap);
        assertEquals(2, barcodeValidityMap.size());
        assertTrue(barcodeValidityMap.containsKey(nonBcrSubmittedBarcode));
        assertTrue(barcodeValidityMap.containsKey(bcrSubmittedBarcode));
        assertEquals(new Boolean(true), barcodeValidityMap.get(nonBcrSubmittedBarcode));
        assertEquals(new Boolean(true), barcodeValidityMap.get(bcrSubmittedBarcode));
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsUUIDsNull() {

        final Map<String, Boolean> result = wsBarcodeAndUUIDValidatorImpl.batchValidateUUIDsReportIndividualResults(null, qcContext, null, false);

        assertNotNull(result);
        assertEquals(0, result.size());
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsUUIDsThrowsWebServiceException() throws WebServiceException {

        wsBarcodeAndUUIDValidatorImpl.setValidationWebServiceQueries(mockValidationWebServiceQueries);

        final String uuid1 = VALID_UUID;
        final List<String> uuids = Arrays.asList(uuid1);
        final String exceptionMessage = "fail!";

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).validateUUIDs(with(uuids));
            will(throwException(new WebServiceException(exceptionMessage)));
        }});

        final Map<String, Boolean> result = wsBarcodeAndUUIDValidatorImpl.batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, true);
        checkResult(result, uuid1, true);

        assertEquals(qcContext.getErrors().toString(), 1, qcContext.getErrorCount());

        final String firstError = qcContext.getErrors().get(0);
        assertTrue(firstError.contains(exceptionMessage));
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsUUIDsValidationResultsNull() throws WebServiceException {

        wsBarcodeAndUUIDValidatorImpl.setValidationWebServiceQueries(mockValidationWebServiceQueries);

        final String uuid1 = VALID_UUID;
        final List<String> uuids = Arrays.asList(uuid1);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).validateUUIDs(with(uuids));
            will(returnValue(null));
        }});

        final Map<String, Boolean> result = wsBarcodeAndUUIDValidatorImpl.batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, true);

        checkResult(result, uuid1, true);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(1, qcContext.getWarningCount());
        assertEquals("A warning occurred while validating uuid(s) '[" + uuid1 + "]': Couldn't validate UUIDs", qcContext.getWarnings().get(0));
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsUUIDsValidationResultsEmpty() throws WebServiceException {

        wsBarcodeAndUUIDValidatorImpl.setValidationWebServiceQueries(mockValidationWebServiceQueries);

        final String uuid1 = VALID_UUID;
        final List<String> uuids = Arrays.asList(uuid1);

        final ValidationResults validationResults = new ValidationResults();

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).validateUUIDs(with(uuids));
            will(returnValue(validationResults));
        }});

        final Map<String, Boolean> result = wsBarcodeAndUUIDValidatorImpl.batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, true);

        checkResult(result, uuid1, true);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(1, qcContext.getWarningCount());
        assertEquals("A warning occurred while validating uuid(s) '[" + uuid1 + "]': Couldn't validate UUIDs", qcContext.getWarnings().get(0));
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsUUIDsValidationResultNull() throws WebServiceException {

        wsBarcodeAndUUIDValidatorImpl.setValidationWebServiceQueries(mockValidationWebServiceQueries);

        final String uuid1 = VALID_UUID;
        final List<String> uuids = Arrays.asList(uuid1);

        final List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        validationResultList.add(null);

        final ValidationResults validationResults = new ValidationResults();
        validationResults.setValidationResult(validationResultList);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).validateUUIDs(with(uuids));
            will(returnValue(validationResults));
        }});

        final Map<String, Boolean> result = wsBarcodeAndUUIDValidatorImpl.batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, true);

        checkResult(result, uuid1, true);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(1, qcContext.getWarningCount());
        assertEquals("A warning occurred while validating uuid(s) '[" + uuid1 + "]': Couldn't validate UUIDs", qcContext.getWarnings().get(0));
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsUUIDsMustNotExistNoErrors() throws WebServiceException {

        wsBarcodeAndUUIDValidatorImpl.setValidationWebServiceQueries(mockValidationWebServiceQueries);

        final String uuid1 = "69de087d-e31d-4ff5-a760-6be8da96b6e2";
        final List<String> uuids = Arrays.asList(uuid1);
        final ValidationResults validationResults = makeValidationResults(uuid1, false, null);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).validateUUIDs(with(uuids));
            will(returnValue(validationResults));
        }});

        final Map<String, Boolean> result = wsBarcodeAndUUIDValidatorImpl.batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, false);

        checkResult(result, uuid1, true);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsUUIDsMustExistNoErrors() throws WebServiceException {

        wsBarcodeAndUUIDValidatorImpl.setValidationWebServiceQueries(mockValidationWebServiceQueries);

        final String uuid1 = VALID_UUID;
        final List<String> uuids = Arrays.asList(uuid1);
        final ValidationResults validationResults = makeValidationResults(uuid1, true, null);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).validateUUIDs(with(uuids));
            will(returnValue(validationResults));
        }});

        final Map<String, Boolean> result = wsBarcodeAndUUIDValidatorImpl.batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, true);

        checkResult(result, uuid1, true);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsUUIDsMustExistButDoesnt() throws WebServiceException {

        wsBarcodeAndUUIDValidatorImpl.setValidationWebServiceQueries(mockValidationWebServiceQueries);

        final String uuid1 = VALID_UUID;
        final List<String> uuids = Arrays.asList(uuid1);
        final ValidationResults validationResults = makeValidationResults(uuid1, false, null);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).validateUUIDs(with(uuids));
            will(returnValue(validationResults));
        }});

        final Map<String, Boolean> result = wsBarcodeAndUUIDValidatorImpl.batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, true);

        checkResult(result, uuid1, true);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsWithErrors() throws WebServiceException {

        wsBarcodeAndUUIDValidatorImpl.setValidationWebServiceQueries(mockValidationWebServiceQueries);

        final String uuid1 = VALID_UUID;
        final List<String> uuids = Arrays.asList(uuid1);
        final String filename = "file.txt";
        final String errorMessage = "Not a valid UUID";
        final ValidationResults validationResults = makeValidationResults(uuid1, false, errorMessage);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).validateUUIDs(with(uuids));
            will(returnValue(validationResults));
        }});

        final Map<String, Boolean> result = wsBarcodeAndUUIDValidatorImpl.batchValidateUUIDsReportIndividualResults(uuids, qcContext, filename, true);

        checkResult(result, uuid1, true);
        assertEquals(1, qcContext.getErrorCount());

        final String firstErrorMessage = new StringBuilder("An error occurred while validating uuid '")
                .append(uuid1).append("': ").append(errorMessage).append(" in file '").append(filename).append("'").toString();
        assertEquals(firstErrorMessage, qcContext.getErrors().get(0));
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsWithErrorsNoFilename() throws WebServiceException {

        wsBarcodeAndUUIDValidatorImpl.setValidationWebServiceQueries(mockValidationWebServiceQueries);

        final String uuid1 = VALID_UUID;
        final List<String> uuids = Arrays.asList(uuid1);
        final String errorMessage = "Not a valid UUID";
        final ValidationResults validationResults = makeValidationResults(uuid1, false, errorMessage);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).validateUUIDs(with(uuids));
            will(returnValue(validationResults));
        }});

        final Map<String, Boolean> result = wsBarcodeAndUUIDValidatorImpl.batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, true);

        checkResult(result, uuid1, true);
        assertEquals(1, qcContext.getErrorCount());

        final String firstErrorMessage = new StringBuilder("An error occurred while validating uuid '")
                .append(uuid1).append("': ").append(errorMessage).toString();
        assertEquals(firstErrorMessage, qcContext.getErrors().get(0));
    }

    /**
     * Return a {@link ValidationResults} that contains a result for the given UUID with its existence in the DB.
     * If <code>errorMessage</code> is not null then it will also add a validation error.
     * to the result.
     *
     * @param uuid the UUID
     * @param uuidExistsInDB the UUID's existence in the DB
     * @param errorMessage the error message to add if not null
     * @return a {@link ValidationResults} that contains a result for the given UUID with its existence in the DB.
     */
    private ValidationResults makeValidationResults(final String uuid,
                                                            boolean uuidExistsInDB,
                                                            final String errorMessage) {

        final ValidationResult validationResult = new ValidationResult();
        validationResult.setValidationObject(uuid);
        validationResult.setExistsInDB(uuidExistsInDB);

        if(errorMessage != null) {

            final ValidationErrors.ValidationError validationError = new ValidationErrors.ValidationError(uuid, errorMessage);

            final List<ValidationErrors.ValidationError> validationErrorList = new ArrayList<ValidationErrors.ValidationError>();
            validationErrorList.add(validationError);

            final ValidationErrors<String> validationErrors = new ValidationErrors<String>();
            validationErrors.setValidationErrors(validationErrorList);

            validationResult.setValidationError(validationErrors);
        }

        final List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        validationResultList.add(validationResult);

        final ValidationResults validationResults = new ValidationResults();
        validationResults.setValidationResult(validationResultList);

        return validationResults;
    }

    /**
     * Assert that the given {@link Map} only contains the given UUID with the given validity.
     *
     * @param result the {@link Map} to check
     * @param uuid the UUID
     * @param isValid the UUID validity
     */
    private void checkResult(final Map<String, Boolean> result, final String uuid, final Boolean isValid) {

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.keySet().contains(uuid));
        assertEquals(isValid, result.get(uuid));
    }

    /**
     * Test a barcode according to the given expectations
     *
     * @param projectNameExists <code>true</code> if the project name is expected to exist, <code>false</code> otherwise
     * @param tssCodeExists <code>true</code> if the tissue source code is expected to exist, <code>false</code> otherwise
     * @param sampleTypeExists <code>true</code> if the sample type is expected to exist, <code>false</code> otherwise
     * @param portionAnalyteExists <code>true</code> if the portion analyte is expected to exist, <code>false</code> otherwise
     * @param bcrCenterIdExists <code>true</code> if the bcr center id is expected to exist, <code>false</code> otherwise
     * @param projectName the project name
     * @param tssCode the tissue source code
     * @param patientCode the patient code
     * @param sampleType the sample type
     * @param sampleNumber the sample number
     * @param portionNumber the portion number
     * @param portionAnalyte the portion analyte
     * @param plateId the plate id
     * @param bcrCenterId the bcr center id
     * @param filename the filename the barcode comes from
     * @param expectedToBeValid <code>true</code> if the barcode is expected to be valid, <code>false</code> otherwise
     * @param expectedErrorCount the expected error count
     * @param expectedAppendedErrorMessage the <code>String</code> to append to the expected error message
     */
    private void checkValidate(final boolean projectNameExists,
                               final boolean tssCodeExists,
                               final boolean sampleTypeExists,
                               final boolean portionAnalyteExists,
                               final boolean bcrCenterIdExists,
                               final String projectName,
                               final String tssCode,
                               final String patientCode,
                               final String sampleType,
                               final String sampleNumber,
                               final String portionNumber,
                               final String portionAnalyte,
                               final String plateId,
                               final String bcrCenterId,
                               final String filename,
                               final boolean expectedToBeValid,
                               final int expectedErrorCount,
                               final String expectedAppendedErrorMessage) {
        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                projectName, tssCode, patientCode, sampleType, sampleNumber, portionNumber, portionAnalyte, plateId, bcrCenterId,
                filename, expectedToBeValid, expectedErrorCount, expectedAppendedErrorMessage, false, false);
    }

    // same as above, but with additional parameters for whether barcode existence should be checked
    private void checkValidate(final boolean projectNameExists,
                               final boolean tssCodeExists,
                               final boolean sampleTypeExists,
                               final boolean portionAnalyteExists,
                               final boolean bcrCenterIdExists,
                               final String projectName,
                               final String tssCode,
                               final String patientCode,
                               final String sampleType,
                               final String sampleNumber,
                               final String portionNumber,
                               final String portionAnalyte,
                               final String plateId,
                               final String bcrCenterId,
                               final String filename,
                               final boolean expectedToBeValid,
                               final int expectedErrorCount,
                               final String expectedAppendedErrorMessage,
                               final boolean checkExistence,
                               final boolean barcodeExists) {

        final String barcode = CommonBarcodeAndUUIDValidatorImplFastTest.createBarcode(
                projectName,
                tssCode,
                patientCode,
                sampleType,
                sampleNumber,
                portionNumber,
                portionAnalyte,
                plateId,
                bcrCenterId
        );

        CommonBarcodeAndUUIDValidatorImplFastTest.createExpectations(mockery, mockCodeTableQueries,
                projectName, tssCode, sampleType, portionAnalyte, bcrCenterId,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        if (checkExistence) {
            mockery.checking(new Expectations() {{
                one(mockBcrIdQueries).exists(barcode);
                will(returnValue(barcodeExists ? 123 : -1));
            }});
        }

        final boolean result = barcodeAndUUIDValidatorImpl.validate(barcode, qcContext, filename, checkExistence);

        assertEquals("Unexpected result", expectedToBeValid, result);
        assertEquals("Unexpected number of errors " + qcContext.getErrors().toString(), expectedErrorCount, qcContext.getErrorCount());

        if(expectedErrorCount > 0) {

            String expectedErrorMessage = null;
            if(expectedAppendedErrorMessage != null) {

                expectedErrorMessage = "An error occurred while validating barcode '" + barcode + "': The Aliquot barcode '" + barcode + "'";

                if(filename != null) {
                    expectedErrorMessage +=  " in file " + filename;
                }

                expectedErrorMessage += expectedAppendedErrorMessage;
            }
            
            assertEquals("Unexpected error message", expectedErrorMessage, qcContext.getErrors().get(0)); // Only expecting 1 error message
        }
    }

    @Test
    public void validateUUID() {

        final String filename = "filename.txt";
        final String valid_uuid = "2dad642f-95ac-4008-989e-b5630757c88b";
        mockery.checking(new Expectations() {{
            one(mockBcrIdQueries).uuidExists(valid_uuid);
            will(returnValue(true));
        }});
        assertTrue(barcodeAndUUIDValidatorImpl.validateBarcodeOrUuid(valid_uuid, qcContext, filename, true));
    }

    @Test
    public void validateBarcode() {

        final String filename = "filename.txt";
        final String validBarcode = "TCGA-06-0881-10A-01W-0421-09";
        mockery.checking(new Expectations() {{
            one(mockCodeTableQueries).projectNameExists("TCGA");
            will(returnValue( true));
            one(mockCodeTableQueries).bcrCenterIdExists("09");
            will(returnValue( true));
            one(mockCodeTableQueries).tssCodeExists("06");
            will(returnValue( true));
            one(mockCodeTableQueries).sampleTypeExists("10");
            will(returnValue( true));
            one(mockCodeTableQueries).portionAnalyteExists("W");
            will(returnValue( true));

            one(mockBcrIdQueries).exists(validBarcode);
            will(returnValue(1));
        }});
        assertTrue(barcodeAndUUIDValidatorImpl.validateBarcodeOrUuid(validBarcode, qcContext, filename, true));
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeWebServiceException() throws WebServiceException {

        final List<String[]> sampleUuidAndSampleTcgaBarcodes = makeSampleUuidAndSampleTcgaBarcodes();

        final String exceptionMessage = "WebServiceException occurred";

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes);
            will(throwException(new WebServiceException(exceptionMessage)));
        }});

        final boolean result = wsBarcodeAndUUIDValidatorImpl.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes, qcContext);
        assertFalse(result);
        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);
        assertEquals("An error occurred while validating uuid(s) and barcode(s) '[SampleUUID:uuid1,SampleTCGABarcode:barcode1],[SampleUUID:uuid2,SampleTCGABarcode:barcode2]': " + exceptionMessage, actualErrorMessage);
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeNoValidationResults() throws WebServiceException {

        final List<String[]> sampleUuidAndSampleTcgaBarcodes = makeSampleUuidAndSampleTcgaBarcodes();

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes);
            will(returnValue(null));
        }});

        final boolean result = wsBarcodeAndUUIDValidatorImpl.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes, qcContext);
        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(1, qcContext.getWarningCount());

        final String actualWarningMessage = qcContext.getWarnings().get(0);
        assertNotNull(actualWarningMessage);

        final String expectedWarningMessage = "A warning occurred while validating uuid(s) and barcode(s) " +
                "'[SampleUUID:uuid1,SampleTCGABarcode:barcode1],[SampleUUID:uuid2,SampleTCGABarcode:barcode2]': " +
                "Couldn't validate SampleUUID(s) and SampleTCGABarcode(s)";
        assertEquals(expectedWarningMessage, actualWarningMessage);
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeNoValidationResult() throws WebServiceException {

        final List<String[]> sampleUuidAndSampleTcgaBarcodes = makeSampleUuidAndSampleTcgaBarcodes();

        final List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        final ValidationResults validationResults = new ValidationResults();
        validationResults.addValidationResult(validationResultList);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes);
            will(returnValue(validationResults));
        }});

        final boolean result = wsBarcodeAndUUIDValidatorImpl.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes, qcContext);
        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(1, qcContext.getWarningCount());

        final String actualWarningMessage = qcContext.getWarnings().get(0);
        assertNotNull(actualWarningMessage);

        final String expectedWarningMessage = "A warning occurred while validating uuid(s) and barcode(s) " +
                "'[SampleUUID:uuid1,SampleTCGABarcode:barcode1],[SampleUUID:uuid2,SampleTCGABarcode:barcode2]': " +
                "Couldn't validate SampleUUID(s) and SampleTCGABarcode(s)";
        assertEquals(expectedWarningMessage, actualWarningMessage);
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeValidationResultNull() throws WebServiceException {

        final List<String[]> sampleUuidAndSampleTcgaBarcodes = makeSampleUuidAndSampleTcgaBarcodes();

        final List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        validationResultList.add(null);
        final ValidationResults validationResults = new ValidationResults();
        validationResults.addValidationResult(validationResultList);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes);
            will(returnValue(validationResults));
        }});

        final boolean result = wsBarcodeAndUUIDValidatorImpl.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes, qcContext);
        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(1, qcContext.getWarningCount());

        final String actualWarningMessage = qcContext.getWarnings().get(0);
        assertNotNull(actualWarningMessage);

        final String expectedWarningMessage = "A warning occurred while validating uuid(s) and barcode(s) " +
                "'[SampleUUID:uuid1,SampleTCGABarcode:barcode1],[SampleUUID:uuid2,SampleTCGABarcode:barcode2]': " +
                "Couldn't validate SampleUUID(s) and SampleTCGABarcode(s)";
        assertEquals(expectedWarningMessage, actualWarningMessage);
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeNoError() throws WebServiceException {

        final List<String[]> sampleUuidAndSampleTcgaBarcodes = makeSampleUuidAndSampleTcgaBarcodes();

        final ValidationResult validationResult = new ValidationResult();
        final List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        validationResultList.add(validationResult);
        final ValidationResults validationResults = new ValidationResults();
        validationResults.addValidationResult(validationResultList);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes);
            will(returnValue(validationResults));
        }});

        final boolean result = wsBarcodeAndUUIDValidatorImpl.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes, qcContext);
        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeValidationErrorsNotNullNoValidationError() throws WebServiceException {

        final List<String[]> sampleUuidAndSampleTcgaBarcodes = makeSampleUuidAndSampleTcgaBarcodes();

        final ValidationErrors validationErrors = new ValidationErrors();
        final ValidationResult validationResult = new ValidationResult();
        validationResult.setValidationError(validationErrors);
        final List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        validationResultList.add(validationResult);
        final ValidationResults validationResults = new ValidationResults();
        validationResults.addValidationResult(validationResultList);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes);
            will(returnValue(validationResults));
        }});

        final boolean result = wsBarcodeAndUUIDValidatorImpl.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes, qcContext);
        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeValidationErrorsNotNullWithValidationError() throws WebServiceException {

        final List<String[]> sampleUuidAndSampleTcgaBarcodes = makeSampleUuidAndSampleTcgaBarcodes();

        final ValidationErrors.ValidationError validationError = new ValidationErrors.ValidationError();
        validationError.setInvalidValue("uuid1");
        validationError.setErrorMessage("SampleUUID not an aliquot UUID.");
        final List<ValidationErrors.ValidationError> validationErrorList = new ArrayList<ValidationErrors.ValidationError>();
        validationErrorList.add(validationError);
        final ValidationErrors validationErrors = new ValidationErrors();
        validationErrors.setValidationErrors(validationErrorList);
        final ValidationResult validationResult = new ValidationResult();
        validationResult.setValidationError(validationErrors);
        final List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        validationResultList.add(validationResult);
        final ValidationResults validationResults = new ValidationResults();
        validationResults.addValidationResult(validationResultList);

        mockery.checking(new Expectations() {{
            one(mockValidationWebServiceQueries).batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes);
            will(returnValue(validationResults));
        }});

        final boolean result = wsBarcodeAndUUIDValidatorImpl.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodes, qcContext);
        assertFalse(result);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());

        final String errorMessage = qcContext.getErrors().get(0);
        assertNotNull(errorMessage);
        assertEquals("An error occurred while validating uuid(s) and barcode(s) 'uuid1': SampleUUID not an aliquot UUID.", errorMessage);
    }

    /**
     * Returns a list of SampleUUID / SampleTCGABarcode pairs for testing
     *
     * @return a list of SampleUUID / SampleTCGABarcode pairs for testing
     */
    private List<String[]> makeSampleUuidAndSampleTcgaBarcodes() {

        final String[] sampleUuidAndSampleTcgaBarcode1 = {"uuid1", "barcode1"};
        final String[] sampleUuidAndSampleTcgaBarcode2 = {"uuid2", "barcode2"};

        final List<String[]> sampleUuidAndSampleTcgaBarcodes = new ArrayList<String[]>();
        sampleUuidAndSampleTcgaBarcodes.add(sampleUuidAndSampleTcgaBarcode1);
        sampleUuidAndSampleTcgaBarcodes.add(sampleUuidAndSampleTcgaBarcode2);

        return sampleUuidAndSampleTcgaBarcodes;
    }
}
