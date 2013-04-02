/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.security.impl;

import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityController;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Julien Baboud
 */
@RunWith(JMock.class)
public class SecurityControllerImplFastTest {

    private final JUnit4Mockery context = new JUnit4Mockery();
    private SecurityController securityController;
    private SecurityUtil securityUtil;

    @Before
    public void setup() {

        securityUtil = context.mock(SecurityUtil.class);
        securityController = new SecurityControllerImpl();
        securityController.setSecurityUtil(securityUtil);
    }

    @Test
    public void testHandleRequestUsername() {

        final String username = "username";

        context.checking(new Expectations() {{
            one(securityUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue(username));
        }});

        final ModelMap modelMap = new ModelMap();
        securityController.handleRequestUsername(modelMap);

        final String expectedKey = "authenticatedPrincipalUsername";
        assertTrue(modelMap.containsKey(expectedKey));
        assertEquals(modelMap.get(expectedKey), username);
    }

    @Test
    public void testHandleRequestAuthorities() {

        final String[] authorities = {"ROLE_X", "ROLE_Y"};

        context.checking(new Expectations() {{
            one(securityUtil).getAuthenticatedPrincipalAuthorities();
            will(returnValue(authorities));
        }});

        final ModelMap modelMap = new ModelMap();
        securityController.handleRequestAuthorities(modelMap);

        final String expectedKey = "authenticatedPrincipalAuthorities";
        assertTrue(modelMap.containsKey(expectedKey));
        assertArrayEquals((String[])modelMap.get(expectedKey), authorities);
    }
}
