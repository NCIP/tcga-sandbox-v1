/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

var ddArchiveChart1;
var ddArchiveChart2;
var ddPieChart1;

function ddArchiveChart(params) {
    var param = params.split(',');
    var disease = param[0];
    var type = param[1];
    FusionCharts._fallbackJSChartWhenNoFlash();
    if (ddArchiveChart1 == null || ddArchiveChart1 == undefined) {
        ddArchiveChart1 = new FusionCharts("Charts/ScrollCombiDY2D.swf", "ddNumberArchiveChartId", "950", "380", "0", "1");
    }
    ddArchiveChart1.setJSONUrl("ddNumberArchive" + type + "Data.json?disease=" + disease);
    ddArchiveChart1.render("chartSubContainer1");
    if (ddArchiveChart2 == null || ddArchiveChart2 == undefined) {
        ddArchiveChart2 = new FusionCharts("Charts/ScrollCombiDY2D.swf", "ddSizeArchiveChartId", "950", "380", "0", "1");
    }
    ddArchiveChart2.setJSONUrl("ddSizeArchive" + type + "Data.json?disease=" + disease);
    ddArchiveChart2.render("chartSubContainer2");
    $('html, body').animate({scrollTop: $("#chartSubContainer1").offset().top }, 2000);
}

function ddPieChart(params) {
    var param = params.split(',');
    var type = param[0];
    var selection = param[1];
    FusionCharts._fallbackJSChartWhenNoFlash();
    if (ddPieChart1 == null || ddPieChart1 == undefined) {
        ddPieChart1 = new FusionCharts("Charts/Column2D.swf", "ddPieChartId", "882", "342", "0", "1");
    }
    var jsonURL = "ddFilterPieChartData.json?type=" + type + "&selection=" + selection;
    ddPieChart1.setJSONUrl(encodeURIComponent(jsonURL));
    ddPieChart1.render("chartSubContainer1");
    document.getElementById('chartSubContainer2').innerHTML = "";
    $('html, body').animate({scrollTop: $("#chartSubContainer1").offset().top }, 2000);
}

pieChartBatch = function() {
    FusionCharts._fallbackJSChartWhenNoFlash();
    var pieChart1 = new FusionCharts("Charts/Bubble.swf", "pieBatchId", "882", "342", "0", "1");
    pieChart1.setJSONUrl("batchBubbleChartData.json?type=batchFilter");
    pieChart1.render("pieBatchContainer");
}

pieChartPlatformType = function() {
    FusionCharts._fallbackJSChartWhenNoFlash();
    var pieChart2 = new FusionCharts("Charts/Bubble.swf", "piePlatformTypeId", "882", "392", "0", "1");
    pieChart2.setJSONUrl("platformTypeBubbleChartData.json?type=platformTypeFilter");
    pieChart2.render("piePlatformTypeContainer");
}

pieChartAccessTier = function() {
    FusionCharts._fallbackJSChartWhenNoFlash();
    var pieChart3 = new FusionCharts("Charts/Pie3D.swf", "pieAccessTiertId", "441", "288", "0", "1");
    pieChart3.setJSONUrl("filterPieChartData.json?type=accessTierFilter");
    pieChart3.render("pieAccessTierContainer");
}

pieChartLevel = function() {
    FusionCharts._fallbackJSChartWhenNoFlash();
    var pieChart4 = new FusionCharts("Charts/Pie3D.swf", "pieLevelId", "441", "288", "0", "1");
    pieChart4.setJSONUrl("filterPieChartData.json?type=levelFilter");
    pieChart4.render("pieLevelContainer");
}

chartTotalArchivesDownloaded = function() {
    FusionCharts._fallbackJSChartWhenNoFlash();
    var archiveChart1 = new FusionCharts("Charts/MSCombiDY2D.swf", "totalArchiveDownloadedChartId", "940", "360", "0", "1");
    archiveChart1.setJSONUrl("totalArchiveDownloadedData.json");
    archiveChart1.render("chartContainer2");
}

chartTotalArchivesReceived = function() {
    FusionCharts._fallbackJSChartWhenNoFlash();
    var archiveChart2 = new FusionCharts("Charts/MSCombiDY2D.swf", "totalArchiveReceivedChartId", "940", "360", "0", "1");
    archiveChart2.setJSONUrl("totalArchiveReceivedData.json");
    archiveChart2.render("chartContainer1");
}

initCharts = function() {
    pieChartBatch();
    pieChartPlatformType();
    pieChartAccessTier();
    pieChartLevel();
    chartTotalArchivesReceived();
    chartTotalArchivesDownloaded();
}

Ext.onReady(initCharts, this);