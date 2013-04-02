<%--
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  --%>
<%@ include file="tagsInclude.jsp" %>
<%@ include file="/includes/page-variables.jspf"%>
<%
    pageCategory = "download_data";  //nav2
    bodyClass =  "platform-design dampage"; //override class
    layout = "damdownload";
    rootDir = "tcga";
    h1String = "Data Download";
    sectionTitle = "TCGA Data Matrix: ";
%>
<%@include file="/tcgaTemplate.jsp"%>