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
import gov.nih.nci.ncicb.tcga.dcc.common.util.FancyExceptionLogger;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.CodeTablesReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Code tables controller
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class CodeTablesController {

    protected final Log logger = LogFactory.getLog(getClass());
    @Autowired
    private CodeTablesReportService service;
    @Autowired
    private DatareportsService commonService;

    /**
     * code tables home handler
     *
     * @return view name
     */
    @RequestMapping(value = CodeTablesReportConstants.CODE_TABLES_REPORT_URL, method = RequestMethod.GET)
    public String codeTablesReportHomeHandler(final ModelMap model, final HttpServletRequest request) {

        commonService.processDisplayTag(CodeTablesReportConstants.CENTER_CODE, getList("CenterCode"), model, request);
        commonService.processDisplayTag(DatareportsCommonConstants.DATA_TYPE, getList("DataType"), model, request);
        commonService.processDisplayTag(CodeTablesReportConstants.DISEASE_STUDY, getList("Tumor"), model, request);
        commonService.processDisplayTag(CodeTablesReportConstants.PLATFORM_CODE, getList("PlatformCode"), model, request);
        commonService.processDisplayTag(CodeTablesReportConstants.TISSUE_SOURCE_SITE, getList("TissueSourceSite"), model, request);
        commonService.processDisplayTag(CodeTablesReportConstants.BCR_BATCH_CODE, getList("BcrBatchCode"), model, request);
        commonService.processDisplayTag(DatareportsCommonConstants.DATA_LEVEL, getList("DataLevel"), model, request);
        commonService.processDisplayTag(SampleSummaryReportConstants.PORTION_ANALYTE, getList("PortionAnalyte"), model, request);
        commonService.processDisplayTag(DatareportsCommonConstants.SAMPLE_TYPE, getList("SampleType"), model, request);
        commonService.processDisplayTag(DatareportsCommonConstants.TISSUE, getList("Tissue"), model, request);

        return CodeTablesReportConstants.CODE_TABLES_REPORT_VIEW;
    }

    /**
     * code table export handler
     *
     * @param model
     * @param codeTablesReport
     * @param exportType
     * @param sort
     * @param dir
     * @return view name
     */
    @RequestMapping(value = CodeTablesReportConstants.CODE_TABLES_EXPORT_URL, method = RequestMethod.POST)
    public String codeTablesExportHandler(
            final ModelMap model,
            @RequestParam(value = CodeTablesReportConstants.CODE_TABLES_REPORT) final String codeTablesReport,
            @RequestParam(value = DatareportsCommonConstants.EXPORT_TYPE) final String exportType,
            @RequestParam(value = DatareportsCommonConstants.SORT, required = false) final String sort,
            @RequestParam(value = DatareportsCommonConstants.DIR, required = false) final String dir) {

        final ViewAndExtensionForExport vae = commonService.getViewAndExtForExport(exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TYPE, exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TITLE, codeTablesReport);
        model.addAttribute(DatareportsCommonConstants.EXPORT_FILENAME, codeTablesReport + vae.getExtension());
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATE_FORMAT, DatareportsCommonConstants.DATE_FORMAT_US);
        if (CodeTablesReportConstants.CENTER_CODE.equals(codeTablesReport)) {
            processComplexExport(model, "CenterCode", CodeTablesReportConstants.CENTER_CODE_COLS, sort, dir);
        } else if (DatareportsCommonConstants.DATA_TYPE.equals(codeTablesReport)) {
            processComplexExport(model, "DataType", CodeTablesReportConstants.DATA_TYPE_COLS, sort, dir);
        } else if (CodeTablesReportConstants.DISEASE_STUDY.equals(codeTablesReport)) {
            processComplexExport(model, "Tumor", CodeTablesReportConstants.TUMOR_COLS, sort, dir);
        } else if (CodeTablesReportConstants.PLATFORM_CODE.equals(codeTablesReport)) {
            processComplexExport(model, "PlatformCode", CodeTablesReportConstants.PLATFORM_COLS, sort, dir);
        } else if (CodeTablesReportConstants.TISSUE_SOURCE_SITE.equals(codeTablesReport)) {
            processComplexExport(model, "TissueSourceSite", CodeTablesReportConstants.TISSUE_SOURCE_SITE_COLS, sort, dir);
        } else if (CodeTablesReportConstants.BCR_BATCH_CODE.equals(codeTablesReport)) {
            processComplexExport(model, "BcrBatchCode", CodeTablesReportConstants.BCR_BATCH_COLS, sort, dir);
        } else if (DatareportsCommonConstants.DATA_LEVEL.equals(codeTablesReport)) {
            processSimpleExport(model, "DataLevel", CodeTablesReportConstants.DATA_LEVEL_COLS);
        } else if (SampleSummaryReportConstants.PORTION_ANALYTE.equals(codeTablesReport)) {
            processSimpleExport(model, "PortionAnalyte", CodeTablesReportConstants.CODE_REPORT_COLS);
        } else if (DatareportsCommonConstants.SAMPLE_TYPE.equals(codeTablesReport)) {
            processSimpleExport(model, "SampleType", CodeTablesReportConstants.SAMPLE_TYPE_COLS);
        } else if (DatareportsCommonConstants.TISSUE.equals(codeTablesReport)) {
            processSimpleExport(model, "Tissue", CodeTablesReportConstants.TISSUE_COLS);
        }

        return vae.getView();
    }

    /**
     * process Complex export function used for refactoring.
     *
     * @param model
     * @param bean
     * @param colMap
     * @param sort
     * @param dir
     */
    private void processComplexExport(
            final ModelMap model, final String bean,
            final Map<String, String> colMap,
            final String sort, final String dir) {
        try {
            final Method getList = service.getClass().getMethod("get" + bean);
            final Method getComparator = service.getClass().getMethod("get" + bean + "Comparator");
            final List list = (List) getList.invoke(service);
            final Map<String, Comparator> comparator = (Map<String, Comparator>) getComparator.invoke(service);
            final List sortedList = commonService.getSortedList(
                    list, comparator, sort, dir);
            model.addAttribute(DatareportsCommonConstants.COLS, colMap);
            model.addAttribute(DatareportsCommonConstants.EXPORT_DATA, sortedList);
        } catch (Exception e) {
            logger.debug(FancyExceptionLogger.printException(e));
        }
    }

    /**
     * process simple export function used for refactoring.
     *
     * @param model
     * @param bean
     * @param colMap
     */
    private void processSimpleExport(final ModelMap model, final String bean, final Map<String, String> colMap) {
        try {
            final Method getList = service.getClass().getMethod("get" + bean);
            final List list = (List) getList.invoke(service);
            model.addAttribute(DatareportsCommonConstants.COLS, colMap);
            model.addAttribute(DatareportsCommonConstants.EXPORT_DATA, list);
        } catch (Exception e) {
            logger.debug(FancyExceptionLogger.printException(e));
        }
    }

    /**
     * get simple code table lists according to name.
     *
     * @param bean
     */
    private List getList(final String bean) {
        try {
            final Method getList = service.getClass().getMethod("get" + bean);
            final List list = (List) getList.invoke(service);
            return list;
        } catch (Exception e) {
            logger.debug(FancyExceptionLogger.printException(e));
            return new LinkedList();
        }
    }

}//End of Class
