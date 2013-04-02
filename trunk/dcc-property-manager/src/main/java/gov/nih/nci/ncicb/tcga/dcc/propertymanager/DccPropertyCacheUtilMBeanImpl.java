/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.propertymanager;

import gov.nih.nci.ncicb.tcga.dcc.propertymanager.DccPropertyCacheMBean;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanServerLocator;

/**
 * A utility class to wrap around calls to DCC property service service.
 * The methods will do a MBean lookup in JBoss and invoke an appropriate service. 
 * 
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 * */
public class DccPropertyCacheUtilMBeanImpl implements DccPropertyCacheUtil{
		
	/** Logger */
	private static final Logger logger = Logger
			.getLogger(DccPropertyCacheUtilMBeanImpl.class);
	
	// mbean proxy reference
	protected DccPropertyCacheMBean mbeanProxyReference;
	
	private DccPropertyCacheUtilMBeanImpl(){
		// singleton , no initialization
	}
	
	private static final DccPropertyCacheUtilMBeanImpl instance = new DccPropertyCacheUtilMBeanImpl();
	     

    public static DccPropertyCacheUtilMBeanImpl getInstance() {    		    
            return instance;
    }

	protected void init() {
		try{
			// Get a type-safe dynamic proxy
			 mbeanProxyReference =
			      (DccPropertyCacheMBean)MBeanServerInvocationHandler.newProxyInstance(
			    		  	MBeanServerLocator.locateJBoss(),
			    		  	new ObjectName("gov.nih.nci.ncicb.tcga.dcc:service=propertymanager"),
			                DccPropertyCacheMBean.class,
			                false);
			}catch 	(MalformedObjectNameException e){
				// can't really recover from this , so log it and throw a runtime exception
				String errorMessage = " Unable to connect to Dcc property MBean service";
				logger.error(errorMessage,e);
				throw new IllegalStateException (errorMessage,e);
			}
	}
			
	
	@Override
	public Properties getDccPropertiesForAnApplication(String applicationName,String serverName){
		Properties properties = new Properties();		
		List<DccProperty> propertyList = getDccPropertyCacheMBean().getAllPropertiesForAnApplication(applicationName,serverName);		
		if (propertyList!= null && propertyList.size() > 0){												
			for (DccProperty property: propertyList ){
				if (StringUtils.isNotEmpty(property.getPropertyValue())){
					properties.setProperty(property.getPropertyName(),property.getPropertyValue());
				}else{
					properties.setProperty(property.getPropertyName(),"");
				}	
			}			
		}						
		return properties; 					
	}

	@Override
	public Properties getAllDccProperties() {		
		Properties properties = new Properties();		 
		List<DccProperty> propertyList = getDccPropertyCacheMBean().getAllProperties();			
		if (propertyList!= null && propertyList.size() > 0){												
			for (DccProperty property: propertyList ){
				if (StringUtils.isNotEmpty(property.getPropertyValue())){
					properties.setProperty(property.getPropertyName(),property.getPropertyValue());
				}else{
					properties.setProperty(property.getPropertyName(),"");
				}
			}			
		}						
		return properties; 		
	}

	@Override
	public String getDccProperty(String propertyName) {
		
		return getDccPropertyCacheMBean().getProperty(propertyName);
	}

    @Override
    public List<DccProperty> getDccPropertyList(){
        return getDccPropertyCacheMBean().getDccPropertyList();
    }

    @Override
    public List<DccProperty> getAllPropertiesForAnApplication(final String appName){
        return getDccPropertyCacheMBean().getAllPropertiesForAnApplication(appName);
    }


    @Override
    public void addOrUpdateProperty(final DccProperty dccProperty){
        getDccPropertyCacheMBean().addOrUpdateProperty(dccProperty);
    }

    @Override
    public void deleteProperty(final DccProperty dccProperty){
        getDccPropertyCacheMBean().deleteProperty(dccProperty);
    }

    private DccPropertyCacheMBean getDccPropertyCacheMBean(){
        if (mbeanProxyReference == null){
            init();
        }
        return mbeanProxyReference;
    }

}
