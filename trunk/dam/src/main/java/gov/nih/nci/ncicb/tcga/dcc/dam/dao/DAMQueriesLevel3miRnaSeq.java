package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;

import java.util.List;

/**
 * Level 3 DAO for miRNA Seq data.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3miRnaSeq extends DAMQueriesLevel3 {
    private static final String MIRNASEQ_TABLE_NAME = "MIRNASEQ_VALUE";
    private static final String MIRNA_ROW_SIZE_QUERY = "select sum(avg_col_len) from avg_col_len where table_name = 'EXPGENE_VALUE' " +
            "and column_name in ('FEATURE', 'READ_COUNT', 'READS_PER_MILLION', 'CROSS_MAPPED')";
    private static final String ISOFORM_ROW_SIZE_QUERY = "select sum(avg_col_len) from avg_col_len where table_name = 'EXPGENE_VALUE' " +
                "and column_name in ('FEATURE', 'READ_COUNT', 'READS_PER_MILLION', 'CROSS_MAPPED'," +
                "'ISOFORM_COORDS', 'MIRNA_REGION_ACCESSION', 'MIRNA_REGION_ANNOTATION')";

    private static final String[] MIRNA_VALUE_COLUMNS = new String[]{"bestbarcode", "FEATURE", "READ_COUNT", "READS_PER_MILLION", "CROSS_MAPPED"};
    private static final String[] ISOFORM_VALUE_COLUMNS = new String[]{
                "bestbarcode", "FEATURE", "ISOFORM_COORDS", "READ_COUNT", "READS_PER_MILLION", "CROSS_MAPPED",
                "MIRNA_REGION_ACCESSION", "MIRNA_REGION_ANNOTATION"};

    private static final String MIRNA_HEADER = "barcode\tmiRNA_ID\tread_count\treads_per_million_miRNA_mapped\tcross-mapped\n";
    private static final String ISOFORM_HEADER = "barcode\tmiRNA_ID\tisoform_coords\tread_count\treads_per_million_miRNA_mapped\tcross-mapped\tmiRNA_region_annotation\tmiRNA_region_accession\n";

    // data in files should be ordered by "feature" value which is the miRNA ID
    private static final String[] ORDER_BY_COLUMNS = new String[]{"FEATURE"};

    /**
     * Gets the name of the table in the database where the data for this type of level 3 is stored.
     *
     * @return table name
     */
    @Override
    protected String getValueTable() {
        return MIRNASEQ_TABLE_NAME;
    }

    /**
     * Gets the list of allowed datatypes for this type of level 3.
     *
     * @return list of datatype names
     */
    @Override
    protected List<String> getAllowedDatatypes() {
        return getDamUtils().getLevel3AllowedDataTypes(DAMUtils.MIRNA_SEQ_TYPE);
    }

    @Override
    protected String getRowSizeQuery(final DataFileLevelTwoThree dataFile) {
        if (dataFile.getSourceFileType().contains("isoform")) {
            return ISOFORM_ROW_SIZE_QUERY;
        } else {
            return MIRNA_ROW_SIZE_QUERY;
        }

    }

    /**
     * Gets an array of the value column names for this DAO.
     *
     * @return array of names
     * @param dataFile
     */
    @Override
    protected String[] getValueColumnNames(final DataFileLevelTwoThree dataFile) {
        if (dataFile.getSourceFileType().contains("isoform")) {
            return ISOFORM_VALUE_COLUMNS;
        } else {
            return MIRNA_VALUE_COLUMNS;
        }
    }

    /**
     * Gets the header for the file, given that the file is for the indicated platform type.
     *
     *
     * @param dataFile
     * @return string header for the file
     */
    @Override
    protected String getFileHeader(DataFileLevelTwoThree dataFile) {
        if (dataFile.getSourceFileType().contains("isoform")) {
            return ISOFORM_HEADER;
        } else {
            return MIRNA_HEADER;
        }
    }

    @Override
    protected String[] getValueQueryOrderByColumns() {
        return ORDER_BY_COLUMNS;
    }
}
