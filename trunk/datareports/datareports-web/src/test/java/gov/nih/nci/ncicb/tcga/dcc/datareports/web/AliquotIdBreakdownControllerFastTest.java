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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotIdBreakdown;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotIdBreakdownReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.AliquotIdBreakdownReportService;
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

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ALIQUOT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ANALYTE_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PARTICIPANT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SAMPLE_ID;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for the AliquotId breakdown controller layer
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AliquotIdBreakdownControllerFastTest {

    private final Mockery context = new JUnit4Mockery();
    public Map<String, String> mockMapCols = new LinkedHashMap<String, String>() {{
        put("aliquotId", "Aliquot ID");
        put("analyteId", "Analyte ID");
        put("sampleId", "Sample ID");
    }};
    private AliquotIdBreakdownReportService service;
    private DatareportsService commonService;
    private AliquotIdBreakdownController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    private ModelMap model;

    @Before
    public void before() throws Exception {

        service = context.mock(AliquotIdBreakdownReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new AliquotIdBreakdownController();

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
    public void testAliquotIdBreakdownReportFullHandler() throws Exception {
        request.setMethod("GET");
        final List<AliquotIdBreakdown> mockbio = makeMockAliquotIdBreakdown();
        context.checking(new Expectations() {{
            allowing(service).getAliquotIdBreakdown();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("aliquotIdBreakdown", mockbio, model, request);
        }});
        final String viewName = controller.aliquotIdBreakdownReportFullHandler(model, session, request,
                null, null, null, null);
        assertTrue(viewName != null);
        assertEquals(AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_REPORT_VIEW, viewName);
    }

    @Test
    public void testAliquotIdBreakdownSimpleHandler() throws Exception {
        final List<AliquotIdBreakdown> mockbio = makeMockAliquotIdBreakdown();
        context.checking(new Expectations() {{
            allowing(service).getAliquotIdBreakdown();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("aliquotIdBreakdown", mockbio, model, request);
        }});
        final String viewName = controller.aliquotIdBreakdownReportSimpleHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("aliquotIdBreakdownReport", viewName);
    }

    @Test
    public void testAliquotIdBreakdownSimpleHandlerWithSessionFull() throws Exception {
        session.setAttribute("aliquotIdBreakdownFilterModel", new ModelMap() {{
            put("flip", "flap");
        }});
        final List<AliquotIdBreakdown> mockbio = makeMockAliquotIdBreakdown();
        context.checking(new Expectations() {{
            allowing(service).getAliquotIdBreakdown();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("aliquotIdBreakdown", mockbio, model, request);
        }});
        final String viewName = controller.aliquotIdBreakdownReportSimpleHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("aliquotIdBreakdownReport", viewName);
        assertTrue(model.get("flip") != null);
        assertEquals("flap", model.get("flip"));
    }

    @Test
    public void testAliquotIdBreakdownReportExportHandler() throws Exception {
        final String sort = "";
        final String dir = "";
        final String columns = "aliquotId,analyteId,sampleId";
        final String filterReq = "";
        final String filterForm = "";
        final List<AliquotIdBreakdown> mockBio = makeMockAliquotIdBreakdown();
        context.checking(new Expectations() {{
            allowing(service).getAliquotIdBreakdown();
            will(returnValue(mockBio));
            allowing(service).getFilteredAliquotIdBreakdownList(mockBio, null, null, null, null);
            will(returnValue(mockBio));
            allowing(service).getAliquotIdBreakdownComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockBio, null, sort, dir);
            will(returnValue(mockBio));
            allowing(commonService).buildReportColumns(AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_COLS, columns +
                    ",project,tissueSourceSite,participant,sampleType,vialId,portionId,portionAnalyte,plateId,centerId");
            will(returnValue(mockMapCols));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
            allowing(commonService).processJsonSingleFilter(ALIQUOT_ID, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(ANALYTE_ID, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(SAMPLE_ID, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(PARTICIPANT_ID, "");
            will(returnValue(null));
        }});

        request.setMethod("GET");
        final String viewName = controller.aliquotIdBreakdownExportHandler(model, "tab", sort, dir, columns,
                filterReq, filterForm);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals("aliquotIdBreakdownReport", title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals("aliquotIdBreakdownReport.txt", fileName);
        List<AliquotIdBreakdown> data = (List<AliquotIdBreakdown>) model.get("data");
        assertEquals(mockBio, data);
    }

    public ViewAndExtensionForExport mockViewAndExt() {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        vae.setExtension(".txt");
        vae.setView("txt");
        return vae;
    }

    public List<AliquotIdBreakdown> makeMockAliquotIdBreakdown() {
        List<AliquotIdBreakdown> list = new LinkedList<AliquotIdBreakdown>();
        list.add(new AliquotIdBreakdown() {{
            setAliquotId("mockaliquot1");
            setAnalyteId("mockanalyte1");
            setSampleId("mocksample1");
            setParticipantId("mockparticipant1");
        }});
        list.add(new AliquotIdBreakdown() {{
            setAliquotId("mockaliquot2");
            setAnalyteId("mockanalyte2");
            setSampleId("mocksample2");
            setParticipantId("mockparticipant2");
        }});
        list.add(new AliquotIdBreakdown() {{
            setAliquotId("mockaliquot3");
            setAnalyteId("mockanalyte3");
            setSampleId("mocksample3");
            setParticipantId("mockparticipant3");
        }});
        return list;
    }

}//End of Class
