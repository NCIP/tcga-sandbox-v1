package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Test for DAMQueries Methylation
 *
 * @author Your Name
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3MethylationSlowTest extends DAMQueriesLevelTwoAndThreeSlowTest {

    private static final String SAMPLES_FOLDER =
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String PROPERTIES_FILE = "tcga_unittest.properties";
    private static final String TEST_DATA_FILE = "portal/dao/Level_3_Methylation_TestDB.xml";

    private DAMQueriesLevel3Methylation methylationQueries;

    public DAMQueriesLevel3MethylationSlowTest() throws IOException {
        super(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    protected DAMQueriesCGCCLevelTwoAndThree initQueryObject() {
        methylationQueries = new DAMQueriesLevel3Methylation();
        DAMQueriesCGCCLevelTwoThreeList damQueriesCGCCLevelTwoThreeList = new DAMQueriesCGCCLevelTwoThreeList();
        damQueriesCGCCLevelTwoThreeList.setDataSource(getDataSource());
        methylationQueries.setLevelTwoThreeList(damQueriesCGCCLevelTwoThreeList);
        return methylationQueries;
    }

    @Override
    protected int getDataLevel() {
        return 3;
    }

    public void testAddFilePaths() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree methylationDataFile = new DataFileLevelThree();
        methylationDataFile.setHybRefIds(Arrays.asList(1L));
        methylationDataFile.setDataSetsDP(Arrays.asList(1));
        methylationDataFile.setFileId("methylation");

        methylationQueries.addPathsToSelectedFiles(Arrays.asList((DataFile)methylationDataFile));
        methylationDataFile.getPath();
        BufferedReader outputReader = new BufferedReader(new FileReader(methylationDataFile.getPath()));
        assertEquals("barcode\tprobe name\tbeta value\tgene symbol\tchromosome\tposition", outputReader.readLine());
        assertEquals("barcodeA\tmethy001\t1.234\tABC\t12\t1000", outputReader.readLine());
        assertNull(outputReader.readLine());
    }
}
