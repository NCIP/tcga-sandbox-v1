<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %><%@ taglib uri="http://www.fusioncharts.com/jsp/functions" prefix="fchelper"%><c:set var="utfBOMString" value="${fchelper:getUTFBOMString()}"/><c:out value="${utfBOMString}" escapeXml="false"/>
<sql:setDataSource dataSource="jdbc/FactoryDB"/>	
<c:set var="strXML" value="<chart caption='工場出力レポート' subCaption='量で' decimals='0' showLabels='1' numberSuffix=' Units' pieSliceDepth='30' formatNumberScale='0'>"/><c:set var="strQuery" value="select * from Japanese_Factory_Master"/>	
<sql:query var="entries"  sql="${strQuery}">	
</sql:query>	
<c:forEach var="row" items="${entries.rows}">		
<c:set var="factoryId" value="${row.FactoryId}"/>	
<c:set var="factoryName" value="${row.FactoryName}"/>	
<c:set var="strQuery2" value="select sum(Quantity) as TotOutput from Factory_Output where FactoryId=${factoryId}"/>	
<sql:query var="factoryDetailEntries"  sql="${strQuery2}">	</sql:query>	
<c:forEach var="detailRow" items="${factoryDetailEntries.rows}">	
<c:set var="totalOutput" value="${detailRow.TotOutput}"/>	
<c:set var="setElem" value="<set label='${factoryName}' value='${totalOutput}' />"/>	
<c:set var="strXML" value="${strXML}${setElem} "/>	
</c:forEach>	
</c:forEach>	
<c:set var="strXML" value="${strXML}</chart> "/>	
<c:set target="${pageContext.response}" property="contentType" value="text/xml; charset=UTF-8"/>
<c:out value="${strXML}" escapeXml="false"/>	
<% 	/*
	Instead of including DBConn.jsp, we will use the DBConnection class present in the applciation context
	as a Bean and use its method to get the Database connection.
	We use the Constants class to compare with the string for dbname got from web.xml.
	This can then be used to write query specific to a database.
	Also, we need to import the Connection class from java.sql.
	
	*/
	/*This page generates the XML data for the Pie Chart contained in JapaneseDBExample.jsp. 	
	
	For the sake of ease, we've used the same database as used by other examples. 
	We have added one more table Japanese_Factory_Master with stores the names of the factory in Japanese language.
	
	Steps to ensure UTF8 xml output for FusionCharts:
		1. Output the BOM bytes 0xef 0xbb 0xbf as shown above in the first few lines
		2. Declare contentType to be text/xml, charSet and pageEncoding to be UTF-8
		3. Use getBytes to get the data from UTF field in the database and to convert it into String, use new String(bytes,"UTF-8")
	Do not output anything other than the BOM, xml declaration and the xml itself. (no empty lines too!)
*/
%>


