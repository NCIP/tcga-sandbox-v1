
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2011 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
<%
    //page variables and overrides
    //pageCategory = "home";           //nav0
    //pageCategory = "query_data";     //nav1
    //pageCategory = "download_data";  //nav2
    //pageCategory = "tools";          //nav3
    pageCategory = "about_data";     //nav4
    rootDir = "tcgaproto";
    bodyClass = "platform-design";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
		<link rel="stylesheet" type="text/css" href="styles/ext-all.css" />
		<link rel="stylesheet" type="text/css" href="styles/ext-overrides.css" />	    
        <link rel="stylesheet" type="text/css" href="styles/xtheme-gray.css" />
	    <%@ include file="/includes/html-head-open.jspf"%>
	    <title><%=h1String%> - <%=pageTitle%></title>
<% if (metaKeywords.length()>0) {%>
		<meta name="keywords" content="<%=metaKeywords%>" /> 
<%}%>
<% if (metaDescription.length()>0) {%>
		<meta name="description" content="<%=metaDescription%>" />
<%}%>
	    
<% if (jsOverride.length() > 0) { %>
		<script  src="scripts/mergedjs.jsp?js=<%=jsOverride%>" type="text/javascript" ></script>
		
<% } %>

<% if (jsOverride.length() > 0) { %>
		<script src="scripts/detailPopup.js" type="text/javascript" ></script>
		
<% } %>
	    <%@include file="/includes/html-head-close.jspf"%>
    </head>

    <body<%=bodyOnLoadjs%> class="<%=bodyClass%> <%=pageCategory%> <%=layout%>">
        <%@include file="/includes/modules/browserWarning.jspf" %>
        <%@include file="/includes/modules/nci-banner.jspf" %>
        <%@include file="/includes/modules/masthead.jspf" %>
        <%@include file="/includes/modules/mainnav.jspf" %>

        <!-- Container for Content and Sidebar -->
        <div id="container">

<% if (layout.equals("test")) {%>
         <%@include file="/includes/pages/tcga-test.jspf" %>
<% } else if (layout.equals("test2")) {%>  
         <%@include file="/includes/pages/tcga-test2.jspf" %>  
<% } else if (layout.equals("test3")) {%>
         <%@include file="/includes/pages/tcga-test4.jspf" %>  
<% } else if (layout.equals("uuidproto")) {%>
         <%@include file="/includes/pages/tcga-uuidproto.jspf" %>  
<% } else {%>
            <!--error -->
<% } %>

        </div>
        <%@include file="/includes/modules/footer.jspf" %>
    </body>
</html>
