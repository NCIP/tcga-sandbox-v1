/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.LatestArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.LatestGenericReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_FROM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_TO;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER_REQ;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FORM_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.LIMIT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SORT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.START;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TOTAL_COUNT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.ARCHIVE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.EMPTY_LATEST_ARCHIVE_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.LATEST_ARCHIVE_DATA;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.LATEST_ARCHIVE_FILTER_DATA_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.LATEST_ARCHIVE_REPORT_JSON_URL;

/**
 * Controller class that export json data for the latest archive report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@Controller
public class LatestArchiveJsonController {

    @Autowired
    private LatestGenericReportService service;

    @Autowired
    private DatareportsService commonService;

    @PostConstruct
    private void initAllLatestArchiveCache() {
        service.getLatestArchiveFilterDistinctValues(ARCHIVE_TYPE);
        service.getLatestArchiveComparator();
    }

    /**
     * latest Archive json handler
     * @param model
     * @param start
     * @param limit
     * @param sort
     * @param dir
     * @param jsonFilterReq
     * @param jsonFormFilter
     * @return model for json generation
     */
    @RequestMapping (value = LATEST_ARCHIVE_REPORT_JSON_URL, method = RequestMethod.POST)
    public ModelMap latestArchiveReportFullHandler(
            final ModelMap model,
            @RequestParam (value = START) final int start,
            @RequestParam (value = LIMIT) final int limit,
            @RequestParam (value = SORT, required = false) final String sort,
            @RequestParam (value = DIR, required = false) final String dir,
            @RequestParam (value = FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam (value = FORM_FILTER, required = false) final String jsonFormFilter) {

        List<String> archiveTypeTab = null;
        String dateFrom = null,dateTo = null;
        List<LatestArchive> latestArchiveList = service.getLatestArchive();
        if (jsonFilterReq != null && !EMPTY_LATEST_ARCHIVE_FILTER.equals(jsonFilterReq)) {
            archiveTypeTab = commonService.processJsonMultipleFilter(ARCHIVE_TYPE, jsonFilterReq);
            dateFrom = commonService.processJsonSingleFilter(DATE_FROM, jsonFilterReq);
            dateTo = commonService.processJsonSingleFilter(DATE_TO, jsonFilterReq);
            latestArchiveList = service.getFilteredLatestArchiveList(service.getLatestArchive(),
                    archiveTypeTab,dateFrom, dateTo);
        }
        if (jsonFormFilter != null && !EMPTY_LATEST_ARCHIVE_FILTER.equals(jsonFormFilter)) {
            archiveTypeTab = commonService.processJsonMultipleFilter(ARCHIVE_TYPE, jsonFormFilter);
            dateFrom = commonService.processJsonSingleFilter(DATE_FROM, jsonFormFilter);
            dateTo = commonService.processJsonSingleFilter(DATE_TO, jsonFormFilter);
        }
        final List<LatestArchive> filteredLatestArchiveList = service.getFilteredLatestArchiveList(
                latestArchiveList,archiveTypeTab, dateFrom, dateTo);
        final List<LatestArchive> sortedLatestArchiveList = commonService.getSortedList(
                filteredLatestArchiveList,
                service.getLatestArchiveComparator(), sort, dir);
        final List<LatestArchive> latestArchiveData = commonService.getPaginatedList(sortedLatestArchiveList,
                start, limit);
        final int totalCount = commonService.getTotalCount(filteredLatestArchiveList);

        model.addAttribute(TOTAL_COUNT, totalCount);
        model.addAttribute(LATEST_ARCHIVE_DATA, latestArchiveData);
        return model;
    }

    /**
     * filter data handler
     * @param model
     * @param filterName
     * @return model for json generation
     */
    @RequestMapping (value = LATEST_ARCHIVE_FILTER_DATA_URL)
    public ModelMap filterDataHandler(
            final ModelMap model,
            @RequestParam (value = FILTER) final String filterName) {

        if (ARCHIVE_TYPE.equals(filterName)) {
            model.addAttribute(ARCHIVE_TYPE+"Data", service.getLatestArchiveFilterDistinctValues(ARCHIVE_TYPE));
        }
        return model;
    }

}//End of Class
