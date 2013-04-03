/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.FileUploadBean;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDBrowserService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDCommonService;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.COLS;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DATA;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DATE_FORMAT_US;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.EXPORT_DATE_FORMAT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.EXPORT_FILENAME;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.EXPORT_TITLE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.EXPORT_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SEARCH_PARAMS;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SORT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TOTAL_COUNT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_COLS;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_EXPORT_URL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_UPLOAD_URL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_UPLOAD_VIEW;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_URL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_VIEW;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_TYPE_COMBO;

/**
 * Controller class for the uuid browser
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class UUIDBrowserController {

    @Autowired
    private UUIDBrowserService service;

    @Autowired
    private UUIDCommonService commonService;

    protected final Log logger = LogFactory.getLog(getClass());

    @RequestMapping(value = UUID_BROWSER_URL, method = RequestMethod.GET)
    public String uuidBrowserHomeHandler() {
        return UUID_BROWSER_VIEW;
    }

    @RequestMapping(value = UUID_BROWSER_UPLOAD_URL, method = RequestMethod.POST)
    public String handleUpload(final ModelMap model, final FileUploadBean fileUploadBean,
                               final HttpSession session) {
        try {
            final MultipartFile file = fileUploadBean.getFile();
            if (file == null) {
                logger.info("Error UUID Upload: File is null");
                model.addAttribute("response", buildManualJsonResponse(false, "Error UUID Upload: File is null"));
            } else {
                logger.info("UUID Upload: processing file ...");
                final Map<String, Object> resMap = service.parseUploadFile(file.getInputStream());
                final List<String> res = (List<String>) resMap.get("uuidList");
                final Boolean hasBarcode = (Boolean) resMap.get("hasBarcode");
                final Boolean allBarcode = (Boolean) resMap.get("allBarcode");
                if (res != null && res.size() > 0) {
                    final List<BiospecimenMetaData> uploadData = service.processMultipleBiospecimenMetadata(res,
                            hasBarcode, allBarcode);
                    session.setAttribute("uploadData", uploadData);
                    model.addAttribute("response", buildManualJsonResponse(true, "upload complete"));
                } else {
                    logger.info("Error UUID Upload: File is empty");
                    model.addAttribute("response", buildManualJsonResponse(false, "Error UUID Upload: File is empty"));
                }
            }
        } catch (IOException iox) {
            logger.info(iox.getMessage());
            model.addAttribute("response", buildManualJsonResponse(false, "Error UUID Upload: Invalid File"));
        }
        return UUID_BROWSER_UPLOAD_VIEW;
    }

    private String buildManualJsonResponse(boolean success, String message) {
        return new JSONObject().element("success", success).element("message", message).toString();
    }

    @RequestMapping(value = UUID_BROWSER_EXPORT_URL, method = RequestMethod.POST)
    public String uuidBrowserExportHandler(
            final ModelMap model, final HttpSession session,
            @RequestParam(value = EXPORT_TYPE) final String exportType,
            @RequestParam(value = SORT, required = false) final String sort,
            @RequestParam(value = DIR, required = false) final String dir,
            @RequestParam(value = TOTAL_COUNT, required = false) final String totalCount,
            @RequestParam(value = SEARCH_PARAMS, required = false) final String searchParams,
            @RequestParam(value = COLS) final String columns) {

        List<BiospecimenMetaData> uuidBrowserList;
        if ("0".equals(totalCount)) {
            uuidBrowserList = new LinkedList<BiospecimenMetaData>();
        } else {
            uuidBrowserList = service.getAllBiospecimenMetadata();
        }
        List<BiospecimenMetaData> uuidBrowserSearchList = uuidBrowserList;
        if (searchParams != null && searchParams.startsWith("{")) {
            if (searchParams.contains("upload complete")) {
                uuidBrowserSearchList = (List<BiospecimenMetaData>) session.getAttribute("uploadData");
            } else if (searchParams.contains(UUID_TYPE_COMBO)) {
                session.removeAttribute("uploadData");
                uuidBrowserSearchList = service.processSecondTab(searchParams, uuidBrowserList);
            } else {
                session.removeAttribute("uploadData");
                uuidBrowserSearchList = service.processFirstTab(searchParams, uuidBrowserList);
            }
        }
        final List<BiospecimenMetaData> sortedUUIDBrowserList = commonService.getSortedList(uuidBrowserSearchList,
                service.getUUIDBrowserComparator(), sort, dir);
        final ViewAndExtensionForExport vae = commonService.getViewAndExtForExport(exportType);
        model.addAttribute(EXPORT_TYPE, exportType);
        model.addAttribute(EXPORT_TITLE, UUID_BROWSER_VIEW);
        model.addAttribute(EXPORT_FILENAME, UUID_BROWSER_VIEW + vae.getExtension());
        model.addAttribute(EXPORT_DATE_FORMAT, DATE_FORMAT_US);
        model.addAttribute(COLS, commonService.buildReportColumns(UUID_BROWSER_COLS, columns));
        model.addAttribute(DATA, sortedUUIDBrowserList);
        return vae.getView();
    }

}//End of Class
