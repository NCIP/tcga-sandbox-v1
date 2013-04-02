/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;

import java.util.List;

/**
 * Level 3 DAO for the cna_value table
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3CNA_SNP extends DAMQueriesLevel3 {

    public static final String CNA_VALUE = "cna_value";
    
    private static final String HEADER = "barcode\tchromosome\tstart\tstop\tnum.mark\tseg.mean\n";
    private static final String ROW_SIZE_SQL = "select sum(avg_col_len) from avg_col_len where table_name = 'CNA_VALUE' " +
            "and column_name in ('CHROMOSOME', 'CHR_START', 'CHR_STOP', 'NUM_MARK', 'SEG_MEAN')";
    private static final String[] VALUE_ATTRIBUTES = new String[]{"bestbarcode", "chromosome", "chr_start",
            "chr_stop", "num_mark", "seg_mean"};
    private static final String[] ORDERBY_COLUMNS = new String[]{"chromosome", "chr_start", "chr_stop"};

    protected String getValueTable() {
        return CNA_VALUE;
    }

    protected List<String> getAllowedDatatypes() {
        return getDamUtils().getLevel3AllowedDataTypes(DAMUtils.CNA_SNP);
    }

    protected String getRowSizeQuery(final DataFileLevelTwoThree dataFile) {
        return ROW_SIZE_SQL;
    }

    protected String[] getValueColumnNames(DataFileLevelTwoThree dataFile) {
        return VALUE_ATTRIBUTES;
    }

    protected String getFileHeader(final DataFileLevelTwoThree dataFile) {
        return HEADER;
    }

    protected String[] getValueQueryOrderByColumns() {
        return ORDERBY_COLUMNS;
    }

}//End of Class
