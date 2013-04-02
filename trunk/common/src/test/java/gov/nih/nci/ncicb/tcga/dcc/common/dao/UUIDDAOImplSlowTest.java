/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UuidBarcodeMapping;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.UUIDTypeQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.GenerationMethod;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDGenerator;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * Test class for testing UUID DAO layer
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

public class UUIDDAOImplSlowTest extends DBUnitTestCase {

    private final static String tcgaTestPropertiesFile = "unittest.properties";
    public static final String UUID_DB_DUMP_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    public static final String UUID_DB_FILE = "UUID_TestDB.xml";

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private UUIDDAOImpl uuidDAOImpl;
    private UUIDTypeQueriesJDBCImpl uuidTypeQueries;
    private CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private Barcode barcode;

    public UUIDDAOImplSlowTest() {
        super(UUID_DB_DUMP_FOLDER, UUID_DB_FILE, tcgaTestPropertiesFile);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        uuidDAOImpl = new UUIDDAOImpl();
        uuidDAOImpl.setDataSource(getDataSource());
        uuidTypeQueries = new UUIDTypeQueriesJDBCImpl();
        uuidTypeQueries.setDataSource(getDataSource());
        commonBarcodeAndUUIDValidator = new CommonBarcodeAndUUIDValidatorImpl();
        simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        uuidDAOImpl.setCommonBarcodeAndUUIDValidator(commonBarcodeAndUUIDValidator);
        uuidDAOImpl.setUuidTypeQueries(uuidTypeQueries);
        barcode = new Barcode();
        Tumor disease = new Tumor();
        disease.setTumorId(1);
        barcode.setDisease(disease);
        barcode.setEffectiveDate(new Date());
        barcode.setItemTypeId(6L);
    }

    @Test
    public void testAddUUID() throws UUIDException {
        Center center = new Center();
        center.setCenterId(1);
        UUIDDetail detail = new UUIDDetail(UUIDGenerator.getUUID().toString(), new Date(), GenerationMethod.Web, center, "master_user");
        Center center2 = new Center();
        center2.setCenterId(2);
        UUIDDetail detail2 = new UUIDDetail(UUIDGenerator.getUUID().toString(), new Date(), GenerationMethod.Web, center, "master_user");
        List<UUIDDetail> uuidDetailList = new ArrayList<UUIDDetail>();
        uuidDetailList.add(detail);
        uuidDetailList.add(detail2);
        int rowsAffected = uuidDAOImpl.addUUID(uuidDetailList);
        assertEquals(2, rowsAffected);
    }

    @Test
    public void testAddNewUUIDs() {
        Center center = new Center();
        center.setCenterId(1);
        String uuid = UUIDGenerator.getUUID().toString();
        // Adding 'test' as prefix for uuid so that I can verify the added records
        uuid = "test_" + uuid.substring(0, uuid.length() - 5);
        UUIDDetail detail = new UUIDDetail(uuid, new Date(), GenerationMethod.Web, center, "master_user");
        Center center2 = new Center();
        center2.setCenterId(2);
        UUIDDetail detail2 = new UUIDDetail(uuid, new Date(), GenerationMethod.Web, center, "master_user");
        List<UUIDDetail> uuidDetailList = new ArrayList<UUIDDetail>();
        uuidDetailList.add(detail);
        uuidDetailList.add(detail2);
        uuidDAOImpl.addNewUUIDs(uuidDetailList);
        final int addedRows = simpleJdbcTemplate.queryForInt("select count(*) from UUID where uuid like 'test%' ");
        assertEquals(1, addedRows);
    }

    @Test
    public void testAddBarcode() throws UUIDException {
        final String barcodeStr = "TCGA-C4-A0F0-01A-12D-A10W-05";
        final String uuid = "9be34abb-c11f-416f-9fd4-34e712676a2f";
        barcode.setBarcode(barcodeStr);
        barcode.setUuid(uuid);

        uuidDAOImpl.addBarcode(barcode);
        // check in db to verify the info is there
        int count = simpleJdbcTemplate.queryForInt("select count(*) from barcode_history where barcode=? and regexp_like(uuid,?,'i')", barcodeStr, uuid);
        long itemTypeId = simpleJdbcTemplate.queryForLong("select item_type_id from barcode_history where barcode=? and regexp_like(uuid,?,'i')", barcodeStr, uuid);
        assertEquals(1, count);
        assertEquals(6, itemTypeId);
    }

