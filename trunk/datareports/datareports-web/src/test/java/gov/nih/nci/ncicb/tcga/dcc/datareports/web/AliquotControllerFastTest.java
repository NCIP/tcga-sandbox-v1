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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.AliquotReportService;
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

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.ALIQUOT_COLS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.LEVEL_ONE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.LEVEL_THREE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.LEVEL_TWO;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ALIQUOT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BCR_BATCH;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PLATFORM;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test of the biospecimen Controller
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class AliquotControllerFastTest {

    private final Mockery context = new JUnit4Mockery();
    public Map<String, String> mockMapCols = new LinkedHashMap<String, String>() {{
        put("disease", "Disease");
        put("center", "Center");
        put("platform", "Platform");
    }};
    private AliquotReportService service;
    private DatareportsService commonService;
    private AliquotController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    private ModelMap model;

    @Before
    public void before() throws Exception {

        service = context.mock(AliquotReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new AliquotController();

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
    public void testAliquotReportFullHandler() throws Exception {
        final List<Aliquot> mockbio = makeMockAliquot();
        context.checking(new Expectations() {{
            allowing(service).getAllAliquot();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("aliquot", mockbio, model, request);
        }});
        request.setMethod("GET");

        final String viewName = controller.aliquotReportFullHandler(model, session, request,
                null, null, null, null, null, null, null, null);
        assertTrue(viewName != null);
        assertEquals(AliquotReportConstants.ALIQUOT_REPORT_VIEW, viewName);
    }

    @Test
    public void testAliquotSimpleHandler() throws Exception {
        final List<Aliquot> mockbio = makeMockAliquot();
        context.checking(new Expectations() {{
            allowing(service).getAllAliquot();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("aliquot", mockbio, model, request);
        }});
        final String viewName = controller.aliquotReportSimpleHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("aliquotReport", viewName);
    }

    @Test
    public void testAliquotSimpleHandlerWithSessionFull() throws Exception {
        final List<Aliquot> mockbio = makeMockAliquot();
        context.checking(new Expectations() {{
            allowing(service).getAllAliquot();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("aliquot", mockbio, model, request);
        }});
        session.setAttribute("aliquotFilterModel", new ModelMap() {{
            put("flip", "flap");
        }});
        final String viewName = controller.aliquotReportSimpleHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("aliquotReport", viewName);
        assertTrue(model.get("flip") != null);
        assertEquals("flap", model.get("flip"));
    }

    @Test
    public void testAliquotReportExportHandler() throws Exception {
        final String sort = "";
        final String dir = "";
        final String columns = "disease,center,platform";
        final String filterReq = "";
        final String filterForm = "";
        final List<Aliquot> mockBio = makeMockAliquot();
        context.checking(new Expectations() {{
            allowing(service).getAllAliquot();
            will(returnValue(mockBio));
            allowing(service).getFilteredAliquotList(mockBio, null, null, null, null, null, null, null, null);
            will(returnValue(mockBio));
            allowing(service).getAliquotComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockBio, null, sort, dir);
            will(returnValue(mockBio));
            allowing(commonService).buildReportColumns(ALIQUOT_COLS, columns);
            will(returnValue(mockMapCols));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
            allowing(commonService).processJsonSingleFilter(ALIQUOT_ID, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(BCR_BATCH, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(DISEASE, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(PLATFORM, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(CENTER, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(LEVEL_ONE, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(LEVEL_TWO, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(LEVEL_THREE, "");
            will(returnValue(null));
        }});

        request.setMethod("GET");
        final String viewName = controller.aliquotExportHandler(model, "tab", sort, dir, columns,
                filterReq, filterForm);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals("aliquotReport", title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals("aliquotReport.txt", fileName);
        List<Aliquot> data = (List<Aliquot>) model.get("data");
        assertEquals(mockBio, data);
    }

    public ViewAndExtensionForExport mockViewAndExt() {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        vae.setExtension(".txt");
        vae.setView("txt");
        return vae;
    }

    public List<Aliquot> makeMockAliquot() {
        List<Aliquot> list = new LinkedList<Aliquot>();
        list.add(new Aliquot() {{
            setAliquotId("mockaliquot1");
            setBcrBatch("1");
            setDisease("GBM");
            setLevelOne("mockSubmitted");
            setLevelTwo("mockSubmitted");
            setLevelThree("mockSubmitted");
            setPlatform("mockplatform1");
            setCenter("mockcenter1");
        }});
        list.add(new Aliquot() {{
            setAliquotId("mockaliquot2");
            setBcrBatch("12");
            setDisease("OV");
            setLevelOne("mockMissing");
            setLevelTwo("mockMissing");
            setLevelThree("mockMissing");
            setPlatform("mockplatform2");
            setCenter("mockcenter2");
        }});
        list.add(new Aliquot() {{
            setAliquotId("mockaliquot3");
            setBcrBatch("23");
            setDisease("GBM");
            setLevelOne("mockSubmitted");
            setLevelTwo("mockSubmitted");
            setLevelThree("mockMissing");
            setPlatform("mockplatform3");
            setCenter("mockcenter3");
        }});
        return list;
    }


}//End of Class
