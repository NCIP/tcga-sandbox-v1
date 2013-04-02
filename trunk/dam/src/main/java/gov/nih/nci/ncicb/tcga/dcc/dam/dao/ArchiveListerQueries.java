package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListLink;

import java.util.List;

/**
 * Interface for database queries needed for archive listing functionality.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ArchiveListerQueries {

    /**
     * Gets ArchiveListLink objects, representing all diseases for which, for the
     * given access level, there are available archives.
     *
     * @param linkMaker the link maker for making URLs for each ArchiveListLink
     * @param accessLevel the access level
     * @return a list of ArchiveListLink objects
     * @throws ArchiveListerQueries.ArchiveListerException if parameters are invalid
     */
    public List<ArchiveListLink> getDiseaseLinks(LinkMaker linkMaker, String accessLevel) throws ArchiveListerException;

    /**
     * Gets ArchiveListLink objects, representing all center types for which, for the
     * given access level and disease, there are available archives.
     *
     * @param linkMaker the link maker for making URLs for each ArchiveListLink
     * @param accessLevel the access level
     * @param disease the disease abbreviation
     * @return a list of ArchiveListLink objects
     * @throws ArchiveListerQueries.ArchiveListerException if parameters are invalid
     */
    public List<ArchiveListLink> getCenterTypeLinks(LinkMaker linkMaker, String accessLevel, String disease) throws ArchiveListerException;

    /**
     * Gets ArchiveListLink objects, representing all centers for which, for the given
     * access level, disease, and center type, there are available archives.
     *
     * @param linkMaker the link maker for making URLs for each ArchiveListLink
     * @param accessLevel the access level
     * @param disease the disease abbreviation
     * @param centerType the center type code
     * @return a list of ArchiveListLink objects
     * @throws ArchiveListerQueries.ArchiveListerException if parameters are invalid
     */
    public List<ArchiveListLink> getCenterLinks(LinkMaker linkMaker, String accessLevel, String disease, String centerType) throws ArchiveListerException;

    /**
     * Gets ArchiveListLink objects, representing all platforms for which, for the given
     * access level, disease, center type, and center, there are available archives.
     *
     * @param linkMaker the link maker for making URLs for each ArchiveListLink
     * @param accessLevel the access level
     * @param disease the disease abbreviation
     * @param centerType the center type code
     * @param center the center domain name
     * @return a list of ArchiveListLink objects
     * @throws ArchiveListerQueries.ArchiveListerException if parameters are invalid
     */
    public List<ArchiveListLink> getPlatformLinks(LinkMaker linkMaker, String accessLevel, String disease, String centerType, String center) throws ArchiveListerException;

    /**
     * Gets ArchiveListLink objects, representing all available archives for the given parameters.
     *
     * @param linkMaker the link maker for making URLs for each ArchiveListLink
     * @param accessLevel the access level -- open or controlled
     * @param disease the disease abbreviation
     * @param centerType the center type code
     * @param center the center domain name
     * @param platform the platform name
     * @return a list of ArchiveListLink objects
     * @throws ArchiveListerQueries.ArchiveListerException if parameters are invalid
     */
    public List<ArchiveListLink> getArchiveLinks(LinkMaker linkMaker, String accessLevel, String disease, String centerType, String center, String platform) throws ArchiveListerException;

    /**
     * Gets ArchiveListLink objects, representing all files for the given archive.
     * @param archiveName the archive name
     * @return a list of ArchiveListLink objects
     * @throws ArchiveListerQueries.ArchiveListerException if parameters are invalid
     */
    public List<ArchiveListLink> getArchiveFileLinks(String archiveName) throws ArchiveListerException;

    /**
     * Gets links to collections that fit the given criteria.
     *
     * @param linkMaker used to make URLs
     * @param accessLevel open or controlled
     * @param diseaseAbbrev the disease abbreviation, or null
     * @param centerType the center type code, or null
     * @param center the center domain name, or null
     * @param platform the platform name, or null
     * @return list of links to collections for files fitting given parameters
     * @throws ArchiveListerException if parameters are invalid
     */
    public List<ArchiveListLink> getCollectionLinks(LinkMaker linkMaker, String accessLevel, String diseaseAbbrev, String centerType, String center, String platform) throws ArchiveListerException;

    /**
     * Gets links to all files that are part of the specified collection.  The collection is determined by the name as
     * well as the other parameters.
     *
     * @param collectionName the name of the collection to get files for
     * @param accessLevel open or controlled
     * @param disease disease abbreviation or null
     * @param centerType center type code or null
     * @param center center domain name or null
     * @param platform platform name or null
     * @return list of links to files in collection with given parameters
     * @throws ArchiveListerException if parameters are invalid
     */
    public List<ArchiveListLink> getCollectionFileLinks(String collectionName, String accessLevel, String disease, String centerType, String center, String platform) throws ArchiveListerException;

    /**
     * Interface for Link Maker
     */
    public interface LinkMaker {
        /**
         * Makes the URL for the given value
         * @param value the value
         * @param isCollection whether the link is for a collection or not
         * @return a URL that can be used to link to the desired listing
         */
        public String makeLinkUrl(String value, boolean isCollection);

        /**
         * Makes the ArchiveListLink representing the current page
         * @param currentPageValue the display name for the current page
         * @return ArchiveListLink
         */
        public ArchiveListLink makeCurrentPage(String currentPageValue);

        /**
         * Makes the ArchiveListLink representing the parent page
         * @param parentPageValue the display name for the parent page
         * @return ArchiveListLink
         */
        public ArchiveListLink makeParentPage(String parentPageValue);
    }

    /**
     * Checked exception for ArchiveLister errors.
     */
    public class ArchiveListerException extends Exception {
        /**
         * Create a new ArchiveListerException with the given error message.
         * @param message the error message
         */
       public ArchiveListerException(final String message) {
           super(message);
       }
    }
}
