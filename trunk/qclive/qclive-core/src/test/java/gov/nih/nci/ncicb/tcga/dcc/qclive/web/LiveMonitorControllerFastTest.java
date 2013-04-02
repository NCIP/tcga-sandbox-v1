/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.QcLiveJobInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.service.LiveMonitor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Test class for LiveMonitorController.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class LiveMonitorControllerFastTest {
    private Mockery context;
    private LiveMonitorController liveMonitorController;
    private LiveMonitor mockLiveMonitor;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private List<QcLiveJobInfo> runningJobs, scheduledJobs;

    @Before
    public void setUp() throws SchedulerException {
        context = new JUnit4Mockery();
        mockLiveMonitor = context.mock(LiveMonitor.class);
        liveMonitorController = new LiveMonitorController();
        liveMonitorController.setLiveMonitor(mockLiveMonitor);
        mockRequest = context.mock(HttpServletRequest.class);
        mockResponse = context.mock(HttpServletResponse.class);
        runningJobs = new ArrayList<QcLiveJobInfo>();
        scheduledJobs = new ArrayList<QcLiveJobInfo>();
    }

    private void setupForNormalExecution() throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockLiveMonitor).getRunningJobs();
            will(returnValue(runningJobs));
            one(mockLiveMonitor).getScheduledJobs();
            will(returnValue(scheduledJobs));
        }});
    }

    @Test
    public void testHandleRequestNoParams() throws SchedulerException {
        setupForNormalExecution();
        context.checking(new Expectations() {{
            final Sequence requestSequence = context.sequence("handleRequest");
            one(mockRequest).getParameter("setRunning");
            inSequence(requestSequence);
            will(returnValue(null));

            one(mockLiveMonitor).isSchedulerRunning();
            inSequence(requestSequence);
            will(returnValue(true));
        }});
        final ModelAndView modelAndView = liveMonitorController.handleRequest(mockRequest, mockResponse);        
        checkModelAndView(modelAndView);
        assertTrue((Boolean) modelAndView.getModelMap().get("QcLiveIsRunning"));
    }

    @Test
    public void testHandleRequestToPauseWhenRunning() throws SchedulerException {
        setupForNormalExecution();
        context.checking(new Expectations() {{
            final Sequence requestSequence = context.sequence("handleRequest");
            one(mockRequest).getParameter("setRunning");
            inSequence(requestSequence);
            will(returnValue("false"));

            one(mockLiveMonitor).isSchedulerRunning();
            inSequence(requestSequence);
            will(returnValue(true));

            one(mockLiveMonitor).suspendScheduler();
            inSequence(requestSequence);

            one(mockLiveMonitor).isSchedulerRunning();
            inSequence(requestSequence);
            will(returnValue(false));            
        }});
        final ModelAndView modelAndView = liveMonitorController.handleRequest(mockRequest, mockResponse);
        checkModelAndView(modelAndView);
        assertFalse((Boolean) modelAndView.getModelMap().get("QcLiveIsRunning"));
    }

    @Test
    public void testHandleRequestToResumeWhenPaused() throws SchedulerException {
        setupForNormalExecution();
        context.checking(new Expectations() {{
            final Sequence requestSequence = context.sequence("handleRequest");

            one(mockRequest).getParameter("setRunning");
            inSequence(requestSequence);
            will(returnValue("true"));

            one(mockLiveMonitor).isSchedulerRunning();
            inSequence(requestSequence);
            will(returnValue(false));

            one(mockLiveMonitor).resumeScheduler();
            inSequence(requestSequence);

            one(mockLiveMonitor).isSchedulerRunning();
            inSequence(requestSequence);
            will(returnValue(true));
        }});
        final ModelAndView modelAndView = liveMonitorController.handleRequest(mockRequest, mockResponse);
        checkModelAndView(modelAndView);
        assertTrue((Boolean) modelAndView.getModelMap().get("QcLiveIsRunning"));
    }

    @Test
    public void testHandleRequestToPauseWhenPaused() throws SchedulerException {
        setupForNormalExecution();
        context.checking(new Expectations() {{
            final Sequence requestSequence = context.sequence("handleRequest");
            one(mockRequest).getParameter("setRunning");
            inSequence(requestSequence);
            will(returnValue("false"));

            one(mockLiveMonitor).isSchedulerRunning();
            inSequence(requestSequence);
            will(returnValue(false));

            one(mockLiveMonitor).isSchedulerRunning();
            inSequence(requestSequence);
            will(returnValue(false));
        }});
        final ModelAndView modelAndView = liveMonitorController.handleRequest(mockRequest, mockResponse);
        checkModelAndView(modelAndView);
        assertFalse((Boolean) modelAndView.getModelMap().get("QcLiveIsRunning"));        
    }

    @Test
    public void testHandleRequestToResumeWhenRunning() throws SchedulerException {
        setupForNormalExecution();
        context.checking(new Expectations() {{
            final Sequence requestSequence = context.sequence("handleRequest");

            one(mockRequest).getParameter("setRunning");
            inSequence(requestSequence);
            will(returnValue("true"));

            one(mockLiveMonitor).isSchedulerRunning();
            inSequence(requestSequence);
            will(returnValue(true));

            one(mockLiveMonitor).isSchedulerRunning();
            inSequence(requestSequence);
            will(returnValue(true));
        }});
        final ModelAndView modelAndView = liveMonitorController.handleRequest(mockRequest, mockResponse);
        checkModelAndView(modelAndView);
        assertTrue((Boolean) modelAndView.getModelMap().get("QcLiveIsRunning"));        
    }

    @Test
    public void testHandleRequestException() throws SchedulerException {
        context.checking(new Expectations() {{
            final Sequence requestSequence = context.sequence("handleRequest");

            one(mockRequest).getParameter("setRunning");
            inSequence(requestSequence);
            will(returnValue("true"));

            one(mockLiveMonitor).isSchedulerRunning();
            inSequence(requestSequence);
            //noinspection ThrowableInstanceNeverThrown
            will(throwException(new SchedulerException("hi")));
        }});
        final ModelAndView modelAndView = liveMonitorController.handleRequest(mockRequest, mockResponse);
        assertEquals("qcLiveJobsError", modelAndView.getViewName());
        final ErrorInfo errorInfo = (ErrorInfo) modelAndView.getModelMap().get("ErrorInfo");
        assertEquals("hi", errorInfo.getMessage());                        
    }

    private void checkModelAndView(final ModelAndView modelAndView) {
        assertEquals("qcLiveJobs", modelAndView.getViewName());
        assertEquals(runningJobs, modelAndView.getModelMap().get("QcLiveRunningJobs"));
        assertEquals(scheduledJobs, modelAndView.getModelMap().get("QcLiveWaitingJobs"));
    }
}
