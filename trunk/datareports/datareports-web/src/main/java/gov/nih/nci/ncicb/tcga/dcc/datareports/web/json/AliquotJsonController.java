/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.AliquotReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Controller that will receive all the json call from the aliquot report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class AliquotJsonController {

    @Autowired
    private AliquotReportService service;

    @Autowired
    private DatareportsService commonService;

    @PostConstruct
    private void initAllAliquotCache() {
        service.getAliquotFilterDistinctValues(DatareportsCommonConstants.DISEASE);
        service.getAliquotFilterDistinctValues(DatareportsCommonConstants.CENTER);
        service.getAliquotFilterDistinctValues(DatareportsCommonConstants.PLATFORM);
        service.getAliquotComparator();
    }

    /**
     * aliquot full json handler
     * @param model
     * @param start
     * @param limit
     * @param sort
     * @param dir
     * @param jsonFilterReq
     * @param jsonFormFilter
     * @return model for json generation
     */
    @RequestMapping (value = AliquotReportConstants.ALIQUOT_REPORT_JSON_URL, method = RequestMethod.POST)
    public ModelMap aliquotReportFullHandler(
            final ModelMap model,
            @RequestParam (value = DatareportsCommonConstants.START) final int start,
            @RequestParam (value = DatareportsCommonConstants.LIMIT) final int limit,
            @RequestParam (value = DatareportsCommonConstants.SORT, required = false) final String sort,
            @RequestParam (value = DatareportsCommonConstants.DIR, required = false) final String dir,
            @RequestParam (value = DatareportsCommonConstants.FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam (value = DatareportsCommonConstants.FORM_FILTER, required = false) final String jsonFormFilter) {

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
        final List<Aliquot> aliquotData = commonService.getPaginatedList(sortedAliquotList, start, limit);
        final int totalCount = commonService.getTotalCount(filteredAliquotList);

        model.addAttribute(DatareportsCommonConstants.TOTAL_COUNT, totalCount);
        model.addAttribute(AliquotReportConstants.ALIQUOT_DATA, aliquotData);
        return model;
    }

    /**
     * aliquot archive handler
     * @param model
     * @param aliquotId
     * @param level
     * @return model for json generation
     */
    @RequestMapping (value = AliquotReportConstants.ALIQUOT_ARCHIVE_JSON_URL)
    public ModelMap aliquotArchiveFullHandler(
            final ModelMap model,
            @RequestParam (value = "aliquotId") final String aliquotId,
            @RequestParam (value = "level") final int level) {

        final List<AliquotArchive> aliquotArchiveData = service.getAllAliquotArchive(aliquotId, level);
        model.addAttribute("aliquotArchiveData", aliquotArchiveData);
        return model;
    }

    /**
     * filter data handler
     * @param model
     * @param filterName
     * @return model for json generation
     */
    @RequestMapping (value = AliquotReportConstants.ALIQUOT_FILTER_DATA_URL)
    public ModelMap filterDataHandler(
            final ModelMap model,
            @RequestParam (value = DatareportsCommonConstants.FILTER) final String filterName) {

        if (DatareportsCommonConstants.DISEASE.equals(filterName)) {
            model.addAttribute(DatareportsCommonConstants.DISEASE+"Data", service.getAliquotFilterDistinctValues(DatareportsCommonConstants.DISEASE));
        }
        if (DatareportsCommonConstants.CENTER.equals(filterName)) {
            model.addAttribute(DatareportsCommonConstants.CENTER+"Data", service.getAliquotFilterDistinctValues(DatareportsCommonConstants.CENTER));
        }
        if (DatareportsCommonConstants.PLATFORM.equals(filterName)) {
            model.addAttribute(DatareportsCommonConstants.PLATFORM+"Data", service.getAliquotFilterDistinctValues(DatareportsCommonConstants.PLATFORM));
        }
        if (DatareportsCommonConstants.LEVEL.equals(filterName)) {
            model.addAttribute(DatareportsCommonConstants.LEVEL+"Data", AliquotReportConstants.ALI_LEVEL_LIST);
        }
        return model;
    }

}//End of Class
