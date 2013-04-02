/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;
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
public class PortionAnalyteQueriesJDBCImplSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/PortionAnalyteQueries_TestData.xml";

    public PortionAnalyteQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    public void testGetPortionAnalytes() {
        PortionAnalyteQueriesJDBCImpl portionAnalyteQueries = new PortionAnalyteQueriesJDBCImpl();
        portionAnalyteQueries.setDataSource(getDataSource());
        List<PortionAnalyte> portionAnalytes = portionAnalyteQueries.getAllPortionAnalytes();
        assertEquals(4, portionAnalytes.size());
        assertEquals("01", portionAnalytes.get(0).getPortionAnalyteCode());
        assertEquals("03", portionAnalytes.get(1).getPortionAnalyteCode());
        assertEquals("04", portionAnalytes.get(2).getPortionAnalyteCode());
        assertEquals("02", portionAnalytes.get(3).getPortionAnalyteCode());
    }
}
