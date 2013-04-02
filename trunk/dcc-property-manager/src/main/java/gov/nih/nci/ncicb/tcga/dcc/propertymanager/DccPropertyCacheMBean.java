/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.propertymanager;

import java.util.List;
import java.util.Map;

/**
 * JBoss MBEan Interface for querying DCC properties. The MBean is initialized by
 * the container before other artifacts ( servlets, ejbs etc..) and 
 * is a singleton. 
 * 
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 * */
public interface DccPropertyCacheMBean extends org.jboss.system.ServiceMBean
{
	/**
	 * Retrieves a property from the cache
	 * @param propertyName property key to retrieve
	 * @return property value, empty String ("") if the property 
	 * not found in the database
	 */
    public String getProperty(String propertyName);
    
    /**
	 * Retrieves all properties from the database
	 * @return dcc property list from the database 
	 */
    public  List<DccProperty> getAllProperties();   
    
    /**
	 * Retrieves all properties for a particular application and server name
	 * @param applicationName application for which to retrieve properties
	 * @param serverName server name for which to retrieve properties
	 * @return dcc property list from the database 
	 * for a particular application or server.
	 * if the server name is empty , return all properties for the server, 
	 * if the application name is empty , return all properties for the server
	 * if the application and server name are both empty , return all properties
	 */
    public List<DccProperty> getAllPropertiesForAnApplication(String applicationName,
    		String serverName);

    /**
     * Retrieves all properties from the database
     * @return dcc property list from the database
     */
    public List<DccProperty> getDccPropertyList();

    /**
     * Retrieves all properties from the database
     * @return dcc property list from the database
     */
    public List<DccProperty> getAllPropertiesForAnApplication(final String appName);
    /**
     * Add or update given property into cache/database
     * @param dccProperty
     */

    public void addOrUpdateProperty(final DccProperty dccProperty);

    /**
     * Removes the given property from cache/database
     * @param dccProperty
     */
    public void deleteProperty(final DccProperty dccProperty);
}
