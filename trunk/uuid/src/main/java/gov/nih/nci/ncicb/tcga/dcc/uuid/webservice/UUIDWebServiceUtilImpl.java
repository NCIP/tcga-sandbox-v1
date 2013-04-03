package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceContext;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationErrors;
import gov.nih.nci.ncicb.tcga.dcc.uuid.webservice.bean.UUIDBrowserWSQueryParamBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for UUID Web Services
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Component
public class UUIDWebServiceUtilImpl implements UUIDWebServiceUtil{

	// Logger
	private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private UUIDService service;

    @Autowired
    private Validator validator;

    public int getCenterId(final String centerName, final String centerType, final String mediaType){
        int centerId = 0;
        if((StringUtils.isNotBlank(centerName)) || (StringUtils.isNotBlank(centerType))) { //at least one is specified

            final int statusCode = HttpStatusCode.OK;
            final String invalidValue = new StringBuilder("[center name:").append(centerName).append("][center type:").append(centerType).append("]").toString();

            if ((StringUtils.isBlank(centerName)) || (StringUtils.isBlank(centerType))) { // both should be specified
                final String errorMessage = "Both center name and center type should be specified.";
                final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, invalidValue, errorMessage);
                throw new WebApplicationException(response);

            } else {
                Center center = service.getCenterByNameAndType(centerName, centerType);
                if(center != null) {
                    centerId = center.getCenterId();
                }else {
                    final String errorMessage = "Center not found for the given name and center type.";
                    final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, invalidValue, errorMessage);
                    throw new WebApplicationException(response);
                }
            }
        }else {
             // this is a valid scenario in web services where center information is optional
        }
        return centerId;
    }

    public int getDiseaseId(final String diseaseName, final String mediaType){

        int diseaseId = -1;
        if (!StringUtils.isBlank(diseaseName)) {
            Tumor tumor = service.getTumorForName(diseaseName);
            if(tumor != null) {
                diseaseId = tumor.getTumorId();
            }else {
                final int statusCode = HttpStatusCode.OK;
                final String errorMessage = "Disease not found.";
                final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, diseaseName, errorMessage);
                throw new WebApplicationException(response);
            }
        }
        return diseaseId;
    }

    protected void setService(final UUIDService service) {
        this.service = service;
    }

    @Override
    public UUIDBrowserWSQueryParamBean getQueryParams(ResourceContext resourceContext, UriInfo uriInfo, String mediaType) {
    	return getQueryParams(resourceContext, uriInfo, mediaType, null);
    }

    @Override
    public UUIDBrowserWSQueryParamBean getQueryParams(ResourceContext resourceContext, UriInfo uriInfo, String mediaType, String queryParamName) {

    	// Check that the query parameters are accessible members of the UUIDBrowserWSQueryParamBean
    	MultivaluedMap<String, String> rawQueryParams = uriInfo.getQueryParameters();
    	Map<String, String> invalidFields = new HashMap<String, String>();
    	if(rawQueryParams != null && !rawQueryParams.isEmpty()) {
    		for(String paramKey : rawQueryParams.keySet()) {
    			try {
    				UUIDBrowserWSQueryParamBean.class.getDeclaredField(paramKey);
				}
    			catch (SecurityException se) {
    				throw new ContainerException(se);
				}
    			catch (NoSuchFieldException nsfe) {
    				invalidFields.put(paramKey, "is not a valid query parameter");
				}
    		}
    	}

    	// If any invalid fields are present, build an error response with an HTTP status code of 422 with the
		// invalid fields as the payload
    	if(invalidFields.size() > 0) {
    		buildErrorResponse(new ValidationErrors<Object>(invalidFields), HttpStatusCode.UNPROCESSABLE_ENTITY, mediaType);
    	}

    	// Retrieve the query parameters from the request URI
    	UUIDBrowserWSQueryParamBean queryParamBean = resourceContext.getResource(UUIDBrowserWSQueryParamBean.class);

    	// Perform validation
    	validate(queryParamBean, queryParamName, mediaType);

    	return queryParamBean;
    }

    @Override
    public <T> void validate(final T bean, final String beanFieldName, final String mediaType) {

    	logger.info("Validating bean properties...");

		Set<ConstraintViolation<T>> constraintViolations = null;

		// If beanFieldName parameter is set, only validate the corresponding property name of the bean,
		// otherwise validate all bean properties
		if(beanFieldName != null && !beanFieldName.trim().isEmpty()) {

			logger.debug("Bean field name '" + beanFieldName + "' provided, performing single bean property validation");

			try {
				constraintViolations = validator.validateProperty(bean, beanFieldName, Default.class);
			}
			catch(IllegalArgumentException iae) {

				// If we get here, it more than likely means that the value specified by beanFieldName is not a
				// valid property of the bean, so we should at least try validation of the entire bean before we
				// fail validation altogether
				constraintViolations = validator.validate(bean);
			}
		}
		else {
			constraintViolations = validator.validate(bean);
		}

		// If any constraint violations are detected, build an error response with an HTTP status code of 422 with the
		// constraint violations as the payload
		if(constraintViolations.size() > 0) {

			if(logger.isErrorEnabled()) {
				StringBuilder violations = new StringBuilder();
				violations.append("Validation failed for bean: \n");
				violations.append("\n" + bean + "\n");
				violations.append("\nConstraint violations:\n");
				for(ConstraintViolation<T> constraintViolation : constraintViolations) {
					violations.append("\tValue: '" + constraintViolation.getInvalidValue() +
							"', Reason: '" + constraintViolation.getMessage() + "'\n");
				}
				logger.error(violations);
			}

			buildErrorResponse(new ValidationErrors<T>(constraintViolations), HttpStatusCode.UNPROCESSABLE_ENTITY, mediaType);
		}

		logger.info("Bean passed all validation constraints");
    }

    /**
     * Builds an error response using the provided validation results, HTTP status code, and media type.
     *
     * @param validationResult - the validation results
     * @param statusCode - the HTTP status code
     * @param mediaType - the {@link javax.ws.rs.core.MediaType} to use for error responses, default is {@link javax.ws.rs.core.MediaType#APPLICATION_XML}
     */
    private void buildErrorResponse(ValidationErrors<?> validationResult, int statusCode, String mediaType) {
    	Response response = Response.status(statusCode)
		.entity(validationResult)
		.type((mediaType == null ? MediaType.APPLICATION_XML : mediaType))
		.build();

    	throw new WebApplicationException(response);
    }
    
    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

}
