/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Submission;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.SubmissionReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SubmissionReportConstants;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Controller for the submission report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class SubmissionReportController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private SubmissionReportService service;

    @RequestMapping(value = SubmissionReportConstants.SUBMISSION_REPORT_URL, method = RequestMethod.GET)
    public String getsubmissionReport(final ModelMap model) {
        model.addAttribute(new Submission());
        return SubmissionReportConstants.SUBMISSION_REPORT_VIEW;
    }

    @RequestMapping(value = SubmissionReportConstants.SUBMISSION_REPORT_URL, method = RequestMethod.POST)
    public String handleSubmission(final ModelMap model,
                                   final Submission submission, final BindingResult result) {
        try {
            if (result.hasErrors()) {
                for (ObjectError error : (List<ObjectError>) result.getAllErrors()) {
                    logger.info("Error: " + error.getCode() + " - " + error.getDefaultMessage());
                }
                model.addAttribute("response", buildManualJsonResponse(false, "Upload failed"));
            } else {
                final MultipartFile file = submission.getFileData();
                parseUploadedFile(file.getInputStream());
                model.addAttribute("response", buildManualJsonResponse(true, file.getOriginalFilename()));
            }
        } catch (IOException iox) {
            logger.info(iox.getMessage());
        }
        return SubmissionReportConstants.SUBMISSION_REPORT_VIEW;
    }

    private String buildManualJsonResponse(boolean success, String message) {
        return new JSONObject().element("success", success).element("message", message).toString();
    }


    protected void parseUploadedFile(final InputStream inputStream) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                //do something
            }
        } catch (Exception e) {
            logger.info(e);
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }


}//End of Class
