/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BCRPipelineReportConstants.BCR_PREP_HOME_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BCRPipelineReportConstants.BCR_PREP_HOME_VIEW;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATAREPORTS_HOME_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATAREPORTS_HOME_VIEW;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.STATS_DASHBOARD_HOME_URL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.STATS_DASHBOARD_HOME_VIEW;

/**
 * Controller that defines the home page of the datareports
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@Controller
public class DatareportsBasicController {

    /**
     * datareports home handler
     * @return view Name
     */
    @RequestMapping (value = DATAREPORTS_HOME_URL, method = RequestMethod.GET)
    public String datareportsHomeHandler() {
        return DATAREPORTS_HOME_VIEW;
    }

    @RequestMapping (value = BCR_PREP_HOME_URL, method = RequestMethod.GET)
    public String bcrPipeLineReportHomeHandler() {
        return BCR_PREP_HOME_VIEW;
    }

    @RequestMapping (value = STATS_DASHBOARD_HOME_URL, method = RequestMethod.GET)
    public String statsDashboardHomeHandler() {
        return STATS_DASHBOARD_HOME_VIEW;
    }

}//End of Class
