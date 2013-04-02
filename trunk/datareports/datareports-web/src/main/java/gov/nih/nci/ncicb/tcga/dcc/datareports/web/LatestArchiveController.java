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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.LatestArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.LatestGenericReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.serverAddress;

/**
 * Controller class for the latest archive,sdrf,maf web report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class LatestArchiveController {

    @Autowired
    private LatestGenericReportService service;

    @Autowired
    private DatareportsService commonService;

    /**
     * latest archive simple handler
     *
     * @param model
     * @param session
     * @param request
     * @return view name
     */
    @RequestMapping(value = LatestGenericReportConstants.LATEST_ARCHIVE_REPORT_URL, method = RequestMethod.GET,
            params = {"!" + LatestGenericReportConstants.ARCHIVE_TYPE, "!" + DatareportsCommonConstants.DATE_FROM,
                    "!" + DatareportsCommonConstants.DATE_TO})
    public String latestArchiveReportSimpleHandler(final ModelMap model, final HttpSession session,
                                                   final HttpServletRequest request) {
        final ModelMap archiModel = (ModelMap) session.getAttribute(LatestGenericReportConstants.LATEST_ARCHIVE_FILTER_MODEL);
        if (archiModel != null && archiModel.size() > 0) {
            model.addAllAttributes(archiModel);
        }
        final List<LatestArchive> latestArchiveList = service.getLatestArchive();
        commonService.processDisplayTag("latestArchive", latestArchiveList, model, request);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        session.removeAttribute(LatestGenericReportConstants.LATEST_ARCHIVE_FILTER_MODEL);
        return LatestGenericReportConstants.LATEST_ARCHIVE_REPORT_VIEW;
    }

    /**
     * latest archive full handler
     *
     * @param model
     * @param session
     * @param request
     * @param archiveType
     * @param dateTo
     * @param dateFrom
     * @return view Name
     */
    @RequestMapping(value = LatestGenericReportConstants.LATEST_ARCHIVE_REPORT_URL, method = RequestMethod.GET)
    public String latestArchiveReportFullHandler(
            final ModelMap model, final HttpSession session, final HttpServletRequest request,
            @RequestParam(value = LatestGenericReportConstants.ARCHIVE_TYPE, required = false) final String archiveType,
            @RequestParam(value = DatareportsCommonConstants.DATE_TO, required = false) final String dateTo,
            @RequestParam(value = DatareportsCommonConstants.DATE_FROM, required = false) final String dateFrom) {
        final List<LatestArchive> latestArchiveList = service.getLatestArchive();
        commonService.processDisplayTag("latestArchive", latestArchiveList, model, request);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        model.addAttribute(DatareportsCommonConstants.SHOW_FILTER_BOX, DatareportsCommonConstants.YES);
        model.addAttribute(LatestGenericReportConstants.ARCHIVE_TYPE, archiveType);
        model.addAttribute(DatareportsCommonConstants.DATE_TO, dateTo);
        model.addAttribute(DatareportsCommonConstants.DATE_FROM, dateFrom);
        session.setAttribute(LatestGenericReportConstants.LATEST_ARCHIVE_FILTER_MODEL, model);

        return LatestGenericReportConstants.LATEST_ARCHIVE_REPORT_VIEW;
    }

    /**
     * latest archive export handler
     *
     * @param model
     * @param exportType
     * @param sort
     * @param dir
     * @param columns
     * @param jsonFilterReq
     * @param jsonFormFilter
     * @return view name
     */
    @RequestMapping(value = LatestGenericReportConstants.LATEST_ARCHIVE_EXPORT_URL, method = RequestMethod.POST)
    public String latestArchiveExportHandler(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.EXPORT_TYPE) final String exportType,
            @RequestParam(value = DatareportsCommonConstants.SORT, required = false) final String sort,
            @RequestParam(value = DatareportsCommonConstants.DIR, required = false) final String dir,
            @RequestParam(value = DatareportsCommonConstants.COLS, required = false) final String columns,
            @RequestParam(value = DatareportsCommonConstants.FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam(value = DatareportsCommonConstants.FORM_FILTER, required = false) final String jsonFormFilter) {

        List<String> archiveTypeTab = null;
        String dateFrom = null, dateTo = null;
        List<LatestArchive> latestArchiveList = service.getLatestArchive();
        if (jsonFilterReq != null && !LatestGenericReportConstants.EMPTY_LATEST_ARCHIVE_FILTER.equals(jsonFilterReq)) {
            archiveTypeTab = commonService.processJsonMultipleFilter(LatestGenericReportConstants.ARCHIVE_TYPE, jsonFilterReq);
            dateFrom = commonService.processJsonSingleFilter(DatareportsCommonConstants.DATE_FROM, jsonFilterReq);
            dateTo = commonService.processJsonSingleFilter(DatareportsCommonConstants.DATE_TO, jsonFilterReq);
            latestArchiveList = service.getFilteredLatestArchiveList(service.getLatestArchive(),
                    archiveTypeTab, dateFrom, dateTo);
        }
        if (jsonFormFilter != null && !LatestGenericReportConstants.EMPTY_LATEST_ARCHIVE_FILTER.equals(jsonFormFilter)) {
            archiveTypeTab = commonService.processJsonMultipleFilter(LatestGenericReportConstants.ARCHIVE_TYPE, jsonFormFilter);
            dateFrom = commonService.processJsonSingleFilter(DatareportsCommonConstants.DATE_FROM, jsonFormFilter);
            dateTo = commonService.processJsonSingleFilter(DatareportsCommonConstants.DATE_TO, jsonFormFilter);
        }
        final List<LatestArchive> filteredLatestArchiveList = service.getFilteredLatestArchiveList(
                latestArchiveList, archiveTypeTab, dateFrom, dateTo);
        final List<LatestArchive> sortedLatestArchiveList = commonService.getSortedList(
                filteredLatestArchiveList,
                service.getLatestArchiveComparator(), sort, dir);
        final ViewAndExtensionForExport vae = commonService.getViewAndExtForExport(exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TYPE, exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TITLE, LatestGenericReportConstants.LATEST_ARCHIVE_REPORT_VIEW);
        model.addAttribute(DatareportsCommonConstants.EXPORT_FILENAME, LatestGenericReportConstants.LATEST_ARCHIVE_REPORT_VIEW + vae.getExtension());
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATE_FORMAT, DatareportsCommonConstants.DATE_TIME_FORMAT_US);
        model.addAttribute(DatareportsCommonConstants.COLS, commonService.buildReportColumns(LatestGenericReportConstants.LATEST_ARCHIVE_COLS, columns));
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATA, sortedLatestArchiveList);
        return vae.getView();
    }

}//End of Class
