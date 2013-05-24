/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAOImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.CenterQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.CodeTableQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.TumorQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.impl.SecurityUtilImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDServiceImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import oracle.jdbc.pool.OracleDataSource;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * DB Unit test for AnnotationQueriesJDBCImpl
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AnnotationQueriesJDBCImplDbUnitSlowTest extends DBUnitTestCase {

    private static String SAMPLE_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static String DAO_DIR = "dao" + File.separator;

    private final static String VALID_ALIQUOT_BARCODE = "TCGA-06-0939-01A-89D-0080-08";
    private final static String INVALID_ALIQUOT_BARCODE_NO_EXIST_DB = "TCGA-03-0209-99A-01R-0231-02";

    private AnnotationQueriesJDBCImpl queries;
    private DccAnnotation annotation;

    private final CommonBarcodeAndUUIDValidator mockCommonBarcodeAndUUIDValidator = new CommonBarcodeAndUUIDValidatorImpl() {
        @Override
        public String validateBarcodeFormatAndCodes(final String input, final String fileName, final String expectedBarcodeType) {
            return null;
        }

        @Override
        public String validateBarcodeFormat(final String input, final String fileName, final String expectedBarcodeType) {
            return null;
        }
    };

    public AnnotationQueriesJDBCImplDbUnitSlowTest() {
        super(SAMPLE_DIR, DAO_DIR + "AnnotationsTestData.xml", "unittest.properties");
    }

    private static OracleDataSource ods;

    @Override
    protected void initDataSource() throws SQLException {
        if (ods == null) {
            ods = new OracleDataSource();
            ods.setUser(userName);
            ods.setPassword(password);
            ods.setURL(connectionURL);
            ods.setConnectionCachingEnabled(true);
        }
        dataSource = ods;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final TumorQueriesJDBCImpl tumorQueries = new TumorQueriesJDBCImpl();
        tumorQueries.setDataSource(getDataSource());

        final CodeTableQueriesJDBCImpl codeTableQueriesJDBC = new CodeTableQueriesJDBCImpl();
        codeTableQueriesJDBC.setDataSource(getDataSource());

        final CommonBarcodeAndUUIDValidatorImpl commonBarcodeAndUUIDValidator = new CommonBarcodeAndUUIDValidatorImpl();
        commonBarcodeAndUUIDValidator.setCodeTableQueries(codeTableQueriesJDBC);

        final UUIDDAOImpl uuidDao = new UUIDDAOImpl();
        uuidDao.setDataSource(getDataSource());

        final CenterQueriesJDBCImpl centerQueries = new CenterQueriesJDBCImpl();
        centerQueries.setDataSource(getDataSource());

        final UUIDServiceImpl uuidService = new UUIDServiceImpl();
        uuidService.setUuidDAO(uuidDao);
        uuidService.setCenterQueries(centerQueries);
        uuidService.setTumorQueries(tumorQueries);

        queries = new AnnotationQueriesJDBCImpl();
        queries.setDataSource(getDataSource());
        queries.setAnnotationIdSequence(new OracleSequenceMaxValueIncrementer(getDataSource(), "ANNOTATION_SEQ"));
        queries.setAnnotationNoteIdSequence(new OracleSequenceMaxValueIncrementer(getDataSource(), "ANNOTATION_NOTE_SEQ"));
        queries.setTumorQueries(tumorQueries);
        queries.setCommonBarcodeAndUUIDValidator(commonBarcodeAndUUIDValidator);
        queries.setUuidService(uuidService);

        annotation = new DccAnnotation();
        final DccAnnotationCategory cat = new DccAnnotationCategory();
        cat.setCategoryId(1L);
        final DccAnnotationClassification classification = new DccAnnotationClassification();
        classification.setAnnotationClassificationId(1L);
        cat.setAnnotationClassification(classification);
        annotation.setAnnotationCategory(cat);
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeId(1L);

        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
        dccAnnotationItem.setItemType(itemType);
        dccAnnotationItem.setItem(VALID_ALIQUOT_BARCODE);

        final Tumor tumor = new Tumor();
        tumor.setTumorId(1);
        dccAnnotationItem.setDisease(tumor);

        annotation.addItem(dccAnnotationItem);

        annotation.setCreatedBy("bozo");
        final Date now = new Date();
        annotation.setDateCreated(now);
        annotation.addNote("hello", "bozo", now);
        annotation.setRescinded(false);
    }

    @Override
    public void tearDown() throws Exception {

        deleteAnnotations();
        super.tearDown();
    }

    @Test
    public void testAddNewAnnotation() throws Exception {

        long id = queries.addNewAnnotation(annotation);
        assertTrue(id > 0);

        final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);
        assertEquals("Aliquot", dccAnnotationItem.getItemType().getItemTypeName());
        assertEquals("Annotation Category 1", annotation.getAnnotationCategory().getCategoryName());
        assertEquals("Notification", annotation.getAnnotationCategory().getAnnotationClassification().getAnnotationClassificationName());

        // do selects directly to look for inserted things
        final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        final int numAnnotations = jdbcTemplate.queryForInt("select count(*) from annotation where annotation_id=?", id);
        assertEquals(1, numAnnotations);
        assertEquals(1, jdbcTemplate.queryForInt("select annotation_category_id from annotation where annotation_id=?", id));

        // look for annotation note
        assertEquals(1, jdbcTemplate.queryForInt("select count(*) from annotation_note where annotation_id=?", id));
        final String noteText = jdbcTemplate.queryForList("select note from annotation_note where annotation_id=?", id).get(0).get("NOTE").toString();
        assertEquals("hello", noteText);

        // for for annotation item
        assertEquals(1, jdbcTemplate.queryForInt("select count(*) from annotation_item where annotation_id=?", id));
        final String itemIdentifier = jdbcTemplate.queryForList("select annotation_item from annotation_item where annotation_id=?", id).get(0).get("ANNOTATION_ITEM").toString();
        assertEquals(VALID_ALIQUOT_BARCODE, itemIdentifier);
        assertEquals(1, jdbcTemplate.queryForInt("select disease_id from annotation_item where annotation_id=?", id));

        // check that the Tumor object has the name and description
        assertEquals("DIS1", dccAnnotationItem.getDisease().getTumorName());
        assertEquals("Disease 1", dccAnnotationItem.getDisease().getTumorDescription());
    }

    @Test
    public void testAddNewAnnotationStrictItemValidation() throws BeanException {

        try {
            final boolean useStrictItemValidation = true;
            queries.addNewAnnotation(annotation, useStrictItemValidation);
            fail("AnnotationQueriesException was not raised");

        } catch (final AnnotationQueries.AnnotationQueriesException e) {

            final String expectedErrorMessage = "The Aliquot barcode 'TCGA-06-0939-01A-89D-0080-08' has failed validation due to following errors :\n" +
                    "The project code 'TCGA' in the barcode does not exist in database\n" +
                    "The tissue source site '06' in the barcode does not exist in database\n" +
                    "The sample Type '01' in the barcode does not exist in database\n" +
                    "The portion analyte 'D' in the barcode does not exist in database\n" +
                    "The bcr Center '08' in the barcode does not exist in database\n" +
                    "\n\n" +
                    "If codes or the barcode is missing in the database, change the item validation to 'Relaxed' if you want to submit the annotation anyway.";

            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void testAddNewAnnotationNotExistDbStrictItemValidation() throws BeanException {
        try {
            queries.setCommonBarcodeAndUUIDValidator(mockCommonBarcodeAndUUIDValidator);
            final boolean useStrictItemValidation = true;
            final DccAnnotation annotation = new DccAnnotation();
            final DccAnnotationCategory cat = new DccAnnotationCategory();
            cat.setCategoryId(1L);
            annotation.setAnnotationCategory(cat);

            final DccAnnotationItemType itemType = new DccAnnotationItemType();
            itemType.setItemTypeId(1L);
            final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
            dccAnnotationItem.setItemType(itemType);
            dccAnnotationItem.setItem(INVALID_ALIQUOT_BARCODE_NO_EXIST_DB);
            final Tumor tumor = new Tumor();
            tumor.setTumorId(1);
            dccAnnotationItem.setDisease(tumor);
            annotation.addItem(dccAnnotationItem);

            queries.addNewAnnotation(annotation, useStrictItemValidation);
        } catch (final AnnotationQueries.AnnotationQueriesException e) {
            final String expetcedErrorMessage = "The barcode: " + INVALID_ALIQUOT_BARCODE_NO_EXIST_DB + " entered has not been submitted to the BCR as of yet." +
                    "\n\n" +
                    "If codes or the barcode is missing in the database, change the item validation to 'Relaxed' if you want to submit the annotation anyway.";
            assertEquals(expetcedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void testAddNewAnnotationNotExistDbRelaxedItemValidation() throws BeanException {
        try {
            queries.setCommonBarcodeAndUUIDValidator(mockCommonBarcodeAndUUIDValidator);
            final DccAnnotation annotation = new DccAnnotation();
            final DccAnnotationCategory cat = new DccAnnotationCategory();
            cat.setCategoryId(1L);
            annotation.setAnnotationCategory(cat);

            final DccAnnotationItemType itemType = new DccAnnotationItemType();
            itemType.setItemTypeId(1L);
            final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
            dccAnnotationItem.setItemType(itemType);
            dccAnnotationItem.setItem(INVALID_ALIQUOT_BARCODE_NO_EXIST_DB);
            final Tumor tumor = new Tumor();
            tumor.setTumorId(1);
            dccAnnotationItem.setDisease(tumor);
            annotation.addItem(dccAnnotationItem);

            annotation.setCreatedBy("bozo");
            final Date now = new Date();
            annotation.setDateCreated(now);
            annotation.addNote("hello", "bozo", now);

            queries.addNewAnnotation(annotation);
        } catch (final AnnotationQueries.AnnotationQueriesException e) {
            fail();
        }
    }

    @Test
    public void testAddNewAnnotationWithoutNote() throws Exception {

        annotation.getNotes().clear();
        long id = queries.addNewAnnotation(annotation);
        assertTrue(id > 0);

        final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);
        assertEquals("Aliquot", dccAnnotationItem.getItemType().getItemTypeName());
        assertEquals("Annotation Category 1", annotation.getAnnotationCategory().getCategoryName());
        assertEquals("Notification", annotation.getAnnotationCategory().getAnnotationClassification().getAnnotationClassificationName());

        // do selects directly to look for inserted things
        final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        final int numAnnotations = jdbcTemplate.queryForInt("select count(*) from annotation where annotation_id=?", id);
        assertEquals(1, numAnnotations);
        assertEquals(1, jdbcTemplate.queryForInt("select annotation_category_id from annotation where annotation_id=?", id));

        // look for annotation note
        assertEquals(0, jdbcTemplate.queryForInt("select count(*) from annotation_note where annotation_id=?", id));

        // for for annotation item
        assertEquals(1, jdbcTemplate.queryForInt("select count(*) from annotation_item where annotation_id=?", id));
        final String itemIdentifier = jdbcTemplate.queryForList("select annotation_item from annotation_item where annotation_id=?", id).get(0).get("ANNOTATION_ITEM").toString();
        assertEquals(VALID_ALIQUOT_BARCODE, itemIdentifier);
        assertEquals(1, jdbcTemplate.queryForInt("select disease_id from annotation_item where annotation_id=?", id));

        // check that the Tumor object has the name and description
        assertEquals("DIS1", dccAnnotationItem.getDisease().getTumorName());
        assertEquals("Disease 1", dccAnnotationItem.getDisease().getTumorDescription());
    }

    @Test
    public void testAddAnnotationMissingDisease() throws BeanException {

        final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);

        dccAnnotationItem.getDisease().setTumorId(null);
        try {
            queries.addNewAnnotation(annotation);
            fail("Exception wasn't thrown when disease ID not set");
        } catch (final AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("Disease ID for must be specified for dccAnnotationItem " + VALID_ALIQUOT_BARCODE + " in new annotation", e.getMessage());
        }
    }

    @Test
    public void testAddAnnotationBadDisease() throws BeanException {

        final int tumorId = 1234567;
        final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);
        dccAnnotationItem.getDisease().setTumorId(tumorId);
        try {
            queries.addNewAnnotation(annotation);
            fail("Exception wasn't thrown when disease ID invalid");
        } catch (AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("Disease ID '" + tumorId + "' is not valid", e.getMessage());
        }
    }

    @Test
    public void testAddAnnotationMissingItem() throws BeanException {

        annotation.getItems().clear();
        try {
            queries.addNewAnnotation(annotation);
            fail("Exception wasn't thrown");
        } catch (AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("Item to annotate must be given", e.getMessage());
        }
    }

    @Test
    public void testAddAnnotationInvalidItemType() throws BeanException {

        final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);
        dccAnnotationItem.getItemType().setItemTypeId(-1L);
        try {
            queries.addNewAnnotation(annotation);
            fail("Exception wasn't thrown");
        } catch (AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("Item type with id -1 does not exist", e.getMessage());
        }
    }

    @Test
    public void testAddAnnotationNoteTooLong() throws BeanException {

        final StringBuilder longNote = new StringBuilder();
        for (int i = 0; i < 401; i++) {
            longNote.append("abcdefghij");
        }
        assertTrue(longNote.length() > 4000); // just to make sure that basic math hasn't changed... beware of running this code in alternate universes
        annotation.getNotes().get(0).setNoteText(longNote.toString());
        try {
            queries.addNewAnnotation(annotation);
            fail("Exception was not thrown");
        } catch (AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("Note text must be 4000 characters or fewer", e.getMessage());
        }
    }

    @Test
    public void testAddAnnotationInvalidCategory() throws BeanException {

        annotation.getAnnotationCategory().setCategoryId(-10L);
        try {
            queries.addNewAnnotation(annotation);
            fail("Exception wasn't thrown");
        } catch (AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("Annotation category with id -10 does not exist", e.getMessage());
        }
    }

    @Test
    public void testInvalidCategoryAndTypeCombo() throws BeanException {

        final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);

        annotation.getAnnotationCategory().setCategoryId(1L);
        dccAnnotationItem.getItemType().setItemTypeId(2L);
        try {
            queries.addNewAnnotation(annotation);
            fail("Exception wasn't thrown");
        } catch (AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("Annotation category 'Annotation Category 1' is not valid for an annotation on an item of type 'Item Type 2'", e.getMessage());
        }
    }

    @Test
    public void testAddNewAnnotationAlreadyExist() throws BeanException {

        try {
            setupExistingAnnotation();
            queries.addNewAnnotation(annotation);
            fail("Expected AnnotationQueriesException was not raised");

        } catch (final AnnotationQueries.AnnotationQueriesException e) {
            checkErrorMessage(e);
        }
    }

    @Test
    public void testGetItemTypes() {

        final List<DccAnnotationItemType> itemTypes = queries.getItemTypes();
        assertEquals(2, itemTypes.size());
        assertEquals(new Long(1), itemTypes.get(0).getItemTypeId());
        assertEquals("Aliquot", itemTypes.get(0).getItemTypeName());
        assertEquals(new Long(2), itemTypes.get(1).getItemTypeId());
        assertEquals("Item Type 2", itemTypes.get(1).getItemTypeName());
    }

    @Test
    public void testGetAnnotationCategories() {

        final List<DccAnnotationCategory> annotationTypes = queries.getAnnotationCategories();
        assertEquals(3, annotationTypes.size());
        assertEquals(new Long(1), annotationTypes.get(0).getCategoryId());
        assertEquals("Annotation Category 1", annotationTypes.get(0).getCategoryName());
        assertEquals(new Long(2), annotationTypes.get(1).getCategoryId());
        assertEquals("Annotation Category 2", annotationTypes.get(1).getCategoryName());
        assertEquals(new Long(1), annotationTypes.get(0).getItemTypes().get(0).getItemTypeId());
        assertEquals(2, annotationTypes.get(1).getItemTypes().size());
        assertEquals(new Long(1), annotationTypes.get(1).getItemTypes().get(0).getItemTypeId());
        assertEquals(new Long(2), annotationTypes.get(1).getItemTypes().get(1).getItemTypeId());
        assertEquals("Annotation Category 3", annotationTypes.get(2).getCategoryName());

        assertEquals("Notification", annotationTypes.get(0).getAnnotationClassification().getAnnotationClassificationName());
        assertEquals("Notification", annotationTypes.get(1).getAnnotationClassification().getAnnotationClassificationName());
        assertEquals("Awesome", annotationTypes.get(2).getAnnotationClassification().getAnnotationClassificationName());
    }

    @Test
    public void testGetAnnotationClassifications() {
        final List<DccAnnotationClassification> classifications = queries.getAnnotationClassifications();
        assertEquals(3, classifications.size());
        assertEquals("Awesome", classifications.get(0).getAnnotationClassificationName());
        assertEquals(new Long(3), classifications.get(0).getAnnotationClassificationId());
        assertEquals("Notification", classifications.get(1).getAnnotationClassificationName());
        assertEquals(new Long(1), classifications.get(1).getAnnotationClassificationId());
        assertEquals("Observation", classifications.get(2).getAnnotationClassificationName());
        assertEquals(new Long(2), classifications.get(2).getAnnotationClassificationId());
    }

    @Test
    public void testGetDiseases() {

        final List<Tumor> diseases = queries.getActiveDiseases();
        assertEquals(3, diseases.size());
        assertEquals(new Integer(1), diseases.get(0).getTumorId());
        assertEquals("Disease 1", diseases.get(0).getTumorDescription());
        assertEquals("DIS1", diseases.get(0).getTumorName());

        assertEquals(new Integer(2), diseases.get(1).getTumorId());
        assertEquals("Disease 2", diseases.get(1).getTumorDescription());
        assertEquals("DIS2", diseases.get(1).getTumorName());

        assertEquals(new Integer(3), diseases.get(2).getTumorId());
        assertEquals("Disease 3", diseases.get(2).getTumorDescription());
        assertEquals("DIS3", diseases.get(2).getTumorName());
    }

    @Test
    public void testGetAnnotationById() throws AnnotationQueries.AnnotationQueriesException, BeanException {

        DccAnnotation annotation = queries.getAnnotationById(100L);
        assertNotNull(annotation);

        final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);
        assertEquals(new Long(1), dccAnnotationItem.getItemType().getItemTypeId());
        assertEquals(new Long(1), annotation.getAnnotationCategory().getCategoryId());
        assertEquals("a chipmunk!", annotation.getCreatedBy());
        assertEquals(1, annotation.getItems().size());
        assertEquals(VALID_ALIQUOT_BARCODE, dccAnnotationItem.getItem());
        assertEquals(1, annotation.getNotes().size());
        assertEquals("these are my favorite food", annotation.getNotes().get(0).getNoteText());
        assertEquals("a squirrel", annotation.getNotes().get(0).getAddedBy());
        assertEquals("Aliquot", dccAnnotationItem.getItemType().getItemTypeName());
        assertEquals("Annotation Category 1", annotation.getAnnotationCategory().getCategoryName());
        assertNotNull(annotation.getNotes().get(0).getDateAdded());
        assertEquals("Notification", annotation.getAnnotationCategory().getAnnotationClassification().getAnnotationClassificationName());

        // now test a more complex annotation (relatively-speaking) -- two items
        annotation = queries.getAnnotationById(101L);
        assertEquals(new Long(1), dccAnnotationItem.getItemType().getItemTypeId());
        assertEquals(new Long(1), annotation.getAnnotationCategory().getCategoryId());
        assertEquals(3, annotation.getItems().size());

        final DccAnnotationItem firstDccAnnotationItem = annotation.getItems().get(0);
        final DccAnnotationItem secondDccAnnotationItem = annotation.getItems().get(1);
        final DccAnnotationItem thirdDccAnnotationItem = annotation.getItems().get(2);

        checkDccAnnotationItem(firstDccAnnotationItem, 2, "acorns");
        checkDccAnnotationItem(secondDccAnnotationItem, 3, "the one with spots");
        checkDccAnnotationItem(thirdDccAnnotationItem, 2, "the one with stripes");

        assertEquals("now with USB capabilities", annotation.getNotes().get(0).getNoteText());

        // now one with two notes
        annotation = queries.getAnnotationById(102L);
        assertEquals(2, annotation.getNotes().size());
        assertEquals("BRAINS!", annotation.getNotes().get(0).getNoteText());
        assertEquals("zombie 1", annotation.getNotes().get(0).getAddedBy());

        assertEquals("ARGH!", annotation.getNotes().get(1).getNoteText());
        assertEquals("zombie 2", annotation.getNotes().get(1).getAddedBy());
        assertEquals("brain", annotation.getItems().get(0).getItem());
    }

    @Test
    public void testGetAnnotationByIdInconsistent() throws AnnotationQueries.AnnotationQueriesException {
        try {
            queries.getAnnotationById(114L);
        } catch (final BeanException be) {
            assertEquals("An annotation must be approved in order to be rescinded.", be.getMessage());
        }
    }

    @Test
    public void testGetAnnotationDoesNotExist() throws BeanException {

        try {
            queries.getAnnotationById(0L);
            fail("exception wasn't thrown!");
        } catch (AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("Annotation with id 0 was not found", e.getMessage());
        }
    }

    @Test
    public void testAddNote() throws AnnotationQueries.AnnotationQueriesException, BeanException {

        // look up this annotation before adding a note to see how many notes are there
        DccAnnotation existingAnnotation = queries.getAnnotationById(100L);
        int initialNumNotes = existingAnnotation.getNotes().size();

        // create and add new note
        final DccAnnotationNote newNote = new DccAnnotationNote();
        newNote.setAddedBy("daffodil");
        newNote.setNoteText("I am glad it is raining");
        queries.addNewAnnotationNote(100L, newNote);

        // get the annotation again and see if the note is there
        existingAnnotation = queries.getAnnotationById(100L);
        assertEquals(initialNumNotes + 1, existingAnnotation.getNotes().size());
        assertEquals("I am glad it is raining", existingAnnotation.getNotes().get(initialNumNotes).getNoteText());
        assertEquals("daffodil", existingAnnotation.getNotes().get(initialNumNotes).getAddedBy());
        assertTrue(newNote.getNoteId() > 0);
        assertNotNull(newNote.getDateAdded());
    }

    @Test
    public void testAddNewAnnotationNoteAlreadyExist() throws BeanException {

        try {
            final DccAnnotationNote existingNote = getExistingNote();
            queries.addNewAnnotationNote(100L, existingNote);
            fail("Expected AnnotationQueriesException was not raised");

        } catch (final AnnotationQueries.AnnotationQueriesException e) {
            checkErrorMessage(e);
        }
    }

    @Test
    public void testEditNote() throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final DccAnnotation annotation = queries.getAnnotationById(100L);
        final DccAnnotationNote firstNote = annotation.getNotes().get(0);
        queries.editAnnotationNote(100L, firstNote, "this is the edited text of this note", "tester");
        assertEquals("this is the edited text of this note", firstNote.getNoteText());
        assertNotNull(firstNote.getDateEdited());
        assertEquals("tester", firstNote.getEditedBy());
    }

    @Test
    public void testEditAnnotationNoteAlreadyExist() throws BeanException {

        try {
            queries.editAnnotationNote(1L, getExistingNote(), getExistingNote().getNoteText(), "doesNotMatter");
        } catch (final AnnotationQueries.AnnotationQueriesException e) {
            checkErrorMessage(e);
        }
    }

    @Test
    public void testGetNoteById() throws AnnotationQueries.AnnotationQueriesException {

        final DccAnnotationNote note = queries.getAnnotationNoteById(1L);
        assertEquals("these are my favorite food", note.getNoteText());
        assertEquals("a squirrel", note.getAddedBy());
        assertEquals("Wed Mar 10 12:17:29 EST 2010", note.getDateAdded().toString());
    }

    @Test
    public void testGetMatchingAnnotationIds() {
        final List<Long> ids = queries.findMatchingAnnotationIds(new AnnotationSearchCriteria("acorns", null, null, null));
        assertEquals(2, ids.size());
        assertTrue(ids.contains(103L));
        assertTrue(ids.contains(101L));
    }

    @Test
    public void testSearchByItem() {

        final List<DccAnnotation> searchResults = queries.searchAnnotations(new AnnotationSearchCriteria("acorns", null, null, null));
        assertEquals(2, searchResults.size());
        // most recent should be first, so we know the order in the list
        assertEquals(new Long(103), searchResults.get(0).getId());
        assertEquals(new Long(101), searchResults.get(1).getId());
        assertEquals("chenjw", searchResults.get(0).getCreatedBy());
        assertEquals("pet rock", searchResults.get(1).getCreatedBy());
        assertEquals("grow on trees", searchResults.get(0).getNotes().get(0).getNoteText());
        assertEquals("now with USB capabilities", searchResults.get(1).getNotes().get(0).getNoteText());
        assertTrue(searchResults.get(0).getApproved());
        assertTrue(searchResults.get(1).getApproved());
    }

    @Test
    public void testSearchByItemBlank() {

        final List<DccAnnotation> searchResults = queries.searchAnnotations(new AnnotationSearchCriteria("", null, null, null));
        assertEquals(6, searchResults.size()); // all should be returned except rescinded annotations
    }

    @Test
    public void testSearchByClassification() {
        final AnnotationSearchCriteria criteria = new AnnotationSearchCriteria();
        criteria.setClassificationId(1L);
        final List<DccAnnotation> searchResults = queries.searchAnnotations(criteria);
        assertEquals(6, searchResults.size());
        for (final DccAnnotation annotation : searchResults) {
            assertEquals("Notification", annotation.getAnnotationCategory().getAnnotationClassification().getAnnotationClassificationName());
        }
    }

    @Test
    public void testSearchByAnnotationCategory() {

        final List<DccAnnotation> searchResults = queries.searchAnnotations(new AnnotationSearchCriteria(null, 2L, null, null));
        assertEquals(1, searchResults.size());
    }

    @Test
    public void testSearchByItemType() {

        final List<DccAnnotation> searchResults = queries.searchAnnotations(new AnnotationSearchCriteria(null, null, 2L, null));
        assertEquals(1, searchResults.size());
        assertEquals(new Long(104), searchResults.get(0).getId());
    }

    @Test
    public void testSearchByKeyword() {

        final List<DccAnnotation> searchResults = queries.searchAnnotations(new AnnotationSearchCriteria(null, null, null, "favorite"));
        assertEquals(1, searchResults.size());
        assertEquals(new Long(100), searchResults.get(0).getId());
    }

    @Test
    public void testSearchByKeywordDifferentCase() {

        final List<DccAnnotation> searchResults = queries.searchAnnotations(new AnnotationSearchCriteria(null, null, null, "FAVORITE")); // real thing is all lowercase
        assertEquals(1, searchResults.size());
        assertEquals(new Long(100), searchResults.get(0).getId());
    }

    @Test
    public void testSearchNoMatch() {

        final List<DccAnnotation> searchResults = queries.searchAnnotations(new AnnotationSearchCriteria("I am not in the db", 10000L, 10021L, "this note is not in the database"));
        assertEquals(0, searchResults.size());
    }

    @Test
    public void testSearchExact() {

        final AnnotationSearchCriteria criteria = new AnnotationSearchCriteria("acorn", null, null, null);
        criteria.setExact(true);
        final List<DccAnnotation> searchResults = queries.searchAnnotations(criteria);
        assertEquals(0, searchResults.size());
    }

    @Test
    public void testSearchNotExact() {

        final AnnotationSearchCriteria criteria = new AnnotationSearchCriteria("acorn", null, null, null);
        criteria.setExact(false);
        final List<DccAnnotation> searchResults = queries.searchAnnotations(criteria);
        assertEquals(2, searchResults.size());
    }

    @Test
    public void testSearchByDisease() {

        final AnnotationSearchCriteria criteria = new AnnotationSearchCriteria();
        criteria.setDiseaseId(1);
        final List<DccAnnotation> searchResults = queries.searchAnnotations(criteria);
        assertEquals(3, searchResults.size());
        for (final DccAnnotation annotation : searchResults) {
            for (final DccAnnotationItem dccAnnotationItem : annotation.getItems()) {
                assertNotNull(dccAnnotationItem.getDisease());
                assertEquals("DIS1", dccAnnotationItem.getDisease().getTumorName());
                assertEquals((Integer) 1, dccAnnotationItem.getDisease().getTumorId());
            }
        }
    }

    @Test
    public void testSearchByRescindedAndNotRescinded() {
        final AnnotationSearchCriteria criteria = new AnnotationSearchCriteria();
        criteria.setIncludeRescinded(true);
        final List<DccAnnotation> searchResults = queries.searchAnnotations(criteria);
        assertEquals(7, searchResults.size());

    }

    @Test
    public void testSearchByNotRescinded() {
        final AnnotationSearchCriteria criteria = new AnnotationSearchCriteria();
        criteria.setIncludeRescinded(false);
        final List<DccAnnotation> searchResults = queries.searchAnnotations(criteria);
        assertEquals(6, searchResults.size());
    }

    @Test
    public void testNonAuthenticatedUserSearchingAnnotationsFromNonCuratedAnnotator() {
        checkNonAuthenticatedUserSearchingAnnotationsFromNonCuratedAnnotator(SecurityUtilImpl.NOT_AUTHENTICATED);
    }

    @Test
    public void testNonAuthenticatedUserEmptyStringSearchingAnnotationsFromNonCuratedAnnotator() {
        checkNonAuthenticatedUserSearchingAnnotationsFromNonCuratedAnnotator("");
    }

    @Test
    public void testNonAuthenticatedUserNullSearchingAnnotationsFromNonCuratedAnnotator() {
        checkNonAuthenticatedUserSearchingAnnotationsFromNonCuratedAnnotator(null);
    }

    @Test
    public void testNonAuthenticatedUserSearchingAnnotationsFromCuratedAnnotator() {
        checkNonAuthenticatedUserSearchingAnnotationsFromCuratedAnnotator(SecurityUtilImpl.NOT_AUTHENTICATED);
    }

    @Test
    public void testNonAuthenticatedUserEmptyStringSearchingAnnotationsFromCuratedAnnotator() {
        checkNonAuthenticatedUserSearchingAnnotationsFromCuratedAnnotator("");
    }

    @Test
    public void testNonAuthenticatedUserNullSearchingAnnotationsFromCuratedAnnotator() {
        checkNonAuthenticatedUserSearchingAnnotationsFromCuratedAnnotator(null);
    }

    @Test
    public void testAuthenticatedUserSearchingAnnotationsFromCuratedAndNonCuratedAnnotator() {

        final String authenticatedUsername = "testAuthenticatedUsername";
        final String annotatorUsername = "pet_rock";
        final int expectedResultSize = 1;
        final boolean expectedApproved = true;

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedResultSize, expectedApproved);
    }

    @Test
    public void testAuthenticatedUserSearchingAnnotationsFromSelfCuratedAndNonCurated() {

        final String authenticatedUsername = "someone";
        final String annotatorUsername = "someone";
        final int expectedResultSize = 2;
        final Boolean expectedApproved = null;

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedResultSize, expectedApproved);
    }

    @Test
    public void testAuthenticatedUserSearchingAllAnnotatorsWithNull() {

        final String authenticatedUsername = "someone";
        final String annotatorUsername = null;
        final int expectedResultSize = 7;
        final Boolean expectedApproved = null;

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedResultSize, expectedApproved);
    }

    @Test
    public void testAuthenticatedUserSearchingAllAnnotatorsWithEmptyString() {

        final String authenticatedUsername = "someone";
        final String annotatorUsername = "";
        final int expectedResultSize = 7;
        final Boolean expectedApproved = null;

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedResultSize, expectedApproved);
    }

    @Test
    public void testSearchMultipleItemsNotExact() {

        final AnnotationSearchCriteria criteria = new AnnotationSearchCriteria();
        criteria.setItem("acorn,brain"); // actual item is "acorns" but will still match b/c not exact
        final List<DccAnnotation> searchResults = queries.searchAnnotations(criteria);
        assertEquals(3, searchResults.size());
        assertEquals("acorns", searchResults.get(0).getItems().get(0).getItem());
        assertEquals("brain", searchResults.get(1).getItems().get(0).getItem());
        assertEquals("acorns", searchResults.get(2).getItems().get(0).getItem());
    }

    @Test
    public void testSearchMultipleItemsExact() {

        final AnnotationSearchCriteria criteria = new AnnotationSearchCriteria();
        criteria.setItem("acorns,brains"); // there is no item "brains"
        final List<DccAnnotation> searchResults = queries.searchAnnotations(criteria);
        assertEquals(2, searchResults.size());
        assertEquals("acorns", searchResults.get(0).getItems().get(0).getItem());
        assertEquals("acorns", searchResults.get(1).getItems().get(0).getItem());
    }

    @Test
    public void testSetCurated() throws AnnotationQueries.AnnotationQueriesException, BeanException {

        DccAnnotation annotation = queries.getAnnotationById(106L);
        assertFalse(annotation.getApproved()); // make sure this annotation is still not curated in the test data
        queries.setCurated(annotation, true);
        assertTrue(annotation.getApproved()); // make sure flag in the bean is updated
        annotation = queries.getAnnotationById(106L);
        assertTrue(annotation.getApproved()); // and database is updated
    }

    /*
     * For some reason the @Test (expected = AnnotationQueries.AnnotationQueriesException) does not work for this
     */
    @Test
    public void testSetNotCuratedAndRescinded() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotation annotation = queries.getAnnotationById(113L);
        assertTrue(annotation.getApproved());
        assertTrue(annotation.getRescinded());
        try {
            queries.setCurated(annotation, false);
        } catch (final BeanException e) {
            assertEquals("An annotation must be approved in order to be rescinded.", e.getMessage());
        }
    }

    @Test
    public void testSetRescinded() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        DccAnnotation annotation = queries.getAnnotationById(111L);
        assertFalse(annotation.getRescinded()); // make sure this annotation is still not rescinded in the test data
        queries.setCurated(annotation, true);
        queries.setRescinded(annotation, true);
        annotation = queries.getAnnotationById(111L);
        assertTrue(annotation.getRescinded());
    }

    @Test
    public void testSetRescindedNoApprove() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotation annotation = queries.getAnnotationById(112L);
        assertFalse(annotation.getRescinded()); // make sure this annotation is still not rescinded in the test data
        try {
            queries.setRescinded(annotation, true);
        } catch (final BeanException e) {
            assertEquals("An annotation must be approved in order to be rescinded.", e.getMessage());
        }
    }

    @Test
    public void testEditAnnotationItem() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        DccAnnotation annotation = queries.getAnnotationById(107L);
        final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);
        dccAnnotationItem.setItem(VALID_ALIQUOT_BARCODE);
        Tumor thisTumor = dccAnnotationItem.getDisease();
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeId(1L);
        dccAnnotationItem.setItemType(itemType);
        dccAnnotationItem.setDisease(thisTumor);
        annotation.setApproved(true);
        annotation.setRescinded(true);
        queries.updateAnnotation(annotation);
        annotation = queries.getAnnotationById(107L);
        assertEquals(VALID_ALIQUOT_BARCODE, getFirstItem(annotation).getItem());
        assertEquals(new Long(1), dccAnnotationItem.getItemType().getItemTypeId());
        assertTrue(annotation.getApproved());
        assertTrue(annotation.getRescinded());
    }

    @Test
    public void testEditAnnotationStrict() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        DccAnnotation annotation = queries.getAnnotationById(107L);
        final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);
        dccAnnotationItem.setItem(VALID_ALIQUOT_BARCODE);
        Tumor thisTumor = dccAnnotationItem.getDisease();
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeId(1L);
        dccAnnotationItem.setItemType(itemType);
        dccAnnotationItem.setDisease(thisTumor);
        try {
            queries.updateAnnotation(107L, annotation, true);
        } catch (final AnnotationQueries.AnnotationQueriesException e) {

            final String expectedErrorMessage = "The Aliquot barcode 'TCGA-06-0939-01A-89D-0080-08' has failed validation due to following errors :\n" +
                    "The project code 'TCGA' in the barcode does not exist in database\n" +
                    "The tissue source site '06' in the barcode does not exist in database\n" +
                    "The sample Type '01' in the barcode does not exist in database\n" +
                    "The portion analyte 'D' in the barcode does not exist in database\n" +
                    "The bcr Center '08' in the barcode does not exist in database\n" +
                    "\n\n" +
                    "If codes or the barcode is missing in the database, change the item validation to 'Relaxed' if you want to submit the annotation anyway.";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void testEditAnnotationNotExistDbStrictItemValidation() throws BeanException {
        try {
            queries.setCommonBarcodeAndUUIDValidator(mockCommonBarcodeAndUUIDValidator);
            final boolean useStrictItemValidation = true;
            DccAnnotation annotation = queries.getAnnotationById(107L);
            final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);
            final DccAnnotationItemType itemType = new DccAnnotationItemType();
            itemType.setItemTypeId(1L);
            dccAnnotationItem.setItemType(itemType);
            dccAnnotationItem.setItem(INVALID_ALIQUOT_BARCODE_NO_EXIST_DB);
            final Tumor tumor = new Tumor();
            tumor.setTumorId(1);
            dccAnnotationItem.setDisease(tumor);
            annotation.addItem(dccAnnotationItem);

            queries.updateAnnotation(107L, annotation, useStrictItemValidation);
        } catch (final AnnotationQueries.AnnotationQueriesException e) {
            final String expetcedErrorMessage = "The barcode: " + INVALID_ALIQUOT_BARCODE_NO_EXIST_DB + " entered has not been submitted to the BCR as of yet." +
                    "\n\n" +
                    "If codes or the barcode is missing in the database, change the item validation to 'Relaxed' if you want to submit the annotation anyway.";
            assertEquals(expetcedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void testEditAnnotationNotExistDbRelaxedItemValidation() throws Exception {

        final boolean useStrictItemValidation = false;
        DccAnnotation annotation = queries.getAnnotationById(107L);
        final DccAnnotationItem dccAnnotationItem = getFirstItem(annotation);
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeId(1L);
        dccAnnotationItem.setItemType(itemType);
        dccAnnotationItem.setItem(INVALID_ALIQUOT_BARCODE_NO_EXIST_DB);
        final Tumor tumor = new Tumor();
        tumor.setTumorId(1);
        dccAnnotationItem.setDisease(tumor);
        annotation.addItem(dccAnnotationItem);

        queries.updateAnnotation(107L, annotation, useStrictItemValidation);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenOneExactMatch() {

        final String sample = "TCGA-06-0939-01A-89D-0080-08";
        checkGetAllAnnotationsForSamplesWhenOneMatch(sample, sample);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenOneMatchWhitespace() {

        final String sample = "   TCGA-06-0939-01A-89D-0080-08  ";
        checkGetAllAnnotationsForSamplesWhenOneMatch(sample, sample.trim());
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenOneExactMatchDuplicate() {

        final String sample = "TCGA-06-0939-01A-89D-0080-08";
        final String samples = sample + "," + sample;
        checkGetAllAnnotationsForSamplesWhenOneMatch(samples, sample);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenTwoExactMatches() {

        final String sample1 = "TCGA-06-0939-01A-89D-0080-08";
        final String sample2 = "brain";
        checkGetAllAnnotationsForSamplesWhenTwoMatches(sample1, sample2);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenTwoMatchesWhitespace() {

        final String sample1 = "   TCGA-06-0939-01A-89D-0080-08   ";
        final String sample2 = "   brain   ";
        checkGetAllAnnotationsForSamplesWhenTwoMatches(sample1, sample2);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenMatchIsNotCurated() {

        final String sample = "an aliquot";
        checkGetAllAnnotationsForSamplesWhenOneMatch(sample, null);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenMatchIsRescinded() {

        final String sample = "a sheep";
        checkGetAllAnnotationsForSamplesWhenOneMatch(sample, sample);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenSampleListIsEmpty() {

        final List<String> emptySampleList = new ArrayList<String>();
        final List<DccAnnotation> result = queries.getAllAnnotationsForSamples(emptySampleList);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenSampleListIsNull() {
        final List<DccAnnotation> result = queries.getAllAnnotationsForSamples(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllAnnotationsCountForSamplesWhenOneExactMatch() {

        final String sample = "TCGA-06-0939-01A-89D-0080-08";
        checkGetAllAnnotationsCountForSamplesWhenOneMatch(sample, sample);
    }

    @Test
    public void testGetAllAnnotationsCountForSamplesWhenOneMatchWhitespace() {

        final String sample = "   TCGA-06-0939-01A-89D-0080-08  ";
        checkGetAllAnnotationsCountForSamplesWhenOneMatch(sample, sample.trim());
    }

    @Test
    public void testGetAllAnnotationsCountForSamplesWhenOneExactMatchDuplicate() {

        final String sample = "TCGA-06-0939-01A-89D-0080-08";
        final String samples = sample + "," + sample;
        checkGetAllAnnotationsCountForSamplesWhenOneMatch(samples, sample);
    }

    @Test
    public void testGetAllAnnotationsCountForSamplesWhenTwoExactMatches() {

        final String sample1 = "TCGA-06-0939-01A-89D-0080-08";
        final String sample2 = "brain";
        checkGetAllAnnotationsCountForSamplesWhenTwoMatches(sample1, sample2);
    }

    @Test
    public void testGetAllAnnotationsCountForSamplesWhenTwoMatchesWhitespace() {

        final String sample1 = "   TCGA-06-0939-01A-89D-0080-08   ";
        final String sample2 = "   brain   ";
        checkGetAllAnnotationsCountForSamplesWhenTwoMatches(sample1, sample2);
    }

    @Test
    public void testGetAllAnnotationsCountForSamplesWhenMatchIsNotCurated() {

        final String sample = "an aliquot";
        checkGetAllAnnotationsCountForSamplesWhenOneMatch(sample, null);
    }

    @Test
    public void testGetAllAnnotationsCountForSamplesWhenMatchIsRescinded() {

        final String sample = "a sheep";
        checkGetAllAnnotationsCountForSamplesWhenOneMatch(sample, sample);
    }

    @Test
    public void testGetAllAnnotationsCountForSamplesWhenSampleListIsEmpty() {

        final List<String> emptySampleList = new ArrayList<String>();
        final Long result = queries.getAllAnnotationsCountForSamples(emptySampleList);

        assertNotNull(result);
        assertEquals(new Long(0L), result);
    }

    @Test
    public void testGetAllAnnotationsCountForSamplesWhenSampleListIsNull() {
        final Long result = queries.getAllAnnotationsCountForSamples(null);

        assertNotNull(result);
        assertEquals(new Long(0L), result);
    }

    public void testGetCategoryNameForId() {
        assertEquals("Annotation Category 3", queries.getAnnotationCategoryNameForId(3L));
        assertNull(queries.getAnnotationCategoryNameForId(123456L));
    }

    /**
     * Query for 1 sample and assert that the result is as expected. If the expected sample is <code>null</code>
     * then it is expected that there is no match.
     *
     * @param sample         the sample to query
     * @param expectedSample the expected sample (<code>null</code> if none is expected)
     */
    private void checkGetAllAnnotationsForSamplesWhenOneMatch(final String sample,
                                                              final String expectedSample) {

        final String[] samplesArray = sample.split(",", -1);
        final List<String> samples = new ArrayList<String>();
        Collections.addAll(samples, samplesArray);

        final List<DccAnnotation> result = queries.getAllAnnotationsForSamples(samples);

        assertNotNull(result);

        if (expectedSample != null) {

            assertEquals(1, result.size());

            final DccAnnotation dccAnnotation = result.get(0);
            assertNotNull(dccAnnotation);

            final List<DccAnnotationItem> dccAnnotationItems = dccAnnotation.getItems();
            assertNotNull(dccAnnotationItems);
            assertEquals(1, dccAnnotationItems.size());

            final DccAnnotationItem dccAnnotationItem = dccAnnotationItems.get(0);
            assertEquals(expectedSample, dccAnnotationItem.getItem());

        } else {
            assertEquals(0, result.size());
        }
    }

    private void checkGetAllAnnotationsCountForSamplesWhenOneMatch(final String sample,
                                                                   final String expectedSample) {
        final String[] samplesArray = sample.split(",", -1);
        final List<String> samples = new ArrayList<String>();
        Collections.addAll(samples, samplesArray);
        final Long result = queries.getAllAnnotationsCountForSamples(samples);
        assertNotNull(result);
        if (expectedSample != null) {
            assertEquals(new Long(1L), result);
        } else {
            assertEquals(new Long(0L), result);
        }
    }

    /**
     * Query for 2 samples for which there are matches and assert that the result is as expected.
     *
     * @param sample1 the first sample
     * @param sample2 the second sample
     */
    private void checkGetAllAnnotationsForSamplesWhenTwoMatches(final String sample1,
                                                                final String sample2) {

        final List<String> samples = new ArrayList<String>();
        samples.add(sample1 + "," + sample2);

        final List<DccAnnotation> result = queries.getAllAnnotationsForSamples(samples);

        assertNotNull(result);
        assertEquals(2, result.size());

        final DccAnnotation firstDccAnnotation = result.get(0);
        assertNotNull(firstDccAnnotation);

        final List<DccAnnotationItem> firstDccAnnotationItems = firstDccAnnotation.getItems();
        assertNotNull(firstDccAnnotationItems);
        assertEquals(1, firstDccAnnotationItems.size());

        final DccAnnotationItem firstDccAnnotationItemFirstItem = firstDccAnnotationItems.get(0);
        assertEquals(sample1.trim(), firstDccAnnotationItemFirstItem.getItem());

        final DccAnnotation secondDccAnnotation = result.get(1);
        assertNotNull(secondDccAnnotation);

        final List<DccAnnotationItem> secondDccAnnotationItems = secondDccAnnotation.getItems();
        assertNotNull(secondDccAnnotationItems);
        assertEquals(1, secondDccAnnotationItems.size());

        final DccAnnotationItem secondDccAnnotationItemFirstItem = secondDccAnnotationItems.get(0);
        assertEquals(sample2.trim(), secondDccAnnotationItemFirstItem.getItem());
    }

    private void checkGetAllAnnotationsCountForSamplesWhenTwoMatches(final String sample1,
                                                                     final String sample2) {
        final List<String> samples = new ArrayList<String>();
        samples.add(sample1 + "," + sample2);
        final Long result = queries.getAllAnnotationsCountForSamples(samples);
        assertNotNull(result);
        assertEquals(new Long(2L), result);
    }

    private void deleteAnnotations() {

        final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        jdbcTemplate.update("delete from annotation_note");
        jdbcTemplate.update("delete from annotation_item");
        jdbcTemplate.update("delete from annotation");
    }

    /**
     * Modify annotation to match an existing one
     */
    private void setupExistingAnnotation() {

        annotation.addNote("these are my favorite food", "doesNotMatter", new Date());
        annotation.getAnnotationCategory().setCategoryId(1L);

        final Tumor tumor = new Tumor();
        tumor.setTumorId(1);

        final DccAnnotationItemType dccAnnotationItemType = new DccAnnotationItemType();
        dccAnnotationItemType.setItemTypeId(1L);

        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
        dccAnnotationItem.setItem(VALID_ALIQUOT_BARCODE);
        dccAnnotationItem.setDisease(tumor);
        dccAnnotationItem.setItemType(dccAnnotationItemType);

        annotation.getItems().add(dccAnnotationItem);
    }

    /**
     * Return an existing note
     *
     * @return an existing note
     */
    private DccAnnotationNote getExistingNote() {

        final DccAnnotationNote result = new DccAnnotationNote();
        result.setNoteId(1L);
        result.setAddedBy("doesMotMatter");
        result.setNoteText("these are my favorite food");
        return result;
    }

    /**
     * Check the content of the given exception
     *
     * @param exception the exception to check
     */
    private void checkErrorMessage(final AnnotationQueries.AnnotationQueriesException exception) {

        final String expectedErrorMessage = "The following annotation is not unique: " +
                "[disease:DIS1][item type:Aliquot][item identifier:" + VALID_ALIQUOT_BARCODE + "][category:Annotation Category 1][note:these are my favorite food]";

        assertEquals("Unexpected error message", expectedErrorMessage, exception.getMessage());
    }

    /**
     * Check that the given <code>DccAnnotation</code> has only 1 <code>DccAnnotationItem</code> and return it
     *
     * @param dccAnnotation the <code>DccAnnotation</code> to check
     * @return the first <code>DccAnnotationItem</code> of the given <code>DccAnnotation</code>
     */
    private DccAnnotationItem getFirstItem(final DccAnnotation dccAnnotation) {

        assertNotNull(dccAnnotation.getItems());
        assertEquals(1, dccAnnotation.getItems().size());

        return dccAnnotation.getItems().get(0);
    }

    /**
     * Check the given <code>DccAnnotationItem</code> against expected values
     *
     * @param dccAnnotationItem the <code>DccAnnotationItem</code> to check
     * @param expectedDiseaseId the expected disease id
     * @param expectedItem      the expected item
     */
    private void checkDccAnnotationItem(final DccAnnotationItem dccAnnotationItem,
                                        final Integer expectedDiseaseId,
                                        final String expectedItem) {

        assertEquals(expectedItem, dccAnnotationItem.getItem());
        assertNotNull(dccAnnotationItem.getDisease());
        assertEquals(expectedDiseaseId, dccAnnotationItem.getDisease().getTumorId());
    }

    /**
     * Run a search for a non curated annotator with the given non authenticated username
     * and check for expectations and assertions
     *
     * @param authenticatedUsername the non authenticated username
     */
    private void checkNonAuthenticatedUserSearchingAnnotationsFromNonCuratedAnnotator(final String authenticatedUsername) {

        final String annotatorUsername = "kermit";
        final int expectedResultSize = 0;
        final boolean expectedApproved = false;

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedResultSize, expectedApproved);
    }

    /**
     * Run a search for a curated annotator with the given non authenticated username
     * and check expectations and assertions
     *
     * @param authenticatedUsername the non authenticated username
     */
    private void checkNonAuthenticatedUserSearchingAnnotationsFromCuratedAnnotator(final String authenticatedUsername) {

        final String annotatorUsername = "pet_rock";
        final int expectedResultSize = 1;
        final boolean expectedApproved = true;

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedResultSize, expectedApproved);
    }

    /**
     * Do a search with the given authenticated username and annotator username
     * and verify expectations and assertions.
     *
     * @param authenticatedUsername the authenticated username
     * @param annotatorUsername     the annotator username
     * @param expectedResultSize    the expected result size
     * @param expectedApproved      <code>true</code> if the first result is expected to be approved
     */
    private void checkSearchByAnnotator(final String authenticatedUsername,
                                        final String annotatorUsername,
                                        final int expectedResultSize,
                                        final Boolean expectedApproved) {

        final AnnotationSearchCriteria criteria = new AnnotationSearchCriteria();
        criteria.setAuthenticatedUsername(authenticatedUsername);
        criteria.setAnnotatorUsername(annotatorUsername);

        final List<DccAnnotation> searchResults = queries.searchAnnotations(criteria);

        assertEquals(expectedResultSize, searchResults.size());
        if (expectedResultSize == 1) {
            assertEquals(expectedApproved, searchResults.get(0).getApproved());
        }
    }

    @Test
    public void testGetAllAnnotations() {
        final List<Long> searchResults = queries.getAllAnnotationIds();
        assertEquals(13, searchResults.size());
    }
}
