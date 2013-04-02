/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetTrace;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;
import org.dbunit.operation.DatabaseOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DAMQueriesTraceSlowTest extends DBUnitTestCase {

    private static final String TEST_DATA_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static String datasetTestData = "portal/TraceDataSetInput.xml";
    private static final String PROPERTIES_FILE = "dccCommon.unittest.properties";

    private DAMQueriesTrace queries;
    private List<DataSet> datasets;
    private List<DataFile> datafiles;

    public DAMQueriesTraceSlowTest() {
        super(TEST_DATA_FOLDER, datasetTestData, PROPERTIES_FILE);
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new DAMQueriesTrace();
        queries.setDataSource(getDataSource());
        queries.setTempfileDirectory(TEST_DATA_FOLDER + "portal" + File.separator + "dao/trace");
        queries.setDamUtils(DAMUtils.getInstance());
    }

    public void testGetDataSetsForDiseaseType() throws DataAccessMatrixQueries.DAMQueriesException{

        // Initialize test data
        initTestData();

        assertTrue(datasets.size() == 2);
        for (final DataSet ds : datasets) {
            assertEquals("TCGA-25-1635-01",ds.getSample());    
            assertTrue(ds.isProtected());
        }
    }

    public void testGetFileInfoForSelectedDataSets() throws DataAccessMatrixQueries.DAMQueriesException{

        // Initialize test data
        initTestData();

        datafiles = queries.getFileInfoForSelectedDataSets(datasets, true);
        assertTrue(datafiles.size() == 1);
        for (final DataFile df : datafiles) {
            assertEquals("hms.harvard.edu__trace", df.getFileName());
            assertEquals("GBM", df.getDiseaseType());
        }
    }

    public void testGetFileInfoForSelectedDataSetsMultipleDisease() throws DataAccessMatrixQueries.DAMQueriesException {
        final String disease1 = "DIS1";
        final String disease2 = "DIS2";
        assertFalse(disease1.equals(disease2));

        final DataSet dataSet1 = makeDataset("3", "D.1", disease1);
        final DataSet dataSet2 = makeDataset("2", "D.1", disease2);

        final List<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);

        datafiles = queries.getFileInfoForSelectedDataSets(dataSets, false);

        assertEquals(2, datafiles.size());

        final DataFile firstDataFile = datafiles.get(0);
        final DataFile secondDataFile = datafiles.get(1);

        assertNotNull(firstDataFile);
        assertNotNull(secondDataFile);

        final String expectedFilenameForDisease1 = "hms.harvard.edu__trace";
        final String expectedFilenameForDisease2 = "broad.mit.edu__trace";

        boolean foundDataFileForDisease1 = false;
        boolean foundDataFileForDisease2 = false;
        for(final DataFile datafile : datafiles) {
            if(expectedFilenameForDisease1.equals(datafile.getFileName())) {
                foundDataFileForDisease1 = true;
                assertEquals(disease1, datafile.getDiseaseType());
            }
            if(expectedFilenameForDisease2.equals(datafile.getFileName())) {
                foundDataFileForDisease2 = true;
                assertEquals(disease2, datafile.getDiseaseType());
            }
        }

        assertTrue(foundDataFileForDisease1);
        assertTrue(foundDataFileForDisease2);
    }

    public void testAddPathsToSelectedFiles() throws DataAccessMatrixQueries.DAMQueriesException{

        // Initialize test data
        initTestData();

        datafiles = queries.getFileInfoForSelectedDataSets(datasets, true);
        assertTrue(datafiles.size() == 1);
        queries.addPathsToSelectedFiles(datafiles) ;
        for (final DataFile df : datafiles) {
            assertNotNull(df.getPath());
            File    dfFile  =   new File(df.getPath());
            assertTrue(dfFile.exists());
            dfFile.deleteOnExit();
        }
    }

    public void testGetDataSetsForControlsNoDisease() throws DataAccessMatrixQueries.DAMQueriesException {

        // Initialize test data
        initTestDataForControls(false, false);

        assertNotNull(datasets);
        assertEquals(0, datasets.size());
    }

    public void testGetDataSetsForControlsGBMDisease() throws DataAccessMatrixQueries.DAMQueriesException {

        final String expectedDisease = "GBM";
        final String expectedBarcode = "TCGA-00-0001-01A-01D-1234-99";

        // Initialize test data
        initTestDataForControls(true, false);

        assertNotNull(datasets);
        assertEquals(1, datasets.size());

        final DataSet dataSet = datasets.get(0);
        assertNotNull(dataSet);
        assertEquals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, dataSet.getAvailability());
        assertEquals(expectedDisease, dataSet.getDiseaseType());
        assertEquals(expectedBarcode, dataSet.getBarcodes().get(0));
    }

    public void testGetDataSetsForControlsGBMAndOVDisease() throws DataAccessMatrixQueries.DAMQueriesException {

        final List<String> expectedDiseases = Arrays.asList("GBM", "OV");
        final List<String> expectedBarcodes = Arrays.asList("TCGA-00-0001-01A-01D-1234-99", "TCGA-00-0001-01A-01D-4567-99");

        // Initialize test data
        initTestDataForControls(true, true);

        assertNotNull(datasets);
        assertEquals(expectedDiseases.size(), datasets.size());

        for(int i=0; i<datasets.size(); i++) {

            final DataSet dataSet = datasets.get(i);
            assertNotNull(dataSet);
            assertEquals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, dataSet.getAvailability());
            assertEquals(expectedDiseases.get(i), dataSet.getDiseaseType());
            assertEquals(expectedBarcodes.get(i), dataSet.getBarcodes().get(0));
        }
    }

    private void initTestData()throws DataAccessMatrixQueries.DAMQueriesException{
        datasets =  queries.getDataSetsForDiseaseType(DataAccessMatrixQueries.DEFAULT_DISEASETYPE);
    }

    /**
     * Initialize test data with control samples records.
     *
     * @param includeGBM <code>true</code> id {@link DataSet} for GBM disease should be included, <code>false</code> otherwise
     * @param includeOV <code>true</code> id {@link DataSet} for OV disease should be included, <code>false</code> otherwise
     * @throws DataAccessMatrixQueries.DAMQueriesException
     */
    private void initTestDataForControls(final boolean includeGBM,
                                         final boolean includeOV)
            throws DataAccessMatrixQueries.DAMQueriesException {

        final List<String> diseases = new ArrayList<String>();

        if(includeGBM) {
            diseases.add("GBM");
        }

        if(includeOV) {
            diseases.add("OV");
        }

        datasets =  queries.getDataSetsForControls(diseases) ;
    }

    /**
     * Create a {@link DataSet} from the given parameters.
     *
     * @param barcode the barcode
     * @param diseaseType the disease type
     * @return a {@link DataSet} from the given parameters.
     */
    protected DataSet makeDataset(final String centerId, final String barcode, final String diseaseType) {

        final DataSetTrace dataset = new DataSetTrace();
        dataset.setBarcodes(Arrays.asList(barcode));
        dataset.setAvailability(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        dataset.setSample(barcode.substring(0, barcode.indexOf(".")));
        dataset.setDiseaseType(diseaseType);
        dataset.setCenterId(centerId);

        return dataset;
    }
}

