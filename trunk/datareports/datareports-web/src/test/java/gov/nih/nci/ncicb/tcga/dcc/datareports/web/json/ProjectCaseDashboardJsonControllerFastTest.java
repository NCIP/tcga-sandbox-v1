/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_DATA;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ProjectCase;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.ProjectCaseDashboardService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

/**
 * Test class for the project case dashboard json controller
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class ProjectCaseDashboardJsonControllerFastTest {

    private final Mockery context = new JUnit4Mockery();

    private ProjectCaseDashboardService service;

    private DatareportsService commonService;

    private ProjectCaseDashboardJsonController controller;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;

    @Before
    public void before() throws Exception {

        service = context.mock(ProjectCaseDashboardService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new ProjectCaseDashboardJsonController();

        Field serviceControllerField = controller.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(controller, service);

        Field commonServiceControllerField = controller.getClass().getDeclaredField("commonService");
        commonServiceControllerField.setAccessible(true);
        commonServiceControllerField.set(controller, commonService);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
    }

    @Test
    public void testProjectCaseDashboardJsonHandler() throws Exception {
        final List<ProjectCase> mockPC = makeMockProjectCases();
        context.checking(new Expectations() {{
            allowing(service).getAllProjectCaseCounts();
            will(returnValue(mockPC));
            allowing(service).getFilteredProjectCaseList(mockPC, null);
            will(returnValue(mockPC));
            allowing(service).getProjectCaseComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockPC, null, null, null);
            will(returnValue(mockPC));
            allowing(commonService).getPaginatedList(mockPC, 0, 25);
            will(returnValue(mockPC));
            allowing(commonService).getTotalCount(mockPC);
            will(returnValue(3));
            allowing(commonService).processJsonMultipleFilter(DISEASE, "");
            will(returnValue(null));
        }});
        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.projectCaseDashboardJsonHandler(model, 0, 25, null, null, "", "");
        assertTrue(resMap != null);
        int totalCount = (Integer) model.get("totalCount");
        assertEquals(3, totalCount);
        List<ProjectCase> json = (List<ProjectCase>) model.get(PROJECT_CASE_DASHBOARD_DATA);
        assertEquals(json.get(0).getDisease(), makeMockProjectCases().get(0).getDisease());
    }

    @Test
    public void testFilterDataHandler() {
        context.checking(new Expectations() {{
            one(service).getProjectCaseFilterDistinctValues("disease");
            will(returnValue(mockExtJsFilter()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.filterDataHandler(model, "disease");
        assertTrue(resMap != null);
        List<ExtJsFilter> json = (List<ExtJsFilter>) resMap.get("diseaseData");
        assertEquals("GBM",json.get(0).getText());
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

    public List<ExtJsFilter> mockExtJsFilter() {
        List<ExtJsFilter> list = new ArrayList<ExtJsFilter>();
        ExtJsFilter mockExtJsFilter = new ExtJsFilter();
        mockExtJsFilter.setId("disease");
        mockExtJsFilter.setText("GBM");
        list.add(mockExtJsFilter);
        return list;
    }

}//End of Class
