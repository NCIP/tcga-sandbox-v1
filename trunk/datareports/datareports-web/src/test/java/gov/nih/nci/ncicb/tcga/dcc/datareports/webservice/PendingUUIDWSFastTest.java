/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.webservice;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.webservice.PendingUUIDResponse;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.PendingUUIDService;
import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * test class for the pending UUID web service
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class PendingUUIDWSFastTest {

    private final Mockery context = new JUnit4Mockery();

    private PendingUUIDService service;

    private PendingUUIDWS webservice;

    private final static String JSON_PATH =
            Thread.currentThread().getContextClassLoader().getResource("pendingUUID").getPath() + File.separator;
    private static String pendingUUIDMessage = "";

    @BeforeClass
    public static void initStaticResources() throws IOException {
        InputStream is = new FileInputStream(JSON_PATH + "pending_barcode_sample.json");
        pendingUUIDMessage = IOUtils.toString(is);
    }

    @Before
    public void before() throws Exception {
        webservice = new PendingUUIDWS();
        service = context.mock(PendingUUIDService.class);
        webservice.setService(service);
    }

    @Test
    public void testProcessJobToJson() throws Exception {
        final List<PendingUUID> pendingUUIDs = new LinkedList<PendingUUID>();
        context.checking(new Expectations() {{
            one(service).parseAndValidatePendingUUIDJson(pendingUUIDMessage);
            will(returnValue(true));
            one(service).getPendingUUIDsFromJson(pendingUUIDMessage);
            will(returnValue(pendingUUIDs));
            one(service).persistPendingUUIDs(pendingUUIDs);
        }});
        final PendingUUIDResponse response = webservice.processJobToJson(pendingUUIDMessage);
        assertNotNull(response);
        assertEquals("success", response.getResponseMessage());
        assertNull(response.getErrorMessages());
    }

    @Test
    public void testProcessJobToXml() throws Exception {
        final List<String> errors = new LinkedList<String>() {{
            add("Error mister");
        }};
        context.checking(new Expectations() {{
            one(service).parseAndValidatePendingUUIDJson(pendingUUIDMessage);
            will(returnValue(false));
            one(service).getErrors();
            will(returnValue(errors));
        }});
        final PendingUUIDResponse response = webservice.processJobToJson(pendingUUIDMessage);
        assertNotNull(response);
        assertEquals("failure", response.getResponseMessage());
        assertNotNull(response.getErrorMessages());
        assertEquals(1, response.getErrorMessages().size());
        assertEquals("Error mister", response.getErrorMessages().get(0));
    }

    @Test
    public void testProcessJobToJsonEmptyMessage() throws Exception {
        final PendingUUIDResponse response = webservice.processJobToJson("");
        assertNotNull(response);
        assertEquals("failure", response.getResponseMessage());
        assertNotNull(response.getErrorMessages());
        assertEquals(1, response.getErrorMessages().size());
        assertEquals("The input json message is empty", response.getErrorMessages().get(0));
    }

    @Test
    public void testProcessJobToJsonNullMessage() throws Exception {
        final PendingUUIDResponse response = webservice.processJobToJson(null);
        assertNotNull(response);
        assertEquals("failure", response.getResponseMessage());
        assertNotNull(response.getErrorMessages());
        assertEquals(1, response.getErrorMessages().size());
        assertEquals("The input json message is empty", response.getErrorMessages().get(0));
    }
}
