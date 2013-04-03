<%@ Page Language="VB" AutoEventWireup="false" CodeFile="Automated.aspx.vb" Inherits="Export_Automated" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    
 <title>FusionCharts XT ASP.NET(VB) Samples</title>
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

    <script type="text/javascript">
        function FC_Rendered(DOMId) {
            if (FusionCharts(DOMId).exportChart) {
                // you can change the value of exportFormat to 'PNG' or 'PDF'
                FusionCharts(DOMId).exportChart({ "exportFormat": 'JPG' });
            }
        }
			
    </script>

</head>
<body>
    <div id="wrapper">
        <div id="header">
            
            <div class="logo">
                <a class="imagelink"  href="http://www.fusioncharts.com/" target="_blank">
                    <img src="../assets/ui/images/fusionchartsv3.2-logo.png" width="131" height="75"
                        alt="FusionCharts XT logo" /></a></div>
            <h1 class="brand-name">FusionCharts XT</h1>
            <h1 class="logo-text">
                FusionCharts ASP.NET(VB) Export Samples</h1>
        </div>
        <div class="content-area">
            <div id="content-area-inner-main">
                <h2 class="headline">
                    Export example - Automatic export chart and download the exported file</h2>
                <div class="gen-chart-render">
                    <center>
                        <asp:Literal ID="Literal1" runat="server"></asp:Literal>
                    </center>
                </div>
                <div class="clear">
                </div>
                <p>&nbsp;
                    </p>
                <p class="small">
                    The chart will automatically export itself after it finishes rendering. The export
                    format is presently set as JPG which you can always change via JavaScript.</p>
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
</body>
</html>
