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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.AliquotReportService;

import java.lang.reflect.Field;
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
 * test class for the aliquot webservice
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith (JMock.class)
public class AliquotWSFastTest {

    private final Mockery context = new JUnit4Mockery();

    private AliquotReportService service;

    private AliquotWS webservice;

    @Before
    public void before() throws Exception {

        webservice = new AliquotWS();
        service = context.mock(AliquotReportService.class);

        //We use reflection to access the private field
        Field serviceControllerField = webservice.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(webservice, service);
    }

    @Test
    public void testGetAliquotReport() throws Exception {
        final List<Aliquot> mockBio = makeMockAliquot();
        context.checking(new Expectations() {{
           allowing(service).getAllAliquot();
           will(returnValue(mockBio));
        }});
        String res = webservice.getAliquotReport();
        assertNotNull(res);
        assertTrue(res.contains("mockSubmitted"));
        assertTrue(res.contains("1"));
        assertTrue(res.contains("GBM"));
    }
     
    public List<Aliquot> makeMockAliquot() {
        List<Aliquot> list = new LinkedList<Aliquot>();
        Aliquot bio = new Aliquot();
            bio.setAliquotId("mockaliquot1");
            bio.setBcrBatch("1");
            bio.setDisease("GBM");
            bio.setLevelOne("mockSubmitted");
            bio.setLevelTwo("mockSubmitted");
            bio.setLevelThree("mockSubmitted");
            bio.setPlatform("mockplatform1");
            bio.setCenter("mockcenter1");

        list.add(bio);

       return list;
    }

}//End of Class
