/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

/**
 * Class for testing UUIDExportController
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

public class UUIDExportControllerFastTest {
    
    private UUIDExportController controller;
    private HttpSession session;
    private List<UUIDDetail> uuidList;

    @Before
    public void setup() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        session = request.getSession();
        controller = new UUIDExportController();
        final UUIDReportService uuidReportService = new UUIDReportServiceImpl();
        controller.setUuidReportService(uuidReportService);
        prepareMockLists();
        session.setAttribute("searchResults", uuidList);
    }

    private void prepareMockLists() {
        Center center = new Center();
        center.setCenterId(1);
        center.setCenterName("broad");
        uuidList = new ArrayList<UUIDDetail>();
        uuidList.add(new UUIDDetail("mammoth", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user"));
    }

    @Test
    public void testUUIDExportForExcel(){

        ModelMap model = new ModelMap();
        controller.uuidExportHandler(model, UUIDConstants.EXCEL, "searchResults", session);
        assertNotNull(model);
        assertEquals(UUIDConstants.EXCEL, model.get("exportType"));
        verifyData(model);
    }

    @Test
    public void testUUIDExportForCSV(){

        ModelMap model = new ModelMap();
        controller.uuidExportHandler(model, UUIDConstants.CSV, "searchResults", session);
        assertNotNull(model);
        assertEquals(UUIDConstants.CSV, model.get("exportType"));
        verifyData(model);
    }

    @Test
    public void testUUIDExportForTab(){

        ModelMap model = new ModelMap();
        controller.uuidExportHandler(model, UUIDConstants.TAB, "searchResults", session);
        assertNotNull(model);
        assertEquals(UUIDConstants.TAB, model.get("exportType"));
        verifyData(model);
    }

    private void verifyData(final ModelMap model) {
        Map<String, String> columns = (LinkedHashMap<String, String>) model.get("cols");
        assertNotNull(columns);
        assertEquals("UUID", columns.get("uuid"));
        assertEquals("Center", columns.get("center"));
        assertEquals("Created By", columns.get("createdBy"));
        assertEquals("Disease", columns.get("diseaseAbbrev"));
        assertEquals("Creation Date", columns.get("creationDate"));
        assertEquals("Creation Method", columns.get("generationMethod"));
        assertEquals("Latest Barcode", columns.get("latestBarcode"));

        List<UUIDDetail> uuidList = (List<UUIDDetail>)model.get("data");
        assertNotNull(uuidList);
        assertEquals("mammoth", uuidList.get(0).getUuid());

    }
    
    
}
