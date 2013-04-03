/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import com.sun.jersey.api.core.InjectParam;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.JSON;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.XML;

/**
 * REST Web service for generating UUIDs
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Path("/generateuuid")
public class GenerateUUIDWebService {

    @QueryParam("numberOfUUIDs")
    private int numberOfUUIDs;
    @QueryParam("centerName")
    private String centerName;
    @QueryParam("centerType")
    private String centerType;

    @InjectParam
    private UUIDService service;

    @InjectParam
    private UUIDWebServiceUtil webServiceUtil;

    private final Log log = LogFactory.getLog(getClass());

    protected GenerateUUIDWebService(int numberOfUUIDs, final String centerName, final String centerType) {
        this.numberOfUUIDs = numberOfUUIDs;
        this.centerName = centerName;
        this.centerType = centerType;
    }

    public GenerateUUIDWebService() {
    }


    public void setUuidService(final UUIDService uuidService) {
        this.service = uuidService;
    }

    /**
     * REST web service for getting UUID list in JSON format
     *
     * @return List of UUID details
     */
    @GET
    @Path(JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<UUIDDetail> generateUUIDToJSON() {
        return generateUUIDs(MediaType.APPLICATION_JSON);
    }

    /**
     * REST web service for getting UUID list in XML format
     *
     * @return List of UUID Details
     */
    @GET
    @Path(XML)
    @Produces(MediaType.APPLICATION_XML)
    public List<UUIDDetail> generateUUIDToXML() {
        return generateUUIDs(MediaType.APPLICATION_XML);
    }

    private List<UUIDDetail> generateUUIDs(final String mediaType) {
        int centerId = webServiceUtil.getCenterId(centerName, centerType, mediaType);
        if (0 == centerId) {
            final int statusCode = HttpStatusCode.OK;
            final String invalidValue = "Missing center name and/or center type.";
            final String errorMessage = "Both center name and center type should be specified.";
            final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, invalidValue, errorMessage);
            throw new WebApplicationException(response);
        }
        List<UUIDDetail> uuidList;
        try {
            uuidList = service.generateUUID(centerId, numberOfUUIDs, UUIDConstants.GenerationMethod.Rest, UUIDConstants.MASTER_USER);
        } catch (UUIDException exception) {
            final String exceptionMessage = "Error while generating UUIDs";
            log.error(exceptionMessage + " - " + exception.getMessage());
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exception.getMessage()));
        } catch (RuntimeException re) {
            //In case a runtime exception ever occurs for any reason, a proper http handling is used
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, re.getMessage()));
        }
        return uuidList;
    }

    protected void setWebServiceUtil(final UUIDWebServiceUtil webServiceUtil) {
        this.webServiceUtil = webServiceUtil;
    }
}
