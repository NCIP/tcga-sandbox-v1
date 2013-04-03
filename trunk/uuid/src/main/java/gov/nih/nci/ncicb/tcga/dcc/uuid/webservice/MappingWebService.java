package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UuidBarcodeMapping;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;

import com.sun.jersey.api.core.InjectParam;

/**
 * Web service for mapping between barcodes and uuids.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Path("/mapping")
public class MappingWebService {
	
    @InjectParam
    private UUIDService uuidService;
    
    @InjectParam
    private UUIDWebServiceUtil uuidWebServiceUtil;

    private CommonBarcodeAndUUIDValidatorImpl commonBarcodeAndUUIDValidator = new CommonBarcodeAndUUIDValidatorImpl();
    
    private static String DEFAULT_MAPPING_RESOURCE_DELIMITER = ",";

    @GET
    @Path("/xml/barcode/{barcode:.+}")
    @Produces(MediaType.APPLICATION_XML)
    public List<UuidBarcodeMapping> getBarcodeMappingXML(@PathParam("barcode") final String barcode) {
        return processBarcode(barcode, MediaType.APPLICATION_XML);
    }
    
    @POST
    @Path("/xml/barcode/batch")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_XML)
    public List<UuidBarcodeMapping> getUUIDsForBarcodesXML(final String barcodes) {
    	final List<String> barcodeList = validateMappingResourceEntity(barcodes, "barcode", MediaType.APPLICATION_XML_TYPE);
        return uuidService.getUUIDsForBarcodes(barcodeList);
    }
    
    @GET
    @Path("/json/barcode/{barcode:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UuidBarcodeMapping> getBarcodeMappingJSON(@PathParam("barcode") final String barcode) {
        return processBarcode(barcode, MediaType.APPLICATION_JSON);
    }

    @POST
    @Path("/json/barcode/batch")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<UuidBarcodeMapping> getUUIDsForBarcodesJSON(final String barcodes) {
    	final List<String> barcodeList = validateMappingResourceEntity(barcodes, "barcode", MediaType.APPLICATION_JSON_TYPE);
        return uuidService.getUUIDsForBarcodes(barcodeList);
    }
    
    /**
     * Process the given barcode. If it ends with the wildcard, strip the wildcard first and
     * return all <code>UuidBarcodeMapping</code> which barcodes starts with the resulting barcode prefix.
     *
     * @param barcode   the barcode, with a wilcard or not
     * @param mediaType the {@link Response} media type if an error occurs
     * @return a list of <code>UuidBarcodeMapping</code> which barcode either match exactly the given barcode if no wildcard is used
     *         or which starts by the given barcode if a wildcard is used
     */
    private List<UuidBarcodeMapping> processBarcode(String barcode,
                                                    final String mediaType) {

        final String wildcard = "*";
        final boolean usingWildcard = barcode.endsWith(wildcard);

        if (usingWildcard) {
            barcode = barcode.substring(0, barcode.length() - 1);
            // Don't validate barcode, since incomplete search should be allowed (eg: TCGA*)
            return getBarcodeMapping(barcode, usingWildcard, mediaType);

        } else if (commonBarcodeAndUUIDValidator.validateAnyBarcodeFormat(barcode.toUpperCase())) { // Do case insensitive validation
            return getBarcodeMapping(barcode, usingWildcard, mediaType);

        } else {
            final int statusCode = HttpStatusCode.OK;
            final String errorMessage = "barcode " + barcode + " is not valid";
            final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, barcode, errorMessage);
            throw new WebApplicationException(response);
        }
    }

    /**
     * Return a list of <code>UuidBarcodeMapping</code> which barcode either match exactly the given barcode if no wildcard is used
     * or which start by the given barcode if a wildcard is used
     *
     * @param barcodeAsString the barcode, as string
     * @param usingWildcard   if <code>true</code> then it will return all <code>UuidBarcodeMapping</code> which barcode starts by the given barcode,
     *                        otherwise it will return only the <code>UuidBarcodeMapping</code> that have an exact barcode match
     * @param mediaType       the {@link Response} media type if an error occurs
     * @return a list of <code>UuidBarcodeMapping</code> which barcode either match exactly the given barcode if no wildcard is used
     *         or which start by the given barcode if a wildcard is used
     */
    private List<UuidBarcodeMapping> getBarcodeMapping(final String barcodeAsString,
                                                       final boolean usingWildcard,
                                                       final String mediaType) {

        final List<UuidBarcodeMapping> result = new ArrayList<UuidBarcodeMapping>();

        if (usingWildcard) { // Looking for all uuids that starts with the given barcode

            final List<Barcode> barcodes = uuidService.getBarcodesStartingWith(barcodeAsString);
            if (barcodes.size() > 0) {
                for (final Barcode barcode : barcodes) {
                    final UuidBarcodeMapping mapping = makeUuidBarcodeMapping(barcode.getBarcode(), barcode.getUuid());
                    result.add(mapping);
                }
            } else {
                final int statusCode = HttpStatusCode.OK;
                final String errorMessage = "No barcode found with prefix " + barcodeAsString;
                final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, barcodeAsString, errorMessage);
                throw new WebApplicationException(response);
            }

        } else { // Looking for an exact match

            final String uuid = uuidService.getUUIDForBarcode(barcodeAsString);
            if (uuid != null) {

                final UuidBarcodeMapping mapping = makeUuidBarcodeMapping(barcodeAsString, uuid);
                result.add(mapping);

            } else {
                final int statusCode = HttpStatusCode.OK;
                final String errorMessage = "barcode " + barcodeAsString + " not found";
                final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, barcodeAsString, errorMessage);
                throw new WebApplicationException(response);
            }
        }

        return result;
    }

    /**
     * Return a <code>UuidBarcodeMapping</code> made of the given barcode and uuid (other attributes are left null)
     *
     * @param barcode the barcode
     * @param uuid    the uuid
     * @return a <code>UuidBarcodeMapping</code> made of the given barcode and uuid (other attributes are left null)
     */
    private UuidBarcodeMapping makeUuidBarcodeMapping(final String barcode, final String uuid) {

        final UuidBarcodeMapping uuidBarcodeMapping = new UuidBarcodeMapping();
        uuidBarcodeMapping.setBarcode(barcode);
        uuidBarcodeMapping.setUuid(uuid);
        return uuidBarcodeMapping;
    }

    @GET
    @Path("/xml/uuid/{uuid:.+}")
    @Produces(MediaType.APPLICATION_XML)
    public UuidBarcodeMapping getUuidMappingXML(@PathParam("uuid") final String uuid) {
        return processUuid(uuid, MediaType.APPLICATION_XML);
    }
    
    @POST
    @Path("/xml/uuid/batch")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_XML)
    public List<UuidBarcodeMapping> getLatestBarcodesForUUIDsXML(final String uuids) {
    	final List<String> uuidList = validateMappingResourceEntity(uuids, "uuid", MediaType.APPLICATION_XML_TYPE);
        return uuidService.getLatestBarcodesForUUIDs(uuidList);
    }
    
    @GET
    @Path("/json/uuid/{uuid:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public UuidBarcodeMapping getUuidMappingJSON(@PathParam("uuid") final String uuid) {
        return processUuid(uuid, MediaType.APPLICATION_JSON);
    }
    
    @POST
    @Path("/json/uuid/batch")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<UuidBarcodeMapping> getLatestBarcodesForUUIDsJSON(final String uuids) {
    	final List<String> uuidList = validateMappingResourceEntity(uuids, "uuid", MediaType.APPLICATION_JSON_TYPE);
        return uuidService.getLatestBarcodesForUUIDs(uuidList);
    }
    
    @GET
    @Path("/xml/center/{center:.+}")
    @Produces(MediaType.APPLICATION_XML)
    public List<UuidBarcodeMapping> getUuidMappingsForCenterXml(@PathParam("center") final String centerName) {
        return getUuidMappingsForCenter(centerName, MediaType.APPLICATION_XML);
    }

    @GET
    @Path("/json/center/{center:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UuidBarcodeMapping> getUuidMappingsForCenterJson(@PathParam("center") final String centerName) {
        return getUuidMappingsForCenter(centerName, MediaType.APPLICATION_JSON);
    }

    /**
     * Return a list of UUID to Barcode mappings for the given center or an error response in the given media type
     * if the center is not a valid BCR center name.
     *
     * @param centerName the center name
     * @param mediaType  the {@link Response} media type if an error occurs
     * @return a list of UUID to Barcode mappings for the given center
     */
    private List<UuidBarcodeMapping> getUuidMappingsForCenter(final String centerName, String mediaType) {
        final Center bcrCenter = uuidService.getCenterByNameAndType(centerName, "BCR");
        if (bcrCenter == null) {
            final int statusCode = HttpStatusCode.OK;
            final String errorMessage = centerName + " is not a valid BCR center name.";
            final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, centerName, errorMessage);
            throw new WebApplicationException(response);

        } else {
            final SearchCriteria searchCriteria = new SearchCriteria();
            searchCriteria.setCenterId(bcrCenter.getCenterId());
            final List<UUIDDetail> searchResults = uuidService.searchUUIDs(searchCriteria);
            final List<UuidBarcodeMapping> mappings = new ArrayList<UuidBarcodeMapping>();
            for (final UUIDDetail uuidDetail : searchResults) {
                final UuidBarcodeMapping uuidBarcodeMapping = new UuidBarcodeMapping();
                uuidBarcodeMapping.setUuid(uuidDetail.getUuid());
                uuidBarcodeMapping.setBarcode(uuidDetail.getLatestBarcode());
                mappings.add(uuidBarcodeMapping);
            }
            return mappings;
        }
    }

    /**
     * Return the UUID to Barcode mapping for the given UUID or an error response in the given media type
     * if the UUID does not have the right format.
     *
     * @param uuid      the UUID
     * @param mediaType the {@link Response} media type if an error occurs
     * @return the UUID to Barcode mapping for the given UUID
     */
    private UuidBarcodeMapping processUuid(final String uuid, final String mediaType) {

        if (commonBarcodeAndUUIDValidator.validateUUIDFormat(uuid)) {
            return getUuidMapping(uuid, mediaType);
        } else {
            final int statusCode = HttpStatusCode.OK;
            final String errorMessage = "uuid " + uuid + " is not valid";
            final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, uuid, errorMessage);
            throw new WebApplicationException(response);
        }
    }

    /**
     * Return the UUID to Barcode mapping for the given UUID or an error response in the given media type
     * if the UUID does exist in the system.
     *
     * @param uuid      the UUID
     * @param mediaType the {@link Response} media type if an error occurs
     * @return the UUID to Barcode mapping for the given UUID
     */
    private UuidBarcodeMapping getUuidMapping(final String uuid, final String mediaType) {

        final String barcode = uuidService.getLatestBarcodeForUUID(uuid);
        final UuidBarcodeMapping mapping = new UuidBarcodeMapping();
        mapping.setUuid(uuid);
        mapping.setBarcode(barcode);
        if (barcode == null) {
            // is this a valid UUID with no barcode, or a UUID that we don't know about?
            try {
                uuidService.getUUIDDetails(uuid);
                // if no exception thrown, then it is valid, just not associated with a barcode, so mark barcode empty
                mapping.setBarcode("");
            } catch (UUIDException e) {
                // this means the UUID doesn't exist in our system
                final int statusCode = HttpStatusCode.OK;
                final String errorMessage = "no such UUID exists in the system";
                final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, uuid, errorMessage);
                throw new WebApplicationException(response);
            }
        }

        return mapping;
    }

    /**
     * Fulfills the same function as the {@link MappingWebService#validateMappingResourceEntity(String, String, MediaType, String)}
     * and uses the {@link DEFAULT_MAPPING_RESOURCE_DELIMITER} as the delimiter for parsing resource entity values.
     * 
     * @param mappingRequestEntity - the request entity to validate
     * @param mappingElementType - the element type to validate (i.e. uuid) for bean validation
     * @param mediaType - the {@link MediaType} corresponding to the request media type
     * @return a list of strings that represent the values parsed from the resource entity
     */
    private List<String> validateMappingResourceEntity(
    		final String mappingRequestEntity, final String mappingElementType, final MediaType mediaType) {
    	return validateMappingResourceEntity(mappingRequestEntity, mappingElementType, mediaType, DEFAULT_MAPPING_RESOURCE_DELIMITER);
    }
    
    /**
     * Validates the entity (i.e. body content) of a request, typically a <tt>POST</tt> request, for UUID mapping resources.
     * The body of the entity must be a plain text delimited string. The entity values are parsed using the value specified
     * by the <tt>delimiter</tt> parameter.
     * <p>
     * The <tt>mappingElementType</tt> should correspond to one if the field names of the {@link UuidBarcodeMapping} bean so
     * that bean validation can be performed.
     * 
     * @param mappingRequestEntity - the request entity to validate
     * @param mappingElementType - the element type to validate (i.e. uuid) for bean validation
     * @param mediaType - the {@link MediaType} corresponding to the request media type
     * @param delimiter - the delimiter used to parse the entity resource values
     * @return a list of strings that represent the values parsed from the resource entity
     */
    private List<String> validateMappingResourceEntity(
    		final String mappingRequestEntity, final String mappingElementType, final MediaType mediaType, final String delimiter) {
    	
    	if(mappingRequestEntity == null || mappingRequestEntity.isEmpty()) {
    		final String errorMessage = "Could not process the request because the request body was empty";
    		final Response errorResponse = 
    				Response.status(HttpStatus.SC_UNPROCESSABLE_ENTITY)
    				.entity(errorMessage)
                    .type(mediaType)
                    .build();
    		
    		throw new WebApplicationException(errorResponse);
    	}
    	
    	final List<String> mappingElements = Arrays.asList(mappingRequestEntity.split(delimiter));
    	final List<UuidBarcodeMapping> uuidBarcodeMappings = new ArrayList<UuidBarcodeMapping>();
    	try {
        	final PropertyDescriptor propertyDescriptor = new PropertyDescriptor(mappingElementType, UuidBarcodeMapping.class);
        	UuidBarcodeMapping uuidBarcodeMapping = null;
    		for(final String mappingElement : mappingElements) {
    			uuidBarcodeMapping = new UuidBarcodeMapping();
    			propertyDescriptor.getWriteMethod().invoke(uuidBarcodeMapping, mappingElement);
    			uuidBarcodeMappings.add(uuidBarcodeMapping);
    		}
    	}
    	catch(Exception e) {
    		throw new WebApplicationException(e);
    	}
    	
		for(UuidBarcodeMapping uuidBarcodeMapping : uuidBarcodeMappings) {
			uuidWebServiceUtil.validate(uuidBarcodeMapping, mappingElementType, mediaType.toString());
		}
    	
    	return mappingElements;
    }
    
    public void setUuidService(final UUIDService uuidService) {
        this.uuidService = uuidService;
    }

}
