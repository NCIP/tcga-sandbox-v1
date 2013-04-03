<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="detailsBean"
	class="com.fusioncharts.sampledata.FactoryDetailsBean" />

<c:set target="${pageContext.response}" property="contentType" value="text/xml"/>
<c:set target="${detailsBean}" property="factoryId" value="${param.factoryId}"/>
<c:out value="${detailsBean.xml}" escapeXml="false"/>