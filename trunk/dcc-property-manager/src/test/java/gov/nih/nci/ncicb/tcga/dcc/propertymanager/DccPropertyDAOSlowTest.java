/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.propertymanager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"/app-context-test.xml"})
public class DccPropertyDAOSlowTest {
		
	@Autowired     
	ApplicationContext applicationContext;
	DccPropertyCache cache;
	@Autowired
	SessionFactory mySessionFactory;	
	@Autowired
	DataSource ds;
			
	@Before
	public void setUp(){
		cache = new DccPropertyCache();
		cache.context = applicationContext;		
		populateTestData();		
	}	
			
	
	@After
	public void tearDown(){
		deleteTestData();
	}
	
	@Test
	public void testLoadProperty() throws Exception{		
		 List<DccProperty> allProps = cache.getAllProperties();	
		 Set propSet = new HashSet();
		 for (Iterator<DccProperty> i = allProps.iterator(); i.hasNext();){
			 DccProperty prop = i.next();
			 propSet.add(prop.getPropertyValue());
		 }
		 
		 assertTrue (propSet.contains("testValue3"));
		 assertTrue (propSet.contains("testValue4"));
		 assertTrue (propSet.contains("testValue1"));
		 assertTrue (propSet.contains("testValue2"));		 		
	}
	
	@Test
	public void testGetProperty() throws Exception{				 			 		
		 assertEquals ("testValue1",cache.getProperty("testProp1")); 
		 assertEquals ("testValue2",cache.getProperty("testProp2"));		 		
	}
	
	@Test
	public void testGetEmptyProperty() throws Exception{				 			 		
		 assertEquals ("",cache.getProperty("")); 
		 assertEquals ("",cache.getProperty(null));
		 assertEquals ("",cache.getProperty("testProp4"));		 		
	}	
	
	@Test
	public void testGetAllPropertiesForAnApplication() throws Exception{				 			 		
		 List<DccProperty> allProps = cache.getAllPropertiesForAnApplication("dam",null);
		 Assert.assertTrue(allProps.size() == 2);	
		 assertEquals ("testProp3",allProps.get(0).getPropertyName());
		 assertEquals ("testProp3",allProps.get(1).getPropertyName());
		 assertEquals ("testValue3",allProps.get(0).getPropertyValue());
		 assertEquals ("testValue4",allProps.get(1).getPropertyValue());
		 
		 // test MixedCase
		 allProps = cache.getAllPropertiesForAnApplication("dAm",null);
		 Assert.assertTrue(allProps.size() == 2);	
		 assertEquals ("testValue3",allProps.get(0).getPropertyValue());
		 assertEquals ("testValue4",allProps.get(1).getPropertyValue());
		 
		 allProps = cache.getAllPropertiesForAnApplication("qClIve",null);
		 Assert.assertTrue(allProps.size() == 1);	
		 assertEquals ("testValue1",allProps.get(0).getPropertyValue());		 
	}

	@Test
	public void testGetAllPropertiesForAnApplicationServer() throws Exception{				 			 		
		 List<DccProperty> allProps = cache.getAllPropertiesForAnApplication("dam","test-server3");
		 Assert.assertTrue(allProps.size() == 1);	
		 assertEquals ("testProp3",allProps.get(0).getPropertyName());
		 assertEquals ("testValue3",allProps.get(0).getPropertyValue());	
		 
		 allProps = cache.getAllPropertiesForAnApplication(null,"test-server3");
		 Assert.assertTrue(allProps.size() == 1);	
		 assertEquals ("testProp3",allProps.get(0).getPropertyName());
		 assertEquals ("testValue3",allProps.get(0).getPropertyValue());
		 
		 allProps = cache.getAllPropertiesForAnApplication(null,"");
		 Assert.assertTrue(allProps.size() == 4);
		 
		 allProps = cache.getAllPropertiesForAnApplication("","");
		 Assert.assertTrue(allProps.size() == 4);
	}
	@Test
	public void testGetInvalidProperty() throws Exception{				 			 		
		 List<DccProperty> allProps = cache.getAllPropertiesForAnApplication("dam","test-server99");
		 Assert.assertTrue(allProps.size() == 0);			 				
	}
	
