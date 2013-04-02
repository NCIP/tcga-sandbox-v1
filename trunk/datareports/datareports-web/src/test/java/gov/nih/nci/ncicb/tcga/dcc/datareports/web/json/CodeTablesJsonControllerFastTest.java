/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.CENTER_CODE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TOTAL_COUNT;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BcrBatchCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CenterCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CodeReport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.DataType;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PlatformCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.CodeTablesReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
 * Test Class for the code table report json controller
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class CodeTablesJsonControllerFastTest {

   private final Mockery context = new JUnit4Mockery();

    private CodeTablesReportService service;

    private DatareportsService commonService;

    private CodeTablesJsonController controller;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void before() throws Exception {

        service = context.mock(CodeTablesReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new CodeTablesJsonController();

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
    public void testCodeTablesReportCplxHandler4TissueSourceSite() throws Exception {
        final String code = "tissueSourceSite";
        final List<TissueSourceSite> mockTissueSourceSite = makeMockCollectionCenter();
        context.checking(new Expectations() {{
           allowing(service).getTissueSourceSite();
           will(returnValue(mockTissueSourceSite));
           allowing(service).getTissueSourceSiteComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockTissueSourceSite,null,null,null);
           will(returnValue(mockTissueSourceSite));
           allowing(commonService).getPaginatedList(mockTissueSourceSite,0,25);
           will(returnValue(mockTissueSourceSite));
           allowing(commonService).getTotalCount(mockTissueSourceSite);
           will(returnValue(3));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.codeTablesReportComplexHandler(model,code,0,25,null,null);
        assertTrue(resMap != null);
        int totalCount = (Integer)model.get("totalCount");
        assertEquals(3,totalCount);
        List<TissueSourceSite> json = (List<TissueSourceSite>)model.get("tissueSourceSiteData");
        assertEquals(json.get(0).getCode(),makeMockCollectionCenter().get(0).getCode());
    }

    @Test
    public void testCodeTablesReportCplxHandler4CenterCode() throws Exception {
        final String code = "centerCode";
        final List<CenterCode> mockCenterCode = makeMockCenterCode();
        context.checking(new Expectations() {{
           allowing(service).getCenterCode();
           will(returnValue(mockCenterCode));
           allowing(service).getCenterCodeComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockCenterCode,null,null,null);
           will(returnValue(mockCenterCode));
           allowing(commonService).getPaginatedList(mockCenterCode,0,25);
           will(returnValue(mockCenterCode));
           allowing(commonService).getTotalCount(mockCenterCode);
           will(returnValue(3));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.codeTablesReportComplexHandler(model,code,0,25,null,null);
        assertTrue(resMap != null);
        int totalCount = (Integer)model.get("totalCount");
        assertEquals(3,totalCount);
        List<CenterCode> json = (List<CenterCode>)model.get("centerCodeData");
        assertEquals(json.get(0).getCode(),makeMockCenterCode().get(0).getCode());
    }

    @Test
    public void testCodeTablesReportCplxHandler4DataType() throws Exception {
        final String code = "dataType";
        final List<DataType> mockDataType = makeMockDataType();
        context.checking(new Expectations() {{
           allowing(service).getDataType();
           will(returnValue(mockDataType));
           allowing(service).getDataTypeComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockDataType,null,null,null);
           will(returnValue(mockDataType));
           allowing(commonService).getPaginatedList(mockDataType,0,25);
           will(returnValue(mockDataType));
           allowing(commonService).getTotalCount(mockDataType);
           will(returnValue(3));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.codeTablesReportComplexHandler(model,code,0,25,null,null);
        assertTrue(resMap != null);
        int totalCount = (Integer)model.get("totalCount");
        assertEquals(3,totalCount);
        List<DataType> json = (List<DataType>)model.get("dataTypeData");
        assertEquals(json.get(0).getFtpDisplay(),makeMockDataType().get(0).getFtpDisplay());
    }

    @Test
    public void testCodeTablesReportCplxHandler4DiseaseStudy() throws Exception {
        final String code = "diseaseStudy";
        final List<Tumor> mockTumor = makeMockTumor();
        context.checking(new Expectations() {{
           allowing(service).getTumor();
           will(returnValue(mockTumor));
           allowing(service).getTumorComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockTumor,null,null,null);
           will(returnValue(mockTumor));
           allowing(commonService).getPaginatedList(mockTumor,0,25);
           will(returnValue(mockTumor));
           allowing(commonService).getTotalCount(mockTumor);
           will(returnValue(3));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.codeTablesReportComplexHandler(model,code,0,25,null,null);
        assertTrue(resMap != null);
        int totalCount = (Integer)model.get("totalCount");
        assertEquals(3,totalCount);
        List<Tumor> json = (List<Tumor>)model.get("diseaseStudyData");
        assertEquals(json.get(0).getTumorName(),makeMockTumor().get(0).getTumorName());
    }
    
    @Test
    public void testCodeTablesReportCplxHandler4PlatformCode() throws Exception {
        final String code = "platformCode";
        final List<PlatformCode> mockPlatformCode = makeMockPlatformCode();
        context.checking(new Expectations() {{
           allowing(service).getPlatformCode();
           will(returnValue(mockPlatformCode));
           allowing(service).getPlatformCodeComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockPlatformCode,null,null,null);
           will(returnValue(mockPlatformCode));
           allowing(commonService).getPaginatedList(mockPlatformCode,0,25);
           will(returnValue(mockPlatformCode));
           allowing(commonService).getTotalCount(mockPlatformCode);
           will(returnValue(3));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.codeTablesReportComplexHandler(model,code,0,25,null,null);
        assertTrue(resMap != null);
        int totalCount = (Integer)model.get("totalCount");
        assertEquals(3,totalCount);
        List<PlatformCode> json = (List<PlatformCode>)model.get("platformCodeData");
        assertEquals(json.get(0).getPlatformName(),makeMockPlatformCode().get(0).getPlatformName());
    }
    
    @Test
    public void testCodeTablesReportSimpleHandler4Analyte() throws Exception {
        final String code = "portionAnalyte";
        final List<CodeReport> mockAnalyte = makeMockCodeReport();
        context.checking(new Expectations() {{
           allowing(service).getPortionAnalyte();
           will(returnValue(mockAnalyte));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.codeTablesReportSimpleHandler(model,code);
        assertTrue(resMap != null);
        List<CodeReport> json = (List<CodeReport>)model.get("portionAnalyteData");
        assertEquals(json.get(0).getDefinition(),makeMockCodeReport().get(0).getDefinition());
    }

    @Test
    public void testCodeTablesReportSimpleHandler4SampleType() throws Exception {
        final String code = "sampleType";
        final List<CodeReport> mockSampleType = makeMockCodeReport();
        context.checking(new Expectations() {{
           allowing(service).getSampleType();
           will(returnValue(mockSampleType));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.codeTablesReportSimpleHandler(model,code);
        assertTrue(resMap != null);
        List<CodeReport> json = (List<CodeReport>)model.get("sampleTypeData");
        assertEquals(json.get(0).getDefinition(),makeMockCodeReport().get(0).getDefinition());
    }
    

    @Test
    public void testCodeTablesReportSimpleHandler4DataLevel() throws Exception {
       final String code = "dataLevel";
        final List<CodeReport> mockDataLevel = makeMockCodeReport();
        context.checking(new Expectations() {{
           allowing(service).getDataLevel();
           will(returnValue(mockDataLevel));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.codeTablesReportSimpleHandler(model,code);
        assertTrue(resMap != null);
        List<CodeReport> json = (List<CodeReport>)model.get("dataLevelData");
        assertEquals(json.get(0).getCode(),makeMockCodeReport().get(0).getCode());

    }

    @Test
    public void testCodeTablesReportSimpleHandler4Tissue() throws Exception {
       final String code = "tissue";
        final List<String> mockTissue = makeMockTissue();
        context.checking(new Expectations() {{
           allowing(service).getTissue();
           will(returnValue(mockTissue));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.codeTablesReportSimpleHandler(model,code);
        assertTrue(resMap != null);
        List<String> json = (List<String>)model.get("tissueData");
        assertEquals(json.get(0),makeMockTissue().get(0));

    }

    @Test
    public void testCodeTablesReportCplxHandler4BcrBatchCode() throws Exception {
        final String code = "bcrBatchCode";
        final List<BcrBatchCode> mockBcrBatchCode = makeMockBcrBatchCode();
        context.checking(new Expectations() {{
           allowing(service).getBcrBatchCode();
           will(returnValue(mockBcrBatchCode));
           allowing(service).getBcrBatchCodeComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockBcrBatchCode,null,null,null);
           will(returnValue(mockBcrBatchCode));
           allowing(commonService).getPaginatedList(mockBcrBatchCode,0,25);
           will(returnValue(mockBcrBatchCode));
           allowing(commonService).getTotalCount(mockBcrBatchCode);
           will(returnValue(3));
        }});

        request.setMethod("POST");
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.codeTablesReportComplexHandler(model,code,0,25,null,null);
        assertTrue(resMap != null);
        int totalCount = (Integer)model.get("totalCount");
        assertEquals(3,totalCount);
        List<BcrBatchCode> json = (List<BcrBatchCode>)model.get("bcrBatchCodeData");
        assertEquals(json.get(0).getBcr(),makeMockBcrBatchCode().get(0).getBcr());
    }

    @Test
    public void testProcessComplexJson() throws Exception {
        final ModelMap map = new ModelMap();
        final List<CenterCode> mockCenterCode = makeMockCenterCode();
        context.checking(new Expectations() {{
           allowing(service).getCenterCode();
           will(returnValue(mockCenterCode));
           allowing(service).getCenterCodeComparator();
           will(returnValue(null));
           allowing(commonService).getSortedList(mockCenterCode,null,null,null);
           will(returnValue(mockCenterCode));
           allowing(commonService).getPaginatedList(mockCenterCode,0,25);
           will(returnValue(mockCenterCode));
           allowing(commonService).getTotalCount(mockCenterCode);
           will(returnValue(3));
        }});
        Method m = controller.getClass().getDeclaredMethod("processComplexJson",
                ModelMap.class,String.class, String.class, Integer.class, Integer.class,
                String.class,String.class);
        m.setAccessible(true);
        m.invoke(controller,map,"CenterCode",CENTER_CODE,0,25,null,null);
        assertTrue(map.size()>0);
        assertEquals(3,map.get(TOTAL_COUNT));
        assertEquals(mockCenterCode, map.get(CENTER_CODE+"Data"));
    }

    public List<TissueSourceSite> makeMockCollectionCenter() {
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

    public List<CenterCode> makeMockCenterCode() {
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

    public List<DataType> makeMockDataType() {
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

    public List<Tumor> makeMockTumor() {
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

    public List<PlatformCode> makeMockPlatformCode() {
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

    public List<CodeReport> makeMockCodeReport() {
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

    public List<String> makeMockTissue() {
        List<String> list = new LinkedList<String>();
        list.add("tissue1");
        list.add("tissue2");
        list.add("tissue3");
        return list;
    }

    public List<BcrBatchCode> makeMockBcrBatchCode() {
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

}//End of Class
