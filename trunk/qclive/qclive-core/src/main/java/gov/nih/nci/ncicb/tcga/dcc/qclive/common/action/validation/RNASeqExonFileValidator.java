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

/**
 * Validator for RNASeq exon data files.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RNASeqExonFileValidator extends RNASeqDataFileValidator {
    protected static final String EXON_HEADER = "exon";
    private static final List<String> EXPECTED_COLUMNS = Arrays.asList(EXON_HEADER, RAW_COUNTS_HEADER, MEDIAN_LENGTH_NORMALIZED_HEADER, RPKM_HEADER);
    private static final String EXON_FORMAT_ERROR = "value for '" + EXON_HEADER + "' must have format 'chr{chromNum}:{startCoord}-{endCoord}:{strand}";
    public static final String EXON_FILE_EXTENSION = "*.exon.quantification.txt,*.exon_quantification.txt";

    @Override
    protected List<String> getExpectedColumns() {
        return EXPECTED_COLUMNS;
    }

    @Override
    protected String getFileExtension() {
        return EXON_FILE_EXTENSION;
    }

    /**
     * Checks for exon-specific columns.  Defers to superclass if a general column.
     * @param value the value
     * @param headerName the header
     * @param context the qc context
     * @param rowNum the row of the file
     * @return if the value is valid
     */
    @Override
    protected boolean valueIsValid(final String value, final String headerName, final QcContext context, final int rowNum) {
        if (headerName.equals(EXON_HEADER)) {
            // need to check the format of all the parts
            String[] exonParts = value.split(":");
            if (exonParts.length != 3) {
                addErrorMessage(context, rowNum, EXON_FORMAT_ERROR);
                return false;
            }

            String[] coordinates = exonParts[1].split("-");
            if (coordinates.length != 2) {
                addErrorMessage(context, rowNum, EXON_FORMAT_ERROR);
                return false;
            }
            return validateChromosome(exonParts[0], context, rowNum) && validateCoordinate(coordinates[0], context, rowNum) &&
                    validateCoordinate(coordinates[1], context, rowNum) && validateStrand(exonParts[2], context, rowNum);

        } else {
            return super.valueIsValid(value, headerName, context, rowNum);
        }
    }

    @Override
    public String getName() {
        return "RNASeq exon file validation";
    }
}
