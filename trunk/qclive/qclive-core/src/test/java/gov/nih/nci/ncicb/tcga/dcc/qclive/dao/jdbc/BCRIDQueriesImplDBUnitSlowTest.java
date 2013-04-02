/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAOImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.CenterQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.TumorQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.UUIDTypeQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDServiceImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessorImpl;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Slow test for BCRIDQueriesImpl JDBC implementation.
 *
 * @author Jeyanthi Thangiah Last updated by: $Author$
 * @version $Rev$
 */
public class BCRIDQueriesImplDBUnitSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "common.unittest.properties";
    private static final String TEST_DATA_FOLDER = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private static final String TEST_DATA_FILE = "/qclive/biospecimen_TestData.xml";

    private final Mockery mockery = new JUnit4Mockery();
    private UUIDDAO mockUuidDAO = mockery.mock(UUIDDAO.class);
    private UUIDTypeQueriesJDBCImpl uuidTypeQueries;
    private CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator;
    private BCRIDQueriesImpl bcridQueries;
    private BCRIDProcessor bcridProcessorForParsing = new BCRIDProcessorImpl();
    private SimpleJdbcTemplate sjdbc;
    private Tumor disease;

    public BCRIDQueriesImplDBUnitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        bcridQueries = new BCRIDQueriesImpl();
        bcridQueries.setDataSource(getDataSource());
        CenterQueriesJDBCImpl centerQueries = new CenterQueriesJDBCImpl();
        centerQueries.setDataSource(getDataSource());
        bcridQueries.setCenterQueries(centerQueries);
        UUIDServiceImpl uuidService = new UUIDServiceImpl();
        UUIDDAOImpl uuidDAO = new UUIDDAOImpl();
        uuidService.setCenterQueries(centerQueries);
        uuidService.setUuidDAO(uuidDAO);
        uuidService.setTumorQueries(new TumorQueriesJDBCImpl());
        uuidDAO.setDataSource(getDataSource());
        uuidTypeQueries = new UUIDTypeQueriesJDBCImpl();
        uuidTypeQueries.setDataSource(getDataSource());
        commonBarcodeAndUUIDValidator = new CommonBarcodeAndUUIDValidatorImpl();
        uuidDAO.setCommonBarcodeAndUUIDValidator(commonBarcodeAndUUIDValidator);
        uuidDAO.setUuidTypeQueries(uuidTypeQueries);
        bcridQueries.setUuidService(uuidService);
        bcridQueries.setUuidDAO(uuidDAO);
        sjdbc = new SimpleJdbcTemplate(bcridQueries.getDataSource());
        disease = new Tumor();
        disease.setTumorId(1);
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        sjdbc.update("delete from barcode_history");
        sjdbc.update("delete from uuid");
        sjdbc.update("delete from biospecimen_to_file");
        sjdbc.update("delete from shipped_biospecimen_file");
        return DatabaseOperation.DELETE_ALL;
    }

    @Test
    public void testGetAllBCRIDs() {
        Collection bcrIDs = bcridQueries.getAllBCRIDs();
        assertNotNull(bcrIDs);
        assertTrue(bcrIDs.iterator().hasNext());
    }

    public void testAddBCRID() {
        BCRID addBCRId = createmockBCRID();
        addBCRId.setFullID("TCGA-02-0001-01C-01R-0178-07");
        int testId = bcridQueries.addBCRID(addBCRId, false);
        int curr_seq = sjdbc
                .queryForInt("SELECT biospecimen_barcode_seq.CURRVAL FROM DUAL");
        assertEquals(testId, curr_seq);
    }

    public void testUpdateBCRIDStatus() {
        BCRID fakeBCRId = createmockBCRID();
        fakeBCRId.setFullID("TCGA-02-0001-01C-01R-0178-03");
        fakeBCRId.setValid(1);
        int update_BCR = bcridQueries.updateBCRIDStatus(fakeBCRId);
        assertEquals(1, update_BCR);
    }

    public void testGetUUIDForBarcode() {
        BCRID mockBCRId = createmockBCRID();
        mockBCRId.setFullID("TCGA-02-0001-01C-01R-0178-02");
        bcridQueries.addBCRID(mockBCRId, false);
        String testUUID = bcridQueries.getBiospecimenUUID(mockBCRId);
        assertEquals(testUUID, "2dad642f-95ac-4008-989e-b5630757c88b");
    }

    public void testUpdateUUIDForBarcode() {
        BCRID mockBCRId = createmockBCRID();
        mockBCRId.setFullID("TCGA-02-0001-01C-01R-0178-01");
        bcridQueries.addBCRID(mockBCRId, false);
        mockBCRId.setUUID("2ccec413-4bd8-4398-b457-707650415301");
        bcridQueries.updateUUIDForBarcode(mockBCRId);
        String testUUID = bcridQueries.getBiospecimenUUID(mockBCRId);
        assertEquals(testUUID, "2ccec413-4bd8-4398-b457-707650415301");
    }

    public void testUpdateShipDate() {
        BCRID fakeBCRId = createmockBCRID();
        fakeBCRId.setId(6951);
        fakeBCRId.setShippingDate("2010-06-12");
        bcridQueries.updateShipDate(fakeBCRId);
        Date testDate = null;
        try {
            testDate = new SimpleDateFormat("yyyy-MM-dd").parse("2010-06-12");
        } catch (ParseException e) {
            System.out.println("Invalid Date Parser Exception ");
            e.printStackTrace();
        }
        int test_plate = sjdbc.queryForInt(
                "SELECT plate_id from biospecimen_barcode where ship_date=?",
                testDate);
        assertEquals(test_plate, 178);
    }

    public void testAddArchiveRelationship() {
        BCRID fakeBCRId = createmockBCRID();
        fakeBCRId.setId(6951);
        bcridQueries.addArchiveRelationship(fakeBCRId, false, new int[]{-1});

    }

    public void testFileAssociation() {
        int testData = bcridQueries.addFileAssociation(1L, 6951, "Image File",
                false, -1);
        int dbData = bcridQueries.findExistingAssociation(1L, 6951,
                "Image File");
        assertEquals(dbData, testData);

        Long fileId = sjdbc.queryForLong("select file_id from shipped_biospecimen_file where shipped_biospecimen_id = ?", 6951);
        assertNotNull(fileId);
        assertEquals(1L, fileId.longValue());

        sjdbc.update(
                "delete from biospecimen_to_file where biospecimen_File_id= ?",
                dbData);
        sjdbc.update("delete from shipped_biospecimen_file where shipped_biospecimen_id = ?", 6951);
    }

    public void testAddFileAssociationWhenAlreadyExists() {

        final Long fileId = 190564L;
        final Integer barcodeId = 254760;
        final String colName = "test col";
        final Boolean useIdFromCommon = false;
        final Object[] args = new Object[]{barcodeId, fileId};
        final String countSql = "select count(*) from shipped_biospecimen_file where shipped_biospecimen_id = ? and file_id = ?";
        final int countBefore = sjdbc.queryForInt(countSql, args);
        assertEquals(1, countBefore);

        bcridQueries.addFileAssociation(fileId, barcodeId, colName, useIdFromCommon, -1);

        final int countAfter = sjdbc.queryForInt(countSql, args);
        assertEquals(1, countAfter);
    }

    public void testAddBioSpecimenToFileAssociations() throws Exception {
        final List<BiospecimenToFile> biospecimentToFileTestDataList = createmockBiospecimenToFileObjects();

        final ITable fileInfoTable = getDataSet().getTable("file_info");
        final List<Integer> biospecimenFileIdTestDataList = new ArrayList<Integer>();
        for (int i = 0; i < fileInfoTable.getRowCount(); i++) {
            biospecimenFileIdTestDataList.add(Integer
                    .valueOf((String) fileInfoTable.getValue(i, "file_id")));
        }

        bcridQueries
                .addBioSpecimenToFileAssociations(biospecimentToFileTestDataList);

        final List<Integer> biospecimenFileIdDBDataList = new BiospecimenIdQuery(
                getDataSource(), "select file_id from biospecimen_to_file")
                .execute();
        assertEquals(15, biospecimenFileIdDBDataList.size());
        for (Integer dbFileId : biospecimenFileIdTestDataList) {
            assertTrue(biospecimenFileIdDBDataList.contains(dbFileId));
        }
        sjdbc.update("delete from biospecimen_to_file");

    }

    public void testUpdateBioSpecimenToFileAssociations() throws Exception {
        final List<BiospecimenToFile> biospecimenToFileTestDataList = createmockBiospecimenToFileObjects();
        for (BiospecimenToFile biospecimen : biospecimenToFileTestDataList) {
            if (biospecimen.getFileId() == 10000) {
                biospecimen.setBiospecimenId(254760);
            }
        }

        bcridQueries.addBioSpecimenToFileAssociations(biospecimenToFileTestDataList);

        final List<BiospecimenToFile> biospecimenUpdateTestData = new ArrayList<BiospecimenToFile>();
        for (BiospecimenToFile biospecimen : biospecimenToFileTestDataList) {
            if (biospecimen.getFileId() == 10000) {
                biospecimen.setFileId(190564L);
                biospecimen.setOldFileId(10000L);
                biospecimenUpdateTestData.add(biospecimen);
            }
        }

        bcridQueries.updateBiospecimenToFileAssociations(biospecimenUpdateTestData);

        final List<Integer> biospecimenToFileIdDBDataList = new BiospecimenIdQuery(getDataSource(), "select file_id from biospecimen_to_file where biospecimen_id = 254760").execute();
        assertEquals(2, biospecimenToFileIdDBDataList.size());
        assertEquals(new Integer(190564), biospecimenToFileIdDBDataList.get(0));

        sjdbc.update("delete from biospecimen_to_file");
    }

    public void testAddBioSpecimenBarcodes() throws Exception {
        final List<BCRID> bcrIds = createmockBCRIDs();
        bcridQueries.addBioSpecimenBarcodes(createmockBCRIDs(), disease);

        List<String> barcodesDBData = sjdbc.query(
                "select barcode from biospecimen_barcode",
                new ParameterizedRowMapper<String>() {
                    public String mapRow(final ResultSet resultSet,
                                         final int rowNum) throws SQLException {
                        return resultSet.getString("barcode");
                    }
                });
        assertTrue(barcodesDBData.size() > 0);
        List<String> barcodesTestData = new ArrayList<String>();
        for (final BCRID bcrId : bcrIds) {
            barcodesTestData.add(bcrId.getFullID());
            // make sure there is a barcode history entry for this barcode, and
            // it matches the biospecimen uuid
            String uuid = sjdbc.queryForObject(
                    "select uuid from barcode_history where barcode=?",
                    String.class, bcrId.getFullID());
            String biospecimenUUID = sjdbc.queryForObject(
                    "select uuid from biospecimen_barcode where barcode=?",
                    String.class, bcrId.getFullID());
            assertEquals(uuid, biospecimenUUID);
        }
        assertTrue(barcodesDBData.containsAll(barcodesTestData));
    }

    public void testAddBioSpecimenBarcodesExistingUUIDs() throws UUIDException {
        BCRID bcrId = createmockBCRID();
        bcrId.setFullID("TCGA-11-2222-33A-44W-5555-66");
        List<BCRID> bcrIds = Arrays.asList(bcrId);
        bcridQueries.addBioSpecimenBarcodes(bcrIds, disease);
        // this should have been added to the biospecimen_barcode table
        assertEquals(
                1,
                sjdbc.queryForInt("select count(*) from biospecimen_barcode where barcode='TCGA-11-2222-33A-44W-5555-66'"));
    }

    public void testGetBiospecimenId() throws Exception {
        final BCRID bcrIdTestData = getBCRIDTestData();
        final List<String> barcodesTestData = new ArrayList<String>();
        List<Integer> biospecimenIdsDBData;

        barcodesTestData.add(bcrIdTestData.getFullID());
        biospecimenIdsDBData = bcridQueries.getBiospecimenIds(barcodesTestData);

        assertTrue(biospecimenIdsDBData.size() == 1);
        assertTrue(bcrIdTestData.getId().equals(biospecimenIdsDBData.get(0)));
    }

    public void testGetBiospecimenIds() throws Exception {
        final List<String> barcodesTestData = new ArrayList<String>();
        List<Integer> biospecimenIdsDBData;

        final List<BCRID> bcrIds = createmockBCRIDs();
        bcridQueries.addBioSpecimenBarcodes(bcrIds, disease);
        barcodesTestData.clear();
        for (final BCRID bcrId : bcrIds) {
            barcodesTestData.add(bcrId.getFullID());
        }
        biospecimenIdsDBData = bcridQueries.getBiospecimenIds(barcodesTestData);
        assertTrue(biospecimenIdsDBData.size() == barcodesTestData.size());
    }

    public void testGetArchiveBarcodes() {
        List<BCRID> bcrIDList = bcridQueries.getArchiveBarcodes(3053);
        assertNotNull(bcrIDList);
        assertEquals(5, bcrIDList.size());
        final List<Integer> expectedIds = Arrays.asList(new Integer[]{254760, 99999,
                254761, 10000, 10001});
        final List<String> expectedBarcodes = Arrays
                .asList(new String[]{"tcga-a3-3308-11a-01w-0898-10",
                        "fake",
                        "tcga-a3-3308-11a-01d-0859-05",
                        "TCGA-02-0001-02C-01R-0178-03",
                        "TCGA-02-0001-03C-01R-0178-03"});
        final List<String> actualBarcodes = new ArrayList<String>();
        final List<Integer> actualIds = new ArrayList<Integer>();
        for (final BCRID bcrid : bcrIDList) {
            actualIds.add(bcrid.getId());
            actualBarcodes.add(bcrid.getFullID());
        }
        assertTrue(expectedIds.containsAll(actualIds));
        assertTrue(expectedBarcodes.containsAll(actualBarcodes));
    }

    public void testGetArchiveBarcodesForEmptySet() {
        List<BCRID> bcrIDList = bcridQueries.getArchiveBarcodes(3053000);
        assertEquals(0, bcrIDList.size());
    }

    @Test
    public void testGetBiospecimenIdForUUID() {
        assertEquals(
                new Long(254761),
                bcridQueries
                        .getBiospecimenIdForUUID("f9a723ff-3715-4a3c-972b-b329825f4f5f"));
    }

    @Test
    public void testGetBiospecimenIdForInvalidUUID() {
        assertNull(bcridQueries
                .getBiospecimenIdForUUID("f9a72311-3715-4a3c-972b-b329825f4f5f"));
    }


    public void testExistingSlideBarcode() {
        assertTrue(bcridQueries.slideBarcodeExists("TCGA-02-0000-01A-01W-1111-66"));
    }

    public void testNewSlideBarcode() {
        assertFalse(bcridQueries.slideBarcodeExists("TCGA-05-0000-01A-01W-1111-66"));
    }

    private List<BiospecimenToFile> createmockBiospecimenToFileObjects()
            throws Exception {
        final List<BiospecimenToFile> biospecimenToFileList = new ArrayList<BiospecimenToFile>();
        final ITable fileInfoTable = getDataSet().getTable("file_info");
        final ITable biospecimenBarcodeTable = getDataSet().getTable(
                "biospecimen_barcode");
        final int biospecimenId = Integer
                .valueOf((String) biospecimenBarcodeTable.getValue(0,
                        "biospecimen_id"));
        for (int i = 0; i < fileInfoTable.getRowCount(); i++) {
            BiospecimenToFile biospecimenToFile = new BiospecimenToFile();
            biospecimenToFile.setFileId(Long.valueOf((String) fileInfoTable
                    .getValue(i, "file_id")));
            biospecimenToFile.setBiospecimenId(biospecimenId);
            biospecimenToFileList.add(biospecimenToFile);
        }
        return biospecimenToFileList;
    }

    private List<BCRID> createmockBCRIDs() throws ParseException {
        final List<BCRID> bcrIds = new ArrayList<BCRID>();

        BCRID fakeBCRId = bcridProcessorForParsing
                .parseAliquotBarcode("TCGA-02-0001-01C-01R-0178-03"); // add
        // existing
        // barcode
        bcrIds.add(fakeBCRId);
        fakeBCRId = bcridProcessorForParsing
                .parseAliquotBarcode("TCGA-02-0010-01C-01R-0178-05"); // new
        // barcode
        bcrIds.add(fakeBCRId);
        fakeBCRId = bcridProcessorForParsing
                .parseAliquotBarcode("TCGA-02-0011-01C-01R-0178-01"); // new
        // barcode
        bcrIds.add(fakeBCRId);

        return bcrIds;
    }

    public BCRID createmockBCRID() {
        BCRID testBCRID = new BCRID();
        testBCRID.setArchiveId(611L);
        testBCRID.setProjectName("TCGA");
        testBCRID.setSiteID("02");
        testBCRID.setPatientID("0002");
        testBCRID.setSampleTypeCode("01");
        testBCRID.setSampleNumberCode("A");
        testBCRID.setPortionNumber("01");
        testBCRID.setPortionTypeCode("R");
        testBCRID.setPlateId("0178");
        testBCRID.setBcrCenterId("01");
        testBCRID.setUUID("2dad642f-95ac-4008-989e-b5630757c88b");
        return testBCRID;
    }

    @Test
    public void testUuidExists() {

        bcridQueries.setUuidDAO(mockUuidDAO);
        final String uuid = "2dad642f-95ac-4008-989e-b5630757c88b";
        mockery.checking(new Expectations() {
            {
                one(mockUuidDAO).uuidExists(uuid);
                will(returnValue(false));
            }
        });
        assertFalse(bcridQueries.uuidExists(uuid));

        mockery.checking(new Expectations() {
            {
                one(mockUuidDAO).uuidExists(uuid);
                will(returnValue(true));
            }
        });
        assertTrue(bcridQueries.uuidExists(uuid));
    }

    @Test
    public void testGetBiospecimenIdsForBarcodes()
            throws Processor.ProcessorException, UUIDException {
        final List<String> barcodes = Arrays
                .asList("TCGA-02-0001-02C-01R-0178-03",
                        "TCGA-02-0001-03C-01R-0178-03");
        final Map<String, Integer> biospecimenIdsByBarcode = bcridQueries.getBiospecimenIdsForBarcodes(barcodes);
        final List<Integer> expectedIds = Arrays.asList(10000,
                10001);
        assertTrue(expectedIds.containsAll(new ArrayList<Integer>(
                biospecimenIdsByBarcode.values())));
    }

    /**
     * @return the first biospecimen entry from test data
     * @throws Exception
     */
    private BCRID getBCRIDTestData() throws Exception {
        final ITable biospecimenTable = getDataSet().getTable(
                "biospecimen_barcode");
        BCRID bcrIdTestData = new BCRID();

        bcrIdTestData.setId(Integer.valueOf((String) biospecimenTable.getValue(
                0, "biospecimen_id")));
        bcrIdTestData.setFullID((String) biospecimenTable
                .getValue(0, "barcode"));
        return bcrIdTestData;
    }

    static class BiospecimenIdQuery extends MappingSqlQuery {

        BiospecimenIdQuery(final DataSource ds, final String selectStmt) {
            super(ds, selectStmt);
        }

        protected Integer mapRow(final ResultSet rs, int rownum)
                throws SQLException {
            return rs.getInt("file_id");
        }
    }
}