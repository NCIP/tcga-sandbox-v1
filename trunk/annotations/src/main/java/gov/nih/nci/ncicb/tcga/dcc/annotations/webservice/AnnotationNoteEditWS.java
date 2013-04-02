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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
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

/**
 * This class provides several web services for editing DccAnnotationNotes in JSON or XML format
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Path("/editannotation")
public class AnnotationNoteEditWS {

    private final Log log = LogFactory.getLog(getClass());

    @InjectParam
    private AnnotationService annotationService;

    @InjectParam("securityUtil")
    private SecurityUtil securityUtil;

    public AnnotationService getAnnotationService() {
        return annotationService;
    }

    public void setAnnotationService(final AnnotationService annotationService) {
        this.annotationService = annotationService;
    }


    public SecurityUtil getSecurityUtil() {
        return securityUtil;
    }

    public void setSecurityUtil(final SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    /**
     * Edit the DccAnnotationNote with the given DccAnnotationNote Id
     * and return it in XML format
     *
     * @param dccAnnotationNoteId the Id of the DccAnnotationNote to be edited
     * @param newNoteTxt          the new note text
     * @return the edited DccAnnotationNote in XML format
     */
    @GET
    @Path("/xml/")
    @Produces(MediaType.APPLICATION_XML)
    public DccAnnotationNote editDccAnnotationNoteToXml(
            @QueryParam("dccAnnotationNoteId") final long dccAnnotationNoteId,
            @QueryParam("newNoteTxt") final String newNoteTxt
    ) {

        editDccAnnotationNote(dccAnnotationNoteId, newNoteTxt);

        return getDccAnnotationNoteById(dccAnnotationNoteId);
    }

    /**
     * Edit the DccAnnotationNote with the given DccAnnotationNote Id
     * and return it in JSON format
     *
     * @param dccAnnotationNoteId the Id of the DccAnnotationNote to be edited
     * @param newNoteTxt          the new note text
     * @return the edited DccAnnotationNote in JSON format
     */
    @GET
    @Path("/json/")
    @Produces(MediaType.APPLICATION_JSON)
    public DccAnnotationNote editDccAnnotationNoteToJson(
            @QueryParam("dccAnnotationNoteId") final long dccAnnotationNoteId,
            @QueryParam("newNoteTxt") final String newNoteTxt
    ) {

        editDccAnnotationNote(dccAnnotationNoteId, newNoteTxt);

        return getDccAnnotationNoteById(dccAnnotationNoteId);
    }

    /**
     * Return the DccAnnotationNote with the given Id
     *
     * @param dccAnnotationNoteId the DccAnnotationNote Id
     * @return the DccAnnotationNote with the given Id
     */
    private DccAnnotationNote getDccAnnotationNoteById(final long dccAnnotationNoteId) {

        DccAnnotationNote dccAnnotationNote;

        try {
            dccAnnotationNote = getAnnotationService().getAnnotationNoteById(dccAnnotationNoteId);

        } catch (AnnotationQueries.AnnotationQueriesException e) {

            final String exceptionMessage = "Could not retrieve DccAnnotationNote with id " + dccAnnotationNoteId;
            log.error(exceptionMessage + " - " + e.getMessage());

            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
        }

        return dccAnnotationNote;
    }

    /**
     * Edit the DccAnnotationNote with the given Id
     *
     * @param dccAnnotationNoteId the Id of the DccAnnotationNote to edit
     * @param newNoteTxt          the new note text
     */
    private void editDccAnnotationNote(final long dccAnnotationNoteId, final String newNoteTxt) {

        if (newNoteTxt == null) {

            final String exceptionMessage = "Editing of DccAnnotationNote with undefined new note text is unsupported";
            log.error(exceptionMessage);

            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
        }

        try {
            getAnnotationService().editAnnotationNote(dccAnnotationNoteId, newNoteTxt, getSecurityUtil().getAuthenticatedPrincipalLoginName());

        } catch (AnnotationQueries.AnnotationQueriesException e) {

            final String exceptionMessage = "Could not edit DccAnnotationNote with id " + dccAnnotationNoteId;
            log.error(exceptionMessage + " - " + e.getMessage());

            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
        } catch (final BeanException be) {

            final String exceptionMessage = "Could not edit DccAnnotationNote with id " + dccAnnotationNoteId;
            log.error(exceptionMessage + " - " + be.getMessage());

            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
        }
    }
}
