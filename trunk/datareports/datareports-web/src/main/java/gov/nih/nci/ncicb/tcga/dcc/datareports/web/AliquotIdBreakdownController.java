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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotIdBreakdown;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotIdBreakdownReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.AliquotIdBreakdownReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import org.apache.commons.lang.StringUtils;
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
 * controller for the aliquot Id breakdown report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@Controller
public class AliquotIdBreakdownController {

    @Autowired
    private AliquotIdBreakdownReportService service;
    @Autowired
    private DatareportsService commonService;

    /**
     * Aliquot Id Breakdown report simple handler
     *
     * @param model
     * @param session
     * @param request
     * @return view name
     */
    @RequestMapping(value = AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_REPORT_URL, method = RequestMethod.GET,
            params = {"!" + DatareportsCommonConstants.ALIQUOT_ID, "!" + DatareportsCommonConstants.ANALYTE_ID, "!" + DatareportsCommonConstants.SAMPLE_ID, "!" + DatareportsCommonConstants.PARTICIPANT_ID})
    public String aliquotIdBreakdownReportSimpleHandler(
            final ModelMap model, final HttpSession session, final HttpServletRequest request) {
        final ModelMap bbModel = (ModelMap) session.getAttribute(AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_FILTER_MODEL);
        if (bbModel != null && bbModel.size() > 0) {
            model.addAllAttributes(bbModel);
        }
        final List<AliquotIdBreakdown> aliquotIdBreakdownList = service.getAliquotIdBreakdown();
        commonService.processDisplayTag("aliquotIdBreakdown", aliquotIdBreakdownList, model, request);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        session.removeAttribute(AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_FILTER_MODEL);
        return AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_REPORT_VIEW;
    }

    /**
     * Aliquot Id Breakdown report full handler
     *
     * @param model
     * @param session
     * @param request
     * @param aliquotId
     * @param analyteId
     * @param sampleId
     * @param participantId
     * @return view name
     */
    @RequestMapping(value = AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_REPORT_URL, method = RequestMethod.GET)
    public String aliquotIdBreakdownReportFullHandler(
            final ModelMap model, final HttpSession session, final HttpServletRequest request,
            @RequestParam(value = DatareportsCommonConstants.ALIQUOT_ID, required = false) final String aliquotId,
            @RequestParam(value = DatareportsCommonConstants.ANALYTE_ID, required = false) final String analyteId,
            @RequestParam(value = DatareportsCommonConstants.SAMPLE_ID, required = false) final String sampleId,
            @RequestParam(value = DatareportsCommonConstants.PARTICIPANT_ID, required = false) final String participantId) {

        final List<AliquotIdBreakdown> aliquotIdBreakdownList = service.getAliquotIdBreakdown();
        commonService.processDisplayTag("aliquotIdBreakdown", aliquotIdBreakdownList, model, request);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        model.addAttribute(DatareportsCommonConstants.SHOW_FILTER_BOX, DatareportsCommonConstants.YES);
        model.addAttribute(DatareportsCommonConstants.ALIQUOT_ID, aliquotId);
        model.addAttribute(DatareportsCommonConstants.ANALYTE_ID, analyteId);
        model.addAttribute(DatareportsCommonConstants.SAMPLE_ID, sampleId);
        model.addAttribute(DatareportsCommonConstants.PARTICIPANT_ID, participantId);
        session.setAttribute(AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_FILTER_MODEL, model);

        return AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_REPORT_VIEW;
    }

    /**
     * aliquotIdBreakdown export handler
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
    @RequestMapping(value = AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_EXPORT_URL, method = RequestMethod.POST)
    public String aliquotIdBreakdownExportHandler(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.EXPORT_TYPE) final String exportType,
            @RequestParam(value = DatareportsCommonConstants.SORT, required = false) final String sort,
            @RequestParam(value = DatareportsCommonConstants.DIR, required = false) final String dir,
            @RequestParam(value = DatareportsCommonConstants.COLS, required = false) final String columns,
            @RequestParam(value = DatareportsCommonConstants.FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam(value = DatareportsCommonConstants.FORM_FILTER, required = false) final String jsonFormFilter) {

        String aliquotId = null, analyteId = null, sampleId = null, participantId = null;
        List<AliquotIdBreakdown> aliquotIdBreakdownList = service.getAliquotIdBreakdown();
        if (jsonFilterReq != null && !AliquotIdBreakdownReportConstants.EMPTY_ALIQUOT_ID_BREAKDOWN_FILTER.equals(jsonFilterReq)) {
            aliquotId = commonService.processJsonSingleFilter(DatareportsCommonConstants.ALIQUOT_ID, jsonFilterReq);
            analyteId = commonService.processJsonSingleFilter(DatareportsCommonConstants.ANALYTE_ID, jsonFilterReq);
            sampleId = commonService.processJsonSingleFilter(DatareportsCommonConstants.SAMPLE_ID, jsonFilterReq);
            participantId = commonService.processJsonSingleFilter(DatareportsCommonConstants.PARTICIPANT_ID, jsonFilterReq);
            aliquotIdBreakdownList = service.getFilteredAliquotIdBreakdownList(
                    service.getAliquotIdBreakdown(),
                    aliquotId, analyteId, sampleId, participantId);
        }
        if (jsonFormFilter != null && !AliquotIdBreakdownReportConstants.EMPTY_ALIQUOT_ID_BREAKDOWN_FILTER.equals(jsonFormFilter)) {
            aliquotId = commonService.processJsonSingleFilter(DatareportsCommonConstants.ALIQUOT_ID, jsonFormFilter);
            analyteId = commonService.processJsonSingleFilter(DatareportsCommonConstants.ANALYTE_ID, jsonFormFilter);
            sampleId = commonService.processJsonSingleFilter(DatareportsCommonConstants.SAMPLE_ID, jsonFormFilter);
            participantId = commonService.processJsonSingleFilter(DatareportsCommonConstants.PARTICIPANT_ID, jsonFormFilter);
        }
        final List<AliquotIdBreakdown> filteredAliquotIdBreakdownList =
                service.getFilteredAliquotIdBreakdownList(aliquotIdBreakdownList,
                        aliquotId, analyteId, sampleId, participantId);
        final List<AliquotIdBreakdown> sortedAliquotIdBreakdownList =
                commonService.getSortedList(filteredAliquotIdBreakdownList,
                        service.getAliquotIdBreakdownComparator(), sort, dir);
        final ViewAndExtensionForExport vae = commonService.getViewAndExtForExport(exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TYPE, exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TITLE, AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_REPORT_VIEW);
        model.addAttribute(DatareportsCommonConstants.EXPORT_FILENAME, AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_REPORT_VIEW + vae.getExtension());
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATE_FORMAT, DatareportsCommonConstants.DATE_FORMAT_US);
        //add all the breakdown to every columns
        final String allColumns = StringUtils.isBlank(columns) ? "" : columns + ",project,tissueSourceSite,participant,sampleType,vialId,portionId," +
                "portionAnalyte,plateId,centerId";
        model.addAttribute(DatareportsCommonConstants.COLS, commonService.buildReportColumns(AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_COLS, allColumns));
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATA, sortedAliquotIdBreakdownList);
        return vae.getView();
    }

}//End of class
