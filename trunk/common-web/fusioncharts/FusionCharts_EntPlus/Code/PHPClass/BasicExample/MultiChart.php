<?php
//We've included ../Includes/FusionCharts_Gen.php, which contains FusionCharts PHP Class
//to help us easily embed the charts.
include("../Includes/FusionCharts_Gen.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <TITLE>
	FusionCharts XT - Multiple Charts on one Page
        </TITLE>
        <?php
        //You need to include the following JS file, if you intend to embed the chart using JavaScript.
        //Embedding using JavaScripts avoids the "Click to Activate..." issue in Internet Explorer
        //When you make your own charts, make sure that the path to this JS file is correct. Else, you would get JavaScript errors.
        ?>
        <SCRIPT LANGUAGE="Javascript" SRC="../../FusionCharts/FusionCharts.js"></SCRIPT>

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
        <?php
        //You need to include the following JS file, if you intend to embed the chart using JavaScript.
        //Embedding using JavaScripts avoids the "Click to Activate..." issue in Internet Explorer
        //When you make your own charts, make sure that the path to this JS file is correct. Else, you
        //would get JavaScript errors.
        ?>
    </head>
    <BODY>

        <div id="wrapper">

            <div id="header">
              <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank"><img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">PHP Class Examples</h1>
</div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline"> Multiple Charts on the same page </h2>

                    <div class="gen-chart-render">

                        <CENTER>
                            <?php

                            //This page demonstrates how you can show multiple charts on the same page.
                            //For this example, We have created 2 instances of the FusionCharts PHP Class
                            //supplied data to both and rendered them


                            //---------- Creating First Chart ----------------------------------------------
                            # Create FusionCharts PHP object
                            $FC = new FusionCharts("Column3D","300","250");
                            # set the relative path of the swf file
                            $FC->setSWFPath("../../FusionCharts/");

                            # Define chart attributes#  Set chart attributes
                            $strParam="caption=Weekly Sales;subcaption=Revenue;xAxisName=Week;yAxisName=Revenue;numberPrefix=$";
                            # Setting chart attributes
                            $FC->setChartParams($strParam);

                            # add chart values and category names for the First Chart
                            $FC->addChartData("40800","Label=Week 1");
                            $FC->addChartData("31400","Label=Week 2");
                            $FC->addChartData("26700","Label=Week 3");
                            $FC->addChartData("54400","Label=Week 4");
                            //-------------------------------------------------------------------

                            //----- Creating Second Chart ---------------------------------------
                            # Create FusionCharts PHP object
                            $FC2 = new FusionCharts("Column3D","300","250");
                            # set the relative path of the swf file
                            $FC2->setSWFPath("../../FusionCharts/");

                            # Define chart attributes
                            $strParam="caption=Weekly Sales;subcaption=Quantity;xAxisName=Week;yAxisName=Quantity";
                            # Setting chart attributes
                            $FC2->setChartParams($strParam);

                            # add chart values and  category names for the second chart
                            $FC2->addChartData("32","Label=Week 1");
                            $FC2->addChartData("35","Label=Week 2");
                            $FC2->addChartData("26","Label=Week 3");
                            $FC2->addChartData("44","Label=Week 4");

                            $FC2->setOffChartCaching(true);
                            //-------------------------------------------------------------------------
                            # Render First Chart
                            $FC->renderChart();

                            # Render Second Chart
                            $FC2->renderChart();

                            ?>

                        </CENTER>

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
    </BODY>
</HTML>
