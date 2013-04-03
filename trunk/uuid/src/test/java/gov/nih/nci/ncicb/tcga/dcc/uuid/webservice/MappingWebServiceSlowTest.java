/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UuidBarcodeMapping;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.web.AbstractSpringAwareGrizzlyJerseyTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.WebAppDescriptor;

/**
 * Test class for the RESTful UUID and barcode batch mapping resources of the
 * {@link MappingWebService}.
 * 
 * @author nichollsmc
 */
public class MappingWebServiceSlowTest extends
        AbstractSpringAwareGrizzlyJerseyTest {

    @Autowired
    private UUIDService uuidService;

    private final WebResource mappingResourceRoot = resource().path("mapping");
    private final WebResource xmlResourcePath = mappingResourceRoot.path("xml");
    private final WebResource jsonResourcePath = mappingResourceRoot.path("json");
    private final WebResource barcodeBatchResourcePathXML = xmlResourcePath.path("barcode").path("batch");
    private final WebResource barcodeBatchResourcePathJSON = jsonResourcePath.path("barcode").path("batch");
    private final WebResource uuidBatchResourcePathXML = xmlResourcePath.path("uuid").path("batch");
    private final WebResource uuidBatchResourcePathJSON = jsonResourcePath.path("uuid").path("batch");

    public MappingWebServiceSlowTest() throws Exception {
        super(new WebAppDescriptor.Builder("gov.nih.nci.ncicb.tcga.dcc.uuid")
                .contextParam("contextConfigLocation", "classpath:conf/applicationContext-ws-test.xml")
                .servletClass(SpringServlet.class)
                .contextListenerClass(ContextLoaderListener.class).build());
    }

    @Test
    public void shouldOnlyAcceptTextPlainMediaType() {
        final List<WebResource> webResources = Arrays.asList(
                barcodeBatchResourcePathXML, barcodeBatchResourcePathJSON,
                uuidBatchResourcePathXML, uuidBatchResourcePathJSON);

        ClientResponse response = null;
        for (final WebResource webResource : webResources) {
            try {
                webResource.type(MediaType.APPLICATION_XML).post(String.class, "");
            }
            catch (UniformInterfaceException uie) {
                response = uie.getResponse();
            }

            assertEquals("Web resource '" + webResource.getURI().toString()
                    + "' should only accept media type '"
                    + MediaType.TEXT_PLAIN + "'.",
                    HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, response.getStatus());
        }
    }

    @Test
    public void shouldOnlyAcceptHTTPPostRequests() {
        final List<WebResource> webResources = Arrays.asList(
                barcodeBatchResourcePathXML, barcodeBatchResourcePathJSON,
                uuidBatchResourcePathXML, uuidBatchResourcePathJSON);

        ClientResponse response = null;
        for (final WebResource webResource : webResources) {
            try {
                webResource.type(MediaType.APPLICATION_XML).get(String.class);
            }
            catch (UniformInterfaceException uie) {
                response = uie.getResponse();
            }

            assertEquals("Web resource '" + webResource.getURI().toString() + "' should only accept HTTP POST requests.",
                    HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatus());
        }
    }
    
    @Test
    public void shouldReturnErrorResponseForEmptyRequestEntity() {
        final List<WebResource> webResources = Arrays.asList(
                barcodeBatchResourcePathXML, barcodeBatchResourcePathJSON,
                uuidBatchResourcePathXML, uuidBatchResourcePathJSON);

        ClientResponse response = null;
        for (final WebResource webResource : webResources) {
            try {
                webResource.type(MediaType.TEXT_PLAIN).post(String.class, "");
            }
            catch (UniformInterfaceException uie) {
                response = uie.getResponse();
            }

            assertEquals("Web resource '" + webResource.getURI().toString()
                    + "' should return error response for empty request entity.",
                    HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());
            assertEquals("Web resource '" + webResource.getURI().toString() 
                    + "' should provide appropriate error message for empty request entity.",
                    "Could not process the request because the request body was empty", response.getEntity(String.class));
        }
    }

    @Test
    public void shouldReturnXMLErrorResponseForInvalidBarcodes() {
        ClientResponse response = null;
        try {
            barcodeBatchResourcePathXML.type(MediaType.TEXT_PLAIN).post(
                    String.class,
                    "TCGA-C4-C3PO,this-is-not-a-valid-barcode,TCGA-C4-R2D2");
        }
        catch (UniformInterfaceException uie) {
            response = uie.getResponse();
        }

        final String expectedResponse = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<validationErrors>")
                .append("<validationError><invalidValue>this-is-not-a-valid-barcode</invalidValue>")
                .append("<errorMessage>contains value(s) with an invalid barcode format</errorMessage></validationError>")
                .append("</validationErrors>").toString();

        assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());
        assertEquals(expectedResponse, response.getEntity(String.class));
    }

    @Test
    public void shouldReturnJSONErrorResponseForInvalidBarcodes() {
        ClientResponse response = null;
        try {
            barcodeBatchResourcePathJSON.type(MediaType.TEXT_PLAIN).post(
                    String.class,
                    "TCGA-C4-C3PO,TCGA-C4-R2D2,this-is-not-a-valid-barcode");
        }
        catch (UniformInterfaceException uie) {
            response = uie.getResponse();
        }

        final String expectedResponse = new StringBuilder()
                .append("{\"validationError\":")
                .append("{\"invalidValue\":\"this-is-not-a-valid-barcode\",")
                .append("\"errorMessage\":\"contains value(s) with an invalid barcode format\"}")
                .append("}").toString();

        assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());
        assertEquals(expectedResponse, response.getEntity(String.class));
    }

    @Test
    public void shouldReturnXMLErrorResponseForInvalidUUIDs() {
        ClientResponse response = null;
        try {
            uuidBatchResourcePathXML
                    .type(MediaType.TEXT_PLAIN)
                    .post(String.class, "448db63b-6eb0-45b0-a386-7bd98766387e,f93b4d22-3a88-49f1-b4f0-3e58fc280f7a,this-is-not-a-valid-uuid");
        }
        catch (UniformInterfaceException uie) {
            response = uie.getResponse();
        }

        final String expectedResponse = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<validationErrors>")
                .append("<validationError><invalidValue>this-is-not-a-valid-uuid</invalidValue>")
                .append("<errorMessage>contains value(s) with an invalid UUID format</errorMessage></validationError>")
                .append("</validationErrors>").toString();

        assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());
        assertEquals(expectedResponse, response.getEntity(String.class));
    }

    @Test
    public void shouldReturnJSONErrorResponseForInvalidUUIDs() {
        ClientResponse response = null;
        try {
            uuidBatchResourcePathJSON
                    .type(MediaType.TEXT_PLAIN)
                    .post(String.class, "46beab59-cc44-46de-b5b7-5a9776a770ff,this-is-not-a-valid-uuid004c4cbd-d8c0-4c99-b98b-e04607758617");
        }
        catch (UniformInterfaceException uie) {
            response = uie.getResponse();
        }

        final String expectedResponse = new StringBuilder()
                .append("{\"validationError\":")
                .append("{\"invalidValue\":\"this-is-not-a-valid-uuid004c4cbd-d8c0-4c99-b98b-e04607758617\",")
                .append("\"errorMessage\":\"contains value(s) with an invalid UUID format\"}")
                .append("}").toString();

        assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());
        assertEquals(expectedResponse, response.getEntity(String.class));
    }

    @Test
    public void shouldReturnXMLResponseForUUIDToBarcodeMappings() {
        final String barcodes = "TCGA-C4-A0B0,TCGA-C9-R1D1,TCGA-11-1111";

        final List<String> barcodeList = Arrays.asList(barcodes.split(","));

        final List<UuidBarcodeMapping> uuidBarcodeMapping = new ArrayList<UuidBarcodeMapping>();
        uuidBarcodeMapping.add(new UuidBarcodeMapping() {
            {
                setBarcode("TCGA-C4-A0B0");
                setUuid("db9e8afb-e863-4bfc-bd48-f5595d2de474");
            }
        });
        uuidBarcodeMapping.add(new UuidBarcodeMapping() {
            {
                setBarcode("TCGA-C9-R1D1");
                setUuid("0c3410c8-754c-468f-bf2a-60641cfc9114");
            }
        });
        uuidBarcodeMapping.add(new UuidBarcodeMapping() {
            {
                setBarcode("TCGA-11-1111");
                setUuid("e01c49bc-9184-4e0d-82ae-88d5a5d2e7b1");
            }
        });

        Mockito.when(uuidService.getUUIDsForBarcodes(barcodeList)).thenReturn(uuidBarcodeMapping);

        final ClientResponse response = 
                barcodeBatchResourcePathXML.type(MediaType.TEXT_PLAIN).post(ClientResponse.class, barcodes);

        final String expectedResponse = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<uuidBarcodeMappings>")
                .append("<uuidMapping><barcode>TCGA-C4-A0B0</barcode><uuid>db9e8afb-e863-4bfc-bd48-f5595d2de474</uuid></uuidMapping>")
                .append("<uuidMapping><barcode>TCGA-C9-R1D1</barcode><uuid>0c3410c8-754c-468f-bf2a-60641cfc9114</uuid></uuidMapping>")
                .append("<uuidMapping><barcode>TCGA-11-1111</barcode><uuid>e01c49bc-9184-4e0d-82ae-88d5a5d2e7b1</uuid></uuidMapping>")
                .append("</uuidBarcodeMappings>").toString();

        assertEquals(expectedResponse, response.getEntity(String.class));
    }

    @Test
    public void shouldReturnJSONResponseForUUIDToBarcodeMappings() {
        final String barcodes = "TCGA-C4-C3PO,TCGA-C4-R2D2,TCGA-00-0000";

        final List<String> barcodeList = Arrays.asList(barcodes.split(","));

        final List<UuidBarcodeMapping> uuidBarcodeMappings = new ArrayList<UuidBarcodeMapping>();
        uuidBarcodeMappings.add(new UuidBarcodeMapping() {
            {
                setBarcode("TCGA-C4-C3PO");
                setUuid("46beab59-cc44-46de-b5b7-5a9776a770ff");
            }
        });
        uuidBarcodeMappings.add(new UuidBarcodeMapping() {
            {
                setBarcode("TCGA-C4-R2D2");
                setUuid("d44c1e52-ee47-4879-9698-9839fea8c76c");
            }
        });
        uuidBarcodeMappings.add(new UuidBarcodeMapping() {
            {
                setBarcode("TCGA-00-0000");
                setUuid("88a9f580-05ae-43e8-bb0e-991ae6e72814");
            }
        });

        Mockito.when(uuidService.getUUIDsForBarcodes(barcodeList)).thenReturn(uuidBarcodeMappings);

        final ClientResponse response = 
                barcodeBatchResourcePathJSON.type(MediaType.TEXT_PLAIN).post(ClientResponse.class, barcodes);

        final String expectedResponse = new StringBuilder()
                .append("{\"uuidMapping\":[")
                .append("{\"barcode\":\"TCGA-C4-C3PO\",\"uuid\":\"46beab59-cc44-46de-b5b7-5a9776a770ff\"},")
                .append("{\"barcode\":\"TCGA-C4-R2D2\",\"uuid\":\"d44c1e52-ee47-4879-9698-9839fea8c76c\"},")
                .append("{\"barcode\":\"TCGA-00-0000\",\"uuid\":\"88a9f580-05ae-43e8-bb0e-991ae6e72814\"}")
                .append("]}").toString();

        assertEquals(expectedResponse, response.getEntity(String.class));
    }

    @Test
    public void shouldReturnXMLResponseForBarcodeToUUIDMappings() {
        final String uuids = "db9e8afb-e863-4bfc-bd48-f5595d2de474,0c3410c8-754c-468f-bf2a-60641cfc9114,e01c49bc-9184-4e0d-82ae-88d5a5d2e7b1";

        final List<String> uuidList = Arrays.asList(uuids.split(","));

        final List<UuidBarcodeMapping> uuidBarcodeMappings = new ArrayList<UuidBarcodeMapping>();
        uuidBarcodeMappings.add(new UuidBarcodeMapping() {
            {
                setUuid("db9e8afb-e863-4bfc-bd48-f5595d2de474");
                setBarcode("TCGA-C4-A0B0");
            }
        });
        uuidBarcodeMappings.add(new UuidBarcodeMapping() {
            {
                setUuid("0c3410c8-754c-468f-bf2a-60641cfc9114");
                setBarcode("TCGA-C9-R1D1");
            }
        });
        uuidBarcodeMappings.add(new UuidBarcodeMapping() {
            {
                setUuid("e01c49bc-9184-4e0d-82ae-88d5a5d2e7b1");
                setBarcode("TCGA-11-1111");
            }
        });

        Mockito.when(uuidService.getLatestBarcodesForUUIDs(uuidList)).thenReturn(uuidBarcodeMappings);

        final ClientResponse response = 
                uuidBatchResourcePathXML.type(MediaType.TEXT_PLAIN).post(ClientResponse.class, uuids);

        final String expectedResponse = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<uuidBarcodeMappings>")
                .append("<uuidMapping><barcode>TCGA-C4-A0B0</barcode><uuid>db9e8afb-e863-4bfc-bd48-f5595d2de474</uuid></uuidMapping>")
                .append("<uuidMapping><barcode>TCGA-C9-R1D1</barcode><uuid>0c3410c8-754c-468f-bf2a-60641cfc9114</uuid></uuidMapping>")
                .append("<uuidMapping><barcode>TCGA-11-1111</barcode><uuid>e01c49bc-9184-4e0d-82ae-88d5a5d2e7b1</uuid></uuidMapping>")
                .append("</uuidBarcodeMappings>").toString();

        assertEquals(expectedResponse, response.getEntity(String.class));
    }

    @Test
    public void shouldReturnJSONResponseForBarcodeToUUIDMappings() {
        final String uuids = "46beab59-cc44-46de-b5b7-5a9776a770ff,d44c1e52-ee47-4879-9698-9839fea8c76c,88a9f580-05ae-43e8-bb0e-991ae6e72814";

        final List<String> uuidList = Arrays.asList(uuids.split(","));

        final List<UuidBarcodeMapping> uuidBarcodeMappings = new ArrayList<UuidBarcodeMapping>();
        uuidBarcodeMappings.add(new UuidBarcodeMapping() {
            {
                setUuid("46beab59-cc44-46de-b5b7-5a9776a770ff");
                setBarcode("TCGA-C4-C3PO");
            }
        });
        uuidBarcodeMappings.add(new UuidBarcodeMapping() {
            {
                setUuid("d44c1e52-ee47-4879-9698-9839fea8c76c");
                setBarcode("TCGA-C4-R2D2");
            }
        });
        uuidBarcodeMappings.add(new UuidBarcodeMapping() {
            {
                setUuid("88a9f580-05ae-43e8-bb0e-991ae6e72814");
                setBarcode("TCGA-00-0000");
            }
        });

        Mockito.when(uuidService.getLatestBarcodesForUUIDs(uuidList)).thenReturn(uuidBarcodeMappings);

        final ClientResponse response = uuidBatchResourcePathJSON.type(MediaType.TEXT_PLAIN).post(ClientResponse.class, uuids);

        final String expectedResponse = new StringBuilder()
                .append("{\"uuidMapping\":[")
                .append("{\"barcode\":\"TCGA-C4-C3PO\",\"uuid\":\"46beab59-cc44-46de-b5b7-5a9776a770ff\"},")
                .append("{\"barcode\":\"TCGA-C4-R2D2\",\"uuid\":\"d44c1e52-ee47-4879-9698-9839fea8c76c\"},")
                .append("{\"barcode\":\"TCGA-00-0000\",\"uuid\":\"88a9f580-05ae-43e8-bb0e-991ae6e72814\"}")
                .append("]}").toString();

        assertEquals(expectedResponse, response.getEntity(String.class));
    }

}
