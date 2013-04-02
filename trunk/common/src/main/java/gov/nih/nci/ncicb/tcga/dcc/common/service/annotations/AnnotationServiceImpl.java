/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.service.annotations;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationClassification;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationSearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.AclSecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.security.impl.SecurityUtilImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.service.RedactionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Implementation of Annotation Service that uses Annotation Queries.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Component
public class AnnotationServiceImpl implements AnnotationService {

    public final static String PENDING = "pending";
    public final static String APPROVED = "approved";
    public final static String REDACTION = "redaction";

    private final SimpleDateFormat sdf = new SimpleDateFormat();


    @Autowired
    private AnnotationQueries annotationQueries;

    @Autowired
    private RedactionService redactionService;

    @Autowired(required=false)
    private AclSecurityUtil aclSecurityUtil;

    @Override
    public DccAnnotation addAnnotation(final Integer diseaseId, 
    		final Long itemTypeId, 
    		final String item, 
    		final Long annotationCategoryId,
    		final String annotationNote, 
    		final String user,
    		final boolean useStrictItemValidation) throws AnnotationQueries.AnnotationQueriesException, BeanException {
    	
        final DccAnnotation annotation = new DccAnnotation();
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeId(itemTypeId);
        
        final Tumor disease = new Tumor();
        disease.setTumorId(diseaseId);
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
        
        dccAnnotationItem.setItemType(itemType);
        dccAnnotationItem.setItem(item);
        dccAnnotationItem.setDisease(disease);
        annotation.addItem(dccAnnotationItem);
        
        final DccAnnotationCategory category = new DccAnnotationCategory();
        category.setCategoryId(annotationCategoryId);
        annotation.setAnnotationCategory(category);
        final Date now = new Date();
        annotation.setDateCreated(now);
        annotation.addNote(annotationNote, user, now);
        annotation.setCreatedBy(user);

        final long id = annotationQueries.addNewAnnotation(annotation, useStrictItemValidation);
        annotation.setId(id);

        // if this is null, means we don't want to add ACLs for new notes (such as when used by qclive)
        if (aclSecurityUtil != null) {
            //Security: Retrieve the note just added and add the expected permission for the currently authenticated user
            if(!annotation.isNotesEmpty()){
                final DccAnnotationNote dccAnnotationNote = annotation.getNotes().get(0);
                aclSecurityUtil.addPermission(dccAnnotationNote, BasePermission.WRITE);
            }
        }

        return annotation;
    }

    @Override
    public DccAnnotation addAnnotation(final Integer diseaseId,
    		final Long itemTypeId,
    		final String item,
    		final Long annotationCategoryId,
    		final String annotationNote,
    		final String user) throws AnnotationQueries.AnnotationQueriesException, BeanException {

    	final boolean useStrictItemValidation = false;
    	return addAnnotation(diseaseId, itemTypeId, item, annotationCategoryId, annotationNote, user, useStrictItemValidation);
    }

    @Override
    public DccAnnotation updateAnnotation(
            final Long annotationId,
    		final Integer diseaseId,
    		final Long itemTypeId,
    		final String item,
            final String status,
    		final Long annotationCategoryId,
            final String updatedBy,
    		final boolean useStrictItemValidation) throws AnnotationQueries.AnnotationQueriesException, BeanException {

        // 1. Get data for existing annotation (by id) from DB
        final DccAnnotation annotation = annotationQueries.getAnnotationById(annotationId);
        // 2. check permission

        if (aclSecurityUtil != null) {  // null means we don't want to add ACLs (such as when used by qclive)
            if (!SecurityUtilImpl.isAdministrator()) { //admin can edit any annotations
                if (!updatedBy.equals(annotation.getCreatedBy())) {
                    throw new AnnotationQueries.AnnotationQueriesException("User doesn't have permissions to update annotation");
                }
            }
        }

        // 3. Update annotation data
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeId(itemTypeId);

        final Tumor disease = new Tumor();
        disease.setTumorId(diseaseId);
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
        dccAnnotationItem.setItemType(itemType);
        dccAnnotationItem.setItem(item);
        dccAnnotationItem.setDisease(disease);
        dccAnnotationItem.setId(annotation.getItems().get(0).getId());
        annotation.getItems().set( 0, dccAnnotationItem );

        final DccAnnotationCategory category = new DccAnnotationCategory();
        category.setCategoryId(annotationCategoryId);
        annotation.setAnnotationCategory(category);
        final Date now = new Date();
        annotation.setDateUpdated(now);
        annotation.setUpdatedBy(updatedBy);
        annotation.setApproved(APPROVED.equalsIgnoreCase(status)?true:false);
        // 4. Update annotation in DB
        annotationQueries.updateAnnotation(annotation.getId(), annotation, useStrictItemValidation);
        // 5. Redacted item process
        if (annotation.getApproved() && REDACTION.equalsIgnoreCase(getClassificationNameFromCategoryId(annotationCategoryId))){
            DiseaseContextHolder.setDisease(getDiseaseAbbreviationFromDiseaseId(diseaseId));
            redactionService.redact(item);
        }

        return annotation;
    }

