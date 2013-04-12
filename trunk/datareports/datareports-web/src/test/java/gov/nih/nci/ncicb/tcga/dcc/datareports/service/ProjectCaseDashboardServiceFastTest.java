/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BCRJson;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ProjectCase;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.ProjectCaseDashboardDAO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the project case dashboard service class
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class ProjectCaseDashboardServiceFastTest {

    private Mockery context;

    private ProjectCaseDashboardDAO dao;

    private ProjectCaseDashboardServiceImpl service;

    private DatareportsServiceImpl commonService;

    private CodeTablesReportService codeTablesReportService;

    protected static final String JSON_PATH =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        dao = context.mock(ProjectCaseDashboardDAO.class);
        service = new ProjectCaseDashboardServiceImpl();
        commonService = new DatareportsServiceImpl();
        codeTablesReportService = context.mock(CodeTablesReportService.class);
        Field daoServiceField = service.getClass().getDeclaredField("daoImpl");
        Field commonServiceField = service.getClass().getDeclaredField("commonService");
        Field codeTablesReportServiceField = service.getClass().getDeclaredField("codeTablesReportService");
        daoServiceField.setAccessible(true);
        commonServiceField.setAccessible(true);
        codeTablesReportServiceField.setAccessible(true);
        daoServiceField.set(service, dao);
        commonServiceField.set(service, commonService);
        codeTablesReportServiceField.set(service, codeTablesReportService);
    }

    @Test
    public void testGetBCRJson() throws Exception {
        final List<BCRJson> bcrJsonList = service.getBCRJson(JSON_PATH + "IGC-BCR_01-2011.json");
        assertNotNull(bcrJsonList);
        assertEquals(12, bcrJsonList.size());
        assertEquals(0.5634f, bcrJsonList.get(0).getQual_pass_rate());
        assertEquals(0f, bcrJsonList.get(10).getQual_pass_rate());
        assertEquals(new Integer(28), bcrJsonList.get(5).getPending_shipment());
        assertEquals(new Integer(921), bcrJsonList.get(1).getReceived());
        assertEquals(new Integer(572), bcrJsonList.get(0).getShipped());
    }

    @Test
    public void testGetBCRJsonNoFile() throws Exception {
        final List<BCRJson> bcrJsonList = service.getBCRJson(JSON_PATH + null);
        assertNotNull(bcrJsonList);
        assertEquals(0, bcrJsonList.size());
    }

    @Test
    public void testGetAllProjectCaseCounts() {
        context.checking(new Expectations() {{
            one(dao).getAllProjectCasesCounts();
            will(returnValue(makeMockProjectCases()));
            allowing(codeTablesReportService).getTumor();
            will(returnValue(makeMockTumor()));
        }});
        final List<ProjectCase> pcList = service.getAllProjectCaseCounts();
        System.out.println(pcList);
        System.out.println(pcList.get(7).getDisease());
        System.out.println(pcList.get(8).getDisease());
        assertNotNull(pcList);
        assertEquals(9, pcList.size());
        assertEquals("COAD", pcList.get(0).getDisease());
        assertEquals("560/500", pcList.get(1).getCopyNumberSNPCGCC());
        assertEquals("21/500", pcList.get(2).getMutationGSC());
        assertEquals("TOTALS", pcList.get(8).getDisease());
    }

    @Test
    public void testGetMostRecentBCRFile() throws Exception {
        String res = service.getMostRecentBCRFile(JSON_PATH, "IGC");
        assertNotNull(res);
        assertEquals("IGC-BCR_01-2012.json", res);
        res = service.getMostRecentBCRFile(JSON_PATH, "NWCH");
        assertNotNull(res);
        assertEquals("NWCH-BCR_03-2013.json", res);
    }

    @Test
    public void testGetFilteredProjectCaseList() {
        final List<String> gbmList = new LinkedList<String>() {{
            add("GBM");
        }};
        final List<ProjectCase> pcList = service.getFilteredProjectCaseList(makeMockProjectCases(), gbmList);
        assertNotNull(pcList);
        assertEquals(2, pcList.size());
        assertEquals("77/500", pcList.get(0).getMicroRNAGSC());
        assertEquals("77/500", pcList.get(1).getMicroRNAGSC());
    }

    @Test
    public void testGetProjectCaseFilterDistinctValues() {
        context.checking(new Expectations() {{
            one(dao).getAllProjectCasesCounts();
            will(returnValue(makeMockProjectCases()));
            allowing(codeTablesReportService).getTumor();
            will(returnValue(makeMockTumor()));
        }});
        final List<ExtJsFilter> bfList = service.getProjectCaseFilterDistinctValues("disease");
        assertNotNull(bfList);
        assertEquals(8, bfList.size());
        assertEquals("COAD", bfList.get(0).getText());
        assertEquals("GBM", bfList.get(1).getText());
    }

    @Test
    public void testGetProjectCaseFilterDistinctValuesWithTotals() {
        final List<ProjectCase> pcList = makeMockProjectCases();
        pcList.add(new ProjectCase() {{
            setDisease("TOTALS");
        }});
        context.checking(new Expectations() {{
            one(dao).getAllProjectCasesCounts();
            will(returnValue(pcList));
            allowing(codeTablesReportService).getTumor();
            will(returnValue(makeMockTumor()));
        }});
        final List<ExtJsFilter> bfList = service.getProjectCaseFilterDistinctValues("disease");
        assertNotNull(bfList);
        assertEquals(8, bfList.size());
        assertEquals("COAD", bfList.get(0).getText());
        assertEquals("GBM", bfList.get(1).getText());
    }

    @Test
    public void testGetProjectCaseComparator() {
        final Map<String, Comparator> map = service.getProjectCaseComparator();
        assertNotNull(map);
    }

    @Test
    public void testProcessRatioNA() throws Exception {
        assertEquals(-1f, service.processRatio("N/A"));
    }

    @Test
    public void testProcessRatioNull() throws Exception {
        assertEquals(0f, service.processRatio(null));
    }

    @Test
    public void testProcessRatio() throws Exception {
        assertEquals(0.501f, service.processRatio("501/1000"));
        assertEquals(0.918f, service.processRatio("459/500"));
    }

    @Test
    public void testProcessBCRJsonFile() throws Exception {
        final List<BCRJson> list = makeMockNWCHBCR();
        Map<String, String> map = new HashMap<String, String>();
        assertNotNull(list);
        assertEquals(8, list.size());
        assertEquals("GBM", list.get(0).getDisease());
        for (BCRJson jo : list) {
            map.put(jo.getDisease(), jo.getDisease());
        }
        assertTrue(map.containsKey("COAD"));
        assertTrue(map.containsKey("READ"));
        assertTrue(map.containsKey("OV"));
        for (BCRJson jo : list) {
            if ("COAD".equals(jo.getDisease())) {
                assertEquals(new Integer(12), jo.getShipped());
                assertEquals(new Integer(13), jo.getPending_shipment());
                assertEquals(new Integer(14), jo.getReceived());
            } else if ("READ".equals(jo.getDisease())) {
                assertEquals(new Integer(12), jo.getShipped());
                assertEquals(new Integer(13), jo.getPending_shipment());
                assertEquals(new Integer(14), jo.getReceived());
            } else if ("OV".equals(jo.getDisease())) {
                assertEquals(new Integer(100), jo.getShipped());
                assertEquals(new Integer(13), jo.getPending_shipment());
                assertEquals(new Integer(122), jo.getReceived());
            }
        }
    }

    @Test
    public void testCompleteBCRProjectCase() throws Exception {
        context.checking(new Expectations() {{
            allowing(codeTablesReportService).getTumor();
            will(returnValue(makeMockTumor()));
        }});
        List<ProjectCase> list = service.completeBCRProjectCase(
                makeMockProjectCases(), makeMockNWCHBCR());
        assertNotNull(list);
        assertEquals(9, list.size());
        assertEquals("GBM", list.get(4).getDisease());
        assertEquals("GBM FullName", list.get(4).getDiseaseName());
        assertEquals("25/500", list.get(4).getShippedBCR());
        assertEquals("14/1000", list.get(4).getReceivedBCR());
        assertEquals("COAD", list.get(0).getDisease());
        assertEquals("COAD FullName", list.get(0).getDiseaseName());
        assertEquals("25/500", list.get(0).getShippedBCR());
        assertEquals("14/1000", list.get(0).getReceivedBCR());
        assertEquals("LGG", list.get(3).getDisease());
        assertEquals("Unknonw", list.get(3).getDiseaseName());
        assertEquals("25/500", list.get(3).getShippedBCR());
        assertEquals("14/1000", list.get(3).getReceivedBCR());
        assertEquals("LUAD", list.get(2).getDisease());
        assertEquals("Unknonw", list.get(2).getDiseaseName());
        assertEquals("135/500", list.get(2).getShippedBCR());
        assertEquals("144/533", list.get(2).getReceivedBCR());
        assertEquals("OV", list.get(5).getDisease());
        assertEquals("OV FullName", list.get(5).getDiseaseName());
        assertEquals("113/500", list.get(5).getShippedBCR());
        assertEquals("122/540", list.get(5).getReceivedBCR());

        assertEquals("TOTALS", list.get(8).getDisease());
        assertEquals("Totals", list.get(8).getDiseaseName());
        assertEquals("398/4000", list.get(8).getShippedBCR());
        assertEquals("350/7073", list.get(8).getReceivedBCR());
        assertEquals("1879/4000", list.get(8).getMethylationCGCC());
        assertEquals("175/4000", list.get(8).getMutationGSC());
    }

    @Test
    public void testGetQCPassRateShippedUnder100() throws Exception {
        Float f = service.getQCPassRate(0, 12, 13, 14);
        assertNotNull(f);
        assertEquals(0.5f, f);
    }

    @Test
    public void testGetQCPassRateShippedEqual100() throws Exception {
        Float f = service.getQCPassRate(0, 100, 13, 140);
        assertNotNull(f);
        assertEquals(0.80714285f, f);
    }

    @Test
    public void testGetQCPassRateShippedOver100() throws Exception {
        Float f = service.getQCPassRate(0, 120, 13, 140);
        assertNotNull(f);
        assertEquals(0.95f, f);
    }

    @Test
    public void testGetCurrentCaseGap() throws Exception {
        long f = service.getCurrentCaseGap(500, 54, 0, 0.5f);
        assertNotNull(f);
        assertEquals(892, f);
    }

    @Test
    public void testIsRatio() throws Exception {
        assertTrue(service.isRatio("123/456"));
        assertFalse(service.isRatio("GBM"));
        assertTrue(service.isRatio("123"));
    }

    @Test
    public void testGetCurrentCaseGapOtherMethod() throws Exception {
        //Test based on disease BLCA from NCWH-BCR_10-2011.json
        final int targetCase = 500;
        final int shipped = 54;
        final int pending = 7;
        final int total_received = 121;

        //QC pass rate not defined in NWCH json so it has to be calculated according to spec
        float qcPassRate = .5f;
        if (total_received != 0 && shipped >= 100) {
            qcPassRate = (new Float(shipped) + new Float(pending)) / new Float(total_received);
        }

        // BCR Shipped numerator is: cases shipped + cases awaiting shipment
        final int bcrShipped = shipped + pending;
        final int caseRequired = (bcrShipped < 100) ? (targetCase * 2) : Math.round(targetCase / qcPassRate);

        //Case Gap == target number of cases - shipped - awaiting_shipment ] / qual_pass_rate
        final int gap = Math.round((targetCase - shipped - pending) / qcPassRate);

        //Other way is: Case Gap == (BCR Shipped denominator - BCR Shipped numerator)*(Cases Required)/(BCR Shipped denominator)
        final int otherWayGap = (targetCase - bcrShipped) * (caseRequired) / (targetCase);

        assertNotNull(gap);
        assertNotNull(otherWayGap);
        assertEquals(otherWayGap, gap);
        assertEquals(878, otherWayGap);
    }

    public List<BCRJson> makeMockNWCHBCR() {
        List<BCRJson> list = new LinkedList<BCRJson>();
        list.add(new BCRJson("GBM", 12, 13, 14, 0f));
        list.add(new BCRJson("OV", 100, 13, 122, 0f));
        list.add(new BCRJson("COAD", 12, 13, 14, 0f));
        list.add(new BCRJson("LGG", 12, 13, 14, 0f));
        list.add(new BCRJson("LUAD", 122, 13, 144, 0f));
        list.add(new BCRJson("READ", 12, 13, 14, 0f));
        list.add(new BCRJson("KIRC", 12, 13, 14, 0f));
        list.add(new BCRJson("KIRP", 12, 13, 14, 0f));
        return list;
    }

    public List<Tumor> makeMockTumor() {
        List<Tumor> list = new LinkedList<Tumor>();
        list.add(new Tumor() {{
            setTumorName("GBM");
            setTumorDescription("GBM FullName");
        }});
        list.add(new Tumor() {{
            setTumorName("OV");
            setTumorDescription("OV FullName");
        }});
        list.add(new Tumor() {{
            setTumorName("COAD");
            setTumorDescription("COAD FullName");
        }});
        return list;
    }

    public List<ProjectCase> makeMockProjectCases() {
        List<ProjectCase> list = new LinkedList<ProjectCase>();
        list.add(new ProjectCase() {{
            setDisease("COAD");
            setOverallProgress("452/500");
            setMethylationCGCC("234/500");
            setCopyNumberSNPCGCC("560/500");
            setMicroRNAGSC("76/500");
            setMutationGSC("21/500");
            setProjectedCaseBCR("500");
        }});
        list.add(new ProjectCase() {{
            setDisease("READ");
            setOverallProgress("452/500");
            setMethylationCGCC("234/500");
            setCopyNumberSNPCGCC("560/500");
            setMicroRNAGSC("76/500");
            setMutationGSC("21/500");
            setProjectedCaseBCR("500");
        }});
        list.add(new ProjectCase() {{
            setDisease("LUAD");
            setOverallProgress("452/500");
            setMethylationCGCC("234/500");
            setCopyNumberSNPCGCC("560/500");
            setMicroRNAGSC("76/500");
            setMutationGSC("21/500");
            setProjectedCaseBCR("500");
        }});
        list.add(new ProjectCase() {{
            setDisease("LGG");
            setOverallProgress("452/500");
            setMethylationCGCC("234/500");
            setCopyNumberSNPCGCC("560/500");
            setMicroRNAGSC("76/500");
            setMutationGSC("21/500");
            setProjectedCaseBCR("500");
        }});
        list.add(new ProjectCase() {{
            setDisease("GBM");
            setOverallProgress("453/500");
            setMethylationCGCC("235/500");
            setCopyNumberSNPCGCC("561/500");
            setMicroRNAGSC("77/500");
            setMutationGSC("22/500");
            setProjectedCaseBCR("500");
        }});
        list.add(new ProjectCase() {{
            setDisease("OV");
            setOverallProgress("454/500");
            setMethylationCGCC("236/500");
            setCopyNumberSNPCGCC("562/500");
            setMicroRNAGSC("78/500");
            setMutationGSC("23/500");
            setProjectedCaseBCR("500");
        }});
        list.add(new ProjectCase() {{
            setDisease("KIRC");
            setOverallProgress("454/500");
            setMethylationCGCC("236/500");
            setCopyNumberSNPCGCC("562/500");
            setMicroRNAGSC("78/500");
            setMutationGSC("23/500");
            setProjectedCaseBCR("500");
        }});
        list.add(new ProjectCase() {{
            setDisease("KIRP");
            setOverallProgress("454/500");
            setMethylationCGCC("236/500");
            setCopyNumberSNPCGCC("562/500");
            setMicroRNAGSC("78/500");
            setMutationGSC("23/500");
            setProjectedCaseBCR("500");
        }});
        return list;
    }

}//End of Class
