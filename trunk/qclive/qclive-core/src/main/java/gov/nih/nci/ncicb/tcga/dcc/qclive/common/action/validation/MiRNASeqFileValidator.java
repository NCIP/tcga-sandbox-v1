/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import java.util.Arrays;
import java.util.List;

/**
 * miRNASeq file validator
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class MiRNASeqFileValidator extends MiRNASeqDataFileValidator {

    public static final String MIRNA_FILE_EXTENSION = "mirna.quantification.txt";

    private static final List<String> EXPECTED_COLUMNS = Arrays.asList(MIRNA_ID, READ_COUNT, READS_PER_MILLION_MIRNA_MAPPED, CROSS_MAPPED);

    @Override
    protected String getFileExtension() {
        return MIRNA_FILE_EXTENSION;
    }

    @Override
    public String getName() {
        return MIRNASEQ + " file validation";
    }

    @Override
    protected List<String> getExpectedColumns() {
        return EXPECTED_COLUMNS;
    }
}