    public void testAddBarcodeWrongUUID() {
        // try to add a barcode when the barcode is already associated with a different id
        barcode.setBarcode("TCGA-BT-A20Q-11A");
        barcode.setUuid("something else");
        try {
            uuidDAOImpl.addBarcode(barcode);
            fail("Exception was not thrown");
        } catch (UUIDException e) {
            // good, expected
            assertEquals("Barcode 'TCGA-BT-A20Q-11A' is already associated with UUID '9be34abb-c11f-416f-9fd4-34e712676a2f'.  It cannot be associated with the UUID 'something else'.", e.getMessage());
        }
    }

    public void testAddBarcodeAlreadyLatest() throws UUIDException {
        // this barcode is already the latest barcode for the uuid
        String barcodeStr = "TCGA-BT-A20Q-11B";
        String uuid = "3625e179-1228-4814-bf1c-af0735d3a210";
        barcode.setBarcode(barcodeStr);
        barcode.setUuid(uuid);
        uuidDAOImpl.addBarcode(barcode);
        int count = simpleJdbcTemplate.queryForInt("select count(*) from barcode_history where barcode=? and regexp_like(uuid,?,'i')",
                barcodeStr, uuid);
        assertEquals("Another barcode history row was added when it shouldn't have been", 1, count);
    }

    public void testAddBarcodeOld() throws UUIDException {
        // there is a barcode history for this barcode and uuid, but it is not the latest one, so should be added again
        String barcodeStr = "TCGA-BT-A20Q-11E";
        String uuid = "1616f098-483f-4fd5-bd00-5ce1e0bc4092";
        barcode.setBarcode(barcodeStr);
        barcode.setUuid(uuid);
        int initialCount = simpleJdbcTemplate.queryForInt("select count(*) from barcode_history where barcode=? and regexp_like(uuid,?,'i')",
                barcodeStr, uuid);
        uuidDAOImpl.addBarcode(barcode);
        int finalCount = simpleJdbcTemplate.queryForInt("select count(*) from barcode_history where barcode=? and regexp_like(uuid,?,'i')",
                barcodeStr, uuid);
        assertEquals(initialCount + 1, finalCount);
    }

    @Test
    public void testResubmissionOfUUID() {
        String uuid = UUIDGenerator.getUUID().toString();
        Center center = new Center();
        center.setCenterId(1);
        UUIDDetail detail = new UUIDDetail(uuid, new Date(), GenerationMethod.Web, center, "master_user");

        Center center2 = new Center();
        center2.setCenterId(2);
        UUIDDetail detail2 = new UUIDDetail(uuid, new Date(), GenerationMethod.Web, center, "master_user");

        List<UUIDDetail> uuidDetailList = new ArrayList<UUIDDetail>();
        uuidDetailList.add(detail);
        uuidDetailList.add(detail2);

        try {
            uuidDAOImpl.addUUID(uuidDetailList);
            fail();
        } catch (UUIDException e) {
            // expected, same UUID cannot be added to the database more than once
            String cause = e.getCause().getMessage();
            assertTrue(cause.contains("unique constraint") && cause.contains("violated"));
        }

    }

    @Test
    public void testAddUUIDFailure() throws UUIDException {

        Center center = new Center();
        center.setCenterId(-10);//center number set to some bad value
        UUIDDetail detail = new UUIDDetail(UUIDGenerator.getUUID().toString(), new Date(), GenerationMethod.Web, center, "master_user");
        List<UUIDDetail> uuidDetailList = new ArrayList<UUIDDetail>();
        uuidDetailList.add(detail);

        try {
            uuidDAOImpl.addUUID(uuidDetailList);
            fail("Should raise UUID Exception");
        } catch (UUIDException ex) {
            assertTrue(ex.getMessage().contains("Error while adding following UUIDs to the database"));
        }
    }

    @Test
    public void testAddbarcodeFailure() throws UUIDException {
        barcode.setBarcode("TCGA-asda-dfer-dgsa-wolf");
        // this uuid is not in the db, so insert will fail
        barcode.setUuid("9be34abb-c11f-416f-9fd4-34e712676a77");
        try {
            uuidDAOImpl.addBarcode(barcode);
            fail("Exception was not thrown");
        } catch (UUIDException e) {
            assertTrue(e.getCause() instanceof DataAccessException);
        }

    }

