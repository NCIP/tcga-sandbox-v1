<?php
//We've included ../Includes/FusionCharts_Gen.php, which contains FusionCharts PHP Class
//to help us easily embed the charts.
include("../Includes/FusionCharts_Gen.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>
		FusionCharts - Simple Column 2D Chart - With Multilingual characters
        </title>
        <?php
        //You need to include the following JS file, if you intend to embed the chart using JavaScript.
        //Embedding using JavaScripts avoids the "Click to Activate..." issue in Internet Explorer
        //When you make your own charts, make sure that the path to this JS file is correct. Else, you
        //would get JavaScript errors.
        ?>
        <script type="text/javascript" LANGUAGE="Javascript" SRC="../../FusionCharts/FusionCharts.js"></script>

        <!--[if IE 6]>
        <script type="text/javascript" src="../assets/ui/js/DD_belatedPNG_0.0.8a-min.js"></script>

<script>
          /* select the element name, css selector, background etc */
          DD_belatedPNG.fix('img');

          /* string argument can be any CSS selector */
        </script>
        <![endif]-->

        <link href="../assets/ui/css/style.css" rel="stylesheet" type="text/css" />
        <style type="text/css">
            h2.headline {
                font: normal 110%/137.5% "Trebuchet MS", Arial, Helvetica, sans-serif;
                padding: 0;
                margin: 25px 0 25px 0;
                color: #7d7c8b;
                text-align: center;
            }
            p.small {
                font: normal 68.75%/150% Verdana, Geneva, sans-serif;
                color: #919191;
                padding: 0;
                margin: 0 auto;
                width: 664px;
                text-align: center;
            }
        </style>
    </head>
    <body>
        <div id="wrapper">

            <div id="header">
              <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank"><img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">PHP Class Examples</h1>
</div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">Example using XML having multilingual text</h2>

                    <div class="gen-chart-render">

                        <center>
                            <?php

                            //This page demonstrates the ease of generating charts containing UTF-8 encoded
                            //multilingual text using FusionCharts PHPClass.
                            //For this chart, we've cread a chart  object used FusionCharts PHP Class
                            //supply chart data and configurations to it and render chart using the instance

                            //Here, we've kept this example very simple.

                            # Create column 2d chart object
                            $FC = new FusionCharts("Column2D","500","400");

                            # Set Relative Path of swf file.
                            $FC->setSWFPath("../../FusionCharts/");

                            # Set Chart attributes
                            $FC->setChartParams("caption=Monthly Sales Summary;subcaption=For the year 2008;");
                            $FC->setChartParams("xAxisName=Month;yAxisName=Sales;numberPrefix=$;showNames=1;");
                            $FC->setChartParams("showValues=0;showColumnShadow=1;animation=1;");
                            $FC->setChartParams("baseFontColor=666666;lineColor=FF5904;lineAlpha=85;");
                            $FC->setChartParams("valuePadding=10;labelDisplay=rotate;useRoundEdges=1");

                            #add chart data values and category names
                            $FC->addChartData("17400","Label=januári");
                            $FC->addChartData("19800","Label=Fevruários");
                            $FC->addChartData("21800","Label=مارس");
                            $FC->addChartData("23800","Label=أبريل");
                            $FC->addChartData("29600","Label=五月");
                            $FC->addChartData("27600","Label=六月");
                            $FC->addChartData("31800","Label=תִּשׁרִי");
                            $FC->addChartData("39700","Label=Marešwān");
                            $FC->addChartData("37800","Label=settèmbre");
                            $FC->addChartData("21900","Label=ottàgono");
                            $FC->addChartData("32900","Label=novèmbre");
                            $FC->addChartData("39800","Label=décembre");

                            # apply style
                            $FC->defineStyle("myCaptionFont","Font","size=12");
                            $FC->applyStyle("DATALABELS","myCaptionFont");

                            # Render  Chart
                            $FC->renderChart();

                            ?>
                        </center>

                    </div>
                    <div class="clear"></div>
                    <p>&nbsp;</p>
                    <p class="small">  </p>

                    <div class="underline-dull"></div>
                </div>
            </div>

            <div id="footer">
                <ul>
                    <li><a href="../index.html"><span>&laquo; Back to list of examples</span></a></li>
                    <li class="pipe">|</li>
                    <li><a href="../NoChart.html"><span>Unable to see the chart above?</span></a></li>
                </ul>
            </div>
        </div>
    </body>
</html>