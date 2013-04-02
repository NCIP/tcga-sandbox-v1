package gov.nih.nci.ncicb.tcga.dcc.common.security.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.StringUtilities;

/**
 * Servlet security filter class that filters all requests and responses for a specific application.
 * 
 * <p/>
 * <b>To use this filter for TCGA applications, the following should be placed in the deployment descriptor 
 * (web.xml):</b>
 * <pre>
 *     &lt;filter>
 *         &lt;display-name>Security Filter&lt;/display-name&gt;
 *         &lt;filter-name>securityFilter&lt;/filter-name&gt;
 *         &lt;filter-class>gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityFilter&lt;/filter-class&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name>allowableResourcesRoot&lt;/param-name&gt;
 *             &lt;param-value>/&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         
 *         &lt;!-- The following initialization parameters are optional. --&gt;
 *         
 *         &lt;!-- If not provided, default is java.lang.Integer.MAX_VALUE (2^31 - 1). --&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name>paramValueMaxLength&lt;/param-name&gt;
 *             &lt;param-value>4000&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;!-- If not provided, default is java.lang.Integer.MAX_VALUE (2^31 - 1). --&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name>queryStringMaxLength&lt;/param-name&gt;
 *             &lt;param-value>4000&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;!-- If not provided, default is java.lang.Integer.MAX_VALUE (2^31 - 1). --&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name>uriStringMaxLength&lt;/param-name&gt;
 *             &lt;param-value>4000&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;!-- If not provided, default is java.lang.Integer.MAX_VALUE (2^31 - 1). --&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name>urlStringMaxLength&lt;/param-name&gt;
 *             &lt;param-value>4000&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;!-- List of query parameter names to be ignored when performing
 *             validation --&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name>ignoreParamValidationForNames&lt;/param-name&gt;
 *             &lt;param-value>paramName1,paramName2&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;!-- Boolean indicating whether or not to bypass the default HTTP 
 *             response status code (200 OK) provided by the ESAPI security 
 *             framework and allow status codes set by the container to pass
 *             through. --&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name>bypassDefaultResponseStatusCode&lt;/param-name&gt;
 *             &lt;param-value>true&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;!-- List of URI paths to enforce strict validation on --&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name>useStrictValidationForURIPaths&lt;/param-name&gt;
 *             &lt;param-value>uri1,uri2&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;!-- List of URI paths that should not use canonicalization during validation --&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name>ignoreCanonicalizationForURIPaths&lt;/param-name&gt;
 *             &lt;param-value>uri1,uri2&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;!-- Boolean indicating whether or not to retain empty values for query parameters.
 *             If this init parameter is not provided, the default will be false, and all query parameter
 *             values with empty strings will be set to null. --&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name>retainEmptyQueryParamValues&lt;/param-name&gt;
 *             &lt;param-value>true&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *     &lt;/filter&gt;
 *     
 *     &lt;filter-mapping>
 *         &lt;filter-name>securityFilter&lt;/filter-name&gt;
 *         &lt;url-pattern>/*&lt;/url-pattern&gt;
 *     &lt;/filter-mapping&gt;
 * </pre>
 * 
 * @author nichollsmc
 */
public class SecurityFilter implements Filter {
	
	// Security logger
	private static final org.owasp.esapi.Logger securityLogger = ESAPI.getLogger(SecurityFilter.class);
	
	// Normal logger
	private static final Log logger = LogFactory.getLog(SecurityFilter.class);
	
	/** Initialization parameter names that can be provided in the deployment descriptor **/
	public static final String ALLOWABLE_RESOURCES_ROOT_INIT_PARAM_NAME = "allowableResourcesRoot";
	public static final String PARAM_VALUE_MAX_LENGTH_INIT_PARAM_NAME = "paramValueMaxLength";
	public static final String QUERY_STRING_MAX_LENGTH_INIT_PARAM_NAME = "queryStringMaxLength";
	public static final String URI_STRING_MAX_LENGTH_INIT_PARAM_NAME = "uriStringMaxLength";
	public static final String URL_STRING_MAX_LENGTH_INIT_PARAM_NAME = "urlStringMaxLength";
	public static final String IGNORE_PARAM_VALIDATION_FOR_NAMES_INIT_PARAM_NAME = "ignoreParamValidationForNames";
	public static final String BYPASS_DEFAULT_RESPONSE_STATUS_CODE_INIT_PARAM_NAME = "bypassDefaultResponseStatusCode";
	public static final String USE_STRICT_VALIDATION_FOR_URI_PATHS_INIT_PARAM_NAME = "useStrictValidationForURIPaths";
	public static final String IGNORE_CANONICALIZATION_FOR_URI_PATHS_INIT_PARAM_NAME = "ignoreCanonicalizationForURIPaths";
	public static final String RETAIN_EMPTY_QUERY_PARAM_VALUES_INIT_PARAM_NAME = "retainEmptyQueryParamValues";
	
