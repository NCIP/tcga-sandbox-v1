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
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * REST web service for getting UUID Details
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Path("/uuiddetail")
public class UUIDDetailWebService {

    private final Log log = LogFactory.getLog(getClass());

    @InjectParam
    private UUIDService service;

    public void setUuidService(final UUIDService uuidService) {
        this.service = uuidService;
    }

    /**
     * REST web service for getting UUID details in JSON format
     *
     * @param uuid uuid to retrieve
     * @return UUID details
     */
    @GET
    @Path("/json/{uuid:[a-fA-F0-9-]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public UUIDDetail getUUIDDetailToJSON(@PathParam("uuid") final String uuid) {
        return getUUIDDetail(uuid, MediaType.APPLICATION_JSON);
    }

    /**
     * REST web service for getting UUID details in XML format
     *
     * @param uuid uuid to retrieve
     * @return UUID details
     */
    @GET
    @Path("/xml/{uuid:[a-fA-F0-9-]+}")
    @Produces(MediaType.APPLICATION_XML)
    public UUIDDetail getUUIDDetailToXML(@PathParam("uuid") final String uuid) {
        return getUUIDDetail(uuid, MediaType.APPLICATION_XML);
    }

    /**
     * Return the detail for the given UUID or an error response in the given media type if the UUID can not be retrieved.
     *
     * @param uuid      the UUID
     * @param mediaType the {@link Response} media type if an error occurs
     * @return the detail for the given UUID
     */
    private UUIDDetail getUUIDDetail(final String uuid,
                                     final String mediaType) {

        try {
            return service.getUUIDDetails(uuid);

        } catch (final UUIDException exception) {
            final int statusCode = HttpStatusCode.OK;
            final String errorMessage = "Could not retrieve UUID : " + uuid;
            log.error(errorMessage + " - " + exception.getMessage());
            final Response response = WebServiceUtil.makeResponse(mediaType, statusCode, uuid, errorMessage);
            throw new WebApplicationException(response);
        }
    }

}
