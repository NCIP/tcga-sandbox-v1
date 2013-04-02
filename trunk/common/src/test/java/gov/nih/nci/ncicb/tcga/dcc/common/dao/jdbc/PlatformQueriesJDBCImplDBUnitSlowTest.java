package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Slow test for Platform queries JDBC Implementation
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class PlatformQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "unittest.properties";

    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String TEST_DATA_FILE = "dao" + File.separator + "PlatformQueries_TestData.xml";

    private static final String appContextFile = "samples/applicationContext-dbunit.xml";
    private final ApplicationContext appContext;
    private final PlatformQueries queries;
    Platform testPlatform;

    public PlatformQueriesJDBCImplDBUnitSlowTest() {

        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        appContext = new ClassPathXmlApplicationContext(appContextFile);
        queries = (PlatformQueries) appContext.getBean("platformQueries");
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Read the first entry from DBUnit test data and initialize platform object
        testPlatform = getPlatformDBTestObject();
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    @Test
    public void testGetPlatformIdByName() throws Exception {
        assertEquals(testPlatform.getPlatformId(), queries.getPlatformIdByName(testPlatform.getPlatformName()));
    }

    @Test
    public void testEmptyDataAccessForPlatformIdByName() throws Exception {

        // platform name doesn't exist in the data set
        final int id = queries.getPlatformIdByName("TEST");
        assertEquals("Unexpected Id", -1, id);
    }

    @Test
    public void testGetPlatformForName() throws Exception {

        final Platform dbPlatform = queries.getPlatformForName(testPlatform.getPlatformName());
        assertNotNull(dbPlatform);
        assertTrue(dbPlatform.equals(testPlatform));
    }

    @Test
    public void testEmptyDataAccessForPlatformName() throws Exception {

        // platform name doesn't exist in the data set
        final Platform platform = queries.getPlatformForName("TEST");
        assertNull(platform);
    }

    @Test
    public void testGetAllPlatforms() throws Exception {

        final Collection<Map<String, Object>> dbPlatformList = queries.getAllPlatforms();
        assertNotNull(dbPlatformList);

        final ITable platformTable = getDataSet().getTable("platform");
        assertTrue(dbPlatformList.size() == platformTable.getRowCount());

        // compare the primary key of test data and db data
        final List<Integer> testData = new ArrayList();
        final List<Integer> dbData = new ArrayList();

        for (int i = 0; i < platformTable.getRowCount(); i++) {
            testData.add(Integer.parseInt((String) platformTable.getValue(i, "platform_id")));
        }

        for (final Map valueMap : dbPlatformList) {
            dbData.add(((Number) valueMap.get("PLATFORM_ID")).intValue());
        }
//        for (final Object ob : dbPlatformList.toArray()) {
//            ListOrderedMap dbPlatform = (ListOrderedMap) ob;
//            dbData.add(((Number) dbPlatform.getValue(dbPlatform.indexOf("PLATFORM_ID"))).intValue());
//        }

        assertTrue(testData.containsAll(dbData));

    }

    @Test
    public void testGetPlatformNameById() throws Exception {
        assertEquals(testPlatform.getPlatformName(), queries.getPlatformNameById(testPlatform.getPlatformId()));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testEmptyDataAccessForPlatformNameById() throws Exception {

        try {
            // platform id doesn't exist in the data set
            queries.getPlatformNameById(1);
            fail("EmptyResultDataAccessException should have been raised");

        } catch (final EmptyResultDataAccessException e) {
            // As this class is extended from DBUnit TestCase @Test annotation is not being used.
            // So the exception is captured to validate
        }

    }

    @Test
    public void testGetPlatformWithAlias() throws Exception {

        final Platform dbPlatform = queries.getPlatformWithAlias(testPlatform.getPlatformAlias());

        assertNotNull(dbPlatform);
        assertTrue(dbPlatform.equals(testPlatform));
    }

    @Test
    public void testGetPlatformWithAliasNonExisting() throws Exception {

        final Platform platform = queries.getPlatformWithAlias("THAT_DOES_NOT_EXIST");
        assertNull(platform);
    }

    @Test
    public void testGetPlatformById() {

        final Platform platform = queries.getPlatformById(36);
        assertNotNull(platform);
        assertEquals("Unexpected platform name", "Human1MDuo", platform.getPlatformName());
    }

    @Test
    public void testGetPlatformByIdDoesNotExist() {

        final Platform platform = queries.getPlatformById(0);
        assertNull(platform);
    }

    /**
     * returns platform object from DBUnit test data first entry
     *
     * @return platform object from DBUnit test data first entry
     * @throws Exception
     */
    private Platform getPlatformDBTestObject() throws Exception {

        final Platform platform = new Platform();

        // Read the first entry from the DBUnit Test data and create Platform object
        ITable platformTable = getDataSet().getTable("platform");
        platform.setPlatformDisplayName((String) platformTable.getValue(0, "platform_display_name"));
        platform.setPlatformId(Integer.parseInt((String) platformTable.getValue(0, "platform_id")));
        platform.setPlatformName((String) platformTable.getValue(0, "platform_name"));
        platform.setCenterType((String) platformTable.getValue(0, "center_type_code"));
        platform.setPlatformAlias((String) platformTable.getValue(0, "platform_alias"));

        return platform;
    }
}
