package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;


/**
 * Slow (DBUnit) test for DAMQueriesLevel3miRNASeq
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3miRnaSeqDbUnitSlowTest extends DAMQueriesLevelTwoAndThreeSlowTest {

    public DAMQueriesLevel3miRnaSeqDbUnitSlowTest() throws IOException {
        super();
    }

    public void setUp() throws Exception {
        super.setUp();
        DAMUtils.getInstance().setDataTypeId(DAMUtils.MIRNASEQ_QUANTIFICATION,"4");
    }

    @Override
    protected DAMQueriesCGCCLevelTwoAndThree initQueryObject() {
        return new DAMQueriesLevel3miRnaSeq();
    }

    @Override
    protected int getDataLevel() {
        return 3;
    }

    public void testGenerateMiRnaFile() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1", "A.2", "A.3"));
        dataFile.setPlatformTypeId("4");
        dataFile.setSourceFileType("mirna");
        dataFile.setHybRefIds(Arrays.asList(1L, 2L, 3L));
        dataFile.setDataSetsDP(Arrays.asList(15));
        dataFile.setFileId("mirna");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);
        assertEquals("barcode\tmiRNA_ID\tread_count\treads_per_million_miRNA_mapped\tcross-mapped\n" +
                "A.1\tmiRNA-1\t100.0\t1000.0\t0\n" +
                "A.1\tmiRNA-2\t200.0\t2000.0\t1\n" +
                "A.1\tmiRNA-3\t300.0\t3000.0\t0\n" +
                "A.1\tmiRNA-4\t400.0\t4000.0\t1\n" +
                "A.2\tmiRNA-1\t101.0\t1001.0\t1\n" +
                "A.2\tmiRNA-2\t201.0\t2001.0\t1\n" +
                "A.2\tmiRNA-3\t301.0\t3001.0\t0\n" +
                "A.2\tmiRNA-4\t401.0\t4001.0\t0\n" +
                "A.3\tmiRNA-1\t102.0\t1002.0\t0\n" +
                "A.3\tmiRNA-2\t202.0\t2002.0\t0\n" +
                "A.3\tmiRNA-3\t302.0\t3002.0\t1\n" +
                "A.3\tmiRNA-4\t402.0\t4002.0\t1", stringWriter.getBuffer().toString().trim());

    }

    public void testGenerateMiRNAIsoformFile() throws IOException {
        final DataFileLevelThree dataFile = new DataFileLevelThree();
        dataFile.setBarcodes(Arrays.asList("A.1", "A.2", "A.3"));
        dataFile.setPlatformTypeId("4");
        dataFile.setSourceFileType("isoform");
        dataFile.setHybRefIds(Arrays.asList(1L, 2L, 3L));
        dataFile.setDataSetsDP(Arrays.asList(16));
        dataFile.setFileId("isoform");

        final StringWriter stringWriter = new StringWriter();
        queries.generateFile(dataFile, stringWriter);

        assertEquals("barcode\tmiRNA_ID\tisoform_coords\tread_count\treads_per_million_miRNA_mapped\tcross-mapped\tmiRNA_region_annotation\tmiRNA_region_accession\n" +
                "A.1\tmiRNA-1\tcoords-1-1\t100.0\t1000.0\t0\tregion-1-1\tannot-1-1\n" +
                "A.1\tmiRNA-2\tcoords-2-1\t200.0\t2000.0\t0\tregion-2-1\tannot-2-1\n" +
                "A.1\tmiRNA-3\tcoords-3-1\t300.0\t3000.0\t0\tregion-3-1\tannot-3-1\n" +
                "A.1\tmiRNA-4\tcoords-4-1\t400.0\t4000.0\t1\tregion-4-1\tannot-4-1\n" +
                "A.2\tmiRNA-1\tcoords-1-2\t101.0\t1001.0\t0\tregion-1-2\tannot-1-2\n" +
                "A.2\tmiRNA-2\tcoords-2-2\t201.0\t2001.0\t0\tregion-2-2\tannot-2-2\n" +
                "A.2\tmiRNA-3\tcoords-3-2\t301.0\t3001.0\t0\tregion-3-2\tannot-3-2\n" +
                "A.2\tmiRNA-4\tcoords-4-2\t401.0\t4001.0\t1\tregion-4-2\tannot-4-2\n" +
                "A.3\tmiRNA-1\tcoords-1-3\t102.0\t1002.0\t1\tregion-1-3\tannot-1-3\n" +
                "A.3\tmiRNA-2\tcoords-2-3\t202.0\t2002.0\t0\tregion-2-3\tannot-2-3\n" +
                "A.3\tmiRNA-3\tcoords-3-3\t302.0\t3002.0\t0\tregion-3-3\tannot-3-3\n" +
                "A.3\tmiRNA-4\tcoords-4-3\t402.0\t4002.0\t0\tregion-4-3\tannot-4-3", stringWriter.getBuffer().toString().trim());
    }

}