    @Test
    public void testGetUUIDDetail() throws UUIDException {
        String uuid = "9be34abb-c11f-416f-9fd4-34e712676a2f";
        UUIDDetail uuidDetail = uuidDAOImpl.getUUIDDetail(uuid);
        assertNotNull(uuidDetail);
        assertTrue(uuidDetail.getBarcodes().size() > 0);
        Barcode barcode = uuidDetail.getBarcodes().get(0);
        assertEquals("TCGA-BT-A20Q-11A", barcode.getBarcode());
    }

    @Test
    public void testUUIDDetailNotFound() throws UUIDException {
        String uuid = "invalid";
        try {
            uuidDAOImpl.getUUIDDetail(uuid);
            fail("Should raise an exception");
        } catch (UUIDException e) {
            assertEquals("UUID " + uuid + " not found", e.getMessage());
        }
    }

    @Test
    public void testGetLatestBarcodeForUUID() {
        assertEquals("TCGA-BT-A20Q-11A", uuidDAOImpl.getLatestBarcodeForUUID("9be34abb-c11f-416f-9fd4-34e712676a2f"));
        assertEquals("TCGA-BT-A20Q-11B", uuidDAOImpl.getLatestBarcodeForUUID("3625e179-1228-4814-bf1c-af0735d3a210"));
        assertEquals("TCGA-BT-A20Q-11C", uuidDAOImpl.getLatestBarcodeForUUID("00631e7c-f206-468f-9f91-b5ce5249a516"));
        assertEquals("TCGA-BT-A20Q-11D", uuidDAOImpl.getLatestBarcodeForUUID("1616f098-483f-4fd5-bd00-5ce1e0bc4092"));
        assertNull(uuidDAOImpl.getLatestBarcodeForUUID("7e80377c-7083-45c6-a8c1-6ff51cc3a0a2"));
    }

    @Test
    public void testGetLatestBarcodeForUUIDCaseInsensitive() {
        assertEquals("TCGA-BT-A20Q-11A", uuidDAOImpl.getLatestBarcodeForUUID("9BE34abb-C11f-416f-9fd4-34e712676a2f"));
        assertEquals("TCGA-BT-A20Q-11B", uuidDAOImpl.getLatestBarcodeForUUID("3625e179-1228-4814-BF1c-af0735d3a210"));
        assertEquals("TCGA-BT-A20Q-11C", uuidDAOImpl.getLatestBarcodeForUUID("00631e7c-f206-468f-9f91-b5ce5249a516"));
        assertEquals("TCGA-BT-A20Q-11D", uuidDAOImpl.getLatestBarcodeForUUID("1616F098-483F-4Fd5-BD00-5CE1E0BC4092"));
        assertNull(uuidDAOImpl.getLatestBarcodeForUUID("7e80377C-7083-45c6-a8c1-6ff51CC3a0a2"));
    }

    @Test
    public void testGetUUIDForBarcode() {
        // this barcode is linked to the same UUID twice with different effective dates
        String barcode = "TCGA-BT-A20Q-11A";
        String uuid = uuidDAOImpl.getUUIDForBarcode(barcode);
        assertEquals("9be34abb-c11f-416f-9fd4-34e712676a2f", uuid);
    }

    @Test
    public void testGetUUIDForBarcodeCaseInsensitive() {
        final String barcode = "TCGA-BT-A20Q-11a";
        final String uuid = uuidDAOImpl.getUUIDForBarcode(barcode);
        assertEquals("9be34abb-c11f-416f-9fd4-34e712676a2f", uuid);
    }

    @Test
    public void testGetUUIDForBarcodeFailure() {
        String barcode = "Fake Barcode";
        String uuid = uuidDAOImpl.getUUIDForBarcode(barcode);
        assertEquals(null, uuid);
    }

@Test
    public void testGetUUIDForBarcodeWith2UUIDs() {
        // this barcode is linked to the same UUID twice with different effective dates
        String barcode = "TCGA-C4-A0F1-01A-11R";
        String uuid = uuidDAOImpl.getUUIDForBarcode(barcode);
        assertEquals("fa1fd379-a55f-46b8-a46e-e024611f4704", uuid);
    }


    @Test
    public void testUuidExists() {
        assertTrue(uuidDAOImpl.uuidExists("9be34abb-c11f-416f-9fd4-34e712676a2f"));
        assertFalse(uuidDAOImpl.uuidExists("mickey-mouse"));
    }

