/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.json;

import gov.nih.nci.ncicb.tcga.dcc.common.annotations.TCGAValue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers.request.GenerateUUIDRequest;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The controller handles JSON requests for UUID UI Pages
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@Controller
public class UUIDController {

    @Autowired
    private UUIDService uuidService;

    @Autowired
    private UUIDReportService uuidReportService;

    @Autowired
    private SecurityUtil securityUtil;

    @TCGAValue(key = "tcga.uuid.maxAllowedUUIDs")
    private static String maxUUIDAllowed;    

    //Returns center data to the generate UUID form on UI
    @RequestMapping (value="/centers.json", method = RequestMethod.GET)
    public ModelMap getCenterData(
            final ModelMap model) {

        List<Center> centerList = uuidService.getCenters();
        model.addAttribute("centerData", centerList);
        return model;
    }


    //Returns the list of generated UUIDs to the extjs Grid Panel
    @RequestMapping (value="/generateUUIDs.json", method = RequestMethod.GET)
    public ModelMap getGeneratedUUIDs(final ModelMap model,
            final GenerateUUIDRequest generateRequest,
            final HttpSession session) throws UUIDException {

            String userName = securityUtil.getAuthenticatedPrincipalLoginName();
           List<UUIDDetail>list = uuidService.generateUUID(generateRequest.getCenterId(), generateRequest.getNumberOfUUIDs(),
                UUIDConstants.GenerationMethod.Web, userName);

           if (session.getAttribute("listOfGeneratedUUIDs") != null) {
               session.removeAttribute("listOfGeneratedUUIDs");
           }
           session.setAttribute("listOfGeneratedUUIDs", list);
           model.addAttribute("listOfGeneratedUUIDs", list);
           return model;
    }

    //Returns the list of generated UUIDs to the extjs Grid Panel
    @RequestMapping (value="/generatedUUIDs.json", method = RequestMethod.GET)
    public ModelMap getListOfGeneratedUUIDs(
            final ModelMap model, final HttpSession session) {

           List<UUIDDetail> list = (List<UUIDDetail>)session.getAttribute("listOfGeneratedUUIDs");

           if (session.getAttribute("listOfGeneratedUUIDs") != null) {
               session.removeAttribute("listOfGeneratedUUIDs");
           }
           session.setAttribute("listOfGeneratedUUIDs", list);
           model.addAttribute("listOfGeneratedUUIDs", list);
           return model;
    }

    // Returns list of active diseases
    @RequestMapping (value="/diseases.json", method= RequestMethod.GET)
    public ModelMap getActiveDiseases(final ModelMap modelMap) {
        List<Tumor> diseases = uuidService.getActiveDiseases();
        modelMap.addAttribute("diseases", diseases);
        return modelMap;
    }

    //Returns the upload results
    @RequestMapping (value="/uploadResults.json", method = RequestMethod.GET)
    public ModelMap getUploadResults(
            final ModelMap model, final HttpSession session) {

           List<UUIDDetail> list = (List<UUIDDetail>)session.getAttribute("listOfUploadedUUIDs");

           if (session.getAttribute("uploadedUUIDs") != null) {
               session.removeAttribute("uploadedUUIDs");
           }
           session.setAttribute("uploadedUUIDs", list);
           model.addAttribute("uploadedUUIDs", list);
           return model;
    }

    //Returns the UUID Details 
    @RequestMapping (value="/uuidDetail.json", method = RequestMethod.GET)
    public ModelMap getUUIDDetails(
            final ModelMap model,
            @RequestParam (value="uuid") final String uuid) {

        try {
            UUIDDetail detail = uuidService.getUUIDDetails(uuid);
            model.addAttribute("uuidDetail", detail);
            model.addAttribute("uuid", uuid);
            model.addAttribute(UUIDConstants.SUCCESS, true);
        } catch (UUIDException e) {
            // all exceptions are caught so the user will get the error message in a clean manner
            model.addAttribute(UUIDConstants.ERROR_MESSAGE, e.getMessage());
            model.addAttribute(UUIDConstants.SUCCESS, false);
        }
        return model;
    }

    //Returns the Constant value for maximum allowed UUIDs
    @RequestMapping (value="/uuidConstant.json", method = RequestMethod.GET)
    public ModelMap getUUIDConstant(final ModelMap model) {
        model.addAttribute("uuidMaxAllowed", maxUUIDAllowed);
        model.addAttribute(UUIDConstants.SUCCESS, true);
        return model;
    }

    protected void setUuidService(final UUIDService uuidService) {
        this.uuidService = uuidService;
    }

    protected void setUuidReportService(final UUIDReportService uuidReportService) {
        this.uuidReportService = uuidReportService;
    }

    public void setMaxUUIDAllowed(String maxUUIDAllowed) {
        this.maxUUIDAllowed = maxUUIDAllowed;
    }
}
