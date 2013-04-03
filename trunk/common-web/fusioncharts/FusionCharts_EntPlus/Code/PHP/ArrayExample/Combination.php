<?php
//We've included ../Includes/FusionCharts.php, which contains functions
//to help us easily embed the charts.
include("../Includes/FusionCharts.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts - Array Example using Combination Column 3D Line Chart</title>
        <link href="../assets/ui/css/style.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="../assets/ui/js/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="../assets/ui/js/lib.js"></script>
        <!--[if IE 6]>
        <script type="text/javascript" src="../assets/ui/js/DD_belatedPNG_0.0.8a-min.js"></script>

<script>
          /* select the element name, css selector, background etc */
          DD_belatedPNG.fix('img');

          /* string argument can be any CSS selector */
        </script>
        <![endif]-->

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
        <SCRIPT LANGUAGE="Javascript" SRC="../../FusionCharts/FusionCharts.js"></SCRIPT>

    </head>
    <BODY>

        <div id="wrapper">

            <div id="header">
                

               <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank"><img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">PHP Basic Examples</h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">Plotting Combination chart from data contained in Array.</h2>

                    <div class="gen-chart-render">

                        <?php
                        //In this example, we plot a Combination chart from data contained
                        //in an array. The array will have three columns - first one for Quarter Name
                        //second one for sales figure and third one for quantity.

                        //Store Quarter Name
                        $arrData[0][1] = "Quarter 1";
                        $arrData[1][1] = "Quarter 2";
                        $arrData[2][1] = "Quarter 3";
                        $arrData[3][1] = "Quarter 4";
                        //Store revenue data
                        $arrData[0][2] = 576000;
                        $arrData[1][2] = 448000;
                        $arrData[2][2] = 956000;
                        $arrData[3][2] = 734000;
                        //Store Quantity
                        $arrData[0][3] = 576;
                        $arrData[1][3] = 448;
                        $arrData[2][3] = 956;
                        $arrData[3][3] = 734;

                        //Now, we need to convert this data into combination XML.
                        //We convert using string concatenation.
                        // $strXML - Stores the entire XML
                        // $strCategories - Stores XML for the <categories> and child <category> elements
                        // $strDataRev - Stores XML for current year's sales
                        // $strDataQty - Stores XML for previous year's sales

                        //Initialize <chart> element
                        $strXML = "<chart palette='4' caption='Product A - Sales Details' PYAxisName='Revenue' SYAxisName='Quantity (in Units)' numberPrefix='$' formatNumberScale='0' showValues='0' decimals='0' >";

                        //Initialize <categories> element - necessary to generate a multi-series chart
                        $strCategories = "<categories>";

                        //Initiate <dataset> elements
                        $strDataRev = "<dataset seriesName='Revenue'>";
                        $strDataQty = "<dataset seriesName='Quantity' parentYAxis='S'>";

                        //Iterate through the data
                        foreach ($arrData as $arSubData) {
                            //Append <category name='...' /> to strCategories
                            $strCategories .= "<category name='" . $arSubData[1] . "' />";
                            //Add <set value='...' /> to both the datasets
                            $strDataRev .= "<set value='" . $arSubData[2] . "' />";
                            $strDataQty .= "<set value='" . $arSubData[3] . "' />";
                        }

                        //Close <categories> element
                        $strCategories .= "</categories>";

                        //Close <dataset> elements
                        $strDataRev .= "</dataset>";
                        $strDataQty .= "</dataset>";

                        //Assemble the entire XML now
                        $strXML .= $strCategories . $strDataRev . $strDataQty . "</chart>";

                        //Create the chart - MS Column 3D Line Combination Chart with data contained in strXML
                        echo renderChart("../../FusionCharts/MSColumn3DLineDY.swf", "", $strXML, "productSales", 600, 300, false, false);
                        ?>
                        
                    </div>
                    <div class="clear"></div>
                    <p>&nbsp;</p>
                    <p class="small"> <!--<p class="small">This dashboard was created using FusionCharts XT, FusionWidgets v3 and FusionMaps v3 You are free to reproduce and distribute this dashboard in its original form, without changing any content, whatsoever. <br />
            &copy; All Rights Reserved</p>
          <p>&nbsp;</p>-->
                    </p>

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