    @Test
    public void testUuidExistsCaseSensitive() {
        assertTrue(uuidDAOImpl.uuidExists("9BE34ABB-c11f-416f-9fd4-34e712676a2f"));
    }

    @Test
    public void testGetBarcodesStartingWithMultipleMatches() throws ParseException {

        final String barcodePrefix = "TCGA-AT";
        final List<Barcode> barcodes = uuidDAOImpl.getBarcodesStartingWith(barcodePrefix);
        assertNotNull(barcodes);
        assertEquals(2, barcodes.size());

        final Barcode firstBarcode = barcodes.get(0);
        final Barcode secondBarcode = barcodes.get(1);

        final boolean exactMatch = false;
        checkBarcode(firstBarcode, barcodePrefix, "12345678-d866-4ce8-b056-0b0d35441dde", 1, "2008-10-15", exactMatch);
        checkBarcode(secondBarcode, barcodePrefix, "12345678-d866-4ce8-b056-0b0d35441dde", 1, "2006-07-15", exactMatch);
    }

    @Test
    public void testGetBarcodesStartingWithOneMatch() throws ParseException {

        final String barcodePrefix = "TCGA-ZT";
        checkGetBarcodesStartingWithOneMatch(barcodePrefix);
    }

    @Test
    public void testGetBarcodesStartingWithOneMatchCaseInsensitive() throws ParseException {

        final String barcodePrefix = "TCGA-zt";
        checkGetBarcodesStartingWithOneMatch(barcodePrefix);
    }

    @Test
    public void testGetBarcodesStartingWithExactMatch() throws ParseException {

        final String barcodePrefix = "TCGA-ZT-A20Q-11E";
        final List<Barcode> barcodes = uuidDAOImpl.getBarcodesStartingWith(barcodePrefix);
        assertNotNull(barcodes);
        assertEquals(1, barcodes.size());

        final Barcode barcode = barcodes.get(0);

        final String expectedUuid = "1616f098-483f-4fd5-bd00-5ce1e0bc4092";
        final Integer expectedDiseaseId = 1;
        final String expectedDateAsString = "2006-10-18";
        final boolean exactMatch = true;

        checkBarcode(barcode, barcodePrefix, expectedUuid, expectedDiseaseId, expectedDateAsString, exactMatch);
    }

    @Test
    public void testGetBarcodesStartingWithNoMatch() {

        final List<Barcode> barcodes = uuidDAOImpl.getBarcodesStartingWith("squirrel");
        assertNotNull(barcodes);
        assertEquals(0, barcodes.size());
    }

    @Test
    public void testUUIDsInDB() {
        final List<String> UUIDs = Arrays.asList(
                "3625e179-1228-4814-bf1c-af0735d3a210",
                "00631e7c-f206-468f-9f91-b5ce5249a516",
                "1616f098-483f-4fd5-bd00-5ce1e0bc4092",
                "7e80377c-7083-45c6-a8c1-6ff51cc3a0a2",
                "a38f4808-d866-4ce8-b056-0b0d35441dde",
                "12345678-d866-4ce8-b056-0b0d35441dde");
        final List<String> UUIDsExistsInTheDB = uuidDAOImpl.getUUIDsExistInDB(UUIDs);
        assertEquals(6, UUIDsExistsInTheDB.size());
    }

    @Test
    public void testUUIDsInDBCaseInsensitive() {
        final List<String> UUIDs = Arrays.asList(
                "9BE34ABB-C11F-416F-9FD4-34E712676A2F",
                "3625e179-1228-4814-BF1c-af0735d3a210");
        final List<String> UUIDsExistsInTheDB = uuidDAOImpl.getUUIDsExistInDB(UUIDs);
        assertEquals(2, UUIDsExistsInTheDB.size());
    }

    @Test
    public void testUUIDsNotInDB() {
        final List<String> UUIDs = Arrays.asList(
                "9be34abb-c11f-416f-9fd4-34e712676a2f",
                "3625e179-1228-4814-bf1c-af0735d3a210",
                "00631e7c-f206-468f-9f91-b5ce5249a516",
                "9be34abb-c11f-416f-9fd4-34e712676a21",
                "3625e179-1228-4814-bf1c-af0735d3a212",
                "00631e7c-f206-468f-9f91-b5ce5249a513"
        );
        final List<String> UUIDsInTheDB = uuidDAOImpl.getUUIDsExistInDB(UUIDs);
        assertEquals(3, UUIDsInTheDB.size());
    }

