package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import junit.framework.TestCase;

import java.io.File;
import java.util.Map;

/**
 * Test for DAM Base Queries Processor.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMBaseQueriesProcessorSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "tcga_unittest.properties";
    private static final String TEST_DATA_FOLDER =
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "/portal/dao/BaseQueries_TestDB.xml";

    private DAMBaseQueriesProcessor baseQueriesProcessor;

    public DAMBaseQueriesProcessorSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }


    public void setUp() throws Exception {
        super.setUp();
        baseQueriesProcessor = new DAMBaseQueriesProcessor();
        baseQueriesProcessor.setDataSource(getDataSource());
    }

    public void testGetSampleBatches() {
        Map<String, Integer> batches = baseQueriesProcessor.getBarcodeBatches();
        assertEquals(3, batches.size());

        assertEquals(new Integer(42), batches.get("TCGA-11-2222-01"));
        assertEquals(new Integer(8), batches.get("TCGA-11-3333-11"));
        assertEquals(new Integer(16), batches.get("TCGA-11-4444-10"));
    }
}
