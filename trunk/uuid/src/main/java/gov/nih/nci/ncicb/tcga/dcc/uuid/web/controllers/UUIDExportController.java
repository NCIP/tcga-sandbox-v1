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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for exporting data in different formats
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@Controller
public class UUIDExportController {

    @Autowired
    private UUIDReportService uuidReportService;

    /**
     * Handles the export request for all the list pages
     * @param model model object containing export parameters for export Type, title, columns and data 
     * @param exportType export type selected by the client 
     * @param dataToBeExported data type to be exported 
     * @param session HttpSession for this request
     * @return the view name depending on the export type [xls/txt]
     */
    @RequestMapping (value = "/uuidExport.htm", method = RequestMethod.GET)
    public String uuidExportHandler(
            final ModelMap model,
            @RequestParam (value = UUIDConstants.EXPORT_TYPE) final String exportType,
            @RequestParam (value = "dataToBeExported") final String dataToBeExported,            
            final HttpSession session) {

        List<UUIDDetail> searchResults = new ArrayList<UUIDDetail>(); 
        if(session.getAttribute(dataToBeExported) != null){
            searchResults = (List<UUIDDetail>) session.getAttribute(dataToBeExported);
        }        

        Map<String, String> columns = new LinkedHashMap<String, String>();
        columns.put("uuid", "UUID");
        columns.put("center", "Center");
        columns.put("createdBy", "Created By");
        columns.put("diseaseAbbrev", "Disease");
        columns.put("creationDate", "Creation Date");
        columns.put("generationMethod", "Creation Method");
        columns.put("latestBarcode", "Latest Barcode");

        ViewAndExtensionForExport vae = getViewAndExtensionForExport(exportType);
        model.addAttribute(UUIDConstants.EXPORT_TYPE, exportType);
        model.addAttribute(UUIDConstants.EXPORT_TITLE, UUIDConstants.EXPORT_UUID);
        model.addAttribute(UUIDConstants.EXPORT_FILENAME, UUIDConstants.EXPORT_UUID+ vae.getExtension());
        model.addAttribute(UUIDConstants.COLS, columns);
        model.addAttribute(UUIDConstants.DATA, searchResults);
        DateFormat dateFormat = new SimpleDateFormat(UUIDConstants.DATE_FORMAT);
        model.addAttribute(UUIDConstants.EXPORT_DATE_FORMAT, dateFormat);
        return vae.getView();
    }

    private ViewAndExtensionForExport getViewAndExtensionForExport(final String exportType) {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        if (UUIDConstants.EXCEL.equals(exportType)) {
            vae.setView(UUIDConstants.EXCEL);
            vae.setExtension(".xls");
        } else if (UUIDConstants.CSV.equals(exportType)) {
            vae.setView("txt");
            vae.setExtension(".csv");
        } else if (UUIDConstants.TAB.equals(exportType)) {
            vae.setView("txt");
            vae.setExtension(".txt");
        }
        return vae;
    }

    protected void setUuidReportService(final UUIDReportService uuidReportService) {
        this.uuidReportService = uuidReportService;
    }
}