    @Test
    public void testBarcodesInDB() {
        final List<String> barcodes = Arrays.asList(
                "TCGA-BT-A20Q-11B",
                "TCGA-BT-A20Q-11C",
                "TCGA-BT-A20Q-11D");
        final List<String> barcodesExistsInTheDB = uuidDAOImpl.getExistingBarcodes(barcodes);
        assertNotNull(barcodesExistsInTheDB);
        assertEquals(3, barcodesExistsInTheDB.size());
    }

    @Test
    public void testBarcodesInDBCaseInsensitive() {
        final List<String> barcodes = Arrays.asList(
                "tcga-BT-A20Q-11B",
                "TCGA-bt-A20Q-11C",
                "tcga-bt-a20q-11d");
        final List<String> barcodesExistsInTheDB = uuidDAOImpl.getExistingBarcodes(barcodes);
        assertNotNull(barcodesExistsInTheDB);
        assertEquals(3, barcodesExistsInTheDB.size());
    }

    @Test
    public void testBarcodesNotInDB() {
        final List<String> barcodes = Arrays.asList(
                "TCGA-ZT-A20Q-11Z",
                "TCGA-BT-A20Q-11B",
                "TCGA-ZT-A20Q-11Y");
        final List<String> barcodesExistsInTheDB = uuidDAOImpl.getExistingBarcodes(barcodes);
        assertNotNull(barcodesExistsInTheDB);
        assertEquals(1, barcodesExistsInTheDB.size());
    }

    @Test
    public void testGetLatestBarcodesForUUIDs() {
        final List<String> uuids = new LinkedList<String>() {{
            add("9be34abb-c11f-416f-9fd4-34e712676a2f");
            add("3625e179-1228-4814-bf1c-af0735d3a210");
            add("00631e7c-f206-468f-9f91-b5ce5249a516");
            add("1616f098-483f-4fd5-bd00-5ce1e0bc4092");
        }};
        final List<UuidBarcodeMapping> barcodesRes = uuidDAOImpl.getLatestBarcodesForUUIDs(uuids);
        assertNotNull(barcodesRes);
        assertEquals(4, barcodesRes.size());
        assertEquals("TCGA-BT-A20Q-11C", barcodesRes.get(0).getBarcode());
        assertEquals("00631e7c-f206-468f-9f91-b5ce5249a516", barcodesRes.get(0).getUuid());
        assertEquals("TCGA-BT-A20Q-11A", barcodesRes.get(3).getBarcode());
        assertEquals("9be34abb-c11f-416f-9fd4-34e712676a2f", barcodesRes.get(3).getUuid());
    }

    @Test
    public void testGetLatestBarcodesForUUIDsCaseInsensitive() {
        final List<String> uuids = new LinkedList<String>() {{
            add("9BE34abb-c11f-416f-9fd4-34e712676a2f");
            add("3625e179-1228-4814-BF1c-af0735d3a210");
            add("00631e7c-f206-468f-9f91-b5ce5249a516");
            add("1616F098-483F-4Fd5-BD00-5CE1E0BC4092");
        }};
        final List<UuidBarcodeMapping> barcodesRes = uuidDAOImpl.getLatestBarcodesForUUIDs(uuids);
        assertNotNull(barcodesRes);
        assertEquals(4, barcodesRes.size());
        assertEquals("TCGA-BT-A20Q-11C", barcodesRes.get(0).getBarcode());
        assertEquals("00631e7c-f206-468f-9f91-b5ce5249a516", barcodesRes.get(0).getUuid());
        assertEquals("TCGA-BT-A20Q-11A", barcodesRes.get(3).getBarcode());
        assertEquals("9be34abb-c11f-416f-9fd4-34e712676a2f", barcodesRes.get(3).getUuid());
    }

