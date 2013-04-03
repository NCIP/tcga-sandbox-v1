<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2010 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
<%@ include file="/includes/page-variables.jspf"%>
<%
 	String reDirectText = "";
    pageCategory = "tools";          //nav3
    layout = "uuid";
    bodyClass = "platform-design";
    if (h1String.length() == 0) {
    	h1String = "UUID Manager";
    }
    rootDir = "uuid";
%>
<%@ include file="/tcgaTemplate.jsp"%>
