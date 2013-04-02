/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BcrBatchCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CenterCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CodeReport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.DataType;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PlatformCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.CodeTablesReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.CENTER_CODE_COLS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.COLS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.EXPORT_DATA;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for the code tables controller
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class CodeTablesControllerFastTest {

    private final Mockery context = new JUnit4Mockery();

    private CodeTablesReportService service;

    private DatareportsService commonService;

    private CodeTablesController controller;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ModelMap model;

    @Before
    public void before() throws Exception {

        service = context.mock(CodeTablesReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new CodeTablesController();

        //We use reflection to access the private field
        Field serviceControllerField = controller.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(controller, service);

        Field commonServiceControllerField = controller.getClass().getDeclaredField("commonService");
        commonServiceControllerField.setAccessible(true);
        commonServiceControllerField.set(controller, commonService);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        model = new ModelMap();
    }

    @Test
    public void testCodeTablesReportExportHandler4TissueSourceSite() throws Exception {
        final String code = "tissueSourceSite";
        final List<TissueSourceSite> mockTissueSourceSite = makeMockCollectionCenter();
        context.checking(new Expectations() {{
            allowing(service).getTissueSourceSite();
            will(returnValue(mockTissueSourceSite));
            allowing(service).getTissueSourceSiteComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockTissueSourceSite, null, null, null);
            will(returnValue(mockTissueSourceSite));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.codeTablesExportHandler(model, code, "tab", null, null);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals(code, title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals(code + ".txt", fileName);
        List<TissueSourceSite> data = (List<TissueSourceSite>) model.get("data");
        assertEquals(mockTissueSourceSite, data);
    }

    @Test
    public void testCodeTablesReportExportHandler4CenterCode() throws Exception {
        final String code = "centerCode";
        final List<CenterCode> mockCenterCode = makeMockCenterCode();
        context.checking(new Expectations() {{
            allowing(service).getCenterCode();
            will(returnValue(mockCenterCode));
            allowing(service).getCenterCodeComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockCenterCode, null, null, null);
            will(returnValue(mockCenterCode));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.codeTablesExportHandler(model, code, "tab", null, null);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals(code, title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals(code + ".txt", fileName);
        List<CenterCode> data = (List<CenterCode>) model.get("data");
        assertEquals(mockCenterCode, data);
    }

    @Test
    public void testCodeTablesReportExportHandler4DataType() throws Exception {
        final String code = "dataType";
        final List<DataType> mockDataType = makeMockDataType();
        context.checking(new Expectations() {{
            allowing(service).getDataType();
            will(returnValue(mockDataType));
            allowing(service).getDataTypeComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockDataType, null, null, null);
            will(returnValue(mockDataType));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.codeTablesExportHandler(model, code, "tab", null, null);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals(code, title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals(code + ".txt", fileName);
        List<DataType> data = (List<DataType>) model.get("data");
        assertEquals(mockDataType, data);
    }

    @Test
    public void testCodeTablesReportExportHandler4DiseaseStudy() throws Exception {
        final String code = "diseaseStudy";
        final List<Tumor> mockTumor = makeMockTumor();
        context.checking(new Expectations() {{
            allowing(service).getTumor();
            will(returnValue(mockTumor));
            allowing(service).getTumorComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockTumor, null, null, null);
            will(returnValue(mockTumor));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.codeTablesExportHandler(model, code, "tab", null, null);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals(code, title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals(code + ".txt", fileName);
        List<Tumor> data = (List<Tumor>) model.get("data");
        assertEquals(mockTumor, data);
    }

    @Test
    public void testCodeTablesReportExportHandler4PlatformCode() throws Exception {
        final String code = "platformCode";
        final List<PlatformCode> mockPlatformCode = makeMockPlatformCode();
        context.checking(new Expectations() {{
            allowing(service).getPlatformCode();
            will(returnValue(mockPlatformCode));
            allowing(service).getPlatformCodeComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockPlatformCode, null, null, null);
            will(returnValue(mockPlatformCode));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.codeTablesExportHandler(model, code, "tab", null, null);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals(code, title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals(code + ".txt", fileName);
        List<PlatformCode> data = (List<PlatformCode>) model.get("data");
        assertEquals(mockPlatformCode, data);
    }

    @Test
    public void testCodeTablesReportExportHandler4BcrBatchCode() throws Exception {
        final String code = "bcrBatchCode";
        final List<BcrBatchCode> mockBcrBatchCode = makeMockBcrBatchCode();
        context.checking(new Expectations() {{
            allowing(service).getBcrBatchCode();
            will(returnValue(mockBcrBatchCode));
            allowing(service).getBcrBatchCodeComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockBcrBatchCode, null, null, null);
            will(returnValue(mockBcrBatchCode));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.codeTablesExportHandler(model, code, "tab", null, null);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals(code, title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals(code + ".txt", fileName);
        List<BcrBatchCode> data = (List<BcrBatchCode>) model.get("data");
        assertEquals(mockBcrBatchCode, data);
    }

    @Test
    public void testCodeTablesReportExportHandler4DataLevel() throws Exception {
        final String code = "dataLevel";
        final List<CodeReport> mockCodeReport = makeMockCodeReport();
        context.checking(new Expectations() {{
            allowing(service).getDataLevel();
            will(returnValue(mockCodeReport));
            allowing(service).getCodeReportComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockCodeReport, null, null, null);
            will(returnValue(mockCodeReport));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.codeTablesExportHandler(model, code, "tab", null, null);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals(code, title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals(code + ".txt", fileName);
        List<CodeReport> data = (List<CodeReport>) model.get("data");
        assertEquals(mockCodeReport, data);
    }

    @Test
    public void testCodeTablesReportExportHandler4Analyte() throws Exception {
        final String code = "portionAnalyte";
        final List<CodeReport> mockCodeReport = makeMockCodeReport();
        context.checking(new Expectations() {{
            allowing(service).getPortionAnalyte();
            will(returnValue(mockCodeReport));
            allowing(service).getCodeReportComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockCodeReport, null, null, null);
            will(returnValue(mockCodeReport));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.codeTablesExportHandler(model, code, "tab", null, null);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals(code, title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals(code + ".txt", fileName);
        List<CodeReport> data = (List<CodeReport>) model.get("data");
        assertEquals(mockCodeReport, data);
    }

    @Test
    public void testCodeTablesReportExportHandler4SampleType() throws Exception {
        final String code = "sampleType";
        final List<CodeReport> mockCodeReport = makeMockCodeReport();
        context.checking(new Expectations() {{
            allowing(service).getSampleType();
            will(returnValue(mockCodeReport));
            allowing(service).getCodeReportComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockCodeReport, null, null, null);
            will(returnValue(mockCodeReport));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.codeTablesExportHandler(model, code, "tab", null, null);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals(code, title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals(code + ".txt", fileName);
        List<CodeReport> data = (List<CodeReport>) model.get("data");
        assertEquals(mockCodeReport, data);
    }

    @Test
    public void testCodeTablesReportExportHandler4Tissue() throws Exception {
        final String code = "tissue";
        final List<String> mockTissue = makeMockTissue();
        context.checking(new Expectations() {{
            allowing(service).getTissue();
            will(returnValue(mockTissue));
            allowing(commonService).getSortedList(mockTissue, null, null, null);
            will(returnValue(mockTissue));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.codeTablesExportHandler(model, code, "tab", null, null);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals(code, title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals(code + ".txt", fileName);
        List<String> data = (List<String>) model.get("data");
        assertEquals(mockTissue, data);
    }

    @Test
    public void testProcessComplexExport() throws Exception {
        final ModelMap map = new ModelMap();
        final List<CenterCode> mockCenterCode = makeMockCenterCode();
        context.checking(new Expectations() {{
            allowing(service).getCenterCode();
            will(returnValue(mockCenterCode));
            allowing(service).getCenterCodeComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockCenterCode, null, null, null);
            will(returnValue(mockCenterCode));
        }});
        Method m = controller.getClass().getDeclaredMethod("processComplexExport",
                ModelMap.class, String.class, Map.class, String.class, String.class);
        m.setAccessible(true);
        m.invoke(controller, map, "CenterCode", CENTER_CODE_COLS, null, null);
        assertTrue(map.size() > 0);
        assertEquals(CENTER_CODE_COLS, map.get(COLS));
        assertEquals(mockCenterCode, map.get(EXPORT_DATA));
    }

    @Test
    public void testprocessSimpleExport() throws Exception {
        final ModelMap map = new ModelMap();
        final List<CenterCode> mockCenterCode = makeMockCenterCode();
        context.checking(new Expectations() {{
            allowing(service).getCenterCode();
            will(returnValue(mockCenterCode));
        }});
        Method m = controller.getClass().getDeclaredMethod("processSimpleExport",
                ModelMap.class, String.class, Map.class);
        m.setAccessible(true);
        m.invoke(controller, map, "CenterCode", CENTER_CODE_COLS);
        assertTrue(map.toString(), map.size() > 0);
        assertEquals(CENTER_CODE_COLS, map.get(COLS));
        assertEquals(mockCenterCode, map.get(EXPORT_DATA));
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

    public ViewAndExtensionForExport mockViewAndExt() {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        vae.setExtension(".txt");
        vae.setView("txt");
        return vae;
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

}//End of class
