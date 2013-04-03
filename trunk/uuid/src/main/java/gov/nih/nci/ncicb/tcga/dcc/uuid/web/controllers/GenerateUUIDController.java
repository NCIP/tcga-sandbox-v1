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
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers.request.GenerateUUIDRequest;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to handle Generate UUID requests
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@Controller
public class GenerateUUIDController {

    @Autowired
    private UUIDService uuidService;

    @Autowired
    private SecurityUtil securityUtil;
    

    /**
     * Handles request for generating new UUIDs
     * @param model
     * @param generateRequest request parameter for new UUID request
     * @param session HttpSession object
     * @return view name
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException exception if the request to add uuid fails
     */
    @RequestMapping (value = "/generateUUID.htm", method = RequestMethod.POST)
    public String generateUUIDDProcessRequest(ModelMap model,
            final GenerateUUIDRequest generateRequest,
            final HttpSession session) throws UUIDException {
        
        String userName = securityUtil.getAuthenticatedPrincipalLoginName();
        int centerId = generateRequest.getCenterId();
        final List<UUIDDetail> list = uuidService.generateUUID(centerId, generateRequest.getNumberOfUUIDs(),
                UUIDConstants.GenerationMethod.Web, userName);

        model.addAttribute("operationName", "displayNewlyGenerated");
        session.setAttribute("listOfGeneratedUUIDs", list);
        return "uuidManagerHome";
    }

    /**
     * Set the UUIDService
     * @param uuidService uuid Service
     */
    protected void setUuidService(final UUIDService uuidService) {
        this.uuidService = uuidService;
    }

    public void setSecurityUtil(final SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }
}
