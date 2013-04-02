/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationClassification;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMetadata;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Test class for DAMQueriesMetadata
 *
 * @author Jessica Chen Last updated by: Jeyanthi Thangiah
 * @version $Rev$
 */

public class DAMQueriesMetadataDBUnitSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "tcga_unittest.properties";
    protected static final String SAMPLES_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    protected static final String METADATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator
                    + "portal" + File.separator + "dao" + File.separator + "metadata";
    private static final String TEST_DATA_FILE = "/portal/dao/metadata/DAMQueriesMetadata_TestDB.xml";

    private DAMQueriesMetadata queries;
    private AnnotationQueries annotationQueries;
    private final Mockery context = new JUnit4Mockery();
    private String[] expectedFiles = new String[]{"sampleAnnotation.txt"};

    public DAMQueriesMetadataDBUnitSlowTest() throws IOException {
        super(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    public void setUp() throws Exception {
        super.setUp();
        queries = new DAMQueriesMetadata() {
            @Override
            protected String getTmpFilePath() throws IOException {
                return METADATA_FOLDER + File.separator + "sampleAnnotation.txt";
            }
        };
        annotationQueries = context.mock(AnnotationQueries.class);
        queries.setAnnotationQueries(annotationQueries);
        queries.setDataSource(getDataSource());
        queries.setTempFileDirectory(METADATA_FOLDER);
        queries.setDamUtils(DAMUtils.getInstance());
    }

    public void testGetFileInfoClassicArchive() throws DataAccessMatrixQueries.DAMQueriesException {
        context.checking(new Expectations() {{
            one(annotationQueries).getAllAnnotationsCountForSamples(with(any(List.class)));
            will(returnValue(new Long(makeAnnotations().size())));
        }});
        List<DataSet> datasets = new ArrayList<DataSet>();
        DataSet dataSet = new DataSetLevelTwoThree();
        dataSet.setArchiveId(1); // archive 1 is the classic archive
        dataSet.setSample("TCGA-01-1234-01");
        datasets.add(dataSet);

        List<DataFile> datafiles = queries.getFileInfoForSelectedDataSets(datasets, false);

        assertEquals(2, datafiles.size());

        //assertEquals("sampleAnnotation.txt", datafiles.get(0).getFileName());
        //assertEquals(109, datafiles.get(0).getSize());

        assertEquals("2", datafiles.get(0).getFileId());  // the sdrf file in archive 1
        assertEquals("test.sdrf.txt", datafiles.get(0).getFileName());
        assertEquals(15, datafiles.get(0).getSize());
        assertEquals("3", datafiles.get(1).getFileId());  // the idf file in archive 1
        assertEquals("test.idf.txt", datafiles.get(1).getFileName());
        assertEquals(3, datafiles.get(1).getSize());
    }

    public void testGetFileInfoNewArchive() throws DataAccessMatrixQueries.DAMQueriesException {
        context.checking(new Expectations() {{
            one(annotationQueries).getAllAnnotationsCountForSamples(with(any(List.class)));
            will(returnValue(new Long(makeAnnotations().size())));
        }});
        List<DataSet> datasets = new ArrayList<DataSet>();
        DataSet dataSet = new DataSetLevelTwoThree();
        dataSet.setArchiveId(2); // archive 2 is the new format data archive
        dataSet.setSample("TCGA-01-1234-01");
        datasets.add(dataSet);

        List<DataFile> datafiles = queries.getFileInfoForSelectedDataSets(datasets, false);

        assertEquals(2, datafiles.size());

        //assertEquals(109, datafiles.get(0).getSize());
        //assertEquals("sampleAnnotation.txt", datafiles.get(0).getFileName());

        assertEquals("6", datafiles.get(0).getFileId());  // the sdrf file in archive 3
        assertEquals(7, datafiles.get(0).getSize());
        assertEquals("mage-tab.sdrf.txt", datafiles.get(0).getFileName());
        assertEquals("7", datafiles.get(1).getFileId());  // the idf file in archive 3
        assertEquals(9, datafiles.get(1).getSize());
        assertEquals("mage-tab.idf.txt", datafiles.get(1).getFileName());
    }

    public void testGetFileInfoMultipleDiseases() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> datasets = new ArrayList<DataSet>();
        final DataSet dataSet1 = new DataSetLevelTwoThree();
        dataSet1.setCenterId("1");
        dataSet1.setArchiveId(1);
        dataSet1.setBarcodes(Arrays.asList("TCGA-01-1234-01"));
        dataSet1.setSample("TCGA-01-1234-01");
        dataSet1.setDiseaseType("GBM");

        final DataSet dataSet2 = new DataSetLevelTwoThree();
        dataSet2.setCenterId("2");
        dataSet2.setArchiveId(1);
        dataSet2.setBarcodes(Arrays.asList("TCGA-01-1234-01"));
        dataSet2.setSample("TCGA-01-1234-02");
        dataSet2.setDiseaseType("BLCA");

        datasets.add(dataSet1);
        datasets.add(dataSet2);

        final List<DataFile> datafiles = queries.getFileInfoForSelectedDataSets(datasets, false);

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
                assertEquals("GBM", datafile.getDiseaseType());
            }
            if(datafile.getCenterId().equals("2")) {
                assertEquals("BLCA", datafile.getDiseaseType());
            }
        }
    }

    private List<DccAnnotation> makeAnnotations() {
        final List<DccAnnotation> res = new LinkedList<DccAnnotation>();
        final DccAnnotation myAnnotation = new DccAnnotation();
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        final Long itemTypeId = 2L;
        itemType.setItemTypeId(itemTypeId);
        itemType.setItemTypeName("my item type");
        final Tumor disease = new Tumor();
        final Integer tumorId = 2;
        disease.setTumorId(tumorId);
        disease.setTumorName("GBM");
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();

        dccAnnotationItem.setItemType(itemType);
        final String item = "my item barcode";
        dccAnnotationItem.setItem(item);
        dccAnnotationItem.setDisease(disease);
        myAnnotation.addItem(dccAnnotationItem);

        final DccAnnotationCategory category = new DccAnnotationCategory();
        final DccAnnotationClassification classification = new DccAnnotationClassification();
        classification.setAnnotationClassificationName("my classification");
        final Long categoryId = 3L;
        category.setCategoryName("my category");
        category.setCategoryId(categoryId);
        category.setAnnotationClassification(classification);
        myAnnotation.setAnnotationCategory(category);
        final Date inThePast = new Date(123456789);
        myAnnotation.setDateCreated(inThePast);
        final String user = "me";
        final String note = "note";
        myAnnotation.addNote(note, user, inThePast);
        myAnnotation.setCreatedBy(user);
        final Long annotationId = 10L;
        myAnnotation.setId(annotationId);
        res.add(myAnnotation);
        return res;
    }

    public void testAddPathsMetadata() throws DataAccessMatrixQueries.DAMQueriesException {
        DataFile sdrfFile = new DataFileMetadata();
        sdrfFile.setFileId("6");
        sdrfFile.setFileName("mage-tab.sdrf.txt");
        sdrfFile.setDiseaseType("TEST");

        queries.addPathsToSelectedFiles(Arrays.asList(sdrfFile));
        assertEquals("test/location/mage-tab.sdrf.txt", sdrfFile.getPath());
        assertEquals("TEST", DiseaseContextHolder.getDisease());
    }

