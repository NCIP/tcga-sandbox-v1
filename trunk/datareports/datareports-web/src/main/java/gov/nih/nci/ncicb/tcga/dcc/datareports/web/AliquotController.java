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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.AliquotReportService;
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

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.serverAddress;

/**
 * Class that create the aliquot controller for the web layer
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class AliquotController {

    @Autowired
    private AliquotReportService service;

    @Autowired
    private DatareportsService commonService;

    /**
     * Aliquot report simple handler
     *
     * @param model
     * @param session
     * @param request
     * @return view name
     */
    @RequestMapping(value = AliquotReportConstants.ALIQUOT_REPORT_URL, method = RequestMethod.GET,
            params = {"!" + DatareportsCommonConstants.DISEASE, "!" + DatareportsCommonConstants.CENTER, "!" + DatareportsCommonConstants.ALIQUOT_ID, "!" + DatareportsCommonConstants.BCR_BATCH, "!" + AliquotReportConstants.LEVEL_ONE,
                    "!" + AliquotReportConstants.LEVEL_TWO, "!" + AliquotReportConstants.LEVEL_THREE, "!" + DatareportsCommonConstants.PLATFORM})
    public String aliquotReportSimpleHandler(final ModelMap model, final HttpSession session, final HttpServletRequest request) {
        final ModelMap aliModel = (ModelMap) session.getAttribute(AliquotReportConstants.ALIQUOT_FILTER_MODEL);
        if (aliModel != null && aliModel.size() > 0) {
            model.addAllAttributes(aliModel);
        }
        final List<Aliquot> aliquotList = service.getAllAliquot();
        commonService.processDisplayTag("aliquot", aliquotList, model, request);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        session.removeAttribute(AliquotReportConstants.ALIQUOT_FILTER_MODEL);
        return AliquotReportConstants.ALIQUOT_REPORT_VIEW;
    }

    /**
     * Aliquot report full handler
     *
     * @param model
     * @param session
     * @param request
     * @param disease
     * @param center
     * @param aliquotId
     * @param bcrBatch
     * @param level1
     * @param level2
     * @param level3
     * @param platform
     * @return view name
     */
    @RequestMapping(value = AliquotReportConstants.ALIQUOT_REPORT_URL, method = RequestMethod.GET)
    public String aliquotReportFullHandler(
            final ModelMap model, final HttpSession session, final HttpServletRequest request,
            @RequestParam(value = DatareportsCommonConstants.DISEASE, required = false) final String disease,
            @RequestParam(value = DatareportsCommonConstants.CENTER, required = false) final String center,
            @RequestParam(value = DatareportsCommonConstants.ALIQUOT_ID, required = false) final String aliquotId,
            @RequestParam(value = DatareportsCommonConstants.BCR_BATCH, required = false) final String bcrBatch,
            @RequestParam(value = AliquotReportConstants.LEVEL_ONE, required = false) final String level1,
            @RequestParam(value = AliquotReportConstants.LEVEL_TWO, required = false) final String level2,
            @RequestParam(value = AliquotReportConstants.LEVEL_THREE, required = false) final String level3,
            @RequestParam(value = DatareportsCommonConstants.PLATFORM, required = false) final String platform) {
        List<Aliquot> aliquotList = service.getAllAliquot();
        commonService.processDisplayTag("aliquot", aliquotList, model, request);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        model.addAttribute(DatareportsCommonConstants.SHOW_FILTER_BOX, DatareportsCommonConstants.YES);
        model.addAttribute(DatareportsCommonConstants.DISEASE, disease);
        model.addAttribute(DatareportsCommonConstants.CENTER, center);
        model.addAttribute(DatareportsCommonConstants.PLATFORM, platform);
        model.addAttribute(DatareportsCommonConstants.ALIQUOT_ID, aliquotId);
        model.addAttribute(DatareportsCommonConstants.BCR_BATCH, bcrBatch);
        model.addAttribute(AliquotReportConstants.LEVEL_ONE, level1);
        model.addAttribute(AliquotReportConstants.LEVEL_TWO, level2);
        model.addAttribute(AliquotReportConstants.LEVEL_THREE, level3);
        session.setAttribute(AliquotReportConstants.ALIQUOT_FILTER_MODEL, model);

        return AliquotReportConstants.ALIQUOT_REPORT_VIEW;
    }

    /**
     * aliquot export handler
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
    @RequestMapping(value = AliquotReportConstants.ALIQUOT_EXPORT_URL, method = RequestMethod.POST)
    public String aliquotExportHandler(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.EXPORT_TYPE) final String exportType,
            @RequestParam(value = DatareportsCommonConstants.SORT, required = false) final String sort,
            @RequestParam(value = DatareportsCommonConstants.DIR, required = false) final String dir,
            @RequestParam(value = DatareportsCommonConstants.COLS, required = false) final String columns,
            @RequestParam(value = DatareportsCommonConstants.FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam(value = DatareportsCommonConstants.FORM_FILTER, required = false) final String jsonFormFilter) {

        String aliquot = null, bcrBatch = null;
        List<String> diseaseTab = null, centerTab = null, platformTab = null,
                levelOneTab = null, levelTwoTab = null, levelThreeTab = null;
        List<Aliquot> aliquotList = service.getAllAliquot();
        if (jsonFilterReq != null && !AliquotReportConstants.EMPTY_ALIQUOT_FILTER.equals(jsonFilterReq)) {
            aliquot = commonService.processJsonSingleFilter(DatareportsCommonConstants.ALIQUOT_ID, jsonFilterReq);
            diseaseTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.DISEASE, jsonFilterReq);
            centerTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.CENTER, jsonFilterReq);
            platformTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.PLATFORM, jsonFilterReq);
            bcrBatch = commonService.processJsonSingleFilter(DatareportsCommonConstants.BCR_BATCH, jsonFilterReq);
            levelOneTab = commonService.processJsonMultipleFilter(AliquotReportConstants.LEVEL_ONE, jsonFilterReq);
            levelTwoTab = commonService.processJsonMultipleFilter(AliquotReportConstants.LEVEL_TWO, jsonFilterReq);
            levelThreeTab = commonService.processJsonMultipleFilter(AliquotReportConstants.LEVEL_THREE, jsonFilterReq);
            aliquotList = service.getFilteredAliquotList(service.getAllAliquot(), aliquot, diseaseTab,
                    centerTab, platformTab, bcrBatch, levelOneTab,
                    levelTwoTab, levelThreeTab);
        }
        if (jsonFormFilter != null && !AliquotReportConstants.EMPTY_ALIQUOT_FILTER.equals(jsonFormFilter)) {
            aliquot = commonService.processJsonSingleFilter(DatareportsCommonConstants.ALIQUOT_ID, jsonFormFilter);
            diseaseTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.DISEASE, jsonFormFilter);
            centerTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.CENTER, jsonFormFilter);
            platformTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.PLATFORM, jsonFormFilter);
            bcrBatch = commonService.processJsonSingleFilter(DatareportsCommonConstants.BCR_BATCH, jsonFormFilter);
            levelOneTab = commonService.processJsonMultipleFilter(AliquotReportConstants.LEVEL_ONE, jsonFormFilter);
            levelTwoTab = commonService.processJsonMultipleFilter(AliquotReportConstants.LEVEL_TWO, jsonFormFilter);
            levelThreeTab = commonService.processJsonMultipleFilter(AliquotReportConstants.LEVEL_THREE, jsonFormFilter);
        }
        final List<Aliquot> filteredAliquotList = service.getFilteredAliquotList(aliquotList,
                aliquot, diseaseTab, centerTab, platformTab, bcrBatch,
                levelOneTab, levelTwoTab, levelThreeTab);
        final List<Aliquot> sortedAliquotList = commonService.getSortedList(filteredAliquotList,
                service.getAliquotComparator(), sort, dir);
        final ViewAndExtensionForExport vae = commonService.getViewAndExtForExport(exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TYPE, exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TITLE, AliquotReportConstants.ALIQUOT_REPORT_VIEW);
        model.addAttribute(DatareportsCommonConstants.EXPORT_FILENAME, AliquotReportConstants.ALIQUOT_REPORT_VIEW + vae.getExtension());
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATE_FORMAT, DatareportsCommonConstants.DATE_FORMAT_US);
        model.addAttribute(DatareportsCommonConstants.COLS, commonService.buildReportColumns(AliquotReportConstants.ALIQUOT_COLS, columns));
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATA, sortedAliquotList);
        return vae.getView();
    }

}//End of Class
