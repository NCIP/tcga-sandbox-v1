/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

/**
 * Test class for UUIDController class
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class UUIDControllerFastTest {

    private final JUnit4Mockery context = new JUnit4Mockery();
    private UUIDController controller;
    private UUIDService uuidService;
    private HttpSession session;

    @Before
    public void setup() {

        final MockHttpServletRequest request = new MockHttpServletRequest();
        session = request.getSession();
        controller = new UUIDController();
        uuidService = context.mock(UUIDService.class);
        controller.setUuidService(uuidService);
        final UUIDReportService uuidReportService = new UUIDReportServiceImpl();
        controller.setUuidReportService(uuidReportService);
    }

    @Test
    public void testGetCenterData(){

        final List<Center> centers = new ArrayList<Center>();
        centers.add(new Center());
        centers.add(new Center());

        context.checking(new Expectations() {{
            one(uuidService).getCenters();
            will(returnValue(centers));
        }});
        ModelMap retMap = controller.getCenterData(new ModelMap());
        assertNotNull(retMap);
        List<Center> centerListRet = (List<Center>) retMap.get("centerData");
        assertEquals(2, centerListRet.size());
    }

   @Test
    public void testGetActiveDiseases(){

        final List<Tumor> tumors = new ArrayList<Tumor>();
        tumors.add(new Tumor());
        tumors.add(new Tumor());

        context.checking(new Expectations() {{
            one(uuidService).getActiveDiseases();
            will(returnValue(tumors));
        }});
        ModelMap retMap = controller.getActiveDiseases(new ModelMap());
        assertNotNull(retMap);
        List<Center> centerListRet = (List<Center>) retMap.get("diseases");
        assertEquals(2, centerListRet.size());
    }


    @Test
    public void testGetUUIDDetails() throws UUIDException {
        final String uuid = "mammoth";
        Center center = new Center();
        center.setCenterId(1);
        center.setCenterName("");
        final UUIDDetail detail = new UUIDDetail(uuid, new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user");
        
        context.checking(new Expectations() {{
            one(uuidService).getUUIDDetails(uuid);
            will(returnValue(detail));
        }});
        
        ModelMap retMap = controller.getUUIDDetails(new ModelMap(), uuid);
        assertNotNull(retMap);
        UUIDDetail detailRet = (UUIDDetail) retMap.get("uuidDetail");
        assertNotNull(detailRet);
        assertEquals(uuid, detailRet.getUuid());
    }

    @Test
    public void testGetUploadResults() {
        List<UUIDDetail> list = new ArrayList<UUIDDetail>();
        UUIDDetail uuid = new UUIDDetail();
        uuid.setUuid("uuid1");
        list.add(uuid);
        session.setAttribute("listOfUploadedUUIDs", list);
        
        ModelMap retMap = controller.getUploadResults(new ModelMap(), session);
        assertNotNull(retMap);
        List<UUIDDetail> detailRet = (List<UUIDDetail>) retMap.get("uploadedUUIDs");
        assertNotNull(detailRet);
        assertEquals("uuid1", detailRet.get(0).getUuid());
    }

    @Test
    public void testGetListOfGeneratedUUIDs() {
        List<UUIDDetail> list = new ArrayList<UUIDDetail>();
        UUIDDetail uuid = new UUIDDetail();
        uuid.setUuid("uuid1");
        list.add(uuid);
        session.setAttribute("listOfGeneratedUUIDs", list);

        ModelMap retMap = controller.getListOfGeneratedUUIDs(new ModelMap(), session);
        assertNotNull(retMap);
        List<UUIDDetail> detailRet = (List<UUIDDetail>) retMap.get("listOfGeneratedUUIDs");
        assertNotNull(detailRet);
        assertEquals("uuid1", detailRet.get(0).getUuid());
    }

}
