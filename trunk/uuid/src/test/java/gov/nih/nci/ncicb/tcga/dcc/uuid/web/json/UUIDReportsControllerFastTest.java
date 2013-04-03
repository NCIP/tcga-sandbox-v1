/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.json;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.REPORT_TYPE_MISSING_UUID;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.REPORT_TYPE_NEW_UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Duration;
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
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

/**
 * Controller for UUID reports 
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@RunWith (JMock.class)
public class UUIDReportsControllerFastTest {

    private final JUnit4Mockery context = new JUnit4Mockery();
    private UUIDReportsController controller;
    private UUIDService uuidService;
    private final Duration duration = Duration.Week;
    private List<UUIDDetail> list;
    private HttpSession session;


    @Before
    public void setup() {

        final MockHttpServletRequest request = new MockHttpServletRequest();
        session = request.getSession();
        controller = new UUIDReportsController();
        uuidService = context.mock(UUIDService.class);
        controller.setUuidService(uuidService);

        final UUIDReportService uuidReportService = new UUIDReportServiceImpl();
        controller.setUuidReportService(uuidReportService);
        
        list = new ArrayList<UUIDDetail>();
        Center center = new Center();
        center.setCenterId(1);
        center.setCenterName("");
        list.add(new UUIDDetail("mammoth", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user"));
    }

    @Test
    public void testGetNewUUIDReport() throws UUIDException {
        
        context.checking(new Expectations() {{
            one(uuidService).getNewlyGeneratedUUIDs(duration);
            will(returnValue(list));
        }});
        ModelMap returnedModel = getReportData(REPORT_TYPE_NEW_UUID);
        verifyModelData(returnedModel);
    }

    @Test
    public void testGetSubmittedUUIDReport() {
        context.checking(new Expectations() {{
            one(uuidService).getSubmittedUUIDs();
            will(returnValue(list));
        }});
        ModelMap returnedModel = getReportData(UUIDConstants.REPORT_TYPE_SUBMITTED_UUID);
        verifyModelData(returnedModel);
    }

    @Test
    public void testGetMissingUUIDReport() {
        context.checking(new Expectations() {{
            one(uuidService).getMissingUUIDs();
            will(returnValue(list));
        }});
        ModelMap returnedModel = getReportData(REPORT_TYPE_MISSING_UUID);
        verifyModelData(returnedModel);
    }

    private ModelMap getReportData(final String reportType) {
        return controller.getUUIDReportResults(new ModelMap(), 0, 5,
                UUIDConstants.COLUMN_UUID, UUIDConstants.ASC, reportType, session);
    }
    
    private void verifyModelData(final ModelMap returnedModel) {
        assertNotNull(returnedModel);
        assertEquals(true, returnedModel.get("success"));
        assertNotNull(returnedModel.get("reportResults"));
        List<UUIDDetail> uuidList = (List<UUIDDetail>) returnedModel.get("reportResults");
        assertEquals("mammoth", uuidList.get(0).getUuid());
    }
    
}
