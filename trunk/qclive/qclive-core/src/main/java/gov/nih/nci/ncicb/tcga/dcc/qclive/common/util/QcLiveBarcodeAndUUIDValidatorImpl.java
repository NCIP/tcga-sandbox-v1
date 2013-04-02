/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationErrors;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResult;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResults;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.BiospecimenIdWsQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.ValidationWebServiceQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl.WebServiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Barcode validator
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class QcLiveBarcodeAndUUIDValidatorImpl extends CommonBarcodeAndUUIDValidatorImpl implements QcLiveBarcodeAndUUIDValidator {

    private BCRIDQueries bcrIdQueries;

    /**
     * Used only by Soundcheck. Do NOT set this property for QcLive.
     */
    private BiospecimenIdWsQueries biospecimenIdWsQueries;

    /**
     * For use by Soundcheck
     */
    private ValidationWebServiceQueries validationWebServiceQueries;

    /**
     * Validate a barcode and put any error message in the context
     *
     * @param input the barcode to validate
     * @param context the <code>QcContext</code>
     * @param fileName the name of the file the barcode is coming from
     * @return <code>true</code> if the barcode is valid, <code>false</code> otherwise
     */
    @Override
    public Boolean validate(final String input, final QcContext context, final String fileName) {
        return validate(input, context, fileName, false);
    }

    @Override
    public Boolean validate(final String input, final QcContext context, final String fileName, final boolean mustExist) {
        String errorMessage = validateAliquotBarcodeFormatAndCodes(input, fileName);
        boolean valid = errorMessage == null;
        if (valid && mustExist) {
            if(!isBarcodeExists(input)) {
                errorMessage = "Barcode '" + input + "' has not been submitted by the BCR yet, so data for it cannot be accepted";
                valid = false;
            }
        }

        if (context != null && !valid) {
            context.addError(MessageFormat.format(
                    MessagePropertyType.BARCODE_VALIDATION_ERROR,
                    input,
        			errorMessage));
        }
        return valid;
    }

    @Override
    public Boolean batchValidate(final List<String> input,
                                 final QcContext context,
                                 final String fileName,
                                 final boolean mustExist) {


        if(input.size() > 0){
            boolean result = false;
            Map<String, Boolean> individualResults;
            if(isUUID( input.get(0))){
                individualResults = batchValidateUUIDsReportIndividualResults(input,context,fileName,mustExist);
            }else{
                individualResults = batchValidateReportIndividualResults(input, context, fileName, mustExist);
            }
            if(individualResults != null && !individualResults.containsValue(new Boolean(false))) {
                result = true;
            }

            return result;

        }
        return true;
    }

    /**
     * Validates multiple barcodes and returns the results in a {@link Map} of barcode->validity.
     *
     * Note: Initialize the {@link Map} with each barcodes set to <code>false</code> so that
     * if any {@link WebServiceException} is caught during the call to the web service,
     * the validity of each barcode will be set properly.
     *
     * @param input list of barcodes
     * @param context the qc context
     * @param fileName the name of the file containing the barcode
     * @param mustExist if true, will fail if the barcode is not already in the database
     * @return the results in a {@link Map} of barcode->validity
     */
    @Override
    public Map<String, Boolean> batchValidateReportIndividualResults(final List<String> input,
                                                                     final QcContext context,
                                                                     final String fileName,
                                                                     final boolean mustExist) {

        final Map<String, Boolean> barcodeValidityMap = new HashMap<String, Boolean>();
        final List<String> correctlyFormattedBarcodes = new ArrayList<String>();

        for(final String barcode: input) {

            barcodeValidityMap.put(barcode, false); // Default barcode validity
            final String errorMessage = validateAliquotBarcodeFormatAndCodes(barcode, fileName);

            if(errorMessage != null){
                context.addError(MessageFormat.format(
                        MessagePropertyType.BARCODE_VALIDATION_ERROR,
                        barcode,
                        errorMessage));

            } else if(mustExist) { // Passed Format and codes validation, and must exist in the DB

                // This API needs to be updated to verify batch of barcodes instead of one by one
                if(bcrIdQueries != null) { // try local database if we can use database query

                    if(bcrIdQueries.exists(barcode) == -1) {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.BARCODE_VALIDATION_ERROR,
                                barcode,
                                "Barcode '" + barcode + "' has not been submitted by the BCR yet, so data for it cannot be accepted"));
                    } else {
                        barcodeValidityMap.put(barcode, true);
                    }

                } else {
                    // This is for standalone validator: barcodes are validated in batches
                    correctlyFormattedBarcodes.add(barcode);
                }

            } else { // Passed Format and codes validation, and does not need to exist in the DB
                barcodeValidityMap.put(barcode, true);
            }
        }

        // Batch validation of correctly formatted barcodes for the standalone validator
        if((correctlyFormattedBarcodes.size() > 0)){

            try{
                final List<String> barcodesNotInDB = biospecimenIdWsQueries.exists(correctlyFormattedBarcodes);
                for(final String barcode: correctlyFormattedBarcodes){
                    if(barcodesNotInDB.contains(barcode)) {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.BARCODE_VALIDATION_ERROR,
                                barcode,
                                "Barcode '" + barcode + "' has not been submitted by the BCR yet, so data for it cannot be accepted"));

                    } else {
                        barcodeValidityMap.put(barcode, true);
                    }
                }

            } catch(final WebServiceException we) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.BARCODE_VALIDATION_ERROR,
                        "",
                        we.getMessage()));
            }
        }

        return barcodeValidityMap;
    }

    @Override
    public Map<String, Boolean> batchValidateUUIDsReportIndividualResults(final List<String> uuids,
                                                                          final QcContext qcContext,
                                                                          final String fileName,
                                                                          final boolean mustExist) {

        final Map<String, Boolean> result = new HashMap<String, Boolean>();

        if(uuids != null) {

            // Default UUID validity
            for(final String uuid : uuids) {
                result.put(uuid, validateUuid(uuid, qcContext, fileName));
            }

            if(mustExist) {
                try {
                    final ValidationResults validationResults = getValidationWebServiceQueries().validateUUIDs(uuids);

                    if(validationResults != null && validationResults.getValidationResult() != null) {

                        for(final ValidationResult validationResult : validationResults.getValidationResult()) {

                            if(validationResult != null) {

                                final String uuid = validationResult.getValidationObject();
                                final boolean uuidExistInDB = validationResult.existsInDB();
                                final ValidationErrors validationErrors = validationResult.getValidationError();

                                List<ValidationErrors.ValidationError> validationErrorList = null;
                                if(validationErrors != null) {
                                    validationErrorList = validationErrors.getValidationError();
                                }

                                boolean foundErrorsForUUID = validationErrorList != null && validationErrorList.size() > 0;

                                // Updating UUID validity
                                if(!foundErrorsForUUID && (!mustExist || (mustExist && uuidExistInDB))) {
                                    if(result.containsKey(uuid)) {
                                        result.put(uuid, true);
                                    }
                                }

                                if(qcContext != null && validationErrorList != null) {

                                    // Updating context
                                    for(final ValidationErrors.ValidationError validationError : validationErrorList) {

                                        if(validationError != null) {

                                            final String invalidValue = validationError.getInvalidValue();
                                            final StringBuilder errorMessage = new StringBuilder(validationError.getErrorMessage());

                                            if(fileName != null) {
                                                errorMessage.append(" in file '").append(fileName).append("'");
                                            }

                                            qcContext.addError(MessageFormat.format(
                                                    MessagePropertyType.UUID_VALIDATION_ERROR,
                                                    invalidValue,
                                                    errorMessage.toString()));
                                        }
                                    }
                                }
                            } else {
                                final String warningMessage = "Couldn't validate UUIDs";
                                qcContext.addWarning(MessageFormat.format(
                                        MessagePropertyType.UUID_VALIDATION_WARNING,
                                        uuids.toString(),
                                        warningMessage));
                            }
                        }

                    } else if(validationResults == null || validationResults.getValidationResult() == null) {

                        final String warningMessage = "Couldn't validate UUIDs";
                        qcContext.addWarning(MessageFormat.format(
                                MessagePropertyType.UUID_VALIDATION_WARNING,
                                uuids.toString(),
                                warningMessage));
                    }

                } catch (final WebServiceException e) {
                    qcContext.addError(MessageFormat.format(
                            MessagePropertyType.UUID_VALIDATION_ERROR,
                            "",
                            e.getMessage()));
                }

            }
        }

        return result;
    }

    /**
     * Validate a uuid and put any error message in the context
     *
     * @param input    the uuid to validate
     * @param context  the <code>QcContext</code>
     * @param fileName the name of the file the barcode is coming from
     * @return <code>true</code> if the uuid is valid, <code>false</code> otherwise
     */
    @Override
    public Boolean validateUuid(final String input, final QcContext context, final String fileName) {
        return validateUuid(input, context, fileName, false);
    }

    /**
     * Validate a uuid and put any error message in the context
     *
     * @param input    the uuid to validate
     * @param context  the <code>QcContext</code>
     * @param fileName the name of the file the uuid is coming from
     * @param mustExist uuid has to exist in the database
     * @return <code>true</code> if the uuid is valid, <code>false</code> otherwise
     */
    @Override
    public Boolean validateUuid(final String input, final QcContext context, final String fileName, final boolean mustExist) {

        String errorMessage = null;
        // check if uuid has valid format
        boolean valid = validateUUIDFormat(input);
        if (!valid) {
            errorMessage = new StringBuilder("The uuid '").append(input).append("' in file ").append(fileName).
                append(" has an invalid format").toString();
        }

        // check if uuid is in DB
        if (valid && mustExist && bcrIdQueries != null) {
            valid = bcrIdQueries.uuidExists(input);
            if (!valid) {
                errorMessage = new StringBuilder("The uuid '").append(input).append("' in file ").append(fileName).
                    append(" has not been submitted by the BCR yet, so data for it cannot be accepted").toString();
            }
        }

        if (!valid && context != null) {
            context.addError(MessageFormat.format( MessagePropertyType.UUID_VALIDATION_ERROR,
                    input, errorMessage));
        }
        return valid;
    }

    /**
     * Validate a barcode of a given type and put any error message in the context
     *
     * @param input the barcode to validate
     * @param context the <code>QcContext</code>
     * @param fileName the name of the file the barcode is coming from
     * @param mustExist if the barcode is required or optional
     * @param barcodeType barcode type
     * @return <code>true</code> if the barcode is valid, <code>false</code> otherwise
     */
    @Override
    public Boolean validateAnyBarcode(final String input, final QcContext context, final String fileName, final boolean mustExist, final String barcodeType) {
        String errorMessage = validateBarcodeFormatAndCodes(input, fileName, barcodeType);
        boolean valid = errorMessage == null;
        if (valid && mustExist) {
            if(!isBarcodeExists(input)) {
                errorMessage = "Barcode '" + input + "' has not been submitted by the BCR yet, so data for it cannot be accepted";
                valid = false;
            }
        }

        if (context != null && !valid) {
            context.addError(MessageFormat.format(
                    MessagePropertyType.BARCODE_VALIDATION_ERROR,
                    input,
        			errorMessage));
        }
        return valid;
    }

    public Boolean validateBarcodeOrUuid(final String input, final QcContext context, final String fileName, final boolean mustExist){
        if (isUUID(input)) {
            return validateUuid(input, context, fileName, mustExist);
        } else {
            return validate(input, context,fileName, mustExist);
        }
    }

    public Boolean isUUID(final String input){
        final Matcher matcher = UUID_PATTERN.matcher(input);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean batchValidateSampleUuidAndSampleTcgaBarcode(final List<String[]> sampleUuidAndSampleTcgaBarcodePairs, final QcContext qcContext) {

        boolean result = false;

        if(sampleUuidAndSampleTcgaBarcodePairs != null && sampleUuidAndSampleTcgaBarcodePairs.size() > 0) {

            try {
                final ValidationResults validationResults = getValidationWebServiceQueries().batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodePairs);

                if(validationResults != null && validationResults.getValidationResult() != null && validationResults.getValidationResult().size() > 0) {

                    for(final ValidationResult validationResult : validationResults.getValidationResult()) {

                        result = true;

                        if(validationResult != null) {

                            final ValidationErrors validationErrors = validationResult.getValidationError();

                            List<ValidationErrors.ValidationError> validationErrorList = null;
                            if(validationErrors != null) {
                                validationErrorList = validationErrors.getValidationError();
                            }

                            final boolean foundErrors = validationErrorList != null && validationErrorList.size() > 0;
                            result &= !foundErrors;

                            if(qcContext != null && foundErrors) {

                                // Updating context
                                for(final ValidationErrors.ValidationError validationError : validationErrorList) {

                                    if(validationError != null) {

                                        final String invalidValue = validationError.getInvalidValue();
                                        final String errorMessage = validationError.getErrorMessage();

                                        qcContext.addError(MessageFormat.format(
                                                MessagePropertyType.UUID_BARCODE_VALIDATION_ERROR,
                                                invalidValue,
                                                errorMessage));
                                    }
                                }
                            }

                        } else {
                            result = true;
                            final String warningMessage = "Couldn't validate SampleUUID(s) and SampleTCGABarcode(s)";
                            qcContext.addWarning(MessageFormat.format(
                                    MessagePropertyType.UUID_BARCODE_VALIDATION_WARNING,
                                    getListAsString(sampleUuidAndSampleTcgaBarcodePairs),
                                    warningMessage));
                        }
                    }

                } else {
                    result = true;
                    final String warningMessage = "Couldn't validate SampleUUID(s) and SampleTCGABarcode(s)";
                    qcContext.addWarning(MessageFormat.format(
                            MessagePropertyType.UUID_BARCODE_VALIDATION_WARNING,
                            getListAsString(sampleUuidAndSampleTcgaBarcodePairs),
                            warningMessage));
                }

            } catch (final WebServiceException e) {
                qcContext.addError(MessageFormat.format(
                        MessagePropertyType.UUID_BARCODE_VALIDATION_ERROR,
                        getListAsString(sampleUuidAndSampleTcgaBarcodePairs),
                        e.getMessage()));
            }

        } else {
            result = true;
        }

        return result;
    }

    /**
     * Returns the given list of SampleUUID/SampleTCGABarcode pairs as a String
     *
     * @param sampleUuidAndSampleTcgaBarcodePairs list of SampleUUID/SampleTCGABarcode pairs
     * @return the given list of SampleUUID/SampleTCGABarcode pairs as a String
     */
    private String getListAsString(final List<String[]> sampleUuidAndSampleTcgaBarcodePairs) {

        String result = null;

        if(sampleUuidAndSampleTcgaBarcodePairs != null) {

            final StringBuilder stringBuilder = new StringBuilder();

            for(final String[] sampleUuidAndSampleTcgaBarcodePair : sampleUuidAndSampleTcgaBarcodePairs) {

                if(sampleUuidAndSampleTcgaBarcodePair.length == 2) {

                    final String uuid = sampleUuidAndSampleTcgaBarcodePair[0];
                    final String barcode = sampleUuidAndSampleTcgaBarcodePair[1];

                    stringBuilder.append("[SampleUUID:").append(uuid).append(",SampleTCGABarcode:").append(barcode).append("],");
                }
            }

            if(stringBuilder.toString().endsWith(",")) {
                result = stringBuilder.substring(0, stringBuilder.length() - 1);
            }
        }

        return result;
    }

    public void setBcrIdQueries(final BCRIDQueries bcrIdQueries) {
        this.bcrIdQueries = bcrIdQueries;
    }

    public void setBiospecimenIdWsQueries(final BiospecimenIdWsQueries biospecimenIdWsQueries) {
        this.biospecimenIdWsQueries = biospecimenIdWsQueries;
    }

    public BiospecimenIdWsQueries getBiospecimenIdWsQueries() {
        return biospecimenIdWsQueries;
    }

    /**
     * Given a barcode, uses either the database query if one exists or the webservice query if one exists
     * to perform a query to determine if the barcode already exists in the system. If neither query exists, does
     * nothing and signifies that all is ok.
     * @param barcode
     * @return <code>boolean</code> returns <code>true</code> if the barcode exists in the system, <code>false</code> otherwise
     */
    protected boolean isBarcodeExists(final String barcode) {
        boolean bRet = true; // if we dont have access to any queries, say it is ok. this is soundcheck behavior.
        if(bcrIdQueries != null && bcrIdQueries.exists(barcode) == -1) { // try local database if we can use database query
                bRet = false;
        } else if(biospecimenIdWsQueries != null) { // otherwise hit the biospecimen metadata webservice to check existence of barcode
            bRet = biospecimenIdWsQueries.exists(barcode);
        }
        return bRet;
    }

    public ValidationWebServiceQueries getValidationWebServiceQueries() {
        return validationWebServiceQueries;
    }

    public void setValidationWebServiceQueries(final ValidationWebServiceQueries validationWebServiceQueries) {
        this.validationWebServiceQueries = validationWebServiceQueries;
    }
}
