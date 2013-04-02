/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidatorImpl;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Abstract class for RNASeq and miRNASeq data file validator
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class AbstractSeqDataFileValidator extends AbstractArchiveFileProcessor<Boolean> {

    private QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator;
    private BarcodeTumorValidator barcodeTumorValidator;
    private MessagePropertyType seqDataFileValidationErrorMessagePropertyType;

    /**
     * Validates a data value for a given header.
     *
     * @param value      the value
     * @param headerName the header
     * @param context    the qc context
     * @param rowNum     the row of the file
     * @return if the value is valid or not
     */
    protected abstract boolean valueIsValid(final String value, final String headerName, final QcContext context, final int rowNum);

    /**
     * Should return the expected columns for the file, in order expected.
     *
     * @return expected column names
     */
    protected abstract List<String> getExpectedColumns();

    /**
     * Reads through each line of the file and checks it.  Doesn't read the entire file in at once because the files
     * can be really large.
     *
     * @param file    the file to process
     * @param context the qc context
     * @return true if all rows of the file are valid, false otherwise
     * @throws ProcessorException
     */
    @Override
    protected Boolean processFile(final File file, final QcContext context) throws ProcessorException {
        boolean isValid = true;
        context.setFile(file);
        if (context.isCenterConvertedToUUID()) {
            isValid = newUUIDProcess(file, context, isValid);
        } else {
            isValid = oldBarcodeProcess(file, context, isValid);
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String headerLine = in.readLine();
            if (headerLine == null) {
                throw new ProcessorException("File " + file.getName() + " is empty");
            }
            String[] headers = headerLine.split("\t", -1);
            if (validateColumnHeaders(headers, context)) {
                int numHeaders = headers.length;
                String dataLine;
                int lineNum = 2;
                while ((dataLine = in.readLine()) != null) {
                    String[] data = dataLine.split("\t", -1);
                    if (validateRowLength(data, numHeaders, context, lineNum)) {
                        for (int i = 0; i < numHeaders; i++) {
                            isValid = valueIsValid(data[i], headers[i], context, lineNum) && isValid;
                        }
                    } else {
                        isValid = false;
                    }
                    lineNum++;
                }
            } else {
                isValid = false;
            }
        } catch (IOException e) {
            isValid = false;
            context.addError(MessageFormat.format(
                    getSeqDataFileValidationErrorMessagePropertyType(),
                    file.getName(),
                    e.getMessage()));
            context.addExceptionToLog(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return isValid;
    }

    protected boolean newUUIDProcess(File file, QcContext context, boolean valid) throws ProcessorException {
        String uuid = null;
        if (file != null) {
            final Matcher uuidMatcher = QcLiveBarcodeAndUUIDValidatorImpl.UUID_PATTERN.matcher(file.getName());
            if (uuidMatcher.find()) {
                uuid = uuidMatcher.group();
            }
        }
        if (uuid == null) {
            // still allow barcodes in filenames
            valid = oldBarcodeProcess(file, context, valid);

        } else if (!getQcLiveBarcodeAndUUIDValidator().isAliquotUUID(uuid)) {
            valid = false;
            addErrorMessage(context, null, "UUID in filename does not represent an aliquot");
        } else if (!getQcLiveBarcodeAndUUIDValidator().isMatchingDiseaseForUUID(uuid,
                context.getArchive().getTumorType())) {
            valid = false;
            addErrorMessage(context, null, "UUID in filename does not belong to the disease set for " +
                    context.getArchive().getTumorType());
        }
        return valid;
    }

    protected boolean oldBarcodeProcess(File file, QcContext context, boolean valid) throws ProcessorException {
        boolean isUUID = false;
        final String identifier = getBarcodeOrUUIDFromFilename(file);
        if (StringUtils.isNotEmpty((identifier))) {
            isUUID = QcLiveBarcodeAndUUIDValidatorImpl.UUID_PATTERN.matcher(identifier).matches();
        }

        if (identifier == null) {
            valid = false;
            addErrorMessage(context, null, "filename must include a valid TCGA aliquot barcode or a UUID");
        } else if (!isUUID && !getQcLiveBarcodeAndUUIDValidator().validateAliquotFormatAndCodes(identifier)) {
            valid = false;
            addErrorMessage(context, null, "TCGA aliquot barcode in filename is invalid");
        } else if (!isUUID && getBarcodeTumorValidator() != null &&
                !getBarcodeTumorValidator().barcodeIsValidForTumor(identifier, context.getArchive().getTumorType())) {
            valid = false;
            addErrorMessage(context, null, "TCGA aliquot barcode in filename does not belong to the disease set for " +
                    context.getArchive().getTumorType());
        }
        return valid;
    }

    protected String getBarcodeOrUUIDFromFilename(final File file) {
        String returnValue = null;
        if (file != null) {
            final Matcher barcodeMatcher = QcLiveBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN.matcher(file.getName());
            final Matcher uuidMatcher = QcLiveBarcodeAndUUIDValidatorImpl.UUID_PATTERN.matcher(file.getName());

            if (barcodeMatcher.find()) {
                returnValue = barcodeMatcher.group(QcLiveBarcodeAndUUIDValidatorImpl.BARCODE_GROUP);
            } else if (uuidMatcher.find()) {
                returnValue = uuidMatcher.group();
            }
        }
        return returnValue;
    }

    protected void addErrorMessage(final QcContext context, final Integer row, final String message) {
        context.addError(MessageFormat.format(
                getSeqDataFileValidationErrorMessagePropertyType(),
                context.getFile().getName(),
                new StringBuilder().append((row != null ? " line " + row : "")).append(": ").append(message).toString()));
    }

    /**
     * Checks the column headers against the expected column names.  Order matters.
     *
     * @param headers the headers from the file
     * @param context the qc context
     * @return if the headers are valid
     */
    protected boolean validateColumnHeaders(final String[] headers, final QcContext context) {
        boolean isValid = true;
        if (headers.length != getExpectedColumns().size()) {
            context.addError(MessageFormat.format(
                    getSeqDataFileValidationErrorMessagePropertyType(),
                    context.getFile().getName(),
                    new StringBuilder().append("Incorrect number of headers.  Expected '").append(getExpectedColumns()).
                            append("' but found '").append(Arrays.asList(headers)).append("'").toString()));
            isValid = false;
        } else {
            for (int i = 0; i < getExpectedColumns().size(); i++) {
                if (!getExpectedColumns().get(i).equals(headers[i])) {
                    context.addError(MessageFormat.format(
                            getSeqDataFileValidationErrorMessagePropertyType(),
                            context.getFile().getName(),
                            new StringBuilder().append("Expected header '").append(getExpectedColumns().get(i)).append("' at column '").
                                    append((i + 1)).append("' but found '").append(headers[i]).append("'").toString()));
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    /*
     * Checks to make sure the row has the right number of fields (compared with number of headers)
     */
    private boolean validateRowLength(final String[] row, final int numHeaders, final QcContext context, final int rowNum) {
        if (row.length != numHeaders) {
            addErrorMessage(context, rowNum, "number of fields should be " + numHeaders + " but is " + row.length + ". " + Arrays.asList(row));
            return false;
        } else {
            return true;
        }
    }

    //
    // Getter / Setter
    //

    public QcLiveBarcodeAndUUIDValidator getQcLiveBarcodeAndUUIDValidator() {
        return qcLiveBarcodeAndUUIDValidator;
    }

    public void setQcLiveBarcodeAndUUIDValidator(final QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator) {
        this.qcLiveBarcodeAndUUIDValidator = qcLiveBarcodeAndUUIDValidator;
    }

    public BarcodeTumorValidator getBarcodeTumorValidator() {
        return barcodeTumorValidator;
    }

    public void setBarcodeTumorValidator(final BarcodeTumorValidator barcodeTumorValidator) {
        this.barcodeTumorValidator = barcodeTumorValidator;
    }

    public MessagePropertyType getSeqDataFileValidationErrorMessagePropertyType() {
        return seqDataFileValidationErrorMessagePropertyType;
    }

    public void setSeqDataFileValidationErrorMessagePropertyType(final MessagePropertyType seqDataFileValidationErrorMessagePropertyType) {
        this.seqDataFileValidationErrorMessagePropertyType = seqDataFileValidationErrorMessagePropertyType;
    }
}
