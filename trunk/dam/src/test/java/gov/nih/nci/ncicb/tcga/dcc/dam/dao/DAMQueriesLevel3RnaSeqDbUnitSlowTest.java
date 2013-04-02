package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Slow test for RnaSeq DAM queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3RnaSeqDbUnitSlowTest extends DAMQueriesLevelTwoAndThreeSlowTest {

    public DAMQueriesLevel3RnaSeqDbUnitSlowTest() throws IOException {
        super();
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected DAMQueriesCGCCLevelTwoAndThree initQueryObject() {
        return new DAMQueriesLevel3RnaSeq();
    }

    @Override
    protected int getDataLevel() {
        return 3;
    }

    public void testGenerateRnaSeqExonFile() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1", "A.2"));
        dataFile.setPlatformTypeId("5");
        dataFile.setSourceFileType("expression_exon");
        dataFile.setHybRefIds(Arrays.asList(1L, 2L));
        dataFile.setDataSetsDP(Arrays.asList(17));
        dataFile.setFileId("expression_exon");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);
        final String expectedValue = "barcode\texon\traw_counts\tmedian_length_normalized\tRPKM\n" +
                "A.1\tEXON-1\t100.0\t1000.0\t1000.1\n" +
                "A.1\tEXON-2\t200.0\t2000.0\t2000.2\n" +
                "A.2\tEXON-3\t300.0\t3000.0\t3000.3\n" +
                "A.2\tEXON-4\t400.0\t4000.0\t4000.4";
        assertEquals(expectedValue, stringWriter.getBuffer().toString().trim());
    }

    public void testGenerateRnaSeqGeneFile() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1", "A.2"));
        dataFile.setPlatformTypeId("5");
        dataFile.setSourceFileType("expression_gene");
        dataFile.setHybRefIds(Arrays.asList(1L, 2L));
        dataFile.setDataSetsDP(Arrays.asList(18));
        dataFile.setFileId("expression_gene");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);
        final String expectedValue = "barcode\tgene\traw_counts\tmedian_length_normalized\tRPKM\n" +
                "A.1\tGENE-1\t100.0\t1000.0\t1000.1\n" +
                "A.1\tGENE-2\t200.0\t2000.0\t2000.2\n" +
                "A.2\tGENE-3\t300.0\t3000.0\t3000.3\n" +
                "A.2\tGENE-4\t400.0\t4000.0\t4000.4";


        assertEquals(expectedValue, stringWriter.getBuffer().toString().trim());
    }

    public void testGenerateRnaSeqJunctionFile() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1", "A.2"));
        dataFile.setPlatformTypeId("5");
        dataFile.setSourceFileType("expression_junction");
        dataFile.setHybRefIds(Arrays.asList(1L, 2L));
        dataFile.setDataSetsDP(Arrays.asList(19 ));
        dataFile.setFileId("expression_junction");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);
        final String expectedValue = "barcode\tjunction\traw_counts\n" +
                "A.1\tJUNCTION-1\t100.0\n" +
                "A.1\tJUNCTION-2\t200.0\n" +
                "A.2\tJUNCTION-3\t300.0\n" +
                "A.2\tJUNCTION-4\t400.0";
        assertEquals(expectedValue, stringWriter.getBuffer().toString().trim());
    }


    public void testGenerateRnaSeqV2ExonFile() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1"));
        dataFile.setPlatformTypeId("5");
        dataFile.setSourceFileType("expression_exon");
        dataFile.setHybRefIds(Arrays.asList(1L));
        dataFile.setDataSetsDP(Arrays.asList(21 ));
        dataFile.setFileId("expression_exon");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);

        final String expectedValue = "barcode\texon\traw_counts\tmedian_length_normalized\tRPKM\n" +
                "A.1\texon\t400.0\t4000.0\t4000.4";
        assertEquals(expectedValue, stringWriter.getBuffer().toString().trim());
    }


    public void testGenerateRnaSeqV2JunctionFile() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1"));
        dataFile.setPlatformTypeId("5");
        dataFile.setSourceFileType("expression_junction");
        dataFile.setHybRefIds(Arrays.asList(1L));
        dataFile.setDataSetsDP(Arrays.asList(22 ));
        dataFile.setFileId("expression_junction");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);

        final String expectedValue = "barcode\tjunction\traw_counts\n" +
                "A.1\tjunction\t400.0";
        assertEquals(expectedValue, stringWriter.getBuffer().toString().trim());
    }

    public void testGenerateRnaSeqV2GeneNormalizedFile() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1"));
        dataFile.setPlatformTypeId("5");
        dataFile.setSourceFileType("expression_rsem_gene_normalized");
        dataFile.setHybRefIds(Arrays.asList(1L));
        dataFile.setDataSetsDP(Arrays.asList(24 ));
        dataFile.setFileId("expression_rsem_gene_normalized");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);

        final String expectedValue = "barcode\tgene_id\tnormalized_count\n" +
                "A.1\tgene_id\t0";
        assertEquals(expectedValue, stringWriter.getBuffer().toString().trim());
    }


    public void testGenerateRnaSeqV2GeneFile() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1"));
        dataFile.setPlatformTypeId("5");
        dataFile.setSourceFileType("expression_rsem_gene");
        dataFile.setHybRefIds(Arrays.asList(1L));
        dataFile.setDataSetsDP(Arrays.asList(23 ));
        dataFile.setFileId("expression_rsem_gene");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);

        final String expectedValue = "barcode\tgene_id\traw_count\tscaled_estimate\ttranscript_id\n" +
                "A.1\tgene_id\t400.0\t1\t1";
        assertEquals(expectedValue, stringWriter.getBuffer().toString().trim());
    }

    public void testGenerateRnaSeqV2IsoformFile() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1"));
        dataFile.setPlatformTypeId("5");
        dataFile.setSourceFileType("expression_rsem_isoforms");
        dataFile.setHybRefIds(Arrays.asList(1L));
        dataFile.setDataSetsDP(Arrays.asList(25 ));
        dataFile.setFileId("expression_rsem_isoforms");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);

        final String expectedValue = "barcode\tisoform_id\traw_count\tscaled_estimate\n" +
                "A.1\tisoform_id\t400.0\t1";
        assertEquals(expectedValue, stringWriter.getBuffer().toString().trim());
    }


    public void testGenerateRnaSeqV2IsoformNormalizedFile() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1"));
        dataFile.setPlatformTypeId("5");
        dataFile.setSourceFileType("expression_rsem_isoforms_normalized");
        dataFile.setHybRefIds(Arrays.asList(1L));
        dataFile.setDataSetsDP(Arrays.asList(26 ));
        dataFile.setFileId("expression_rsem_isoforms_normalized");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);

        final String expectedValue = "barcode\tisoform_id\tnormalized_count\n" +
                "A.1\tisoform_id\t0";
        assertEquals(expectedValue, stringWriter.getBuffer().toString().trim());
    }
}
