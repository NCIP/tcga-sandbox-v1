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
	FusionCharts XT - Simple Column 3D Chart (with XML data hard-coded in PHP page itself)
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
                    <h2 class="headline"> Simple Column 3D Chart (with XML data hard-coded in PHP page itself) </h2>

                    <div class="gen-chart-render">

                        <CENTER>
                            <?php

                            //This page demonstrates the ease of generating charts using FusionCharts PHP Class.
                            //For this chart, we've used a string variable to contain our entire XML data.

                            //Ideally, you would generate XML data documents at run-time, after interfacing with
                            //forms or databases etc.Such examples are also present.
                            //Here, we've kept this example very simple.

                            //Create an XML data document in a string variable
                            $strXML = "<chart caption='Monthly Unit Sales' xAxisName='Month' yAxisName='Units' >";
                            $strXML .= "<set Label='Jan' value='462' />";
                            $strXML .= "<set Label='Feb' value='857' />";
                            $strXML .= "<set Label='Mar' value='671' />";
                            $strXML .= "<set Label='Apr' value='494' />";
                            $strXML .= "<set Label='May' value='761' />";
                            $strXML .= "<set Label='Jun' value='960' />";
                            $strXML .= "<set Label='Jul' value='629' />";
                            $strXML .= "<set Label='Aug' value='622' />";
                            $strXML .= "<set Label='Sep' value='376' />";
                            $strXML .= "<set Label='Oct' value='494' />";
                            $strXML .= "<set Label='Nov' value='761' />";
                            $strXML .= "<set Label='Dec' value='960' />";
                            $strXML .=  "</chart>";


                            # Create object of FusionCharts class of single series
                            $FC = new FusionCharts("Column3D","600","300");

                            # Set Relative Path of swf file. default path is �charts/�
                            $FC->setSWFPath("../../FusionCharts/");
                            //Create the chart - Column 3D Chart with data from strXML
                            # Create the Chart
                            $FC->renderChartFromExtXML($strXML);
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
