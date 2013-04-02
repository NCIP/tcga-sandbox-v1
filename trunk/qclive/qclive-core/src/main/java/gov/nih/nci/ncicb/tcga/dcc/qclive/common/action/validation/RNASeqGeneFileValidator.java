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
 * Implementation of RNASeqDataFileValidator that validates gene files.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RNASeqGeneFileValidator extends RNASeqDataFileValidator {
    private static final String GENE_HEADER = "gene";
    private static final List<String> EXPECTED_COLUMNS = Arrays.asList(GENE_HEADER, RAW_COUNTS_HEADER, MEDIAN_LENGTH_NORMALIZED_HEADER, RPKM_HEADER);
    private static final Pattern GENE_VALUE_PATTERN = Pattern.compile("(.+)\\|(.+)");
    private static final Pattern GENE_NAME_PATTERN = Pattern.compile("([a-zA-Z0-9_\\-\\.]+|\\?)");
    private static final Pattern GENE_ID_PATTERN = Pattern.compile("[0-9]+");
    public static final String GENE_FILE_EXTENSION = "gene.quantification.txt";

    @Override
    protected String getFileExtension() {
        return GENE_FILE_EXTENSION;
    }

    @Override
    protected List<String> getExpectedColumns() {
        return EXPECTED_COLUMNS;
    }

    /**
     * Checks for gene-file specific columns (gene column).  Others are delegated to parent class method.
     * @param value the value
     * @param headerName the header
     * @param context the qc context
     * @param rowNum the row of the file
     * @return if the value is valid
     */
    @Override
    protected boolean valueIsValid(final String value, final String headerName, final QcContext context, final int rowNum) {
        boolean isValid = true;
        if (headerName.equals(GENE_HEADER)) {
            // do nothing, no validation for gene column
        } else {
            isValid = super.valueIsValid(value, headerName, context, rowNum);
        }
        return isValid;
    }

    @Override
    public String getName() {
        return "RNASeq gene file validation";
    }
}
