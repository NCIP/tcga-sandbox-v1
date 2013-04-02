/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Test for DccAnnotation bean
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DccAnnotationFastTest {
	
    private DccAnnotation annotation;

    @Before
    public void setup() {
        annotation = new DccAnnotation();
    }

    @Test
    public void testSimpleSetters() {
    	
    	final String expectedItemTypeName = "testTypeName";
    	final String expectedItem = "testItem";
        final String expectedCreatedBy = "aUsername";
        final String expectedUpdatedBy = "uUsername";
        final String expectedCategoryName = "testCategoryName";
        final Date expectedDateCreated = new Date();
        final Date expectedDateUpdated = new Date();

        
    	annotation.addItem(getDccAnnotationItem(expectedItem, expectedItemTypeName));
        annotation.setDateCreated(expectedDateCreated);
        annotation.setCreatedBy(expectedCreatedBy);
        annotation.setDateUpdated(expectedDateUpdated);
        annotation.setAnnotationCategory(getDccAnnotationCategory(expectedCategoryName));
        annotation.setUpdatedBy(expectedUpdatedBy);
        assertNotNull(annotation.getItems());
        assertEquals(1, annotation.getItems().size());
        final DccAnnotationItem actualDccAnnotationItem = annotation.getItems().get(0);
        assertEquals(expectedItem, actualDccAnnotationItem.getItem());
        assertEquals(expectedItemTypeName, actualDccAnnotationItem.getItemType().getItemTypeName());
        assertEquals(expectedDateCreated, annotation.getDateCreated());
        assertEquals(expectedCreatedBy, annotation.getCreatedBy());
        assertEquals(expectedUpdatedBy, annotation.getUpdatedBy());
        assertEquals(expectedDateUpdated, annotation.getDateUpdated()) ;
        assertEquals(expectedCategoryName, annotation.getAnnotationCategory().getCategoryName());
    }

    @Test
    public void testSetApproved() throws BeanException {
    	
        annotation.setApproved(true);
        assertTrue(annotation.getApproved());
        assertEquals(DccAnnotation.STATUS_APPROVED, annotation.getStatus());

        annotation.setApproved(false);
        assertFalse(annotation.getApproved());
        assertEquals(DccAnnotation.STATUS_PENDING, annotation.getStatus());        
    }

    @Test
    public void testSetApprovedAndRescinded() throws BeanException {
        annotation.setApproved(true);
        annotation.setRescinded(true);
        assertTrue(annotation.getApproved());
        assertTrue(annotation.getRescinded());
        assertEquals(DccAnnotation.STATUS_RESCINDED, annotation.getStatus());
    }

    @Test(expected = BeanException.class)
    public void testSetRescinded() throws BeanException {
        annotation.setRescinded(true);
        annotation.setApproved(false);
    }

    @Test(expected = BeanException.class)
    public void testSetNotApprovedAndRescinded() throws BeanException {
        annotation.setRescinded(true);
        annotation.setApproved(false);
    }

    @Test
    public void testSetApprovedAndRescindedFalse() throws BeanException {
        annotation.setApproved(false);
        annotation.setRescinded(false);
        assertFalse(annotation.getRescinded());
        assertFalse(annotation.getApproved());
        assertEquals(DccAnnotation.STATUS_PENDING, annotation.getStatus());
    }

    @Test
    public void testAddNote() {
    	
    	final String noteText = "note text";
    	final String addedBy = "username";
        final Date now = new Date();
        
        annotation.addNote(noteText, addedBy, now);
        
        assertNotNull(annotation.getNotes());
        assertEquals(1, annotation.getNotes().size());
        final DccAnnotationNote note = annotation.getNotes().get(0);
        assertEquals(noteText, note.getNoteText());
        assertEquals(addedBy, note.getAddedBy());
        assertEquals(now, note.getDateAdded());
    }

    @Test
    public void testAddDccAnnotationNote() {
        final String noteText = "just a text";
        final String addedBy = "me";
        final Date now = new Date();
        final DccAnnotationNote note = new DccAnnotationNote();
        note.setNoteText(noteText);
        note.setAddedBy(addedBy);
        note.setDateAdded(now);
        annotation.addNote(note);
        assertNotNull(annotation.getNotes());
        assertEquals(1, annotation.getNotes().size());
        assertEquals(noteText, annotation.getNotes().get(0).getNoteText());
        assertEquals(addedBy, annotation.getNotes().get(0).getAddedBy());
        assertEquals(now, note.getDateAdded());
    }

    @Test
    public void testAddNoteNullDate() {
    	
        annotation.addNote("a note", "someone");
        final DccAnnotationNote note = annotation.getNotes().get(0);
        assertNotNull(note.getDateAdded());
    }
    
    @Test
    public void testConstructor() {
    	assertFalse(annotation.getRescinded());
    	assertFalse(annotation.getApproved());
    	assertNotNull(annotation.getItems());
    	assertNotNull(annotation.getNotes());
    }

    @Test
    public void testGetDiseasesAndItemTypes() {
        final DccAnnotation annotation = new DccAnnotation();
        final DccAnnotationItem item1 = new DccAnnotationItem();
        final DccAnnotationItemType itemType1 = new DccAnnotationItemType();
        final Long id1 = (long) 1 ;
        final Long id2 = (long) 2;

        item1.setItemType(itemType1);
        item1.setId(id1);
        final Tumor disease1 = new Tumor();
        item1.setDisease(disease1);
        final DccAnnotationItem item2 = new DccAnnotationItem();
        final DccAnnotationItemType itemType2 = new DccAnnotationItemType();
        item2.setItemType(itemType2);
        item2.setId(id2);
        final Tumor disease2 = new Tumor();
        item2.setDisease(disease2);
        annotation.addItem(item1);
        annotation.addItem(item2);
        assertEquals(id1, annotation.getItems().get(0).getId());
        assertEquals(id2, annotation.getItems().get(1).getId());
        final List<Tumor> diseases = annotation.getDiseases();
        assertEquals(2, diseases.size());
        assertEquals(disease1, diseases.get(0));
        assertEquals(disease2, diseases.get(1));

        final List<DccAnnotationItemType> itemTypes = annotation.getItemTypes();
        assertEquals(2, itemTypes.size());
        assertEquals(itemType1, itemTypes.get(0));
        assertEquals(itemType2, itemTypes.get(1));
    }

    /**
     * Return a <code>DccAnnotationItem</code> with the given item and item type name
     * 
     * @param item the item
     * @param itemTypeName the item type name
     * @return a <code>DccAnnotationItem</code> with the given item and item type name
     */
    private DccAnnotationItem getDccAnnotationItem(final String item, final String itemTypeName) {
		
		final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
        final DccAnnotationItemType dccAnnotationItemType = new DccAnnotationItemType();
        dccAnnotationItemType.setItemTypeName(itemTypeName);
    	dccAnnotationItem.setItem(item);
    	dccAnnotationItem.setItemType(dccAnnotationItemType);
		return dccAnnotationItem;
	}

    /**
     * Return a <code>DccAnnotationCategory</code> with the given category name
     * 
     * @param categoryName the category name
     * @return a <code>DccAnnotationCategory</code> with the given category name
     */
	private DccAnnotationCategory getDccAnnotationCategory(final String categoryName) {
		
		final DccAnnotationCategory dccAnnotationCategory = new DccAnnotationCategory();
        dccAnnotationCategory.setCategoryName(categoryName);
		return dccAnnotationCategory;
	}
}
