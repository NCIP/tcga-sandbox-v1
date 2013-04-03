/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Class to test REST web service for getting UUID Details
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class UUIDDetailWebServiceFastTest {

    private Mockery context = new JUnit4Mockery();
    private UUIDDetailWebService uuidWS;
    private UUIDService uuidService;

    @Before
    public void setUp() {
        uuidService = context.mock(UUIDService.class);
        uuidWS = new UUIDDetailWebService();
        uuidWS.setUuidService(uuidService);
    }

    @Test
    public void testGetUUIDDetailToJSON() throws UUIDException {
        final String uuidVal = "uniqueId";
        final UUIDDetail uuid = new UUIDDetail();
        uuid.setUuid(uuidVal);

        context.checking(new Expectations() {{
            one(uuidService).getUUIDDetails("uniqueId");
            will(returnValue(uuid));
        }});

        UUIDDetail actualUUID = uuidWS.getUUIDDetailToJSON(uuidVal);
        assertNotNull(actualUUID);
        assertEquals(uuid, actualUUID);
    }

    @Test
    public void testGetUUIDDetailToJSONFailure() throws UUIDException {
        final String uuidVal = "nonExistentUUID";
        final UUIDDetail uuid = new UUIDDetail();
        uuid.setUuid(uuidVal);

        context.checking(new Expectations() {{
            one(uuidService).getUUIDDetails(uuidVal);
            will(throwException(new UUIDException("UUID not found")));
        }});

        // this should throw WebApplicationException
        try {
            uuidWS.getUUIDDetailToJSON(uuidVal);
            fail("WebApplicationException was not thrown.");

        } catch(final WebApplicationException e) {

            final String expectedErrorMessage = "Could not retrieve UUID : " + uuidVal;
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    uuidVal, expectedErrorMessage, MediaType.APPLICATION_JSON);
        }
    }

    @Test
    public void testGetUUIDDetailToXML() throws Exception {
        final String uuidVal = "uniqueId";
        final UUIDDetail uuid = new UUIDDetail();
        uuid.setUuid(uuidVal);

        context.checking(new Expectations() {{
            one(uuidService).getUUIDDetails("uniqueId");
            will(returnValue(uuid));
        }});

        UUIDDetail actualUUID = uuidWS.getUUIDDetailToXML(uuidVal);
        assertNotNull(actualUUID);
        assertEquals(uuid, actualUUID);
    }

    @Test(expected= WebApplicationException.class)
    public void testGetUUIDDetailToXMLFailure() throws UUIDException {
        final String uuidVal = "nonExistentUUID";
        final UUIDDetail uuid = new UUIDDetail();
        uuid.setUuid(uuidVal);

        context.checking(new Expectations() {{
            one(uuidService).getUUIDDetails(uuidVal);
            will(throwException(new UUIDException("UUID not found")));
        }});

        // this should throw WebApplicationException
        uuidWS.getUUIDDetailToXML(uuidVal);
    }
}
