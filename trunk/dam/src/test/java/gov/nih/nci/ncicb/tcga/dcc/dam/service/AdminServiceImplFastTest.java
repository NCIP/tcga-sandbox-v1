package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccProperty;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DccPropertyQueries;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test class for AdminServiceImpl
 * @author ramanr
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class AdminServiceImplFastTest {
    private final Mockery context = new JUnit4Mockery();
    private AdminServiceImpl adminService;
    private DccPropertyQueries mockDccQueries;

    @Before
    public void setUp() throws Exception {
        adminService = new AdminServiceImpl();
        mockDccQueries = context.mock(DccPropertyQueries.class);
        adminService.setDccPropertyCacheUtil(mockDccQueries);
    }



    @Test
    public void getAllPropertiesForAnApplication()throws Exception{

        final List<DccProperty> properties = new ArrayList<DccProperty>();
        final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyName("property");
        dccProperty.setPropertyValue("value");

        context.checking(new Expectations() {{
            one(mockDccQueries).getDccPropertiesForAnApplication("dam");
            will(returnValue(properties));
        }});

        final List<DccProperty> actualProperties = adminService.getAllPropertiesForAnApplication("dam");
        assertTrue(properties.equals(actualProperties));

    }

    @Test
      public void addProperty()throws Exception{

          final DccProperty dccProperty = new DccProperty();
          dccProperty.setPropertyName("property");
          dccProperty.setPropertyValue("value");

          context.checking(new Expectations() {{
              one(mockDccQueries).addOrUpdateProperty(dccProperty);
          }});

          adminService.addOrUpdateProperty(dccProperty);

      }

    @Test
    public void updateProperty()throws Exception{

          final DccProperty dccProperty = new DccProperty();
          dccProperty.setPropertyName("property");
          dccProperty.setPropertyValue("value");

          context.checking(new Expectations() {{
              one(mockDccQueries).addOrUpdateProperty(dccProperty);
          }});

          adminService.addOrUpdateProperty(dccProperty);

    }

    @Test
    public void deleteProperty()throws Exception{

          final DccProperty dccProperty = new DccProperty();
          dccProperty.setPropertyName("property");
          dccProperty.setPropertyValue("value");

          context.checking(new Expectations() {{
              one(mockDccQueries).deleteProperty(dccProperty);
          }});

          adminService.deleteProperty(dccProperty);

    }

}
