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
                    <p class="text" align="center">Showcasing Print Manager features. <br />
Please wait for a few seconds after the chart renders to make the Print Manager ready to print. <br />
When the chart becomes ready for printing, the disabled button below will get enabled.</p>
					<p>&nbsp;</p>
                    <div class="gen-chart-render">
	
<%
	'This page demonstrates the ease of generating charts using FusionCharts ASPClass.
	'For this chart, we've cread a chart  object used FusionCharts ASP Class
	'supply chart data and configurations to it and render chart using the instance
	
	'Here, we've kept this example very simple.
	
	dim FC
	' Create FusionCharts ASP class object
 	set FC = new FusionCharts
	
	' Enable Print Manager
	FC.enablePrintManager(True)
	
	' Set chart type to Column 3D
	call FC.setChartType("Column3D")
	' Set chart size 
	call FC.setSize("600","300")
	
	' Set Relative Path of swf file.
 	Call FC.setSWFPath("../../FusionCharts/")
		
	dim strParam
	' Define chart attributes
	strParam="caption=Monthly Unit Sales;xAxisName=Month;yAxisName=Units"

 	' Set Chart attributes
 	Call FC.setChartParams(strParam)
	
	' Add chart data values and category names
	Call FC.addChartData("462","label=Jan","")
	Call FC.addChartData("857","label=Feb","")
	Call FC.addChartData("671","label=Mar","")
	Call FC.addChartData("494","label=Apr","")
	Call FC.addChartData("761","label=May","")
	Call FC.addChartData("960","label=Jun","")
	Call FC.addChartData("629","label=Jul","")
	Call FC.addChartData("622","label=Aug","")
	Call FC.addChartData("376","label=Sep","")
	Call FC.addChartData("494","label=Oct","")
	Call FC.addChartData("761","label=Nov","")
	Call FC.addChartData("960","label=Dec","")
	
	' Render Chart with JS Embedded Method
 	Call FC.renderChart(false)

%>
	<script type="text/javascript">// <!--
		FusionCharts.addEventListener (
			FusionChartsEvents.PrintReadyStateChange ,
			function (identifier, parameter) {
				if(parameter.ready) {
					alert("Chart is now ready for printing.");
					document.getElementById('printButton').disabled = false;
					document.getElementById('printButton').value = "Print Now";
				}
			});
			// -->
	</script>

</div>
					 <br/>
                    
                    <input style="margin-left: 397px" type="button" onclick="FusionCharts.printManager.managedPrint()" value="Please wait..." disabled="disabled" id="printButton" />

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