    @Override
    public DccAnnotation rescindAnnotation(
            final Long annotationId,
            final String updatedBy
    ) throws AnnotationQueries.AnnotationQueriesException, BeanException {
        // 1. Get data for existing annotation (by id) from DB
        final DccAnnotation annotation = annotationQueries.getAnnotationById(annotationId);

        // 2. check permission
        if (aclSecurityUtil != null) {  // null means we don't want to add ACLs (such as when used by qclive)
            if (!SecurityUtilImpl.isAdministrator()) { //admin can edit any annotations
                    throw new AnnotationQueries.AnnotationQueriesException("User doesn't have permissions to update annotation");
            }
        }

        // 3. rescind the annotation
        annotation.setRescinded(true);
        annotationQueries.updateAnnotation(annotation.getId(), annotation, false);
        final String systemAnnotationNoteText = "This annotation was rescinded by " + updatedBy + " on " + sdf.format(new Date());
        final DccAnnotationNote systemNote = addNewAnnotationNote(annotation.getId(), systemAnnotationNoteText, updatedBy, new Date());
        annotation.addNote(systemNote);

        // 4. Redacted item rescinding process
        final String classification = annotation.getAnnotationCategory().getAnnotationClassification().getAnnotationClassificationName();
        if (annotation.getApproved() && REDACTION.equalsIgnoreCase(classification)){
            for (final DccAnnotationItem item:annotation.getItems()) {
                DiseaseContextHolder.setDisease(item.getDisease().getTumorName());
                redactionService.rescind(item.getItem());
            }
        }

        return annotation;
    }

    /**
     * get classification name from the category id of an annotation
     * @param categoryId
     * @return classification name
     */
    public String getClassificationNameFromCategoryId(final Long categoryId){
        final List<DccAnnotationCategory> annotationCategoryList = getAnnotationCategories();
        for (final DccAnnotationCategory annotationCategory:annotationCategoryList){
            if (annotationCategory.getCategoryId().equals(categoryId)){
                return annotationCategory.getAnnotationClassification().getAnnotationClassificationName();
            }
        }
        return null;
    }

    /**
     * get Disease Abbreviation From Disease Id
     * @param diseaseId
     * @return disease abbreviation
     */
    public String getDiseaseAbbreviationFromDiseaseId(final Integer diseaseId){
        for (final Tumor disease:getActiveDiseases()){
            if (disease.getTumorId().equals(diseaseId)){
                return disease.getTumorName();
            }
        }
        return null;
    }

    public List<DccAnnotationItemType> getItemTypes() {
        return annotationQueries.getItemTypes();
    }

    public List<DccAnnotationCategory> getAnnotationCategories() {
        return annotationQueries.getAnnotationCategories();
    }

    @Override
    public List<DccAnnotationClassification> getAnnotationClassifications() {
        return annotationQueries.getAnnotationClassifications();
    }

    public void setAnnotationQueries(final AnnotationQueries annotationQueries) {
        this.annotationQueries = annotationQueries;
    }

    public List<Tumor> getActiveDiseases() {
        return annotationQueries.getActiveDiseases();
    }

    public DccAnnotation getAnnotationById(final Long annotationId) throws AnnotationQueries.AnnotationQueriesException, BeanException {
        return annotationQueries.getAnnotationById(annotationId);
    }

    @Override
    public DccAnnotationNote getAnnotationNoteById(final Long dccAnnotationNoteId) throws AnnotationQueries.AnnotationQueriesException {
        return annotationQueries.getAnnotationNoteById(dccAnnotationNoteId);
    }

