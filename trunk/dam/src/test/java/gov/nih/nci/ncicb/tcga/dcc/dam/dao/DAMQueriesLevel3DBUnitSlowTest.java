/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DBUnit test class for Level3Queries.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel3DBUnitSlowTest extends DAMQueriesLevelTwoAndThreeSlowTest {

    public DAMQueriesLevel3DBUnitSlowTest() throws IOException {
        super();
    }

    protected DAMQueriesCGCCLevelTwoAndThree initQueryObject() {
        return new DAMQueriesLevel3ExpGene() {

            @Override
            protected String getRowSizeQuery(DataFileLevelTwoThree dataFile) {
                return "select 10 from dual";
            }

            @Override
            protected String getSampleFromBarcode(final String barcode) {
                return barcode.substring(0, 1);
            }

            @Override
            protected String getUniqueFilename(final DataFileLevelTwoThree dataFile) {
                return "outputFile.txt";
            }

            @Override
            protected List<String> getAllowedDatatypes(){
                List<String> ExpGeneGroup = new ArrayList<String>();
                ExpGeneGroup.add("3");
                return ExpGeneGroup;
            }

            @Override
            protected String getFileHeader(final DataFileLevelTwoThree dataFile){
                return "barcode\tgene symbol\tvalue\n";
            }

            /**
             * Overridden to avoid having to query postgres database to get initial list.
             * @param diseaseType the disease type
             * @return list of data sets
             */
            @Override
            protected List<DataSet> buildInitialList(final String diseaseType,
                                                     final boolean forControls) {

                if(forControls) {
                    return makeInitialControlDataSets();
                } else {
                    return makeInitialDatasets();
                }
            }

            @Override
            protected void setDiseaseInContext(final String diseaseType) {
                getDiseaseSetInContextList().add(diseaseType);
            }
        };

    }

    public void testGetDataSetsForDiseaseType() throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> datasets = queries.getDataSetsForDiseaseType(null);
        // make a map of which data sets are available for which barcodes
        Map<String, DataSetLevelTwoThree> barcodeToDataset = new HashMap<String, DataSetLevelTwoThree>();
        for (final DataSet dataSet : datasets) {
            assertTrue(dataSet instanceof DataSetLevelTwoThree);
            assertEquals(1, dataSet.getBarcodes().size());
            assertEquals("3", dataSet.getLevel());
            barcodeToDataset.put(dataSet.getBarcodes().get(0), (DataSetLevelTwoThree)dataSet);
        }
        // expect: A.1 to be pending, A.2 and A.3 to be available for experiment id 2, B.1 pending, B.2 available, B.3 pending
        checkDataSet(barcodeToDataset, "A.1", DataAccessMatrixQueries.AVAILABILITY_PENDING, 0, 0);
        checkDataSet(barcodeToDataset, "A.2", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, 2, 2);
        checkDataSet(barcodeToDataset, "A.3", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, 2, 2);
        checkDataSet(barcodeToDataset, "B.1", DataAccessMatrixQueries.AVAILABILITY_PENDING, 0, 0);
        checkDataSet(barcodeToDataset, "B.2", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, 2, 1);
        checkDataSet(barcodeToDataset, "B.3", DataAccessMatrixQueries.AVAILABILITY_PENDING, 0, 0);
        checkDataSet(barcodeToDataset, "C.1", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, 3, 1);
        checkDataSet(barcodeToDataset, "C.2", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, 3, 1);
        checkDataSet(barcodeToDataset, "D.1", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, 3, 1);
        
    }

    public void testGetFileInfoNotConsolidated()
            throws DataAccessMatrixQueries.DAMQueriesException {

        final DataSetLevelTwoThree dataSet = makeDatasetForDisease(1, 1, "A.2", 3, "DIS1");
        final DataFile dataFile = new DataFileLevelThree();
        dataSet.setDataFiles(Arrays.asList(dataFile));

        final List<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(dataSet);

        final List<DataFile> dataFiles = queries.getFileInfoForSelectedDataSets(dataSets, false);
        assertEquals(1, dataFiles.size());
        assertEquals(dataFile, dataFiles.get(0));
    }

    public void testGetFileInfoNotConsolidatedMultipleDiseases()
            throws DataAccessMatrixQueries.DAMQueriesException {

        final String disease1 = "DIS1";
        final DataSetLevelTwoThree dataSet1 = makeDatasetForDisease(1, 1, "A.1", 3, disease1);
        final DataFile dataFile1 = new DataFileLevelThree();
        dataSet1.setDataFiles(Arrays.asList(dataFile1));

        final String disease2 = "DIS2";
        final DataSetLevelTwoThree dataSet2 = makeDatasetForDisease(1, 1, "A.2", 3, disease2);
        final DataFile dataFile2 = new DataFileLevelThree();
        dataSet2.setDataFiles(Arrays.asList(dataFile2));

        final List<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);

        final List<DataFile> dataFiles = queries.getFileInfoForSelectedDataSets(dataSets, false);
        assertEquals(2, dataFiles.size());

        // Check that we got the 2 expected DataFiles
        final DataFile firstDataFileInList = dataFiles.get(0);
        assertNotNull(firstDataFileInList);

        final DataFile secondDataFileInList = dataFiles.get(1);
        assertNotNull(secondDataFileInList);

        assertFalse(firstDataFileInList.equals(secondDataFileInList));

        if(disease1.equals(firstDataFileInList.getDiseaseType())) {
            assertEquals(dataFile1, firstDataFileInList);
        } else {
            assertEquals(dataFile2, firstDataFileInList);
        }

        if(disease1.equals(secondDataFileInList.getDiseaseType())) {
            assertEquals(dataFile1, secondDataFileInList);
        } else {
            assertEquals(dataFile2, secondDataFileInList);
        }

        // Check that the 2 diseases got set in the context
        assertEquals(2, getDiseaseSetInContextList().size());
        assertTrue(getDiseaseSetInContextList().contains(disease1));
        assertTrue(getDiseaseSetInContextList().contains(disease2));
    }

    public void testGetFileInfoForOneDataSet()
            throws DataAccessMatrixQueries.DAMQueriesException {

        final String diseaseType = "DIS1";
        final DataSetLevelTwoThree dataSet = makeDatasetForDisease(1, 1, "A.2", 3, diseaseType);
        dataSet.setExperimentID(2);

        final List<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(dataSet);

        final List<DataFile> dataFiles = queries.getFileInfoForSelectedDataSets(dataSets, true);
        assertEquals(2, dataFiles.size());

        for(final DataFile dataFile : dataFiles) {

            assertTrue(dataFile instanceof DataFileLevelThree);
            assertEquals(1, dataFile.getBarcodes().size());
            assertEquals("A.2", dataFile.getBarcodes().iterator().next());
            assertEquals(1, ((DataFileLevelThree)dataFile).getHybRefIds().size());
            assertEquals(new Long(2), ((DataFileLevelThree)dataFile).getHybRefIds().iterator().next());
            assertEquals(diseaseType, dataFile.getDiseaseType());
        }

        // Check that 1 disease got set in the context
        assertEquals(1, getDiseaseSetInContextList().size());
        assertTrue(getDiseaseSetInContextList().contains(diseaseType));
    }

    public void testGetFileInfoForTwoDataSetDistinctDisease()
            throws DataAccessMatrixQueries.DAMQueriesException {

        final String diseaseType1 = "DIS1";
        final DataSetLevelTwoThree dataSet1 = makeDatasetForDisease(1, 1, "A.2", 3, diseaseType1);
        dataSet1.setExperimentID(2);

        final String diseaseType2 = "DIS2";
        final DataSetLevelTwoThree dataSet2 = makeDatasetForDisease(1, 1, "A.2", 3, diseaseType2);
        dataSet2.setExperimentID(2);

        final List<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);

        final List<DataFile> dataFiles = queries.getFileInfoForSelectedDataSets(dataSets, true);
        assertEquals(4, dataFiles.size());

        final DataFile dataFile1 = dataFiles.get(0);
        final DataFile dataFile2 = dataFiles.get(1);
        final DataFile dataFile3 = dataFiles.get(2);
        final DataFile dataFile4 = dataFiles.get(3);

        assertNotNull(dataFile1);
        assertNotNull(dataFile2);
        assertNotNull(dataFile3);
        assertNotNull(dataFile4);

        int diseaseType1Counter = 0;
        int diseaseType2Counter = 0;

        for(final DataFile dataFile : dataFiles) {

            assertTrue(dataFile instanceof DataFileLevelThree);
            assertEquals(1, dataFile.getBarcodes().size());
            assertEquals("A.2", dataFile.getBarcodes().iterator().next());
            assertEquals(1, ((DataFileLevelThree)dataFile).getHybRefIds().size());
            assertEquals(new Long(2), ((DataFileLevelThree)dataFile).getHybRefIds().iterator().next());

            final String actualDiseaseType = dataFile.getDiseaseType();
            if(diseaseType1.equals(actualDiseaseType)) {
                diseaseType1Counter++;
            } else if(diseaseType2.equals(actualDiseaseType)) {
                diseaseType2Counter++;
            }
        }

        // Checking that 2 DataFiles were found for each disease type
        assertEquals(2, diseaseType1Counter);
        assertEquals(2, diseaseType2Counter);

        // Check that the 2 diseases got set in the context
        assertEquals(2, getDiseaseSetInContextList().size());
        assertTrue(getDiseaseSetInContextList().contains(diseaseType1));
        assertTrue(getDiseaseSetInContextList().contains(diseaseType2));
    }

    public void testGetFileInfoTwoPlatforms() throws DataAccessMatrixQueries.DAMQueriesException {
        DataSetLevelTwoThree dataSet1 = makeDatasetForDisease(1, 1, "A.2", 3, "DIS1");
        dataSet1.setExperimentID(2);
        DataSetLevelTwoThree dataSet2 = makeDatasetForDisease(5, 0, "B.1", 3, "DIS1");
        dataSet2.setDataDepositBaseName("center_anotherPlatform.disease");
        dataSet2.setExperimentID(4);
        List<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        List<DataFile> dataFiles = queries.getFileInfoForSelectedDataSets(dataSets, true);
        // should be 2 files for data set 1, and 1 file for data set 2
        assertEquals(3, dataFiles.size());
        Map<String, DataFileLevelTwoThree> sourceFileTypes = new HashMap<String, DataFileLevelTwoThree>();
        for (final DataFile file : dataFiles) {
            sourceFileTypes.put(((DataFileLevelTwoThree)file).getSourceFileType(), (DataFileLevelTwoThree)file);
        }
        assertTrue(sourceFileTypes.containsKey("spring"));
        assertTrue(sourceFileTypes.containsKey("type_1"));
        assertTrue(sourceFileTypes.containsKey("type_2"));

        assertEquals(1, sourceFileTypes.get("spring").getBarcodes().size());
        assertEquals("B.1", sourceFileTypes.get("spring").getBarcodes().iterator().next());
        assertEquals(1, sourceFileTypes.get("type_1").getBarcodes().size());
        assertEquals("A.2", sourceFileTypes.get("type_1").getBarcodes().iterator().next());
    }

    public void testGetFileInfo() throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> datasets = queries.getDataSetsForDiseaseType(null);
        List<DataSet> availableDatasets = new ArrayList<DataSet>();
        for (final DataSet dataset : datasets) {
            if (dataset.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE)) {
                availableDatasets.add(dataset);
            }
        }
        // use the datasets, retrieval of which was tested in other method
        List<DataFile> datafiles = queries.getFileInfoForSelectedDataSets(availableDatasets, true);
        // should be 2 files, for type_1 and type_2
        assertEquals(2, datafiles.size());
        assertTrue(datafiles.get(0) instanceof DataFileLevelThree);
        assertTrue(datafiles.get(1) instanceof DataFileLevelThree);
        Map<String, DataFileLevelThree> dataFileForType = new HashMap<String, DataFileLevelThree>();
        dataFileForType.put(((DataFileLevelThree)datafiles.get(0)).getSourceFileType(), (DataFileLevelThree)datafiles.get(0));
        dataFileForType.put(((DataFileLevelThree)datafiles.get(1)).getSourceFileType(), (DataFileLevelThree)datafiles.get(1));
        assertNotNull("Expected file type type_1 was not in result", dataFileForType.get("type_1"));
        assertNotNull("Expected file type type_2 was not in result", dataFileForType.get("type_2"));

        assertEquals(6, dataFileForType.get("type_1").getHybRefIds().size());
        assertEquals("type_1", dataFileForType.get("type_1").getFileId());
        assertTrue(dataFileForType.get("type_1").getHybRefIds().contains(2l));
        assertTrue(dataFileForType.get("type_1").getHybRefIds().contains(3l));
        assertTrue(dataFileForType.get("type_1").getHybRefIds().contains(5l));
        assertTrue(dataFileForType.get("type_1").getHybRefIds().contains(7l));
        assertTrue(dataFileForType.get("type_1").getHybRefIds().contains(8l));
        assertTrue(dataFileForType.get("type_1").getHybRefIds().contains(9l));
        assertEquals(6, dataFileForType.get("type_1").getBarcodes().size());
        assertTrue(dataFileForType.get("type_1").getBarcodes().contains("A.2"));
        assertTrue(dataFileForType.get("type_1").getBarcodes().contains("A.3"));
        assertTrue(dataFileForType.get("type_1").getBarcodes().contains("B.2"));
        assertTrue(dataFileForType.get("type_1").getBarcodes().contains("C.1"));
        assertTrue(dataFileForType.get("type_1").getBarcodes().contains("C.2"));
        assertTrue(dataFileForType.get("type_1").getBarcodes().contains("D.1"));
        assertEquals(2, dataFileForType.get("type_1").getDataSetsDP().size());
        assertTrue(dataFileForType.get("type_1").getDataSetsDP().contains(2));
        assertTrue(dataFileForType.get("type_1").getDataSetsDP().contains(6));

        assertEquals(2, dataFileForType.get("type_2").getHybRefIds().size());
        assertTrue(dataFileForType.get("type_2").getHybRefIds().contains(2l));
        assertTrue(dataFileForType.get("type_2").getHybRefIds().contains(3l));
        assertEquals(2, dataFileForType.get("type_2").getBarcodes().size());
        assertTrue(dataFileForType.get("type_2").getBarcodes().contains("A.2"));
        assertTrue(dataFileForType.get("type_2").getBarcodes().contains("A.3"));
        assertTrue(dataFileForType.get("type_2").getDataSetsDP().contains(4));

        // number of lines = 3 data lines plus header = 4
        assertEquals(3072, dataFileForType.get("type_1").getSize());
        assertEquals(1024, dataFileForType.get("type_2").getSize());
    }

    public void testGetFileInfoSeparate() throws DataAccessMatrixQueries.DAMQueriesException {
        testGetFileInfoSeparate(8);
    }

    public void testAddPaths() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        String outputFile = SAMPLES_FOLDER + "portal" + File.separator + "dao" + File.separator + "outputFile.txt";

        BufferedReader outputReader = null;

        try {
            // set up data file object like we would get from the getFileInfo method
            DataFileLevelThree dataFile = new DataFileLevelThree();
            dataFile.setFileName("testFile");
            dataFile.setFileId("type_1");
            dataFile.setPlatformTypeId("3");
            dataFile.setProtected(false);
            dataFile.setHybRefIds(Arrays.asList(2l, 3l, 5l, 7l, 8l, 9l));
            dataFile.addDataSetID(2);
            dataFile.addDataSetID(6);
            dataFile.setDiseaseType("HELLO");
            List<DataFile> datafiles = new ArrayList<DataFile>();
            datafiles.add(dataFile);
            queries.addPathsToSelectedFiles(datafiles);
            assertEquals("HELLO", DiseaseContextHolder.getDisease());
            assertEquals(outputFile, dataFile.getPath());
            //noinspection IOResourceOpenedButNotSafelyClosed
            outputReader = new BufferedReader(new FileReader(outputFile));
            assertEquals("barcode\tgene symbol\tvalue", outputReader.readLine());

            assertEquals("A.2\tAAA\t1.2", outputReader.readLine());
            assertEquals("A.3\tAAA\t1.3", outputReader.readLine());
            assertEquals("B.2\tAAA\t1.4", outputReader.readLine());

            assertEquals("C.1\tBBB\t0.01", outputReader.readLine());
            assertEquals("C.1\tCCC\t0.02", outputReader.readLine());
            assertEquals("C.1\tDDD\t-0.3", outputReader.readLine());

            assertEquals("C.2\tBBB\t10.1", outputReader.readLine());
            assertEquals("C.2\tCCC\t-9.5", outputReader.readLine());
            assertEquals("C.2\tDDD\t12", outputReader.readLine());

            assertEquals("D.1\tBBB\t5.3", outputReader.readLine());
            assertEquals("D.1\tCCC\t123", outputReader.readLine());
            assertEquals("D.1\tDDD\t0.987", outputReader.readLine());

            assertNull(outputReader.readLine());
        } finally {
            new File(outputFile).deleteOnExit();
            IOUtils.closeQuietly(outputReader);
        }
    }

    public void testGetDataSetsForControls() throws DataAccessMatrixQueries.DAMQueriesException {

        final String disease1 = "DIS1";
        final String disease2 = "DIS2";
        final String barcode = "Control.1";

        final List<DataSet> dataSets = queries.getDataSetsForControls(Arrays.asList(disease1, disease2));
        assertNotNull(dataSets);
        assertEquals(2, dataSets.size());

        final DataSet dataSet1 = dataSets.get(0);
        final DataSet dataSet2 = dataSets.get(1);

        // Should be the same control data set, twice, once for each disease
        assertNotNull(dataSet1);
        assertNotNull(dataSet2);
        assertEquals(barcode, dataSet1.getBarcodes().get(0));
        assertEquals(barcode, dataSet2.getBarcodes().get(0));
        assertEquals(disease1, dataSet1.getDiseaseType());
        assertEquals(disease2, dataSet2.getDiseaseType());
        assertEquals(DataAccessMatrixQueries.AVAILABILITY_PENDING, dataSet1.getAvailability());
        assertEquals(DataAccessMatrixQueries.AVAILABILITY_PENDING, dataSet2.getAvailability());
    }

    public void testGetDataSetsForDiseaseTypeForControl() throws DataAccessMatrixQueries.DAMQueriesException {

        final String disease = "DIS";
        final String barcode = "Control.1";

        final List<DataSet> dataSets = queries.getDataSetsForDiseaseType(disease, true);
        assertNotNull(dataSets);
        assertEquals(1, dataSets.size());

        final DataSet dataSet = dataSets.get(0);
        assertNotNull(dataSet);
        assertEquals(barcode, dataSet.getBarcodes().get(0));
        assertEquals(disease, dataSet.getDiseaseType());
        assertEquals(DataAccessMatrixQueries.AVAILABILITY_PENDING, dataSet.getAvailability());
    }

    public void testGetDataSetsForDiseaseTypeForNonControl() throws DataAccessMatrixQueries.DAMQueriesException {

        final String disease = "DIS";
        final List<String> expectedBarcode = Arrays.asList("A.1", "A.2", "A.3", "B.1", "B.2", "B.3", "C.1", "C.2", "D.1");
        final List expectedAvailability = Arrays.asList(
                DataAccessMatrixQueries.AVAILABILITY_PENDING,
                DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                DataAccessMatrixQueries.AVAILABILITY_PENDING,
                DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                DataAccessMatrixQueries.AVAILABILITY_PENDING,
                DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                DataAccessMatrixQueries.AVAILABILITY_AVAILABLE
        );

        final List<DataSet> dataSets = queries.getDataSetsForDiseaseType(disease, false);
        assertNotNull(dataSets);
        assertEquals(9, dataSets.size());

        for(int i=0; i<dataSets.size(); i++) {

            final DataSet dataSet = dataSets.get(i);
            assertNotNull(dataSet);
            assertEquals(expectedBarcode.get(i), dataSet.getBarcodes().get(0));
            assertEquals(disease, dataSet.getDiseaseType());
            assertEquals(expectedAvailability.get(i), dataSet.getAvailability());
        }
    }

    protected int getDataLevel() {
        return 3;
    }
}
