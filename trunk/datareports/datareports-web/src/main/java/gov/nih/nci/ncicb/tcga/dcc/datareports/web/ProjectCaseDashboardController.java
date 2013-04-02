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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ProjectCase;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.serverAddress;

/**
 * Web controller of the project case dashboard
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class ProjectCaseDashboardController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private ProjectCaseDashboardService service;

    @Autowired
    private DatareportsService commonService;

    /**
     * handler of the Project Case url without any request parameter
     *
     * @param model   of request
     * @param session of request
     * @param request
     * @return view name of request
     */
    @RequestMapping(value = ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_URL, method = RequestMethod.GET,
            params = {"!" + DatareportsCommonConstants.DISEASE})
    public String projectCaseDashboardHandler(final ModelMap model, final HttpSession session,
                                              final HttpServletRequest request) {
        final ModelMap pcModel = (ModelMap) session.getAttribute(ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_FILTER_MODEL);
        if (pcModel != null && pcModel.size() > 0) {
            model.addAllAttributes(pcModel);
        }
        final List<ProjectCase> projectCaseList = service.getAllProjectCaseCounts();
        commonService.processDisplayTag("projectCase", projectCaseList, model, request);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        session.removeAttribute(ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_FILTER_MODEL);
        return ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_VIEW;
    }

    /**
     * handler of the Project case url with request parameter
     *
     * @param model   of request
     * @param session of request
     * @param request
     * @param disease url parameter
     * @return forward view name of request
     */
    @RequestMapping(value = ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_URL, method = RequestMethod.GET)
    public String projectCaseDashboardFullHandler(
            final ModelMap model, final HttpSession session, final HttpServletRequest request,
            @RequestParam(value = DatareportsCommonConstants.DISEASE, required = false) final String disease) {
        final List<ProjectCase> projectCaseList = service.getAllProjectCaseCounts();
        commonService.processDisplayTag("projectCase", projectCaseList, model, request);
        model.addAttribute(DatareportsCommonConstants.SERVER_URL, serverAddress);
        model.addAttribute(DatareportsCommonConstants.SHOW_FILTER_BOX, DatareportsCommonConstants.YES);
        model.addAttribute(DatareportsCommonConstants.DISEASE, disease);
        session.setAttribute(ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_FILTER_MODEL, model);

        return ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_VIEW;
    }

    /**
     * handler of the export url for project Case
     *
     * @param model          of request
     * @param exportType     parameter for export
     * @param sort           parameter for export
     * @param dir            of sort parameter for export
     * @param columns        to export
     * @param jsonFilterReq  url filter
     * @param jsonFormFilter form filter
     * @return export view
     */
    @RequestMapping(value = ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_EXPORT_URL, method = RequestMethod.POST)
    public String projectCodeDashboardExportHandler(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.EXPORT_TYPE) final String exportType,
            @RequestParam(value = DatareportsCommonConstants.SORT, required = false) final String sort,
            @RequestParam(value = DatareportsCommonConstants.DIR, required = false) final String dir,
            @RequestParam(value = DatareportsCommonConstants.COLS, required = false) final String columns,
            @RequestParam(value = DatareportsCommonConstants.FILTER_REQ, required = false) final String jsonFilterReq,
            @RequestParam(value = DatareportsCommonConstants.FORM_FILTER, required = false) final String jsonFormFilter) {

        List<String> diseaseTab = null;
        List<ProjectCase> projectCaseList = service.getAllProjectCaseCounts();
        if (jsonFilterReq != null && !ProjectCaseDashboardConstants.EMPTY_PROJECT_CASE_DASHBOARD_FILTER.equals(jsonFilterReq)) {
            diseaseTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.DISEASE, jsonFilterReq);
            projectCaseList = service.getFilteredProjectCaseList(service.getAllProjectCaseCounts(), diseaseTab);
        }
        if (jsonFormFilter != null && !ProjectCaseDashboardConstants.EMPTY_PROJECT_CASE_DASHBOARD_FILTER.equals(jsonFormFilter)) {
            diseaseTab = commonService.processJsonMultipleFilter(DatareportsCommonConstants.DISEASE, jsonFormFilter);
        }
        final List<ProjectCase> filteredProjectCaseList = service.getFilteredProjectCaseList(projectCaseList, diseaseTab);
        final ProjectCase total = filteredProjectCaseList.remove(filteredProjectCaseList.size() - 1);
        final List<ProjectCase> sortedProjectCaseList = commonService.getSortedList(filteredProjectCaseList,
                service.getProjectCaseComparator(), sort, dir);
        sortedProjectCaseList.add(total);
        final ViewAndExtensionForExport vae = commonService.getViewAndExtForExport(exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TYPE, exportType);
        model.addAttribute(DatareportsCommonConstants.EXPORT_TITLE, ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_VIEW);
        model.addAttribute(DatareportsCommonConstants.EXPORT_FILENAME, ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_VIEW + vae.getExtension());
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATE_FORMAT, DatareportsCommonConstants.DATE_FORMAT_US);
        model.addAttribute(DatareportsCommonConstants.COLS, commonService.buildReportColumns(ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_COLS, columns));
        model.addAttribute(DatareportsCommonConstants.EXPORT_DATA, sortedProjectCaseList);
        return vae.getView();
    }

}//End of Class
