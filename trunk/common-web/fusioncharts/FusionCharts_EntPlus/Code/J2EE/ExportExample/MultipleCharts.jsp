<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %> 
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="chartData" class="com.fusioncharts.sampledata.ExportRenderData"/>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="title" value="FusionCharts - Export example - Export Multiple Charts on one Page" scope="request"/>
<c:set var="header1" value="JSP Export Examples" scope="request"/>
<c:set var="header2" value="FusionCharts - Export example - Export Multiple Charts on one Page" scope="request"/>
<c:set var="intro" value="Click any one of the buttons below to export all the charts present in the page. " scope="request"/>
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>


<%--  This page demonstrates exporting multiple charts on one page.
	  Note that each chart image is exported to a different file. 
--%>

<tags:template4>
<script type="text/javascript">
	
var initiateExport = false;

function exportCharts(exportFormat)
{
		initiateExport = true;
		for ( var chartRef in FusionCharts.items )
		{
			//alert(chartRef);
			if ( FusionCharts.items[chartRef].exportChart )
			{
				document.getElementById ( "linkToExportedFile" ).innerHTML = "Exporting...";
				FusionCharts.items[chartRef].exportChart( { "exportFormat" : exportFormat } );
			}
			else
			{
				
				document.getElementById ( "linkToExportedFile" ).innerHTML = "Please wait till the chart completes rendering..." ;
			}
		}
		
}

function FC_Exported ( statusObj )
{
	if (initiateExport)  
	{
		initiateExport = false;
		document.getElementById ( "linkToExportedFile" ).innerHTML = "";
	}
	
	if ( statusObj.statusCode == "1" )
	{
		document.getElementById ( "linkToExportedFile" ).innerHTML += "Export successful. View it from <a target='_blank' href='" + statusObj.fileName + "'>here</a>.<br/>";	
	}
	else
	{
		document.getElementById ( "linkToExportedFile" ).innerHTML += "Export unsuccessful. Notice from export handler : " + statusObj.statusMessage + "<br/>" ;	

	}
}

</script>
		

<c:catch var="fcTagError">
<%--  
    chartId=some unique id;
    filename = "../../FusionCharts/Column3D.swf";
	url="Data/SaveData.xml";
	width="600";
	height="300";
 --%>	
<fc:render chartId="${chartData.uniqueId}" swfFilename="${folderPath}${chartData.filename}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="true" xmlUrl="${chartData.saveUrl}" />
<br/>
<br/>
<%--  
    chartId=some unique id;
    filename = "../../FusionCharts/Pie3D.swf";
	url="Data/SaveData.xml";
	width="600";
	height="300";
 --%>	
<fc:render chartId="${chartData.uniqueId}" swfFilename="${folderPath}${chartData.filename2}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="true" xmlUrl="${chartData.saveUrl}" />
<br/>
<br/>
<%--  
    chartId=some unique id;
    filename = "../../FusionCharts/Column2D.swf";
	url="Data/SaveData.xml";
	width="600";
	height="300";
 --%>	
<fc:render chartId="${chartData.uniqueId}" swfFilename="${folderPath}${chartData.filename3}" width="${chartData.width}" height="${chartData.height}" debugMode="false" registerWithJS="true" xmlUrl="${chartData.saveUrl}" />

</c:catch>
<c:if test="${not empty fcTagError}">
	Tag Error: <br/>${fcTagError}
</c:if>
<input value="Export to JPG" type="button" onClick="JavaScript:exportCharts('jpg')" />
<input value="Export to PNG" type="button" onClick="JavaScript:exportCharts('PNG')" />
<input value="Export to PDF" type="button" onClick="JavaScript:exportCharts('PDF')" />

<div id="linkToExportedFile" style="margin:10px; padding:5px; width:550px; background:#efefef; border : 1px dashed #cdcdcd; color: 666666;">Exported status.</div>
</tags:template4>
