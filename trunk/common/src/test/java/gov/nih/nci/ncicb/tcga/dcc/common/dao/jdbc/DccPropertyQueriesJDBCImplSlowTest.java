package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccProperty;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DccPropertyQueries;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.List;

/**
 * Test class for Dccproperty queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DccPropertyQueriesJDBCImplSlowTest  extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/DccPropertyTestData.xml";
    private static final String appContextFile = "samples/applicationContext-dbunit.xml";
    private final ApplicationContext appContext;
    private final DccPropertyQueries queries;

    public DccPropertyQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        appContext = new ClassPathXmlApplicationContext(appContextFile);
        queries = (DccPropertyQueries) appContext.getBean("dccPropertyQueries");
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    public void testGetPropertyValue() throws Exception {
        final String value = queries.getPropertyValue("tcga.dam.property_name_1","dam");
        assertEquals("dam_value_1",value);
    }

    public void testGetDccPropertiesForAnApplication() throws Exception {
        final DccProperty expectedData = new DccProperty();
        expectedData.setPropertyId(4l);
        expectedData.setPropertyDescription("property_desc");
        expectedData.setPropertyName("tcga.qclive.property_name_1");
        expectedData.setPropertyValue("qclive_value_1");
        expectedData.setServerName("server");
        expectedData.setApplicationName("qclive");

        final List<DccProperty> properties = queries.getDccPropertiesForAnApplication("qclive");
        assertEquals(1,properties.size());
        assertEquals(expectedData,properties.get(0));
    }

    public void testAddProperty() throws Exception {
        final DccProperty expectedData = new DccProperty();
        expectedData.setPropertyId(DccProperty.UNASSIGNED_PROPERTY_ID);
        expectedData.setPropertyDescription("property_desc");
        expectedData.setPropertyName("tcga.uuid.property_name_1");
        expectedData.setPropertyValue("uuid_value_1");
        expectedData.setServerName("server");
        expectedData.setApplicationName("uuid");

        queries.addOrUpdateProperty(expectedData);
        final DccProperty property = queries.getDccProperty("tcga.uuid.property_name_1","uuid");
        assertNotNull(property);
        expectedData.setPropertyId(property.getPropertyId());
        assertEquals(expectedData,property);
    }

    public void testUpdateProperty() throws Exception {
        final DccProperty expectedData = new DccProperty();
        expectedData.setPropertyId(2l);
        expectedData.setPropertyDescription("property_desc");
        expectedData.setPropertyName("tcga.dam.property_name_2");
        expectedData.setPropertyValue("new value");
        expectedData.setServerName("server");
        expectedData.setApplicationName("dam");

        queries.addOrUpdateProperty(expectedData);
        final DccProperty property = queries.getDccProperty("tcga.dam.property_name_2","dam");
        assertNotNull(property);
        assertEquals(expectedData,property);
    }

    public void testGetDccProperty() throws Exception {
        final DccProperty expectedData = new DccProperty();
        expectedData.setPropertyId(2l);
        expectedData.setPropertyDescription("property_desc");
        expectedData.setPropertyName("tcga.dam.property_name_2");
        expectedData.setPropertyValue("dam_value_2");
        expectedData.setServerName("server");
        expectedData.setApplicationName("dam");

        final DccProperty property = queries.getDccProperty("tcga.dam.property_name_2","dam");
        assertNotNull(property);
        assertEquals(expectedData,property);
    }

    public void testDeleteProperty() throws Exception {
           final DccProperty expectedData = new DccProperty();
           expectedData.setPropertyId(2l);
           expectedData.setPropertyDescription("property_desc");
           expectedData.setPropertyName("tcga.dam.property_name_2");
           expectedData.setPropertyValue("dam_value_2");
           expectedData.setServerName("server");
           expectedData.setApplicationName("dam");

            queries.deleteProperty(expectedData);
           final DccProperty property = queries.getDccProperty("tcga.dam.property_name_2","dam");
           assertNull(property);
       }


}
