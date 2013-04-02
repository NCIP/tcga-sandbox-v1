/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.webservice;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.SampleSummaryReportService;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * test class for the sample summary webservice
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith (JMock.class)
public class SampleSummaryWSFastTest {

    private final Mockery context = new JUnit4Mockery();

    private SampleSummaryReportService service;

    private SampleSummaryWS webservice;

    @Before
    public void before() throws Exception {

        webservice = new SampleSummaryWS();
        service = context.mock(SampleSummaryReportService.class);

        //We use reflection to access the private field
        Field serviceControllerField = webservice.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(webservice, service);
    }

    @Test
    public void testGetAliquotReport() throws Exception {
        final List<SampleSummary> mockSS = makeMockSampleSummary();
        context.checking(new Expectations() {{
            allowing(service).getSampleSummaryReport();
            will(returnValue(mockSS));
        }});
        String res = webservice.getSampleSummaryReport();
        assertNotNull(res);
        assertTrue(res.contains("D"));
        assertTrue(res.contains("GBM"));
        assertTrue(res.contains("mockplatform1"));
        assertTrue(res.contains("mockcenter1"));
    }

    public List<SampleSummary> makeMockSampleSummary() {
        List<SampleSummary> list = new LinkedList<SampleSummary>();
        SampleSummary ss = new SampleSummary();
        ss.setPortionAnalyte("D");
        ss.setDisease("GBM");
        ss.setPlatform("mockplatform1");
        ss.setCenterName("mockcenter1");
        ss.setLevelFourSubmitted("Y");
        ss.setCenterType("mockcentertype");
        ss.setTotalBCRSent(1l);
        ss.setLastRefresh(new Timestamp(123));
        ss.setTotalBCRUnaccountedFor(1l);
        ss.setTotalCenterSent(1l);
        ss.setTotalCenterUnaccountedFor(1l);
        ss.setTotalLevelOne(1l);
        ss.setTotalLevelTwo(2l);
        ss.setTotalLevelThree(3l);
        list.add(ss);

        return list;
    }

}//End of Class
