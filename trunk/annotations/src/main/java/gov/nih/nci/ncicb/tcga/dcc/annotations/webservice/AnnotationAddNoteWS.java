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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * This class provides several web services for adding <code>DccAnnotationNote</code>s to <code>DccAnnotation</code>s
 * in JSON or XML format
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Path("/addannotationnote")
public class AnnotationAddNoteWS {

    private final Log log = LogFactory.getLog(getClass());

    @InjectParam
    private AnnotationService annotationService;

    @InjectParam("securityUtil")
    private SecurityUtil securityUtil;

    /**
     * Add a new <code>DccAnnotationNote</code> to the <code>DccAnnotation</code> with the given Id
     * and return it in XML format
     *
     * @param dccAnnotationIdAsString the <code>DccAnnotation</code> Id, as String
     * @param noteTxt                 the note text to add
     * @return the new <code>DccAnnotationNote</code> in XML format
     */
    @GET
    @Path("/xml/")
    @Produces(MediaType.APPLICATION_XML)
    public DccAnnotationNote addDccAnnotationNoteToXml(
            @QueryParam("dccAnnotationId") final String dccAnnotationIdAsString,
            @QueryParam("noteTxt") final String noteTxt) {

        return addDccAnnotationNote(dccAnnotationIdAsString, noteTxt);
    }

    /**
     * Add a new <code>DccAnnotationNote</code> to the <code>DccAnnotation</code> with the given Id
     * and return it in JSON format
     *
     * @param dccAnnotationIdAsString the <code>DccAnnotation</code> Id, as String
     * @param noteTxt                 the note text to add
     * @return the new <code>DccAnnotationNote</code> in JSON format
     */
    @GET
    @Path("/json/")
    @Produces(MediaType.APPLICATION_JSON)
    public DccAnnotationNote addDccAnnotationNoteToJson(
            @QueryParam("dccAnnotationId") final String dccAnnotationIdAsString,
            @QueryParam("noteTxt") final String noteTxt) {

        return addDccAnnotationNote(dccAnnotationIdAsString, noteTxt);
    }

    /**
     * Add a <code>DccAnnotationNote</code> with the give text to the <code>DccAnnotation</code> with the given Id
     *
     * @param dccAnnotationIdAsString the <code>DccAnnotation</code> Id, as String
     * @param noteTxt                 the text for the <code>DccAnnotationNote</code> to add
     * @return the added <code>DccAnnotationNote</code>
     */
    private DccAnnotationNote addDccAnnotationNote(final String dccAnnotationIdAsString, final String noteTxt) {

        DccAnnotationNote result = null;

        final long dccAnnotationId = validate(dccAnnotationIdAsString);

        if (dccAnnotationIdExist(dccAnnotationId)) {

            try {
                // Note: the annotation mote text is validated at the DAO level

                // Note: if the web service user is not authenticated at that point, that is not a problem because as soon as the protected resource is called
                // (at the DAO level), Spring will display a login page and re-call the web service after the user authenticated
                // at which point the 'addedBy' field will be properly set
                final String addedBy = getSecurityUtil().getAuthenticatedPrincipalLoginName();
                final Date dateAdded = new Date();
                result = getAnnotationService().addNewAnnotationNote(dccAnnotationId, noteTxt, addedBy, dateAdded);

            } catch (final AnnotationQueries.AnnotationQueriesException e) {

                final String errorMessage = new StringBuilder("Error while adding note to annotation with id '")
                        .append(dccAnnotationId)
                        .append("': ")
                        .append(e.getMessage())
                        .toString();

                WebServiceUtil.logAndThrowWebApplicationException(log, errorMessage, HttpStatusCode.INTERNAL_SERVER_ERROR);
            } catch (final BeanException be) {

                final String errorMessage = new StringBuilder("Error while adding note to annotation with id '")
                        .append(dccAnnotationId)
                        .append("': ")
                        .append(be.getMessage())
                        .toString();

                WebServiceUtil.logAndThrowWebApplicationException(log, errorMessage, HttpStatusCode.INTERNAL_SERVER_ERROR);
            }
        }

        return result;
    }

    /**
     * Return the <code>Long</code> value of the <code>DccAnnotation</code> Id, provided as a String
     *
     * @param dccAnnotationIdAsString the <code>DccAnnotation</code> Id, provided as a String
     * @return the <code>Long</code> value of the <code>DccAnnotation</code> Id, provided as a String
     */
    private Long validate(final String dccAnnotationIdAsString) {

        Long result = null;

        if (StringUtils.isBlank(dccAnnotationIdAsString)) {
            WebServiceUtil.logAndThrowWebApplicationException(log, "Please provide an annotation Id.", HttpStatusCode.PRECONDITION_FAILED);
        }

        try {
            result = Long.parseLong(dccAnnotationIdAsString);

        } catch (final NumberFormatException e) {

            final String errorMessage = new StringBuilder("Please provide a valid annotation Id: '")
                    .append(dccAnnotationIdAsString)
                    .append("' is not a number.")
                    .toString();

            WebServiceUtil.logAndThrowWebApplicationException(log, errorMessage, HttpStatusCode.PRECONDITION_FAILED);
        }

        return result;
    }

    /**
     * Return <code>true<code> if the given <code>DccAnnotation</code> Id exists, <code>false<code> otherwise.
     *
     * @param dccAnnotationId the <code>DccAnnotation</code> Id
     * @return <code>true<code> if the given <code>DccAnnotation</code> Id exists, <code>false<code> otherwise
     */
    private boolean dccAnnotationIdExist(final long dccAnnotationId) {

        boolean result = false;

        try {
            final DccAnnotation dccAnnotation = getAnnotationService().getAnnotationById(dccAnnotationId);
            result = dccAnnotation != null;

        } catch (final AnnotationQueries.AnnotationQueriesException e) {

            final String errorMessage = new StringBuilder("Error while retrieving annotation with id '")
                    .append(dccAnnotationId)
                    .append("': ")
                    .append(e.getMessage())
                    .toString();

            WebServiceUtil.logAndThrowWebApplicationException(log, errorMessage, HttpStatusCode.PRECONDITION_FAILED);
        } catch (final BeanException be) {

            final String errorMessage = new StringBuilder("Error while retrieving annotation with id '")
                    .append(dccAnnotationId)
                    .append("': ")
                    .append(be.getMessage())
                    .toString();

            WebServiceUtil.logAndThrowWebApplicationException(log, errorMessage, HttpStatusCode.PRECONDITION_FAILED);
        }

        return result;
    }

    /*
     * Getter / Setter
     */

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
}
