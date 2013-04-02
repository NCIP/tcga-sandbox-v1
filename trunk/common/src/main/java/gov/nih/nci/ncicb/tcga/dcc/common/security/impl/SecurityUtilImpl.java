/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.security.impl;

import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;

/**
 * This is the default implementation of the Util interface.
 *
 * @author Julien Baboud
 */
public class SecurityUtilImpl implements SecurityUtil {

    private static Log logger = LogFactory.getLog(SecurityUtilImpl.class);

    /**
     * The value that will be used in place of a login name if the user is not authenticated
     */
    public final static String NOT_AUTHENTICATED = "not_authenticated";

    /**
     * Message key/value for AuthenticationCredentialsNotFoundException
     */
    private static final String AUTHENTICATION_CREDENTIALS_NOT_FOUND_EXCEPTION_MESSAGE_KEY = "AuthenticationCredentialsNotFoundException";
    private static final String AUTHENTICATION_CREDENTIALS_NOT_FOUND_EXCEPTION_MESSAGE_VALUE =
            "You need to authenticate prior to using this feature.";

    /**
     * Message key/value for AccessDeniedException
     */
    private static final String ACCESS_DENIED_EXCEPTION_MESSAGE_KEY = "AccessDeniedException";
    private static final String ACCESS_DENIED_EXCEPTION_MESSAGE_VALUE = "You do not have the appropriate authority to use this feature.";

    /**
     * @return the login of the authenticated principal. If not authenticated then it will return the default value <code>not_authenticated</code>
     */
    public String getAuthenticatedPrincipalLoginName() {

        String result = NOT_AUTHENTICATED;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                result = ((UserDetails) authentication.getPrincipal()).getUsername();
            } else {
                result = authentication.getPrincipal().toString();
            }
        }

        return result;
    }

    /**
     * @return the default value to return to the UI in place of a username when the user is not authenticated
     */
    public String getNotAuthenticated() {
        return NOT_AUTHENTICATED;
    }

    /**
     * @return the message key that should be used by a controller's model to deal with <code>AuthenticationCredentialsNotFoundException</code
     */
    public String getAuthenticationCredentialsNotFoundExceptionMessageKey() {
        return AUTHENTICATION_CREDENTIALS_NOT_FOUND_EXCEPTION_MESSAGE_KEY;
    }

    /**
     * @return the message value that should be used by a controller's model to deal with <code>AuthenticationCredentialsNotFoundException</code
     */
    public String getAuthenticationCredentialsNotFoundExceptionMessageValue() {
        return AUTHENTICATION_CREDENTIALS_NOT_FOUND_EXCEPTION_MESSAGE_VALUE;
    }

    /**
     * @return the message key that should be used by a controller's model to deal with <code>AccessDeniedException</code
     */
    public String getAccessDeniedExceptionMessageKey() {
        return ACCESS_DENIED_EXCEPTION_MESSAGE_KEY;
    }

    /**
     * @return the message value that should be used by a controller's model to deal with <code>AccessDeniedException</code
     */
    public String getAccessDeniedExceptionMessageValue() {
        return ACCESS_DENIED_EXCEPTION_MESSAGE_VALUE;
    }

    /**
     * @return an array of String representing the authorities (<code>GrantedAuthority</code>) granted to the authenticated principal
     */
    public String[] getAuthenticatedPrincipalAuthorities() {

        String[] result = new String[0];

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            GrantedAuthority[] grantedAuthorities = authentication.getAuthorities();
            if(grantedAuthorities != null) {
                result = new String[grantedAuthorities.length];
                for(int i=0; i < grantedAuthorities.length; i++) {
                    result[i] = grantedAuthorities[i].getAuthority();
                }
            } else {
                logger.debug("The authenticated user's authorities are null for user " + getAuthenticatedPrincipalLoginName());
            }
        }

        return result;
    }

    /**
     * check if the user has administrator role
     * @return true if user is an administrator
     */
    public static boolean isAdministrator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            final GrantedAuthority[] grantedAuthorities = authentication.getAuthorities();
            if (grantedAuthorities != null) {
                for (int i = 0; i < grantedAuthorities.length; i++) {
                    if ("ROLE_ANNOTATIONS_ADMINISTRATOR".equals(grantedAuthorities[i].getAuthority())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
