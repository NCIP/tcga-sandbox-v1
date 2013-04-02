/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 */
public interface CenterQueries {

    /**
     * Returns the center id for a given center name and center type code
     * @param centerName center name
     * @param centerType center type code
     * @return Center Id
     */
    public Integer findCenterId( String centerName, String centerType);

    @Deprecated
    /**
     * Returns a Collection of Map of column names Vs values for the center table
     * Reason for deprecating : this method should ideally return a List of Center beans, however this method has been used
     * in DAM at places.  A separate ticket has been added for changing them to use the correct method.
     */
    public Collection<Map<String, Object>> getAllCenters();

    /**
     * Returns list of all the centers
     * @return list of centers
     */    
    public List<Center> getCenterList();

    /**
     * Returns list of all the real centers with existing bcr center id
     *
     * @return list of centers
     */
    public List<Center> getRealCenterList();

    /**
     *
     * Returns the center for a given center Id
     * @param centerId center Id
     * @return Center for the given cenetr Id, null otherwise
     */    
    public Center getCenterById( Integer centerId );

    /**
     * Returns a center for a given name and center type code
     * @param centerName center name
     * @param centerTypeCode center type code
     * @return Returns a center for the specified name and center type code, null otherwise
     */    
    public Center getCenterByName( String centerName, String centerTypeCode);

    /**
     * Looks up center to BCR Center mapping table and returns the center ID for the given BCR Center ID
     * @param bcrCenterCode BCR Center code
     * @return Center ID
     */
    public Integer getCenterIdForBCRCenter(String bcrCenterCode);
        
    /**
     * Retrieves a list of converted to UUID centers
     * @return a list of converted to UUID centers
     */
    public List<Center> getConvertedToUUIDCenters();
    
    /**
     * True if a center is converted to UUID, false otherwise
     * @param center to check against converted centers
     */
    public boolean isCenterCenvertedToUUID(Center center);

    /**
     * True if the center is converted to UUID, false otherwise
     * @param centerName
     * @param centerTypeCode
     * @return True/False
     */
    public boolean isCenterConvertedToUUID(final String centerName, final String centerTypeCode);

    public boolean doesCenterRequireMageTab(final String centerName, final String centerTypeCode);
    
}
