/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.json;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDBrowserService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ANALYTE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BCR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.COLUMN;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.LIMIT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PLATFORM;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.RECEIVING_CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SAMPLE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SEARCH_PARAMS;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SORT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.START;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TISSUE_SOURCE_SITE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TOTAL_COUNT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_DATA;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_FILTER_DATA_URL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_JSON_URL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_PARENT_DATA;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_PARENT_JSON_URL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_TYPE_COMBO;

/**
 * Json Controller class for the uuid browser
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class UUIDBrowserJsonController {

    @Autowired
    private UUIDBrowserService service;
    @Autowired
    private UUIDCommonService commonService;

    @PostConstruct
    private void initAllUUIDBrowserCache() {
        service.getAllPlatforms();
        service.getAllTissueSourceSites();
        service.getAllUuidTypes();
        service.getUUIDBrowserComparator();
    }

    /**
     * handle uuid browser json search
     *
     * @param model
     * @param start
     * @param limit
     * @param sort
     * @param dir
     * @param searchParams
     * @return model
     */
    @RequestMapping(value = UUID_BROWSER_JSON_URL)
    public ModelMap uuidBrowserJsonHandler(
            final ModelMap model, final HttpSession session,
            @RequestParam(value = START) final int start,
            @RequestParam(value = LIMIT) final int limit,
            @RequestParam(value = SORT, required = false) final String sort,
            @RequestParam(value = DIR, required = false) final String dir,
            @RequestParam(value = SEARCH_PARAMS, required = false) final String searchParams) {

        List<BiospecimenMetaData> uuidBrowserSearchList = service.getAllBiospecimenMetadata();
        if (searchParams != null && searchParams.startsWith("{")) {
            if (searchParams.contains("upload complete")) {
                uuidBrowserSearchList = (List<BiospecimenMetaData>) session.getAttribute("uploadData");
            } else if (searchParams.contains(UUID_TYPE_COMBO)) {
                session.removeAttribute("uploadData");
                uuidBrowserSearchList = service.processSecondTab(searchParams, uuidBrowserSearchList);
            } else {
                session.removeAttribute("uploadData");
                uuidBrowserSearchList = service.processFirstTab(searchParams, uuidBrowserSearchList);
            }
        }
        final List<BiospecimenMetaData> sortedUUIDBrowserList = commonService.getSortedList(uuidBrowserSearchList,
                service.getUUIDBrowserComparator(), sort, dir);
        final List<BiospecimenMetaData> uuidBrowserData = commonService.getPaginatedList(sortedUUIDBrowserList, start,
                limit);
        final int totalCount = commonService.getTotalCount(sortedUUIDBrowserList);
        model.addAttribute(TOTAL_COUNT, totalCount);
        model.addAttribute(UUID_BROWSER_DATA, uuidBrowserData);
        return model;
    }

    /**
     * handle uuid parent relationship with no aliquots.
     *
     * @param model
     * @param uuid
     * @return model
     */
    @RequestMapping(value = UUID_PARENT_JSON_URL)
    public ModelMap uuidParentJsonHandler(
            final ModelMap model,
            @RequestParam(value = UUID) final String uuid) {
        model.addAttribute(UUID_PARENT_DATA, service.getAllBiospecimenMetadataParentsNoAliquot(uuid));
        return model;
    }

    /**
     * handle aliquot uuid parent relationship.
     *
     * @param model
     * @param uuid
     * @return model
     */
    @RequestMapping(value = "/aliquotUuid.json")
    public ModelMap aliquotUuidJsonHandler(
            final ModelMap model,
            @RequestParam(value = START) final int start,
            @RequestParam(value = LIMIT) final int limit,
            @RequestParam(value = UUID, required = false) final String uuid,
            @RequestParam(value = "parentUUID", required = false) final String parentUUID) {
        if (StringUtils.isNotBlank(uuid)) {
            List<BiospecimenMetaData> uuidList = (List<BiospecimenMetaData>)
                    CollectionUtils.select(service.getAllBiospecimenMetadata(), new Predicate() {
                        public boolean evaluate(Object o) {
                            return uuid.equalsIgnoreCase(((BiospecimenMetaData) o).getUuid());
                        }
                    });
            model.addAttribute(TOTAL_COUNT, uuidList.size());
            model.addAttribute("aliquotUuidData", uuidList);
        } else if (StringUtils.isNotBlank(parentUUID)) {
            List<BiospecimenMetaData> parentUuidList = (List<BiospecimenMetaData>)
                    CollectionUtils.select(service.getAllBiospecimenMetadata(), new Predicate() {
                        public boolean evaluate(Object o) {
                            return parentUUID.equalsIgnoreCase(((BiospecimenMetaData) o).getParentUUID());
                        }
                    });
            model.addAttribute(TOTAL_COUNT, parentUuidList.size());
            model.addAttribute("aliquotUuidData", commonService.getPaginatedList(
                    parentUuidList, start, limit));
        }
        return model;
    }

    /**
     * filter data handler
     *
     * @param model
     * @param filterName
     * @return model for json generation
     */
    @RequestMapping(value = UUID_BROWSER_FILTER_DATA_URL)
    public ModelMap filterDataHandler(final ModelMap model,
                                      @RequestParam(value = COLUMN, required = false) final String column,
                                      @RequestParam(value = FILTER) final String filterName) {

        if (DISEASE.equals(filterName)) {
            model.addAttribute("diseaseData", service.getAllDiseases(column));
        }
        if (RECEIVING_CENTER.equals(filterName)) {
            model.addAttribute("receivingCenterData", service.getAllCenters(column));
        }
        if (PLATFORM.equals(filterName)) {
            model.addAttribute("platformData", service.getAllPlatforms());
        }
        if (UUID_TYPE.equals(filterName)) {
            model.addAttribute("uuidTypeData", service.getAllUuidTypes());
        }
        if (SAMPLE_TYPE.equals(filterName)) {
            model.addAttribute("sampleTypeData", service.getAllSampleTypes(column));
        }
        if (ANALYTE_TYPE.equals(filterName)) {
            model.addAttribute("analyteTypeData", service.getAllPortionAnalytes(column));
        }
        if (BCR.equals(filterName)) {
            model.addAttribute("bcrData", service.getAllBCRs(column));
        }
        if (TISSUE_SOURCE_SITE.equals(filterName)) {
            model.addAttribute("tissueSourceSiteData", service.getAllTissueSourceSites());
        }
        return model;
    }

    /**
     * build webservice url from filter
     *
     * @param model
     * @param filter
     * @return model
     */
    @RequestMapping(value = "/buildWSUrl.json")
    public ModelMap buildWSUrl(final ModelMap model, HttpServletRequest request,
                               @RequestParam(value = FILTER) final String filter) {
        model.addAttribute("urlXml", commonService.buildServerURL(request) + service.buildWSUrl(request, filter, "xml"));
        model.addAttribute("urlJson", commonService.buildServerURL(request) + service.buildWSUrl(request, filter, "json"));
        return model;
    }

}//End of Class
