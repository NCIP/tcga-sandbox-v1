/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.LatestArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.LatestGenericReportService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ModelMap;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_FROM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_TO;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.ARCHIVE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.LATEST_ARCHIVE_COLS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test Class for the latest archive report controller
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class LatestArchiveControllerFastTest {

    private final Mockery context = new JUnit4Mockery();
    public Map<String, String> mockMapCols = new LinkedHashMap<String, String>() {{
        put("archiveName", "Archive");
        put("dateAdded", "Date Added");
        put("archiveType", "Archive Type");
    }};
    private LatestGenericReportService service;
    private DatareportsService commonService;
    private LatestArchiveController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    private ModelMap model;

    @Before
    public void before() throws Exception {

        service = context.mock(LatestGenericReportService.class);
        commonService = context.mock(DatareportsService.class);
        controller = new LatestArchiveController();

        //We use reflection to access the private field
        Field serviceControllerField = controller.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(controller, service);

        Field commonServiceControllerField = controller.getClass().getDeclaredField("commonService");
        commonServiceControllerField.setAccessible(true);
        commonServiceControllerField.set(controller, commonService);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        model = new ModelMap();
    }

    @Test
    public void testLatestArchiveReportFullHandler() throws Exception {
        request.setMethod("GET");
        final List<LatestArchive> mockbio = makeMockLatestArchive();
        context.checking(new Expectations() {{
            allowing(service).getLatestArchive();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("latestArchive", mockbio, model, request);
        }});
        final String viewName = controller.latestArchiveReportFullHandler(model, session, request,
                null, null, null);
        assertTrue(viewName != null);
        assertEquals(LatestGenericReportConstants.LATEST_ARCHIVE_REPORT_VIEW, viewName);
    }

    @Test
    public void testLatestArchiveSimpleHandler() throws Exception {
        final List<LatestArchive> mockbio = makeMockLatestArchive();
        context.checking(new Expectations() {{
            allowing(service).getLatestArchive();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("latestArchive", mockbio, model, request);
        }});
        final String viewName = controller.latestArchiveReportSimpleHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("latestArchiveReport", viewName);
    }

    @Test
    public void testLatestArchiveSimpleHandlerWithSessionFull() throws Exception {
        session.setAttribute("latestArchiveFilterModel", new ModelMap() {{
            put("pif", "paf");
        }});
        final List<LatestArchive> mockbio = makeMockLatestArchive();
        context.checking(new Expectations() {{
            allowing(service).getLatestArchive();
            will(returnValue(mockbio));
            allowing(commonService).processDisplayTag("latestArchive", mockbio, model, request);
        }});
        final String viewName = controller.latestArchiveReportSimpleHandler(model, session, request);
        assertTrue(viewName != null);
        assertEquals("latestArchiveReport", viewName);
        assertTrue(model.get("pif") != null);
        assertEquals("paf", model.get("pif"));
    }

    @Test
    public void testLatestArchiveReportExportHandler() throws Exception {
        final String sort = "";
        final String dir = "";
        final String columns = "archiveName,dateAdded,archiveType";
        final String filterReq = "";
        final String filterForm = "";
        final List<LatestArchive> mockArch = makeMockLatestArchive();
        context.checking(new Expectations() {{
            allowing(service).getLatestArchive();
            will(returnValue(mockArch));
            allowing(service).getFilteredLatestArchiveList(mockArch, null, null, null);
            will(returnValue(mockArch));
            allowing(service).getLatestArchiveComparator();
            will(returnValue(null));
            allowing(commonService).getSortedList(mockArch, null, sort, dir);
            will(returnValue(mockArch));
            allowing(commonService).buildReportColumns(LATEST_ARCHIVE_COLS, columns);
            will(returnValue(mockMapCols));
            allowing(commonService).getViewAndExtForExport("tab");
            will(returnValue(mockViewAndExt()));
            allowing(commonService).processJsonMultipleFilter(ARCHIVE_TYPE, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(DATE_FROM, "");
            will(returnValue(null));
            allowing(commonService).processJsonSingleFilter(DATE_TO, "");
            will(returnValue(null));
        }});

        request.setMethod("GET");
        final String viewName = controller.latestArchiveExportHandler(model, "tab", sort, dir, columns,
                filterReq, filterForm);
        assertTrue(viewName != null);
        assertEquals("txt", viewName);
        String title = (String) model.get("title");
        assertEquals("latestArchiveReport", title);
        String exportType = (String) model.get("exportType");
        assertEquals("tab", exportType);
        String fileName = (String) model.get("fileName");
        assertEquals("latestArchiveReport.txt", fileName);
        List<LatestArchive> data = (List<LatestArchive>) model.get("data");
        assertEquals(mockArch, data);
    }

    public ViewAndExtensionForExport mockViewAndExt() {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        vae.setExtension(".txt");
        vae.setView("txt");
        return vae;
    }

    public List<LatestArchive> makeMockLatestArchive() {
        List<LatestArchive> list = new LinkedList<LatestArchive>();
        list.add(new LatestArchive() {{
            setArchiveName("mockarchive1");
            setDateAdded(new java.util.Date(123456789));
            setArchiveUrl("mockarchiveurl1");
            setArchiveType("mockarchivetype1");
            setSdrfName("mocksdrf1");
            setSdrfUrl("mocksdrfurl1");
            setMafName("mockmaf1");
            setMafUrl("mockmafurl1");
        }});
        list.add(new LatestArchive() {{
            setArchiveName("mockarchive2");
            setDateAdded(new java.util.Date(123654789));
            setArchiveUrl("mockarchiveurl2");
            setArchiveType("mockarchivetype2");
            setSdrfName("mocksdrf2");
            setSdrfUrl("mocksdrfurl2");
            setMafName("mockmaf2");
            setMafUrl("mockmafurl2");
        }});
        list.add(new LatestArchive() {{
            setArchiveName("mockarchive3");
            setDateAdded(new java.util.Date(987654321));
            setArchiveUrl("mockarchiveurl3");
            setArchiveType("mockarchivetype3");
            setSdrfName("mocksdrf3");
            setSdrfUrl("mocksdrfurl3");
            setMafName("mockmaf3");
            setMafUrl("mockmafurl3");
        }});
        return list;
    }

}//End of Class
