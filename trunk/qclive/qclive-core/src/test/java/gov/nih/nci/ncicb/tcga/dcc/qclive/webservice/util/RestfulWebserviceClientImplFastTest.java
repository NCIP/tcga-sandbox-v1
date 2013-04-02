package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util;

import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.assertEquals;

/**
 * Test class for the RestfulWebserviceClientImpl
 *
 * @author Deepak Srinivasan
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class RestfulWebserviceClientImplFastTest {
    private RestfulWebserviceClientImpl client;
    private WebserviceOutput output;

    @Before
    public void setup() {
        client = new RestfulWebserviceClientImpl() {
            protected WebserviceOutput get(WebResource s, WebserviceInput input) {
                return output;
            }
        };
    }

    @Test
    public void testExecuteGetSuccess() {
        output = new WebserviceOutput(200);
        final String uri = "https://whoami.fake.foo/foo";
        final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_JSON_TYPE,  uri);
        final WebserviceOutput output = client.executeGet(config);
        assertEquals(200, output.getStatus());
    }

    @Test
    public void testExecuteGetFail() {
        output = new WebserviceOutput(500);
        final String uri = "https://whoami.fake.foo/foo";
        final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_JSON_TYPE,  uri);
        final WebserviceOutput output = client.executeGet(config);
        assertEquals(500, output.getStatus());
    }

    @Test
    public void testExecuteValidBarcode() {
        output = new WebserviceOutput(200);
        final String uri = "https://whoami.fake.foo/uuid/uuidws/metadata/json/barcode/TCGA-02-0001";
        final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_JSON_TYPE,  uri);
        final WebserviceOutput output = client.executeGet(config);
        assertEquals(200, output.getStatus());
    }

    @Test
    public void testExecuteGetInvalidUri() {
        output = new WebserviceOutput(404);
        final String uri = "https://whoami.fake.foo/foo";
        final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_JSON_TYPE,  uri);
        final WebserviceOutput output = client.executeGet(config);
        assertEquals(404, output.getStatus());
    }


    @Test
    public void testExecuteInvalidBarcode() {
        output = new WebserviceOutput(500);
        final String uri = "https://whoami.fake.foo/uuid/uuidws/metadata/json/barcode/TCGA-A2-0001";
        final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_JSON_TYPE,  uri);
        final WebserviceOutput output = client.executeGet(config);
        assertEquals(500, output.getStatus());
    }

    @Test
    public void testExecuteInvalidBarcodeFormat() {
        output = new WebserviceOutput(422);
        final String uri = "https://whoami.fake.foo/uuid/uuidws/metadata/json/barcode/TCGA-A2-0001-01";
        final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_JSON_TYPE,  uri);
        final WebserviceOutput output = client.executeGet(config);
        assertEquals(422, output.getStatus());
    }

    @Test
    public void testExecuteInvalidApplicationType() {
        output = new WebserviceOutput(406);
        final String uri = "https://whoami.fake.foo/uuid/uuidws/metadata/json/barcode/TCGA-02-0001-01";
        final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_XML_TYPE,  uri);
        final WebserviceOutput output = client.executeGet(config);
        assertEquals(406, output.getStatus());
    }
}
