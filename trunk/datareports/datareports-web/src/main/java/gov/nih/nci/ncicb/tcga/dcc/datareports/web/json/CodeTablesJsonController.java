/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.common.util.FancyExceptionLogger;
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

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.BCR_BATCH_CODE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.CENTER_CODE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.CODE_TABLES_REPORT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.CODE_TABLES_REPORT_JSON_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.DISEASE_STUDY;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.PLATFORM_CODE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.TISSUE_SOURCE_SITE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATA_LEVEL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATA_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.LIMIT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SAMPLE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SORT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.START;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TISSUE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TOTAL_COUNT;

/**
 * Json controller for the code tables
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class CodeTablesJsonController {

    @Autowired
    private CodeTablesReportService service;

    @Autowired
    private DatareportsService commonService;

    protected final Log logger = LogFactory.getLog(getClass());

    @PostConstruct
    private void initAllLatestArchiveCache() {
        service.getBcrBatchCodeComparator();
        service.getCenterCodeComparator();
        service.getDataTypeComparator();
        service.getSampleTypeComparator();
        service.getPlatformCodeComparator();
        service.getTumorComparator();
        service.getCodeReportComparator();
        service.getTissueSourceSiteComparator();
    }

    /**
     * Complex code report with paging
     *
     * @param model
     * @param codeTablesReport
     * @param start
     * @param limit
     * @param sort
     * @param dir
     * @return model for json generation
     */
    @RequestMapping(value = CODE_TABLES_REPORT_JSON_URL, method = RequestMethod.POST)
    public ModelMap codeTablesReportComplexHandler(
            final ModelMap model,
            @RequestParam(value = CODE_TABLES_REPORT) final String codeTablesReport,
            @RequestParam(value = START) final int start,
            @RequestParam(value = LIMIT) final int limit,
            @RequestParam(value = SORT, required = false) final String sort,
            @RequestParam(value = DIR, required = false) final String dir) {

        if (CENTER_CODE.equals(codeTablesReport)) {
            processComplexJson(model, "CenterCode", CENTER_CODE, start, limit, sort, dir);
        } else if (DATA_TYPE.equals(codeTablesReport)) {
            processComplexJson(model, "DataType", DATA_TYPE, start, limit, sort, dir);
        } else if (DISEASE_STUDY.equals(codeTablesReport)) {
            processComplexJson(model, "Tumor", DISEASE_STUDY, start, limit, sort, dir);
        } else if (PLATFORM_CODE.equals(codeTablesReport)) {
            processComplexJson(model, "PlatformCode", PLATFORM_CODE, start, limit, sort, dir);
        } else if (TISSUE_SOURCE_SITE.equals(codeTablesReport)) {
            processComplexJson(model, "TissueSourceSite", TISSUE_SOURCE_SITE, start, limit, sort, dir);
        } else if (BCR_BATCH_CODE.equals(codeTablesReport)) {
            processComplexJson(model, "BcrBatchCode", BCR_BATCH_CODE, start, limit, sort, dir);
        }

        return model;
    }

    /**
     * process Complex json model manipulation
     *
     * @param model
     * @param bean
     * @param code
     * @param start
     * @param limit
     * @param sort
     * @param dir
     */
    private void processComplexJson(
            final ModelMap model, final String bean, final String code,
            final Integer start, final Integer limit, final String sort, final String dir) {
        try {
            final Method getList = service.getClass().getMethod("get" + bean);
            final Method getComparator = service.getClass().getMethod("get" + bean + "Comparator");
            final List list = (List) getList.invoke(service);
            final int totalCount = commonService.getTotalCount(list);
            final Map<String, Comparator> comparator = (Map<String, Comparator>) getComparator.invoke(service);
            final List sortedList = commonService.getSortedList(
                    list, comparator, sort, dir);
            final List xData = commonService.getPaginatedList(sortedList, start, limit);
            model.addAttribute(TOTAL_COUNT, totalCount);
            model.addAttribute(code + "Data", xData);
        } catch (Exception e) {
            logger.debug(FancyExceptionLogger.printException(e));
        }
    }

    /**
     * Simple report without paging
     *
     * @param model
     * @param codeTablesReport
     * @return model for json generation
     */
    @RequestMapping(value = CODE_TABLES_REPORT_JSON_URL, method = RequestMethod.GET)
    public ModelMap codeTablesReportSimpleHandler(
            final ModelMap model,
            @RequestParam(value = CODE_TABLES_REPORT) final String codeTablesReport) {

        if (DATA_LEVEL.equals(codeTablesReport)) {
            model.addAttribute(DATA_LEVEL + "Data", service.getDataLevel());
        } else if (SampleSummaryReportConstants.PORTION_ANALYTE.equals(codeTablesReport)) {
            model.addAttribute(SampleSummaryReportConstants.PORTION_ANALYTE + "Data", service.getPortionAnalyte());
        } else if (SAMPLE_TYPE.equals(codeTablesReport)) {
            model.addAttribute(SAMPLE_TYPE + "Data", service.getSampleType());
        } else if (TISSUE.equals(codeTablesReport)) {
            model.addAttribute(TISSUE + "Data", service.getTissue());
        }

        return model;
    }

}//End of Class
