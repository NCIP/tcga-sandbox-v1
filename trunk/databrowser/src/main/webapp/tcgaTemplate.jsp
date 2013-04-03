<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~ Software License, Version 1.0 Copyright 2010 SRA International, Inc.
~ Copyright Notice.  The software subject to this notice and license includes both human
~ readable source code form and machine readable, binary, object code form (the "caBIG
~ Software").
~
~ Please refer to the complete License text for full details at the root of the project.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
		<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
		<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta name='gwt:module' content='anomalysearch.AnomalySearch/anomalysearch.AnomalySearch'>
	    <script type="text/javascript" language="javascript" src="AnomalySearch/AnomalySearch.nocache.js"></script>
    	<%@ include file="/includes/html-head.jspf"%>

	    <title><%=sectionTitle%><%=h1String%> - <%=pageTitle%></title>
	    <script src="scripts/scripts.js" type="text/javascript" ></script>
	    <script type="text/javascript" src="<%=host%>/web/news/dbnews.js"></script>
	    <style><!--
	    	.databrowser a,.databrowser a:link,.databrowser a:visited,.databrowser a:active,.databrowser a.hand {color: #0c70a8;}
	    --></style>
  
    </head>
    <body<%=bodyOnLoadjs%> class="<%=bodyClass%> <%=pageCategory%> <%=layout%>">
        <%@include file="/includes/modules/browserWarning.jspf" %>
        <%@include file="/includes/modules/nci-banner.jspf" %>
        <%@include file="/includes/modules/masthead.jspf" %>
        <%@include file="/includes/modules/mainnav.jspf" %>
        <!-- Container for Content and Sidebar -->
        <div id="container">
        	<% if (layout.equals("databrowser")) { %>	
				<%@ include file="/includes/pages/tcga-anomaly-search.jspf" %>
			<% } %> 
        </div>
        <%@include file="/includes/modules/footer.jspf" %>
    </body>
</html>