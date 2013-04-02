/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.webservice;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationErrors;
import org.apache.commons.logging.Log;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods that are useful for web services
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class WebServiceUtil {

    /**
     * Builds a <code>Response</code> out of the given HTTP status code and message
     * @param httpStatusCode the HTTP status code
     * @param message the associated plain text message
     * @return the Response
     */
    public static Response getStatusResponse(final int httpStatusCode, final String message) {

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("<h2>HTTP STATUS ")
                .append(httpStatusCode)
                .append(" - ")
                .append(HttpStatusCode.getMessageForHttpStatusCode(httpStatusCode))
                .append(".</h2>")
                .append("<br /><p>")
                .append(message)
                .append("</p>");

        final ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.status(httpStatusCode);
        responseBuilder.entity(stringBuilder.toString());
        responseBuilder.type(MediaType.TEXT_HTML);

        return responseBuilder.build();
    }

    /**
     * Log the given error message and throw a <code>WebApplicationException</code> with the given HTTP status code
     *
     * @param log the <code>Log</code> to use for the logging
     * @param errorMessage the error message to log and display in the <code>WebApplicationException</code>
     * @param httpStatusCode the HTTP status code to set for the <code>WebApplicationException</code>
     */
    public static void logAndThrowWebApplicationException(final Log log, final String errorMessage, final int httpStatusCode) {

        log.error(errorMessage);
        throw new WebApplicationException(getStatusResponse(httpStatusCode, errorMessage));
    }

    /**
     * Return the error message displayed by the given <code>WebApplicationException</code>
     *
     * @param e the <code>WebApplicationException</code>
     * @return the error message displayed by the given <code>WebApplicationException</code>
     */
    public static String getWebErrorMessage(final WebApplicationException e) {
        return e.getResponse().getEntity().toString();
    }

    /**
     * An enum for the unit tests, to be able to choose between XML or JSON return type
     */
    public enum ReturnType {
        XML,
        JSON
    }

    /**
     * Return an error {@link Response} of the given type with the given error message and invalid value.
     *
     * Note: if <code>mediaType</code> is null then "application/xml" will be used.
     *
     * @param mediaType the type of response that should be returned
     * @param statusCode the HTTP status code of the response
     * @param invalidValue the value that caused the error
     * @param errorMessage the error message to include in the response
     * @return an error {@link Response} of the given type with the given error message and invalid value
     */
    public static Response makeResponse(final String mediaType,
                                        final int statusCode,
                                        final String invalidValue,
                                        final String errorMessage) {

        final ValidationErrors.ValidationError validationError = new ValidationErrors.ValidationError(invalidValue, errorMessage);
        final List<ValidationErrors.ValidationError> validationErrorList = new ArrayList<ValidationErrors.ValidationError>();
        validationErrorList.add(validationError);
        final ValidationErrors validationErrors = new ValidationErrors(validationErrorList);

        return Response.status(statusCode)
                .entity(validationErrors)
                .type((mediaType == null ? MediaType.APPLICATION_XML : mediaType))
                .build();
    }
}