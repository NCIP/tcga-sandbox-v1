package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.generation.FileGenerator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;
import org.apache.commons.io.IOUtils;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Slow test for DAMQueriesClinical.  Uses unit test database and made-up data.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesClinicalBiotabSlowTest extends DBUnitTestCase {
    private static final String SAMPLES_FOLDER =
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "portal/dao/clinical/Clinical_TestDB.xml";
    private static final String PROPERTIES_FILE = "tcga_unittest.properties";


    private DAMQueriesClinicalBiotab clinicalDAO;
    private static final String TEST_DISEASE_ABBREVIATION = "disease";

    public DAMQueriesClinicalBiotabSlowTest() throws IOException {
        super(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    public void setUp() throws Exception {
        super.setUp();
        clinicalDAO = new DAMQueriesClinicalBiotab();
        clinicalDAO.setDataSource(getDataSource());
        clinicalDAO.setTempfileDirectory(SAMPLES_FOLDER + "portal/dao/clinical");

        final gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ClinicalMetaQueriesJDBCImpl clinicalMetaQueries = new gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ClinicalMetaQueriesJDBCImpl();
        clinicalMetaQueries.setDataSource( getDataSource() );
        clinicalDAO.setClinicalMetaQueries(clinicalMetaQueries);
        clinicalDAO.setDamUtils(DAMUtils.getInstance());
    }

    public void testGetDataSetsForDiseaseType() throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> dataSets = clinicalDAO.getDataSetsForDiseaseType(TEST_DISEASE_ABBREVIATION);
        testGetDataSets(dataSets);
    }

    private void testGetDataSets(final List<DataSet> dataSets) throws DataAccessMatrixQueries.DAMQueriesException {
        assertNotNull(dataSets);
        // should be one public and one protected data set per sample, and there are 5 samples
        assertEquals(5, dataSets.size());
        Set<String> publicSampleBarcodes = new TreeSet<String>();
        Set<String> protectedSampleBarcodes = new TreeSet<String>();
        for (final DataSet dataSet : dataSets) {
            assertEquals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, dataSet.getAvailability());
            assertEquals(TEST_DISEASE_ABBREVIATION, dataSet.getDiseaseType());
            assertEquals(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE, dataSet.getPlatformTypeId());
            assertEquals(DataAccessMatrixQueries.LEVEL_CLINICAL, dataSet.getLevel());
            if (dataSet.isProtected()) {
                protectedSampleBarcodes.add(dataSet.getSample());
            } else {
                publicSampleBarcodes.add(dataSet.getSample());
            }
            assertTrue(dataSet.getArchiveId() != 0);
            assertEquals("Batch 7", dataSet.getBatch());
        }
        assertEquals(5, publicSampleBarcodes.size());
        assertEquals(0, protectedSampleBarcodes.size());
        String[] expectedSamples = new String[]{"TCGA-00-0001-01", "TCGA-00-0001-02", "TCGA-00-0001-03", "TCGA-00-0002-01", "TCGA-00-0002-02"};
        assertTrue(Arrays.equals(expectedSamples, publicSampleBarcodes.toArray()));
    }

    public void testGetDataSetsForControls() throws DataAccessMatrixQueries.DAMQueriesException {
        // controls for clinical data are the same, just use the control schema
        clinicalDAO.setControlDiseaseAbbreviation(TEST_DISEASE_ABBREVIATION);
        final List<DataSet> dataSets = clinicalDAO.getDataSetsForControls(null);
        testGetDataSets(dataSets);
    }

    public void testGetDataSetsForControlsNoControlSchema() throws DataAccessMatrixQueries.DAMQueriesException {
        // if controlDiseaseAbbreviation not set, that means no control schema so data sets should be null
        assertNull(clinicalDAO.getDataSetsForControls(Arrays.asList("dis1", "dis2")));
    }


}
