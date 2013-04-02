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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

/**
 * DB Unit test for Level 1 queries DAO.
 *
 * @author Jessica Chen Last updated by: Jeyanthi Thangiah
 * @version $Rev$
 */
public class DAMQueriesCGCCLevel1DBUnitSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "tcga_unittest.properties";
    private static final String TEST_DATA_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "/portal/dao/Level1_TestDB.xml";

    private DAMQueriesCGCCLevel1 queries;

    public DAMQueriesCGCCLevel1DBUnitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Test
    public void testGetDataSetsForDiseaseType() throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> dataSets = queries.getDataSetsForDiseaseType("TUM");
        assertNotNull(dataSets);
        assertEquals(7, dataSets.size());
        int numDataSetsFor11 = 0; // expect 4, status = available
        int numDataSetsFor13 = 0;  // expect 2, status = not avail
        int numDataSetsFor15 = 0; // expect 1
        int numDataSetsFor14 = 0; // expect 1
        for (final DataSet dataSet : dataSets) {
            if (dataSet.getLevel().equals("1") || dataSet.getLevel().equals("2")) {
                assertTrue(dataSet.isProtected());
            } else if (dataSet.getLevel().equals("3")) {
                assertFalse(dataSet.isProtected());
            } else {
                fail("Unexpected level:" + dataSet.getLevel());
            }

            if (dataSet.getArchiveId() == 11) {
                numDataSetsFor11++;
                assertEquals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, dataSet.getAvailability());
            } else if (dataSet.getArchiveId() == 13) {
                numDataSetsFor13++;
                assertEquals(DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE, dataSet.getAvailability());
            } else if (dataSet.getArchiveId() == 15) {
                numDataSetsFor15++;
                assertEquals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, dataSet.getAvailability());
            } else if (dataSet.getArchiveId() == 14) {
                numDataSetsFor14++;
                assertEquals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE, dataSet.getAvailability());
            } else {
                fail("Data Set for unexpected archive");
            }
        }
        assertEquals(3, numDataSetsFor11);
        assertEquals(2, numDataSetsFor13);
        assertEquals(1, numDataSetsFor14);
        assertEquals(1, numDataSetsFor15);
    }

    @Test
    public void testGetDataSetsForControls() throws DataAccessMatrixQueries.DAMQueriesException {
        List<String> diseaseTypes = Arrays.asList("TUM");
        List<DataSet> dataSets = queries.getDataSetsForControls(diseaseTypes);
        assertNotNull(dataSets);
        assertEquals(1, dataSets.size());
        assertEquals("TEST-00-0004-00", dataSets.get(0).getSample());
    }

    @Test
    public void testGetFileInfoForSelectedDataSets() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> selectedDataSets = new ArrayList<DataSet>();
        DataSetLevelOne dataSet = new DataSetLevelOne();
        dataSet.setArchiveId(11);
        dataSet.setPlatformId("1");
        dataSet.setCenterId("1");
        dataSet.setProtected(true);
        dataSet.setSample("hello");
        dataSet.setLevel("1"); // really?  we have to set the level on a DataSetLevelOne object?  I object.
        final List<String> barcodes = new ArrayList<String>();
        barcodes.add("TCGA-00-0001-00");
        barcodes.add("TCGA-00-0002-00");
        dataSet.setBarcodes(barcodes);
        selectedDataSets.add(dataSet);

        List<DataFile> files = queries.getFileInfoForSelectedDataSets(selectedDataSets, false);
        assertEquals(2, files.size()); // one file per barcode in dataset
        assertEquals("file2.txt", files.get(0).getFileName());
        assertEquals("2", files.get(0).getFileId());
        assertEquals("file1.txt", files.get(1).getFileName());
        assertEquals("1", files.get(1).getFileId());
        assertEquals("hello/hello", files.get(0).getDisplaySample());
        assertEquals(files.get(0).getBarcodes(), dataSet.getBarcodes());

        assertEquals("hello/hello", files.get(1).getDisplaySample());
        assertEquals(files.get(1).getBarcodes(), dataSet.getBarcodes());

        assertTrue(files.get(0).isProtected());
        assertTrue(files.get(1).isProtected());
    }

    @Test
    public void testGetFileInfoForSelectedDataSetsMultipleDiseases() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> dataSets = new ArrayList<DataSet>();
        final DataSet ds1 = makeDataSet("1", "1", "TCGA-00-0001-00", "DIS1");
        final DataSet ds2 = makeDataSet("1", "2", "TCGA-00-0002-00", "DIS2");
        dataSets.add(ds1);
        dataSets.add(ds2);

        final List<DataFile> datafiles = queries.getFileInfoForSelectedDataSets(dataSets, false);
        assertEquals(4, datafiles.size());

        final DataFile firstDataFile = datafiles.get(0);
        final DataFile secondDataFile = datafiles.get(1);
        final DataFile thirdDataFile = datafiles.get(2);
        final DataFile fourthDataFile = datafiles.get(3);

        assertNotNull(firstDataFile);
        assertNotNull(secondDataFile);
        assertNotNull(thirdDataFile);
        assertNotNull(fourthDataFile);

        for(final DataFile datafile : datafiles) {
            if(datafile.getCenterId().equals("1")) {
                assertEquals("DIS1", datafile.getDiseaseType());
            }
            if(datafile.getCenterId().equals("2")) {
                assertEquals("DIS2", datafile.getDiseaseType());
            }
        }
    }

    @Test
    public void testGetFileInfoForSelectedDataSetsMultiplatformBarCode() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> selectedDataSets = new ArrayList<DataSet>();
        final DataSetLevelOne dataSet = new DataSetLevelOne();
        dataSet.setArchiveId(14);
        dataSet.setPlatformId("1");
        dataSet.setCenterId("2");
        dataSet.setProtected(true);
        dataSet.setSample("hello");
        dataSet.setLevel("1");
        dataSet.setBarcodes(Arrays.asList("TCGA-00-0006-00"));
        selectedDataSets.add(dataSet);
        final List<DataFile> files1 = queries.getFileInfoForSelectedDataSets(selectedDataSets, false);
        assertEquals(1, files1.size());
        assertEquals("aaa.txt", files1.get(0).getFileName());

        dataSet.setPlatformId("3");
        final List<DataFile> files2 = queries.getFileInfoForSelectedDataSets(selectedDataSets, false);
        assertEquals(1, files2.size());
        assertEquals("bbb.txt", files2.get(0).getFileName());
    }

    @Test
    public void testAddPathsToSelectedFiles() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataFile> selectedFiles = new ArrayList<DataFile>();
        DataFileLevelOne level1File = new DataFileLevelOne();
        level1File.setFileId("1");
        level1File.setDiseaseType("TEST");
        DataFileLevelTwo level2File = new DataFileLevelTwo();
        level2File.setFileId("squirrel");
        DataFileLevelThree level3File = new DataFileLevelThree();
        level3File.setFileId("chipmunk");
        selectedFiles.add(level1File);
        selectedFiles.add(level2File);
        selectedFiles.add(level3File);

        queries.addPathsToSelectedFiles(selectedFiles);
        // paths should only be set on level 1 files
        assertEquals("test/file1.txt", level1File.getPath());
        assertNull(level2File.getPath());
        assertNull(level3File.getPath());
        assertEquals("TEST", DiseaseContextHolder.getDisease());
    }

    @Test
    public void testGetSubmittedSampleIds() throws DataAccessMatrixQueries.DAMQueriesException {
        Set<String> samples = queries.getSubmittedSampleIds("TUM");
        assertEquals(3, samples.size());
        assertTrue(samples.contains("2|1|TEST-00-0001-00"));
        assertTrue(samples.contains("2|1|TEST-00-0002-00"));
        assertTrue(samples.contains("2|1|TEST-00-0003-00"));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new DAMQueriesCGCCLevel1();
        queries.setDataSource(getDataSource());
        queries.setDamUtils(DAMUtils.getInstance());
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
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }
    
    private DataSet makeDataSet(final String platformId, final String centerId, final String barcode, final String diseaseType) {
        DataSetLevelOne retDataSet = new DataSetLevelOne();
        retDataSet.setPlatformId(platformId);
        retDataSet.setCenterId(centerId);
        retDataSet.setBarcodes(Arrays.asList(barcode));
        retDataSet.setDiseaseType(diseaseType);
        return retDataSet;
    }

}
