<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %> 
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="chartData" class="com.fusioncharts.sampledata.BasicRenderData"/>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="title" value="FusionCharts - Simple Column 3D Chart" scope="request"/>
<c:set var="header1" value="JSP Basic Examples" scope="request"/>
<c:set var="header2" value="Basic example using dataStr method" scope="request"/>
<c:set var="intro" value="If you view the source of this page, you'll see that the XML data
is present in this same page (inside HTML code). We're not calling any
external XML files to serve XML data. dataStr method is
ideal when you've to plot small amounts of data."/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>
	<%--This page demonstrates the ease of generating charts using FusionCharts.
	For this chart, we've placed our entire XML data in the body of the fc:renderHTML tag.
	
	Ideally, you would generate XML data documents at run-time, after interfacing with
	forms or databases etc.Such examples are also present.
	Here, we've kept this example very simple.
	--%>
<%--  
    chartId="myFirst"
    swfFilename = "../../FusionCharts/Column3D.swf"
	width="600"
	height="300"
	debugMode="false"
 --%>		
	<%-- Create the chart - Column 3D Chart with data from strXML variable using dataStr method--%>
<tags:template1>
<fc:renderHTML chartId="${chartData.chartId}" swfFilename="${folderPath}${chartData.swfFilename}" width="${chartData.width}" height="${chartData.height}" debugMode="false" >
<chart caption='Monthly Unit Sales' xAxisName='Month' yAxisName='Units' showValues='0' formatNumberScale='0' showBorder='1'>
<set label='Jan' value='462' />
<set label='Feb' value='857' />
<set label='Mar' value='671' />
<set label='Apr' value='494' />
<set label='May' value='761' />
<set label='Jun' value='960' />
<set label='Jul' value='629' />
<set label='Aug' value='622' />
<set label='Sep' value='376' />
<set label='Oct' value='494' />
<set label='Nov' value='761' />
<set label='Dec' value='960' />
</chart>
</fc:renderHTML>
</tags:template1>