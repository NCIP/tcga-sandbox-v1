/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BamTelemetry;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.BamTelemetryReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants.BAM_TELEMETRY_DATA;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants.BAM_TELEMETRY_FILTER_DATA_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants.BAM_TELEMETRY_REPORT_JSON_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants.EMPTY_BAM_TELEMETRY_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ALIQUOT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ALIQUOT_UUID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATA_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_FROM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_TO;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER_REQ;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FORM_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.LIMIT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.MOLECULE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SORT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.START;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TOTAL_COUNT;

/**
 * Json controller for the bam telemetry report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class BamTelemetryJsonController {

    @Autowired
    private BamTelemetryReportService service;

    @Autowired
    private DatareportsService commonService;

    @PostConstruct
    private void initAllBamTelemetryCache() {
        service.getAllBamTelemetry();
        service.getBamTelemetryFilterDistinctValues(DISEASE);
        service.getBamTelemetryFilterDistinctValues(CENTER);
        service.getBamTelemetryFilterDistinctValues(DATA_TYPE);
        service.getBamTelemetryFilterDistinctValues(MOLECULE);
        service.getBamTelemetryComparator();
    }

    /**
     * BamTelemetry full json handler
     *
     * @param model
     * @param start
     * @param limit
     * @param sort
     * @param dir
     * @param jsonFilterReq
     * @param jsonFormFilter
     * @return
     */
    @RequestMapping(value = BAM_TELEMETRY_REPORT_JSON_URL, method = RequestMethod.POST)
    public ModelMap bamTelemetryReportFullHandler(
            final ModelMap model,
            @RequestParam(value = START) final int start,
            @RequestParam(value = LIMIT) final int limit,
            @RequestParam(value = SORT, required = false) final String sort,
            @RequestParam(value = DIR, required = false) final String dir,
            @RequestParam(value = FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam(value = FORM_FILTER, required = false) final String jsonFormFilter) {

        String aliquotId = null, aliquotUUID = null, dateFrom = null, dateTo = null;
        List<String> diseaseTab = null, centerTab = null, moleculeTab = null,
                dataTypeTab = null;
        List<BamTelemetry> bamTelemetryList = service.getAllBamTelemetry();
        if (jsonFilterReq != null && !EMPTY_BAM_TELEMETRY_FILTER.equals(jsonFilterReq)) {
            aliquotId = commonService.processJsonSingleFilter(ALIQUOT_ID, jsonFilterReq);
            aliquotUUID = commonService.processJsonSingleFilter(ALIQUOT_UUID, jsonFilterReq);
            dateFrom = commonService.processJsonSingleFilter(DATE_FROM, jsonFilterReq);
            dateTo = commonService.processJsonSingleFilter(DATE_TO, jsonFilterReq);
            diseaseTab = commonService.processJsonMultipleFilter(DISEASE, jsonFilterReq);
            centerTab = commonService.processJsonMultipleFilter(CENTER, jsonFilterReq);
            dataTypeTab = commonService.processJsonMultipleFilter(DATA_TYPE, jsonFilterReq);
            moleculeTab = commonService.processJsonMultipleFilter(MOLECULE, jsonFilterReq);
            bamTelemetryList = service.getFilteredBamTelemetryList(service.getAllBamTelemetry(), aliquotUUID, aliquotId, dateFrom,
                    dateTo, diseaseTab, centerTab, dataTypeTab, moleculeTab);
        }
        if (jsonFormFilter != null && !EMPTY_BAM_TELEMETRY_FILTER.equals(jsonFormFilter)) {
            aliquotId = commonService.processJsonSingleFilter(ALIQUOT_ID, jsonFormFilter);
            aliquotUUID = commonService.processJsonSingleFilter(ALIQUOT_UUID, jsonFormFilter);
            dateFrom = commonService.processJsonSingleFilter(DATE_FROM, jsonFormFilter);
            dateTo = commonService.processJsonSingleFilter(DATE_TO, jsonFormFilter);
            diseaseTab = commonService.processJsonMultipleFilter(DISEASE, jsonFormFilter);
            centerTab = commonService.processJsonMultipleFilter(CENTER, jsonFormFilter);
            dataTypeTab = commonService.processJsonMultipleFilter(DATA_TYPE, jsonFormFilter);
            moleculeTab = commonService.processJsonMultipleFilter(MOLECULE, jsonFormFilter);
        }
        final List<BamTelemetry> filteredBamTelemetryList = service.getFilteredBamTelemetryList(bamTelemetryList, aliquotUUID,
                aliquotId, dateFrom, dateTo, diseaseTab, centerTab, dataTypeTab, moleculeTab);
        final List<BamTelemetry> sortedBamTelemetryList = commonService.getSortedList(filteredBamTelemetryList,
                service.getBamTelemetryComparator(), sort, dir);
        final List<Aliquot> aliquotData = commonService.getPaginatedList(sortedBamTelemetryList, start, limit);
        final int totalCount = commonService.getTotalCount(filteredBamTelemetryList);
        model.addAttribute(TOTAL_COUNT, totalCount);
        model.addAttribute(BAM_TELEMETRY_DATA, aliquotData);
        return model;
    }

    /**
     * filter data handler
     *
     * @param model
     * @param filterName
     * @return model for json generation
     */
    @RequestMapping(value = BAM_TELEMETRY_FILTER_DATA_URL)
    public ModelMap filterDataHandler(
            final ModelMap model,
            @RequestParam(value = FILTER) final String filterName) {

        if (DISEASE.equals(filterName)) {
            model.addAttribute(DISEASE + "Data", service.getBamTelemetryFilterDistinctValues(DISEASE));
        }
        if (CENTER.equals(filterName)) {
            model.addAttribute(CENTER + "Data", service.getBamTelemetryFilterDistinctValues(CENTER));
        }
        if (DATA_TYPE.equals(filterName)) {
            model.addAttribute(DATA_TYPE + "Data", service.getBamTelemetryFilterDistinctValues(DATA_TYPE));
        }
        if (MOLECULE.equals(filterName)) {
            model.addAttribute(MOLECULE + "Data", service.getBamTelemetryFilterDistinctValues(MOLECULE));
        }
        if (ALIQUOT_UUID.equals(filterName)) {
            model.addAttribute(ALIQUOT_UUID + "Data", service.getBamTelemetryFilterDistinctValues(ALIQUOT_UUID));
        }
        return model;
    }


}//End of class
