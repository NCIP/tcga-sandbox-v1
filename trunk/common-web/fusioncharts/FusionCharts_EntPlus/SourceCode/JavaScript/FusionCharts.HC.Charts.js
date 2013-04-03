/**!
 * @license FusionCharts JavaScript Library
 * Copyright FusionCharts Technologies LLP
 * License Information at <http://www.fusioncharts.com/license>
 *
 * @author FusionCharts Technologies LLP
 * @version fusioncharts/3.2.2-servicerelease1.4200
 */

(function () {
    // Register the module with FusionCharts and als oget access to a global
    // variable within the core's scope.
    var global = FusionCharts(['private', 'modules.renderer.highcharts-charts']);
    // Check whether the module has been already registered. If true, then
    // do not bother to re-register.
    if (global === undefined) {
        return;
    }

    var lib = global.hcLib,

    //strings
    BLANKSTRINGPLACEHOLDER = lib.BLANKSTRINGPLACEHOLDER,
    BLANKSTRING = lib.BLANKSTRING,

    createTrendLine = lib.createTrendLine,

    //add the tools thats are requared
    pluck = lib.pluck,
    getValidValue = lib.getValidValue,
    pluckNumber = lib.pluckNumber,
    defaultPaletteOptions = lib.defaultPaletteOptions,
    getFirstValue = lib.getFirstValue,
    getDefinedColor = lib.getDefinedColor,
    parsePointValue = lib.parsePointValue,
    parseUnsafeString = lib.parseUnsafeString,
    FC_CONFIG_STRING = lib.FC_CONFIG_STRING,
    extend2 = lib.extend2,//old: jarendererExtend / margecolone
    getDashStyle = lib.getDashStyle, // returns dashed style of a line series

    toPrecision = lib.toPrecision,

    stubFN = lib.stubFN,
    hasSVG = lib.hasSVG,

    getColumnColor = lib.graphics.getColumnColor,
    getFirstColor = lib.getFirstColor,
    setLineHeight = lib.setLineHeight,
    pluckFontSize = lib.pluckFontSize, // To get the valid font size (filters negative values)
    pluckColor = lib.pluckColor,
    getFirstAlpha = lib.getFirstAlpha,
    getDarkColor = lib.graphics.getDarkColor,
    getLightColor = lib.graphics.getLightColor,
    convertColor = lib.graphics.convertColor,
    COLOR_TRANSPARENT = lib.COLOR_TRANSPARENT,
    POSITION_CENTER = lib.POSITION_CENTER,
    POSITION_TOP = lib.POSITION_TOP,
    POSITION_BOTTOM = lib.POSITION_BOTTOM,
    POSITION_RIGHT = lib.POSITION_RIGHT,
    POSITION_LEFT = lib.POSITION_LEFT,
    INT_ZERO = 0,

    chartAPI = lib.chartAPI,

    titleSpaceManager = lib.titleSpaceManager,

    placeLegendBlockBottom = lib.placeLegendBlockBottom,

    placeLegendBlockRight = lib.placeLegendBlockRight,

    mapSymbolName = lib.graphics.mapSymbolName,

    singleSeriesAPI = chartAPI.singleseries,

    multiSeriesAPI = chartAPI.multiseries,
    COMMASTRING = lib.COMMASTRING,
    STRINGUNDEFINED  = lib.STRINGUNDEFINED ,
    ZEROSTRING = lib.ZEROSTRING,
    ONESTRING = lib.ONESTRING,
    HUNDREDSTRING = lib.HUNDREDSTRING,
    PXSTRING = lib.PXSTRING,
    BGRATIOSTRING = lib.BGRATIOSTRING,
    COMMASPACE = lib.COMMASPACE,
    creditLabel = false;





    /////////////////  column2d //////////////
    //add the charts
    //only the point and default series will differ from singleSeriesAPI
    chartAPI('column2d', {
        standaloneInit: true,
        creditLabel : creditLabel
    }, chartAPI.column2dbase);


    /////////////// column3d ///////////
    // we inherit Column2D for data manipulation
    // and change the defaultSeriesType to column3d
    chartAPI('column3d', {
        defaultSeriesType : 'column3d',
        defaultPlotShadow: 1
    }, chartAPI.column2d);


    /////////////// Bar2D ///////////
    // we inherit Column2D for data manipulation
    // and change the defaultSeriesType to bar and space management to barbase
    chartAPI('bar2d', {
        isBar : true,
        defaultSeriesType : 'bar',
        spaceManager : chartAPI.barbase
    }, chartAPI.column2d);


    /////////////// Line ///////////
    chartAPI('line', {
        standaloneInit: true,
        creditLabel : creditLabel
    }, chartAPI.linebase);


    /////////////// Area ///////////
    chartAPI('area2d', {
        standaloneInit: true,
        creditLabel : creditLabel
    }, chartAPI.area2dbase);


    /////////////// pie2d ///////////
    chartAPI('pie2d', {
        standaloneInit: true,
        defaultSeriesType : 'pie',
        defaultPlotShadow: 1,

        point : function (chartName, series, data, FCChartObj, HCObj) {
            var name, index, dataValue, dataObj, pointShadow,
            setColor, setAlpha, setRatio, setPlotBorderColor, setPlotBorderAlpha,
            totalValue = 0, displayValueText, labelText, toolText, pValue, value,
            TTValue, dataIndex = 0, dataArr = [], displayValue,
            // thickness of pie slice border
            setBorderWidth = pluck(FCChartObj.plotborderthickness , ONESTRING),
            // whether to use 3d lighing effect on pie
            use3DLighting = pluckNumber(FCChartObj.use3dlighting, 1),
            // radius of the pie 3d lighting effect
            radius3D = use3DLighting ? pluckNumber(FCChartObj.radius3d,
            FCChartObj['3dradius'], 90) : 100,
            // whether to show the zero values on pie
            showZeroPies = pluckNumber(FCChartObj.showzeropies, 1),
            // Flag to decide, whether we show pie label, tolltext and values
            labelsEnabled = true,
            showPercentInToolTip = pluckNumber(FCChartObj.showpercentintooltip, 1),
            showLabels = pluckNumber(FCChartObj.showlabels, 1),
            showValues = pluckNumber(FCChartObj.showvalues, 1),
            showPercentValues = pluckNumber(FCChartObj.showpercentvalues, FCChartObj.showpercentagevalues, 0),
            toolTipSepChar = pluck(FCChartObj.tooltipsepchar, FCChartObj.hovercapsepchar, COMMASPACE),
            labelSepChar = pluck(FCChartObj.labelsepchar, toolTipSepChar),
            piebordercolor = pluck(FCChartObj.plotbordercolor, FCChartObj.piebordercolor),
            NumberFormatter = HCObj[FC_CONFIG_STRING].numberFormatter,
            length = data.length, HcDataObj;

            // radius3d can not be greater than 100 and can not be less than 0
            if (radius3D > 100) {
                radius3D = 100;
            }
            if (radius3D < 0) {
                radius3D = 0;
            }

            //enable the legend for the pie
            if (pluckNumber(FCChartObj.showlegend, 0)) {
                HCObj.legend.enabled = true;
                HCObj.legend.reversed =
                    !Boolean(pluckNumber(FCChartObj.reverselegend , 0));
                series.showInLegend = true;
            }

            // If both the labels and the values are disable then disable the datalabels
            if (!showLabels && !showValues) {
                HCObj.plotOptions.series.dataLabels.enabled = false;

                // If labels, values and tooltex are disabled then don't need to calculate
                // labels and tooltext
                if (HCObj.tooltip.enabled === false) {
                    labelsEnabled = false;
                }
            }

            // Filtering null and 0 values from data
            for (index = 0; index < length; index += 1) {
                dataObj = data[index];

                dataValue = NumberFormatter.getCleanValue(dataObj.value, true);

                if (!(dataValue === null || (!showZeroPies && dataValue === 0))) {
                    dataArr.push(dataObj);
                    totalValue += dataValue;
                }
            }

            // Pass the configuration whether user wants to supprss rotation.
            series.enableRotation = dataArr.length > 1 ? pluck(FCChartObj.enablerotation, 1) : 0;

            for (index = dataArr.length - 1; index >= 0; index -= 1) {
                // numberFormatter.getCleanValue(dataObj.value, true);
                // individual data obj
                // for further manipulation
                dataObj = dataArr[index];

                // Taking the value
                // we multiply the value with 1 to convert it to integer
                dataValue = NumberFormatter.getCleanValue(dataObj.value, true);


                // Label provided with data point
                name = parseUnsafeString(pluck(dataObj.label, dataObj.name, BLANKSTRING));

                // parsing slice cosmetics attribute supplied in data points
                // Color for each slice
                setColor = pluck(dataObj.color,
                HCObj.colors[index % HCObj.colors.length]);
                // Alpha for each slice
                setAlpha = pluck(dataObj.alpha, FCChartObj.plotfillalpha, HUNDREDSTRING);
                // each slice border color
                setPlotBorderColor = pluck(dataObj.bordercolor, piebordercolor, getLightColor(setColor, 25)).
                    split(COMMASTRING)[0];
                // each slice border alpha
                setPlotBorderAlpha = FCChartObj.showplotborder == ZEROSTRING ?
                    ZEROSTRING : pluck(dataObj.borderalpha, FCChartObj.plotborderalpha,
                FCChartObj.pieborderalpha, '80');


                // Used to set alpha of the shadow
                pointShadow = {
                    opacity: Math.max(setAlpha, setPlotBorderAlpha) / 100
                };

                // Finally insert the value and other point cosmetics in HighChart's series.data array
                series.data.push({
                    showInLegend: !(name === BLANKSTRING), // prevent legend item when no label
                    y: dataValue,
                    name: name,
                    shadow: pointShadow,
                    toolText: parseUnsafeString(getValidValue(dataObj.tooltext)),
                    color: this.getPointColor(setColor, setAlpha, radius3D),
                    borderColor: convertColor(setPlotBorderColor,
                    setPlotBorderAlpha),
                    borderWidth: setBorderWidth,
                    link : getValidValue(dataObj.link),
                    sliced: Boolean(pluckNumber(dataObj.issliced, 0))
                });

                // Adding label, tooltext, and display value
                if(labelsEnabled) {
                    toolText = name;
                    pValue = NumberFormatter.percentValue(dataValue / totalValue * 100);
                    value = NumberFormatter.dataLabels(dataValue) || BLANKSTRING;
                    TTValue = showPercentInToolTip === 1 ? pValue : value;
                    labelText = showLabels === 1 ? toolText : BLANKSTRING;
                    displayValueText = showValues === 1 ?
                        (showPercentValues === 1 ? pValue : value ) : BLANKSTRING;

                    displayValue = getValidValue(dataObj.displayvalue);

                    if (displayValue) {
                        displayValueText = displayValue;
                    } else {
                        //create the datalabel str
                        if (displayValueText !== BLANKSTRING && labelText !== BLANKSTRING) {
                            displayValueText = labelText + labelSepChar + displayValueText;
                        }
                        else {
                            displayValueText = pluck(labelText, displayValueText);
                        }
                    }

                    // Create the Tooltext
                    if (toolText != BLANKSTRING) {
                        toolText = toolText + toolTipSepChar + TTValue;
                    }
                    else {
                        toolText = TTValue;
                    }
                    HcDataObj = series.data[dataIndex];
                    HcDataObj.displayValue = displayValueText;
                    HcDataObj.toolText = pluck(HcDataObj.toolText, toolText);
                    dataIndex += 1;
                }
            }

            ///special conf for pie/doughnut
            HCObj.legend.enabled = FCChartObj.showlegend == ONESTRING ? true : false;
            HCObj.chart.startingAngle = pluck(FCChartObj.startingangle, 0);

            //return series
            return series;
        },
        // Function that produce the point color
        getPointColor : function (color, alpha, radius3D) {
            var colorObj, shadowIntensity, shadowColor, highLightIntensity, highLight;
            color = getFirstColor(color);
            alpha = getFirstAlpha(alpha);
            if (radius3D < 100 && hasSVG) { //radial gradient is not supported in VML
                shadowIntensity = Math.floor((0.85*(100-0.35*radius3D))*100)/100;
                shadowColor = getDarkColor(color, shadowIntensity);
                highLightIntensity = Math.floor((0.5*(100+radius3D))*100)/100;
                highLight = getLightColor(color, highLightIntensity);
                colorObj = {
                    FCcolor : {
                        color : highLight + COMMASTRING + shadowColor,
                        alpha : alpha + COMMASTRING + alpha,
                        ratio : radius3D + ',100',
                        radialGradient : true
                    }
                };
            }
            else {
                colorObj = {
                    FCcolor : {
                        color : color + COMMASTRING + color,
                        alpha : alpha + COMMASTRING + alpha,
                        ratio : '0,100'
                    }
                };
            }

            return colorObj;
        },

        //add the axis configurar function
        configureAxis: function (hcJSON, fcJSON) {
            var length = 0, conf = hcJSON[FC_CONFIG_STRING], labelArr, dataArr, index = 0;
            //fix for pie datalabels style issue
            hcJSON.plotOptions.series.dataLabels.style = hcJSON.xAxis.labels.style;
            hcJSON.plotOptions.series.dataLabels.color = hcJSON.xAxis.labels.style.color;


            delete conf.x;
            delete conf[0];
            delete conf[1];
            // Making plotBorder and plotBackground transpatent
            //temp: added color to border
            hcJSON.chart.plotBorderColor =  hcJSON.chart.plotBackgroundColor = COLOR_TRANSPARENT;


            labelArr = conf.pieDATALabels = [];
            if (hcJSON.series.length === 1) {
                if ((dataArr = hcJSON.series[0].data) && (length = hcJSON.series[0].data.length) > 0 &&
                    hcJSON.plotOptions.series.dataLabels.enabled) {
                    for (; length --;) {
                        if (dataArr[length] && getValidValue(dataArr[length].displayValue) !== undefined) {
                            labelArr.push(dataArr[length].displayValue)
                        }
                    }
                }
            }

        },
        spaceManager: function (hcJSON, fcJSON, width, height) {
            var conf = hcJSON[FC_CONFIG_STRING], textWidthArr = [],
            FCchartName = conf.FCchartName,
            SmartLabel = conf.smartLabel, length = pluckNumber(conf.pieDATALabels && conf.pieDATALabels.length, 0), labelMaxW = 0,
            textObj, fcJSONChart = fcJSON.chart,
            manageLabelOverflow = pluckNumber(fcJSONChart.managelabeloverflow, 0),
            slicingDistance = pluckNumber(fcJSONChart.slicingdistance, 20),
            pieRadius = pluckNumber(fcJSONChart.pieradius, 0),
            enableSmartLabels = pluckNumber(fcJSONChart.enablesmartlabels, fcJSONChart.enablesmartlabel, 1),
            skipOverlapLabels = pluckNumber(fcJSONChart.skipoverlaplabels, fcJSONChart.skipoverlaplabel, 1),
            isSmartLineSlanted = pluckNumber(fcJSONChart.issmartlineslanted, 1),
            labelDistance = pluckNumber(fcJSONChart.labeldistance, fcJSONChart.nametbdistance, 5),
            smartLabelClearance = pluckNumber(fcJSONChart.smartlabelclearance, 5),
            chartWorkingWidth = width - (hcJSON.chart.marginRight + hcJSON.chart.marginLeft),
            chartWorkingHeight = height - (hcJSON.chart.marginTop + hcJSON.chart.marginBottom),
            minOfWH = Math.min(chartWorkingHeight, chartWorkingWidth),
            smartLineColor = pluck(fcJSONChart.smartlinecolor, defaultPaletteOptions.plotFillColor[hcJSON.chart.paletteIndex ]),
            smartLineAlpha = pluckNumber(fcJSONChart.smartlinealpha, 100),
            smartLineThickness = pluckNumber(fcJSONChart.smartlinethickness, 1),
            dataLebelsOptions = hcJSON.plotOptions.series.dataLabels,
            
            pieMinRadius = pieRadius === 0 ? minOfWH * 0.15 : pieRadius,
            avableRedius = 0, pieMinDia = 2 * pieMinRadius;

            dataLebelsOptions.connectorWidth = smartLineThickness;
            dataLebelsOptions.connectorColor = convertColor(smartLineColor, smartLineAlpha);


            
            chartWorkingHeight -= titleSpaceManager(hcJSON, fcJSON, chartWorkingWidth,
            pieMinDia < chartWorkingHeight ? chartWorkingHeight - pieMinDia : chartWorkingHeight / 2);

            if (fcJSONChart.showlegend == ONESTRING) {
                if (pluck(fcJSONChart.legendposition, POSITION_BOTTOM).toLowerCase() != POSITION_RIGHT) {
                    chartWorkingHeight -= placeLegendBlockBottom(hcJSON, fcJSON, chartWorkingWidth,
                    chartWorkingHeight / 2, true);
                } else {
                    chartWorkingWidth -= placeLegendBlockRight(hcJSON, fcJSON,
                    chartWorkingWidth / 3, chartWorkingHeight, true);
                }
            }



            //now get the max width requared for all display text
            //set the style
            SmartLabel.setStyle(dataLebelsOptions.style);
            for (; length --;) {
                textWidthArr[length] = textObj = SmartLabel.getOriSize(conf.pieDATALabels[length]);
                labelMaxW = Math.max(labelMaxW, textObj.width);
            }

            //if the smart label is on then modify the label distance
            if (enableSmartLabels) {
                labelDistance = smartLabelClearance + slicingDistance;
            }
            //if redius not supplyed then auto calculate it
            if (pieRadius === 0) {
                avableRedius = Math.min((chartWorkingWidth / 2) - labelMaxW, chartWorkingHeight / 2) - labelDistance;
                if (avableRedius >= pieMinRadius) {//there has space for min width
                    pieMinRadius = avableRedius;
                }
                else {
                    var shortFall = pieMinRadius - avableRedius;
                    labelDistance = Math.max(labelDistance - shortFall, slicingDistance);
                }
            }


            //add the slicing distance
            hcJSON.plotOptions.pie.slicedOffset  = slicingDistance;
            hcJSON.plotOptions.pie.size  =  2 * pieMinRadius;
            hcJSON.plotOptions.series.dataLabels.distance =  labelDistance;
            hcJSON.plotOptions.series.dataLabels.isSmartLineSlanted =  isSmartLineSlanted;
            hcJSON.plotOptions.series.dataLabels.enableSmartLabels =  enableSmartLabels;


            //if the chart is a doughnut charts
            if (FCchartName === 'doughnut2d' || FCchartName === 'doughnut3d') {
                var doughnutRadius = pluckNumber(fcJSONChart.doughnutradius, 0), x,
                innerradius, innerpercentR, ratioStr, diff50Percent, radius3Dpercent,
                use3DLighting = pluckNumber(fcJSONChart.use3dlighting, 1), poin2nd, point, data,
                radius3D = use3DLighting ? pluckNumber(fcJSONChart.radius3d, fcJSONChart['3dradius'], 50) : 100;
                if (radius3D > 100) {
                    radius3D = 100;
                }
                if (radius3D < 0) {
                    radius3D = 0;
                }

                /*
                 *decide inner redius
                 */
                if (doughnutRadius === 0 || doughnutRadius >= pieMinRadius) {
                    innerradius =  pieMinRadius / 2;
                }
                else {
                    innerradius = doughnutRadius;
                }

                hcJSON.plotOptions.pie.innerSize = 2 * innerradius;

                /*
                 *create doughnut type 3d lighting
                 */
                if (radius3D > 0 && hasSVG) { //radial gradient is not supported in VML
                    innerpercentR = parseInt(innerradius / pieMinRadius * 100, 10);
                    diff50Percent = (100 - innerpercentR) / 2;
                    radius3Dpercent = parseInt(diff50Percent * radius3D / 100, 10);
                    poin2nd = 2 * (diff50Percent - radius3Dpercent);
                    ratioStr = innerpercentR + COMMASTRING + radius3Dpercent + COMMASTRING + poin2nd + COMMASTRING + radius3Dpercent;
                    //loop for all points
                    if (hcJSON.series[0] && hcJSON.series[0].data) {
                        data = hcJSON.series[0].data;
                        for (x = 0, length = data.length; x < length; x += 1) {
                            point = data[x];
                            if (point.color.FCcolor) {
                                point.color.FCcolor.ratio = ratioStr;
                            }
                        }
                    }

                }
            }

        },

        creditLabel : creditLabel,

        eiMethods: {
            'togglePieSlice': function (index) {
                var vars = this.jsVars,
                hcObj = vars.hcObj,
                series;

                if (hcObj && hcObj.series &&
                    (series = hcObj.series[0]) && series.data &&
                    series.data[index] && series.data[index].slice) {

                    return series.data[series.xIncrement - 1 - index].slice();
                }
            }
        }
    }, singleSeriesAPI);


    /////////////// pie3d ///////////
    // we inherit pie3d for data manipulation
    chartAPI('pie3d', {
        defaultSeriesType : 'pie',
        creditLabel : creditLabel,

        // Pie2D (base) has defaultPlotShadow, but 3d does not.
        defaultPlotShadow: 0
    }, chartAPI.pie2d);




    /////////////// Doughnut2d ///////////
    // we inherit Pie2D for data manipulation
    // and change the getPointColor function to get the Doughnut chart color shade
    chartAPI('doughnut2d', {
        //function that produce the point color
        getPointColor : function (color, alpha, radius3D) {
            var colorObj, shadowIntensity, shadowColor, highLightIntensity, highLight;
            color = getFirstColor(color);
            alpha = getFirstAlpha(alpha);
            if (radius3D < 100 && hasSVG) { //radial gradient is not supported in VML
                shadowIntensity = Math.floor((85-0.2*(100-radius3D))*100)/100;
                shadowColor = getDarkColor(color, shadowIntensity);
                highLightIntensity = Math.floor((100-0.5*radius3D)*100)/100;
                highLight = getLightColor(color, highLightIntensity);
                colorObj = {
                    FCcolor : {
                        color : shadowColor + COMMASTRING + highLight+ COMMASTRING + highLight +
                            COMMASTRING + shadowColor,
                        alpha : alpha + COMMASTRING + alpha + COMMASTRING + alpha + COMMASTRING + alpha,
                        radialGradient : true
                    }
                };
            }
            else {
                colorObj = convertColor(color, alpha);
            }

            return colorObj;
        }
    }, chartAPI.pie2d);


    /////////////// Doughnut3d ///////////
    // we inherit doughnut2d for Doughnut3d
    chartAPI('doughnut3d', {
        // Diughnut2D (base derived from Pie2D) has defaultPlotShadow,
        // but 3D does not.
        defaultPlotShadow: 0
    }, chartAPI.doughnut2d);


    /////////////// Pareto2d ///////////
    chartAPI('pareto2d', {
        standaloneInit: true,
        point : function (chartName, series, data, FCChartObj, HCObj) {

            var itemValue, index, setColor, setAlpha, setRatio, dataLabel,
            lineAlpha, lineThickness, lineDashed, lineDashLen, lineDashGap, lineShadowOptions,
            anchorBgColor, anchorBgAlpha, anchorAlpha, anchorBorderColor, dashStyle,
            anchorBorderThickness, anchorRadius, anchorSides, toolText, countPoint,
            displayValue, columnDataObj, dataObj, colorArr, lineColor, showLabel,
            pointShadow, plotBorderAlpha, drawAnchors, displayValuePercent,
            // length of the data
            length = data.length,
            sumValue = 0,
            seriesLine = {},
            paletteIndex = HCObj.chart.paletteIndex,
            is3d = /3d$/.test(HCObj.chart.defaultSeriesType),
            isBar = this.isBar,
            setAngle = pluck(360 - FCChartObj.plotfillangle, 90),
            setBorderWidth = pluckNumber(FCChartObj.plotborderthickness , 1),
            isRoundEdges = HCObj.chart.useRoundEdges,
            toolTipSepChar = pluck(FCChartObj.tooltipsepchar, ", "),
            setPlotBorderColor = pluck(FCChartObj.plotbordercolor,
            defaultPaletteOptions.plotBorderColor[paletteIndex]).split(COMMASTRING)[0],
            setPlotBorderAlpha = FCChartObj.showplotborder == ZEROSTRING  ?
                ZEROSTRING : pluck(FCChartObj.plotborderalpha, FCChartObj.plotfillalpha, HUNDREDSTRING),
            xAxisObj = HCObj.xAxis,
            showCumulativeLine = pluckNumber(FCChartObj.showcumulativeline, 1),
            conf = HCObj[FC_CONFIG_STRING],
            axisGridManager = conf.axisGridManager,
            xAxisConf = conf.x,
            showtooltip = FCChartObj.showtooltip != ZEROSTRING,
            dataOnlyArr = [],
            tempLineSeries = [],
            // use3DLighting to show gredient color effect in 3D Column charts
            use3DLighting = pluckNumber(FCChartObj.use3dlighting, 1),
            NumberFormatter = HCObj[FC_CONFIG_STRING].numberFormatter,
            showLineValues = pluckNumber(FCChartObj.showlinevalues, FCChartObj.showvalues);

            // Managing plot border color for 3D column chart
            // 3D column chart doesn't show the plotborder by default until we set showplotborder true
            setPlotBorderAlpha = is3d ? (FCChartObj.showplotborder ? setPlotBorderAlpha : ZEROSTRING) : setPlotBorderAlpha;
            // Default  plotBorderColor  is FFFFFF for this 3d chart
            setPlotBorderColor = is3d ? pluck(FCChartObj.plotbordercolor, "#FFFFFF") : setPlotBorderColor;

            for (index = 0, countPoint = 0; index < length; index += 1) {
                dataObj = data[index];
                // vLine
                if (data[index].vline) {
                    axisGridManager.addVline(xAxisObj, dataObj, countPoint, HCObj);
                }
                else {
                    itemValue = NumberFormatter.getCleanValue(dataObj.value, true);
                    //if valid data then only add the point
                    if (itemValue !== null) {
                        //save the malid value so that no further parsePointValue needed
                        dataObj.value = itemValue;
                        dataOnlyArr.push(dataObj)
                        countPoint += 1;
                    }

                }
            }

            length = dataOnlyArr.length;

            //short the data
            dataOnlyArr.sort(function (a, b) {
                return b.value - a.value;
            });

            if (showCumulativeLine) {
                // If line is a dashed line
                lineDashed = pluckNumber(FCChartObj.linedashed, 0);
                // Managing line series color
                lineColor = getFirstColor(pluck(FCChartObj.linecolor,
                defaultPaletteOptions.plotBorderColor[paletteIndex]));
                // alpha of the line series
                lineAlpha = pluck(FCChartObj.linealpha, 100);
                // length of the dash
                lineDashLen = pluckNumber(FCChartObj.linedashlen, 5);
                // distance between dash
                lineDashGap = pluckNumber(FCChartObj.linedashgap, 4);
                // Thickness of the line
                lineThickness = pluckNumber(FCChartObj.linethickness, 2);
                // Line shadow options is created here once and this object is later
                // passed on to every data-point of the line-series.
                lineShadowOptions = {
                    opacity: lineAlpha / 100
                };

                // Whether to draw the anchors or not
                drawAnchors = pluckNumber(FCChartObj.drawanchors , FCChartObj.showanchors);
                if (drawAnchors === undefined) {
                    drawAnchors = lineAlpha != ZEROSTRING;
                }

                // Anchor cosmetics
                // Thickness of anchor border
                anchorBorderThickness = pluckNumber(FCChartObj.anchorborderthickness, 1);
                // sides of the anchor
                anchorSides = pluckNumber(FCChartObj.anchorsides, 0);
                // radius of anchor
                anchorRadius = pluckNumber(FCChartObj.anchorradius, 3);
                anchorBorderColor = getFirstColor(pluck(FCChartObj.anchorbordercolor,
                lineColor));
                anchorBgColor = getFirstColor(pluck(FCChartObj.anchorbgcolor,
                defaultPaletteOptions.anchorBgColor[paletteIndex]));
                anchorAlpha = getFirstAlpha(pluck(FCChartObj.anchoralpha, HUNDREDSTRING));
                //anchorBGalpha should not inherit from anchoralpha. but to replicate flash comented
                anchorBgAlpha = (getFirstAlpha(pluck(FCChartObj.anchorbgalpha, anchorAlpha))* anchorAlpha) / 100;

                // Dash Style
                dashStyle = lineDashed ? getDashStyle(lineDashLen, lineDashGap,
                lineThickness) : undefined;

                // Create line-series object
                seriesLine = {
                    yAxis: 1,
                    data: [],
                    type: 'line',
                    color: convertColor(lineColor, lineAlpha),
                    lineWidth: lineThickness,
                    marker: {
                        enabled: drawAnchors,
                        fillColor: convertColor(anchorBgColor, anchorBgAlpha),
                        lineColor: convertColor(anchorBorderColor, anchorAlpha),
                        lineWidth: anchorBorderThickness,
                        radius: anchorRadius,
                        symbol: mapSymbolName(anchorSides)
                    }
                };
            }
            else {
                if (FCChartObj.showsecondarylimits !== '1') {
                    FCChartObj.showsecondarylimits = '0';
                }
                if (FCChartObj.showdivlinesecondaryvalue !== '1') {
                    FCChartObj.showdivlinesecondaryvalue = '0';
                }
            }


            // Iterate through all level data
            for (index = 0; index < length; index += 1) {
                // individual data obj
                // for further manipulation
                dataObj = dataOnlyArr[index];
                // we check showLabel in individual data
                // if its set to 0 than we do not show the particular label
                showLabel = pluckNumber(dataObj.showlabel, FCChartObj.showlabels, 1);

                // Label of the data
                // getFirstValue returns the first defined value in arguments
                // we check if showLabel is not set to 0 in data
                // then we take the label given in data, it can be given using label as well as name too
                // we give priority to label if label is not there, we check the name attribute
                //dataLabel = parseUnsafeString(!showLabel ? BLANKSTRING : getFirstValue(dataObj.label, dataObj.name));
                dataLabel = parseUnsafeString(!showLabel ? BLANKSTRING : getFirstValue(dataObj.label, dataObj.name));

                // adding label in HighChart xAxis categories
                // increase category counter by one
                axisGridManager.addXaxisCat(xAxisObj, index, index, dataLabel);
                sumValue += itemValue = dataObj.value;

                // Color of the each data point
                setColor = pluck(dataObj.color, HCObj.colors[index % HCObj.colors.length]) +
                    COMMASTRING + getDefinedColor(FCChartObj.plotgradientcolor, defaultPaletteOptions.plotGradientColor[paletteIndex]);
                // Alpha of the data
                setAlpha = pluck(dataObj.alpha, FCChartObj.plotfillalpha, HUNDREDSTRING);
                // Fill ratio of the data
                setRatio = pluck(dataObj.ratio, FCChartObj.plotfillratio);

                // Used to set alpha of the shadow
                pointShadow = {
                    opacity: setAlpha / 100
                };
                plotBorderAlpha = pluck(dataObj.alpha, setPlotBorderAlpha) + BLANKSTRING;

                //calculate the color object for the set
                colorArr = getColumnColor (setColor, setAlpha, setRatio, setAngle,
                isRoundEdges, setPlotBorderColor, plotBorderAlpha, isBar, is3d);

                // Finally add the data
                // we call getPointStub function that manage displayValue, toolText and link
                series.data.push(extend2(this.getPointStub(dataObj, itemValue,
                dataLabel, HCObj), {
                    y : itemValue,
                    shadow: pointShadow,
                    color: colorArr[0],
                    borderColor: colorArr[1],
                    borderWidth: setBorderWidth,
                    use3DLighting : use3DLighting
                }));

                // Set the maximum and minimum found in data
                // pointValueWatcher use to calculate the maximum and minimum value of the Axis
                this.pointValueWatcher(HCObj, itemValue);

                // If we need we need to show the line series in pareto chart
                if (showCumulativeLine) {
                    // add the data to temp Line series data for farther calculation
                    tempLineSeries.push({
                        value : sumValue,
                        dataLabel : dataLabel,
                        tooltext : getValidValue(dataObj.tooltext)
                    });
                }
            }
            // set the xAxisConf catCount for further use
            xAxisConf.catCount = length;
            //create the dummy situation so that it work same as DYaxis with percentStacking
            //create the dummy axis conf object
            if (!conf[1]) {
                conf[1] = {};
            }
            // configure this axis to show this axis values as percent
            conf[1].stacking100Percent = true;

            // Line series on pareto
            if (showCumulativeLine && sumValue > 0) {

                // Iterate through line series and calculate the line series point values
                for (index = 0, length = tempLineSeries.length; index < length; index += 1) {
                    // individual data obj
                    dataObj = tempLineSeries [index];
                    // individual data object of column series
                    columnDataObj = series.data[index];
                    //value upto 2 decimal
                    itemValue = (dataObj.value / sumValue * 100);
                    displayValuePercent = NumberFormatter.percentValue(itemValue);

                    // display value for the line series data point
                    displayValue = columnDataObj.displayValue !== BLANKSTRING ?
                        displayValuePercent : BLANKSTRING;

                    if (showLineValues == 1) {
                        displayValue = displayValuePercent;
                    }
                    if (showLineValues == 0) {
                        displayValue = BLANKSTRING;
                    }

                    dataLabel = dataObj.dataLabel;

                    // Manipulating tooltext of the line series
                    toolText = showtooltip ? (dataObj.tooltext !== undefined ?
                        dataObj.tooltext : ((dataLabel !== BLANKSTRING ?
                        dataLabel  + toolTipSepChar : BLANKSTRING) + displayValuePercent)) : BLANKSTRING;

                    seriesLine.data.push({
                        shadow: lineShadowOptions,
                        y: itemValue,
                        toolText: toolText,
                        displayValue: displayValue,
                        //retrive link from column series
                        link: columnDataObj.link,
                        dashStyle: dashStyle
                    });
                }

                //return series
                return [series, seriesLine];
            }
            else {
                //remove all s axis text
                return series;
            }
        },
        defaultSeriesType : 'column',
        isDual: true,
        creditLabel : creditLabel
    }, singleSeriesAPI);


    /////////////// Pareto3d ///////////
    // we inherit pareto2d
    // and change the defaultSeriesType to column3d to render 3D Column
    chartAPI('pareto3d', {
        defaultSeriesType : 'column3d',
        defaultPlotShadow: 1
    }, chartAPI.pareto2d);


    /////////////// mscolumn2d ///////////
    chartAPI('mscolumn2d', {
        standaloneInit: true,
        creditLabel : creditLabel
    }, chartAPI.mscolumn2dbase);


    /////////////// mscolumn3d ///////////
    // we inherit mscolumn2d
    // and change the defaultSeriesType to column3d to render mscolumn3d chart
    chartAPI('mscolumn3d', {
        defaultSeriesType : 'column3d',
        // Default shadow is visible for 3D variant of MSColumn2D chart
        defaultPlotShadow: 1
    }, chartAPI.mscolumn2d);


    /////////////// msbar2d ///////////
    // we inherit mscolumn2d
    // and change the defaultSeriesType to bar to render msbar2d chart
    chartAPI('msbar2d', {
        isBar : true,
        defaultSeriesType : 'bar',
        spaceManager : chartAPI.barbase
    }, chartAPI.mscolumn2d);


    /////////////// msbar2d ///////////
    // we inherit msbar2d to render msbar3d chart
    chartAPI('msbar3d', {
        defaultSeriesType : 'bar3d',
        defaultPlotShadow: 1
    }, chartAPI.msbar2d);


    /////////////// msline ///////////
    chartAPI('msline', {
        standaloneInit: true,
        creditLabel : creditLabel
    }, chartAPI.mslinebase);


    /////////////// msline ///////////
    chartAPI('msarea', {
        standaloneInit: true,
        creditLabel : creditLabel
    }, chartAPI.msareabase);


    /***************  STACKED CHARTS  *********/
    //////  stackedcolumn2d  //////
    chartAPI('stackedcolumn2d', {
        isStacked: true
    }, chartAPI.mscolumn2d);


    ////// stackedcolumn3d //////
    chartAPI('stackedcolumn3d', {
        isStacked: true
    }, chartAPI.mscolumn3d);




    ////// stackedbar2d //////
    chartAPI('stackedbar2d', {
        isStacked: true
    }, chartAPI.msbar2d);


    ////// stackedbar3d //////
    chartAPI('stackedbar3d', {
        isStacked: true
    }, chartAPI.msbar3d);


    ////// stackedarea2d  //////
    chartAPI('stackedarea2d', {
        isStacked: true,
        showSum : 0
    }, chartAPI.msarea);

    ///******* NOT OPTIMIZE  ******////////
    chartAPI('marimekko', {
        isValueAbs : true,
        distributedColumns : true,
        isStacked: true,
        xAxisMinMaxSetter : stubFN,
        postSeriesAddition : function (HCObj, FCObj, width, height) {
            var conf = HCObj[FC_CONFIG_STRING],
            axisConfStack,
            length,
            stackArr,
            value,
            catPosition,
            y,
            total = 0,
            xAxis = HCObj.xAxis,
            catObj,
            volumeXratio = 100 / conf.marimekkoTotal,
            midposition,
            catArr = [],
            series = HCObj.series,
            xdistance,
            startPosition = 0,
            endPosition,
            plotBorderThickness = pluckNumber(FCObj.chart.plotborderthickness, 1),
            rotateValues = HCObj.chart.rotateValues,
            rotatePercentVals = pluckNumber(FCObj.chart.rotatexaxispercentvalues, 0),
            // this calculation is to properly align the vLine label border
            // with plotbottom
            vLineLabelYOffset = plotBorderThickness * -0.5 -
                (plotBorderThickness % 2 + (rotatePercentVals ? 0 : 4)),
            vLineLabelXOffset = rotatePercentVals ? 3 : 0,
            vLineLabelRotation = rotateValues ? 270 : 0,

            //for the first y axis
            axisStack = conf[0],
            isVline = !axisStack.stacking100Percent,
            inCanvasStyle = conf.inCanvasStyle;

            conf.isXYPlot = true;
            conf.distributedColumns = true;

            xAxis.min = 0;
            xAxis.max = 100;

            //remove all grid related conf
            xAxis.labels.enabled = false;
            xAxis.gridLineWidth = INT_ZERO;
            xAxis.alternateGridColor = COLOR_TRANSPARENT;

            axisConfStack = axisStack.stack;

            //stop interactive legend for marimekko
            FCObj.chart.interactivelegend = '0';

            //save the ref of the cat labels to set the position
            for (y = 0, length = HCObj.xAxis.plotLines.length; y < length; y += 1) {
                catObj = xAxis.plotLines[y];
                if (catObj.isGrid) {
                    //add the isCat attr so that it will work like scatter cat label
                    catObj.isCat = true
                    catArr[catObj.value] = catObj;
                }
            }

            if (axisConfStack.floatedcolumn && (stackArr = axisConfStack.floatedcolumn[0])) {
                for (catPosition = 0, length = stackArr.length; catPosition < length; ) {
                    total += value = (stackArr[catPosition].p || 0);
                    xdistance = value * volumeXratio;
                    midposition = startPosition + (xdistance / 2);
                    endPosition = startPosition + xdistance;
                    for (y = 0; y < series.length; y+= 1) {
                        HCObj.series[y].data[catPosition]['_FCX'] = startPosition;
                        HCObj.series[y].data[catPosition]['_FCW'] = xdistance;
                    }

                    //add the total value
                    if (conf.showStackTotal) {
                        HCObj.xAxis.plotLines.push({
                            value : midposition,
                            width : 0,
                            isVline : isVline,
                            isTrend : !isVline,
                            label : {
                                align : POSITION_CENTER,
                                textAlign : vLineLabelRotation,
                                rotation : rotateValues ? 270 : 0,
                                style : conf.trendStyle,
                                verticalAlign : POSITION_TOP,
                                y : 0,
                                x : 0,
                                text : conf.numberFormatter.yAxis(toPrecision(value, 10))
                            }
                        });
                    }

                    //position the cat labels
                    if (catArr[catPosition]) {
                        catArr[catPosition].value = midposition;
                        // In case of marimekko charts we need the width
                        // (xdistance) of each column also to render the
                        // horizontal axis. Hence saving here for future use.
                        catArr[catPosition]._weight = xdistance;
                    }

                    catPosition += 1;

                    //add the stack %
                    if (conf.showXAxisPercentValues && catPosition < length) {
                        HCObj.xAxis.plotLines.push({
                            value : endPosition,
                            width : 0,
                            isVine : true,
                            label : {
                                align : POSITION_CENTER,
                                textAlign :  rotatePercentVals ? POSITION_LEFT : POSITION_CENTER,
                                rotation : rotatePercentVals ? 270 : 0,
                                style : {
                                    color: inCanvasStyle.color,
                                    fontSize : inCanvasStyle.fontSize,
                                    fontFamily : inCanvasStyle.fontFamily,
                                    lineHeight : inCanvasStyle.lineHeight,
                                    border: '1px solid',
                                    borderColor: inCanvasStyle.color,
                                    backgroundColor: '#ffffff',
                                    backgroundOpacity: 1
                                },
                                verticalAlign : POSITION_BOTTOM,
                                y : vLineLabelYOffset,
                                x : vLineLabelXOffset,
                                text : conf.numberFormatter.percentValue(endPosition)
                            },
                            zIndex  : 5
                        });
                    }
                    startPosition = endPosition;
                }
            }
        },
        defaultSeriesType : 'floatedcolumn'
    }, chartAPI.stackedcolumn2d);

    ///// msstackedcolumn2d //////
    chartAPI('msstackedcolumn2d', {
        series: function (FCObj, HCObj, chartName, width, height) {
            var i, len, index, length,
            conf = HCObj[FC_CONFIG_STRING], lineset, totalDataSets = 0,
            series, minDataLength, seriesArr = [], innerDataSet;

            //enable the legend
            HCObj.legend.enabled = Boolean(pluckNumber(FCObj.chart.showlegend, 1))

            if (FCObj.dataset && FCObj.dataset.length > 0) {
                // add category
                this.categoryAdder(FCObj, HCObj);
                //add data series
                for (i = 0, len = FCObj.dataset.length; i < len; i += 1) {
                    if (innerDataSet = FCObj.dataset[i].dataset){
                        for (index = 0, length = innerDataSet.length; index < length; index += 1, totalDataSets += 1) {
                            series = {
                                data : [],
                                stack : i
                            };
                            minDataLength = Math.min(conf.oriCatTmp.length,
                            innerDataSet[index].data && innerDataSet[index].data.length)
                            //add data to the series
                            seriesArr = this.point(chartName, series,
                            innerDataSet[index], FCObj.chart, HCObj, minDataLength, totalDataSets, i);
                            // Turn of shadow for this chart in order to avoid series shadow
                            // overflow.
                            //seriesArr.shadow = false;
                            //push the data at the series array
                            HCObj.series.push(seriesArr);
                        }
                    }
                }

                // Adding lineset to HighChart series
                //if dual then it is the combi series
                if (this.isDual && FCObj.lineset && FCObj.lineset.length > 0) {
                    for (index = 0, length = FCObj.lineset.length; index < length; index += 1, totalDataSets += 1) {
                        series = {
                            data: [],
                            yAxis: 1,
                            type: 'line'
                        };
                        lineset = FCObj.lineset[index];
                        minDataLength = Math.min(conf.oriCatTmp.length,
                        lineset.data && lineset.data.length);

                        HCObj.series.push(chartAPI.msline.point.call(this, "msline", series,
                        lineset, FCObj.chart, HCObj, minDataLength, totalDataSets));
                    }
                }

                ///configure the axis
                this.configureAxis(HCObj, FCObj);
                ///////////Trend-lines /////////////////
                if (FCObj.trendlines) {
                    createTrendLine(FCObj.trendlines, HCObj.yAxis, HCObj[FC_CONFIG_STRING], false, this.isBar);
                }
            }
        }
    }, chartAPI.stackedcolumn2d)


    /*
     * *** Combination Charts ***
     * MSCombi2D
     * MSCombi3D
     * MSColumnLine3D
     * StackedColumn2DLine
     * StackedColumn3DLine
     * MSCombiDY2D
     * MSColumn3DLineDY
     * StackedColumn3DLineDY
     * MSStackedColumn2DLineDY
     */



    /////// mscombi2d ///////
    chartAPI('mscombi2d', {
        series : function (FCObj, HCObj, chartName) {
            var seriesIndex, length, dataset, catLength, FCChartObj = FCObj.chart,
            series, lineArr = [], columnArr = [], areaArr = [],
            isSY, renderAs,
            conf = HCObj[FC_CONFIG_STRING],
            isDual = this.isDual;

            //enable the legend
            HCObj.legend.enabled = Boolean(pluckNumber(FCObj.chart.showlegend, 1));

            if (FCObj.dataset && FCObj.dataset.length > 0) {
                // add category
                this.categoryAdder(FCObj, HCObj);
                catLength = conf.oriCatTmp.length;
                //add data series
                for (seriesIndex = 0, length = FCObj.dataset.length; seriesIndex < length; seriesIndex += 1) {
                    dataset = FCObj.dataset[seriesIndex];
                    isSY = isDual && pluck(dataset.parentyaxis, 'p').toLowerCase() === 's' ? true : false;
                    series = {
                        data : []
                    };
                    if (isSY) {
                        series.yAxis = 1;
                    }
                    renderAs = getFirstValue(dataset.renderas, BLANKSTRING).toLowerCase();
                    switch(renderAs){
                        case 'line':
                            series.type = 'line';
                            lineArr.push(chartAPI.msline.point.call(this, chartName, series, dataset,
                            FCChartObj, HCObj, catLength, seriesIndex));
                            break;
                        case 'area':
                            series.type = 'area';
                            //if there has any area chart then set series2D3Dshift as true
                            HCObj.chart.series2D3Dshift = true;
                            areaArr.push(chartAPI.msarea.point.call(this, chartName, series, dataset, FCChartObj, HCObj,
                            catLength, seriesIndex));
                            break;
                        case 'column':
                            columnArr.push(chartAPI.mscolumn2d.point.call(this, chartName, series,
                            FCObj.dataset[seriesIndex], FCChartObj, HCObj, catLength, seriesIndex));
                            break;
                        default:
                            if (isSY) {
                                series.type = 'line';
                                lineArr.push(chartAPI.msline.point.call(this, chartName, series, dataset,
                                FCChartObj, HCObj, catLength, seriesIndex));
                            }
                            else {
                                columnArr.push(chartAPI.mscolumn2d.point.call(this, chartName, series,
                                FCObj.dataset[seriesIndex], FCChartObj, HCObj, catLength, seriesIndex));
                            }
                    }
                }

                //push the data at the series array
                
                if (FCChartObj.areaovercolumns !== '0') {
                    HCObj.series = HCObj.series.concat(columnArr, areaArr, lineArr);
                }
                else {
                    HCObj.series = HCObj.series.concat(areaArr, columnArr, lineArr);
                }
                if (columnArr.length === 0) {
                    conf.hasNoColumn = true;
                }
                ///configure the axis
                this.configureAxis(HCObj, FCObj);
                ///////////Trend-lines /////////////////
                if (FCObj.trendlines) {
                    createTrendLine (FCObj.trendlines, HCObj.yAxis, HCObj[FC_CONFIG_STRING], false, this.isBar);
                }
            }
        }
    }, chartAPI.mscolumn2d);


    /////// mscombi3d ///////
    chartAPI('mscombi3d', {
        series : chartAPI.mscombi2d.series,
        eiMethods: 'view2D,view3D,resetView,rotateView,getViewAngles,fitToStage'
    },
    chartAPI.mscolumn3d);


    ///// MSColumnLine3D //////
    chartAPI('mscolumnline3d', {}, chartAPI.mscombi3d);


    ///// stackedcolumn2dline //////
    chartAPI('stackedcolumn2dline', {
        isStacked: true,
        stack100percent : 0
    }, chartAPI.mscombi2d);


    ///// stackedcolumn3dline //////
    chartAPI('stackedcolumn3dline', {
        isStacked: true,
        stack100percent : 0
    },
    chartAPI.mscombi3d);

    /// Stacked with dual Y aixs
    ///// mscombidy2d ////////
    chartAPI('mscombidy2d', {
        isDual: true
    }, chartAPI.mscombi2d);


    /////   MSColumn3DLineDY ////////
    chartAPI('mscolumn3dlinedy', {
        isDual: true
    }, chartAPI.mscolumnline3d);


    /////   StackedColumn3DLineDY ////////
    chartAPI('stackedcolumn3dlinedy', {
        isDual: true
    }, chartAPI.stackedcolumn3dline);


    ///// msstackedcolumn2dlinedy //////
    chartAPI('msstackedcolumn2dlinedy', {
        isDual: true,
        stack100percent : 0
    }, chartAPI.msstackedcolumn2d);

    //////////////// ------  End of Combination Charts ----- ////////////


    //////////////******* NOT OPTIMIZE ********/////////////


    ////////////// scroll charts /////////////
    ////scrollcolumn2d////
    chartAPI('scrollcolumn2d', {
        postSeriesAddition: chartAPI.scrollbase.postSeriesAddition,
        avgScrollPointWidth : 40
    }, chartAPI.mscolumn2d);

    ////scrollline2d////
    chartAPI('scrollline2d', {
        postSeriesAddition: chartAPI.scrollbase.postSeriesAddition,
        avgScrollPointWidth : 75
    }, chartAPI.msline);

    ////scrollarea2d////
    chartAPI('scrollarea2d', {
        postSeriesAddition: chartAPI.scrollbase.postSeriesAddition,
        avgScrollPointWidth : 75
    }, chartAPI.msarea);

    ////scrollstackedcolumn2d////
    chartAPI('scrollstackedcolumn2d', {
        postSeriesAddition: function (hcObj, fcObj, width, height) {
            // #FCXT-37 fixed
            chartAPI.base.postSeriesAddition.call(this, hcObj, fcObj, width, height);
            chartAPI.scrollbase.postSeriesAddition.call(this, hcObj, fcObj, width, height);
        },
        //postSeriesAddition: chartAPI.scrollbase.postSeriesAddition,
        avgScrollPointWidth : 75
    }, chartAPI.stackedcolumn2d);

    ////scrollcombi2d////
    chartAPI('scrollcombi2d', {
        postSeriesAddition: chartAPI.scrollbase.postSeriesAddition,
        avgScrollPointWidth : 40
    }, chartAPI.mscombi2d);

    ////scrollcombidy2d////
    chartAPI('scrollcombidy2d', {
        postSeriesAddition: chartAPI.scrollbase.postSeriesAddition,
        avgScrollPointWidth : 40
    }, chartAPI.mscombidy2d);


    ////////////// XY charts /////////////

    chartAPI('scatter', {
        standaloneInit: true,
        defaultSeriesType: 'scatter',
        creditLabel: creditLabel
    }, chartAPI.scatterbase);




    /////   Bubble  ////////
    chartAPI('bubble', {
        standaloneInit: true,
        standaloneInut: true,
        defaultSeriesType: 'bubble',
        point: function (chartName, series, dataset, FCChartObj, HCObj, catLength, seriesIndex) {
            if (dataset.data) {
                var itemValueY, index, drawAnchors, dataObj,
                setColor, setAlpha,
                plotFillAlpha,
                showPlotBorder,
                plotBorderColor,
                plotBorderThickness,
                plotBorderAlpha,
                seriesAnchorBorderColor,
                seriesAnchorSymbol,
                hasValidPoint = false,
                seriesAnchorBorderThickness, seriesAnchorBgColor,
                itemValueX, itemValueZ, pointStub,
                chartTypeConf = chartAPI[chartName],
                // Data array in dataset object
                data = dataset.data,
                dataLength = data.length,
                conf = HCObj[FC_CONFIG_STRING],
                // showValues attribute in individual dataset
                datasetShowValues = pluckNumber(dataset.showvalues, conf.showValues),
                bubbleScale = pluckNumber(FCChartObj.bubblescale, 1),
                negativeColor = pluck(FCChartObj.negativecolor, "FF0000"),
                bubblePlotOptions = HCObj.plotOptions.bubble,
                NumberFormatter = conf.numberFormatter,
                //Regratation line
                showRegressionLine = pluckNumber(dataset.showregressionline,
                FCChartObj.showregressionline, 0);

                bubblePlotOptions.bubbleScale = bubbleScale;

                // Dataset seriesname
                series.name = getValidValue(dataset.seriesname);
                // If showInLegend set to false
                // We set series.name blank
                if (pluckNumber(dataset.includeinlegend) === 0 || series.name === undefined) {
                    series.showInLegend = false;
                }

                // Managing line series markers
                // Whether to drow the Anchor or not
                drawAnchors = Boolean(pluckNumber(dataset.drawanchors , dataset.showanchors , FCChartObj.drawanchors, 1));

                // Plot Border Cosmetics
                plotFillAlpha = pluck(dataset.plotfillalpha, dataset.bubblefillalpha, FCChartObj.plotfillalpha, HUNDREDSTRING);
                showPlotBorder = pluckNumber(dataset.showplotborder, FCChartObj.showplotborder, 1);
                plotBorderColor = getFirstColor(pluck(dataset.plotbordercolor, FCChartObj.plotbordercolor, "666666"));
                plotBorderThickness = pluck(dataset.plotborderthickness, FCChartObj.plotborderthickness, 1);
                plotBorderAlpha = pluck(dataset.plotborderalpha, FCChartObj.plotborderalpha, "95");

                // Anchor cosmetics
                // We first look into dataset then chart obj and then default value.
                seriesAnchorSymbol = 'circle';
                seriesAnchorBorderColor = plotBorderColor;
                seriesAnchorBorderThickness = showPlotBorder == 1 ? plotBorderThickness : 0;
                seriesAnchorBgColor = pluck(dataset.color, dataset.plotfillcolor,
                FCChartObj.plotfillcolor, HCObj.colors[seriesIndex % HCObj.colors.length]);

                series.marker = {
                    enabled: drawAnchors,
                    fillColor: this.getPointColor(seriesAnchorBgColor, HUNDREDSTRING),
                    lineColor: {
                        FCcolor: {
                            color: seriesAnchorBorderColor,
                            alpha: plotBorderAlpha
                        }
                    },
                    lineWidth: seriesAnchorBorderThickness,
                    symbol: seriesAnchorSymbol
                };

                if (showRegressionLine) {
                    series.events = {
                        hide : this.hideRLine,
                        show : this.showRLine
                    };
                    //regration object used in XY chart
                    //create here to avoid checking always
                    var regrationObj = {
                        sumX : 0,
                        sumY : 0,
                        sumXY : 0,
                        sumXsqure : 0,
                        sumYsqure : 0,
                        xValues : [],
                        yValues : []
                    }, regSeries,
                    showYOnX = pluckNumber(dataset.showyonx, FCChartObj.showyonx, 1),
                    regressionLineColor = getFirstColor(pluck(dataset.regressionlinecolor,
                    FCChartObj.regressionlinecolor, seriesAnchorBgColor)),
                    regressionLineThickness = pluckNumber(dataset.regressionlinethickness,
                    FCChartObj.regressionlinethickness, 1),
                    regressionLineAlpha = getFirstAlpha(pluckNumber(dataset.regressionlinealpha,
                    FCChartObj.regressionlinealpha, 100)),
                    regLineColor = convertColor(regressionLineColor, regressionLineAlpha);
                }

                // Iterate through all level data
                for (index = 0; index < dataLength; index += 1) {
                    // Individual data obj
                    // for further manipulation
                    dataObj = data[index];
                    if (dataObj) {
                        itemValueY = NumberFormatter.getCleanValue(dataObj.y);
                        itemValueX = NumberFormatter.getCleanValue(dataObj.x);
                        itemValueZ = NumberFormatter.getCleanValue(dataObj.z, true);

                        // If value is null we assign
                        if (itemValueY === null) {
                            series.data.push({
                                y: null,
                                x: itemValueX
                            });
                            continue;
                        }

                        hasValidPoint = true;

                        setColor = getFirstColor(pluck(dataObj.color, (dataObj.z < 0 ? negativeColor : seriesAnchorBgColor)));
                        setAlpha = pluck(dataObj.alpha, plotFillAlpha);

                        // Get the point stubs like disPlayValue, tooltext and link
                        pointStub = chartTypeConf
                        .getPointStub(dataObj, itemValueY, itemValueX, HCObj, dataset, datasetShowValues);

                        setColor = pluckNumber(FCChartObj.use3dlighting) === 0 ?
                            setColor : chartTypeConf.getPointColor(setColor, setAlpha);

                        // storing the absolute value of the z-value
                        // (since this will be used to calculate radius which can't be negative)
                        if (itemValueZ !== null) {
                            // getting the larger vaue
                            bubblePlotOptions.zMax = bubblePlotOptions.zMax > itemValueZ ? bubblePlotOptions.zMax : itemValueZ;
                            bubblePlotOptions.zMin = bubblePlotOptions.zMin < itemValueZ ? bubblePlotOptions.zMin : itemValueZ;
                        }

                        // Finally add the data
                        // we call getPointStub function that manage displayValue, toolText and link
                        series.data.push({
                            y: itemValueY,
                            x: itemValueX,
                            z: itemValueZ,
                            displayValue : pointStub.displayValue,
                            toolText : pointStub.toolText,
                            link: pointStub.link,
                            marker : {
                                enabled: drawAnchors,
                                fillColor: setColor,
                                lineColor: {
                                    FCcolor: {
                                        color: seriesAnchorBorderColor,
                                        alpha: plotBorderAlpha
                                    }
                                },
                                lineWidth: seriesAnchorBorderThickness,
                                symbol: seriesAnchorSymbol
                            }
                        });

                        // Set the maximum and minimum found in data
                        // pointValueWatcher use to calculate the maximum and minimum value of the Axis
                        this.pointValueWatcher(HCObj, itemValueY, itemValueX, showRegressionLine && regrationObj);
                    }
                    else {
                        // add the data
                        series.data.push({
                            y : null
                        });
                    }
                }
                if (showRegressionLine) {
                    regSeries = {
                        type : 'line',
                        color : regLineColor,
                        showInLegend: false,
                        lineWidth : regressionLineThickness,
                        enableMouseTracking : false,
                        marker : {
                            enabled : false
                        },
                        data : this.getRegressionLineSeries(regrationObj, showYOnX, dataLength),
                        zIndex : 0
                    };
                    series = [series, regSeries];
                }
            }

            // If all the values in current dataset is null
            // we will not show its legend
            if (!hasValidPoint) {
                series.showInLegend = false
            }
            return series;
        },

        // Function to create tooltext for individual data points
        getPointStub: function (setObj, value, label, HCObj, dataset, datasetShowValues) {
            var toolText, displayValue, dataLink, HCConfig = HCObj[FC_CONFIG_STRING],
            formatedVal = value === null ? value : HCConfig.numberFormatter.dataLabels(value),
            seriesname, tooltipSepChar = HCConfig.tooltipSepChar;

            //create the tooltext
            if (!HCConfig.showTooltip) {
                toolText = BLANKSTRING;
            }
            // if tooltext is given in data object
            else if (getValidValue(setObj.tooltext) !== undefined) {
                toolText = parseUnsafeString(setObj.tooltext);
            }
            else {//determine the tooltext then
                if (formatedVal === null) {
                    toolText = false;
                } else {
                    if (HCConfig.seriesNameInToolTip) {
                        seriesname = pluck(dataset && dataset.seriesname);
                    }
                    toolText = seriesname ? seriesname + tooltipSepChar : BLANKSTRING;
                    toolText += label ? label + tooltipSepChar : BLANKSTRING;
                    toolText += formatedVal;
                    toolText += setObj.z ? tooltipSepChar + setObj.z : BLANKSTRING;
                }
            }

            //create the displayvalue
            if (!pluckNumber(setObj.showvalue, datasetShowValues, HCConfig.showValues)) {
                displayValue = BLANKSTRING;
            }
            else if (pluck(setObj.name, setObj.label) !== undefined) {
                displayValue = parseUnsafeString(pluck(setObj.name, setObj.label));
            }
            else {//determine the dispalay value then
                displayValue = formatedVal;
            }

            ////create the link
            dataLink = getValidValue(setObj.link);

            return {
                displayValue : displayValue,
                toolText : toolText,
                link: dataLink
            };
        }
    }, chartAPI.scatter);


    ////scrollcombidy2d////
    chartAPI('zoomline', {
        standaloneInit: true,
        hasVDivLine : true,
        defaultSeriesType : 'stepzoom',

        xAxisMinMaxSetter: function (hcObj, fcObj, canvasWidth) {

            this.base.xAxisMinMaxSetter.apply(this, arguments);

            var conf = hcObj[FC_CONFIG_STRING],
            xAxis = hcObj.xAxis,
            xAxisConf = conf.x,
            categories = xAxis.categories;

            xAxis.min = 0;
            xAxis.max = xAxisConf.catCount - 1;

        },
        series : function (FCObj, HCObj, chartName) {
            var index, length, conf = HCObj[FC_CONFIG_STRING],
            series, seriesArr,
            FCChartObj = FCObj.chart, dataFound,
            //Whether data is provided in compact mode
            compactMode = pluckNumber(FCChartObj.compactdatamode, 0);
            //enable the legend
            HCObj.legend.enabled = Boolean(pluckNumber(FCChartObj.showlegend, 1));
            

            if (FCObj.dataset && FCObj.dataset.length > 0) {
                // add category
                if (FCObj.categories && FCObj.categories[0] && FCObj.categories[0].category){
                    this.categoryAdder(FCObj, HCObj);
                }
                //add data series
                for (index = 0, length = FCObj.dataset.length; index < length; index += 1) {
                    if (FCObj.dataset[index].data) {
                        series = {
                            data : []
                        };
                        //add data to the series
                        seriesArr = this.point(chartName, series,
                        FCObj.dataset[index], FCObj.chart, HCObj, conf.oriCatTmp.length,
                        index);
                        //if the returned series is an array of series (case: pareto)
                        if (seriesArr instanceof Array) {
                            HCObj.series = HCObj.series.concat(seriesArr)
                        }
                        //all other case there will be only1 series
                        else {
                            HCObj.series.push(seriesArr);
                        }
                        dataFound = true;
                    }
                }
                if (dataFound) {
                    ///configure the axis
                    this.configureAxis(HCObj, FCObj);
                    ///////////Trend-lines /////////////////
                    //for log it will be done in configureAxis
                    if (FCObj.trendlines && !this.isLog) {
                        createTrendLine (FCObj.trendlines, HCObj.yAxis, conf,
                        false, this.isBar);
                    }
                }

            }
        },
            
        point: function (chartName, series, dataset, FCChartObj, HCObj, catLength, seriesIndex) {

            //Store attributes
            var seriesName, lineColorDef, lineAlphaDef, lineThickness, lineDashed,
            lineDashLen, lineDashGap, includeInLegend, valuePosition, showValues,
            dataLabel,
            drawAnchors, setAnchorSidesDef, setAnchorRadiusDef, setAnchorBorderColorDef,
            setAnchorBorderThicknessDef, setAnchorBgColorDef, setAnchorAlphaDef,
            setAnchorBgAlphaDef,
            setAnchorSides, setAnchorRadius, setAnchorBorderColor, setAnchorBorderThickness,
            setAnchorBgColor, setAnchorAlpha, setAnchorBgAlpha,

            hasValidPoint, dataObj, lineColor, lineAlpha, pointShadow, pointAnchorEnabled,
            compactMode, dataSeparator, displayValue, index,
            data = dataset.data,
            length = data && data.length || 0,

            // HighChart configuration object
            conf = HCObj[FC_CONFIG_STRING],
            // take the series type
            seriesType = pluck(series.type, this.defaultSeriesType),
            // Check the chart is a stacked chart or not
            isStacked = HCObj.plotOptions[seriesType] && HCObj.plotOptions[seriesType].stacking,
            // 100% stacked chart takes absolute values only
            isValueAbs = pluck(this.isValueAbs, conf.isValueAbs, false),
            // showValues attribute in individual dataset
            datasetShowValues = pluckNumber(dataset.showvalues, conf.showValues),
            seriesYAxis = pluckNumber(series.yAxis, 0),
            NumberFormatter = conf.numberFormatter;


            //Whether data is provided in compact mode
            compactMode = pluckNumber(FCChartObj.compactdatamode, 0);
            //If data is provided in compact mode, separator character
            dataSeparator = pluck(FCChartObj.dataseparator, "|");


            seriesName = pluck(dataset.seriesname, BLANKSTRING);
            // Line cosmetics attributes
            // Color of the line series
            lineColorDef = getFirstColor(pluck(dataset.color, FCChartObj.linecolor,
            HCObj.colors[seriesIndex % HCObj.colors.length]));
            // Alpha of the line
            lineAlphaDef = pluck(dataset.alpha, FCChartObj.linealpha, HUNDREDSTRING);
            // Line Thickness
            lineThickness = pluckNumber(dataset.linethickness, FCChartObj.linethickness, 2);
            // Whether to use dashline
            lineDashed = Boolean(pluckNumber(dataset.dashed, FCChartObj.linedashed, 0));
            // line dash attrs
            lineDashLen = pluckNumber(dataset.linedashlen, FCChartObj.linedashlen, 5);
            lineDashGap = pluckNumber(dataset.linedashgap, FCChartObj.linedashgap, 4);

            includeInLegend = pluckNumber(dataset.includeinlegend, 1);
            valuePosition = pluck(dataset.valueposition, FCChartObj.valueposition);
            showValues = pluckNumber(dataset.showvalues, FCChartObj.showvalues);




            //Data set anchors
            drawAnchors = pluckNumber(dataset.drawanchors, dataset.showanchors, FCChartObj.drawanchors, FCChartObj.showanchors);

            // Anchor cosmetics
            // We first look into dataset then chart obj and then default value.
            setAnchorSidesDef = pluckNumber(dataset.anchorsides,
            FCChartObj.anchorsides, 0);
            setAnchorRadiusDef = pluckNumber(dataset.anchorradius,
            FCChartObj.anchorradius, 3);
            setAnchorBorderColorDef = getFirstColor(pluck(dataset.anchorbordercolor,
            FCChartObj.anchorbordercolor, lineColorDef));
            setAnchorBorderThicknessDef = pluckNumber(dataset.anchorborderthickness,
            FCChartObj.anchorborderthickness, 1);
            setAnchorBgColorDef = getFirstColor(pluck(dataset.anchorbgcolor,
            FCChartObj.anchorbgcolor, lineColorDef));
            setAnchorAlphaDef = pluck(dataset.anchoralpha, FCChartObj.anchoralpha,
            HUNDREDSTRING);
            setAnchorBgAlphaDef = pluck(dataset.anchorbgalpha, FCChartObj.anchorbgalpha,
            setAnchorAlphaDef);

            pointAnchorEnabled = drawAnchors === undefined ?
                lineAlphaDef != 0 : !!drawAnchors;

            //set the marker attr at series
            series.marker = {
                enabled: pointAnchorEnabled,
                fillColor: {
                    FCcolor: {
                        color: setAnchorBgColorDef,
                        alpha: ((setAnchorBgAlphaDef * setAnchorAlphaDef) / 100) + BLANKSTRING
                    }
                },
                lineColor: {
                    FCcolor: {
                        color: setAnchorBorderColorDef,
                        alpha: setAnchorAlphaDef + BLANKSTRING
                    }
                },
                lineWidth: setAnchorBorderThicknessDef,
                radius: setAnchorRadiusDef,
                symbol: mapSymbolName(setAnchorSidesDef)
            };

            series.name = seriesName;



            // Set the line color and alpha to
            // HC seris obj with FusionCharts color format using FCcolor obj
            series.color = {
                FCcolor: {
                    color: lineColorDef,
                    alpha: lineAlphaDef
                }
            };

            // Set the line thickness (line width)
            series.lineWidth = lineThickness;
            // Create line dash
            // Using dashStyle of HC
            series.dashStyle = lineDashed ? getDashStyle(lineDashLen, lineDashGap, lineThickness) : undefined;

            // If includeInLegend set to false
            // We set series.name blank
            if (pluckNumber(dataset.includeinlegend) === 0 ||
                series.name === undefined || (lineAlphaDef == 0 &&
                drawAnchors !== 1)) {
                series.showInLegend = false;
            }

            if (data) {
                //Counter
                var setCount = 0;
                //Based on whether data is in compact mode or not, take action
                if (compactMode) {
                    //Split the values on separator
                    var arrValues = data[0].split(dataSeparator);
                    // Used to set alpha of the shadow
                    pointShadow = {
                        opacity: lineAlpha / 100
                    };

                    //Iterate and add to data model.
                    for (index = 0; index < catLength; index += 1) {
                        //Now, get value.
                        var itemValue = NumberFormatter.getCleanValue(arrValues[index]);
                        if (itemValue === null) {
                            // add the data
                            series.data.push({
                                y : null
                            });
                            continue;
                        }
                        hasValidPoint = true;
                        //create the displayvalue
                        dataLabel = NumberFormatter.dataLabels(itemValue);
                        displayValue = showValues ? dataLabel : BLANKSTRING

                        // Finally add the data
                        // we call getPointStub function that manage displayValue, toolText and link
                        series.data.push({
                            y : itemValue,
                            displayValue : displayValue,
                            shadow: pointShadow,
                            toolText : dataLabel
                        });

                        // Set the maximum and minimum found in data
                        // pointValueWatcher use to calculate the maximum and minimum value of the Axis
                        this.pointValueWatcher(HCObj, itemValue, seriesYAxis,
                        isStacked, index, 0, seriesType);
                    }
                } else {
                    for (index = 0; index < catLength; index += 1) {

                        // Individual data obj
                        // for further manipulation
                        dataObj = data[index];
                        if (dataObj) {
                            itemValue = NumberFormatter.getCleanValue(dataObj.value, isValueAbs);

                            if (itemValue === null) {
                                // add the data
                                series.data.push({
                                    y : null
                                });
                                continue;
                            }

                            hasValidPoint = true;

                            // Anchor cosmetics in data points
                            // Getting anchor cosmetics for the data points or its default values
                            setAnchorSides = pluckNumber(dataObj.anchorsides, setAnchorSidesDef);
                            setAnchorRadius = pluckNumber(dataObj.anchorradius, setAnchorRadiusDef);
                            setAnchorBorderColor = getFirstColor(pluck(dataObj.anchorbordercolor, setAnchorBorderColorDef));
                            setAnchorBorderThickness = pluckNumber(dataObj.anchorborderthickness, setAnchorBorderThicknessDef);
                            setAnchorBgColor = getFirstColor(pluck(dataObj.anchorbgcolor, setAnchorBgColorDef));
                            setAnchorAlpha = pluck(dataObj.anchoralpha, setAnchorAlphaDef);
                            setAnchorBgAlpha = pluck(dataObj.anchorbgalpha, setAnchorBgAlphaDef);

                            // Managing line series cosmetics
                            // Color of the line
                            lineColor = getFirstColor(pluck(dataObj.color, lineColorDef));

                            // alpha
                            lineAlpha = pluck(dataObj.alpha, lineAlphaDef);

                            // Used to set alpha of the shadow
                            pointShadow = {
                                opacity: lineAlpha / 100
                            };

                            pointAnchorEnabled = drawAnchors === undefined ?
                                lineAlpha != 0 : !!drawAnchors;

                            //create the displayvalue
                            dataLabel = NumberFormatter.dataLabels(itemValue);
                            displayValue = showValues ? dataLabel : BLANKSTRING

                            // Finally add the data
                            // we call getPointStub function that manage displayValue, toolText and link
                            series.data.push(
                            {
                                displayValue: displayValue,
                                toolText: dataLabel,
                                y : itemValue,
                                shadow: pointShadow,
                                color: {
                                    FCcolor: {
                                        color: lineColor,
                                        alpha: lineAlpha
                                    }
                                }
                            });

                            // Set the maximum and minimum found in data
                            // pointValueWatcher use to calculate the maximum and minimum value of the Axis
                            this.pointValueWatcher(HCObj, itemValue, seriesYAxis,
                            isStacked, index, 0, seriesType);
                        }
                        else {
                            // add the data
                            series.data.push({
                                y : null
                            });
                        }
                    }
                }
            }

            if (!hasValidPoint) {
                series.showInLegend = false
            }

            return series;
        },

        categoryAdder : function(FCObj, HCObj) {
            var index, countCat = 0, fontSize, conf = HCObj[FC_CONFIG_STRING],
            axisGridManager = conf.axisGridManager,
            xAxisObj = HCObj.xAxis, dataLabel, axisConf = conf.x,
            FCChartObj = FCObj.chart, pixelsperpoint,
            HCChartObj = HCObj.chart,
            showLabels = pluckNumber(FCChartObj.showlabels, 1);
            HCChartObj.zoomType = 'x';
            xAxisObj.maxZoom = 2;
            pixelsperpoint = pluckNumber(FCChartObj.pixelsperpoint, 15);
            if (pixelsperpoint <= 0) {
                pixelsperpoint = 15;
            }
            conf.pixelsperpoint = pixelsperpoint;
            if (FCObj.categories && FCObj.categories[0] && FCObj.categories[0].category) {
                //update the font relate attr in HC cat
                if (FCObj.categories[0].font) {
                    HCObj.xAxis.labels.style.fontFamily  = FCObj.categories[0].font;
                }
                if ((fontSize = pluckNumber(FCObj.categories[0].fontsize)) !== undefined) {
                    if (fontSize < 1) {
                        fontSize = 1;
                    }
                    HCObj.xAxis.labels.style.fontSize  = fontSize + PXSTRING;
                    setLineHeight(HCObj.xAxis.labels.style);
                }
                if (FCObj.categories[0].fontcolor) {
                    HCObj.xAxis.labels.style.color  = FCObj.categories[0].fontcolor.
                        split(COMMASTRING)[0].replace(/^\#?/, "#");
                }
                //temp object for cat text in data tooltext
                var oriCatTmp = HCObj[FC_CONFIG_STRING].oriCatTmp,
                categories = FCObj.categories[0],
                category = categories.category;




                var length = category.length,
                //Whether data is provided in compact mode
                compactMode = pluckNumber(FCChartObj.compactdatamode, 0),
                //If data is provided in compact mode, separator character
                dataSeparator = pluck(FCChartObj.dataseparator, "|");

                //Based on whether data is in compact mode or not, take action
                if (compactMode) {
                    //Split the values on separator
                    var categoryArr = category[0].split(dataSeparator);
                    //Iterate and add to data model.
                    for (index = 0; index < categoryArr.length; index += 1) {
                        dataLabel = parseUnsafeString(getFirstValue(categoryArr[index],
                        categoryArr[index].name));
                        //axisGridManager.addXaxisCat(xAxisObj, countCat, countCat, dataLabel);
                        oriCatTmp[countCat] = dataLabel;
                        countCat += 1;
                    }
                } else {
                    for (index = 0; index < category.length; index += 1) {
                        if (!category[index].vline) {
                            dataLabel = (category[index].showlabel === '0') ? BLANKSTRING :
                                parseUnsafeString(getFirstValue(FCObj.categories[0].category[index].label,
                            FCObj.categories[0].category[index].name));
                            //axisGridManager.addXaxisCat(xAxisObj, countCat, countCat, dataLabel);
                            oriCatTmp[countCat] = getFirstValue(parseUnsafeString(
                            FCObj.categories[0].category[index].tooltext), dataLabel);
                            countCat += 1;
                        }
                        else {
                            axisGridManager.addVline(xAxisObj, category[index], countCat);
                        }
                    }
                }
                if (showLabels) {
                    xAxisObj.categories = oriCatTmp;
                }
            }
            var LastIndex = countCat - 1;
            conf.displayStartIndex = pluckNumber(FCChartObj.displaystartindex, 0);
            conf.displayEndIndex = pluckNumber(FCChartObj.displayendindex, LastIndex);
            if (conf.displayStartIndex < 0 || conf.displayStartIndex >= LastIndex) {
                conf.displayStartIndex = 0;
            }
            if (conf.displayEndIndex <= conf.displayStartIndex || conf.displayEndIndex > LastIndex) {
                conf.displayEndIndex = LastIndex;
            }
            axisConf.catCount = countCat;
            HCChartObj.hasScroll = true;
            //set stepZoom attributes
            var stepZoom = HCChartObj.stepZoom = {};
            stepZoom.pixelsperpoint = conf.pixelsperpoint;
            stepZoom.displayStartIndex = conf.displayStartIndex;
            stepZoom.displayEndIndex = conf.displayEndIndex;
            stepZoom.scrollColor = getFirstColor(pluck(FCChartObj.scrollcolor,
            defaultPaletteOptions.altHGridColor[HCObj.chart.paletteIndex]));
            stepZoom.scrollHeight = pluckNumber(FCChartObj.scrollheight, 16);
            stepZoom.scrollPadding = pluckNumber(FCChartObj.scrollpadding,
            HCObj.chart.plotBorderWidth);
            stepZoom.scrollBtnWidth = pluckNumber(FCChartObj.scrollbtnwidth,
            FCChartObj.scrollheight, 16);
            stepZoom.scrollBtnPadding = pluckNumber(FCChartObj.scrollbtnpadding, 0);
            //add the space for scroller
            conf.marginBottomExtraSpace += stepZoom.scrollPadding + stepZoom.scrollHeight
        },

        placeHorizontalAxis :  function (axisObj, axisConf, hcJSON,
        fcJSON, width, maxHeight, minCanWidth) {
            var conf = hcJSON[FC_CONFIG_STRING],

            textObj, plotObj, index, titleText, labelObj,
            lastUsedStyle, minWidth, temp, maxStaggerLines, tempLabelWidth,
            labelTextWidth, labelTextPadding = 4, autoWrapLimit,

            rotation = 0, titleHeightUsed = 0, labelHeight = 10, stepValue = 1,
            labelY = 0, nameLineHeight = 0, catCount = 0, testStr = 'W',

            noWrap = false, isStagger = false, isNone = false,

            isStepped = pluckNumber(fcJSON.chart.labelstep, 0),
            labelDisplay = axisConf.labelDisplay,
            labelPadding = axisConf.horizontalLabelPadding,

            marginBottomExtraSpace = conf.marginBottomExtraSpace,
            availableSpaceLeft = hcJSON.chart.marginLeft,
            availableSpaceRight = hcJSON.chart.marginRight,

            SmartLabel = conf.smartLabel,
            pixelsperpoint = conf.pixelsperpoint,

            catLen = axisConf.catCount,
            slantLabels = axisConf.slantLabels,
            unitWidth = width / (axisObj.max - axisObj.min),

            
            tedendHeight = 0, oppTrendHeight = 0, labelSize = {
                w: 0,
                h: 0
            };

            if (axisObj.labels.style) {
                lastUsedStyle = axisObj.labels.style;
                SmartLabel.setStyle(lastUsedStyle);
                temp = SmartLabel.getOriSize(testStr);
                labelHeight = temp.height;
                minWidth = temp.width + labelTextPadding;
                autoWrapLimit = SmartLabel.getOriSize("WWW").width + labelTextPadding;
            }

            var axisMin, axisMax, labelEdge, leftModify, rightModify, excessWidth, i,
            plotLinesArr, plotBandsArr, gridLinesArr = [], nonGridLinesArr = [],
            reductionFactor, firstDifference = 0, lastDifference = 0, lastGridIndex,
            gridLinesLen, perCatWidth, canvasLeftSpace, canvasRightSpace,
            totalDifference, length, xAxisNamePadding = axisConf.horizontalAxisNamePadding, labelSpace = 0,
            staggerLines = axisConf.staggerLines, bottomSpace = tedendHeight, chartPlotWidth,
            widthToAdd, padWidth;

            if (axisObj.title && axisObj.title.text != BLANKSTRING) {
                lastUsedStyle = axisObj.title.style;
                SmartLabel.setStyle(lastUsedStyle);
                nameLineHeight = SmartLabel.getOriSize(testStr).height;
                //now get the title space
                axisObj.title.rotation = 0;
                titleText = SmartLabel.getSmartText(axisObj.title.text, width, maxHeight);
                titleHeightUsed = titleText.height;
            }

            if (hcJSON.chart.marginLeft != parseInt(fcJSON.chart.chartleftmargin, 10)) {
                leftModify = true;
            }
            if (hcJSON.chart.marginRight != parseInt(fcJSON.chart.chartrightmargin, 10)) {
                rightModify = true;
            }

            // if the chartmargin is to be changed to accomodate the first and last labels
            // then excessWidth is the limit upto which the total chartmargins can be changed.
            excessWidth = width - minCanWidth;

            switch (labelDisplay) {
                case 'none':
                    isNone = true;
                    noWrap = true;
                    break;
                case 'rotate':
                    if (slantLabels) {
                        rotation = 300;
                    } else {
                        rotation = 270;
                    }
                    temp = labelHeight;
                    labelHeight = minWidth;
                    minWidth = temp;
                    noWrap = true;
                    break;
                case 'stagger':
                    noWrap = true;
                    isStagger = true;
                    maxStaggerLines = Math.floor((maxHeight - nameLineHeight) / labelHeight);
                    if (maxStaggerLines < staggerLines) {
                        staggerLines = maxStaggerLines;
                    }
                    break;
                default ://none
            }

            // if the chart is not scatter chart
            i = 0;
            plotLinesArr = axisObj.plotLines;

            // 1. segregate the grid plot lines from the non grid plot lines
            for (length = plotLinesArr.length; i < length; i += 1) {
                plotObj = plotLinesArr[i];
                if (plotObj && plotObj.label && (typeof plotObj.label.text !== STRINGUNDEFINED)) {
                    nonGridLinesArr.push(plotObj);
                }
            }

            plotBandsArr = axisObj.plotBands;

            for (i = 0, length = plotBandsArr.length; i < length; i += 1) {
                plotObj = plotBandsArr[i];
                if (plotObj && plotObj.label && (typeof plotObj.label.text !== STRINGUNDEFINED)) {
                    nonGridLinesArr.push(plotObj);
                }
            }

            plotLinesArr = axisObj.categories || [];

            for (i = 0, length = plotLinesArr.length; i < length; i += 1) {
                gridLinesArr.push({
                    value: i, 
                    label: {
                        text: plotLinesArr[i]
                    }
                });
            }
            //axisObj.labels.enabled = true;

            lastGridIndex = gridLinesArr.length - 1;
            gridLinesLen = gridLinesArr.length;

            if (isStagger) {
                if (staggerLines > gridLinesLen) {
                    staggerLines = gridLinesLen;
                } else if (staggerLines < 2) {
                    staggerLines = 2;
                }
            }

            if (gridLinesLen) {
                if (axisObj.scroll && axisObj.scroll.viewPortMin && axisObj.scroll.viewPortMax) {
                    axisMin = axisObj.scroll.viewPortMin;
                    axisMax = axisObj.scroll.viewPortMax;
                    leftModify = false;
                    rightModify = false;
                } else {
                    axisMin = axisObj.min;
                    axisMax = axisObj.max;
                }
                // 2. calculate width for each label

                chartPlotWidth = (gridLinesArr[lastGridIndex].value - gridLinesArr[0].value) * unitWidth,
                perCatWidth = Math.max(pixelsperpoint, chartPlotWidth / (catLen - 1)),
                canvasLeftSpace = (gridLinesArr[0].value - axisMin) * unitWidth,
                canvasRightSpace = (axisMax - gridLinesArr[lastGridIndex].value) * unitWidth;

                if (labelDisplay === 'auto') {
                    if (perCatWidth < autoWrapLimit) {
                        if (slantLabels) {
                            rotation = 300;
                        } else {
                            rotation = 270;
                        }
                        temp = labelHeight;
                        labelHeight = minWidth;
                        minWidth = temp;
                        noWrap = true;
                    }
                } else if (labelDisplay === 'stagger') {
                    perCatWidth *= staggerLines;
                }

                // 4. calculate width for first label
                tempLabelWidth = (canvasLeftSpace + availableSpaceLeft) * 2;

                // if the distance b/w the first data point and min is greater than the distace between two adjacent data points
                labelObj = plotLinesArr[0].label;
                if (labelObj && labelObj.text) {
                    if (labelObj.style) {
                        SmartLabel.setStyle(labelObj.style);
                    }
                    labelTextWidth = Math.min(perCatWidth, SmartLabel.getOriSize(labelObj.text).width + labelTextPadding);
                    // if the label doesnt fit in the given space
                    if (labelTextWidth > tempLabelWidth) {
                        firstDifference = (labelTextWidth - tempLabelWidth) / 2;
                    }
                }

                // 5. calculate width for the last label
                tempLabelWidth = (canvasRightSpace + availableSpaceRight) * 2;

                // if the distance b/w the first data point and min is greater than the distace between two adjacent data points
                labelObj = plotLinesArr[lastGridIndex].label;
                if (labelObj && labelObj.text) {
                    if (labelObj.style) {
                        SmartLabel.setStyle(labelObj.style);
                    }
                    labelTextWidth = Math.min(perCatWidth, SmartLabel.getOriSize(labelObj.text).width + labelTextPadding);
                    // if the label doesnt fit in the given space
                    if (labelTextWidth > tempLabelWidth) {
                        lastDifference = (labelTextWidth - tempLabelWidth) / 2;
                    }
                }

                // 6. do we need to change chart margin or canvas padding?
                totalDifference = firstDifference + lastDifference;
                if (totalDifference > 0) {
                    if (excessWidth > totalDifference) { // change the chart margins
                        reductionFactor =  (lastDifference * width)/(lastDifference + width);
                        reductionFactor = reductionFactor ? reductionFactor + 4 : 0; // for label padding;
                        hcJSON.chart.marginRight += reductionFactor;
                        width -= reductionFactor;

                        reductionFactor =  (firstDifference * width)/(firstDifference + width);
                        reductionFactor = reductionFactor ? reductionFactor + 4 : 0; // for label padding;
                        hcJSON.chart.marginLeft += reductionFactor;
                        width -= reductionFactor;

                        unitWidth = width / (axisObj.max - axisObj.min);
                    } else { //change the padding
                        if (firstDifference < lastDifference) {
                            // try and remove the greater of the two from chart margin
                            if ((excessWidth >= lastDifference) && rightModify) {
                                reductionFactor =  (lastDifference * width)/(lastDifference + width);
                                reductionFactor = reductionFactor ? reductionFactor + 4 : 0; // for label padding;
                                hcJSON.chart.marginRight += reductionFactor;
                                width -= reductionFactor;
                                unitWidth = width / (axisObj.max - axisObj.min);

                            } else if (leftModify) {
                                reductionFactor =  (firstDifference * width)/(firstDifference + width);
                                reductionFactor = reductionFactor ? reductionFactor + 4 : 0; // for label padding;
                                hcJSON.chart.marginLeft += reductionFactor;
                                width -= reductionFactor;
                                unitWidth = width / (axisObj.max - axisObj.min);
                            }
                        } else {
                            if ((excessWidth >= firstDifference) && leftModify) {
                                reductionFactor =  (firstDifference * width)/(firstDifference + width);
                                reductionFactor = reductionFactor ? reductionFactor + 4 : 0; // for label padding;
                                hcJSON.chart.marginLeft += reductionFactor;
                                width -= reductionFactor;
                                unitWidth = width / (axisObj.max - axisObj.min);

                            } else if (rightModify) {
                                reductionFactor =  (lastDifference * width)/(lastDifference + width);
                                reductionFactor = reductionFactor ? reductionFactor + 4 : 0; // for label padding;
                                hcJSON.chart.marginRight += reductionFactor;
                                width -= reductionFactor;
                                unitWidth = width / (axisObj.max - axisObj.min);
                            }
                        }
                    }
                }

                if (!isStagger && !isNone) {
                    if (!isStepped) {
                        stepValue = Math.ceil(minWidth / perCatWidth);
                        perCatWidth *= stepValue;
                    } else {
                        perCatWidth *= isStepped;
                        perCatWidth = Math.max(perCatWidth, minWidth);
                    }
                }

                // start setting the label dimensions
                for(index = 0; index < gridLinesLen; index += 1) {

                    plotObj = gridLinesArr[index];

                    if (index % stepValue && plotObj.label) {
                        plotObj.label.text = BLANKSTRING;
                        continue;
                    }

                    if (plotObj && plotObj.label && getValidValue(plotObj.label.text) !== undefined) {

                        labelObj = plotObj.label;
                        //if the style not implemented then implement it
                        if (labelObj.style && labelObj.style !== lastUsedStyle) {
                            lastUsedStyle = labelObj.style;
                            SmartLabel.setStyle(lastUsedStyle);
                        }

                        if (!isNone) {
                            if (rotation || isStagger) {
                                textObj = SmartLabel.getOriSize(labelObj.text);
                            }
                            else {//wrap
                                textObj = SmartLabel.getSmartText(labelObj.text, (perCatWidth - labelTextPadding), // 4px is removed for label padding
                                maxHeight, noWrap);
                            }
                            labelSize.w = Math.max(labelSize.w, textObj.width + labelTextPadding);
                            labelSize.h = Math.max(labelSize.h, textObj.height);
                        }
                    }
                }
            }

            for(index = 0, length = nonGridLinesArr.length; index < length; index += 1) {

                plotObj = nonGridLinesArr[index];

                if (plotObj && plotObj.label && getValidValue(plotObj.label.text) !== undefined) {

                    labelObj = plotObj.label;
                    //if the style not implemented then implement it
                    if (labelObj.style && labelObj.style !== lastUsedStyle) {
                        lastUsedStyle = labelObj.style;
                        SmartLabel.setStyle(lastUsedStyle);
                    }

                    textObj = SmartLabel.getOriSize(labelObj.text);
                    if (labelObj.verticalAlign === POSITION_BOTTOM) {
                        tedendHeight= mathMax(tedendHeight, textObj.height);
                    } else {
                        oppTrendHeight = mathMax(oppTrendHeight, textObj.height);
                    }
                }
            }

            if (axisObj.scroll && axisObj.scroll.enabled && !rotation && !isNone) {
                widthToAdd = labelSize.w / 2;
                if (hcJSON.chart.marginLeft < widthToAdd) {
                    padWidth = widthToAdd - hcJSON.chart.marginLeft;
                    if (excessWidth > padWidth) {
                        width -= padWidth;
                        excessWidth -= padWidth;
                        hcJSON.chart.marginLeft += padWidth;
                    }
                }
                if (hcJSON.chart.marginRight < widthToAdd) {
                    padWidth = widthToAdd - hcJSON.chart.marginRight;
                    if (excessWidth > padWidth) {
                        width -= padWidth;
                        excessWidth -= padWidth;
                        hcJSON.chart.marginRight += padWidth;
                    }
                }
            }

            //now calculate the required space height
            if (isNone) {
                labelSpace = labelHeight;
            }
            else if (rotation) {
                labelSpace = labelSize.w;
            } else if (isStagger){
                labelSpace = staggerLines * labelHeight;
            } else {
                labelSpace = labelSize.h;
            }

            if (labelSpace > 0) {
                bottomSpace += labelPadding + labelSpace;
            }

            if (titleHeightUsed > 0) {
                bottomSpace += titleHeightUsed + xAxisNamePadding;
            }

            var difference, totalSpace = oppTrendHeight + bottomSpace + 2; 
            temp = 0;

            /// Reduce the element size if required
            if (totalSpace > maxHeight) {
                difference = totalSpace - maxHeight;
                if (xAxisNamePadding > difference) {
                    xAxisNamePadding -= difference;
                    difference = 0;
                } else {
                    difference -= xAxisNamePadding;
                    xAxisNamePadding = 0;
                    if (labelPadding > difference) {
                        labelPadding -= difference;
                        difference = 0;
                    } else {
                        difference -= labelPadding;
                        labelPadding = 0;
                    }
                }

                // reduce the opposite side text or canvas text
                if (oppTrendHeight > difference) {
                    oppTrendHeight -= difference;
                    difference = 0;
                } else {
                    if (oppTrendHeight > 0) {
                        difference -= oppTrendHeight;
                        oppTrendHeight = 0;
                    }
                    if (difference > 0) {
                        if (tedendHeight > difference) {
                            tedendHeight -= difference;
                            difference = 0;
                        }
                        else {
                            if (tedendHeight > 0) {
                                difference -= tedendHeight;
                                tedendHeight = 0;
                            }
                            if (difference > 0) {
                                if ((temp = titleHeightUsed - nameLineHeight) > difference) {
                                    titleHeightUsed -= difference;
                                    difference = 0
                                }
                                else {
                                    difference -= temp;
                                    titleHeightUsed = nameLineHeight;
                                    if (difference > 0) {
                                        if ((temp = labelSpace - labelHeight) > difference) {
                                            labelSpace -= difference;
                                            difference = 0
                                        }
                                        else {
                                            difference -= temp;
                                            labelSpace = labelHeight;
                                            if (difference > 0) {
                                                difference -= titleHeightUsed + xAxisNamePadding;
                                                titleHeightUsed = 0;
                                                if (difference > 0) {
                                                    difference -= labelSpace
                                                    labelSpace = 0
                                                    if (difference > 0) {
                                                        labelPadding -= difference;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Place the elements
            //add extraspace if any
            labelPadding += marginBottomExtraSpace;

            var labelX = conf.is3d ? - hcJSON.chart.xDepth : 0, trendTextY = labelSpace + labelPadding,
            textAlign, yShipment, perLabelH, perLabelW, xShipment = labelX, adjustedPx = labelHeight * 0.5;
            labelY = labelHeight + labelPadding, length = gridLinesArr.length;
            catCount = 0;

            if (rotation) {
                perLabelH = perCatWidth;
                perLabelW = labelSpace - labelTextPadding;
                axisObj.labels.rotation = rotation;
                axisObj.labels.align = 'right';
                axisObj.labels.y = labelPadding - marginBottomExtraSpace + labelTextPadding;
                axisObj.labels.x = (minWidth / 2);

            }
            else if (isStagger) {
                perLabelH = labelHeight;
                perLabelW = (perCatWidth * staggerLines) - labelTextPadding;
            }
            else {
                perLabelH = labelSpace;
                perLabelW = perCatWidth - labelTextPadding;
                //xShipment += 0;
            }

            for(index = 0; index < length; index += stepValue) {
                plotObj = gridLinesArr[index];
                if (plotObj && plotObj.label && getValidValue(plotObj.label.text) !== undefined) {

                    labelObj = plotObj.label;
                    //if the style not implemented then implement it
                    if (labelObj.style && labelObj.style !== lastUsedStyle) {
                        lastUsedStyle = labelObj.style;
                        SmartLabel.setStyle(lastUsedStyle);
                    }

                    if (!isNone) {
                        textObj = SmartLabel.getSmartText(labelObj.text, perLabelW, perLabelH, noWrap);
                        axisObj.categories[index] = textObj.text;
                    }
                    catCount += 1
                }
            }

            length = nonGridLinesArr.length;
            var aoppTrendHeight = 0, atedendHeight = 0;
            for(index = 0; index < length; index += 1) {
                plotObj = nonGridLinesArr[index].plotObj ? nonGridLinesArr[index].plotObj : nonGridLinesArr[index];
                if (plotObj && plotObj.label && getValidValue(plotObj.label.text) !== undefined) {

                    labelObj = plotObj.label;
                    //if the style not implemented then implement it
                    if (labelObj.style && labelObj.style !== lastUsedStyle) {
                        lastUsedStyle = labelObj.style;
                        SmartLabel.setStyle(lastUsedStyle);
                    }
                    if (labelObj.verticalAlign === POSITION_BOTTOM) {
                        textObj = SmartLabel.getSmartText(labelObj.text, width, tedendHeight, true);
                        atedendHeight = Math.max(atedendHeight, textObj.height);
                        labelObj.text = textObj.text;
                        labelObj.y = trendTextY + SmartLabel.getOriSize(testStr).height;
                        labelObj.x = xShipment;
                    } else {
                        textObj = SmartLabel.getSmartText(labelObj.text, width, oppTrendHeight, true);
                        aoppTrendHeight = Math.max(aoppTrendHeight, textObj.height);
                        labelObj.text = textObj.text;
                        labelObj.y = - ((oppTrendHeight - SmartLabel.getOriSize(testStr).height) + labelPadding + 2); 
                    }
                }
            }

            if (titleHeightUsed > 0) {
                SmartLabel.setStyle(axisObj.title.style);
                //now get the title space
                titleText = SmartLabel.getSmartText(axisObj.title.text, width, titleHeightUsed);
                axisObj.title.text = titleText.text;
                axisObj.title.margin = trendTextY + atedendHeight + xAxisNamePadding;
            }

            bottomSpace = atedendHeight;

            if (labelSpace > 0) {
                conf.horizontalAxisHeight = labelPadding + labelSpace - marginBottomExtraSpace;
                bottomSpace += conf.horizontalAxisHeight;
            }

            if (titleHeightUsed > 0) {
                bottomSpace += titleHeightUsed + xAxisNamePadding;
            }
            //
            hcJSON.chart.marginBottom += bottomSpace;

            if (aoppTrendHeight > 0) {
                hcJSON.chart.marginTop += aoppTrendHeight;
                bottomSpace += aoppTrendHeight;
            }

            return bottomSpace;
        },

        creditLabel : creditLabel,
        defaultPlotShadow: 1
    }, chartAPI.msline);


    /////////////// ssgrid ///////////
    chartAPI('ssgrid', {
        standaloneInit: true,
        defaultSeriesType : 'ssgrid',
        chart : function (container, chartName, obj, width, height, FCObj) {
            //clone FC data so that any modiffication on it will not effect the original
            obj = extend2({}, obj);
            //clone the chart obj from graph or blank object
            obj.chart = obj.chart || obj.graph || {};
            delete obj.graph;

            // POINT FUNCTION

            // Declation of variables to be used
            var dataObj,
            setColor,
            setAlpha,
            textStyle,
            index = 0, itemValue,
            label, dataArr = [],
            FCChartObj = obj.chart,
            data = obj.data,
            length = data && data.length,
            SmartLabelManager = this.smartLabel,
            NumberFormatter = new lib.NumberFormatter(FCChartObj, this.name),
            chartHeight = container.offsetHeight,
            chartWidth = container.offsetWidth,
            textSizeObj,
            GParams = {},
            maxHeight = 0,
            numItems = 0,
            fontSize,
            // palette of the chart
            paletteIndex = (FCChartObj.palette > 0 && FCChartObj.palette < 6 ?
                FCChartObj.palette : pluckNumber(this.paletteIndex, 1)) - 1,
            HCObj = {
                chart: {
                    renderTo: container,
                    ignoreHiddenSeries: false,
                    events: {
                    },
                    spacingTop: 0,
                    spacingRight: 0,
                    spacingBottom: 0,
                    spacingLeft: 0,
                    marginTop: 0,
                    marginRight: 0,
                    marginBottom: 0,
                    marginLeft: 0,
                    borderRadius: 0,
                    borderColor: '#000000',
                    borderWidth: 1,
                    defaultSeriesType: 'ssgrid',
                    style : {
                        fontFamily: pluck(FCChartObj.basefont, 'Verdana'),
                        fontSize:  pluckFontSize(FCChartObj.basefontsize, 20) + PXSTRING,
                        color: pluck(FCChartObj.basefontcolor, defaultPaletteOptions.
                            baseFontColor[paletteIndex]).replace(/^#?([a-f0-9]+)/ig, '#$1')
                    },
                    plotBackgroundColor : COLOR_TRANSPARENT
                },
                labels: {
                    smartLabel: SmartLabelManager
                },
                
                colors: ['AFD8F8', 'F6BD0F', '8BBA00', 'FF8E46', '008E8E',
                    'D64646', '8E468E', '588526', 'B3AA00', '008ED6',
                    '9D080D', 'A186BE', 'CC6600', 'FDC689', 'ABA000',
                    'F26D7D', 'FFF200', '0054A6', 'F7941C', 'CC3300',
                    '006600', '663300', '6DCFF6'],
                credits: {
                    href: 'http://www.fusioncharts.com?BS=FCHSEvalMark',
                    text: 'FusionCharts',
                    enabled: this.creditLabel
                },
                legend: {
                    enabled : false
                },
                series: [],
                subtitle: {
                    text: BLANKSTRING
                },
                title: {
                    text : BLANKSTRING
                },
                tooltip: {
                    enabled : false
                },

                // DO the exporting module
                exporting: {
                    buttons: {
                        exportButton: {},
                        printButton: {
                            enabled: false
                        }
                    }
                }
            },
            // Array of default colors (paletteColors)
            // We use it to specify the individual data point color
            defaultColors = HCObj.colors,
            // Length of the default colors
            defaultColLen = HCObj.colors.length,


            //Total sum of values
            sumOfValues = 0,
            itemsPerPage = 0,
            //Height for each data row
            rowHeight = 0,
            //Maximum width for value column
            maxValWidth = 0,
            //Label width and x position
            maxLabelWidth = 0,
            labelX = 0,
            actualDataLen = 0,
            HCChartObj,
            configureObj = FCObj.jsVars.cfgStore;

            HCChartObj = HCObj.chart;

            setLineHeight(HCObj.chart.style)

            //Now, store all parameters
            //Whether to show percent values?
            GParams.showPercentValues = pluckNumber(configureObj.showpercentvalues, FCChartObj.showpercentvalues, 0);
            //Number of items per page
            GParams.numberItemsPerPage = pluck(configureObj.numberitemsperpage, FCChartObj.numberitemsperpage);
            //Whether to show shadow
            GParams.showShadow = pluckNumber(configureObj.showshadow, FCChartObj.showshadow, 0);
            //Font Properties
            GParams.baseFont = pluck(configureObj.basefont, FCChartObj.basefont, 'Verdana');
            fontSize = pluckFontSize(configureObj.basefontsize, FCChartObj.basefontsize, 10);
            GParams.baseFontSize = fontSize + PXSTRING;
            GParams.baseFontColor = getFirstColor(pluck(configureObj.basefontcolor, FCChartObj.basefontcolor,
            defaultPaletteOptions.baseFontColor[paletteIndex]));

            //Alternate Row Color
            GParams.alternateRowBgColor = getFirstColor(pluck(configureObj.alternaterowbgcolor, FCChartObj.alternaterowbgcolor,
            defaultPaletteOptions.altHGridColor[paletteIndex]));
            GParams.alternateRowBgAlpha = pluck(configureObj.alternaterowbgalpha, FCChartObj.alternaterowbgalpha,
            defaultPaletteOptions.altHGridAlpha[paletteIndex]) + BLANKSTRING;

            //List divider properties
            GParams.listRowDividerThickness = pluckNumber(configureObj.listrowdividerthickness, FCChartObj.listrowdividerthickness, 1);
            GParams.listRowDividerColor = getFirstColor(pluck(configureObj.listrowdividercolor, FCChartObj.listrowdividercolor,
            defaultPaletteOptions.borderColor[paletteIndex]));
            GParams.listRowDividerAlpha = (pluckNumber(configureObj.listrowdivideralpha, FCChartObj.listrowdivideralpha,
            defaultPaletteOptions.altHGridAlpha[paletteIndex]) + 15) + BLANKSTRING;

            //Color box properties
            GParams.colorBoxWidth = pluckNumber(configureObj.colorboxwidth, FCChartObj.colorboxwidth, 8);
            GParams.colorBoxHeight = pluckNumber(configureObj.colorboxheight, FCChartObj.colorboxheight, 8);
            //Navigation Properties
            GParams.navButtonRadius = pluckNumber(configureObj.navbuttonradius, FCChartObj.navbuttonradius, 7);
            GParams.navButtonColor = getFirstColor(pluck(configureObj.navbuttoncolor, FCChartObj.navbuttoncolor,
            defaultPaletteOptions.canvasBorderColor[paletteIndex]));
            GParams.navButtonHoverColor = getFirstColor(pluck(configureObj.navbuttonhovercolor, FCChartObj.navbuttonhovercolor,
            defaultPaletteOptions.altHGridColor[paletteIndex]));

            //Paddings
            GParams.textVerticalPadding = pluckNumber(configureObj.textverticalpadding, FCChartObj.textverticalpadding, 3);
            GParams.navButtonPadding = pluckNumber(configureObj.navbuttonpadding, FCChartObj.navbuttonpadding, 5);
            GParams.colorBoxPadding = pluckNumber(configureObj.colorboxpadding, FCChartObj.colorboxpadding, 10);
            GParams.valueColumnPadding = pluckNumber(configureObj.valuecolumnpadding, FCChartObj.valuecolumnpadding, 10);
            GParams.nameColumnPadding = pluckNumber(configureObj.namecolumnpadding, FCChartObj.namecolumnpadding, 5);

            GParams.borderThickness = pluckNumber(configureObj.borderthickness, FCChartObj.borderthickness, 1);
            GParams.borderColor  = getFirstColor(pluck(configureObj.bordercolor, FCChartObj.bordercolor,
            defaultPaletteOptions.borderColor[paletteIndex]));
            GParams.borderAlpha  = pluck(configureObj.borderalpha, FCChartObj.borderalpha,
            defaultPaletteOptions.borderAlpha[paletteIndex]) + BLANKSTRING;

            GParams.bgColor  = pluck(configureObj.bgcolor, FCChartObj.bgcolor, 'FFFFFF');
            GParams.bgAlpha  = pluck(configureObj.bgalpha, FCChartObj.bgalpha, HUNDREDSTRING);
            GParams.bgRatio = pluck(configureObj.bgratio, FCChartObj.bgratio, HUNDREDSTRING);
            GParams.bgAngle = pluck(configureObj.bgangle, FCChartObj.bgangle, ZEROSTRING);

            // Setting the Chart border cosmetics
            // SSGrid shows a round edge in chart border
            // so we use borderThickness / 16 as a radius
            // to show the round edge
            HCChartObj.borderRadius = GParams.borderThickness / 16;
            HCChartObj.borderWidth = GParams.borderThickness;
            HCChartObj.borderColor = {
                FCcolor: {
                    color: GParams.borderColor,
                    alpha: GParams.borderAlpha
                }
            }

            // Setting the Chart background cosmetics
            HCChartObj.backgroundColor = {
                FCcolor: {
                    color: GParams.bgColor,
                    alpha: GParams.bgAlpha,
                    ratio: GParams.bgRatio,
                    angle: GParams.bgAngle
                }
            }

            // Creating the text style for SSGrid
            textStyle = {
                fontFamily: GParams.baseFont,
                fontSize:  GParams.baseFontSize,
                color: GParams.baseFontColor
            };
            setLineHeight(textStyle)
            // setting the style to LabelManagement
            SmartLabelManager.setStyle(textStyle);

            for(index = 0; index < length; index += 1) {
                dataObj = data[index];
                itemValue = NumberFormatter.getCleanValue(dataObj.value);
                label = parseUnsafeString(getFirstValue(dataObj.label, dataObj.name));
                // Color of the particular data
                setColor = getFirstColor(pluck(dataObj.color, defaultColors[index % defaultColLen]));
                // Alpha of the data
                setAlpha = pluck(dataObj.alpha, FCChartObj.plotfillalpha, HUNDREDSTRING);
                if (label != BLANKSTRING || itemValue != null) {
                    dataArr.push({
                        value: itemValue,
                        label: label,
                        color: setColor
                    });
                    sumOfValues = sumOfValues + itemValue;
                    actualDataLen += 1;
                }
            }

            /*
             * calculates the various points on the chart.
             */
            //Format all the numbers on the chart and store their display values
            //We format and store here itself, so that later, whenever needed,
            //we just access displayValue instead of formatting once again.
            for(index = 0; index < actualDataLen; index += 1) {
                dataObj = dataArr[index];
                itemValue = dataObj.value;
                //Format and store
                dataObj.dataLabel = dataObj.label;
                //Display Value
                dataObj.displayValue = GParams.showPercentValues ?
                    NumberFormatter.percentValue(itemValue / sumOfValues * 100) :

                    NumberFormatter.dataLabels(itemValue);
                //Now, we need to iterate through the value fields to get the max width
                //Simulate
                textSizeObj = SmartLabelManager.getOriSize(dataObj.displayValue);
                //Store maximum width
                maxValWidth = Math.max(maxValWidth, (textSizeObj.width + GParams.valueColumnPadding));
            }


            //Now, there are two different flows from here on w.r.t calculation of height
            //Case 1: If the user has specified his own number of items per page
            if (GParams.numberItemsPerPage) {
                //In this case, we simply divide the page into the segments chosen by user
                //If all items are able to fit in this single page
                if (GParams.numberItemsPerPage >= actualDataLen) {
                    //This height is perfectly alright and we can fit all
                    //items in a single page
                    //Set number items per page to total items.
                    GParams.numberItemsPerPage = actualDataLen;
                    //So, NO need to show the navigation buttons
                    rowHeight = chartHeight / GParams.numberItemsPerPage;
                    //End Index
                    itemsPerPage = actualDataLen;
                }
                else {
                    //We need to allot space for the navigation buttons
                    var cHeight = chartHeight;
                    //Deduct the radius and padding of navigation buttons from height
                    cHeight = cHeight - 2 * (GParams.navButtonPadding + GParams.navButtonRadius);
                    //Now, get the maximum possible number of items that we can fit in each page
                    itemsPerPage = GParams.numberItemsPerPage;
                    //Height for each row
                    rowHeight = cHeight / itemsPerPage;

                }
            } else {
                //Case 2: If we've to calculate best fit. We already have the maximum height
                //required by each row of data.
                //Storage for maximum height
                //Now, get the height required for any single text field
                //We do not consider wrapping.
                //Create text box and get height
                //textSizeObj = SmartLabelManager.getOriSize("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_=/*-+~`");
                //Get the max of two
                maxHeight = parseInt(textStyle.lineHeight, 10);
                //Add text vertical padding (for both top and bottom)
                maxHeight = maxHeight + 2 * GParams.textVerticalPadding;
                //Also compare with color box height - as that's also an integral part
                maxHeight = Math.max(maxHeight, GParams.colorBoxHeight);
                //Now that we have the max possible height, we need to calculate the page length.
                //First check if we can fit all items in a single page
                numItems = chartHeight/maxHeight;
                if (numItems >= actualDataLen) {
                    //We can fit all items in one page
                    rowHeight = (chartHeight / actualDataLen);
                    //Navigation buttons are not required.
                    //End Index
                    itemsPerPage = actualDataLen;
                } else {
                    //We cannot fit all items in same page. So, need to show
                    //navigation buttons. Reserve space for them.
                    //We need to allot space for the navigation buttons
                    cHeight = chartHeight;
                    //Deduct the radius and padding of navigation buttons from height
                    cHeight = cHeight - 2 * (GParams.navButtonPadding + GParams.navButtonRadius);
                    //Now, get the maximum possible number of items that we can fit in each page
                    itemsPerPage = Math.floor(cHeight / maxHeight);
                    //Height for each row
                    rowHeight = cHeight / itemsPerPage;
                }
            }
            //Now, we calculate the maximum avaiable width for data label column
            maxLabelWidth = chartWidth - GParams.colorBoxPadding -
                GParams.colorBoxWidth - GParams.nameColumnPadding -
                maxValWidth - GParams.valueColumnPadding;
            labelX = GParams.colorBoxPadding + GParams.colorBoxWidth + GParams.nameColumnPadding;


            // Storing series configuration options in HC Chart object
            HCChartObj.height = chartHeight;
            HCChartObj.width = chartWidth;
            HCChartObj.rowHeight = rowHeight;

            HCChartObj.labelX = labelX;

            HCChartObj.colorBoxWidth = GParams.colorBoxWidth;
            HCChartObj.colorBoxHeight = GParams.colorBoxHeight;
            HCChartObj.colorBoxX = GParams.colorBoxPadding;

            HCChartObj.valueX = GParams.colorBoxPadding + GParams.colorBoxWidth +
                GParams.nameColumnPadding + maxLabelWidth + GParams.valueColumnPadding;
            HCChartObj.valueColumnPadding = GParams.valueColumnPadding;

            HCChartObj.textStyle = textStyle;


            HCChartObj.listRowDividerAttr = {
                'stroke-width': GParams.listRowDividerThickness,
                stroke: {
                    FCcolor: {
                        color: GParams.listRowDividerColor,
                        alpha: GParams.listRowDividerAlpha
                    }
                }
            };

            HCChartObj.alternateRowColor = {
                FCcolor: {
                    color: GParams.alternateRowBgColor,
                    alpha: GParams.alternateRowBgAlpha
                }
            };

            HCChartObj.navButtonRadius = GParams.navButtonRadius;
            HCChartObj.navButtonPadding = GParams.navButtonPadding;
            HCChartObj.navButtonColor = GParams.navButtonColor;
            HCChartObj.navButtonHoverColor = GParams.navButtonHoverColor;

            HCChartObj.lineHeight = parseInt(textStyle.lineHeight, 10);


            //debugger;
            // Now, we create render array page wise
            var dataRender = [], pageIndex = 0, visible = true;
            for (index = 0; index < actualDataLen & itemsPerPage != 0; index += 1) {
                //Update indexes.
                if (index % itemsPerPage == 0) {
                    dataRender.push({
                        data: [],
                        visible: visible

                    });
                    visible = false;
                    pageIndex += 1;
                }
                dataObj = dataArr[index];

                dataRender[pageIndex - 1].data.push({
                    label: SmartLabelManager.getSmartText(dataObj.dataLabel, maxLabelWidth, rowHeight).text,
                    displayValue: dataObj.displayValue,
                    y: dataObj.value,
                    color: dataObj.color
                });
            }
            HCObj.series = dataRender;


            //////Expprt Module/////
            HCObj.exporting.enabled = FCChartObj.exportenabled == '1' ? true: false;
            HCObj.exporting.buttons.exportButton.enabled = FCChartObj.exportshowmenuitem == '0' ? false : true;
            HCObj.exporting.filename = FCChartObj.exportfilename ? FCChartObj.exportfilename : 'FusionCharts';
            HCObj.exporting.width = chartWidth;

            //call the chart conf function
            return HCObj;
        },
        creditLabel : creditLabel
    }, chartAPI.base);

})();


