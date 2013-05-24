/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationClassification;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;

import java.util.List;

/**
 * Interface for annotation DAO.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface AnnotationQueries {

    /**
     * Add the given <code>DccAnnotation</code> with the following option:
     * <p/>
     * - the item validation can be set to be strict or relaxed.
     * <p/>
     * In strict mode, it will be matched against a regular expression and check that the various codes making up the item are present in the database.
     * In relaxed mode, it will only be matched against a regular expression.
     *
     * @param annotation              the annotation to add
     * @param useStrictItemValidation whether to validate the annotation's item against the database values or not
     * @return the ID of the newly added annotation
     * @throws AnnotationQueriesException if there is an error adding the annotation
     * @throws BeanException
     */
    public long addNewAnnotation(final DccAnnotation annotation, final boolean useStrictItemValidation)
            throws AnnotationQueriesException, BeanException;

    /**
     * Add the given <code>DccAnnotation</code> with the following option:
     * <p/>
     * - the item validation is set to be relaxed
     * <p/>
     * It will attempt to validate it against a regular expression
     * but it will not check that the various codes making up the item are present in the database)
     *
     * @param annotation the annotation to add
     * @return the ID of the newly added annotation
     * @throws AnnotationQueriesException if there is an error adding the annotation
     * @throws BeanException 
     */
    public long addNewAnnotation(final DccAnnotation annotation)
            throws AnnotationQueriesException, BeanException;

    /**
     * Gets a list of item types that are defined in the database.
     *
     * @return a list of DccAnnotationItemType objects
     */
    public List<DccAnnotationItemType> getItemTypes();

    /**
     * Gets a list of annotation categories that are defined in the database.
     *
     * @return a list of DccAnnotationCategory objects
     */
    public List<DccAnnotationCategory> getAnnotationCategories();

    /**
     * Gets a list of active diseases in the database
     *
     * @return a list of Tumor objects
     */
    public List<Tumor> getActiveDiseases();

    /**
     * Gets the annotation given by the ID.
     *
     * @param annotationId the annotation id
     * @return a DccAnnotation object with all items and notes included
     * @throws AnnotationQueriesException if no such annotation id is found
     */
    public DccAnnotation getAnnotationById(final Long annotationId) throws AnnotationQueriesException, BeanException;

    /**
     * Adds a new note to an annotation.
     *
     * @param annotationId the id of the annotation
     * @param note         the note to add
     * @return the note ID
     * @throws AnnotationQueriesException if the note couldn't be added
     */
    public Long addNewAnnotationNote(final Long annotationId, final DccAnnotationNote note) throws AnnotationQueriesException, BeanException;

    /**
     * Edits the given note; sets the text, the editedBy field, and also the dateEdited field.
     *
     * @param annotationId the ID of the annotation the note belongs to
     * @param note         the note to edit
     * @param newText      the new text for the note
     * @param editor       the username of the person editing
     * @throws AnnotationQueriesException if the note was not found or could not be updated
     */
    public void editAnnotationNote(Long annotationId, final DccAnnotationNote note, final String newText, final String editor)
            throws AnnotationQueriesException, BeanException;

    /**
     * Gets the note object for the given ID.
     *
     * @param noteId the note id
     * @return note with that id
     * @throws AnnotationQueriesException if the note was not found
     */
    public DccAnnotationNote getAnnotationNoteById(Long noteId) throws AnnotationQueriesException;

    /**
     * Update the given <code>DccAnnotation</code> with the following option:
     * <p/>
     * - the item validation can be set to be strict or relaxed.
     * <p/>
     * In strict mode, it will be matched against a regular expression and check that the various codes making up the item are present in the database.
     * In relaxed mode, it will only be matched against a regular expression.
     *
     * @param annotationId            the ID of the annotation to update
     * @param annotation              the annotation to update
     * @param useStrictItemValidation whether to validate the annotation's item against the database values or not
     * @throws AnnotationQueriesException if there is an error updating the annotation
     */
    public void updateAnnotation(final long annotationId, final DccAnnotation annotation, final boolean useStrictItemValidation) throws AnnotationQueriesException;

    /**
     * Update the given <code>DccAnnotation</code> with the following option:
     * <p/>
     * - the item validation is set to be relaxed
     * <p/>
     * It will attempt to validate it against a regular expression
     * but it will not check that the various codes making up the item are present in the database)
     *
     * @param annotation the annotation to update
     * @throws AnnotationQueriesException if there is an error adding the annotation
     */
    public void updateAnnotation(final DccAnnotation annotation) throws AnnotationQueriesException;

    /**
     * Search for annotations on the given item and/or category.  Null values are ignored (not searched on).
     *
     * @param searchCriteria the search criteria
     * @return a list of annotations, sorted by date added (most recent first) -- will be empty if no matches
     */
    public List<DccAnnotation> searchAnnotations(AnnotationSearchCriteria searchCriteria);

    /**
     * Finds all annotation IDs for annotations that match the search criteria
     *
     * @param searchCriteria the search criteria
     * @return list of annotation IDs
     */
    public List<Long> findMatchingAnnotationIds(final AnnotationSearchCriteria searchCriteria);

    /**
     * Search for annotations count on the given item and/or category.  Null values are ignored (not searched on).
     *
     * @param searchCriteria the search criteria
     * @return a list of annotations, sorted by date added (most recent first) -- will be empty if no matches
     */
    public Long getCountForMatchingAnnotationIds(final AnnotationSearchCriteria searchCriteria);

    /**
     * Search for all annotations. This methods returns all annotations that exist.
     *
     * @return List of all <code>DccAnnotation</code> sorted by date added (most recent first)
     *         -- will be empty if no annotations found
     */
    public List<Long> getAllAnnotationIds();

    /**
     * Return the list of {@link DccAnnotation}s for the given samples.
     * <p/>
     * Notes:
     * - each sample has to be a barcode (no UUID support for now)
     * - un-approved annotations will not be included
     * - rescinded annotations will be included
     *
     * @param samples the list of samples to return the list of {@link DccAnnotation} for
     * @return the list of {@link DccAnnotation}s for the given samples
     */
    public List<DccAnnotation> getAllAnnotationsForSamples(final List<String> samples);

    /**
     * Return the count of {@link DccAnnotation}s for the given samples.
     * <p/>
     * Notes:
     * - each sample has to be a barcode (no UUID support for now)
     * - un-approved annotations will not be included
     * - rescinded annotations will be included
     *
     * @param samples the list of samples to return the list of {@link DccAnnotation} for
     * @return the list of {@link DccAnnotation}s for the given samples
     */
    public Long getAllAnnotationsCountForSamples(List<String> samples);

    public void setCurated(DccAnnotation annotation, boolean isCurated) throws AnnotationQueriesException, BeanException;

    public void setRescinded(DccAnnotation annotation, boolean isRescinded) throws AnnotationQueriesException, BeanException;

    /**
     * Gets all classifications.
     *
     * @return list of classifications beans
     */
    public List<DccAnnotationClassification> getAnnotationClassifications();

    /**
     * Gets the annotation category name for the given id.  Returns null if not found.
     * @param categoryId the category id
     * @return the category name or null
     */
    public String getAnnotationCategoryNameForId(Long categoryId);

    /**
     * Exception for use by the AnnotationQueries object.
     */
    public class AnnotationQueriesException extends Exception {
        public AnnotationQueriesException(final String message) {
            super(message);
        }

        public AnnotationQueriesException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
