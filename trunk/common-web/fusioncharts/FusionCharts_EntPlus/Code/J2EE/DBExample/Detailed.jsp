<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc"%>
<jsp:useBean id="factoryDetails" class="com.fusioncharts.sampledata.FactoryDetailsBean" />
<c:set var="folderPath" value="../../FusionCharts/" />
<c:set var="title" value="FusionCharts - Database + Drill-Down Example"	scope="request" />
<c:set var="header1" value="FusionCharts Database and Drill-Down Example" scope="request" />
<c:set var="header2" value="Detailed report for the factory" scope="request" />
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>

<c:set target="${factoryDetails}" property="factoryId" value="${param.factoryId}"/>	

      <%-- 
				This page is invoked from Default.jsp. When the user clicks on a pie
			slice in Default.jsp, the factory Id is passed to this page. We need
			to get that factory id, get the information from database and then show
			a detailed chart.
			--%>      

<tags:template2>
	<%-- Create the chart - Pie 3D Chart with data contained in bean --%>

	<fc:render chartId="${factoryDetails.chartId}"
		swfFilename="${folderPath}${factoryDetails.filename}"
		width="${factoryDetails.width}" height="${factoryDetails.height}"
		debugMode="false"  xmlData="${factoryDetails.xml}" />
	
</tags:template2>
