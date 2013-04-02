
<%@include file="/includes/page-variables.jspf"%>
<% rootDir = "tcga"; %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    <%@ include file="/includes/html-head.jspf"%>  	
	<title><%=sectionTitle%><%=h1String%> - <%=pageTitle%></title>        
	
	<%if (layout.equals("tcgahome")||layout.equals("site-map")) {%>
	    <script type="text/javascript" src="/<%=rootDir%>/scripts/merged.js?vers=<%=timestamp%>&js=newsDisplay.js,newsDisplayRightAnnouncements.js,homePageTable.js"></script>	  
    <% } else if (layout.equals("cancerdetails")) {%>
		<script type="text/javascript" src="/<%=rootDir%>/scripts/merged.js?vers=<%=timestamp%>&js=thirdParty/raphael/raphael.js,extensions/drawingUtils.js,extensions/raphaelShapes.js,extensions/raphaelDepth.js,newsDisplay.js,newsDisplayRightAnnouncements.js,detailsPageTable.js,detailsPageChart.js"></script>  
	<% } else if (layout.equals("platform-design")) {%>
    	<script type="text/javascript" src="/<%=rootDir%>/scripts/platforms.js"></script>
	<% } else if (layout.equals("announcements")) {%>
    	<script type="text/javascript" src="/<%=rootDir%>/scripts/merged.js?vers=<%=timestamp%>&js=newsDisplay.js,news.js"></script>   
	<%}%>
	
    </head>
    <body<%=bodyOnLoadjs%> class="<%=bodyClass%> <%=pageCategory%> <%=layout%>">
        <%@include file="/includes/modules/browserWarning.jspf" %>
        <%@include file="/includes/modules/nci-banner.jspf" %>
        <%@include file="/includes/modules/masthead.jspf" %>
        <%@include file="/includes/modules/mainnav.jspf" %>
        <!-- Container for Content and Sidebar -->
        <div id="container">
			<%        if (layout.equals("rtools")) {%>
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
			<% } else if (layout.equals("faqs")) {%>
			           <%@include file="/includes/pages/tcga-faqs.jspf" %>
			<% } else if (layout.equals("portalhelp")) {%>
			           <%@include file="/includes/pages/tcga-portal-help.jspf" %>
			<% } else if (layout.equals("portaladditionalhelp")) {%>
			           <%@include file="/includes/pages/tcga-portal-additional-help.jspf" %>
			<% } else if (layout.equals("citedata")) {%>
			           <%@include file="/includes/pages/tcga-cite-data.jspf" %>
			<% } else if (layout.equals("searcharchivesguide")) {%>
			           <%@include file="/includes/pages/tcga-search-archives-guide.jspf" %>
			<% } else if (layout.equals("admin-main")) {%>
			           <%@include file="/includes/pages/tcga-admin-main.jspf" %>
			<% } else if (layout.equals("admin-meta-data-browser")) {%>
			           <%@include file="/includes/pages/tcga-admin-meta-data-browser.jspf" %>
			<% } else if (layout.equals("admin-annotations")) {%>
			           <%@include file="/includes/pages/tcga-admin-annotations.jspf" %>
			<% } else if (layout.equals("admin-data-access")) {%>
			           <%@include file="/includes/pages/tcga-admin-data-access.jspf" %>
			<% } else if (layout.equals("admin-data-portal")) {%>
			           <%@include file="/includes/pages/tcga-admin-data-portal.jspf" %>
			<% } else if (layout.equals("admin-data-reports")) {%>
			           <%@include file="/includes/pages/tcga-admin-data-reports.jspf" %>
			<% } %>
        </div>
        <%@include file="/includes/modules/footer.jspf" %>
    </body>
</html>

