<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ Language=VBScript %>
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts - Array Example using Stacked Column 3D Chart</title>
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
</HEAD>
	<%
	'We've included ../Includes/FusionCharts.asp, which contains functions
	'to help us easily embed the charts.
	%>
<!-- #INCLUDE FILE="../Includes/FusionCharts.asp" -->
    <BODY>

        <div id="wrapper">

            <div id="header">
              

               <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank"><img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">ASP Basic Examples</h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">Plotting Stacked Chart from data contained in Array.</h2>

                    <div class="gen-chart-render">
<%
	'In this example, we plot a Stacked chart from data contained
	'in an array. The array will have three columns - first one for Quarter Name
	'and the next two for data values. The first data value column would store sales information
	'for Product A and the second one for Product B.
		
	Dim arrData(4,3)
	'Store Name of Products
	arrData(0,1) = "Quarter 1"
	arrData(1,1) = "Quarter 2"
	arrData(2,1) = "Quarter 3"
	arrData(3,1) = "Quarter 4"
	'Sales data for Product A
	arrData(0,2) = 567500
	arrData(1,2) = 815300
	arrData(2,2) = 556800
	arrData(3,2) = 734500
	'Sales data for Product B
	arrData(0,3) = 547300
	arrData(1,3) = 594500
	arrData(2,3) = 754000
	arrData(3,3) = 456300	

	'Now, we need to convert this data into multi-series XML. 
	'We convert using string concatenation.
	'strXML - Stores the entire XML
	'strCategories - Stores XML for the <categories> and child <category> elements
	'strDataProdA - Stores XML for current year's sales
	'strDataProdB - Stores XML for previous year's sales
	Dim strXML, strCategories, strDataProdA, strDataProdB, i
	
	'Initialize <chart> element
	strXML = "<chart caption='Sales' numberPrefix='$' formatNumberScale='0'>"
	
	'Initialize <categories> element - necessary to generate a stacked chart
	strCategories = "<categories>"
	
	'Initiate <dataset> elements
	strDataProdA = "<dataset seriesName='Product A'>"
	strDataProdB = "<dataset seriesName='Product B'>"
	
	'Iterate through the data	
	For i=0 to UBound(arrData)-1
		'Append <category name='...' /> to strCategories
		strCategories = strCategories & "<category name='" & arrData(i,1) & "' />"
		'Add <set value='...' /> to both the datasets
		strDataProdA = strDataProdA & "<set value='" & arrData(i,2) & "' />"
		strDataProdB = strDataProdB & "<set value='" & arrData(i,3) & "' />"		
	Next
	
	'Close <categories> element
	strCategories = strCategories & "</categories>"
	
	'Close <dataset> elements
	strDataProdA = strDataProdA & "</dataset>"
	strDataProdB = strDataProdB & "</dataset>"
	
	'Assemble the entire XML now
	strXML = strXML & strCategories & strDataProdA & strDataProdB & "</chart>"
	
	'Create the chart - Stacked Column 3D Chart with data contained in strXML
	Call renderChart("../../FusionCharts/StackedColumn3D.swf", "", strXML, "productSales", 500, 300, false, false)
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
    </BODY>
</HTML>