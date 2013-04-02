/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_FROM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_TO;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.ARCHIVE_TYPE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.LatestArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.LatestGenericReportService;

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
import org.springframework.ui.ModelMap;

/**
 * Test class for the latest archive report json controller
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith (JMock.class)
public class LatestArchiveJsonControllerFastTest {

    private final Mockery context = new JUnit4Mockery();

    private LatestGenericReportService service;

    private DatareportsService commonService;

    private LatestArchiveJsonController controller;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void before() throws Exception {

        service = context.mock(LatestGenericReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new LatestArchiveJsonController();

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
    public void testLatestArchiveReportFullHandlerTest() throws Exception {
        final List<LatestArchive> mockArch = makeMockLatestArchive();
        context.checking(new Expectations() {{
           allowing(service).getLatestArchive();
           will(returnValue(mockArch));
           allowing(service).getFilteredLatestArchiveList(mockArch,null,null,null);
           will(returnValue(mockArch));
           allowing(service).getLatestArchiveComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockArch,null,null,null);
           will(returnValue(mockArch));
           allowing(commonService).getPaginatedList(mockArch,0,50);
           will(returnValue(mockArch));
           allowing(commonService).getTotalCount(mockArch);
           will(returnValue(3));
           allowing(commonService).processJsonSingleFilter(DATE_FROM,"");
           will(returnValue(null));
           allowing(commonService).processJsonSingleFilter(DATE_TO,"");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter(ARCHIVE_TYPE,"");
           will(returnValue(null));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.latestArchiveReportFullHandler(model,0,50,null,null,"","");
        assertTrue(resMap != null);
        int totalCount = (Integer)model.get("totalCount");
        assertEquals(3,totalCount);
        List<LatestArchive> json = (List<LatestArchive>)model.get("latestArchiveData");
        assertEquals(json.get(0).getArchiveName(),makeMockLatestArchive().get(0).getArchiveName());
    }

    @Test
    public void testFilterDataHandler(){
        context.checking(new Expectations() {{
           one(service).getLatestArchiveFilterDistinctValues("archiveType");
           will(returnValue(mockExtJsFilter()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.filterDataHandler(model,"archiveType");
        assertTrue(resMap != null);
        List<ExtJsFilter> json = (List<ExtJsFilter>)resMap.get("archiveTypeData");
        assertEquals(json.get(0).getText(),makeMockLatestArchive().get(0).getArchiveType());
    }

    public List<ExtJsFilter> mockExtJsFilter(){
       List<ExtJsFilter> list = new ArrayList<ExtJsFilter>();
       ExtJsFilter mockExtJsFilter = new ExtJsFilter();
       mockExtJsFilter.setId("archiveType");
       mockExtJsFilter.setText("mockarchivetype1");
       list.add(mockExtJsFilter);
       return list;
    }

    public List<LatestArchive> makeMockLatestArchive() {
        List<LatestArchive> list = new LinkedList<LatestArchive>();
        list.add(new LatestArchive() {{
            setArchiveName("mockarchive1");
            setDateAdded(new java.util.Date(123456789));
            setArchiveUrl("mockarchiveurl1");
            setArchiveType("mockarchivetype1");
            setSdrfName("mocksdrf1");
            setSdrfUrl("mocksdrfurl1");
            setMafName("mockmaf1");
            setMafUrl("mockmafurl1");
        }});
        list.add(new LatestArchive() {{
            setArchiveName("mockarchive2");
            setDateAdded(new java.util.Date(123654789));
            setArchiveUrl("mockarchiveurl2");
            setArchiveType("mockarchivetype2");
            setSdrfName("mocksdrf2");
            setSdrfUrl("mocksdrfurl2");
            setMafName("mockmaf2");
            setMafUrl("mockmafurl2");
        }});
        list.add(new LatestArchive() {{
            setArchiveName("mockarchive3");
            setDateAdded(new java.util.Date(987654321));
            setArchiveUrl("mockarchiveurl3");
            setArchiveType("mockarchivetype3");
            setSdrfName("mocksdrf3");
            setSdrfUrl("mocksdrfurl3");
            setMafName("mockmaf3");
            setMafUrl("mockmafurl3");
        }});
        return list;
    }

}//End of Class
