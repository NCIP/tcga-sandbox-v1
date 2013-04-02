package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util;

/**
 * Provide glue APIs to call restful webservices
 *
 * @author Deepak Srinivasan
 *         Last updated by: $Author$
 * @version $Rev$
 */

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.BarcodeListWS;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResults;

public class RestfulWebserviceClientImpl implements RestfulWebserviceClient {
    /**
     * Executes a GET call to a webservice API using the parameters provided
     * and returns an object representing the results returned by the call
     * @param input
     * @return <code>WebserviceOuput</code> - the output from the webservice call
     */
    public WebserviceOutput executeGet(final WebserviceInput input) {
        final Client client = getClient();
        final WebResource service = client.resource(input.getUri());
        return get(service, input);
    }

    /**
     * Provided a <code>WebResource</code>, calls the get method and returns an output
     * object containing the results of the call
     * @param s
     * @return <code>WebserviceOuput</code> - the output from the webservice call
     */
    protected WebserviceOutput get(final WebResource s, final WebserviceInput input) {

        final ClientResponse clientResponse = s.accept(input.getMediaType()).get(ClientResponse.class);
        final WebserviceOutput output = new WebserviceOutput(clientResponse.getStatus());
        final Class outputClass = input.getOutputEntityClassName();

        if (BarcodeListWS.class.equals(outputClass)) {
            output.setBarcodeListWS(clientResponse.getEntity(BarcodeListWS.class));
        } else if (ValidationResults.class.equals(outputClass)) {
            output.setValidationResults(clientResponse.getEntity(ValidationResults.class));
        }

        return output;
    }

    /**
     * Creates and returns a Jersey client for use in executing RESTful webservice calls
     * @return <code>Client</code> - The client used in Jersey calls to a webservice
     */
    private Client getClient() {
        final ClientConfig conf = new DefaultClientConfig();
        return Client.create();
    }
}
