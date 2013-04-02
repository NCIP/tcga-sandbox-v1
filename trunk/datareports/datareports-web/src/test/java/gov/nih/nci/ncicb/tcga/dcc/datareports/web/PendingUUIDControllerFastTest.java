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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.PendingUUIDReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.PendingUUIDReportService;
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

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BATCH;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BCR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PLATE_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.PendingUUIDReportConstants.PENDING_UUID_REPORT_COLS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class PendingUUIDControllerFastTest {

    private final Mockery context = new JUnit4Mockery();
    public Map<String, String> mockMapCols = new LinkedHashMap<String, String>() {{
        put("bcr", "BCR");
    }};
    private PendingUUIDReportService service;
    private DatareportsService commonService;
    private PendingUUIDController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    private ModelMap model;

    @Before
    public void before() throws Exception {

        service = context.mock(PendingUUIDReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new PendingUUIDController();

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
    public void testPendingUUIDReportFullHandler() throws Exception {
        request.setMethod("GET");
        final List<PendingUUID> mockbio = makeMockPendingUUID();
        context.checking(new Expectations() {{
            allowing(service).getAllPendingUUIDs();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("pendingUUID", mockbio, model, request);
        }});
        final String viewName = controller.pendingUUIDReportFullHandler(model, session, request, null,
                null, null, null);
        assertTrue(viewName != null);
        assertEquals(PendingUUIDReportConstants.PENDING_UUID_REPORT_VIEW, viewName);
    }

    @Test
    public void testPendingUUIDReportSimpleHandler() throws Exception {
        final List<PendingUUID> mockbio = makeMockPendingUUID();
        context.checking(new Expectations() {{
            allowing(service).getAllPendingUUIDs();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("pendingUUID", mockbio, model, request);
        }});
        final String viewName = controller.pendingUUIDReportHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("shipped-items-pending-bcr-data-submission", viewName);
    }

    @Test
    public void testPendingUUIDReportSimpleHandlerWithSessionFull() throws Exception {
        session.setAttribute("pendingUUIDFilterModel", new ModelMap() {{
            put("flip", "flap");
        }});
        final List<PendingUUID> mockbio = makeMockPendingUUID();
        context.checking(new Expectations() {{
            allowing(service).getAllPendingUUIDs();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("pendingUUID", mockbio, model, request);
        }});
        final String viewName = controller.pendingUUIDReportHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("shipped-items-pending-bcr-data-submission", viewName);
        assertTrue(model.get("flip") != null);
        assertEquals("flap", model.get("flip"));
    }

    @Test
    public void testPendingUUIDReportExportHandler() throws Exception {
        final String sort = "";
        final String dir = "";
        final String columns = "bcr";
        final String filterReq = "";
        final String filterForm = "";
        final List<PendingUUID> mockPUUID = makeMockPendingUUID();
        context.checking(new Expectations() {{
            allowing(service).getAllPendingUUIDs();
            will(returnValue(mockPUUID));
            allowing(service).getFilteredPendingUUIDList(mockPUUID, null, null, null, null);
            will(returnValue(mockPUUID));
            allowing(service).getPendingUUIDComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockPUUID, null, sort, dir);
            will(returnValue(mockPUUID));
            allowing(commonService).buildReportColumns(PENDING_UUID_REPORT_COLS, columns);
            will(returnValue(mockMapCols));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
            allowing(commonService).processJsonMultipleFilter(BCR, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(CENTER, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(BATCH, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(PLATE_ID, "");
            will(returnValue(null));
        }});

        request.setMethod("GET");
        final String viewName = controller.pendingUUIDReportExportHandler(model, "tab", sort, dir, columns,
                filterReq, filterForm);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals("shipped-items-pending-bcr-data-submission", title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals("shipped-items-pending-bcr-data-submission.txt", fileName);
        List<PendingUUID> data = (List<PendingUUID>) model.get("data");
        assertEquals(mockPUUID, data);
    }

    public ViewAndExtensionForExport mockViewAndExt() {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        vae.setExtension(".txt");
        vae.setView("txt");
        return vae;
    }

    public List<PendingUUID> makeMockPendingUUID() {
        List<PendingUUID> list = new LinkedList<PendingUUID>();
        list.add(new PendingUUID() {{
            setBcr("IGC");
            setBcrAliquotBarcode("barcode1");
            setUuid("uuid1");
        }});
        list.add(new PendingUUID() {{
            setBcr("IGC");
            setBcrAliquotBarcode("barcode2");
            setUuid("uuid2");
        }});
        list.add(new PendingUUID() {{
            setBcr("NCH");
            setBcrAliquotBarcode("barcode3");
            setUuid("uuid3");
        }});
        return list;
    }
}
