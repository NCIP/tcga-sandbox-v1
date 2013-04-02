package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListLink;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.ArchiveListerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for listing archives (or directories containing archives) in a way similar to browsing a filesystem via Apache
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Controller
public class ArchiveListerController {

    public static final String PAGE_NAME = "list.htm";
    public static final String PARAM_NAME_ACCESS_LEVEL = "accessLevel";
    public static final String PARAM_NAME_DISEASE = "disease";
    public static final String PARAM_NAME_CENTER_TYPE = "centerType";
    public static final String PARAM_NAME_CENTER = "center";
    public static final String PARAM_NAME_PLATFORM = "platform";
    public static final String PARAM_NAME_ARCHIVE = "archive";
    private static final String PARAM_NAME_COLLECTION_NAME = "collection";

    private ArchiveListerService archiveListerService;

    /**
     * Handles a request for listing archives (or directories that lead to archives).  Only access level is required.
     *
     * If request is successful, the model will contain a ArchiveListInfo object stored with the key "archiveListInfo".
     * If request fails, the model will contain an error message string stored with the key "errorMessage".
     *
     * If access level only is provided, results will be links to the disease level.
     * If access level and disease are provided, results will be links to the centerType level
     * If access level, disease, and center type are provided, results will be links to the center level
     * If access level, disease, center type, and center are provided, results will be links to the platform level
     * If access level, disease, center type, center, and platform are provided, results will be archives
     * If all parameters are required, results will be archive files
     *
     * @param model the data model
     * @param accessLevel the access level for the request (required)
     * @param disease the disease abbreviation (optional)
     * @param centerType the center type (optional)
     * @param center the center (optional)
     * @param platform the platform (optional)
     * @param archiveName the archive name (optional)
     * @param collectionName the collection name (optional)
     * @return the view to display
     */
    @RequestMapping(value="/" + PAGE_NAME, method = RequestMethod.GET)
    public String handleRequest(final ExtendedModelMap model,
                                @RequestParam(value = "accessLevel", required = false) final String accessLevel,
                                @RequestParam(value = "disease", required = false) final String disease,
                                @RequestParam(value = "centerType", required = false) final String centerType,
                                @RequestParam(value = "center", required = false) final String center,
                                @RequestParam(value = "platform", required = false) final String platform,
                                @RequestParam(value = "archive", required = false) final String archiveName,
                                @RequestParam(value = "collection", required = false) final String collectionName) {

        try {
            final ArchiveListInfo archiveListInfo = getArchiveListInfo(accessLevel, disease, centerType, center, platform, archiveName, collectionName);
            model.addAttribute("archiveListInfo", archiveListInfo);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "archiveList";
    }

    public void setArchiveListerService(final ArchiveListerService archiveListerService) {
        this.archiveListerService = archiveListerService;
    }

    /**
     * Get the info needed for listing archives/paths to archives for the given parameters.
     * Once a null parameter is encountered, then only previous parameters will be considered -- if
     * a non-null parameter occurs later, it will be ignored.
     *
     * For example, if accessLevel and disease are specified, and centerType is null, but center is specified,
     * then the ArchiveListInfo returned will contain centerType links for the disease and accessLevel -- it will
     * not use the center parameter value.
     *
     * @param accessLevel must be either open or controller
     * @param disease     must be null or a valid disease abbreviation from the database
     * @param centerType  must be null or a valid center type code from the database
     * @param center      must be null or a valid center domain name from the database
     * @param platform    must be null of a valid platform name from the database
     * @param archiveName must be null or a valid archive name from the database
     * @param collectionName must be null of a valid file_collection name from the database
     * @return the ArchiveListInfo object for the parameters given
     * @throws IllegalArgumentException if any of the used parameters are invalid
     * @throws gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries.ArchiveListerException if there is an error with the parameters
     */
    private ArchiveListInfo getArchiveListInfo(final String accessLevel,
                                               final String disease,
                                               final String centerType,
                                               final String center,
                                               final String platform,
                                               final String archiveName,
                                               final String collectionName) throws ArchiveListerQueries.ArchiveListerException {


        final ArchiveListerQueries.LinkMaker linkMaker;
        final StringBuilder baseParams = new StringBuilder();
        String parentParams = null;

        if (accessLevel == null) {
            throw new ArchiveListerQueries.ArchiveListerException("accessLevel (open or controlled) must be given");
        } else {
            addParam(baseParams, PARAM_NAME_ACCESS_LEVEL, accessLevel);

            if (collectionName != null) {
                if (disease != null) {
                    addParam(baseParams, PARAM_NAME_DISEASE, disease);
                }
                if (centerType != null) {
                    addParam(baseParams, PARAM_NAME_CENTER_TYPE, centerType);
                }
                if (center != null) {
                    addParam(baseParams, PARAM_NAME_CENTER, center);
                }
                if (platform != null) {
                    addParam(baseParams, PARAM_NAME_PLATFORM, platform);
                }
                parentParams = baseParams.toString();
                addParam(baseParams, PARAM_NAME_COLLECTION_NAME, collectionName);
                linkMaker = makeLinkMaker(null, baseParams.toString(), parentParams);
            } else {

                if (disease == null) {
                    linkMaker = makeLinkMaker(PARAM_NAME_DISEASE, baseParams.toString(), parentParams);

                } else { // disease is specified
                    parentParams = baseParams.toString();
                    addParam(baseParams, PARAM_NAME_DISEASE, disease);

                    if (centerType == null) {
                        linkMaker = makeLinkMaker(PARAM_NAME_CENTER_TYPE, baseParams.toString(), parentParams);

                    } else { // centerType is specified
                        parentParams = baseParams.toString();
                        addParam(baseParams, PARAM_NAME_CENTER_TYPE, centerType);

                        if (center == null) {
                            linkMaker = makeLinkMaker(PARAM_NAME_CENTER, baseParams.toString(), parentParams);

                        } else { // center is specified
                            parentParams = baseParams.toString();
                            addParam(baseParams, PARAM_NAME_CENTER, center);

                            if (platform == null) {
                                linkMaker = makeLinkMaker(PARAM_NAME_PLATFORM, baseParams.toString(), parentParams);


                            } else { // platform is specified
                                parentParams = baseParams.toString();
                                addParam(baseParams, PARAM_NAME_PLATFORM, platform);

                                if (archiveName == null) {
                                    linkMaker = makeLinkMaker(PARAM_NAME_ARCHIVE, baseParams.toString(), parentParams);

                                } else { // archiveName is specified
                                    parentParams = baseParams.toString();
                                    addParam(baseParams, PARAM_NAME_ARCHIVE, archiveName);
                                    linkMaker = makeLinkMaker(null, baseParams.toString(), parentParams);
                                }
                            }
                        }
                    }
                }
            }
        }

        return archiveListerService.getArchiveListInfo(linkMaker, accessLevel, disease, centerType, center, platform, archiveName, collectionName);
    }

    /**
     * Make a link maker.  That is, make a class that knows how to make links that this controller can understand, given the parameter passed to it.
     * @param paramName the name of the parameter the link is for
     * @param baseParams other parameters that should be part of the url
     * @param parentUrl url for the parent page
     * @return a link maker object
     */
    protected ArchiveListerQueries.LinkMaker makeLinkMaker(final String paramName, final String baseParams, final String parentUrl) {
        final String[] paramPairs = baseParams.split("&");
        final StringBuilder currentDir = new StringBuilder();
        for (final String paramPair : paramPairs) {
            final String[] keyValue = paramPair.split("=");
            currentDir.append("/").append(keyValue[1]);
        }

        return new ArchiveListerQueries.LinkMaker() {
            public String makeLinkUrl(final String value, final boolean isCollection) {
                if (paramName == null) {
                    return value;
                } else {
                    return new StringBuilder().append(PAGE_NAME).append("?").append(baseParams).append("&").append(isCollection ? PARAM_NAME_COLLECTION_NAME : paramName).append("=").append(value).toString();
                }
            }

            public ArchiveListLink makeCurrentPage(final String currentPageValue) {
                final ArchiveListLink currentPage = new ArchiveListLink();
                currentPage.setUrl(new StringBuilder().append(PAGE_NAME).append("?").append(baseParams).toString());
                currentPage.setDisplayName(currentDir.toString());
                return currentPage;
            }

            public ArchiveListLink makeParentPage(final String parentPageValue) {
                if (parentUrl == null) {
                    return null;
                } else {
                    final ArchiveListLink parentPage = new ArchiveListLink();
                    parentPage.setUrl(new StringBuilder().append(PAGE_NAME).append("?").append(parentUrl).toString());
                    parentPage.setDisplayName(parentPageValue);
                    return parentPage;
                }
            }
        };
    }


    /**
     * Adds a parameter to the given string builder, in the form paramName=paramValue.  If param string has size > 0,
     * then "&" will be appended first.
     *
     * @param paramString the string to add to
     * @param paramName the parameter name
     * @param paramValue the parameter value
     */
    private void addParam(final StringBuilder paramString, final String paramName, final String paramValue) {
        if (paramString.length() > 0) {
            paramString.append("&");
        }
        paramString.append(paramName).append("=").append(paramValue);
    }
}
