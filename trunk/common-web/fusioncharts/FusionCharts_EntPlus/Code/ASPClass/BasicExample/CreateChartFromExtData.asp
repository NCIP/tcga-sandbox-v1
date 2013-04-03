<%@ Language=VBScript %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts XT - ASP Class Array Example - Simple Column 3D Chart (with XML data hard-coded in ASP page itself)</title>
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
                    <p class="text" align="center">Simple Column 3D Chart (with XML data hard-coded in ASP page itself)</p>
					<p>&nbsp;</p>
                    <div class="gen-chart-render">

<%
	
	'This page demonstrates the ease of generating charts using FusionCharts ASP Class.
	'For this chart, we've used a string variable to contain our entire XML data.
	
	'Ideally, you would generate XML data documents at run-time, after interfacing with
	'forms or databases etc.Such examples are also present.
	'Here, we've kept this example very simple.
	
	'Create an XML data document in a string variable
	dim strXML
	strXML = "<chart caption='Monthly Unit Sales' xAxisName='Month' yAxisName='Units' >"
	strXML = strXML &  "<set label='Jan' value='462' />"
	strXML = strXML &  "<set label='Feb' value='857' />"
	strXML = strXML &  "<set label='Mar' value='671' />"
	strXML = strXML &  "<set label='Apr' value='494' />"
	strXML = strXML &  "<set label='May' value='761' />"
	strXML = strXML &  "<set label='Jun' value='960' />"
	strXML = strXML &  "<set label='Jul' value='629' />"
	strXML = strXML &  "<set label='Aug' value='622' />"
	strXML = strXML &  "<set label='Sep' value='376' />"
	strXML = strXML &  "<set label='Oct' value='494' />"
	strXML = strXML &  "<set label='Nov' value='761' />"
	strXML = strXML &  "<set label='Dec' value='960' />"
	strXML = strXML &  "</chart>"

	
	dim FC
	' Create First FusionCharts ASP class object
	set FC = new FusionCharts
	' Set chart type to Column3D
	call FC.setChartType("Column3D")
	' Set chart size
	call FC.setSize("600","300")
	
	' Set Relative Path of swf file. default path is charts/
 	call FC.setSWFPath("../../FusionCharts/")
	' Create the chart - Column 3D Chart with data from strXML 
	' Create the Chart 
 	call FC.renderChartFromExtXML(strXML,false)
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