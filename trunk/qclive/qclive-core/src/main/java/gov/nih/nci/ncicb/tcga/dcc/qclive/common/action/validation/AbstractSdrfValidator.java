/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.IDF;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractSdrfHandler;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidatorImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Abstract class for SdrfValidator.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public abstract class AbstractSdrfValidator extends
        AbstractSdrfHandler<Archive, Boolean> {

    private BarcodeTumorValidator barcodeTumorValidator;
    private QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator;
    private static final String BLANK = "";
    private static final String SDRF_NULL = "->";

    protected static final String EXTRACT_NAME_COLUMN_NAME = "Extract Name";
    protected static final String BARCODE_COMMENT_COLUMN_NAME = "Comment [TCGA Barcode]";
    protected static final int NUM_HEADERS = 1;
    protected static final int ZERO_INDEX_OFFSET = 1;

    protected static final List<String> REQUIRED_COMMENT_COLUMNS = new ArrayList<String>();

    static {
        REQUIRED_COMMENT_COLUMNS.add(COMMENT_ARCHIVE_NAME);
        REQUIRED_COMMENT_COLUMNS.add(COMMENT_DATA_LEVEL);
        REQUIRED_COMMENT_COLUMNS.add(COMMENT_DATA_TYPE);
        REQUIRED_COMMENT_COLUMNS.add(COMMENT_INCLUDE_FOR_ANALYSIS);
    }

    /**
     * Gets the collection of headers allowed in the SDRF. Order isn't
     * important. Headers that aren't in this list will be considered invalid.
     *
     * @return collection of allowed header names
     * @throws ProcessorException if there is an error getting the list
     */
    protected abstract Collection<String> getAllowedSdrfHeaders()
            throws ProcessorException;

    /**
     * Gets a map of columns to check, where the key is the header name and the
     * value is whether or not the column is required.
     *
     * @return map with header names and whether or not the column is required
     */
    protected abstract Map<String, Boolean> getColumnsToCheck();

    /**
     * Validates the given file header to see whether it is allowed for the
     * given level.
     *
     * @param context the qc context
     * @param header  the name of the file header
     * @param row     the row number being checked
     * @param level   the value of the level column for this file column
     * @return true if the file header and level are allowed together, false if
     *         not.
     */
    protected abstract boolean validateFileHeaderAndLevel(
            final QcContext context, final String header, final int row,
            final String level);

    /**
     * Runs any other validations needed.
     *
     * @param context       the qc context
     * @param sdrfNavigator the sdrf content
     * @return true if the validations passed, false if not
     */
    protected abstract boolean runSpecificValidations(QcContext context,
                                                      TabDelimitedContentNavigator sdrfNavigator);

    /**
     * Gets whether or not this SDRF spec implementation requires every column
     * to have at least one non-blank data value. Some implementations do not
     * allow entirely blank (where blank = '->') columns while some do.
     *
     * @return if columns are required to have data
     */
    protected abstract boolean getDataRequired();

    public String getName() {
        return "SDRF validation";
    }

    /**
     * Main method for this validator. Checks headers and then column values for
     * various things. If archive is not a mage-tab archive will just return
     * true.
     *
     * @param archive the archive to validate
     * @param context the context for this QC call
     * @return whether or not validation passed
     * @throws ProcessorException if an error is found that will prevent additional validations
     *                            from running
     */
    protected Boolean doWork(final Archive archive, final QcContext context)
            throws ProcessorException {
        if (!Archive.TYPE_MAGE_TAB.equals(archive.getArchiveType())) {
            // this validator only runs on mage-tab archives
            return true;
        }
        context.setArchive(archive);
        final TabDelimitedContent sdrf = archive.getSdrf();
        if (sdrf == null) {
            archive.setDeployStatus(Archive.STATUS_INVALID);
            throw new ProcessorException("Archive does not have an SDRF");
        }
        final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent(sdrf);

        boolean isValid = headersAreValid(sdrfNavigator, context);

        int numHeaders = sdrfNavigator.getHeaders().size();
        // check that each line has the same number of elements as the header
        for (int i = 0; i < sdrfNavigator.getNumRows(); i++) {
            if (sdrfNavigator.getRowByID(i).length != numHeaders) {
                archive.setDeployStatus(Archive.STATUS_INVALID);
                throw new ProcessorException(new StringBuilder()
                        .append("Malformed SDRF file: row ").append(i + 1)
                        .append(" has ")
                        .append(sdrfNavigator.getRowByID(i).length)
                        .append(" elements but there are ").append(numHeaders)
                        .append(" headers").toString());
            }
        }
        isValid = validateHeaderTokenCount(
                sdrfNavigator.getTabDelimitedContent(), context)
                && isValid;
        isValid = checkAllColumnsForBlanks(sdrfNavigator, context) && isValid;
        isValid = checkRequiredColumns(sdrfNavigator, context) && isValid;
        isValid = validateCommentColumns(context, sdrfNavigator) && isValid;
        isValid = runSpecificValidations(context, sdrfNavigator) && isValid;
        // only check the other things if the required columns are there --
        // otherwise may have problems
        if (isValid) {
            isValid = validateWithIDF(archive, context, sdrfNavigator)
                    && isValid;
            isValid = validateBarcodesAndUuids(context, sdrfNavigator)
                    && isValid;
            isValid = validateNonBarcodeRowData(context, sdrfNavigator)
                    && isValid;
            checkExtractNamePerArchive(context, sdrfNavigator);
        }

        if (!isValid) {
            archive.setDeployStatus(Archive.STATUS_INVALID);
        }
        return isValid;
    }

    /**
     * Iterates through *ALL* columns (not just expected columns) and make sure
     * that none of them contain blank values (empty string) Nulls should be
     * represented by '->'.
     *
     * @param sdrfNavigator a {@link TabDelimitedContentNavigator} for the SDRF
     * @param context       the qclive context
     * @return <code>true</code> if the SDRF does not contain any blank value,
     *         <code>false</code> otherwise
     */
    protected boolean checkAllColumnsForBlanks(
            final TabDelimitedContentNavigator sdrfNavigator,
            final QcContext context) {

        boolean result = true;

        final List<String> sdrfHeaders = sdrfNavigator.getHeaders();
        final int numberOfColumns = sdrfHeaders.size();

        for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
            final List<String> values = sdrfNavigator
                    .getColumnValues(columnIndex);

            // Column must not contain a blank value
            if (values.contains(BLANK)) {

                result = false;
                context.addError(MessageFormat.format(
                        MessagePropertyType.SDRF_COLUMN_BLANK_VALUE_ERROR,
                        sdrfHeaders.get(columnIndex), columnIndex + 1)); // Make
                // it
                // 1-based
                // column
                // index
            }
        }

        return result;
    }

    /**
     * Checks columns based on the getColumnsToCheck
     *
     * @param sdrfNavigator
     * @param context
     * @return
     */
    protected boolean checkRequiredColumns(
            final TabDelimitedContentNavigator sdrfNavigator,
            final QcContext context) {
        boolean isValid = true;
        for (final String columnName : getColumnsToCheck().keySet()) {
            isValid = validateColumn(sdrfNavigator, columnName,
                    getColumnsToCheck().get(columnName), getDataRequired(),
                    context)
                    && isValid;
        }

        if (context.isCenterConvertedToUUID()) {
            isValid &= validateColumn(sdrfNavigator, AbstractSdrfValidator.BARCODE_COMMENT_COLUMN_NAME, true, false, context);
            isValid &= checkCommentTCGABarcodePlacement(context, sdrfNavigator);
        }
        return isValid;
    }

    protected boolean checkCommentTCGABarcodePlacement(final QcContext context,
                                                       final TabDelimitedContentNavigator sdrfNavigator) {
        Boolean isValid = true;
        final Integer extractNameIndex = sdrfNavigator.getHeaderIDByName(EXTRACT_NAME_COLUMN_NAME);
        final Integer barcodeCommentIndex = sdrfNavigator.getHeaderIDByName(BARCODE_COMMENT_COLUMN_NAME);
        if (extractNameIndex != -1 && barcodeCommentIndex != -1 && barcodeCommentIndex != extractNameIndex + 1) {
            isValid = false;
            context.addError(MessageFormat.format(MessagePropertyType.COLUMN_PRECEDENCE_ERROR,
                    1, barcodeCommentIndex, EXTRACT_NAME_COLUMN_NAME, BARCODE_COMMENT_COLUMN_NAME));

        }
        return isValid;
    }

    /**
     * Validates a column: makes sure that:
     * <p/>
     * - if a column is required but not present, it will fail validation. - if
     * data is required but it can't find at least one value in the given column
     * that is not null ('->') it will also fail validation.
     * <p/>
     * Note: there is no validation for blank values as it is done for all
     * columns beforehand.
     *
     * @param sdrfNavigator a {@link TabDelimitedContentNavigator} for the SDRF
     * @param columnName    the name of the column to validate
     * @param isRequired    <code>true</code> if that column is required,
     *                      <code>false</code> otherwise
     * @param dataRequired  <code>true</code> if at least 1 non-null (non '->') value in
     *                      that column is required, <code>false</code> otherwise
     * @param context       the qclive context
     * @return <code>true</code> if all columns with the given name are valid,
     *         <code>false</code> otherwise
     */
    protected boolean validateColumn(
            final TabDelimitedContentNavigator sdrfNavigator,
            final String columnName, final boolean isRequired,
            final boolean dataRequired, final QcContext context) {

        boolean result = true;

        final List<Integer> sdrfHeaderColumnIndexes = sdrfNavigator
                .getHeaderIdsForName(columnName);

        if (sdrfHeaderColumnIndexes.size() > 0) {

            for (final Integer columnIndex : sdrfHeaderColumnIndexes) {

                final List<String> values = sdrfNavigator
                        .getColumnValues(columnIndex);

                // Validate column data: not null ('->') and valid
                boolean foundNonNull = false;
                int lineNum = 2; // 1 is header
                for (final String value : values) {
                    if (!SDRF_NULL.equals(value.trim())) {
                        foundNonNull = true;
                        result &= validateColumnValue(columnName, value,
                                lineNum, context);
                    }
                    lineNum++;
                }

                // Make sure at least 1 non null value on that column was found
                // if data is required
                if (dataRequired && !foundNonNull) {

                    result = false;
                    context.addError(MessageFormat.format(
                            MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
                            context.getArchive(), columnName));
                }
            }

        } else if (isRequired) {

            // No column found, but it is required
            result = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.MISSING_REQUIRED_SDRF_COLUMN_ERROR,
                    columnName));
        }

        return result;
    }

    /**
     * Validates the value for the given column. Should add an error to the
     * context if it isn't valid.
     *
     * @param columnName the column header
     * @param value      the value
     * @param lineNum    the line where the value was from
     * @param context    the qc context
     * @return whether the value is valid or not
     */
    protected abstract boolean validateColumnValue(String columnName,
                                                   String value, int lineNum, QcContext context);

    /*
      * Convenience method -- if column is null/not found will return null.
      */
    private List<String> getColumnValues(
            final TabDelimitedContentNavigator navigator, final Integer column) {
        if (column == null) {
            return null;
        } else {
            return navigator.getColumnValues(column);
        }
    }

    /**
     * Checks if the headers are valid using the getAllowedSdrfHeaders list: If
     * the header is in the format "A [B]" then the header is considered valid
     * if "A" is in the allowed list or if "A [B]" is. (Useful for being able to
     * add new Comment columns without causing problems.)
     *
     * @param sdrfNavigator the sdrf content
     * @param context       the qc context
     * @return if the headers are valid
     * @throws ProcessorException
     */
    protected boolean headersAreValid(
            final TabDelimitedContentNavigator sdrfNavigator,
            final QcContext context) throws ProcessorException {
        // get list of allowed headers. all headers in SDRF must be part of
        // this, but all
        boolean isValid = true;
        final Collection<String> allowedHeaders = getAllowedSdrfHeaders();
        for (final String header : sdrfNavigator.getHeaders()) {
            String altHeader = header;
            if (header.contains("[")) {
                altHeader = header.substring(0, header.indexOf("[")).trim();
            }

            if (!allowedHeaders.contains(header)
                    && !allowedHeaders.contains(altHeader)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                        context.getArchive(),
                        new StringBuilder()
                                .append("contains an invalid header: '")
                                .append(header).append("'").toString()));
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Checks for leading or trailing whitespace in SDRF values.
     *
     * @param context       the qc context
     * @param sdrfNavigator the sdrf
     * @return whether the validation passed or not
     */
    protected boolean validateNonBarcodeRowData(final QcContext context,
                                                final TabDelimitedContentNavigator sdrfNavigator) {
        boolean isValid = true;
        int columnCount = sdrfNavigator.getHeaders().size();
        for (int i = 0; i < columnCount; ++i) {
            List<String> values = sdrfNavigator.getColumnValues(i);
            int row = 2; // start with 2 because ignore the header
            for (final String original : values) {
                final String trimmed = original.trim();
                if (original.length() != trimmed.length()) {
                    isValid = false;
                    context.addError(MessageFormat
                            .format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                    context.getArchive(),
                                    new StringBuilder()
                                            .append("line '")
                                            .append(row)
                                            .append(" column '")
                                            .append(i + 1)
                                            .append("': ")
                                            .append(sdrfNavigator.getHeaders()
                                                    .get(i))
                                            .append(" '")
                                            .append(original)
                                            .append("' has leading or trailing whitespace")
                                            .toString()));
                }
                row++;
            }
        }
        return isValid;
    }

    /**
     * Validates barcodes and uuids in the Extract Name column, making sure they
     * are full aliquot barcodes or valid uuids. If barcode or uuid isn't valid,
     * then checks if the row represents a control sample.
     *
     * @param context       the qc context
     * @param sdrfNavigator the sdrf
     * @return whether all Extract Names are valid barcodes (or controls) or not
     * @throws ProcessorException
     */
    protected boolean validateBarcodesAndUuids(final QcContext context,
                                               final TabDelimitedContentNavigator sdrfNavigator)
            throws ProcessorException {

        boolean isValid = true;
        boolean mustExist = true;

        final List<String> extractNames = getColumnValues(sdrfNavigator,
                sdrfNavigator.getHeaderIDByName(EXTRACT_NAME_COLUMN_NAME));

        final List<String> barcodesToValidate = new ArrayList<String>();
        final List<String> uuidsToValidate = new ArrayList<String>();

        int row = 1; // first data row starts at row 1 (which is line 2 in the
        // file)
        for (int i = 0; i < extractNames.size(); i++) {

            final String extractName = extractNames.get(i);

            if (!isValidControlRow(context, sdrfNavigator, row, extractName)) {

                if (context.isCenterConvertedToUUID()) {
                    // we have remote impl for this now so can run it standalone or not
                    final List<String> barcodes = getColumnValues(
                            sdrfNavigator,
                            sdrfNavigator
                                    .getHeaderIDByName(BARCODE_COMMENT_COLUMN_NAME));

                    if (barcodes != null
                            && barcodes.size() == extractNames.size()) {
                        isValid &= validateConvertedUUIDs(barcodes.get(i), row,
                                extractName, context, mustExist);
                    } else {
                        isValid = false;
                        context.addError(MessageFormat
                                .format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                        context.getArchive(),
                                        new StringBuilder()
                                                .append(" The number of barcodes in ")
                                                .append(BARCODE_COMMENT_COLUMN_NAME)
                                                .append(" is not the same as UUIDs in ")
                                                .append(EXTRACT_NAME)
                                                .toString()));
                    }
                    // but only do batch validation if standalone
                    if (context.isStandaloneValidator()) {
                        if (!uuidsToValidate.contains(extractName)) {
                            uuidsToValidate.add(extractName);
                        }
                    }
                } else {
                    // not converted, so could be either uuids or barcodes

                    if (extractName != null && !extractName.trim().startsWith("TCGA")) {

                        if (context.isStandaloneValidator()) {
                            if (!uuidsToValidate.contains(extractName)) {
                                uuidsToValidate.add(extractName);
                            }
                        } else {


                            isValid &= qcLiveBarcodeAndUUIDValidator.validateUuid(
                                    extractName, context, context.getArchive()
                                    .getSdrfFile().getName(), mustExist);
                        }
                    } else {
                        // validate as barcode
                        if (context.isStandaloneValidator()) {
                            barcodesToValidate.add(extractName);
                        } else {
                            isValid &= qcLiveBarcodeAndUUIDValidator
                                    .validate(extractName, context, context
                                            .getArchive().getSdrfFile()
                                            .getName(), mustExist);
                        }

                        if (isValid) {
                            if (barcodeTumorValidator != null
                                    && !barcodeTumorValidator
                                    .barcodeIsValidForTumor(
                                            extractName, context
                                            .getArchive()
                                            .getTumorType())) {
                                isValid = false;
                                context.addError(MessageFormat
                                        .format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                                context.getArchive(),
                                                new StringBuilder()
                                                        .append("line ")
                                                        .append(row)
                                                        .append(": ")
                                                        .append("Barcode '")
                                                        .append(extractName)
                                                        .append("' does not belong to the disease set for tumor type '")
                                                        .append(context
                                                                .getArchive()
                                                                .getTumorType())
                                                        .append("'").toString()));
                            }
                        }
                    }
                }
            }
            row++;
        }

        final Map<String, Boolean> barcodeValidityMap = getBarcodeValidity(
                barcodesToValidate, uuidsToValidate, context);
        isValid = !barcodeValidityMap.containsValue(false) && isValid;

        return isValid;
    }

    protected boolean validateConvertedUUIDs(final String barcode, final int row,
                                             final String extractName, final QcContext context,
                                             final boolean mustExist) {
        boolean isValid = true;

        if ((extractName != null) && (!extractName.trim().startsWith("TCGA"))) {

            isValid = qcLiveBarcodeAndUUIDValidator.validateUuid(
                    extractName, context,
                    context.getArchive().getSdrfFile().getName(), mustExist);

            if (isValid && !this.qcLiveBarcodeAndUUIDValidator.isAliquotUUID(extractName)) {
                isValid = false;
                context.addError(MessageFormat
                        .format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                context.getArchive(),
                                new StringBuffer().append("line ")
                                        .append(row)
                                        .append(": Extract ")
                                        .append(extractName)
                                        .append(" found in Extract Name column is not")
                                        .append(" an aliquot UUID. Only aliquot UUIDs are allowed for this data type").toString()));
            }
            final String disease = context.getArchive().getTumorType();
            if (isValid && !qcLiveBarcodeAndUUIDValidator.isMatchingDiseaseForUUID(extractName, disease)) {
                isValid = false;
                context.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                        context.getArchive(),
                        new StringBuffer().append("line ")
                                .append(row)
                                .append(": The disease for UUID '")
                                .append(extractName)
                                .append("' found in ")
                                .append(EXTRACT_NAME)
                                .append(" column does not match the archive disease '")
                                .append(disease)
                                .append("'.").toString()));

            }
            // per APPS-6351 , make sure that barcode and UUID are consistent
            if (!context.isNoRemote()) {  // if we don't have at least webservices, can't do this call
                if (isValid && !qcLiveBarcodeAndUUIDValidator.validateUUIDBarcodeMapping(
                        extractName, barcode)) {
                    isValid = false;
                    context.addError(MessageFormat.format(
                            MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                            context.getArchive(),
                            new StringBuffer().append("line ")
                                    .append(row)
                                    .append(": The metadata for UUID '")
                                    .append(extractName)
                                    .append("' found in ")
                                    .append(EXTRACT_NAME)
                                    .append(" column and barcode '")
                                    .append(barcode)
                                    .append("' found in ")
                                    .append(BARCODE_COMMENT_COLUMN_NAME)
                                    .append(" column do not match").toString()));
                }
            } else {
                if (!qcLiveBarcodeAndUUIDValidator.validateAliquotBarcodeFormat(barcode)) {
                    isValid = false;
                    context.addError(MessageFormat.format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                            context.getArchive(),
                            new StringBuilder().append("line ").append(row).append(": barcode '").append(barcode).append("' found in ").
                                    append(BARCODE_COMMENT_COLUMN_NAME).append(" is not a valid aliquot barcode").toString()));
                }
            }

        } else {
            isValid = false;
            context.addError(MessageFormat
                    .format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                            context.getArchive(),
                            new StringBuffer().append("line ")
                                    .append(row)
                                    .append(": Barcode ")
                                    .append(extractName)
                                    .append(" found in Extract Name column. UUID must be used as aliquot identifiers").toString()));
        }
        return isValid;

    }

    /**
     * Return a {@link Map} holding the validity of individual barcodes given a
     * {@link List} of barcodes to validate.
     *
     * @param barcodes a {@link List} of barcodes to validate
     * @param uuids    uuids to validate
     * @param context  the qclive context
     * @return {@link Map} holding the validity of individual barcodes given a
     *         {@link List} of barcodes to validate
     */
    private Map<String, Boolean> getBarcodeValidity(
            final List<String> barcodes, final List<String> uuids,
            final QcContext context) {

        final Map<String, Boolean> result = new HashMap<String, Boolean>();

        if (context.isStandaloneValidator()) {
            final String fileName = context.getArchive().getSdrfFile()
                    .getName();

            if (barcodes.size() > 0) {
                result.putAll(qcLiveBarcodeAndUUIDValidator
                        .batchValidateReportIndividualResults(barcodes, context,
                                fileName, true));
            }
            if (uuids.size() > 0) {
                result.putAll(qcLiveBarcodeAndUUIDValidator
                        .batchValidateUUIDsReportIndividualResults(uuids, context,
                                fileName, true));
            }
        }
        return result;
    }

    /**
     * Checks if the row is a valid control row. By default always returns
     * false. Subclasses can override to allow control rows.
     *
     * @param context       the qc context
     * @param sdrfNavigator the sdrf
     * @param row           the row number
     * @param extractName   the extract name for this row
     * @return
     */
    protected boolean isValidControlRow(final QcContext context,
                                        final TabDelimitedContentNavigator sdrfNavigator, final int row,
                                        final String extractName) {
        // by default, no control rows
        return false;
    }

    /**
     * Validates the SDRF against the IDF. Checks that all Term Source REF
     * values in the SDRF have matching Term Source Name values in the IDF.
     *
     * @param archive       the archive
     * @param context       the qc context
     * @param sdrfNavigator the sdrf
     * @return whether validation against the IDF passed or not
     * @throws ProcessorException
     */
    private boolean validateWithIDF(final Archive archive,
                                    final QcContext context,
                                    final TabDelimitedContentNavigator sdrfNavigator)
            throws ProcessorException {
        boolean isValid = true;
        try {
            final IDF idf = AbstractIdfValidator.getIdf(archive);
            final List<String> termSourceNames = idf
                    .getAllIDFColValuesByColName(TERM_SOURCE_NAME);
            // make sure Term Source REF values in SDRF are only those in IDF
            final List<Integer> colNums = sdrfNavigator
                    .getHeaderIdsForName(TERM_SOURCE_REF);
            final List<String> termSourceRefs = new ArrayList<String>();
            for (final Integer col : colNums) {
                termSourceRefs.addAll(sdrfNavigator.getColumnValues(col));
            }
            termSourceRefs.removeAll(termSourceNames);
            while (termSourceRefs.contains("->")) {
                termSourceRefs.remove("->");
            }
            if (termSourceRefs.size() > 0) {
                context.addError(MessageFormat
                        .format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                context.getArchive(),
                                new StringBuilder()
                                        .append("Term Source REF values don't match Term Source Names in the IDF: ")
                                        .append(termSourceRefs).toString()));
                isValid = false;
            }
        } catch (IOException e) {
            archive.setDeployStatus(Archive.STATUS_INVALID);
            throw new ProcessorException(new StringBuilder()
                    .append("I/O error getting IDF: ").append(e.getMessage())
                    .toString());
        }
        return isValid;
    }

    protected boolean validateCommentColumns(final QcContext context,
                                             final TabDelimitedContentNavigator sdrfNavigator) {
        boolean isValid = true;
        Map<Integer, Integer> excludedRows = new HashMap<Integer, Integer>();
        for (int colNum = 0; colNum < sdrfNavigator.getHeaders().size(); colNum++) {
            final String header = sdrfNavigator.getHeaders().get(colNum);
            if (getFileColumnNames().contains(header)) {
                // found a file column, so look for comment columns after it
                final Map<String, Integer> commentColumns = getFileCommentColumns(
                        sdrfNavigator, colNum);
                // first check that all are there
                isValid = checkFileCommentColumns(context, commentColumns)
                        && isValid;
                // for each file column, for each row that has a non-blank value
                // for the file name, make sure the comment columns are
                // non-blank and formatted correctly
                final List<String> fileNames = getColumnValues(sdrfNavigator,
                        colNum);
                final List<String> dataLevels = getColumnValues(sdrfNavigator,
                        commentColumns.get(COMMENT_DATA_LEVEL));
                final List<String> includeValues = getColumnValues(
                        sdrfNavigator,
                        commentColumns.get(COMMENT_INCLUDE_FOR_ANALYSIS));
                final List<String> archiveNameValues = getColumnValues(
                        sdrfNavigator, commentColumns.get(COMMENT_ARCHIVE_NAME));
                if (fileNames != null) {
                    for (int index = 0; index < fileNames.size(); index++) {
                        String fileName = fileNames.get(index);
                        if (!fileName.equals("->")) {
                            if (excludedRows.get(index) != null) {
                                context.addError(MessageFormat
                                        .format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                                context.getArchive(),
                                                new StringBuilder()
                                                        .append("line '")
                                                        .append(index
                                                                + NUM_HEADERS
                                                                + ZERO_INDEX_OFFSET)
                                                        .append("' was marked for exclusion from analysis in column '")
                                                        .append(excludedRows
                                                                .get(index))
                                                        .append("' but column '")
                                                        .append(colNum)
                                                        .append("' contains a file name rather than '->'")
                                                        .toString()));
                                isValid = false;
                            } else {
                                String level = null;
                                String archiveLevel = null;
                                // check the data level value for this row/col
                                if (dataLevels != null) {
                                    level = dataLevels.get(index);
                                    Matcher matcher = LEVEL_PATTERN
                                            .matcher(level);
                                    if (!matcher.matches()) {
                                        context.addError(MessageFormat
                                                .format(MessagePropertyType.SDRF_LINE_VALUE_FORMAT_ERROR,
                                                        (index + NUM_HEADERS + ZERO_INDEX_OFFSET),
                                                        COMMENT_DATA_LEVEL,
                                                        "'Level N' where N is a valid level number",
                                                        level));
                                        isValid = false;
                                    } else {
                                        // make sure the level number is an
                                        // actual number
                                        try {
                                            Integer.valueOf(matcher.group(1));
                                        } catch (NumberFormatException ex) {
                                            context.addError(MessageFormat
                                                    .format(MessagePropertyType.LINE_VALUE_FORMAT_ERROR,
                                                            (index
                                                                    + NUM_HEADERS + ZERO_INDEX_OFFSET),
                                                            COMMENT_DATA_LEVEL,
                                                            "'Level N' where N is a valid level number",
                                                            level));
                                            isValid = false;
                                        }
                                        isValid = validateFileHeaderAndLevel(
                                                context, header, index, level)
                                                && isValid;
                                    }
                                }
                                if (includeValues != null) {
                                    String include = includeValues.get(index);
                                    if (!include.equalsIgnoreCase("yes")
                                            && !include.equalsIgnoreCase("no")) {
                                        context.addError(MessageFormat
                                                .format(MessagePropertyType.LINE_VALUE_FORMAT_ERROR,
                                                        (index + NUM_HEADERS + ZERO_INDEX_OFFSET),
                                                        COMMENT_INCLUDE_FOR_ANALYSIS,
                                                        "either 'yes' or 'no'",
                                                        include));
                                        isValid = false;
                                    } else if (include.equalsIgnoreCase("no")) {
                                        excludedRows.put(index, colNum);
                                    }
                                }
                                if (archiveNameValues != null) {
                                    String archiveName = archiveNameValues
                                            .get(index);
                                    Matcher archiveNameMatcher = ArchiveNameValidator.ARCHIVE_NAME_PATTERN
                                            .matcher(archiveName);
                                    if (!archiveNameMatcher.matches()) {
                                        context.addError(MessageFormat
                                                .format(MessagePropertyType.LINE_VALUE_ERROR,
                                                        (index + NUM_HEADERS + ZERO_INDEX_OFFSET),
                                                        COMMENT_ARCHIVE_NAME,
                                                        "must be a valid archive name",
                                                        archiveName));
                                        isValid = false;
                                    } else {
                                        archiveLevel = archiveNameMatcher
                                                .group(ArchiveNameValidator.INDEX_IN_ARCHIVE_NAME_ARCHIVE_TYPE);
                                    }
                                }
                                // if both level and archive name were found and
                                // valid, make sure the archive level matches
                                // the stated file level
                                if (level != null && archiveLevel != null) {
                                    String levelName = level.replace(' ', '_');
                                    if (!levelName.equals(archiveLevel)) {
                                        context.addError(MessageFormat
                                                .format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                                        context.getArchive(),
                                                        new StringBuilder()
                                                                .append("line ")
                                                                .append(index
                                                                        + NUM_HEADERS
                                                                        + ZERO_INDEX_OFFSET)
                                                                .append(": ")
                                                                .append(fileName)
                                                                .append(" was marked as '")
                                                                .append(level)
                                                                .append("' but it is within archive with level '")
                                                                .append(archiveLevel
                                                                        .replace(
                                                                                '_',
                                                                                ' '))
                                                                .append("'")
                                                                .toString()));
                                        isValid = false;
                                    }
                                }
                                // todo check data type value for the file/level
                                // QCL-400
                            }
                        }
                    }
                }
            }
        }

        return isValid;
    }

    /**
     * Checks for required comment columns in the SDRF for a given file column.
     * Will add errors to context for any missing. Note: comment columns must
     * immediately follow a file column.
     *
     * @param context        the qc context
     * @param commentColumns a map of the comment columns where key is name and value is
     *                       column index
     * @return if all required comment columns are present or not
     */
    public boolean checkFileCommentColumns(final QcContext context,
                                           final Map<String, Integer> commentColumns) {
        boolean isValid = true;
        for (final String requiredComment : getRequiredCommentColumns()) {
            if (commentColumns.get(requiredComment) == null) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_SDRF_COLUMN_ERROR,
                        requiredComment));
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * Checks for required comment columns in the SDRF for a given file column.
     * Will add errors to context for any missing. Note: comment columns must
     * immediately follow a file column. This static method is used in other
     * classes like CgccExperimentValidator
     *
     * @param context        the qc context
     * @param colNum         the column number of the file column
     * @param header         the file column header name
     * @param commentColumns a map of the comment columns where key is name and value is
     *                       column index
     * @return if all required comment columns are present or not
     */
    public static boolean checkFileCommentColumns(final QcContext context,
                                                  final int colNum, final String header,
                                                  final Map<String, Integer> commentColumns) {
        boolean isValid = true;
        for (final String requiredComment : REQUIRED_COMMENT_COLUMNS) {
            if (commentColumns.get(requiredComment) == null) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_SDRF_COLUMN_ERROR,
                        requiredComment));
                isValid = false;
            }
        }
        return isValid;
    }

    /*
      * Adds a warning message if the SDRF file specifies that a extract name
      * (barcode or uuid) is present in multiple archive files
      */
    protected void checkExtractNamePerArchive(final QcContext context,
                                              final TabDelimitedContentNavigator sdrfNavigator) {

        // Map of extractName( Barcode or UUID)##level Vs archive name
        Map<String, String> extractNameArchiveMap = new HashMap<String, String>();
        Boolean extractNameIsUUID = context.isCenterConvertedToUUID();

        for (int colNum = 0; colNum < sdrfNavigator.getHeaders().size(); colNum++) {

            final String header = sdrfNavigator.getHeaders().get(colNum);
            if (getFileColumnNames().contains(header)) {
                // get the archive name and level columns
                final Map<String, Integer> commentColumns = getFileCommentColumns(
                        sdrfNavigator, colNum);
                final List<String> archiveNameValues = getColumnValues(
                        sdrfNavigator, commentColumns.get(COMMENT_ARCHIVE_NAME));

                final List<String> levelColumn = getColumnValues(sdrfNavigator,
                        commentColumns.get(COMMENT_DATA_LEVEL));

                // get the list of barcodes
                final List<String> extractNames = getColumnValues(
                        sdrfNavigator,
                        sdrfNavigator
                                .getHeaderIDByName(EXTRACT_NAME_COLUMN_NAME));

                String extractName;
                String archiveName;
                String hashMapKey;

                boolean nullValues = (archiveNameValues == null)
                        || (levelColumn == null) || (extractNames == null);

                if (!nullValues) {
                    for (int rowIndex = 0, maxCount = extractNames.size(); rowIndex < maxCount; rowIndex++) {

                        extractName = extractNames.get(rowIndex);
                        archiveName = archiveNameValues.get(rowIndex);
                        hashMapKey = getHashMapKey(extractName,
                                levelColumn.get(rowIndex));

                        if (!extractNameArchiveMap.containsKey(hashMapKey)) {
                            extractNameArchiveMap.put(hashMapKey, archiveName);
                        } else {
                            // extractName and level are same
                            String archiveNameFromHashMap = extractNameArchiveMap
                                    .get(hashMapKey);
                            if (!(archiveName
                                    .equalsIgnoreCase(archiveNameFromHashMap))) {
                                // check if both archives have same serial
                                // numbers, if yes, they should be allowed, so
                                // no warning
                                if (!checkIfEverythingUntilSerialIndexIsSame(
                                        archiveName, archiveNameFromHashMap)) {
                                    // only add warning if the extract name is
                                    // an actual barcode, not a control
                                    if ((!extractNameIsUUID && QcLiveBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN
                                            .matcher(extractName).matches())
                                            || (extractNameIsUUID && qcLiveBarcodeAndUUIDValidator.validateUUIDFormat(extractName))) {
                                        context.addWarning("Extract name "
                                                + extractName
                                                + " is included in both "
                                                + archiveNameFromHashMap
                                                + " and " + archiveName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*
      * Used by checkBarcodePerArchive
      */
    private String getHashMapKey(final String barcode, final String level) {
        return barcode + "##" + level + "##";
    }

    private boolean checkIfEverythingUntilSerialIndexIsSame(
            final String firstArchive, final String secondArchive) {

        boolean returnValue = true;

        final Matcher matcherFirstArchive = ArchiveNameValidator.ARCHIVE_NAME_PATTERN
                .matcher(firstArchive);
        final Matcher matcherSecondArchive = ArchiveNameValidator.ARCHIVE_NAME_PATTERN
                .matcher(secondArchive);

        if (matcherFirstArchive.matches() && matcherSecondArchive.matches()) {
            for (int matcherIndex = 1; matcherIndex < ArchiveNameValidator.INDEX_IN_ARCHIVE_NAME_REVISION; matcherIndex++) {

                if (!((matcherFirstArchive.group(matcherIndex))
                        .equals(matcherSecondArchive.group(matcherIndex)))) {
                    returnValue = false;
                    break;
                }
            }
        }
        return returnValue;
    }

    /**
     * Checks whether or not the SDRF file content rows have the same number of
     * tokens as in the header
     *
     * @param sdrf      TabDelimitedContent object containing valid sdrf values
     * @param qcContext the qcLive context
     * @return True if the sdrf contains the the same number of tokens as in the
     *         header , false otherwise
     */
    protected Boolean validateHeaderTokenCount(final TabDelimitedContent sdrf,
                                               final QcContext qcContext) {
        Boolean isValid = true;
        int numOfHeaders = 0;

        if (sdrf != null && sdrf.getTabDelimitedHeaderValues() != null
                && sdrf.getTabDelimitedHeaderValues().length > 0) {

            numOfHeaders = sdrf.getTabDelimitedHeaderValues().length;
            // check if there are content rows,
            if (sdrf.getTabDelimitedContents().size() >= 2) {
                // skip the header
                for (int i = 1; i < sdrf.getTabDelimitedContents().size(); i++) {
                    if (sdrf.getTabDelimitedContents().get(i).length == numOfHeaders) {
                        isValid = isValid && true;
                    } else {
                        isValid = false;
                        qcContext
                                .addError(MessageFormat
                                        .format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                                qcContext.getArchive(),
                                                " A row  "
                                                        + i
                                                        + " in the SDRF file contains a number of tokens "
                                                        + "different than the number of headers in the file"));
                    }
                }

            } else {
                throw new IllegalArgumentException(
                        "TabDelimitedContentNavigator must contain at least one content row and a header");
            }
        } else {
            throw new IllegalArgumentException(
                    " Unable to get a list of headers from TabDelimitedContentNavigator object"
                            + " check your input.");
        }
        return isValid;
    }

    public void setBarcodeTumorValidator(
            final BarcodeTumorValidator barcodeTumorValidator) {
        this.barcodeTumorValidator = barcodeTumorValidator;
    }

    public void setQcLiveBarcodeAndUUIDValidator(
            final QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator) {
        this.qcLiveBarcodeAndUUIDValidator = qcLiveBarcodeAndUUIDValidator;
    }

    public QcLiveBarcodeAndUUIDValidator getQcLiveBarcodeAndUUIDValidator() {
        return qcLiveBarcodeAndUUIDValidator;
    }

    public List<String> getFileColumnNames() {
        return FILE_COLUMN_NAMES;
    }

    public List<String> getRequiredCommentColumns() {
        return REQUIRED_COMMENT_COLUMNS;
    }
}
