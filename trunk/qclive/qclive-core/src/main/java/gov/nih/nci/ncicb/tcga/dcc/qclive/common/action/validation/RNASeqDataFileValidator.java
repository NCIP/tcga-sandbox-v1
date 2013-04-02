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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtils;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parent validator for RNASeq level 3 data files.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class RNASeqDataFileValidator extends AbstractSeqDataFileValidator {

    public static final String RNASEQ = "RNASeq";
    protected static final String RAW_COUNTS_HEADER = "raw_counts";
    protected static final String MEDIAN_LENGTH_NORMALIZED_HEADER = "median_length_normalized";
    protected static final String RPKM_HEADER = "RPKM";
    private static final String PLUS_STRAND = "+";
    private static final String MINUS_STRAND = "-";

    public ChromInfoUtils chromInfoUtils;

    /**
     * Checks if any results from each file failed.
     * @param results the results of each processFile call.  Map key = File and value = return from processFile
     * @param context the qc context
     * @return true if all files passed, false if any failed
     */
    @Override
    protected Boolean getReturnValue(final Map<File, Boolean> results, final QcContext context) {
        return !results.containsValue(false);
    }

    /**
     * Validates a data value for a given header.
     * @param value the value
     * @param headerName the header
     * @param context the qc context
     * @param rowNum the row of the file
     * @return if the value is valid or not
     */
    protected boolean valueIsValid(final String value, final String headerName, final QcContext context, final int rowNum) {
        boolean isValid = true;
        if (RAW_COUNTS_HEADER.equals(headerName)) {
            try {
                Float floatValue = Float.parseFloat(value);
                if (floatValue < 0.0) {
                    addErrorMessage(context, rowNum, "'" + RAW_COUNTS_HEADER + "' cannot be negative");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                addErrorMessage(context, rowNum, "'" + RAW_COUNTS_HEADER + "' value must be a floating point number");
                isValid = false;
            }
        } else if (MEDIAN_LENGTH_NORMALIZED_HEADER.equals(headerName)) {
            try {
                final Float floatValue = Float.parseFloat(value);
                if (floatValue < 0) {
                    addErrorMessage(context, rowNum, "'" + MEDIAN_LENGTH_NORMALIZED_HEADER + "' cannot be negative");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                addErrorMessage(context, rowNum, "'" + MEDIAN_LENGTH_NORMALIZED_HEADER + "' value must be a number");
                isValid = false;
            }
        } else if (RPKM_HEADER.equals(headerName)) {
            try {
                final Float rpkmValue = Float.parseFloat(value);
                if (rpkmValue < 0) {
                    addErrorMessage(context, rowNum, "'" + RPKM_HEADER + "' value cannot be negative");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                addErrorMessage(context, rowNum, "'" + RPKM_HEADER + "' value must be a number");
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * Validates a chromosome value.  Is expected to have format "chrN" where N is 1-22, X, Y, or M.
     * @param chromosomeValue the value of the chromosome field
     * @param context the qc context
     * @param rowNum the line of the file
     * @return if the chromosome is valid
     */
    protected boolean validateChromosome(final String chromosomeValue, final QcContext context, final int rowNum) {

        if (!getChromInfoUtils().isValidChromValue(chromosomeValue)) {
            addErrorMessage(context, rowNum, "chromosome value '"+chromosomeValue+"' is not valid");
            return false;
        }
        return true;
    }

    /**
     * Validates a coordinate.  Must be a positive integer.
     * @param coordinateValue the coordinate
     * @param context the context
     * @param rowNum the file line num
     * @return if the coordinate is valid
     */
    protected boolean validateCoordinate(final String coordinateValue, final QcContext context, final int rowNum) {
        // must be a positive integer
        try {
            final Integer coordinate = Integer.valueOf(coordinateValue);
            if (coordinate < 1) {
                addErrorMessage(context, rowNum, "coordinate value '" + coordinateValue + "' is not valid, must be a positive integer");
                return false;
            }
        } catch (NumberFormatException e) {
            addErrorMessage(context, rowNum, "coordinate value '" + coordinateValue + "' is not valid, must be a positive integer");
            return false;
        }
        return true;
    }

    /**
     * Validates a strand.  Must be + or -.
     * @param strandValue the strand
     * @param context the context
     * @param rowNum the file row num
     * @return if the strand is valid
     */
    protected boolean validateStrand(final String strandValue, final QcContext context, final int rowNum) {
        if (!PLUS_STRAND.equals(strandValue) && !MINUS_STRAND.equals(strandValue)) {
            addErrorMessage(context, rowNum, "strand value '" + strandValue + "' is not valid, must be '" + PLUS_STRAND + "' or '" + MINUS_STRAND + "'");
            return false;
        } else {
            return true;
        }
    }

    protected void addWarningMessage(final QcContext context, final Integer row, final String message) {
        context.addWarning(context.getFile().getName() + (row != null ? " line " + row : "") + ": " + message);
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
                && !archive.getPlatform().contains(MiRNASeqDataFileValidator.MIRNASEQ)
                && archive.getPlatform().contains(RNASEQ);
    }

    public ChromInfoUtils getChromInfoUtils() {
        return chromInfoUtils;
    }

    public void setChromInfoUtils(ChromInfoUtils chromInfoUtils) {
        this.chromInfoUtils = chromInfoUtils;
    }
}
