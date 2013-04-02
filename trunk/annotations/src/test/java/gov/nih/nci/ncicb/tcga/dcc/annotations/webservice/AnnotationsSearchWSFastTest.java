/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * AnnotationsSearchWS unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AnnotationsSearchWSFastTest {

    private Mockery context = new JUnit4Mockery();
    private AnnotationsSearchWS annotationsSearchWS;
    private AnnotationService mockAnnotationService;
    private TumorQueries mockTumorQueries;
    private List<DccAnnotation> expectedDccAnnotations;

    @Before
    public void setUp() {
        mockAnnotationService = context.mock(AnnotationService.class);

        annotationsSearchWS = new AnnotationsSearchWS();
        annotationsSearchWS.setAnnotationService(mockAnnotationService);

        mockTumorQueries = context.mock(TumorQueries.class);
        annotationsSearchWS.setTumorQueries(mockTumorQueries);

        expectedDccAnnotations = new ArrayList<DccAnnotation>();
    }

    @Test
    public void testSearchToXmlByDisease() {
        final String disease = "TEST";
        context.checking(new Expectations() {{
            one(mockTumorQueries).getTumorIdByName(disease);
            will(returnValue(123));
            one(mockAnnotationService).searchAnnotations(123, null, null, null, null, null, false, null, false, null, null, null, null);
            will(returnValue(new ArrayList<DccAnnotation>()));
        }});

        annotationsSearchWS.searchDccAnnotationsToXml("TEST", null, null, null, null, null, false, null, null, null, null);
    }

    @Test (expected = WebApplicationException.class)
    public void testSearchToXmlByDiseaseNotFound() {
        final String disease = "TEST";
        context.checking(new Expectations() {{
            one(mockTumorQueries).getTumorIdByName(disease);
            will(returnValue(null));            
        }});

        annotationsSearchWS.searchDccAnnotationsToXml("TEST", null, null, null, null, null, false, null, null, null, null);
    }

    @Test
    public void testSearchDccAnnotationsToXmlResultNotNull() throws AnnotationQueries.AnnotationQueriesException {

        final String item = "item";
        final Long categoryId = 1L;
        final Long itemTypeId = 1L;
        final String keyword = "keyword";
        final boolean itemExact = false;

        context.checking(new Expectations() {{
            one(mockAnnotationService).searchAnnotations(null, item, categoryId, null, itemTypeId, keyword, itemExact, null, false, null, null, null, null);
            will(returnValue(expectedDccAnnotations));
        }});

        final List<DccAnnotation> dccAnnotations =
                annotationsSearchWS.searchDccAnnotationsToXml(null, item, null, ""+categoryId, ""+itemTypeId, keyword, itemExact, null, null, null, null);

        assertNotNull("Search result should not be null", dccAnnotations);
    }

    @Test
    public void testSearchDccAnnotationsToXmlResultContainsOneElement() {

        final String item = "item";
        final Long categoryId = 1L;
        final Long itemTypeId = 1L;
        final String keyword = "keyword";
        final DccAnnotation dccAnnotation = new DccAnnotation();
        final boolean itemExact = false;

        expectedDccAnnotations.add(dccAnnotation);

        context.checking(new Expectations() {{
            one(mockAnnotationService).searchAnnotations(null, item, categoryId, null, itemTypeId, keyword, itemExact, null, false, null, null, null, null);
            will(returnValue(expectedDccAnnotations));
        }});

        final List<DccAnnotation> dccAnnotations =
                annotationsSearchWS.searchDccAnnotationsToXml(null, item, null, ""+categoryId, ""+itemTypeId, keyword, itemExact, null, null, null, null);

        assertNotNull("Search result should not be null", dccAnnotations);
        assertTrue("Search result should contain expected DccAnnotation", dccAnnotations.contains(dccAnnotation));
    }

    @Test
    public void testSearchDccAnnotationsToJsonResultNotNull() throws AnnotationQueries.AnnotationQueriesException {

        final String item = "item";
        final Long categoryId = 1L;
        final Long itemTypeId = 1L;
        final String keyword = "keyword";
        final boolean itemExact = false;

        context.checking(new Expectations() {{
            one(mockAnnotationService).searchAnnotations(null, item, categoryId, null, itemTypeId, keyword, itemExact, null, false, null, null, null, null);
            will(returnValue(expectedDccAnnotations));
        }});

        final List<DccAnnotation> dccAnnotations =
                annotationsSearchWS.searchDccAnnotationsToJson(null, item, null, ""+categoryId, ""+itemTypeId, keyword, itemExact, null, null, null, null);

        assertNotNull("Search result should not be null", dccAnnotations);
    }

    @Test
    public void testSearchDccAnnotationsToJsonResultContainsOneElement() {

        final String item = "item";
        final Long categoryId = 1L;
        final Long itemTypeId = 1L;
        final String keyword = "keyword";
        final DccAnnotation dccAnnotation = new DccAnnotation();
        final boolean itemExact = false;

        expectedDccAnnotations.add(dccAnnotation);

        context.checking(new Expectations() {{
            one(mockAnnotationService).searchAnnotations(null, item, categoryId, null, itemTypeId, keyword, itemExact, null, false, null, null, null, null);
            will(returnValue(expectedDccAnnotations));
        }});

        final List<DccAnnotation> dccAnnotations =
                annotationsSearchWS.searchDccAnnotationsToJson(null, item, null, ""+categoryId, ""+itemTypeId, keyword, itemExact, null, null, null, null);

        assertNotNull("Search result should not be null", dccAnnotations);
        assertTrue("Search result should contain expected DccAnnotation", dccAnnotations.contains(dccAnnotation));
    }

    @Test
    public void testSearchDccAnnotationsWithRowLimit() {

        context.checking(new Expectations() {{
            one(mockAnnotationService).searchAnnotations(null, null, null, null, null, null, false, null, false, null, 10, null, null);
        }});

        annotationsSearchWS.searchDccAnnotationsToJson(null, null, null, null, null, null, false, null, "10", null, null);
    }

    @Test
    public void testSearchDccAnnotationsToJsonWithRowLimitInvalid() {

        try {
            annotationsSearchWS.searchDccAnnotationsToJson(null, null, null, null, null, null, false, null, "3.5", null, null);
            fail("exception was not thrown");
        } catch(Exception exceptionTest) {
            assertTrue("webAppException is thrown for invalid limit",(exceptionTest instanceof WebApplicationException)); 
        }
    }

    @Test
    public void testSearchDccAnnotationsToXmlWithRowLimitInvalid() {

        try {
            annotationsSearchWS.searchDccAnnotationsToXml(null, null, null, null, null, null, false, null, "3.4", null, null);
            fail("exception was not thrown");
        } catch(Exception exceptionTest) {
            assertTrue("webAppException is thrown for invalid limit",(exceptionTest instanceof WebApplicationException));
        }
    }

    @Test
    public void testSearchDccAnnotationsToJsonpWithRowLimitInvalid() {

        try {
            annotationsSearchWS.searchDccAnnotationsToJsonP(null, null, null, null, null, null, false, null, "abc", null, null, null);
            fail("exception was not thrown");
        } catch(Exception exceptionTest) {
            assertTrue("webAppException is thrown for invalid limit",(exceptionTest instanceof WebApplicationException));
        }
    }

    @Test
    public void testSearchDccAnnotationsToXmlWithAnnotationIdValid() {

        final Long annotationId = 1L;
        final List<DccAnnotation> expectedDccAnnotations = getDccAnnotations(annotationId);

        context.checking(new Expectations() {{
            one(mockAnnotationService).searchAnnotations(null, null, null, null, null, null, false, null, false, null, null, Arrays.asList(annotationId), null);
            will(returnValue(expectedDccAnnotations));
        }});

        final List<DccAnnotation> actualDccAnnotations = annotationsSearchWS.searchDccAnnotationsToJson(
                null, null, null, null, null, null, false, null, null, annotationId.toString(), null);

        assertNotNull(actualDccAnnotations);
        assertEquals(1, actualDccAnnotations.size());
        assertEquals(annotationId, actualDccAnnotations.get(0).getId());
    }

    @Test
    public void testSearchDccAnnotationsToXmlWithAnnotationIds() {
        final List<Long> expectedAnnotationIds = Arrays.asList(1L, 2L, 3L, 4L);
        final List<DccAnnotation> results = getDccAnnotations(1L, 2L, 3L, 4L);
        context.checking(new Expectations() {{
            one(mockAnnotationService).searchAnnotations(null, null, null, null, null, null, false, null, false, null, null, expectedAnnotationIds, null);
            will(returnValue(results));
        }});
        final List<DccAnnotation> actualDccAnnotations = annotationsSearchWS.searchDccAnnotationsToJson(
                null, null, null, null, null, null, false, null, null, "1,2;3, 4", null);
        assertEquals(results, actualDccAnnotations);
    }

    @Test
    public void testSearchDccAnnotationsToXmlWithAnnotationIdInvalid() {

        final String annotationIdAsString = "notALong";
        List<DccAnnotation> actualDccAnnotations = null;

        try {
            actualDccAnnotations = annotationsSearchWS.searchDccAnnotationsToXml(null, null, null, null, null, null, false, null, null, annotationIdAsString, null);
            fail("WebApplicationException should have been raised.");

        } catch(final WebApplicationException e) {

            assertNull(actualDccAnnotations);
            final String[] expectedContents = {
                    "HTTP STATUS 500 - Internal Server Error.",
                    "The query parameter provided ('notALong') contains an invalid long. Please provide a valid long."
            };

            AnnotationAddWSFastTest.checkWebErrorMessage(e, expectedContents);
        }
    }

    @Test
    public void testSearchDccAnnotationsToXmlNonAuthenticatedNullByAnnotator() {

        final String authenticatedUsername = null;
        final String annotatorUsername = "testAnnotatorUsername";

        context.checking(new Expectations() {{
            one(mockAnnotationService).searchAnnotations(null, null, null, null, null, null, false, null, false, authenticatedUsername, null, null, annotatorUsername);
            will(returnValue(expectedDccAnnotations));
        }});

        final List<DccAnnotation> actualDccAnnotations = annotationsSearchWS.searchDccAnnotationsToXml(
                null, null, null, null, null, null, false, null, null, null, annotatorUsername);

        assertNotNull(actualDccAnnotations);
        assertEquals(0, actualDccAnnotations.size());
    }

    /**
     * Return a <code>List</code> of <code>DccAnnotation</code>s with <code>DccAnnotation</code>s that have the given annotationId
     *
     * @param annotationIds the annotation Ids of the <code>DccAnnotation</code>s in the <code>List</code>
     * @return a <code>List</code> of <code>DccAnnotation</code> with <code>DccAnnotation</code>s that have the given annotationIds
     */
    private List<DccAnnotation> getDccAnnotations(final Long... annotationIds) {

        final List<DccAnnotation> result = new ArrayList<DccAnnotation>();
        for (final Long annotationId : annotationIds) {
            final DccAnnotation dccAnnotation = new DccAnnotation();
            dccAnnotation.setId(annotationId);
            result.add(dccAnnotation);
        }
        return result;
    }
}
