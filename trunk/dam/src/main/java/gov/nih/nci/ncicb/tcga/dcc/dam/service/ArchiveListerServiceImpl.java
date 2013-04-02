package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListLink;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries;

import java.util.List;

/**
 * Implementation of ArchiveListerService.  This is the service layer that communicates with the DAO, given requests
 * from any kind of controller (or elsewhere).
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveListerServiceImpl implements ArchiveListerService {
    private ArchiveListerQueries archiveListerQueries;

    public void setArchiveListerQueries(final ArchiveListerQueries archiveListerQueries) {
        this.archiveListerQueries = archiveListerQueries;
    }

    /**
     * Gets the ArchiveListInfo for the given parameters.  Uses the LinkMaker object passed in to contstruct the URLs needed
     * for the links on the page.  The first null parameter encountered indicates the level for the listing.  E.g. if
     * disease is the first null then listing is for diseases.  If platform is the first null then the listing is platforms.
     *
     * @param linkMaker object used for creating URLs according to format of whoever is calling this
     * @param accessLevel the access level
     * @param disease the disease abbreviation, or null
     * @param centerType the center type code, or null
     * @param center the center domain name, or null
     * @param platform the platform name, or null
     * @param archiveName the archive name, or null
     * @return an object holding the archive listing for the parameters
     * @throws IllegalArgumentException if accessLevel is null
     */
    @Override
    public ArchiveListInfo getArchiveListInfo(final ArchiveListerQueries.LinkMaker linkMaker, final String accessLevel,
                                              final String disease, final String centerType, final String center,
                                              final String platform, final String archiveName, final String collectionName)
            throws ArchiveListerQueries.ArchiveListerException {

        final ArchiveListInfo archiveListInfo = new ArchiveListInfo();
        final List<ArchiveListLink> pageLinks;
        final String currentPageName;
        final String parentPageName;

        if (accessLevel == null) {
            throw new ArchiveListerQueries.ArchiveListerException("accessLevel (open or controlled) must be given");
        } else {

            if (collectionName != null) {
                pageLinks = archiveListerQueries.getCollectionFileLinks(collectionName, accessLevel, disease, centerType, center, platform);
                currentPageName = collectionName;
                if (disease == null) {
                    parentPageName = accessLevel;
                } else if (centerType == null) {
                    parentPageName = disease;
                } else if (center == null) {
                    parentPageName = centerType;
                } else if (platform == null) {
                    parentPageName = center;
                } else {
                    parentPageName = platform;
                }

            } else {

                if (disease == null) {
                    // return links to all diseases for access level
                    pageLinks = archiveListerQueries.getDiseaseLinks(linkMaker, accessLevel);
                    pageLinks.addAll(archiveListerQueries.getCollectionLinks(linkMaker, accessLevel, null, null, null, null));
                    currentPageName = accessLevel;
                    parentPageName = null;

                } else { // disease is specified
                    if (centerType == null) {
                        // return links to all centerTypes for disease and accessLevel
                        pageLinks = archiveListerQueries.getCenterTypeLinks(linkMaker, accessLevel, disease);
                        pageLinks.addAll(archiveListerQueries.getCollectionLinks(linkMaker, accessLevel, disease, null, null, null));
                        currentPageName = disease;
                        parentPageName = accessLevel;

                    } else { // centerType is specified
                        if (center == null) {
                            // return links to all centers for centerType, disease, and accessLevel
                            pageLinks = archiveListerQueries.getCenterLinks(linkMaker,
                                    accessLevel,
                                    disease,
                                    centerType);
                            pageLinks.addAll(archiveListerQueries.getCollectionLinks(linkMaker, accessLevel, disease, centerType, null, null));
                            currentPageName = centerType;
                            parentPageName = disease;

                        } else { // center is specified
                            if (platform == null) {
                                // return links to all platforms for center, centerType, disease, and accessLevel
                                pageLinks = archiveListerQueries.getPlatformLinks(
                                        linkMaker,
                                        accessLevel,
                                        disease,
                                        centerType,
                                        center);
                                pageLinks.addAll(archiveListerQueries.getCollectionLinks(linkMaker, accessLevel, disease, centerType, center, null));
                                currentPageName = center;
                                parentPageName = centerType;

                            } else { // platform is specified
                                if (archiveName == null) {
                                    // return links to all archives for platform, center, centerType, disease, accessLevel
                                    pageLinks = archiveListerQueries.getArchiveLinks(linkMaker,
                                            accessLevel,
                                            disease,
                                            centerType,
                                            center,
                                            platform);
                                    pageLinks.addAll(archiveListerQueries.getCollectionLinks(linkMaker, accessLevel, disease, centerType, center, platform));
                                    currentPageName = platform;
                                    parentPageName = center;

                                } else { // archiveName is specified

                                    // return links to archive files
                                    pageLinks =  archiveListerQueries.getArchiveFileLinks(archiveName);
                                    currentPageName = archiveName;
                                    parentPageName = platform;
                                }
                            }
                        }
                    }
                }
            }
        }

        archiveListInfo.setPageLinks(pageLinks);
        archiveListInfo.setCurrentPage(linkMaker.makeCurrentPage(currentPageName));
        archiveListInfo.setParentPage(linkMaker.makeParentPage(parentPageName));
        return archiveListInfo;
    }

}
