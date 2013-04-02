/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.webservice;

import com.sun.jersey.api.core.InjectParam;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
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

/**
 * This class provides several web services for retrieving Annotations related information in JSON or XML format
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Path("/viewannotation")
public class AnnotationViewWS {

    private final Log log = LogFactory.getLog(getClass());

    @InjectParam
    private AnnotationService annotationService;

    public AnnotationService getAnnotationService() {
        return annotationService;
    }

    public void setAnnotationService(final AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    /**
     * This web service returns the DccAnnotation with the given Id in XML format
     *
     * @param dccAnnotationId the Id of the DccAnnotation to retrieve
     * @return the DccAnnotation in XML format
     * @throws WebApplicationException
     */
    @GET
    @Path("/xml{dccAnnotationId:(/[a-zA-Z0-9]*)?}") //We only expect digits but we should be more forgiving
    // to give feedback to the user if anything else is entered
    @Produces(MediaType.APPLICATION_XML)
    public DccAnnotation getDccAnnotationToXml(@PathParam("dccAnnotationId") final String dccAnnotationId) {
        return getDccAnnotation(parseDccAnnotationId(dccAnnotationId));
    }

    /**
     * This web service returns the DccAnnotation with the given Id in JSON format
     *
     * @param dccAnnotationId the Id of the DccAnnotation to retrieve
     * @return the DccAnnotation in JSON format
     * @throws WebApplicationException
     */
    @GET
    @Path("/json{dccAnnotationId:(/[a-zA-Z0-9]*)?}") //We only expect digits but we should be more forgiving
    // to give feedback to the user if anything else is entered
    @Produces(MediaType.APPLICATION_JSON)
    public DccAnnotation getDccAnnotationToJson(@PathParam("dccAnnotationId") final String dccAnnotationId) {
        return getDccAnnotation(parseDccAnnotationId(dccAnnotationId));
    }

    /**
     * Returns a DccAnnotation given its Id.
     *
     * @param dccAnnotationId the Id of the DccAnnotation to retrieve
     * @return the DccAnnotation in JSON format
     * @throws WebApplicationException if the DccAnnotation can not be retrieved
     */
    private DccAnnotation getDccAnnotation(final long dccAnnotationId) {

        try {
            return getAnnotationService().getAnnotationById(dccAnnotationId);
        } catch (final AnnotationQueries.AnnotationQueriesException e) {

            final String exceptionMessage = "Could not retrieve DccAnnotation with id " + dccAnnotationId;
            log.error(exceptionMessage + " - " + e.getMessage());

            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
        } catch (final BeanException be) {
            final String exceptionMessage = "Could not retrieve DccAnnotation with id " + dccAnnotationId;
            log.error(exceptionMessage + " - " + be.getMessage());

            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
        }
    }

    /**
     * Parse a DccAnnotation Id String and return an int if valid, or throw a WebApplicationException otherwise.
     * It is expected that the input String starts with the character '/'
     *
     * @param dccAnnotationId as a String, starting with the character '/'
     * @return the dccAnnotationId as an int
     */
    private long parseDccAnnotationId(final String dccAnnotationId) {

        if (dccAnnotationId != null && dccAnnotationId.length() > 0) {

            //The id input is expected to start with the character '/'
            final String dccAnnotationIdString = dccAnnotationId.substring(1);

            try {
                return Long.parseLong(dccAnnotationIdString);

            } catch (NumberFormatException e) {

                final String exceptionMessage = "Please provide a valid DccAnnotation id in the URL. "
                        + (dccAnnotationIdString != null ? "Invalid value found was:" + dccAnnotationIdString : "No DccAnnotation id was provided");
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
            }

        } else {

            final String exceptionMessage = "Please provide a DccAnnotation id in the URL.";
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
        }
    }
}
