package gov.nih.nci.ncicb.tcga.dcc.common.security.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.filters.SecurityWrapperRequest;

/**
 * Wrapper class that overrides functionality provided by the {@link org.owasp.esapi.filters.SecurityWrapperRequest}
 * that are specific to TCGA software applications.
 * 
 * @author nichollsmc
 *
 */
public class SecurityFilterRequestWrapper extends SecurityWrapperRequest {
	
	private static final Logger securityLogger = ESAPI.getLogger(SecurityFilterRequestWrapper.class);
	
	private Integer paramValueMaxLength;
	private Integer queryStringMaxLength;
	private Integer uriStringMaxLength;
	private Integer urlStringMaxLength;
	private List<String> ignoreParamValidationForNames;
	private List<String> strictValidationURIs;
	private List<String> noncanonicalizedURIs;
	private boolean retainEmptyQueryParamValues;
	
	public static final String IF_NONE_MATCH_HEADER_NAME = "If-None-Match";

	public SecurityFilterRequestWrapper(final HttpServletRequest request) {
		super(request);
	}

    @Override
    public String getPathInfo() {
        String path = ((HttpServletRequest)super.getRequest()).getPathInfo();
		if (path == null) return null;
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP path: " + path, path, "HTTPPath", 4000, true);
        } catch (ValidationException e) {
            // already logged
        }
        return clean;
    }
    
    @Override
	public String getHeader(final String headerName) {
    	if(IF_NONE_MATCH_HEADER_NAME.equals(headerName)) {
        	return ((HttpServletRequest)super.getRequest()).getHeader(headerName);
        }
        else {
        	return super.getHeader(headerName);
        }
    }

    @Override
	public Enumeration getHeaders(final String headerName) {
        if(IF_NONE_MATCH_HEADER_NAME.equals(headerName)) {
        	return ((HttpServletRequest)super.getRequest()).getHeaders(headerName);
        }
        else {
        	return super.getHeaders(headerName);
        }
    }
    
    @Override
    public String getParameter(final String name, final boolean allowNull, final int maxLength, final String regexName) {
    	
    	final HttpServletRequest httpServletRequest = (HttpServletRequest)super.getRequest();
    	final boolean useStrictValidationForURI = containsURI(httpServletRequest.getRequestURI(), strictValidationURIs);
    	
    	if(ignoreParamValidationForNames != null && ignoreParamValidationForNames.contains(name)) {
    		return httpServletRequest.getParameter(name);
    	}
    	else {
    		final String orig = httpServletRequest.getParameter(name);
    		
    		// If the original parameter value is an empty string and should be retained, return the original value
    		if(orig != null && orig.trim().isEmpty() && retainEmptyQueryParamValues) {
    			return orig;
    		}
    		
            String clean = null;
            final String validationRegexPropName = useStrictValidationForURI ? "HTTPParameterValue" : "TCGAHTTPParameterValue";
            try {
                clean = ESAPI.validator().getValidInput("HTTP parameter name: " + name, orig, validationRegexPropName, paramValueMaxLength, allowNull);
            } 
            catch (ValidationException ve) {
            	if(useStrictValidationForURI) {
            		throw new IntrusionException("Input validation failure", ve.getLogMessage());
            	}
            	else {
            		// validation exception already logged
            	}
            }
            
            return clean;
    	}
    }
    
    @Override
    public Map<String, String[]> getParameterMap() {
        @SuppressWarnings({"unchecked"})
        final HttpServletRequest httpServletRequest = (HttpServletRequest)super.getRequest();
        final Map<String, String[]> map = httpServletRequest.getParameterMap();
        final Map<String, String[]> cleanMap = new HashMap<String, String[]>();
        final boolean useStrictValidationForURI = containsURI(httpServletRequest.getRequestURI(), strictValidationURIs);
        
        for(final Object o : map.entrySet()) {
            try {
            	Map.Entry e = (Map.Entry) o;
                final String name = (String) e.getKey();
                final String cleanName = ESAPI.validator().getValidInput("HTTP parameter name: " + name, name, "HTTPParameterName", 100, true);

                if(ignoreParamValidationForNames != null && ignoreParamValidationForNames.contains(name)) {
                	cleanMap.put(cleanName, (String[]) e.getValue());
                }
                else {
                	final String[] value = (String[]) e.getValue();
	                final String[] cleanValues = new String[value.length];
	                final String validationRegexPropName = useStrictValidationForURI ? "HTTPParameterValue" : "TCGAHTTPParameterValue";
	                for (int j = 0; j < value.length; j++) {
	                	// If the original parameter value is an empty string and should be retained, add the original value to the 
	                	// list of clean values without validation
	            		if(value[j] != null && value[j].trim().isEmpty() && retainEmptyQueryParamValues) {
	            			cleanValues[j] = value[j];
	            		}
	            		else {
	            			final String cleanValue = ESAPI.validator().getValidInput(
	            					"HTTP parameter value: " + value[j], value[j], validationRegexPropName, paramValueMaxLength, true);
	            			cleanValues[j] = cleanValue;
	            		}
	                }
	                
	                cleanMap.put(cleanName, cleanValues);
                }
            } 
            catch (ValidationException ve) {
            	if(useStrictValidationForURI) {
            		throw new IntrusionException("Input validation failure", ve.getLogMessage());
            	}
            	else {
            		// validation exception already logged
            	}
            }
        }
        
        return cleanMap;
    }
    
    @Override
    public String[] getParameterValues(final String name) {
    	
    	final HttpServletRequest httpServletRequest = (HttpServletRequest)super.getRequest();
    	final boolean useStrictValidationForURI = containsURI(httpServletRequest.getRequestURI(), strictValidationURIs);
    	
    	if(ignoreParamValidationForNames != null && ignoreParamValidationForNames.contains(name)) {
    		return httpServletRequest.getParameterValues(name);
    	}
    	else {
	        final String[] values = httpServletRequest.getParameterValues(name);
	        List<String> newValues;
	        
	        if(values == null) {
	        	return null;
	        }
	        
	        newValues = new ArrayList<String>();
	        final String validationRegexPropName = useStrictValidationForURI ? "HTTPParameterValue" : "TCGAHTTPParameterValue";
	        for(final String value : values) {
	            try {
	            	// If the original parameter value is an empty string and should be retained, add the original value to the 
                	// list of new values without validation
            		if(value != null && value.trim().isEmpty() && retainEmptyQueryParamValues) {
            			newValues.add(value);
            		}
            		else {
            			final String cleanValue = ESAPI.validator().getValidInput(
            					"HTTP parameter value: " + value, value, validationRegexPropName, paramValueMaxLength, true);
            			newValues.add(cleanValue);
            		}
	            } 
	            catch (ValidationException ve) {
	            	if(useStrictValidationForURI) {
	            		throw new IntrusionException("Input validation failure", ve.getLogMessage());
	            	}
	            	else {
	            		// validation exception already logged
	            	}
	            }
	        }
	        return newValues.toArray(new String[newValues.size()]);
    	}
    }
    
    @Override
    public String getQueryString() {
    	final HttpServletRequest httpServletRequest = (HttpServletRequest)super.getRequest();
        final String query = httpServletRequest.getQueryString();
        final boolean ignoreCanonicalizationForURI = containsURI(httpServletRequest.getRequestURI(), noncanonicalizedURIs);
        String clean = "";
        try {
        	if(ignoreCanonicalizationForURI) {
        		clean = ESAPI.validator().getValidInput("HTTP query string: " + query, query, "TCGAHTTPQueryString", queryStringMaxLength, true, false);
        	}
        	else {
        		clean = ESAPI.validator().getValidInput("HTTP query string: " + query, query, "TCGAHTTPQueryString", queryStringMaxLength, true);
        	}
        } 
        catch (ValidationException e) {
            // already logged
        }
        return clean;
    }
    
    @Override
    public String getRequestURI() {
        final String uri = ((HttpServletRequest)super.getRequest()).getRequestURI();
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP URI: " + uri, uri, "TCGAHTTPURI", uriStringMaxLength, false);
        } 
        catch (ValidationException e) {
            // already logged
        }
        return clean;
    }
    
    @Override
    public StringBuffer getRequestURL() {
        final String url = ((HttpServletRequest)super.getRequest()).getRequestURL().toString();
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP URL: " + url, url, "TCGAHTTPURL", urlStringMaxLength, false);
        } 
        catch (ValidationException e) {
            // already logged
        }
        return new StringBuffer(clean);
    }
    
    private boolean containsURI(final String requestURI, final List<String> requestURIs) {
    	if(requestURI == null || requestURI.isEmpty()) {
    		return false;
    	}
    	
    	if(requestURIs != null && !requestURIs.isEmpty()) {
    		for(String uri : requestURIs) {
    			if(requestURI.contains(uri)) {
    				return true;
    			}
    		}
    		return false;
    	}
    	else {
    		return false;
    	}
    }
    
    public Integer getParamValueMaxLength() {
		return paramValueMaxLength;
	}

	public void setParamValueMaxLength(final Integer paramValueMaxLength) {
		this.paramValueMaxLength = paramValueMaxLength;
	}

	public Integer getQueryStringMaxLength() {
		return queryStringMaxLength;
	}

	public void setQueryStringMaxLength(final Integer queryStringMaxLength) {
		this.queryStringMaxLength = queryStringMaxLength;
	}

	public Integer getUriStringMaxLength() {
		return uriStringMaxLength;
	}

	public void setUriStringMaxLength(final Integer uriStringMaxLength) {
		this.uriStringMaxLength = uriStringMaxLength;
	}

	public Integer getUrlStringMaxLength() {
		return urlStringMaxLength;
	}

	public void setUrlStringMaxLength(final Integer urlStringMaxLength) {
		this.urlStringMaxLength = urlStringMaxLength;
	}

	public List<String> getIgnoreParamValidationForNames() {
		return ignoreParamValidationForNames;
	}

	public void setIgnoreParamValidationForNames(final List<String> ignoreParamValidationForNames) {
		this.ignoreParamValidationForNames = ignoreParamValidationForNames;
	}

	public List<String> getStrictValidationURIs() {
		return strictValidationURIs;
	}

	public void setStrictValidationURIs(final List<String> strictValidationURIs) {
		this.strictValidationURIs = strictValidationURIs;
	}

	public List<String> getNoncanonicalizedURIs() {
		return noncanonicalizedURIs;
	}

	public void setNoncanonicalizedURIs(final List<String> noncanonicalizedURIs) {
		this.noncanonicalizedURIs = noncanonicalizedURIs;
	}

	public void setRetainEmptyQueryParamValues(final boolean retainEmptyQueryParamValues) {
		this.retainEmptyQueryParamValues = retainEmptyQueryParamValues;
	}
	
	public boolean canRetainEmptyQueryParamValues() {
		return retainEmptyQueryParamValues;
	}
}