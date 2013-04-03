<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %> 
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="chartData" class="com.fusioncharts.sampledata.TransparentRenderData"/>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="title" value="FusionCharts - Transparent Column 3D Chart" scope="request"/>
<c:set var="header1" value="JSP Basic Examples" scope="request"/>
<c:set var="header2" value="FusionCharts - Transparent Column 3D Chart" scope="request"/>
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>


<%--  This page demonstrates the ease of generating charts using FusionCharts.
	For this chart, we've used a pre-defined Data.xml (contained in /Data/ folder)
	Ideally, you would NOT use a physical data file. Instead you'll have 
	your own code virtually relay the XML data document. Such examples are also present.
	For a head-start, we've kept this example very simple.
--%>
<%--  
    chartId="myFirstChart_Transparent";
    filename = "../../FusionCharts/Column3D.swf";
	url="Data/Data_Transparent.xml";
	width="600";
	height="300";
 --%>	
<%-- Create the chart - Column 3D Chart with data from Data/Data.xml --%>
<tags:template2>
<div style="padding:40px; background-color:#9d7fbd; border:1px solid #745C92; width: 600px;">
<c:catch var="fcTagError">
<fc:render chartId="${chartData.chartId}" swfFilename="${folderPath}${chartData.swfFilename}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="false" xmlUrl="${chartData.url}" windowMode="transparent" />
</c:catch>
<c:catch var="fcTagError2">
<fc:renderHTML chartId="${chartData.chartId2}" swfFilename="${folderPath}${chartData.swfFilename}" width="${chartData.width}" height="${chartData.height}" debugMode="false" xmlUrl="${chartData.url}" windowMode="opaque" />
</c:catch>
</div>
<c:if test="${not empty fcTagError}">
	Tag Error: <br/>${fcTagError}
</c:if>
<c:if test="${not empty fcTagError2}">
	Tag Error: <br/>${fcTagError2}
</c:if>
</tags:template2>
