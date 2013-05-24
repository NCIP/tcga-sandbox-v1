/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamTelemetry;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
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
import org.springframework.ui.ModelMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants.ANALYTE_CODE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants.LIBRARY_STRATEGY;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ALIQUOT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATA_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_FROM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_TO;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for the json controller of the bam telemetry report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class BamTelemetryJsonControllerFastTest {

    private final Mockery context = new JUnit4Mockery();

    private BamTelemetryReportService service;

    private DatareportsService commonService;

    private BamTelemetryJsonController controller;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void before() throws Exception {

        service = context.mock(BamTelemetryReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new BamTelemetryJsonController();

        //We use reflection to access the private field
        Field serviceControllerField = controller.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(controller, service);

        Field commonServiceControllerField = controller.getClass().getDeclaredField("commonService");
        commonServiceControllerField.setAccessible(true);
        commonServiceControllerField.set(controller, commonService);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testBamTelemetryReportFullHandler() throws Exception {
        final List<BamTelemetry> mockBamTelemetry = makeMockBamTelemetryRows();
        context.checking(new Expectations() {{
            allowing(service).getAllBamTelemetry();
            will(returnValue(mockBamTelemetry));
            allowing(service).getFilteredBamTelemetryList(mockBamTelemetry, null, null, null, null, null, null, null,
                    null, null);
            will(returnValue(mockBamTelemetry));
            allowing(service).getBamTelemetryComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockBamTelemetry, null, null, null);
            will(returnValue(mockBamTelemetry));
            allowing(commonService).getPaginatedList(mockBamTelemetry, 0, 50);
            will(returnValue(mockBamTelemetry));
            allowing(commonService).getTotalCount(mockBamTelemetry);
            will(returnValue(3));
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
            allowing(commonService).processJsonMultipleFilter(ANALYTE_CODE, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(LIBRARY_STRATEGY, "");
            will(returnValue(null));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.bamTelemetryReportFullHandler(model, 0, 50, null, null, "", "");
        assertTrue(resMap != null);
        int totalCount = (Integer) model.get("totalCount");
        assertEquals(3, totalCount);
        List<BamTelemetry> json = (List<BamTelemetry>) model.get("bamTelemetryData");
        assertEquals(json.get(0).getDisease(), makeMockBamTelemetryRows().get(0).getDisease());
    }

    @Test
    public void testFilterDataHandler() {
        context.checking(new Expectations() {{
            one(service).getBamTelemetryFilterDistinctValues("disease");
            will(returnValue(mockExtJsFilter()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.filterDataHandler(model, "disease");
        assertTrue(resMap != null);
        List<ExtJsFilter> json = (List<ExtJsFilter>) resMap.get("diseaseData");
        assertEquals(json.get(0).getText(), makeMockBamTelemetryRows().get(0).getDisease());
    }

    public List<ExtJsFilter> mockExtJsFilter() {
        List<ExtJsFilter> list = new ArrayList<ExtJsFilter>();
        ExtJsFilter mockExtJsFilter = new ExtJsFilter();
        mockExtJsFilter.setId("disease");
        mockExtJsFilter.setText("GBM");
        list.add(mockExtJsFilter);
        return list;
    }

    public List<BamTelemetry> makeMockBamTelemetryRows() {
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
