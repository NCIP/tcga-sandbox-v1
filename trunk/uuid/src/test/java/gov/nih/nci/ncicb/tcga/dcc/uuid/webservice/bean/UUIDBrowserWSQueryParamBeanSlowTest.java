/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice.bean;


import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationErrors;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.MetadataSearchWS;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Unit test for UUIDBrowserWebService query and path parameter validation.
 * 
 * @author Matt Nicholls 
 * 		   Last updated by: nichollsmc
 * 
 * @version
 */
public class UUIDBrowserWSQueryParamBeanSlowTest extends JerseyTest {

	// Initialize UUIDBrowserWebService resource URIs
	private final WebResource xmlUUIDBrowserResource = resource().path("metadata").path("xml");
	private final WebResource jsonUUIDBrowserResource = resource().path("metadata").path("json");
	private final WebResource barcodeMetaDataXMLResource = resource().path("metadata").path("xml").path("barcode");
	private final WebResource barcodeMetaDataJSONResource = resource().path("metadata").path("json").path("barcode");
	private final WebResource uuidMetaDataXMLResource = resource().path("metadata").path("xml").path("uuid");
	private final WebResource uuidMetaDataJSONResource = resource().path("metadata").path("json").path("uuid");
	
	private static final String PATH_SEPARATOR = "\u002f";
	private static final String EMPTY_PATH = PATH_SEPARATOR + "\u0020";
	private static final String URI_PREFIX = "http://localhost:";
	private static final int URI_PORT = 9998;
	
	private static final Map<String, String> specialBarcodeMap = new HashMap<String, String>();
	static {
		specialBarcodeMap.put("Examination", "TCGA-02-0001-E3124");
		specialBarcodeMap.put("Radiation", "TCGA-02-0001-R2");
		specialBarcodeMap.put("Surgery", "TCGA-02-0001-S145");
		specialBarcodeMap.put("Drug", "TCGA-02-0001-C1");
	}
	
	// Setup the jersey test harness
	public UUIDBrowserWSQueryParamBeanSlowTest() throws Exception {
		super(new WebAppDescriptor.Builder("gov.nih.nci.ncicb.tcga.dcc.uuid")
				.contextParam("contextConfigLocation", "classpath:conf/applicationContext-jdbc-test.xml")
				.servletClass(SpringServlet.class)
				.contextListenerClass(ContextLoaderListener.class).build());
	}
	
	@Test
	public void testWebResourceURIPaths() {
		assertEquals(URI_PREFIX + URI_PORT + "/metadata/xml", xmlUUIDBrowserResource.getURI().toString());
		assertEquals(URI_PREFIX + URI_PORT + "/metadata/json", jsonUUIDBrowserResource.getURI().toString());
		assertEquals(URI_PREFIX + URI_PORT + "/metadata/xml/barcode", barcodeMetaDataXMLResource.getURI().toString());
		assertEquals(URI_PREFIX + URI_PORT + "/metadata/json/barcode", barcodeMetaDataJSONResource.getURI().toString());
		assertEquals(URI_PREFIX + URI_PORT + "/metadata/xml/uuid", uuidMetaDataXMLResource.getURI().toString());
		assertEquals(URI_PREFIX + URI_PORT + "/metadata/json/uuid", uuidMetaDataJSONResource.getURI().toString());
	}
	
