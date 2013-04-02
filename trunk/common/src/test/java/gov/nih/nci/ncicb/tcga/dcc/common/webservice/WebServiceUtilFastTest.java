/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationErrors;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * WebServiceUtil unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class WebServiceUtilFastTest {

    private Mockery mockery = new JUnit4Mockery();
    private Log mockLog;

    @Before
    public void setUp() {
        mockLog = mockery.mock(Log.class);
    }

    @Test
    public void testCorrectStatusCode() {

        final int httpStatusCode = 404;
        final String message = "test";
        final Response response = WebServiceUtil.getStatusResponse(httpStatusCode, message);

        testResponse(response, httpStatusCode);
    }

    @Test
    public void testLogAndThrowWebApplicationException() {

        final String errorMessage = "There was an error";
        final int httpStatusCode = HttpStatusCode.ACCEPTED;

        mockery.checking(new Expectations() {{
            one(mockLog).error(errorMessage);
        }});

        try {
            WebServiceUtil.logAndThrowWebApplicationException(mockLog, errorMessage, httpStatusCode);
        } catch(final WebApplicationException e) {
            testResponse(e.getResponse(), httpStatusCode);
        }
    }

    @Test
    public void testMakeResponseXMLOK() {

        final String mediaType = MediaType.APPLICATION_XML;
        final int statusCode = HttpStatusCode.OK;
        final String invalidValue = "invalid value";
        final String errorMessage = "errMsg";
        final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, invalidValue, errorMessage);

        checkValidationErrorsResponse(response, statusCode, invalidValue, errorMessage, mediaType);
    }

    @Test
    public void testMakeResponseJsonTeaPot() {

        final String mediaType = MediaType.APPLICATION_JSON;
        final int statusCode = HttpStatusCode.I_M_A_TEAPOT;
        final String invalidValue = "invalid value";
        final String errorMessage = "errMsg";
        final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, invalidValue, errorMessage);

        checkValidationErrorsResponse(response, statusCode, invalidValue, errorMessage, mediaType);
    }

    /**
     * Test that the <code>Response</code> has the expected <code>HTTPStatusCode</code>
     *
     * @param response the <code>Response</code>
     * @param httpStatusCode the expected <code>HTTPStatusCode</code>
     */
    private void testResponse(final Response response, final int httpStatusCode) {
        assertEquals(httpStatusCode, response.getStatus());
    }

    /**
     * Check that the given {@link WebApplicationException} is a <code>ValidationErrors</code> response with the expected values.
     *
     * @param response the {@link Response} to check
     * @param expectedStatusCode the expected status code
     * @param expectedInvalidValue the expected invalid value
     * @param expectedErrorMessage the expected error message
     * @param expectedMediaType the expected media type
     */
    public static void checkValidationErrorsResponse(final Response response,
                                                     final int expectedStatusCode,
                                                     final String expectedInvalidValue,
                                                     final String expectedErrorMessage,
                                                     final String expectedMediaType) {

        Assert.assertEquals(expectedStatusCode, response.getStatus());

        final Object entity = response.getEntity();
        Assert.assertEquals(ValidationErrors.class, entity.getClass());
        final ValidationErrors validationErrors = (ValidationErrors) entity;
        Assert.assertNotNull(validationErrors);

        final List<ValidationErrors.ValidationError> validationErrorList = validationErrors.getValidationError();
        Assert.assertNotNull(validationErrorList);
        Assert.assertEquals(1, validationErrorList.size());

        final ValidationErrors.ValidationError validationError = validationErrorList.get(0);
        Assert.assertNotNull(validationError);
        Assert.assertEquals(expectedInvalidValue, validationError.getInvalidValue());
        Assert.assertEquals(expectedErrorMessage, validationError.getErrorMessage());

        final MultivaluedMap<String, Object> multivaluedMap = response.getMetadata();
        Assert.assertNotNull(multivaluedMap);

        final Object contentType = multivaluedMap.get("Content-Type");
        Assert.assertNotNull(contentType);
        assertTrue(contentType instanceof LinkedList);

        final LinkedList linkedList = (LinkedList) contentType;
        Assert.assertEquals(1, linkedList.size());
        Assert.assertEquals(expectedMediaType, linkedList.get(0).toString());
    }
}
