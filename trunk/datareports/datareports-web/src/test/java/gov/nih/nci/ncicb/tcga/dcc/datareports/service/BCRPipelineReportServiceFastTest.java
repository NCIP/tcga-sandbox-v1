/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BCRJson;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.GraphConfig;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.NodeData;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.Output;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.Total;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.TumorTypes;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Test class for the bcrpipeline report service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class BCRPipelineReportServiceFastTest {

    private BCRPipelineReportServiceImpl service;
    protected static final String JSON_PATH =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    @Before
    public void setup() {
        service = new BCRPipelineReportServiceImpl();
        DatareportsProperties props = new DatareportsProperties();
        props.setPipelineReportJsonFilesPath(JSON_PATH);
        BCRJson bcr = new BCRJson();
        bcr.setDq_genotype(1);
        bcr.setDq_init_screen(2);
        bcr.setDq_mol(3);
        bcr.setDq_other(4);
        bcr.setDq_path(5);
        bcr.setPending_init_screen(6);
        bcr.setPending_mol_qc(7);
        bcr.setPending_path_qc(8);
        bcr.setPending_shipment(9);
        bcr.setQual_mol(10);
        bcr.setQual_path(11);
        bcr.setReceived(12);
        bcr.setShipped(13);
        bcr.setSubmitted_to_bcr(14);
        bcr.setQualified_hold(1);
        service.setBcrJson(bcr);
    }

    @Test
    public void getNodeDataListDataTest() {
        List<NodeData> ndList = service.getNodeDataListData();
        assertNotNull(ndList);
        assertEquals(5, ndList.size());
        assertEquals("bcr", ndList.get(0).getName());
        assertEquals("Genotype/Final Review", ndList.get(3).getLabel());
        assertEquals(new Integer(2), ndList.get(0).getOutputs().get(0).getCount());
        assertEquals(new Integer(14), ndList.get(0).getOutputs().get(1).getCount());
        assertEquals(new Integer(6), ndList.get(0).getOutputs().get(2).getCount());
        assertEquals("Pending BCR Initial Screening", ndList.get(0).getOutputs().get(2).getLabel());
    }

    @Test
    public void getGraphConfigDataTest() {
        GraphConfig gc = service.getGraphConfigData();
        assertNotNull(gc);
        assertEquals("raphgraph", gc.getRenderTo());
        assertEquals((Object) 187, gc.getPathHeight());
    }

    @Test
    public void getTotalDataTest() {
        Total tot = service.getTotalData();
        assertNotNull(tot);
        assertEquals("#d8eff6", tot.getFill());
    }

    @Test
    public void getTumorTypesDataTest() {
        TumorTypes tt = service.getTumorTypesData();
        assertNotNull(tt);
        assertEquals("#d8eff6", tt.getFill());
    }

    @Test
    public void addToOutputList() {
        List<Output> list = new LinkedList<Output>();
        Output outGood1 = new Output(1, "Un", "red", "up");
        Output outGood2 = new Output(0, "Zero", "red", "up");
        Output outBad = new Output(2, "Deux", "red", "up");
        service.addToOutputList(list, outGood1);
        service.addToOutputList(list, outBad);
        service.addToOutputList(list, outGood2);
        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    public void getDatesFromInputFiles() {
        List<ExtJsFilter> res = service.getDatesFromInputFiles();
        assertNotNull(res);
        assertEquals(5, res.size());
        assertEquals("03-2013", res.get(0).getId());
        assertEquals("Mar-2013", res.get(0).getText());
        assertEquals("01-2012", res.get(1).getId());
        assertEquals("Jan-2012", res.get(1).getText());
        assertEquals("12-2011", res.get(2).getId());
        assertEquals("Dec-2011", res.get(2).getText());
    }

    @Test
    public void readInputFiles() {
        assertEquals(0, service.readBCRInputFiles("blahblah", "01-2011"));
        assertEquals(1, service.readBCRInputFiles("COAD", "01-2011"));
        assertEquals(1, service.readBCRInputFiles("All", "01-2011"));
        BCRJson bcr = service.getBcrJson();
        assertNotNull(bcr);
        assertEquals("All", bcr.getDisease());
        assertEquals(new Integer(21), bcr.getDq_genotype());
    }

}//End of Class
