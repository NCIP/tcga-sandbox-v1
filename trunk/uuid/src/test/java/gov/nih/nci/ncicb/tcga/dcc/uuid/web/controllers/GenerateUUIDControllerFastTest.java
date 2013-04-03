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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers.request.GenerateUUIDRequest;

import java.util.ArrayList;

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
 * Class for testing GenerateUUIDController
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@RunWith (JMock.class)
public class GenerateUUIDControllerFastTest {

    private final JUnit4Mockery context = new JUnit4Mockery();
    private GenerateUUIDController controller;
    private UUIDService uuidService;
    private HttpSession session;
    private SecurityUtil mockSecurityUtil;    

    @Before
    public void setup() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        session = request.getSession();
        controller = new GenerateUUIDController();
        uuidService = context.mock(UUIDService.class);
        controller.setUuidService(uuidService);
        mockSecurityUtil = context.mock(SecurityUtil.class);
        controller.setSecurityUtil(mockSecurityUtil);
    }

    @Test
    public void testGenerateUUIDDProcessRequest() throws UUIDException {
        final String userName = "userName";
        context.checking(new Expectations() {{
            one(uuidService).generateUUID(1, 1, UUIDConstants.GenerationMethod.Web, userName);
            will(returnValue(new ArrayList<UUIDDetail>()));
            one(mockSecurityUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue(userName));
        }});

        GenerateUUIDRequest generateRequest = new GenerateUUIDRequest(1, 1);
        ModelMap model = new ModelMap();
        String view = controller.generateUUIDDProcessRequest(model,generateRequest, session);
        assertNotNull(view);
        assertEquals("uuidManagerHome", view);
        String operName = String.valueOf(model.get("operationName"));
        assertEquals("displayNewlyGenerated", operName);
    }

}
