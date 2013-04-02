package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.TumorQueriesJDBCImpl;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: ramanr Date: May 27, 2010 Time: 11:23:37 AM To change this template use File |
 * Settings | File Templates.
 */
public class DiseaseQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/DiseaseQueries_TestData.xml";

    private TumorQueriesJDBCImpl queries;

    public DiseaseQueriesJDBCImplDBUnitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new TumorQueriesJDBCImpl();
        queries.setDataSource(getDataSource());

    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }


    @Test
    public void testGetDiseaseIdByName() throws Exception {

        int diseaseId = queries.getTumorIdByName("GBM");
        assertEquals(1, diseaseId);

        // Disease not in the database
        diseaseId = queries.getTumorIdByName("TEST");
        assertEquals(-1, diseaseId);

        diseaseId = queries.getTumorIdByName(null);
        assertEquals(-1, diseaseId);

    }

    @Test
    public void testGetDiseaseForName() throws Exception {

        Tumor disease = queries.getTumorForName("GBM");
        assertEquals("GBM", disease.getTumorName());

        // Invalid scenario - disease  not in the dataset
        disease = queries.getTumorForName("TEST");
        assertNull(disease);
    }

    @Test
    public void testGetAllDiseases() throws Exception {
        // There are 3 entries in the data set
        Collection diseasesList = queries.getAllTumors();
        assertEquals(3, diseasesList.size());
    }

    @Test
    public void testGetDiseaseNameById() throws Exception {

        String diseaseName = queries.getTumorNameById(1);
        assertEquals("GBM", diseaseName);

        // Invalid scenario - disease id 10 is not in the dataset
        diseaseName = queries.getTumorNameById(10);
        assertNull(diseaseName);
    }

    @Test
    public void testGetTissueIdsForDisease() throws Exception {

        List tissueIdList = queries.getTissueIdsForTumor("GBM");
        assertEquals(3, tissueIdList.size());

        // Invalid scenario - disease description is not in the dataset
        tissueIdList = queries.getTissueIdsForTumor("TEST");
        assertEquals(0, tissueIdList.size());
    }

}
