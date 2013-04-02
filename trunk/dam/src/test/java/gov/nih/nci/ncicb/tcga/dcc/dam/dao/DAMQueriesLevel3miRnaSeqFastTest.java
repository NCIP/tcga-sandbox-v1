package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;

import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Fast test for miRNASeq level 3 Queries class
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DAMQueriesLevel3miRnaSeqFastTest {
    private final Mockery context = new JUnit4Mockery();
    private DAMQueriesLevel3miRnaSeq level3miRnaSeqQueries;
    private DataFileLevelTwoThree dataFile;
    private DAMUtilsI mockDamUtils;

    @Before
    public void setUp() throws Exception {
        level3miRnaSeqQueries = new DAMQueriesLevel3miRnaSeq();
        mockDamUtils = context.mock(DAMUtilsI.class);
        level3miRnaSeqQueries.setDamUtils(mockDamUtils);
        dataFile = new DataFileLevelThree();
    }

    @Test
    public void testGetValueTable() {
     assertEquals("MIRNASEQ_VALUE", level3miRnaSeqQueries.getValueTable());
    }

    @Test
    public void testGetAllowedDataTypes() {
        context.checking(new Expectations() {{
            one(mockDamUtils).getLevel3AllowedDataTypes(DAMUtils.MIRNA_SEQ_TYPE);
            will(returnValue(Arrays.asList("Quantification-miRNA", "Quantification-miRNA Isoform")));
        }});
        assertEquals(Arrays.asList("Quantification-miRNA", "Quantification-miRNA Isoform"), level3miRnaSeqQueries.getAllowedDatatypes());
    }

    @Test
    public void testGetRowSizeQueryMiRna() {
        dataFile.setSourceFileType("mirna.txt");
        assertEquals("select sum(avg_col_len) from avg_col_len where table_name = 'EXPGENE_VALUE' " +
                "and column_name in ('FEATURE', 'READ_COUNT', 'READS_PER_MILLION', 'CROSS_MAPPED')",
                level3miRnaSeqQueries.getRowSizeQuery(dataFile));
    }

    @Test
    public void testGetRowSizeQueryIsoform() {
        dataFile.setSourceFileType("isoform.txt");
        assertEquals("select sum(avg_col_len) from avg_col_len where table_name = 'EXPGENE_VALUE' " +
                "and column_name in ('FEATURE', 'READ_COUNT', 'READS_PER_MILLION', 'CROSS_MAPPED'," +
                "'ISOFORM_COORDS', 'MIRNA_REGION_ACCESSION', 'MIRNA_REGION_ANNOTATION')",
                level3miRnaSeqQueries.getRowSizeQuery(dataFile));
    }

    @Test
    public void testGetValueColumnNames() {
        dataFile.setSourceFileType("mirna.txt");
        assertArrayEquals(new String[]{"bestbarcode", "FEATURE", "READ_COUNT", "READS_PER_MILLION", "CROSS_MAPPED"},
                level3miRnaSeqQueries.getValueColumnNames(dataFile)
        );
    }

    @Test
    public void testGetValueColumnNamesIsoform() {
        dataFile.setSourceFileType("isoform.txt");
        assertArrayEquals(new String[]{
                "bestbarcode", "FEATURE", "ISOFORM_COORDS", "READ_COUNT", "READS_PER_MILLION", "CROSS_MAPPED",
                "MIRNA_REGION_ACCESSION", "MIRNA_REGION_ANNOTATION"},
                level3miRnaSeqQueries.getValueColumnNames(dataFile)
        );
    }

    @Test
    public void testGetFileHeader() {
        dataFile.setSourceFileType("blah.mirna.data.txt");
                assertEquals("barcode\tmiRNA_ID\tread_count\treads_per_million_miRNA_mapped\tcross-mapped\n",
                        level3miRnaSeqQueries.getFileHeader(dataFile));

    }

    @Test
    public void testGetFileHeaderIsoform() {
        dataFile.setSourceFileType("blah.isoform.data.txt");
        assertEquals("barcode\tmiRNA_ID\tisoform_coords\tread_count\treads_per_million_miRNA_mapped\tcross-mapped\tmiRNA_region_annotation\tmiRNA_region_accession\n",
                level3miRnaSeqQueries.getFileHeader(dataFile));
    }

    @Test
    public void testGetValueQueryOrderByColumns() {
        dataFile.setSourceFileType("mirna");
        assertArrayEquals(new String[]{"FEATURE"},
                level3miRnaSeqQueries.getValueQueryOrderByColumns());
    }
}
