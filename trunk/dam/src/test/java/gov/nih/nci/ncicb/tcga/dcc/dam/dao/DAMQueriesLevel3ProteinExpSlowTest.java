package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Slow test for DAMQueriesLevel3ProteinExp
 *
 * @author Your Name
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3ProteinExpSlowTest extends DAMQueriesLevelTwoAndThreeSlowTest {
    private DAMQueriesLevel3ProteinExp proteinQueries;

    private static final String SAMPLES_FOLDER =
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String PROPERTIES_FILE = "tcga_unittest.properties";
    private static final String TEST_DATA_FILE = "portal/dao/Level_3_Protein_TestDB.xml";

    public DAMQueriesLevel3ProteinExpSlowTest() throws IOException {
        super(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    protected DAMQueriesCGCCLevelTwoAndThree initQueryObject() {
        proteinQueries = new DAMQueriesLevel3ProteinExp();
        DAMQueriesCGCCLevelTwoThreeList damQueriesCGCCLevelTwoThreeList = new DAMQueriesCGCCLevelTwoThreeList();
        damQueriesCGCCLevelTwoThreeList.setDataSource(getDataSource());
        proteinQueries.setLevelTwoThreeList(damQueriesCGCCLevelTwoThreeList);
        return proteinQueries;
    }

    @Override
    protected int getDataLevel() {
        return 3;
    }

    @Test
    public void testMakeFiles() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DataFileLevelThree proteinDataFile = new DataFileLevelThree();
        proteinDataFile.setHybRefIds(Arrays.asList(1L));
        proteinDataFile.setDataSetsDP(Arrays.asList(1));
        proteinDataFile.setFileId("protein-expression");

        proteinQueries.addPathsToSelectedFiles(Arrays.asList((DataFile)proteinDataFile));
        proteinDataFile.getPath();
        BufferedReader outputReader = new BufferedReader(new FileReader(proteinDataFile.getPath()));
        assertEquals("barcode\tantibody name\tgene name\tprotein expression value", outputReader.readLine());
        assertEquals("barcodeA\tAntibody123\tABC123\t0.9876", outputReader.readLine());
        assertEquals("barcodeA\tAntibody456\tGENE12\t1200.4", outputReader.readLine());
        assertNull(outputReader.readLine());
    }


}