//    public void testAddPathsToSelectedFiles() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
//        try {
//            final List<DataSet> dataSets = new ArrayList<DataSet>();
//            final DataSet dataSet1 = new DataSet();
//            dataSet1.setSample("TCGA-01-1234-01");
//            final DataSet dataSet2 = new DataSet();
//            dataSet2.setSample("TCGA-02-5678-01");
//            dataSets.add(dataSet1);
//            dataSets.add(dataSet2);
//            context.checking(new Expectations() {{
//                one(annotationQueries).getAllAnnotationsCountForSamples(with(any(List.class)));
//                will(returnValue(new Long(makeAnnotations().size())));
//                one(annotationQueries).getAllAnnotationsForSamples(with(any(List.class)));
//                will(returnValue(makeAnnotations()));
//            }});
//            List<DataFile> dataFiles = queries.getFileInfoForSelectedDataSets(dataSets, true);
//            queries.addPathsToSelectedFiles(dataFiles);
//            for (final String expectedFile : expectedFiles) {
//                compareFiles(METADATA_FOLDER + File.separator + "expected" + File.separator + expectedFile,
//                        METADATA_FOLDER + File.separator + expectedFile);
//            }
//        } finally {
//            for (final String expectedFile : expectedFiles) {
//                File generatedFile = new File(METADATA_FOLDER + File.separator + expectedFile);
//                if (generatedFile.exists()) {
//                    generatedFile.deleteOnExit();
//                }
//            }
//        }
//    }
//
//    protected void compareFiles(final String fname1, final String fname2) throws IOException {
//
//        BufferedReader r1 = null;
//        BufferedReader r2 = null;
//
//        try {
//            File f1 = new File(fname1);
//            File f2 = new File(fname2);
//            //noinspection IOResourceOpenedButNotSafelyClosed
//            r1 = new BufferedReader(new FileReader(f1));
//            //noinspection IOResourceOpenedButNotSafelyClosed
//            r2 = new BufferedReader(new FileReader(f2));
//            String l1, l2;
//            while ((l1 = r1.readLine()) != null) {
//                l2 = r2.readLine();
//                assertEquals(fname1, l1, l2);
//            }
//            l2 = r2.readLine();
//            assertNull("File " + fname2 + " had more lines than expected: " + l2, l2);
//        } finally {
//            IOUtils.closeQuietly(r1);
//            IOUtils.closeQuietly(r2);
//        }
//    }
}

