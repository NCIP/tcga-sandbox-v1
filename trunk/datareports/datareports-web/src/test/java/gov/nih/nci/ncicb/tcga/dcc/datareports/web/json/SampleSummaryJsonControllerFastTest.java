/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.SampleSummaryReportService;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.ui.ModelMap;

/**
 * Test class for the sample summary json controller
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class SampleSummaryJsonControllerFastTest {

    private final Mockery context = new JUnit4Mockery();

    private SampleSummaryReportService service;

    private DatareportsService commonService;

    private SampleSummaryJsonController controller;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void before() throws Exception {

        service = context.mock(SampleSummaryReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new SampleSummaryJsonController();

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
    public void testSampleSummaryReportFullHandler() throws Exception {
        final String columns = "tumor,center,platform";

        final List<SampleSummary> mockSamples = makeMockSampleSummary();
        context.checking(new Expectations() {{
           allowing(service).processSampleSummary(null);
           will(returnValue(mockSamples));
           allowing(service).getFilteredSampleSummaryList(mockSamples,null,null,null,null,null);
           will(returnValue(mockSamples));
           allowing(service).getSampleSummaryComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockSamples,null,null,null);
           will(returnValue(mockSamples));
           allowing(commonService).getPaginatedList(mockSamples,0,50);
           will(returnValue(mockSamples));
           allowing(commonService).getTotalCount(mockSamples);
           will(returnValue(1));
           allowing(commonService).processJsonMultipleFilter("disease","");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter("center","");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter("portionAnalyte","");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter("platform","");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter("levelFourSubmitted","");
           will(returnValue(null));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.sampleSummaryReportFullHandler(model,0,50,null,null,null,"","");
        assertTrue(resMap != null);
        int totalCount = (Integer)resMap.get("totalCount");
        assertEquals(1,totalCount);
        List<SampleSummary> json = (List<SampleSummary>)resMap.get("sampleSummaryData");
        assertEquals(json.get(0).getDisease(),makeMockSampleSummary().get(0).getDisease());
    }

    @Test
    public void testFilterDataHandler(){
        context.checking(new Expectations() {{
           one(service).getSampleSummaryFilterDistinctValues("disease");
           will(returnValue(mockExtJsFilter()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.filterDataHandler(model,"disease");
        List<ExtJsFilter> json = (List<ExtJsFilter>)resMap.get("diseaseData");
        assertEquals(json.get(0).getText(),makeMockSampleSummary().get(0).getDisease());
    }

    public List<ExtJsFilter> mockExtJsFilter(){
       List<ExtJsFilter> list = new ArrayList<ExtJsFilter>();
       ExtJsFilter mockExtJsFilter = new ExtJsFilter();
       mockExtJsFilter.setId("disease");
       mockExtJsFilter.setText("GBM");
       list.add(mockExtJsFilter);
       return list;
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


}//End of Class