    @Test
    public void testGetLatestBarcodesForUUIDsWithInvalid() {
        final List<String> uuids = Arrays.asList(
                "9be34abb-c11f-416f-9fd4-34e712676a2f", "uuid1",
                "00631e7c-f206-468f-9f91-b5ce5249a516", "3625e179-1228-4814-bf1c-af0735d3a210");
        final List<UuidBarcodeMapping> barcodesRes = uuidDAOImpl.getLatestBarcodesForUUIDs(uuids);
        assertNotNull(barcodesRes);
        assertEquals(4, barcodesRes.size());
        assertEquals("TCGA-BT-A20Q-11C", barcodesRes.get(0).getBarcode());
        assertEquals("00631e7c-f206-468f-9f91-b5ce5249a516", barcodesRes.get(0).getUuid());
        assertEquals("uuid1", barcodesRes.get(3).getUuid());
        assertEquals("barcode mapping for uuid 'uuid1' was not found", barcodesRes.get(3).getBarcode());
    }

    @Test
    public void testgetUUIDsForBarcodes() {
        final List<String> barcodes = Arrays.asList("TCGA-BT-A20Q-11A", "TCGA-BT-A20Q-11B",
                "TCGA-BT-A20Q-11C", "TCGA-BT-A20Q-11D");
        final List<UuidBarcodeMapping> uuidRes = uuidDAOImpl.getUUIDsForBarcodes(barcodes);
        assertNotNull(uuidRes);
        assertEquals(4, uuidRes.size());
        assertEquals("3625e179-1228-4814-bf1c-af0735d3a210", uuidRes.get(1).getUuid());
        assertEquals("TCGA-BT-A20Q-11B", uuidRes.get(1).getBarcode());
        assertEquals("00631e7c-f206-468f-9f91-b5ce5249a516", uuidRes.get(2).getUuid());
        assertEquals("TCGA-BT-A20Q-11C", uuidRes.get(2).getBarcode());
    }

    @Test
    public void testgetUUIDsForBarcodesCaseInsensitive() {
        final List<String> barcodes = Arrays.asList("tcga-BT-A20Q-11A", "TCGA-bt-A20Q-11B",
                "tcga-bt-a20q-11c", "tcga-bt-a20q-11D");
        final List<UuidBarcodeMapping> uuidRes = uuidDAOImpl.getUUIDsForBarcodes(barcodes);
        assertNotNull(uuidRes);
        assertEquals(4, uuidRes.size());
        assertEquals("3625e179-1228-4814-bf1c-af0735d3a210", uuidRes.get(1).getUuid());
        assertEquals("TCGA-BT-A20Q-11B", uuidRes.get(1).getBarcode());
        assertEquals("00631e7c-f206-468f-9f91-b5ce5249a516", uuidRes.get(2).getUuid());
        assertEquals("TCGA-BT-A20Q-11C", uuidRes.get(2).getBarcode());
    }

    @Test
    public void testgetUUIDsForBarcodesWithInvalid() {
        final List<String> barcodes = Arrays.asList("TCGA-BT-A20Q-11A", "TCGA-BT-A20Q-11B",
                "barcode1", "TCGA-BT-A20Q-11D");
        final List<UuidBarcodeMapping> uuidRes = uuidDAOImpl.getUUIDsForBarcodes(barcodes);
        assertNotNull(uuidRes);
        assertEquals(4, uuidRes.size());
        assertEquals("uuid mapping for barcode 'BARCODE1' was not found", uuidRes.get(0).getUuid());
        assertEquals("BARCODE1", uuidRes.get(0).getBarcode());
        assertEquals("3625e179-1228-4814-bf1c-af0735d3a210", uuidRes.get(2).getUuid());
        assertEquals("TCGA-BT-A20Q-11B", uuidRes.get(2).getBarcode());
    }

    @Test
    public void testProcessUnionClauseLowerCase() throws Exception {
        final String res = uuidDAOImpl.processUnionClause(3, "uuid", StringUtil.CaseSensitivity.LOWER_CASE);
        assertEquals("select lower(?) as uuid from dual union " +
                "select lower(?) as uuid from dual union " +
                "select lower(?) as uuid from dual", res);
    }

    @Test
    public void testProcessUnionClauseUpperCase() throws Exception {
        final String res = uuidDAOImpl.processUnionClause(3, "uuid", StringUtil.CaseSensitivity.UPPER_CASE);
        assertEquals("select upper(?) as uuid from dual union " +
                "select upper(?) as uuid from dual union " +
                "select upper(?) as uuid from dual", res);
    }

    @Test
    public void testProcessUnionClause() throws Exception {
        final String res = uuidDAOImpl.processUnionClause(3, "barcode", StringUtil.CaseSensitivity.CASE_SENSITIVE);
        assertEquals("select ? as barcode from dual union " +
                "select ? as barcode from dual union " +
                "select ? as barcode from dual", res);
    }

