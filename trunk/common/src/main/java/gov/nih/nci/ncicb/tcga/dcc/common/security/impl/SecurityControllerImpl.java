/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.security.impl;

import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityController;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Julien Baboud
 *
 * This controller adds security related information into the model, to be processed by the view
 */
@Controller
public class SecurityControllerImpl implements SecurityController {

    @Autowired
    private SecurityUtil securityUtil;

    /**
     * Adds the authenticated principal username to the model
     *
     * @param model the model to use in the view
     * @return the model
     */
    @RequestMapping(value = "/username.securityjson", method = RequestMethod.GET)
    public ModelMap handleRequestUsername(final ModelMap model) {

        return model.addAttribute("authenticatedPrincipalUsername", securityUtil.getAuthenticatedPrincipalLoginName());
    }

    /**
     * Adds the authenticated principal username to the model
     *
     * @param model the model to use in the view
     * @return the model
     */
    @RequestMapping(value = "/authorities.securityjson", method = RequestMethod.GET)
    public ModelMap handleRequestAuthorities(final ModelMap model) {

        return model.addAttribute("authenticatedPrincipalAuthorities", securityUtil.getAuthenticatedPrincipalAuthorities());
    }

    /**
     * For unit tests only :-/
     *
     * @param securityUtil
     */
    public void setSecurityUtil(final SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }
}
