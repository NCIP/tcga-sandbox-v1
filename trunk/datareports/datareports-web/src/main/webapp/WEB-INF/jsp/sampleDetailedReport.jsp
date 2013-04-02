<%--
  ~ Software License, Version 1.0 Copyright 2013 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  --%>

<%@include file="/includes/page-variables.jspf" %>
<%
    //pagelevel overrides
    layout = "sampledetailed";
    bodyClass = "platform-design";
    h1String = "Sample Detailed for TCGA Data";
    metaKeywords = "cancer sample, cancer sample counts, tcga missing data, tcga missing, tcga ship dates, DCC received, tcga submitted samples";
    metaDescription = "Report of expected and DCC-received samples.";
%>
<%@include file="/tcgaTemplate.jsp" %>