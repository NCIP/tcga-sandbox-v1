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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.security.access.annotation.Secured;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.PartialCacheKey;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

/**
 * JDBC implementation of AnnotationQueries.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AnnotationQueriesJDBCImpl extends JdbcDaoSupport implements AnnotationQueries {

    private static final String CATEGORY_FIELD_ID = "ID";
    private static final String CATEGORY_FIELD_NAME = "NAME";
    private static final String CATEGORY_FIELD_ITEM_TYPES = "ITEM_TYPES";

    /*
    * Insert queries
    */

    /**
     * Query for inserting an annotation
     */
    private static final String INSERT_ANNOTATION_QUERY = "insert into annotation (annotation_id, annotation_category_id, entered_by, entered_date) " +
            "values (?, ?, ?, ?)";

    /**
     * query for inserting a note
     */
    private static final String INSERT_ANNOTATION_NOTE_QUERY = "insert into annotation_note (annotation_note_id, annotation_id, note, entered_by, entered_date) " +
            "values (?, ?, ?, ?, ?)";

    /**
     * query for inserting an item
     */
    private static final String INSERT_ANNOTATION_ITEM_QUERY = "insert into annotation_item (annotation_item_id, annotation_id, item_type_id, annotation_item, disease_id) " +
            "values (annotation_item_seq.nextval, ?, ?, ?, ?)";

    /*
    * Update queries
    */

    /**
     * Query for updating a note
     */
    private static final String EDIT_NOTE_QUERY = "update annotation_note set note=?, modified_by=?, modified_date=? where annotation_note_id=?";

    /**
     * Query for updating the curated value of an annotation
     */
    public static final String UPDATE_CURATED_QUERY = "update annotation set curated=? where annotation_id=?";

    /**
     * Query for updating the rescinded value of an annotation
     */
    public static final String UPDATE_RESCINDED_QUERY = "update annotation set rescinded=? where annotation_id=?";

    /*
    * Select queries
    */

    /**
     * Query for selecting item types
     */
    private static final String ITEM_TYPES_QUERY = "select item_type_id as ID, " +
            "type_display_name as NAME, " +
            "type_description as description " +
            "from annotation_item_type " +
            "order by name";

    /**
     * Query for selecting categories
     */
    private static final String CATEGORIES_QUERY = "select annotation_category.annotation_category_id as ID, " +
            "category_display_name as NAME, " +
            "item_type_id as ITEM_TYPES, " +
            "classification_display_name, annotation_classification.annotation_classification_id " +
            "from annotation_category, annotation_classification, " +
            "annotation_category_item_type " +
            "where annotation_category.annotation_category_id=annotation_category_item_type.annotation_category_id and " +
            "annotation_classification.annotation_classification_id=annotation_category.annotation_classification_id " +
            "order by category_display_name, item_type_id";

    private static final String CLASSIFICATION_FOR_CATEGORY_QUERY = "select annotation_classification.annotation_classification_id, " +
            "classification_display_name from annotation_classification, annotation_category " +
            "where annotation_classification.annotation_classification_id=annotation_category.annotation_classification_id " +
            "and annotation_category_id=?";

    private static final String CLASSIFICATIONS_QUERY = "select annotation_classification_id, classification_display_name " +
            "from annotation_classification order by classification_display_name";

    /**
     * Query for selecting a disease
     */
    private static final String DISEASE_QUERY = "select disease_id as ID, " +
            "disease_abbreviation as NAME, " +
            "disease_name as DESCRIPTION " +
            "from disease where active=1 order by disease_name";

    /**
     * Query for selecting a category item type
     */
    private static final String CATEGORY_ITEM_TYPE_QUERY = "select count(*) " +
            "from annotation_category_item_type " +
            "where item_type_id=? " +
            "and annotation_category_id=?";

    /**
     * Query for selecting an item type name
     */
    private static final String ITEM_TYPE_NAME_QUERY = "select type_display_name " +
            "from annotation_item_type " +
            "where item_type_id=?";

    /**
     * Query for selecting a category name
     */
    private static final String CATEGORY_NAME_QUERY = "select category_display_name " +
            "from annotation_category " +
            "where annotation_category_id=?";

    /**
     * Query for selecting an annotation notes
     */
    private static final String SELECT_ANNOTATION_NOTES_QUERY = "select annotation_note_id as id, " +
            "note, " +
            "entered_by as added_by, " +
            "entered_date as date_added, " +
            "modified_by as edited_by, " +
            "modified_date as date_edited " +
            "from annotation_note " +
            "where annotation_id=? " +
            "order by entered_date";

    /**
     * Query for selecting an annotation (without the notes)
     */
    private static final String SELECT_ANNOTATION_BY_ID_QUERY =
            "select a.annotation_category_id, " +
                    "ac.category_display_name as annotation_category, " +
                    "cl.classification_display_name, " +
                    "cl.annotation_classification_id, " +
                    "a.entered_by as created_by, " +
                    "d.disease_id, " +
                    "d.disease_abbreviation, " +
                    "d.disease_name, " +
                    "a.entered_date as created_date, " +
                    "a.modified_by as edited_by, " +
                    "a.modified_date as date_edited, " +
                    "ai.annotation_item_id," +
                    "ai.item_type_id, " +
                    "ait.type_display_name as item_type, " +
                    "ai.annotation_item, " +
                    "a.curated, " +
                    "a.rescinded " +
                    "from annotation a, annotation_item ai, annotation_category ac, annotation_classification cl, annotation_item_type ait, disease d " +
                    "where a.annotation_id=ai.annotation_id " +
                    "and d.disease_id=ai.disease_id " +
                    "and ac.annotation_category_id=a.annotation_category_id " +
                    "and ait.item_type_id=ai.item_type_id " +
                    "and ac.annotation_classification_id=cl.annotation_classification_id " +
                    "and a.annotation_id=? " +
                    "order by ai.annotation_item";

    /**
     * Query for selecting a note
     */
    private static final String SELECT_NOTE_BY_ID_QUERY = "select note, annotation_id, " +
            "entered_by, " +
            "entered_date, " +
            "modified_by, " +
            "modified_date " +
            "from annotation_note " +
            "where annotation_note_id=?";

    /**
     * Query that returns the number of annotations that are identical (ideally never > 1):
     * <p/>
     * - same disease
     * - same item type
     * - same item
     * - same category
     * - same note
     */
    private static final String SAME_ANNOTATION_COUNT_QUERY = "select count(*) " +
            "from annotation a, " +
            "annotation_note an, " +
            "annotation_category ac, " +
            "annotation_category_item_type acit, " +
            "annotation_item_type ait, " +
            "annotation_item ai " +
            "where an.note=? " +
            "and an.annotation_id=a.annotation_id " +
            "and a.annotation_category_id=ac.annotation_category_id " +
            "and ac.annotation_category_id=?" +
            "and acit.annotation_category_id=ac.annotation_category_id " +
            "and acit.item_type_id=ait.item_type_id " +
            "and ait.item_type_id=? " +
            "and ai.item_type_id=ait.item_type_id " +
            "and ai.annotation_id=a.annotation_id " +
            "and ai.annotation_item=? " +
            "and ai.disease_id=?";

    /**
     * Query that returns the id of the annotation that contains the note with the given id
     */
    private static final String SELECT_ANNOTATION_ID_BY_NOTE_ID_QUERY = "select annotation_id from annotation_note where annotation_note_id=?";

    private static final String NON_UNIQUE_ANNOTATION_EXCEPTION_MESSAGE = "The following annotation is not unique: " +
            "[disease:{0}][item type:{1}][item identifier:{2}][category:{3}][note:{4}]";

    /**
     * Query to update annotation_item
     */
    private static final String UPDATE_ANNOTATION_ITEM = "update annotation_item set item_type_id= ?, annotation_item=?, " +
            "disease_id=? where annotation_item_id = ?";

    /**
     * Query to update annotation record
     */
    private static final String UPDATE_ANNOTATION = "update annotation set annotation_category_id = ?, modified_by = ?, " +
            "modified_date = ?, curated = ?, rescinded = ? where annotation_id = ?";

    private DataFieldMaxValueIncrementer annotationIdSequence, annotationNoteIdSequence;

    @Autowired
    private TumorQueries tumorQueries;

    @Autowired
    private CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator;

    @Autowired
    private UUIDService uuidService;

    /**
     * Lock used to synchronize methods that update annotations in the database
     * to ensure that concurrent executions do not allow the creation of duplicates
     */
    private final Object lock = new Object();

    public void setTumorQueries(final TumorQueries tumorQueries) {
        this.tumorQueries = tumorQueries;
    }

    public void setUuidService(final UUIDService uuidService) {
        this.uuidService = uuidService;
    }

    @Override
    @Secured({"ROLE_ANNOTATION_CREATOR"})
    public long addNewAnnotation(final DccAnnotation annotation, final boolean useStrictItemValidation)
            throws AnnotationQueriesException, BeanException {
        synchronized (lock) {

            // if this fails it will throw an exception, which is why there is no return value
            validateNewAnnotation(annotation, useStrictItemValidation);

            // get the next ID
            long annotationId = annotationIdSequence.nextLongValue();

            // add a row to the annotation table
            getJdbcTemplate().update(INSERT_ANNOTATION_QUERY,
                    annotationId,
                    annotation.getAnnotationCategory().getCategoryId(),
                    annotation.getCreatedBy(),
                    new java.sql.Timestamp(annotation.getDateCreated().getTime()));

            // add row(s) to the annotation_item table
            for (final DccAnnotationItem dccAnnotationItem : annotation.getItems()) {
                addNewAnnotationItem(annotationId, dccAnnotationItem);
            }

            // add row(s) to the annotation_note table
            if (!annotation.isNotesEmpty()) {
                for (final DccAnnotationNote note : annotation.getNotes()) {
                    addNewAnnotationNote(annotationId, note);
                }
            }
            return annotationId;
        }
    }

    @Override
    @Secured({"ROLE_ANNOTATION_CREATOR"})
    public long addNewAnnotation(final DccAnnotation annotation)
            throws AnnotationQueriesException, BeanException {

        final boolean useStrictItemValidation = false;
        return addNewAnnotation(annotation, useStrictItemValidation);
    }

    /**
     * Gets the name of a category with the given id.  Throws an exception if no such id found.
     *
     * @param annotationCategoryId the category id
     * @return the name of the category
     * @throws AnnotationQueriesException if no such category exists in the db
     */
    public String getAnnotationCategoryById(final Long annotationCategoryId) throws AnnotationQueriesException {
        try {
            Map<String, Object> row = getJdbcTemplate().queryForMap(CATEGORY_NAME_QUERY, annotationCategoryId);
            return String.valueOf(row.get("CATEGORY_DISPLAY_NAME"));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new AnnotationQueriesException("Annotation category with id " + annotationCategoryId + " does not exist");
        }
    }

    /**
     * Gets the name of an item type with the given id.  Throws an exception if no such id found.
     *
     * @param itemTypeId the item type id
     * @return the name of the item type
     * @throws AnnotationQueriesException if no such item type exists in the db
     */
    public DccAnnotationItemType getItemTypeById(final Long itemTypeId) throws AnnotationQueriesException {
        try {
            Map<String, Object> row = getJdbcTemplate().queryForMap(ITEM_TYPE_NAME_QUERY, itemTypeId);
            String typeName = String.valueOf(row.get("TYPE_DISPLAY_NAME"));
            DccAnnotationItemType itemType = new DccAnnotationItemType();
            itemType.setItemTypeId(itemTypeId);
            itemType.setItemTypeName(typeName);
            return itemType;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new AnnotationQueriesException("Item type with id " + itemTypeId + " does not exist");
        }
    }

    /**
     * Adds a new annotation item to an annotation.
     *
     * @param annotationId      the id of the annotation
     * @param dccAnnotationItem the <code>DccAnnotationItem</code> to add
     */
    @Secured("ROLE_ANNOTATION_CREATOR")
    @TriggersRemove(cacheName = "annotationsCache",
            keyGenerator = @KeyGenerator(
                    name = "HashCodeCacheKeyGenerator", properties = @Property(name = "includeMethod", value = "false")
            )
    )
    public void addNewAnnotationItem(@PartialCacheKey final Long annotationId, final DccAnnotationItem dccAnnotationItem) {

        getJdbcTemplate().update(INSERT_ANNOTATION_ITEM_QUERY,
                annotationId,
                dccAnnotationItem.getItemType().getItemTypeId(),
                dccAnnotationItem.getItem(),
                dccAnnotationItem.getDisease().getTumorId());
    }

    /**
     * Adds a new note to an annotation.
     *
     * @param annotationId the id of the annotation
     * @param note         the note to add
     */
    @TriggersRemove(cacheName = "annotationsCache",
            keyGenerator = @KeyGenerator(
                    name = "HashCodeCacheKeyGenerator", properties = @Property(name = "includeMethod", value = "false")
            )
    )
    @Secured("ROLE_ANNOTATION_CREATOR")
    public Long addNewAnnotationNote(@PartialCacheKey final Long annotationId, final DccAnnotationNote note)
            throws AnnotationQueriesException, BeanException {

        synchronized (lock) {

            final DccAnnotation dccAnnotation = getAnnotationById(annotationId);
            validateNote(note, dccAnnotation);

            long noteId = annotationNoteIdSequence.nextLongValue();
            getJdbcTemplate().update(INSERT_ANNOTATION_NOTE_QUERY,
                    noteId,
                    annotationId,
                    note.getNoteText(),
                    note.getAddedBy(),
                    note.getDateAdded()
            );

            note.setNoteId(noteId);

            return noteId;

        }
    }

    public void setAnnotationIdSequence(final DataFieldMaxValueIncrementer annotationIdSequence) {
        this.annotationIdSequence = annotationIdSequence;
    }

    public void setAnnotationNoteIdSequence(final DataFieldMaxValueIncrementer annotationNoteIdSequence) {
        this.annotationNoteIdSequence = annotationNoteIdSequence;
    }

    /**
     * Gets a list of item types that are defined in the database, sorted by type name.
     *
     * @return a list of DccAnnotationItemType objects
     */
    public List<DccAnnotationItemType> getItemTypes() {
        return getJdbcTemplate().query(ITEM_TYPES_QUERY,
                new ParameterizedRowMapper<DccAnnotationItemType>() {
                    public DccAnnotationItemType mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        final DccAnnotationItemType itemType = new DccAnnotationItemType();
                        itemType.setItemTypeId(resultSet.getLong("id"));
                        itemType.setItemTypeName(resultSet.getString("name"));
                        itemType.setItemTypeDescription(resultSet.getString("description"));
                        return itemType;
                    }
                });
    }

    /**
     * Gets a list of annotation categories that are defined in the database, sorted by category name.
     *
     * @return a list of DccAnnotationCategory objects
     */
    public List<DccAnnotationCategory> getAnnotationCategories() {
        // key is annotation cat id, val is the map of values: id, name, itemTypes
        final Map<Long, DccAnnotationCategory> annotationCategories = new HashMap<Long, DccAnnotationCategory>();
        getJdbcTemplate().query(CATEGORIES_QUERY, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                Long id = resultSet.getLong(CATEGORY_FIELD_ID);
                String name = resultSet.getString(CATEGORY_FIELD_NAME);
                Long itemTypeId = resultSet.getLong(CATEGORY_FIELD_ITEM_TYPES);
                DccAnnotationCategory annotationCat = annotationCategories.get(id);

                if (annotationCat == null) {
                    annotationCat = new DccAnnotationCategory();
                    annotationCategories.put(id, annotationCat);
                    annotationCat.setCategoryId(id);
                    annotationCat.setCategoryName(name);
                }
                final DccAnnotationClassification classification = new DccAnnotationClassification();
                classification.setAnnotationClassificationId(resultSet.getLong("annotation_classification_id"));
                classification.setAnnotationClassificationName(resultSet.getString("classification_display_name"));
                annotationCat.setAnnotationClassification(classification);

                DccAnnotationItemType itemType = new DccAnnotationItemType();
                itemType.setItemTypeId(itemTypeId);
                annotationCat.addItemType(itemType);
            }
        });

        List<DccAnnotationCategory> sortedAnnotationCategories = new ArrayList<DccAnnotationCategory>();
        sortedAnnotationCategories.addAll(annotationCategories.values());
        Collections.sort(sortedAnnotationCategories);  // DccAnnotationCategory implements Comparable
        return sortedAnnotationCategories;
    }

    /**
     * The returned List will contain Map object with the following keys: ID, NAME.
     *
     * @return a list of diseases as key/value pairs
     */
    public List<Tumor> getActiveDiseases() {
        return getJdbcTemplate().query(DISEASE_QUERY,
                new ParameterizedRowMapper<Tumor>() {
                    public Tumor mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        final Tumor tumor = new Tumor();
                        tumor.setTumorId(resultSet.getInt("ID"));
                        tumor.setTumorName(resultSet.getString("NAME"));
                        tumor.setTumorDescription(resultSet.getString("DESCRIPTION"));
                        return tumor;
                    }
                });
    }

    /**
     * Gets all classifications defined in the database.
     *
     * @return a list of classification beans
     */
    public List<DccAnnotationClassification> getAnnotationClassifications() {
        return getJdbcTemplate().query(CLASSIFICATIONS_QUERY,
                new ClassificationMapper());
    }

    @Override
    public String getAnnotationCategoryNameForId(final Long categoryId) {
        String category = null;
        try {
            category = getAnnotationCategoryById(categoryId);
        } catch (AnnotationQueriesException e) {
            // doesn't exist
        }
        return category;
    }

    /**
     * Gets the annotation given by the ID.
     *
     * @param annotationId the annotation id
     * @return a DccAnnotation object with all items and notes included
     * @throws AnnotationQueriesException if no such annotation id is found
     */
    @Cacheable(cacheName = "annotationsCache",
            keyGenerator = @KeyGenerator(
                    name = "HashCodeCacheKeyGenerator", properties = @Property(name = "includeMethod", value = "false")
            )
    )
    public DccAnnotation getAnnotationById(final Long annotationId) throws AnnotationQueriesException, BeanException {

        // multiple rows possible, because we will get one row per annotation item
        final List<Map<String, Object>> annotationData = getJdbcTemplate().queryForList(SELECT_ANNOTATION_BY_ID_QUERY, annotationId);

        if (annotationData.isEmpty()) {
            throw new AnnotationQueriesException("Annotation with id " + annotationId + " was not found");
        }

        final DccAnnotation annotation = new DccAnnotation();
        final Map<String, Object> firstRow = annotationData.get(0);
        annotation.setId(annotationId);

        final DccAnnotationCategory category = new DccAnnotationCategory();
        category.setCategoryId(Long.valueOf(firstRow.get("ANNOTATION_CATEGORY_ID").toString()));
        category.setCategoryName(firstRow.get("ANNOTATION_CATEGORY").toString());
        final DccAnnotationClassification classification = new DccAnnotationClassification();
        classification.setAnnotationClassificationName(firstRow.get("CLASSIFICATION_DISPLAY_NAME").toString());
        classification.setAnnotationClassificationId(Long.valueOf(firstRow.get("ANNOTATION_CLASSIFICATION_ID").toString()));
        category.setAnnotationClassification(classification);

        annotation.setAnnotationCategory(category);

        annotation.setCreatedBy(firstRow.get("CREATED_BY").toString());

        final java.sql.Timestamp createdDate = (java.sql.Timestamp) firstRow.get("CREATED_DATE");
        annotation.setDateCreated(new Date(createdDate.getTime()));

        annotation.setApproved(firstRow.get("CURATED").toString().equals("1"));

        annotation.setRescinded(firstRow.get("RESCINDED").toString().equals("1"));

        // Add each annotation item
        for (final Map<String, Object> resultRow : annotationData) {

            final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
            dccAnnotationItem.setItem((String) resultRow.get("ANNOTATION_ITEM"));
            dccAnnotationItem.setId(Long.valueOf(resultRow.get("ANNOTATION_ITEM_ID").toString()));
            final DccAnnotationItemType itemType = new DccAnnotationItemType();
            itemType.setItemTypeId(Long.valueOf(firstRow.get("ITEM_TYPE_ID").toString()));
            itemType.setItemTypeName(firstRow.get("ITEM_TYPE").toString());
            dccAnnotationItem.setItemType(itemType);

            final Tumor disease = new Tumor();
            disease.setTumorId(Integer.valueOf(resultRow.get("DISEASE_ID").toString()));
            disease.setTumorName(resultRow.get("DISEASE_ABBREVIATION").toString());
            disease.setTumorDescription(resultRow.get("DISEASE_NAME").toString());
            dccAnnotationItem.setDisease(disease);

            annotation.addItem(dccAnnotationItem);
        }

        // separate query for notes -- can just set the result as the note variable
        final List<DccAnnotationNote> notes = getJdbcTemplate().
                query(SELECT_ANNOTATION_NOTES_QUERY,
                        new ParameterizedRowMapper<DccAnnotationNote>() {
                            public DccAnnotationNote mapRow(final ResultSet resultSet, final int i) throws SQLException {
                                final DccAnnotationNote note = new DccAnnotationNote();
                                note.setNoteId(resultSet.getLong("id"));
                                note.setNoteText(resultSet.getString("note"));
                                note.setAddedBy(resultSet.getString("added_by"));
                                note.setDateAdded(resultSet.getTimestamp("date_added"));
                                // modified_by as edited_by, modified_date as date_edited
                                final String editedBy = resultSet.getString("edited_by");
                                if (!resultSet.wasNull()) {
                                    note.setEditedBy(editedBy);
                                }
                                final Timestamp editedDate = resultSet.getTimestamp("date_edited");
                                if (!resultSet.wasNull()) {
                                    note.setDateEdited(editedDate);
                                }
                                return note;
                            }
                        },
                        annotationId);
        annotation.setNotes(notes);

        return annotation;
    }

    /**
     * Edits the given note; sets the text, the editedBy field, and also the dateEdited field.
     *
     * @param note    the note to edit
     * @param newText the new text for the note
     * @param editor  the username of the person editing
     * @throws AnnotationQueriesException if the note was not found
     */
    @Secured({"ROLE_ANNOTATION_EDITOR", "ACL_ANNOTATION_NOTE_EDITOR"})
    @TriggersRemove(cacheName = "annotationsCache",
            keyGenerator = @KeyGenerator(
                    name = "HashCodeCacheKeyGenerator", properties = @Property(name = "includeMethod", value = "false")
            )
    )
    public void editAnnotationNote(@PartialCacheKey final Long annotationId, final DccAnnotationNote note, final String newText, final String editor)
            throws AnnotationQueriesException, BeanException {

        synchronized (lock) {

            // Set the note object to have the modified properties
            final Date now = new Date();
            note.setDateEdited(now);
            note.setEditedBy(editor);
            note.setNoteText(newText);

            // Validate the new note
            final DccAnnotation dccAnnotation = getAnnotationByNoteId(note.getNoteId());
            validateNote(note, dccAnnotation);

            // Persist the new note
            int rowsUpdated = getJdbcTemplate().update(EDIT_NOTE_QUERY,
                    newText, editor, now, note.getNoteId());

            // if number updated is zero, throw exception
            if (rowsUpdated == 0) {
                throw new AnnotationQueriesException("Note was not found in the database");
            }
        }
    }

    /**
     * Return the <code>DccAnnotation</code> that contains the <code>DccAnnotationNote</code> with the given id
     *
     * @param noteId the <code>DccAnnotationNote</code> id
     * @return the <code>DccAnnotation</code> that contains the <code>DccAnnotationNote</code> with the given id
     * @throws AnnotationQueriesException if the annotation for the note was not found
     */
    private DccAnnotation getAnnotationByNoteId(final Long noteId)
            throws AnnotationQueriesException, BeanException {

        final DccAnnotation result;

        try {
            final long annotationId = getJdbcTemplate().queryForLong(SELECT_ANNOTATION_ID_BY_NOTE_ID_QUERY, noteId);
            result = getAnnotationById(annotationId);

        } catch (final DataAccessException e) {
            throw new AnnotationQueriesException("Could not retrieve the annotation for the note with id '" + noteId + "': " + e.getMessage());
        }

        return result;
    }

    /**
     * Gets the note object for the given ID.
     *
     * @param noteId the note id
     * @return note with that id
     * @throws AnnotationQueriesException if the note was not found
     */
    public DccAnnotationNote getAnnotationNoteById(final Long noteId) throws AnnotationQueriesException {
        Map<String, Object> noteData;
        try {
            noteData = getJdbcTemplate().queryForMap(SELECT_NOTE_BY_ID_QUERY, noteId);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new AnnotationQueriesException("No note with id '" + noteId + "' was found in the database");
        }
        DccAnnotationNote note = new DccAnnotationNote();
        note.setNoteId(noteId);
        note.setNoteText((String) noteData.get("note"));
        note.setAddedBy((String) noteData.get("entered_by"));
        note.setAnnotationId(Long.valueOf(noteData.get("annotation_id").toString()));
        final java.sql.Timestamp addedDate = (java.sql.Timestamp) noteData.get("entered_date");
        note.setDateAdded(new Date(addedDate.getTime()));
        if (noteData.get("modified_by") != null) {
            note.setEditedBy((String) noteData.get("modified_by"));
            final java.sql.Timestamp editedDate = (java.sql.Timestamp) noteData.get("modified_date");
            note.setDateEdited(new Date(editedDate.getTime()));
        }

        return note;
    }

    public List<DccAnnotation> searchAnnotations(final AnnotationSearchCriteria searchCriteria) {
        List<Long> matchingIds = findMatchingAnnotationIds(searchCriteria);
        return getAnnotations(matchingIds);
    }

    /**
     * Search for annotations on the given item and/or category.  Null values are ignored (not searched on).
     *
     * @param searchCriteria the search criteria
     * @return a list of annotations, sorted by date added (most recent first) -- will be empty if no matches
     */
    public List<Long> findMatchingAnnotationIds(final AnnotationSearchCriteria searchCriteria) {
        final List<Object> params = new ArrayList<Object>();
        String query = searchCriteria.buildQuery(params);

        return getJdbcTemplate().query(
                query,
                new ParameterizedRowMapper<Long>() {
                    public Long mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        return resultSet.getLong("annotation_id");
                    }
                }, params.toArray());
    }

    /**
     * Search for annotations count on the given item and/or category.  Null values are ignored (not searched on).
     *
     * @param searchCriteria the search criteria
     * @return a list of annotations, sorted by date added (most recent first) -- will be empty if no matches
     */
    public Long getCountForMatchingAnnotationIds(final AnnotationSearchCriteria searchCriteria) {
        final List<Object> params = new ArrayList<Object>();
        final String queryCount = searchCriteria.buildQuery(params).replace("select distinct annotation.annotation_id",
                "select count(distinct annotation.annotation_id)");
        return getJdbcTemplate().queryForLong(queryCount, params.toArray());
    }

    @Override
    /**
     * This method returns all annotations in the database regardless of user or status. Hence no security privileges
     * are checked for it.
     * @return a list of annotations, sorted by date added (most recent first) -- will be empty if no matches
     */
    public List<Long> getAllAnnotationIds() {
        final String query = "select distinct annotation.annotation_id from annotation";
        return getJdbcTemplate().query(
                query,
                new ParameterizedRowMapper<Long>() {
                    @Override
                    public Long mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        return resultSet.getLong("annotation_id");
                    }
                }
        );
    }

    @Override
    public List<DccAnnotation> getAllAnnotationsForSamples(List<String> samples) {
        List<DccAnnotation> result = new ArrayList<DccAnnotation>();
        if (samples != null) {
            final List<String> normalizedSamples = StringUtil.normalize(samples);
            if (normalizedSamples.size() > 0) {
                final StringBuilder itemSearch = new StringBuilder();
                final Iterator<String> normalizedSamplesIterator = normalizedSamples.iterator();
                while (normalizedSamplesIterator.hasNext()) {
                    itemSearch.append(normalizedSamplesIterator.next());
                    if (normalizedSamplesIterator.hasNext()) {
                        itemSearch.append(AnnotationSearchCriteria.ANNOTATION_ITEM_SEPARATOR);
                    }
                }
                final AnnotationSearchCriteria annotationSearchCriteria = new AnnotationSearchCriteria();
                annotationSearchCriteria.setItem(itemSearch.toString());
                annotationSearchCriteria.setCurated(true);
                annotationSearchCriteria.setIncludeRescinded(true);

                final List<Long> matchingIds = findMatchingAnnotationIds(annotationSearchCriteria);
                result = getAnnotations(matchingIds);
            }
        }
        return result;
    }

    @Override
    public Long getAllAnnotationsCountForSamples(List<String> samples) {
        Long result = 0L;
        if (samples != null) {
            final List<String> normalizedSamples = StringUtil.normalize(samples);
            if (normalizedSamples.size() > 0) {
                final StringBuilder itemSearch = new StringBuilder();
                final Iterator<String> normalizedSamplesIterator = normalizedSamples.iterator();
                while (normalizedSamplesIterator.hasNext()) {
                    itemSearch.append(normalizedSamplesIterator.next());
                    if (normalizedSamplesIterator.hasNext()) {
                        itemSearch.append(AnnotationSearchCriteria.ANNOTATION_ITEM_SEPARATOR);
                    }
                }
                final AnnotationSearchCriteria annotationSearchCriteria = new AnnotationSearchCriteria();
                annotationSearchCriteria.setItem(itemSearch.toString());
                annotationSearchCriteria.setCurated(true);
                annotationSearchCriteria.setIncludeRescinded(true);

                result = getCountForMatchingAnnotationIds(annotationSearchCriteria);
            }
        }
        return result;
    }

    @Override
    public void setCurated(final DccAnnotation annotation, final boolean isCurated)
            throws AnnotationQueriesException, BeanException {
        annotation.isValidStatusUpdate(isCurated, annotation.getRescinded()); //this has to be valid before the state can be changed
        try {
            getJdbcTemplate().update(UPDATE_CURATED_QUERY, isCurated ? 1 : 0, annotation.getId());
        } catch (DataAccessException e) {
            throw new AnnotationQueriesException("Failed to update curated status", e);
        }
        annotation.setApproved(isCurated);
    }

    @Override
    public void setRescinded(final DccAnnotation annotation, final boolean isRescinded)
            throws AnnotationQueriesException, BeanException {
        annotation.isValidStatusUpdate(annotation.getApproved(), isRescinded); //this has to be valid before the state can be changed
        try {
            getJdbcTemplate().update(UPDATE_RESCINDED_QUERY, isRescinded ? 1 : 0, annotation.getId());
        } catch (DataAccessException e) {
            throw new AnnotationQueriesException("Failed to update rescinded status", e);
        }
        annotation.setRescinded(isRescinded);
    }

    /**
     * updates the Annotation record for the given DccAnnotation
     *
     * @param annotationId            the annotation ID
     * @param annotation              the DccAnnotation to update
     * @param useStrictItemValidation whether to check the item barcode parts or not
     * @throws AnnotationQueriesException
     */
    @Secured({"ROLE_ANNOTATION_EDITOR"})
    @TriggersRemove(cacheName = "annotationsCache",
            keyGenerator = @KeyGenerator(
                    name = "HashCodeCacheKeyGenerator", properties = @Property(name = "includeMethod", value = "false")
            )
    )
    public void updateAnnotation(@PartialCacheKey final long annotationId, final DccAnnotation annotation, final boolean useStrictItemValidation) throws AnnotationQueriesException {
        synchronized (lock) {
            final boolean includeNote = false;
            // if this fails it will throw an exception, which is why there is no return value
            validateAnnotation(annotation, useStrictItemValidation, includeNote);

            // update the Items first
            for (final DccAnnotationItem item : annotation.getItems()) {
                updateAnnotationItem(item);
            }
            // now update the annotation record
            getJdbcTemplate().update(UPDATE_ANNOTATION, annotation.getAnnotationCategory().getCategoryId(),
                    annotation.getUpdatedBy(), annotation.getDateUpdated(), annotation.getApproved() ? 1 : 0, annotation.getRescinded() ? 1 : 0,
                    annotation.getId());
        }
    }

    @Override
    @Secured({"ROLE_ANNOTATION_CREATOR"})
    public void updateAnnotation(final DccAnnotation annotation) throws AnnotationQueriesException {

        final boolean useStrictItemValidation = false;
        updateAnnotation(annotation.getId(), annotation, useStrictItemValidation);
    }

    /**
     * updates the AnnotationItem record for the given DccAnnotationItem and AnnotationId
     *
     * @param item the AnnotationItem to update
     */
    private void updateAnnotationItem(final DccAnnotationItem item) {
        getJdbcTemplate().update(UPDATE_ANNOTATION_ITEM, item.getItemType().getItemTypeId(), item.getItem(),
                item.getDisease().getTumorId(), item.getId());
    }

    /**
     * Retrieves the <code>DccAnnotation</code> associated with the List of annotation ids passed in
     *
     * @param annotationIds the list of annotation IDs for wich to get the DccAnnotation beans
     * @return List of <code>DccAnnotation</code> sorted by date added (most recent first)
     */
    private List<DccAnnotation> getAnnotations(final List<Long> annotationIds) {
        final List<DccAnnotation> annotations = new ArrayList<DccAnnotation>();
        for (final Long id : annotationIds) {
            try {
                annotations.add(getAnnotationById(id));
            } catch (AnnotationQueriesException e) {
                // the only reason this would happen is if between the above search and now, the annotation was deleted from the db.
                // in that case, just exclude it from the results so no need to handle the exception
            } catch (BeanException be) {
                // This can only happen if the annotation in question is not in a valid bean state
                // Exclude it from the results.
                // @TODO : Log it?
            }
        }

        // sort by date created, descending
        Collections.sort(annotations, new Comparator<DccAnnotation>() {
            public int compare(final DccAnnotation a1, final DccAnnotation a2) {
                return a2.getDateCreated().compareTo(a1.getDateCreated());
            }
        });
        return annotations;
    }

    /**
     * Validate the given <code>DccAnnotation</code>
     *
     * @param annotation              the <code>DccAnnotation</code> to validate
     * @param useStrictItemValidation whether to validate the annotation's item against the database values or not
     * @throws AnnotationQueriesException if the validation failed
     */
    private void validateNewAnnotation(final DccAnnotation annotation, final boolean useStrictItemValidation) throws AnnotationQueriesException {
        final boolean includeNote = true;
        validateAnnotation(annotation, useStrictItemValidation, includeNote);
    }

    /**
     * Validate the given <code>DccAnnotation</code>
     *
     * @param annotation              the <code>DccAnnotation</code> to validate
     * @param useStrictItemValidation whether to validate the annotation's item against the database values or not
     * @param includeNote             whether to include the validation of the annotation notes
     * @throws AnnotationQueriesException if the validation failed
     */
    private void validateAnnotation(final DccAnnotation annotation, final boolean useStrictItemValidation, final boolean includeNote) throws AnnotationQueriesException {

        // 1. check that annotation category is valid
        final String annotationCategoryName = getAnnotationCategoryById(annotation.getAnnotationCategory().getCategoryId()); // will throw exception if not found
        annotation.getAnnotationCategory().setCategoryName(annotationCategoryName);

        annotation.getAnnotationCategory().setAnnotationClassification(getClassificationForCategory(annotation.getAnnotationCategory()));

        // 2. check items not blank
        if (annotation.getItems() == null || annotation.getItems().isEmpty()) {
            throw new AnnotationQueriesException("Item to annotate must be given");
        }

        // For each annotation item
        for (final DccAnnotationItem dccAnnotationItem : annotation.getItems()) {

            // 3. check that item type id is valid
            final DccAnnotationItemType dccAnnotationItemType = getItemTypeById(dccAnnotationItem.getItemType().getItemTypeId()); // will throw exception if not found
            dccAnnotationItem.setItemType(dccAnnotationItemType);

            // 4. check that annotation category is allowed with this item type
            if (!checkAnnotationCategoryItemValidity(dccAnnotationItemType.getItemTypeId(), annotation.getAnnotationCategory().getCategoryId())) {
                throw new AnnotationQueriesException("Annotation category '" + annotationCategoryName + "' is not valid for an annotation on an item of type '" + dccAnnotationItemType.getItemTypeName() + "'");
            }

            // 5. verify the disease exists
            if (dccAnnotationItem.getDisease() == null || dccAnnotationItem.getDisease().getTumorId() == null) {
                throw new AnnotationQueriesException("Disease ID for must be specified for dccAnnotationItem " + dccAnnotationItem.getItem() + " in new annotation");
            }

            final Tumor tumor = tumorQueries.getTumorForId(dccAnnotationItem.getDisease().getTumorId());
            if (tumor == null) {
                throw new AnnotationQueriesException("Disease ID '" + dccAnnotationItem.getDisease().getTumorId() + "' is not valid");
            }

            dccAnnotationItem.setDisease(tumor);

            // 6. Item validation
            final String item = dccAnnotationItem.getItem();
            final String itemTypeName = dccAnnotationItem.getItemType().getItemTypeName();
            String itemValidationErrorMessage = null;

            if (useStrictItemValidation) {
                itemValidationErrorMessage = getCommonBarcodeAndUUIDValidator().validateBarcodeFormatAndCodes(item, null, itemTypeName);
                if (itemValidationErrorMessage == null) {
                    if (uuidService.getUUIDForBarcode(item) == null) {
                        itemValidationErrorMessage = "The barcode: " + item + " entered has not been submitted to the BCR as of yet.";
                    }
                }

                if (itemValidationErrorMessage != null) {
                    itemValidationErrorMessage += "\n\nIf codes or the barcode is missing in the database, change the item validation to 'Relaxed' if you want to submit the annotation anyway.";
                }
            } else {
                itemValidationErrorMessage = getCommonBarcodeAndUUIDValidator().validateBarcodeFormat(item, null, itemTypeName);
            }

            if (itemValidationErrorMessage != null) {
                throw new AnnotationQueriesException(itemValidationErrorMessage);
            }
        }
        if (includeNote) {

            if (!annotation.isNotesEmpty()) {
                // 7. Validate notes
                for (final DccAnnotationNote note : annotation.getNotes()) {
                    validateNote(note, annotation);
                }
            }
        }
    }

    private DccAnnotationClassification getClassificationForCategory(final DccAnnotationCategory annotationCategory) {
        return getJdbcTemplate().queryForObject(CLASSIFICATION_FOR_CATEGORY_QUERY,
                new ClassificationMapper(), annotationCategory.getCategoryId());
    }

    private class ClassificationMapper implements ParameterizedRowMapper<DccAnnotationClassification> {
        @Override
        public DccAnnotationClassification mapRow(final ResultSet resultSet, final int resultNum) throws SQLException {
            final DccAnnotationClassification classification = new DccAnnotationClassification();
            classification.setAnnotationClassificationId(resultSet.getLong("annotation_classification_id"));
            classification.setAnnotationClassificationName(resultSet.getString("classification_display_name"));
            return classification;
        }
    }

    private void validateNote(final DccAnnotationNote note, final DccAnnotation annotation) throws AnnotationQueriesException {

        if (note.getNoteText() != null && note.getNoteText().trim().length() > 0) {
            // username can't be empty
            if (note.getAddedBy() == null || note.getAddedBy().trim().length() == 0) {
                throw new AnnotationQueriesException("Note author must be given");
            }

            if (note.getDateAdded() == null) {
                note.setDateAdded(new Date());
            }

            // note length has to be 4000 or less because of the column size
            if (note.getNoteText().length() > 4000) {
                throw new AnnotationQueriesException("Note text must be 4000 characters or fewer");
            }

            // Make sure the note does not already exist in the DB
            final long categoryId = annotation.getAnnotationCategory().getCategoryId();
            for (final DccAnnotationItem dccAnnotationItem : annotation.getItems()) {

                final int tumorId = dccAnnotationItem.getDisease().getTumorId();
                final long itemTypeId = dccAnnotationItem.getItemType().getItemTypeId();

                if (exist(tumorId, itemTypeId, dccAnnotationItem.getItem(), categoryId, note.getNoteText())) {

                    final Object[] args = new Object[]{
                            dccAnnotationItem.getDisease().getTumorName(),
                            dccAnnotationItem.getItemType().getItemTypeName(),
                            dccAnnotationItem.getItem(),
                            annotation.getAnnotationCategory().getCategoryName(),
                            note.getNoteText()
                    };

                    final String message = MessageFormat.format(NON_UNIQUE_ANNOTATION_EXCEPTION_MESSAGE, args);

                    throw new AnnotationQueriesException(message);
                }
            }
        }
    }

    private boolean checkAnnotationCategoryItemValidity(final long itemTypeId, final long annotationCategoryId) {
        int count = getJdbcTemplate().queryForInt(CATEGORY_ITEM_TYPE_QUERY, itemTypeId, annotationCategoryId);
        return count > 0;
    }

    /**
     * Return <code>true</code> if the given annotation already exists in the database, <code>false</code> otherwise
     * <p/>
     * Here, the annotation is given as a combination of:
     * <p/>
     * - a disease
     * - an item type
     * - an item identifier
     * - an annotation category
     * - a note text
     *
     * @param tumorId        the tumor id
     * @param itemTypeId     the item type id
     * @param itemIdentifier the item identifier
     * @param categoryId     the category id
     * @param noteText       the note text
     * @return <code>true</code> if the given annotation already exists in the database, <code>false</code> otherwise
     */
    private boolean exist(final Integer tumorId,
                          final Long itemTypeId,
                          final String itemIdentifier,
                          final Long categoryId,
                          final String noteText) {

        final Object[] args = new Object[]{
                noteText,
                categoryId,
                itemTypeId,
                itemIdentifier,
                tumorId
        };

        final int sameAnnotationCount = getJdbcTemplate().queryForInt(SAME_ANNOTATION_COUNT_QUERY, args);

        return sameAnnotationCount > 0;
    }

    /*
     * Getter / Setter
     */

    public void setCommonBarcodeAndUUIDValidator(final CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator) {
        this.commonBarcodeAndUUIDValidator = commonBarcodeAndUUIDValidator;
    }

    public CommonBarcodeAndUUIDValidator getCommonBarcodeAndUUIDValidator() {
        return commonBarcodeAndUUIDValidator;
    }
}
