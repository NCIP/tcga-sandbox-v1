<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2011 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
<%@include file="/includes/page-variables.jspf"%>
<%
	h1String = "Data Browser";
    pageCategory = "query_data";     //nav1
    layout = "databrowser";
    bodyClass = "platform-design";
    rootDir = "tcga-portal";
    metaKeywords = "data browser, anomaly, gene, participant, pathway, correlation, mutations, CGWB, Cancer Genome Workbench";
	metaDescription = "The Data Browser allows you to discover possible copy number, gene expression, methylation and correlation anomalies for a set of genes, participants or known pathways.";
%>
<%@ include file="/tcgaTemplate.jsp"%>
