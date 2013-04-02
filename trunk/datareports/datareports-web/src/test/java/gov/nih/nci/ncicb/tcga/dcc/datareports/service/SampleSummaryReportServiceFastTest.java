/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.GBM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.BCR_SENT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.BCR_UNKNOWN;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.CENTER_SENT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.CENTER_UNKNOWN;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.LEVEL1_SS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.LEVEL2_SS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.LEVEL3_SS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sample;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.SampleSummaryReportDAO;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the SampleSummaryReportService with mock objects
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
@RunWith(JMock.class)
public class SampleSummaryReportServiceFastTest {

    private Mockery context;

    private SampleSummaryReportDAO dao;

    private SampleSummaryReportServiceImpl service;

    private DatareportsServiceImpl commonService;

    private List<Sample> sampleList;

    private Sample sample;

    private SampleSummary sampleSummary;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        dao = context.mock(SampleSummaryReportDAO.class);
        service = new SampleSummaryReportServiceImpl();
        commonService = new DatareportsServiceImpl();
        sampleList = null;
        sample = null;
        sampleSummary = new SampleSummary("GBM", "broad.mit.edu", "CGCC", "T", "Genome_Wide_SNP_6");
        //We use reflection to access the private field
        Field daoServiceField = service.getClass().getDeclaredField("daoImpl");
        Field commonServiceField = service.getClass().getDeclaredField("commonService");
        daoServiceField.setAccessible(true);
        commonServiceField.setAccessible(true);
        daoServiceField.set(service, dao);
        commonServiceField.set(service, commonService);
    }

    @Test
    public void testGetSampleSummaryReport() {
        context.checking(new Expectations() {{
            one(dao).getSampleSummaryRows(GBM);
            will(returnValue(mockSampleSummaryList()));
        }});

        final List<SampleSummary> samples = service.getSampleSummaryReport(GBM);
        assertEquals(7, samples.size());

        // index 0 below
        SampleSummary summary = samples.get(0);
        assertEquals("broad.mit.edu (CGCC)", summary.getCenter());
        assertEquals(GBM, summary.getDisease());
        assertEquals("D", summary.getPortionAnalyte());
        assertEquals("Genome_Wide_SNP_6", summary.getPlatform());
        assertEquals(Long.valueOf(318L), summary.getTotalBCRSent());
        assertEquals(Long.valueOf(497L), summary.getTotalCenterSent());
        assertEquals(Long.valueOf(179L), summary.getTotalBCRUnaccountedFor());
        assertEquals(Long.valueOf(0L), summary.getTotalCenterUnaccountedFor());
        assertEquals("Y*", summary.getLevelFourSubmitted());

        // index 5 below
        summary = samples.get(2);
        assertEquals("broad.mit.edu (CGCC)", summary.getCenter());
        assertEquals(GBM, summary.getDisease());
        assertEquals("G", summary.getPortionAnalyte());
        assertEquals("Genome_Wide_SNP_6", summary.getPlatform());
        assertEquals(null, summary.getTotalBCRSent());
        assertEquals(Long.valueOf(70L), summary.getTotalCenterSent());
        assertEquals(Long.valueOf(70L), summary.getTotalBCRUnaccountedFor());
        assertEquals(Long.valueOf(0L), summary.getTotalCenterUnaccountedFor());
    }

    @Test
    public void testDrillDownSamplesForTotalSamplesBCRSent() {
        context.checking(new Expectations() {{
            one(dao).getSamplesForTotalSamplesBCRSent("GBM", "broad.mit.edu", "CGCC", "T");
            will(returnValue(makeMockSamples1()));
        }});
        sampleList = service.getDrillDown(sampleSummary, BCR_SENT);
        // this is supposed to return mockSamples1 so let's test on that !
        assertEquals(7, sampleList.size());
        sample = sampleList.get(5);
        assertEquals("Domi power-up 6", sample.getName());
    }

    @Test
    public void testDrillDownSamplesForTotalSamplesCenterSent() {
        context.checking(new Expectations() {{
            one(dao).getSamplesForTotalSamplesCenterSent("GBM", "broad.mit.edu", "CGCC", "T", "Genome_Wide_SNP_6");
            will(returnValue(makeMockSamples2()));
        }});
        sampleList = service.getDrillDown(sampleSummary, CENTER_SENT);
        assertEquals(5, sampleList.size());
        sample = sampleList.get(3);
        assertEquals("Domi power-down 4", sample.getName());
    }

    @Test
    public void testDrillDownSamplesForTotalSamplesUnaccountedForBCR() {
        context.checking(new Expectations() {{
            one(dao).getSamplesForTotalSamplesUnaccountedForBCR("GBM", "broad.mit.edu", "CGCC", "T", "Genome_Wide_SNP_6");
            will(returnValue(makeMockSamples3()));
        }});
        sampleList = service.getDrillDown(sampleSummary, BCR_UNKNOWN);
        assertEquals(3, sampleList.size());
        sample = sampleList.get(1);
        assertEquals("Domi normal 2", sample.getName());
    }

    @Test
    public void testDrillDownSamplesForTotalSamplesUnaccountedForCenter() {
        context.checking(new Expectations() {{
            one(dao).getSamplesForTotalSamplesUnaccountedForCenter("GBM", "broad.mit.edu", "CGCC", "T", "Genome_Wide_SNP_6");
            will(returnValue(makeMockSamples2()));
        }});
        sampleList = service.getDrillDown(sampleSummary, CENTER_UNKNOWN);
        assertEquals(5, sampleList.size());
        sample = sampleList.get(4);
        assertEquals("Domi power-down 5", sample.getName());
    }

    @Test
    public void testDrillDowngetSamplesForLevelTotal1() {
        context.checking(new Expectations() {{
            one(dao).getSamplesForLevelTotal("GBM", "broad.mit.edu", "CGCC", "T", "Genome_Wide_SNP_6", 1);
            will(returnValue(makeMockSamples1()));
        }});
        sampleList = service.getDrillDown(sampleSummary, LEVEL1_SS);
        assertEquals(7, sampleList.size());
        sample = sampleList.get(5);
        assertEquals("Domi power-up 6", sample.getName());
    }

    @Test
    public void testDrillDowngetSamplesForLevelTotal2() {
        context.checking(new Expectations() {{
            one(dao).getSamplesForLevelTotal("GBM", "broad.mit.edu", "CGCC", "T", "Genome_Wide_SNP_6", 2);
            will(returnValue(makeMockSamples2()));
        }});
        sampleList = service.getDrillDown(sampleSummary, LEVEL2_SS);
        assertEquals(5, sampleList.size());
        sample = sampleList.get(3);
        assertEquals("Domi power-down 4", sample.getName());

    }

    @Test
    public void testDrillDowngetSamplesForLevelTotal3() {
        context.checking(new Expectations() {{
            one(dao).getSamplesForLevelTotal("GBM", "broad.mit.edu", "CGCC", "T", "Genome_Wide_SNP_6", 3);
            will(returnValue(makeMockSamples3()));
        }});
        sampleList = service.getDrillDown(sampleSummary, LEVEL3_SS);
        assertEquals(3, sampleList.size());
        sample = sampleList.get(0);
        assertEquals("Domi normal 1", sample.getName());
    }


    @Test
    public void testFindSampleSummary() {
        context.checking(new Expectations() {{
            one(dao).getSampleSummaryRows(GBM);
            will(returnValue(mockSampleSummaryList()));
        }});

        List<SampleSummary> samples = service.getSampleSummaryReport(GBM);
        assertEquals(7, samples.size());
        // let's see if we find index 7
        sampleSummary = service.findSampleSummary(samples, "GBM", "unc.edu (CGCC)", "T", "H-miRNA_8x15K");
        assertNotNull(sampleSummary);
        assertEquals("GBM", sampleSummary.getDisease());
        assertEquals("unc.edu", sampleSummary.getCenterName());
        assertEquals("CGCC", sampleSummary.getCenterType());
        assertEquals("T", sampleSummary.getPortionAnalyte());
        assertEquals("H-miRNA_8x15K", sampleSummary.getPlatform());
        assertEquals(Long.valueOf(168L), sampleSummary.getTotalBCRSent());
        assertEquals(Long.valueOf(319L), sampleSummary.getTotalCenterSent());
        assertEquals(Long.valueOf(0L), sampleSummary.getTotalCenterUnaccountedFor());
        assertEquals(Long.valueOf(120L), sampleSummary.getTotalLevelThree());
    }

    @Test
    public void testGetFilteredSampleSummaryReport() {
        context.checking(new Expectations() {{
            one(dao).getSampleSummaryRows();
            will(returnValue(mockSampleSummaryList()));
        }});
        List<SampleSummary> samples = service.getFilteredSampleSummaryReport("broad.mit.edu");
        assertEquals(6, samples.size());
    }

    @Test
    public void processSampleSummaryTest() {
        context.checking(new Expectations() {{
            one(dao).getSampleSummaryRows();
            will(returnValue(mockSampleSummaryList()));
        }});

        List<SampleSummary> samples = service.processSampleSummary("broad.mit.edu (CGCC)");
        assertNotNull(samples);
        assertEquals(6, samples.size());
    }

    @Test
    public void testGetFilteredSampleSummaryList() throws Exception {
        List<String> gbmList = new LinkedList<String>() {{
            add("GBM");
        }};
        List<SampleSummary> bList = service.getFilteredSampleSummaryList(mockSampleSummaryList(),
                gbmList, null, null, null, null);
        assertNotNull(bList);
        assertEquals(5, bList.size());
        assertEquals("GBM", bList.get(0).getDisease());
    }

    @Test
    public void testGetFilteredSampleSummaryListForMultipleLevel() throws Exception {
        List<String> list = new LinkedList<String>() {{
            add("Y");
            add("N");
        }};
        List<SampleSummary> bList = service.getFilteredSampleSummaryList(mockSampleSummaryList(),
                null, null, null, null, list);
        assertNotNull(bList);
        assertEquals(2, bList.size());
        assertEquals("Undetermined", bList.get(0).getPlatform());
    }

    @Test
    public void testGetFilteredSampleSummaryListForSingleLevel() throws Exception {
        List<String> list = new LinkedList<String>() {{
            add("Y");
        }};
        List<SampleSummary> bList = service.getFilteredSampleSummaryList(mockSampleSummaryList(),
                null, null, null, null, list);
        assertNotNull(bList);
        assertEquals(1, bList.size());
        assertEquals("T", bList.get(0).getPortionAnalyte());
    }

    @Test
    public void testGetFilteredSampleSummaryListForNull() throws Exception {
        List<SampleSummary> bList = service.getFilteredSampleSummaryList(mockSampleSummaryList(),
                null, null, null, null, null);
        assertNotNull(bList);
        assertEquals(mockSampleSummaryList().size(), bList.size());
    }

    @Test
    public void testGetSampleSummaryFilterDistinctValues() {
        context.checking(new Expectations() {{
            one(dao).getSampleSummaryRows();
            will(returnValue(mockSampleSummaryList()));
        }});
        List<ExtJsFilter> bfList = service.getSampleSummaryFilterDistinctValues("disease");
        assertNotNull(bfList);
        assertEquals(2, bfList.size());
        assertEquals("GBM", bfList.get(0).getText());
        assertEquals("OV", bfList.get(1).getText());
    }

    @Test
    public void testGetSampleSummaryComparator() throws Exception {
        Map<String, Comparator> map = service.getSampleSummaryComparator();
        assertNotNull(map);
    }

    @Test
    public void testGetSampleComparatorBCR() throws Exception {
        Map<String, Comparator> map = service.getSampleComparator(true);
        assertNotNull(map);
    }

    @Test
    public void testGetSampleComparatorCenter() throws Exception {
        Map<String, Comparator> map = service.getSampleComparator(false);
        assertNotNull(map);
    }

    @Test
    public void testGetLatest() throws Exception {
        Date date = service.getLatest(mockSampleSummaryList());
        assertNotNull(date);
        assertEquals(123456789, date.getTime());
    }

    public List<Sample> makeMockSamples1() {
        List<Sample> list = new LinkedList<Sample>();
        list.add(new Sample("Domi power-up 1"));
        list.add(new Sample("Domi power-up 2"));
        list.add(new Sample("Domi power-up 3"));
        list.add(new Sample("Domi power-up 4"));
        list.add(new Sample("Domi power-up 5"));
        list.add(new Sample("Domi power-up 6"));
        list.add(new Sample("Domi power-up 7"));
        return list;
    }

    public List<Sample> makeMockSamples2() {
        List<Sample> list = new LinkedList<Sample>();
        list.add(new Sample("Domi power-down 1"));
        list.add(new Sample("Domi power-down 2"));
        list.add(new Sample("Domi power-down 3"));
        list.add(new Sample("Domi power-down 4"));
        list.add(new Sample("Domi power-down 5"));
        return list;
    }

    public List<Sample> makeMockSamples3() {
        List<Sample> list = new LinkedList<Sample>();
        list.add(new Sample("Domi normal 1"));
        list.add(new Sample("Domi normal 2"));
        list.add(new Sample("Domi normal 3"));
        return list;
    }

    /**
     * Generated data.  Not worth putting in constants
     *
     * @return List of data
     */
    public List<SampleSummary> mockSampleSummaryList() {
        List<SampleSummary> list = new ArrayList<SampleSummary>();
        list.add(new SampleSummary() {{
            setDisease("GBM");
            setCenterName("broad.mit.edu");
            setCenterType("CGCC");
            setPortionAnalyte("D");
            setPlatform("Genome_Wide_SNP_6");
            setTotalBCRSent(318L);
            setTotalCenterSent(497L);
            setTotalBCRUnaccountedFor(179L);
            setTotalCenterUnaccountedFor(0L);
            setTotalLevelOne(226L);
            setTotalLevelTwo(211L);
            setTotalLevelThree(211L);
            setLevelFourSubmitted("Y*");
            setLastRefresh(new java.sql.Timestamp(123456789));
        }});
        list.add(new SampleSummary() {{
            setDisease("OV");
            setCenterName("broad.mit.edu");
            setCenterType("CGCC");
            setPortionAnalyte("D");
            setPlatform("Genome_Wide_SNP_6");
            setTotalBCRSent(null);
            setTotalCenterSent(556L);
            setTotalBCRUnaccountedFor(556L);
            setTotalCenterUnaccountedFor(0L);
            setTotalLevelOne(null);
            setTotalLevelTwo(null);
            setTotalLevelThree(null);
            setLevelFourSubmitted("Y*");
            setLastRefresh(null);
        }});
        list.add(new SampleSummary() {{
            setDisease("GBM");
            setCenterName("broad.mit.edu");
            setCenterType("CGCC");
            setPortionAnalyte("G");
            setPlatform("Genome_Wide_SNP_6");
            setTotalBCRSent(null);
            setTotalCenterSent(70L);
            setTotalBCRUnaccountedFor(70L);
            setTotalCenterUnaccountedFor(0L);
            setTotalLevelOne(null);
            setTotalLevelTwo(null);
            setTotalLevelThree(null);
            setLevelFourSubmitted("Y*");
            setLastRefresh(null);
        }});
        list.add(new SampleSummary() {{
            setDisease("OV");
            setCenterName("broad.mit.edu");
            setCenterType("CGCC");
            setPortionAnalyte("R");
            setPlatform("HT_HG-U133A");
            setTotalBCRSent(null);
            setTotalCenterSent(293L);
            setTotalBCRUnaccountedFor(293L);
            setTotalCenterUnaccountedFor(0L);
            setTotalLevelOne(null);
            setTotalLevelTwo(null);
            setTotalLevelThree(null);
            setLevelFourSubmitted("Y*");
            setLastRefresh(null);
        }});
        list.add(new SampleSummary() {{
            setDisease("GBM");
            setCenterName("broad.mit.edu");
            setCenterType("CGCC");
            setPortionAnalyte("R");
            setPlatform("HT_HG-U133A");
            setTotalBCRSent(173L);
            setTotalCenterSent(321L);
            setTotalBCRUnaccountedFor(148L);
            setTotalCenterUnaccountedFor(0L);
            setTotalLevelOne(126L);
            setTotalLevelTwo(126L);
            setTotalLevelThree(126L);
            setLevelFourSubmitted("Y*");
            setLastRefresh(null);
        }});
        list.add(new SampleSummary() {{
            setDisease("GBM");
            setCenterName("broad.mit.edu");
            setCenterType("GSC");
            setPortionAnalyte("R");
            setPlatform(null);
            setTotalBCRSent(4L);
            setTotalCenterSent(null);
            setTotalBCRUnaccountedFor(0L);
            setTotalCenterUnaccountedFor(4L);
            setTotalLevelOne(null);
            setTotalLevelTwo(null);
            setTotalLevelThree(null);
            setLevelFourSubmitted(null);
            setLastRefresh(null);
        }});
        list.add(new SampleSummary() {{
            setDisease("GBM");
            setCenterName("unc.edu");
            setCenterType("CGCC");
            setPortionAnalyte("T");
            setPlatform("H-miRNA_8x15K");
            setTotalBCRSent(168L);
            setTotalCenterSent(319L);
            setTotalBCRUnaccountedFor(151L);
            setTotalCenterUnaccountedFor(0L);
            setTotalLevelOne(120L);
            setTotalLevelTwo(120L);
            setTotalLevelThree(120L);
            setLevelFourSubmitted("Y");
            setLastRefresh(null);
        }});
        return list;
    }

}  //End of Class
