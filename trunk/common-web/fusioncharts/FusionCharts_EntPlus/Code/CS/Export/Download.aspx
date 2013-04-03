<%@ Page Language="C#" AutoEventWireup="true" CodeFile="Download.aspx.cs" Inherits="Export_download_after_export" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>FusionCharts - Export Example - Export chart and download the exported file</title>
    <link href="../assets/ui/css/style.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="../FusionCharts/FusionCharts.js"></script>

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
</head>
<body>
    <form id="form1" runat="server">
    <div id="wrapper">
        <div id="header">
           
            <div class="logo">
                <a class="imagelink" href="../Default.aspx">
                    <img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75"
                        alt="FusionCharts XT logo" /></a></div>
            <h1 class="brand-name">FusionCharts XT</h1>
            <h1 class="logo-text">
                ASP.NET(C#) Export Examples</h1>
        </div>
        <div class="content-area">
            <div id="content-area-inner-main">
                <h2 class="headline">
                    Export example - Export chart and download the exported file
                </h2>
                <div class="gen-chart-render">

                    <script type="text/javascript">

        // this function exports chart from JavaScript
        function exportChart(exportFormat) {
            // checks if exportChart function is present and call exportChart function
            if (FusionCharts("myFirst").exportChart)
                FusionCharts("myFirst").exportChart({ "exportFormat": exportFormat });
            else
                alert("Please wait till the chart completes rendering...");

        }
	
	
                    </script>

                    <div>
                        <div id="div1" align="center">
                            <asp:Literal ID="Literal1" runat="server"></asp:Literal>
                        </div>
                        <br />
                        <input value="Export to JPG" type="button" onclick="JavaScript:exportChart('JPG')" />
                        <input value="Export to PNG" type="button" onclick="JavaScript:exportChart('PNG')" />
                        <input value="Export to PDF" type="button" onclick="JavaScript:exportChart('PDF')" />
                    </div>
                    <div class="clear">
                    </div>
                    <p>&nbsp;
                        </p>
                    <p class="small">
                        Right click on the chart to accee various export options or click any of the buttons
                        below</p>
                    <div class="underline-dull">
                    </div>
                </div>
            </div>
            <div id="footer">
                <ul>
                    <li><a href="../Default.aspx"><span>&laquo; Back to list of examples</span></a></li>
                    <li class="pipe">|</li>
                    <li><a href="../NoChart.html"><span>Unable to see the chart above?</span></a></li>
                </ul>
            </div>
        </div>
    </form>
</body>
</html>
