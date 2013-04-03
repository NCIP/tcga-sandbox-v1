<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc"%>
<jsp:useBean id="factoriesBean"
	class="com.fusioncharts.sampledata.FactoriesBean" />
<jsp:useBean id="chart2Data"
	class="com.fusioncharts.sampledata.EmptyRenderData" />
<c:set var="folderPath" value="../../FusionCharts/" />
<c:set var="title" value="FusionCharts AJAX and Database Example"
	scope="request" />
<c:set var="header1"
	value="FusionCharts AJAX and Database Example"
	scope="request" />
<c:set var="header2"
	value="Inter-connected charts - Click on any pie slice to see detailed chart below."
	scope="request" />
<c:set var="intro"
	value="The charts in this page have been dynamically generated using data contained in a database."
	scope="request" />		
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>

<tags:template2>
<SCRIPT LANGUAGE="JavaScript">
				
				
				/** 
				 * updateChart method is invoked when the user clicks on a pie slice.
				 * In this method, we get the index of the factory after which we request for XML data
				 * for that that factory from FactoryData.jsp, and finally
				 * update the Column Chart.
				 *	@param	factoryIndex	Sequential Index of the factory.
				*/		
				function updateChart(factoryIndex){		

					var strURL = "FactoryChart.jsp?FactoryId=" + factoryIndex;

					$.ajax({
					  type: "GET",
					  url: strURL,
					  success: function(msg){
							 $("#detailedChart").show().html(msg);
					  }
					});

					
				}

				
			</SCRIPT>
	<%-- Create the chart - Pie 3D Chart with data contained in bean --%>

	<fc:render chartId="${factoriesBean.chartId}"
		swfFilename="${folderPath}${factoriesBean.filename}"
		width="${factoriesBean.width}" height="${factoriesBean.height}"
		debugMode="false"  xmlData="${factoriesBean.xml}" />
<BR>
<div id="detailedChart" style="width:600px;height:300px;display:none;"></div>

   <BR/>
	<BR/>

</tags:template2>