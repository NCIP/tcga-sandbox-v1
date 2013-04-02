package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries;


/**
 * Service layer for listing archives.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ArchiveListerService {

    /**
     * Gets the ArchiveListInfo for the given parameters.  Uses the LinkMaker object passed in to contstruct the URLs needed
     * for the links on the page.  The first null parameter encountered indicates the level for the listing.  E.g. if
     * disease is the first null then listing is for diseases.  If platform is the first null then the listing is platforms.
     *
     * If archive name is specified, all files in that archive will be listed.
     * If collection name is specified, all files in that collection will be listed.
     *
     * @param linkMaker object used for creating URLs according to format of whoever is calling this
     * @param accessLevel the access level
     * @param disease the disease abbreviation, or null
     * @param centerType the center type code, or null
     * @param center the center domain name, or null
     * @param platform the platform name, or null
     * @param archiveName the archive name, or null
     * @param collectionName the collection name, or null
     * @return an object holding the archive listing for the parameters
     * @throws gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries.ArchiveListerException if parameters are invalid
     */
    public ArchiveListInfo getArchiveListInfo(ArchiveListerQueries.LinkMaker linkMaker, String accessLevel,
                                              String disease, String centerType, String center, String platform,
                                              String archiveName, String collectionName)
            throws ArchiveListerQueries.ArchiveListerException;

}
