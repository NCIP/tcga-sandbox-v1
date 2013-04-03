<?php
//We've included ../Includes/FusionCharts.php, which contains functions
//to help us easily embed the charts.
include("../Includes/FusionCharts.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <TITLE>
	FusionCharts - Export chart and save the exported file to a server-side folder
        </TITLE>
        <link href="../assets/ui/css/style.css" rel="stylesheet" type="text/css" />

        <?php
        //You need to include the following JS file, if you intend to embed the chart using JavaScript.
        ?>
        <SCRIPT LANGUAGE="Javascript" SRC="../../FusionCharts/FusionCharts.js"></SCRIPT>


        <script type="text/javascript">

            // this function exports chart
            function exportChart(exportFormat)
            {
                if ( FusionCharts("myFirst").exportChart )
                {
                    document.getElementById ( "linkToExportedFile" ).innerHTML = "Exporting...";
                    FusionCharts("myFirst").exportChart( { "exportFormat" : exportFormat } );
                }
                else
                {
                    document.getElementById ( "linkToExportedFile" ).innerHTML = "Please wait till the chart completes rendering..." ;

                }

            }

            // This event handler function is called by the chart after the export is completed.
            // The statusCode property when found "1" states that the export is successful
            // You can get the access file name from fileName property
            function FC_Exported ( statusObj )
            {
                if ( statusObj.statusCode == "1" )
                {
                    document.getElementById ( "linkToExportedFile" ).innerHTML = "Export successful. You can view it from <a target='_blank' href='" + statusObj.fileName + "'>here</a>.";
                }
                else
                {
                    // If the export is found unsuccussful get the reason from notice property
                    document.getElementById ( "linkToExportedFile" ).innerHTML = "Export unsuccessful. Notice from export handler : " + statusObj.notice;
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
                    <h2 class="headline">Export example - Export chart and save the exported file to a server-side folder</h2>

                    <div class="gen-chart-render">

                        <CENTER>
                            <?php

                            //Create the chart - Column 3D Chart with data from Data/Data.xml
                            echo renderChart("../../FusionCharts/Column3D.swf", "Data/SaveData.xml", "", "myFirst", 600, 300, false, true);
                            ?>

                            <div id="linkToExportedFile" style="margin-top:10px; padding:5px; width:600px; background:#efefef; border : 1px dashed #cdcdcd; color: 666666;">Exported status.</div>
                            <br/>
                            <input value="Export to JPG" type="button" onClick="JavaScript:exportChart('JPG')" />
                            <input value="Export to PNG" type="button" onClick="JavaScript:exportChart('PNG')" />
                            <input value="Export to PDF" type="button" onClick="JavaScript:exportChart('PDF')" />

                        </CENTER>

                    </div>
                    <div class="clear"></div>
                    <p>&nbsp;</p>
                    <p class="small">Right click on the chart to accee various export options or click any of the buttons below</p>


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
