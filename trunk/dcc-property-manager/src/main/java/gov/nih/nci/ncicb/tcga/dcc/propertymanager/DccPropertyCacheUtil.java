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
import java.util.Properties;

import javax.management.MalformedObjectNameException;

/**
 * An interface for a utility class to wrap around calls to DCC property service service. 
 * 
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 * */
public interface DccPropertyCacheUtil {
	/**
	 * Will return a Properties object for a given application
	 * @param application name 
	 * @param server name
	 * @return an object with properties	 
	 */
	public Properties getDccPropertiesForAnApplication(String application,String serverName);
	
	/**
	 * Will return a all properties in the caching service
	 * @return an object with properties	 
	 */	
	public Properties getAllDccProperties();
	
	/**
	 * Will return a value for a property requested
	 * @param propertyName
	 * @return a property value
	 */
	public String getDccProperty(String propertyName);


    /**
     * Will return a all properties in the caching service
     * @return list of DCCProperty objects
     */
    public List<DccProperty> getDccPropertyList();

    /**
     * Will return all properties for the given app from the caching service
     * @return list of DCCProperty objects
     */
    public List<DccProperty> getAllPropertiesForAnApplication(final String appName);

    /**
     * Add or update the give property data into the cache/database
     * @param dccProperty
     */
    public void addOrUpdateProperty(final DccProperty dccProperty);

    /**
     * Removes the given property from cache/database
     * @param dccProperty
     */
    public void deleteProperty(final DccProperty dccProperty);


}
