/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;


import gov.nih.nci.ncicb.tcga.dcc.uuid.webservice.bean.UUIDBrowserWSQueryParamBean;
import javax.validation.ConstraintViolation;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.ResourceContext;

/**
 * Utility Interface for UUID Web Services
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

public interface UUIDWebServiceUtil {

    /**
     * Return the center Id for the given center name and type.
     *
     * @param centerName the center name
     * @param centerType the center type
     * @param mediaType the {@link Response} media type if an error occurs
     * @return the center Id for the given center name and type
     */
    public int getCenterId(final String centerName, final String centerType, final String mediaType);

    /**
     * Return the disease Id for the given disease.
     * @param diseaseName the disease name
     * @param mediaType the {@link Response} media type if an error occurs
     * @return Return the disease Id for the given disease
     */
    public int getDiseaseId(final String diseaseName, final String mediaType);

    /**
     * Creates an instance of an {@link UUIDBrowserWSQueryParamBean} using the query parameters
     * from the request URI(s) for this service.
     * 
     * <p>
     * The bean created by this method will also perform validation of the bean and all of its properties.
     * 
     * @param resourceContext - instance of {@link ResourceContext} used to retrieve an instance of {@link UUIDBrowserWSQueryParamBean}
     * @param uriInfo - URI information associated with a request
     * @param mediaType - the {@link MediaType} to use for error responses, default is {@link MediaType#APPLICATION_XML}
     * @return a validated instance of {@link UUIDBrowserWSQueryParamBean}
     */
    public UUIDBrowserWSQueryParamBean getQueryParams(ResourceContext resourceContext, UriInfo uriInfo, String mediaType);
    
    /**
     * Creates an instance of an {@link UUIDBrowserWSQueryParamBean} using the query parameters
     * from the request URI(s) for this service.
     * 
     * <p>
     * This method behaves the same way as {@link UUIDWebServiceUtil#getQueryParams()} and takes
     * an additional parameter for specifying a single query parameter name which is used for
     * validation. If the query parameter name is null, the entire bean created by this method
     * will be validated, otherwise only the property corresponding to the query parameter name will 
     * be validated.
     * 
     * @param resourceContext - instance of {@link ResourceContext} used to retrieve an instance of {@link UUIDBrowserWSQueryParamBean}
     * @param uriInfo - URI information associated with a request
     * @param mediaType - the {@link MediaType} to use for error responses, default is {@link MediaType#APPLICATION_XML}
     * @param queryParamName - query parameter name to be validated
     * @return a validated instance of {@link UUIDBrowserWSQueryParamBean}
     */
    public UUIDBrowserWSQueryParamBean getQueryParams(ResourceContext resourceContext, UriInfo uriInfo, String mediaType, String queryParamName);
    
    /**
     * Performs bean validation per the JSR-303 specification on generic bean instances.
     * 
     * <p>
     * By default this method will perform validation on the entire bean and all of its properties. If the 
     * <code>beanFieldName</code> parameter is provided, only the property of the bean corresponding to the 
     * value of <code>beanFieldName</code> will be validated.
     * 
     * <p>
     * If any {@link ConstraintViolation}s are detected, this method will throw a runtime exception of type 
     * {@link WebApplicationException} with an HTTP status code from the {@link Response.Status.BAD_REQUEST} 
     * family of status codes, for example "HTTP/1.1 422 Unprocessable Entity" indicating that the request
     * was understood, but failed model validation.
     * 
     * @param <T> bean - the generic bean type to validate
     * @param beanFieldName - the specific property of the bean to be validated, if null or not a valid property of
     * the bean, all bean properties will be validated
     * @param mediaType - the {@link MediaType} to use for error responses, default is {@link MediaType#APPLICATION_XML}
     * @throws WebApplicationException if constraint violations are produced after bean validation
     */
    public <T> void validate(T bean, String beanFieldName, String mediaType);
}
