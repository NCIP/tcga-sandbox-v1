package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BatchNumberAssignment;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BatchNumberQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;

import java.io.File;
import java.util.List;

import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Slow test for batchnumber queries JDBC Implementation
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BatchNumberQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/BatchnumberQueries_TestData.xml";
    private static final String CENTER_DOMAIN_NAME = "nationwidechildrens.org";
    private static final String DISEASE = "COAD";

    private static final String appContextFile = "samples/applicationContext-dbunit.xml";
    private final ApplicationContext appContext;
    private final BatchNumberQueries queries;

    public BatchNumberQueriesJDBCImplDBUnitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        appContext = new ClassPathXmlApplicationContext(appContextFile);
        queries = (BatchNumberQueries) appContext.getBean("batchNumberQueries");

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }


    public void testGetBatchNumberAssignment() {
        final Integer batch_id = 10;
        List<BatchNumberAssignment> batchNumberAssignments = queries.getBatchNumberAssignment(batch_id);
        assertEquals(1, batchNumberAssignments.size());
        BatchNumberAssignment batchNumberAssignment = batchNumberAssignments.get(0);
        assertEquals(batch_id, batchNumberAssignment.getBatchId());
        assertEquals(DISEASE, batchNumberAssignment.getDisease());
        assertEquals(CENTER_DOMAIN_NAME, batchNumberAssignment.getCenterDomainName());
    }

    public void testGetBatchNumberAssignmentMultiple() {
        List<BatchNumberAssignment> batchAssignments = queries.getBatchNumberAssignment(0);
        assertEquals(2, batchAssignments.size());
        assertEquals("CNTL", batchAssignments.get(0).getDisease());
        assertEquals("CNTL", batchAssignments.get(1).getDisease());
        assertEquals("intgen.org", batchAssignments.get(0).getCenterDomainName());
        assertEquals("nationwidechildrens.org", batchAssignments.get(1).getCenterDomainName());
    }

    public void testGetBatchNumberAssignmentNone() {
        assertNull(queries.getBatchNumberAssignment(456));
    }

    public void testIsValidBatchAssignment() {
        assertTrue(queries.isValidBatchNumberAssignment(0, "CNTL", "intgen.org"));
        assertTrue(queries.isValidBatchNumberAssignment(0, "CNTL", "nationwidechildrens.org"));
        assertFalse(queries.isValidBatchNumberAssignment(0, "squirrel", "intgen.org"));
        assertTrue(queries.isValidBatchNumberAssignment(10, "COAD", "nationwidechildrens.org"));

    }

}
