/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.web;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationClassification;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Contains controller methods for annotations application.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class AnnotationControllerImpl implements AnnotationController {

    @Autowired
    private AnnotationService annotationService;

    protected static final String ERROR_MESSAGE_KEY = "errorMessage";

    /**
     * Centralized login retrieval and key/value authentication error messages
     */
    @Autowired
    private SecurityUtil util;
    
    public static final String ATTRIBUTE_LAST_SEARCH_RESULTS = "lastSearchResults";

    public SecurityUtil getUtil() {
        return util;
    }

    /**
     * @param util the security util object
     */
    public void setUtil(final SecurityUtil util) {
        this.util = util;
    }

    @Override
    @RequestMapping (value="/addAnnotation.json", method={RequestMethod.POST} )
    public ExtendedModelMap addAnnotationHandler(final HttpSession session,
                                                 final ExtendedModelMap model,
                                                 @RequestParam (value="annotationId") final Long annotationId,
                                                 @RequestParam (value="diseaseId") final Integer diseaseId,
                                                 @RequestParam (value="itemTypeId") final Long itemTypeId,
                                                 @RequestParam (value="item") final String item,
                                                 @RequestParam (value="editstatus") final String status,
                                                 @RequestParam (value="annotationCategoryId") final Long annotationCategoryId,
                                                 @RequestParam (value="note") final String annotationNote,
                                                 @RequestParam (value="useStrictItemValidation") final Boolean useStrictItemValidation,
                                                 @RequestParam (value="rescinded") final String rescinded) {
        try {
            DccAnnotation annotation;
            if(rescinded != null && "true".equals(rescinded)) {
                annotation = annotationService.rescindAnnotation(
                        annotationId,
                        getUtil().getAuthenticatedPrincipalLoginName());
            } else {
                annotation = (annotationId == -1 ?
                                                    annotationService.addAnnotation(    // add new annotation
                                                        diseaseId,
                                                        itemTypeId,
                                                        item,
                                                        annotationCategoryId,
                                                        annotationNote,
                                                        getUtil().getAuthenticatedPrincipalLoginName(),
                                                        useStrictItemValidation) :
                                                    annotationService.updateAnnotation( // update existing annotation
                                                        annotationId,
                                                        diseaseId,
                                                        itemTypeId,
                                                        item,
                                                        status,
                                                        annotationCategoryId,
                                                        getUtil().getAuthenticatedPrincipalLoginName(),
                                                        useStrictItemValidation)
                                                );
            }
            model.addAttribute("annotation", annotation);
            model.addAttribute("annotationId", annotation.getId());
            model.addAttribute("success", true); // needed for form results by ExtJs
        } catch(AuthenticationCredentialsNotFoundException e) {
            model.addAttribute(getUtil().getAuthenticationCredentialsNotFoundExceptionMessageKey(), getUtil().getAuthenticationCredentialsNotFoundExceptionMessageValue());
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
            model.addAttribute("success", false);

        } catch(AccessDeniedException e) {
            model.addAttribute(getUtil().getAccessDeniedExceptionMessageKey(), getUtil().getAccessDeniedExceptionMessageValue());
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
            model.addAttribute("success", false);

        } catch (Exception e) {
            // all exceptions are caught so the user will get the error message in a clean manner
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
            model.addAttribute("success", false);  // needed for form results by ExtJs
        }
        return model;
    }

    /**
     * Handles requests to get the allowed item types for annotations.  The model will have the list of item types
     * mapped to "itemTypes". If there was an error, the model will contain the error message mapped to "errorMessage".
     *
     * @param model the model
     * @return the model, containing the "itemTypes" or "errorMessage"
     */
    @RequestMapping (value="/itemTypes.json", method=RequestMethod.GET)
    public ExtendedModelMap getItemTypes(final ExtendedModelMap model) {
        try {
            final List<DccAnnotationItemType> itemTypes = annotationService.getItemTypes();
            model.addAttribute("itemTypes", itemTypes);
        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
        }
        return model;
    }

    /**
     * Handles requests to get allowed annotation categories.
     *
     * @param model the model
     * @return the model with "annotationCategories" or "errorMessage"
     */
    @RequestMapping (value="/annotationCategories.json", method=RequestMethod.GET)
    public ExtendedModelMap getAnnotationCategories(final ExtendedModelMap model) {
        try {
            final List<DccAnnotationCategory> annotationCategories = annotationService.getAnnotationCategories();
            model.addAttribute("annotationCategories", annotationCategories);
        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
        }
        return model;
    }

    /**
     * Handles request to get all annotation classifications.
     *
     * @param model the model
     * @return the model with either "annotationClassifications" or "errorMessage"
     */
    @RequestMapping (value="/annotationClassifications.json", method=RequestMethod.GET)
    public ExtendedModelMap getAnnotationClassifications(final ExtendedModelMap model) {
        try {
            final List<DccAnnotationClassification> classifications = annotationService.getAnnotationClassifications();
            model.addAttribute("annotationClassifications", classifications);
        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
        }
        return model;
    }

    /**
     * Handles requests to get active diseases.  The model will either contain a List of Tumor beans, or if there was
     * an error getting the list, the model will map the error
     * message to "errorMessage".
     *
     * @param modelMap the model
     * @return the model with "diseases" or "errorMessage"
     */
    @RequestMapping (value="/diseases.json", method=RequestMethod.GET)
    public ExtendedModelMap getActiveDiseases(final ExtendedModelMap modelMap) {
        try {
            final List<Tumor> diseases = annotationService.getActiveDiseases();
            modelMap.addAttribute("diseases", diseases);
        } catch (Exception e) {
            modelMap.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
        }
        return modelMap;
    }

    /**
     * Handles request for annotation detail for given ID.  The returned model will contain the annotation object
     * with the key "annotation".  If the annotation was not found, there will be an error message mapped to the key
     * "errorMessage".
     *
     * @param model the model
     * @param annotationId the ID of the annotation to get
     * @return the model with "annotation" or "errorMessage"
     */
    @RequestMapping (value="/annotation.json", method=RequestMethod.GET)
    public ExtendedModelMap getAnnotationById(final ExtendedModelMap model,
                                              @RequestParam (value="annotationId") final Long annotationId) {
        try {
            final DccAnnotation annotation = annotationService.getAnnotationById(annotationId);
            model.addAttribute("annotation", annotation);
        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
        }
        return model;
    }

    /**
     * Sets the annotation service to be used by this controller.
     *
     * @param annotationService the annotation service
     */
    public void setAnnotationService(final AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    /**
     *  Handles requests to add a new note to an annotation.
     *
     * @param session the session
     * @param model the model
     * @param annotationId the ID of the annotation to add the note to
     * @param noteText the text for the new note
     * @return model with "note" and "noteId" or "errorMessage"      
     */
    @RequestMapping (value="/addNote.json", method={RequestMethod.POST} )
    public ExtendedModelMap addNoteToAnnotation(
            final HttpSession session,
            final ExtendedModelMap model,
            @RequestParam (value="annotationId") final Long annotationId,
            @RequestParam (value="note") final String noteText) {
        try {
            final DccAnnotationNote note = annotationService.addNewAnnotationNote(annotationId, noteText,
                    getUtil().getAuthenticatedPrincipalLoginName(), new Date());
            model.addAttribute("note", note);
            model.addAttribute("noteId", note.getNoteId());
            model.addAttribute("success", true); // needed for form results by ExtJs
        } catch(AuthenticationCredentialsNotFoundException e) {
            model.addAttribute(getUtil().getAuthenticationCredentialsNotFoundExceptionMessageKey(), getUtil().getAuthenticationCredentialsNotFoundExceptionMessageValue());
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
            model.addAttribute("success", false);

        } catch(AccessDeniedException e) {
            model.addAttribute(getUtil().getAccessDeniedExceptionMessageKey(), getUtil().getAccessDeniedExceptionMessageValue());
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
            model.addAttribute("success", false);

        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
            model.addAttribute("success", false); // needed for form results by ExtJs
        }
        return model;
    }

    /**
     * Handles requests for editing an annotation note's text.  
     *
     * @param session the session
     * @param model the model
     * @param noteId the ID of the note to edit
     * @param newNoteText the new text for the note
     * @return the model with "note" or "errorMessage"
     */
    @RequestMapping (value="/editNote.json", method={RequestMethod.POST} )
    public ExtendedModelMap editAnnotationNote(
            final HttpSession session,
            final ExtendedModelMap model,
            @RequestParam (value="noteId") final Long noteId,
            @RequestParam (value="note") final String newNoteText) {

        try {
            final DccAnnotationNote editedNote = annotationService.editAnnotationNote(noteId, newNoteText, getUtil().getAuthenticatedPrincipalLoginName());
            model.addAttribute("note", editedNote);
            model.addAttribute("success", true); // needed for form results by ExtJs
        } catch(AuthenticationCredentialsNotFoundException e) {
            model.addAttribute(getUtil().getAuthenticationCredentialsNotFoundExceptionMessageKey(), getUtil().getAuthenticationCredentialsNotFoundExceptionMessageValue());
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
            model.addAttribute("success", false);

        } catch(AccessDeniedException e) {
            model.addAttribute(getUtil().getAccessDeniedExceptionMessageKey(), getUtil().getAccessDeniedExceptionMessageValue());
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
            model.addAttribute("success", false);

        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
            model.addAttribute("success", false);
        }

        return model;
    }

    /**
     *  Handles requests to search annotations.
     *
     * @param session the HttpSession
     * @param model the model
     * @param diseaseId the disease id to search on
     * @param item the item name to search on
     * @param classificationId classification id to search on
     * @param categoryId the category id to search on
     * @param itemTypeId the item type id
     * @param keyword the note keyword
     * @param exact if the item match should be exact
     * @param status status of the annotation
     * @param includeRescinded  if set to <code>false</code>, the default, returns only unrescinded annotations, otherwise returns rescinded and unrescinded annotations
     * @param annotationIdString comma-separated string of annotation ids, or null
     * @param annotatorUsername annotator
     * @return model with "annotations" or "errorMessage"
     */
    @RequestMapping (value="/search.json", method={RequestMethod.POST, RequestMethod.GET} )
    public ExtendedModelMap searchAnnotations(
            final HttpSession session,
            final ExtendedModelMap model,
            @RequestParam (value="diseaseId", required=false) final Integer diseaseId,
            @RequestParam (value="item", required=false) final String item,
            @RequestParam (value="annotationClassificationId", required = false) final Long classificationId,
            @RequestParam (value="annotationCategoryId", required=false) final Long categoryId,
            @RequestParam (value="itemTypeId", required=false) final Long itemTypeId,
            @RequestParam (value="keyword", required=false) final String keyword,
            @RequestParam (value="exactItem", required=false) Boolean exact,
            @RequestParam (value="status", required=false) final String status,
            @RequestParam (value="includeRescinded", required=false) final Boolean includeRescinded,
            @RequestParam (value="annotationId", required=false) final String annotationIdString,
            @RequestParam (value="annotatorUsername", required=false) final String annotatorUsername) {

        try {
            if (exact == null) {
                exact = false;
            }

            final List<Long> annotationIds = getAnnotationIds(annotationIdString);
            final List<DccAnnotation> annotations = annotationService.searchAnnotations(diseaseId, item, categoryId, classificationId,
                    itemTypeId, keyword, exact, status, includeRescinded, getAuthenticatedUsernameOrNull(), null, annotationIds, annotatorUsername);
            model.addAttribute("annotations", annotations);
            model.addAttribute("success", true);
            session.setAttribute(ATTRIBUTE_LAST_SEARCH_RESULTS, annotations);

        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
            model.addAttribute("success", false);
        }

        return model;
    }

    /**
     *
     * @param annotationIdString string of ids, separated by commas, semicolons, tabs, or newlines
     * @return list of Longs
     * @throws NumberFormatException if any item in the list is not a long
     */
    public static List<Long> getAnnotationIds(final String annotationIdString) throws NumberFormatException {
        List<Long> annotationIds = null;
        if (annotationIdString != null && annotationIdString.trim().length() > 0) {
            annotationIds = new ArrayList<Long>();
            final String[] ids = annotationIdString.split("[,|;|\\t|\\n|\r\n]");
            for (String id : ids) {
                id = id.trim();
                if (id.length() > 0) {
                    final Long annotationId = Long.valueOf(id);
                    annotationIds.add(annotationId);
                }
            }
        }
        return annotationIds;
    }

    private String getAuthenticatedUsernameOrNull() {
        String username = null;
        try {
            username = getUtil().getAuthenticatedPrincipalLoginName();
        } catch (AuthenticationCredentialsNotFoundException e) {
            // okay, that just means not logged in
        }
        return username;
    }
}
