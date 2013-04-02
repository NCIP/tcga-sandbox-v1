/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * QcLiveBarcodeAndUUIDValidatorRemoteImpl unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QcLiveBarcodeAndUUIDValidatorRemoteImplFastTest {

    private static final String FILE_NAME = "file.txt";


    /**
     * The names of the different barcodes types
     */
    public static String ALIQUOT_ITEM_TYPE_NAME = "Aliquot";
    public static String ANALYTE_ITEM_TYPE_NAME = "Analyte";
    public static String PATIENT_ITEM_TYPE_NAME = "Patient";
    public static String PORTION_ITEM_TYPE_NAME = "Portion";
    public static String SAMPLE_ITEM_TYPE_NAME = "Sample";
    public static String SLIDE_ITEM_TYPE_NAME = "Slide";
    public static String DRUG_ITEM_TYPE_NAME = "Drug";
    public static String RADIATION_ITEM_TYPE_NAME = "Radiation";
    public static String EXAMINATION_ITEM_TYPE_NAME = "Examination";
    public static String SURGERY_ITEM_TYPE_NAME = "Surgery";
    public static String SHIPMENT_PORTION_ITEM_TYPE_NAME = "Shipment Portion";
    public static String SHIPPED_PORTION_ITEM_TYPE_NAME = "Shipped Portion"; // this is a synonym of Shipment Portion
    public static String PARTICIPANT_ITEM_TYPE_NAME = "Participant"; // this is a synonym of Patient

    private static final String INVALID_UUID = "invalid-uuid";
    private static final String INVALID_ALIQUOT_BARCODE = "invalid-aliquot-barcode";
    private static final String INVALID_ANALYTE_BARCODE = "invalid-analyte-barcode";
    private static final String INVALID_PATIENT_BARCODE = "invalid-patient-barcode";
    private static final String INVALID_PORTION_BARCODE = "invalid-portion-barcode";
    private static final String INVALID_SAMPLE_BARCODE = "invalid-sample-barcode";
    private static final String INVALID_SLIDE_BARCODE = "invalid-slide-barcode";
    private static final String INVALID_DRUG_BARCODE = "invalid-drug-barcode";
    private static final String INVALID_RADIATION_BARCODE = "invalid-radiation-barcode";
    private static final String INVALID_EXAMINATION_BARCODE = "invalid-examination-barcode";
    private static final String INVALID_SURGERY_BARCODE = "invalid-surgery-barcode";
    private static final String INVALID_SHIPMENT_PORTION_BARCODE = "invalid-shipment-portion-barcode";
    private static final String INVALID_SHIPPED_PORTION_BARCODE = "invalid-shipped-portion-barcode";
    private static final String INVALID_PARTICIPANT_BARCODE = "invalid-participant-barcode";

    private static final String VALID_UUID = "69de087d-e31d-4ff5-a760-6be8da96b6e2";
    private static final String VALID_ALIQUOT_BARCODE_FORMAT = "TCGA-00-0000-00A-00A-0000-00";
    private static final String VALID_ANALYTE_BARCODE_FORMAT = "TCGA-00-0000-00A-00A";
    private static final String VALID_PATIENT_BARCODE_FORMAT = "TCGA-00-0000";
    private static final String VALID_PORTION_BARCODE_FORMAT = "TCGA-00-0000-00A-00";
    private static final String VALID_SAMPLE_BARCODE_FORMAT = "TCGA-00-0000-00A";
    private static final String VALID_SLIDE_BARCODE_FORMAT = "TCGA-00-0000-00A-00-TS0";
    private static final String VALID_DRUG_BARCODE_FORMAT = "TCGA-CAN-BE-ANYTHING";
    private static final String VALID_RADIATION_BARCODE_FORMAT = "TCGA-CAN-BE-ANYTHING";
    private static final String VALID_EXAMINATION_BARCODE_FORMAT = "TCGA-CAN-BE-ANYTHING";
    private static final String VALID_SURGERY_BARCODE_FORMAT = "TCGA-CAN-BE-ANYTHING";
    private static final String VALID_SHIPMENT_PORTION_BARCODE_FORMAT = "TCGA-00-0000-00A-00-0000-00";
    private static final String VALID_SHIPPED_PORTION_BARCODE_FORMAT = "TCGA-00-0000-00A-00-0000-00";
    private static final String VALID_PARTICIPANT_BARCODE_FORMAT = "TCGA-00-0000";

    private QcLiveBarcodeAndUUIDValidatorRemoteImpl qcLiveBarcodeAndUUIDValidatorRemote;
    private QcContext qcContext = new QcContext();

    @Before
    public void setUp() {
        qcLiveBarcodeAndUUIDValidatorRemote = new QcLiveBarcodeAndUUIDValidatorRemoteImpl();
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidAliquot() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_ALIQUOT_BARCODE, FILE_NAME, ALIQUOT_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Aliquot barcode '").append(INVALID_ALIQUOT_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidAliquotFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_ALIQUOT_BARCODE_FORMAT, FILE_NAME, ALIQUOT_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidAnalyte() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_ANALYTE_BARCODE, FILE_NAME, ANALYTE_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Analyte barcode '").append(INVALID_ANALYTE_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidAnalyteFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_ANALYTE_BARCODE_FORMAT, FILE_NAME, ANALYTE_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidPatient() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_PATIENT_BARCODE, FILE_NAME, PATIENT_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Patient barcode '").append(INVALID_PATIENT_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidPatientFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_PATIENT_BARCODE_FORMAT, FILE_NAME, PATIENT_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidPortion() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_PORTION_BARCODE, FILE_NAME, PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Portion barcode '").append(INVALID_PORTION_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidPortionFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_PORTION_BARCODE_FORMAT, FILE_NAME, PORTION_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidSample() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_SAMPLE_BARCODE, FILE_NAME, SAMPLE_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Sample barcode '").append(INVALID_SAMPLE_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidSampleFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_SAMPLE_BARCODE_FORMAT, FILE_NAME, SAMPLE_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidSlide() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_SLIDE_BARCODE, FILE_NAME, SLIDE_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Slide barcode '").append(INVALID_SLIDE_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidSlideFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_SLIDE_BARCODE_FORMAT, FILE_NAME, SLIDE_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidDrug() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_DRUG_BARCODE, FILE_NAME, DRUG_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Drug barcode '").append(INVALID_DRUG_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidDrugFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_DRUG_BARCODE_FORMAT, FILE_NAME, DRUG_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidRadiation() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_RADIATION_BARCODE, FILE_NAME, RADIATION_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Radiation barcode '").append(INVALID_RADIATION_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidRadiationFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_RADIATION_BARCODE_FORMAT, FILE_NAME, RADIATION_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidExamination() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_EXAMINATION_BARCODE, FILE_NAME, EXAMINATION_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Examination barcode '").append(INVALID_EXAMINATION_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidExaminationFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_EXAMINATION_BARCODE_FORMAT, FILE_NAME, EXAMINATION_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidSurgery() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_SURGERY_BARCODE, FILE_NAME, SURGERY_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Surgery barcode '").append(INVALID_SURGERY_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidSurgeryFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_SURGERY_BARCODE_FORMAT, FILE_NAME, SURGERY_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidShipmentPortion() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_SHIPMENT_PORTION_BARCODE, FILE_NAME, SHIPMENT_PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Shipment Portion barcode '").append(INVALID_SHIPMENT_PORTION_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidShipmentPortionFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_SHIPMENT_PORTION_BARCODE_FORMAT, FILE_NAME, SHIPMENT_PORTION_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidShippedPortion() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_SHIPPED_PORTION_BARCODE, FILE_NAME, SHIPPED_PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Shipped Portion barcode '").append(INVALID_SHIPPED_PORTION_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidShippedPortionFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_SHIPPED_PORTION_BARCODE_FORMAT, FILE_NAME, SHIPPED_PORTION_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithInvalidParticipant() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(INVALID_PARTICIPANT_BARCODE, FILE_NAME, PARTICIPANT_ITEM_TYPE_NAME);
        assertNotNull(result);

        final String expectedErrorMessage = new StringBuilder("The Participant barcode '").append(INVALID_PARTICIPANT_BARCODE)
                .append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, result);
    }

    @Test
    public void testValidateBarcodeFormatAndCodesWithValidParticipantFormat() {

        final String result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeFormatAndCodes(VALID_PARTICIPANT_BARCODE_FORMAT, FILE_NAME, PARTICIPANT_ITEM_TYPE_NAME);
        assertNull(result);
    }

    @Test
    public void testValidateUUIDMetadata() {

        final boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateUUIDMetadata(new MetaDataBean());
        assertTrue(result);
    }

    @Test
    public void testIsAliquotUUIDWhenUuidCorrectlyFormatted() {

        final boolean result = qcLiveBarcodeAndUUIDValidatorRemote.isAliquotUUID(VALID_UUID);
        assertTrue(result);
    }

    @Test
    public void testIsAliquotUUIDWhenUuidIncorrectlyFormatted() {

        final boolean result = qcLiveBarcodeAndUUIDValidatorRemote.isAliquotUUID(INVALID_UUID);
        assertTrue(result);
    }

    @Test
    public void testGetMetadataWhenUuidCorrectlyFormatted() throws CommonBarcodeAndUUIDValidator.CommonBarcodeAndUUIDValidatorException {

        final MetaDataBean result = qcLiveBarcodeAndUUIDValidatorRemote.getMetadata(VALID_UUID);
        assertNull(result);
    }

    @Test
    public void testGetMetadataWhenUuidIncorrectlyFormatted() throws CommonBarcodeAndUUIDValidator.CommonBarcodeAndUUIDValidatorException {

        final MetaDataBean result = qcLiveBarcodeAndUUIDValidatorRemote.getMetadata(INVALID_UUID);
        assertNull(result);
    }

    @Test
    public void testIsMatchingDiseaseForUUIDWhenUuidCorrectlyFormatted() {

        final boolean result = qcLiveBarcodeAndUUIDValidatorRemote.isMatchingDiseaseForUUID(VALID_UUID, "DA");
        assertTrue(result);
    }

    @Test
    public void testIsMatchingDiseaseForUUIDWhenUuidIncorrectlyFormatted() {

        final boolean result = qcLiveBarcodeAndUUIDValidatorRemote.isMatchingDiseaseForUUID(INVALID_UUID, "DA");
        assertTrue(result);
    }

    @Test
    public void testValidateUuidWithMustExistArgTrueWhenUuidValid() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateUuid(VALID_UUID, qcContext, FILE_NAME, true);
        assertNotNull(result);
        assertTrue(result);

        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testValidateUuidWithMustExistArgFalseWhenUuidValid() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateUuid(VALID_UUID, qcContext, FILE_NAME, false);
        assertNotNull(result);
        assertTrue(result);

        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testValidateUuidWithMustExistArgTrueWhenUuidInvalid() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateUuid(INVALID_UUID, qcContext, FILE_NAME, true);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating uuid '").append(INVALID_UUID).append("': The uuid '")
                .append(INVALID_UUID).append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateUuidWithMustExistArgFalseWhenUuidInvalid() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateUuid(INVALID_UUID, qcContext, FILE_NAME, false);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating uuid '").append(INVALID_UUID).append("': The uuid '")
                .append(INVALID_UUID).append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateUuidWhenUuidValid() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateUuid(VALID_UUID, qcContext, FILE_NAME);
        assertNotNull(result);
        assertTrue(result);

        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testValidateUuidWhenUuidInvalid() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateUuid(INVALID_UUID, qcContext, FILE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating uuid '").append(INVALID_UUID).append("': The uuid '")
                .append(INVALID_UUID).append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidAliquotThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_ALIQUOT_BARCODE_FORMAT, qcContext, FILE_NAME, false, ALIQUOT_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidAliquotThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_ALIQUOT_BARCODE_FORMAT, qcContext, FILE_NAME, true, ALIQUOT_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidAliquot() {

        final String barcode = INVALID_ALIQUOT_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, ALIQUOT_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Aliquot barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidAnalyteThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_ANALYTE_BARCODE_FORMAT, qcContext, FILE_NAME, false, ANALYTE_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidAnalyteThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_ANALYTE_BARCODE_FORMAT, qcContext, FILE_NAME, true, ANALYTE_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidAnalyte() {

        final String barcode = INVALID_ANALYTE_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, ANALYTE_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Analyte barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidPatientThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_PATIENT_BARCODE_FORMAT, qcContext, FILE_NAME, false, PATIENT_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidPatientThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_PATIENT_BARCODE_FORMAT, qcContext, FILE_NAME, true, PATIENT_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidPatient() {

        final String barcode = INVALID_PATIENT_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, PATIENT_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Patient barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidPortionThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_PORTION_BARCODE_FORMAT, qcContext, FILE_NAME, false, PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidPortionThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_PORTION_BARCODE_FORMAT, qcContext, FILE_NAME, true, PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidPortion() {

        final String barcode = INVALID_PORTION_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Portion barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidSampleThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_SAMPLE_BARCODE_FORMAT, qcContext, FILE_NAME, false, SAMPLE_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidSampleThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_SAMPLE_BARCODE_FORMAT, qcContext, FILE_NAME, true, SAMPLE_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidSample() {

        final String barcode = INVALID_SAMPLE_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, SAMPLE_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Sample barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidSlideThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_SLIDE_BARCODE_FORMAT, qcContext, FILE_NAME, false, SLIDE_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidSlideThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_SLIDE_BARCODE_FORMAT, qcContext, FILE_NAME, true, SLIDE_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidSlide() {

        final String barcode = INVALID_SLIDE_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, SLIDE_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Slide barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidDrugThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_DRUG_BARCODE_FORMAT, qcContext, FILE_NAME, false, DRUG_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidDrugThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_DRUG_BARCODE_FORMAT, qcContext, FILE_NAME, true, DRUG_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidDrug() {

        final String barcode = INVALID_DRUG_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, DRUG_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Drug barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidRadiationThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_RADIATION_BARCODE_FORMAT, qcContext, FILE_NAME, false, RADIATION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidRadiationThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_RADIATION_BARCODE_FORMAT, qcContext, FILE_NAME, true, RADIATION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidRadiation() {

        final String barcode = INVALID_RADIATION_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, RADIATION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Radiation barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidExaminationThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_EXAMINATION_BARCODE_FORMAT, qcContext, FILE_NAME, false, EXAMINATION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidExaminationThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_EXAMINATION_BARCODE_FORMAT, qcContext, FILE_NAME, true, EXAMINATION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidExamination() {

        final String barcode = INVALID_EXAMINATION_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, EXAMINATION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Examination barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidSurgeryThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_SURGERY_BARCODE_FORMAT, qcContext, FILE_NAME, false, SURGERY_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidSurgeryThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_SURGERY_BARCODE_FORMAT, qcContext, FILE_NAME, true, SURGERY_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidSurgery() {

        final String barcode = INVALID_SURGERY_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, SURGERY_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Surgery barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidShipmentPortionThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_SHIPMENT_PORTION_BARCODE_FORMAT, qcContext, FILE_NAME, false, SHIPMENT_PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidShipmentPortionThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_SHIPMENT_PORTION_BARCODE_FORMAT, qcContext, FILE_NAME, true, SHIPMENT_PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidShipmentPortion() {

        final String barcode = INVALID_SHIPMENT_PORTION_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, SHIPMENT_PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Shipment Portion barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidShippedPortionThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_SHIPPED_PORTION_BARCODE_FORMAT, qcContext, FILE_NAME, false, SHIPPED_PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidShippedPortionThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_SHIPPED_PORTION_BARCODE_FORMAT, qcContext, FILE_NAME, true, SHIPPED_PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidShippedPortion() {

        final String barcode = INVALID_SHIPPED_PORTION_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, SHIPPED_PORTION_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Shipped Portion barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateAnyBarcodeWithValidParticipantThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_PARTICIPANT_BARCODE_FORMAT, qcContext, FILE_NAME, false, PARTICIPANT_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithValidParticipantThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(VALID_PARTICIPANT_BARCODE_FORMAT, qcContext, FILE_NAME, true, PARTICIPANT_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateAnyBarcodeWithInvalidParticipant() {

        final String barcode = INVALID_PARTICIPANT_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateAnyBarcode(barcode, qcContext, FILE_NAME, false, PARTICIPANT_ITEM_TYPE_NAME);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Participant barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateWithAliquotCorrectlyFormattedThatDoesNotNeedToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validate(VALID_ALIQUOT_BARCODE_FORMAT, qcContext, FILE_NAME, false);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateWithAliquotCorrectlyFormattedThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validate(VALID_ALIQUOT_BARCODE_FORMAT, qcContext, FILE_NAME, true);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateWithAliquotIncorrectlyFormattedThatDoesNotNeedToExist() {

        final String barcode = INVALID_ALIQUOT_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validate(barcode, qcContext, FILE_NAME, false);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Aliquot barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateWithAliquotIncorrectlyFormattedThatMustExist() {

        final String barcode = INVALID_ALIQUOT_BARCODE;

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validate(barcode, qcContext, FILE_NAME, true);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);

        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(barcode)
                .append("': The Aliquot barcode '").append(barcode).append("' in file ")
                .append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testBatchValidateReportIndividualResultsWhenAliquotBarcodesMustExist() {

        final List<String> aliquotBarcodes = new ArrayList<String>();
        aliquotBarcodes.add(INVALID_ALIQUOT_BARCODE);
        aliquotBarcodes.add(VALID_ALIQUOT_BARCODE_FORMAT);

        final Map<String, Boolean> result = qcLiveBarcodeAndUUIDValidatorRemote.batchValidateReportIndividualResults(aliquotBarcodes, qcContext, FILE_NAME, true);
        assertNotNull(result);
        assertEquals(aliquotBarcodes.size(), result.size());

        assertTrue(result.containsKey(INVALID_ALIQUOT_BARCODE));
        assertTrue(result.containsKey(VALID_ALIQUOT_BARCODE_FORMAT));

        final Boolean individualResultForInvalidAliquot = result.get(INVALID_ALIQUOT_BARCODE);
        assertNotNull(individualResultForInvalidAliquot);
        assertFalse(individualResultForInvalidAliquot);

        final Boolean individualResultForValidAliquot = result.get(VALID_ALIQUOT_BARCODE_FORMAT);
        assertNotNull(individualResultForValidAliquot);
        assertTrue(individualResultForValidAliquot);
    }

    @Test
    public void testBatchValidateReportIndividualResultsWhenAliquotBarcodesDoNotHaveToExist() {

        final List<String> aliquotBarcodes = new ArrayList<String>();
        aliquotBarcodes.add(INVALID_ALIQUOT_BARCODE);
        aliquotBarcodes.add(VALID_ALIQUOT_BARCODE_FORMAT);

        final Map<String, Boolean> result = qcLiveBarcodeAndUUIDValidatorRemote.batchValidateReportIndividualResults(aliquotBarcodes, qcContext, FILE_NAME, false);
        assertNotNull(result);
        assertEquals(aliquotBarcodes.size(), result.size());

        assertTrue(result.containsKey(INVALID_ALIQUOT_BARCODE));
        assertTrue(result.containsKey(VALID_ALIQUOT_BARCODE_FORMAT));

        final Boolean individualResultForInvalidAliquot = result.get(INVALID_ALIQUOT_BARCODE);
        assertNotNull(individualResultForInvalidAliquot);
        assertFalse(individualResultForInvalidAliquot);

        final Boolean individualResultForValidAliquot = result.get(VALID_ALIQUOT_BARCODE_FORMAT);
        assertNotNull(individualResultForValidAliquot);
        assertTrue(individualResultForValidAliquot);
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsWhenUuidsMustExist() {

        final List<String> uuids = new ArrayList<String>();
        uuids.add(VALID_UUID);
        uuids.add(INVALID_UUID);

        final Map<String, Boolean> result = qcLiveBarcodeAndUUIDValidatorRemote.batchValidateUUIDsReportIndividualResults(uuids, qcContext, FILE_NAME, true);
        assertNotNull(result);
        assertEquals(uuids.size(), result.size());

        assertTrue(result.containsKey(INVALID_UUID));
        assertTrue(result.containsKey(VALID_UUID));

        final Boolean individualResultForInvalidUuid = result.get(INVALID_UUID);
        assertNotNull(individualResultForInvalidUuid);
        assertFalse(individualResultForInvalidUuid);

        final Boolean individualResultForValidUuid = result.get(VALID_UUID);
        assertNotNull(individualResultForValidUuid);
        assertTrue(individualResultForValidUuid);
    }

    @Test
    public void testBatchValidateUUIDsReportIndividualResultsWhenUuidsDoNotHaveToExist() {

        final List<String> uuids = new ArrayList<String>();
        uuids.add(VALID_UUID);
        uuids.add(INVALID_UUID);

        final Map<String, Boolean> result = qcLiveBarcodeAndUUIDValidatorRemote.batchValidateUUIDsReportIndividualResults(uuids, qcContext, FILE_NAME, false);
        assertNotNull(result);
        assertEquals(uuids.size(), result.size());

        assertTrue(result.containsKey(INVALID_UUID));
        assertTrue(result.containsKey(VALID_UUID));

        final Boolean individualResultForInvalidUuid = result.get(INVALID_UUID);
        assertNotNull(individualResultForInvalidUuid);
        assertFalse(individualResultForInvalidUuid);

        final Boolean individualResultForValidUuid = result.get(VALID_UUID);
        assertNotNull(individualResultForValidUuid);
        assertTrue(individualResultForValidUuid);
    }

    @Test
    public void testValidateBarcodeOrUuidWithCorrectlyFormattedUuidThatDoesNotHaveToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeOrUuid(VALID_UUID, qcContext, FILE_NAME, false);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateBarcodeOrUuidWithCorrectlyFormattedUuidThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeOrUuid(VALID_UUID, qcContext, FILE_NAME, true);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateBarcodeOrUuidWithIncorrectlyFormattedUuidThatDoesNotHaveToExist() {

        final String input = INVALID_UUID;
        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeOrUuid(input, qcContext, FILE_NAME, false);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);
        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(input)
                .append("': The Aliquot barcode '").append(input).append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateBarcodeOrUuidWithIncorrectlyFormattedUuidThatMustExist() {

        final String input = INVALID_UUID;
        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeOrUuid(input, qcContext, FILE_NAME, true);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);
        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(input)
                .append("': The Aliquot barcode '").append(input).append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateBarcodeOrUuidWithCorrectlyFormattedAliquotThatDoesNotHaveToExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeOrUuid(VALID_ALIQUOT_BARCODE_FORMAT, qcContext, FILE_NAME, false);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateBarcodeOrUuidWithCorrectlyFormattedAliquotThatMustExist() {

        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeOrUuid(VALID_ALIQUOT_BARCODE_FORMAT, qcContext, FILE_NAME, true);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateBarcodeOrUuidWithIncorrectlyFormattedAliquotThatDoesNotHaveToExist() {

        final String input = INVALID_ALIQUOT_BARCODE;
        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeOrUuid(input, qcContext, FILE_NAME, false);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);
        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(input)
                .append("': The Aliquot barcode '").append(input).append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testValidateBarcodeOrUuidWithIncorrectlyFormattedAliquotThatMustExist() {

        final String input = INVALID_ALIQUOT_BARCODE;
        final Boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateBarcodeOrUuid(input, qcContext, FILE_NAME, true);
        assertNotNull(result);
        assertFalse(result);

        assertEquals(1, qcContext.getErrorCount());

        final String actualErrorMessage = qcContext.getErrors().get(0);
        assertNotNull(actualErrorMessage);
        final String expectedErrorMessage = new StringBuilder("An error occurred while validating barcode '").append(input)
                .append("': The Aliquot barcode '").append(input).append("' in file ").append(FILE_NAME).append(" has an invalid format").toString();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeWithCorrectlyFormattedUuidAndAliquotBarcode() {

        final List<String[]> sampleUuidAndSampleTcgaBarcodePairs = new ArrayList<String[]>();
        final String[] sampleUuidAndSampleTcgaBarcodePair = {VALID_UUID, VALID_ALIQUOT_BARCODE_FORMAT};
        sampleUuidAndSampleTcgaBarcodePairs.add(sampleUuidAndSampleTcgaBarcodePair);

        final boolean result = qcLiveBarcodeAndUUIDValidatorRemote.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodePairs, qcContext);
        assertTrue(result);
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeWithIncorrectlyFormattedUuidAndAliquotBarcode() {

        final List<String[]> sampleUuidAndSampleTcgaBarcodePairs = new ArrayList<String[]>();
        final String[] sampleUuidAndSampleTcgaBarcodePair = {INVALID_UUID, INVALID_ALIQUOT_BARCODE};
        sampleUuidAndSampleTcgaBarcodePairs.add(sampleUuidAndSampleTcgaBarcodePair);

        final boolean result = qcLiveBarcodeAndUUIDValidatorRemote.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodePairs, qcContext);
        assertFalse(result);
        assertEquals(2, qcContext.getErrorCount());

        final String expectedErrorMessage1 = "An error occurred while validating uuid 'invalid-uuid': The uuid 'invalid-uuid' in file  has an invalid format";
        final String expectedErrorMessage2 = "An error occurred while validating barcode 'invalid-aliquot-barcode': The Aliquot barcode 'invalid-aliquot-barcode' in file  has an invalid format";

        assertTrue(qcContext.getErrors().contains(expectedErrorMessage1));
        assertTrue(qcContext.getErrors().contains(expectedErrorMessage2));
    }

    @Test
    public void testValidateUUIDBarcodeMappingWithCorrectlyFormattedUuidAndAliquotBarcode() {

        final boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateUUIDBarcodeMapping(VALID_UUID, VALID_ALIQUOT_BARCODE_FORMAT);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testValidateUUIDBarcodeMappingWithIncorrectlyFormattedUuidAndAliquotBarcode() {

        final boolean result = qcLiveBarcodeAndUUIDValidatorRemote.validateUUIDBarcodeMapping(INVALID_UUID, INVALID_ALIQUOT_BARCODE);
        assertNotNull(result);
        assertFalse(result);
    }
}
