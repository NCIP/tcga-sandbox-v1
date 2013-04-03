<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %> 
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="chartData" class="com.fusioncharts.sampledata.MultiLingualData"/>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>
<c:set var="title" value="FusionCharts UTF8 Example" scope="request"/>
<c:set var="header1" value="JSP UTF8 Examples" scope="request"/>
<c:set var="header2" value="Example using data from pre-built JapaneseData.xml" scope="request"/>
<c:set target="${pageContext.response}" property="contentType" value="text/html; charset=UTF-8"/>
<c:set target="${pageContext.request}" property="characterEncoding" value="UTF-8"/>    
<tags:template3> 
<%--
				In this example, we show how to use UTF characters in charts created with FusionCharts 
				Here, the XML data for the chart is present in Data/JapaneseData.xml. 
				The xml file should be created and saved with an editor
				which places the UTF8 BOM. The first line of the xml should contain the
				xml declaration like this: <?xml version="1.0" encoding="UTF-8" ?>
				
				The pageEncoding and chartSet headers for the page have been set to UTF-8
				in the first line of this jsp file.
				--%>
					
<fc:render chartId="${chartData.chartId}" swfFilename="${folderPath}${chartData.filename}" width="${chartData.width}" height="${chartData.height}" xmlUrl="${chartData.url}" />
</tags:template3> 