    /**
     * Adds a new note with the given parameters to the annotation with the given id.
     *
     * @param annotationId the ID of the annotation the note is for
     * @param noteText the text of the note
     * @param addedBy the username of the person adding the note
     * @param dateAdded the date the note was added (if null will use now)
     * @return the new note object
     * @throws AnnotationQueries.AnnotationQueriesException if the note can't be added to the database
     */
    public DccAnnotationNote addNewAnnotationNote(final Long annotationId, final String noteText, final String addedBy,
                                                  final Date dateAdded)
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotationNote note = new DccAnnotationNote();
        note.setNoteText(noteText);
        note.setAddedBy(addedBy);
        note.setDateAdded(dateAdded == null ? new Date() : dateAdded);
        final long noteId = annotationQueries.addNewAnnotationNote(annotationId, note);
        note.setNoteId(noteId);

        if (aclSecurityUtil != null) {
            //Security: add the expected permission on the note to the currently authenticated user
            aclSecurityUtil.addPermission(note, BasePermission.WRITE);
        }
        return note;
    }

    /**
     * Edits the note with the given ID to have the new note text.
     *
     * @param noteId the ID of the note to edit
     * @param newText the new text of the note
     * @param editor the username of the person editing
     * @return the note object with fields updated
     * @throws AnnotationQueries.AnnotationQueriesException if the note was not found
     */
    public DccAnnotationNote editAnnotationNote(final Long noteId, final String newText, final String editor)
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotationNote note = annotationQueries.getAnnotationNoteById(noteId);
        annotationQueries.editAnnotationNote(note.getAnnotationId(), note, newText, editor);
        return note;
    }

    @Override
    public List<DccAnnotation> searchAnnotations(final Integer diseaseId,
                                                 final String item,
                                                 final Long categoryId,
                                                 final Long classificationId,
                                                 final Long itemTypeId,
                                                 final String keyword,
                                                 final boolean exact,
                                                 final String status,
                                                 final Boolean includeRescinded,
                                                 final String authenticatedUsername,
                                                 final Integer rowLimit,
                                                 List<Long> annotationIds,
                                                 final String annotatorUsername) {



        if(annotationIds == null) {
            //Do a search with the other filters
            final AnnotationSearchCriteria criteria = new AnnotationSearchCriteria(item, categoryId, itemTypeId, keyword);
            criteria.setDiseaseId(diseaseId);
            criteria.setExact(exact);
            criteria.setAuthenticatedUsername(authenticatedUsername);
            criteria.setRowLimit(rowLimit);
            criteria.setAnnotatorUsername(annotatorUsername);
            criteria.setClassificationId(classificationId);

            if (StringUtils.isBlank(status)){
                criteria.setCurated(null);
            } else if (PENDING.equalsIgnoreCase(status)){
                criteria.setCurated(false);
            } else if (APPROVED.equalsIgnoreCase(status)){
                criteria.setCurated(true);
            }

            criteria.setIncludeRescinded(includeRescinded);

            annotationIds = annotationQueries.findMatchingAnnotationIds(criteria);

        }
        return getAnnotationsForIds(annotationIds);
    }

    public void curate(final DccAnnotation annotation)
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        annotationQueries.setCurated(annotation, true);
    }

    @Override
    @PostConstruct
    public List<DccAnnotation> getAllAnnotations() {
        return getAnnotationsForIds(annotationQueries.getAllAnnotationIds());
    }

    @Override
    public List<DccAnnotation> getAllAnnotationsForSamples(List<String> samples) {
        return annotationQueries.getAllAnnotationsForSamples(samples);
    }

    public void setAclSecurityUtil(final AclSecurityUtil aclSecurityUtil) {
        this.aclSecurityUtil = aclSecurityUtil;
    }

    public void setRedactionService(final RedactionService redactionService) {
        this.redactionService = redactionService;
    }

    // gets annotations by ID one at a time, calling the DAO (or an ehcache proxy!)
    private List<DccAnnotation> getAnnotationsForIds(final List<Long> annotationIds) {
        List<DccAnnotation> annotations = new ArrayList<DccAnnotation>();

        //Retrieve the annotation with the given Ids
        for (final Long annotationId : annotationIds) {
            try {
                annotations.add(getAnnotationById(annotationId));
            } catch(final AnnotationQueries.AnnotationQueriesException e) {
                // The annotation with the given Id wasn't found
            } catch (final BeanException be) {
                // The annotation for the given id is in an inconsistent state
                // @TODO : log this?
            }
        }

        // sort by date created, descending
        Collections.sort(annotations, new Comparator<DccAnnotation>() {
            public int compare(final DccAnnotation a1, final DccAnnotation a2) {
                if (a2.getDateCreated() == null || a1.getDateCreated() == null) {
                    return 0;
                }
                return a2.getDateCreated().compareTo(a1.getDateCreated());
            }
        });
        return annotations;
    }
}
