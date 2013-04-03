<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %> 
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="chartData" class="com.fusioncharts.sampledata.ArrayRenderData"/>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="title" value="FusionCharts - Array Example using Single Series Column
3D Chart" scope="request"/>
<c:set var="header1" value="JSP Array Examples" scope="request"/>
<c:set var="header2" value="Plotting single series chart from data contained in Array." scope="request"/>
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>

<tags:template2> 
	<%-- In this example, we plot a single series chart from data contained
	in an array. The array will have two columns - first one for data label
	and the next one for data values.
	
    --%>
 <%-- Create the chart - Column 3D Chart with data contained in bean --%>
<fc:render chartId="${chartData.chartId}" swfFilename="${folderPath}${chartData.filename}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="false" xmlData="${chartData.xml}" />
</tags:template2>
