<%@ include file="/includes/tagsInclude.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  ~ Software License, Version 1.0 Copyright 2013 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  --%>

<%
    //page variables and overrides
    pageCategory = "about_data";     //nav4
    rootDir = "datareports";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/includes/html-head.jspf" %>
    <title><%=sectionTitle%><%=h1String%> - <%=pageTitle%>
    </title>
    <% if (layout.equals("drhome") || layout.equals("submissionreport")) { %>
    <script src="scripts/detailPopup.js" type="text/javascript"></script>
    <script type="text/javascript" src="/web/news/drnews.js"></script>
    <% } else if (layout.equals("statsdashboard")) { %>
    <script src="scripts/merged.js?vers=<%=timestamp%>&js=../Charts/FusionCharts.js ../Charts/FusionChartsExportComponent.js ../Charts/FusionCharts.HC.js ../Charts/FusionCharts.HC.Charts.js statsDashboard.js"
            type="text/javascript"></script>

    <% } else { %>
    <script src="scripts/merged.js?vers=<%=timestamp%>&js=PanelResizer.js ColumnHeaderGroup.js LovCombo.js datareportExtJsOverride.js reportToolbar.js scripts.js"
            type="text/javascript"></script>
    <% } %>
    <% if (layout.equals("aliquotIdBreakdown")) { %>
    <script src="scripts/aliquotIdBreakdownReport.js" type="text/javascript"></script>

    <% } else if (layout.equals("aliquotReport")) { %>
    <script src="scripts/aliquotReport.js" type="text/javascript"></script>

    <% } else if (layout.equals("bamtReport")) { %>
    <script src="scripts/bamTelemetryReport.js" type="text/javascript"></script>

    <% } else if (layout.equals("bcrpipeline")) { %>
    <script src="scripts/merged.js?vers=<%=timestamp%>&js=thirdParty/raphael/raphael.js graphWidgets.js graphListeners.js graphDraw.js BCRPipeLineReport.js"
            type="text/javascript"></script>

    <% } else if (layout.equals("samplesummary")) { %>
    <script src="scripts/sampleSummaryReport.js" type="text/javascript"></script>

    <% } else if (layout.equals("codetables")) { %>
    <script src="scripts/codeTablesReport.js" type="text/javascript"></script>

    <% } else if (layout.equals("latestarchive")) { %>
    <script src="scripts/latestArchiveReport.js" type="text/javascript"></script>

    <% } else if (layout.equals("shipped-items-pending-bcr-data-submission")) { %>
    <script src="scripts/pendingUUIDReport.js" type="text/javascript"></script>

    <% } else if (layout.equals("projectcase")) { %>
    <script src="scripts/merged.js?vers=<%=timestamp%>&js=ProgressColumn4ProjectCaseDashboard.js projectCaseDashboard.js"
            type="text/javascript"></script>
    <% } %>
    <div style="position:absolute;">
        <a href="#skip">
            <img src="/tcgafiles/ftp_auth/distro_ftpusers/anonymous/images/skipnav.gif" border="0" height="1" width="1" alt="Skip Navigation"
                 title="Skip Navigation"/>
        </a>
    </div>
</head>

<body<%=bodyOnLoadjs%> class="<%=bodyClass%> datareports <%=pageCategory%> <%=layout%>">

<%@include file="/includes/modules/browserWarning.jspf" %>
<%@include file="/includes/modules/nci-banner.jspf" %>
<%@include file="/includes/modules/masthead.jspf" %>
<%@include file="/includes/modules/mainnav.jspf" %>

<!-- Container for Content and Sidebar -->
<div id="container">

    <% if (layout.equals("drhome")) {%>
    <%@include file="/includes/pages/tcga-datareports-home.jspf" %>
    <% } else if (layout.equals("statsdashboard")) { %>
    <%@include file="/includes/pages/tcga-statsdashboard.jspf" %>
    <% } else if (layout.equals("aliquotIdBreakdown")) { %>
    <%@include file="/includes/pages/tcga-aliquot-breakdown-report.jspf" %>
    <% } else if (layout.equals("aliquotReport")) { %>
    <%@include file="/includes/pages/tcga-aliquot-report.jspf" %>
    <% } else if (layout.equals("bamtReport")) { %>
    <%@include file="/includes/pages/tcga-bam-telemetry-report.jspf" %>
    <% } else if (layout.equals("bcrpipeline")) { %>
    <%@include file="/includes/pages/tcga-bcr-pipeline-report.jspf" %>
    <% } else if (layout.equals("submissionreport")) { %>
    <%@include file="/includes/pages/tcga-submission-report.jspf" %>
    <% } else if (layout.equals("samplesummary")) { %>
    <%@include file="/includes/pages/tcga-sample-summary-report.jspf" %>
    <% } else if (layout.equals("sampledetailed")) { %>
    <%@include file="/includes/pages/tcga-sample-detailed-report.jspf" %>
    <% } else if (layout.equals("codetables")) { %>
    <%@include file="/includes/pages/tcga-code-tables-report.jspf" %>
    <% } else if (layout.equals("latestarchive")) { %>
    <%@include file="/includes/pages/tcga-latest-archive-report.jspf" %>
    <% } else if (layout.equals("shipped-items-pending-bcr-data-submission")) { %>
    <%@include file="/includes/pages/tcga-shipped-items-pending-bcr-data-submission.jspf" %>
    <% } else if (layout.equals("projectcase")) { %>
    <%@include file="/includes/pages/tcga-project-case-dashboard.jspf" %>
    <% } else if (layout.equals("cacheadmin")) { %>
    <%@include file="/includes/pages/tcga-cacheadmin.jspf" %>
    <% } %>

</div>
<%@include file="/includes/modules/footer.jspf" %>
</body>
</html>
