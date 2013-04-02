package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;

/**
 * DAO class for level3 RNASeq queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3RnaSeq extends DAMQueriesLevel3  {

    private static final String TABLE_NAME = "RNASEQ_VALUE";
    private static final Map<String,String> rowSizeQueryBySourceFileType = new HashMap<String,String>();
    private static final Map<String,String[]> valueColumnsBySourceFileType = new HashMap<String,String[]>();
    private static final Map<String,String> headerBySourceFileType = new HashMap<String,String>();

    private static final String SELECT_COLUMN_PREFIX =  "select sum(avg_col_len) from avg_col_len where " +
            "table_name = '"+TABLE_NAME+"' and " +
            "column_name in(";

    static {
        // initialize row size queries
        rowSizeQueryBySourceFileType.put(ConstantValues.EXON_QUANTIFICATION_SOURCE_FILE_TYPE,SELECT_COLUMN_PREFIX+
                "'FEATURE','RAW_COUNTS','MEDIAN_LENGTH_NORMALIZED','RPKM')");
        rowSizeQueryBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_SOURCE_FILE_TYPE,SELECT_COLUMN_PREFIX+
                "'FEATURE','RAW_COUNTS','MEDIAN_LENGTH_NORMALIZED','RPKM')");
        rowSizeQueryBySourceFileType.put(ConstantValues.JUNCTION_QUANTIFICATION_SOURCE_FILE_TYPE,SELECT_COLUMN_PREFIX+
                "'FEATURE','RAW_COUNTS')");
        rowSizeQueryBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_SOURCE_FILE_TYPE,SELECT_COLUMN_PREFIX+
                "'FEATURE','NORMALIZED_COUNTS')");
        rowSizeQueryBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_GENE_SOURCE_FILE_TYPE,SELECT_COLUMN_PREFIX+
                "'FEATURE','RAW_COUNTS','SCALED_ESTIMATE','TRANSCRIPT_ID')");
        rowSizeQueryBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_SOURCE_FILE_TYPE,SELECT_COLUMN_PREFIX+
                "'FEATURE','NORMALIZED_COUNTS')");
        rowSizeQueryBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_ISOFORMS_SOURCE_FILE_TYPE,SELECT_COLUMN_PREFIX+
                "'FEATURE','RAW_COUNTS','SCALED_ESTIMATE')");

        // initialize  value columns
        valueColumnsBySourceFileType.put(ConstantValues.EXON_QUANTIFICATION_SOURCE_FILE_TYPE,
                new String[]{"BESTBARCODE","FEATURE","RAW_COUNTS","MEDIAN_LENGTH_NORMALIZED","RPKM"});
        valueColumnsBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_SOURCE_FILE_TYPE,
                new String[]{"BESTBARCODE","FEATURE","RAW_COUNTS","MEDIAN_LENGTH_NORMALIZED","RPKM"});
        valueColumnsBySourceFileType.put(ConstantValues.JUNCTION_QUANTIFICATION_SOURCE_FILE_TYPE,
                new String[]{"BESTBARCODE","FEATURE","RAW_COUNTS"});

        valueColumnsBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_SOURCE_FILE_TYPE,
                new String[]{"BESTBARCODE","FEATURE","NORMALIZED_COUNTS"});
        valueColumnsBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_GENE_SOURCE_FILE_TYPE,
                new String[]{"BESTBARCODE","FEATURE","RAW_COUNTS","SCALED_ESTIMATE","TRANSCRIPT_ID"});
        valueColumnsBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_SOURCE_FILE_TYPE,
                new String[]{"BESTBARCODE","FEATURE","NORMALIZED_COUNTS"});
        valueColumnsBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_ISOFORMS_SOURCE_FILE_TYPE,
                new String[]{"BESTBARCODE","FEATURE","RAW_COUNTS","SCALED_ESTIMATE"});

        // initialize headers
        headerBySourceFileType.put(ConstantValues.EXON_QUANTIFICATION_SOURCE_FILE_TYPE,
                "barcode\texon\traw_counts\tmedian_length_normalized\tRPKM\n");
        headerBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_SOURCE_FILE_TYPE,
                "barcode\tgene\traw_counts\tmedian_length_normalized\tRPKM\n");
        headerBySourceFileType.put(ConstantValues.JUNCTION_QUANTIFICATION_SOURCE_FILE_TYPE,
                "barcode\tjunction\traw_counts\n");
        headerBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_SOURCE_FILE_TYPE,
                "barcode\tgene_id\tnormalized_count\n");
        headerBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_GENE_SOURCE_FILE_TYPE,
                "barcode\tgene_id\traw_count\tscaled_estimate\ttranscript_id\n");
        headerBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_SOURCE_FILE_TYPE,
                "barcode\tisoform_id\tnormalized_count\n");
        headerBySourceFileType.put(ConstantValues.GENE_QUANTIFICATION_RSEM_ISOFORMS_SOURCE_FILE_TYPE,
                "barcode\tisoform_id\traw_count\tscaled_estimate\n");

    };

    private static final String[] ORDER_BY_COLUMNS = new String[]{"FEATURE"};


    @Override
    protected String getValueTable() {
        return TABLE_NAME;
    }


    @Override
    protected List<String> getAllowedDatatypes() {
        return getDamUtils().getLevel3AllowedDataTypes(DAMUtils.RNA_SEQ_TYPE);
    }


    @Override
    protected String getRowSizeQuery(final DataFileLevelTwoThree dataFile) {
        return rowSizeQueryBySourceFileType.get(dataFile.getSourceFileType());

    }

    @Override
    protected String[] getValueColumnNames(final DataFileLevelTwoThree dataFile) {
        return valueColumnsBySourceFileType.get(dataFile.getSourceFileType());
    }


    @Override
    protected String getFileHeader(DataFileLevelTwoThree dataFile) {
        return headerBySourceFileType.get(dataFile.getSourceFileType());
    }

    @Override
    protected String[] getValueQueryOrderByColumns() {
        return ORDER_BY_COLUMNS;
    }

}
