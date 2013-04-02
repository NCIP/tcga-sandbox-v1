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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;

import java.util.Date;
import java.util.List;

/**
 * Service interface for annotations.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface AnnotationService {

    /**
     *  Adds the annotation with given parameters.
     *
     *  If 'useStrictItemValidation' is set to <code>true</code> the operation will throw an exception
     *  when attempting to add an item that does not match a regular expression or that does not exist in the database.
     *
     *  Note: the resulting annotation will only contain one item.
     *
     * @param diseaseId the disease id for the annotation
     * @param itemTypeId the item type for the annotation
     * @param item the item (barcode or UUID)
     * @param annotationCategoryId the annotation category
     * @param annotationNote the annotation
     * @param user the user adding the annotation
     * @param useStrictItemValidation whether to validate the annotation's item against the database values or not
     * @return the annotation object created, if successful
     * @throws AnnotationQueries.AnnotationQueriesException if adding the annotation failed
     */
    public DccAnnotation addAnnotation(
    		final Integer diseaseId,
    		final Long itemTypeId,
    		final String item,
    		final Long annotationCategoryId,
    		final String annotationNote,
    		final String user,
    		final boolean useStrictItemValidation) throws AnnotationQueries.AnnotationQueriesException, BeanException;

    /**
     *  Adds the annotation with given parameters.
     *
     *  Item validation is set to be relaxed (it will only attempt to match it against a regular expression)
     *
     *  Note: the resulting annotation will only contain one item.
     *
     * @param diseaseId the disease id for the annotation
     * @param itemTypeId the item type for the annotation
     * @param item the item (barcode or UUID)
     * @param annotationCategoryId the annotation category
     * @param annotationNote the annotation
     * @param user the user adding the annotation
     * @return the annotation object created, if successful
     * @throws AnnotationQueries.AnnotationQueriesException if adding the annotation failed
     */
    public DccAnnotation addAnnotation(
    		final Integer diseaseId,
    		final Long itemTypeId,
    		final String item,
    		final Long annotationCategoryId,
    		final String annotationNote,
    		final String user) throws AnnotationQueries.AnnotationQueriesException, BeanException;

    /**
     *  Update (pending) annotation with given parameters.
     *
     *  If 'useStrictItemValidation' is set to <code>true</code> the operation will throw an exception
     *  when attempting to add an item that does not match a regular expression or that does not exist in the database.
     *
     *  Note: the resulting annotation will only contain one item.
     *
     * @param annotationId the disease id for the annotation
     * @param diseaseId the disease id for the annotation
     * @param itemTypeId the item type for the annotation
     * @param item the item (barcode or UUID)
     * @param status the status approved or pending of the annotation
     * @param annotationCategoryId the annotation category
     * @param updatedBy the user adding the annotation
     * @param useStrictItemValidation whether to validate the annotation's item against the database values or not
     * @return the annotation object created, if successful
     * @throws AnnotationQueries.AnnotationQueriesException if adding the annotation failed
     */
    public DccAnnotation updateAnnotation(
            final Long annotationId,
    		final Integer diseaseId,
    		final Long itemTypeId,
    		final String item,
            final String status,
    		final Long annotationCategoryId,
            final String updatedBy,
    		final boolean useStrictItemValidation) throws AnnotationQueries.AnnotationQueriesException, BeanException;

    /**
     * Rescinds a {@link DccAnnotation} by setting the attributes rescinded on the annotation. Adds an automated
     * {@link DccAnnotationNote} to the annotation indicating that the annotation was rescinded by the updatedBy user.
     * The note contains the updatedBy user who rescinded the annotation.
     * An annotation can be rescinded only by an annotation admin and must be approved before it can be rescinded
     *
     * @param annotationId the id of the annotation
     * @param updatedBy the user rescinding the annotation
     * @return A {@link DccAnnotation} with any values updated to reflect current db state
     * @throws AnnotationQueries.AnnotationQueriesException if the rescind operation fails to update the database or
     *          the updatedBy user does not have permissions to rescind the annotation.
     * @throws BeanException if the annotation state after rescind is invalid
     */
    public DccAnnotation rescindAnnotation(
            final Long annotationId,
            final String updatedBy
    ) throws AnnotationQueries.AnnotationQueriesException, BeanException;

    /**
     * Get the list of valid item types. Each item in the returned list contains a map with ID and NAME fields.
     *
     * @return a list of item types
     */
    public List<DccAnnotationItemType> getItemTypes();

    /**
     * Get the list of valid annotation categories.
     *
     * @return a list of annotation categories
     */
    public List<DccAnnotationCategory> getAnnotationCategories();

    /**
     *
     * @return list of annotation classifications
     */
    public List<DccAnnotationClassification> getAnnotationClassifications();

    /**
     * Get the active diseases.  Each item in the returned list contains a map with ID and NAME fields.
     *
     * @return a list of diseases
     */
    public List<Tumor> getActiveDiseases();

    /**
     * Gets the annotation for the given id.
     *
     * @param annotationId the id of the annotation
     * @return the annotation
     * @throws AnnotationQueries.AnnotationQueriesException if the annotation could not be found
     */
    public DccAnnotation getAnnotationById(final Long annotationId) throws AnnotationQueries.AnnotationQueriesException, BeanException;

    /**
     * Return the DccAnnotationNote gor the given Id
     *
     * @param dccAnnotationNoteId the Id of the DccAnnotationNote
     * @return the DccAnnotationNote
     * @throws AnnotationQueries.AnnotationQueriesException if the annotation could not be found
     */
    DccAnnotationNote getAnnotationNoteById(final Long dccAnnotationNoteId) throws AnnotationQueries.AnnotationQueriesException;

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
                                                  final Date dateAdded) throws AnnotationQueries.AnnotationQueriesException, BeanException;

    /**
     * Edits the note with the given ID to have the new note text.
     *
     * @param noteId the ID of the note to edit
     * @param newText the new text of the note
     * @param editor the username of the person editing
     * @return the note object with fields updated
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries.AnnotationQueriesException if the note was not found
     */
    public DccAnnotationNote editAnnotationNote(final Long noteId, final String newText, final String editor)
            throws AnnotationQueries.AnnotationQueriesException, BeanException;

    /**
     * Search for annotations.
     *
     * @param diseaseId the id of the disease
     * @param item the name of the item(s) -- comma-separated if more than one
     * @param categoryId the category id
     * @param classificationId the classification id
     * @param itemTypeId the item type id
     * @param keyword the note keyword
     * @param itemExact true if item match should be exact, false if item should start with given item string
     * @param status status of annotation
     * @param includeRescinded if set to <code>false</code>, the default, returns rescinded and unrescinded annotations, otherwise only unrescinded annotations
     * @param authenticatedUsername the authenticated user, if any
     * @param rowLimit limit to the number of results (null or value <1 for all)
     * @param annotationIds the Ids of the <code>DccAnnotation</code> to include in the returned list (ignoring other filters), if not null
     * @param annotatorUsername the username of the user who created the annotation
     * @return list of annotations for the item
     */
    public List<DccAnnotation> searchAnnotations(final Integer diseaseId,
                                                 final String item,
                                                 final Long categoryId,
                                                 final Long classificationId,
                                                 final Long itemTypeId,
                                                 final String keyword,
                                                 final boolean itemExact,
                                                 final String status,
                                                 final Boolean includeRescinded,
                                                 final String authenticatedUsername,
                                                 final Integer rowLimit,
                                                 final List<Long> annotationIds,
                                                 final String annotatorUsername);

    public void curate(DccAnnotation annotation) throws AnnotationQueries.AnnotationQueriesException, BeanException;

    /**
     * Search for all annotations in the database.
     * NOTE : This query is very expensive and should not be used in an user interactive scenario
     * It eagerly loads all associated objects of DccAnnotation model object and fills it up.
     * @return A comprehensive list of all DccAnnotation in the database
     */
    public List<DccAnnotation> getAllAnnotations();

    /**
     * Return the list of {@link DccAnnotation}s for the given samples.
     *
     * Notes:
     * - each sample has to be a barcode (no UUID support for now)
     * - un-approved annotations will not be included
     * - rescinded annotations will be included
     *
     * @param samples the list of samples to return the list of {@link DccAnnotation} for
     * @return the list of {@link DccAnnotation}s for the given samples
     */
    public List<DccAnnotation> getAllAnnotationsForSamples(final List<String> samples);
}
