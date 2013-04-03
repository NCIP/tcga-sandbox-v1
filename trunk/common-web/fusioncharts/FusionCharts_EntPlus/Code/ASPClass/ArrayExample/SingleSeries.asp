<%@ Language=VBScript %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts XT - ASP Class Array Example using Single Series Column 3D Chart</title>
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
		<SCRIPT LANGUAGE="Javascript" SRC="../../FusionCharts/FusionCharts.js"></SCRIPT>
</head>
	<%
	'We've included ../Includes/FusionCharts.asp, which contains functions
	'to help us easily embed the charts.
	%>
<!--#include file="../Includes/FusionCharts_Gen.asp"-->
    <body>

        <div id="wrapper">

            <div id="header">
                

               <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank"><img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">ASP Class Examples</h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <p class="text" align="center">Plotting single series chart from data contained in Array</p>
					<p>&nbsp;</p>
                    <div class="gen-chart-render">
<%
	'In this example, using FusionCharts ASP Class we plot a single series chart 
	'from data contained in an array. 
	
	'The array needs to have two columns - first one for data labels/names
	'and the next one for data values.
	
	'Let's store the sales data for 6 products in our array). We also store
	'the name of products. 
	'Store Name of Products
	dim arrData(6,2)
	arrData(0, 0) = "Product A"
	arrData(1, 0) = "Product B"
	arrData(2, 0) = "Product C"
	arrData(3, 0) = "Product D"
	arrData(4, 0) = "Product E"
	arrData(5, 0) = "Product F"
	'Store sales data
	arrData(0, 1) = 567500
	arrData(1, 1) = 815300
	arrData(2, 1) = 556800
	arrData(3, 1) = 734500
	arrData(4, 1) = 676800
	arrData(5, 1) = 648500

	dim FC
	' Create First FusionCharts ASP class object
	set FC = new FusionCharts
	' Set chart type to Column 3D
	call FC.setChartType("Column3D")
	' Set chart size
	call FC.setSize("600","300")

	' Set Relative Path of swf file.
 	call FC.setSWFPath("../../FusionCharts/")

	dim strParam
	' Define chart attributes
	strParam="caption=Sales by Product;numberPrefix=$"

 	'  Set chart attributes
 	call FC.setChartParams(strParam)
	
	' call FusionCharts ASP Class Function to add data from the array 
	call FC.addChartDataFromArray(arrData , "")
	
	' Render the chart
 	call FC.renderChart(false)
%>
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
    </body>
</html>