    @Test
    public void getDccPropertyList() throws Exception{
        final List<DccProperty> actualData = cache.getDccPropertyList();
        Assert.assertTrue(actualData.size() == 4);
        final List<DccProperty> expectedData = getDccPropertyObjects();
        assertTrue(actualData.containsAll(expectedData));
    }
	
	@Test
	public void testGetAppPropertiesInvalidApp() throws Exception{				 			 		
		 List<DccProperty> allProps = cache.getAllPropertiesForAnApplication("qcliveU",null);			 
		 Assert.assertTrue(allProps.size() == 0);			
	}

    @Test
    public void addProperty() {
        assertFalse( "newValue".equals(cache.getProperty("newProperty")));
        final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyId(DccProperty.UNASSIGNED_PROPERTY_ID);
        dccProperty.setPropertyName("newProperty");
        dccProperty.setPropertyValue("newValue");
        dccProperty.setApplicationName("qclive");
        cache.addOrUpdateProperty(dccProperty);
        assertTrue( "newValue".equals(cache.getProperty("newProperty")));
    }

    @Test
    public void updateProperty() {
        List<DccProperty>  properties =  getDccPropertyObjects();
        DccProperty propertyToUpdate = properties.get(0);
        assertTrue( propertyToUpdate.getPropertyValue().equals(cache.getProperty(propertyToUpdate.getPropertyName())));
        propertyToUpdate.setPropertyValue("newValue");
        cache.addOrUpdateProperty(propertyToUpdate);
        assertTrue( "newValue".equals(cache.getProperty(propertyToUpdate.getPropertyName())));
    }


    @Test
    public void deleteProperty() {
        List<DccProperty>  properties =  getDccPropertyObjects();
        DccProperty propertyToDelete = properties.get(0);
        assertTrue( propertyToDelete.getPropertyValue().equals(cache.getProperty(propertyToDelete.getPropertyName())));
        cache.deleteProperty(propertyToDelete);
        assertTrue( "".equals(cache.getProperty(propertyToDelete.getPropertyName())));
    }
    
    @Test
    public void testGetAllPropForAppSvr(){
    	 List<DccProperty>  properties =  getDccPropertyObjects();
         DccProperty propertyToDelete = properties.get(0);
    }

	/**
	 * populates test data used in the test
	 */	
	private void populateTestData(){
		JdbcTemplate template = new JdbcTemplate(ds);
		// delete all from the table
		template.update(" delete from dcc_property");
        final List<DccProperty> dccProperties =  getDccPropertyObjects();
		// insert test rows		
        for(final DccProperty dccProperty: dccProperties){
            final StringBuffer insertQuery = new StringBuffer("insert into dcc_property values (")
                    .append(dccProperty.getPropertyId())
                    .append(",'")
                    .append(dccProperty.getPropertyName())
                    .append("','")
                    .append(dccProperty.getPropertyValue())
                    .append("','")                    
                    .append(dccProperty.getPropertyDescription())
                    .append("','")
                    .append(dccProperty.getApplicationName())
                    .append("','")
                    .append(dccProperty.getServerName())
                    .append("')");
            template.update(insertQuery.toString());
        }
	}
	
	/**
	 * deletes test data used in the test
	 */	
	private void deleteTestData(){		
		new JdbcTemplate(ds).update("delete from dcc_property p where p.PROPERTY_NAME like '%testProp%'");		 		 
	}
    
    private List<DccProperty>  getDccPropertyObjects(){
        final List<DccProperty> dccProperties = new ArrayList<DccProperty>();
        for(int i=1; i < 5; i++){
            final DccProperty dccProperty = new DccProperty();
            dccProperty.setPropertyId((long)i);
            dccProperty.setPropertyName("testProp" + i);
            dccProperty.setPropertyValue("testValue"+i);
            dccProperty.setPropertyDescription("testDesc"+i);            
            dccProperty.setServerName("test-server" + i);
            if(i == 3){
                dccProperty.setApplicationName("dam");
            }else if (i == 4){
                dccProperty.setApplicationName("dam");
                dccProperty.setPropertyName("testProp" + (i - 1));                
            }else if (i == 1){
                dccProperty.setApplicationName("qclive");
            }else if (i == 2){
                dccProperty.setApplicationName("uuid");
            }
            dccProperties.add(dccProperty);
        }
        return dccProperties;        
    }
}





