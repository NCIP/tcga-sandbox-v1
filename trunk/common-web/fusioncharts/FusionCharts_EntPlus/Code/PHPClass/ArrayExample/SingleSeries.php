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
	FusionCharts XT - Array Example using Single Series Column 3D Chart
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
                    <h2 class="headline">Plotting single series chart from data contained in Array</h2>

                    <div class="gen-chart-render">

                        <CENTER>
                            <?php
                            //In this example, using FusionCharts PHP Class we plot a single series chart
                            //from data contained in an array.

                            //The array needs to have two columns - first one for data labels/names
                            //and the next one for data values.

                            //Let's store the sales data for 6 products in our array). We also store
                            //the name of products.
                            //Store Name of Products
                            $arrData[0][0] = "Product A";
                            $arrData[1][0] = "Product B";
                            $arrData[2][0] = "Product C";
                            $arrData[3][0] = "Product D";
                            $arrData[4][0] = "Product E";
                            $arrData[5][0] = "Product F";
                            //Store sales data
                            $arrData[0][1] = 567500;
                            $arrData[1][1] = 815300;
                            $arrData[2][1] = 556800;
                            $arrData[3][1] = 734500;
                            $arrData[4][1] = 676800;
                            $arrData[5][1] = 648500;

                            # Create FusionCharts PHP Class object for single series column3d chart
                            $FC = new FusionCharts("Column3D","600","300");

                            # Set Relative Path of swf file.
                            $FC->setSWFPath("../../FusionCharts/");

                            # Define chart attributes
                            $strParam="caption=Sales by Product;numberPrefix=$";

                            #  Set chart attributes
                            $FC->setChartParams($strParam);


                            ## call FusionCharts PHP Class Function to add data from the array
                            $FC->addChartDataFromArray($arrData);

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