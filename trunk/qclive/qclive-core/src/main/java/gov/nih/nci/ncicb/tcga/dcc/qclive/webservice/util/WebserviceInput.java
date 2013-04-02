package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Model used as input to the RestfulWebserviceClientImpl
 * Encapsulates commonly required parameters to execute a call to a restful webservice
 * via the Jersey API
 *
 * @author Deepak Srinivasan
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class WebserviceInput {
    /**
     * It is upto the caller to determine the corect <code>MediaType</code> to use based
     * on what the service is willing to respond with.
     */
    private MediaType mediaType;

    /**
     * The <code>URI</code> <code>String</code> of the webservice to call.
     */
    private String uri;


    private Class outputEntityClassName;
    /**
     * Constructor for the mandatory parameters
     * @param mediaType
     * @param uri
     */
    public WebserviceInput(MediaType mediaType, String uri) {
        this.mediaType = mediaType;
        this.uri = uri;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * Using the uri, automatically URL encoding the provided string so that illegal characters are escaped
     * as per the rules of the Jersey UriBuilder
     * @return <code>URI</code> - the URI that can be used in further calls
     */
    public URI getUri() {
        return UriBuilder.fromUri(uri).build();
    }

    public Class getOutputEntityClassName() {
        return outputEntityClassName;
    }

    public void setOutputEntityClassName(Class outputEntityClassName) {
        this.outputEntityClassName = outputEntityClassName;
    }
}
