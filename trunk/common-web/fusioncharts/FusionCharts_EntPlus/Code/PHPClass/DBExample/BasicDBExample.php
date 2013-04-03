<?php
//We've included ../Includes/FusionCharts_Gen.php, which contains
//FusionCharts PHP Class to help us easily embed charts
//We've also used ../Includes/DBConn.php to easily connect to a database.
include("../Includes/FusionCharts_Gen.php");
include("../Includes/DBConn.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <TITLE>
	FusionCharts XT - Database Example
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

                    <div class="gen-chart-render">

                        <CENTER>

                            <?php
                            //In this example, we show how to connect FusionCharts to a database.
                            //For the sake of ease, we've used an MySQL databases containing two
                            //tables.
                            
                            // Connect to the Database
                            $link = connectToDB();
                            
                            # Create pie 3d chart object using FusionCharts PHP Class
                            $FC = new FusionCharts("Pie3D","650","450");
                            
                            # Set Relative Path of swf file.
                            $FC->setSWFPath("../../FusionCharts/");
                            
                            # Define chart attributes
                            $strParam="caption=Factory Output report;subCaption=By Quantity;pieSliceDepth=30; showBorder=1;numberSuffix= Units";
                            
                            #  Set chart attributes
                            $FC->setChartParams($strParam);
                            
                            
                            // Fetch all factory records usins SQL Query
                            //Store chart data values in 'total' column/field and category names in 'FactoryName'
                            $strQuery = "select a.FactoryID, b.FactoryName, sum(a.Quantity) as total from Factory_Output a, Factory_Master b where a.FactoryId=b.FactoryId group by a.FactoryId,b.FactoryName";
                            $result = mysql_query($strQuery) or die(mysql_error());
                            
                            //Pass the SQL Query result to the FusionCharts PHP Class function
                            //along with field/column names that are storing chart values and corresponding category names
                            //to set chart data from database
                            if ($result) {
                                $FC->addDataFromDatabase($result, "total", "FactoryName");
                            }
                            mysql_close($link);
                            
                            # Render the chart
                            $FC->renderChart();
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
