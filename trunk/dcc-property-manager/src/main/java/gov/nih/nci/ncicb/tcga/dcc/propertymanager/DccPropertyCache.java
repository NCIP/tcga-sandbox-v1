/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.propertymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * JBoss MBEan for querying DCC properties. The MBean is initialized by the
 * container before other artifacts ( servlets, ejbs etc..) and is a singleton.
 * 
 * @author Stan Girshik Last updated by: $Author$
 * @version $Rev$
 * */
public class DccPropertyCache extends org.jboss.system.ServiceMBeanSupport
		implements DccPropertyCacheMBean {

	/** Logger */
	private static final Logger logger = Logger
			.getLogger(DccPropertyCache.class);

	// spring application context
	protected ApplicationContext context = null;

	/**
	 * Retrieves a property from the cache
	 * 
	 * @param propertyName
	 *            property key to retrieve
	 * @return property value, emptry String ("") if the property not found in
	 *         the database
	 */
	public String getProperty(String propertyName) {
		String propertyToReturn = "";
		if (StringUtils.isNotEmpty(propertyName)) {
			propertyToReturn = getDccPropertyDao().getPropertyValue(propertyName);
		}
		return propertyToReturn;
	}

	/**
	 * Retrieves all properties from the database
	 * 
	 * @return key-value pairs from the databse
	 */
	public  List<DccProperty> getAllProperties() {
		return getDccPropertyDao().getDccPropertyList();
	}

    public List<DccProperty> getDccPropertyList(){
        return getDccPropertyDao().getDccPropertyList();
    }

    @Override
    public List<DccProperty> getAllPropertiesForAnApplication(final String appName){
        return getDccPropertyDao().getAllPropertiesForAnApplication(appName);
    }

    @Override
    public void addOrUpdateProperty(final DccProperty dccProperty){
        getDccPropertyDao().addOrUpdateProperty(dccProperty);
    }

    @Override
    public void deleteProperty(final DccProperty dccProperty){
        getDccPropertyDao().deleteProperty(dccProperty);
    }

	/**
	 * Retrieves all properties for a particular application / server combination
	 * 
	 * @return key-value pairs from the database
	 */
	public List<DccProperty> getAllPropertiesForAnApplication(String applicationName,String serverName) {
		List<DccProperty> returnProperties = new ArrayList<DccProperty>();
		
		if (StringUtils.isNotEmpty(applicationName) && StringUtils.isNotEmpty(serverName)){
			returnProperties = getDccPropertyDao().getAllPropertiesForApplicationAndServer(applicationName, serverName);
		}else if (StringUtils.isNotEmpty(applicationName) && StringUtils.isEmpty(serverName)){
			returnProperties = getDccPropertyDao().getAllPropertiesForAnApplication(applicationName);
		}else if (StringUtils.isNotEmpty(serverName) && StringUtils.isEmpty(applicationName)){
			returnProperties = getDccPropertyDao().getAllPropertiesForServer(serverName);
		}else if (StringUtils.isEmpty(serverName) && StringUtils.isEmpty(applicationName)){
			returnProperties = getAllProperties();
		}
		
		return returnProperties;
	}


	@Override
	public void createService() throws Exception {
		logger.info("Creating Dcc Property Manager");
		// initialize Spring
		context = new ClassPathXmlApplicationContext("app-context.xml");
	}
	
	@Override
	public void destroyService() throws Exception {
		// destroying Dcc Property Manager
		logger.info("destroying Dcc Property Manager");
		context = null;
	}
	
	@Override
	public void startService() throws Exception {
		logger.info("starting Dcc Property Manager application");
	}
	
	@Override
	public void stopService() {
		logger.info("stopping Dcc Property Manager application");
	}

    private DccPropertyDao getDccPropertyDao(){
        return (DccPropertyDao) context.getBean("dccPropertyDao");
    }
}
