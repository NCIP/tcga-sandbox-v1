/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.BubbleXYZ;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.StatsDashboardService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

/**
 * Json controller for the stats dashboard
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Controller
public class StatsDashboardJsonController {

    @Autowired
    private StatsDashboardService service;

    protected final Log logger = LogFactory.getLog(getClass());

    @RequestMapping(value = "/totalArchiveDownloadedData.json")
    public ModelMap totalArchiveDownloadedData(final ModelMap model) {
        model.addAttribute("chart", service.getChartForTotalArchiveDownloaded());
        model.addAttribute("categories", service.getCategoryListForArchives(service.getNumberArchivesDownloadedTotal()));
        model.addAttribute("dataset", service.getDatasetListForArchives(true, false, "Downloaded", null,
                service.getNumberArchivesDownloadedTotal(), service.getSizeArchivesDownloadedTotal()));
        return model;
    }

    @RequestMapping(value = "/ddNumberArchiveDownloadedData.json")
    public ModelMap ddNumberArchiveDownloadedData(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.DISEASE) final String disease) {
        model.addAttribute("chart", service.getChartForDrillDownArchiveDownloaded(disease, "Number"));
        model.addAttribute("categories", service.getCategoryListForArchives(service.getNumberArchivesDownloadedDrillDown(disease)));
        model.addAttribute("dataset", service.getDatasetListForArchives(false, true, null, "Number",
                service.getNumberArchivesDownloadedDrillDown(disease),
                service.getCumulativeNumberArchivesDownloadedDrillDown(disease)));
        return model;
    }

    @RequestMapping(value = "/ddSizeArchiveDownloadedData.json")
    public ModelMap ddSizeArchiveDownloadedData(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.DISEASE) final String disease) {
        model.addAttribute("chart", service.getChartForDrillDownArchiveDownloaded(disease, "Size"));
        model.addAttribute("categories", service.getCategoryListForArchives(service.getSizeArchivesDownloadedDrillDown(disease)));
        model.addAttribute("dataset", service.getDatasetListForArchives(false, true, null, "Size",
                service.getSizeArchivesDownloadedDrillDown(disease),
                service.getCumulativeSizeArchivesDownloadedDrillDown(disease)));
        return model;
    }

    @RequestMapping(value = "/totalArchiveReceivedData.json")
    public ModelMap totalArchiveReceivedData(final ModelMap model) {
        model.addAttribute("chart", service.getChartForTotalArchiveReceived());
        model.addAttribute("categories", service.getCategoryListForArchives(service.getNumberArchivesReceivedTotal()));
        model.addAttribute("dataset", service.getDatasetListForArchives(true, false, "Received", null,
                service.getNumberArchivesReceivedTotal(), service.getSizeArchivesReceivedTotal()));
        return model;
    }

    @RequestMapping(value = "/ddNumberArchiveReceivedData.json")
    public ModelMap ddNumberArchiveReceivedData(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.DISEASE) final String disease) {
        model.addAttribute("chart", service.getChartForDrillDownArchiveReceived(disease, "Number"));
        model.addAttribute("categories", service.getCategoryListForArchives(service.getNumberArchivesReceivedDrillDown(disease)));
        model.addAttribute("dataset", service.getDatasetListForArchives(false, true, null, "Number",
                service.getNumberArchivesReceivedDrillDown(disease),
                service.getCumulativeNumberArchivesReceivedDrillDown(disease)));
        return model;
    }

    @RequestMapping(value = "/ddSizeArchiveReceivedData.json")
    public ModelMap ddSizeArchiveReceivedData(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.DISEASE) final String disease) {
        model.addAttribute("chart", service.getChartForDrillDownArchiveReceived(disease, "Size"));
        model.addAttribute("categories", service.getCategoryListForArchives(service.getSizeArchivesReceivedDrillDown(disease)));
        model.addAttribute("dataset", service.getDatasetListForArchives(false, true, null, "Size",
                service.getSizeArchivesReceivedDrillDown(disease),
                service.getCumulativeSizeArchivesReceivedDrillDown(disease)));
        return model;
    }

    @RequestMapping(value = "/filterPieChartData.json")
    public ModelMap filterPieChartData(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.TYPE) final String type) {
        model.addAttribute("chart", service.getChartForFilterPieChart(type));
        model.addAttribute("data", service.getFilterPieChart(type));
        return model;
    }

    @RequestMapping(value = "/batchBubbleChartData.json")
    public ModelMap batchBubbleChartData(final ModelMap model) {
        model.addAttribute("chart", service.getChartForBubbleBatch());
        model.addAttribute("dataset", new HashMap() {{
            put("data", service.getBubbleChartBatch());
        }});
        return model;
    }

    @RequestMapping(value = "/platformTypeBubbleChartData.json")
    public ModelMap platformTypeBubbleChartData(final ModelMap model) {
        final List<BubbleXYZ> data = service.getBubbleChartPlatformType();
        model.addAttribute("chart", service.getChartForBubblePlatformType("" + data.size()));
        model.addAttribute("categories", service.getCategoryForBubblePlatformType());
        model.addAttribute("dataset", new HashMap() {{
            put("color", "4371AB");
            put("data", data);
        }});
        return model;
    }

    @RequestMapping(value = "/ddFilterPieChartData.json")
    public ModelMap ddFilterPieChartData(
            final ModelMap model,
            @RequestParam(value = DatareportsCommonConstants.TYPE) final String type,
            @RequestParam(value = DatareportsCommonConstants.SELECTION) final String selection) {
        model.addAttribute("chart", service.getChartForDrillDownFilterPieChart(type, selection));
        model.addAttribute("data", service.getFilterPieChartDrillDown(type, selection));
        return model;
    }


}//End of Class

