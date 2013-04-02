package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;

import java.util.List;
import java.util.Map;

/**
 * Class to return data from the uuid_hierarchy table that is needed by other apps than the uuid browser
 *
 * @author Shelley Alonso
 *         Last updated by: $Shelley Alonso$
 * @version $Rev$
 */

public interface UUIDHierarchyQueries {
    /*
    * get all uuid's for a family starting with either the parent uuid or parent barcode
    * depending on the value of parentFormat
    *
    * @param parent
    * @param parentFormat
    * @return uuidList
     */
    public List<UUIDDetail> getChildUUIDs(final String parent, final String parentFormat);
    
    /**
     * Persist UUID hierarchy object to database
     * @param uuidHierarchy data structure with values to persist 
     */
    public void persistUUIDHierarchy(List<BiospecimenMetaData> uuidHierarchy);

    /**
     * retrurns TCGA center id
     * @param receivingCenterId center id to lookup
     * @return tcga center id
     */
    public Long getTcgaCenterIdFromBcrId(final String receivingCenterId);
    
    /**
     * Get UUIDItemTypeId
     * @param uuidType
     * @return uuidTypeId
     */
    public Long getUUIDItemTypeId(final String uuidType);
    
    /**
     * Get a list groupinng platforms and uuid  
     * @return a list of uuid - platforms List maps
     */
    public Map<String,String> getPlatformsPerUUID();               
    
    /**
     * Updates all UUID hierarchy platforms
     * A convenience method that first calls getPlatformsPerUUID to get all uuid/platforms combination 
     * then for every UUID calls updateUUIDHierarchyPlatforms
     */
    public void updateAllUUIDHierarchyPlatforms ();
    
    /**
     * deletes all platforms in the table 
     */
    public void deletePlatforms();
    
    /**
     * removes duplicate platforms in the table
     */
    public void deduplicatePlatforms();

    /**
     * Gets meta data for the given UUIDS
     * @param uuids
     * @return
     */
    public Map<String,BiospecimenMetaData> getMetaData(final List<String> uuids);
}
