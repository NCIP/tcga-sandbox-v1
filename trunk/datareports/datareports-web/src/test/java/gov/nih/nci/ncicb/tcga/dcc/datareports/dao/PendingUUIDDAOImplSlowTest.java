/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc.PendingUUIDDAOImpl;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The slow test class for {@link gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc.PendingUUIDDAOImpl}.
 *
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class PendingUUIDDAOImplSlowTest extends DBUnitTestCase {

    private PendingUUIDDAOImpl pendingUUIDDAO;
    private CenterQueries centerQueries;
    private static final String PROPERTIES_FILE = "unittest.properties";

    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "PendingUUIDTestData.xml";

    private static final String appContextFile = "samples/applicationContext-dbunit.xml";
    private final ApplicationContext appContext;
    private JdbcTemplate jdbcTemplate;

    public PendingUUIDDAOImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        appContext = new ClassPathXmlApplicationContext(appContextFile);
        pendingUUIDDAO = (PendingUUIDDAOImpl) appContext.getBean("pendingUUIDDAOImpl");
        centerQueries = (CenterQueries) appContext.getBean("centerQueries");
        jdbcTemplate = new JdbcTemplate(getDataSource());
        pendingUUIDDAO.setCenterQueries(centerQueries);
        pendingUUIDDAO.setCenters(centerQueries.getRealCenterList());
    }

    @Test
    public void testInsertPendingUUID() {
        PendingUUID pendingUUID = getPendingUUID();
        PendingUUID resultingPendingUUID;
        pendingUUIDDAO.insertPendingUUID(pendingUUID);
        resultingPendingUUID = jdbcTemplate.query(" select * from pending_uuid where uuid = ?",
                pendingUUIDDAO.pendingUUIDRowMapper, pendingUUID.getUuid()).get(0);
        assertEquals(pendingUUID.getAnalyteType(), resultingPendingUUID.getAnalyteType());
        assertEquals(pendingUUID.getBcr(), resultingPendingUUID.getBcr());
        assertEquals(pendingUUID.getBcrAliquotBarcode(), resultingPendingUUID.getBcrAliquotBarcode());
        assertEquals(pendingUUIDDAO.getCenterDisplayText(pendingUUID.getCenter()),
                resultingPendingUUID.getCenter());
        assertEquals(pendingUUID.getItemType(), resultingPendingUUID.getItemType());
        assertEquals(pendingUUID.getPlateCoordinate(), resultingPendingUUID.getPlateCoordinate());
        assertEquals(pendingUUID.getPlateId(), resultingPendingUUID.getPlateId());
        assertEquals(pendingUUID.getPortionNumber(), resultingPendingUUID.getPortionNumber());
        assertEquals(pendingUUID.getSampleType(), resultingPendingUUID.getSampleType());
        assertEquals(pendingUUID.getUuid(), resultingPendingUUID.getUuid());
        assertEquals(pendingUUID.getVialNumber(), resultingPendingUUID.getVialNumber());
        assertEquals(pendingUUID.getDccReceivedDate(), resultingPendingUUID.getDccReceivedDate());
    }

    public void testInsertPendingUUIDMissingUUID() {
        jdbcTemplate.execute(" delete from pending_uuid ");
        PendingUUID sourcePendingUUID = getPendingUUID();
        sourcePendingUUID.setUuid(null);
        pendingUUIDDAO.insertPendingUUID(sourcePendingUUID);
        int resultingPendingUUIDId = jdbcTemplate.queryForInt("select max(pending_uuid_id) from pending_uuid");
        assertTrue(resultingPendingUUIDId > 0);
    }

    @Test
    public void testInsertPendingUUIDMissingValues() {

        PendingUUID sourcePendingUUID = new PendingUUID();
        sourcePendingUUID.setUuid("e05d462c-7987-494d-8012-727e75656124");
        sourcePendingUUID.setBcr("IGC");

        pendingUUIDDAO.insertPendingUUID(sourcePendingUUID);
        PendingUUID resultingPendingUUID = jdbcTemplate.query("select * from pending_uuid where uuid = ?",
                pendingUUIDDAO.pendingUUIDRowMapper, sourcePendingUUID.getUuid()).get(0);
        assertEquals(null, resultingPendingUUID.getAnalyteType());
        assertEquals("e05d462c-7987-494d-8012-727e75656124", resultingPendingUUID.getUuid());
        assertEquals("IGC", resultingPendingUUID.getBcr());
        assertEquals(null, resultingPendingUUID.getBcrAliquotBarcode());
        assertEquals(null, resultingPendingUUID.getCenter());
        assertEquals(null, resultingPendingUUID.getItemType());
        assertEquals(null, resultingPendingUUID.getPlateCoordinate());
        assertEquals(null, resultingPendingUUID.getPlateId());
        assertEquals(null, resultingPendingUUID.getPortionNumber());
        assertEquals(null, resultingPendingUUID.getSampleType());
        assertEquals(null, resultingPendingUUID.getVialNumber());
        assertEquals(null, resultingPendingUUID.getDccReceivedDate());
    }

    @Test
    public void testGetAllPendingUUIDs() {
        jdbcTemplate.execute(" delete from pending_uuid ");
        // insert UUIDs
        PendingUUID sourcePendingUUID = getPendingUUID();
        pendingUUIDDAO.insertPendingUUID(sourcePendingUUID);

        PendingUUID sourcePendingUUID2 = getPendingUUID();
        sourcePendingUUID2.setUuid("e05d462c-7987-494d-8012-727e75656125");
        pendingUUIDDAO.insertPendingUUID(sourcePendingUUID2);

        List<PendingUUID> pendingUUIDs =
                pendingUUIDDAO.getAllPendingUUIDs();
        assertEquals(2, pendingUUIDs.size());
    }

    @Test
    public void testDeletePendingUUID() {
        jdbcTemplate.execute(" delete from pending_uuid ");
        // insert UUIDs
        PendingUUID sourcePendingUUID = getPendingUUID();
        pendingUUIDDAO.insertPendingUUID(sourcePendingUUID);
        int numDeleted = pendingUUIDDAO.deletePendingUUID(sourcePendingUUID.getUuid());
        assertEquals(1, numDeleted);
    }

    @Test
    public void testDeletePendingUUIDLowercase() {

        final int numberOfRecordsDeleted = pendingUUIDDAO.deletePendingUUID("84bb7b3f-2a02-438b-a76e-5974bfcb4049");
        assertEquals(1, numberOfRecordsDeleted);
    }

    @Test
    public void testDeletePendingUUIDUppercase() {

        final int numberOfRecordsDeleted = pendingUUIDDAO.deletePendingUUID("84BB7B3F-2A02-438B-A76E-5974BFCB4049");
        assertEquals(1, numberOfRecordsDeleted);
    }

    @Test
    public void testDeletePendingUUIDNonExsistant() {
        jdbcTemplate.execute(" delete from pending_uuid ");
        // insert UUIDs
        PendingUUID sourcePendingUUID = getPendingUUID();
        pendingUUIDDAO.insertPendingUUID(sourcePendingUUID);
        int numDeleted = pendingUUIDDAO.deletePendingUUID("badUuid");
        assertEquals(0, numDeleted);
    }

    @Test
    public void testInsertPendingUUIDList() {
        jdbcTemplate.execute(" delete from pending_uuid ");
        List<PendingUUID> pendingUUIDList = new ArrayList<PendingUUID>();
        for (int i = 0; i < 2050; i++) {
            PendingUUID pendingUUID = getPendingUUID();
            pendingUUID.setUuid("" + i);
            pendingUUIDList.add(pendingUUID);
        }
        pendingUUIDDAO.insertPendingUUIDList(pendingUUIDList);

        assertEquals(2050, jdbcTemplate.queryForInt("select count(*) from pending_uuid"));
    }

    @Test
    public void testGetPendingUuidExists() {

        final String uuid = "84bb7b3f-2a02-438b-a76e-5974bfcb4049";
        final PendingUUID pendingUUID = pendingUUIDDAO.getPendingUuid(uuid);

        assertNotNull(pendingUUID);
        assertEquals(uuid, pendingUUID.getUuid());
    }

    @Test
    public void testGetPendingUuidExistsUpperCase() {

        final String uuid = "84BB7B3F-2A02-438B-A76E-5974BFCB4049";
        final PendingUUID pendingUUID = pendingUUIDDAO.getPendingUuid(uuid);

        assertNotNull(pendingUUID);
        assertEquals(uuid.toLowerCase(), pendingUUID.getUuid());
    }

    @Test
    public void testGetPendingUuidDoesNotExists() {

        final String uuid = "notAValidPendingUuid";
        final PendingUUID pendingUUID = pendingUUIDDAO.getPendingUuid(uuid);

        assertNull(pendingUUID);
    }

    private PendingUUID getPendingUUID() {
        PendingUUID pendingUUID = new PendingUUID();
        pendingUUID.setBatchNumber("3");
        pendingUUID.setAnalyteType("D");
        pendingUUID.setBcr("IGC");
        pendingUUID.setBcrAliquotBarcode("TCGA-DK-A1AC-01A-11D-A13V-01");
        pendingUUID.setUuid("e05d462c-7987-494d-8012-727e75656124");
        pendingUUID.setCenter("20");
        pendingUUID.setItemType("3");
        pendingUUID.setPlateCoordinate("34:45");
        pendingUUID.setPlateId("A182");
        pendingUUID.setPortionNumber("1");
        pendingUUID.setSampleType("01");
        pendingUUID.setShippedDate(new Date());
        pendingUUID.setVialNumber("1");
        return pendingUUID;
    }

    @Test
    public void testUUIDPendingExists() {
        PendingUUID sourcePendingUUID = getPendingUUID();
        pendingUUIDDAO.insertPendingUUID(sourcePendingUUID);
        assertTrue(pendingUUIDDAO.alreadyPendingUUID("e05d462c-7987-494d-8012-727e75656124"));
        assertTrue(pendingUUIDDAO.alreadyPendingBarcode("TCGA-DK-A1AC-01A-11D-A13V-01"));
    }

    @Test
    public void testUUIDPendingNotExists() {
        PendingUUID sourcePendingUUID = getPendingUUID();
        pendingUUIDDAO.insertPendingUUID(sourcePendingUUID);
        assertFalse(pendingUUIDDAO.alreadyPendingUUID("123"));
        assertFalse(pendingUUIDDAO.alreadyPendingUUID(""));
        assertFalse(pendingUUIDDAO.alreadyPendingBarcode("123"));
        assertFalse(pendingUUIDDAO.alreadyPendingBarcode(""));
    }

    @Test
    public void testUUIDShippedBiospecExists() {
        assertTrue(pendingUUIDDAO.alreadyReceivedUUID("uuid2"));
        assertTrue(pendingUUIDDAO.alreadyReceivedBarcode("barcode"));
    }

    @Test
    public void testUUIDShippedBiospecDoesntExists() {
        assertFalse(pendingUUIDDAO.alreadyReceivedUUID("uuid12345"));
        assertFalse(pendingUUIDDAO.alreadyReceivedUUID(null));
        assertFalse(pendingUUIDDAO.alreadyReceivedBarcode("barcode12345"));
        assertFalse(pendingUUIDDAO.alreadyReceivedBarcode(""));
    }

    @Test
    public void testGetPendingUUIDs() {

        final String uuid1 = "84bb7b3f-2a02-438b-a76e-5974bfcb4049";
        final String uuid2 = "12345678-abcd-9012-efgh-345678901234";
        final List<String> uuids = Arrays.asList(uuid1, uuid2, "thisUUIDDoesNotExist");
        final List<PendingUUID> pendingUUIDs = pendingUUIDDAO.getPendingUUIDs(uuids);

        assertNotNull(pendingUUIDs);
        assertEquals(2, pendingUUIDs.size());

        final List<String> retrievedUUIDs = new ArrayList<String>();
        for (final PendingUUID pendingUUID : pendingUUIDs) {

            assertNotNull(pendingUUID);
            retrievedUUIDs.add(pendingUUID.getUuid());
        }

        assertEquals(2, retrievedUUIDs.size());
        assertTrue(retrievedUUIDs.contains(uuid1));
        assertTrue(retrievedUUIDs.contains(uuid2));
    }

    @Test
    public void testGetPendingUUIDsNull() {

        final List<PendingUUID> pendingUUIDs = pendingUUIDDAO.getPendingUUIDs(null);

        assertNotNull(pendingUUIDs);
        assertEquals(0, pendingUUIDs.size());
    }

    @Test
    public void testGetPendingUUIDsEmpty() {

        final List<PendingUUID> pendingUUIDs = pendingUUIDDAO.getPendingUUIDs(new ArrayList<String>());

        assertNotNull(pendingUUIDs);
        assertEquals(0, pendingUUIDs.size());
    }

    @Test
    public void testIsValidCenter() {
        assertTrue(pendingUUIDDAO.isValidCenter("20"));
    }

    @Test
    public void testIsInValidCenter() {
        assertFalse(pendingUUIDDAO.isValidCenter("55"));
    }

    @Test
    public void testIsValidBatchNumber() {
        assertTrue(pendingUUIDDAO.isValidBatchNumber("20"));
    }

    @Test
    public void testIsInValidBatchNumber() {
        assertFalse(pendingUUIDDAO.isValidBatchNumber("555"));
    }

    @Test
    public void testIsSampleType() {
        assertTrue(pendingUUIDDAO.isValidSampleType("C"));
    }

    @Test
    public void testIsInvalidSampleType() {
        assertFalse(pendingUUIDDAO.isValidSampleType("B"));
    }

    @Test
    public void testIsAnalyteType() {
        assertTrue(pendingUUIDDAO.isValidAnalyteType("H"));
    }

    @Test
    public void testIsInvalidAnalyteType() {
        assertFalse(pendingUUIDDAO.isValidAnalyteType("V"));
    }
}
