<?php
//We've included ../Includes/FusionCharts.php and ../Includes/DBConn.php, which contains
//functions to help us easily embed the charts and connect to a database.
include("../Includes/FusionCharts.php");
include("../Includes/DBConn.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts - Multiseries chart using data from database</title>
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
                <h1 class="logo-text">FusionCharts Multiseries chart using data from database</h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">Output of various factories</h2>

                    <div class="gen-chart-render">
                        <?php
                        //In this example, we show how to connect FusionCharts to a database.
                        //For the sake of ease, we've used an MySQL databases containing two
                        //tables.

                        // Connect to the DB
                        $link = connectToDB();


                        // SQL query for category labels
                        $strQueryCategories = "select distinct DATE_FORMAT(Factory_Output.DatePro,'%c-%d-%Y') as DatePro from Factory_Output order by DatePro";

                        // Query database
                        $resultCategories = mysql_query($strQueryCategories) or die(mysql_error());

                        // SQL query for factory output data
                        $strQueryData = "select Factory_Master.FactoryName, DATE_FORMAT(Factory_Output.DatePro,'%c-%d-%Y') as DatePro, Factory_Output.Quantity from Factory_Master Factory_Master, Factory_Output Factory_Output where Factory_Output.FactoryID = Factory_Master.FactoryId order by Factory_Output.FactoryID, Factory_Output.DatePro";

                        // Query database
                        $resultData = mysql_query($strQueryData) or die(mysql_error());

                        //We also keep a flag to specify whether we've to animate the chart or not.
                        //If the user is viewing the detailed chart and comes back to this page, he shouldn't
                        //see the animation again.
                        $animateChart = @$_GET['animate'];
                        //Set default value of 1
                        if ($animateChart=="")
                            $animateChart = "1";

                        //$strXML will be used to store the entire XML document generated
                        //Generate the chart element
                        $strXML = "<chart legendPostion='' caption='Factory Output report' subCaption='By Quantity' xAxisName='Factory' yAxisName='Units' showValues='0' formatNumberScale='0' rotateValues='1' animation=' " . $animateChart . "'>";

                        // Build category XML
                        $strXML .= buildCategories ($resultCategories, "DatePro");

                        // Build datasets XML
                        $strXML .= buildDatasets ( $resultData, "Quantity", "FactoryName");

                        //Finally, close <chart> element
                        $strXML .= "</chart>";


                        //Create the chart - Pie 3D Chart with data from strXML
                        echo renderChart("../../FusionCharts/MSLine.swf", "", $strXML, "FactorySum", 700, 400, false, false);


                        // Free database resource
                        mysql_free_result($resultCategories);
                        mysql_free_result($resultData);
                        mysql_close($link);


                        /***********************************************************************************************
	 * Function to build XML for categories
	 * @param	$result 			Database resource
	 * @param 	$labelField 	Field name as String that contains value for chart category labels
	 *
	 *	@return categories XML node
                         */
                        function buildCategories ( $result, $labelField ) {
                            $strXML = "";
                            if ($result) {
                                $strXML = "<categories>";
                                while($ors = mysql_fetch_array($result)) {
                                    $strXML .= "<category label='" . $ors[$labelField]. "'/>";
                                }
                                $strXML .= "</categories>";
                            }
                            return $strXML;
                        }

                        /***********************************************************************************************
	 * Function to build XML for datesets that would contain chart data
	 * @param	$result 			Database resource. The data should come ordered by a control break
	 									field which would require to identify datasets and set its value to
										dataset's series name
	 * @param 	$valueField 	Field name as String that contains value for chart dataplots
	 * @param 	$controlBreak 	Field name as String that contains value for chart dataplots
	 *
	 *	@return 						Dataset XML node
                         */
                        function buildDatasets ($result, $valueField, $controlBreak ) {
                            $strXML = "";
                            if ($result) {

                                $controlBreakValue ="";

                                while( $ors = mysql_fetch_array($result) ) {

                                    if( $controlBreakValue != $ors[$controlBreak] ) {
                                        $controlBreakValue =  $ors[$controlBreak];
                                        $strXML .= ( $strXML =="" ? "" : "</dataset>") . ( "<dataset seriesName='" . $controlBreakValue . "'>" ) ;
                                    }
                                    $strXML .= "<set value='" . $ors[$valueField] . "'/>";

                                }
                                $strXML .= "</dataset>";
                            }
                            return $strXML;

                        }

                        ?>
                    </div>
                    <div class="clear"></div>
                    <p>&nbsp;</p>
                    <p class='small'>This is very simple implementation using a simple database. Complexity of real implementataion can vary as per database structure.</p>

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
