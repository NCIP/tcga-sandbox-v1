/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
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
 * Implementation of RNASeqDataFileValidator that validates splice junction files.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RNASeqJunctionFileValidator extends RNASeqDataFileValidator {
    private static final Pattern BASIC_JUNCTION_PATTERN = Pattern.compile("(.+):(.+):(.+),(.+):(.+):(.+)");
    private static final String JUNCTION_HEADER_NAME = "junction";
    private static final List<String> EXPECTED_COLUMNS = Arrays.asList(JUNCTION_HEADER_NAME, "raw_counts");
    public static final String JUNCTION_FILE_EXTENSION = "*.spljxn.quantification.txt,*.junction_quantification.txt";

    @Override
    protected List<String> getExpectedColumns() {
        return EXPECTED_COLUMNS;
    }

    @Override
    protected String getFileExtension() {
        return JUNCTION_FILE_EXTENSION;
    }

    /**
     * Checks splice-junction-specific columns, passes others to superclass method.
     * @param value the value
     * @param headerName the header
     * @param context the qc context
     * @param rowNum the row of the file
     * @return if the value is valid
     */
    protected boolean valueIsValid(final String value, final String headerName, final QcContext context, final int rowNum) {
        boolean isValid = true;
        if (headerName.equals(JUNCTION_HEADER_NAME)) {
            Matcher junctionMatcher = BASIC_JUNCTION_PATTERN.matcher(value);
            if (junctionMatcher.matches()) {
                String chrom1 = junctionMatcher.group(1);
                String chrom2 = junctionMatcher.group(4);
                if (!chrom1.toLowerCase().equals(chrom2.toLowerCase())) {
                    // just a warning for this
                    addWarningMessage(context, rowNum, "both points are not on the same chromosome for '" + JUNCTION_HEADER_NAME + "' value");                    
                }
                isValid = validateChromosome(chrom1, context, rowNum) && isValid;
                isValid = validateCoordinate(junctionMatcher.group(2), context, rowNum) && isValid;
                isValid = validateStrand(junctionMatcher.group(3), context, rowNum) && isValid;
                isValid = validateChromosome(chrom2, context, rowNum) && isValid;
                isValid = validateCoordinate(junctionMatcher.group(5), context, rowNum) && isValid;
                isValid = validateStrand(junctionMatcher.group(6), context, rowNum) && isValid;
            } else {
                addErrorMessage(context, rowNum, "value for '" + JUNCTION_HEADER_NAME + "' must have format 'chr{chrom}:{coord}:{strand},chr{chrom}:{coord}:{strand}");
                isValid = false;
            }
        } else {
            isValid = super.valueIsValid(value, headerName, context, rowNum);
        }
        return isValid;
    }

    @Override
    public String getName() {
        return "RNASeq splice junction file validation";
    }
}
