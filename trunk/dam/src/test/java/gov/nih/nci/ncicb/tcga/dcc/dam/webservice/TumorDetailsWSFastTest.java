/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.DataTypeCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.TumorDetailsService;

import javax.ws.rs.WebApplicationException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * TumorDetailsWS unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class TumorDetailsWSFastTest {

    private Mockery context = new JUnit4Mockery();
    private TumorDetailsService mockTumorDetailsService;
    private TumorDetailsWS tumorDetailsWS;

    @Before
    public void setup() {
        mockTumorDetailsService = context.mock(TumorDetailsService.class);
        tumorDetailsWS = new TumorDetailsWS();
        tumorDetailsWS.setTumorDetailsService(mockTumorDetailsService);
    }

    @Test
    public void testGetTumorSampleTypeCount() {

        final String diseaseAbbreviation = "GBM";

        final DataTypeCount expectedCaseCount = new DataTypeCount();
        expectedCaseCount.setTumorAbbrev(diseaseAbbreviation);
        expectedCaseCount.setCountType(DataTypeCount.CountType.Case);

        final DataTypeCount expectedHealthyCount = new DataTypeCount();
        expectedHealthyCount.setTumorAbbrev(diseaseAbbreviation);
        expectedHealthyCount.setCountType(DataTypeCount.CountType.HealthyControl);

        final DataTypeCount[] expectedTumorSampleTypeCountArray = new DataTypeCount[2];
        expectedTumorSampleTypeCountArray[0] = expectedCaseCount;
        expectedTumorSampleTypeCountArray[1] = expectedHealthyCount;

        context.checking(new Expectations() {{
            allowing(mockTumorDetailsService).getTumorDataTypeCountArray(diseaseAbbreviation);
            will(returnValue(expectedTumorSampleTypeCountArray));
        }});

        final DataTypeCount[] tumorSampleTypeCountArray = tumorDetailsWS.getTumorSampleTypeCountToJson(diseaseAbbreviation);

        assertNotNull(tumorSampleTypeCountArray);
        assertEquals(2, tumorSampleTypeCountArray.length);
        assertEquals(expectedCaseCount, tumorSampleTypeCountArray[0]);
        assertEquals(expectedHealthyCount, tumorSampleTypeCountArray[1]);
    }

    @Test
    public void testGetTumorSampleTypeCountThrowWebApplicationException() {

        final String diseaseAbbreviation = "GBM";
        final String exceptionMessage = "Expected exception.";

        context.checking(new Expectations() {{
            allowing(mockTumorDetailsService).getTumorDataTypeCountArray(diseaseAbbreviation);
            will(throwException(new WebApplicationException(
                    WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, exceptionMessage)))
            );
        }});

        try {
            tumorDetailsWS.getTumorSampleTypeCountToJson(diseaseAbbreviation);
            fail("WebApplicationException should have been raised");

        } catch(final WebApplicationException e) {
            assertEquals("Unexpected response: ", 
                    "<h2>HTTP STATUS 500 - Internal Server Error.</h2><br /><p>" + exceptionMessage + "</p>",
                    e.getResponse().getEntity().toString());
        }
    }
}
