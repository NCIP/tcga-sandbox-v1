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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * miRNASeq isoform file validator
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class MiRNASeqIsoformFileValidator extends MiRNASeqDataFileValidator {

    public static final String ISOFORM_FILE_EXTENSION = "isoform.quantification.txt";

    /**
     * Headers specific to miRNA isoform files
     */
    protected static final String ISOFORM_COORDS = "isoform_coords";
    protected static final String MIRNA_REGION = "miRNA_region";

    private static final List<String> EXPECTED_COLUMNS = Arrays.asList(MIRNA_ID, ISOFORM_COORDS, READ_COUNT, READS_PER_MILLION_MIRNA_MAPPED, CROSS_MAPPED, MIRNA_REGION);

    /**
     * Regexp to validate column's values
     */
    private final String ANNOTATION_WORD_REGEXP = "^mature|star|stemloop|precursor|unannotated$";
    private final Pattern ANNOTATION_WORD_PATTERN = Pattern.compile(ANNOTATION_WORD_REGEXP);

    private final String MIMAT_ACC_NUMBER_REGEXP = "^MIMAT[0-9]+$";
    private final Pattern MIMAT_ACC_NUMBER_PATTERN = Pattern.compile(MIMAT_ACC_NUMBER_REGEXP);

    private final String WORD_REGEXP = "^[a-zA-Z0-9\\._-]+$";
    private final Pattern WORD_PATTERN = Pattern.compile(WORD_REGEXP);

    private final String STRAND_REGEXP = "^\\+|-$";
    private final Pattern STRAND_PATTERN = Pattern.compile(STRAND_REGEXP);

    private final String NON_ZERO_POSITIVE_INT_REGEXP = "[1-9]+[0-9]*";
    private final String START_END_COORD_REGEXP = "^(" + NON_ZERO_POSITIVE_INT_REGEXP + ")-(" + NON_ZERO_POSITIVE_INT_REGEXP + ")$";
    private final Pattern START_END_COORD_PATTERN = Pattern.compile(START_END_COORD_REGEXP);

    @Override
    protected String getFileExtension() {
        return ISOFORM_FILE_EXTENSION;
    }

    @Override
    public String getName() {
        return MIRNASEQ + " isoform file validation";
    }

    /**
     * Should return the expected columns for the file, in order expected.
     *
     * @return expected column names
     */
    @Override
    protected List<String> getExpectedColumns() {
        return EXPECTED_COLUMNS;
    }

    /**
     * Validates a data value for a given header.
     *
     * @param value      the value
     * @param headerName the header
     * @param context    the qc context
     * @param rowNum     the row of the file
     * @return if the value is valid or not
     */
    @Override
    protected boolean valueIsValid(String value, String headerName, QcContext context, int rowNum) {

        boolean isValid;

        if(ISOFORM_COORDS.equals(headerName)) {
            isValid = validateIsoformCoords(value, context, rowNum);
        } else if(MIRNA_REGION.equals(headerName)) {
            isValid = validateMirnaRegion(value, context, rowNum);
        } else {
            isValid = super.valueIsValid(value, headerName, context, rowNum);
        }

        return isValid;
    }

    /**
     * validate the mirna_region field
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a valid mirna_region field
     */
    private boolean validateMirnaRegion(final String value, final QcContext context, final int rowNum) {

        boolean isValid;

        final String mirnaRegionSeparator = ",";
        final String[] valueFieldArray = value.split(mirnaRegionSeparator);

        int valueFieldArrayLength = valueFieldArray.length;

        if(valueFieldArrayLength == 2) {

            final String annotationWord = valueFieldArray[0];
            final String mimatAccNumber = valueFieldArray[1];

            isValid = validateAnnotationWord(annotationWord, context, rowNum);
            isValid = validateMimatAccNumber(mimatAccNumber, context, rowNum) && isValid;

        } else if(valueFieldArrayLength == 1) {
            final String annotationWord = valueFieldArray[0];
            isValid = validateAnnotationWord(annotationWord, context, rowNum);
            
        } else {
            addErrorMessage(context, rowNum, "'" + MIRNA_REGION + "' is invalid (It is made of " + valueFieldArrayLength + " fields, but a maximum of 2 is expected)");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Validate a mimat_acc_number value
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a valid mimat_acc_number
     */
    private boolean validateMimatAccNumber(final String value, final QcContext context, final int rowNum) {

        boolean isValid = MIMAT_ACC_NUMBER_PATTERN.matcher(value).matches();

        if(!isValid) {
            addErrorMessage(context, rowNum, "Invalid 'MIMAT ACC Number': " + value);
        }

        return isValid;
    }

    /**
     * Validate a annotation word value
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a valid annotation word
     */
    private boolean validateAnnotationWord(final String value, final QcContext context, final int rowNum) {

        boolean isValid = ANNOTATION_WORD_PATTERN.matcher(value).matches();

        if(!isValid) {
            addErrorMessage(context, rowNum, "Invalid Annotation Word: " + value);
        }

        return isValid;
    }

    /**
     * Validate Isoform Coords
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a valid Isoform Coords
     */
    private boolean validateIsoformCoords(final String value, final QcContext context, final int rowNum) {

        boolean isValid;

        final String isoformCoordsSeparator = ":";
        final int expectedIsoformCoordsFieldNumber = 4;

        final String[] valueFieldArray = value.split(isoformCoordsSeparator);
        int valueFieldArrayLength = valueFieldArray.length;

        if(valueFieldArrayLength == expectedIsoformCoordsFieldNumber) {

            final String genomeBuild = valueFieldArray[0];
            final String chromosomeId = valueFieldArray[1];
            final String startEndCoord = valueFieldArray[2];
            final String strand = valueFieldArray[3];

            isValid = validateGenomeBuild(genomeBuild, context, rowNum);
            isValid = validateChromosomeId(chromosomeId, context, rowNum) && isValid;
            isValid = validateStartEndCoord(startEndCoord, context, rowNum) && isValid;
            isValid = validateStrand(strand, context, rowNum) && isValid;

        } else {
            addErrorMessage(context, rowNum, "Invalid Isoform coords  (It is made of " + valueFieldArrayLength + " fields, but expected " + expectedIsoformCoordsFieldNumber + ")");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Validate a genome build
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a valid genome build
     */
    private boolean validateGenomeBuild(final String value, final QcContext context, final int rowNum) {

        boolean isValid = validateWord(value);

        if(!isValid) {
            addErrorMessage(context, rowNum, "Invalid Genome build value: " + value);
        }

        return isValid;
    }

    /**
     * Validate a chromosome Id
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a valid chromosome Id
     */
    private boolean validateChromosomeId(final String value, final QcContext context, final int rowNum) {

        boolean isValid = validateWord(value);

        if(!isValid) {
            addErrorMessage(context, rowNum, "Invalid Chromosome Id value: " + value);
        }

        return isValid;
    }

    /**
     * Validate a word
     *
     * @param value the value to validate
     * @return <code>true</code> if the value is a valid word
     */
    private boolean validateWord(final String value) {
        return WORD_PATTERN.matcher(value).matches();
    }

    /**
     * Validate a strand
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a valid strand
     */
    private boolean validateStrand(final String value, final QcContext context, final int rowNum) {

        boolean isValid = STRAND_PATTERN.matcher(value).matches();

        if(!isValid) {
            addErrorMessage(context, rowNum, "Invalid Strand value: " + value);
        }

        return isValid;
    }

    /**
     * Validate a start-end coord
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a valid start-end coord
     */
    private boolean validateStartEndCoord(final String value, final QcContext context, final int rowNum) {

        boolean isValid = true;
        Matcher matcher = START_END_COORD_PATTERN.matcher(value);

        if(matcher.find()) {
            
            final String start = matcher.group(1);
            final String end = matcher.group(2);

            final Integer startInt = Integer.parseInt(start);
            final Integer endInt = Integer.parseInt(end);

            if(startInt > endInt) {
                addErrorMessage(context, rowNum, "Invalid Start-End Coord value (Start coord > End coord: " + start + ">" + end + "): " + value);
                isValid = false;
            }

        } else {
            addErrorMessage(context, rowNum, "Invalid start-end Coord value: " + value);
            isValid = false;
        }

        return isValid;
    }
}
