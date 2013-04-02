package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * Fast test for Level3RNASeq queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3RnaSeqFastTest {
    private final Mockery context = new JUnit4Mockery();
    private DAMQueriesLevel3RnaSeq level3RnaSeqQueries;
    private DataFileLevelTwoThree dataFile;
    private DAMUtilsI mockDamUtils;

    @Before
    public void setUp() throws Exception {
        level3RnaSeqQueries = new DAMQueriesLevel3RnaSeq();
        mockDamUtils = context.mock(DAMUtilsI.class);
        level3RnaSeqQueries.setDamUtils(mockDamUtils);
        dataFile = new DataFileLevelThree();
    }

    @Test
    public void getValueTable(){
        assertEquals("Invalid table name","RNASEQ_VALUE", level3RnaSeqQueries.getValueTable());
    }

    @Test
    public void getRowSizeQueryForExon(){
        dataFile.setSourceFileType("expression_exon");
        assertEquals("select sum(avg_col_len) from avg_col_len where table_name = 'RNASEQ_VALUE' and column_name in(" +
                "'FEATURE','RAW_COUNTS','MEDIAN_LENGTH_NORMALIZED','RPKM')",
                level3RnaSeqQueries.getRowSizeQuery(dataFile));
    }


    @Test
    public void getRowSizeQueryForGene(){
        dataFile.setSourceFileType("expression_gene");
        assertEquals("select sum(avg_col_len) from avg_col_len where table_name = 'RNASEQ_VALUE' and column_name in(" +
                "'FEATURE','RAW_COUNTS','MEDIAN_LENGTH_NORMALIZED','RPKM')",
                level3RnaSeqQueries.getRowSizeQuery(dataFile));
    }


    @Test
    public void getRowSizeQueryForJunction(){
        dataFile.setSourceFileType("expression_junction");
        assertEquals("select sum(avg_col_len) from avg_col_len where table_name = 'RNASEQ_VALUE' and column_name in(" +
                "'FEATURE','RAW_COUNTS')",
                level3RnaSeqQueries.getRowSizeQuery(dataFile));
    }

    @Test
    public void getValueColumnNamesForExon() {
        dataFile.setSourceFileType("expression_exon");
        assertArrayEquals(new String[]{"BESTBARCODE","FEATURE","RAW_COUNTS","MEDIAN_LENGTH_NORMALIZED","RPKM"},
                level3RnaSeqQueries.getValueColumnNames(dataFile)
        );
    }

    @Test
    public void getValueColumnNamesForGene() {
        dataFile.setSourceFileType("expression_gene");
        assertArrayEquals(new String[]{"BESTBARCODE","FEATURE","RAW_COUNTS","MEDIAN_LENGTH_NORMALIZED","RPKM"},
                level3RnaSeqQueries.getValueColumnNames(dataFile)
        );
    }

    @Test
    public void getValueColumnNamesForJunction() {
        dataFile.setSourceFileType("expression_junction");
        assertArrayEquals(new String[]{"BESTBARCODE","FEATURE","RAW_COUNTS"},
                level3RnaSeqQueries.getValueColumnNames(dataFile)
        );
    }


    @Test
    public void getFileHeaderForExon() {
        dataFile.setSourceFileType("expression_exon");
                assertEquals("barcode\texon\traw_counts\tmedian_length_normalized\tRPKM\n",
                level3RnaSeqQueries.getFileHeader(dataFile));
    }

    @Test
    public void getFileHeaderForGene() {
        dataFile.setSourceFileType("expression_gene");
                assertEquals("barcode\tgene\traw_counts\tmedian_length_normalized\tRPKM\n",
                level3RnaSeqQueries.getFileHeader(dataFile));

    }

    @Test
    public void getFileHeaderForJunction() {
        dataFile.setSourceFileType("expression_junction");
                assertEquals("barcode\tjunction\traw_counts\n",
                level3RnaSeqQueries.getFileHeader(dataFile));

    }

    @Test
    public void getValueQueryOrderByColumns() {
        assertArrayEquals(new String[]{"FEATURE"},
                level3RnaSeqQueries.getValueQueryOrderByColumns());
    }

}
