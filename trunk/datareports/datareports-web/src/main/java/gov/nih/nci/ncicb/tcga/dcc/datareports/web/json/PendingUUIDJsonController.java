/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.PendingUUIDReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BATCH;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BCR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER_REQ;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FORM_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.LIMIT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PLATE_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SORT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.START;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TOTAL_COUNT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.PendingUUIDReportConstants.EMPTY_PENDING_UUID_REPORT_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.PendingUUIDReportConstants.PENDING_UUID_REPORT_DATA;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.PendingUUIDReportConstants.PENDING_UUID_REPORT_FILTER_DATA_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.PendingUUIDReportConstants.PENDING_UUID_REPORT_JSON_URL;

/**
 * json controller layer of the pending uuid report
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class PendingUUIDJsonController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private PendingUUIDReportService service;

    @Autowired
    private DatareportsService commonService;

    @PostConstruct
    private void initAllPendingUUIDCache() {
        service.getPendingUUIDFilterDistinctValues(BCR);
        service.getPendingUUIDFilterDistinctValues(CENTER);
        service.getPendingUUIDComparator();
    }

    /**
     * filter data handler
     *
     * @param model      of request
     * @param filterName for filtering
     * @return model for json generation
     */
    @RequestMapping(value = PENDING_UUID_REPORT_FILTER_DATA_URL)
    public ModelMap filterDataHandler(
            final ModelMap model,
            @RequestParam(value = FILTER) final String filterName) {

        if (BCR.equals(filterName)) {
            model.addAttribute(BCR + "Data", service.getPendingUUIDFilterDistinctValues(BCR));
        }
        if (CENTER.equals(filterName)) {
            model.addAttribute(CENTER + "Data", service.getPendingUUIDFilterDistinctValues(CENTER));
        }
        return model;
    }

    /**
     * handle pending UUID data json
     *
     * @param model          of request
     * @param start          param for paging
     * @param limit          param for paging
     * @param sort           param for sorting
     * @param dir            param for sorting
     * @param jsonFilterReq  for url filtering
     * @param jsonFormFilter for web form filtering
     * @return model of request
     */
    @RequestMapping(value = PENDING_UUID_REPORT_JSON_URL, method = RequestMethod.POST)
    public ModelMap pendingUUIDReportJsonHandler(
            final ModelMap model,
            @RequestParam(value = START) final int start,
            @RequestParam(value = LIMIT) final int limit,
            @RequestParam(value = SORT, required = false) final String sort,
            @RequestParam(value = DIR, required = false) final String dir,
            @RequestParam(value = FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam(value = FORM_FILTER, required = false) final String jsonFormFilter) {

        List<String> bcrTab = null, centerTab = null;
        String batch = null, plateId = null;
        List<PendingUUID> pendingUUIDList = service.getAllPendingUUIDs();
        if (jsonFilterReq != null && !EMPTY_PENDING_UUID_REPORT_FILTER.equals(jsonFilterReq)) {
            bcrTab = commonService.processJsonMultipleFilter(BCR, jsonFilterReq);
            centerTab = commonService.processJsonMultipleFilter(CENTER, jsonFilterReq);
            batch = commonService.processJsonSingleFilter(BATCH, jsonFilterReq);
            plateId = commonService.processJsonSingleFilter(PLATE_ID, jsonFilterReq);
            pendingUUIDList = service.getFilteredPendingUUIDList(service.getAllPendingUUIDs(), bcrTab, centerTab,
                    batch, plateId);
        }
        if (jsonFormFilter != null && !EMPTY_PENDING_UUID_REPORT_FILTER.equals(jsonFormFilter)) {
            bcrTab = commonService.processJsonMultipleFilter(BCR, jsonFormFilter);
            centerTab = commonService.processJsonMultipleFilter(CENTER, jsonFormFilter);
            batch = commonService.processJsonSingleFilter(BATCH, jsonFormFilter);
            plateId = commonService.processJsonSingleFilter(PLATE_ID, jsonFormFilter);
        }
        final List<PendingUUID> filteredPendingUUIDList = service.getFilteredPendingUUIDList(pendingUUIDList, bcrTab,
                centerTab, batch, plateId);
        final List<PendingUUID> sortedPendingUUIDList = commonService.getSortedList(filteredPendingUUIDList,
                service.getPendingUUIDComparator(), sort, dir);
        final List<PendingUUID> pendingUUIDData = commonService.getPaginatedList(sortedPendingUUIDList, start, limit);
        final int totalCount = commonService.getTotalCount(filteredPendingUUIDList);
        model.addAttribute(TOTAL_COUNT, totalCount);
        model.addAttribute(PENDING_UUID_REPORT_DATA, pendingUUIDData);
        return model;
    }

}
