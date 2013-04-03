<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.fusioncharts.com/jsp/core" prefix="fc" %> 
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="restaurantBean" class="com.fusioncharts.sampledata.RestaurantBean"/>
<c:set var="folderPath" value="../../FusionCharts/"/>
<c:set var="title" value="FusionCharts - Form Based Data Charting Example" scope="request"/>
<c:set var="header1" value="FusionCharts Form-Based Data Example" scope="request"/>
<c:set var="header2" value="Restaurant Sales Chart below" scope="request"/>
<c:set var="intro" value="Click on any pie slice to see slicing effect. Or,
		right click on chart and choose 'Enable Rotation', and then drag and
		rotate the chart."/>
<c:set var="jsPath" value="${folderPath}" scope="request"/>
<c:set var="assetCSSPath" value="../assets/ui/css/" scope="request"/>
<c:set var="assetJSPath" value="../assets/ui/js/" scope="request"/>
<c:set var="assetImagePath" value="../assets/ui/images/" scope="request"/>

<c:set target="${restaurantBean}" property="soups" value="${param.Soups}"/>	
<c:set target="${restaurantBean}" property="salads" value="${param.Salads}"/>
<c:set target="${restaurantBean}" property="sandwiches" value="${param.Sandwiches}"/>
<c:set target="${restaurantBean}" property="beverages" value="${param.Beverages}"/>
<c:set target="${restaurantBean}" property="desserts" value="${param.Desserts}"/>

<tags:template2> 
<fc:render chartId="${restaurantBean.chartId}" swfFilename="${folderPath}${restaurantBean.filename}" width="${restaurantBean.width}" height="${restaurantBean.height}" xmlData="${restaurantBean.xml}" />
<br/>
<a href='javascript:history.go(-1);'>Enter data again</a> <br/><br/>
</tags:template2>