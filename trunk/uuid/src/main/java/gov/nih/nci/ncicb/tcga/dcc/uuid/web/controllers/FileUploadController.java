/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.FileUploadBean;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for uploading UUIDs
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@Controller
public class FileUploadController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private UUIDService uuidService;

    @Autowired
    private UUIDReportService uuidReportService;

    @Autowired
    private SecurityUtil securityUtil;

    /**
     * This handles the file upload request for UUIDs. Users can upload a file containing
     * UUIDs (one per row) to save it to DCC database.
     * Extjs File upload works in a totally different way as compared to the
     * normal AJAX requests. Instead of sending a JSON response to the client, the response is
     * written to a hidden <iFrame> that is inserted in the body. (See extJs docs for more details)
     * For this reason the response is sent to an intermediate view. Check the uuidUploadStatus.jsp
     *
     * @param fileUploadBean file upload bean containing multipart file and the center id
     * @param session        HttpSession object
     * @return model with upload 'status'
     */


    @RequestMapping(value = "/uuidUpload.htm", method = RequestMethod.POST)
    public String getUploadFile(final ModelMap model,
                                final FileUploadBean fileUploadBean, final HttpSession session) {
        try {
            final MultipartFile file = fileUploadBean.getFile();
            if (file == null) {
                logger.info("Error UUID Upload: File is null");
                model.addAttribute("response", buildManualJsonResponse(false, "Error UUID Upload: File is null"));
            } else {
                logger.info("UUID Upload: processing file ...");
                final List<String> list = parseUploadFile(file.getInputStream());
                String userName = securityUtil.getAuthenticatedPrincipalLoginName();
                int centerId = fileUploadBean.getCenterId();
                final List<UUIDDetail> uuidList = uuidService.uploadUUID(centerId, list, userName);
                session.setAttribute("listOfUploadedUUIDs", uuidList);
                model.addAttribute("response", buildManualJsonResponse(true, file.getOriginalFilename()));
            }
        } catch (IOException ioException) {
            logger.info(ioException.getMessage());
            model.addAttribute("response", buildManualJsonResponse(false, "Error: " +
                    StringUtil.truncate(ioException.getMessage(), 150)));
        } catch (UUIDException uuidException) {
            logger.info(uuidException.getMessage());
            String errorMessage = uuidException.getMessage();
            Throwable cause = uuidException.getCause();
            if (cause != null && cause.getMessage().contains("unique constraint") &&
                    cause.getMessage().contains("violated")) {
                errorMessage += " already exists in the system";
            }
            model.addAttribute("response", buildManualJsonResponse(false, errorMessage));
        }
        return "uuidUploadStatus";
    }

    private String buildManualJsonResponse(boolean success, String message) {
        return new JSONObject().element("success", success).element("message", message).toString();
    }

    /**
     * Reads the inputStream for a list of UUIDs
     *
     * @param inputStream inputStream for the uploaded file
     * @return list of UUID Strings
     * @throws IOException exception while processing the file
     */
    protected List<String> parseUploadFile(final InputStream inputStream) throws UUIDException, IOException {

        List<String> uuidList = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String strLine;
            int lineNumber = 1;
            while ((strLine = br.readLine()) != null) {
                if (uuidService.isValidUUID(strLine)) {
                    uuidList.add(strLine);
                } else {
                    throw new UUIDException("The uuid " + strLine + " at line: " + lineNumber + " is not valid.");
                }
                ++lineNumber;
            }
        } catch (Exception e) {
                    throw new UUIDException("An error occurred. The uploaded file does not seem valid");
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return uuidList;
    }

    public void setUuidService(final UUIDService uuidService) {
        this.uuidService = uuidService;
    }

    public void setUuidReportService(final UUIDReportService uuidreportService) {
        this.uuidReportService = uuidreportService;
    }

    public void setSecurityUtil(final SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }
}