/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * DBUnit tests for TumorQueries.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TumorQueriesJDBCImplSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/TumorQueries_TestData.xml";

    private TumorQueriesJDBCImpl tumorQueries;

    public TumorQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }
    public void setUp() throws Exception {
        super.setUp();
        tumorQueries = new TumorQueriesJDBCImpl();
        tumorQueries.setDataSource(getDataSource());
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetTumorForId() {
        Tumor disease1 = tumorQueries.getTumorForId(1);
        assertNotNull(disease1);
        assertEquals("TEST1", disease1.getTumorName());
    }

    public void testGetTumorForIdBadId() {
        Tumor nonExistent = tumorQueries.getTumorForId(12345);
        assertNull(nonExistent);
    }

    public void testGetTumorForName() {
        Tumor disease2 = tumorQueries.getTumorForName("TEST2");
        assertEquals(new Integer(2), disease2.getTumorId());
    }

    public void testGetTumorIdForName() {
        Integer id = tumorQueries.getTumorIdByName("TEST1");
        assertEquals((Integer) 1, id);
    }

    public void testGetTumorIdForNameBad() {
        Integer id = tumorQueries.getTumorIdByName("too much cake");
        assertEquals( new Integer(-1), id);
    }

    public void testGetAllTumors() {
        Collection<Map<String, Object>> allTumors = tumorQueries.getAllTumors();
        assertEquals(2, allTumors.size());
        for (final Map tumorInfo : allTumors) {
            assertNotNull(tumorInfo.get("DISEASE_ID"));
            assertNotNull(tumorInfo.get("DISEASE_ABBREVIATION"));
            assertNotNull(tumorInfo.get("DISEASE_NAME"));
        }
    }

    public void testGetTissueIdsForTumor() {
        List<Integer> tissueIds = tumorQueries.getTissueIdsForTumor("TEST1");
        assertEquals(2, tissueIds.size());
        assertEquals(20, tissueIds.get(0).intValue());
        assertEquals(21, tissueIds.get(1).intValue());
    }

    public void testGetTissueIdForTumorBad() {
        List<Integer> tissueIds = tumorQueries.getTissueIdsForTumor("Friday");
        assertEquals(0, tissueIds.size());
    }


}
