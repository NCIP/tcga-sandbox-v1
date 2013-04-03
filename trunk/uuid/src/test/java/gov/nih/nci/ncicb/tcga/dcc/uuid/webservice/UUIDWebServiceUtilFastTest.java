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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test class for UUIDWebServiceUtil
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class UUIDWebServiceUtilFastTest {

    private Mockery context = new JUnit4Mockery();    
    private UUIDWebServiceUtilImpl uuidWebServiceUtil;
    private UUIDService uuidService;
    
    @Before
    public void setup(){
        uuidWebServiceUtil = new UUIDWebServiceUtilImpl();
        uuidService = context.mock(UUIDService.class);
        uuidWebServiceUtil.setService(uuidService);
    }


    @Test
    public void testGetCenterId() {
        final String centerName = "centerName", centerType = "centerType";
        int centerId = 100;
        final Center center = new Center();
        center.setCenterId(centerId);
        center.setCenterName(centerName);
        center.setCenterType(centerType);

        context.checking(new Expectations() {{
            one(uuidService).getCenterByNameAndType(centerName, centerType);
            will(returnValue(center));
        }});
        int centerIdActual = uuidWebServiceUtil.getCenterId(centerName, centerType, MediaType.APPLICATION_JSON);
        assertEquals(centerId, centerIdActual);
    }

    @Test
    public void testGetCenterIdForWrongCenterInfo() {

        final String centerName = "wrongCenterName", centerType = "wrongCenterType";

        context.checking(new Expectations() {{
            one(uuidService).getCenterByNameAndType(centerName, centerType);
            will(returnValue(null));
        }});

        final String mediaType = MediaType.APPLICATION_JSON;

        try {
            uuidWebServiceUtil.getCenterId(centerName, centerType, mediaType);
            fail();

        } catch(final WebApplicationException e) {
            final String expectedErrorMessage = "Center not found for the given name and center type.";
            final String expectedInvalidValue = "[center name:wrongCenterName][center type:wrongCenterType]";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    expectedInvalidValue, expectedErrorMessage, mediaType);
        }
    }

    @Test
    public void testGetCenterIdForNullCenterType() {

        final String centerName = "centerName", centerType = null;
        final String mediaType = MediaType.APPLICATION_JSON;

        try {
            uuidWebServiceUtil.getCenterId(centerName, centerType, mediaType);
            fail();

        } catch(WebApplicationException e) {
            final String expectedErrorMessage = "Both center name and center type should be specified.";
            final String expectedInvalidValue = "[center name:centerName][center type:null]";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    expectedInvalidValue, expectedErrorMessage, mediaType);
        }
    }

    @Test
    public void testGetCenterIdForNullCenterName() {

        final String centerName = null, centerType = "centerType";
        final String mediaType = MediaType.APPLICATION_JSON;

        try {
            uuidWebServiceUtil.getCenterId(centerName, centerType, MediaType.APPLICATION_JSON);
            fail();

        } catch(final WebApplicationException e) {
            final String expectedErrorMessage = "Both center name and center type should be specified.";
            final String expectedInvalidValue = "[center name:null][center type:centerType]";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    expectedInvalidValue, expectedErrorMessage, mediaType);
        }
    }

    @Test
    public void testGetDiseaseId() {
        final String tumorName = "tumorName";
        int tumorId = 1;
        final Tumor tumor = new Tumor();
        tumor.setTumorId(tumorId );
        tumor.setTumorName(tumorName);
         context.checking(new Expectations() {{
             one(uuidService).getTumorForName(tumorName);
             will(returnValue(tumor));
         }});
         int actualTumorId = uuidWebServiceUtil.getDiseaseId(tumorName, MediaType.APPLICATION_JSON);
         assertEquals(tumorId, actualTumorId);

    }

    @Test
    public void testGetDiseaseIdForUnknownDisease() {

        final String tumorName = "nonexistent";
        final String mediaType = MediaType.APPLICATION_JSON;

        context.checking(new Expectations() {{
            one(uuidService).getTumorForName(tumorName);
            will(returnValue(null));
        }});

        try {
            uuidWebServiceUtil.getDiseaseId(tumorName, MediaType.APPLICATION_JSON);
            fail();

        } catch(final WebApplicationException e) {
            final String expectedErrorMessage = "Disease not found.";
            final String expectedInvalidValue = "nonexistent";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    expectedInvalidValue, expectedErrorMessage, mediaType);
        }
    }
    
}
