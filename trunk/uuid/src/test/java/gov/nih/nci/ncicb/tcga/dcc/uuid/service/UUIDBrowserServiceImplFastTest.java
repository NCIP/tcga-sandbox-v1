/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDType;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDServiceImpl;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.uuid.dao.UUIDBrowserDAO;
import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for the UUID Browser service layer
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class UUIDBrowserServiceImplFastTest {

    private Mockery context;

    private UUIDBrowserDAO uuidBrowserDAO;

    private UUIDBrowserServiceImpl uuidBrowserServiceImpl;

    private UUIDCommonServiceImpl uuidCommonService;

    private UUIDServiceImpl uuidService;

    private HttpServletRequest request;

    private static final String SAMPLE_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        uuidBrowserDAO = context.mock(UUIDBrowserDAO.class);
        request = context.mock(HttpServletRequest.class);
        uuidBrowserServiceImpl = new UUIDBrowserServiceImpl();
        uuidCommonService = new UUIDCommonServiceImpl();
        uuidService = new UUIDServiceImpl();
        final Field daoServiceField = uuidBrowserServiceImpl.getClass().getDeclaredField("uuidBrowserDAO");
        final Field commonServiceField = uuidBrowserServiceImpl.getClass().getDeclaredField("uuidCommonService");
        final Field serviceField = uuidBrowserServiceImpl.getClass().getDeclaredField("uuidService");
        serviceField.setAccessible(true);
        daoServiceField.setAccessible(true);
        commonServiceField.setAccessible(true);
        daoServiceField.set(uuidBrowserServiceImpl, uuidBrowserDAO);
        commonServiceField.set(uuidBrowserServiceImpl, uuidCommonService);
        serviceField.set(uuidBrowserServiceImpl, uuidService);
    }

    @Test
    public void testGetUUIDFromBarcode() throws Exception {
        context.checking(new Expectations() {{
            one(uuidBrowserDAO).getBiospecimenMetaDataRowsFromBarcode("mockbarcode1");
            will(returnValue(makeMockUUIDBrowserRows1()));
        }});
        final List<BiospecimenMetaData> uuidList = uuidBrowserServiceImpl.getBiospecimenMetadataFromBarcode("mockbarcode1");
        assertNotNull(uuidList);
        assertEquals(1, uuidList.size());
        assertEquals("GBM", uuidList.get(0).getDisease());
    }


    @Test
    public void getExistingBarcodes() throws Exception {
        final List<String> barcodes = Arrays.asList("Barcode1", "Barcode2");
        context.checking(new Expectations() {{
            one(uuidBrowserDAO).getExistingBarcodes(barcodes);
            will(returnValue(barcodes));
        }});
        final List<String> existingBarcodes = uuidBrowserServiceImpl.getExistingBarcodes(barcodes);
        assertNotNull(barcodes);
        assertEquals(2, barcodes.size());

    }

    @Test
    public void testGetUUIDFromUUID() throws Exception {
        context.checking(new Expectations() {{
            one(uuidBrowserDAO).getBiospecimenMetaDataRowsFromUUID("2");
            will(returnValue(makeMockUUIDBrowserRows2()));
        }});
        final List<BiospecimenMetaData> uuidList = uuidBrowserServiceImpl.getBiospecimenMetadataFromUUID("2");
        assertNotNull(uuidList);
        assertEquals(1, uuidList.size());
        assertEquals("OV", uuidList.get(0).getDisease());
    }

    @Test
    public void testGetUUIDFromUUIDs() throws Exception {
        final List<String> uuids = new LinkedList<String>() {{
            add("1");
            add("2");
        }};
        context.checking(new Expectations() {{
            one(uuidBrowserDAO).getBiospecimenMetaDataRowsFromMultipleUUID(uuids);
            will(returnValue(makeMockUUIDBrowserRows3()));
        }});
        final List<BiospecimenMetaData> uuidList = uuidBrowserServiceImpl.getBiospecimenMetadataFromMultipleUUID(uuids);
        assertNotNull(uuidList);
        assertEquals(2, uuidList.size());
        assertEquals("OV", uuidList.get(1).getDisease());
    }

    @Test
    public void testGetAllUuidTypes() throws Exception {
        context.checking(new Expectations() {{
            one(uuidBrowserDAO).getUuidTypes();
            will(returnValue(makeMockUUIDBrowserRows4()));
        }});
        final List<ExtJsFilter> uuidList = uuidBrowserServiceImpl.getAllUuidTypes();
        assertNotNull(uuidList);
        assertEquals(10, uuidList.size());
        assertEquals("Participant", uuidList.get(0).getText());
        assertEquals("Sample", uuidList.get(1).getText());
        assertEquals("Portion", uuidList.get(2).getText());
        assertEquals("Analyte", uuidList.get(3).getText());
        assertEquals("Slide", uuidList.get(4).getText());
        assertEquals("Aliquot", uuidList.get(5).getText());
        assertEquals("(Radiation)", uuidList.get(6).getText());
        assertEquals("(Drug)", uuidList.get(7).getText());
        assertEquals("(Examination)", uuidList.get(8).getText());
        assertEquals("(Surgery)", uuidList.get(9).getText());
    }

    @Test
    public void testBarcodeRegex() throws Exception {
        final String barcode1 = "TCGA*";
        final String barcode2 = "*0012*1C*";
        assertTrue(uuidBrowserServiceImpl.getBarcodeRegexMatch(barcode1, "TCGA-01-1234"));
        assertFalse(uuidBrowserServiceImpl.getBarcodeRegexMatch(barcode1, "testouille"));
        assertTrue(uuidBrowserServiceImpl.getBarcodeRegexMatch(barcode2, "TCGA-01-0012-12341C-01"));
        assertTrue(uuidBrowserServiceImpl.getBarcodeRegexMatch(barcode2, "TCGA-01-0012-12341C"));
        assertFalse(uuidBrowserServiceImpl.getBarcodeRegexMatch(barcode2, "TCGA-01-0012-12343C-01"));
        assertFalse(uuidBrowserServiceImpl.getBarcodeRegexMatch(barcode2, "TCGA-01-0022-12341C-01"));
    }

    @Test
    public void testGetRootParticipantUUID() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserDAO).getUUIDRows();
            will(returnValue(makeMockUUIDBrowserRowsWithParentRelationShip()));
        }});
        final String res = uuidBrowserServiceImpl.getRootParticipantUUID("3");
        assertEquals("1", res);
    }

    @Test
    public void testGetAnalyteTypeCode() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserDAO).getPortionAnalytes();
            will(returnValue(makeMockPortionAnalyte()));
        }});
        final String res = uuidBrowserServiceImpl.getAnalyteTypeCode("RNA,DNA");
        assertNotNull(res);
        assertEquals("R,D", res);
    }

    @Test
    public void testGetSampleTypeCode() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserDAO).getSampleTypes();
            will(returnValue(makeMockSampleType()));
        }});
        final String res = uuidBrowserServiceImpl.getSampleTypeCode("Cell Line Control,Solid Tissue Normal");
        assertNotNull(res);
        assertEquals("20,11", res);
    }

    @Test
    public void testTrimBCR() throws Exception {
        final String bcr = "bcr1 (BCR),bcr2 (BCR)";
        final String res = uuidBrowserServiceImpl.trimBCR(bcr);
        assertNotNull(res);
        assertEquals("bcr1,bcr2", res);
    }

    @Test
    public void testReceivingCenterComboWithBcrCode() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserDAO).getCentersWithBCRCode();
            will(returnValue(makeMockCenterWithBCRCode()));
        }});
        final List<String> centerTypeList = new LinkedList<String>() {{
            add("GDAC");
        }};
        final List<String> centerList = uuidBrowserServiceImpl.receivingCenterCombo(makeMockReceivingCenterCombo(), centerTypeList);
        assertNotNull(centerList);
        assertEquals(3, centerList.size());
        assertEquals("center 1 (GDAC)", centerList.get(0));
        assertEquals("center 2 (GDAC)", centerList.get(1));
        assertEquals("center with bcr code (GDAC)", centerList.get(2));
    }

    @Test
    public void testReceivingCenterComboWithBcrCodeCenterTypeConflict() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserDAO).getCentersWithBCRCode();
            will(returnValue(makeMockCenterWithBCRCode()));
        }});
        final List<String> centerTypeList = new LinkedList<String>() {{
            add("GSC");
        }};
        final List<String> centerList = uuidBrowserServiceImpl.receivingCenterCombo(makeMockReceivingCenterCombo(), centerTypeList);
        assertNotNull(centerList);
        assertEquals(3, centerList.size());
        assertEquals("center 1 (GSC)", centerList.get(0));
        assertEquals("center 2 (GSC)", centerList.get(1));
        assertEquals("Error centerType conflict", centerList.get(2));
    }

    @Test
    public void testBuildWSUrl() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getContextPath();
            will(returnValue("myWebApp"));
            allowing(uuidBrowserDAO).getSampleTypes();
            will(returnValue(makeMockSampleType()));
            allowing(uuidBrowserDAO).getPortionAnalytes();
            will(returnValue(makeMockPortionAnalyte()));
        }});
        final String filter = "{\"uuidTypeCombo\":\"Participant,Aliquot\",\"" +
                "platformCombo\":\"\",\"participant\":\"\",\"batch\":\"\",\"" +
                "diseaseChoiceCombo\":[[\"Abbreviation\"]],\"diseaseCombo\":\"" +
                "COAD,GBM\",\"sampleChoiceCombo\":[[\"Type\"]],\"sampleCombo\":\"" +
                "Bone Marrow Normal,Cell Line Control\",\"vial\":\"\",\"portion\":\"\",\"" +
                "analyteChoiceCombo\":[[\"Type\"]],\"analyteCombo\":\"DNA\",\"plate\":\"\",\"" +
                "bcrChoiceCombo\":[[\"Name\"]],\"bcrCombo\":\"intgen.org (BCR)\",\"tssChoiceCombo" +
                "\":\"TSS ID\",\"tssCombo\":\"36,GA,GZ\",\"tissueSourceSite\":\"AA\",\"centerChoiceCombo" +
                "\":[[\"Name\"]],\"centerCombo\":\"mdanderson.org\",\"centerTypeCombo\":\"CGCC\",\"" +
                "updateBefore\":\"01/05/2011\",\"updateAfter\":\"\"}";
        final String res = uuidBrowserServiceImpl.buildWSUrl(request, filter, "xml");
        assertNotNull(res);
        assertEquals("myWebApp/uuidws/metadata/xml?elementType=Participant,Aliquot&disease=COAD,GBM&" +
                "sampleType=20&analyteType=D&bcr=intgen.org&tss=AA&center=mdanderson.org&centerType=CGCC&" +
                "updatedBefore=01/05/2011", res);
    }

    @Test
    public void testBuildWSUrlWithCenterBcrCode() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getContextPath();
            will(returnValue("myWebApp"));
            allowing(uuidBrowserDAO).getSampleTypes();
            will(returnValue(makeMockSampleType()));
            allowing(uuidBrowserDAO).getPortionAnalytes();
            will(returnValue(makeMockPortionAnalyte()));
        }});
        final String filter = "{\"uuidTypeCombo\":\"Participant,Aliquot\",\"" +
                "platformCombo\":\"\",\"participant\":\"\",\"batch\":\"\",\"" +
                "diseaseChoiceCombo\":[[\"Abbreviation\"]],\"diseaseCombo\":\"" +
                "COAD,GBM\",\"sampleChoiceCombo\":[[\"Type\"]],\"sampleCombo\":\"" +
                "Bone Marrow Normal,Cell Line Control\",\"vial\":\"\",\"portion\":\"\",\"" +
                "analyteChoiceCombo\":[[\"Type\"]],\"analyteCombo\":\"DNA\",\"plate\":\"\",\"" +
                "bcrChoiceCombo\":[[\"Name\"]],\"bcrCombo\":\"intgen.org (BCR)\",\"tssChoiceCombo" +
                "\":\"TSS ID\",\"tssCombo\":\"36,GA,GZ\",\"tissueSourceSite\":\"AA\",\"centerChoiceCombo" +
                "\":[[\"Name\"]],\"centerCombo\":\"mdanderson.org~09,broad.mit.edu~08\",\"centerTypeCombo\":\"CGCC\",\"" +
                "updateBefore\":\"01/05/2011\",\"updateAfter\":\"\"}";
        final String res = uuidBrowserServiceImpl.buildWSUrl(request, filter, "xml");
        assertNotNull(res);
        assertEquals("myWebApp/uuidws/metadata/xml?elementType=Participant,Aliquot&disease=COAD,GBM&" +
                "sampleType=20&analyteType=D&bcr=intgen.org&tss=AA&center=09,08&centerType=CGCC&" +
                "updatedBefore=01/05/2011", res);
    }

    @Test
    public void testParseUploadFileAllBarcodes() throws UUIDException, IOException {
        FileInputStream fileInputStream = null;
        try {
            String testDir = SAMPLE_DIR + "upload/";
            File uploadFile = new File(testDir + "uuidUploadAllBarcode.txt");
            fileInputStream = new FileInputStream(uploadFile);
            final Map<String, Object> resMap = uuidBrowserServiceImpl.parseUploadFile(fileInputStream);
            final List<String> res = (List<String>) resMap.get("uuidList");
            final Boolean hasBarcode = (Boolean) resMap.get("hasBarcode");
            final Boolean allBarcode = (Boolean) resMap.get("allBarcode");
            assertNotNull(resMap);
            assertEquals(3, resMap.size());
            assertNotNull(res);
            assertEquals(5, res.size());
            assertEquals("TCGA-01-1234-56789", res.get(0));
            assertEquals(Boolean.TRUE, hasBarcode);
            assertEquals(Boolean.TRUE, allBarcode);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    @Test
    public void testParseUploadFileAllUUID() throws UUIDException, IOException {
        FileInputStream fileInputStream = null;
        try {
            String testDir = SAMPLE_DIR + "upload/";
            File uploadFile = new File(testDir + "uuidUpload.txt");
            fileInputStream = new FileInputStream(uploadFile);
            final Map<String, Object> resMap = uuidBrowserServiceImpl.parseUploadFile(fileInputStream);
            final List<String> res = (List<String>) resMap.get("uuidList");
            final Boolean hasBarcode = (Boolean) resMap.get("hasBarcode");
            final Boolean allBarcode = (Boolean) resMap.get("allBarcode");
            assertNotNull(resMap);
            assertEquals(3, resMap.size());
            assertNotNull(res);
            assertEquals(2, res.size());
            assertEquals("4774e379-5e08-4b41-a87d-c014d4e15afb", res.get(0));
            assertEquals(Boolean.FALSE, hasBarcode);
            assertEquals(Boolean.FALSE, allBarcode);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    @Test
    public void testParseUploadFileUUIDAndBarcode() throws UUIDException, IOException {
        FileInputStream fileInputStream = null;
        try {
            String testDir = SAMPLE_DIR + "upload/";
            File uploadFile = new File(testDir + "uuidUploadBarcodeAndUUID.txt");
            fileInputStream = new FileInputStream(uploadFile);
            final Map<String, Object> resMap = uuidBrowserServiceImpl.parseUploadFile(fileInputStream);
            final List<String> res = (List<String>) resMap.get("uuidList");
            final Boolean hasBarcode = (Boolean) resMap.get("hasBarcode");
            final Boolean allBarcode = (Boolean) resMap.get("allBarcode");
            assertNotNull(resMap);
            assertEquals(3, resMap.size());
            assertNotNull(res);
            assertEquals(8, res.size());
            assertEquals("4774e379-5e08-4b41-a87d-c014d4e15afb", res.get(0));
            assertEquals("TCGA-01-1234-56789", res.get(1));
            assertEquals(Boolean.TRUE, hasBarcode);
            assertEquals(Boolean.FALSE, allBarcode);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    @Test
    public void testGetCenterCodeFromCenterCombo() throws Exception {
        final List<String> list = new LinkedList<String>() {{
            add("center1~20");
            add("center2~21");
            add("center3~20");
        }};
        final List<String> res = uuidBrowserServiceImpl.getCenterCodeFromCenterCombo(list);
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("21", res.get(0));
        assertEquals("20", res.get(1));
    }

    @Test
    public void testFilterUUIDFromBarcodeList() throws Exception {
        List<String> list = new LinkedList<String>() {{
            add("TCGA-blah");
            add("uuid1");
            add("uuid2");
        }};
        final Map resMap = uuidBrowserServiceImpl.filterUUIDFromBarcodeList(list);
        assertNotNull(resMap);
        assertEquals(2, resMap.size());
        assertEquals("[uuid1, uuid2]", resMap.get("uuidList").toString());
        assertEquals("[TCGA-blah]", resMap.get("barcodeList").toString());
    }

    public List<BiospecimenMetaData> makeMockUUIDBrowserRowsWithParentRelationShip() {
        List<BiospecimenMetaData> list = new LinkedList<BiospecimenMetaData>();
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode1");
            setUuid("1");
            setUuidType("Participant");
            setParentUUID(null);
            setDisease("GBM");
            setPlatform("mockplatform1");
            setReceivingCenter("mockcenter1");
        }});
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode2");
            setUuid("2");
            setUuidType("Sample");
            setParentUUID("1");
            setDisease("OV");
            setPlatform("mockplatform2");
            setReceivingCenter("mockcenter2");
        }});
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode3");
            setUuid("3");
            setUuidType("Portion");
            setParentUUID("2");
            setDisease("OV");
            setPlatform("mockplatform3");
            setReceivingCenter("mockcenter3");
        }});
        return list;
    }

    public List<SampleType> makeMockSampleType() {
        List<SampleType> list = new LinkedList<SampleType>();
        list.add(new SampleType() {{
            this.setDefinition("Primary solid Tumor");
            this.setSampleTypeCode("01");
            this.setIsTumor(true);
        }});
        list.add(new SampleType() {{
            this.setDefinition("Solid Tissue Normal");
            this.setSampleTypeCode("11");
            this.setIsTumor(false);
        }});
        list.add(new SampleType() {{
            this.setDefinition("Cell Line Control");
            this.setSampleTypeCode("20");
            this.setIsTumor(false);
        }});
        return list;
    }

    public List<PortionAnalyte> makeMockPortionAnalyte() {
        List<PortionAnalyte> list = new LinkedList<PortionAnalyte>();
        list.add(new PortionAnalyte() {{
            this.setDefinition("DNA");
            this.setPortionAnalyteCode("D");
        }});
        list.add(new PortionAnalyte() {{
            this.setDefinition("RNA");
            this.setPortionAnalyteCode("R");
        }});
        return list;
    }

    public List<BiospecimenMetaData> makeMockUUIDBrowserRows1() {
        List<BiospecimenMetaData> list = new LinkedList<BiospecimenMetaData>();
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode1");
            setUuid("1");
            setDisease("GBM");
            setPlatform("mockplatform1");
            setReceivingCenter("mockcenter1");
        }});
        return list;
    }

    public List<BiospecimenMetaData> makeMockUUIDBrowserRows2() {
        List<BiospecimenMetaData> list = new LinkedList<BiospecimenMetaData>();
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode2");
            setUuid("2");
            setDisease("OV");
            setPlatform("mockplatform2");
            setReceivingCenter("mockcenter2");
        }});
        return list;
    }

    public List<BiospecimenMetaData> makeMockUUIDBrowserRows3() {
        List<BiospecimenMetaData> list = new LinkedList<BiospecimenMetaData>();
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode1");
            setUuid("1");
            setDisease("GBM");
            setPlatform("mockplatform1");
            setReceivingCenter("mockcenter1");
        }});
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode2");
            setUuid("2");
            setDisease("OV");
            setPlatform("mockplatform2");
            setReceivingCenter("mockcenter2");
        }});
        return list;
    }

    public List<UUIDType> makeMockUUIDBrowserRows4() {
        List<UUIDType> list = new LinkedList<UUIDType>();
        list.add(new UUIDType(1, "Participant", 0, "patient"));
        list.add(new UUIDType(2, "Sample", 1, "sample"));
        list.add(new UUIDType(3, "Portion", 2, "portion"));
        list.add(new UUIDType(4, "Analyte", 3, "analyte"));
        list.add(new UUIDType(5, "Slide", 4, "slide"));
        list.add(new UUIDType(6, "Aliquot", 5, "aliquot"));
        list.add(new UUIDType(7, "Radiation", 6, "radiation"));
        list.add(new UUIDType(8, "Drug", 7, "drug"));
        list.add(new UUIDType(9, "Examination", 8, "examination"));
        list.add(new UUIDType(10, "Surgery", 9, "surgery"));
        return list;
    }

    public List<String> makeMockReceivingCenterCombo() {
        List<String> list = new LinkedList<String>();
        list.add("center 1");
        list.add("center 2");
        list.add("center 3~24");
        return list;
    }

    public List<Center> makeMockCenterWithBCRCode() {
        List<Center> list = new LinkedList<Center>();
        Center c = new Center();
        c.setCenterName("center with bcr code");
        c.setCenterType("GDAC");
        c.setBcrCenterId("24");
        list.add(c);
        return list;
    }
}
