/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DAMJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.DAMJobStatusService;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.PollingRequest;
import org.codehaus.jettison.json.JSONWriter;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Contacted via AJAX from the Data Access File Processing page. It checks to see if the file packaging has finished, by
 * querying the qrtz_job_history table in the Quartz schema.
 *
 * Prints the resulting information as JSON directly to the response's writer.
 *
 * The JSON object will always contains property "status" which will have a value of Queued, Started, Succeeded, Failed, or Unknown
 * If also contains property "message" which has any additional information about the status, including a link to download
 * if one is available.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataAccessResultPollingController extends WebController {

    private DAMJobStatusService damJobStatusService;

    /**
     * Handles a request for polling the status.
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param command the command (expected to be a PollingRequest)
     * @param errors for binding errors
     * @return null since the response is written directly to the response
     */
    protected ModelAndView handle(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final Object command,
                                  final BindException errors) {


        try {
            final PollingRequest pollingRequest = (PollingRequest) command;
            pollingRequest.validate();

            final String jobName = pollingRequest.getFilePackagerKey().toString(); // The job name is the same as the FilePackager UUID
            final DAMJobStatus jobStatus = damJobStatusService.getJobStatusForJobKey(jobName);

            final JSONWriter jsonWriter = new JSONWriter(response.getWriter());

            jsonWriter.object();

            jsonWriter.key("status").value(jobStatus.getStatus());
            jsonWriter.key("message").value(jobStatus.getMessage());

            jsonWriter.endObject();
            response.setContentType("text/json");

        } catch (final Exception e) {
            new ErrorInfo(e); //logs itself
        }

        return null;
    }

    public void setDamJobStatusService(final DAMJobStatusService damJobStatusService) {
        this.damJobStatusService = damJobStatusService;
    }
}