    @Test
    public void testAddParticipantFileUUIDAssociation() {
        uuidDAOImpl.addParticipantFileUUIDAssociation("9bE34ABB-c11f-416f-9fd4-34e712676a2f", 1l);
        uuidDAOImpl.addParticipantFileUUIDAssociation("9bE34ABB-c11f-416f-9fd4-34e712676a2f", 1l);

        assertEquals(1, simpleJdbcTemplate.queryForInt
                ("select count(file_id) from PARTICIPANT_UUID_FILE where " +
                        "uuid='9be34abb-c11f-416f-9fd4-34e712676a2f' and file_id=1"));
    }

    @Test
    public void testAddParticipantUUIDAssocBadInput() {
        try {
            uuidDAOImpl.addParticipantFileUUIDAssociation("9be34abb-c11f-416f-9fd4-34e712676a2f", 0l);
            fail();
        } catch (IllegalArgumentException e) {
            // expected , swallow
        }
        try {
            uuidDAOImpl.addParticipantFileUUIDAssociation("", 0l);
            fail();
        } catch (IllegalArgumentException e) {
            // expected , swallow
        }

    }

    @Test
    public void testAddParticipantFileUUIDAssociationBatch() {

        final String uuid = "9be34abb-c11f-416f-9fd4-34e712676a2f";
        final Long fileId = 1L;

        final Object[] uuidFileId = {uuid, fileId};
        final String countSql = "select count(*) from participant_uuid_file where regexp_like(uuid,?,'i') and file_id=?";
        final int beforeCount = getSimpleJdbcTemplate().queryForInt(countSql, uuidFileId);
        assertEquals(0, beforeCount);

        final Object[] patientUUIDAndFileId = {uuid, fileId, uuid, fileId};

        final List patientsUUIDAndFileId = new ArrayList<Object[]>();
        patientsUUIDAndFileId.add(patientUUIDAndFileId);

        uuidDAOImpl.addParticipantFileUUIDAssociation(patientsUUIDAndFileId);

        final int afterCount = getSimpleJdbcTemplate().queryForInt(countSql, uuidFileId);
        assertEquals(1, afterCount);
    }

    @Test
    public void testAddParticipantFileUUIDAssociationBatchCalledTwice() {

        final String uuid = "9be34abb-c11f-416f-9fd4-34e712676a2f";
        final Long fileId = 1L;

        final Object[] uuidFileId = {uuid, fileId};
        final String countSql = "select count(*) from participant_uuid_file where regexp_like(uuid,?,'i') and file_id=?";
        final int beforeCount = getSimpleJdbcTemplate().queryForInt(countSql, uuidFileId);
        assertEquals(0, beforeCount);

        final Object[] patientUUIDAndFileId = {uuid, fileId, uuid, fileId};

        final List patientsUUIDAndFileId = new ArrayList<Object[]>();
        patientsUUIDAndFileId.add(patientUUIDAndFileId);

        uuidDAOImpl.addParticipantFileUUIDAssociation(patientsUUIDAndFileId);

        final int afterCount = getSimpleJdbcTemplate().queryForInt(countSql, uuidFileId);
        assertEquals(1, afterCount);

        // Deleting records that have been inserted and calling addParticipantFileUUIDAssociation()
        // with the same list to make sure it wasn't cleared by the 1st call
        final String deleteSql = "delete from participant_uuid_file where regexp_like(uuid,?,'i') and file_id=?";
        getSimpleJdbcTemplate().update(deleteSql, uuidFileId);

        final int beforeCount2 = getSimpleJdbcTemplate().queryForInt(countSql, uuidFileId);
        assertEquals(0, beforeCount2);

        uuidDAOImpl.addParticipantFileUUIDAssociation(patientsUUIDAndFileId);

        final int afterCount2 = getSimpleJdbcTemplate().queryForInt(countSql, uuidFileId);
        assertEquals(1, afterCount2);
    }

