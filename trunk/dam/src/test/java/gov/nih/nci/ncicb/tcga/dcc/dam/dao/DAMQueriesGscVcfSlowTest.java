/*
 *
 *  * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 *  * Copyright Notice.  The software subject to this notice and license includes both human
 *  * readable source code form and machine readable, binary, object code form (the "caBIG
 *  * Software").
 *  *
 *  * Please refer to the complete License text for full details at the root of the project.
 *
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetGscVcf;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetMutation;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Slow test for GscVcf DAM Queries implementation.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesGscVcfSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "tcga_unittest.properties";
    private static final String TEST_DATA_FOLDER =
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "/portal/dao/GscVcf_TestDB.xml";

    private DAMQueriesGscVcf damQueriesGscVcf;

    public DAMQueriesGscVcfSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        damQueriesGscVcf = new DAMQueriesGscVcf();
        damQueriesGscVcf.setDamUtils(DAMUtils.getInstance());
        damQueriesGscVcf.setDataSource(getDataSource());
    }

    public void testGetDataSetsForDisease() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> diseaseDataSets = damQueriesGscVcf.getDataSetsForDiseaseType("DIS1");
        assertEquals(6, diseaseDataSets.size());

        final List<String> sampleBarcodes = new ArrayList<String>();
        for (final DataSet dataSet : diseaseDataSets) {
            assertTrue(dataSet instanceof DataSetGscVcf);
            sampleBarcodes.add(dataSet.getSample());
            assertEquals("2", dataSet.getLevel());
            if (dataSet.getPlatformId().equals("1")) {
                assertFalse(dataSet.isProtected());
                assertEquals("1", dataSet.getPlatformTypeId());

            } else if (dataSet.getPlatformId().equals("2")) {
                assertTrue(dataSet.isProtected());
                assertEquals("2", dataSet.getPlatformTypeId());
            }
        }
        assertTrue(sampleBarcodes.contains("TCGA-00-1111-01"));
        assertTrue(sampleBarcodes.contains("TCGA-00-1111-10"));
        assertTrue(sampleBarcodes.contains("TCGA-00-2222-01"));
        assertTrue(sampleBarcodes.contains("TCGA-00-2222-10"));
        assertTrue(sampleBarcodes.contains("TCGA-00-1111-01"));
        assertTrue(sampleBarcodes.contains("TCGA-00-5555-01"));
        assertTrue(sampleBarcodes.contains("TCGA-00-5555-10"));
    }

    public void testGetDataSetsForControl() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> controlDataSets = damQueriesGscVcf.getDataSetsForControls(Arrays.asList("DIS1", "DIS2"));
        assertEquals(1, controlDataSets.size());
        assertEquals("TCGA-07-7777-20", controlDataSets.get(0).getSample());
        assertFalse(controlDataSets.get(0).isProtected());
    }

    public void testGetFilesForSelectedDataSets() throws DataAccessMatrixQueries.DAMQueriesException {

        final List<DataSet> dataSets = damQueriesGscVcf.getDataSetsForDiseaseType("DIS1");
        // add some data sets that we should ignore because they are for a different DAO
        dataSets.add(new DataSetLevelOne());
        dataSets.add(new DataSetMutation());
        dataSets.add(new DataSetLevelTwoThree());

        final List<DataFile> dataFiles = damQueriesGscVcf.getFileInfoForSelectedDataSets(dataSets, false);
        assertEquals(3, dataFiles.size());
        for (final DataFile dataFile : dataFiles) {
            assertEquals("DIS1", dataFile.getDiseaseType());
            assertEquals("2", dataFile.getLevel());
            assertEquals("1", dataFile.getCenterId());
            assertTrue(dataFile.isPermanentFile());

            if (dataFile.getFileName().equals("file1.vcf")) {
                assertEquals(2, dataFile.getBarcodes().size());
                assertTrue(dataFile.getBarcodes().contains("TCGA-00-1111-01A-01W-1111-99"));
                assertTrue(dataFile.getBarcodes().contains("TCGA-00-1111-10A-01W-1111-99"));
                assertEquals("2", dataFile.getPlatformId());
                assertEquals("2", dataFile.getPlatformTypeId());
                assertEquals("1", dataFile.getFileId());
                assertTrue(dataFile.isProtected());

            } else if (dataFile.getFileName().equals("file2.vcf")) {
                assertEquals(2, dataFile.getBarcodes().size());
                assertTrue(dataFile.getBarcodes().contains("TCGA-00-2222-01A-01W-1111-99"));
                assertTrue(dataFile.getBarcodes().contains("TCGA-00-2222-10A-01W-1111-99"));
                assertEquals("2", dataFile.getPlatformId());
                assertEquals("2", dataFile.getPlatformTypeId());
                assertEquals("3", dataFile.getFileId());
                assertTrue(dataFile.isProtected());

            } else if (dataFile.getFileName().equals("somatic.vcf")) {
                assertEquals(2, dataFile.getBarcodes().size());
                assertTrue(dataFile.getBarcodes().contains("TCGA-00-5555-01A-01W-1111-99"));
                assertTrue(dataFile.getBarcodes().contains("TCGA-00-5555-10A-01W-1111-99"));
                assertEquals("1", dataFile.getPlatformId());
                assertEquals("1", dataFile.getPlatformTypeId());
                assertEquals("7", dataFile.getFileId());
                assertFalse(dataFile.isProtected());

            } else {
                fail("Unexpected data file: " + dataFile.getFileName());
            }
        }
    }

    public void testAddPathsToFiles() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> dataSets = damQueriesGscVcf.getDataSetsForDiseaseType("DIS1");
        final List<DataFile> dataFiles = damQueriesGscVcf.getFileInfoForSelectedDataSets(dataSets, false);

        damQueriesGscVcf.addPathsToSelectedFiles(dataFiles);
        
        for (final DataFile dataFile : dataFiles) {
            if (dataFile.getFileId().equals("1")) {
                assertEquals("/fake/path/to/archive/file1.vcf", dataFile.getPath());
            } else if (dataFile.getFileId().equals("3")) {
                assertEquals("/fake/path/to/archive/file2.vcf", dataFile.getPath());
            } else if (dataFile.getFileId().equals("7")) {
                assertEquals("/public/archive/somatic.vcf", dataFile.getPath());
            } else {
                fail("Unexpected data file id: " + dataFile.getFileId());
            }
        }
    }

}
