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
 * DAO for level 3 expgene_value table
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3ExpGene extends DAMQueriesLevel3 {

    public static final String EXPGENE_VALUE = "expgene_value";

    private static final String[] VALUE_ATTRIBUTES = new String[]{"bestbarcode", "entrez_gene_symbol", "expression_value"};
    private static final String[] ORDERBY_COLUMNS = new String[]{ "entrez_gene_symbol" };
    private static final String ROW_SIZE_SQL = "select sum(avg_col_len) from avg_col_len where " +
            "table_name = 'EXPGENE_VALUE' and column_name in ('ENTREZ_GENE_SYMBOL', 'EXPRESSION_VALUE')";

    protected String getValueTable() {
        return EXPGENE_VALUE;
    }

    protected List<String> getAllowedDatatypes() {
        return getDamUtils().getLevel3AllowedDataTypes(DAMUtils.EXPGENE);
    }

    protected String getRowSizeQuery(final DataFileLevelTwoThree dataFile) {
        return ROW_SIZE_SQL;
    }

    protected String[] getValueColumnNames(final DataFileLevelTwoThree dataFile) {
        return VALUE_ATTRIBUTES;
    }

    /**
     * get header for level3 ExpGene dataTypes
     *
     * @param dataFile the data file the header is for
     * @return header string
     */
    protected String getFileHeader(final DataFileLevelTwoThree dataFile) {
        final String exonId = DAMUtils.getInstance().getDataTypeId(DAMUtils.EXON);
        final String mirnaId = DAMUtils.getInstance().getDataTypeId(DAMUtils.MIRNA);
       if (exonId !=null && exonId.equals(dataFile.getPlatformTypeId())) {
            return "barcode\tprobeset id\tscore\n";
        } else if (mirnaId !=null && mirnaId.equals(dataFile.getPlatformTypeId())) {
            return "barcode\tmiRNA id\tvalue\n";
        } else {
            return "barcode\tgene symbol\tvalue\n";
        }
    }

    protected String[] getValueQueryOrderByColumns() {
        return ORDERBY_COLUMNS;
    }

}//End of Class
