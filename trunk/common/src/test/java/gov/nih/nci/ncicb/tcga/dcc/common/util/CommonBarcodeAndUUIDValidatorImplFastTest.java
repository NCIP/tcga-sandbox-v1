/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * CommonBarcodeAndUUIDValidatorImpl unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class CommonBarcodeAndUUIDValidatorImplFastTest {

    /**
     * Valid codes for barcode
     */
    private static final String PROJECT_NAME = "TCGA";
    private static final String TSS_CODE = "06";
    private static final String PATIENT_CODE = "2345";
    private static final String SAMPLE_TYPE = "01";
    private static final String SAMPLE_NUMBER = "A";
    private static final String PORTION_NUMBER = "89";
    private static final String PORTION_ANALYTE = "D";
    private static final String PLATE_ID = "0123";
    private static final String BCR_CENTER_ID = "08";


    private final static String VALID_ALIQUOT_BARCODE = "TCGA-06-0939-01A-89D-0080-08";
    private final static String VALID_ANALYTE_BARCODE = "TCGA-06-0939-01A-89D";
    private final static String VALID_ANCILLARY_BARCODE = "TCGA-02-0001-E3124";
    private final static String VALID_PATIENT_BARCODE = "TCGA-06-0939";
    private final static String VALID_PORTION_BARCODE = "TCGA-06-0939-01A-01";
    private final static String VALID_SAMPLE_BARCODE = "TCGA-06-0939-01A";
    private final static String VALID_SLIDE_BARCODE = "TCGA-06-0939-01A-01-TSA"; // The final character matches the portion number (A->1, B->2, C->3)
    private final static String VALID_DRUG_BARCODE = "TCGA-ITCY-BITCY";
    private final static String VALID_RADIATION_BARCODE = "TCGA-SPIDER-IS";
    private final static String VALID_EXAMINATION_BARCODE = "TCGA-A-COOL";
    private final static String VALID_SURGERY_BARCODE = "TCGA-NURSERY-RHYME";
    private final static String VALID_UUID = "69de087d-e31d-4ff5-a760-6be8da96b6e2";
    private final static String VALID_SHIPMENT_PORTION_BARCODE = "TCGA-06-0939-01A-89-1234-20";
    private final String DRUG_BARCODE = "TCGA-A5-A0GJ-D5084";
    private final String RADIATION_BARCODE = "TCGA-A1-A0SO-R4448";
    private final String EXAMINATION_BARCODE = "TCGA-74-6573-E1";
    private final String SURGERY_BARCODE = "TCGA-24-2290-S1";
    private final String DRUG_BARCODE_2 = "TCGA-A1-A0FE-C1";
    private final String DRUG_BARCODE_3 = "TCGA-A2-B03J-H34";
    private final String DRUG_BARCODE_4 = "TCGA-A9-AFGS-I007";
    private final String DRUG_BARCODE_5 = "TCGA-A5-44GJ-T5";

    /**
     * Mixing it up
     */
    private final static String INVALID_ALIQUOT_BARCODE = VALID_UUID;
    private final static String INVALID_ANALYTE_BARCODE = VALID_ALIQUOT_BARCODE;
    private final static String INVALID_ANCILLARY_BARCODE = "TCGA-02-0001-1234";
    private final static String INVALID_PATIENT_BARCODE = VALID_ANALYTE_BARCODE;
    private final static String INVALID_PORTION_BARCODE = VALID_PATIENT_BARCODE;
    private final static String INVALID_SAMPLE_BARCODE = VALID_PORTION_BARCODE;
    private final static String INVALID_SLIDE_BARCODE = "TCGA-06-0939-01A-01-BS9"; // The final character does not match the portion number (9 <> 1)
    private final static String INVALID_DRUG_BARCODE = "CGA-ITCY-BITCY";
    private final static String INVALID_RADIATION_BARCODE = "GA-SPIDER-IS";
    private final static String INVALID_EXAMINATION_BARCODE = "A-A-COOL";
    private final static String INVALID_SURGERY_BARCODE = "NURSERY-RHYME";
    private final static String INVALID_UUID_BARCODE = "1-2-3-4-5";
    private static final String VALID_PARTICIPANT_BARCODE = VALID_PATIENT_BARCODE;

    private Mockery mockery = new JUnit4Mockery();
    private CommonBarcodeAndUUIDValidatorImpl commonBarcodeAndUUIDValidatorImpl;
    private CodeTableQueries mockCodeTableQueries;
    private ShippedBiospecimenQueries mockShippedQueries;
    private UUIDDAO mockUUIDDAO;

    @Before
    public void setUp() {
        mockShippedQueries = mockery.mock(ShippedBiospecimenQueries.class);
        mockUUIDDAO = mockery.mock(UUIDDAO.class);
        commonBarcodeAndUUIDValidatorImpl = new CommonBarcodeAndUUIDValidatorImpl();
        commonBarcodeAndUUIDValidatorImpl.shippedBiospecimenQueries = mockShippedQueries;
        mockCodeTableQueries = mockery.mock(CodeTableQueries.class);
        commonBarcodeAndUUIDValidatorImpl.setCodeTableQueries(mockCodeTableQueries);
    }

    @Test
    public void testValidateBarcodeValid() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final String expectedAppendedErrorMessage = null;
        final String appendToBarcode = null;

        checkValidateBarcode(
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                filename,
                expectedAppendedErrorMessage,
                appendToBarcode);
    }

    @Test
    public void testValidateBarcodeInvalidProjectName() {

        final boolean projectNameExists = false;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final String expectedAppendedErrorMessage = " has failed validation due to following errors :\n"
                + "The project code '" + PROJECT_NAME + "' in the barcode does not exist in database\n";
        final String appendToBarcode = null;

        checkValidateBarcode(
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                filename,
                expectedAppendedErrorMessage,
                appendToBarcode);
    }

    @Test
    public void testValidateBarcodeInvalidTssCode() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = false;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final String expectedAppendedErrorMessage = " has failed validation due to following errors :\n"
                + "The tissue source site '" + TSS_CODE + "' in the barcode does not exist in database\n";
        final String appendToBarcode = null;

        checkValidateBarcode(
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                filename,
                expectedAppendedErrorMessage,
                appendToBarcode);
    }

    @Test
    public void testValidateBarcodeInvalidSampleType() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = false;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final String expectedAppendedErrorMessage = " has failed validation due to following errors :\n"
                + "The sample Type '" + SAMPLE_TYPE + "' in the barcode does not exist in database\n";
        final String appendToBarcode = null;

        checkValidateBarcode(
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                filename,
                expectedAppendedErrorMessage,
                appendToBarcode);
    }

    @Test
    public void testValidateBarcodeInvalidPortionAnalyte() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = false;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final String expectedAppendedErrorMessage = " has failed validation due to following errors :\n"
                + "The portion analyte '" + PORTION_ANALYTE + "' in the barcode does not exist in database\n";
        final String appendToBarcode = null;

        checkValidateBarcode(
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                filename,
                expectedAppendedErrorMessage,
                appendToBarcode);
    }

    @Test
    public void testValidateBarcodeInvalidBcrCenterId() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = false;

        final String filename = "filename.txt";
        final String expectedAppendedErrorMessage = " has failed validation due to following errors :\n"
                + "The bcr Center '" + BCR_CENTER_ID + "' in the barcode does not exist in database\n";
        final String appendToBarcode = null;

        checkValidateBarcode(
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                filename,
                expectedAppendedErrorMessage,
                appendToBarcode);
    }

    @Test
    public void testValidateBarcodeInvalidBarcodeWhitespace() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final String expectedAppendedErrorMessage = " has leading or trailing whitespace";
        final String appendToBarcode = "   ";

        checkValidateBarcodeWithoutCodeTable(
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                filename,
                expectedAppendedErrorMessage,
                appendToBarcode);
    }

    @Test
    public void testValidateBarcodeInvalidFormat() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final String filename = "filename.txt";
        final String expectedAppendedErrorMessage = " has an invalid format";
        final String appendToBarcode = "-invalid-format";

        checkValidateBarcodeWithoutCodeTable(
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                filename,
                expectedAppendedErrorMessage,
                appendToBarcode);
    }

    @Test
    public void testValidateValid() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final boolean expectedToBeValid = true;

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                expectedToBeValid);
    }

    @Test
    public void testValidateInvalid() {

        final boolean projectNameExists = false;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        final boolean expectedToBeValid = false;

        checkValidate(projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists,
                expectedToBeValid);
    }

    @Test
    public void testGetName() {
        assertEquals("Unexpected name", "barcode validation", commonBarcodeAndUUIDValidatorImpl.getName());
    }

    @Test
    public void testValidateAnyBarcodeValid() {
        assertTrue("barcode is invalid: " + VALID_ALIQUOT_BARCODE,
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat(VALID_ALIQUOT_BARCODE));
        assertTrue("barcode is invalid: " + VALID_ANALYTE_BARCODE,
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat(VALID_ANALYTE_BARCODE));
        assertTrue("barcode is invalid: " + VALID_PATIENT_BARCODE,
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat(VALID_PATIENT_BARCODE));
        assertTrue("barcode is invalid: " + VALID_PORTION_BARCODE,
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat(VALID_PORTION_BARCODE));
        assertTrue("barcode is invalid: " + VALID_SAMPLE_BARCODE,
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat(VALID_SAMPLE_BARCODE));
        assertTrue("barcode is invalid: " + VALID_SLIDE_BARCODE,
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat(VALID_SLIDE_BARCODE));
        assertTrue("barcode is invalid: " + VALID_PARTICIPANT_BARCODE,
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat(VALID_PARTICIPANT_BARCODE));
    }

    @Test
    public void testValidateAnyBarcodeInvalid() {
        assertFalse("barcode is valid: " + "TCGA",
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat("TCGA"));
        assertFalse("barcode is valid: " + "1234",
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat("1234"));
        assertFalse("barcode is valid: " + "a1b2c3",
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat("a1b2c3"));
        assertFalse("barcode is valid: " + "TCGA/123456789",
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat("TCGA/123456789"));
        assertFalse("barcode is valid: " + "TCGA-IS-NOT-A-BARCODE",
                commonBarcodeAndUUIDValidatorImpl.validateAnyBarcodeFormat("TCGA-IS-NOT-A-BARCODE"));
    }

    @Test
    public void testValidateAliquotBarcodeValid() {
        assertTrue("Aliquot barcode is invalid: " + VALID_ALIQUOT_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateAliquotBarcodeFormat(VALID_ALIQUOT_BARCODE));
    }

    @Test
    public void testValidateAliquotBarcodeInvalid() {
        assertFalse("Aliquot barcode is valid: " + INVALID_ALIQUOT_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateAliquotBarcodeFormat(INVALID_ALIQUOT_BARCODE));
    }

    @Test
    public void testValidateAncillaryBarcodeValid() {
        assertTrue("Ancillary barcode is invalid: " + VALID_ANCILLARY_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateAncillaryBarcodeFormat(VALID_ANCILLARY_BARCODE));
    }

    @Test
    public void testValidateAncillaryBarcodeInvalid() {
        assertFalse("Ancillary barcode is valid: " + INVALID_ANCILLARY_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateAncillaryBarcodeFormat(INVALID_ANCILLARY_BARCODE));
    }

    @Test
    public void testValidateAnalyteBarcodeValid() {
        assertTrue("Analyte barcode is invalid: " + VALID_ANALYTE_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateAnalyteBarcodeFormat(VALID_ANALYTE_BARCODE));
    }

    @Test
    public void testValidateAnalyteBarcodeInvalid() {
        assertFalse("Analyte barcode is valid: " + INVALID_ANALYTE_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateAnalyteBarcodeFormat(INVALID_ANALYTE_BARCODE));
    }

    @Test
    public void testValidatePatientBarcodeValid() {
        assertTrue("Patient barcode is invalid: " + VALID_PATIENT_BARCODE, commonBarcodeAndUUIDValidatorImpl.validatePatientBarcodeFormat(VALID_PATIENT_BARCODE));
    }

    @Test
    public void testValidatePatientBarcodeInvalid() {
        assertFalse("Patient barcode is valid: " + INVALID_PATIENT_BARCODE, commonBarcodeAndUUIDValidatorImpl.validatePatientBarcodeFormat(INVALID_PATIENT_BARCODE));
    }

    @Test
    public void testValidatePortionBarcodeValid() {
        assertTrue("Portion barcode is invalid: " + VALID_PORTION_BARCODE, commonBarcodeAndUUIDValidatorImpl.validatePortionBarcodeFormat(VALID_PORTION_BARCODE));
    }

    @Test
    public void testValidatePortionBarcodeInvalid() {
        assertFalse("Portion barcode is valid: " + INVALID_PORTION_BARCODE, commonBarcodeAndUUIDValidatorImpl.validatePortionBarcodeFormat(INVALID_PORTION_BARCODE));
    }

    @Test
    public void testValidateSampleBarcodeValid() {
        assertTrue("Sample barcode is invalid: " + VALID_SAMPLE_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateSampleBarcodeFormat(VALID_SAMPLE_BARCODE));
    }

    @Test
    public void testValidateSampleBarcodeInvalid() {
        assertFalse("Sample barcode is valid: " + INVALID_SAMPLE_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateSampleBarcodeFormat(INVALID_SAMPLE_BARCODE));
    }

    @Test
    public void testValidateSlideBarcodeValid() {
        assertTrue("Slide barcode is invalid: " + VALID_SLIDE_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateSlideBarcodeFormat(VALID_SLIDE_BARCODE));
    }

    @Test
    public void testValidateSlideBarcodeInvalid() {
        assertFalse("Slide barcode is valid: " + INVALID_SLIDE_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateSlideBarcodeFormat(INVALID_SLIDE_BARCODE));
    }

    @Test
    public void testValidateUUIDInvalidFormat() {
        assertFalse("UUID is valid: " + INVALID_UUID_BARCODE, commonBarcodeAndUUIDValidatorImpl.validateUUIDFormat(INVALID_UUID_BARCODE));
    }

    @Test
    public void testValidateUUIDInvalidWhenNull() {
        assertFalse(commonBarcodeAndUUIDValidatorImpl.validateUUIDFormat(null));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesValidAliquot() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, PORTION_ANALYTE, BCR_CENTER_ID,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        assertNull(commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_ALIQUOT_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidAliquot() {

        final boolean projectNameExists = false;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, PORTION_ANALYTE, BCR_CENTER_ID,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        final String expectedErrorMessage = "The Aliquot barcode 'TCGA-06-0939-01A-89D-0080-08' has failed validation due to following errors :\n" +
                "The project code 'TCGA' in the barcode does not exist in database\n";

        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_ALIQUOT_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidAliquotIsNull() {

        final String expectedErrorMessage = "The Aliquot barcode 'null' is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(null, null, CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidAliquotIsEmpty() {

        final String expectedErrorMessage = "The Aliquot barcode '' is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes("", null, CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesValidAnalyte() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, PORTION_ANALYTE, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        assertNull(commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_ANALYTE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.ANALYTE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidAnalyteIsNull() {

        final String expectedErrorMessage = "The Analyte barcode 'null' in file testFile.txt is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(null, "testFile.txt", CommonBarcodeAndUUIDValidatorImpl.ANALYTE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidAnalyteIsEmpty() {

        final String expectedErrorMessage = "The Analyte barcode '' in file testFile.txt is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes("", "testFile.txt", CommonBarcodeAndUUIDValidatorImpl.ANALYTE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidAnalyte() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = false;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, PORTION_ANALYTE, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        final String expectedErrorMessage = "The Analyte barcode 'TCGA-06-0939-01A-89D' in file testFile.txt has failed validation due to following errors :\n" +
                "The tissue source site '06' in the barcode does not exist in database\n";

        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_ANALYTE_BARCODE, "testFile.txt", CommonBarcodeAndUUIDValidatorImpl.ANALYTE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesValidPatient() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, null, null, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        assertNull(commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_PATIENT_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.PATIENT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidPatient() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = false;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, null, null, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        final String expectedErrorMessage = "The Patient barcode 'TCGA-06-0939' in file testFile.txt has failed validation due to following errors :\n" +
                "The tissue source site '06' in the barcode does not exist in database\n";

        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_PATIENT_BARCODE, "testFile.txt", CommonBarcodeAndUUIDValidatorImpl.PATIENT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidPatientIsNull() {

        final String expectedErrorMessage = "The Patient barcode 'null' in file testFile.txt is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(null, "testFile.txt", CommonBarcodeAndUUIDValidatorImpl.PATIENT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidPatientIsEmpty() {

        final String expectedErrorMessage = "The Patient barcode '' in file testFile.txt is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes("", "testFile.txt", CommonBarcodeAndUUIDValidatorImpl.PATIENT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesValidPortion() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, null, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        assertNull(commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_PORTION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.PORTION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidPortion() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = false;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, null, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        final String expectedErrorMessage = "The Portion barcode 'TCGA-06-0939-01A-01' has failed validation due to following errors :\n" +
                "The sample Type '01' in the barcode does not exist in database\n";

        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_PORTION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.PORTION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidPortionIsNull() {

        final String expectedErrorMessage = "The Portion barcode 'null' is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(null, null, CommonBarcodeAndUUIDValidatorImpl.PORTION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidPortionIsEmpty() {

        final String expectedErrorMessage = "The Portion barcode '' is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes("", null, CommonBarcodeAndUUIDValidatorImpl.PORTION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesValidSample() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, null, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        assertNull(commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_SAMPLE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SAMPLE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidSample() {

        final boolean projectNameExists = false;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, null, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        final String expectedErrorMessage = "The Sample barcode 'TCGA-06-0939-01A' in file testFile.txt has failed validation due to following errors :\n" +
                "The project code 'TCGA' in the barcode does not exist in database\n";

        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_SAMPLE_BARCODE, "testFile.txt", CommonBarcodeAndUUIDValidatorImpl.SAMPLE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidSampleIsNull() {

        final String expectedErrorMessage = "The Sample barcode 'null' in file testFile.txt is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(null, "testFile.txt", CommonBarcodeAndUUIDValidatorImpl.SAMPLE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidSampleIsEmpty() {

        final String expectedErrorMessage = "The Sample barcode '' in file testFile.txt is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes("", "testFile.txt", CommonBarcodeAndUUIDValidatorImpl.SAMPLE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesValidSlide() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, null, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        assertNull(commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_SLIDE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidSlideMissingCode() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = false;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, null, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        final String expectedErrorMessage = "The Slide barcode 'TCGA-06-0939-01A-01-TSA' has failed validation due to following errors :\n" +
                "The tissue source site '06' in the barcode does not exist in database\n";

        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_SLIDE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidSlideIsNull() {

        final String expectedErrorMessage = "The Slide barcode 'null' is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(null, null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidSlideIsEmpty() {

        final String expectedErrorMessage = "The Slide barcode '' is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes("", null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatAndCodesInvalidSlideNumberMismatch() {

        final boolean projectNameExists = true;
        final boolean tssCodeExists = true;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, null, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        final String expectedErrorMessage = "The Slide barcode 'TCGA-06-0939-01A-01-BS9' has an invalid format " +
                "(the slide number '9' does not match the portion number '01')";

        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(INVALID_SLIDE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test

    public void testValidateBarcodeFormatAndCodesInvalidSlideNumberMismatchAndMissingCode() {

        final boolean projectNameExists = false;
        final boolean tssCodeExists = false;
        final boolean sampleTypeExists = true;
        final boolean portionAnalyteExists = true;
        final boolean bcrCenterIdExists = true;

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, null, null,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        final String expectedErrorMessage = "The Slide barcode '" + INVALID_SLIDE_BARCODE + "' has failed validation due to following errors :\n" +
                "The project code 'TCGA' in the barcode does not exist in database\n" +
                "The tissue source site '06' in the barcode does not exist in database\n" +
                "the slide number '9' does not match the portion number '01'\n";

        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(INVALID_SLIDE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatValidAliquot() {

        final String expectedErrorMessage = null;
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_ALIQUOT_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidAliquotIsNull() {

        final String expectedErrorMessage = "The Aliquot barcode 'null' is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(null, null, CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidAliquotIsEmpty() {

        final String expectedErrorMessage = "The Aliquot barcode '' is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("", null, CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidAliquotLeadingWhitespace() {

        final String expectedErrorMessage = "The Aliquot barcode '   " + VALID_ALIQUOT_BARCODE + "' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("   " + VALID_ALIQUOT_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidAliquotTrailingWhitespace() {

        final String expectedErrorMessage = "The Aliquot barcode '" + VALID_ALIQUOT_BARCODE + "   ' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_ALIQUOT_BARCODE + "   ", null, CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidAliquotFormat() {

        final String expectedErrorMessage = "The Aliquot barcode '" + INVALID_ALIQUOT_BARCODE + "' has an invalid format";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(INVALID_ALIQUOT_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatUnsupportedBarcodeType() {

        final String expectedErrorMessage = "The Unsupported Barcode Type barcode '" + VALID_UUID + "' is not a supported barcode type";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_UUID, null, "Unsupported Barcode Type"));
    }

    @Test
    public void testValidateBarcodeFormatValidAnalyte() {

        final String expectedErrorMessage = null;
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_ANALYTE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.ANALYTE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidAnalyteLeadingWhitespace() {

        final String expectedErrorMessage = "The Analyte barcode '   " + VALID_ANALYTE_BARCODE + "' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("   " + VALID_ANALYTE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.ANALYTE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidAnalyteIsNull() {

        final String expectedErrorMessage = "The Analyte barcode 'null' is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(null, null, CommonBarcodeAndUUIDValidatorImpl.ANALYTE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidAnalyteIsEmpty() {

        final String expectedErrorMessage = "The Analyte barcode '' is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("", null, CommonBarcodeAndUUIDValidatorImpl.ANALYTE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidAnalyteTrailingWhitespace() {

        final String expectedErrorMessage = "The Analyte barcode '" + VALID_ANALYTE_BARCODE + "   ' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_ANALYTE_BARCODE + "   ", null, CommonBarcodeAndUUIDValidatorImpl.ANALYTE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidAnalyteFormat() {

        final String expectedErrorMessage = "The Analyte barcode '" + VALID_UUID + "' has an invalid format";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_UUID, null, CommonBarcodeAndUUIDValidatorImpl.ANALYTE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatValidPatient() {

        final String expectedErrorMessage = null;
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_PATIENT_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.PATIENT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidPatientIsNull() {

        final String expectedErrorMessage = "The Patient barcode 'null' is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(null, null, CommonBarcodeAndUUIDValidatorImpl.PATIENT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidPatientIsEmpty() {

        final String expectedErrorMessage = "The Patient barcode '' is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("", null, CommonBarcodeAndUUIDValidatorImpl.PATIENT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidPatientLeadingWhitespace() {

        final String expectedErrorMessage = "The Patient barcode '   " + VALID_PATIENT_BARCODE + "' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("   " + VALID_PATIENT_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.PATIENT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidPatientTrailingWhitespace() {

        final String expectedErrorMessage = "The Patient barcode '" + VALID_PATIENT_BARCODE + "   ' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_PATIENT_BARCODE + "   ", null, CommonBarcodeAndUUIDValidatorImpl.PATIENT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidPatientFormat() {

        final String expectedErrorMessage = "The Patient barcode '" + VALID_UUID + "' has an invalid format";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_UUID, null, CommonBarcodeAndUUIDValidatorImpl.PATIENT_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatValidPortion() {

        final String expectedErrorMessage = null;
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_PORTION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.PORTION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidPortionIsNull() {

        final String expectedErrorMessage = "The Portion barcode 'null' is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(null, null, CommonBarcodeAndUUIDValidatorImpl.PORTION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidPortionIsEmpty() {

        final String expectedErrorMessage = "The Portion barcode '' is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("", null, CommonBarcodeAndUUIDValidatorImpl.PORTION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidPortionLeadingWhitespace() {

        final String expectedErrorMessage = "The Portion barcode '   " + VALID_PORTION_BARCODE + "' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("   " + VALID_PORTION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.PORTION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidPortionTrailingWhitespace() {

        final String expectedErrorMessage = "The Portion barcode '" + VALID_PORTION_BARCODE + "   ' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_PORTION_BARCODE + "   ", null, CommonBarcodeAndUUIDValidatorImpl.PORTION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidPortionFormat() {

        final String expectedErrorMessage = "The Portion barcode '" + VALID_UUID + "' has an invalid format";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_UUID, null, CommonBarcodeAndUUIDValidatorImpl.PORTION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatValidSample() {

        final String expectedErrorMessage = null;
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_SAMPLE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SAMPLE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSampleLeadingWhitespace() {

        final String expectedErrorMessage = "The Sample barcode '   " + VALID_SAMPLE_BARCODE + "' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("   " + VALID_SAMPLE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SAMPLE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSampleIsNull() {

        final String expectedErrorMessage = "The Sample barcode 'null' is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(null, null, CommonBarcodeAndUUIDValidatorImpl.SAMPLE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSampleIsEmpty() {

        final String expectedErrorMessage = "The Sample barcode '' is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("", null, CommonBarcodeAndUUIDValidatorImpl.SAMPLE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSampleTrailingWhitespace() {

        final String expectedErrorMessage = "The Sample barcode '" + VALID_SAMPLE_BARCODE + "   ' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_SAMPLE_BARCODE + "   ", null, CommonBarcodeAndUUIDValidatorImpl.SAMPLE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSampleFormat() {

        final String expectedErrorMessage = "The Sample barcode '" + VALID_UUID + "' has an invalid format";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_UUID, null, CommonBarcodeAndUUIDValidatorImpl.SAMPLE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatValidSlide() {

        final String expectedErrorMessage = null;
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_SLIDE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSlideIsNull() {

        final String expectedErrorMessage = "The Slide barcode 'null' is null";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(null, null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSlideIsEmpty() {

        final String expectedErrorMessage = "The Slide barcode '' is empty";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("", null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSlideLeadingWhitespace() {

        final String expectedErrorMessage = "The Slide barcode '   " + VALID_SLIDE_BARCODE + "' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat("   " + VALID_SLIDE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSlideTrailingWhitespace() {

        final String expectedErrorMessage = "The Slide barcode '" + VALID_SLIDE_BARCODE + "   ' has leading or trailing whitespace";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_SLIDE_BARCODE + "   ", null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSlideFormat() {

        final String expectedErrorMessage = "The Slide barcode '" + INVALID_SLIDE_BARCODE + "' has an invalid format (the slide number '9' does not match the portion number '01')";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(INVALID_SLIDE_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatValidDrug() {

        final String expectedErrorMessage = null;
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_DRUG_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.DRUG_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidDrug() {

        final String expectedErrorMessage = "The Drug barcode '" + INVALID_DRUG_BARCODE + "' has an invalid format";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(INVALID_DRUG_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.DRUG_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatValidRadiation() {

        final String expectedErrorMessage = null;
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_RADIATION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.RADIATION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidRadiation() {

        final String expectedErrorMessage = "The Radiation barcode '" + INVALID_RADIATION_BARCODE + "' has an invalid format";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(INVALID_RADIATION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.RADIATION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatValidExamination() {

        final String expectedErrorMessage = null;
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_EXAMINATION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.EXAMINATION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidExamination() {

        final String expectedErrorMessage = "The Examination barcode '" + INVALID_EXAMINATION_BARCODE + "' has an invalid format";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(INVALID_EXAMINATION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.EXAMINATION_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatValidSurgery() {

        final String expectedErrorMessage = null;
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_SURGERY_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SURGERY_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeFormatInvalidSurgery() {

        final String expectedErrorMessage = "The Surgery barcode '" + INVALID_SURGERY_BARCODE + "' has an invalid format";
        assertEquals(expectedErrorMessage, commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(INVALID_SURGERY_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SURGERY_ITEM_TYPE_NAME));
    }

    @Test
    public void testValidateBarcodeValidShipmentPortion() {
        // if valid, will return null (no error message)
        final String output = commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_SHIPMENT_PORTION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SHIPMENT_PORTION_ITEM_TYPE_NAME);
        assertNull(output, output);
    }

    @Test
    public void testValidateBarcodeValidShippedPortion() {
        final String output = commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormat(VALID_SHIPMENT_PORTION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SHIPPED_PORTION_ITEM_TYPE_NAME);
        assertNull(output, output);
    }

    @Test
    public void testValidateShipmentPortionWithCodes() {
        mockery.checking(new Expectations() {{
            one(mockCodeTableQueries).projectNameExists("TCGA");
            will(returnValue(true));
            one(mockCodeTableQueries).tssCodeExists("06");
            will(returnValue(true));
            one(mockCodeTableQueries).sampleTypeExists("01");
            will(returnValue(true));
        }});
        final String output = commonBarcodeAndUUIDValidatorImpl.validateBarcodeFormatAndCodes(VALID_SHIPMENT_PORTION_BARCODE, null, CommonBarcodeAndUUIDValidatorImpl.SHIPMENT_PORTION_ITEM_TYPE_NAME);
        assertNull(output, output);
    }

    @Test
    public void testGetAliquotBarcodeExist() {

        final String input = VALID_ALIQUOT_BARCODE;
        final String output = commonBarcodeAndUUIDValidatorImpl.getAliquotBarcode(input);

        assertEquals(VALID_ALIQUOT_BARCODE, output);
    }

    @Test
    public void testGetAliquotBarcodeContained() {

        final String input = "blabla" + VALID_ALIQUOT_BARCODE + "blabla";
        final String output = commonBarcodeAndUUIDValidatorImpl.getAliquotBarcode(input);

        assertEquals(VALID_ALIQUOT_BARCODE, output);
    }

    @Test
    public void testGetAliquotBarcodeDoesNotExist() {

        final String input = "notAValidAliquotBarcode";
        final String output = commonBarcodeAndUUIDValidatorImpl.getAliquotBarcode(input);

        assertNull(output);
    }

    @Test
    public void testGetPatientBarcodeExist() {

        final String input = VALID_PATIENT_BARCODE;
        final String output = commonBarcodeAndUUIDValidatorImpl.getPatientBarcode(input);

        assertEquals(VALID_PATIENT_BARCODE, output);
    }

    @Test
    public void testGetPatientBarcodeContained() {

        final String input = "blabla" + VALID_PATIENT_BARCODE + "blabla";
        final String output = commonBarcodeAndUUIDValidatorImpl.getPatientBarcode(input);

        assertEquals(VALID_PATIENT_BARCODE, output);
    }

    @Test
    public void testGetPatientBarcodeDoesNotExist() {

        final String input = "notAValidPatientBarcode";
        final String output = commonBarcodeAndUUIDValidatorImpl.getPatientBarcode(input);

        assertNull(output);
    }

    @Test
    public void testGetUUIDExist() {

        final String input = VALID_UUID;
        final String output = commonBarcodeAndUUIDValidatorImpl.getUUID(input);

        assertEquals(VALID_UUID, output);
    }

    @Test
    public void testGetUUIDContained() {

        final String input = "blabla" + VALID_UUID + "blabla";
        final String output = commonBarcodeAndUUIDValidatorImpl.getUUID(input);

        assertEquals(VALID_UUID, output);
    }

    @Test
    public void testGetUUIDDoesNotExist() {

        final String input = "notAValidUUID";
        final String output = commonBarcodeAndUUIDValidatorImpl.getUUID(input);

        assertNull(output);
    }

    @Test
    public void testGetItemTypeGood() throws Exception {
        assertEquals("Aliquot", commonBarcodeAndUUIDValidatorImpl.getItemType(VALID_ALIQUOT_BARCODE));
        assertEquals("Analyte", commonBarcodeAndUUIDValidatorImpl.getItemType(VALID_ANALYTE_BARCODE));
        assertEquals("Participant", commonBarcodeAndUUIDValidatorImpl.getItemType(VALID_PATIENT_BARCODE));
        assertEquals("Portion", commonBarcodeAndUUIDValidatorImpl.getItemType(VALID_PORTION_BARCODE));
        assertEquals("Sample", commonBarcodeAndUUIDValidatorImpl.getItemType(VALID_SAMPLE_BARCODE));
        assertEquals("Slide", commonBarcodeAndUUIDValidatorImpl.getItemType(VALID_SLIDE_BARCODE));
        assertEquals("Drug", commonBarcodeAndUUIDValidatorImpl.getItemType(DRUG_BARCODE));
        assertEquals("Radiation", commonBarcodeAndUUIDValidatorImpl.getItemType(RADIATION_BARCODE));
        assertEquals("Examination", commonBarcodeAndUUIDValidatorImpl.getItemType(EXAMINATION_BARCODE));
        assertEquals("Surgery", commonBarcodeAndUUIDValidatorImpl.getItemType(SURGERY_BARCODE));
        assertEquals("Shipped Portion", commonBarcodeAndUUIDValidatorImpl.getItemType(VALID_SHIPMENT_PORTION_BARCODE));
        assertEquals("Drug", commonBarcodeAndUUIDValidatorImpl.getItemType(DRUG_BARCODE_2));
        assertEquals("Drug", commonBarcodeAndUUIDValidatorImpl.getItemType(DRUG_BARCODE_3));
        assertEquals("Drug", commonBarcodeAndUUIDValidatorImpl.getItemType(DRUG_BARCODE_4));
        assertEquals("Drug", commonBarcodeAndUUIDValidatorImpl.getItemType(DRUG_BARCODE_5));
    }

    @Test
    public void testGetItemTypeBad() throws Exception {
        assertNull(commonBarcodeAndUUIDValidatorImpl.getItemType("gopher"));
        assertNull(commonBarcodeAndUUIDValidatorImpl.getItemType("TCGA"));
        assertNull(commonBarcodeAndUUIDValidatorImpl.getItemType("TCGA-12-1234-123456789"));
        assertNull(commonBarcodeAndUUIDValidatorImpl.getItemType("TCGA-12-1234-A9"));
        assertNull(commonBarcodeAndUUIDValidatorImpl.getItemType("TCGA-12-1234-BC9"));
    }

    @Test
    public void testIsAliquotUUID() {
        mockery.checking(new Expectations() {
            {
                one(mockShippedQueries).getUUIDLevel("69de087d-e31d-4ff5-a760-6be8da96b6e2");
                will(returnValue("Aliquot"));
            }
        });
        assertTrue(commonBarcodeAndUUIDValidatorImpl.isAliquotUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2"));
    }

    @Test
    public void testIsAliquotUUIDNotAliquot() {
        mockery.checking(new Expectations() {
            {
                one(mockShippedQueries).getUUIDLevel("69de087d-e31d-4ff5-a760-6be8da96b6e2");
                will(returnValue("Shipped Portion"));
            }
        });
        assertFalse(commonBarcodeAndUUIDValidatorImpl.isAliquotUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsAliquotUUIDEmptyInput() {
        commonBarcodeAndUUIDValidatorImpl.isAliquotUUID("");
    }

    @Test
    public void testValidateUUIDMetadata() {
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2");
        metadata.setAnalyteCode(this.PORTION_ANALYTE);
        metadata.setParticipantCode(PATIENT_CODE);
        metadata.setPlateId(PLATE_ID);
        metadata.setPortionCode(PORTION_NUMBER);
        metadata.setProjectCode(PROJECT_NAME);
        metadata.setReceivingCenterId(BCR_CENTER_ID);
        metadata.setSampleCode(SAMPLE_TYPE);
        metadata.setTssCode(TSS_CODE);
        metadata.setVial(SAMPLE_NUMBER);
        mockery.checking(new Expectations() {
            {
                one(mockShippedQueries).retrieveUUIDMetadata("69de087d-e31d-4ff5-a760-6be8da96b6e2");
                will(returnValue(metadata));
            }
        });
        assertTrue(commonBarcodeAndUUIDValidatorImpl.validateUUIDMetadata(metadata));
    }

    @Test
    public void testValidateUUIDInvalidMetadata() {
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2");
        metadata.setAnalyteCode(this.PORTION_ANALYTE);
        metadata.setParticipantCode(PATIENT_CODE);
        metadata.setPlateId(PLATE_ID);
        metadata.setPortionCode(PORTION_NUMBER);
        metadata.setProjectCode(PROJECT_NAME);
        metadata.setReceivingCenterId(BCR_CENTER_ID);
        metadata.setSampleCode(SAMPLE_TYPE);
        metadata.setTssCode(TSS_CODE);
        metadata.setVial(SAMPLE_NUMBER);
        mockery.checking(new Expectations() {
            {
                one(mockShippedQueries).retrieveUUIDMetadata("69de087d-e31d-4ff5-a760-6be8da96b6e2");
                will(returnValue(metadata));
            }
        });

        final MetaDataBean metadata2 = new MetaDataBean();
        metadata2.combineMetadata(metadata);
        metadata2.setVial("");

        assertFalse(commonBarcodeAndUUIDValidatorImpl.validateUUIDMetadata(metadata2));
    }

    @Test
    public void testGetMetadata() throws CommonBarcodeAndUUIDValidator.CommonBarcodeAndUUIDValidatorException {
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2");
        metadata.setAnalyteCode(this.PORTION_ANALYTE);
        metadata.setParticipantCode(PATIENT_CODE);
        metadata.setPlateId(PLATE_ID);
        metadata.setPortionCode(PORTION_NUMBER);
        metadata.setProjectCode(PROJECT_NAME);
        metadata.setReceivingCenterId(BCR_CENTER_ID);
        metadata.setSampleCode(SAMPLE_TYPE);
        metadata.setTssCode(TSS_CODE);
        metadata.setVial(SAMPLE_NUMBER);
        mockery.checking(new Expectations() {{
            one(mockShippedQueries).retrieveUUIDMetadata("69de087d-e31d-4ff5-a760-6be8da96b6e2");
            will(returnValue(metadata));
        }});
        final MetaDataBean res = commonBarcodeAndUUIDValidatorImpl.getMetadata("69de087d-e31d-4ff5-a760-6be8da96b6e2");
        assertNotNull(res);
        assertEquals("TCGA-06-2345", res.getPatientBuiltBarcode());
    }

    @Test
    public void testGetMetadataThrowsCommonBarcodeAndUUIDValidatorException() {

        final String uuid = "69de087d-e31d-4ff5-a760-6be8da96b6e2";

        mockery.checking(new Expectations() {{
            one(mockShippedQueries).retrieveUUIDMetadata(uuid);
            will(returnValue(null));
        }});

        try {
            commonBarcodeAndUUIDValidatorImpl.getMetadata(uuid);
            fail("CommonBarcodeAndUUIDValidatorException was not thrown");

        } catch (final CommonBarcodeAndUUIDValidator.CommonBarcodeAndUUIDValidatorException e) {
            assertNotNull(e);
            assertEquals("Could not retrieve metadata for UUID '" + uuid + "'", e.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateUUIDEmptyInput() {
        commonBarcodeAndUUIDValidatorImpl.validateUUIDMetadata(new MetaDataBean());
    }


    @Test
    public void testIsMatchingDiseaseForUUID() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(mockShippedQueries).getDiseaseForUUID("uuid1");
            will(returnValue("GBM"));
        }});
        assertTrue(commonBarcodeAndUUIDValidatorImpl.isMatchingDiseaseForUUID("uuid1", "GBM"));
        assertFalse(commonBarcodeAndUUIDValidatorImpl.isMatchingDiseaseForUUID("uuid1", "OV"));
    }

    @Test
    public void testIsMatchingDiseaseForUUIDNullDisease() {
        mockery.checking(new Expectations() {{
            one(mockShippedQueries).getDiseaseForUUID("something");
            will(returnValue(null));
        }});

        assertFalse(commonBarcodeAndUUIDValidatorImpl.isMatchingDiseaseForUUID("something", "TEST"));
    }

    @Test
    public void testIsMatchingDiseaseForCNTL() {
        mockery.checking(new Expectations() {{
            one(mockShippedQueries).getDiseaseForUUID("hello");
            will(returnValue("CNTL"));
        }});

        assertTrue(commonBarcodeAndUUIDValidatorImpl.isMatchingDiseaseForUUID("hello", "LUSC"));
    }

    @Test
    public void validateUUIDBarcodeMappingTest() {
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2");
        metadata.setAnalyteCode(this.PORTION_ANALYTE);
        metadata.setParticipantCode(PATIENT_CODE);
        metadata.setPlateId(PLATE_ID);
        metadata.setPortionCode(PORTION_NUMBER);
        metadata.setProjectCode(PROJECT_NAME);
        metadata.setReceivingCenterId(BCR_CENTER_ID);
        metadata.setSampleCode(SAMPLE_TYPE);
        metadata.setTssCode(TSS_CODE);
        metadata.setVial(SAMPLE_NUMBER);
        metadata.setAliquot(true);
        mockery.checking(new Expectations() {
            {
                one(mockShippedQueries).retrieveUUIDMetadata("69de087d-e31d-4ff5-a760-6be8da96b6e2");
                will(returnValue(metadata));
            }
        });
        assertTrue(commonBarcodeAndUUIDValidatorImpl.validateUUIDBarcodeMapping("69de087d-e31d-4ff5-a760-6be8da96b6e2", "TCGA-06-2345-01A-89D-0123-08"));
    }

    @Test
    public void validateUUIDBarcodeNotEqualMappingTest() {
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2");
        metadata.setAnalyteCode(this.PORTION_ANALYTE);
        metadata.setParticipantCode(PATIENT_CODE);
        metadata.setPlateId(PLATE_ID);
        metadata.setPortionCode(PORTION_NUMBER);
        metadata.setProjectCode(PROJECT_NAME);
        metadata.setReceivingCenterId(BCR_CENTER_ID);
        metadata.setSampleCode(SAMPLE_TYPE);
        metadata.setTssCode(TSS_CODE);
        metadata.setVial(SAMPLE_NUMBER);
        metadata.setAliquot(true);
        mockery.checking(new Expectations() {
            {
                one(mockShippedQueries).retrieveUUIDMetadata("69de087d-e31d-4ff5-a760-6be8da96b6e2");
                will(returnValue(metadata));
            }
        });
        assertFalse(commonBarcodeAndUUIDValidatorImpl.validateUUIDBarcodeMapping("69de087d-e31d-4ff5-a760-6be8da96b6e2", "TCGA-06-2345-01A-89D-0123"));
    }

    /**
     * Test a barcode according to the given expectations
     *
     * @param projectNameExists            <code>true</code> if the project name is expected to exist, <code>false</code> otherwise
     * @param tssCodeExists                <code>true</code> if the tissue source code is expected to exist, <code>false</code> otherwise
     * @param sampleTypeExists             <code>true</code> if the sample type is expected to exist, <code>false</code> otherwise
     * @param portionAnalyteExists         <code>true</code> if the portion analyte is expected to exist, <code>false</code> otherwise
     * @param bcrCenterIdExists            <code>true</code> if the bcr center id is expected to exist, <code>false</code> otherwise
     * @param filename                     the filename the barcode comes from
     * @param expectedAppendedErrorMessage the expected appended error message
     * @param appendToBarcode              if not <code>null</code>, the <code>String</code> to append to the barcode
     */
    private void checkValidateBarcode(final boolean projectNameExists,
                                      final boolean tssCodeExists,
                                      final boolean sampleTypeExists,
                                      final boolean portionAnalyteExists,
                                      final boolean bcrCenterIdExists,
                                      final String filename,
                                      final String expectedAppendedErrorMessage,
                                      final String appendToBarcode) {

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, PORTION_ANALYTE, BCR_CENTER_ID,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        String barcode = createBarcode(
                PROJECT_NAME,
                TSS_CODE,
                PATIENT_CODE,
                SAMPLE_TYPE,
                SAMPLE_NUMBER,
                PORTION_NUMBER,
                PORTION_ANALYTE,
                PLATE_ID,
                BCR_CENTER_ID
        );

        if (appendToBarcode != null) {
            barcode += appendToBarcode;
        }

        final String result = commonBarcodeAndUUIDValidatorImpl.validateAliquotBarcodeFormatAndCodes(barcode, filename);

        String expectedErrorMessage = null;
        if (expectedAppendedErrorMessage != null) {

            expectedErrorMessage = "The Aliquot barcode '" + barcode + "'";

            if (filename != null) {
                expectedErrorMessage += " in file " + filename;
            }

            expectedErrorMessage += expectedAppendedErrorMessage;
        }

        assertEquals("Unexpected result: " + result, expectedErrorMessage, result);
    }

    /**
     * Test a barcode according to the given expectations
     *
     * @param projectNameExists            <code>true</code> if the project name is expected to exist, <code>false</code> otherwise
     * @param tssCodeExists                <code>true</code> if the tissue source code is expected to exist, <code>false</code> otherwise
     * @param sampleTypeExists             <code>true</code> if the sample type is expected to exist, <code>false</code> otherwise
     * @param portionAnalyteExists         <code>true</code> if the portion analyte is expected to exist, <code>false</code> otherwise
     * @param bcrCenterIdExists            <code>true</code> if the bcr center id is expected to exist, <code>false</code> otherwise
     * @param filename                     the filename the barcode comes from
     * @param expectedAppendedErrorMessage the expected appended error message
     * @param appendToBarcode              if not <code>null</code>, the <code>String</code> to append to the barcode
     */
    private void checkValidateBarcodeWithoutCodeTable(final boolean projectNameExists,
                                                      final boolean tssCodeExists,
                                                      final boolean sampleTypeExists,
                                                      final boolean portionAnalyteExists,
                                                      final boolean bcrCenterIdExists,
                                                      final String filename,
                                                      final String expectedAppendedErrorMessage,
                                                      final String appendToBarcode) {


        String barcode = createBarcode(
                PROJECT_NAME,
                TSS_CODE,
                PATIENT_CODE,
                SAMPLE_TYPE,
                SAMPLE_NUMBER,
                PORTION_NUMBER,
                PORTION_ANALYTE,
                PLATE_ID,
                BCR_CENTER_ID
        );

        if (appendToBarcode != null) {
            barcode += appendToBarcode;
        }

        final String result = commonBarcodeAndUUIDValidatorImpl.validateAliquotBarcodeFormatAndCodes(barcode, filename);

        String expectedErrorMessage = null;
        if (expectedAppendedErrorMessage != null) {

            expectedErrorMessage = "The Aliquot barcode '" + barcode + "'";

            if (filename != null) {
                expectedErrorMessage += " in file " + filename;
            }

            expectedErrorMessage += expectedAppendedErrorMessage;
        }

        assertEquals("Unexpected result: " + result, expectedErrorMessage, result);
    }

    /**
     * Test a barcode according to the given expectations
     *
     * @param projectNameExists    <code>true</code> if the project name is expected to exist, <code>false</code> otherwise
     * @param tssCodeExists        <code>true</code> if the tissue source code is expected to exist, <code>false</code> otherwise
     * @param sampleTypeExists     <code>true</code> if the sample type is expected to exist, <code>false</code> otherwise
     * @param portionAnalyteExists <code>true</code> if the portion analyte is expected to exist, <code>false</code> otherwise
     * @param bcrCenterIdExists    <code>true</code> if the bcr center id is expected to exist, <code>false</code> otherwise
     * @param expectedToBeValid    <code>true</code> if the barcode is expected to be valid, <code>false</code> otherwise
     */
    private void checkValidate(final boolean projectNameExists,
                               final boolean tssCodeExists,
                               final boolean sampleTypeExists,
                               final boolean portionAnalyteExists,
                               final boolean bcrCenterIdExists,
                               final boolean expectedToBeValid) {

        createExpectations(mockery, mockCodeTableQueries,
                PROJECT_NAME, TSS_CODE, SAMPLE_TYPE, PORTION_ANALYTE, BCR_CENTER_ID,
                projectNameExists, tssCodeExists, sampleTypeExists, portionAnalyteExists, bcrCenterIdExists
        );

        String barcode = createBarcode(
                PROJECT_NAME,
                TSS_CODE,
                PATIENT_CODE,
                SAMPLE_TYPE,
                SAMPLE_NUMBER,
                PORTION_NUMBER,
                PORTION_ANALYTE,
                PLATE_ID,
                BCR_CENTER_ID
        );

        assertEquals(expectedToBeValid, commonBarcodeAndUUIDValidatorImpl.validateAliquotFormatAndCodes(barcode));
    }

    /**
     * Create expectations for the running unit test
     *
     * @param mockery              a <code>Mockery</code> instance
     * @param mockCodeTableQueries a mock <code>codeTableQueries</code>
     * @param projectName          the project name
     * @param tssCode              the tissue source code
     * @param sampleType           the sample type
     * @param portionAnalyte       the portion analyte
     * @param bcrCenterId          the bcr center id
     * @param projectNameExists    <code>true</code> if the project name is expected to exist, <code>false</code> otherwise
     * @param tssCodeExists        <code>true</code> if the tissue source code is expected to exist, <code>false</code> otherwise
     * @param sampleTypeExists     <code>true</code> if the sample type is expected to exist, <code>false</code> otherwise
     * @param portionAnalyteExists <code>true</code> if the portion analyte is expected to exist, <code>false</code> otherwise
     * @param bcrCenterIdExists    <code>true</code> if the bcr center id is expected to exist, <code>false</code> otherwise
     */
    public static void createExpectations(final Mockery mockery,
                                          final CodeTableQueries mockCodeTableQueries,
                                          final String projectName,
                                          final String tssCode,
                                          final String sampleType,
                                          final String portionAnalyte,
                                          final String bcrCenterId,
                                          final boolean projectNameExists,
                                          final boolean tssCodeExists,
                                          final boolean sampleTypeExists,
                                          final boolean portionAnalyteExists,
                                          final boolean bcrCenterIdExists) {

        mockery.checking(new Expectations() {{

            if (projectName != null) {
                one(mockCodeTableQueries).projectNameExists(projectName);
                will(returnValue(projectNameExists));
            }

            if (tssCode != null) {
                one(mockCodeTableQueries).tssCodeExists(tssCode);
                will(returnValue(tssCodeExists));
            }

            if (sampleType != null) {
                one(mockCodeTableQueries).sampleTypeExists(sampleType);
                will(returnValue(sampleTypeExists));
            }

            if (portionAnalyte != null) {
                one(mockCodeTableQueries).portionAnalyteExists(portionAnalyte);
                will(returnValue(portionAnalyteExists));
            }

            if (bcrCenterId != null) {
                one(mockCodeTableQueries).bcrCenterIdExists(bcrCenterId);
                will(returnValue(bcrCenterIdExists));
            }
        }});
    }

    /**
     * Create a barcode with the given codes
     *
     * @param projectName    the project name
     * @param tssCode        the tissue source code
     * @param patientCode    the patient code
     * @param sampleType     the sample type
     * @param sampleNumber   the sample number
     * @param portionNumber  the portion number
     * @param portionAnalyte the portion analyte
     * @param plateId        the plate id
     * @param bcrCenterId    the brc center id
     * @return the barcode
     */
    public static String createBarcode(final String projectName,
                                       final String tssCode,
                                       final String patientCode,
                                       final String sampleType,
                                       final String sampleNumber,
                                       final String portionNumber,
                                       final String portionAnalyte,
                                       final String plateId,
                                       final String bcrCenterId) {

        final StringBuilder result = new StringBuilder(projectName).append("-")
                .append(tssCode).append("-")
                .append(patientCode).append("-")
                .append(sampleType).append(sampleNumber).append("-")
                .append(portionNumber).append(portionAnalyte).append("-")
                .append(plateId).append("-")
                .append(bcrCenterId);

        return result.toString();
    }
}
