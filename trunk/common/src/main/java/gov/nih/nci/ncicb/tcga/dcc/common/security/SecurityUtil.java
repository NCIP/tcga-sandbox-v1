/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.security;

/**
 * This interface should be implemented by objects that want to provide a centralized point to retrieve
 * an authenticated user's login and provide default key/value messages to be stored by controllers' model
 * to deal with Exceptions related to authentication.
 *
 * @author Julien Baboud
 */
public interface SecurityUtil {

    /**
     * @return the login of the authenticated principal
     */
    public String getAuthenticatedPrincipalLoginName();

    /**
     * @return the default value to return to the UI in place of a username when the user is not authenticated
     */
    public String getNotAuthenticated();

    /**
     * @return the message key that should be used by a controller's model to deal with <code>AuthenticationCredentialsNotFoundException</code
     */
    public String getAuthenticationCredentialsNotFoundExceptionMessageKey();

    /**
     * @return the message value that should be used by a controller's model to deal with <code>AuthenticationCredentialsNotFoundException</code
     */
    public String getAuthenticationCredentialsNotFoundExceptionMessageValue();

    /**
     * @return the message key that should be used by a controller's model to deal with <code>AccessDeniedException</code
     */
    public String getAccessDeniedExceptionMessageKey();

    /**
     * @return the message value that should be used by a controller's model to deal with <code>AccessDeniedException</code
     */
    public String getAccessDeniedExceptionMessageValue();

    /**
     * @return an array of String representing the authorities (<code>GrantedAuthority</code>) granted to the authenticated principal
     */
    public String[] getAuthenticatedPrincipalAuthorities();
}
