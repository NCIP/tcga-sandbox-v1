<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ Language=VBScript %>
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts - Form Based Data Charting Example</title>
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

<CENTER>
        <div id="wrapper">

            <div id="header">
                

               <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank"><img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">ASP Basic Examples</h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">FusionCharts Form-Based Data Example</h2>

                    <div class="gen-chart-render">

                        <h4>Restaurant Sales Chart below</h4>
<%
	
	'We first request the data from the form (Default.asp)
	Dim intSoups, intSalads, intSandwiches, intBeverages, intDesserts
	intSoups = Int(Request.Form("Soups"))
	intSalads = Int(Request.Form("Salads"))
	intSandwiches = Int(Request.Form("Sandwiches"))
	intBeverages = Int(Request.Form("Beverages"))
	intDesserts   = Int(Request.Form("Desserts"))
	
	'In this example, we're directly showing this data back on chart.
	'In your apps, you can do the required processing and then show the 
	'relevant data only.
	
	'Now that we've the data in variables, we need to convert this into XML.
	'The simplest method to convert data into XML is using string concatenation.	
	Dim strXML
	'Initialize <chart> element
	strXML = "<chart caption='Sales by Product Category' subCaption='For this week' showPercentValues='1' pieSliceDepth='30' showBorder='1'>"
	'Add all data
	strXML = strXML & "<set label='Soups' value='" & intSoups & "' />"
	strXML = strXML & "<set label='Salads' value='" & intSalads & "' />"
	strXML = strXML & "<set label='Sandwiches' value='" & intSandwiches & "' />"
	strXML = strXML & "<set label='Beverages' value='" & intBeverages & "' />"
	strXML = strXML & "<set label='Desserts' value='" & intDesserts & "' />"
	'Close <chart> element
	strXML = strXML & "</chart>"
	
	'Create the chart - Pie 3D Chart with data from strXML
	Call renderChart("../../FusionCharts/Pie3D.swf", "", strXML, "Sales", 500, 300, false, false)
%>
                    </div>
                    <div class="clear"></div>
                    <p>&nbsp;</p>
                    <p class="small"> Click on any pie slice to see slicing effect. Or, right click on chart and choose "Enable Rotation", and then drag and rotate the chart.<!--<p class="small">This dashboard was created using FusionCharts XT, FusionWidgets v3 and FusionMaps v3 You are free to reproduce and distribute this dashboard in its original form, without changing any content, whatsoever. <br />
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