/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDBrowserService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDCommonService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ModelMap;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BARCODE_FIELD;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.FILE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_COLS;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_FIELD;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for the uuid browser controller
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class UUIDBrowserControllerFastTest {

    private final Mockery context = new JUnit4Mockery();

    private UUIDBrowserService service;

    private UUIDCommonService commonService;

    private UUIDBrowserController controller;

    private MockHttpServletRequest request;

    private MockHttpSession session;

    @Before
    public void before() throws Exception {

        service = context.mock(UUIDBrowserService.class);
        commonService = context.mock(UUIDCommonService.class);
        controller = new UUIDBrowserController();

        //We use reflection to access the private field
        Field serviceControllerField = controller.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(controller, service);

        Field commonServiceControllerField = controller.getClass().getDeclaredField("commonService");
        commonServiceControllerField.setAccessible(true);
        commonServiceControllerField.set(controller, commonService);

        request = new MockHttpServletRequest();
        session = new MockHttpSession();
    }

    @Test
    public void testUuidBrowserHomeHandler() throws Exception {
        request.setMethod("GET");
        final String viewName = controller.uuidBrowserHomeHandler();
        assertTrue(viewName != null);
        assertEquals("uuidBrowser", viewName);
    }

    @Test
    public void testUUIDBrowserExportHandler() throws Exception {
        final String sort = "";
        final String dir = "";
        final String columns = "disease,uuid,platform";
        final String searchParams = "";
        final List<BiospecimenMetaData> mockUUID = makeMockUUIDBrowser();
        context.checking(new Expectations() {{
            allowing(service).getAllBiospecimenMetadata();
            will(returnValue(mockUUID));
            allowing(service).getSearchBiospecimenMetadataList(mockUUID, null, null, null, null);
            will(returnValue(mockUUID));
            allowing(service).getUUIDBrowserComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockUUID, null, sort, dir);
            will(returnValue(mockUUID));
            allowing(commonService).buildReportColumns(UUID_BROWSER_COLS, columns);
            will(returnValue(mockMapCols));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
            allowing(commonService).processJsonSingleFilter(UUID_FIELD, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(BARCODE_FIELD, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(FILE, "");
            will(returnValue(null));
        }});

        request.setMethod("GET");
        ModelMap model = new ModelMap();
        final String viewName = controller.uuidBrowserExportHandler(model, session, "tab", sort, dir, "123", searchParams, columns);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals("uuidBrowser", title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals("uuidBrowser.txt", fileName);
        List<BiospecimenMetaData> data = (List<BiospecimenMetaData>) model.get("data");
        assertEquals(mockUUID, data);
    }

    @Test
    public void testUUIDBrowserExportHandlerWithSessionUploadData() throws Exception {
        final String sort = "";
        final String dir = "";
        final String columns = "disease,uuid,platform";
        final String searchParams = "{ upload complete";
        final List<BiospecimenMetaData> mockUUID = makeMockUUIDBrowser();
        context.checking(new Expectations() {{
            allowing(service).getAllBiospecimenMetadata();
            will(returnValue(mockUUID));
            allowing(service).getUUIDBrowserComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockUUID, null, sort, dir);
            will(returnValue(mockUUID));
            allowing(commonService).buildReportColumns(UUID_BROWSER_COLS, columns);
            will(returnValue(mockMapCols));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
            allowing(commonService).processJsonSingleFilter(UUID_FIELD, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(BARCODE_FIELD, "");
            will(returnValue(null));
            allowing(commonService).processJsonMultipleFilter(FILE, "");
            will(returnValue(null));
        }});

        request.setMethod("GET");
        session.setAttribute("uploadData", mockUUID);
        ModelMap model = new ModelMap();
        final String viewName = controller.uuidBrowserExportHandler(model, session, "tab", sort, dir, "123", searchParams, columns);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals("uuidBrowser", title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals("uuidBrowser.txt", fileName);
        List<BiospecimenMetaData> data = (List<BiospecimenMetaData>) model.get("data");
        assertEquals(mockUUID, data);
    }

    public ViewAndExtensionForExport mockViewAndExt() {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        vae.setExtension(".txt");
        vae.setView("txt");
        return vae;
    }

    public Map<String, String> mockMapCols = new LinkedHashMap<String, String>() {{
        put("disease", "Disease");
        put("uuid", "UUID");
        put("platform", "Platform");
    }};

    public List<BiospecimenMetaData> makeMockUUIDBrowser() {
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
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode3");
            setUuid("3");
            setDisease("GBM");
            setPlatform("mockplatform3");
            setReceivingCenter("mockcenter3");
        }});
        return list;
    }
}
