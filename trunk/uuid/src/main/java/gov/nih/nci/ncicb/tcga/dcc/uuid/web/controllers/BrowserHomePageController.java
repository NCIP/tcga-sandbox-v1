/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for UUID Browser home page
 */

@Controller
public class BrowserHomePageController {

    @RequestMapping(value="/browserPage.htm")
    public String getUUIDBrowserHomeScreen() {
        return "uuidBrowserHome";
    }

}
