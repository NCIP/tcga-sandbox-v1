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
 * DAO for protein expression level 3 data.
 *
 * @author Jessica Walton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3ProteinExp extends DAMQueriesLevel3 {

    private static final String PROTEINEXP_VALUE_TABLE = "PROTEINEXP_VALUE";

    private static final String HEADER = "barcode\tantibody name\tgene name\tprotein expression value\n";
    private static final String ROW_SIZE_SQL = "select sum(avg_col_len) from avg_col_len where table_name = '" +
            PROTEINEXP_VALUE_TABLE + "' " +
            "and column_name in ('ANTIBODY_NAME', 'HUGO_GENE_SYMBOL', 'PROTEIN_EXPRESSION_VALUE')";
    private static final String[] VALUE_ATTRIBUTES = new String[]{"bestbarcode", "antibody_name", "hugo_gene_symbol",
            "protein_expression_value"};
    private static final String[] ORDERBY_COLUMNS = new String[]{"antibody_name", "hugo_gene_symbol"};

    /**
     * Gets the name of the table in the database where the data for this type of level 3 is stored.
     *
     * @return "PROTEINEXP_VALUE"
     */
    @Override
    protected String getValueTable() {
        return PROTEINEXP_VALUE_TABLE;
    }

    /**
     * Gets the list of allowed datatypes for this type of level 3.
     *
     * @return list of datatype names
     */
    @Override
    protected List<String> getAllowedDatatypes() {
        return getDamUtils().getLevel3AllowedDataTypes(DAMUtils.PROTEIN_EXP);
    }

    /**
     * Gets the query to use to determine the row size (in bytes) of one row of the generated file.
     *
     * @param dataFile the file to be generated
     * @return the estimated size in bytes of one row of the file
     */
    @Override
    protected String getRowSizeQuery(final DataFileLevelTwoThree dataFile) {
        return ROW_SIZE_SQL;
    }

    /**
     * Gets an array of the value column names for this DAO.
     *
     * @param dataFile the data file bean the columns are for
     * @return array of names
     */
    @Override
    protected String[] getValueColumnNames(final DataFileLevelTwoThree dataFile) {
        return VALUE_ATTRIBUTES;
    }

    /**
     * Gets the header for the file, given that the file is for the indicated platform type.
     *
     * @param platformTypeId the platform type of the file we are generating
     * @return string header for the file
     */
    @Override
    protected String getFileHeader(final DataFileLevelTwoThree platformTypeId) {
        return HEADER;
    }

    /**
     * Gets the column names to put in the order by clause of the query for this type
     *
     * @return column names for ORDER BY clause
     */
    @Override
    protected String[] getValueQueryOrderByColumns() {
        return ORDERBY_COLUMNS;
    }
}
