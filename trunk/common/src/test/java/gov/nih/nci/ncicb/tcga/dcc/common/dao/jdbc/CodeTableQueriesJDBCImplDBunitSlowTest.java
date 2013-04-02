/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import org.junit.Test;

import java.io.File;

/**
 * Test class for CodeTableQueriesJDBCImpl
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class CodeTableQueriesJDBCImplDBunitSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/codeTables_testData.xml";

    private CodeTableQueriesJDBCImpl codeQueries;

    public CodeTableQueriesJDBCImplDBunitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        codeQueries = new CodeTableQueriesJDBCImpl();
        codeQueries.setDataSource(getDataSource());
    }

    @Test
    public void testProjectNameExists() {
        assertTrue(codeQueries.projectNameExists("TCGA"));
        assertFalse(codeQueries.projectNameExists("Incorrect"));
    }

    @Test
    public void testTssCodeExists() {
        assertTrue(codeQueries.tssCodeExists("CJ"));
        assertFalse(codeQueries.tssCodeExists("Incorrect"));
    }

    @Test
    public void testSampleTypeExists() {
        assertTrue(codeQueries.sampleTypeExists("02"));
        assertFalse(codeQueries.sampleTypeExists("Incorrect"));
    }

    @Test
    public void testPortionAnalyteExists() {
        assertTrue(codeQueries.portionAnalyteExists("D"));
        assertFalse(codeQueries.portionAnalyteExists("Incorrect"));
    }

    @Test
    public void testBcrCenterIdExists() {
        assertTrue(codeQueries.bcrCenterIdExists("01"));
        assertFalse(codeQueries.bcrCenterIdExists("Incorrect"));
    }
}