    @Test
    public void testAddParticipantFileUUIDAssociationBatchRecordExists() {

        final String uuid = "a38f4808-d866-4ce8-b056-0b0d35441dde";
        final Long fileId = 2L;

        final Object[] uuidFileId = {uuid, fileId};
        final String countSql = "select count(*) from participant_uuid_file where uuid=lower(?) and file_id=?";
        final int beforeCount = getSimpleJdbcTemplate().queryForInt(countSql, uuidFileId);
        assertEquals(1, beforeCount);

        final Object[] patientUUIDAndFileId = {uuid, fileId, uuid, fileId};

        final List patientsUUIDAndFileId = new ArrayList<Object[]>();
        patientsUUIDAndFileId.add(patientUUIDAndFileId);

        uuidDAOImpl.addParticipantFileUUIDAssociation(patientsUUIDAndFileId);

        final int afterCount = getSimpleJdbcTemplate().queryForInt(countSql, uuidFileId);
        assertEquals(1, afterCount);
    }

    @Test
    public void testAddParticipantFileUUIDAssociationBatchRecordExistsUUIDUpperCase() {

        final String uuid = "A38F4808-D866-4CE8-B056-0B0D35441DDE";
        final Long fileId = 2L;

        final Object[] uuidFileId = {uuid, fileId};
        final String countSql = "select count(*) from participant_uuid_file where uuid=lower(?) and file_id=?";
        final int beforeCount = getSimpleJdbcTemplate().queryForInt(countSql, uuidFileId);
        assertEquals(1, beforeCount);

        final Object[] patientUUIDAndFileId = {uuid, fileId, uuid, fileId};

        final List patientsUUIDAndFileId = new ArrayList<Object[]>();
        patientsUUIDAndFileId.add(patientUUIDAndFileId);

        uuidDAOImpl.addParticipantFileUUIDAssociation(patientsUUIDAndFileId);

        final Object[] uuidLowerCaseFileId = {uuid.toLowerCase(), fileId};
        final String countExactMatchSql = "select count(*) from participant_uuid_file where regexp_like(uuid,?,'i') and file_id=?";
        final int afterCount = getSimpleJdbcTemplate().queryForInt(countExactMatchSql, uuidLowerCaseFileId);
        assertEquals(1, afterCount);
    }


    /**
     * Search for the given barcode prefix and check assertions
     *
     * @param barcodePrefix the barcode prefix
     * @throws ParseException
     */
    private void checkGetBarcodesStartingWithOneMatch(final String barcodePrefix) throws ParseException {

        final List<Barcode> barcodes = uuidDAOImpl.getBarcodesStartingWith(barcodePrefix);
        assertNotNull(barcodes);
        assertEquals(1, barcodes.size());

        final Barcode barcode = barcodes.get(0);

        final String expectedUuid = "1616f098-483f-4fd5-bd00-5ce1e0bc4092";
        final Integer expectedDiseaseId = 1;
        final String expectedDateAsString = "2006-10-18";
        final boolean exactMatch = false;

        checkBarcode(barcode, barcodePrefix, expectedUuid, expectedDiseaseId, expectedDateAsString, exactMatch);
    }

    /**
     * Check expectations against a given <code>Barcode</code>
     *
     * @param barcode               the <code>Barcode</code> to check
     * @param expectedBarcodePrefix the expected barcode prefix
     * @param expectedUuid          the expected UUID
     * @param expectedDiseaseId     the expected disease Id
     * @param expectedDateAsString  the expected date as a <code>String</code>
     * @param exactMatch
     * @throws ParseException
     */
    private void checkBarcode(final Barcode barcode,
                              final String expectedBarcodePrefix,
                              final String expectedUuid,
                              final Integer expectedDiseaseId,
                              final String expectedDateAsString,
                              boolean exactMatch) throws ParseException {

        assertNotNull(barcode);

        if (exactMatch) {
            assertEquals(expectedBarcodePrefix, barcode.getBarcode());
        } else {
            assertTrue(barcode.getBarcode().toUpperCase().startsWith(expectedBarcodePrefix.toUpperCase()));
        }

        assertEquals(expectedUuid, barcode.getUuid());
        assertEquals(expectedDiseaseId, barcode.getDisease().getTumorId());
        assertEquals(getDateFromString(expectedDateAsString), barcode.getEffectiveDate());
    }


    /**
     * Return a <code>Date</code> from parsing the given input <code>String</code> in the format "yyyy-MM-dd"
     *
     * @param dateAsString the input to parse
     * @return a <code>Date</code> from the parsing given input <code>String</code> in the format "yyyy-MM-dd"
     * @throws ParseException
     */
    private Date getDateFromString(final String dateAsString) throws ParseException {
        return simpleDateFormat.parse(dateAsString);
    }

}

