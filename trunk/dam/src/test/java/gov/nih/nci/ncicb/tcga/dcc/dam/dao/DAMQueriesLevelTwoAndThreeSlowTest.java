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
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSetReducerI;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Parent test class for level 2 and 3 tests.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
    public abstract class DAMQueriesLevelTwoAndThreeSlowTest extends DBUnitTestCase {
    protected static final String SAMPLES_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    protected static final String PROPERTIES_FILE = "tcga_unittest.properties";
    protected static final String TEST_DATA_FILE = "portal/dao/Level_2_3_TestDB.xml";

    private final String[] barcodes1_1 = new String[] {"A.1", "A.2", "A.3", "B.1", "B.2", "B.3" };
    private final String[] barcode2_0 = new String[] {"C.1", "C.2", "D.1"};

    protected DAMQueriesCGCCLevelTwoAndThree queries;

    public DAMQueriesLevelTwoAndThreeSlowTest(final String sampleFolder, final String testFile, final String propertiesFile) {
        super(sampleFolder, testFile, propertiesFile);
    }

    public DAMQueriesLevelTwoAndThreeSlowTest() throws IOException {
        this(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    protected List<String> diseaseSetInContextList;

    public void setUp() throws Exception {
        super.setUp();
        queries = initQueryObject();
        queries.setDataSource(getDataSource());
        queries.setTempfileDirectory(SAMPLES_FOLDER + "portal" + File.separator + "dao");
        queries.setDataSetReducer( new DataSetReducerI() {
            public List<DataSet> reduceLevelTwoThree(final List<DataSet> dsList, final int level) {
                return dsList;
            }
            public List<DataSet> reduceLevelOne(final List<DataSet> dsList) {
                return dsList;
            }
        });
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(getDataSource());
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(dataSourceTransactionManager);
        queries.setTransactionTemplate(transactionTemplate);
        queries.setDamUtils(DAMUtils.getInstance());

        diseaseSetInContextList = new ArrayList<String>();
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    protected void testGetFileInfoSeparate(final int expectedNumDataFiles) throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> datasets = queries.getDataSetsForDiseaseType(null);
        List<DataSet> availableDatasets = new ArrayList<DataSet>();
        for (final DataSet dataset : datasets) {
            if (dataset.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE)) {
                availableDatasets.add(dataset);
            }
        }
        List<DataFile> dataFiles = queries.getFileInfoForSelectedDataSets(availableDatasets, false);
        assertEquals(expectedNumDataFiles, dataFiles.size());
        for (final DataFile dataFile : dataFiles) {
            // each file should have only 1 barcode in it
            assertEquals(1, dataFile.getBarcodes().size());
            assertEquals(1, dataFile.getSamples().size());
            // filename should contain the barcode, to make sure filenames don't conflict with each other
            assertTrue(dataFile.getFileName().contains(dataFile.getBarcodes().iterator().next()));
        }
    }

    protected abstract DAMQueriesCGCCLevelTwoAndThree initQueryObject();

    protected List<DataSet> makeInitialDatasets() {
        List<DataSet> dataSets = new ArrayList<DataSet>();
        for (final String barcode : barcodes1_1) {
            dataSets.add(makeDatasetForDisease(1, 1, barcode, getDataLevel(), "DIS1"));
        }
        for (final String barcode : barcode2_0) {
            dataSets.add(makeDatasetForDisease(2, 0, barcode, getDataLevel(), "DIS1"));
        }
        return dataSets;
    }

    protected List<DataSet> makeInitialControlDataSets() {
        List<DataSet> controlDataSets = new ArrayList<DataSet>();
        controlDataSets.add(makeDatasetForDisease(2, 0, "Control.1", getDataLevel(), "DIS1"));
        return controlDataSets;
    }

    protected abstract int getDataLevel();

    /**
     * Return a {@link DataSetLevelTwoThree} created with the given parameters
     *
     * @param batch the batch number
     * @param revision the revision number
     * @param barcode the barcode
     * @param level the level
     * @param diseaseType the disease type
     * @return a {@link DataSetLevelTwoThree} created with the given parameters
     */
    protected DataSetLevelTwoThree makeDatasetForDisease(final int batch,
                                                         final int revision,
                                                         final String barcode,
                                                         final int level,
                                                         final String diseaseType) {

        final DataSetLevelTwoThree dataset = new DataSetLevelTwoThree();
        dataset.setDataDepositBaseName("center_platform.disease");
        dataset.setDataDepositBatch(batch);
        dataset.setDataRevision(revision);
        dataset.setBarcodes(Arrays.asList(barcode));
        dataset.setCenterName("center");
        dataset.setPlatformName("platform");
        dataset.setAvailability(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        dataset.setLevel(String.valueOf(level));
        dataset.setSample(barcode.substring(0, barcode.indexOf(".")));
        dataset.setPlatformTypeId("3");
        dataset.setPlatformId("1");
        dataset.setDiseaseType(diseaseType);

        return dataset;
    }

    /**
     * Create a {@link DataSet} from the given parameters.
     * Barcodes may participate in more than one platform because the same sample may be analysed
     * on more than one platform.
     *
     * @param batch the batch number
     * @param revision the revision number
     * @param barcode the barcode
     * @param level the level
     * @param platformId the platform Id
     * @param diseaseType the disease type
     * @return a {@link DataSet} from the given parameters.
     */
    protected DataSetLevelTwoThree makeDataset(final int batch,
                                               final int revision,
                                               final String barcode,
                                               final int level,
                                               final String platformId,
                                               final String diseaseType) {

        final DataSetLevelTwoThree dataset = new DataSetLevelTwoThree();
        dataset.setDataDepositBaseName("center_platform.disease");
        dataset.setDataDepositBatch(batch);
        dataset.setDataRevision(revision);
        dataset.setBarcodes(Arrays.asList(barcode));
        dataset.setCenterName("center");
        dataset.setPlatformName("platform");
        dataset.setAvailability(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        dataset.setLevel(String.valueOf(level));
        dataset.setSample(barcode.substring(0, barcode.indexOf(".")));
        dataset.setPlatformTypeId("3");
        dataset.setPlatformId(platformId);
        dataset.setDiseaseType(diseaseType);

        return dataset;
    }

     protected void checkDataSet(final Map<String, DataSetLevelTwoThree> barcodeToDataset, final String barcode,
                                 final String expectedAvailability, final int expectedExperimentId, final int expectedNumberOfDataFiles) {
         DataSetLevelTwoThree dataSet = barcodeToDataset.get(barcode);
         assertEquals(expectedAvailability, dataSet.getAvailability());
         assertEquals(expectedExperimentId, dataSet.getExperimentID());
         if (expectedNumberOfDataFiles == 0) {
             assertTrue(dataSet.getDataFiles() == null || dataSet.getDataFiles().size() == 0);
         } else {
             assertEquals(expectedNumberOfDataFiles, dataSet.getDataFiles().size());
             for (final DataFile dataFile : dataSet.getDataFiles()) {
                 assertEquals((Integer) expectedExperimentId, ((DataFileLevelTwoThree)dataFile).getExperimentId());
                 assertEquals(barcode, dataFile.getBarcodes().iterator().next());
                 assertEquals(dataSet.getCenterName(), ((DataFileLevelTwoThree) dataFile).getCenterName());
                 assertEquals(dataSet.getPlatformName(), ((DataFileLevelTwoThree) dataFile).getPlatformName());
             }
         }

    }

    protected DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }

    public List<String> getDiseaseSetInContextList() {
        return diseaseSetInContextList;
    }
}
