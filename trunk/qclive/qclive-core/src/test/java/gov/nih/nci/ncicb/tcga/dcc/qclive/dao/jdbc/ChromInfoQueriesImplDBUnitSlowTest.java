package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import org.junit.Test;

import java.io.File;

/**
 * @author Tarek Hassan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ChromInfoQueriesImplDBUnitSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "common.unittest.properties";
    private static final String TEST_DATA_FOLDER = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private static final String TEST_DATA_FILE = "/qclive/dao/ChromInfo_TestData.xml";
    private ChromInfoQueriesImpl queries;

    public ChromInfoQueriesImplDBUnitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new ChromInfoQueriesImpl();
        queries.setDataSource(getDataSource());

    }

    @Test
    public void testGetAllChromInfo() throws Exception {
        // data set has 7 entries
        assertEquals(7, queries.getAllChromInfo().size());
    }

}
