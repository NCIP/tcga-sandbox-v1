/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ProjectCase;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.ProjectCaseDashboardConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.ProjectCaseDashboardService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FILTER_REQ;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.FORM_FILTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.LIMIT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SORT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.START;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TOTAL_COUNT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_FILTER_DATA_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_JSON_URL;

/**
 * Controller class for the Project Case dashboard
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */


@Controller
public class ProjectCaseDashboardJsonController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private ProjectCaseDashboardService service;

    @Autowired
    private DatareportsService commonService;

    @PostConstruct
    private void initAllProjectCaseCache() {
        service.getAllProjectCaseCounts();
        service.getProjectCaseFilterDistinctValues(DISEASE);
        service.getProjectCaseComparator();
    }


    public void refreshQuartzPCODCacheAndProc() {
        service.refreshProjectCaseDashboardProcedure();
        service.emptyPCODCache();
        initAllProjectCaseCache();
    }

    /**
     * filter data handler
     *
     * @param model      of request
     * @param filterName fro filtering
     * @return model for json generation
     */
    @RequestMapping(value = PROJECT_CASE_DASHBOARD_FILTER_DATA_URL)
    public ModelMap filterDataHandler(
            final ModelMap model,
            @RequestParam(value = FILTER) final String filterName) {

        if (DISEASE.equals(filterName)) {
            model.addAttribute(DISEASE + "Data", service.getProjectCaseFilterDistinctValues(DISEASE));
        }
        return model;
    }

    /**
     * handle project case data json
     *
     * @param model          of request
     * @param start          param for paging
     * @param limit          param for paging
     * @param sort           param for sorting
     * @param dir            param for sorting
     * @param jsonFilterReq  for url filtering
     * @param jsonFormFilter for web form filtering
     * @return model of request
     */
    @RequestMapping(value = PROJECT_CASE_DASHBOARD_JSON_URL, method = RequestMethod.POST)
    public ModelMap projectCaseDashboardJsonHandler(
            final ModelMap model,
            @RequestParam(value = START) final int start,
            @RequestParam(value = LIMIT) final int limit,
            @RequestParam(value = SORT, required = false) final String sort,
            @RequestParam(value = DIR, required = false) final String dir,
            @RequestParam(value = FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam(value = FORM_FILTER, required = false) final String jsonFormFilter) {

        List<String> diseaseTab = null;
        List<ProjectCase> projectCaseList = service.getAllProjectCaseCounts();
        if (jsonFilterReq != null && !ProjectCaseDashboardConstants.EMPTY_PROJECT_CASE_DASHBOARD_FILTER.equals(jsonFilterReq)) {
            diseaseTab = commonService.processJsonMultipleFilter(DISEASE, jsonFilterReq);
            projectCaseList = service.getFilteredProjectCaseList(service.getAllProjectCaseCounts(), diseaseTab);
        }
        if (jsonFormFilter != null && !ProjectCaseDashboardConstants.EMPTY_PROJECT_CASE_DASHBOARD_FILTER.equals(jsonFormFilter)) {
            diseaseTab = commonService.processJsonMultipleFilter(DISEASE, jsonFormFilter);
        }
        final List<ProjectCase> filteredProjectCaseList = service.getFilteredProjectCaseList(projectCaseList, diseaseTab);
        final ProjectCase total = filteredProjectCaseList.remove(filteredProjectCaseList.size() - 1);
        final List<ProjectCase> sortedProjectCaseList = commonService.getSortedList(filteredProjectCaseList,
                service.getProjectCaseComparator(), sort, dir);
        sortedProjectCaseList.add(total);
        final List<ProjectCase> projectCaseData = commonService.getPaginatedList(sortedProjectCaseList, start, limit);
        final int totalCount = commonService.getTotalCount(filteredProjectCaseList);
        model.addAttribute(TOTAL_COUNT, totalCount);
        model.addAttribute(ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_DATA, projectCaseData);
        return model;
    }

}//End of Class
