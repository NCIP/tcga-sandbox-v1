package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccProperty;

import java.util.List;

/**
 * Service class interface for admin related requests.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface AdminService {


    /**
     * Returns list of property bean for the given app from the cache
     * @return list of DCCProperty bean
     */
    public List<DccProperty> getAllPropertiesForAnApplication(final String appName);

    /**
     * Add or update the give property data into cache/database
     * @param dccProperty
     */
    public void addOrUpdateProperty(final DccProperty dccProperty);

    /**
     * Removes the given property from cache/database
     * @param dccProperty
     */
    public void deleteProperty(final DccProperty dccProperty);

}
