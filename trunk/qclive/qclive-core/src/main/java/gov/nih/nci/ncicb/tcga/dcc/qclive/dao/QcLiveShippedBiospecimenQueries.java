package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;


import java.util.List;

/**
 * Interface for queries to shipped_biospecimen table for QCLive.  All methods in this have been merged into the common
 * ShippedBiospecimenQueries class.  Do not use or add to this!
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Deprecated
public interface QcLiveShippedBiospecimenQueries {

    /**
     * Gets the ID of the shipped biospecimen for the given UUID, if any.
     * @param uuid the UUID
     * @return ID or null if UUID not found
     */
    public Long getShippedBiospecimenIdForUUID(String uuid);

    /**
     * Adds a relationship between the given biospecimen and file, by ids.
     * @param biospecimenId the biospecimen id
     * @param fileId the file id
     */
    public void addFileRelationship(Long biospecimenId, Long fileId);
    
    
    /**
     * Returns True if the shipped biospecimen for the given UUID of shipped portion type exists, false otherwise 
     * @param uuid the UUID
     * @return ID or null if UUID not found
     */
    public Boolean isShippedBiospecimenShippedPortionUUIDValid(String uuid);
}
