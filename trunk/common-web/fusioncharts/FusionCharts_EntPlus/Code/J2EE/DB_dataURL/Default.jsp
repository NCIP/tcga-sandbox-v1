<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/functions" prefix="fchelper" %>  
<%@page import="com.fusioncharts.FusionChartsHelper" %>
<jsp:useBean id="chartData" class="com.fusioncharts.sampledata.DynamicRenderData"/>
<c:set var="animateChart" value="${param.animate}"/>
<c:if test="${empty animateChart}">
<c:set var="animateChart" value="1"/>
</c:if>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="title" value="FusionCharts - dataURL and Database Example" scope="request"/>
<c:set var="header1" value="FusionCharts Database Examples" scope="request"/>
<c:set var="header2" value="Click on any pie slice to slice it out.Or, right click to enable
rotation mode." scope="request"/>
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>

<c:set var="urlWithParams" value="${chartData.url}?animate=${animateChart}&test1=abc def&test2=etc ger"/>
<% // In case we are using fc:renderHTML then the parameters need to encoded, in which case, please use the following encodedURL %>
<c:set var="encodedURL" value="${fchelper:encodeDataURL(urlWithParams,true)}"/>

<tags:template2> 

 <%-- Create the chart - Pie 3D Chart with data got from another jsp --%>

<fc:render chartId="${chartData.chartId}" swfFilename="${folderPath}${chartData.filename}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="false" xmlUrl="${urlWithParams}" />
</tags:template2>