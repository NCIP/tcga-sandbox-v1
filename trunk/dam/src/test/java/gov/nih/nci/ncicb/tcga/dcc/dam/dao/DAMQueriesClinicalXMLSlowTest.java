package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Slow test for DAMQueriesClinicalXML
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesClinicalXMLSlowTest extends DBUnitTestCase {
    private static final String SAMPLES_FOLDER =
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "portal/dao/clinical/Clinical_XML_TestDB.xml";
    private static final String PROPERTIES_FILE = "tcga_unittest.properties";

    private DAMQueriesClinicalXML damQueriesClinicalXML;

    public DAMQueriesClinicalXMLSlowTest() {
        super(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    public void setUp() throws Exception {
        super.setUp();

        damQueriesClinicalXML = new DAMQueriesClinicalXML();
        damQueriesClinicalXML.setDataSource(getDataSource());

    }

    public void testGetDataSetsForDisease() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> dataSets = damQueriesClinicalXML.getDataSetsForDiseaseType("TEST");
        verifyDataSets(dataSets);
    }

    private void verifyDataSets(final List<DataSet> dataSets) {
        assertEquals(3, dataSets.size());
        final Map<String, DataSet> dataSetsBySample = new HashMap<String, DataSet>();

        for (final DataSet dataSet : dataSets) {
            assertEquals(DataAccessMatrixQueries.CLINICAL_XML_CENTER, dataSet.getCenterId());
            assertEquals(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE, dataSet.getPlatformTypeId());
            assertEquals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, dataSet.getAvailability());
            assertEquals(DataAccessMatrixQueries.LEVEL_CLINICAL, dataSet.getLevel());

            dataSetsBySample.put(dataSet.getSample(), dataSet);
        }

        DataSet sample1DataSet = dataSetsBySample.get("sample1");
        DataSet sample2DataSet = dataSetsBySample.get("sample2");
        DataSet sample3DataSet = dataSetsBySample.get("sample3");

        assertNotNull(sample1DataSet);
        assertNotNull(sample2DataSet);
        assertNotNull(sample3DataSet);

        checkDataSet(sample1DataSet, "Batch 12", new String[]{"patient1_clinical.xml", "patient1_biospecimen.xml"},
                new String[]{"/path/to/patient1_clinical.xml", "/path/to/patient1_biospecimen.xml"}, new long[]{200, 201});

        checkDataSet(sample2DataSet, "Batch 12", new String[]{"patient2_aux.xml"}, new String[]{"/path/to/patient2.aux.xml"},
                new long[]{202});

        checkDataSet(sample3DataSet, "Batch 42", new String[]{"patient3_clinical.xml", "patient3_biospecimen.xml"},
                new String[]{"/path/to/patient3_clinical.xml", "/path/to/patient3_biospecimen.xml"}, new long[]{203, 204});

        assertEquals("Batch 42", sample3DataSet.getBatch());

    }

    public void testGetControlDataSets() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> controlDataSets = damQueriesClinicalXML.getDataSetsForControls(null);
        // control logic is the same as disease, so should get same results
        verifyDataSets(controlDataSets);
    }

    private void checkDataSet(final DataSet dataSet, final String expectedBatch,
                              final String[] expectedFileNames, final String[] expectedFilePaths, final long[] expectedFileSizes) {
        assertEquals(expectedBatch, dataSet.getBatch());
        assertEquals(expectedFileNames.length, dataSet.getDataFiles().size());
        assertEquals(DataAccessMatrixQueries.CLINICAL_XML_CENTER, dataSet.getCenterId());
        assertEquals(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE, dataSet.getPlatformTypeId());

        for (int i=0; i<expectedFileNames.length; i++) {
            final DataFile dataFile = dataSet.getDataFiles().get(i);
            assertEquals(expectedFileNames[i], dataFile.getFileName());
            assertEquals(expectedFilePaths[i], dataFile.getPath());
            assertEquals(DataAccessMatrixQueries.CLINICAL_XML_CENTER, dataFile.getCenterId());
            assertEquals(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE, dataFile.getPlatformTypeId());
            assertEquals("selected_samples", dataFile.getDisplaySample());
            assertEquals(expectedFileSizes[i], dataFile.getSize());
            assertTrue(dataFile.isPermanentFile());
            assertFalse(dataFile.isProtected());
            assertFalse(dataFile.mayPossiblyGenerateCacheFile());
        }
    }
}
