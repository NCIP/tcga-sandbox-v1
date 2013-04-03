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
                    <h2 class="headline">Click on any pie slice to see detailed data</h2>

                    <div class="gen-chart-render">
<%
	'In this example, we show how to connect FusionCharts to a database.
	'For the sake of ease, we've used an Access database which is present in
	'../DB/FactoryDB.mdb. It just contains two tables, which are linked to each
	'other. 
		
	'Database Objects - Initialization
	Dim oRs, oRs2, strQuery
	'strXML will be used to store the entire XML document generated
	Dim strXML
	
	'We also keep a flag to specify whether we've to animate the chart or not.
	'If the user is viewing the detailed chart and comes back to this page, he shouldn't
	'see the animation again.
	Dim animateChart
	animateChart = Request.QueryString("animate")
	'Set default value of 1
	if animateChart="" then
		animateChart = "1"
	end if
	
	'Create the recordset to retrieve data
	Set oRs = Server.CreateObject("ADODB.Recordset")

	'Generate the chart element
	strXML = "<chart caption='Factory Output report' subCaption='By Quantity' pieSliceDepth='30' showBorder='1' formatNumberScale='0' numberSuffix=' Units' animation=' " & animateChart & "'>"
	
	'Iterate through each factory
	strQuery = "select * from Factory_Master"
	Set oRs = oConn.Execute(strQuery)
	
	While Not oRs.Eof
		'Now create second recordset to get details for this factory
		Set oRs2 = Server.CreateObject("ADODB.Recordset")
		strQuery = "select sum(Quantity) as TotOutput from Factory_Output where FactoryId=" & ors("FactoryId")
		Set oRs2 = oConn.Execute(strQuery)				
		'Generate <set label='..' value='..' link='..' />
		'Note that we're setting link as Detailed.asp?FactoryId=<<FactoryId>>
		strXML = strXML & "<set label='" & ors("FactoryName") & "' value='" & ors2("TotOutput") & "' link='" & Server.URLEncode("Detailed.asp?FactoryId=" & ors("FactoryId")) & "'/>"
		'Close recordset
		Set oRs2 = Nothing
		oRs.MoveNext
	Wend
	'Finally, close <chart> element
	strXML = strXML & "</chart>"
	Set oRs = nothing
	
	'Create the chart - Pie 3D Chart with data from strXML
	Call renderChart("../../FusionCharts/Pie3D.swf", "", strXML, "FactorySum", 600, 300, false, false)	
%>
                    </div>
                    <div class="clear"></div>
                    <p>&nbsp;</p>
                    <p class="small">Or, right click on any pie to enable slicing or rotation mode. </p>

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