<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2010 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    	<%@ include file="/includes/html-head.jspf"%>
	    <title><%=sectionTitle%><%=h1String%> - <%=pageTitle%></title>
	    <script type="text/javascript" src="/<%=rootDir%>/scripts/widgets/security.js"></script>
    	<script type="text/javascript" src="/<%=rootDir%>/scripts/merged.js?vers=<%=timestamp%>&js=annotations.js"></script>
		<script type="text/javascript">
		    Ext.onReady(function() {
		        Ext.QuickTips.init();
		        annotationApplicationRender();
		        tcga.annotations.security.updateUIWithUserNameFromServer();
		    });
		</script>
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
        	<% if (layout.equals("annotations")) { %>
           		<%@include file="/includes/pages/tcga-annotations.jspf" %>
           	<% } %>	
        </div>
        <%@include file="/includes/modules/footer.jspf" %>
    </body>
</html>
