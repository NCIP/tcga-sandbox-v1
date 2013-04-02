/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.datareports.security;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.PendingUUIDProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import java.util.ArrayList;

/**
 * Unit test for the PendingUUIDProcessor secured methods
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class PendingUUIDProcessorSecurityFastTest extends AbstractDependencyInjectionSpringContextTests {

    @Autowired
    private PendingUUIDProcessor pendingUUIDProcessor;

    @Override
    protected String getConfigPath() {
        return "appContextTest.xml";
    }

    @Override
    protected void onSetUp() throws Exception {

        assertNotNull(pendingUUIDProcessor);
        logout();
    }

    @Test
    public void testAccessAuthorized() {

        login("penguin", "fish");
        pendingUUIDProcessor.persistPendingUUIDs(new ArrayList<PendingUUID>());
    }

    @Test
    public void testAccessDeniedBadCredentials() {

        try {
            login("penguin", "meat");
            pendingUUIDProcessor.persistPendingUUIDs(new ArrayList<PendingUUID>());
            fail("BadCredentialsException was not thrown.");

        } catch (final BadCredentialsException e) {
            //Expected
        }
    }

    @Test
    public void testAccessDeniedNotAuthorized() {

        try {
            login("squirrel", "nuts");
            pendingUUIDProcessor.persistPendingUUIDs(new ArrayList<PendingUUID>());
            fail("AccessDeniedException was not thrown.");

        } catch (final AccessDeniedException e) {
            //Expected
        }
    }

    /**
     * Authenticate with the given username and password
     *
     * @param username the username
     * @param password the password
     */
    private void login(final String username, final String password) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    /**
     * Clear spring security context
     */
    private void logout() {
        SecurityContextHolder.clearContext();
    }
}
