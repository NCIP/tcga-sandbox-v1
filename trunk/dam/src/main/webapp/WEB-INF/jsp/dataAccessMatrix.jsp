<%--
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  --%>
<%@ include file="tagsInclude.jsp" %>
<%@include file="/includes/page-variables.jspf"%>
<%
    pageCategory = "download_data";  //nav2
    
    if((request.getParameter("showMatrix") != null) &&
       (request.getParameter("showMatrix").equals("true"))) {
        bodyClass =  "platform-design dampage showmatrix"; //override class
    } else {
        bodyClass =  "platform-design dampage"; //override class
    }
    
    layout = "dam";
    rootDir = "tcga";
    h1String = "Data Matrix";
    bodyOnLoadjs = " onload=\"pageLoad()\"";
    metaKeywords = "Data Matrix, data filter,data batch, center, platform, data type, tumor samples, tissue data archives";
	metaDescription = "Search, build and download data using the Data Matrix.";
%>
<%@include file="/tcgaTemplate.jsp"%>
