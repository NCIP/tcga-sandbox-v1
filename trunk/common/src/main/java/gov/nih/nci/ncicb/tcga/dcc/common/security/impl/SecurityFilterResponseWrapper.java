package gov.nih.nci.ncicb.tcga.dcc.common.security.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.filters.SecurityWrapperResponse;

/**
 * Wrapper class that overrides functionality provided by the {@link org.owasp.esapi.filters.SecurityWrapperResponse}
 * that are specific to TCGA software applications.
 * 
 * @author nichollsmc
 *
 */
public class SecurityFilterResponseWrapper extends SecurityWrapperResponse {
	
	private final Logger logger = ESAPI.getLogger(SecurityFilterResponseWrapper.class);
	
	private boolean bypassDefaultResponseStatusCode;
	
	public static final String ETAG_HEADER_NAME = "ETag";

	public SecurityFilterResponseWrapper(final HttpServletResponse response) {
		super(response);
	}
	
	@Override
	public void addHeader(final String headerName, final String headerValue) {
		if(ETAG_HEADER_NAME.equals(headerName)) {
			((HttpServletResponse)super.getResponse()).addHeader(headerName, headerValue);
		}
		else {
			super.addHeader(headerName, headerValue);
		}
		
	}
	
	@Override
	public void setHeader(final String headerName, final String headerValue) {
		if(ETAG_HEADER_NAME.equals(headerName)) {
			((HttpServletResponse)super.getResponse()).setHeader(headerName, headerValue);
		}
		else {
			super.addHeader(headerName, headerValue);
		}
	}
	
	@Override
    public void sendError(int sc) throws IOException {
    	if(bypassDefaultResponseStatusCode) {
    		((HttpServletResponse)super.getResponse()).sendError(sc, getHTTPMessage(sc));
    	}
    	else {
    		super.sendError(sc);
    	}
    }

	@Override
    public void sendError(int sc, String msg) throws IOException {
		if(bypassDefaultResponseStatusCode) {
    		((HttpServletResponse)super.getResponse()).sendError(sc, ESAPI.encoder().encodeForHTML(msg));
    	}
    	else {
    		super.sendError(sc, msg);
    	}
    }
	
	@Override
	public void setStatus(int sc) {
		if(bypassDefaultResponseStatusCode) {
			((HttpServletResponse)super.getResponse()).setStatus(sc);
		}
		else {
			super.setStatus(sc);
		}
    }
	
    @Deprecated
    public void setStatus(int sc, String sm) {
    	if(bypassDefaultResponseStatusCode) {
    		try {
    			((HttpServletResponse)super.getResponse()).sendError(sc, ESAPI.encoder().encodeForHTML(sm));
    		} 
    		catch(IOException e) {
    			logger.warning(Logger.SECURITY_FAILURE, "Attempt to set response status failed", e);
	        }
		}
		else {
			super.setStatus(sc, sm);
		}
    }
    
    /**
     * returns a text message for the HTTP response code
     */
    private String getHTTPMessage(int sc) {
        return "HTTP error code: " + sc;
    }
    
    /**
     * Sets the flag that indicates whether or not default HTTP response status codes should be bypassed.
     * 
     * @param bypassDefaultResponseStatusCode - set to true if HTTP status codes should be bypassed, false otherwise.
     */
    public void setBypassDefaultResponseStatusCode(boolean bypassDefaultResponseStatusCode) {
		this.bypassDefaultResponseStatusCode = bypassDefaultResponseStatusCode;
	}
    
    /**
     * Retrieves the boolean indicating whether or not to bypass the default HTTP response status code (200 OK) 
     * provided by the ESAPI security framework and allow status codes set by the container to pass through.
     * 
     * @return true if the response status code can be bypassed, false otherwise (200 OK) will be used
     */
    public boolean canBypassDefaultResponseStatusCode() {
		return bypassDefaultResponseStatusCode;
	}
}