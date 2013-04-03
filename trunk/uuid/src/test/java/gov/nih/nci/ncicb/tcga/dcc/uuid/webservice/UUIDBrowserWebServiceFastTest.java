/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtilFastTest;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.AliquotUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.CenterUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.MetadataSearchWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.MetadataViewWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.ShippedPortionUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDBrowserService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDCommonService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.webservice.bean.UUIDBrowserWSQueryParamBean;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for the UUID browser Webservice
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class UUIDBrowserWebServiceFastTest {

    private final Mockery context = new JUnit4Mockery();
    private UUIDBrowserService uuidBrowserService;
    private UUIDService uuidService;
    private UUIDCommonService uuidCommonService;
    private UUIDBrowserWebService webService;
    private UriInfo uriInfo;
    private UriBuilder mockUri = UriBuilder.fromUri("http://myserverisgreat.com/uuid/uuidws/xml/uuid");

    @Before
    public void before() throws Exception {
        webService = new UUIDBrowserWebService();
        uuidBrowserService = context.mock(UUIDBrowserService.class);
        uuidCommonService = context.mock(UUIDCommonService.class);
        uuidService = context.mock(UUIDService.class);
        uriInfo = context.mock(UriInfo.class);
        Field serviceControllerField = webService.getClass().getDeclaredField("uuidBrowserService");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(webService, uuidBrowserService);
        Field commonServiceControllerField = webService.getClass().getDeclaredField("uuidCommonService");
        commonServiceControllerField.setAccessible(true);
        commonServiceControllerField.set(webService, uuidCommonService);
        Field uuidServiceControllerField = webService.getClass().getDeclaredField("uuidService");
        uuidServiceControllerField.setAccessible(true);
        uuidServiceControllerField.set(webService, uuidService);
        Field uriInfoField = webService.getClass().getDeclaredField("uriInfo");
        uriInfoField.setAccessible(true);
        uriInfoField.set(webService, uriInfo);
    }

    @Test
    public void testProcessUUIDBrowserWS() throws Exception {
        final List<BiospecimenMetaData> mockList = makeMockUUIDBrowser();
        final List<String> strList = new LinkedList<String>() {{
            add("uuid1");
        }};
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getAllBiospecimenMetadata();
            will(returnValue(mockList));
            allowing(uuidBrowserService).processMultipleBiospecimenMetadata(strList, false, false);
            will(returnValue(mockList));
            allowing(uriInfo).getAbsolutePathBuilder();
            will(returnValue(mockUri));
            allowing(uuidCommonService).makeListFromString(null);
            will(returnValue(null));
            allowing(uuidBrowserService).getBiospecimenMetadataList(mockList, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null, null, null);
            will(returnValue(mockList));
        }});
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid("uuid1");
        MetadataSearchWS metadata = webService.processSearchUUIDBrowserWS(queryParamBean);
        assertNotNull(metadata);
        assertEquals(mockList.size(), metadata.getTcgaElement().size());
    }

    @Test
    public void testGetUUIDAndBarcodeList() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidService).isValidUUID("1");
            will(returnValue(true));
            allowing(uuidService).isValidUUID("2");
            will(returnValue(true));
        }});
        UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid("1,2");
        queryParamBean.setBarcode("1,2");
        Map<String, Object> res = webService.getUUIDAndBarcodeInfo(queryParamBean);
        final List<String> list = (List<String>) res.get("uuidList");
        final Boolean hasBarcode = (Boolean) res.get("hasBarcode");
        final Boolean allBarcode = (Boolean) res.get("allBarcode");
        assertNotNull(res);
        assertTrue(res.size() == 3);
        assertEquals("1", list.get(0));
        assertEquals(Boolean.TRUE, hasBarcode);
        assertEquals(Boolean.FALSE, allBarcode);
    }

    @Test
    public void testResolveAnalyteType() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getPortionAnalytes();
            will(returnValue(makeMockPortionAnalyte()));
        }});
        List<String> res = webService.resolveAnalyteType(Arrays.asList("D"));
        assertNotNull(res);
        assertTrue(res.size() == 1);
        assertEquals("DNA", res.get(0));
        assertNull(webService.resolveAnalyteType(null));
    }

    @Test
    public void testResolveAnalyteTypeBad() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getPortionAnalytes();
            will(returnValue(makeMockPortionAnalyte()));
        }});
        assertTrue(webService.resolveAnalyteType(Arrays.asList("BOB")).isEmpty());
    }

    @Test
    public void testResolveSampleType() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getSampleTypes();
            will(returnValue(makeMockSampleType()));
        }});
        List<String> res = webService.resolveSampleType(Arrays.asList("01"));
        assertNotNull(res);
        assertTrue(res.size() == 1);
        assertEquals("Primary solid Tumor", res.get(0));
        assertNull(webService.resolveSampleType(null));
    }

    @Test
    public void testResolveSampleTypeNORMAL() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getSampleTypes();
            will(returnValue(makeMockSampleType()));
        }});
        List<String> res = webService.resolveSampleType(Arrays.asList("normal"));
        assertNotNull(res);
        assertTrue(res.size() == 1);
        assertEquals("Solid Tissue Normal", res.get(0));
        assertNull(webService.resolveSampleType(null));
    }

    @Test
    public void testResolveSampleTypeTUMOR() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getSampleTypes();
            will(returnValue(makeMockSampleType()));
        }});
        List<String> res = webService.resolveSampleType(Arrays.asList("tumor"));
        assertNotNull(res);
        assertTrue(res.size() == 1);
        assertEquals("Primary solid Tumor", res.get(0));
        assertNull(webService.resolveSampleType(null));
    }

    @Test
    public void testResolveSampleTypeCELL_LINE() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getSampleTypes();
            will(returnValue(makeMockSampleType()));
        }});
        List<String> res = webService.resolveSampleType(Arrays.asList("cellLine"));
        assertNotNull(res);
        assertTrue(res.size() == 1);
        assertEquals("Cell Line Control", res.get(0));
        assertNull(webService.resolveSampleType(null));
    }

    @Test
    public void testResolveSampleTypeBad() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getSampleTypes();
            will(returnValue(makeMockSampleType()));
        }});
        assertTrue(webService.resolveSampleType(Arrays.asList("BOB")).isEmpty());
    }

    @Test
    public void testResolveSlideLayer() throws Exception {
        List<String> res = webService.resolveSlideLayer(Arrays.asList("T"));
        assertNotNull(res);
        assertTrue(res.size() == 1);
        assertEquals("top", res.get(0));
        assertNull(webService.resolveSlideLayer(null));
    }

    @Test
    public void testResolveSlideLayerBad() throws Exception {
        assertTrue(webService.resolveSlideLayer(Arrays.asList("bikini-bottom")).isEmpty());
    }

    @Test
    public void testResolveBCR() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getCenters();
            will(returnValue(makeMockBCR()));
        }});
        List<String> res = webService.resolveBCR(Arrays.asList("IGC"));
        assertNotNull(res);
        assertTrue(res.size() == 1);
        assertEquals("intgen.org (BCR)", res.get(0));
        assertNull(webService.resolveCenter(null));
    }

    @Test
    public void testResolveBCRBad() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getCenters();
            will(returnValue(makeMockBCR()));
        }});
        assertTrue(webService.resolveBCR(Arrays.asList("BOB")).isEmpty());
    }

    @Test
    public void testResolveCenter() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getCentersWithBCRCode();
            will(returnValue(makeMockCenter()));
        }});
        List<String> res = webService.resolveCenter(Arrays.asList("BI"));
        assertNotNull(res);
        assertTrue(res.size() == 1);
        assertEquals("broad.mit.edu", res.get(0));
        assertNull(webService.resolveCenter(null));
    }

    @Test
    public void testResolveCenterBad() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getCentersWithBCRCode();
            will(returnValue(makeMockCenter()));
        }});
        assertTrue(webService.resolveCenter(Arrays.asList("BOB")).isEmpty());
    }

    @Test
    public void testResolveCenterWithBCRId() throws Exception {
        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getCentersWithBCRCode();
            will(returnValue(makeMockCenter()));
        }});
        List<String> res = webService.resolveCenter(Arrays.asList("20"));
        assertNotNull(res);
        assertTrue(res.size() == 1);
        assertEquals("mdanderson.org~20", res.get(0));
        assertNull(webService.resolveCenter(null));
    }

    @Test
    public void testProcessViewUUIDBrowserWSForSample() throws Exception {

        final String uuid = "2";
        final List<BiospecimenMetaData> mockList = makeMockUUIDBrowser();
        final List<BiospecimenMetaData> searchList = new LinkedList<BiospecimenMetaData>() {{
            add(mockList.get(1));
        }};
        final List<String> strList = new LinkedList<String>() {{
            add("2");
        }};

        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getAllBiospecimenMetadata();
            will(returnValue(mockList));
            allowing(uuidBrowserService).processMultipleBiospecimenMetadata(strList, false, false);
            will(returnValue(searchList));
            allowing(uriInfo).getAbsolutePathBuilder();
            will(returnValue(mockUri));
            allowing(uuidBrowserService).getCenters();
            will(returnValue(makeMockCenter()));
            allowing(uuidBrowserService).getDiseases();
            will(returnValue(makeMockDisease()));
            allowing(uuidBrowserService).getTissueSourceSites();
            will(returnValue(makeMockTissueSourceSite()));
            allowing(uuidBrowserService).getSampleTypes();
            will(returnValue(makeMockSampleType()));
            allowing(uuidBrowserService).getAllBiospecimenMetadataParents(uuid);
            will(returnValue(mockList));
            allowing(uuidBrowserService).getSampleChildrenList(uuid, mockList);
            will(returnValue(mockList));
        }});

        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid(uuid);

        final MetadataViewWS meta = webService.processViewUUIDBrowserWS(MediaType.APPLICATION_JSON, queryParamBean);

        assertNotNull(meta);
        assertNotNull(meta.getTcgaElement());
        assertEquals(uuid, meta.getTcgaElement().getUuid());
        assertEquals("Sample", meta.getTcgaElement().getElementType());
        assertEquals("Indivumed", meta.getTcgaElement().getTss().getName());
        assertEquals("http://myserverisgreat.com/uuid/1", meta.getTcgaElement().getParticipant().getParticipant());
    }

    @Test
    public void testProcessViewUUIDBrowserWSForShippedPortion() throws Exception {

        final String uuid = "4";
        final List<BiospecimenMetaData> mockList = makeMockUUIDBrowser();
        final List<BiospecimenMetaData> searchList = new LinkedList<BiospecimenMetaData>() {{
            add(mockList.get(3));
        }};
        final List<String> strList = new LinkedList<String>() {{
            add(uuid);
        }};

        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getAllBiospecimenMetadata();
            will(returnValue(mockList));
            allowing(uuidBrowserService).processMultipleBiospecimenMetadata(strList, false, false);
            will(returnValue(searchList));
            allowing(uriInfo).getAbsolutePathBuilder();
            will(returnValue(mockUri));
            allowing(uuidBrowserService).getCenters();
            will(returnValue(makeMockCenter()));
            allowing(uuidBrowserService).getCentersWithBCRCode();
            will(returnValue(makeMockCenter()));
            allowing(uuidBrowserService).getDiseases();
            will(returnValue(makeMockDisease()));
            allowing(uuidBrowserService).getTissueSourceSites();
            will(returnValue(makeMockTissueSourceSite()));
            allowing(uuidBrowserService).getSampleTypes();
            will(returnValue(makeMockSampleType()));
            allowing(uuidBrowserService).getAllBiospecimenMetadataParents(uuid);
            will(returnValue(mockList));
            allowing(uuidBrowserService).getSampleChildrenList(uuid, mockList);
            will(returnValue(mockList));
        }});

        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid(uuid);

        final MetadataViewWS meta = webService.processViewUUIDBrowserWS(MediaType.APPLICATION_JSON, queryParamBean);

        assertNotNull(meta);
        assertNotNull(meta.getTcgaElement());
        assertEquals(uuid, meta.getTcgaElement().getUuid());
        assertEquals("shippedPortion", meta.getTcgaElement().getElementType());
        assertEquals("Indivumed", meta.getTcgaElement().getTss().getName());
        assertEquals("http://myserverisgreat.com/uuid/2", meta.getTcgaElement().getParticipant().getParticipant());

        final List<ShippedPortionUUIDWS> shippedPortionUUIDWSs = meta.getTcgaElement().getShippedPortion();
        assertNotNull(shippedPortionUUIDWSs);
        assertEquals(1, shippedPortionUUIDWSs.size());

        final ShippedPortionUUIDWS shippedPortionUUIDWS = shippedPortionUUIDWSs.get(0);
        assertNotNull(shippedPortionUUIDWS);

        final CenterUUIDWS centerUUIDWS = shippedPortionUUIDWS.getReceivingCenter();
        assertNotNull(centerUUIDWS);
        assertEquals("40", centerUUIDWS.getCode());
    }

    @Test
    public void testProcessViewUUIDBrowserWSForAliquot() throws Exception {

        final String uuid = "5";
        final List<BiospecimenMetaData> mockList = makeMockUUIDBrowser();
        final List<BiospecimenMetaData> searchList = new LinkedList<BiospecimenMetaData>() {{
            add(mockList.get(4));
        }};
        final List<String> strList = new LinkedList<String>() {{
            add(uuid);
        }};

        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getAllBiospecimenMetadata();
            will(returnValue(mockList));
            allowing(uuidBrowserService).processMultipleBiospecimenMetadata(strList, false, false);
            will(returnValue(searchList));
            allowing(uriInfo).getAbsolutePathBuilder();
            will(returnValue(mockUri));
            allowing(uuidBrowserService).getCenters();
            will(returnValue(makeMockCenter()));
            allowing(uuidBrowserService).getCentersWithBCRCode();
            will(returnValue(makeMockCenter()));
            allowing(uuidBrowserService).getDiseases();
            will(returnValue(makeMockDisease()));
            allowing(uuidBrowserService).getTissueSourceSites();
            will(returnValue(makeMockTissueSourceSite()));
            allowing(uuidBrowserService).getSampleTypes();
            will(returnValue(makeMockSampleType()));
            allowing(uuidBrowserService).getAllBiospecimenMetadataParents(uuid);
            will(returnValue(mockList));
            allowing(uuidBrowserService).getSampleChildrenList(uuid, mockList);
            will(returnValue(mockList));
        }});

        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid(uuid);

        final MetadataViewWS meta = webService.processViewUUIDBrowserWS(MediaType.APPLICATION_JSON, queryParamBean);

        assertNotNull(meta);
        assertNotNull(meta.getTcgaElement());
        assertEquals(uuid, meta.getTcgaElement().getUuid());
        assertEquals("Aliquot", meta.getTcgaElement().getElementType());
        assertEquals("Indivumed", meta.getTcgaElement().getTss().getName());
        assertEquals("http://myserverisgreat.com/uuid/1", meta.getTcgaElement().getParticipant().getParticipant());

        final List<AliquotUUIDWS> aliquotUUIDWSs = meta.getTcgaElement().getAliquot();
        assertNotNull(aliquotUUIDWSs);
        assertEquals(1, aliquotUUIDWSs.size());

        final AliquotUUIDWS aliquotUUIDWS = aliquotUUIDWSs.get(0);
        assertNotNull(aliquotUUIDWS);

        final CenterUUIDWS centerUUIDWS = aliquotUUIDWS.getReceivingCenter();
        assertNotNull(centerUUIDWS);
        assertEquals("50", centerUUIDWS.getCode());
    }

    @Test
    public void testProcessViewUUIDBrowserWSEmptyResultForUuid() throws Exception {

        final String uuid = "2";
        final List<BiospecimenMetaData> mockList = makeMockUUIDBrowser();
        final List<BiospecimenMetaData> searchList = new LinkedList<BiospecimenMetaData>();
        final List<String> strList = new LinkedList<String>() {{
            add(uuid);
        }};
        final String mediaType = MediaType.APPLICATION_JSON;

        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getAllBiospecimenMetadata();
            will(returnValue(mockList));
            allowing(uuidService).isValidUUID(uuid);
            will(returnValue(true));
            allowing(uuidBrowserService).processMultipleBiospecimenMetadata(strList, false, false);
            will(returnValue(searchList));
        }});

        try {
            final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
            queryParamBean.setUuid(uuid);
            webService.processViewUUIDBrowserWS(mediaType, queryParamBean);
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final int expectedStatusCode = HttpStatusCode.OK;
            final String expectedErrorMessage = "UUID '" + uuid + "' not found.";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), expectedStatusCode, uuid, expectedErrorMessage, mediaType);
        }
    }

    @Test
    public void testProcessViewUUIDBrowserWSEmptyResultForBarcode() throws Exception {

        final String barcode = "TCGA-B";
        final List<BiospecimenMetaData> mockList = makeMockUUIDBrowser();
        final List<BiospecimenMetaData> searchList = new LinkedList<BiospecimenMetaData>();
        final List<String> strList = new LinkedList<String>() {{
            add(barcode);
        }};
        final String mediaType = MediaType.APPLICATION_XML;

        context.checking(new Expectations() {{
            allowing(uuidBrowserService).getAllBiospecimenMetadata();
            will(returnValue(mockList));
            allowing(uuidBrowserService).processMultipleBiospecimenMetadata(strList, true, true);
            will(returnValue(searchList));
        }});

        try {
            final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
            queryParamBean.setBarcode(barcode);
            webService.processViewUUIDBrowserWS(mediaType, queryParamBean);
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final int expectedStatusCode = HttpStatusCode.OK;
            final String expectedErrorMessage = "Barcode '" + barcode + "' not found.";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), expectedStatusCode, barcode, expectedErrorMessage, mediaType);
        }
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

    public List<Center> makeMockCenter() {
        List<Center> list = new LinkedList<Center>();
        list.add(new Center() {{
            setShortName("BI");
            setBcrCenterId("01");
            setCenterName("broad.mit.edu");
        }});
        list.add(new Center() {{
            setShortName("MDA");
            setBcrCenterId("20");
            setCenterName("mdanderson.org");
        }});
        list.add(new Center() {{
            setShortName("mockcenter4");
            setBcrCenterId("40");
            setCenterName("mockcenter4.org");
            setCenterType("TEST");
        }});
        list.add(new Center() {{
            setShortName("mockcenter5");
            setBcrCenterId("50");
            setCenterName("mockcenter5.org");
            setCenterType("TEST");
        }});
        return list;
    }

    public List<Tumor> makeMockDisease() {
        List<Tumor> list = new LinkedList<Tumor>();
        list.add(new Tumor() {{
            this.setTumorName("COAD");
            this.setTumorDescription("Colon adenocarcinoma");
        }});
        list.add(new Tumor() {{
            this.setTumorName("GBM");
            this.setTumorDescription("Glioblastoma multiforme");
        }});
        return list;
    }

    public List<TissueSourceSite> makeMockTissueSourceSite() {
        List<TissueSourceSite> list = new LinkedList<TissueSourceSite>();
        list.add(new TissueSourceSite() {{
            this.setTissueSourceSiteId("AA");
            this.setName("Indivumed");
        }});
        list.add(new TissueSourceSite() {{
            this.setTissueSourceSiteId("02");
            this.setName("MD Anderson Cancer Center");
        }});
        return list;
    }

    public List<Center> makeMockBCR() {
        List<Center> list = new LinkedList<Center>();
        list.add(new Center() {{
            setShortName("IGC");
            setBcrCenterId("01");
            setCenterName("intgen.org");
            setCenterType("BCR");
        }});
        return list;
    }


    public List<BiospecimenMetaData> makeMockUUIDBrowser() {
        List<BiospecimenMetaData> list = new LinkedList<BiospecimenMetaData>();
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode1");
            setUuid("1");
            setUuidType("Participant");
            setParentUUID(null);
            setDisease("GBM");
            setPlatform("mockplatform1");
            setReceivingCenter("mockcenter1");
            setCenterCode("01");
            setTissueSourceSite("AA");
            setRedacted(false);
        }});
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode2");
            setUuid("2");
            setUuidType("Sample");
            setParentUUID("1");
            setDisease("COAD");
            setPlatform("mockplatform2");
            setReceivingCenter("mockcenter2");
            setCenterCode("20");
            setTissueSourceSite("AA");
            setRedacted(false);
        }});
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode3");
            setUuid("3");
            setUuidType("Portion");
            setParentUUID("2");
            setDisease("OV");
            setPlatform("mockplatform3");
            setReceivingCenter("mockcenter3");
            setCenterCode("30");
            setTissueSourceSite("02");
            setRedacted(false);
        }});
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode4");
            setUuid("4");
            setUuidType("shippedPortion");
            setParentUUID("3");
            setDisease("READ");
            setPlatform("mockplatform4");
            setReceivingCenter("mockcenter4.org (TEST)");
            setCenterCode("40");
            setTissueSourceSite("AA");
            setRedacted(false);
        }});
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode5");
            setUuid("5");
            setUuidType("Aliquot");
            setParentUUID("4");
            setDisease("BLCA");
            setPlatform("mockplatform5");
            setReceivingCenter("mockcenter5.org (TEST)");
            setCenterCode("50");
            setTissueSourceSite("AA");
            setRedacted(false);
        }});
        return list;
    }

}//End of class
