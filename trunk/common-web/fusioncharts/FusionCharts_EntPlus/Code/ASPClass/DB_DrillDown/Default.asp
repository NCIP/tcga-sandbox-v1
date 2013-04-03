<%@LANGUAGE="VBSCRIPT"%>
<% option explicit %>
<%
'We've included ../Includes/FusionCharts_Gen.asp, which contains
'FusionCharts ASP Class to help us easily embed charts 
'We've also used ../Includes/DBConn.asp to easily connect to a database
%>
<!--#include file="../Includes/DBConn.asp"-->
<!--#include file="../Includes/FusionCharts_Gen.asp"-->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts XT - ASP Class Database and Drilldown Example using Single Series Column 3D Chart</title>
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

    <body>

        <div id="wrapper">

            <div id="header">
                

               <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank"><img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">ASP Class Examples</h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <p class="text" align="center">The Pie chart below has been rendered usind ASP Class, getting data from databse. <br>
The chart shows summarized data of three factories.
Click on any pie slice to render a column 2D chart with detailed data for that Factory.</p>
					<p>&nbsp;</p>
                    <div class="gen-chart-render">
<%
    'In this example, we show how to connect FusionCharts to a database.
    'For the sake of ease, we've used an MySQL databases containing two
    'tables.

	dim FC
	' Create FusionCharts ASP class object
	set FC = new FusionCharts
	' Set chart type to Pie 3D
	Call FC.setChartType("Pie3D")
	' Set chart size 
	Call FC.setSize("650","450")
	
	' Set Relative Path of swf file.
 	Call FC.setSWFPath("../../FusionCharts/")
	
	dim strParam
	' Define chart attributes
   	strParam="caption=Factory Output report;subCaption=By Quantity;pieSliceDepth=30;showBorder=1; showLabels=1;numberSuffix= Units"

 	' Set chart attributes
 	Call FC.setChartParams(strParam)
	
  	' Fetch all factory records creating SQL query
	dim strQuery
	strQuery = "select a.FactoryID, b.FactoryName, sum(a.Quantity) as total from Factory_output a, Factory_Master b where a.FactoryId=b.FactoryId group by a.FactoryId,b.FactoryName"
	
	Dim oRs
	'Create the recordset to retrieve data
    Set oRs = Server.CreateObject("ADODB.Recordset")
	Set oRs = oConn.Execute(strQuery)
    
	'Pass the SQL query result and Drill-Down link format to ASP Class Function
	' this function will automatically add chart data from database
	'
	 'The last parameter passed i.e. "Detailed.asp?FactoryId=##FactoryID##"
	 'drill down link from the current chart
	 'Here, the link redirects to another ASP file Detailed.asp 
	 'with a query string variable -FactoryId
	 'whose value would be taken from the Query result created above.
	 'Any thing placed between ## and ## will be regarded 
	 'as a field/column name in the SQL query result.
	 'value from that column will be assingned as the query variable's value
	 'Hence, for each dataplot in the chart the resultant query variable's value
	 'will be different
	'
	if Not oRs.Bof then
		Call FC.addDataFromDatabase(oRs, "total", "FactoryName","","Detailed.asp?FactoryId=##FactoryID##")
	End If

    oRs.Close
	set oRs=Nothing
    
    'Create the chart 
 	Call FC.renderChart(false)
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