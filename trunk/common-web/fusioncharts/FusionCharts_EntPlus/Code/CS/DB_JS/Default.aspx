<%@ Page Language="C#" AutoEventWireup="true" CodeFile="Default.aspx.cs" Inherits="DB_JS_Default" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link href="../assets/ui/css/style.css" rel="stylesheet" type="text/css" />
    <title>FusionCharts - Database + JavaScript Example </title>
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
		//Here, we use a mix of server side script (ASP.NET) and JavaScript to
		//render our data for factory chart in JavaScript variables. We'll later
		//utilize this data to dynamically plot charts.
		
		//All our data is stored in the data array. From ASP.NET, we iterate through
		//each recordset of data and then store it as nested arrays in this data array.
		var data = new Array();
		//Write the data as JavaScript variables here
		<%=GetScript()%>
		
		
		//The data is now present as arrays in JavaScript. Local JavaScript functions
		//can access it and make use of it. We'll see how to make use of it.
		
		
		/** 
		 * updateChart method is invoked when the user clicks on a pie slice.
		 * In this method, we get the index of the factory, build the XML data
		 * for that that factory, using data stored in data array, and finally
		 * update the Column Chart.
		 *	@param	factoryIndex	Sequential Index of the factory.
		*/		
		function updateChart(factoryIndex){
			//Storage for XML data document
			var strXML = "<chart palette='2' caption='Factory " + factoryIndex  + " Output ' subcaption='(In Units)' xAxisName='Date (dd/MM)' showValues='1' labelStep='2' >";
			
			//Add <set> elements
			var i=0;
			for (i=0; i<data[factoryIndex].length; i++){
				strXML = strXML + "<set label='" + data[factoryIndex][i][0] + "' value='" + data[factoryIndex][i][1] + "' />";
			}
			
			//Closing Chart Element
			strXML = strXML + "</chart>";
						
			//Update it's XML - The new way
			FusionCharts("FactoryDetailed").setDataXML(strXML);
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
                    FusionCharts Database + JavaScript Example</h1>
            </div>
            <div class="content-area">
                <div id="content-area-inner-main">
                    <h2 class="headline">
                        Inter-connected charts - Click on any pie slice to see detailed chart below</h2>
                    <div class="gen-chart-render">
                        <center>
                            <asp:Literal ID="Literal1" runat="server"></asp:Literal>
                            <br>
                            <asp:Literal ID="Literal2" runat="server"></asp:Literal>
                        </center>
                    </div>
                    <div class="clear">
                    </div>
                    <p>&nbsp;
                        </p>
                    <p class="small">
                        The charts in this page have been dynamically generated using data contained in
                        a database. We've NOT hard-coded the data in JavaScript</p>
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
