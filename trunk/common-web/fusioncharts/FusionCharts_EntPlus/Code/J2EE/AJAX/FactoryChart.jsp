<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc"%>
<c:set var="folderPath" value="../../FusionCharts/" />
<jsp:useBean id="detailsBean"
	class="com.fusioncharts.sampledata.FactoryDetailsBean" />
<c:set target="${detailsBean}" property="factoryId" value="${param.FactoryId}"/>

	<%-- Create the chart - Column 2D Chart with data contained in bean --%>

	<fc:renderHTML chartId="${detailsBean.chartId}"
		swfFilename="${folderPath}${detailsBean.filename}"
		width="${detailsBean.width}" height="${detailsBean.height}"
		debugMode="false"  xmlData="${detailsBean.xml}" useSingleQuotes="${detailsBean.useSingleQuotes}"/>
		