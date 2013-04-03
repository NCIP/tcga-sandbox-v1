<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<jsp:useBean id="chartData"
	class="com.fusioncharts.sampledata.BasicRenderData" />
<c:set var="folderPath" value="../../FusionCharts/" />
<c:set var="title"
	value="FusionCharts - Simple Column 3D Chart using setXMLData method"
	scope="request" />
<c:set var="header1" value="JSP Basic Examples" scope="request" />
<c:set var="header2"
	value="Basic example using setJSONData method (with JSON data hard-coded in
bean itself)"
	scope="request" />
<c:set var="intro"
	value="If you view the source of this page, you'll see that the JSON data
is present in this same page (inside HTML code). We're not calling any
external XML files to serve the data." />
<c:set var="jsPath" value="${folderPath}" scope="request" />
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>

<tags:template2>
	<c:catch var="fcTagError">
		<fc:render chartId="${chartData.chartId}"
			swfFilename="${folderPath}${chartData.swfFilename}"
			width="${chartData.width}" height="${chartData.height}"
			debugMode="false" registerWithJS="false" dataFormat="json">
{    "chart": {        "caption" : "Monthly Unit Sales",        
                       "xAxisName":"Month",
                       "yAxisName":"Units",
                       "showValues":"0",
                       "formatNumberScale":"0",
                       "showBorder":"1"   },    
			"data": [        
						{"label":"Jan","value":"462"},        
						{"label":"Feb","value":"857"},        
						{"label":"Mar","value":"671"},        
						{"label":"Apr","value":"494"},        
						{"label":"May","value":"761"},        
						{"label":"Jun","value":"960"},        
						{"label":"Jul","value":"629"},        
						{"label":"Aug","value":"622"},        
						{"label":"Sep","value":"376"},        
						{"label":"Oct","value":"494"},        
						{"label":"Nov","value":"761"},        
						{"label":"Dec","value":"960"}    
					]
}
</fc:render>
	</c:catch>
	<c:if test="${not empty fcTagError}">
	${fcTagError}
	</c:if>
</tags:template2>