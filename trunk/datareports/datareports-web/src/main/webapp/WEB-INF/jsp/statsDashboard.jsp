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
	//pagelevel overrides
	layout =  "statsdashboard";
	bodyClass = "platform-design"; 
	h1String = "Data Statistics Dashboard";
    metaKeywords = "Data statistics dashboard, tcga most requested,most requested platforms,most requested batches,most requested access type,total archives,total archives received, total archives downloaded";
	metaDescription = "Visual breakdown of user statistics and available TCGA data.";
%>
<%@include file="/tcgaTemplate.jsp"%>