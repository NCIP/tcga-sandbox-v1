<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<sql:setDataSource dataSource="jdbc/FactoryDB"/>

	<c:set var="animateChart" value="${param.animate}"/>
	<c:set var="strXML" value="<chart caption='Factory Output report' subCaption='By Quantity' pieSliceDepth='30' showBorder='1' formatNumberScale='0' numberSuffix=' Units' animation='${animateChart}'>"/>
	<c:set var="strQuery" value="select * from Factory_Master"/>
	<sql:query var="entries"  sql="${strQuery}">
	</sql:query>
	<c:forEach var="row" items="${entries.rows}">
	
	<c:set var="factoryId" value="${row.FactoryId}"/>
	<c:set var="factoryName" value="${row.FactoryName}"/>
	<c:set var="strQuery2" value="select sum(Quantity) as TotOutput from Factory_Output where FactoryId=${factoryId}"/>
	<sql:query var="factoryDetailEntries"  sql="${strQuery2}">
	</sql:query>
	<c:forEach var="detailRow" items="${factoryDetailEntries.rows}">
	<c:set var="totalOutput" value="${detailRow.TotOutput}"/>
	<c:set var="setElem" value="<set label='${factoryName}' value='${totalOutput}' />"/>
	<c:set var="strXML" value="${strXML}${setElem} "/>
	</c:forEach>
	</c:forEach>
	<c:set var="strXML" value="${strXML}</chart> "/>
	<c:set target="${pageContext.response}" property="contentType" value="text/xml"/>
	<c:out value="${strXML}" escapeXml="false"/>
	