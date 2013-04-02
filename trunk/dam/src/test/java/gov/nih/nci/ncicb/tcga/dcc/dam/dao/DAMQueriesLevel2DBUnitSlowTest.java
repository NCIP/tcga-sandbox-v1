/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.SourceFileTypeFinder;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoConsolidated;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import oracle.jdbc.pool.OracleDataSource;
import org.junit.Test;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DBUnit tests for Level2 DAO.  Currently, if you run the entire test file, it will fail after a few tests
 * due to some strange OracleXE issue.  If you run each test on its own, they all pass.  Annoying!
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel2DBUnitSlowTest extends DAMQueriesLevelTwoAndThreeSlowTest {
    private static final String Level2_Files_Dir = SAMPLES_FOLDER+
            "filePackager"+
            File.separator;
    private static final String Tmp_Dir = Level2_Files_Dir +
            "tmp"+
            File.separator;

    public DAMQueriesLevel2DBUnitSlowTest() throws IOException {
        super();
    }


    private List<Long> fetchIds(final DAMQueriesLevel2 queries) {
        return new SimpleJdbcTemplate(queries.getDataSource()).query("select * from TMPHYBREF", new ParameterizedRowMapper<Long>() {
            public Long mapRow(final ResultSet resultSet, final int i) throws SQLException {
                return resultSet.getLong(1);
            }
        });
    }


    private List<Integer> fetchDsIds(final DAMQueriesLevel2 queries) {
        return new SimpleJdbcTemplate(queries.getDataSource()).query("select * from TMPDATASET", new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(final ResultSet resultSet, final int i) throws SQLException {
                return resultSet.getInt(1);
            }
        });
    }

    public void testGetDataSetsForDiseaseType() throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> datasets = queries.getDataSetsForDiseaseType(null);
        assertNotNull(datasets);
        assertEquals(9, datasets.size());
        Map<String, DataSetLevelTwoThree> barcodeToDataset = new HashMap<String, DataSetLevelTwoThree>();
        for (final DataSet dataSet : datasets) {
            assertTrue(dataSet instanceof DataSetLevelTwoThree);
            assertEquals(1, dataSet.getBarcodes().size());
            assertEquals("2", dataSet.getLevel());
            barcodeToDataset.put(dataSet.getBarcodes().get(0), (DataSetLevelTwoThree)dataSet);            
        }
        checkDataSet(barcodeToDataset, "A.1", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        checkDataSet(barcodeToDataset, "A.2", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        checkDataSet(barcodeToDataset, "A.3", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);

        checkDataSet(barcodeToDataset, "B.1", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        checkDataSet(barcodeToDataset, "B.2", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        checkDataSet(barcodeToDataset, "B.3", DataAccessMatrixQueries.AVAILABILITY_PENDING);
        checkDataSet(barcodeToDataset, "C.1", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        checkDataSet(barcodeToDataset, "C.2", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        checkDataSet(barcodeToDataset, "D.1", DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
    }

    public void testGetDataSetsForControl() throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> datasets = queries.getDataSetsForControls(Arrays.asList("DIS1", "DIS2"));
        assertNotNull(datasets);
        assertEquals(2, datasets.size());

        // should be same control dataset, twice, once for each disease
        assertEquals("Control.1", datasets.get(0).getBarcodes().get(0));
        assertEquals("Control.1", datasets.get(1).getBarcodes().get(0));
        assertEquals("DIS1", datasets.get(0).getDiseaseType());
        assertEquals("DIS2", datasets.get(1).getDiseaseType());
        assertEquals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, datasets.get(0).getAvailability());
        assertEquals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, datasets.get(1).getAvailability());
    }


    public void testGetFileInfoOneDataSet() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final List<DataSet> dataSets = new ArrayList<DataSet>();
        final DataSetLevelTwoThree dataSet = makeDatasetForDisease(1, 1, "B.1", 2, "DIS1");
        dataSets.add(dataSet);

        final SourceFileTypeFinder sourceFileTypeFinder = new SourceFileTypeFinder() {
            @Override
            public String findSourceFileType(final long fileId) throws IOException {
                return "copynumber_byallele";
            }
        };
        ((DAMQueriesLevel2)queries).setSourceFileTypeFinder(sourceFileTypeFinder);

        final List<DataFile> files = queries.getFileInfoForSelectedDataSets(dataSets, true);
        assertEquals(1, files.size());
        assertEquals("center_platform_copynumber_byallele.txt", files.get(0).getFileName());

        final DataFile dataFile = ((DataFileLevelTwoConsolidated) files.get(0)).getConstituentDataFiles().first();
        assertEquals(1, dataFile.getBarcodes().size());
        assertEquals("B.1", "B.1",dataFile.getBarcodes().toArray()[0]);
        assertEquals(dataSet.isProtected(), dataFile.isProtected());
    }

    public void testGetFileInfoForSelectedDataSetsNotConsolidated() throws DataAccessMatrixQueries.DAMQueriesException {
        DAMQueriesLevel2.MAX_IN_CLAUSE = 3;
        final List<DataSet> datasets = queries.getDataSetsForDiseaseType(null);
        final List<DataSet> availableDatasets = new ArrayList<DataSet>();
        for (final DataSet dataset : datasets) {
            if (dataset.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE)) {
                availableDatasets.add(dataset);
            }
        }
        // consolidate is false, so expect individual files
        final List<DataFile> files = queries.getFileInfoForSelectedDataSets(availableDatasets, false);
        assertEquals(8, files.size());

        List<String> expectedBarcodes = Arrays.asList(new String[] { "A.1", "A.2", "A.3", "B.1", "B.2", "C.1", "C.2","D.1"});
        List<String> expectedFileNames = Arrays.asList(new String[]{"A.1.txt", "A.2.txt", "A.3.txt", "B.1.txt", "B.2.txt", "C.1.txt", "C.2.txt", "D.1.txt"});
        for(final DataFile dataFile: files){
            assertEquals(1,dataFile.getBarcodes().size());
            final String barcode = dataFile.getBarcodes().toArray(new String[1])[0];
            assertTrue(expectedBarcodes.contains(barcode));
            assertEquals(expectedFileNames.get(expectedBarcodes.indexOf(barcode)),expectedFileNames.get(expectedBarcodes.indexOf(barcode)),dataFile.getFileName());;
        }
    }

    public void testGetFileInfoForSelectedDataSetsMultiPlatformBarcode()
            throws DataAccessMatrixQueries.DAMQueriesException {

        final String disease = "DIS1";
        final List<DataSet> dataSets = new ArrayList<DataSet>();
        final DataSetLevelTwoThree dataSet1 = makeDataset(1, 1, "D.1", 2, "1", disease);
        dataSets.add(dataSet1);

        final List<DataFile> files1 = queries.getFileInfoForSelectedDataSets(dataSets, false);
        assertEquals(1, files1.size());
        assertEquals("D.1.txt", files1.get(0).getFileName());
        dataSets.clear();

        final DataSetLevelTwoThree dataSet2 = makeDataset(1, 1, "D.1", 2, "2", disease);
        dataSets.add(dataSet2);

        final List<DataFile> files2 = queries.getFileInfoForSelectedDataSets(dataSets, false);
        assertEquals(1, files2.size());
        assertEquals("E.1.txt", files2.get(0).getFileName());
    }

    public void testgetFileInfoForSelectedDataSetsMultipleDiseases()
            throws DataAccessMatrixQueries.DAMQueriesException {

        final String disease1 = "DIS1";
        final String disease2 = "DIS2";
        assertFalse(disease1.equals(disease2));

        final DataSetLevelTwoThree dataSet1 = makeDataset(1, 1, "D.1", 2, "1", disease1);
        final DataSetLevelTwoThree dataSet2 = makeDataset(1, 1, "D.1", 2, "2", disease2);

        final List<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);

        final List<DataFile> dataFiles = queries.getFileInfoForSelectedDataSets(dataSets, false);
        assertEquals(2, dataFiles.size());

        final DataFile firstDataFile = dataFiles.get(0);
        final DataFile secondDataFile = dataFiles.get(1);

        assertNotNull(firstDataFile);
        assertNotNull(secondDataFile);

        final String expectedFilenameForDisease1 = "D.1.txt";
        final String expectedFilenameForDisease2 = "E.1.txt";

        boolean foundDataFileForDisease1 = false;
        boolean foundDataFileForDisease2 = false;

        if(expectedFilenameForDisease1.equals(firstDataFile.getFileName())) {
            foundDataFileForDisease1 = true;
            assertEquals(disease1, firstDataFile.getDiseaseType());
        } else if(expectedFilenameForDisease2.equals(firstDataFile.getFileName())) {
            foundDataFileForDisease2 = true;
            assertEquals(disease2, firstDataFile.getDiseaseType());
        }

        if(expectedFilenameForDisease1.equals(secondDataFile.getFileName())) {
            foundDataFileForDisease1 = true;
            assertEquals(disease1, secondDataFile.getDiseaseType());
        } else if(expectedFilenameForDisease2.equals(secondDataFile.getFileName())) {
            foundDataFileForDisease2 = true;
            assertEquals(disease2, secondDataFile.getDiseaseType());
        }

        assertTrue(foundDataFileForDisease1);
        assertTrue(foundDataFileForDisease2);
    }

    public void testGetFileInfoForSelectedDataSetsConsolidated() throws DataAccessMatrixQueries.DAMQueriesException {
        DAMQueriesLevel2.MAX_IN_CLAUSE = 3;
        final List<DataSet> datasets = queries.getDataSetsForDiseaseType(null);
        final List<DataSet> availableDatasets = new ArrayList<DataSet>();
        for (final DataSet dataset : datasets) {
            if (dataset.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE)) {
                availableDatasets.add(dataset);
            }
        }

        ((DAMQueriesLevel2)queries).setSourceFileTypeFinder(getSourceFileTypeFinder());

        // consolidate is true, so expect consolidated files
        final List<DataFile> files = queries.getFileInfoForSelectedDataSets(availableDatasets, true);
        assertEquals(4, files.size());

        DataFileLevelTwoConsolidated consolidatedFileA = null;
        DataFileLevelTwoConsolidated consolidatedFileB = null;
        DataFileLevelTwoConsolidated consolidatedFileC = null;
        DataFileLevelTwoConsolidated consolidatedFileD = null;

        for (final DataFile dataFile: files) {
            if (dataFile.getFileName().equals("center_platform_type_a.txt")) {
                consolidatedFileA = (DataFileLevelTwoConsolidated) dataFile;
            } else if (dataFile.getFileName().equals("center_platform_type_b.txt")) {
                consolidatedFileB = (DataFileLevelTwoConsolidated) dataFile;
            } else if (dataFile.getFileName().equals("center_platform_type_c.txt")) {
                consolidatedFileC = (DataFileLevelTwoConsolidated) dataFile;
            } else if  (dataFile.getFileName().equals("center_platform_type_d.txt")) {
                consolidatedFileD = (DataFileLevelTwoConsolidated) dataFile;
            } else {
                fail("Unexpected file name: " + dataFile.getFileName());
            }
        }
        assertNotNull(consolidatedFileA);
        DataFileLevelTwo[] constituentDataFiles = consolidatedFileA.getConstituentDataFiles().toArray(new DataFileLevelTwo[3]);

        assertEquals(3, constituentDataFiles.length);
        assertEquals("1", constituentDataFiles[0].getFileId());
        assertEquals("2", constituentDataFiles[1].getFileId());
        assertEquals("3", constituentDataFiles[2].getFileId());

        assertNotNull(consolidatedFileB);
        constituentDataFiles = consolidatedFileB.getConstituentDataFiles().toArray(new DataFileLevelTwo[2]);
        assertEquals(2, constituentDataFiles.length);
        assertEquals("4", constituentDataFiles[0].getFileId());
        assertEquals("5", constituentDataFiles[1].getFileId());

        assertNotNull(consolidatedFileC);
        constituentDataFiles = consolidatedFileC.getConstituentDataFiles().toArray(new DataFileLevelTwo[2]);
        assertEquals(2, constituentDataFiles.length);
        assertEquals("6", constituentDataFiles[0].getFileId());
        assertEquals("7", constituentDataFiles[1].getFileId());

        assertNotNull(consolidatedFileD);
        assertEquals(1, consolidatedFileD.getConstituentDataFiles().size());
        assertEquals("8", consolidatedFileD.getConstituentDataFiles().first().getFileId());
    }

    public void testAddPathToSelectedFiles() throws DataAccessMatrixQueries.DAMQueriesException {
        DAMQueriesLevel2.MAX_IN_CLAUSE = 3;
        List<DataSet> datasets = queries.getDataSetsForDiseaseType("DIS2");
        List<DataSet> availableDatasets = new ArrayList<DataSet>();
        for (final DataSet dataset : datasets) {
            if (dataset.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE)) {
                availableDatasets.add(dataset);
            }
        }

        List<DataFile> files = queries.getFileInfoForSelectedDataSets(availableDatasets, false);
        queries.addPathsToSelectedFiles(files);

        assertEquals(8, files.size());

        List<String> expectedBarcodes = Arrays.asList(new String[] { "A.1", "A.2", "A.3", "B.1", "B.2", "C.1", "C.2","D.1"});
        List<String> expectedFileNames = Arrays.asList(new String[] { "A.1.txt", "A.2.txt", "A.3.txt", "B.1.txt", "B.2.txt", "C.1.txt", "C.2.txt","D.1.txt"});
        List<String> expectedFileLocation = Arrays.asList(new String[] { "/tcgafiles/ftp_auth/A.1.txt",
                "/tcgafiles/ftp_auth/A.2.txt",
                "/tcgafiles/ftp_auth/A.3.txt",
                "/tcgafiles/ftp_auth/B.1.txt",
                "/tcgafiles/ftp_auth/B.2.txt",
                "/tcgafiles/ftp_auth/C.1.txt",
                "/tcgafiles/ftp_auth/C.2.txt",
                "/tcgafiles/ftp_auth/D.1.txt"});
        for(final DataFile dataFile: files){
            assertEquals(1,dataFile.getBarcodes().size());
            final String barcode = dataFile.getBarcodes().toArray(new String[1])[0];
            assertTrue(expectedBarcodes.contains(barcode));
            assertEquals(expectedFileNames.get(expectedBarcodes.indexOf(barcode)),expectedFileNames.get(expectedBarcodes.indexOf(barcode)),dataFile.getFileName());;
            assertEquals(expectedFileLocation.get(expectedBarcodes.indexOf(barcode)),expectedFileLocation.get(expectedBarcodes.indexOf(barcode)),dataFile.getPath());
        }

    }

    @Test
    public void testAddPathToConsolidatedFiles() throws DataAccessMatrixQueries.DAMQueriesException,IOException {
        DAMQueriesLevel2.MAX_IN_CLAUSE = 3;
        List<DataSet> datasets = queries.getDataSetsForDiseaseType(null);
        List<DataSet> availableDatasets = new ArrayList<DataSet>();
        for (final DataSet dataset : datasets) {
            if (dataset.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE)) {
                availableDatasets.add(dataset);
            }
        }
        ((DAMQueriesLevel2)queries).setSourceFileTypeFinder(getSourceFileTypeFinder());

        List<DataFile> files = queries.getFileInfoForSelectedDataSets(availableDatasets, true);
        FileUtil.makeDir(Tmp_Dir);
        queries.setTempfileDirectory(Tmp_Dir);
        List<File> level2Files = createFiles(files);
        queries.addPathsToSelectedFiles(files);

        assertEquals(4, files.size());
        for(final DataFile dataFile: files){
            assertTrue(new File(dataFile.getPath()).exists());
            final String expectedData = getExpectedData(dataFile);
            final File consolidatedFile = new File(dataFile.getPath());
            final String actualData = FileUtil.readFile(consolidatedFile,true);
            assertEquals("Data doesn't match", expectedData,actualData);

        }
        // clean up files
        FileUtil.deleteDir(new File(Tmp_Dir));
        for(final File level2File: level2Files){
            level2File.delete();
        }
    }

    protected void checkDataSet(final Map<String, DataSetLevelTwoThree> barcodeToDataset,
                                final String barcode,
                                final String expectedAvailability) {
        assertEquals(expectedAvailability, barcodeToDataset.get(barcode).getAvailability());
    }

    protected DAMQueriesCGCCLevelTwoAndThree initQueryObject() {
        queries = makeLevel2Queries();
        return queries;
    }

    private List<File> createFiles(final List<DataFile> selectedFiles )throws IOException{
        final List<File> level2Files = new ArrayList<File>();
        for(final DataFile dataFile: selectedFiles){
            // get the selected files
            DataFileLevelTwoConsolidated dataFileLevelTwoConsolidated = (DataFileLevelTwoConsolidated)dataFile;
            for(final DataFile level2DataFile: dataFileLevelTwoConsolidated.getConstituentDataFiles()){
                final String fileName = level2DataFile.getPath().substring(level2DataFile.getPath().lastIndexOf("/")+1);
                final String filePath = Level2_Files_Dir+fileName;
                level2DataFile.setPath(filePath);
                level2Files.add(createLevel2File(filePath,"Test"+fileName,"Probe"+fileName));
            }
        }
        return level2Files;
    }

    private String getExpectedData(final DataFile dataFile){
        final StringBuilder expectedData = new StringBuilder("Hybridization REF");
        // get the header
        for(final DataFile level2DataFile: ((DataFileLevelTwoConsolidated) dataFile).getConstituentDataFiles()){
            final String fileName = level2DataFile.getFileName();
            expectedData.append("\tTest")
            .append(fileName)
            .append("\tTest")
            .append(fileName);
        }
        expectedData.append("\n")
        .append("Composite Element REF");
        // get the minor header
        for(final DataFile level2DataFile: ((DataFileLevelTwoConsolidated) dataFile).getConstituentDataFiles()){
            final String fileName = level2DataFile.getFileName();
            expectedData.append("\tBeta_Value")
            .append("\tMethylated_Signal_Intensity (M)");
        }
        expectedData.append("\n")
        .append("cg00000292");

       // get the probe data
        for(final DataFile level2DataFile: ((DataFileLevelTwoConsolidated) dataFile).getConstituentDataFiles()){
            final String fileName = level2DataFile.getFileName();
            expectedData.append("\tProbe")
            .append(fileName)
            .append("\tProbe")
            .append(fileName);

        }
        return expectedData.toString();
    }

    private File createLevel2File(final String filePath,
                                  final String aliquotBarcode,
                                  final String probeData) throws IOException{
        String data = "Hybridization REF\t" + aliquotBarcode + "\t" + aliquotBarcode+"\n" +
                "Composite Element REF\tBeta_Value\tMethylated_Signal_Intensity (M)\n" +
                "cg00000292\t"+probeData+"\t"+probeData;
        final File level2File = new File(filePath);
        FileUtil.writeContentToFile(data,level2File);
        return level2File;
    }

    private DAMQueriesLevel2 makeLevel2Queries() {
        DAMQueriesLevel2 queries = new DAMQueriesLevel2() {
             @Override
             protected List<DataSet> buildInitialList(final String diseaseType, final boolean forControls) {
                 if (forControls) {
                     return makeInitialControlDataSets();
                 } else {
                     return makeInitialDatasets();
                 }
             }

            @Override
            protected int getAverageValueSize() {
                return 9;
            }

            @Override
            protected String getSampleFromBarcode(final String barcode) {
                return barcode;
            }

            @Override
            protected String getUniqueFilename(final DataFileLevelTwoThree dataFile) {
                return dataFile.getSourceFileType() + ".txt";
            }

            @Override
            protected void insertTempHybrefIds(final Collection<Long> ids) {
                super.insertTempHybrefIds(ids);
                List<Long> foundIds = fetchIds(this);
                assertEquals(ids.size(), foundIds.size());
            }
            @Override
            protected void insertTempDataSetIds(final Collection<Integer> ids) {
                super.insertTempDataSetIds(ids);
                List<Integer> foundDsIds = fetchDsIds(this);
                assertEquals(ids.size(), foundDsIds.size());
            }
         };
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(getDataSource());
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        queries.setTransactionTemplate(template);
        return queries;
    }

    protected int getDataLevel() {
        return 2;
    }

    // this seems to solve the Oracle XE TNS Listener errors...
    private static OracleDataSource ods;

    @Override
    protected void initDataSource() throws SQLException {
        if (ods == null) {
            ods = new OracleDataSource();
            ods.setUser(userName);
            ods.setPassword(password);
            ods.setURL(connectionURL);
            ods.setConnectionCachingEnabled(true);
        }
        dataSource = ods;
    }

    private SourceFileTypeFinder getSourceFileTypeFinder(){
        return new SourceFileTypeFinder() {
            @Override
            public String findSourceFileType(final long fileId) throws IOException {
                if (fileId == 1 || fileId == 2 || fileId == 3) {
                    return "type_a";
                } else if (fileId == 4 || fileId == 5) {
                    return "type_b";
                } else if (fileId == 6 || fileId == 7) {
                    return "type_c";
                } else if (fileId == 8) {
                    return "type_d";
                } else {
                    return null;
                }
            }
        };

    }


}
