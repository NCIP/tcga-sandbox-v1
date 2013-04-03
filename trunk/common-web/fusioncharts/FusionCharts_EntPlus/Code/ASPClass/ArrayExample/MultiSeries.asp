<%@ Language=VBScript %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts XT - ASP Class Array Example using Multi Series Column 3D Chart</title>
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
                    <p class="text" align="center">Plotting multi-series chart from data contained in Array</p>
					<p>&nbsp;</p>
                    <div class="gen-chart-render">
<%

	
	'In this example, using FusionCharts ASP Class we plot 
	'we plot a mulitseries chart from data contained in arrays
	
	' The arrays need to be of the following  structure :
	
	'	1. Array to store Category Namesnames :
	'	
	'	  A single dimensional array storing the category names
	'	
	'	2. A 2 Dimensional Array to store data values 

	'		** Each row will store data for 1 dataset
	'	 
	'	 Column 1 will store : Dataset Series Name.
	'	 Column 2 will store : Dataset attributes 
	'	  		(as list separated by delimiter.)
	'	 Column 3 and rest will store : values of the dataset
		  
	''
	
	'Let's store the sales data for 6 products in our array. We also store the name of products. 
	
	'Store Name of Products
	dim arrCatNames(6)
	arrCatNames(0) = "Product A"
	arrCatNames(1) = "Product B"
	arrCatNames(2) = "Product C"
	arrCatNames(3) = "Product D"
	arrCatNames(4) = "Product E"
	arrCatNames(5) = "Product F"
	'Store sales data for current year
	dim arrData(2,8)
	arrData(0,0) = "Current Year"
	arrData(0,1) = "" ' Dataset Parameters
	arrData(0,2) = 567500
	arrData(0,3) = 815300
	arrData(0,4) = 556800
	arrData(0,5) = 734500
	arrData(0,6) = 676800
	arrData(0,7) = 648500
	'Store sales data for previous year
	arrData(1,0) = "Previous Year"
	arrData(1,1) = "" ' Dataset Parameter
	arrData(1,2) = 547300
	arrData(1,3) = 584500
	arrData(1,4) = 754000
	arrData(1,5) = 456300
	arrData(1,6) = 754500
	arrData(1,7) = 437600

	' Create FusionCharts ASP Class object for multiseies column3d chart
 	dim FC
	' Create First FusionCharts ASP class object
	set FC = new FusionCharts
	' Set chart type to Multi Series Column 3D
	call FC.setChartType("MSColumn3D")
	' Set chart size
	call FC.setSize("600","300")
	' Set chart ID
	call FC.setID("FC1")
	
	' Set Relative Path of swf file. 
 	call FC.setSWFPath("../../FusionCharts/")
		
	dim strParam
	' Define chart attributes
	strParam="caption=Sales by Product;numberPrefix=$;rotateValues=1"

 	' Set chart attributes
 	call FC.setChartParams(strParam)
	
	' Pass the 2 arrays storing data and category names to 
	' FusionCharts ASP Class function addChartDataFromArray
	call FC.addChartDataFromArray(arrData, arrCatNames)
	
	 '  Render Second Chart with JS Embedded Method
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