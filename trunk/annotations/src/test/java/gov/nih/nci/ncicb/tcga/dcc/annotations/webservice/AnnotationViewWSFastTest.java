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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

/**
 * AnnotationViewWS unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AnnotationViewWSFastTest {

    private Mockery context = new JUnit4Mockery();
    private AnnotationViewWS annotationViewWS;
    private AnnotationService annotationService;

    private final static String urlSeparator = "/";

    @Before
    public void setUp() {
        annotationService = context.mock(AnnotationService.class);
        annotationViewWS = new AnnotationViewWS();
        annotationViewWS.setAnnotationService(annotationService);
    }

    @Test
    public void testGetDccAnnotationToJsonWhenDccAnnotationCanBeRetrieved()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final Long dccAnnotationId = 0L;
        final DccAnnotation expectedDccAnnotation = new DccAnnotation();
        expectedDccAnnotation.setId(dccAnnotationId);

        context.checking(new Expectations() {{
            one(annotationService).getAnnotationById(0L);
            will(returnValue(expectedDccAnnotation));
        }});

        DccAnnotation actualDccAnnotation = annotationViewWS.getDccAnnotationToJson(urlSeparator + dccAnnotationId);

        assertEquals(expectedDccAnnotation, actualDccAnnotation);
    }

    @Test
    public void testGetDccAnnotationToJsonWhenDccAnnotationCanNotBeRetrieved()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final Long dccAnnotationId = 0L;
        final DccAnnotation expectedDccAnnotation = new DccAnnotation();
        expectedDccAnnotation.setId(dccAnnotationId);

        context.checking(new Expectations() {{
            one(annotationService).getAnnotationById(0L);
            will(throwException(new AnnotationQueries.AnnotationQueriesException("Can not be retrieved")));
        }});

        DccAnnotation actualDccAnnotation = null;
        try {
            actualDccAnnotation = annotationViewWS.getDccAnnotationToJson(urlSeparator + dccAnnotationId);
            fail("WebApplicationException was not thrown");
        } catch(WebApplicationException e) {
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }

        assertNull(actualDccAnnotation);
    }


    @Test
    public void testGetDccAnnotationToJsonWhenDccAnnotationIdNotProvided() throws AnnotationQueries.AnnotationQueriesException {

        //dccAnnotationId not starting with url separator as expected
        final String dccAnnotationId = null;

        DccAnnotation actualDccAnnotation = null;
        try {
            actualDccAnnotation = annotationViewWS.getDccAnnotationToJson(dccAnnotationId);
            fail("WebApplicationException was not thrown");
        } catch(WebApplicationException e) {
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }

        assertNull(actualDccAnnotation);
    }

    @Test
    public void testGetDccAnnotationToJsonWhenDccAnnotationIdProvidedButInvalid() throws AnnotationQueries.AnnotationQueriesException {

        final String dccAnnotationId = "AB01";

        DccAnnotation actualDccAnnotation = null;
        try {
            actualDccAnnotation = annotationViewWS.getDccAnnotationToJson(urlSeparator + dccAnnotationId);
            fail("WebApplicationException was not thrown");
        } catch(WebApplicationException e) {
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }

        assertNull(actualDccAnnotation);
    }

    @Test
    public void testGetDccAnnotationToXmlWhenDccAnnotationCanBeRetrieved()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final Long dccAnnotationId = 0L;
        final DccAnnotation expectedDccAnnotation = new DccAnnotation();
        expectedDccAnnotation.setId(dccAnnotationId);

        context.checking(new Expectations() {{
            one(annotationService).getAnnotationById(0L);
            will(returnValue(expectedDccAnnotation));
        }});

        DccAnnotation actualDccAnnotation = annotationViewWS.getDccAnnotationToXml(urlSeparator + dccAnnotationId);

        assertEquals(expectedDccAnnotation, actualDccAnnotation);
    }

    @Test
    public void testGetDccAnnotationToXmlWhenDccAnnotationCanNotBeRetrieved()
            throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final Long dccAnnotationId = 0L;
        final DccAnnotation expectedDccAnnotation = new DccAnnotation();
        expectedDccAnnotation.setId(dccAnnotationId);

        context.checking(new Expectations() {{
            one(annotationService).getAnnotationById(0L);
            will(throwException(new AnnotationQueries.AnnotationQueriesException("Can not be retrieved")));
        }});

        DccAnnotation actualDccAnnotation = null;
        try {
            actualDccAnnotation = annotationViewWS.getDccAnnotationToXml(urlSeparator + dccAnnotationId);
            fail("WebApplicationException was not thrown");
        } catch(WebApplicationException e) {
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }

        assertNull(actualDccAnnotation);
    }

    @Test
    public void testGetDccAnnotationToXmlWhenDccAnnotationIdNotProvided() throws AnnotationQueries.AnnotationQueriesException {

        //dccAnnotationId not starting with url separator as expected
        final String dccAnnotationId = null;

        DccAnnotation actualDccAnnotation = null;
        try {
            actualDccAnnotation = annotationViewWS.getDccAnnotationToXml(dccAnnotationId);
            fail("WebApplicationException was not thrown");
        } catch(WebApplicationException e) {
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }

        assertNull(actualDccAnnotation);
    }

    @Test
    public void testGetDccAnnotationToXmlWhenDccAnnotationIdProvidedButInvalid() throws AnnotationQueries.AnnotationQueriesException {

        final String dccAnnotationId = "AB01";

        DccAnnotation actualDccAnnotation = null;
        try {
            actualDccAnnotation = annotationViewWS.getDccAnnotationToXml(urlSeparator + dccAnnotationId);
            fail("WebApplicationException was not thrown");
        } catch(WebApplicationException e) {
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getResponse().getStatus());
        }

        assertNull(actualDccAnnotation);
    }
}
