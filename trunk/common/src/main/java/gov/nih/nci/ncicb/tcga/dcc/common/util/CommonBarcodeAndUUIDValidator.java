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

/**
 * Provide common barcode validation
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface CommonBarcodeAndUUIDValidator {

    /**
     * Validate an aliquot barcode (formatting and individual codes)
     *
     * @param input the barcode to validate
     * @return <code>true</code> if the barcode is valid, <code>false</code> otherwise
     */
    public Boolean validateAliquotFormatAndCodes(final String input);

    /**
     * Validate an aliquot barcode (formatting and individual codes) and return the error message, if any
     *
     * @param input    the barcode to validate
     * @param fileName the name of the file the barcode is coming from
     * @return the error message, if any
     */
    public String validateAliquotBarcodeFormatAndCodes(final String input, final String fileName);

    /**
     * Validate the formatting and codes of the given input expecting it to be a barcode of a given type
     *
     * @param input               the input to validate
     * @param fileName            the name of the file the barcode is coming from
     * @param expectedBarcodeType the expected barcode type
     * @return the error message, if any
     */
    public String validateBarcodeFormatAndCodes(final String input, final String fileName, final String expectedBarcodeType);

    /**
     * Validate the formatting of the given input expecting it to be a barcode of a given type
     *
     * @param input               the input to validate
     * @param fileName            the name of the file the barcode is coming from
     * @param expectedBarcodeType the expected barcode type
     * @return the error message, if any
     */
    public String validateBarcodeFormat(final String input, final String fileName, final String expectedBarcodeType);

    /**
     * Validate any type of barcode (formatting only)
     *
     * @param barcode the barcode to validate
     * @return <code>true</code> if the input matches a barcode, <code>false</code> otherwise
     */
    public boolean validateAnyBarcodeFormat(final String barcode);

    /**
     * Validate an Aliquot barcode (formatting only)
     *
     * @param aliquotBarcode the Aliquot barcode to validate
     * @return <code>true</code> if the input matches an Aliquot barcode, <code>false</code> otherwise
     */
    public boolean validateAliquotBarcodeFormat(final String aliquotBarcode);

    /**
     * Validate an Analyte barcode (formatting only)
     *
     * @param analyteBarcode the Analyte barcode to validate
     * @return <code>true</code> if the input matches an Analyte barcode, <code>false</code> otherwise
     */
    public boolean validateAnalyteBarcodeFormat(final String analyteBarcode);

    /**
     * Validate a "special" barcode such as Examination={E}, Radiation={R}, Surgery={S} and Drug={C,D,H,I,T}
     *
     * @param ancillaryBarcode - the special barcode to validate
     * @return <code>true</code> if the input matches a special barcode format, <code>false</code> otherwise
     */
    public boolean validateAncillaryBarcodeFormat(final String ancillaryBarcode);

    /**
     * Validate a Patient barcode (formatting only)
     *
     * @param patientBarcode the Patient barcode to validate
     * @return <code>true</code> if the input matches a Patient barcode, <code>false</code> otherwise
     */
    public boolean validatePatientBarcodeFormat(final String patientBarcode);

    /**
     * Validate a Portion barcode (formatting only)
     *
     * @param portionBarcode the Portion barcode to validate
     * @return <code>true</code> if the input matches a Portion barcode, <code>false</code> otherwise
     */
    public boolean validatePortionBarcodeFormat(final String portionBarcode);

    /**
     * Validate a Shipment Portion barcode (formatting only)
     *
     * @param shipmentPortionBarcode the barcode to validate
     * @return <code>true</code> if the input matches a Shipment Portion barcode, <code>false</code> otherwise.
     */
    public boolean validateShipmentPortionBarcodeFormat(final String shipmentPortionBarcode);

    /**
     * Validate a Sample barcode (formatting only)
     *
     * @param sampleBarcode the Sample barcode to validate
     * @return <code>true</code> if the input matches a Sample barcode, <code>false</code> otherwise
     */
    public boolean validateSampleBarcodeFormat(final String sampleBarcode);

    /**
     * Validate a Slide barcode (formatting only)
     *
     * @param slideBarcode the Slide barcode to validate
     * @return <code>true</code> if the input matches a Slide barcode, <code>false</code> otherwise
     */
    public boolean validateSlideBarcodeFormat(final String slideBarcode);


    /**
     * Validate a Drug barcode (formatting only)
     *
     * @param drugBarcode the Drug barcode to validate
     * @return <code>true</code> if the input matches a Drug barcode, <code>false</code> otherwise
     */
    public boolean validateDrugBarcodeFormat(final String drugBarcode);

    /**
     * Validate a Radiation barcode (formatting only)
     *
     * @param radiationBarcode the Radiation barcode to validate
     * @return <code>true</code> if the input matches a Radiation barcode, <code>false</code> otherwise
     */
    public boolean validateRadiationBarcodeFormat(final String radiationBarcode);

    /**
     * Validate a Surgery barcode (formatting only)
     *
     * @param surgeryBarcode the Surgery barcode to validate
     * @return <code>true</code> if the input matches a Surgery barcode, <code>false</code> otherwise
     */
    public boolean validateSurgeryBarcodeFormat(final String surgeryBarcode);

    /**
     * Validate an Examination barcode (formatting only)
     *
     * @param examinationBarcode the Examination barcode to validate
     * @return <code>true</code> if the input matches a Examination barcode, <code>false</code> otherwise
     */
    public boolean validateExaminationBarcodeFormat(final String examinationBarcode);

    /**
     * Validate an UUID (formatting only)
     *
     * @param uuid the UUID to validate
     * @return <code>true</code> if the input matches an UUID, <code>false</code> otherwise
     */
    public boolean validateUUIDFormat(final String uuid);

    /**
     * Return the aliquot barcode if the input contains one
     *
     * @param inputToParse the input to parse
     * @return the aliquot barcode if the input contains one
     */
    public String getAliquotBarcode(final String inputToParse);

    /**
     * Return the patient barcode if the input contains one
     *
     * @param inputToParse the input to parse
     * @return the patient barcode if the input contains one
     */
    public String getPatientBarcode(final String inputToParse);

    /**
     * Return the UUID if the input contains one
     *
     * @param inputToParse the input to parse
     * @return the UUID if the input contains one
     */
    public String getUUID(final String inputToParse);

    /**
     * Return the Item type of a barcode
     *
     * @param barcode the barcode to parse
     * @return the corresponding item type
     */
    public String getItemType(final String barcode);

    /**
     * Returns true if metadata in the db identical to the one passed in the parameter
     *
     * @param metadata object to validate
     */
    public boolean validateUUIDMetadata(final MetaDataBean metadata);

    /**
     * True is an UUID is an aliquot UUID, false otherwise
     *
     * @param uuid to validate
     * @return true if a UUID is an aliquot, false otherwise
     */
    public boolean isAliquotUUID(final String uuid);

    /**
     * Return the metadata in a database from a uuid
     *
     * @param uuid the uuid
     * @return the MetaDataBean if it exists, null otherwise
     */
    public MetaDataBean getMetadata(final String uuid) throws CommonBarcodeAndUUIDValidatorException;

    /**
     * test if the input disease abbreviation match the input uuid
     *
     * @param uuid
     * @param diseaseAbbreviation
     * @return true if the input disease abbreviation match the input uuid
     */
    public boolean isMatchingDiseaseForUUID(final String uuid, final String diseaseAbbreviation);

    /**
     * Returns true if the metadata associated with the UUID
     * mateches the barcode, false otherwise
     *
     * @param UUID    - uuid for which to retrieve metadata
     * @param barcode - barcode to compare
     * @return true if the metadata associated with the UUID
     */
    public boolean validateUUIDBarcodeMapping(final String UUID, final String barcode);

    public class CommonBarcodeAndUUIDValidatorException extends Exception {

        public CommonBarcodeAndUUIDValidatorException(final String s) {
            super(s);
        }
    }
}
