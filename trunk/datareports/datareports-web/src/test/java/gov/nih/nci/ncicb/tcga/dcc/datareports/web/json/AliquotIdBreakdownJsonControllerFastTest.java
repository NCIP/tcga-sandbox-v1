/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ALIQUOT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ANALYTE_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PARTICIPANT_ID;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SAMPLE_ID;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotIdBreakdown;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.AliquotIdBreakdownReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;

import java.lang.reflect.Field;
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
 * test class for the AliquotId breakdown json controller
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class AliquotIdBreakdownJsonControllerFastTest {

    private final Mockery context = new JUnit4Mockery();

    private AliquotIdBreakdownReportService service;

    private DatareportsService commonService;

    private AliquotIdBreakdownJsonController controller;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void before() throws Exception {

        service = context.mock(AliquotIdBreakdownReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new AliquotIdBreakdownJsonController();

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
    public void testAliquotIdBreakdownReportFullHandler() throws Exception {
        final List<AliquotIdBreakdown> mockBio = makeMockAliquotIdBreakdown();
        context.checking(new Expectations() {{
           allowing(service).getAliquotIdBreakdown();
           will(returnValue(mockBio));
           allowing(service).getFilteredAliquotIdBreakdownList(mockBio,null,null,null,null);
           will(returnValue(mockBio));
           allowing(service).getAliquotIdBreakdownComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockBio,null,null,null);
           will(returnValue(mockBio));
           allowing(commonService).getPaginatedList(mockBio,0,50);
           will(returnValue(mockBio));
           allowing(commonService).getTotalCount(mockBio);
           will(returnValue(3));
           allowing(commonService).processJsonSingleFilter(ALIQUOT_ID,"");
           will(returnValue(null));
           allowing(commonService).processJsonSingleFilter(ANALYTE_ID,"");
           will(returnValue(null));
           allowing(commonService).processJsonSingleFilter(SAMPLE_ID,"");
           will(returnValue(null));
           allowing(commonService).processJsonSingleFilter(PARTICIPANT_ID,"");
           will(returnValue(null));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.aliquotIdBreakdownReportFullHandler(model,0,50,null,null,"","");
        assertTrue(resMap != null);
        int totalCount = (Integer)model.get("totalCount");
        assertEquals(3,totalCount);
        List<AliquotIdBreakdown> json = (List<AliquotIdBreakdown>)model.get("aliquotIdBreakdownData");
        assertEquals(json.get(0).getAliquotId(), makeMockAliquotIdBreakdown().get(0).getAliquotId());
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
