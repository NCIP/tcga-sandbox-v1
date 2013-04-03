
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %> 
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="chartData" class="com.fusioncharts.sampledata.BasicRenderData"/>
<%@page import="com.fusioncharts.sampledata.ChartType" %>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="title" value="FusionCharts - Multiple Charts on one Page" scope="request"/>
<c:set var="header1" value="JSP Basic Examples" scope="request"/>
<c:set var="header2" value="Multiple Charts on the same page. Each chart has a unique ID." scope="request"/>
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>
<% 
pageContext.setAttribute("col2dChart",ChartType.COLUMN2D.getFileName());
pageContext.setAttribute("lineChart",ChartType.LINE.getFileName());
%>

	<%--
	 This page demonstrates how you can show multiple charts on the same page.
	 For this example, all the charts use the pre-built Data.xml (contained in /Data/ folder)
	 However, you can very easily change the data source for any chart. 
	
	 IMPORTANT NOTE: Each chart necessarily needs to have a unique ID on the page.
	 If you do not provide a unique Id, only the last chart might be visible.
	 Here, we've used the ID chart1, chart2 and chart3 for the 3 charts on page.
	 --%>
	
	<%--Create the chart - Column 3D Chart with data from Data/Data.xml--%> 
<tags:template2>
<fc:printManager enabled="true"></fc:printManager>
<fc:render chartId="${chartData.chartId}" swfFilename="${folderPath}${chartData.swfFilename}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="true" xmlUrl="${chartData.url}" />
<BR>
<BR>
	<%-- Now, create a Column2D Chart--%>
<fc:render chartId="${chartData.uniqueId}" swfFilename="${folderPath}${col2dChart}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="true" xmlUrl="${chartData.url}" />
<BR>
<BR>
	<%-- Now, create a Line2D Chart --%>
<fc:render chartId="${chartData.uniqueId}" swfFilename="${folderPath}${lineChart}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="true" xmlUrl="${chartData.url}" />
<script type="text/javascript">

                            // The Print Manager takes a  bit of time to make all the charts ready for managed print
                            // Hence, declaring an event listener to listen to the PrintReadyStateChange evnet that
                            // Print Manager invokes when all charts are ready for print
                            // Add an event listener
                            FusionCharts.addEventListener("PrintReadyStateChange", function (e, a) {
                                // Enables Print button when print manager is ready
                                document.getElementById('printButton').disabled = !a.ready;
                                // Also adds pertinent text
                                document.getElementById('status').innerHTML = a.ready ?
                                    'Charts are ready for print. You may use File->Print now.' :
                                    'Charts are not ready for managed print. Please wait.'
                            });

                        </script>
                        <BR><BR>
                        <!-- adds button which will invoke managedPrint() function when Print Manager is ready -->
                        <button type="button" disabled="disabled" id="printButton" onClick="FusionCharts.printManager.managedPrint()">Print Page</button>
                        <span id="status">Charts are not ready for managed print. Please wait.</span>

</tags:template2>