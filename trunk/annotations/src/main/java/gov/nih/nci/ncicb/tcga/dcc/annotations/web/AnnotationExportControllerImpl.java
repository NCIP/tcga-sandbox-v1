/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.web;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller to handle export requests.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Controller
public class AnnotationExportControllerImpl implements AnnotationExportController {

    /**
     * Handle request to export search results from last search.
     *
     * @param session the http session
     * @param model the model
     * @param exportFormat the export format (xl, tab, or csv)
     * @return the name of the view to show
     */
    @RequestMapping(value = "/export.htm", method = RequestMethod.GET)
    public String exportSearchResults(final HttpSession session,
                                      final ModelMap model,
                                      @RequestParam(value = "exportFormat") final String exportFormat) {
        List<DccAnnotation> lastSearchResults = (List<DccAnnotation>) session.getAttribute(AnnotationControllerImpl.ATTRIBUTE_LAST_SEARCH_RESULTS);
        if (lastSearchResults == null) {
            lastSearchResults = new ArrayList<DccAnnotation>();
        }

        final Map<String, String> columns = new LinkedHashMap<String, String>();
        columns.put("id", "ID");
        columns.put("diseases", "Disease");
        columns.put("itemTypes", "Item Type");
        columns.put("items", "Item Barcode");
        columns.put("annotationCategory.annotationClassification", "Annotation Classification");
        columns.put("annotationCategory", "Annotation Category");
        columns.put("notes", "Annotation Notes");
        columns.put("dateCreated", "Date Created");
        columns.put("createdBy", "Created By");        
        columns.put("status", "Status");

        ViewAndExtensionForExport viewAndExtension = getViewAndExtensionForExport(exportFormat);

        model.addAttribute(ExportUtils.ATTRIBUTE_EXPORT_TYPE, exportFormat);
        model.addAttribute(ExportUtils.ATTRIBUTE_FILE_NAME, "tcga_annotations" + viewAndExtension.getExtension());
        model.addAttribute(ExportUtils.ATTRIBUTE_COLUMN_HEADERS, columns);
        model.addAttribute(ExportUtils.ATTRIBUTE_DATA, lastSearchResults);
        model.addAttribute(ExportUtils.ATTRIBUTE_DATE_FORMAT, new SimpleDateFormat("MM/dd/yyyy"));
        model.addAttribute(ExportUtils.ATTRIBUTE_TITLE, "TCGA Annotations");
        return viewAndExtension.getView();
    }

    private ViewAndExtensionForExport getViewAndExtensionForExport(final String exportType) {
        ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        if ("xl".equals(exportType)) {
            vae.setView("xl");
            vae.setExtension(".xlsx");
        } else if ("csv".equals(exportType)) {
            vae.setView("txt");
            vae.setExtension(".csv");
        } else { // default
            vae.setView("txt");
            vae.setExtension(".txt");
        }
        return vae;
    }
}
