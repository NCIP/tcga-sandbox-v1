<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
<%@include file="/includes/page-variables.jspf"%>
<%
    //page variables and overrides
    //pageCategory = "home";           //nav0
    //pageCategory = "query_data";     //nav1
    pageCategory = "download_data";  //nav2
    //pageCategory = "tools";          //nav3
    //pageCategory = "about_data";     //nav4
    layout =  "code-d";
    h1String = "Code Tables Report";
    jsOverride = "extensions/configurableLookTabPanel.js,extensions/clickablePanel.js,miframe.js,PanelResizer.js,LovCombo.js,codeTablesReport.js";
%>
<%@include file="/tcgaTemplate1.jsp"%>