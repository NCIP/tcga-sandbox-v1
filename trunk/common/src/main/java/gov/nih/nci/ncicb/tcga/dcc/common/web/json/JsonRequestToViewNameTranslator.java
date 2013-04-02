/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.web.json;

import org.springframework.web.servlet.RequestToViewNameTranslator;

import javax.servlet.http.HttpServletRequest;

/**
 * Translator used for JSON requests to route all views to the JsonView. The ref
 * to the defined JsonView is pased to this translator.
 *
 * @version 1.0
 */
public class JsonRequestToViewNameTranslator implements RequestToViewNameTranslator {

    private String view = "";

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getViewName(HttpServletRequest request) throws Exception {
        return view;
    }
}
