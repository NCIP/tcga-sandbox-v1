<%@ Page Language="C#" AutoEventWireup="true" CodeFile="ManagedPrint.aspx.cs" Inherits="BasicExample_ManagedPrint" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>FusionCharts - Using Managed Print</title>

    <script type="text/javascript" src="../FusionCharts/FusionCharts.js"></script>

    <link href="../assets/ui/css/style.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="../assets/ui/js/jquery-1.4.2.min.js"></script>

    <script type="text/javascript" src="../assets/ui/js/lib.js"></script>

    <!--[if IE 6]>
        <script>
                <script type="text/javascript" src="../assets/ui/js/DD_belatedPNG_0.0.8a-min.js"></script>
          /* select the element name, css selector, background etc */
          DD_belatedPNG.fix('img');

          /* string argument can be any CSS selector */
        </script>
        <![endif]-->
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
                ASP.NET(C#) Basic Examples</h1>
        </div>
        <div class="content-area">
            <div id="content-area-inner-main">
                <h2 class="headline">
                    Multiple Charts on the same page. Setting up manged-print for better printing in
                    Mozilla based browsers.</h2>
                <div class="gen-chart-render">
                    <asp:Literal ID="Literal1" runat="server"></asp:Literal>
                    <br />
                    <br />
                    <asp:Literal ID="Literal2" runat="server"></asp:Literal>
                    <br />
                    <br />
                    <asp:Literal ID="Literal3" runat="server"></asp:Literal>
                    <br />
                    <asp:Literal ID="Literal4" runat="server"></asp:Literal>

                    <script type="text/javascript">

                            // The Print Manager takes a  bit of time to make all the charts ready for managed print
                            // Hence, declaring an event listener to listen to the PrintReadyStateChange evnet that
                            // Print Manager invokes when all charts are ready for print
                            // Add an event listener
                            FusionCharts.addEventListener("PrintReadyStateChange", function(e, a) {
                                // Enables Print button when print manager is ready
                                document.getElementById('printButton').disabled = !a.ready;
                                // Also adds pertinent text
                                document.getElementById('status').innerHTML = a.ready ?
                                    'Charts are ready for print. You may use File->Print now.' :
                                    'Charts are not ready for managed print. Please wait.'
                            });

                    </script>

                    <br />
                    <br />
                    <!-- adds button which will invoke managedPrint() function when Print Manager is ready -->
                    <button type="button" disabled="disabled" id="printButton" onclick="FusionCharts.printManager.managedPrint()">
                        Print Page</button>
                    <span id="status">Charts are not ready for managed print. Please wait.</span>
                </div>
                <div class="clear">
                </div>
                <p>&nbsp;
                    </p>
                <p class="small">
                    <!--<p class="small">This dashboard was created using FusionCharts XT, FusionWidgets v3 and FusionMaps v3 You are free to reproduce and distribute this dashboard in its original form, without changing any content, whatsoever. <br />
            &copy; All Rights Reserved</p>
          <p>&nbsp;</p>-->
                </p>
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
