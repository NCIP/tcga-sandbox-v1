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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class DccPropertyCacheUtilMBeanImplTest {

	DccPropertyCacheUtilMBeanImpl dccPropertyCache;
	DccPropertyCacheMBean mockCache;
    private final Mockery context = new JUnit4Mockery();   
	
	
	@Before
	public void setUp(){
		mockCache = context.mock(DccPropertyCacheMBean.class);
		dccPropertyCache =  DccPropertyCacheUtilMBeanImpl.getInstance();
		dccPropertyCache.mbeanProxyReference = mockCache;		
	}
	
	@Test
	public void getDccProperty(){
		
		context.checking(new Expectations() {{
	            one(mockCache).getProperty("testProp1");
	            will(returnValue("testVal1"));
	            one(mockCache).getProperty("");
	            will(returnValue(""));
	        }});
		Assert.assertEquals("testVal1",dccPropertyCache.getDccProperty("testProp1"));
		Assert.assertEquals("",dccPropertyCache.getDccProperty(""));		
	}
								
	@Test
	public void getAllDccProperties(){
		final  List<DccProperty>  returnPropList = new ArrayList<DccProperty>();
		
		DccProperty property = new DccProperty();
		property.setPropertyName("param1");
		property.setPropertyValue("val1");
		returnPropList.add(property);		
		
		context.checking(new Expectations() {{
            one(mockCache).getAllProperties();
            will(returnValue(returnPropList));
		}});
		Properties resultingProperties = dccPropertyCache.getAllDccProperties();
		assertEquals("val1",resultingProperties.get("param1"));
	}

    @Test
    public void getDccPropertyList(){
        final List<DccProperty> properties = new ArrayList<DccProperty>();
        final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyName("property");
        dccProperty.setPropertyValue("value");
        properties.add(dccProperty);

        context.checking(new Expectations() {{
            one(mockCache).getDccPropertyList();
            will(returnValue(properties));
        }});
        final List<DccProperty> actualProperties = dccPropertyCache.getDccPropertyList();
        assertTrue(properties.equals(actualProperties));
    }


    @Test
	public void getAllDccPropertiesForAnApp(){
    	final List<DccProperty> properties = new ArrayList<DccProperty>();
    	final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyName("param1");
        dccProperty.setPropertyValue("val1");        
        properties.add(dccProperty);        		
		
		context.checking(new Expectations() {{
            one(mockCache).getAllPropertiesForAnApplication("qcLive",null);
            will(returnValue(properties));
		}});
		Properties resultingProperties = dccPropertyCache.getDccPropertiesForAnApplication("qcLive",null);
		assertEquals("val1",resultingProperties.get("param1"));
	}

    @Test
    public void addProperty(){

        final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyName("property");
        dccProperty.setPropertyValue("value");

        context.checking(new Expectations() {{
            one(mockCache).addOrUpdateProperty(dccProperty);
        }});
        dccPropertyCache.addOrUpdateProperty(dccProperty);

    }

    @Test
    public void updateProperty(){

        final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyName("property");
        dccProperty.setPropertyValue("value");

        context.checking(new Expectations() {{
            one(mockCache).addOrUpdateProperty(dccProperty);
        }});
        dccPropertyCache.addOrUpdateProperty(dccProperty);

    }

    @Test
    public void deleteProperty(){

        final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyName("property");
        dccProperty.setPropertyValue("value");

        context.checking(new Expectations() {{
            one(mockCache).deleteProperty(dccProperty);
        }});
        dccPropertyCache.deleteProperty(dccProperty);

    }

}
