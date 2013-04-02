/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BcrBatchCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CenterCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CodeReport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.DataType;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PlatformCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Tissue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.CodeTablesReportDAO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Test class for the code tables report service
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class CodeTablesReportServiceFastTest {

    private Mockery context;

    private CodeTablesReportDAO dao;

    private CodeTablesReportServiceImpl service;

    private DatareportsServiceImpl commonService;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        dao = context.mock(CodeTablesReportDAO.class);
        service = new CodeTablesReportServiceImpl();
        commonService = new DatareportsServiceImpl();
        Field daoServiceField = service.getClass().getDeclaredField("daoImpl");
        Field commonServiceField = service.getClass().getDeclaredField("commonService");
        daoServiceField.setAccessible(true);
        commonServiceField.setAccessible(true);
        daoServiceField.set(service, dao);
        commonServiceField.set(service, commonService);
    }

    @Test
    public void testGetCollectionCenter() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getTissueSourceSite();
            will(returnValue(makeMockCollectionCenterRows()));
        }});
        List<TissueSourceSite> list = service.getTissueSourceSite();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("code2", list.get(1).getCode());
    }

    @Test
    public void testGetCollectionCenterComparator() throws Exception {
        Map<String, Comparator> map = service.getTissueSourceSiteComparator();
        assertNotNull(map);
    }

    @Test
    public void testGetDataLevel() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getDataLevel();
            will(returnValue(makeMockCodeReportRows()));
        }});
        List<CodeReport> list = service.getDataLevel();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("code3", list.get(2).getCode());
        assertEquals("def2", list.get(1).getDefinition());
    }

    @Test
    public void testGetCenterCode() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getCenterCode();
            will(returnValue(makeMockCenterCodeRows()));
        }});
        List<CenterCode> list = service.getCenterCode();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("centerShortName3", list.get(2).getShortName());
        assertEquals("center1", list.get(0).getCenterName());
    }

    @Test
    public void testGetCenterCodeComparator() throws Exception {
        Map<String, Comparator> map = service.getCenterCodeComparator();
        assertNotNull(map);
    }

    @Test
    public void testGetDataType() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getDataType();
            will(returnValue(makeMockDataTypeRows()));
        }});
        List<DataType> list = service.getDataType();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("ftpDisplay3", list.get(2).getFtpDisplay());
        assertEquals("type1", list.get(0).getCenterType());
    }

    @Test
    public void testGetDataTypeComparator() throws Exception {
        Map<String, Comparator> map = service.getDataTypeComparator();
        assertNotNull(map);
    }

    @Test
    public void testGetTumor() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getTumor();
            will(returnValue(makeMockTumorRows()));
        }});
        List<Tumor> list = service.getTumor();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("tumor3", list.get(2).getTumorName());
        assertEquals("description1", list.get(0).getTumorDescription());
    }

    @Test
    public void testGetTumorComparator() throws Exception {
        Map<String, Comparator> map = service.getTumorComparator();
        assertNotNull(map);
    }

    @Test
    public void testGetPlatformCode() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getPlatformCode();
            will(returnValue(makeMockPlatformCodeRows()));
        }});
        List<PlatformCode> list = service.getPlatformCode();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("platform3", list.get(2).getPlatformName());
        assertEquals("alias2", list.get(1).getPlatformAlias());
    }

    @Test
    public void testGetPlatformCodeComparator() throws Exception {
        Map<String, Comparator> map = service.getPlatformCodeComparator();
        assertNotNull(map);
    }

    @Test
    public void testGetAnalyte() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getPortionAnalyte();
            will(returnValue(makeMockCodeReportRows()));
        }});
        List<CodeReport> list = service.getPortionAnalyte();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("code3", list.get(2).getCode());
        assertEquals("def2", list.get(1).getDefinition());
    }

    @Test
    public void testGetSampleType() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getSampleType();
            will(returnValue(makeMockSampleTypeRows()));
        }});
        List<SampleType> list = service.getSampleType();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("code1", list.get(0).getSampleTypeCode());
        assertEquals("def3", list.get(2).getDefinition());
        assertEquals("letter1", list.get(0).getShortLetterCode());
    }

    @Test
    public void testGetCodeReportComparator() throws Exception {
        Map<String, Comparator> map = service.getCodeReportComparator();
        assertNotNull(map);
    }

    @Test
    public void testGetTissue() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getTissue();
            will(returnValue(makeMockTissueRows()));
        }});
        List<Tissue> list = service.getTissue();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("tissue2", list.get(1).getTissue());
    }

    @Test
    public void testGetBcrBatchCode() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getBcrBatchCode();
            will(returnValue(makeMockBcrBatchCodeRows()));
        }});
        List<BcrBatchCode> list = service.getBcrBatchCode();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("bcr2", list.get(1).getBcr());
    }

    @Test
    public void testGetBcrBatchCodeComparator() throws Exception {
        Map<String, Comparator> map = service.getBcrBatchCodeComparator();
        assertNotNull(map);
    }

    public List<TissueSourceSite> makeMockCollectionCenterRows() {
        List<TissueSourceSite> list = new LinkedList<TissueSourceSite>();
        list.add(new TissueSourceSite() {{
            setCode("code1");
            setDefinition("definition1");
            setStudyName("studyname1");
            setBcr("bcr1");
        }});
        list.add(new TissueSourceSite() {{
            setCode("code2");
            setDefinition("definition2");
            setStudyName("studyname2");
            setBcr("bcr2");
        }});
        list.add(new TissueSourceSite() {{
            setCode("code3");
            setDefinition("definition3");
            setStudyName("studyname3");
            setBcr("bcr3");
        }});
        return list;
    }

    public List<BcrBatchCode> makeMockBcrBatchCodeRows() {
        List<BcrBatchCode> list = new LinkedList<BcrBatchCode>();
        list.add(new BcrBatchCode() {{
            setBcrBatch("bcrbatch1");
            setStudyCode("studycode1");
            setStudyName("studyname1");
            setBcr("bcr1");
        }});
        list.add(new BcrBatchCode() {{
            setBcrBatch("bcrbatch2");
            setStudyCode("studycode2");
            setStudyName("studyname2");
            setBcr("bcr2");
        }});
        list.add(new BcrBatchCode() {{
            setBcrBatch("bcrbatch3");
            setStudyCode("studycode3");
            setStudyName("studyname3");
            setBcr("bcr3");
        }});
        return list;
    }

    public List<CenterCode> makeMockCenterCodeRows() {
        List<CenterCode> list = new LinkedList<CenterCode>();
        list.add(new CenterCode() {{
            setCode("code1");
            setCenterName("center1");
            setCenterType("type1");
            setCenterDisplayName("centerDisplayName1");
            setShortName("centerShortName1");
        }});
        list.add(new CenterCode() {{
            setCode("code2");
            setCenterName("center2");
            setCenterType("type2");
            setCenterDisplayName("centerDisplayName2");
            setShortName("centerShortName2");
        }});
        list.add(new CenterCode() {{
            setCode("code3");
            setCenterName("center3");
            setCenterType("type3");
            setCenterDisplayName("centerDisplayName3");
            setShortName("centerShortName3");
        }});
        return list;
    }

    public List<DataType> makeMockDataTypeRows() {
        List<DataType> list = new LinkedList<DataType>();
        list.add(new DataType() {{
            setCenterType("type1");
            setDisplayName("displayName1");
            setFtpDisplay("ftpDisplay1");
            setAvailable("available1");
        }});
        list.add(new DataType() {{
            setCenterType("type2");
            setDisplayName("displayName2");
            setFtpDisplay("ftpDisplay2");
            setAvailable("available2");
        }});
        list.add(new DataType() {{
            setCenterType("type3");
            setDisplayName("displayName3");
            setFtpDisplay("ftpDisplay3");
            setAvailable("available3");
        }});
        return list;
    }

    public List<Tumor> makeMockTumorRows() {
        List<Tumor> list = new LinkedList<Tumor>();
        list.add(new Tumor() {{
            setTumorName("tumor1");
            setTumorDescription("description1");
        }});
        list.add(new Tumor() {{
            setTumorName("tumor2");
            setTumorDescription("description2");
        }});
        list.add(new Tumor() {{
            setTumorName("tumor3");
            setTumorDescription("description3");
        }});
        return list;
    }

    public List<PlatformCode> makeMockPlatformCodeRows() {
        List<PlatformCode> list = new LinkedList<PlatformCode>();
        list.add(new PlatformCode() {{
            setPlatformName("platform1");
            setPlatformAlias("alias1");
            setPlatformDisplayName("displayname1");
            setAvailable("available1");
        }});
        list.add(new PlatformCode() {{
            setPlatformName("platform2");
            setPlatformAlias("alias2");
            setPlatformDisplayName("displayname2");
            setAvailable("available2");
        }});
        list.add(new PlatformCode() {{
            setPlatformName("platform3");
            setPlatformAlias("alias3");
            setPlatformDisplayName("displayname3");
            setAvailable("available3");
        }});
        return list;
    }

    public List<CodeReport> makeMockCodeReportRows() {
        List<CodeReport> list = new LinkedList<CodeReport>();
        list.add(new CodeReport() {{
            setCode("code1");
            setDefinition("def1");
        }});
        list.add(new CodeReport() {{
            setCode("code2");
            setDefinition("def2");
        }});
        list.add(new CodeReport() {{
            setCode("code3");
            setDefinition("def3");
        }});
        return list;
    }

    public List<Tissue> makeMockTissueRows() {
        List<Tissue> list = new LinkedList<Tissue>();
        list.add(new Tissue() {{
            setTissue("tissue1");
        }});
        list.add(new Tissue() {{
            setTissue("tissue2");
        }});
        list.add(new Tissue() {{
            setTissue("tissue3");
        }});
        return list;
    }

    public List<SampleType> makeMockSampleTypeRows() {
        List<SampleType> list = new LinkedList<SampleType>();
        list.add(new SampleType() {{
            setSampleTypeCode("code1");
            setDefinition("def1");
            setShortLetterCode("letter1");
        }});
        list.add(new SampleType() {{
            setSampleTypeCode("code2");
            setDefinition("def2");
            setShortLetterCode("letter2");
        }});
        list.add(new SampleType() {{
            setSampleTypeCode("code3");
            setDefinition("def3");
            setShortLetterCode("letter3");
        }});
        return list;
    }

}//End of Class
