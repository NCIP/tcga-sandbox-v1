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
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.ProjectCaseDashboardConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.ProjectCaseDashboardService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ModelMap;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_COLS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for the project case dashboard controller
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class ProjectCaseDashboardControllerFastTest {

    private final Mockery context = new JUnit4Mockery();
    public Map<String, String> mockMapCols = new LinkedHashMap<String, String>() {{
        put("disease", "Disease");
    }};
    private ProjectCaseDashboardService service;
    private DatareportsService commonService;
    private ProjectCaseDashboardController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    private ModelMap model;

    @Before
    public void before() throws Exception {

        service = context.mock(ProjectCaseDashboardService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new ProjectCaseDashboardController();

        Field serviceControllerField = controller.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(controller, service);

        Field commonServiceControllerField = controller.getClass().getDeclaredField("commonService");
        commonServiceControllerField.setAccessible(true);
        commonServiceControllerField.set(controller, commonService);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        model = new ModelMap();
    }

    @Test
    public void testProjectCaseDashboardFullHandler() throws Exception {
        request.setMethod("GET");
        final List<ProjectCase> mockbio = makeMockProjectCases();
        context.checking(new Expectations() {{
            allowing(service).getAllProjectCaseCounts();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("projectCase", mockbio, model, request);
        }});
        final String viewName = controller.projectCaseDashboardFullHandler(model, session, request, null);
        assertTrue(viewName != null);
        assertEquals(ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_VIEW, viewName);
    }

    @Test
    public void testProjectCaseDashboardSimpleHandler() throws Exception {
        final List<ProjectCase> mockbio = makeMockProjectCases();
        context.checking(new Expectations() {{
            allowing(service).getAllProjectCaseCounts();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("projectCase", mockbio, model, request);
        }});
        final String viewName = controller.projectCaseDashboardHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("projectCaseDashboard", viewName);
    }

    @Test
    public void testProjectCaseDashboardSimpleHandlerWithSessionFull() throws Exception {
        session.setAttribute("projectCaseDashboardFilterModel", new ModelMap() {{
            put("flip", "flap");
        }});
        final List<ProjectCase> mockbio = makeMockProjectCases();
        context.checking(new Expectations() {{
            allowing(service).getAllProjectCaseCounts();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("projectCase", mockbio, model, request);
        }});
        final String viewName = controller.projectCaseDashboardHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("projectCaseDashboard", viewName);
        assertTrue(model.get("flip") != null);
        assertEquals("flap", model.get("flip"));
    }

    @Test
    public void testProjectCaseDashboardExportHandler() throws Exception {
        final String sort = "";
        final String dir = "";
        final String columns = "disease";
        final String filterReq = "";
        final String filterForm = "";
        final List<ProjectCase> mockBio = makeMockProjectCases();
        context.checking(new Expectations() {{
            allowing(service).getAllProjectCaseCounts();
            will(returnValue(mockBio));
            allowing(service).getFilteredProjectCaseList(mockBio, null);
            will(returnValue(mockBio));
            allowing(service).getProjectCaseComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockBio, null, sort, dir);
            will(returnValue(mockBio));
            allowing(commonService).buildReportColumns(PROJECT_CASE_DASHBOARD_COLS, columns);
            will(returnValue(mockMapCols));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
            allowing(commonService).processJsonMultipleFilter(DISEASE, "");
            will(returnValue(null));
        }});

        request.setMethod("GET");
        final String viewName = controller.projectCodeDashboardExportHandler(model, "tab", sort, dir, columns,
                filterReq, filterForm);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals("projectCaseDashboard", title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals("projectCaseDashboard.txt", fileName);
        List<ProjectCase> data = (List<ProjectCase>) model.get("data");
        assertEquals(mockBio, data);
    }

    public ViewAndExtensionForExport mockViewAndExt() {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        vae.setExtension(".txt");
        vae.setView("txt");
        return vae;
    }

    public List<ProjectCase> makeMockProjectCases() {
        List<ProjectCase> list = new LinkedList<ProjectCase>();
        list.add(new ProjectCase() {{
            setDisease("COAD");
            setOverallProgress("452/500");
            setMethylationCGCC("234/500");
            setCopyNumberSNPCGCC("560/500");
            setMicroRNAGSC("76/500");
            setMutationGSC("21/500");
        }});
        list.add(new ProjectCase() {{
            setDisease("GBM");
            setOverallProgress("453/500");
            setMethylationCGCC("235/500");
            setCopyNumberSNPCGCC("561/500");
            setMicroRNAGSC("77/500");
            setMutationGSC("22/500");
        }});
        list.add(new ProjectCase() {{
            setDisease("OV");
            setOverallProgress("454/500");
            setMethylationCGCC("236/500");
            setCopyNumberSNPCGCC("562/500");
            setMicroRNAGSC("78/500");
            setMutationGSC("23/500");
        }});
        return list;
    }

}//End of Class
