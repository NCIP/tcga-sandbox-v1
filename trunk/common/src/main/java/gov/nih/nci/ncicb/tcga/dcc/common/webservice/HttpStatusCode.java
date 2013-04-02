/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.webservice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class facilitates the handling of HTTP status codes
 * and their associated plain text messages.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class HttpStatusCode {

    /**
     * A subset of standard HTTP status codes
     * (See http://en.wikipedia.org/wiki/List_of_HTTP_status_codes for a complete list)
     */
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int ACCEPTED = 202;
    public static final int NO_CONTENT = 204;
    public static final int NOT_FOUND = 404;
    public static final int PRECONDITION_FAILED = 412;
    public static final int REQUEST_ENTITY_TOO_LARGE = 413;
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int I_M_A_TEAPOT = 418;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int INTERNAL_SERVER_ERROR = 500;

    /**
     * Default message for unknown HTTP status code
     */
    private static final String UNKNOWN_HTTP_STATUS_CODE = "Unknown HTTP status code";

    /**
     * A Map that associate an HTTP status code to its plain text message
     */
    private static final Map<Integer, String> STATUS_CODE_MAP = new LinkedHashMap<Integer, String>() {{

        put(OK, "OK");
        put(CREATED, "Created");
        put(ACCEPTED, "Accepted");
        put(NO_CONTENT, "No Content");
        put(NOT_FOUND, "Not Found");
        put(PRECONDITION_FAILED, "Precondition Failed");
        put(REQUEST_ENTITY_TOO_LARGE, "Request Entity Too Large");
        put(UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type");
        put(I_M_A_TEAPOT, "I'm a teapot");
        put(UNPROCESSABLE_ENTITY, "Unprocessable Entity - Request failed model validation");
        put(INTERNAL_SERVER_ERROR, "Internal Server Error");
    }};

    /**
     * Return the plain text message associated with the given HTTP status code
     *
     * @param httpStatusCode the HTTP status code
     * @return the status code message
     */
    public static String getMessageForHttpStatusCode(final int httpStatusCode) {

        if (STATUS_CODE_MAP.containsKey(httpStatusCode)) {
            return STATUS_CODE_MAP.get(httpStatusCode);
        } else {
            return UNKNOWN_HTTP_STATUS_CODE;
        }
    }
}