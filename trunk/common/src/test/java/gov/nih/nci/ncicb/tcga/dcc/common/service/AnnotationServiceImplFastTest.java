/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationClassification;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationSearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.AclSecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.security.impl.SecurityUtilImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationServiceImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.util.AnnotationTestUtil;
import junit.framework.Assert;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.acls.domain.BasePermission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for AnnotationServiceImpl.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class AnnotationServiceImplFastTest {

    private Mockery context = new JUnit4Mockery();
    private AnnotationServiceImpl annotationService;
    private AnnotationQueries mockAnnotationQueries;
    private RedactionService mockRedactionService;
    private AclSecurityUtil mockAclSecurityUtil;

    @Before
    public void setup() {
        annotationService = new AnnotationServiceImpl();
        mockAnnotationQueries = context.mock(AnnotationQueries.class);
        annotationService.setAnnotationQueries(mockAnnotationQueries);
        mockAclSecurityUtil = context.mock(AclSecurityUtil.class);
        mockRedactionService = context.mock(RedactionService.class);
        annotationService.setAclSecurityUtil(mockAclSecurityUtil);
        annotationService.setRedactionService(mockRedactionService);
    }

    @Test
    public void testAddAnnotation() throws AnnotationQueries.AnnotationQueriesException, BeanException {

        context.checking(new Expectations() {{
            // will pretend the annotation saved and was given ID = 10
            one(mockAnnotationQueries).addNewAnnotation(with(any(DccAnnotation.class)), with(false));
            will(returnValue(10L));
            one(mockAclSecurityUtil).addPermission(with(any(DccAnnotationNote.class)), with(BasePermission.WRITE));
        }});

        final DccAnnotation annotation = annotationService.addAnnotation(1, 1L, "item", 2L, "note", "me");

        assertNotNull(annotation);
        assertNotNull(annotation.getItems());
        assertEquals(1, annotation.getItems().size());
        final DccAnnotationItem firstDccAnnotationItem = annotation.getItems().get(0);
        assertEquals(new Long(1), firstDccAnnotationItem.getItemType().getItemTypeId());
        assertEquals("item", firstDccAnnotationItem.getItem());
        assertEquals(new Long(2), annotation.getAnnotationCategory().getCategoryId());
        assertEquals(1, annotation.getNotes().size());
        assertEquals("note", annotation.getNotes().get(0).getNoteText());
        assertEquals("me", annotation.getNotes().get(0).getAddedBy());
        assertNotNull(annotation.getNotes().get(0).getDateAdded());
        assertNotNull(annotation.getDateCreated());
        assertEquals(annotation.getDateCreated(), annotation.getNotes().get(0).getDateAdded());
        assertEquals("me", annotation.getCreatedBy());
        assertEquals(new Long(10), annotation.getId());
        assertEquals(new Integer(1), firstDccAnnotationItem.getDisease().getTumorId());
    }

    @Test
    public void testUpdateAnnotation() throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final DccAnnotation myAnnotation = new DccAnnotation();
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        final Long itemTypeId = 2L;
        itemType.setItemTypeId(itemTypeId);

        final Tumor disease = new Tumor();
        final Integer tumorId = 2;
        disease.setTumorId(tumorId);
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();

        dccAnnotationItem.setItemType(itemType);
        final String item = "myitem";
        dccAnnotationItem.setItem(item);
        dccAnnotationItem.setDisease(disease);
        myAnnotation.addItem(dccAnnotationItem);

        final DccAnnotationCategory category = new DccAnnotationCategory();
        final Long categoryId = 3L;
        category.setCategoryId(categoryId);
        myAnnotation.setAnnotationCategory(category);
        final Date now = new Date();
        myAnnotation.setDateCreated(now);
        final String user = "me";
        final String note = "note";
        final String status = "Approved";
        myAnnotation.addNote(note, user, now);
        myAnnotation.setCreatedBy(user);
        final Long annotationId = 10L;
        myAnnotation.setId(annotationId);

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(with(annotationId));
            will(returnValue(myAnnotation));
            one(mockAnnotationQueries).updateAnnotation(with(any(Long.class)), with(any(DccAnnotation.class)), with(false));
            one(mockAnnotationQueries).getAnnotationCategories();
            will(returnValue(new ArrayList<DccAnnotationCategory>()));
        }});

        final DccAnnotation annotation = annotationService.updateAnnotation( annotationId, tumorId, itemTypeId, item, status, categoryId, user, false );

        assertNotNull(annotation);
        assertNotNull(annotation.getItems());
        assertEquals(1, annotation.getItems().size());
        final DccAnnotationItem firstDccAnnotationItem = annotation.getItems().get(0);
        assertEquals(itemTypeId, firstDccAnnotationItem.getItemType().getItemTypeId());
        assertEquals(item, firstDccAnnotationItem.getItem());
        assertEquals(categoryId, annotation.getAnnotationCategory().getCategoryId());
        assertEquals(now, annotation.getDateCreated());

        assertNotNull(annotation.getDateUpdated());
        assertEquals(user, annotation.getUpdatedBy());
        assertEquals(annotationId, annotation.getId());
        assertEquals(tumorId, firstDccAnnotationItem.getDisease().getTumorId());
    }

    @Test
    public void testRescindAnnotationAdmin() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotation myAnnotation = new DccAnnotation();
        final Date now = new Date();
        myAnnotation.setDateCreated(now); //lets check date created was not changed
        myAnnotation.setDateUpdated(now); //lets check data updated was not changed
        final String user = "the great dictator";
        final String note = "note";
        myAnnotation.addNote( note, user, now); //lets check the note was not changed
        myAnnotation.setCreatedBy(user); //lets check the createdby was not changed
        myAnnotation.setUpdatedBy(user); //lets check the updatedby was not changed

        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeId(1L);
        final Tumor disease = new Tumor();
        disease.setTumorId(1);
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
        dccAnnotationItem.setItemType(itemType);
        dccAnnotationItem.setItem("blah");
        dccAnnotationItem.setDisease(disease);
        dccAnnotationItem.setId(1L);
        myAnnotation.setItems(new LinkedList<DccAnnotationItem>(){{
            add(dccAnnotationItem);
        }});

        final DccAnnotationCategory category = new DccAnnotationCategory();
        category.setCategoryId(1L);
        DccAnnotationClassification classification = new DccAnnotationClassification();
        classification.setAnnotationClassificationName("la class");
        category.setAnnotationClassification(classification);
        myAnnotation.setAnnotationCategory(category);

        final Long annotationId = 10L;
        myAnnotation.setId(annotationId);
        myAnnotation.setApproved(true);
        annotationService.setAclSecurityUtil(null); //dont need to check admin privs for this

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(with(annotationId));
            will(returnValue(myAnnotation));
            one(mockAnnotationQueries).updateAnnotation(with(any(Long.class)), with(any(DccAnnotation.class)), with(false));
            one(mockAnnotationQueries).addNewAnnotationNote(with(any(Long.class)), with(any(DccAnnotationNote.class)));
            will(returnValue(23L));
        }});

        final DccAnnotation rescindedAnnotation = annotationService.rescindAnnotation(annotationId, user);
        assertEquals(now, rescindedAnnotation.getDateCreated());
        assertEquals(now, rescindedAnnotation.getDateUpdated());
        assertEquals(2, rescindedAnnotation.getNotes().size());
        assertEquals("note", rescindedAnnotation.getNotes().get(0).getNoteText());
        assertEquals(user, rescindedAnnotation.getCreatedBy());
        assertEquals(user, rescindedAnnotation.getUpdatedBy());

        assertEquals("This annotation was rescinded by " + user + " on " + new SimpleDateFormat().format(now), rescindedAnnotation.getNotes().get(1).getNoteText());
        assertEquals(true, rescindedAnnotation.getRescinded());
        assertEquals(true, rescindedAnnotation.getApproved());
        assertEquals(DccAnnotation.STATUS_RESCINDED, rescindedAnnotation.getStatus());
    }

    @Test
    public void testRescindAnnotationNonAdmin() throws BeanException {
        try {
            final DccAnnotation myAnnotation = new DccAnnotation();
            final Date now = new Date();
            myAnnotation.setDateCreated(now); //lets check date created was not changed
            myAnnotation.setDateUpdated(now); //lets check data updated was not changed
            final String user = "the great dictator";
            final String note = "note";
            myAnnotation.addNote( note, user, now); //lets check the note was not changed
            myAnnotation.setCreatedBy(user); //lets check the createdby was not changed
            myAnnotation.setUpdatedBy(user); //lets check the updatedby was not changed
            final Long annotationId = 10L;
            myAnnotation.setId(annotationId);
            myAnnotation.setApproved(true);

            context.checking(new Expectations() {{
                one(mockAnnotationQueries).getAnnotationById(with(annotationId));
                will(returnValue(myAnnotation));
            }});

            final DccAnnotation rescindedAnnotation = annotationService.rescindAnnotation(annotationId, user);
        } catch (AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("User doesn't have permissions to update annotation", e.getMessage());
        }
    }

    @Test
    public void testRescindRedactedAnnotation() throws Exception {
        final DccAnnotation myAnnotation = new DccAnnotation();
        final Date now = new Date();
        myAnnotation.setDateCreated(now);
        myAnnotation.setDateUpdated(now);
        final String user = "overseer";
        final String note = "papier";
        myAnnotation.addNote( note, user, now);
        myAnnotation.setCreatedBy(user);
        myAnnotation.setUpdatedBy(user);

        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeId(1L);
        final Tumor disease = new Tumor();
        disease.setTumorId(1);
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
        dccAnnotationItem.setItemType(itemType);
        dccAnnotationItem.setItem("TCGA-12-3456");
        dccAnnotationItem.setDisease(disease);
        dccAnnotationItem.setId(1L);
        myAnnotation.setItems(new LinkedList<DccAnnotationItem>(){{
            add(dccAnnotationItem);
        }});

        final DccAnnotationCategory category = new DccAnnotationCategory();
        category.setCategoryId(1L);
        DccAnnotationClassification classification = new DccAnnotationClassification();
        classification.setAnnotationClassificationName("redaction");
        category.setAnnotationClassification(classification);
        myAnnotation.setAnnotationCategory(category);

        final Long annotationId = 10L;
        myAnnotation.setId(annotationId);
        myAnnotation.setApproved(true);
        annotationService.setAclSecurityUtil(null); //dont need to check admin privs for this

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(with(annotationId));
            will(returnValue(myAnnotation));
            one(mockAnnotationQueries).updateAnnotation(with(any(Long.class)), with(any(DccAnnotation.class)), with(false));
            one(mockAnnotationQueries).addNewAnnotationNote(with(any(Long.class)), with(any(DccAnnotationNote.class)));
            will(returnValue(23L));
            one(mockRedactionService).rescind("TCGA-12-3456");
        }});

        final DccAnnotation rescindedAnnotation = annotationService.rescindAnnotation(annotationId, user);
        assertEquals(now, rescindedAnnotation.getDateCreated());
        assertEquals(now, rescindedAnnotation.getDateUpdated());
        assertEquals(2, rescindedAnnotation.getNotes().size());
        assertEquals("papier", rescindedAnnotation.getNotes().get(0).getNoteText());
        assertEquals(user, rescindedAnnotation.getCreatedBy());
        assertEquals(user, rescindedAnnotation.getUpdatedBy());

        assertEquals("This annotation was rescinded by " + user + " on " + new SimpleDateFormat().format(now), rescindedAnnotation.getNotes().get(1).getNoteText());
        assertEquals(true, rescindedAnnotation.getRescinded());
        assertEquals(true, rescindedAnnotation.getApproved());
        assertEquals(DccAnnotation.STATUS_RESCINDED, rescindedAnnotation.getStatus());
    }

    @Test
    public void testAddAnnotationValidStrictItemValidation()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final String expectedExceptionMessage = "code TCGB wasn't found in database";

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).addNewAnnotation(with(any(DccAnnotation.class)), with(true));
            will(throwException(new AnnotationQueries.AnnotationQueriesException(expectedExceptionMessage)));
        }});

        try {
            final boolean useStrictItemValidation = true;
            annotationService.addAnnotation(1, 1L, "item TCGB", 2L, "note", "me", useStrictItemValidation);
            fail("AnnotationQueriesException was not raised");
        } catch(final AnnotationQueries.AnnotationQueriesException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void testUpdateAnnotationValidStrictItemValidation()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final String expectedExceptionMessage = "code TCGB wasn't found in database";
        final DccAnnotation myAnnotation = new DccAnnotation();
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        final Long itemTypeId = 2L;
        itemType.setItemTypeId(itemTypeId);

        final Tumor disease = new Tumor();
        final Integer tumorId = 2;
        disease.setTumorId(tumorId);
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();

        dccAnnotationItem.setItemType(itemType);
        final String item = "myitem";
        dccAnnotationItem.setItem(item);
        dccAnnotationItem.setDisease(disease);
        myAnnotation.addItem(dccAnnotationItem);

        final DccAnnotationCategory category = new DccAnnotationCategory();
        final Long categoryId = 3L;
        category.setCategoryId(categoryId);
        myAnnotation.setAnnotationCategory(category);
        final Date now = new Date();
        myAnnotation.setDateCreated(now);
        final String user = "me";
        final String note = "note";
        final String status = "Approved";
        myAnnotation.addNote( note, user, now);
        myAnnotation.setCreatedBy(user);
        final Long annotationId = 10L;
        myAnnotation.setId(annotationId);

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(with(10L));
            will(returnValue(myAnnotation));
            one(mockAnnotationQueries).updateAnnotation(with(any(Long.class)), with(any(DccAnnotation.class)), with(true));
            will(throwException(new AnnotationQueries.AnnotationQueriesException(expectedExceptionMessage)));
        }});

        try {
            annotationService.updateAnnotation( annotationId, tumorId, itemTypeId, item, status, categoryId, user, true );

            fail("AnnotationQueriesException was not raised");
        } catch(final AnnotationQueries.AnnotationQueriesException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void testUpdateAnnotationInvalidPermissions()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final String expectedExceptionMessage = "User doesn't have permissions to update annotation";
        final DccAnnotation myAnnotation = new DccAnnotation();
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        final Long itemTypeId = 2L;
        itemType.setItemTypeId(itemTypeId);

        final Tumor disease = new Tumor();
        final Integer tumorId = 2;
        disease.setTumorId(tumorId);
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();

        dccAnnotationItem.setItemType(itemType);
        final String item = "myitem";
        dccAnnotationItem.setItem(item);
        dccAnnotationItem.setDisease(disease);
        myAnnotation.addItem(dccAnnotationItem);

        final DccAnnotationCategory category = new DccAnnotationCategory();
        final Long categoryId = 3L;
        category.setCategoryId(categoryId);
        myAnnotation.setAnnotationCategory(category);
        final Date now = new Date();
        myAnnotation.setDateCreated(now);
        final String user = "me";
        final String note = "note";
        final String status = "Approved";
        myAnnotation.addNote( note, user, now);
        myAnnotation.setCreatedBy(user);
        final Long annotationId = 10L;
        myAnnotation.setId(annotationId);

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(with(10L));
            will(returnValue(myAnnotation));
        }});

        try {
            annotationService.updateAnnotation( annotationId, tumorId, itemTypeId, item, status, categoryId, "him", false );
            fail("AnnotationQueriesException was not raised");
        } catch(final AnnotationQueries.AnnotationQueriesException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void testUpdateApproveRedaction() throws BeanException, AnnotationQueries.AnnotationQueriesException {

        final DccAnnotation myAnnotation = new DccAnnotation();
        final Date now = new Date();
        myAnnotation.setDateCreated(now);
        myAnnotation.setDateUpdated(now);
        final String user = "tester";
        myAnnotation.setCreatedBy(user);
        myAnnotation.setUpdatedBy(user);

        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeId(1L);
        final Tumor disease = new Tumor();
        disease.setTumorId(1);
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
        dccAnnotationItem.setItemType(itemType);
        dccAnnotationItem.setItem("TCGA-12-3456");
        dccAnnotationItem.setDisease(disease);
        dccAnnotationItem.setId(1L);
        myAnnotation.setItems(new LinkedList<DccAnnotationItem>(){{
            add(dccAnnotationItem);
        }});

        final DccAnnotationCategory category = new DccAnnotationCategory();
        category.setCategoryId(1L);
        DccAnnotationClassification classification = new DccAnnotationClassification();
        classification.setAnnotationClassificationName("redaction");
        category.setAnnotationClassification(classification);
        myAnnotation.setAnnotationCategory(category);

        final Long annotationId = 10L;
        myAnnotation.setId(annotationId);
        myAnnotation.setApproved(true);
        annotationService.setAclSecurityUtil(null);

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(10L);
            will(returnValue(myAnnotation));

            one(mockAnnotationQueries).updateAnnotation(10L, myAnnotation, true);

            one(mockAnnotationQueries).getAnnotationCategories();
            will(returnValue(Arrays.asList(category)));

            one(mockAnnotationQueries).getActiveDiseases();
            will(returnValue(Arrays.asList(disease)));

            // make sure this is called since the annotation is approved and a redaction
            one(mockRedactionService).redact("TCGA-12-3456", 1L);
        }});

        annotationService.updateAnnotation(annotationId, 1, 1L, "TCGA-12-3456", "approved", 1L, "tester", true);

    }

    @Test
    public void testGetItemTypes() {
        final List<Map<String, Object>> itemTypes = new ArrayList<Map<String, Object>>();
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getItemTypes();
            will(returnValue(itemTypes));
        }});

        assertSame(itemTypes, annotationService.getItemTypes());
    }

    @Test
    public void testGetAnnotationCategories() {
        final List<Map<String, Object>> annotationCats = new ArrayList<Map<String, Object>>();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationCategories();
            will(returnValue(annotationCats));
        }});
        assertSame(annotationCats, annotationService.getAnnotationCategories());
    }

    @Test
    public void testGetActiveDiseases() {
        final List<Map<String, Object>> diseases = new ArrayList<Map<String, Object>>();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getActiveDiseases();
            will(returnValue(diseases));
        }});
        assertSame(diseases, annotationService.getActiveDiseases());
    }

    @Test
    public void testGetAnnotationById()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotation annotation = new DccAnnotation();
        annotation.setId(0L);
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(0L);
            will(returnValue(annotation));            
        }});
        
        final DccAnnotation returnedAnnotation = annotationService.getAnnotationById(0L);
        assertNotNull(annotation);
        assertSame(annotation, returnedAnnotation);
    }

    @Test
    public void testGetAnnotationNotFound()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotation annotation = new DccAnnotation();
        annotation.setId(1L);
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(1L);
            //noinspection ThrowableInstanceNeverThrown
            will(throwException(new AnnotationQueries.AnnotationQueriesException("id not found")));
        }});
        try {
            annotationService.getAnnotationById(1L);
            fail("exception not thrown");
        } catch (AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("id not found", e.getMessage());
        }
    }

    @Test
    public void testAddAnnotationNote()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).addNewAnnotationNote(with(23L), with(any(DccAnnotationNote.class)));
            will(returnValue(345L));
            one(mockAclSecurityUtil).addPermission(with(any(DccAnnotationNote.class)), with(BasePermission.WRITE));
        }});
        final Date now = new Date();
        DccAnnotationNote newNote = annotationService.addNewAnnotationNote(23L, "this is text", "me", now);
        assertEquals("this is text", newNote.getNoteText());
        assertEquals("me", newNote.getAddedBy());
        assertEquals(now, newNote.getDateAdded());
        assertEquals(new Long(345), newNote.getNoteId());
    }

    @Test
    public void testAddNoteInvalid()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).addNewAnnotationNote(with(4L), with(any(DccAnnotationNote.class)));
            //noinspection ThrowableInstanceNeverThrown
            will(throwException(new AnnotationQueries.AnnotationQueriesException("invalid note")));
        }});

        try {
            annotationService.addNewAnnotationNote(4L, "text", null, null);
            fail("exception wasn't thrown");
        } catch (AnnotationQueries.AnnotationQueriesException e) {
            assertEquals("invalid note", e.getMessage());
        }
    }

    @Test
    public void testEditNote()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotationNote note = new DccAnnotationNote();
        note.setNoteId(23L);
        note.setAnnotationId(1L);

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationNoteById(23L);
            will(returnValue(note));
            one(mockAnnotationQueries).editAnnotationNote(1L, note, "new text", "test user");
            will(editNoteText("new text"));
        }});

        final DccAnnotationNote editedNote = annotationService.editAnnotationNote(23L, "new text", "test user");
        assertSame(note, editedNote);
        assertEquals("new text", note.getNoteText());
    }

    private static Action editNoteText(final String text) {
        return new Action() {

            public void describeTo(final Description description) {
                description.appendText("edits note text");
            }

            public Object invoke(final Invocation invocation) throws Throwable {
                ((DccAnnotationNote) invocation.getParameter(1)).setNoteText(text);
                return null;
            }
        };
    }

    @Test
    public void testSearchAnnotationsNoCriteria()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final List<Long> ids = new ArrayList<Long>();
        ids.add(123L);
        final DccAnnotation annotation123 = new DccAnnotation();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(with(AnnotationTestUtil.criteriaMatching(null, null, null, null, null, null, null)));
            will(returnValue(ids));
            one(mockAnnotationQueries).getAnnotationById(123L);
            will(returnValue(annotation123));
        }});
        final List<DccAnnotation> annotations = annotationService.searchAnnotations(null, null, null, null, null, null, false, null, null, null, null, null, null);
        assertEquals(annotation123, annotations.get(0));
    }

    @Test
    public void testSearchWithZeroCategoryId() {

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(with(AnnotationTestUtil.criteriaMatching("item", null, 0L, null, null, null, null)));
        }});

        annotationService.searchAnnotations(null, "item", 0L, null, null, null, false, null, null, null, null, null, null);
    }

    @Test
    public void testSearchWithClassification() {
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(with(AnnotationTestUtil.criteriaMatching(null, 123L, null, null, null, null, null)));
        }});
        annotationService.searchAnnotations(null, null, null, 123L, null, null, false, null, null, null, null, null, null);
    }

    @Test
    public void testSearchAnnotations()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final List<Long> ids = new ArrayList<Long>();
        ids.add(5L);
        final DccAnnotation annotation5 = new DccAnnotation();

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(with(AnnotationTestUtil.criteriaMatching("item", null, 25L, 15L, "keyword", null, null)));
            will(returnValue(ids));
            one(mockAnnotationQueries).getAnnotationById(5L);
            will(returnValue(annotation5));
        }});

        final List<DccAnnotation> annotations = annotationService.searchAnnotations(null, "item", 25L, null, 15L, "keyword", false, null, null, null, null, null, null);
        assertEquals(annotation5, annotations.get(0));
    }

    @Test
    public void testSearchExact() {
        final List<Long> ids = new ArrayList<Long>();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(with(AnnotationTestUtil.criteriaMatching("item", null, 25L, 15L, "keyword", null, null)));
            will(returnValue(ids));
        }});

        final List<DccAnnotation> annotations = annotationService.searchAnnotations(null, "item", 25L, null, 15L, "keyword", true, null, null, null, null, null, null);
    }

    @Test
    public void testSearchByStatus() {
        final List<Long> ids = new ArrayList<Long>();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(with(fillUpAnnotationSearchCriteria(true)));
            will(returnValue(ids));
        }});
        annotationService.searchAnnotations(null, "item", 25L, null, 15L, "keyword", true, "approved", false, null, null, null, null);
    }

    @Test
    public void testSearchLoggedIn() {
        final List<Long> ids = new ArrayList<Long>();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(with(AnnotationTestUtil.criteriaMatching("item", null, 25L, 15L, "keyword", "someone", null)));
            will(returnValue(ids));
        }});

        annotationService.searchAnnotations(null, "item", 25L, null, 15L, "keyword", false, null, null, "someone", null, null, null);
    }

    @Test
    public void testSearchWithExistingAnnotationId()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final Long annotationId = 1L;

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(annotationId);
            will(returnValue(getDccAnnotation(annotationId)));
        }});

        final List<DccAnnotation> annotations = annotationService.searchAnnotations(null, null, null, null, null, null, false, null, null, null, null, Arrays.asList(annotationId), null);

        assertNotNull(annotations);
        assertEquals(1, annotations.size());
        assertEquals(annotationId, annotations.get(0).getId());
    }

    @Test
    public void testSearchWithMultipleAnnotationIds()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotation annotation1 = getDccAnnotation(1L);
        annotation1.setDateCreated(new Date());
        final DccAnnotation annotation2 = getDccAnnotation(2L);
        annotation2.setDateCreated(new Date());
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(1L);
            will(returnValue(annotation1));
            one(mockAnnotationQueries).getAnnotationById(2L);
            will(returnValue(annotation2));
        }});

        final List<DccAnnotation> annotations = annotationService.searchAnnotations(null, null, null, null, null, null, false, null, null, null, null, Arrays.asList(1L, 2L), null);
        assertEquals(2, annotations.size());
        assertTrue(annotations.contains(annotation1));
        assertTrue(annotations.contains(annotation2));
    }

    @Test
    public void testSearchWithNonExistingAnnotationId()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final Long annotationId = 2L;

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(annotationId);
            will(throwException(new AnnotationQueries.AnnotationQueriesException("Annotation with Id " + annotationId + "does not exist.")));
        }});

        final List<DccAnnotation> annotations = annotationService.searchAnnotations(null, null, null, null, null, null, false, null, null, null, null, Arrays.asList(annotationId), null);

        assertNotNull(annotations);
        assertEquals(0, annotations.size());
    }

    @Test
    public void testCurate()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotation annotation = new DccAnnotation();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).setCurated(annotation, true);
        }});
        annotationService.curate(annotation);
    }

    @Test
    public void testAuthenticatedUserSearchingItsOwnAnnotations() {

        final String username = "testUsername";
        final List<Long> expectedResults = new ArrayList<Long>();

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(
                    with(AnnotationTestUtil.criteriaMatching(null, null, null, null, null, username, username)));
            will(returnValue(expectedResults));
        }});

        final List<DccAnnotation> annotations = annotationService.searchAnnotations(null, null, null, null, null, null, false, null, null, username, null, null, username);

        assertNotNull(annotations);
        assertEquals(0, annotations.size());
    }

    @Test
    public void testAuthenticatedUserSearchingOthersAnnotations() {

        final String authenticatedUsername = "testAuthenticatedUsername";
        final String annotatorUsername = "testAnnotatorUsername";
        final List<Long> expectedResults = new ArrayList<Long>();

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(
                    with(AnnotationTestUtil.criteriaMatching(null, null, null, null, null, authenticatedUsername, annotatorUsername)));
            will(returnValue(expectedResults));
        }});

        final List<DccAnnotation> annotations = annotationService.searchAnnotations(
                null, null, null, null, null, null, false, null, null, authenticatedUsername, null, null, annotatorUsername);

        assertNotNull(annotations);
        assertEquals(0, annotations.size());
    }

    @Test
    public void testNonAuthenticatedUserSearchingAnnotationsByAnnotator() {
        checkNonAuthenticatedUserSearchingAnnotationsByAnnotator(SecurityUtilImpl.NOT_AUTHENTICATED);
    }

    @Test
    public void testNonAuthenticatedUserEmptyStringSearchingAnnotationsByAnnotator() {
        checkNonAuthenticatedUserSearchingAnnotationsByAnnotator("");
    }

    @Test
    public void testNonAuthenticatedUserNullSearchingAnnotationsByAnnotator() {
        checkNonAuthenticatedUserSearchingAnnotationsByAnnotator(null);
    }

    @Test
    public void testNonAuthenticatedUserSearchingAllAnnotations() {

        final String authenticatedUsername = SecurityUtilImpl.NOT_AUTHENTICATED;
        final String annotatorUsername = null;

        checkNonAuthenticatedUserSearchingAllAnnotators(authenticatedUsername, annotatorUsername);
    }

    @Test
    public void testNonAuthenticatedUserEmptyStringSearchingAllAnnotations() {

        final String authenticatedUsername = "";
        final String annotatorUsername = null;

        checkNonAuthenticatedUserSearchingAllAnnotators(authenticatedUsername, annotatorUsername);
    }

    @Test
    public void testNonAuthenticatedUserNullSearchingAllAnnotations() {

        final String authenticatedUsername = null;
        final String annotatorUsername = null;

        checkNonAuthenticatedUserSearchingAllAnnotators(authenticatedUsername, annotatorUsername);
    }

    @Test
    public void testNonAuthenticatedUserSearchingAllAnnotationsWithEmptyString() {

        final String authenticatedUsername = SecurityUtilImpl.NOT_AUTHENTICATED;
        final String annotatorUsername = "";

        checkNonAuthenticatedUserSearchingAllAnnotators(authenticatedUsername, annotatorUsername);
    }

    @Test
    public void testNonAuthenticatedUserEmptyStringSearchingAllAnnotationsWithEmptyString() {

        final String authenticatedUsername = "";
        final String annotatorUsername = "";

        checkNonAuthenticatedUserSearchingAllAnnotators(authenticatedUsername, annotatorUsername);
    }

    @Test
    public void testNonAuthenticatedUserNullSearchingAllAnnotationsWithEmptyString() {

        final String authenticatedUsername = null;
        final String annotatorUsername = "";

        checkNonAuthenticatedUserSearchingAllAnnotators(authenticatedUsername, annotatorUsername);
    }

    @Test
    public void testGetClassificationNameFromCategoryId() throws Exception {
        final DccAnnotationCategory dac = new DccAnnotationCategory();
        dac.setCategoryId(1L);
        dac.setAnnotationClassification(new DccAnnotationClassification() {{
            setAnnotationClassificationName("Slytherin");
        }});
        context.checking(new Expectations() {{
            allowing(mockAnnotationQueries).getAnnotationCategories();
            will(returnValue(new ArrayList<DccAnnotationCategory>() {{
                add(dac);
            }}
            ));
        }});
        assertEquals("Slytherin", annotationService.getClassificationNameFromCategoryId(1L));
        assertNull(annotationService.getClassificationNameFromCategoryId(2L));
    }

    @Test
    public void testGetAllAnnotations()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final List<Long> allIds = Arrays.asList(1L, 2L, 3L, 4L);
        final DccAnnotation annotation1 = getDccAnnotation(1L);
        final DccAnnotation annotation2 = getDccAnnotation(2L);
        final DccAnnotation annotation3 = getDccAnnotation(3L);
        final DccAnnotation annotation4 = getDccAnnotation(4L);
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAllAnnotationIds();
            will(returnValue(allIds));
            one(mockAnnotationQueries).getAnnotationById(1L);
            will(returnValue(annotation1));
            one(mockAnnotationQueries).getAnnotationById(2L);
            will(returnValue(annotation2));
            one(mockAnnotationQueries).getAnnotationById(3L);
            will(returnValue(annotation3));
            one(mockAnnotationQueries).getAnnotationById(4L);
            will(returnValue(annotation4));
        }});

        final List<DccAnnotation> annotations = annotationService.getAllAnnotations();
        assertEquals(4, annotations.size());
        assertTrue(annotations.contains(annotation1));
        assertTrue(annotations.contains(annotation2));
        assertTrue(annotations.contains(annotation3));
        assertTrue(annotations.contains(annotation4));
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenOneExactMatch() {

        final String sample = "sample";
        checkGetAllAnnotationsForSamplesWhenOneMatch(sample, sample);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenOneMatchWhitespace() {

        final String sample = "sample";
        final String whitespace = "   ";
        checkGetAllAnnotationsForSamplesWhenOneMatch(whitespace + sample + whitespace, sample);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenOneExactMatchDuplicate() {

        final String sample = "sample";
        final String samples = sample + "," + sample;
        checkGetAllAnnotationsForSamplesWhenOneMatch(samples, sample);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenTwoExactMatches() {

        final String sample1 = "sample 1";
        final String sample2 = "sample 2";
        checkGetAllAnnotationsForSamplesWhenTwoMatches(sample1, sample2);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenTwoMatchesWhitespace() {

        final String sample1 = "   sample 1   ";
        final String sample2 = "   sample 2   ";
        checkGetAllAnnotationsForSamplesWhenTwoMatches(sample1, sample2);
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenSampleListIsEmpty() {

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAllAnnotationsForSamples(new ArrayList<String>());
            will(returnValue(new ArrayList<String>()));
        }});

        final List<DccAnnotation> result = annotationService.getAllAnnotationsForSamples(new ArrayList<String>());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllAnnotationsForSamplesWhenSampleListIsNull() {

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAllAnnotationsForSamples(null);
            will(returnValue(new ArrayList<String>()));
        }});

        final List<DccAnnotation> result = annotationService.getAllAnnotationsForSamples(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Query for 1 sample and assert that the result is as expected. If the expected sample is <code>null</code>
     * then it is expected that there is no match.
     *
     * @param sample the sample to query
     * @param expectedSample the expected sample (<code>null</code> if none is expected)
     */
    private void checkGetAllAnnotationsForSamplesWhenOneMatch(final String sample,
                                                              final String expectedSample) {

        final String[] samplesArray = sample.split(",", -1);
        final List<String> samples = new ArrayList<String>();
        for(final String sampleItem : samplesArray) {
            samples.add(sampleItem);
        }

        final List<DccAnnotation> expectedResult = new ArrayList<DccAnnotation>();

        if(expectedSample != null) {
            expectedResult.add(makeAnnotation(expectedSample));
        }

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAllAnnotationsForSamples(samples);
            will(returnValue(expectedResult));
        }});

        final List<DccAnnotation> result = annotationService.getAllAnnotationsForSamples(samples);

        assertNotNull(result);

        if(expectedSample != null) {

            assertEquals(1, result.size());

            final DccAnnotation dccAnnotation = result.get(0);
            assertNotNull(dccAnnotation);

            final List<DccAnnotationItem> dccAnnotationItems = dccAnnotation.getItems();
            assertNotNull(dccAnnotationItems);
            assertEquals(1, dccAnnotation.getItems().size());

            final DccAnnotationItem dccAnnotationItem = dccAnnotationItems.get(0);
            assertNotNull(dccAnnotationItem);
            Assert.assertEquals(expectedSample, dccAnnotationItem.getItem());

        } else {
            assertEquals(0, result.size());
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
        samples.add(sample1);
        samples.add(sample2);

        final List<DccAnnotation> expectedResult = new ArrayList<DccAnnotation>();
        expectedResult.add(makeAnnotation(sample1.trim()));
        expectedResult.add(makeAnnotation(sample2.trim()));

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAllAnnotationsForSamples(samples);
            will(returnValue(expectedResult));
        }});

        final List<DccAnnotation> result = annotationService.getAllAnnotationsForSamples(samples);

        assertNotNull(result);
        assertEquals(2, result.size());

        final DccAnnotation firstDccAnnotation = result.get(0);
        assertNotNull(firstDccAnnotation);

        final List<DccAnnotationItem> firstDccAnnotationItems = firstDccAnnotation.getItems();
        assertNotNull(firstDccAnnotationItems);
        assertEquals(1, firstDccAnnotation.getItems().size());

        final DccAnnotationItem firstDccAnnotationItem = firstDccAnnotationItems.get(0);
        assertNotNull(firstDccAnnotationItem);
        Assert.assertEquals(sample1.trim(), firstDccAnnotationItem.getItem());

        final DccAnnotation secondDccAnnotation = result.get(1);
        assertNotNull(secondDccAnnotation);

        final List<DccAnnotationItem> secondDccAnnotationItems = secondDccAnnotation.getItems();
        assertNotNull(secondDccAnnotationItems);
        assertEquals(1, secondDccAnnotation.getItems().size());

        final DccAnnotationItem secondDccAnnotationItem = secondDccAnnotationItems.get(0);
        assertNotNull(secondDccAnnotationItem);
        Assert.assertEquals(sample2.trim(), secondDccAnnotationItem.getItem());
    }

    /**
     * Return a {@link DccAnnotation} with the given item
     *
     * @param item the item value
     * @returna {@link DccAnnotation} with the given item
     */
    private DccAnnotation makeAnnotation(final String item) {

        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
        dccAnnotationItem.setItem(item);

        final DccAnnotation dccAnnotation = new DccAnnotation();
        dccAnnotation.addItem(dccAnnotationItem);

        return dccAnnotation;
    }

    /**
     * Run a search by a non authenticated user for all annotators,
     * and check expectations and assertions.
     *
     * @param authenticatedUsername the non authenticated username
     * @param annotatorUsername the annotated username (empty or null)
     */
    private void checkNonAuthenticatedUserSearchingAllAnnotators(final String authenticatedUsername, final String annotatorUsername) {

        final List<Long> expectedResults = new ArrayList<Long>();

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(
                    with(AnnotationTestUtil.criteriaMatching(null, null, null, null, null, authenticatedUsername, annotatorUsername)));
            will(returnValue(expectedResults));
        }});

        final List<DccAnnotation> annotations = annotationService.searchAnnotations(
                null, null, null, null, null, null, false, null, null, authenticatedUsername, null, null, annotatorUsername);

        assertNotNull(annotations);
        assertEquals(0, annotations.size());
    }

    /**
     * Do a search with the given authenticated username (non authenticated)
     * and check expectations and assertions
     *
     * @param authenticatedUsername the non authenticated username
     */
    private void checkNonAuthenticatedUserSearchingAnnotationsByAnnotator(final String authenticatedUsername) {

        final String annotatorUsername = "testAnnotatorUsername";
        final List<Long> expectedResults = new ArrayList<Long>();

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(
                    with(AnnotationTestUtil.criteriaMatching(null, null, null, null, null, authenticatedUsername, annotatorUsername)));
            will(returnValue(expectedResults));
        }});

        final List<DccAnnotation> annotations = annotationService.searchAnnotations(
                null, null, null, null, null, null, false, null, false, authenticatedUsername, null, null, annotatorUsername);

        assertNotNull(annotations);
        assertEquals(0, annotations.size());
    }

    /**
     * Return a <code>DccAnnotation</code> with the given Id
     *
     * @param annotationId the Id of <code>DccAnnotation</code> to return
     * @return a <code>DccAnnotation</code> with the given Id
     */
    private DccAnnotation getDccAnnotation(final Long annotationId) {

        final DccAnnotation result = new DccAnnotation();
        result.setId(annotationId);

        return result;
    }

    /**
     * fill up a AnnotationSearchCriteria bean by curated values
     * @param curated
     * @return AnnotationSearchCriteria
     */
    private AnnotationSearchCriteria fillUpAnnotationSearchCriteria(final Boolean curated){
        final AnnotationSearchCriteria search = new AnnotationSearchCriteria();
        search.setDiseaseId(null);
        search.setItem("item");
        search.setCategoryId(25L);
        search.setClassificationId(null);
        search.setItemTypeId(15L);
        search.setKeyword("keyword");
        search.setExact(true);
        search.setCurated(curated);
        search.setAuthenticatedUsername(null);
        search.setRowLimit(null);
        search.setAnnotatorUsername(null);
        return search;
    }
}
