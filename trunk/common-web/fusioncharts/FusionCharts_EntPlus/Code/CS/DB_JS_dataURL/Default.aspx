<%@ Page Language="C#" AutoEventWireup="true" CodeFile="Default.aspx.cs" Inherits="DB_JS_dataURL_Default" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>FusionCharts - Database + JavaScript Example </title>
    <link href="../assets/ui/css/style.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="../assets/ui/js/jquery-1.4.2.min.js"></script>

    <script type="text/javascript" src="../assets/ui/js/lib.js"></script>

    <style type="text/css">
        h2.headline
        {
            font: normal 110%/137.5% "Trebuchet MS" , Arial, Helvetica, sans-serif;
            padding: 0;
            margin: 25px 0 25px 0;
            color: #7d7c8b;
            text-align: center;
        }
        p.small
        {
            font: normal 68.75%/150% Verdana, Geneva, sans-serif;
            color: #919191;
            padding: 0;
            margin: 0 auto;
            width: 664px;
            text-align: center;
        }
    </style>

    <script type="text/javascript" src="../FusionCharts/FusionCharts.js">
		//You need to include the above JS file, if you intend to embed the chart using JavaScript.
		//Embedding using JavaScripts avoids the "Click to Activate..." issue in Internet Explorer
		//When you make your own charts, make sure that the path to this JS file is correct. Else, you would get JavaScript errors.
    </script>

    <script type="text/javascript">
		
		/** 
		 * updateChart method is invoked when the user clicks on a pie slice.
		 * In this method, we get the index of the factory after which we request for XML data
		 * for that that factory from FactoryData.aspx, and finally
		 * update the Column Chart.
		 *	@param	factoryIndex	Sequential Index of the factory.
		*/
        function updateChart(factoryIndex) {
            //DataURL for the chart
            var strURL = "FactoryData.aspx?FactoryId=" + factoryIndex;

            //Sometimes, the above URL and XML data gets cached by the browser.
            //If you want your charts to get new XML data on each request,
            //you can add the following line:
            //strURL = strURL + "&currTime=" + getTimeForURL();
            //getTimeForURL method is defined below and needs to be included
            //This basically adds a ever-changing parameter which bluffs
            //the browser and forces it to re-load the XML data every time.

            //Get reference to chart object using Dom ID "FactoryDetailed"
            //Send request for XML
            FusionCharts("FactoryDetailed").setXMLUrl(strURL);
        }
        
		/**
		 * getTimeForURL method returns the current time 
		 * in a URL friendly format, so that it can be appended to
		 * dataURL for effective non-caching.
		*/
		function getTimeForURL(){
			var dt = new Date();
			var strOutput = "";
			strOutput = dt.getHours() + "_" + dt.getMinutes() + "_" + dt.getSeconds() + "_" + dt.getMilliseconds();
			return strOutput;
		}
    </script>

</head>
<body>
    <center>
        <form id='form1' name='form1' method='post' runat="server">
        <div id="wrapper">
            <div id="header">
                
                <div class="logo">
                    <a class="imagelink" href="../Default.aspx">
                        <img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75"
                            alt="FusionCharts XT logo" /></a></div>
                <h1 class="brand-name">FusionCharts XT</h1>
                <h1 class="logo-text">
                    FusionCharts Database + JavaScript (dataURL method) Example</h1>
            </div>
            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">
                        Inter-connected charts - Click on any pie slice to see detailed chart below</h2>
                    <div class="gen-chart-render">
                        <asp:Literal ID="Literal1" runat="server"></asp:Literal>
                        <br>
                        <asp:Literal ID="Literal2" runat="server"></asp:Literal>
                    </div>
                    <div class="clear">
                    </div>
                    <p>&nbsp;
                        </p>
                    <p class="small">
                        The charts in this page have been dynamically generated using data contained in
                        a database.</p>
                    <div class="underline-dull">
                    </div>
                </div>
            </div>
            <div id="footer">
                <ul>
                    <li><a href="../Default.aspx"><span>&laquo; Back to list of examples</span></a></li>
                    <li cl ass="pipe">|</li>
                    <li><a href="../NoChart.html"><span>Unable to see the chart above?</span></a></li>
                </ul>
            </div>
        </div>
        </form>
    </center>
</body>
</html>
