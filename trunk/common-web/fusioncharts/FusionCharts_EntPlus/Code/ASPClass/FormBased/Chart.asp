<%@ Language=VBScript %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts XT - Using Form based data to create chart</title>
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
                    <p class="text" align="center">The Restaurant Sales data submitted through form has been plotted as chart below</p>
					<p>&nbsp;</p>
                    <div class="gen-chart-render">
<%
	'We first request the data from the form (Default.asp)
	dim intSoups, intSalads, intSandwiches,	intBeverages, intDesserts
	intSoups = Request("Soups")
	intSalads = Request("Salads")
	intSandwiches = Request("Sandwiches")
	intBeverages = Request("Beverages")
	intDesserts = Request("Desserts")
	
	'In this example, we're directly showing this data back on chart.
	'In your apps, you can do the required processing and then show the 
	'relevant data only.
	
	'Now that we've the data in variables, we need to convert this into chart data using
	'FusionCharts ASP Class
		
 	dim FC
	' Create FusionCharts ASP class object
	set FC = new FusionCharts
	' Set chart type to pie 3D
	Call FC.setChartType("Pie3D")
	' Set chart size 
	Call FC.setSize("600","300")
	
	' Set Relative Path of swf file. 
 	Call FC.setSWFPath("../../FusionCharts/")
	
	dim strParam
	' Define chart attributes
	strParam="caption=Sales by Product Category;subCaption=For this week;showPercentValues=1;  showPercentageInLabel=1;pieSliceDepth=25;showBorder=1;showLabels=1"

 	' Set chart attributes
 	Call FC.setChartParams(strParam)
	
	' Add chart data from form Field
	Call FC.addChartData(intSoups,"Label=Soups","")
	Call FC.addChartData(intSalads,"Label=Salads","")
	Call FC.addChartData(intSandwiches,"Label=Sandwitches","")
	Call FC.addChartData(intBeverages,"Label=Beverages","")
	Call FC.addChartData(intDesserts,"Label=Desserts","")
	
	'Create the chart 
 	Call FC.renderChart(false)
%><BR/>
                        <a class="qua qua-button" style="margin-left: 360px" href="Default.asp"><span>Enter data again</span></a>

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