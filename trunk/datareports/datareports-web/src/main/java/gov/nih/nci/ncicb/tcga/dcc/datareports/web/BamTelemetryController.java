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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamTelemetry;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.BamTelemetryReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * controller layer of the bam telemetry report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class BamTelemetryController {

    @Autowired
    private BamTelemetryReportService service;

    @Autowired
    private DatareportsService commonService;

    /**
     * BamTelemetry report simple handler
     *
     * @param model
     * @param session
     * @param request
     * @return view name
     */
    @RequestMapping(value = BamTelemetryReportConstants.BAM_TELEMETRY_REPORT_URL, method = RequestMethod.GET,
            params = {"!" + DatareportsCommonConstants.DISEASE, "!" + DatareportsCommonConstants.CENTER,
                    "!" + DatareportsCommonConstants.ALIQUOT_ID, "!" + DatareportsCommonConstants.ALIQUOT_UUID,
                    "!" + DatareportsCommonConstants.DATA_TYPE, "!" + BamTelemetryReportConstants.ANALYTE_CODE,
                    "!" + BamTelemetryReportConstants.LIBRARY_STRATEGY,
                    "!" + DatareportsCommonConstants.DATE_FROM, "!" + DatareportsCommonConstants.DATE_TO})
    public String bamTelemetryReportSimpleHandler(final ModelMap model, final HttpSession session,
                                                  final HttpServletRequest request) {
        final ModelMap bamModel = (ModelMap) session.getAttribute(BamTelemetryReportConstants.BAM_TELEMETRY_FILTER_MODEL);
        if (bamModel != null && bamModel.size() > 0) {
            model.addAllAttributes(bamModel);
        }
        final List<BamTelemetry> bamTelemetryList = service.getAllBamTelemetry();
        commonService.processDisplayTag("bamTelemetry", bamTelemetryList, model, request);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, DatareportsProperties.serverAddress);
        session.removeAttribute(BamTelemetryReportConstants.BAM_TELEMETRY_FILTER_MODEL);
        return BamTelemetryReportConstants.BAM_TELEMETRY_REPORT_VIEW;
    }

    /**
     * BamTelemetry report full handler
     *
     * @param model
     * @param session
     * @param request
     * @param disease
     * @param center
     * @param aliquotId
     * @param dataType
     * @param analyteCode
     * @param libraryStrategy
     * @param dateFrom
     * @param dateTo
     * @return view name
     */
    @RequestMapping(value = BamTelemetryReportConstants.BAM_TELEMETRY_REPORT_URL, method = RequestMethod.GET)
    public String bamTelemetryReportFullHandler(
            final ModelMap model, final HttpSession session, final HttpServletRequest request,
            @RequestParam(value = DatareportsCommonConstants.DISEASE, required = false) final String disease,
            @RequestParam(value = DatareportsCommonConstants.CENTER, required = false) final String center,
            @RequestParam(value = DatareportsCommonConstants.ALIQUOT_ID, required = false) final String aliquotId,
            @RequestParam(value = DatareportsCommonConstants.ALIQUOT_UUID, required = false) final String aliquotUUID,
            @RequestParam(value = DatareportsCommonConstants.DATA_TYPE, required = false) final String dataType,
            @RequestParam(value = BamTelemetryReportConstants.ANALYTE_CODE, required = false) final String analyteCode,
            @RequestParam(value = BamTelemetryReportConstants.LIBRARY_STRATEGY, required = false) final String libraryStrategy,
            @RequestParam(value = DatareportsCommonConstants.DATE_FROM, required = false) final String dateFrom,
            @RequestParam(value = DatareportsCommonConstants.DATE_TO, required = false) final String dateTo) {

        final List<BamTelemetry> bamTelemetryList = service.getAllBamTelemetry();
        commonService.processDisplayTag("bamTelemetry", bamTelemetryList, model, request);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, DatareportsProperties.serverAddress);
        model.addAttribute(DatareportsCommonConstants.SHOW_FILTER_BOX, DatareportsCommonConstants.YES);
        model.addAttribute(DatareportsCommonConstants.DISEASE, disease);
        model.addAttribute(DatareportsCommonConstants.CENTER, center);
        model.addAttribute(DatareportsCommonConstants.DATA_TYPE, dataType);
        model.addAttribute(DatareportsCommonConstants.ALIQUOT_ID, aliquotId);
        model.addAttribute(DatareportsCommonConstants.ALIQUOT_UUID, aliquotUUID);
        model.addAttribute(BamTelemetryReportConstants.ANALYTE_CODE, analyteCode);
        model.addAttribute(BamTelemetryReportConstants.LIBRARY_STRATEGY, libraryStrategy);
        model.addAttribute(DatareportsCommonConstants.DATE_FROM, dateFrom);
        model.addAttribute(DatareportsCommonConstants.DATE_TO, dateTo);
        session.setAttribute(BamTelemetryReportConstants.BAM_TELEMETRY_FILTER_MODEL, model);

        return BamTelemetryReportConstants.BAM_TELEMETRY_REPORT_VIEW;
    }

    /**
     * BamTelemetry export handler
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
    @RequestMapping(value = BamTelemetryReportConstants.BAM_TELEMETRY_EXPORT_URL, method = RequestMethod.POST)
    public String bamTelemetryExportHandler(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.EXPORT_TYPE) final String exportType,
            @RequestParam(value = DatareportsCommonConstants.SORT, required = false) final String sort,
            @RequestParam(value = DatareportsCommonConstants.DIR, required = false) final String dir,
            @RequestParam(value = DatareportsCommonConstants.COLS, required = false) final String columns,
            @RequestParam(value = DatareportsCommonConstants.FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam(value = DatareportsCommonConstants.FORM_FILTER, required = false) final String jsonFormFilter) {

        String aliquotId = null, dateFrom = null, dateTo = null, aliquotUUID = null;
        List<String> diseaseTab = null, centerTab = null, analyteCodeTab = null, dataTypeTab = null,
                libraryStrategyTab = null;
        List<BamTelemetry> bamTelemetryList = service.getAllBamTelemetry();
        if (jsonFilterReq != null && !BamTelemetryReportConstants.EMPTY_BAM_TELEMETRY_FILTER.equals(jsonFilterReq)) {
            aliquotId = commonService.processJsonSingleFilter(DatareportsCommonConstants.ALIQUOT_ID, jsonFilterReq);
            aliquotUUID = commonService.processJsonSingleFilter(DatareportsCommonConstants.ALIQUOT_UUID, jsonFilterReq);
            dateFrom = commonService.processJsonSingleFilter(DatareportsCommonConstants.DATE_FROM, jsonFilterReq);
            dateTo = commonService.processJsonSingleFilter(DatareportsCommonConstants.DATE_TO, jsonFilterReq);
            diseaseTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.DISEASE, jsonFilterReq);
            centerTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.CENTER, jsonFilterReq);
            dataTypeTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.DATA_TYPE, jsonFilterReq);
            analyteCodeTab = commonService.processJsonMultipleFilter(BamTelemetryReportConstants.ANALYTE_CODE, jsonFilterReq);
            libraryStrategyTab = commonService.processJsonMultipleFilter(BamTelemetryReportConstants.LIBRARY_STRATEGY, jsonFilterReq);
            bamTelemetryList = service.getFilteredBamTelemetryList(service.getAllBamTelemetry(), aliquotUUID, aliquotId, dateFrom,
                    dateTo, diseaseTab, centerTab, dataTypeTab, analyteCodeTab, libraryStrategyTab);
        }
        if (jsonFormFilter != null && !BamTelemetryReportConstants.EMPTY_BAM_TELEMETRY_FILTER.equals(jsonFormFilter)) {
            aliquotId = commonService.processJsonSingleFilter(DatareportsCommonConstants.ALIQUOT_ID, jsonFormFilter);
            aliquotUUID = commonService.processJsonSingleFilter(DatareportsCommonConstants.ALIQUOT_UUID, jsonFormFilter);
            dateFrom = commonService.processJsonSingleFilter(DatareportsCommonConstants.DATE_FROM, jsonFormFilter);
            dateTo = commonService.processJsonSingleFilter(DatareportsCommonConstants.DATE_TO, jsonFormFilter);
            diseaseTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.DISEASE, jsonFormFilter);
            centerTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.CENTER, jsonFormFilter);
            dataTypeTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.DATA_TYPE, jsonFormFilter);
            analyteCodeTab = commonService.processJsonMultipleFilter(BamTelemetryReportConstants.ANALYTE_CODE, jsonFormFilter);
            libraryStrategyTab = commonService.processJsonMultipleFilter(BamTelemetryReportConstants.LIBRARY_STRATEGY, jsonFormFilter);
        }
        final List<BamTelemetry> filteredBamTelemetryList = service.getFilteredBamTelemetryList(bamTelemetryList, aliquotUUID,
                aliquotId, dateFrom, dateTo, diseaseTab, centerTab, dataTypeTab, analyteCodeTab, libraryStrategyTab);
        final List<BamTelemetry> sortedBamTelemetryList = commonService.getSortedList(filteredBamTelemetryList,
                service.getBamTelemetryComparator(), sort, dir);
        final ViewAndExtensionForExport vae = commonService.getViewAndExtForExport(exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TYPE, exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TITLE, BamTelemetryReportConstants.BAM_TELEMETRY_REPORT_VIEW);
        model.addAttribute(DatareportsCommonConstants.EXPORT_FILENAME, BamTelemetryReportConstants.BAM_TELEMETRY_REPORT_VIEW + vae.getExtension());
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATE_FORMAT, DatareportsCommonConstants.DATE_FORMAT_US);
        model.addAttribute(DatareportsCommonConstants.COLS, commonService.buildReportColumns(BamTelemetryReportConstants.BAM_TELEMETRY_COLS, columns));
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATA, sortedBamTelemetryList);
        return vae.getView();
    }


}//End of class
