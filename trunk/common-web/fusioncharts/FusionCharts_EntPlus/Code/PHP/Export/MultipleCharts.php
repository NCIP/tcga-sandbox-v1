<?php
//We've included ../Includes/FusionCharts.php, which contains functions
//to help us easily embed the charts.
include("../Includes/FusionCharts.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts - Export example - Export Multiple Charts on one Page</title>
        <link href="../assets/ui/css/style.css" rel="stylesheet" type="text/css" />
        <?php
        //You need to include the following JS file, if you intend to embed the chart using JavaScript.
        //Embedding using JavaScripts avoids the "Click to Activate..." issue in Internet Explorer
        //When you make your own charts, make sure that the path to this JS file is correct. Else, you would get JavaScript errors.
        ?>
        <SCRIPT LANGUAGE="Javascript" SRC="../../FusionCharts/FusionCharts.js"></SCRIPT>

        <script type="text/javascript">

            var initiateExport = false;

            function exportCharts(exportFormat)
            {
                initiateExport = true;
                for ( var chartRef in FusionCharts.items )
                {
                    //alert(chartRef);
                    if ( FusionCharts.items[chartRef].exportChart )
                    {
                        document.getElementById ( "linkToExportedFile" ).innerHTML = "Exporting...";
                        FusionCharts.items[chartRef].exportChart( { "exportFormat" : exportFormat } );
                    }
                    else
                    {

                        document.getElementById ( "linkToExportedFile" ).innerHTML = "Please wait till the chart completes rendering..." ;
                    }
                }

            }

            function FC_Exported ( statusObj )
            {
                if (initiateExport)
                {
                    initiateExport = false;
                    document.getElementById ( "linkToExportedFile" ).innerHTML = "";
                }

                if ( statusObj.statusCode == "1" )
                {
                    document.getElementById ( "linkToExportedFile" ).innerHTML += "Export successful. View it from <a target='_blank' href='" + statusObj.fileName + "'>here</a>.<br/>";
                }
                else
                {
                    document.getElementById ( "linkToExportedFile" ).innerHTML += "Export unsuccessful. Notice from export handler : " + statusObj.notice + "<br/>" ;

                }
            }


        </script>

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

    </head>
    <BODY>

        <div id="wrapper">

            <div id="header">
                

               <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank"><img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">FusionCharts Examples</h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">Export example - Export Multiple Charts on one Page</h2>

                    <div class="gen-chart-render">
                        <center>
                            <?php

                            //This page demonstrates how you can show multiple charts on the same page.
                            //For this example, all the charts use the pre-built Data.xml (contained in /Data/ folder)
                            //However, you can very easily change the data source for any chart.

                            //IMPORTANT NOTE: Each chart necessarily needs to have a unique ID on the page.
                            //If you do not provide a unique Id, only the last chart might be visible.
                            //Here, we've used the ID chart1, chart2 and chart3 for the 3 charts on page.

                            //Create the chart - Column 3D Chart with data from Data/Data.xml
                            echo renderChart("../../FusionCharts/Column3D.swf", "Data/SaveData.xml", "", "chart1", 600, 300, false, true);
                            ?>
                            <BR><BR>
                            <?php
                            //Now, create a Column 2D Chart
                            echo renderChart("../../FusionCharts/Column2D.swf", "Data/SaveData.xml", "", "chart2", 600, 300, false, true);
                            ?>
                            <BR><BR>
                            <?php
                            //Now, create a Line 2D Chart
                            echo renderChart("../../FusionCharts/Line.swf", "Data/SaveData.xml", "", "chart3", 600, 300, false, true);
                            ?>


                            <div id="linkToExportedFile" style="margin-top:10px; padding:5px; width:600px; background:#efefef; border : 1px dashed #cdcdcd; color: 666666;">Exported status.</div>
                            <br/>

                            <input value="Export to JPG" type="button" onClick="JavaScript:exportCharts('JPG')" />
                            <input value="Export to PNG" type="button" onClick="JavaScript:exportCharts('PNG')" />
                            <input value="Export to PDF" type="button" onClick="JavaScript:exportCharts('PDF')" />
                        </center>
                    </div>
                    <div class="clear"></div>
                    <p>&nbsp;</p>
                    <p class="small">Click any one of the buttons below to export all the charts present in the page. </p>


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
