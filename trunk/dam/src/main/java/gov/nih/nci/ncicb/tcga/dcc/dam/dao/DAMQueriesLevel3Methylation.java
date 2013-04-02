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
 * Level 3 DAO for methylation_value table
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3Methylation  extends DAMQueriesLevel3 {

    public static final String METHYLATION_VALUE = "methylation_value";
    
    private static final String[] VALUE_ATTRIBUTES = new String[]{"bestbarcode", "probe_name", "beta_value", "entrez_gene_symbol", "chromosome", "chr_position" };
    private static final String[] ORDERBY_COLUMNS = new String[]{"entrez_gene_symbol", "chromosome", "chr_position" };
    private static final String ROW_SIZE_SQL = "select sum(avg_col_len) from avg_col_len where table_name = 'METHYLATION_VALUE' and column_name in ('CHROMOSOME', 'CHR_POSITION', 'BETA_VALUE')";
    private static final String HEADER = "barcode\tprobe name\tbeta value\tgene symbol\tchromosome\tposition\n";

    protected String getValueTable() {
        return METHYLATION_VALUE;
    }

    protected List<String> getAllowedDatatypes() {
        return getDamUtils().getLevel3AllowedDataTypes(DAMUtils.METHYLATION);
    }

    protected String getRowSizeQuery(final DataFileLevelTwoThree dataFile) {
        return ROW_SIZE_SQL;
    }

    protected String[] getValueColumnNames(final DataFileLevelTwoThree dataFile) {
        return VALUE_ATTRIBUTES;
    }

    protected String getFileHeader( final DataFileLevelTwoThree dataFile ) {
        return HEADER;
    }

    protected String[] getValueQueryOrderByColumns() {
        return ORDERBY_COLUMNS;
    }
}
