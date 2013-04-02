/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.web;

import gov.nih.nci.ncicb.tcga.dcc.common.exception.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.service.LiveMonitor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the request to display all the currently running QCLive jobs.
 *
 * @author saraswatv
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class LiveMonitorController implements Controller {

    private LiveMonitor liveMonitor;

    public ModelAndView handleRequest(final HttpServletRequest httpServletRequest,
                                      final HttpServletResponse httpServletResponse) {

        ModelAndView modelAndView;
        try {
            final String setRunningParam = httpServletRequest.getParameter("setRunning");
            if (setRunningParam != null) {
                final boolean shouldBeRunning = Boolean.valueOf(setRunningParam);
                final boolean isSchedulerRunning = liveMonitor.isSchedulerRunning();
                if (shouldBeRunning && !isSchedulerRunning) {
                    liveMonitor.resumeScheduler();
                } else if (!shouldBeRunning && isSchedulerRunning) {
                    liveMonitor.suspendScheduler();
                }
            }
            final Map<String, Object> models = new HashMap<String, Object>();
            models.put("QcLiveRunningJobs", liveMonitor.getRunningJobs());
            models.put("QcLiveWaitingJobs", liveMonitor.getScheduledJobs());
            models.put("QcLiveIsRunning", liveMonitor.isSchedulerRunning());
            modelAndView = new ModelAndView("qcLiveJobs", models);
        }
        catch (Exception e) {
            modelAndView = new ModelAndView("qcLiveJobsError", "ErrorInfo", new ErrorInfo(e));
        }
        return modelAndView;
    }

    public void setLiveMonitor(final LiveMonitor liveMonitor) {
        this.liveMonitor = liveMonitor;
    }
}
