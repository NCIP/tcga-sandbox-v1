<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
'We've included ../Includes/FusionCharts.asp, which contains functions
'to help us easily embed the charts.
%>
<!-- #INCLUDE FILE="../Includes/FusionCharts.asp" -->
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title>FusionCharts - Simple Column 3D Chart using JSON in dataStr method</title>
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
                <h1 class="logo-text">ASP Basic Examples</h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">Basic example using dataStr method (with JSON data hard-coded in ASP page itself)</h2>

                    <div class="gen-chart-render">
                        <%

                        'This page demonstrates the ease of generating charts using FusionCharts.
                        'For this chart, we've used a string variable to contain our entire JSON data.

                        'Ideally, you would generate/retrieve JSON data documents at run-time, after interfacing with
                        'forms or databases etc.Such examples are also present.
                        'Here, we've kept this example very simple.

                        'Create an JSON data document in a string variable
                        strJSON  = "{""chart"":{""caption"":""Monthly Unit Sales"", ""xaxisname"":""Month"",""yaxisname"":""Units"", ""showvalues"":""0"",""formatnumberscale"":""0"", ""showborder"":""1""  },""data"":[{ ""label"":""Jan"", ""value"":""462""  },{ ""label"":""Feb"", ""value"":""857""  },{ ""label"":""Mar"", ""value"":""671""  },{ ""label"":""Apr"", ""value"":""494""  },{ ""label"":""May"", ""value"":""761""  },{ ""label"":""Jun"", ""value"":""960""  },{ ""label"":""Jul"", ""value"":""629""  },{ ""label"":""Aug"", ""value"":""622""  },{ ""label"":""Sep"", ""value"":""376""  },       { ""label"":""Oct"", ""value"":""494""  },{ ""label"":""Nov"", ""value"":""761""  },{ ""label"":""Dec"", ""value"":""960""  }]}"

                        ' set chart data format to json
                        FC_SetDataFormat("json")
                        'Create the chart - Column 3D Chart with data from strJSON variable using dataStr method
                        Call renderChart("../../FusionCharts/Column3D.swf", "", strJSON, "myNext", 600, 300, false, true)
                        %>
                    </div>
                    <div class="clear"></div>
                    <p>&nbsp;</p>
                    <p class="small"> If you view the source of this page, you'll see that the JSON data is present in this same page (inside HTML code). We're not calling any external JSON (or script) files to serve JSON data. dataStr method is ideal when you've to plot small amounts of data. <!--<p class="small">This dashboard was created using FusionCharts XT, FusionWidgets v3 and FusionMaps v3 You are free to reproduce and distribute this dashboard in its original form, without changing any content, whatsoever. <br />
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
