/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotIdBreakdown;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.AliquotIdBreakdownReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_DATA;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_REPORT_JSON_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotIdBreakdownReportConstants.EMPTY_ALIQUOT_ID_BREAKDOWN_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ALIQUOT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ANALYTE_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER_REQ;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FORM_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.LIMIT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PARTICIPANT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SAMPLE_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SORT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.START;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TOTAL_COUNT;

/**
 * aliquot Id breakdown json controller
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class AliquotIdBreakdownJsonController {

    @Autowired
    private AliquotIdBreakdownReportService service;

    @Autowired
    private DatareportsService commonService;

    @PostConstruct
    private void initAllBiospecimenBreakdownCache() {
        service.getAliquotIdBreakdownComparator();
    }

    /**
     * aliquot Id Breakdown full json handler
     * @param model
     * @param start
     * @param limit
     * @param sort
     * @param dir
     * @param jsonFilterReq
     * @param jsonFormFilter
     * @return model for json generation
     */
    @RequestMapping (value = ALIQUOT_ID_BREAKDOWN_REPORT_JSON_URL, method = RequestMethod.POST)
    public ModelMap aliquotIdBreakdownReportFullHandler(
            final ModelMap model,
            @RequestParam (value = START) final int start,
            @RequestParam (value = LIMIT) final int limit,
            @RequestParam (value = SORT, required = false) final String sort,
            @RequestParam (value = DIR, required = false) final String dir,
            @RequestParam (value = FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam (value = FORM_FILTER, required = false) final String jsonFormFilter) {

        String aliquotId = null, analyteId = null, sampleId = null, participantId = null;
        List<AliquotIdBreakdown> aliquotIdBreakdownList = service.getAliquotIdBreakdown();
        if (jsonFilterReq != null && !EMPTY_ALIQUOT_ID_BREAKDOWN_FILTER.equals(jsonFilterReq)) {
            aliquotId = commonService.processJsonSingleFilter(ALIQUOT_ID, jsonFilterReq);
            analyteId = commonService.processJsonSingleFilter(ANALYTE_ID, jsonFilterReq);
            sampleId = commonService.processJsonSingleFilter(SAMPLE_ID, jsonFilterReq);
            participantId = commonService.processJsonSingleFilter(PARTICIPANT_ID, jsonFilterReq);
            aliquotIdBreakdownList = service.getFilteredAliquotIdBreakdownList(
                    service.getAliquotIdBreakdown(),
                    aliquotId, analyteId, sampleId, participantId);
        }
        if (jsonFormFilter != null && !EMPTY_ALIQUOT_ID_BREAKDOWN_FILTER.equals(jsonFormFilter)) {
            aliquotId = commonService.processJsonSingleFilter(ALIQUOT_ID,jsonFormFilter);
            analyteId = commonService.processJsonSingleFilter(ANALYTE_ID,jsonFormFilter);
            sampleId = commonService.processJsonSingleFilter(SAMPLE_ID,jsonFormFilter);
            participantId = commonService.processJsonSingleFilter(PARTICIPANT_ID,jsonFormFilter);
        }
        final List<AliquotIdBreakdown> filteredAliquotIdBreakdownList =
                service.getFilteredAliquotIdBreakdownList(aliquotIdBreakdownList,
                    aliquotId, analyteId, sampleId, participantId);
        final List<AliquotIdBreakdown> sortedAliquotIdBreakdownList =
                commonService.getSortedList(filteredAliquotIdBreakdownList,
                service.getAliquotIdBreakdownComparator(), sort, dir);
        final List<AliquotIdBreakdown> aliquotIdBreakdownData = commonService.getPaginatedList(
                sortedAliquotIdBreakdownList, start, limit);
        final int totalCount = commonService.getTotalCount(filteredAliquotIdBreakdownList);

        model.addAttribute(TOTAL_COUNT, totalCount);
        model.addAttribute(ALIQUOT_ID_BREAKDOWN_DATA, aliquotIdBreakdownData);
        return model;
    }

}//End of Class
