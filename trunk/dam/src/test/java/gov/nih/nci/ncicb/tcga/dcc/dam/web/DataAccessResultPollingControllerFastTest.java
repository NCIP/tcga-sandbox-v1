/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import static org.junit.Assert.assertEquals;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DAMJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.DAMJobStatusService;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.PollingRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.validation.BindException;

/**
 * DataAccessResultPollingController unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DataAccessResultPollingControllerFastTest {

    private static final String UUID_AS_STRING = "414993ff-796a-48f9-a2b7-11ae6a6ac890";
    private static final String SUCCESS_VIEW = "successView";

    private Mockery mockery;
    private DAMJobStatusService mockDamJobStatusService;
    private DataAccessResultPollingController dataAccessResultPollingController;
    private HttpServletRequest mockHttpServletRequest;
    private HttpServletResponse mockHttpServletResponse;
    private BindException bindException;
    private PollingRequest command;
    private StringWriter stringWriter;
    private DAMJobStatus jobStatus;


    @Before
    public void setUp() throws IOException {

        mockery = new JUnit4Mockery();

        mockDamJobStatusService = mockery.mock(DAMJobStatusService.class);
        mockHttpServletRequest = mockery.mock(HttpServletRequest.class);
        mockHttpServletResponse = mockery.mock(HttpServletResponse.class);

        dataAccessResultPollingController = new DataAccessResultPollingController();
        dataAccessResultPollingController.setDamJobStatusService(mockDamJobStatusService);
        dataAccessResultPollingController.setSuccessView(SUCCESS_VIEW);
        stringWriter = new StringWriter();

        bindException = null;
        jobStatus = new DAMJobStatus();

        command = new PollingRequest();
        command.setFilePackagerKey(UUID.fromString(UUID_AS_STRING));

        mockery.checking(new Expectations() {{
            one(mockDamJobStatusService).getJobStatusForJobKey(UUID_AS_STRING);
            will(returnValue(jobStatus));

            allowing(mockHttpServletResponse).getWriter();
            will(returnValue(new PrintWriter(stringWriter)));

            allowing(mockHttpServletResponse).setContentType("text/json");
        }});

    }


    @Test
    public void testHandle() {
        jobStatus.setStatus("aStatus");
        jobStatus.setMessage("aMessage");

        dataAccessResultPollingController.handle(mockHttpServletRequest, mockHttpServletResponse, command, bindException);
        assertEquals("{\"status\":\"aStatus\",\"message\":\"aMessage\"}", stringWriter.toString());
    }

}
