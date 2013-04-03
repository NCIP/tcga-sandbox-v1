<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %> 
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="chartData" class="com.fusioncharts.sampledata.ExportRenderData"/>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="title" value="FusionCharts - Export chart and save the exported file to a server-side folder" scope="request"/>
<c:set var="header1" value="JSP Export Examples" scope="request"/>
<c:set var="header2" value="FusionCharts - Export chart and save the exported file to a server-side folder" scope="request"/>
<c:set var="intro" value="Right click on the chart to access various export options or click any of the buttons below" scope="request"/>
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>


<%--  This page demonstrates the ease of generating charts using FusionCharts.
	For this chart, we've used a pre-defined Data.xml (contained in /Data/ folder)
	The chart would be exported to jpg format, this can be changed in the parameter to the javascript.
	The exported image is saved to the server in the folder specified in properties file.
--%>

<tags:template4>
<script type="text/javascript">
	
// this function exports chart
function exportChart(exportFormat)
{
		if ( FusionCharts('<c:out value="${chartData.chartId}"/>').exportChart )
		{
			document.getElementById ( "linkToExportedFile" ).innerHTML = "Exporting...";
			FusionCharts('<c:out value="${chartData.chartId}"/>').exportChart( { "exportFormat" : exportFormat } );
		}
		else
		{
			document.getElementById ( "linkToExportedFile" ).innerHTML = "Please wait till the chart completes rendering..." ;
			
		}
		
}

// This event handler function is called by the chart after the export is completed.
// The statusCode property when found "1" states that the export is successful
// You can get the access file name from fileName property
function FC_Exported ( statusObj )
{
	if ( statusObj.statusCode == "1" )
	{
		document.getElementById ( "linkToExportedFile" ).innerHTML = "Export successful. You can view it from <a target='_blank' href='" + statusObj.fileName + "'>here</a>.";	
	}
	else
	{
		// If the export is found unsuccussful get the reason from notice property
		document.getElementById ( "linkToExportedFile" ).innerHTML = "Export unsuccessful. Error from export handler : " + statusObj.statusMessage;	
	}
}


</script>
		
<%--  
    chartId="exportSample";
    filename = "../../FusionCharts/Column3D.swf";
	url="Data/SaveData.xml";
	width="600";
	height="300";
 --%>	
<%--  Create the chart - Column 3D Chart with data from Data/SaveData.xml --%>
<c:catch var="fcTagError">

<fc:render chartId="${chartData.chartId}" swfFilename="${folderPath}${chartData.filename}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="true" xmlUrl="${chartData.saveUrl}" dataFormat="xml-url" >

</fc:render>
</c:catch>

<c:if test="${not empty fcTagError}">
	Tag Error: <br/>${fcTagError}
</c:if>
<input value="Export to JPG" type="button" onClick="JavaScript:exportChart('JPG')" />
<input value="Export to PNG" type="button" onClick="JavaScript:exportChart('PNG')" />
<input value="Export to PDF" type="button" onClick="JavaScript:exportChart('PDF')" />

<div id="linkToExportedFile" style="margin:10px; padding:5px; width:550px; background:#efefef; border : 1px dashed #cdcdcd; color: 666666;">Exported status.</div>
</tags:template4>
