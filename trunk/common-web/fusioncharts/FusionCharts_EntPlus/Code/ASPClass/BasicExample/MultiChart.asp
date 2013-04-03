<%@ Language=VBScript %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts XT - ASP Class Array Example - Multiple Charts on one Page</title>
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
                    <p class="text" align="center">Plotting multiple Charts on one Page</p>
					<p>&nbsp;</p>
                    <div class="gen-chart-render">
<%
	
	'This page demonstrates how you can show multiple charts on the same page.
	'For this example, We have created 2 instances of the FusionCharts ASP Class
	'supplied data to both and rendered them
	

	'---------- Creating First Chart ----------------------------------------------
	 
	 dim FC
	 ' Create FusionCharts ASP class object
	 set FC = new FusionCharts
	 ' Set chart type to Column 3D
	 call FC.setChartType("Column3D")
	 ' Set chart size 
	 call FC.setSize("300","250")
		 
	 
	 ' set the relative path of the swf file
	 Call FC.setSWFPath("../../FusionCharts/")
	 
	 ' Set chart attributes
	 dim strParam
	 ' Define chart attributes
	 strParam="caption=Weekly Sales;subcaption=Revenue;xAxisName=Week;yAxisName=Revenue;numberPrefix=$"
	 ' Set Chart attributes
	 Call FC.setChartParams(strParam)
	 
	 ' add chart values and category names for the First Chart
	 Call FC.addChartData("40800","name=Week 1","")
	 Call FC.addChartData("31400","name=Week 2","")
	 Call FC.addChartData("26700","name=Week 3","")
	 Call FC.addChartData("54400","name=Week 4","")
	''------------------------------------------------------------------- 
	
	''----- Creating Second Chart ---------------------------------------
	 
	 dim FC2
	 ' Create another FusionCharts ASP class object
	 set FC2 = new FusionCharts
	 ' Set chart type to Column 3D
	 call FC2.setChartType("Column3D")
	 ' Set chart size 
	 call FC2.setSize("300","250")
	 	  
	 ' set the relative path of the swf file
	 Call FC2.setSWFPath("../../FusionCharts/")
	 
	 ' Define chart attributes
	 strParam="caption=Weekly Sales;subcaption=Quantity;xAxisName=Week;yAxisName=Quantity"
	
	 ' Set Chart attributes
	 Call FC2.setChartParams(strParam)
	 
	 ' Add chart values and  category names for the second chart
	 Call FC2.addChartData("32","name=Week 1","")
	 Call FC2.addChartData("35","name=Week 2","")
	 Call FC2.addChartData("26","name=Week 3","")
	 Call FC2.addChartData("44","name=Week 4","")
	 
	 ' Set Chart Caching Off for to Fix Caching error in FireFox
	 Call FC2.setOffChartCaching(true)  
	 ''------------------------------------------------------------------------- 
	 ' Render First Chart
	 Call FC.renderChart(false)
	
	 ' Render Second Chart
	 Call FC2.renderChart(false)
 
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