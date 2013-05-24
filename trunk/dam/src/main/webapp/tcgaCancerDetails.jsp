<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<%@include file="includes/page-variables.jspf"%>
<%
    layout =  "cancerdetails";
    h1String = "Cancer Details - " + request.getParameter("diseaseName") + ": Case Counts";

    String disease_abbreviation = request.getParameter("diseaseType");
    String disease_name = request.getParameter("diseaseName");
    metaKeywords = disease_abbreviation + "," + disease_name + "," + "tumor,Methylation,Gene Expression,miRNA Expression,SNP,case counts,tissue samples";
    metaDescription = "View the breakdown of cases and organ-specific controls for " + disease_name + "(" + disease_abbreviation + ") data.";

%>
<%@include file="/tcgaTemplate.jsp"%>