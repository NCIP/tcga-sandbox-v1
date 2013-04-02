/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
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
import org.springframework.ui.ModelMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BATCH;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BCR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PLATE_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.PendingUUIDReportConstants.PENDING_UUID_REPORT_DATA;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class PendingUUIDJsonControllerFastTest {

    private final Mockery context = new JUnit4Mockery();

    private PendingUUIDReportService service;

    private DatareportsService commonService;

    private PendingUUIDJsonController controller;

    private MockHttpServletRequest request;

    @Before
    public void before() throws Exception {

        service = context.mock(PendingUUIDReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new PendingUUIDJsonController();

        Field serviceControllerField = controller.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(controller, service);

        Field commonServiceControllerField = controller.getClass().getDeclaredField("commonService");
        commonServiceControllerField.setAccessible(true);
        commonServiceControllerField.set(controller, commonService);

        request = new MockHttpServletRequest();
    }

    @Test
    public void testPendingUUIDReportJsonHandler() throws Exception {
        final List<PendingUUID> mockPUUID = makeMockPendingUUID();
        context.checking(new Expectations() {{
            allowing(service).getAllPendingUUIDs();
            will(returnValue(mockPUUID));
            allowing(service).getFilteredPendingUUIDList(mockPUUID, null, null, null, null);
            will(returnValue(mockPUUID));
            allowing(service).getPendingUUIDComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockPUUID, null, null, null);
            will(returnValue(mockPUUID));
            allowing(commonService).getPaginatedList(mockPUUID, 0, 25);
            will(returnValue(mockPUUID));
            allowing(commonService).getTotalCount(mockPUUID);
            will(returnValue(3));
            allowing(commonService).processJsonMultipleFilter(BCR, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(CENTER, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(BATCH, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(PLATE_ID, "");
            will(returnValue(null));
        }});
        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.pendingUUIDReportJsonHandler(model, 0, 25, null, null, "", "");
        assertTrue(resMap != null);
        int totalCount = (Integer) model.get("totalCount");
        assertEquals(3, totalCount);
        List<PendingUUID> json = (List<PendingUUID>) model.get(PENDING_UUID_REPORT_DATA);
        assertEquals(json.get(0).getBcr(), makeMockPendingUUID().get(0).getBcr());
    }

    @Test
    public void testFilterDataHandler() {
        context.checking(new Expectations() {{
            one(service).getPendingUUIDFilterDistinctValues("bcr");
            will(returnValue(mockExtJsFilter()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.filterDataHandler(model, "bcr");
        assertTrue(resMap != null);
        List<ExtJsFilter> json = (List<ExtJsFilter>) resMap.get("bcrData");
        assertEquals("IGC", json.get(0).getText());
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

    public List<ExtJsFilter> mockExtJsFilter() {
        List<ExtJsFilter> list = new ArrayList<ExtJsFilter>();
        ExtJsFilter mockExtJsFilter = new ExtJsFilter();
        mockExtJsFilter.setId("bcr");
        mockExtJsFilter.setText("IGC");
        list.add(mockExtJsFilter);
        return list;
    }
}
