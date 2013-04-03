/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtilFastTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test class for GenerateUUIDWebService
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class GenerateUUIDWebServiceFastTest {

    private Mockery context = new JUnit4Mockery();
    private GenerateUUIDWebService generateUUIDWS;
    private GenerateUUIDWebService generateUUIDWSBad;
    private UUIDService uuidService;
    private final String centerName = "centerName", centerType = "centerType";
    private final List<UUIDDetail> expectedList = new ArrayList<UUIDDetail>();

    @Before
    public void setUp() throws UUIDException {
        generateUUIDWS = new GenerateUUIDWebService(1, centerName, centerType);
        generateUUIDWSBad = new GenerateUUIDWebService(1, null, null);
        uuidService = context.mock(UUIDService.class);
        expectedList.add(new UUIDDetail());
        generateUUIDWS.setUuidService(uuidService);
        generateUUIDWSBad.setUuidService(uuidService);
        UUIDWebServiceUtilImpl wsUtil = new UUIDWebServiceUtilImpl();
        wsUtil.setService(uuidService);
        generateUUIDWS.setWebServiceUtil(wsUtil);
        generateUUIDWSBad.setWebServiceUtil(wsUtil);
    }

    @Test
    public void testGenerateUUIDToJSON() throws UUIDException {
        mockGenerateWebService();
        List<UUIDDetail> actualList = generateUUIDWS.generateUUIDToJSON();
        assertNotNull(actualList);
        assertEquals(expectedList, actualList);
    }

    @Test
    public void testGenerateUUIDToXML() throws UUIDException {
        mockGenerateWebService();
        List<UUIDDetail> actualList = generateUUIDWS.generateUUIDToXML();
        assertNotNull(actualList);
        assertEquals(expectedList, actualList);
    }

    @Test
    public void testGenerateUUIDToXMLWithNoCenterParam() throws Exception {

        try {
            generateUUIDWSBad.generateUUIDToXML();
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final int expectedStatusCode = HttpStatusCode.OK;
            final String expectedInvalidValue = "Missing center name and/or center type.";
            final String expectedErrorMessage = "Both center name and center type should be specified.";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(),
                    expectedStatusCode, expectedInvalidValue, expectedErrorMessage, MediaType.APPLICATION_XML);
        }
    }

    @Test
    public void testGenerateUUIDToJSONWithNoCenterParam() throws Exception {

        try {
            generateUUIDWSBad.generateUUIDToJSON();
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final int expectedStatusCode = HttpStatusCode.OK;
            final String expectedInvalidValue = "Missing center name and/or center type.";
            final String expectedErrorMessage = "Both center name and center type should be specified.";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(),
                    expectedStatusCode, expectedInvalidValue, expectedErrorMessage, MediaType.APPLICATION_JSON);
        }
    }

    private void mockGenerateWebService() throws UUIDException {
        context.checking(new Expectations() {{
            one(uuidService).generateUUID(1, 1, UUIDConstants.GenerationMethod.Rest, UUIDConstants.MASTER_USER);
            will(returnValue(expectedList));
        }});

        final Center center = new Center();
        center.setCenterId(1);
        center.setCenterName(centerName);
        center.setCenterType(centerType);
        context.checking(new Expectations() {{
            one(uuidService).getCenterByNameAndType(centerName, centerType);
            will(returnValue(center));
        }});
    }

}
