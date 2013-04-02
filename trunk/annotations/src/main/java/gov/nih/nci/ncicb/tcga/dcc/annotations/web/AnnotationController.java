/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.web;

import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import org.springframework.ui.ExtendedModelMap;

import javax.servlet.http.HttpSession;

/**
 * Service interface for annotations.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */

public interface AnnotationController {

    /**
     * Handles requests to add an annotation that are sent using POST. The model returned has a boolean mapped to
     * "success" indicating whether the add call succeeded or not.  If success is true, the model will have
     * the new annotation object mapped to "annotation", the annotation ID mapped to "annotationId". If success is false
     * the error message will be in the model under the key "errorMessage".
     * <p><tt>rescind</tt> takes precedence over edit/add. If <tt>rescind</tt> is set to "true", then the annotation will be rescinded.
     * Only if <tt>rescind</tt> is "false", will the situation of whether to edit/add be considered</p>
     *
     * @param session the HttpSession for this request
     * @param model the model
     * @param annotationId -1 for addNewAnnotation, o.w. updateAnnotation
     * @param diseaseId taken from "diseaseId" parameter
     * @param itemTypeId taken from "itemTypeId" parameter
     * @param item taken from "item" parameter
     * @param status taken from "editstatus" parameter
     * @param annotationCategoryId taken from "annotationTypeId" parameter
     * @param annotationNote taken from "note" parameter
     * @param useStrictItemValidation whether to validate the annotation's item against the database values or not
     * @param rescinded whether to rescind the annotation.
     * @return a model contain either the new annotation or an error message.
     */
    public ExtendedModelMap addAnnotationHandler(
            final HttpSession session,
            final ExtendedModelMap model,
            final Long annotationId,
            final Integer diseaseId,
            final Long itemTypeId,
            final String item,
            final String status,
            final Long annotationCategoryId,
            final String annotationNote,
            final Boolean useStrictItemValidation,
            final String rescinded);

    /**
     * Handles requests to get the allowed item types for annotations.  The model will have the list of item types
     * mapped to "itemTypes". If there was an error,
     * the model will contain the error message mapped to "errorMessage".
     *
     * @param model the model
     * @return the model, containing the "itemTypes" or "errorMessage"
     */
    public ExtendedModelMap getItemTypes(final ExtendedModelMap model);

    /**
     * Handles requests to get allowed annotation categories. The model will contain a List DccAnnotationCatgory beans
     * with the key "annotationCategories".  If there was an error, the model will map the error message to "errorMessage".
     *
     * @param model the model
     * @return the model with "annotationCategories" or "errorMessage"
     */
    public ExtendedModelMap getAnnotationCategories(final ExtendedModelMap model);

    /**
     * Handles request to get all known annotation classifications.
     *
     * @param model the model
     * @return the model with "annotationClassifications" or "errorMessage"
     */
    public ExtendedModelMap getAnnotationClassifications(final ExtendedModelMap model);

    /**
     * Handles requests to get active diseases.  The model will either contain a List of Tumor beans mapped to "diseases",
     * or if there was an error getting the list, the model will map the error
     * message to "errorMessage".
     *
     * @param modelMap the model
     * @return the model with "diseases" or "errorMessage"
     */
    public ExtendedModelMap getActiveDiseases(final ExtendedModelMap modelMap);

    /**
     * Handles request for annotation detail for given ID.  The returned model will contain the annotation object
     * with the key "annotation".  If the annotation was not found, there will be an error message mapped to the key
     * "errorMessage".
     *
     * @param model the model
     * @param annotationId the ID of the annotation to get
     * @return the model with "annotation" or "errorMessage"
     */
    public ExtendedModelMap getAnnotationById(final ExtendedModelMap model, final Long annotationId);

    /**
     * Sets the annotation service to be used by this controller.
     *
     * @param annotationService the annotation service
     */
    public void setAnnotationService(final AnnotationService annotationService);

    /**
     *  Handles requests to add a new note to an annotation.
     *
     * @param session the session
     * @param model the model
     * @param annotationId the ID of the annotation to add the note to
     * @param noteText the text for the new note
     * @return model with "note" and "noteId" or "errorMessage"
     */
    public ExtendedModelMap addNoteToAnnotation(
            final HttpSession session,
            final ExtendedModelMap model,
            final Long annotationId,
            final String noteText);

    /**
     * Handles requests for editing an annotation note's text.
     *
     * @param session the session
     * @param model the model
     * @param noteId the ID of the note to edit
     * @param newNoteText the new text for the note
     * @return the model with "note" or "errorMessage"
     */
    public ExtendedModelMap editAnnotationNote(
            final HttpSession session,
            final ExtendedModelMap model,
            final Long noteId,
            final String newNoteText);

    /**
     *  Handles requests to search annotations.
     *
     * @param model the model
     * @param session the Http session object
     * @param diseaseId the disease id
     * @param item the item name to search on
     * @param classificationId classification id to search on
     * @param categoryId the category id to search on
     * @param itemTypeId the item type id
     * @param keyword the note keyword
     * @param exact if the item match should be exact
     * @param status status of the annotation
     * @param includeRescinded  if set to <code>false</code>, the default, returns only unrescinded annotations, otherwise returns rescinded and unrescinded annotations
     * @param annotationIdString the Id of the annotation to return, if not null, ignoring other filters
     * @param annotatorUsername the username of the user who created the annotation
     * @return model with "annotations" or "errorMessage"
     */
    public ExtendedModelMap searchAnnotations(
            final HttpSession session,
            final ExtendedModelMap model,            
            final Integer diseaseId,
            final String item,
            final Long classificationId,
            final Long categoryId,
            final Long itemTypeId,
            final String keyword,
            Boolean exact,
            final String status,
            final Boolean includeRescinded,
            final String annotationIdString,
            final String annotatorUsername);
}
