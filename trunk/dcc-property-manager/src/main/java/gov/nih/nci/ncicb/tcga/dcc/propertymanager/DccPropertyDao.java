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
 * An interface for DCC property service. The interface is used to provide
 * a contract for property queries in prperty cache.
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 * */
public interface DccPropertyDao {
	
	
	/**
     * Retrieves property value for a given propertyName    
     * @return property value , NOTE: if a property is not
     * found, an empty string will be returned.
     */
	public String getPropertyValue(String propertyName);
	
	/**
     * Retrieves all properties for a given application    
     * @return a list of DccProperties for a given application
     */
	public  List<DccProperty> getAllPropertiesForAnApplication(String applicationName);
	
	/**
     * Retrieves all properties for a given server    
     * @return list of DccProperties for a given server
     */
	public  List<DccProperty> getAllPropertiesForServer(String serverName);	
	
	/**
     * Retrieves all properties for a given application / server combination    
     * @return a list of DccProperties for a given application / server combination    
     */
	public  List<DccProperty> getAllPropertiesForApplicationAndServer(String applicationName , String serverName);

    /**
     * Retrieves all DCC Property values
     * @return list of DCC Property values
     */

    public List<DccProperty> getDccPropertyList();

    /**
     * Add or update the given Property object into cache/database
     * @param dccProperty
     */
    public void addOrUpdateProperty(final DccProperty dccProperty);

    /**
     * Removes property from cache/database
     * @param dccProperty
     */
    public void deleteProperty(final DccProperty dccProperty);

}