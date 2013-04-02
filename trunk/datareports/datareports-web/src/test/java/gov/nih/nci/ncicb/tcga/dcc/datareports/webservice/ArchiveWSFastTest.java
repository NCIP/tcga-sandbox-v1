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
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.LatestGenericReportService;

import java.lang.reflect.Field;
import java.util.Date;
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
 * Test class for latest archive webservice
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith (JMock.class)
public class ArchiveWSFastTest {

    private final Mockery context = new JUnit4Mockery();

    private LatestGenericReportService service;

    private ArchiveWS webservice;

    @Before
    public void before() throws Exception {

        webservice = new ArchiveWS();
        service = context.mock(LatestGenericReportService.class);

        //We use reflection to access the private field
        Field serviceControllerField = webservice.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(webservice, service);
    }

    @Test
    public void testGetArchiveReport() throws Exception {
        final List<Archive> mockArchive = makeMockArchive();
        context.checking(new Expectations() {{
           allowing(service).getLatestArchiveWS();
           will(returnValue(mockArchive));
        }});
        String res = webservice.getArchiveReport(null);
        assertNotNull(res);
        assertTrue(res.trim().contains("ARCHIVE_NAME\tDATE_ADDED\tARCHIVE_URL\n" +
                "mock archive name"));
    }

    @Test
    public void testGetArchiveReportWithQuery() throws Exception {
        final List<Archive> mockArchive = makeMockArchive();
        context.checking(new Expectations() {{
           allowing(service).getLatestArchiveWSByType("typo");
           will(returnValue(mockArchive));
        }});
        String res = webservice.getArchiveReport("typo");
        assertNotNull(res);
        assertTrue(res.trim().contains("ARCHIVE_NAME\tDATE_ADDED\tARCHIVE_URL\n" +
                "mock archive name"));
    }

    public List<Archive> makeMockArchive() {
        List<Archive> list = new LinkedList<Archive>();
        Archive arch = new Archive();
            arch.setDeployLocation("mock archive url");
            arch.setRealName("mock archive name");
            arch.setDateAdded(new Date(123456789));
        list.add(arch);

       return list;
    }

}//End of Class
