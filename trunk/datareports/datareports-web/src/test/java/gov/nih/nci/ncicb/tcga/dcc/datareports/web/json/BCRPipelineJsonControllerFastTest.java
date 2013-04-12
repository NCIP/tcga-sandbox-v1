/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.GraphConfig;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.NodeData;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.Position;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.Total;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.BCRPipelineReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.CodeTablesReportService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for the pipelineJsonController
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class BCRPipelineJsonControllerFastTest {

    private final Mockery context = new JUnit4Mockery();
    private BCRPipelineReportService service;
    private CodeTablesReportService diseaseService;
    private BCRPipelineJsonController controller;

    @Before
    public void before() throws Exception {

        service = context.mock(BCRPipelineReportService.class);
        diseaseService = context.mock(CodeTablesReportService.class);
        controller = new BCRPipelineJsonController();

        Field serviceControllerField = controller.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(controller, service);

        Field diseaseServiceControllerField = controller.getClass().getDeclaredField("diseaseService");
        diseaseServiceControllerField.setAccessible(true);
        diseaseServiceControllerField.set(controller, diseaseService);
    }

    @Test
    public void testAllDisease() throws Exception {
        context.checking(new Expectations() {{
            allowing(diseaseService).getTumor();
            will(returnValue(makeMockTumor()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.allDisease(model);
        assertTrue(resMap != null);
        List<Tumor> json = (List<Tumor>) model.get("diseases");
        assertEquals(json.get(0).getTumorName(), makeMockTumor().get(0).getTumorName());
    }

    @Test
    public void testAllDatesFromJsonFiles() throws Exception {
        context.checking(new Expectations() {{
            allowing(service).getDatesFromInputFiles();
            will(returnValue(makeMockDatesFiles()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.allDatesFromJsonFiles(model);
        assertTrue(resMap != null);
        List<ExtJsFilter> json = (List<ExtJsFilter>) resMap.get("datesFromFile");
        assertEquals(json.get(0).getText(), makeMockDatesFiles().get(0).getText());
    }

    @Test
    public void testPipeLineReportData() throws Exception {
        context.checking(new Expectations() {{
            allowing(service).readBCRInputFiles("GBM", "08-2010");
            will(returnValue(1));
            allowing(service).getGraphConfigData();
            will(returnValue(new GraphConfig()));
            allowing(service).getNodeDataListData();
            will(returnValue(new LinkedList<NodeData>() {{
                add(new NodeData("name", "label", "image", "numericLabel", 1, 2));
            }}));
            allowing(service).getTotalData();
            will(returnValue(new Total(12, 21, new Position(1, 2), "fill", "Total")));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.pipeLineReportData(model, "GBM", "08-2010");
        assertTrue(resMap != null);
        List<NodeData> nList = (List<NodeData>) resMap.get("nodeData");
        Total tot = (Total) resMap.get("totals");
        assertEquals("name", nList.get(0).getName());
        assertEquals((Object) 12, tot.getTextSize());
    }

    @Test
    public void testPipeLineReportDataBad() throws Exception {
        context.checking(new Expectations() {{
            allowing(service).readBCRInputFiles("flu", "08-2010");
            will(returnValue(0));
            allowing(service).getGraphConfigData();
            will(returnValue(new GraphConfig()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.pipeLineReportData(model, "flu", "08-2010");
        assertTrue(resMap != null);
        NodeData n = (NodeData) resMap.get("nodeData");
        assertEquals("FailErrorFail", n.getName());
    }

    public List<Tumor> makeMockTumor() {
        List<Tumor> list = new LinkedList<Tumor>();
        list.add(new Tumor() {{
            setTumorName("GBM");
            setTumorDescription("description1");
        }});
        list.add(new Tumor() {{
            setTumorName("OV");
            setTumorDescription("description2");
        }});
        list.add(new Tumor() {{
            setTumorName("READ");
            setTumorDescription("description3");
        }});
        return list;
    }

    public List<ExtJsFilter> makeMockDatesFiles() {
        List<ExtJsFilter> list = new ArrayList<ExtJsFilter>();
        ExtJsFilter mockExtJsFilter = new ExtJsFilter();
        mockExtJsFilter.setId("08-2010");
        mockExtJsFilter.setText("08-2010");
        list.add(mockExtJsFilter);
        return list;
    }

    public List<ExtJsFilter> makeMockBCRFiles() {
        List<ExtJsFilter> list = new ArrayList<ExtJsFilter>();
        ExtJsFilter mockExtJsFilter = new ExtJsFilter();
        mockExtJsFilter.setId("igc");
        mockExtJsFilter.setText("igc");
        list.add(mockExtJsFilter);
        return list;
    }

}//End of Class
