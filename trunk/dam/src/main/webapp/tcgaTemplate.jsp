<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2011 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
<% rootDir = "tcga"; %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    <%@ include file="/includes/html-head.jspf"%>

	<%if (layout.equals("tcgahome")||layout.equals("site-map")||layout.equals("help")||layout.equals("contact")) {%>
	    <script type="text/javascript" src="/web/news/home-text.js"></script>
	    <script type="text/javascript" src="/<%=rootDir%>/scripts/merged.js?vers=<%=timestamp%>&js=newsDisplay.js newsDisplayRightAnnouncements.js homePageTable.js"></script>
    <% } else if (layout.equals("cancerdetails")) {%>
        <script type="text/javascript" src="/web/news/home-text.js"></script>
		<script type="text/javascript" src="/<%=rootDir%>/scripts/merged.js?vers=<%=timestamp%>&js=../Charts/FusionCharts.js ../Charts/FusionCharts.HC.js ../Charts/FusionCharts.HC.Charts.js newsDisplay.js newsDisplayRightAnnouncements.js detailsPageTable.js detailsPageChart.js"></script>
	<% } else if (layout.equals("platform-design")) {%>
    	<script type="text/javascript" src="/<%=rootDir%>/scripts/platforms.js"></script>
	<% } else if (layout.equals("announcements")) {%>
    	<script type="text/javascript" src="/<%=rootDir%>/scripts/merged.js?vers=<%=timestamp%>&js=newsDisplay.js news.js"></script>
	<% } else if (layout.equals("datatype")) {%>
          <script type="text/javascript" src="/<%=rootDir%>/scripts/dataTypesDisplay.js"></script>
    <%}%>

	<title><%=sectionTitle%><%=h1String%> - <%=pageTitle%></title>
    <div style="position:absolute;">
        <a href="#skip">
            <img src="/tcgafiles/ftp_auth/distro_ftpusers/anonymous/images/skipnav.gif" border="0" height="1" width="1" alt="Skip Navigation" title="Skip Navigation" />
        </a>
    </div>
    </head>
    <body<%=bodyOnLoadjs%> class="<%=bodyClass%> <%=pageCategory%> <%=layout%>">

    <%@include file="/includes/modules/browserWarning.jspf" %>
    <%@include file="/includes/modules/nci-banner.jspf" %>
    <%@include file="/includes/modules/masthead.jspf" %>
    <%@include file="/includes/modules/mainnav.jspf" %>
    <!-- Container for Content and Sidebar -->
    <div id="container">

<%
    if (layout.equals("rtools")) {%>
           <%@include file="/includes/pages/tcga-tools.jspf" %>
<% } else if (layout.equals("tcgahome")) {%>
           <%@include file="/includes/pages/tcga-home.jspf" %>
<% } else if (layout.equals("atools")) {%>
           <%@include file="/includes/pages/tcga-atools.jspf" %>
<% } else if (layout.equals("ddata")) {%>
           <%@include file="/includes/pages/tcga-download-data.jspf" %>
<% } else if (layout.equals("mutations")) {%>
           <%@include file="/includes/pages/tcga-mutations-data.jspf" %>
<% } else if (layout.equals("help")) {%>
           <%@include file="/includes/pages/tcga-help.jspf" %>
<% } else if (layout.equals("site-map")) {%>
           <%@include file="/includes/pages/tcga-site-map.jspf" %>
<% } else if (layout.equals("contact")) {%>
           <%@include file="/includes/pages/tcga-contact.jspf" %>
<% } else if (layout.equals("cancerdetails")) {%>
           <%@include file="/includes/pages/tcga-cancer-details.jspf" %>
<% } else if (layout.equals("announcements")) {%>
           <%@include file="/includes/pages/tcga-announcements.jspf" %>
<% } else if (layout.equals("adata")) {%>
           <%@include file="/includes/pages/tcga-about-data.jspf" %>
<% } else if (layout.equals("datatype")) {%>
           <%@include file="/includes/pages/tcga-data-type.jspf" %>
<% } else if (layout.equals("platform-design")) {%>
           <%@include file="/includes/pages/tcga-platform-design.jspf" %>
<% } else if (layout.equals("access-tiers")) {%>
           <%@include file="/includes/pages/tcga-access-tiers.jspf" %>
<% } else if (layout.equals("queuemonitor")) {%>
           <%@include file="/includes/pages/tcga-queue-monitor.jspf" %>
<% } else if (layout.equals("sessionError")) {%>
           <%@include file="/includes/pages/tcga-dam-damSessionError.jspf" %>
<% } else if (layout.equals("findfiles")) {%>
           <%@include file="/includes/pages/tcga-dam-findFiles.jspf" %>
<% } else if (layout.equals("archiveresults")) {%>
           <%@include file="/includes/pages/tcga-dam-archiveresults.jspf" %>
<% } else if (layout.equals("damdownload")) {%>
		   <%@include file="/includes/pages/tcga-dam-download.jspf" %>
<% } else if (layout.equals("damfileloading")) {%>
		   <%@include file="/includes/pages/tcga-dam-fileloading.jspf" %>
<% } else if (layout.equals("dam")) {%>
           <%@include file="/includes/pages/tcga-dam.jspf" %>
<% } else if (layout.equals("damerror")) {%>
		   <%@ include file="/includes/pages/tcga-dam-damerror.jspf" %>
<% } else if (layout.equals("damshowfiles")) {%>
		   <%@ include file="/includes/pages/tcga-dam-showfiles.jspf" %>
<% } else if (layout.equals("findarchivesform")) {%>
		   <%@ include file="/includes/pages/tcga-dam-findarchives.jspf" %>
<% } else if (layout.equals("archivelist")) {%>
		   <%@ include file="/includes/pages/tcga-archivelist.jspf" %>
<% } else if (layout.equals("downloadStatus")) {%>
		   <%@ include file="/includes/pages/tcga-dam-damstatus.jspf" %>
<% } else if (layout.equals("admin-main")) {%>
<% } else if (layout.equals("admin")) {%>
<% } %>
        </div>
        <%@include file="/includes/modules/footer.jspf" %>
    </body>
</html>
