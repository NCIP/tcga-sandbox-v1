<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <title><c:out value="${title}"/></title>
        <!-- ../assets/ui/css/ ../assets/ui/js/ ../assets/ui/images/ -->
        <link href="${assetCSSPath}style.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="${assetJSPath}jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="${assetJSPath}lib.js"></script>
        <!--[if IE 6]>
          <script type="text/javascript" src="${assetJSPath}DD_belatedPNG_0.0.8a-min.js"></script>
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
        <!-- 
        //You need to include the following JS file, if you intend to embed the chart using JavaScript.
        //Embedding using JavaScripts avoids the "Click to Activate..." issue in Internet Explorer
        //When you make your own charts, make sure that the path to this JS file is correct. Else, you
        //would get JavaScript errors.
        -->
        <SCRIPT LANGUAGE="Javascript" SRC="${jsPath}FusionCharts.js"></SCRIPT>

    </head>
    <BODY>

        <div id="wrapper">

            <div id="header">
                <div class="back-to-home"><a href="../index.html">Back to home</a></div>

                <div class="logo"><a class="imagelink"  href="http://www.fusioncharts.com" target="_blank">
                <img src="${assetImagePath}fusionchartsv3.2-logo.png" width="131" height="75" alt="FusionCharts v3.2 logo" /></a></div>
                <h1 class="brand-name">FusionCharts</h1>
                <h1 class="logo-text"><c:out value="${header1}"/></h1>
            </div>

            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline"><c:out value="${header2}"/></h2>

                    <div class="gen-chart-render">
                       <jsp:doBody var="bodycontent"/>
					   <c:out value="${bodycontent}" escapeXml="false"/>
                    </div>
                    <div class="clear"></div>
                    <p>&nbsp;</p>
                    <p class="small"> ${intro} 
                    <!--<p class="small">This dashboard was created using FusionCharts v3, FusionWidgets v3 and FusionMaps v3 You are free to reproduce and distribute this dashboard in its original form, without changing any content, whatsoever. <br />
            &copy; All Rights Reserved</p>
          <p>&nbsp;</p>-->
                    </p>

                    <div class="underline-dull"></div>
                </div>
            </div>

            <div id="footer">
                <ul>
                    <li><a href="../index.html"><span>&laquo; Back to list of examples</span></a></li>
                    <li cl ass="pipe">|</li>
                    <li><a href="../NoChart.html"><span>Unable to see the chart above?</span></a></li>
                </ul>
            </div>
        </div>
    </BODY>
</HTML>
