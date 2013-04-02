package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimenElement;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface for shipped biospecimen queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ShippedBiospecimenQueries {


    /**
     * Adds to shipped_biospecimen table as well as shipped_element table.  Will use shipped biospecimen type
     * to look up type id -- will throw IllegalArgumentException if type is unknown.
     *
     * @param shippedBiospecimens biospecimens to add
     */
    public void addShippedBiospecimens(final List<ShippedBiospecimen> shippedBiospecimens);

    /**
     * Adds shippedbiospecimens into shippedbiospecimen table
     * @param shippedBiospecimens to add
     * @param shippedItemId item id, will look up if null
     */

    public void addShippedBiospecimens(final List<ShippedBiospecimen> shippedBiospecimens,Integer shippedItemId);
    /**
     * Adds shipped biospecimen elements into shipped biospecimen elements table
     * @param shippedBiospecimenElements elements to add
     */
    public void addShippedBiospecimenElements(final List<ShippedBiospecimenElement> shippedBiospecimenElements);

      /**
     * returns shipped elements type data
     * @return map which contains ids indexed by name
     */
    public Map<String,Integer> getShippedElementsType();

    /**
     * returns shipped item type id
     * @param shippedItemType name of item type
     * @return id for the given shipped item type, or null if not found
     */
    public Integer getShippedItemId(final String shippedItemType);


    /**
     * returns biospecimen id for the given uuid
     * @param UUID
     * @return
     */
    public Long getShippedBiospecimenId(final String UUID);

    /**
     * Get a list of Longs corresponding to UUIDs passed in.
     * @param uuids
     * @return
     */
    public List<Long> getShippedBiospecimenIds(List<String> uuids);

    /**
     * returns shipped biospecimen element id
     * @param shippedBiospecimenId
     * @param elementTypeId
     * @return
     */
    public Long getShippedBiospecimenElementId(final Long shippedBiospecimenId, final Integer elementTypeId);

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

    /**
     * Adds shipped biospecimen to file relationships for all IDs in the list, to the given file id
     * @param biospecimenIds list of shipped biospecimen ids
     * @param fileId file id
     */
    public void addFileRelationships(List<Long> biospecimenIds, Long fileId);
    
    /**
     * Returns a list of all redacted participants for a given participants list
     * @param participantCodeList a collection of participant codes for which check redacted
     * @return a list of redactions 
     */
    public List<String> getRedactedParticipants(Collection<String> participantCodeList);

    /**
     * Adds a relationship between a shipped biospecimen and an archive.  Will do nothing
     * if the relationship already exists.
     *
     * @param biospecimenId the shipped biospecimen id
     * @param archiveId the archive id
     */
    public void addArchiveRelationship(Long biospecimenId, Long archiveId);

    /**
     * Adds shipped biospecimen to archive relationships for all IDs in the list, to the given archive id
     * @param biospecimenIds list of shipped biospecimen ids
     * @param archiveId the archive id
     */
    public void addArchiveRelationships(List<Long> biospecimenIds, Long archiveId);

    /**
     * Retrieves UUID metadata from the database
     * @param UUID for which to return metadata
     */
    public MetaDataBean retrieveUUIDMetadata(String UUID) ;
    
    /**
     * Retrieves UUID level. Only work for shipped portions and 
     * @param uuid to get the level
     * @return UUID level
     */
    public String getUUIDLevel (final String uuid);
    
    /**
     * return the disease abbreviation corresponding to the input uuid
     *
     * @param uuid
     * @return disease abbreviation
     */
    public String getDiseaseForUUID(final String uuid);
}
