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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sample;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.SampleSummaryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.COLS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PLATFORM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.serverAddress;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.PORTION_ANALYTE;

/**
 * Class that create the sample summary controller for the web layer
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class SampleSummaryController {

    @Autowired
    private SampleSummaryReportService service;
    @Autowired
    private DatareportsService commonService;

    /**
     * sample Summary simple handler
     *
     * @param model
     * @param session
     * @param centerEmail
     * @return view name
     */
    @RequestMapping(value = SampleSummaryReportConstants.SAMPLE_SUMMARY_REPORT_URL, method = RequestMethod.GET,
            params = {"!" + DatareportsCommonConstants.DISEASE, "!" + DatareportsCommonConstants.CENTER,
                    "!" + SampleSummaryReportConstants.PORTION_ANALYTE,
                    "!" + SampleSummaryReportConstants.LEVEL4_SS,
                    "!" + DatareportsCommonConstants.PLATFORM})
    public String sampleSummarySimpleHandler(final ModelMap model, final HttpSession session, final HttpServletRequest request,
                                             @RequestParam(value = SampleSummaryReportConstants.CENTER_EMAIL, required = false) final String centerEmail) {

        final List<SampleSummary> sampleSummaryList = service.getSampleSummaryReport();
        model.addAttribute(SampleSummaryReportConstants.LAST_REFRESH, service.getLatest(sampleSummaryList));
        model.addAttribute(SampleSummaryReportConstants.CENTER_EMAIL, centerEmail);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        commonService.processDisplayTag("sampleSummary", sampleSummaryList, model, request);

        final ModelMap ssModel = (ModelMap) session.getAttribute(SampleSummaryReportConstants.SAMPLE_SUMMARY_FILTER_MODEL);
        if (ssModel != null && ssModel.size() > 0) {
            model.addAllAttributes(ssModel);
        }
        session.removeAttribute(SampleSummaryReportConstants.SAMPLE_SUMMARY_FILTER_MODEL);
        return SampleSummaryReportConstants.SAMPLE_SUMMARY_REPORT_VIEW;
    }

    /**
     * sample summary full handler
     *
     * @param model
     * @param session
     * @param disease
     * @param portionAnalyte
     * @param platform
     * @param level4
     * @param centerEmail
     * @param center
     * @return view name
     */
    @RequestMapping(value = SampleSummaryReportConstants.SAMPLE_SUMMARY_REPORT_URL, method = RequestMethod.GET)
    public String sampleSummaryFullHandler(
            final ModelMap model, final HttpSession session, final HttpServletRequest request,
            @RequestParam(value = DatareportsCommonConstants.DISEASE, required = false) final String disease,
            @RequestParam(value = SampleSummaryReportConstants.PORTION_ANALYTE, required = false) final String portionAnalyte,
            @RequestParam(value = DatareportsCommonConstants.PLATFORM, required = false) final String platform,
            @RequestParam(value = SampleSummaryReportConstants.LEVEL4_SS, required = false) final String level4,
            @RequestParam(value = SampleSummaryReportConstants.CENTER_EMAIL, required = false) final String centerEmail,
            @RequestParam(value = DatareportsCommonConstants.CENTER, required = false) final String center) {

        final List<SampleSummary> sampleSummaryList = service.getSampleSummaryReport();
        model.addAttribute(SampleSummaryReportConstants.LAST_REFRESH, service.getLatest(sampleSummaryList));
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        model.addAttribute(DatareportsCommonConstants.SHOW_FILTER_BOX, DatareportsCommonConstants.YES);
        model.addAttribute(DatareportsCommonConstants.DISEASE, disease);
        model.addAttribute(SampleSummaryReportConstants.PORTION_ANALYTE, portionAnalyte);
        model.addAttribute(DatareportsCommonConstants.PLATFORM, platform);
        model.addAttribute(SampleSummaryReportConstants.LEVEL4_SS, level4);
        model.addAttribute(DatareportsCommonConstants.CENTER, center);
        model.addAttribute(SampleSummaryReportConstants.CENTER_EMAIL, centerEmail);
        commonService.processDisplayTag("sampleSummary", sampleSummaryList, model, request);
        session.setAttribute(SampleSummaryReportConstants.SAMPLE_SUMMARY_FILTER_MODEL, model);
        return SampleSummaryReportConstants.SAMPLE_SUMMARY_REPORT_VIEW;
    }

    /**
     * sample summary handler for requests coming from email
     *
     * @param model
     * @param session
     * @param centerEmail
     * @param disease
     * @param center
     * @param portionAnalyte
     * @param platform
     * @param colId
     * @return view name
     */
    @RequestMapping(value = SampleSummaryReportConstants.SAMPLE_SUMMARY_REPORT_URL, method = RequestMethod.GET,
            params = {SampleSummaryReportConstants.CENTER_EMAIL, DatareportsCommonConstants.DISEASE, DatareportsCommonConstants.CENTER, SampleSummaryReportConstants.PORTION_ANALYTE, DatareportsCommonConstants.PLATFORM, DatareportsCommonConstants.COLS})
    public String sampleSummaryComingFromEmailHandler(
            final ModelMap model, final HttpSession session,
            @RequestParam(value = SampleSummaryReportConstants.CENTER_EMAIL) final String centerEmail,
            @RequestParam(value = DatareportsCommonConstants.DISEASE) final String disease,
            @RequestParam(value = DatareportsCommonConstants.CENTER) final String center,
            @RequestParam(value = SampleSummaryReportConstants.PORTION_ANALYTE) final String portionAnalyte,
            @RequestParam(value = DatareportsCommonConstants.PLATFORM) final String platform,
            @RequestParam(value = DatareportsCommonConstants.BCR) final boolean bcr,
            @RequestParam(value = DatareportsCommonConstants.COLS) final String colId) {

        model.addAttribute(SampleSummaryReportConstants.LAST_REFRESH, service.getLatest(service.getSampleSummaryReport()));
        model.addAttribute(SampleSummaryReportConstants.CENTER_EMAIL, centerEmail);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        model.addAttribute(DatareportsCommonConstants.DISEASE + "Email", disease);
        model.addAttribute(DatareportsCommonConstants.CENTER + "Email", center);
        model.addAttribute(SampleSummaryReportConstants.PORTION_ANALYTE + "Email", portionAnalyte);
        model.addAttribute(DatareportsCommonConstants.PLATFORM + "Email", platform);
        model.addAttribute(DatareportsCommonConstants.BCR + "Email", bcr);
        model.addAttribute(DatareportsCommonConstants.COLS, colId);
        model.addAttribute("colName", SampleSummaryReportConstants.SAMPLE_SUMMARY_COLS.get(colId));
        model.addAttribute(DatareportsCommonConstants.MODE, "comingFromEmail");
        session.setAttribute(SampleSummaryReportConstants.SAMPLE_SUMMARY_FILTER_MODEL, model);
        return "forward:" + SampleSummaryReportConstants.SAMPLE_SUMMARY_REPORT_VIEW + "Tmp.htm";
    }

    /**
     * sample summary export handler
     *
     * @param model
     * @param exportType
     * @param sort
     * @param dir
     * @param columns
     * @param centerFromEmail
     * @param jsonFilterReq
     * @param jsonFormFilter
     * @return view name
     */
    @RequestMapping(value = SampleSummaryReportConstants.SAMPLE_SUMMARY_EXPORT_URL, method = RequestMethod.POST)
    public String sampleSummaryExportHandler(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.EXPORT_TYPE) final String exportType,
            @RequestParam(value = DatareportsCommonConstants.SORT, required = false) final String sort,
            @RequestParam(value = DatareportsCommonConstants.DIR, required = false) final String dir,
            @RequestParam(value = DatareportsCommonConstants.COLS, required = false) final String columns,
            @RequestParam(value = SampleSummaryReportConstants.CENTER_EMAIL, required = false) final String centerFromEmail,
            @RequestParam(value = DatareportsCommonConstants.FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam(value = DatareportsCommonConstants.FORM_FILTER, required = false) final String jsonFormFilter) {

        List<String> diseaseTab = null, centerTab = null, platformTab = null, portionAnalyteTab = null,
                levelFourTab = null;
        List<SampleSummary> sampleSummaryList = service.processSampleSummary(centerFromEmail);
        if (jsonFilterReq != null && !SampleSummaryReportConstants.EMPTY_SAMPLE_SUMMARY_FILTER.equals(jsonFilterReq)) {
            diseaseTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.DISEASE, jsonFilterReq);
            centerTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.CENTER, jsonFilterReq);
            portionAnalyteTab = commonService.processJsonMultipleFilter(SampleSummaryReportConstants.PORTION_ANALYTE, jsonFilterReq);
            platformTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.PLATFORM, jsonFilterReq);
            levelFourTab = commonService.processJsonMultipleFilter(SampleSummaryReportConstants.LEVEL4_SS, jsonFilterReq);
            sampleSummaryList = service.getFilteredSampleSummaryList(
                    service.processSampleSummary(centerFromEmail),
                    diseaseTab, centerTab, portionAnalyteTab, platformTab, levelFourTab);
        }
        if (jsonFormFilter != null && !SampleSummaryReportConstants.EMPTY_SAMPLE_SUMMARY_FILTER.equals(jsonFormFilter)) {
            diseaseTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.DISEASE, jsonFormFilter);
            centerTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.CENTER, jsonFormFilter);
            portionAnalyteTab = commonService.processJsonMultipleFilter(SampleSummaryReportConstants.PORTION_ANALYTE, jsonFormFilter);
            platformTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.PLATFORM, jsonFormFilter);
            levelFourTab = commonService.processJsonMultipleFilter(SampleSummaryReportConstants.LEVEL4_SS, jsonFormFilter);
        }
        final List<SampleSummary> filteredSampleSummaryList = service.getFilteredSampleSummaryList(sampleSummaryList,
                diseaseTab, centerTab, portionAnalyteTab, platformTab, levelFourTab);
        final List<SampleSummary> sortedSampleSummaryList = commonService.getSortedList(filteredSampleSummaryList,
                service.getSampleSummaryComparator(), sort, dir);
        final ViewAndExtensionForExport vae = commonService.getViewAndExtForExport(exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TYPE, exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TITLE, SampleSummaryReportConstants.SAMPLE_SUMMARY_REPORT_VIEW);
        model.addAttribute(DatareportsCommonConstants.EXPORT_FILENAME, SampleSummaryReportConstants.SAMPLE_SUMMARY_REPORT_VIEW + vae.getExtension());
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATE_FORMAT, DatareportsCommonConstants.DATE_FORMAT_US);
        model.addAttribute(DatareportsCommonConstants.COLS, commonService.buildReportColumns(SampleSummaryReportConstants.SAMPLE_SUMMARY_COLS, columns));
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATA, sortedSampleSummaryList);
        return vae.getView();
    }

    /**
     * sample Export handler
     *
     * @param model
     * @param exportType
     * @param sort
     * @param dir
     * @param disease
     * @param center
     * @param analyte
     * @param platform
     * @param colId
     * @return view name
     */
    @RequestMapping(value = SampleSummaryReportConstants.SAMPLE_DETAILED_EXPORT_URL, method = RequestMethod.POST)
    public String sampleDetailedExportHandler(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.EXPORT_TYPE) final String exportType,
            @RequestParam(value = DatareportsCommonConstants.SORT, required = false) final String sort,
            @RequestParam(value = DatareportsCommonConstants.DIR, required = false) final String dir,
            @RequestParam(value = DatareportsCommonConstants.DISEASE) final String disease,
            @RequestParam(value = DatareportsCommonConstants.CENTER) final String center,
            @RequestParam(value = SampleSummaryReportConstants.PORTION_ANALYTE) final String analyte,
            @RequestParam(value = DatareportsCommonConstants.PLATFORM) final String platform,
            @RequestParam(value = DatareportsCommonConstants.BCR) final boolean bcr,
            @RequestParam(value = DatareportsCommonConstants.COLS) final String colId) {

        final List<SampleSummary> sampleSummaryList = service.getSampleSummaryReport();
        final SampleSummary sampleSummary = service.findSampleSummary(sampleSummaryList, disease, center,
                analyte, platform);
        final List<Sample> sampleList = service.getDrillDown(sampleSummary, colId);
        final List<Sample> sortedSampleList = commonService.getSortedList(sampleList,
                service.getSampleComparator(bcr), sort, dir);
        final ViewAndExtensionForExport vae = commonService.getViewAndExtForExport(exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TYPE, exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TITLE, SampleSummaryReportConstants.SAMPLE_DETAILED_REPORT_VIEW);
        model.addAttribute(DatareportsCommonConstants.EXPORT_FILENAME, SampleSummaryReportConstants.SAMPLE_DETAILED_REPORT_VIEW + vae.getExtension());
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATE_FORMAT, DatareportsCommonConstants.DATE_TIME_FORMAT_US);
        if (bcr) {
            model.addAttribute(DatareportsCommonConstants.COLS, commonService.buildReportColumns(SampleSummaryReportConstants.SAMPLE_BCR_COLS, "name,sampleDate"));
        } else {
            model.addAttribute(DatareportsCommonConstants.COLS, commonService.buildReportColumns(SampleSummaryReportConstants.SAMPLE_CENTER_COLS, "name,sampleDate"));
        }
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATA, sortedSampleList);
        return vae.getView();
    }

    @RequestMapping(value = "/sampleDetailedReport.htm", method = RequestMethod.GET)
    public ModelMap sampleDetailedHandler(final ModelMap model, final HttpServletRequest request,
                                          @RequestParam(value = DISEASE) final String disease,
                                          @RequestParam(value = CENTER) final String centerName,
                                          @RequestParam(value = "centerType") final String centerType,
                                          @RequestParam(value = PORTION_ANALYTE) final String analyte,
                                          @RequestParam(value = PLATFORM) final String platform,
                                          @RequestParam(value = COLS) final String colId) {

        final List<SampleSummary> sampleSummaryList = service.getSampleSummaryReport();
        final String center = centerName + " (" + centerType + ")";
        final SampleSummary sampleSummary = service.findSampleSummary(sampleSummaryList, disease, center,
                analyte, platform);
        final List<Sample> sampleList = service.getDrillDown(sampleSummary, colId);
        commonService.processDisplayTag("sample", sampleList, model, request);
        model.addAttribute(DatareportsCommonConstants.DISEASE, disease);
        model.addAttribute(DatareportsCommonConstants.CENTER, center);
        model.addAttribute(SampleSummaryReportConstants.PORTION_ANALYTE, analyte);
        model.addAttribute(DatareportsCommonConstants.PLATFORM, platform);
        model.addAttribute(DatareportsCommonConstants.COLS, colId);
        return model;
    }

}//End of Class
