<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
'We've included ../Includes/FusionCharts.asp, which contains functions
'to help us easily embed the charts.
%>
	<!-- #INCLUDE FILE="../Includes/FusionCharts.asp" -->
	<!-- #INCLUDE FILE="../Includes/DBConn.asp" -->
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts - Linked charts</title>
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
        <%
        'You need to include the following JS file, if you intend to embed the chart using JavaScript.
        'Embedding using JavaScripts avoids the "Click to Activate..." issue in Internet Explorer
        'When you make your own charts, make sure that the path to this JS file is correct. Else, you
        'would get JavaScript errors.
        %>
        <SCRIPT LANGUAGE="Javascript" SRC="../../FusionCharts/FusionCharts.js"></SCRIPT>

    </head>
    <BODY>

        <div id="wrapper">

            <div id="header">
                

               <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank"><img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">FusionCharts Database and Linked charts</h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">Click on any pie slice to open linked chart</h2>

                    <div class="gen-chart-render">

                        <%
                        'In this example, we show how to connect FusionCharts to a database.
                        'For the sake of ease, we've used an MySQL databases containing two
                        'tables.
						Dim result, oRs2, strQuery
						Dim strXML
						'Create the recordset to retrieve data
						Set result = Server.CreateObject("ADODB.Recordset")

                        'strXML will be used to store the entire XML document generated
                        'Generate the chart element
                        strXML = "<chart caption='Factory Output report' subCaption='By Quantity' pieSliceDepth='30' showBorder='1' formatNumberScale='0' numberSuffix=' Units' >"
                        
						' Fetch all factory records
                        strQuery = "select fm.FactoryId, fm.FactoryName, sum(fo.Quantity) as TotOutput from Factory_Output fo, Factory_Master fm  where fm.FactoryId=fo.FactoryId group by fm.FactoryId, fm.FactoryName"
						Set result = oConn.Execute(strQuery)

                        ' Iterate through each factory
                        If Not result Is Nothing Then
                            While Not result.Eof
                                'Generate <set label='..' value='..' link='..' />
                                'Note that we're setting link as newchart-xmlurl-url
                                'This link denotes that linked chart would open
                                'The source data for each each is defined in the URL which will get data dynamically from the database as per the fctory id
                                strXML = strXML & "<set label='" & result("FactoryName") & "' value='" & result("TotOutput") & "'  link='newchart-xmlurl-FactoryData.asp?factoryId=" & result("FactoryId") & "'/>"
								result.MoveNext
                            Wend
                        End If
                        

                        'Finally, close <chart> element
                        strXML = strXML  & "</chart>"


                        'Create the chart - Pie 3D Chart with data from strXML
                        Call renderChart("../../FusionCharts/Pie3D.swf", "", strXML, "FactorySum", 500, 250, false, true)
                        %>
                        <br>
                        <center>
                            <!-- linked chart container -->
                            <div id="linked-chart" style="width:600px; height:250px; border:1px solid #999;">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" height="250" >
                                    <tr>
                                        <td align="center" valign="middle">Click on a pie slice above to see the linked chart appear here</td>
                                    </tr>
                                </table>
                            </div>
                        </center>

                        <script type="text/javascript">

                            // Configure linked chart
                            // set chart type to column2D, render the linked chart to another container
                            // configute the overlay button
                            FusionCharts("FactorySum").configureLink (
                            {
                                swfUrl	: "../../FusionCharts/Column2D.swf",
                                renderAt : "linked-chart",
                                width 	: "100%" ,
                                height	: "100%" ,
                                debugMode	: "0" ,
                                overlayButton: {
                                    show : true,
                                    message: ' x ',
                                    bgColor:'E4E7D9',
                                    borderColor: 'B7BBA9'
                                }
                            }, 0);

                            // store the content of the target div
                            var store =document.getElementById("linked-chart").innerHTML;

                            // When linked chart is closed revert back to the target div's original content
                            // This is achieve by setting event-listener to LinkedItemClosed event
                            FusionCharts("FactorySum").addEventListener(FusionChartsEvents.LinkedItemClosed, function() { document.getElementById("linked-chart").innerHTML = store;  } );

                        </script>

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
