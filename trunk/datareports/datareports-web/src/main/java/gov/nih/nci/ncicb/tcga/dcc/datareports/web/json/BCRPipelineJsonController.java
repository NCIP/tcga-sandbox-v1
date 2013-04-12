/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.NodeData;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.BCRPipelineReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.CodeTablesReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ALL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;

/**
 * Json controller class for all bcrpipeline reports data calls
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class BCRPipelineJsonController {

    @Autowired
    private BCRPipelineReportService service;

    @Autowired
    private CodeTablesReportService diseaseService;

    protected final Log logger = LogFactory.getLog(getClass());

    @RequestMapping(value = "/diseases.json", method = RequestMethod.GET)
    public ModelMap allDisease(final ModelMap model) {
        model.addAttribute("diseases", diseaseService.getTumor());
        return model;
    }

    @RequestMapping(value = "/datesFromFile.json", method = RequestMethod.GET)
    public ModelMap allDatesFromJsonFiles(final ModelMap model) {
        model.addAttribute("datesFromFile", service.getDatesFromInputFiles());
        return model;
    }

    @RequestMapping(value = "/pRepData.json", method = RequestMethod.GET)
    public ModelMap pipeLineReportData(
            final ModelMap model,
            @RequestParam(value = DISEASE, required = false) final String disease,
            @RequestParam(value = DATE, required = false) final String date) {

        int success = service.readBCRInputFiles(disease, date);
        if (success == 1) {
            model.addAttribute("graphConfig", service.getGraphConfigData());
            model.addAttribute("nodeData", service.getNodeDataListData());
            model.addAttribute("totals", service.getTotalData());
            if (disease == null || ALL.equalsIgnoreCase(disease)) {
                model.addAttribute("tumorTypes", service.getTumorTypesData());
            }
        } else {
            model.addAttribute("nodeData", new NodeData("FailErrorFail", null, null, null, null, null));
        }
        return model;
    }

}//End of Class
