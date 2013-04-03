<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc"%>
<jsp:useBean id="factoriesBean"
	class="com.fusioncharts.sampledata.MSFactoriesData" />
<c:set var="folderPath" value="../../FusionCharts/" />
<c:set var="title" value="FusionCharts - Multiseries chart using data from database"	scope="request" />
<c:set var="header1" value="JSP Database Examples"	scope="request" />
<c:set var="header2" value="Output of various factories."	scope="request" />
<c:set var="intro" value="This is very simple implementation using a simple database. Complexity of real implementataion can vary as per database structure."	scope="request" />

<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>
	
<tags:template2> 
	<%-- In this example, we plot a single series chart from data contained
	in an array. The array will have two columns - first one for data label
	and the next one for data values.
	
    --%>
 <%-- Create the chart - Pie 3D Chart with data contained in bean --%>

<fc:render chartId="${factoriesBean.chartId}" swfFilename="${folderPath}${factoriesBean.filename}" width="${factoriesBean.width}" height="${factoriesBean.height}" debugMode="false" registerWithJS="false" xmlData="${factoriesBean.xml}" />
</tags:template2>