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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import java.util.Date;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * AnnotationAddNoteWS unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AnnotationAddNoteWSFastTest {

    private AnnotationAddNoteWS annotationAddNoteWS;

    private Mockery mockery = new JUnit4Mockery();
    private SecurityUtil mockSecurityUtil;
    private AnnotationService mockAnnotationService;

    @Before
    public void setUp() {

        mockSecurityUtil = mockery.mock(SecurityUtil.class);
        mockAnnotationService = mockery.mock(AnnotationService.class);

        annotationAddNoteWS = new AnnotationAddNoteWS();
        annotationAddNoteWS.setSecurityUtil(mockSecurityUtil);
        annotationAddNoteWS.setAnnotationService(mockAnnotationService);
    }

    @Test
    public void testAddDccAnnotationNoteToXmlWithNonNullNoteAndExistingAnnotationWhenPrincipalAuthenticated()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        testAddDccAnnotationNoteWithNonNullNoteAndExistingAnnotationWhenPrincipalAuthenticated(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationNoteToJsonWithNonNullNoteAndExistingAnnotationWhenPrincipalAuthenticated()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        testAddDccAnnotationNoteWithNonNullNoteAndExistingAnnotationWhenPrincipalAuthenticated(WebServiceUtil.ReturnType.JSON);
    }

    @Test(expected = WebApplicationException.class)
    public void testAddDccAnnotationNoteToXmlWithNonNullNoteAndNonExistingAnnotation()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        testAddDccAnnotationNoteWithNonNullNoteAndNonExistingAnnotation(WebServiceUtil.ReturnType.XML);
    }

    @Test(expected = WebApplicationException.class)
    public void testAddDccAnnotationNoteToJsonWithNonNullNoteAndNonExistingAnnotation()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        testAddDccAnnotationNoteWithNonNullNoteAndNonExistingAnnotation(WebServiceUtil.ReturnType.JSON);
    }

    @Test(expected = WebApplicationException.class)
    public void testAddDccAnnotationNoteToXmlWithNullNoteAndExistingAnnotation()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        testAddDccAnnotationNoteWithNullNoteAndExistingAnnotation(WebServiceUtil.ReturnType.XML);
    }

    @Test(expected = WebApplicationException.class)
    public void testAddDccAnnotationNoteToJsonWithNullNoteAndExistingAnnotation()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {
        testAddDccAnnotationNoteWithNullNoteAndExistingAnnotation(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationNoteToXmlDccAnnotationIdNotProvided() {
        testAddDccAnnotationNoteDccAnnotationIdNotProvided(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationNoteToJsonDccAnnotationIdNotProvided() {
        testAddDccAnnotationNoteDccAnnotationIdNotProvided(WebServiceUtil.ReturnType.JSON);
    }

    @Test
    public void testAddDccAnnotationNoteToXmlDccAnnotationIdNotANumber() {
        testAddDccAnnotationNoteDccAnnotationIdNotANumber(WebServiceUtil.ReturnType.XML);
    }

    @Test
    public void testAddDccAnnotationNoteToJsonDccAnnotationIdNotANumber() {
        testAddDccAnnotationNoteDccAnnotationIdNotANumber(WebServiceUtil.ReturnType.JSON);
    }

    /**
     * Will unit test addDccAnnotationNoteToXml() or addDccAnnotationNoteToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     *  - the annotation Id is not a number
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationNoteDccAnnotationIdNotANumber(final WebServiceUtil.ReturnType returnType) {
        testAddDccAnnotationNoteDccAnnotationIdInvalid(returnType, "A42", "Please provide a valid annotation Id");
    }

    /**
     * Will unit test addDccAnnotationNoteToXml() or addDccAnnotationNoteToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     *  - the annotation Id is null
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     */
    private void testAddDccAnnotationNoteDccAnnotationIdNotProvided(final WebServiceUtil.ReturnType returnType) {
        testAddDccAnnotationNoteDccAnnotationIdInvalid(returnType, null, "Please provide an annotation Id");
    }

    /**
     * Will unit test addDccAnnotationNoteToXml() or addDccAnnotationNoteToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     *  - the annotation Id is invalid
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     * @param dccAnnotationIdAsString the annotation Id, as String (must be invalid)
     * @param expectedErrorMessageContent the expected error message content
     */
    private void testAddDccAnnotationNoteDccAnnotationIdInvalid(final WebServiceUtil.ReturnType returnType,
                                                                final String dccAnnotationIdAsString,
                                                                final String expectedErrorMessageContent) {

        final String noteTxt = "Note text";

        try {
            switch(returnType) {
                case XML:
                    annotationAddNoteWS.addDccAnnotationNoteToXml(dccAnnotationIdAsString, noteTxt);
                    break;
                case JSON:
                    annotationAddNoteWS.addDccAnnotationNoteToJson(dccAnnotationIdAsString, noteTxt);
                    break;
            }
            fail("WebApplicationException wasn't thrown.");
            
        } catch(final WebApplicationException e) {

            final String[] expectedContents = {expectedErrorMessageContent};
            AnnotationAddWSFastTest.checkWebErrorMessage(e, expectedContents);
        }
    }

    /**
     * Will unit test addDccAnnotationNoteToXml() or addDccAnnotationNoteToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     *  - the note text is not null
     *  - the annotation to add the note to exists
     *  - the principal is authenticated
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries.AnnotationQueriesException
     */
    private void testAddDccAnnotationNoteWithNonNullNoteAndExistingAnnotationWhenPrincipalAuthenticated(final WebServiceUtil.ReturnType returnType)
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final long dccAnnotationId = 1;
        final String noteTxt = "Note text";
        final String pretendUsername = getPretendUsername();

        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getAnnotationById(dccAnnotationId);
            will(returnValue(getPretendDccAnnotation(dccAnnotationId)));
            one(mockSecurityUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue(getPretendUsername()));
            one(mockAnnotationService).addNewAnnotationNote(with(dccAnnotationId), with(noteTxt), with(pretendUsername), with(any(Date.class)));
            will(returnValue(getPretendDccAnnotationNote(noteTxt, pretendUsername)));
        }});

        DccAnnotationNote dccAnnotationNote = null;
        switch(returnType) {
            case XML:
                dccAnnotationNote = annotationAddNoteWS.addDccAnnotationNoteToXml(String.valueOf(dccAnnotationId), noteTxt);
                break;
            case JSON:
                dccAnnotationNote = annotationAddNoteWS.addDccAnnotationNoteToJson(String.valueOf(dccAnnotationId), noteTxt);
                break;
        }

        assertNotNull(dccAnnotationNote);
        assertEquals("Unexpected note text:", noteTxt, dccAnnotationNote.getNoteText());
        assertEquals("Unexpected added by:", getPretendUsername(), dccAnnotationNote.getAddedBy());
    }

    /**
     * Will unit test addDccAnnotationNoteToXml() or addDccAnnotationNoteToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     *  - the note text is not null
     *  - the annotation to add the note to does not exist
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries.AnnotationQueriesException
     */
    private void testAddDccAnnotationNoteWithNonNullNoteAndNonExistingAnnotation(final WebServiceUtil.ReturnType returnType)
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final long dccAnnotationId = 1;
        final String noteTxt = "Note text";

        mockery.checking(new Expectations() {{
            one(mockAnnotationService).getAnnotationById(dccAnnotationId);
            will(throwException(new AnnotationQueries.AnnotationQueriesException("No such Id!")));
        }});

        switch(returnType) {
            case XML:
                annotationAddNoteWS.addDccAnnotationNoteToXml(String.valueOf(dccAnnotationId), noteTxt);
                break;
            case JSON:
                annotationAddNoteWS.addDccAnnotationNoteToJson(String.valueOf(dccAnnotationId), noteTxt);
               break;
        }
    }

    /**
     * Will unit test addDccAnnotationNoteToXml() or addDccAnnotationNoteToJson(), depending on the given <code>ReturnType</code>
     * with the following preconditions:
     *  - the note text is null
     *  - the annotation to add the note to exists
     *
     * @param returnType the <code>ReturnType</code> that will decide which method gets called
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries.AnnotationQueriesException
     */
    private void testAddDccAnnotationNoteWithNullNoteAndExistingAnnotation(final WebServiceUtil.ReturnType returnType)
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final long dccAnnotationId = 1;
        final String noteTxt = null;

        mockery.checking(new Expectations() {{
            one(mockSecurityUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue(getPretendUsername()));
            one(mockAnnotationService).getAnnotationById(dccAnnotationId);
            will(returnValue(getPretendDccAnnotation(dccAnnotationId)));
            one(mockAnnotationService).addNewAnnotationNote(with(dccAnnotationId), with(noteTxt), with(getPretendUsername()), with(any(Date.class)));
            will(throwException(new AnnotationQueries.AnnotationQueriesException("Note can not be null!")));
        }});

        switch(returnType) {
            case XML:
                annotationAddNoteWS.addDccAnnotationNoteToXml(String.valueOf(dccAnnotationId), noteTxt);
                break;
            case JSON:
                annotationAddNoteWS.addDccAnnotationNoteToJson(String.valueOf(dccAnnotationId), noteTxt);
                break;
        }
    }

    /**
     * Return an authenticated principal login name
     * (for JMock expectations)
     *
     * @return an authenticated principal login name
     */
    private String getPretendUsername() {
        return "unitTestUsername";
    }

    /**
     * Return a <code>DccAnnotation</code> with the given Id
     * (for JMock expectations)
     *
     * @param dccAnnotationId the Id for the <code>DccAnnotation</code>
     * @return a <code>DccAnnotation</code> with the given Id
     */
    private DccAnnotation getPretendDccAnnotation(final long dccAnnotationId) {

        final DccAnnotation result = new DccAnnotation();
        result.setId(dccAnnotationId);

        return result;
    }

    /**
     * Return a <code>DccAnnotationNote</code> with the given note text
     * (for JMock expectations)
     *
     * @param noteTxt the note text for the <code>DccAnnotationNote</code>
     * @param addedBy the 'added by' for the <code>DccAnnotationNote</code>
     * @return a <code>DccAnnotationNote</code> with the given note text and 'added by'
     */
    private DccAnnotationNote getPretendDccAnnotationNote(final String noteTxt, final String addedBy) {

        final DccAnnotationNote result = new DccAnnotationNote();
        result.setNoteText(noteTxt);
        result.setAddedBy(addedBy);

        return result;
    }
}
