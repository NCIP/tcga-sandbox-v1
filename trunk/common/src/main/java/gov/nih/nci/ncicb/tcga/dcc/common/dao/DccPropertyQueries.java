package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccProperty;

import java.util.List;
import java.util.Properties;

/**
 * An interface for a utility class to wrap around calls to DCC property service service.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface DccPropertyQueries {

    /**
     * Will return property value for the given property name
     * @param propertyName
     * @return
     */
    public String getPropertyValue (final String propertyName, final String applicationName);

    /**
     *
     * @param propertyName
     * @param applicationName
     * @return
     */
    public DccProperty getDccProperty (final String propertyName, final String applicationName);

    /**
     * Will return all properties for the given app from the caching service
     * @return list of DCCProperty objects
     */
    public List<DccProperty> getDccPropertiesForAnApplication(final String applicationName);

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
