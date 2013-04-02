package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Slow test for Datatype queries JDBC Implementation
 *
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */

public class DataTypeQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/DataTypeQueries_TestData.xml";

    private static final String appContextFile = "samples/applicationContext-dbunit.xml";
    private static final int INVALID_PLATFORM_ID = -1;
    private final ApplicationContext appContext;
    private final DataTypeQueries queries;
    Platform testPlatform;

    public DataTypeQueriesJDBCImplDBUnitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        appContext = new ClassPathXmlApplicationContext(appContextFile);
        queries = (DataTypeQueries) appContext.getBean("dataTypeQueries");

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Read the platform table first entry from DBUnit test data and initialize platform object
        testPlatform = getPlatformTestObject();
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }


    public void testGetFirstDataTypeDisplayNameForPlatform() throws Exception {
        final String dbData = queries.getBaseDataTypeDisplayNameForPlatform(testPlatform.getPlatformId());
        assertNotNull(dbData);
        assertEquals(getBaseDataTypeDisplayNameTestObject(testPlatform.getPlatformId()), dbData);
    }

    public void testEmptyDataAccessForGetFirstDataTypeDisplayNameForPlatform() throws Exception {
        try {
            // platform id doesn't exsist in the data set
            queries.getBaseDataTypeDisplayNameForPlatform(INVALID_PLATFORM_ID);
        } catch (Exception e) {
            assertTrue(e instanceof EmptyResultDataAccessException);
        }

    }

    public void testGetCenterTypeIdForPlatformId() throws Exception {
        assertEquals(testPlatform.getCenterType(), queries.getCenterTypeIdForPlatformId(testPlatform.getPlatformId()));
    }

    public void testEmptyDataAccessForGetCenterTypeIdForPlatformId() throws Exception {
        try {
            // platform id doesn't exsist in the data set
            queries.getCenterTypeIdForPlatformId(INVALID_PLATFORM_ID);
        } catch (Exception e) {
            assertTrue(e instanceof EmptyResultDataAccessException);
        }

    }

    public void testGetAllDataTypes() throws Exception {
        // get db datatype list
        final Collection<Map<String, Object>> dbDataTypeList = queries.getAllDataTypes();
        assertNotNull(dbDataTypeList);

        ITable dataTypeTable = getDataSet().getTable("data_type");
        assertTrue(dbDataTypeList.size() == dataTypeTable.getRowCount());


        final List<Integer> testData = new ArrayList<Integer>();
        final List<Integer> dbData = new ArrayList<Integer>();

        // get tet datatype list
        for (int i = 0; i < dataTypeTable.getRowCount(); i++) {
            testData.add(Integer.parseInt((String) dataTypeTable.getValue(i, "data_type_id")));
        }
        for (final Map valueMap : dbDataTypeList) {
            dbData.add(((Number) valueMap.get("DATA_TYPE_ID")).intValue());
        }

        // compare the primary key of test data and db data
        assertTrue(testData.containsAll(dbData));


    }

    public void testGetDataTypeFTPDisplayForPlatform() throws Exception {
        assertEquals("clin", queries.getDataTypeFTPDisplayForPlatform("36"));
        assertEquals("snp", queries.getDataTypeFTPDisplayForPlatform("7"));
    }

    public void testEmptyDataAccessForGetDataTypeFTPDisplayForPlatform() throws Exception {
        assertNull(queries.getDataTypeFTPDisplayForPlatform(String.valueOf(INVALID_PLATFORM_ID)));
    }

    public void testGetDataTypesId() throws Exception{
        final Map<String,Long> actualValue =  queries.getAllDataTypesId();
        assertEquals(3, actualValue.size());

        final Map<String,Long> expectedValue = new HashMap<String,Long>();
        expectedValue.put("Complete Clinical Set",1l);
        expectedValue.put("SNP",7l);
        expectedValue.put("Copy Number Results",8l);

        for(final String dataTypeName: expectedValue.keySet()){
            assertEquals(expectedValue.get(dataTypeName),actualValue.get(dataTypeName));
        }

    }

    /**
     * returns platform object from  DBUnit test data first entry
     *
     * @return
     * @throws Exception
     */

    private Platform getPlatformTestObject() throws Exception {
        // Read the first entry from the DBUnit Test data and create Platform object
        final Platform platform = new Platform();
        ITable platformTable = getDataSet().getTable("platform");
        platform.setPlatformDisplayName((String) platformTable.getValue(0, "platform_display_name"));
        platform.setPlatformId(Integer.parseInt((String) platformTable.getValue(0, "platform_id")));
        platform.setPlatformName((String) platformTable.getValue(0, "platform_name"));
        platform.setCenterType((String) platformTable.getValue(0, "center_type_code"));
        return platform;
    }


    private String getBaseDataTypeDisplayNameTestObject(int platformId) throws Exception {
        ITable dataTypeTable = getDataSet().getTable("data_type");
        ITable platformTable = getDataSet().getTable("platform");

        // get the base data type id  for given platform id
        int baseDataTypeId = -1;
        for (int i = 0; i < platformTable.getRowCount(); i++) {
            if (Integer.parseInt((String) platformTable.getValue(i, "platform_id")) == platformId) {
                baseDataTypeId = Integer.parseInt((String) platformTable.getValue(i, "base_data_type_id"));
                break;
            }
        }
        // get base data type name from datatype table for the given platform id
        for (int i = 0; i < dataTypeTable.getRowCount(); i++) {
            if (Integer.parseInt((String) dataTypeTable.getValue(i, "data_type_id")) == baseDataTypeId)
                return ((String) dataTypeTable.getValue(i, "name"));
        }
        return null;
    }


}
