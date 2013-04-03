<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc"%>
<jsp:useBean id="factoriesBean"
	class="com.fusioncharts.sampledata.FactoriesWithLinkToDetailedBean" />
<c:set var="folderPath" value="../../FusionCharts/" />
<c:set var="title" value="FusionCharts Database + Drill-Down Example"	scope="request" />
<c:set var="header1" value="JSP Database Examples" scope="request" />
<c:set var="header2"
	value="Click on any pie slice to see detailed data." scope="request" />
<c:set var="intro" value="Or, right click on any pie to enable slicing or rotation mode." scope="request" />
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>

      <%-- 
				In this example, we show how to connect FusionCharts to a database.
	For the sake of ease, we've used mySQL database. It just contains two tables, which are linked to each
	other.
			--%>      
<tags:template2>

	<%-- Create the chart - Pie 3D Chart with data contained in bean --%>

	<fc:render chartId="${factoriesBean.chartId}"
		swfFilename="${folderPath}${factoriesBean.filename}"
		width="${factoriesBean.width}" height="${factoriesBean.height}"
		debugMode="false"  xmlData="${factoriesBean.xml}" />
	
</tags:template2>