/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.security.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * @author Julien Baboud
 */
public class SecurityUtilImplFastTest {

    private SecurityUtil securityUtil;

    @Before
    public void setup() {

        securityUtil = new SecurityUtilImpl();

        //Reset Authentication object for the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testGetAuthenticatedPrincipalLoginNameWhenNotAuthenticated() {
        assertEquals(securityUtil.getNotAuthenticated(), securityUtil.getAuthenticatedPrincipalLoginName());
    }

    @Test
    public void testGetAuthenticatedPrincipalLoginNameWhenAuthenticatedPrincipalIsNotOfUserDetails() {

        final String principal = "usernameTest";
        final String password = "passwordTest";
        final Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertEquals(principal, securityUtil.getAuthenticatedPrincipalLoginName());
    }

    @Test
    public void testGetAuthenticatedPrincipalLoginNameWhenAuthenticatedPrincipalIsOfUserDetails() {

        final String username = "usernameTest";
        final String password = "passwordTest";
        final boolean enabled = false;
        final boolean accountNonExpired = false;
        final boolean credentialsNonExpired = false;
        final boolean accountNonLocked = false;
        final GrantedAuthority[] authorities = new GrantedAuthority[0];
        final User principal = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertEquals(username, securityUtil.getAuthenticatedPrincipalLoginName());
    }

    @Test
    public void testGetAuthenticatedPrincipalAuthoritiesWhenPrincipalIsNotAuthenticated() {
        assertArrayEquals(new String[0], securityUtil.getAuthenticatedPrincipalAuthorities());
    }

    @Test
    public void testGetAuthenticatedPrincipalAuthoritiesWhenPrincipalIsAuthenticated() {

        final String username = "usernameTest";
        final String password = "passwordTest";
        final boolean enabled = true;
        final boolean accountNonExpired = true;
        final boolean credentialsNonExpired = true;
        final boolean accountNonLocked = true;
        final GrantedAuthority[] authorities = {new GrantedAuthorityImpl("ROLE_X"), new GrantedAuthorityImpl("ROLE_Y")};
        final User principal = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertNotNull(authentication.getAuthorities());
        assertArrayEquals(authorities, securityUtil.getAuthenticatedPrincipalAuthorities());
    }

    @Test
    public void testGetAuthenticationCredentialsNotFoundExceptionMessageKey() {
        assertEquals("AuthenticationCredentialsNotFoundException", securityUtil.getAuthenticationCredentialsNotFoundExceptionMessageKey());
    }

    @Test
    public void testGetAuthenticationCredentialsNotFoundExceptionMessageValue() {
        assertEquals("You need to authenticate prior to using this feature.", securityUtil.getAuthenticationCredentialsNotFoundExceptionMessageValue());
    }

    @Test
    public void testGetAccessDeniedExceptionMessageKey() {
        assertEquals("AccessDeniedException", securityUtil.getAccessDeniedExceptionMessageKey());
    }

    @Test
    public void testGetAccessDeniedExceptionMessageValue() {
        assertEquals("You do not have the appropriate authority to use this feature.", securityUtil.getAccessDeniedExceptionMessageValue());
    }
}