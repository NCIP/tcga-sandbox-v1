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
	FusionCharts XT - Form Based Data Charting Example
        </title>
        <?php
        //You need to include the following JS file, if you intend to embed the chart using JavaScript.
        //Embedding using JavaScripts avoids the "Click to Activate..." issue in Internet Explorer
        //When you make your own charts, make sure that the path to this JS file is correct. Else, you would get JavaScript errors.
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
                    <h2 class="headline"> ChartTopText </h2>

                    <div class="gen-chart-render">

                        <center>
                            <?php
                            //We first request the data from the form (Default.php)
                            $intSoups = $_REQUEST['Soups'];
                            $intSalads = $_REQUEST['Salads'];
                            $intSandwiches = $_REQUEST['Sandwiches'];
                            $intBeverages = $_REQUEST['Beverages'];
                            $intDesserts = $_REQUEST['Desserts'];
                            //In this example, we're directly showing this data back on chart.
                            //In your apps, you can do the required processing and then show the
                            //relevant data only.

                            //Now that we've the data in variables, we need to convert this into chart data using
                            //FusionCharts PHP Class

                            # Create Pie 3d chart object
                            $FC = new FusionCharts("Pie3D","600","300");

                            # Set Relative Path of swf file.
                            $FC->setSWFPath("../../FusionCharts/");


                            # Define chart attributes
                            $strParam="caption=Sales by Product Category;subCaption=For this week;showPercentValues=1;  showPercentageInLabel=1;pieSliceDepth=25;showBorder=1;decimals=0";

                            # Set chart attributes
                            $FC->setChartParams($strParam);

                            //Add all data
                            $FC->addChartData($intSoups,"name=Soups");
                            $FC->addChartData($intSalads,"name=Salads");
                            $FC->addChartData($intSandwiches,"name=Sandwitches");
                            $FC->addChartData($intBeverages,"name=Beverages");
                            $FC->addChartData($intDesserts,"name=Desserts");

                            //Create the chart
                            $FC->renderChart();
                            ?>
                            <br/>
                            <a href="Default.php" style="margin-left: 360px;" class="qua qua-button"><span>Enter data again</span></a>
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