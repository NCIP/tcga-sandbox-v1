/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sample;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.SampleSummaryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BCR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.COLS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER_REQ;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FORM_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.LEVEL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.LIMIT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PLATFORM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SORT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.START;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TOTAL_COUNT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.CENTER_EMAIL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.EMPTY_SAMPLE_SUMMARY_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.LEVEL4_SS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.PORTION_ANALYTE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.SAMPLE_DETAILED_JSON_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.SAMPLE_SUMMARY_DATA;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.SAMPLE_SUMMARY_FILTER_DATA_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.SAMPLE_SUMMARY_REPORT_JSON_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.SS_LEVEL_LIST;

/**
 * Sample Summary Json controller processing all json calls
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class SampleSummaryJsonController {

    @Autowired
    private SampleSummaryReportService service;

    @Autowired
    private DatareportsService commonService;

    @PostConstruct
    private void initAllSampleSummaryCache() {
        service.getSampleSummaryFilterDistinctValues(DISEASE);
        service.getSampleSummaryFilterDistinctValues(CENTER);
        service.getSampleSummaryFilterDistinctValues(PORTION_ANALYTE);
        service.getSampleSummaryFilterDistinctValues(PLATFORM);
        service.getSampleSummaryComparator();
    }

    /**
     * sample summary json full handler
     * @param model
     * @param start
     * @param limit
     * @param sort
     * @param dir
     * @param centerFromEmail
     * @param jsonFilterReq
     * @param jsonFormFilter
     * @return model for json generation
     */
    @RequestMapping (value = SAMPLE_SUMMARY_REPORT_JSON_URL, method = RequestMethod.POST)
    public ModelMap sampleSummaryReportFullHandler(
            final ModelMap model,
            @RequestParam (value = START) final int start,
            @RequestParam (value = LIMIT) final int limit,
            @RequestParam (value = SORT, required = false) final String sort,
            @RequestParam (value = DIR, required = false) final String dir,
            @RequestParam (value = CENTER_EMAIL, required = false) final String centerFromEmail,
            @RequestParam (value = FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam (value = FORM_FILTER, required = false) final String jsonFormFilter) {

        List<String> diseaseTab = null, centerTab = null, platformTab = null, analyteTab = null, levelFourTab = null;
        List<SampleSummary> sampleSummaryList = service.processSampleSummary(centerFromEmail);
        if (jsonFilterReq != null && !EMPTY_SAMPLE_SUMMARY_FILTER.equals(jsonFilterReq)) {
            diseaseTab = commonService.processJsonMultipleFilter(DISEASE, jsonFilterReq);
            centerTab = commonService.processJsonMultipleFilter(CENTER, jsonFilterReq);
            analyteTab = commonService.processJsonMultipleFilter(PORTION_ANALYTE, jsonFilterReq);
            platformTab = commonService.processJsonMultipleFilter(PLATFORM, jsonFilterReq);
            levelFourTab = commonService.processJsonMultipleFilter(LEVEL4_SS, jsonFilterReq);
            sampleSummaryList = service.getFilteredSampleSummaryList(
                    service.processSampleSummary(centerFromEmail),
                    diseaseTab, centerTab, analyteTab, platformTab, levelFourTab);
        }
        if (jsonFormFilter != null && !EMPTY_SAMPLE_SUMMARY_FILTER.equals(jsonFormFilter)) {
            diseaseTab = commonService.processJsonMultipleFilter(DISEASE, jsonFormFilter);
            centerTab = commonService.processJsonMultipleFilter(CENTER, jsonFormFilter);
            analyteTab = commonService.processJsonMultipleFilter(PORTION_ANALYTE, jsonFormFilter);
            platformTab = commonService.processJsonMultipleFilter(PLATFORM, jsonFormFilter);
            levelFourTab = commonService.processJsonMultipleFilter(LEVEL4_SS, jsonFormFilter);
        }
        final List<SampleSummary> filteredSampleSummaryList = service.getFilteredSampleSummaryList(sampleSummaryList,
                diseaseTab, centerTab, analyteTab, platformTab, levelFourTab);
        final List<SampleSummary> sortedSampleSummaryList = commonService.getSortedList(filteredSampleSummaryList,
                service.getSampleSummaryComparator(), sort, dir);
        final List<SampleSummary> sampleSummaryData = commonService.getPaginatedList(sortedSampleSummaryList, start, limit);
        final int totalCount = commonService.getTotalCount(filteredSampleSummaryList);

        model.addAttribute(TOTAL_COUNT, totalCount);
        model.addAttribute(SAMPLE_SUMMARY_DATA, sampleSummaryData);
        return model;
    }

    /**
     * filter data handler
     * @param model
     * @param filterName
     * @return model for json generation
     */
    @RequestMapping (value = SAMPLE_SUMMARY_FILTER_DATA_URL)
    public ModelMap filterDataHandler(final ModelMap model,
            @RequestParam (value = FILTER) final String filterName) {

        if (DISEASE.equals(filterName)) {
            model.addAttribute(DISEASE+"Data", service.getSampleSummaryFilterDistinctValues(DISEASE));
        }
        if (CENTER.equals(filterName)) {
            model.addAttribute(CENTER+"Data", service.getSampleSummaryFilterDistinctValues(CENTER));
        }
        if (PORTION_ANALYTE.equals(filterName)) {
            model.addAttribute(PORTION_ANALYTE+"Data",
                    service.getSampleSummaryFilterDistinctValues(PORTION_ANALYTE));
        }
        if (PLATFORM.equals(filterName)) {
            model.addAttribute(PLATFORM+"Data", service.getSampleSummaryFilterDistinctValues(PLATFORM));
        }
        if (LEVEL.equals(filterName)) {
            model.addAttribute(LEVEL+"Data", SS_LEVEL_LIST);
        }
        return model;
    }
    

    @RequestMapping (value = SAMPLE_DETAILED_JSON_URL, method = RequestMethod.POST)
    public ModelMap sampleDetailedHandler(final ModelMap model,
            @RequestParam (value = START) final int start,
            @RequestParam (value = LIMIT) final int limit,
            @RequestParam (value = SORT, required = false) final String sort,
            @RequestParam (value = DIR, required = false) final String dir,
            @RequestParam (value = DISEASE) final String disease,
            @RequestParam (value = CENTER) final String center,
            @RequestParam (value = PORTION_ANALYTE) final String analyte,
            @RequestParam (value = PLATFORM) final String platform,
            @RequestParam (value = BCR) final boolean bcr,
            @RequestParam (value = COLS) final String colId) {
        
        final List<SampleSummary> sampleSummaryList = service.getSampleSummaryReport();
        final SampleSummary sampleSummary = service.findSampleSummary(sampleSummaryList,disease,center,
                analyte,platform);
        final List<Sample> sampleList = service.getDrillDown(sampleSummary,colId);
        final List<Sample> sortedSampleList = commonService.getSortedList(sampleList,
                service.getSampleComparator(bcr), sort, dir);
        final List<Sample> sampleData = commonService.getPaginatedList(sortedSampleList, start, limit);
        final int totalCount = commonService.getTotalCount(sampleList);
        model.addAttribute("sampleTotal", totalCount);
        model.addAttribute("sampleData", sampleData);
        return model;
    }

}//End of Class
