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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationSearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.AclSecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.security.impl.SecurityUtilImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationServiceImpl;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.ui.ExtendedModelMap;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


/**
 * Test class for AnnotationController
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class AnnotationControllerImplFastTest {

    private JUnit4Mockery context = new JUnit4Mockery();
    private AnnotationControllerImpl controller;
    private AnnotationQueries mockAnnotationQueries;
    private AnnotationService mockAnnotationService;
    private ExtendedModelMap modelMap;
    private HttpSession mockSession;
    private SecurityUtil mockUtil;
    private AclSecurityUtil mockAclSecurityUtil;
    private AnnotationService annotationService;

    @Before
    public void setup() {

        annotationService = new AnnotationServiceImpl();
        mockAnnotationQueries = context.mock(AnnotationQueries.class);
        ((AnnotationServiceImpl)annotationService).setAnnotationQueries(mockAnnotationQueries);
        mockAnnotationService = context.mock(AnnotationService.class);


        mockUtil = context.mock(SecurityUtil.class);

        controller = new AnnotationControllerImpl();
        controller.setAnnotationService(annotationService);
        controller.setUtil(mockUtil);

        mockSession = context.mock(HttpSession.class);
        modelMap = new ExtendedModelMap();

        mockAclSecurityUtil = context.mock(AclSecurityUtil.class);
        ((AnnotationServiceImpl) annotationService).setAclSecurityUtil(mockAclSecurityUtil);
    }

    @Test
    public void testAddAnnotationHandler() throws Exception {
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).addNewAnnotation(with(any(DccAnnotation.class)), with(false));
            will(returnValue(10L));
            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("username"));
            one(mockAclSecurityUtil).addPermission(with(any(DccAnnotationNote.class)), with(BasePermission.WRITE));
        }});

        final ExtendedModelMap returnedMap = controller.addAnnotationHandler(mockSession, modelMap, -1L,
                1, 1L, "ABC", "Approved", 2L, "A note", false, "false");
        assertEquals(10L, returnedMap.get("annotationId"));
        DccAnnotation annotation = (DccAnnotation) returnedMap.get("annotation");
        assertNotNull(annotation);
        assertFalse(returnedMap.containsKey("errorMessage"));
    }

    @Test
    public void testAddAnnotationHandlerStrict() throws Exception {

        final String expectedErrorMessage = "This item has invalid codes";

        context.checking( new Expectations() {{
            one(mockAnnotationQueries).addNewAnnotation(with(any(DccAnnotation.class)), with(true));
            will(throwException(new AnnotationQueries.AnnotationQueriesException(expectedErrorMessage)));

            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("username"));
        }});

        final ExtendedModelMap returnedMap = controller.addAnnotationHandler(mockSession, modelMap, -1L, 1, 1L, "ABC","Approved", 2L, "A note", true, "false");

        assertTrue(returnedMap.containsKey("errorMessage"));
        assertEquals(expectedErrorMessage, returnedMap.get("errorMessage"));

        assertTrue(returnedMap.containsKey("success"));
        assertEquals(new Boolean(false), returnedMap.get("success"));
    }

    @Test
    public void testUpdateAnnotationHandler() throws Exception {

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

        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(with(any(Long.class)));
            will(returnValue(myAnnotation));
            one(mockAnnotationQueries).getAnnotationCategories();
            will(returnValue(new ArrayList<DccAnnotationCategory>()));
            one(mockAnnotationQueries).updateAnnotation(with(any(Long.class)), with(any(DccAnnotation.class)), with(false));
            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("me"));
        }});

        final ExtendedModelMap returnedMap = controller.addAnnotationHandler(mockSession, modelMap, annotationId,
                tumorId, categoryId, item, status, categoryId, note, false, "false");
        assertEquals(10L, returnedMap.get("annotationId"));
        assertEquals(myAnnotation, returnedMap.get("annotation"));
        assertFalse(returnedMap.containsKey("errorMessage"));
    }

    @Test
    public void testUpdateAnnotationHandlerStrict() throws Exception {

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

        final String expectedErrorMessage = "This item has invalid codes";

        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(with(any(Long.class)));
            will(returnValue(myAnnotation));

            one(mockAnnotationQueries).updateAnnotation(with(any(Long.class)), with(any(DccAnnotation.class)), with(true));
            will(throwException(new AnnotationQueries.AnnotationQueriesException(expectedErrorMessage)));

            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("me"));
        }});

        final ExtendedModelMap returnedMap = controller.addAnnotationHandler(mockSession, modelMap, annotationId,
                tumorId, categoryId, item, status, categoryId, note, true, "false");

        assertTrue(returnedMap.containsKey("errorMessage"));
        assertEquals(expectedErrorMessage, returnedMap.get("errorMessage"));

        assertTrue(returnedMap.containsKey("success"));
        assertEquals(new Boolean(false), returnedMap.get("success"));
    }

    @Test
    public void testRescindAnotationHandler() throws BeanException, AnnotationQueries.AnnotationQueriesException {
        final Long annotationId = 1L;
        final String updatedBy = "city lights";
        final DccAnnotation myAnnotation = new DccAnnotation();
        myAnnotation.setId(annotationId);
        myAnnotation.setApproved(true);
        final DccAnnotationCategory category = new DccAnnotationCategory();
        final Long categoryId = 3L; //
        category.setCategoryId(categoryId);
        final DccAnnotationClassification annotationClassification = new DccAnnotationClassification();
        annotationClassification.setAnnotationClassificationId(1L);
        annotationClassification.setAnnotationClassificationName(AnnotationServiceImpl.APPROVED);
        category.setAnnotationClassification(annotationClassification);
        myAnnotation.setAnnotationCategory(category);

        ((AnnotationServiceImpl) annotationService).setAclSecurityUtil(null);

        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(with(any(Long.class)));
            will(returnValue(myAnnotation));

            one(mockAnnotationQueries).updateAnnotation(with(any(Long.class)), with(any(DccAnnotation.class)), with(false));

            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("me"));

            one(mockAnnotationQueries).addNewAnnotationNote(with(any(Long.class)), with(any(DccAnnotationNote.class)));
        }});

        final ExtendedModelMap emm = controller.addAnnotationHandler(mockSession, modelMap,
                -999L,new Integer(-999),-999L,null,"ignored",-999L,null,false,"true");

        assertTrue(emm.containsKey("success"));
        assertEquals(new Boolean(true), emm.get("success"));
        assertFalse(emm.containsKey("errorMessage"));

        assertNotNull(emm.get("annotation"));
        assertEquals(true, ((DccAnnotation)emm.get("annotation")).getRescinded());
        assertEquals(1, ((DccAnnotation)emm.get("annotation")).getNotes().size());
    }

    @Test
    public void testRescindAnnotationHandlerNotRescinded() throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final long annotationId = 1L;
        final String updatedBy = "frenzy";
        final DccAnnotation myAnnotation = new DccAnnotation();
        myAnnotation.setApproved(false);
        myAnnotation.setId(annotationId);
        final String expectedErrorMessage = "what kind of moon is a blue moon?";
        ((AnnotationServiceImpl) annotationService).setAclSecurityUtil(null);

        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(with(any(Long.class)));
            will(returnValue(myAnnotation));

            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("me"));
        }});

        final ExtendedModelMap emm = controller.addAnnotationHandler(mockSession, modelMap,
                -999L,new Integer(-999),-999L,null,"ignored",-999L,null,false,"true");

        assertTrue(emm.containsKey("errorMessage"));
        assertEquals("An annotation must be approved in order to be rescinded.", emm.get("errorMessage"));

        assertTrue(emm.containsKey("success"));
        assertEquals(new Boolean(false), emm.get("success"));
    }

    @Test
    public void testGetItemTypesHandler() {
        final List<Map<String, Object>> itemTypes = new ArrayList<Map<String, Object>>();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getItemTypes();
            will(returnValue(itemTypes));
        }});

        ExtendedModelMap returnedMap = controller.getItemTypes(modelMap);
        assertTrue(returnedMap.containsKey("itemTypes"));
        assertSame(itemTypes, returnedMap.get("itemTypes"));
        assertFalse(returnedMap.containsKey("errorMessage"));
    }

    @Test
    public void testGetAnnotationCategoriesHandler() {
        final List<Map<String, Object>> annotationCats = new ArrayList<Map<String, Object>>();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationCategories();
            will(returnValue(annotationCats));
        }});

        ExtendedModelMap returnedMap = controller.getAnnotationCategories(modelMap);
        assertTrue(returnedMap.containsKey("annotationCategories"));
        assertSame(annotationCats, returnedMap.get("annotationCategories"));
        assertFalse(returnedMap.containsKey("errorMessage"));
    }

    @Test
    public void testGetDiseasesHandler() {
        final List<Map<String, Object>> diseases = new ArrayList<Map<String, Object>>();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getActiveDiseases();
            will(returnValue(diseases));
        }});

        ExtendedModelMap returnedMap = controller.getActiveDiseases(modelMap);
        assertTrue(returnedMap.containsKey("diseases"));
        assertSame(diseases, returnedMap.get("diseases"));
        assertFalse(returnedMap.containsKey("errorMessage"));
    }

    @Test
    public void testGetAnnotationHandler()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotation annotation = new DccAnnotation();
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(2L);
            will(returnValue(annotation));
        }});

        ExtendedModelMap returnedMap = controller.getAnnotationById(modelMap, 2L);
        assertTrue(returnedMap.containsKey("annotation"));
        assertSame(annotation, returnedMap.get("annotation"));
        assertFalse(returnedMap.containsKey("errorMessage"));
    }

    @Test
    public void testAddNoteHandler()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).addNewAnnotationNote(with(3L), with(any(DccAnnotationNote.class)));
            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("username"));
            one(mockAclSecurityUtil).addPermission(with(any(DccAnnotationNote.class)), with(BasePermission.WRITE));
        }});
        ExtendedModelMap returnedModel = controller.addNoteToAnnotation(mockSession, modelMap, 3L, "hello");
        assertFalse(String.valueOf(returnedModel.get("errorMessage")), returnedModel.containsKey("errorMessage"));
        assertTrue(returnedModel.containsKey("noteId"));
        assertTrue(returnedModel.containsKey("note"));
        DccAnnotationNote note = (DccAnnotationNote) returnedModel.get("note");
        assertEquals("hello", note.getNoteText());
        assertEquals("username", note.getAddedBy());
        assertNotNull(note.getDateAdded());
    }

    @Test
    public void testEditNoteHandler()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotationNote note = new DccAnnotationNote();
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getAnnotationNoteById(12L);
            will(returnValue(note));
            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("username"));
            one(mockAnnotationQueries).editAnnotationNote(null, note, "new text", "username");
        }});
        ExtendedModelMap returnedModel = controller.editAnnotationNote(mockSession, modelMap, 12L, "new text");
        assertTrue(returnedModel.containsKey("success"));
        assertTrue(returnedModel.containsKey("note"));
        assertSame(note, returnedModel.get("note"));
        assertFalse(returnedModel.containsKey("errorMessage"));
    }

    @Test
    public void testSearchAnnotations() {
        final List<DccAnnotation> results = new ArrayList<DccAnnotation>();
        final List<Long> idResults = new ArrayList<Long>();
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(with(any(AnnotationSearchCriteria.class)));
            will(returnValue(idResults ));
            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue(null));
            one(mockSession).setAttribute("lastSearchResults", results);
        }});
        ExtendedModelMap returnedModel = controller.searchAnnotations(mockSession, modelMap,
                null, "a barcode", null, 45L, 50L, "a keyword", false, null, null, null, null);
        assertTrue(returnedModel.containsKey("annotations"));
        assertNotNull(returnedModel.get("annotations"));
        assertFalse(returnedModel.containsKey("errorMessage"));
    }

    @Test
    public void testSearchAnnotationsAuthenticated() {
        final List<DccAnnotation> results = new ArrayList<DccAnnotation>();
        final List<Long> idResults = new ArrayList<Long>();
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(with(any(AnnotationSearchCriteria.class)));
            will(returnValue(idResults ));
            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("someone"));
            one(mockSession).setAttribute("lastSearchResults", results);
        }});
        ExtendedModelMap returnedModel = controller.searchAnnotations(mockSession, modelMap,
                null, "a barcode", null, 45L, 50L, "a keyword", false, null, null, null, null);
        assertTrue(returnedModel.containsKey("annotations"));
        assertNotNull(returnedModel.get("annotations"));
        assertFalse(returnedModel.containsKey("errorMessage"));
    }

    @Test
    public void testSearchAnnotationsWithExistingAnnotationId()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final Long annotationId = 1L;

        final DccAnnotation expectedDccAnnotation = getDccAnnotation(annotationId);
        final List<DccAnnotation> expectedLastSearchResult = new ArrayList<DccAnnotation>();
        expectedLastSearchResult.add(expectedDccAnnotation);

        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(annotationId);
            will(returnValue(expectedDccAnnotation));
            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("someone"));
            one(mockSession).setAttribute("lastSearchResults", expectedLastSearchResult);
        }});

        final ExtendedModelMap returnedModel = controller.searchAnnotations(mockSession, modelMap,
                null, null, null, null, null, null, false, null, null, String.valueOf(annotationId), null);

        assertNotNull(returnedModel);
        assertFalse(returnedModel.containsKey("errorMessage"));
        assertTrue(returnedModel.containsKey("annotations"));
        final List<DccAnnotation> actualDccAnnotations = (List<DccAnnotation>)returnedModel.get("annotations");
        assertNotNull(actualDccAnnotations);
        assertEquals(1, actualDccAnnotations.size());
        assertSame(actualDccAnnotations.get(0), expectedDccAnnotation);
    }

    @Test
    public void testSearchAnnotationsWithNonExistingAnnotationId()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final Long annotationId = 2L;

        final List<DccAnnotation> expectedLastSearchResult = new ArrayList<DccAnnotation>();

        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(annotationId);
            will(throwException(new AnnotationQueries.AnnotationQueriesException("DccAnnotation with Id " + annotationId + " does not exist.")));
            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("someone"));
            one(mockSession).setAttribute("lastSearchResults", expectedLastSearchResult);
        }});

        final ExtendedModelMap returnedModel = controller.searchAnnotations(mockSession, modelMap,
                null, null, null, null, null, null, false, null, null, String.valueOf(annotationId), null);

        assertNotNull(returnedModel);
        assertFalse(returnedModel.containsKey("errorMessage"));
        assertTrue(returnedModel.containsKey("annotations"));
        final List<DccAnnotation> actualDccAnnotations = (List<DccAnnotation>)returnedModel.get("annotations");
        assertNotNull(actualDccAnnotations);
        assertEquals(0, actualDccAnnotations.size());
    }

    @Test
    public void testSearchMultipleAnnotationIds()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        final DccAnnotation annotation1 = getDccAnnotation(1L);
        final DccAnnotation annotation2 = getDccAnnotation(2L);
        final DccAnnotation annotation3 = getDccAnnotation(3L);
        final DccAnnotation annotation4 = getDccAnnotation(4L);

        final List<DccAnnotation> expectedLastSearchResult = new ArrayList<DccAnnotation>();
        expectedLastSearchResult.add(annotation1);
        expectedLastSearchResult.add(annotation2);
        expectedLastSearchResult.add(annotation3);
        expectedLastSearchResult.add(annotation4);

        context.checking( new Expectations() {{
            one(mockAnnotationQueries).getAnnotationById(1L);
            will(returnValue(annotation1));
            one(mockAnnotationQueries).getAnnotationById(2L);
            will(returnValue(annotation2));
            one(mockAnnotationQueries).getAnnotationById(3L);
            will(returnValue(annotation3));
            one(mockAnnotationQueries).getAnnotationById(4L);
            will(returnValue(annotation4));
            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue("someone"));
            one(mockSession).setAttribute("lastSearchResults", expectedLastSearchResult);
        }});

        final ExtendedModelMap returnedModel = controller.searchAnnotations(mockSession, modelMap,
                null, null, null, null, null, null, false, null, null, "1\t2,3 ; 4", null);

        assertTrue(returnedModel.containsKey("annotations"));
        assertTrue(((List<DccAnnotation>)returnedModel.get("annotations")).contains(annotation1));
        assertTrue(((List<DccAnnotation>)returnedModel.get("annotations")).contains(annotation2));
        assertTrue(((List<DccAnnotation>)returnedModel.get("annotations")).contains(annotation3));
        assertTrue(((List<DccAnnotation>)returnedModel.get("annotations")).contains(annotation4));
    }

    @Test
    public void testSearchAuthenticatedUserSearchingItsOwnAnnotations() {

        final String authenticatedUsername = "testAuthenticatedUsername";
        final String annotatorUsername = authenticatedUsername;
        checkSearchByAnnotator(authenticatedUsername, annotatorUsername);
    }

    @Test
    public void testSearchAuthenticatedUserSearchingOthersAnnotations() {
        checkSearchByAnnotator("testAuthenticatedUsername", "testAnnotatedUsername");
    }

    @Test
    public void testSearchNonAuthenticatedUserSearchingAnnotationsByAnnotator() {
        checkSearchByAnnotator(SecurityUtilImpl.NOT_AUTHENTICATED, "testAnnotatedUsername");
    }

    @Test
    public void testGetClassifications() {
        final List<DccAnnotationClassification> classifications = new ArrayList<DccAnnotationClassification>();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationClassifications();
            will(returnValue(classifications));
        }});
        final ExtendedModelMap returnedModel = controller.getAnnotationClassifications(modelMap);
        assertEquals(classifications, returnedModel.get("annotationClassifications"));

    }

    /**
     * Search with the given authenticated username and annotator username
     * and verify expectations and assertions
     *
     * @param authenticatedUsername the authenticated username
     * @param annotatorUsername the annotator username
     */
    private void checkSearchByAnnotator(final String authenticatedUsername, final String annotatorUsername) {

        final List<DccAnnotation> expectedResults = new ArrayList<DccAnnotation>();

        final List<Long> idResults = new ArrayList<Long>();
        context.checking( new Expectations() {{
            one(mockAnnotationQueries).findMatchingAnnotationIds(with(any(AnnotationSearchCriteria.class)));
            will(returnValue(idResults ));
            one(mockUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue(authenticatedUsername));
            one(mockSession).setAttribute("lastSearchResults", expectedResults);
        }});

        ExtendedModelMap returnedModel = controller.searchAnnotations(mockSession, modelMap,
                null, null, null, null, null, null, false, null, null, "", annotatorUsername);

        assertNotNull(returnedModel);
        assertTrue(returnedModel.containsKey("annotations"));
        assertNotNull(returnedModel.get("annotations"));
        assertFalse(returnedModel.containsKey("errorMessage"));
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
}
