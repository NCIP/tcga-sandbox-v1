/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

/**
 * AnnotationNoteEditWS unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AnnotationNoteEditWSFastTest {

    private Mockery context = new JUnit4Mockery();
    private AnnotationNoteEditWS annotationNoteEditWS;
    private AnnotationService annotationService;
    private SecurityUtil securityUtil;


    @Before
    public void setUp() {
        annotationService = context.mock(AnnotationService.class);
        securityUtil = context.mock(SecurityUtil.class);

        annotationNoteEditWS = new AnnotationNoteEditWS();
        annotationNoteEditWS.setAnnotationService(annotationService);
        annotationNoteEditWS.setSecurityUtil(securityUtil);
    }

    @Test
    public void testEditXmlExistingNoteWithNonNullNodeTxtValue()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final Long dccAnnotationNoteId = 0L;
        final String noteField = "this is a note";
        final String authenticatedPrincipalLoginName = "testUser";

        final Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();

        final DccAnnotationNote expectedDccAnnotationNote = new DccAnnotationNote();
        expectedDccAnnotationNote.setNoteId(dccAnnotationNoteId);
        expectedDccAnnotationNote.setNoteText(noteField);
        expectedDccAnnotationNote.setEditedBy(authenticatedPrincipalLoginName);
        expectedDccAnnotationNote.setDateEdited(date);

        final Sequence sequence = context.sequence("sequence");

        context.checking(new Expectations() {{

            one(securityUtil).getAuthenticatedPrincipalLoginName();
            inSequence(sequence);
            will(returnValue(authenticatedPrincipalLoginName));

            one(annotationService).editAnnotationNote(dccAnnotationNoteId, noteField, authenticatedPrincipalLoginName);
            inSequence(sequence);

            one(annotationService).getAnnotationNoteById(dccAnnotationNoteId);
            inSequence(sequence);
            will(returnValue(expectedDccAnnotationNote));
        }});

        DccAnnotationNote dccAnnotationNote = annotationNoteEditWS.editDccAnnotationNoteToXml(dccAnnotationNoteId, noteField);

        assertEquals(dccAnnotationNoteId, dccAnnotationNote.getNoteId());
        assertEquals(noteField, dccAnnotationNote.getNoteText());
        assertEquals(authenticatedPrincipalLoginName, dccAnnotationNote.getEditedBy());
        assertEquals(date, dccAnnotationNote.getDateEdited());
    }

    @Test
    public void testEditXmlNonExistingNote()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final long dccAnnotationNoteId = 0;
        final String noteField = "this is a note";
        final String authenticatedPrincipalLoginName = "testUser";

        final Sequence sequence = context.sequence("sequence");

        context.checking(new Expectations() {{

            one(securityUtil).getAuthenticatedPrincipalLoginName();
            inSequence(sequence);
            will(returnValue(authenticatedPrincipalLoginName));

            one(annotationService).editAnnotationNote(dccAnnotationNoteId, noteField, authenticatedPrincipalLoginName);
            inSequence(sequence);
            will(throwException(new AnnotationQueries.AnnotationQueriesException("DccAnnotation can not be found")));
        }});

        DccAnnotationNote dccAnnotationNote = null;
        try {
            dccAnnotationNote = annotationNoteEditWS.editDccAnnotationNoteToXml(dccAnnotationNoteId, noteField);
            fail("WebApplicationException was not thrown");

        } catch(WebApplicationException e) {
            assertNull(dccAnnotationNote);
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }
    }

    @Test
    public void testEditXmlExistingNoteWithNullNoteTxtValue() throws AnnotationQueries.AnnotationQueriesException {

        final long dccAnnotationNoteId = 0;
        final String noteField = null;
        final String authenticatedPrincipalLoginName = "testUser";

        final Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();

        final DccAnnotationNote expectedDccAnnotationNote = new DccAnnotationNote();
        expectedDccAnnotationNote.setNoteId(dccAnnotationNoteId);
        expectedDccAnnotationNote.setNoteText(noteField);
        expectedDccAnnotationNote.setEditedBy(authenticatedPrincipalLoginName);
        expectedDccAnnotationNote.setDateEdited(date);

        DccAnnotationNote dccAnnotationNote = null;
        try {
            dccAnnotationNote = annotationNoteEditWS.editDccAnnotationNoteToXml(dccAnnotationNoteId, noteField);
            fail("WebApplicationException was not thrown");

        } catch(WebApplicationException e) {
            assertNull(dccAnnotationNote);
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }
    }

    @Test
    public void testEditXmlExistingNoteWithNonNullNodeTxtValueThrowException()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final long dccAnnotationNoteId = 0;
        final String noteField = "this is a note";
        final String authenticatedPrincipalLoginName = "testUser";

        final Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();

        final DccAnnotationNote expectedDccAnnotationNote = new DccAnnotationNote();
        expectedDccAnnotationNote.setNoteId(dccAnnotationNoteId);
        expectedDccAnnotationNote.setNoteText(noteField);
        expectedDccAnnotationNote.setEditedBy(authenticatedPrincipalLoginName);
        expectedDccAnnotationNote.setDateEdited(date);

        final Sequence sequence = context.sequence("sequence");

        context.checking(new Expectations() {{

            one(securityUtil).getAuthenticatedPrincipalLoginName();
            inSequence(sequence);
            will(returnValue(authenticatedPrincipalLoginName));

            one(annotationService).editAnnotationNote(dccAnnotationNoteId, noteField, authenticatedPrincipalLoginName);
            inSequence(sequence);
            will(throwException(new AnnotationQueries.AnnotationQueriesException("")));
        }});

        try {
            annotationNoteEditWS.editDccAnnotationNoteToXml(dccAnnotationNoteId, noteField);
            fail("WebApplicationException was not thrown");

        } catch(WebApplicationException e) {
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }
    }

    @Test
    public void testEditJsonExistingNoteWithNonNullNodeTxtValue()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final Long dccAnnotationNoteId = 0L;
        final String noteField = "this is a note";
        final String authenticatedPrincipalLoginName = "testUser";

        final Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();

        final DccAnnotationNote expectedDccAnnotationNote = new DccAnnotationNote();
        expectedDccAnnotationNote.setNoteId(dccAnnotationNoteId);
        expectedDccAnnotationNote.setNoteText(noteField);
        expectedDccAnnotationNote.setEditedBy(authenticatedPrincipalLoginName);
        expectedDccAnnotationNote.setDateEdited(date);

        final Sequence sequence = context.sequence("sequence");

        context.checking(new Expectations() {{

            one(securityUtil).getAuthenticatedPrincipalLoginName();
            inSequence(sequence);
            will(returnValue(authenticatedPrincipalLoginName));

            one(annotationService).editAnnotationNote(dccAnnotationNoteId, noteField, authenticatedPrincipalLoginName);
            inSequence(sequence);

            one(annotationService).getAnnotationNoteById(dccAnnotationNoteId);
            inSequence(sequence);
            will(returnValue(expectedDccAnnotationNote));
        }});

        DccAnnotationNote dccAnnotationNote = annotationNoteEditWS.editDccAnnotationNoteToJson(dccAnnotationNoteId, noteField);

        assertEquals(dccAnnotationNoteId, dccAnnotationNote.getNoteId());
        assertEquals(noteField, dccAnnotationNote.getNoteText());
        assertEquals(authenticatedPrincipalLoginName, dccAnnotationNote.getEditedBy());
        assertEquals(date, dccAnnotationNote.getDateEdited());
    }

    @Test
    public void testEditJsonNonExistingNote()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final long dccAnnotationNoteId = 0;
        final String noteField = "this is a note";
        final String authenticatedPrincipalLoginName = "testUser";

        final Sequence sequence = context.sequence("sequence");

        context.checking(new Expectations() {{

            one(securityUtil).getAuthenticatedPrincipalLoginName();
            inSequence(sequence);
            will(returnValue(authenticatedPrincipalLoginName));

            one(annotationService).editAnnotationNote(dccAnnotationNoteId, noteField, authenticatedPrincipalLoginName);
            inSequence(sequence);
            will(throwException(new AnnotationQueries.AnnotationQueriesException("DccAnnotation can not be found")));
        }});

        DccAnnotationNote dccAnnotationNote = null;
        try {
            dccAnnotationNote = annotationNoteEditWS.editDccAnnotationNoteToJson(dccAnnotationNoteId, noteField);
            fail("WebApplicationException was not thrown");

        } catch(WebApplicationException e) {
            assertNull(dccAnnotationNote);
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }
    }

    @Test
    public void testEditJsonExistingNoteWithNullNoteTxtValue() throws AnnotationQueries.AnnotationQueriesException {

        final long dccAnnotationNoteId = 0L;
        final String noteField = null;
        final String authenticatedPrincipalLoginName = "testUser";

        final Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();

        final DccAnnotationNote expectedDccAnnotationNote = new DccAnnotationNote();
        expectedDccAnnotationNote.setNoteId(dccAnnotationNoteId);
        expectedDccAnnotationNote.setNoteText(noteField);
        expectedDccAnnotationNote.setEditedBy(authenticatedPrincipalLoginName);
        expectedDccAnnotationNote.setDateEdited(date);

        DccAnnotationNote dccAnnotationNote = null;
        try {
            dccAnnotationNote = annotationNoteEditWS.editDccAnnotationNoteToJson(dccAnnotationNoteId, noteField);
            fail("WebApplicationException was not thrown");

        } catch(WebApplicationException e) {
            assertNull(dccAnnotationNote);
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }
    }

    @Test
    public void testEditJsonExistingNoteWithNonNullNodeTxtValueThrowException()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final long dccAnnotationNoteId = 0;
        final String noteField = "this is a note";
        final String authenticatedPrincipalLoginName = "testUser";

        final Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();

        final DccAnnotationNote expectedDccAnnotationNote = new DccAnnotationNote();
        expectedDccAnnotationNote.setNoteId(dccAnnotationNoteId);
        expectedDccAnnotationNote.setNoteText(noteField);
        expectedDccAnnotationNote.setEditedBy(authenticatedPrincipalLoginName);
        expectedDccAnnotationNote.setDateEdited(date);

        final Sequence sequence = context.sequence("sequence");

        context.checking(new Expectations() {{

            one(securityUtil).getAuthenticatedPrincipalLoginName();
            inSequence(sequence);
            will(returnValue(authenticatedPrincipalLoginName));

            one(annotationService).editAnnotationNote(dccAnnotationNoteId, noteField, authenticatedPrincipalLoginName);
            inSequence(sequence);
            will(throwException(new AnnotationQueries.AnnotationQueriesException("")));
        }});

        try {
            annotationNoteEditWS.editDccAnnotationNoteToJson(dccAnnotationNoteId, noteField);
            fail("WebApplicationException was not thrown");

        } catch(WebApplicationException e) {
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }
    }
}
