/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parent validator for miRNASeq level 3 data files
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class MiRNASeqDataFileValidator extends AbstractSeqDataFileValidator {

    public static final String MIRNASEQ = "miRNASeq";

    /**
     * Headers common to miRNA files
     */
    protected static final String MIRNA_ID = "miRNA_ID";
    protected static final String READ_COUNT = "read_count";
    protected static final String READS_PER_MILLION_MIRNA_MAPPED = "reads_per_million_miRNA_mapped";
    protected static final String CROSS_MAPPED = "cross-mapped";

    /**
     * Regexp to validate column's values
     */
    private static String MIRNA_ID_REGEXP = "^[a-zA-Z0-9\\._-]+$";
    private static String CROSS_MAPPED_REGEXP = "^[YN]$";

    private static Pattern MIRNA_ID_PATTERN = Pattern.compile(MIRNA_ID_REGEXP);
    private static Pattern CROSS_MAPPED_PATTERN = Pattern.compile(CROSS_MAPPED_REGEXP);

    @Override
    protected Boolean getReturnValue(final Map<File, Boolean> results, final QcContext context) {
        return !results.containsValue(false);
    }

    @Override
    protected Boolean getDefaultReturnValue(final Archive archive) {
        return true;
    }

    @Override
    protected boolean isCorrectArchiveType(final Archive archive) throws ProcessorException {

        return Experiment.TYPE_CGCC.equals(archive.getExperimentType())
                && Archive.TYPE_LEVEL_3.equals(archive.getArchiveType())
                && archive.getPlatform() != null // just in case
                && archive.getPlatform().contains(MiRNASeqDataFileValidator.MIRNASEQ);
    }

    @Override
    protected boolean valueIsValid(final String value, final String headerName, final QcContext context, final int rowNum) {

        boolean isValid = true;

        if(MIRNA_ID.equals(headerName)) {
            isValid = validateMirnaId(value, context, rowNum);
        } else if(READ_COUNT.equals(headerName)) {
            isValid = validateReadCount(value, context, rowNum);
        } else if(READS_PER_MILLION_MIRNA_MAPPED.equals(headerName)) {
            isValid = validateReadsPerMillionMirnaMapped(value, context, rowNum);
        } else if(CROSS_MAPPED.equals(headerName)) {
            isValid = validateCrossMapped(value, context, rowNum);
        }

        return isValid;
    }

    /**
     * Validate mirna_id value
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is valid
     */
    private boolean validateMirnaId(final String value, final QcContext context, final int rowNum) {

        boolean isValid = MIRNA_ID_PATTERN.matcher(value).matches();

        if(!isValid) {
            addErrorMessage(context, rowNum, "Invalid '" + MIRNA_ID + "' value: " + value);
        }

        return isValid;
    }

    /**
     * Validate cross_mapped value
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is valid
     */
    private boolean validateCrossMapped(final String value, final QcContext context, final int rowNum) {

        boolean isValid = CROSS_MAPPED_PATTERN.matcher(value).matches();

        if(!isValid) {
            addErrorMessage(context, rowNum, "'" + CROSS_MAPPED + "' value '" + value + "' must be 'Y' or 'N'");
        }

        return isValid;
    }

    /**
     * Validate read_count value (positive integer or float)
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a positive integer or float
     */
    private boolean validateReadCount(final String value, final QcContext context, final int rowNum) {
        return validatePositiveIntegerOrFloat(value, context, rowNum);
    }

    /**
     * Validate reads_per_million_mapped value (positive integer or float)
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a positive integer or float
     */
    private boolean validateReadsPerMillionMirnaMapped(final String value, final QcContext context, final int rowNum) {
        return validatePositiveIntegerOrFloat(value, context, rowNum);
    }

    /**
     * Validate a positive integer or float
     *
     * @param value the value to validate
     * @param context the context
     * @param rowNum the number of the row on which the value comes from
     * @return <code>true</code> if the value is a positive integer or float
     */
    private boolean validatePositiveIntegerOrFloat(final String value, final QcContext context, int rowNum) {

        boolean isValid = true;

        try {
            final Float floatValue = Float.parseFloat(value);
            if (floatValue < 0.0) {
                addErrorMessage(context, rowNum, "'" + READ_COUNT + "' value '" + value + "' cannot be negative");
                isValid = false;
            }

        } catch (final NumberFormatException e) {
            addErrorMessage(context, rowNum, "'" + READ_COUNT + "' value '" + value + "' must be an integer or floating point number");
            isValid = false;
        }

        return isValid;
    }
}
