/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.security;

import org.springframework.ui.ModelMap;

/**
 * @author Julien Baboud
 *
 * This controller adds security related information into the model, to be processed by the view
 */
public interface SecurityController {

    /**
     * Adds the authenticated principal username to the model
     *
     * @param model the model to use in the view
     * @return the model
     */
    public ModelMap handleRequestUsername(final ModelMap model);

    /**
     * Adds the authenticated principal username to the model
     *
     * @param model the model to use in the view
     * @return the model
     */
    public ModelMap handleRequestAuthorities(final ModelMap model);

    /**
     * For unit tests only :-/
     *
     * @param securityUtil
     */
    public void setSecurityUtil(SecurityUtil securityUtil);
}
