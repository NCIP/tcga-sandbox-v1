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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sample;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.SampleSummaryReportService;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.LAST_REFRESH;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.SAMPLE_BCR_COLS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.SAMPLE_SUMMARY_COLS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Analyze the ModelAndView the controller returns to see if it is correct
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
@RunWith(JMock.class)
public class SampleSummaryControllerFastTest {

    private final Mockery context = new JUnit4Mockery();
    public Map<String, String> mockMapCols = new LinkedHashMap<String, String>() {{
        put("disease", "Cancer Type");
        put("center", "Center");
        put("platform", "Platform");
    }};
    private SampleSummaryReportService service;
    private DatareportsService commonService;
    private SampleSummaryController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    private ModelMap model;

    @Before
    public void before() throws Exception {

        service = context.mock(SampleSummaryReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new SampleSummaryController();

        //We use reflection to access the private field
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
    public void testSampleSummaryFullHandler() throws Exception {
        request.setMethod("GET");
        final List<SampleSummary> mockbio = makeMockSampleSummary();
        context.checking(new Expectations() {{
            allowing(service).getSampleSummaryReport();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("sampleSummary", mockbio, model, request);
            allowing(service).getLatest(mockbio);
            will(returnValue(new Timestamp(new Date().getTime())));
        }});
        final String viewName = controller.sampleSummaryFullHandler(model, session, request, null, null, null, null, null, null);
        assertTrue(viewName != null);
        assertEquals(SampleSummaryReportConstants.SAMPLE_SUMMARY_REPORT_VIEW, viewName);
        Timestamp lastRefresh = (Timestamp) model.get(LAST_REFRESH);
        assertNotNull(lastRefresh);
    }

    @Test
    public void testSampleSummarySimpleHandler() throws Exception {
        final List<SampleSummary> mockbio = makeMockSampleSummary();
        context.checking(new Expectations() {{
            allowing(service).getSampleSummaryReport();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("sampleSummary", mockbio, model, request);
            allowing(service).getLatest(mockbio);
            will(returnValue(new Timestamp(new Date().getTime())));
        }});
        final String viewName = controller.sampleSummarySimpleHandler(model, session, request, null);
        assertTrue(viewName != null);
        assertEquals("sampleSummaryReport", viewName);
    }

    @Test
    public void testSampleSummarySimpleHandlerWithSessionFull() throws Exception {
        final List<SampleSummary> mockbio = makeMockSampleSummary();
        context.checking(new Expectations() {{
            allowing(service).getSampleSummaryReport();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("sampleSummary", mockbio, model, request);
            allowing(service).getLatest(mockbio);
            will(returnValue(new Timestamp(new Date().getTime())));
        }});
        session.setAttribute("sampleSummaryFilterModel", new ModelMap() {{
            put("tic", "tac");
        }});
        final String viewName = controller.sampleSummarySimpleHandler(model, session, request, null);
        assertTrue(viewName != null);
        assertEquals("sampleSummaryReport", viewName);
        assertTrue(model.get("tic") != null);
        assertEquals("tac", model.get("tic"));
    }

    @Test
    public void testSampleSummaryComingFromEmailHandler() throws Exception {
        final List<SampleSummary> mockSamples = makeMockSampleSummary();
        context.checking(new Expectations() {{
            allowing(service).getSampleSummaryReport();
            will(returnValue(mockSamples));
            allowing(service).getLatest(mockSamples);
            will(returnValue(new Timestamp(new Date().getTime())));
        }});
        final String viewName = controller.sampleSummaryComingFromEmailHandler(model, session,
                null, null, null, null, null, false, null);
        assertTrue(viewName != null);
        assertEquals("forward:sampleSummaryReportTmp.htm", viewName);
    }

    @Test
    public void testSampleExportHandler() throws Exception {
        final String sort = "";
        final String dir = "";
        final String columns = "name,sampleDate";
        final String colId = "totalBCRSent";
        final List<Sample> mockSamples = makeMockSampleList();
        final List<SampleSummary> mockSampleSummary = makeMockSampleSummary();
        context.checking(new Expectations() {{
            allowing(service).getSampleSummaryReport();
            will(returnValue(mockSampleSummary));
            allowing(service).findSampleSummary(mockSampleSummary, null, null, null, null);
            will(returnValue(mockSampleSummary.get(0)));
            allowing(service).getDrillDown(mockSampleSummary.get(0), colId);
            will(returnValue(mockSamples));
            allowing(service).getSampleComparator(true);
            will(returnValue(null));
            allowing(commonService).getSortedList(mockSamples, null, sort, dir);
            will(returnValue(mockSamples));
            allowing(commonService).buildReportColumns(SAMPLE_BCR_COLS, columns);
            will(returnValue(new LinkedHashMap<String, String>() {{
                put("name", "Sample");
                put("sampleDate", "Ship Date");
            }}));
            allowing(commonService).getViewAndExtForExport("xl");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        final String viewName = controller.sampleDetailedExportHandler(model, "xl", sort, dir, null, null, null, null,
                true, colId);
        assertTrue(viewName != null);
        assertEquals("xl", viewName);
        String title = (String) model.get("title");
        assertEquals("sampleDetailedReport", title);
        String exportType = (String) model.get("exportType");
        assertEquals("xl", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals("sampleDetailedReport.xls", fileName);
        List<Sample> data = (List<Sample>) model.get("data");
        assertEquals(mockSamples, data);
    }

    @Test
    public void testSampleSummaryExportHandler() throws Exception {
        final String sort = "";
        final String dir = "";
        final String columns = "disease,center,platform";
        final String filterReq = "";
        final String filterForm = "";

        final List<SampleSummary> mockSamples = makeMockSampleSummary();
        context.checking(new Expectations() {{
            allowing(service).processSampleSummary(null);
            will(returnValue(mockSamples));
            allowing(service).getFilteredSampleSummaryList(mockSamples, null, null, null, null, null);
            will(returnValue(mockSamples));
            allowing(service).getSampleSummaryComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockSamples, null, sort, dir);
            will(returnValue(mockSamples));
            allowing(commonService).buildReportColumns(SAMPLE_SUMMARY_COLS, columns);
            will(returnValue(mockMapCols));
            allowing(commonService).getViewAndExtForExport("xl");
            will(returnValue(mockViewAndExt()));
            allowing(commonService).processJsonMultipleFilter("disease", "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter("center", "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter("portionAnalyte", "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter("platform", "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter("levelFourSubmitted", "");
            will(returnValue(null));
        }});

        request.setMethod("GET");
        final String viewName = controller.sampleSummaryExportHandler(model, "xl", sort, dir, columns,
                null, filterReq, filterForm);
        assertTrue(viewName != null);
        assertEquals("xl", viewName);
        String title = (String) model.get("title");
        assertEquals("sampleSummaryReport", title);
        String exportType = (String) model.get("exportType");
        assertEquals("xl", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals("sampleSummaryReport.xls", fileName);
        List<SampleSummary> data = (List<SampleSummary>) model.get("data");
        assertEquals(mockSamples, data);
    }

    public List<SampleSummary> makeMockSampleSummary() {
        List<SampleSummary> list = new ArrayList<SampleSummary>();
        SampleSummary mockSummary = new SampleSummary();
        mockSummary.setDisease("GBM");
        mockSummary.setCenterType("DOPE");
        mockSummary.setCenterName("www.rpi.edu");
        mockSummary.setPortionAnalyte("D");
        mockSummary.setPlatform("Super Nintendo");
        mockSummary.setTotalBCRSent(101L);
        mockSummary.setTotalCenterSent(202L);
        mockSummary.setTotalBCRUnaccountedFor(101L);
        mockSummary.setTotalCenterUnaccountedFor(0L);
        mockSummary.setTotalLevelOne(42L);
        mockSummary.setTotalLevelTwo(42L);
        mockSummary.setTotalLevelThree(42L);
        mockSummary.setLevelFourSubmitted("Y*");
        mockSummary.setLastRefresh(new Timestamp(new Date().getTime()));
        list.add(mockSummary);

        return list;
    }

    public List<Sample> makeMockSampleList() {
        List<Sample> sampleList = new LinkedList<Sample>();
        Sample s = new Sample();
        s.setName("testouille");
        sampleList.add(s);
        sampleList.add(new Sample("bouhboubh"));
        sampleList.add(new Sample("Yo Domi, wassup dude !"));
        sampleList.add(new Sample("La vie est belle"));
        sampleList.add(new Sample("et vous etes commem elle"));
        return sampleList;
    }

    public ViewAndExtensionForExport mockViewAndExt() {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        vae.setExtension(".xls");
        vae.setView("xl");
        return vae;
    }


}//End of Class