	@Test
	public void testEmptyQueryParams() {
		WebResource webResource = client().resource(xmlUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("barcode", "").queryParam("updatedBefore", "");
		assertNotNull(webResource.get(MetadataSearchWS.class));
		
		webResource = client().resource(jsonUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("barcode", "").queryParam("updatedAfter", "");
		assertNotNull(webResource.get(MetadataSearchWS.class));
	}
	
    @Test
    public void testEmptyPathParamsForBarcodeMetaDataXMLResource() {

        WebResource webResource = client().resource(barcodeMetaDataXMLResource.getURI());
        webResource = webResource.path(EMPTY_PATH);

        final int expectedStatusCode = HttpStatusCode.OK;
        final String expectedMediaType = MediaType.APPLICATION_XML;
        final String expectedInvalidValue = " ";
        final String expectedErrorMessage = "Barcode ' ' not found.";

        checkWebResourceValidationErrors(webResource, expectedStatusCode, expectedMediaType, expectedInvalidValue, expectedErrorMessage);
    }

    @Test
    public void testEmptyPathParamsForBarcodeMetaDataJSONResource() {

		WebResource webResource = client().resource(barcodeMetaDataJSONResource.getURI());
		webResource = webResource.path(EMPTY_PATH);

        final int expectedStatusCode = HttpStatusCode.OK;
        final String expectedMediaType = MediaType.APPLICATION_JSON;
        final String expectedInvalidValue = " ";
        final String expectedErrorMessage = "Barcode ' ' not found.";

        checkWebResourceValidationErrors(webResource, expectedStatusCode, expectedMediaType, expectedInvalidValue, expectedErrorMessage);
    }

    @Test
    public void testEmptyPathParamsForUuidMetaDataXMLResource() {

        WebResource webResource = client().resource(uuidMetaDataXMLResource.getURI());
        webResource = webResource.path(EMPTY_PATH);

        final int expectedStatusCode = HttpStatusCode.OK;
        final String expectedMediaType = MediaType.APPLICATION_XML;
        final String expectedInvalidValue = " ";
        final String expectedErrorMessage = "UUID ' ' not found.";

        checkWebResourceValidationErrors(webResource, expectedStatusCode, expectedMediaType, expectedInvalidValue, expectedErrorMessage);
    }

    @Test
    public void testEmptyPathParams() {

		WebResource webResource = client().resource(uuidMetaDataJSONResource.getURI());
		webResource = webResource.path(EMPTY_PATH);

        final int expectedStatusCode = HttpStatusCode.OK;
        final String expectedMediaType = MediaType.APPLICATION_JSON;
        final String expectedInvalidValue = " ";
        final String expectedErrorMessage = "UUID ' ' not found.";

        checkWebResourceValidationErrors(webResource, expectedStatusCode, expectedMediaType, expectedInvalidValue, expectedErrorMessage);
    }

	@Test
	public void testMispelledQueryParamsXML() {
		WebResource webResource = client().resource(xmlUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("bacon", "").queryParam("egg", "").queryParam("cheese", "");
		
		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		expectedResponse.append("<validationErrors>");
		expectedResponse.append("<validationError><invalidValue>bacon</invalidValue><errorMessage>is not a valid query parameter</errorMessage></validationError>");
		expectedResponse.append("<validationError><invalidValue>cheese</invalidValue><errorMessage>is not a valid query parameter</errorMessage></validationError>");
		expectedResponse.append("<validationError><invalidValue>egg</invalidValue><errorMessage>is not a valid query parameter</errorMessage></validationError>");
		expectedResponse.append("</validationErrors>");
		
		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			ClientResponse response = uie.getResponse();
			String errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
	}
	
	@Test
	public void testMispelledQueryParamsJSON() {
		WebResource webResource = client().resource(jsonUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("tic", "").queryParam("tac", "").queryParam("toe", "");
		
		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("{\"validationError\":[");
		expectedResponse.append("{\"invalidValue\":\"toe\",\"errorMessage\":\"is not a valid query parameter\"},");
		expectedResponse.append("{\"invalidValue\":\"tic\",\"errorMessage\":\"is not a valid query parameter\"},");
		expectedResponse.append("{\"invalidValue\":\"tac\",\"errorMessage\":\"is not a valid query parameter\"}");
		expectedResponse.append("]}");
		
		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			ClientResponse response = uie.getResponse();
			String errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
	}
	
	@Test
	public void testValidateBarcodeQueryParamXML() {
		
		ClientResponse response;
		String errorResponse;
		
		WebResource webResource = 
			client().resource(xmlUUIDBrowserResource.getURI()).queryParam("barcode", "invalid-barcode,TCGA-01-007");
		
		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		expectedResponse.append("<validationErrors>");
		expectedResponse.append("<validationError><invalidValue>invalid-barcode,TCGA-01-007</invalidValue>" +
				"<errorMessage>contains value(s) with an invalid barcode format</errorMessage></validationError>");
		expectedResponse.append("</validationErrors>");
		
		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
		
		webResource = client().resource(barcodeMetaDataXMLResource.getURI()).path("invalid-barcode,TCGA-01-007");
		
		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
	}
	
	@Test
	public void testValidSpecialBarcodeXML() {
		
		WebResource webResource;
		
		// Validate special barcode formats Examination, Radiation, Surgery, and Drug
		String specialBarcode = null;
		for(String barcodeKey : specialBarcodeMap.keySet()) {
			specialBarcode = specialBarcodeMap.get(barcodeKey);
			webResource = client().resource(xmlUUIDBrowserResource.getURI()).queryParam("barcode", specialBarcode);
			try {
				// Should not fail
				webResource.get(MetadataSearchWS.class);
			}
			catch (UniformInterfaceException uie) {
				fail("Barcode '" + specialBarcode + "' for barcode type '" + barcodeKey + 
						"' has a valid barcode format and should not fail XML service innvocation.");
			}
		}
	}
	
	@Test
	public void testValidateBarcodeQueryParamJSON() {
		
		ClientResponse response;
		String errorResponse;
		
		WebResource webResource = 
			client().resource(jsonUUIDBrowserResource.getURI()).queryParam("barcode", "invalid-barcode,TCGA-01-007,orange-juice");
		
		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("{\"validationError\":");
		expectedResponse.append("{\"invalidValue\":\"invalid-barcode,TCGA-01-007,orange-juice\",");
		expectedResponse.append("\"errorMessage\":\"contains value(s) with an invalid barcode format\"}}");

		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			
			assertEquals(expectedResponse.toString(), errorResponse);
		}
		
		webResource = client().resource(barcodeMetaDataJSONResource.getURI()).path("invalid-barcode,TCGA-01-007,orange-juice");
		
		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
	}
	
	@Test
	public void testValidSpecialBarcodeJSON() {
		
		WebResource webResource;
		
		// Validate special barcode formats Examination, Radiation, Surgery, and Drug
		String specialBarcode = null;
		for(String barcodeKey : specialBarcodeMap.keySet()) {
			specialBarcode = specialBarcodeMap.get(barcodeKey);
			webResource = client().resource(jsonUUIDBrowserResource.getURI()).queryParam("barcode", specialBarcode);
			try {
				// Should not fail
				webResource.get(MetadataSearchWS.class);
			}
			catch (UniformInterfaceException uie) {
				fail("Barcode '" + specialBarcode + "' for barcode type '" + barcodeKey + 
						"' has a valid barcode format and should not fail JSON service innvocation.");
			}
		}
	}
	
	@Test
	public void testValidateUUIDQueryParamXML() {
		
		ClientResponse response;
		String errorResponse;
		
		WebResource webResource = client().resource(xmlUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("uuid", "optimusPrime,1");
		
		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		expectedResponse.append("<validationErrors>");
		expectedResponse.append("<validationError><invalidValue>optimusPrime,1</invalidValue><errorMessage>contains value(s) with an invalid UUID format</errorMessage></validationError>");
		expectedResponse.append("</validationErrors>");
		
		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
		
		webResource = client().resource(uuidMetaDataXMLResource.getURI());
		webResource = webResource.path("optimusPrime,1");
		
		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
	}
	
	@Test
	public void testValidateUUIDQueryParamJSON() {
		
		ClientResponse response;
		String errorResponse;
		
		WebResource webResource = client().resource(jsonUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("uuid", "brown,blue,1,3,green");
		
		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("{\"validationError\":");
		expectedResponse.append("{\"invalidValue\":\"brown,blue,1,3,green\",");
		expectedResponse.append("\"errorMessage\":\"contains value(s) with an invalid UUID format\"}}");

		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
		
		webResource = client().resource(uuidMetaDataJSONResource.getURI());
		webResource = webResource.path("brown,blue,1,3,green");

		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);

			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
	}
	
	@Test
	public void testValidateIntegerQueryParam() {
		
		ClientResponse response;
		String errorResponse;
		
		WebResource webResource = client().resource(xmlUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("batch", "not-an-integer,3");
		
		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		expectedResponse.append("<validationErrors>");
		expectedResponse.append("<validationError><invalidValue>not-an-integer,3</invalidValue>" +
				"<errorMessage>contains value(s) that could not be parsed to an integer</errorMessage></validationError>");
		expectedResponse.append("</validationErrors>");

		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
		
		webResource = client().resource(jsonUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("batch", "not-an-integer,3");
		
		expectedResponse = new StringBuilder();
		expectedResponse.append("{\"validationError\":");
		expectedResponse.append("{\"invalidValue\":\"not-an-integer,3\",");
		expectedResponse.append("\"errorMessage\":\"contains value(s) that could not be parsed to an integer\"}}");
		
		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
		
		webResource = client().resource(jsonUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("batch", "3,4,5");
		assertNotNull(webResource.get(MetadataSearchWS.class));
	}
	
	@Test
	public void testValidateDateQueryParam() {
		
		ClientResponse response;
		String errorResponse;
		
		WebResource webResource = client().resource(xmlUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("updatedBefore", "not-a-valid-date");
		
		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		expectedResponse.append("<validationErrors>");
		expectedResponse.append("<validationError><invalidValue>not-a-valid-date</invalidValue>" +
				"<errorMessage>has an invalid date format, expected format MM/DD/YYYY</errorMessage></validationError>");
		expectedResponse.append("</validationErrors>");

		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
		
		webResource = client().resource(jsonUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("updatedBefore", "not-a-valid-date");
		
		expectedResponse = new StringBuilder();
		expectedResponse.append("{\"validationError\":");
		expectedResponse.append("{\"invalidValue\":\"not-a-valid-date\",");
		expectedResponse.append("\"errorMessage\":\"has an invalid date format, expected format MM/DD/YYYY\"}}");
		
		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
		
		webResource = client().resource(jsonUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("updatedAfter", "01/16/1979");
		assertNotNull(webResource.get(MetadataSearchWS.class));
	}
	
	@Test
	public void testValidateSlideLayerQueryParam() {
		
		ClientResponse response;
		String errorResponse;
		
		WebResource webResource = client().resource(xmlUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("slideLayer", "Up,Down,Left,Right");
		
		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		expectedResponse.append("<validationErrors>");
		expectedResponse.append("<validationError><invalidValue>Up,Down,Left,Right</invalidValue>" +
				"<errorMessage>has invalid slide layer(s), should be one of T, M, B, Top, Middle, or Bottom</errorMessage></validationError>");
		expectedResponse.append("</validationErrors>");

		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
		
		webResource = client().resource(jsonUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("slideLayer", "Up,Down,Left,Right");
		
		expectedResponse = new StringBuilder();
		expectedResponse.append("{\"validationError\":");
		expectedResponse.append("{\"invalidValue\":\"Up,Down,Left,Right\",");
		expectedResponse.append("\"errorMessage\":\"has invalid slide layer(s), should be one of T, M, B, Top, Middle, or Bottom\"}}");
		
		try {
			webResource.get(MetadataSearchWS.class);
		}
		catch (UniformInterfaceException uie) {
			response = uie.getResponse();
			errorResponse = response.getEntity(String.class);
			assertEquals(HttpStatusCode.UNPROCESSABLE_ENTITY, response.getStatus());
			assertEquals(expectedResponse.toString(), errorResponse);
		}
		
		webResource = client().resource(xmlUUIDBrowserResource.getURI());
		webResource = webResource.queryParam("slideLayer", "Top,M,Bottom");
		assertNotNull(webResource.get(MetadataSearchWS.class));
	}
	
	@Override
    protected int getPort(int defaultPort) {
        return URI_PORT;
    }
	
    /**
     * Check the given {@link WebResource} against expected values
     *
     * @param webResource the {@link WebResource} to check
     * @param expectedStatusCode the expected HTTP status code
     * @param expectedMediaType the expected media type
     * @param expectedInvalidValue the expected invalid value
     * @param expectedErrorMessage the expected error message
     */
    private void checkWebResourceValidationErrors(final WebResource webResource,
                                                  final int expectedStatusCode,
                                                  final String expectedMediaType,
                                                  final String expectedInvalidValue,
                                                  final String expectedErrorMessage) {

        final int statusCode = webResource.head().getStatus();
        assertEquals(expectedStatusCode, statusCode);

        final String mediaType = webResource.head().getType().toString();
        assertEquals(expectedMediaType, mediaType);

        final ValidationErrors validationErrors = webResource.get(ValidationErrors.class);
        Assert.assertNotNull(validationErrors);

        final List<ValidationErrors.ValidationError> validationErrorList = validationErrors.getValidationError();
        Assert.assertNotNull(validationErrorList);
        Assert.assertEquals(1, validationErrorList.size());

        final ValidationErrors.ValidationError validationError = validationErrorList.get(0);
        Assert.assertNotNull(validationError);
        Assert.assertEquals(expectedInvalidValue, validationError.getInvalidValue());
        Assert.assertEquals(expectedErrorMessage, validationError.getErrorMessage());
    }
}
