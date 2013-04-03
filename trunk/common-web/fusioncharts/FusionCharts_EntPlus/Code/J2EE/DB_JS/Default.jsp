<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc"%>
<jsp:useBean id="factoriesBean"
	class="com.fusioncharts.sampledata.FactoriesWithJSLinkBean" />
<jsp:useBean id="chart2Data"
	class="com.fusioncharts.sampledata.EmptyRenderData" />
<c:set var="folderPath" value="../../FusionCharts/" />
<c:set var="title" value="FusionCharts - Database + JavaScript Example"
	scope="request" />
<c:set var="header1"
	value="JSP Database Examples"
	scope="request" />
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>
<tags:template2>
<SCRIPT LANGUAGE="JavaScript">
				//Here, we use a mix of server side code (jsp) and JavaScript to
				//render our data for factory chart in JavaScript variables. We'll later
				//utilize this data to dynamically plot charts.
				
				//All our data is stored in the data array. From jsp, we iterate through
				//each resultset of data and then store it as nested arrays in this data array.
				var data = new Array();
				
				
				/*Write the data as JavaScript variables here
				The data is now present as arrays in JavaScript. Local JavaScript functions
				can access it and make use of it. We'll see how to make use of it.
				*/
				
				<c:out value="${factoriesBean.factoryDetailsJsArrAsStr}" escapeXml="false"/>
				/** 
				 * updateChart method is invoked when the user clicks on a pie slice.
				 * In this method, we get the index of the factory, build the XML data
				 * for that that factory, using data stored in data array, and finally
				 * update the Column Chart.
				 * @param	factoryIndex	Sequential Index of the factory.
				 * @param	factoryName For display purpose
				*/		
				function updateChart(factoryIndex,factoryName){
					//Storage for XML data document
					var strXML = "<chart palette='2' caption='Factory: " + factoryName  + " Output ' subcaption='(In Units)' xAxisName='Date' showValues='1' labelStep='2' >";
					
					//Add <set> elements
					var i=0;
					for (i=0; i<data[factoryIndex].length; i++){
						strXML = strXML + "<set label='" + data[factoryIndex][i][0] + "' value='" + data[factoryIndex][i][1] + "' />";
					}
					
					//Closing Chart Element
					strXML = strXML + "</chart>";
								
					//Get reference to chart object using Dom ID "FactoryDetailed"
					var chartObj = FusionCharts('FactoryDetailed');
					//Update it's XML
					chartObj.setXMLData(strXML);
				}
			</SCRIPT>
	<%-- Create the chart - Pie 3D Chart with data contained in bean --%>

	<fc:render chartId="${factoriesBean.chartId}"
		swfFilename="${folderPath}${factoriesBean.filename}"
		width="${factoriesBean.width}" height="${factoriesBean.height}"
		debugMode="false"  xmlData="${factoriesBean.xml}"/>

   <BR/>
	<BR/>
	<%--  
				Column 2D Chart with changed "No data to display" message
				We initialize the chart with <chart></chart>
    --%>

	<fc:render chartId="${chart2Data.chartId}"
		swfFilename="${folderPath}${chart2Data.filename}${chart2Data.noDataParameter}"
		width="${chart2Data.width}" height="${chart2Data.height}"
		debugMode="false" registerWithJS="true" xmlData="${chart2Data.xml}" />
</tags:template2>