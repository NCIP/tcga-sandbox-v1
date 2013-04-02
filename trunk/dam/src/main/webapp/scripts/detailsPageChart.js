/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.namespace('tcga.dataPortal');

tcga.dataPortal.createTumorCancerSamplesChart = function(data) {

    var matchedColor = "999966";
    var unmatchedColor = "336699";
    var tumorColor = "993300";
    var stackColors = [ tumorColor, matchedColor, unmatchedColor ];

    //begin data set
    var datasetSettings = "[";

    // for each data type (stack) in the chart... i.e fusion chart 'series' values
    for (i = 0; i < data.length; i++) {

        if (i != 0)
            datasetSettings += ',';

        datasetSettings += '{"seriesname":"' + data[i].get('sampleType') + '","color":"' + stackColors[i] + '","showvalues":"0",';
        datasetSettings += '"data":[';
        datasetSettings += '{"value":"' + data[i].get('copyNumber') + '"},';
        datasetSettings += '{"value":"' + data[i].get('methylation') + '"},';
        datasetSettings += '{"value":"' + data[i].get('geneExpression') + '"},';
        datasetSettings += '{"value":"' + data[i].get('miRnaExpression') + '"}';
        datasetSettings += ']}';
    }

    //end data set
    datasetSettings += ']';

    //complete json data object with chart setting
    var chartSetting = '{"palette":"2","caption":"Data Type Summary","showlabels":"1","showvalues":"1","numberprefix":"","showsum":"1", "formatnumberscale":"0", "subcaption": "by Number of Samples","xaxisname": "Data Type","Yaxisname": "Number of Samples","useroundedges":"0", "legendborderalpha":"0"}';
    var categoriesSetting = '[{"category":[{"label":"Copy Number"}, {"label":"Methylation"},{"label":"Gene Expression"},{"label":"miRNA Expression" } ]}]';
    var strJSONObject = '{"chart":' + chartSetting + ',' + '"categories":' + categoriesSetting + ',' + '"dataset":' + datasetSettings + '}';

    //feed object into the chart (swf file) and render into cancerSamplesSummaryChart div
    FusionCharts._fallbackJSChartWhenNoFlash();
    var myChart = FusionCharts.render("/tcga/Charts/StackedColumn2D.swf", "myChartId", "620", "500", "cancerSamplesSummaryChart", strJSONObject, "json");
}
