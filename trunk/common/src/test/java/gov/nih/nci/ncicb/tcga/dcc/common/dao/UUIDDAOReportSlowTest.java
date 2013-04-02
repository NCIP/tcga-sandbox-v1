/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Duration;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import org.junit.Test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The test class for tests related to reports in UUIDDAOImpl
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class UUIDDAOReportSlowTest extends DBUnitTestCase {

    private final static String tcgaTestPropertiesFile = "unittest.properties";
    public static final String UUID_DB_DUMP_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    public static final String UUID_DB_FILE = "UUID_Report_TestDB.xml";

    private UUIDDAOImpl uuidDAOImpl;

    public UUIDDAOReportSlowTest() {
        super(UUID_DB_DUMP_FOLDER, UUID_DB_FILE, tcgaTestPropertiesFile);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        uuidDAOImpl = new UUIDDAOImpl();
        uuidDAOImpl.setDataSource(getDataSource());
    }

    @Test
    public void testNewlyGeneratedUUIDsReportForMasterUser() {
        mockDAO();
        List<UUIDDetail> uuidList = uuidDAOImpl.getNewlyGeneratedUUIDs(Duration.Month);
        assertNotNull(uuidList);
        assertEquals(3, uuidList.size());
        assertEquals("00631e7c-f206-468f-9f91-b5ce5249a516", uuidList.get(0).getUuid());
        assertEquals("jye3gf45-3424-7456-s45t-sef4rtw53ah4", uuidList.get(1).getUuid());
        assertEquals("3625e179-1228-4814-bf1c-af0735d3a210", uuidList.get(2).getUuid());

        uuidList = uuidDAOImpl.getNewlyGeneratedUUIDs(Duration.Week);
        assertNotNull(uuidList);
        assertEquals(2, uuidList.size());
        assertEquals("00631e7c-f206-468f-9f91-b5ce5249a516", uuidList.get(0).getUuid());
        assertEquals("jye3gf45-3424-7456-s45t-sef4rtw53ah4", uuidList.get(1).getUuid());

        uuidList = uuidDAOImpl.getNewlyGeneratedUUIDs(Duration.Day);
        assertNotNull(uuidList);
        assertEquals(1, uuidList.size());
        assertEquals("00631e7c-f206-468f-9f91-b5ce5249a516", uuidList.get(0).getUuid());

    }

    @Test
    public void testSubmittedUUIDsReportForMasterUser() {
        List<UUIDDetail> uuidList = uuidDAOImpl.getSubmittedUUIDs();
        assertNotNull(uuidList);
        assertEquals(4, uuidList.size());
        assertEquals("1616f098-483f-4fd5-bd00-5ce1e0bc4092", uuidList.get(0).getUuid());
        assertEquals("00631e7c-f206-468f-9f91-b5ce5249a516", uuidList.get(1).getUuid());
        assertEquals("3625e179-1228-4814-bf1c-af0735d3a210", uuidList.get(2).getUuid());
        assertEquals("9be34abb-c11f-416f-9fd4-34e712676a2f", uuidList.get(3).getUuid());
    }

    @Test
    public void testGetMissingUUIDs() {
        List<UUIDDetail> missingUUIDs = uuidDAOImpl.getMissingUUIDs();
        assertNotNull(missingUUIDs);
        assertEquals(3, missingUUIDs.size());
        assertEquals("a38f4808-d866-4ce8-b056-0b0d35441dde", missingUUIDs.get(0).getUuid());
        assertEquals("7e80377c-7083-45c6-a8c1-6ff51cc3a0a2", missingUUIDs.get(1).getUuid());
        assertEquals("jye3gf45-3424-7456-s45t-sef4rtw53ah4", missingUUIDs.get(2).getUuid());
    }

    private void mockDAO() {
        uuidDAOImpl = new UUIDDAOImpl() {
            // please note that the month value in Calendar API zero based, whereas dates in the dbunit test sample file are 1 based
            protected Date getCurrentDate() {
                Calendar cal = Calendar.getInstance();
                cal.set(2008, 9, 15, 12, 30, 0);
                return cal.getTime();
            }
        };
        uuidDAOImpl.setDataSource(getDataSource());
    }

}
