<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %> 
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="chartData" class="com.fusioncharts.sampledata.ExportRenderData"/>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="title" value="FusionCharts - Export chart and download the exported file" scope="request"/>
<c:set var="header1" value="JSP Export Examples" scope="request"/>
<c:set var="header2" value="Export example - Export chart and download the exported file" scope="request"/>
<c:set var="intro" value="Right click on the chart to access various export options or click any of the buttons below" scope="request"/>
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>


<%--  This page demonstrates the ease of generating chart images using FusionCharts.
	For this chart, we've used a pre-defined DownloadData.xml (contained in /Data/ folder)
	The chart would be exported to jpg format, this can be changed in the parameter to the javascript.
	The exported image can then be downloaded to the local machine.
--%>

<tags:template4>
<script type="text/javascript">
	
	// this function exports chart from JavaScript
	function exportChart(exportFormat)
	{
		
			// checks if exportChart function is present and call exportChart function
			if ( FusionCharts('<c:out value="${chartData.chartId}"/>').exportChart )		{		
				FusionCharts('<c:out value="${chartData.chartId}"/>').exportChart( { "exportFormat" : exportFormat } );
			}
			else
				alert ( "Please wait till the chart completes rendering..." );
			
	}
	
	
</script>
<%--  
    chartId="exportSample";
    filename = "../../FusionCharts/Column3D.swf";
	url="Data/DownloadData.xml";
	width="600";
	height="300";
 --%>	
<%-- Create the chart - Column 3D Chart with data from Data/DownloadData.xml --%>
<c:catch var="fcTagError">
<fc:render chartId="${chartData.chartId}" swfFilename="${folderPath}${chartData.filename}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="true" xmlUrl="${chartData.downloadUrl}" />
</c:catch>
<c:if test="${not empty fcTagError}">
	Tag Error: <br/>${fcTagError}
</c:if>
<input value="Export to JPG" type="button" onClick="JavaScript:exportChart('JPG')" />
<input value="Export to PNG" type="button" onClick="JavaScript:exportChart('PNG')" />
<input value="Export to PDF" type="button" onClick="JavaScript:exportChart('PDF')" />

</tags:template4>
