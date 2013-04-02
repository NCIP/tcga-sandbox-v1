/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.AliquotReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;

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
 * Test class for the biospecimen json controller
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class AliquotJsonControllerFastTest {

    private final Mockery context = new JUnit4Mockery();

    private AliquotReportService service;

    private DatareportsService commonService;

    private AliquotJsonController controller;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void before() throws Exception {

        service = context.mock(AliquotReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new AliquotJsonController();

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
    public void testAliquotReportFullHandler() throws Exception {
        final List<Aliquot> mockBio = makeMockAliquot();
        context.checking(new Expectations() {{
           allowing(service).getAllAliquot();
           will(returnValue(mockBio));
           allowing(service).getFilteredAliquotList(mockBio,null,null,null,null,null,null,null,null);
           will(returnValue(mockBio));
           allowing(service).getAliquotComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockBio,null,null,null);
           will(returnValue(mockBio));
           allowing(commonService).getPaginatedList(mockBio,0,50);
           will(returnValue(mockBio));
           allowing(commonService).getTotalCount(mockBio);
           will(returnValue(3));
           allowing(commonService).processJsonSingleFilter(ALIQUOT_ID,"");
           will(returnValue(null));
           allowing(commonService).processJsonSingleFilter(BCR_BATCH,"");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter(DISEASE,"");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter(PLATFORM,"");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter(CENTER,"");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter(LEVEL_ONE,"");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter(LEVEL_TWO,"");
           will(returnValue(null));
           allowing(commonService).processJsonMultipleFilter(LEVEL_THREE,"");
           will(returnValue(null));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.aliquotReportFullHandler(model,0,50,null,null,"","");
        assertTrue(resMap != null);
        int totalCount = (Integer)model.get("totalCount");
        assertEquals(3,totalCount);
        List<Aliquot> json = (List<Aliquot>)model.get("aliquotData");
        assertEquals(json.get(0).getDisease(),makeMockAliquot().get(0).getDisease());
    }

    @Test
    public void testAliquotArchiveFullHandler(){
        context.checking(new Expectations() {{
           one(service).getAllAliquotArchive("mockAliquot",2);
           will(returnValue(mockAliquotArchive()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.aliquotArchiveFullHandler(model,"mockAliquot",2);
        assertTrue(resMap != null);
        List<AliquotArchive> json = (List<AliquotArchive>)resMap.get("aliquotArchiveData");
        assertEquals(json.get(0).getFileName(),mockAliquotArchive().get(0).getFileName());
        assertEquals("bouh",json.get(0).getArchiveName());
    }

    @Test
    public void testFilterDataHandler(){
        context.checking(new Expectations() {{
           one(service).getAliquotFilterDistinctValues("disease");
           will(returnValue(mockExtJsFilter()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.filterDataHandler(model,"disease");
        assertTrue(resMap != null);
        List<ExtJsFilter> json = (List<ExtJsFilter>)resMap.get("diseaseData");
        assertEquals(json.get(0).getText(),makeMockAliquot().get(0).getDisease());
    }

    public List<ExtJsFilter> mockExtJsFilter(){
       List<ExtJsFilter> list = new ArrayList<ExtJsFilter>();
       ExtJsFilter mockExtJsFilter = new ExtJsFilter();
       mockExtJsFilter.setId("disease");
       mockExtJsFilter.setText("GBM");
       list.add(mockExtJsFilter);
       return list;
    }

    public List<AliquotArchive> mockAliquotArchive(){
       List<AliquotArchive> list = new ArrayList<AliquotArchive>();
       AliquotArchive arc = new AliquotArchive();
       arc.setFileId(2);
       arc.setFileName("bah");
       arc.setFileUrl("bah.htm");
       arc.setArchiveId(1);
       arc.setArchiveName("bouh");
       list.add(arc);
       return list;
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