	private static final String DEFAULT_ALLOWABLE_RESOURCES_ROOT_VALUE = "WEB-INF";
	private static final Integer DEFAULT_MAX_LENGTH = Integer.MAX_VALUE;
	private static final String FILTER_CONFIG_PARAM_VALUE_SEPARATOR = ",";
	
	private FilterConfig filterConfig;
	
	@Override
	public void init(final FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
	
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
			throws ServletException, IOException {

		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			throw new ServletException("SecurityFilter only supports HTTP requests");
		}
		
		HttpServletRequest httpServletRequest = null;
        HttpServletResponse httpServletResponse = null;
        SecurityFilterRequestWrapper secureRequest = null;
        SecurityFilterResponseWrapper secureResponse = null;
        String allowableResourcesRoot = null;
        
		try {
			httpServletRequest = (HttpServletRequest)request;
			httpServletResponse = (HttpServletResponse)response;
			
			if(logger.isDebugEnabled()) {
				final StringBuilder requestOutput = new StringBuilder();
				
				requestOutput.append("\n\nRaw HTTP servlet request parameters for request URI: [")
				.append(httpServletRequest.getRequestURI())
				.append("] with query string: [")
				.append(httpServletRequest.getQueryString())
				.append("]")
				.append(queryParamsToString(httpServletRequest));
				
				logger.debug(requestOutput.toString());
			}
			
	        secureRequest = getSecurityFilterRequestWrapper(httpServletRequest);
	        secureResponse = getSecurityFilterRequestWrapper(httpServletResponse);
	        
	        if(logger.isDebugEnabled()) {
				final StringBuilder secureRequestOutput = new StringBuilder();
				
				secureRequestOutput.append("\n\nSecure HTTP servlet request parameters for request URI: [")
				.append(secureRequest.getRequestURI())
				.append("] with query string: [")
				.append(secureRequest.getQueryString())
				.append("]")
				.append(queryParamsToString(secureRequest));
				
				logger.debug(secureRequestOutput.toString());
			}
	        
	        // Set the configuration on the wrapped request
	        allowableResourcesRoot = StringUtilities.replaceNull(
	        		filterConfig.getInitParameter(ALLOWABLE_RESOURCES_ROOT_INIT_PARAM_NAME), DEFAULT_ALLOWABLE_RESOURCES_ROOT_VALUE); 
            secureRequest.setAllowableContentRoot(allowableResourcesRoot);
            ESAPI.httpUtilities().setCurrentHTTP(secureRequest, secureResponse);
            
            // Invoke the next filter in the chain
            filterChain.doFilter(ESAPI.currentRequest(), ESAPI.currentResponse());
        } 
		catch(Exception e) {
			// Log the security error and set the request attribute for downstream processors, if any
			securityLogger.error(org.owasp.esapi.Logger.SECURITY_FAILURE, "Error in SecurityWrapper: " + e.getMessage(), e);
			request.setAttribute("message", e.getMessage());
			
			if(secureResponse.canBypassDefaultResponseStatusCode()) {
				secureResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
        }
		finally {
            // VERY IMPORTANT
            // clear out the ThreadLocal variables in the authenticator
            // some containers could possibly reuse this thread without clearing the User
            // Issue 70 - http://code.google.com/p/owasp-esapi-java/issues/detail?id=70
            ESAPI.httpUtilities().clearCurrent();
        }
	}
	
	@Override
	public void destroy() {
		// Do nothing
	}
	
	private SecurityFilterRequestWrapper getSecurityFilterRequestWrapper(final HttpServletRequest httpServletRequest) {
		
		final SecurityFilterRequestWrapper securityFilterRequestWrapper = new SecurityFilterRequestWrapper(httpServletRequest);
		
		securityFilterRequestWrapper.setParamValueMaxLength(getMaxLength(PARAM_VALUE_MAX_LENGTH_INIT_PARAM_NAME));
		securityFilterRequestWrapper.setQueryStringMaxLength(getMaxLength(QUERY_STRING_MAX_LENGTH_INIT_PARAM_NAME));
		securityFilterRequestWrapper.setUriStringMaxLength(getMaxLength(URI_STRING_MAX_LENGTH_INIT_PARAM_NAME));
		securityFilterRequestWrapper.setUrlStringMaxLength(getMaxLength(URL_STRING_MAX_LENGTH_INIT_PARAM_NAME));
		securityFilterRequestWrapper.setIgnoreParamValidationForNames(getValuesForFilterConfigParam(IGNORE_PARAM_VALIDATION_FOR_NAMES_INIT_PARAM_NAME));
		securityFilterRequestWrapper.setStrictValidationURIs(getValuesForFilterConfigParam(USE_STRICT_VALIDATION_FOR_URI_PATHS_INIT_PARAM_NAME));
		securityFilterRequestWrapper.setNoncanonicalizedURIs(getValuesForFilterConfigParam(IGNORE_CANONICALIZATION_FOR_URI_PATHS_INIT_PARAM_NAME));
		securityFilterRequestWrapper.setRetainEmptyQueryParamValues(getBooleanForFilterConfigParam(RETAIN_EMPTY_QUERY_PARAM_VALUES_INIT_PARAM_NAME));
		
		return securityFilterRequestWrapper;
				
	}
	
	private SecurityFilterResponseWrapper getSecurityFilterRequestWrapper(final HttpServletResponse httpServletResponse) {
		
		final SecurityFilterResponseWrapper securityFilterResponseWrapper = new SecurityFilterResponseWrapper(httpServletResponse);
		
		securityFilterResponseWrapper.setBypassDefaultResponseStatusCode(
				getBooleanForFilterConfigParam(BYPASS_DEFAULT_RESPONSE_STATUS_CODE_INIT_PARAM_NAME));
		
		return securityFilterResponseWrapper;
				
	}
	
	private Integer getMaxLength(final String value) {
		Integer maxLength;
		try {
			maxLength = Integer.parseInt(filterConfig.getInitParameter(value));
		}
		catch(NumberFormatException nfe) {
			return DEFAULT_MAX_LENGTH;
		}
		
		if(maxLength > 0) {
			return maxLength;
		}
		else {
			return DEFAULT_MAX_LENGTH;
		}
	}
	
	private List<String> getValuesForFilterConfigParam(final String filterConfigParam) {
		final String values = filterConfig.getInitParameter(filterConfigParam);
		final List<String> valueList = new ArrayList<String>();
		if(values != null && !values.isEmpty()) {
			final String[] parsedValues = values.split(FILTER_CONFIG_PARAM_VALUE_SEPARATOR);
			if(parsedValues.length > 0) {
				valueList.addAll(Arrays.asList(parsedValues));
			}
		}
		
		return valueList;
	}
	
	private boolean getBooleanForFilterConfigParam(final String filterConfigParam) {
		return Boolean.parseBoolean(filterConfig.getInitParameter(filterConfigParam));
	}
	
	private String queryParamsToString(final HttpServletRequest httpServletRequest) {
		final StringBuilder output = new StringBuilder();
		output.append("\n---------------------------------\n");
		if(httpServletRequest != null) {
			Map<String, String[]> paramMap = httpServletRequest.getParameterMap();
			if(paramMap != null && !paramMap.isEmpty()) {
				for(final String paramName : paramMap.keySet()) {
					output.append(paramName + ": ");
					String[] paramValues = paramMap.get(paramName);
					if(paramValues != null && paramValues.length > 0) {
						for(final String paramValue : paramValues) {
							output.append(paramValue + " ");
						}
					}
					output.append("\n");
				}
			}
			else {
				output.append("No query parameters were found.\n");
			}
			
		}
		else {
			output.append("HTTP servlet request object was null");
		}
		output.append("---------------------------------\n");
		
		return output.toString();
	}
}