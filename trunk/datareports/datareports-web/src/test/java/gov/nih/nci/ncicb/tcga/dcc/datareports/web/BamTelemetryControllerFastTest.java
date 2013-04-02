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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BamTelemetry;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.BamTelemetryReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
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

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants.BAM_TELEMETRY_COLS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ALIQUOT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATA_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_FROM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_TO;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.MOLECULE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test for the controller of the bam telemetry report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class BamTelemetryControllerFastTest {

    private final Mockery context = new JUnit4Mockery();
    public Map<String, String> mockMapCols = new LinkedHashMap<String, String>() {{
        put("disease", "Disease");
        put("center", "Center");
    }};
    private BamTelemetryReportService service;
    private DatareportsService commonService;
    private BamTelemetryController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    private ModelMap model;

    @Before
    public void before() throws Exception {

        service = context.mock(BamTelemetryReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new BamTelemetryController();

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
    public void testBamTelemetryReportFullHandler() throws Exception {
        request.setMethod("GET");
        final List<BamTelemetry> mockbio = makeMockBamTelemetry();
        context.checking(new Expectations() {{
            allowing(service).getAllBamTelemetry();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("bamTelemetry", mockbio, model, request);
        }});
        final String viewName = controller.bamTelemetryReportFullHandler(model, session, request,
                null, null, null, null, null, null, null, null);
        assertTrue(viewName != null);
        assertEquals(BamTelemetryReportConstants.BAM_TELEMETRY_REPORT_VIEW, viewName);
    }

    @Test
    public void testBamTelemetrySimpleHandler() throws Exception {
        final List<BamTelemetry> mockbio = makeMockBamTelemetry();
        context.checking(new Expectations() {{
            allowing(service).getAllBamTelemetry();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("bamTelemetry", mockbio, model, request);
        }});
        final String viewName = controller.bamTelemetryReportSimpleHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("bamTelemetryReport", viewName);
    }

    @Test
    public void testBamTelemetrySimpleHandlerWithSessionFull() throws Exception {
        session.setAttribute("bamTelemetryFilterModel", new ModelMap() {{
            put("flip", "flap");
        }});
        final List<BamTelemetry> mockbio = makeMockBamTelemetry();
        context.checking(new Expectations() {{
            allowing(service).getAllBamTelemetry();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("bamTelemetry", mockbio, model, request);
        }});
        final String viewName = controller.bamTelemetryReportSimpleHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("bamTelemetryReport", viewName);
        assertTrue(model.get("flip") != null);
        assertEquals("flap", model.get("flip"));
    }

    @Test
    public void testBamTelemetryReportExportHandler() throws Exception {
        final String sort = "";
        final String dir = "";
        final String columns = "disease,center";
        final String filterReq = "";
        final String filterForm = "";
        final List<BamTelemetry> mockBamTelemetry = makeMockBamTelemetry();
        context.checking(new Expectations() {{
            allowing(service).getAllBamTelemetry();
            will(returnValue(mockBamTelemetry));
            allowing(service).getFilteredBamTelemetryList(mockBamTelemetry, null, null, null, null, null, null, null, null);
            will(returnValue(mockBamTelemetry));
            allowing(service).getBamTelemetryComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockBamTelemetry, null, sort, dir);
            will(returnValue(mockBamTelemetry));
            allowing(commonService).buildReportColumns(BAM_TELEMETRY_COLS, columns);
            will(returnValue(mockMapCols));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
            allowing(commonService).processJsonSingleFilter(ALIQUOT_ID, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(DATE_FROM, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(DATE_TO, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(DISEASE, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(CENTER, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(DATA_TYPE, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(MOLECULE, "");
            will(returnValue(null));
        }});

        request.setMethod("GET");
        final String viewName = controller.bamTelemetryExportHandler(model, "tab", sort, dir, columns,
                filterReq, filterForm);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals("bamTelemetryReport", title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals("bamTelemetryReport.txt", fileName);
        List<BamTelemetry> data = (List<BamTelemetry>) model.get("data");
        assertEquals(mockBamTelemetry, data);
    }

    public ViewAndExtensionForExport mockViewAndExt() {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        vae.setExtension(".txt");
        vae.setView("txt");
        return vae;
    }

    public List<BamTelemetry> makeMockBamTelemetry() {
        List<BamTelemetry> list = new LinkedList<BamTelemetry>();
        list.add(new BamTelemetry() {{
            setAliquotId("mockaliquot1");
            setDisease("GBM");
            setCenter("mockcenter1");
        }});
        list.add(new BamTelemetry() {{
            setAliquotId("mockaliquot2");
            setDisease("OV");
            setCenter("mockcenter2");
        }});
        list.add(new BamTelemetry() {{
            setAliquotId("mockaliquot3");
            setDisease("GBM");
            setCenter("mockcenter3");
        }});
        return list;
    }


}//End of Class
