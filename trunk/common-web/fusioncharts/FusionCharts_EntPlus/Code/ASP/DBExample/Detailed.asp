<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ Language=VBScript %>
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts - Database and Drill-Down Example</title>
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
	<!-- #INCLUDE FILE="../Includes/DBConn.asp" -->
<BODY>

        <div id="wrapper">

            <div id="header">
               

               <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank"><img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">FusionCharts Database and Drill-Down Example</h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">Detailed report for the factory</h2>

                    <div class="gen-chart-render">
<%
	'This page is invoked from Default.asp. When the user clicks on a pie
	'slice in Default.asp, the factory Id is passed to this page. We need
	'to get that factory id, get information from database and then show
	'a detailed chart.
	
	'First, get the factory Id
	Dim FactoryId
	'Request the factory Id from Querystring
	FactoryId = Request.QueryString("FactoryId")
	
	Dim oRs, strQuery
	'strXML will be used to store the entire XML document generated
	Dim strXML, intCounter	
	intCounter = 0
	
	Set oRs = Server.CreateObject("ADODB.Recordset")
	'Generate the chart element string
	strXML = "<chart palette='2' caption='Factory " & FactoryId &" Output ' subcaption='(In Units)' xAxisName='Date' showValues='1' labelStep='2' >"
	'Now, we get the data for that factory
	strQuery = "select * from Factory_Output where FactoryId=" & FactoryId
	Set oRs = oConn.Execute(strQuery)
	While Not oRs.Eof		
		'Here, we convert date into a more readable form for set label.
		strXML = strXML & "<set label='" & datePart("d",ors("DatePro")) & "/" & datePart("m",ors("DatePro")) & "' value='" & ors("Quantity") & "'/>"		
		Set oRs2 = Nothing
		oRs.MoveNext
	Wend
	'Close <chart> element
	strXML = strXML & "</chart>"
	Set oRs = nothing
	
	'Create the chart - Column 2D Chart with data from strXML
	Call renderChart("../../FusionCharts/Column2D.swf", "", strXML, "FactoryDetailed", 600, 300, false, false)
%>
                        <BR/>
                        <a class="qua qua-button" style="margin-left: 360px" href="Default.asp?animate=0"><span>Back to Summary</span></a>


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