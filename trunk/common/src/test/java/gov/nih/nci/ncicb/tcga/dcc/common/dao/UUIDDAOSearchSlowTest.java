/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The test class for tests related to search functionality in UUIDDAOImpl
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class UUIDDAOSearchSlowTest extends DBUnitTestCase {

    private final static String tcgaTestPropertiesFile = "unittest.properties";
    public static final String UUID_DB_DUMP_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    public static final String UUID_DB_FILE = "UUID_TestDB.xml";
    private UUIDDAOImpl uuidDAOImpl;

    public UUIDDAOSearchSlowTest() {
        super(UUID_DB_DUMP_FOLDER, UUID_DB_FILE, tcgaTestPropertiesFile);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        uuidDAOImpl = new UUIDDAOImpl();
        uuidDAOImpl.setDataSource(getDataSource());
    }

    @Test
    public void testSearchUUIDByUUID() throws UUIDException {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setUuid("9be34abb-c11f-416f-9fd4-34e712676a2f");

        List<UUIDDetail> uuidList = uuidDAOImpl.searchUUIDs(criteria);
        assertNotNull(uuidList);
        assertEquals(1, uuidList.size());
        assertEquals("9be34abb-c11f-416f-9fd4-34e712676a2f", uuidList.get(0).getUuid());
    }

    @Test
    public void testSearchUUIDByCenter() throws UUIDException {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setCenterId(1);

        List<UUIDDetail> uuidList = uuidDAOImpl.searchUUIDs(criteria);
        assertNotNull(uuidList);
        assertEquals(2, uuidList.size());
        assertEquals("00631e7c-f206-468f-9f91-b5ce5249a516", uuidList.get(0).getUuid());
        assertEquals("9be34abb-c11f-416f-9fd4-34e712676a2f", uuidList.get(1).getUuid());

        //center number set to some bad value
        int centerId = uuidList.get(0).getCenter().getCenterId();
        assertEquals(1, centerId);
        centerId = uuidList.get(1).getCenter().getCenterId();
        assertEquals(1, centerId);
    }

    @Test
    public void testSearchUUIDByDiseaseAndCenter() throws UUIDException {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setCenterId(1);
        criteria.setDisease(1);

        List<UUIDDetail> uuidList = uuidDAOImpl.searchUUIDs(criteria);
        assertNotNull(uuidList);
        assertTrue(uuidList.size() > 0);
        int centerId = uuidList.get(0).getCenter().getCenterId();
        assertEquals(1, centerId);
    }

    @Test
    public void testGetSearchQuery() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setUuid("some uuid1");
        criteria.setBarcode("some barcode");
        criteria.setCenterId(1);
        criteria.setCreationDate(new Date());
        criteria.setDisease(1);
        criteria.setSubmittedBy("some user");

        String query = uuidDAOImpl.getSearchQuery(criteria, new ArrayList<Object>());
        assertNotNull(query);
        String expected = "SELECT U.UUID, U.CENTER_ID, DOMAIN_NAME, CENTER_TYPE_CODE, " +
                "U.CREATED_BY, U.CREATE_DATE, U.GENERATION_METHOD_ID, U.LATEST_BARCODE_ID, B.BARCODE, D.DISEASE_ABBREVIATION " +
                " FROM UUID U INNER JOIN CENTER C ON U.CENTER_ID = C.CENTER_ID " +
                " LEFT OUTER JOIN BARCODE_HISTORY B ON B.BARCODE_ID = U.LATEST_BARCODE_ID " +
                " LEFT OUTER JOIN DISEASE D ON B.DISEASE_ID = D.DISEASE_ID " +
                " WHERE 1=1  and U.UUID = ?  and B.BARCODE = ?  and B.DISEASE_ID = ?  and U.CENTER_ID = ? " +
                " and U.CREATED_BY = ?  and U.CREATE_DATE > ?  and U.CREATE_DATE < ?  ORDER BY U.CREATE_DATE DESC ";

        assertEquals(expected, query);
    }

    @Test
    public void testGetSearchQueryForCenter() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setCenterId(1);
        String query = uuidDAOImpl.getSearchQuery(criteria, new ArrayList<Object>());
        assertNotNull(query);
        String expected = "SELECT U.UUID, U.CENTER_ID, DOMAIN_NAME, CENTER_TYPE_CODE, " +
                "U.CREATED_BY, U.CREATE_DATE, U.GENERATION_METHOD_ID, U.LATEST_BARCODE_ID, B.BARCODE, D.DISEASE_ABBREVIATION " +
                " FROM UUID U INNER JOIN CENTER C ON U.CENTER_ID = C.CENTER_ID " +
                " LEFT OUTER JOIN BARCODE_HISTORY B ON B.BARCODE_ID = U.LATEST_BARCODE_ID " +
                " LEFT OUTER JOIN DISEASE D ON B.DISEASE_ID = D.DISEASE_ID " +
                " WHERE 1=1  and U.CENTER_ID = ? " +
                " ORDER BY U.CREATE_DATE DESC ";

        assertEquals(expected, query);
    }

    @Test
    public void testGetSearchQueryForDisease() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setDisease(1);
        String query = uuidDAOImpl.getSearchQuery(criteria, new ArrayList<Object>());
        assertNotNull(query);
        String expected = "SELECT U.UUID, U.CENTER_ID, DOMAIN_NAME, CENTER_TYPE_CODE, " +
                "U.CREATED_BY, U.CREATE_DATE, U.GENERATION_METHOD_ID, U.LATEST_BARCODE_ID, B.BARCODE, D.DISEASE_ABBREVIATION " +
                " FROM UUID U INNER JOIN CENTER C ON U.CENTER_ID = C.CENTER_ID " +
                " LEFT OUTER JOIN BARCODE_HISTORY B ON B.BARCODE_ID = U.LATEST_BARCODE_ID " +
                " LEFT OUTER JOIN DISEASE D ON B.DISEASE_ID = D.DISEASE_ID " +
                " WHERE 1=1  and B.DISEASE_ID = ? " +
                " ORDER BY U.CREATE_DATE DESC ";

        assertEquals(expected, query);
    }


    @Test
    public void testGetSearchQueryForUUID() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setUuid("some uuid");
        String query = uuidDAOImpl.getSearchQuery(criteria, new ArrayList<Object>());
        assertNotNull(query);
        String expected = "SELECT U.UUID, U.CENTER_ID, DOMAIN_NAME, CENTER_TYPE_CODE, " +
                "U.CREATED_BY, U.CREATE_DATE, U.GENERATION_METHOD_ID, U.LATEST_BARCODE_ID, B.BARCODE, D.DISEASE_ABBREVIATION " +
                " FROM UUID U INNER JOIN CENTER C ON U.CENTER_ID = C.CENTER_ID " +
                " LEFT OUTER JOIN BARCODE_HISTORY B ON B.BARCODE_ID = U.LATEST_BARCODE_ID " +
                " LEFT OUTER JOIN DISEASE D ON B.DISEASE_ID = D.DISEASE_ID " +
                " WHERE 1=1  and U.UUID = ? " +
                " ORDER BY U.CREATE_DATE DESC ";

        assertEquals(expected, query);
    }

}
