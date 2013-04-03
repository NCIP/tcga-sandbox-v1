<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %> 
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="chartData" class="com.fusioncharts.sampledata.MultiLingualData"/>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>


<c:set var="title" value="FusionCharts Examples" scope="request"/>
<c:set var="header1" value="JSP UTF8 Examples" scope="request"/>

<c:set var="header2" value="Example using multi-lingual text - dataStr method" scope="request"/>

<c:set target="${pageContext.response}" property="contentType" value="text/html; charset=UTF-8"/>
<tags:template3> 
<fc:render chartId="${chartData.chartId}" swfFilename="${folderPath}${chartData.filename}" width="${chartData.width}" height="${chartData.height}" xmlData="${chartData.mixedXml}" />
</tags:template3> 
