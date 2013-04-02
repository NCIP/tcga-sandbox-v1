/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CommonBarcodeAndUUIDValidator implementation
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class CommonBarcodeAndUUIDValidatorImpl implements CommonBarcodeAndUUIDValidator {

    public final static Integer BARCODE_GROUP = 1;
    public final static Integer PROJECT_GROUP = 2;
    public final static Integer TSS_GROUP = 3;
    public final static Integer PATIENT_GROUP = 4;
    public final static Integer SAMPLE_ID_GROUP = 5;
    public final static Integer SAMPLE_TYPE_CODE_GROUP = 6;
    public final static Integer SAMPLE_NUMBER_GROUP = 7;
    public final static Integer PORTION_ID_GROUP = 8;
    public final static Integer PORTION_NUMBER_GROUP = 9;
    public final static Integer PORTION_ANALYTE_GROUP = 10;
    public final static Integer PLATE_ID_GROUP = 11;
    public final static Integer BCR_CENTER_ID_GROUP = 12;
    public final static Integer SLIDE_PORTION_NUMBER = 8;
    public final static Integer SLIDE_GROUP = 9;
    public final static Integer SLIDE_CODE_GROUP = 10;
    public final static Integer SLIDE_NUMBER_GROUP = 11;

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
    // this is a synonym of Shipment Portion
    public static String SHIPPED_PORTION_ITEM_TYPE_NAME = "Shipped Portion";
    // this is a synonym of Patient
    public static String PARTICIPANT_ITEM_TYPE_NAME = "Participant";

    /**
     * Regular expressions for the different barcodes
     */
    public final static String ALIQUOT_BARCODE_REGEXP = "((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\\d{2})([A-Z]{1}))-((\\d{2})([A-Z]{1}))-([A-Z0-9]{4})-(\\d{2}))";
    private final static String ANALYTE_BARCODE_REGEXP = "^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\\d{2})([A-Z]{1}))-((\\d{2})([A-Z]{1})))$";
    public final static String PATIENT_BARCODE_REGEXP = "((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4}))";
    private final static String PATIENT_BARCODE_REGEXP_STRICT = "^" + PATIENT_BARCODE_REGEXP + "$";
    private final static String PORTION_BARCODE_REGEXP = "^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\\d{2})([A-Z]{1}))-(\\d{2}))$";
    private final static String SAMPLE_BARCODE_REGEXP = "^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\\d{2})([A-Z]{1})))$";
    private final static String SLIDE_BARCODE_REGEXP = "^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\\d{2})([A-Z]{1}))-(\\d{2})-(([T|M|B]S)([A-Z0-9])))$";
    public final static String SHIPMENT_PORTION_BARCODE_REGEXP = "((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\\d{2})([A-Z]{1}))-(\\d{2})-([A-Z0-9]{4})-(\\d{2}))";
    private final static String HEX_REGEXP = "[0-9a-fA-F]";
    private final static String DRUG_BARCODE_REGEXP = "TCGA-.*";
    private final static String RADIATION_BARCODE_REGEXP = "TCGA-.*";
    private final static String EXAMINATION_BARCODE_REGEXP = "TCGA-.*";
    private final static String SURGERY_BARCODE_REGEXP = "TCGA-.*";
    private final static String DRUG_BARCODE_REGEXP_REFINED = "TCGA-([A-Z0-9]{2})-([A-Z0-9]{4})-[CDHIT].*";
    ;
    private final static String RADIATION_BARCODE_REGEXP_REFINED = "TCGA-([A-Z0-9]{2})-([A-Z0-9]{4})-[R].*";
    ;
    private final static String EXAMINATION_BARCODE_REGEXP_REFINED = "TCGA-([A-Z0-9]{2})-([A-Z0-9]{4})-[E].*";
    private final static String SURGERY_BARCODE_REGEXP_REFINED = "TCGA-([A-Z0-9]{2})-([A-Z0-9]{4})-[S].*";
    ;

    /**
     * Barcode regular expression for "special" barcode formats such as Examination={E}, Radiation={R}, Surgery={S} and Drug={C,D,H,I,T}
     */
    private final static String ANCILLARY_BARCODE_REGEXP = "TCGA-([A-Z0-9]{2})-([A-Z0-9]{4})-[A-Z].*";

    private final static String UUID_REGEXP = HEX_REGEXP + "{8}-" + HEX_REGEXP + "{4}-" + HEX_REGEXP + "{4}-" + HEX_REGEXP + "{4}-" + HEX_REGEXP + "{12}";
    private final static String UUID_REGEXP_STRICT = "^" + UUID_REGEXP + "$";

    /**
     * Pre-compiled Pattern for each regular expression
     */
    public final static Pattern ALIQUOT_BARCODE_PATTERN = Pattern.compile(ALIQUOT_BARCODE_REGEXP);
    private final static Pattern ANALYTE_BARCODE_PATTERN = Pattern.compile(ANALYTE_BARCODE_REGEXP);
    private final static Pattern ANCILLARY_BARCODE_PATTERN = Pattern.compile(ANCILLARY_BARCODE_REGEXP);
    private final static Pattern PATIENT_BARCODE_PATTERN_STRICT = Pattern.compile(PATIENT_BARCODE_REGEXP_STRICT);
    public final static Pattern PATIENT_BARCODE_PATTERN = Pattern.compile(PATIENT_BARCODE_REGEXP);
    private final static Pattern PORTION_BARCODE_PATTERN = Pattern.compile(PORTION_BARCODE_REGEXP);
    private final static Pattern SAMPLE_BARCODE_PATTERN = Pattern.compile(SAMPLE_BARCODE_REGEXP);
    private final static Pattern DRUG_BARCODE_PATTERN = Pattern.compile(DRUG_BARCODE_REGEXP);
    private final static Pattern RADIATION_BARCODE_PATTERN = Pattern.compile(RADIATION_BARCODE_REGEXP);
    private final static Pattern EXAMINATION_BARCODE_PATTERN = Pattern.compile(EXAMINATION_BARCODE_REGEXP);
    private final static Pattern SLIDE_BARCODE_PATTERN = Pattern.compile(SLIDE_BARCODE_REGEXP);
    private final static Pattern SURGERY_BARCODE_PATTERN = Pattern.compile(SURGERY_BARCODE_REGEXP);
    public final static Pattern UUID_PATTERN = Pattern.compile(UUID_REGEXP);
    private final static Pattern UUID_STRICT_PATTERN = Pattern.compile(UUID_REGEXP_STRICT);
    private final static Pattern SHIPMENT_PORTION_BARCODE_PATTERN = Pattern.compile(SHIPMENT_PORTION_BARCODE_REGEXP);
    private final static Pattern DRUG_BARCODE_PATTERN_REFINED = Pattern.compile(DRUG_BARCODE_REGEXP_REFINED);
    private final static Pattern RADIATION_BARCODE_PATTERN_REFINED = Pattern.compile(RADIATION_BARCODE_REGEXP_REFINED);
    private final static Pattern EXAMINATION_BARCODE_PATTERN_REFINED = Pattern.compile(EXAMINATION_BARCODE_REGEXP_REFINED);
    private final static Pattern SURGERY_BARCODE_PATTERN_REFINED = Pattern.compile(SURGERY_BARCODE_REGEXP_REFINED);

    /**
     * A map of barcode type to corresponding pattern
     */
    private final static Map<String, Pattern> barcodeTypeToPatternMap = new HashMap<String, Pattern>();

    static {
        barcodeTypeToPatternMap.put(ALIQUOT_ITEM_TYPE_NAME, ALIQUOT_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(ANALYTE_ITEM_TYPE_NAME, ANALYTE_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(PATIENT_ITEM_TYPE_NAME, PATIENT_BARCODE_PATTERN_STRICT);
        barcodeTypeToPatternMap.put(PORTION_ITEM_TYPE_NAME, PORTION_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(SAMPLE_ITEM_TYPE_NAME, SAMPLE_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(SLIDE_ITEM_TYPE_NAME, SLIDE_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(DRUG_ITEM_TYPE_NAME, DRUG_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(RADIATION_ITEM_TYPE_NAME, RADIATION_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(EXAMINATION_ITEM_TYPE_NAME, EXAMINATION_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(SURGERY_ITEM_TYPE_NAME, SURGERY_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(SHIPMENT_PORTION_ITEM_TYPE_NAME, SHIPMENT_PORTION_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(SHIPPED_PORTION_ITEM_TYPE_NAME, SHIPMENT_PORTION_BARCODE_PATTERN);
        barcodeTypeToPatternMap.put(PARTICIPANT_ITEM_TYPE_NAME, PATIENT_BARCODE_PATTERN);
    }

    /**
     * The different part of a barcode that can be validated against the database
     */
    protected static final String PROJECT = "project";
    protected static final String TSS = "tss";
    protected static final String SAMPLE_TYPE = "sampleType";
    protected static final String PORTION_ANALYTE = "portionAnalyte";
    protected static final String BCR_CENTER = "bcrCenter";

    /**
     * A map of barcode type to array of codes to be checked against the database
     */
    private final static Map<String, String[]> barcodeTypeToCodesMap = new HashMap<String, String[]>();

    static {
        final String[] aliquotCodes = {PROJECT, TSS, SAMPLE_TYPE, PORTION_ANALYTE, BCR_CENTER};
        final String[] analyteCodes = {PROJECT, TSS, SAMPLE_TYPE, PORTION_ANALYTE};
        final String[] patientCodes = {PROJECT, TSS};
        final String[] portionCodes = {PROJECT, TSS, SAMPLE_TYPE};
        final String[] sampleCodes = {PROJECT, TSS, SAMPLE_TYPE};
        final String[] slideCodes = {PROJECT, TSS, SAMPLE_TYPE};
        final String[] shipmentPortionCodes = {PROJECT, TSS, SAMPLE_TYPE};

        barcodeTypeToCodesMap.put(ALIQUOT_ITEM_TYPE_NAME, aliquotCodes);
        barcodeTypeToCodesMap.put(ANALYTE_ITEM_TYPE_NAME, analyteCodes);
        barcodeTypeToCodesMap.put(PATIENT_ITEM_TYPE_NAME, patientCodes);
        barcodeTypeToCodesMap.put(PORTION_ITEM_TYPE_NAME, portionCodes);
        barcodeTypeToCodesMap.put(SAMPLE_ITEM_TYPE_NAME, sampleCodes);
        barcodeTypeToCodesMap.put(SLIDE_ITEM_TYPE_NAME, slideCodes);
        barcodeTypeToCodesMap.put(SHIPMENT_PORTION_ITEM_TYPE_NAME, shipmentPortionCodes);
        barcodeTypeToCodesMap.put(SHIPPED_PORTION_ITEM_TYPE_NAME, shipmentPortionCodes);
    }

    /**
     * Codes Queries
     */
    @Autowired
    protected CodeTableQueries codeTableQueries;

    @Autowired
    protected ShippedBiospecimenQueries shippedBiospecimenQueries;

    @Override
    public Boolean validateAliquotFormatAndCodes(final String input) {
        return validateAliquotBarcodeFormatAndCodes(input, null) == null;
    }

    @Override
    public String validateAliquotBarcodeFormatAndCodes(final String input, final String fileName) {
        return validateBarcodeFormatAndCodes(input, fileName, ALIQUOT_ITEM_TYPE_NAME);
    }

    @Override
    public String validateBarcodeFormatAndCodes(final String input, final String fileName, final String expectedBarcodeType) {

        final boolean validateCodes = true;
        return validateBarcode(input, fileName, expectedBarcodeType, validateCodes);
    }

    @Override
    public String validateBarcodeFormat(final String input, final String fileName, final String expectedBarcodeType) {

        final boolean validateCodes = false;
        return validateBarcode(input, fileName, expectedBarcodeType, validateCodes);
    }

    @Override
    public boolean validateAnyBarcodeFormat(final String barcode) {
        if (validateAliquotBarcodeFormat(barcode)) {
            return true;
        }
        if (validateAnalyteBarcodeFormat(barcode)) {
            return true;
        }
        if (validatePatientBarcodeFormat(barcode)) {
            return true;
        }
        if (validatePortionBarcodeFormat(barcode)) {
            return true;
        }
        if (validateSampleBarcodeFormat(barcode)) {
            return true;
        }
        if (validateSlideBarcodeFormat(barcode)) {
            return true;
        }
        if (validateShipmentPortionBarcodeFormat(barcode)) {
            return true;
        }
        if (validateAncillaryBarcodeFormat(barcode)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean validateAliquotBarcodeFormat(final String aliquotBarcode) {
        return ALIQUOT_BARCODE_PATTERN.matcher(aliquotBarcode).matches();
    }

    @Override
    public boolean validateAnalyteBarcodeFormat(final String analyteBarcode) {
        return ANALYTE_BARCODE_PATTERN.matcher(analyteBarcode).matches();
    }

    @Override
    public boolean validateAncillaryBarcodeFormat(final String ancillaryBarcode) {
        return ANCILLARY_BARCODE_PATTERN.matcher(ancillaryBarcode).matches();
    }

    @Override
    public boolean validatePatientBarcodeFormat(final String patientBarcode) {
        return PATIENT_BARCODE_PATTERN_STRICT.matcher(patientBarcode).matches();
    }

    @Override
    public boolean validatePortionBarcodeFormat(final String portionBarcode) {
        return PORTION_BARCODE_PATTERN.matcher(portionBarcode).matches();
    }

    @Override
    public boolean validateShipmentPortionBarcodeFormat(final String shipmentPortionBarcode) {
        return SHIPMENT_PORTION_BARCODE_PATTERN.matcher(shipmentPortionBarcode).matches();
    }

    @Override
    public boolean validateSampleBarcodeFormat(final String sampleBarcode) {
        return SAMPLE_BARCODE_PATTERN.matcher(sampleBarcode).matches();
    }

    @Override
    public boolean validateDrugBarcodeFormat(final String drugBarcode) {
        return DRUG_BARCODE_PATTERN_REFINED.matcher(drugBarcode).matches();
    }

    @Override
    public boolean validateRadiationBarcodeFormat(final String radiationBarcode) {
        return RADIATION_BARCODE_PATTERN_REFINED.matcher(radiationBarcode).matches();
    }

    @Override
    public boolean validateSurgeryBarcodeFormat(final String surgeryBarcode) {
        return SURGERY_BARCODE_PATTERN_REFINED.matcher(surgeryBarcode).matches();
    }

    @Override
    public boolean validateExaminationBarcodeFormat(final String examinationBarcode) {
        return EXAMINATION_BARCODE_PATTERN_REFINED.matcher(examinationBarcode).matches();
    }

    @Override
    public boolean validateSlideBarcodeFormat(final String slideBarcode) {

        boolean result = false;
        final Matcher matcher = SLIDE_BARCODE_PATTERN.matcher(slideBarcode);

        if (matcher.matches()) {
            //Verify that Slide number is equivalent to the Portion number
            final String portionNumber = matcher.group(SLIDE_PORTION_NUMBER);
            final String slideNumber = matcher.group(SLIDE_NUMBER_GROUP);

            if (isEquivalentNumber(portionNumber, slideNumber)) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean validateUUIDFormat(final String uuid) {

        boolean result = false;

        if (uuid != null) {

            final Matcher matcher = UUID_STRICT_PATTERN.matcher(uuid);
            result = matcher.matches();
        }

        return result;
    }

    @Override
    public String getAliquotBarcode(final String inputToParse) {

        String result = null;

        final Matcher matcher = ALIQUOT_BARCODE_PATTERN.matcher(inputToParse);
        if (matcher.find()) {
            result = matcher.group(0);
        }

        return result;
    }

    @Override
    public String getPatientBarcode(final String inputToParse) {

        String result = null;

        final Matcher matcher = PATIENT_BARCODE_PATTERN.matcher(inputToParse);
        if (matcher.find()) {
            result = matcher.group(0);
        }

        return result;
    }

    @Override
    public String getUUID(final String inputToParse) {

        String result = null;

        final Matcher matcher = UUID_PATTERN.matcher(inputToParse);
        if (matcher.find()) {
            result = matcher.group(0);
        }

        return result;
    }

    @Override
    public String getItemType(final String barcode) {
        if (validateAliquotBarcodeFormat(barcode)) {
            return ALIQUOT_ITEM_TYPE_NAME;
        }
        if (validateAnalyteBarcodeFormat(barcode)) {
            return ANALYTE_ITEM_TYPE_NAME;
        }
        if (validatePatientBarcodeFormat(barcode)) {
            return PARTICIPANT_ITEM_TYPE_NAME;
        }
        if (validatePortionBarcodeFormat(barcode)) {
            return PORTION_ITEM_TYPE_NAME;
        }
        if (validateSampleBarcodeFormat(barcode)) {
            return SAMPLE_ITEM_TYPE_NAME;
        }
        if (validateSlideBarcodeFormat(barcode)) {
            return SLIDE_ITEM_TYPE_NAME;
        }
        if (validateShipmentPortionBarcodeFormat(barcode)) {
            return SHIPPED_PORTION_ITEM_TYPE_NAME;
        }

        //Checks to be executed after every other checks fail
        if (validateDrugBarcodeFormat(barcode)) {
            return DRUG_ITEM_TYPE_NAME;
        }
        if (validateRadiationBarcodeFormat(barcode)) {
            return RADIATION_ITEM_TYPE_NAME;
        }
        if (validateExaminationBarcodeFormat(barcode)) {
            return EXAMINATION_ITEM_TYPE_NAME;
        }
        if (validateSurgeryBarcodeFormat(barcode)) {
            return SURGERY_ITEM_TYPE_NAME;
        }
        return null;
    }

    /**
     * Validate the formatting (and optionally the codes) of the given input expecting it to be a barcode of a given type
     *
     * @param input               the input to validate
     * @param fileName            the name of the file the barcode is coming from
     * @param expectedBarcodeType the expected barcode type
     * @param validateCodes       <code>true</code> if the barcode individual codes should be validated against the database, <code>false</code> otherwise
     * @return the error message, if any
     */
    private String validateBarcode(final String input, final String fileName, final String expectedBarcodeType, final boolean validateCodes) {

        Boolean valid = true;
        final List<String> errors = new ArrayList<String>();
        final StringBuilder errorMsg = new StringBuilder();

        if (input == null) {
            valid = false;
            errorMsg.append(" is null");

        } else if ("".equals(input)) {
            valid = false;
            errorMsg.append(" is empty");

        } else if (input.trim().length() != input.length()) {
            valid = false;
            errorMsg.append(" has leading or trailing whitespace");

        } else if (barcodeTypeToPatternMap.containsKey(expectedBarcodeType)) {

            final String trimmedInput = input.trim();
            final Matcher barcodeMatcher = barcodeTypeToPatternMap.get(expectedBarcodeType).matcher(trimmedInput);

            if (barcodeMatcher.matches()) {

                if (validateCodes) {

                    if (codeTableQueries != null) { // this check is needed since SoundCheck does not have access to Database

                        final String[] codeGroups = barcodeTypeToCodesMap.get(expectedBarcodeType);
                        if (codeGroups != null) {
                            for (final String codeGroup : codeGroups) {

                                if (PROJECT.equals(codeGroup)) {
                                    valid = codeExists(PROJECT, barcodeMatcher.group(PROJECT_GROUP), errors) && valid;
                                } else if (TSS.equals(codeGroup)) {
                                    valid = codeExists(TSS, barcodeMatcher.group(TSS_GROUP), errors) && valid;
                                } else if (SAMPLE_TYPE.equals(codeGroup)) {
                                    valid = codeExists(SAMPLE_TYPE, barcodeMatcher.group(SAMPLE_TYPE_CODE_GROUP), errors) && valid;
                                } else if (PORTION_ANALYTE.equals(codeGroup)) {
                                    valid = codeExists(PORTION_ANALYTE, barcodeMatcher.group(PORTION_ANALYTE_GROUP), errors) && valid;
                                } else if (BCR_CENTER.equals(codeGroup)) {
                                    valid = codeExists(BCR_CENTER, barcodeMatcher.group(BCR_CENTER_ID_GROUP), errors) && valid;
                                }
                            }
                        }
                    }

                    if (errors.size() > 0) {
                        errorMsg.append(" has failed validation due to following errors :\n");
                        for (final String error : errors) {
                            errorMsg.append(error).append("\n");
                        }
                    }
                }

                // The Slide is a special case because, in addition to matching the regexp,
                // the Slide number must be equivalent to the Portion number
                if (SLIDE_ITEM_TYPE_NAME.equals(expectedBarcodeType) && !validateSlideBarcodeFormat(input)) {

                    valid = false;

                    final String portionNumber = barcodeMatcher.group(SLIDE_PORTION_NUMBER);
                    final String slideNumber = barcodeMatcher.group(SLIDE_NUMBER_GROUP);

                    final StringBuffer slideErrorMsg = new StringBuffer("the slide number '")
                            .append(slideNumber)
                            .append("' does not match the portion number '")
                            .append(portionNumber)
                            .append("'");

                    if (errors.size() > 0) { // Add to the list of errors
                        errorMsg.append(slideErrorMsg.toString() + "\n");
                    } else { // Create individual error message
                        errorMsg.append(" has an invalid format (").append(slideErrorMsg).append(")");
                    }
                }

            } else {
                valid = false;
                errorMsg.append(" has an invalid format");
            }

        } else {
            valid = false;
            errorMsg.append(" is not a supported barcode type");
        }

        if (!valid) {
            final StringBuilder errorStr = new StringBuilder()
                    .append("The ")
                    .append(expectedBarcodeType)
                    .append(" barcode '")
                    .append(input)
                    .append("'");

            if (fileName != null) {
                errorStr.append(" in file ").append(fileName);
            }

            errorStr.append(errorMsg);
            return errorStr.toString();

        } else {
            return null;
        }
    }

    /**
     * Return <code>true</code> if the code of the given type exists in the database, <code>false</code> otherwise
     *
     * @param codeType  the code type
     * @param codeValue the code value
     * @param errors    a list to add errors to, if any
     * @return <code>true</code> if the code of the given type exists in the database, <code>false</code> otherwise
     */
    private boolean codeExists(final String codeType, final String codeValue, final List<String> errors) {
        boolean codeExists = false;
        String errorMsg = null;
        if (codeType.equals(PROJECT)) {
            errorMsg = "The project code '" + codeValue + "' in the barcode does not exist in database";
            codeExists = codeTableQueries.projectNameExists(codeValue);
        } else if (codeType.equals(TSS)) {
            errorMsg = "The tissue source site '" + codeValue + "' in the barcode does not exist in database";
            codeExists = codeTableQueries.tssCodeExists(codeValue);
        } else if (codeType.equals(SAMPLE_TYPE)) {
            errorMsg = "The sample Type '" + codeValue + "' in the barcode does not exist in database";
            codeExists = codeTableQueries.sampleTypeExists(codeValue);
        } else if (codeType.equals(PORTION_ANALYTE)) {
            errorMsg = "The portion analyte '" + codeValue + "' in the barcode does not exist in database";
            codeExists = codeTableQueries.portionAnalyteExists(codeValue);
        } else if (codeType.equals(BCR_CENTER)) {
            errorMsg = "The bcr Center '" + codeValue + "' in the barcode does not exist in database";
            codeExists = codeTableQueries.bcrCenterIdExists(codeValue);
        }
        if (!codeExists) {
            errors.add(errorMsg);
        }
        return codeExists;
    }

    /**
     * Return <code>true</code> if the portion number and the slide number are equivalent, <code>false</code> otherwise:
     * <p/>
     * 1 === 1 or 1 === A
     * 2 === 2 or 2 === B
     * 3 === 3 or 3 === C
     * etc ...
     *
     * @param portionNumber the portion number
     * @param slideNumber   the slide number
     * @return <code>true</code> if the portion number and the slide number are equivalent, <code>false</code> otherwise
     */
    private boolean isEquivalentNumber(final String portionNumber, final String slideNumber) {

        final Integer portionNumberAsInteger = getInteger(portionNumber);
        final Integer slideNumberAsInteger = getInteger(slideNumber);
        boolean result = portionNumberAsInteger != null && portionNumberAsInteger.equals(slideNumberAsInteger);

        return result;
    }

    /**
     * Return the <code>Integer</code> value of the given input:
     * <p/>
     * - if the input can be parsed into a number, it will be that number
     * - if the input is a single alphabetical character, convert it to its order number in the alphabet (1-based)
     * - otherwise return <code>null</code>
     *
     * @param input the input to parse
     * @return the <code>Integer</code> value of the given input, or <code>null</code> if it can't be parsed
     */
    private Integer getInteger(final String input) {

        Integer result = null;

        if (!StringUtils.isBlank(input)) {

            try {
                result = Integer.parseInt(input);
            } catch (final NumberFormatException e) {

                if (input.length() == 1) {

                    char firstChar = input.toUpperCase().charAt(0);
                    result = 1 + firstChar - 'A';
                }
            }
        }

        return result;
    }

    public String getName() {
        return "barcode validation";
    }

    public void setCodeTableQueries(final CodeTableQueries codeTableQueries) {
        this.codeTableQueries = codeTableQueries;
    }

    public void setShippedBiospecimenQueries(ShippedBiospecimenQueries shippedBiospecimenQueries) {
        this.shippedBiospecimenQueries = shippedBiospecimenQueries;
    }

    @Override
    public boolean validateUUIDMetadata(final MetaDataBean newMetadata) {
        boolean isValidMetadata = false;

        if (newMetadata != null && StringUtils.isNotEmpty(newMetadata.getUUID())) {
            MetaDataBean existingMetadata = shippedBiospecimenQueries.retrieveUUIDMetadata(newMetadata.getUUID());
            if (existingMetadata.equals(newMetadata)) {
                isValidMetadata = true;
            }
        } else {
            throw new IllegalArgumentException(" unable to validate empty metadata or empty UUID");
        }
        return isValidMetadata;
    }

    @Override
    public boolean validateUUIDBarcodeMapping(final String UUID, final String barcode) {
        boolean isValidMapping = false;

        if (StringUtils.isEmpty(UUID)) {
            throw new IllegalArgumentException(" UUID must not be empty when comparing to barcode");
        }
        if (StringUtils.isEmpty(barcode)) {
            throw new IllegalArgumentException(" Barcode must not be empty when comparing to UUID");
        }

        MetaDataBean existingMetadata = shippedBiospecimenQueries.retrieveUUIDMetadata(UUID);

        if (existingMetadata != null
                && existingMetadata.isAliquot()
                && barcode.equals(existingMetadata.getAliquotBuiltBarcode())) {
            isValidMapping = true;
        }
        return isValidMapping;
    }


    @Override
    public boolean isAliquotUUID(final String uuid) {
        boolean isAliquot = false;

        if (StringUtils.isNotEmpty(uuid)) {
            String uuidLevel = shippedBiospecimenQueries.getUUIDLevel(uuid);
            if (StringUtils.isNotEmpty(uuidLevel)
                    && ALIQUOT_ITEM_TYPE_NAME.equalsIgnoreCase(uuidLevel)) {
                isAliquot = true;
            }
        } else {
            throw new IllegalArgumentException(" Unable to validate an empty UUID");
        }
        return isAliquot;
    }

    @Override
    public MetaDataBean getMetadata(final String uuid) throws CommonBarcodeAndUUIDValidatorException {

        MetaDataBean result = null;

        if (StringUtils.isNotEmpty(uuid)) {
            result = shippedBiospecimenQueries.retrieveUUIDMetadata(uuid);
        }

        if (result == null) {
            throw new CommonBarcodeAndUUIDValidatorException("Could not retrieve metadata for UUID '" + uuid + "'");
        }

        return result;
    }

    @Override
    public boolean isMatchingDiseaseForUUID(final String uuid, final String diseaseAbbreviation) {
        final String disease = shippedBiospecimenQueries.getDiseaseForUUID(uuid);
        return disease != null && (disease.equalsIgnoreCase(diseaseAbbreviation) || disease.equals(ConstantValues.CONTROL_DISEASE));
    }
}
