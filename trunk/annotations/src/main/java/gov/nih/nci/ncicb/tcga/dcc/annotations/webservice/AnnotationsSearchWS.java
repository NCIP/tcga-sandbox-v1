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
import com.sun.jersey.api.json.JSONWithPadding;
import gov.nih.nci.ncicb.tcga.dcc.annotations.web.AnnotationControllerImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * This class provides several web services for searching Annotations in JSON or XML format
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Path("/searchannotations")
public class AnnotationsSearchWS {

    @InjectParam
    private AnnotationService annotationService;

    @InjectParam
    private TumorQueries tumorQueries;

    public AnnotationService getAnnotationService() {
        return annotationService;
    }

    public void setAnnotationService(final AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    /**
     * Returns a list of DccAnnotation, based on search criteria, in XML format
     *
     * @param disease           search criteria: matching the disease
     * @param item              search criteria: matching an item
     * @param classificationId  search criteria: matching classification id
     * @param categoryId        search criteria: matching the category Id
     * @param itemTypeId        search criteria: matching the item type Id
     * @param keyword           search criteria: matching a note
     * @param itemExact         true to force a case sensitive search on the item, false otherwise (false by default)
     * @param rowLimit          the limit for the number of results to return (use null or any number less than 1 to return all)
     * @param annotationId      the Id of the <code>DccAnnotation</code> to include in the returned list (ignoring other filters), if not null
     * @param annotatorUsername the username of the user who created the annotation
     * @return the list of DccAnnotation
     */
    @GET
    @Path("/xml")
    @Produces(MediaType.APPLICATION_XML)
    public List<DccAnnotation> searchDccAnnotationsToXml(
            @QueryParam("disease") final String disease,
            @QueryParam("item") final String item,
            @QueryParam("classificationId") final String classificationId,
            @QueryParam("categoryId") final String categoryId,
            @QueryParam("itemTypeId") final String itemTypeId,
            @QueryParam("keyword") final String keyword,
            @QueryParam("itemExact") @DefaultValue("false") final boolean itemExact,
            @QueryParam("status") final String status,
            @QueryParam("limit") final String rowLimit,
            @QueryParam("annotationId") final String annotationId,
            @QueryParam("annotatorUsername") final String annotatorUsername) {

        return searchDccAnnotations(disease, item, classificationId, categoryId, itemTypeId, keyword, itemExact, status, rowLimit, annotationId, annotatorUsername);
    }

    /**
     * Returns a list of DccAnnotation, based on search criteria, in JSON format
     *
     * @param disease           search criteria: matching the disease
     * @param item              search criteria: matching an item
     * @param classificationId  search criteria: matching classification id
     * @param categoryId        search criteria: matching the category Id
     * @param itemTypeId        search criteria: matching the item type Id
     * @param keyword           search criteria: matching a note
     * @param itemExact         true to force a case sensitive search on the item, false otherwise (false by default)
     * @param rowLimit          the limit for the number of results to return (use null or any number less than 1 to return all)
     * @param annotationId      the Id of the <code>DccAnnotation</code> to include in the returned list (ignoring other filters), if not null
     * @param annotatorUsername search criteria: matching creator of annotator
     * @return the list of DccAnnotation
     */
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DccAnnotation> searchDccAnnotationsToJson(
            @QueryParam("disease") final String disease,
            @QueryParam("item") final String item,
            @QueryParam("classificationId") final String classificationId,
            @QueryParam("categoryId") final String categoryId,
            @QueryParam("itemTypeId") final String itemTypeId,
            @QueryParam("keyword") final String keyword,
            @QueryParam("itemExact") @DefaultValue("false") final boolean itemExact,
            @QueryParam("status") final String status,
            @QueryParam("limit") final String rowLimit,
            @QueryParam("annotationId") final String annotationId,
            @QueryParam("annotatorUsername") final String annotatorUsername) {

        return searchDccAnnotations(disease, item, classificationId, categoryId, itemTypeId, keyword, itemExact, status, rowLimit, annotationId, annotatorUsername);
    }

    /**
     * Returns a list of DccAnnotation, based on search criteria, in JSONP format
     *
     * @param disease           search criteria: matching the disease
     * @param item              search criteria: matching an item
     * @param classificationId  search criteria: matching classification id
     * @param categoryId        search criteria: matching the category Id
     * @param itemTypeId        search criteria: matching the item type Id
     * @param keyword           search criteria: matching a note
     * @param itemExact         true to force a case sensitive search on the item, false otherwise (false by default)
     * @param rowLimit          the limit for the number of results to return (use null or any number less than 1 to return all)
     * @param annotationId      the Id of the <code>DccAnnotation</code> to include in the returned list (ignoring other filters), if not null
     * @param annotatorUsername the username of the user who created the annotation
     * @param callback          the callback function
     * @return the list of DccAnnotation
     */
    @GET
    @Path("/jsonp")
    @Produces("application/x-javascript")
    public JSONWithPadding searchDccAnnotationsToJsonP(
            @QueryParam("disease") final String disease,
            @QueryParam("item") final String item,
            @QueryParam("classificationId") final String classificationId,
            @QueryParam("categoryId") final String categoryId,
            @QueryParam("itemTypeId") final String itemTypeId,
            @QueryParam("keyword") final String keyword,
            @QueryParam("itemExact") @DefaultValue("false") final boolean itemExact,
            @QueryParam("status") final String status,
            @QueryParam("limit") final String rowLimit,
            @QueryParam("annotationId") final String annotationId,
            @QueryParam("annotatorUsername") final String annotatorUsername,
            @QueryParam("callback") @DefaultValue("fn") final String callback) {
        return new JSONWithPadding(new GenericEntity<List<DccAnnotation>>
                (searchDccAnnotations(disease, item, classificationId, categoryId, itemTypeId, keyword, itemExact, status, rowLimit, annotationId, annotatorUsername)) {
        }, callback);
    }

    /**
     * Returns a list of DccAnnotation based on search criteria
     *
     * @param disease           the disease abbreviation to search
     * @param item              search criteria: matching an item
     * @param classificationId  search criteria: matching classification id
     * @param categoryId        search criteria: matching the category Id
     * @param itemTypeId        search criteria: matching the item type Id
     * @param keyword           search criteria: matching a note
     * @param itemExact         true to force a case sensitive search on the item, false otherwise
     * @param rowLimit          the limit for the number of results to return (use null or any number less than 1 to return all)
     * @param annotationId      the Id of the <code>DccAnnotation</code> to include in the returned list (ignoring other filters), if not null
     * @param annotatorUsername the username of the user who created the annotation
     * @return the list of DccAnnotation
     */
    private List<DccAnnotation> searchDccAnnotations(
            final String disease,
            final String item,
            final String classificationId,
            final String categoryId,
            final String itemTypeId,
            final String keyword,
            final boolean itemExact,
            final String status,
            final String rowLimit,
            final String annotationId,
            final String annotatorUsername) {

        Long categoryIdLong = null;
        if (categoryId != null) { // the query parameter was provided in the URL
            categoryIdLong = getLongFromString(categoryId);
        }

        Long classificationIdLong = null;
        if (classificationId != null) {
            classificationIdLong = getLongFromString(classificationId);
        }

        Long itemTypeIdLong = null;
        if (itemTypeId != null) { // the query parameter was provided in the URL
            itemTypeIdLong = getLongFromString(itemTypeId);
        }

        Integer diseaseId = null;
        if (disease != null) {
            diseaseId = tumorQueries.getTumorIdByName(disease);
            if (diseaseId == null) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.NO_CONTENT, "Disease abbreviation '" + disease + "' was not found"));
            }
        }

        Integer rowLimit_int = null;
        if (rowLimit != null) {
            rowLimit_int = getIntegerFromString(rowLimit, "limit");
        }

        try {
            final List<Long> annotationIds = AnnotationControllerImpl.getAnnotationIds(annotationId);
            return getAnnotationService().searchAnnotations(
                    diseaseId,
                    item,
                    categoryIdLong,
                    classificationIdLong,
                    itemTypeIdLong,
                    keyword,
                    itemExact,
                    status,
                    false, // return not-rescinded anotations
                    null, //todo this shouldn't be null but set to the actual authenticated (or not) username (APPS-3044)
                    rowLimit_int,
                    annotationIds,
                    annotatorUsername);

        } catch (NumberFormatException e) {
            final String exceptionMessage = "The query parameter provided ('" + annotationId + "') contains an invalid long. Please provide a valid long.";
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
        }
    }

    /**
     * Parses the given input as an Long and return the result.
     * Display a 500 error page if the input can't be converted to a Long.
     *
     * @param integerAsLong the input to be parsed as a Long
     * @return the Integer if parsed successfully
     */
    private Long getLongFromString(final String integerAsLong) {

        try {
            return Long.parseLong(integerAsLong);

        } catch (NumberFormatException e) {

            final String exceptionMessage = "The query parameter provided ('" + integerAsLong + "') is not a valid long. Please provide a valid long.";
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
        }
    }

    /**
     * Parses the given input as an Integer and return the result.
     * Display a 500 error page if the input can't be converted to an Integer with the field in error
     *
     * @param integerAsString the input to be parsed as an Integer
     * @param fieldName       for which the test is being performed
     * @return the Integer if parsed successfully
     */
    private Integer getIntegerFromString(final String integerAsString, final String fieldName) {

        try {
            return Integer.parseInt(integerAsString);

        } catch (NumberFormatException e) {

            final String exceptionMessage = "The query parameter provided ('" + integerAsString + "') for '" + fieldName + "' is not a valid integer. Please provide a valid integer.";
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage));
        }
    }

    public void setTumorQueries(final TumorQueries tumorQueries) {
        this.tumorQueries = tumorQueries;
    }
}
