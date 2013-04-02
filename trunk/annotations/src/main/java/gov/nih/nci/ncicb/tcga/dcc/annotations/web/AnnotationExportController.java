/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.web;

import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpSession;

/**
 * Interface for annotation export controller.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface AnnotationExportController {

    /**
     * Handles request to export last search results.
     *
     * @param session the http session
     * @param model the model
     * @param exportType the export type
     * @return the view name
     */
    public String exportSearchResults(HttpSession session, ModelMap model, String exportType);
}
