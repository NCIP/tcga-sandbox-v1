<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<jsp:useBean id="factoriesBean"
	class="com.fusioncharts.sampledata.FactoriesBean" />
<c:set var="folderPath" value="../../FusionCharts/" />
<c:set var="title" value="FusionCharts - Database Example"
	scope="request" />
<c:set var="header1" value="JSP Database Examples"
	scope="request" />
<c:set var="jsPath" value="${folderPath}" scope="request" />
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>

<sql:setDataSource dataSource="jdbc/FactoryDB" />

<c:set var="strXML"
	value="<chart caption='Factory Output report' subCaption='By Quantity' pieSliceDepth='30' showBorder='1' formatNumberScale='0' numberSuffix=' Units' animation='${animateChart}'>" />
<c:set var="strQuery" value="select * from Factory_Master" />
<sql:query var="entries" sql="${strQuery}">
</sql:query>
<c:forEach var="row" items="${entries.rows}">

	<c:set var="factoryId" value="${row.FactoryId}" />
	<c:set var="factoryName" value="${row.FactoryName}" />
	<c:set var="strQuery2"
		value="select sum(Quantity) as TotOutput from Factory_Output where FactoryId=${factoryId}" />
	<sql:query var="factoryDetailEntries" sql="${strQuery2}">
	</sql:query>
	<c:forEach var="detailRow" items="${factoryDetailEntries.rows}">
		<c:set var="totalOutput" value="${detailRow.TotOutput}" />
		<c:set var="setElem"
			value="<set label='${factoryName}' value='${totalOutput}' />" />
		<c:set var="strXML" value="${strXML}${setElem} " />
	</c:forEach>
</c:forEach>
<c:set var="strXML" value="${strXML}</chart> " />

<tags:template2>
	<%-- In this example, we plot a single series chart from data contained
	in an array. The array will have two columns - first one for data label
	and the next one for data values.
	
    --%>
	<%-- Create the chart - Pie 3D Chart with data contained in bean --%>

	<fc:render chartId="${factoriesBean.chartId}"
		swfFilename="${folderPath}${factoriesBean.filename}"
		width="${factoriesBean.width}" height="${factoriesBean.height}"
		debugMode="false" registerWithJS="false" xmlData="${strXML}" />
</tags:template2>
