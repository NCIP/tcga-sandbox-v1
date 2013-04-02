/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.PendingUUIDReportConstants;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BATCH;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BCR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.COLS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_FORMAT_US;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.EXPORT_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER_REQ;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FORM_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PLATE_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SERVER_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SHOW_FILTER_BOX;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SORT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.YES;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.serverAddress;

/**
 * Controller layer of the pending UUID report
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class PendingUUIDController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private PendingUUIDReportService service;

    @Autowired
    private DatareportsService commonService;

    /**
     * handler of the PendingUUID url without any request parameter
     *
     * @param model   of request
     * @param session of request
     * @param request
     * @return view name of request
     */
    @RequestMapping(value = PendingUUIDReportConstants.PENDING_UUID_REPORT_URL, method = RequestMethod.GET,
            params = {"!" + BCR, "!" + CENTER, "!" + BATCH, "!" + PLATE_ID})
    public String pendingUUIDReportHandler(final ModelMap model, final HttpSession session,
                                           final HttpServletRequest request) {
        final ModelMap pcModel = (ModelMap) session.getAttribute(PendingUUIDReportConstants.PENDING_UUID_REPORT_FILTER_MODEL);
        if (pcModel != null && pcModel.size() > 0) {
            model.addAllAttributes(pcModel);
        }
        final List<PendingUUID> pendingUUIDList = service.getAllPendingUUIDs();
        commonService.processDisplayTag("pendingUUID", pendingUUIDList, model, request);
        model.addAttribute(SERVER_URL, serverAddress);
        session.removeAttribute(PendingUUIDReportConstants.PENDING_UUID_REPORT_FILTER_MODEL);
        return PendingUUIDReportConstants.PENDING_UUID_REPORT_VIEW;
    }

    /**
     * handler of the Pending uuid url with request parameter
     *
     * @param model   of request
     * @param session of request
     * @param request
     * @param bcr     url parameter
     * @return forward view name of request
     */
    @RequestMapping(value = PendingUUIDReportConstants.PENDING_UUID_REPORT_URL, method = RequestMethod.GET)
    public String pendingUUIDReportFullHandler(
            final ModelMap model, final HttpSession session, final HttpServletRequest request,
            @RequestParam(value = BCR, required = false) final String bcr,
            @RequestParam(value = CENTER, required = false) final String center,
            @RequestParam(value = BATCH, required = false) final String batch,
            @RequestParam(value = PLATE_ID, required = false) final String plateId) {
        final List<PendingUUID> pendingUUIDList = service.getAllPendingUUIDs();
        commonService.processDisplayTag("pendingUUID", pendingUUIDList, model, request);
        model.addAttribute(SERVER_URL, serverAddress);
        model.addAttribute(SHOW_FILTER_BOX, YES);
        model.addAttribute(BCR, bcr);
        model.addAttribute(CENTER, center);
        model.addAttribute(BATCH, batch);
        model.addAttribute(PLATE_ID, plateId);
        session.setAttribute(PendingUUIDReportConstants.PENDING_UUID_REPORT_FILTER_MODEL, model);

        return PendingUUIDReportConstants.PENDING_UUID_REPORT_VIEW;
    }

    /**
     * handler of the export url for pending UUID
     *
     * @param model          of request
     * @param exportType     parameter for export
     * @param sort           parameter for export
     * @param dir            of sort parameter for export
     * @param columns        to export
     * @param jsonFilterReq  url filter
     * @param jsonFormFilter form filter
     * @return export view
     */
    @RequestMapping(value = PendingUUIDReportConstants.PENDING_UUID_REPORT_EXPORT_URL, method = RequestMethod.POST)
    public String pendingUUIDReportExportHandler(
            final ModelMap model,
            @RequestParam(value = EXPORT_TYPE) final String exportType,
            @RequestParam(value = SORT, required = false) final String sort,
            @RequestParam(value = DIR, required = false) final String dir,
            @RequestParam(value = COLS, required = false) final String columns,
            @RequestParam(value = FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam(value = FORM_FILTER, required = false) final String jsonFormFilter) {

        List<String> bcrTab = null, centerTab = null;
        String batch = null, plateId = null;
        List<PendingUUID> pendingUUIDList = service.getAllPendingUUIDs();
        if (jsonFilterReq != null && !PendingUUIDReportConstants.EMPTY_PENDING_UUID_REPORT_FILTER.equals(jsonFilterReq)) {
            bcrTab = commonService.processJsonMultipleFilter(BCR, jsonFilterReq);
            centerTab = commonService.processJsonMultipleFilter(CENTER, jsonFilterReq);
            batch = commonService.processJsonSingleFilter(BATCH, jsonFilterReq);
            plateId = commonService.processJsonSingleFilter(PLATE_ID, jsonFilterReq);
            pendingUUIDList = service.getFilteredPendingUUIDList(service.getAllPendingUUIDs(), bcrTab, centerTab,
                    batch, plateId);
        }
        if (jsonFormFilter != null && !PendingUUIDReportConstants.EMPTY_PENDING_UUID_REPORT_FILTER.equals(jsonFormFilter)) {
            bcrTab = commonService.processJsonMultipleFilter(BCR, jsonFormFilter);
            centerTab = commonService.processJsonMultipleFilter(CENTER, jsonFormFilter);
            batch = commonService.processJsonSingleFilter(BATCH, jsonFormFilter);
            plateId = commonService.processJsonSingleFilter(PLATE_ID, jsonFormFilter);
        }
        final List<PendingUUID> filteredPendingUUIDList = service.getFilteredPendingUUIDList(pendingUUIDList, bcrTab,
                centerTab, batch, plateId);
        final List<PendingUUID> sortedPendingUUIDList = commonService.getSortedList(filteredPendingUUIDList,
                service.getPendingUUIDComparator(), sort, dir);
        final ViewAndExtensionForExport vae = commonService.getViewAndExtForExport(exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TYPE, exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TITLE, PendingUUIDReportConstants.PENDING_UUID_REPORT_VIEW);
        model.addAttribute(DatareportsCommonConstants.EXPORT_FILENAME, PendingUUIDReportConstants.PENDING_UUID_REPORT_VIEW + vae.getExtension());
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATE_FORMAT, DATE_FORMAT_US);
        model.addAttribute(DatareportsCommonConstants.COLS, commonService.buildReportColumns(PendingUUIDReportConstants.PENDING_UUID_REPORT_COLS, columns));
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATA, sortedPendingUUIDList);
        return vae.getView();
    }
}
