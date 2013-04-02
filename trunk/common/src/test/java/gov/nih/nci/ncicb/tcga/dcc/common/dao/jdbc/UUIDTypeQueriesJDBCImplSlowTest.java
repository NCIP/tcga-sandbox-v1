/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;

import java.io.File;
import java.util.List;

/**
 * DBUnit test for SampleTypeQueries.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class UUIDTypeQueriesJDBCImplSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/UUIDTypeQueries_TestData.xml";

    public UUIDTypeQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    public void testGetUUIDTypes() {
        UUIDTypeQueriesJDBCImpl uuidTypeQueries = new UUIDTypeQueriesJDBCImpl();
        uuidTypeQueries.setDataSource(getDataSource());
        List<UUIDType> uuidTypes = uuidTypeQueries.getAllUUIDTypes();
        assertEquals(4, uuidTypes.size());
        assertEquals(new Integer(4), uuidTypes.get(0).getUuidTypeId());
        assertEquals("Participant", uuidTypes.get(0).getUuidType());
        assertEquals(new Integer(2), uuidTypes.get(1).getUuidTypeId());
        assertEquals("Sample", uuidTypes.get(1).getUuidType());
        assertEquals(new Integer(3), uuidTypes.get(2).getUuidTypeId());
        assertEquals("Analyte", uuidTypes.get(2).getUuidType());
        assertEquals(new Integer(1), uuidTypes.get(3).getUuidTypeId());
        assertEquals("Aliquot", uuidTypes.get(3).getUuidType());
    }

    public void testGetUUIDTypeIDGood() {
        UUIDTypeQueriesJDBCImpl uuidTypeQueries = new UUIDTypeQueriesJDBCImpl();
        uuidTypeQueries.setDataSource(getDataSource());
        assertEquals(new Long(2L), uuidTypeQueries.getUUIDTypeID("Sample"));
        assertEquals(new Long(4L), uuidTypeQueries.getUUIDTypeID("Participant"));
        assertEquals(new Long(3L), uuidTypeQueries.getUUIDTypeID("Analyte"));
        assertEquals(new Long(1L), uuidTypeQueries.getUUIDTypeID("Aliquot"));
    }

    public void testGetUUIDTypeIDBad() {
        UUIDTypeQueriesJDBCImpl uuidTypeQueries = new UUIDTypeQueriesJDBCImpl();
        uuidTypeQueries.setDataSource(getDataSource());
        assertNull(uuidTypeQueries.getUUIDTypeID(null));
        assertNull(uuidTypeQueries.getUUIDTypeID("Viking"));
    }
}
