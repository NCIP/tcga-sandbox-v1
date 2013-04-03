<%@LANGUAGE="VBSCRIPT"%>
<% option explicit %><%
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
                    <p class="text" align="center">The Column chart below has been rendered usind ASP Class, getting data from databse.<br>
The chart shows detailed data of a factories.</p>
					<p>&nbsp;</p>
                    <div class="gen-chart-render">
<%
    'This page is invoked from Default.asp. When the user clicks on a pie
    'slice in Default.asp, the factory Id is passed to this page. We need
    'to get that factory id, get information from database and then show
    'a detailed chart.

    'Request the factory Id from Querystring
    dim FactoryId
	FactoryId = Request("FactoryId")
	
	dim FC
	' Create FusionCharts ASP class object
 	set FC = new FusionCharts
	' Set chart type to Column 2D
	Call FC.setChartType("Column2D")
	' Set chart size 
	Call FC.setSize("600","300")

	' Set Relative Path of swf file.
 	Call FC.setSWFPath("../../FusionCharts/")
	
	dim strParam
	' Define chart attributes
	strParam="caption=Factory " & FactoryId & " Output;subcaption=(In Units);xAxisName=Date;rotateLabels=1;slantLabels=1"

 	' Set chart attributes
 	Call FC.setChartParams(strParam)
	   

    'Now, we get the data for that factory 
	'storing chart values in 'Quantity' column and category names in 'DDate'
	dim strQuery
    strQuery = "select Quantity, format(DatePro,'dd/MM/yyyy') as DDate from Factory_Output where FactoryId=" & FactoryId
    
	'For SQL Server 2000 Query
	'strQuery =  "select Quantity, convert(varchar,DatePro,103) as DDate from Factory_Output where FactoryId=" & FactoryId
	
	Dim oRs
	'Create the recordset to retrieve data
    Set oRs = Server.CreateObject("ADODB.Recordset")
	Set oRs = oConn.Execute(strQuery)
	    
    'Pass the SQL query result to the FusionCharts ASP Class' function 
    'that will extract data from database and add to the chart.
    if not oRs.bof then
       Call FC.addDataFromDatabase(oRs, "Quantity", "DDate","","")
    end if

    oRs.Close
	set oRs=Nothing
    'Create the chart
 	Call FC.renderChart(false)
%>
</div><BR/>
                        <a class="qua qua-button" style="margin-left: 360px" href="Default.asp"><span>Back to Summarized Chart </span></a>

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