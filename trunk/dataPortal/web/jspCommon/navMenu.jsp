<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<%! String homePageMenuItems = new String("/tcgaHome2.jsp /tcgaContact.jsp /tcgaRss.jsp /tcgaCancerDetails.jsp /tcgaSiteMap.jsp"); %>
<%! String queryTheDataMenuItems = new String("/tcga-portal"); %>
<%! String downloadDataMenuItems = new String("/tcgaDownload.jsp /WEB-INF/jsp/dataAccessMatrix.jsp /WEB-INF/jsp/dataAccessDownload.jsp /WEB-INF/jsp/dataAccessFileProcessing.jsp /WEB-INF/jsp/findArchivesForm.jsp /WEB-INF/jsp/showFiles.jsp"); %>
<%! String toolsMenuItems = new String("/tcgaTools.jsp  /tcgaAnalyticalTools.jsp  /annotations /uuid"); %>
<%! String aboutTheDataMenuItems = new String("/tcgaAbout.jsp /tcgaDataType.jsp /tcgaPlatformDesign.jsp /tcgaAccessTiers.jsp /datareports /tcgaAnnouncements.jsp /tcgaHelp.jsp"); %>
<%
    String secureHost = new String("https://" + request.getServerName());
    String host = new String("http://" + request.getServerName());
    String dbHost = new String("http://" + request.getServerName().replace("data", "portal"));
    boolean uuid = false;
    boolean annotations = false;
    boolean dataReports = false;
    boolean dataBrowser = false;
    boolean download = false;
%>

<script type="text/javascript">
    var tcgaHost='<% out.print(host); %>';
    var tcgaSecureHost='<% out.print(secureHost); %>';
    var tcgaDbHost='<% out.print(dbHost); %>';
</script>

<div id="navContainer">
    <div id="topHeader" align="center">
        <img src="images/header/top_header.gif" alt="" usemap="#topHeaderMapName" class="navImages">
    </div>
	
	 <map id="topHeaderMapId" name="topHeaderMapName">
	    <area shape="rect" coords="6,0,223,27" href="http://www.cancer.gov/" target="_blank" alt="National Cancer Institute">
	    <area shape="rect" coords="519,2,794,28" href="http://www.genome.gov/" target="_blank" alt="National Human Genome Research Institute">
 	 </map>
	
    <div id="header" align="center">
		<img src="images/header/header_3single.gif" alt="" usemap="#header" class="navImages">
		<div id="extraNavLinks" style="text-align: right;">
			<a class="whiteLink" href="<% out.print(host); %>/tcga/tcgaContact.jsp">Contact Us</a> <!-- |
			<a class="rssIcon whiteLink" href="/tcga/tcgaRss.jsp">RSS</a> -->
		</div>
    </div>
    <div id="nav">
         <!-- Start Navigation Menu -->
         <div class="navlink left
			<%
				if (homePageMenuItems.contains(request.getServletPath())) {
					out.print(" navselected");
				}
			%>
			"><a href="<% out.print(host); %>/tcga/tcgaHome2.jsp">Home</a></div>
			
			<div class="navlink
			<%
				if (queryTheDataMenuItems.contains(request.getServletPath()) || dataBrowser) {
					out.print(" navselected");
				}
			%>
			"><a href="<% out.print(dbHost); %>/tcga-portal/">Query the Data</a></div>

         <div class="navlink
			<%
				if (downloadDataMenuItems.contains(request.getServletPath()) || download) {
					out.print(" navselected ");
				}
			%>
			hoverMenu">
			   <a href="<% out.print(host); %>/tcga/tcgaDownload.jsp">Download Data</a>
			   <div class="subNavMenu">
			   	<table>
				     <tr><td><a href="<% out.print(host); %>/tcga/tcgaDownload.jsp">Download Data</a></td></tr>
				     <tr><td><a href="<% out.print(host); %>/tcga/dataAccessMatrix.htm">Data Matrix</a></td></tr>
				     <tr><td><a href="<% out.print(host); %>/tcga/findArchives.htm">Bulk Download</a></td></tr>
				     <tr><td><a href="<% out.print(host); %>/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/"target="newWin">Open-Access HTTP Directory</a><div class="newWinIcon">&nbsp;</div></td></tr>
				     <tr><td><a href="<% out.print(secureHost); %>/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/"target="newWin">Controlled-Access HTTP Directory</a><div class="newWinIcon">&nbsp;</div></td></tr>
			   	</table>
			   </div>
			</div>
			
         <div class="navlink
			<%
				if (toolsMenuItems.contains(request.getServletPath()) || uuid || annotations) {
					out.print(" navselected ");
				}
			%>
			hoverMenu">
         	<a href="<% out.print(host); %>/tcga/tcgaTools.jsp">Tools</a>
			   <div class="subNavMenu">
			   	<table>
				     <tr><td><a href="<% out.print(host); %>/tcga/tcgaTools.jsp">Tools</a></td></tr>
				     <tr><td><a href="<% out.print(host); %>/tcga/tcgaAnalyticalTools.jsp">Analytical Tools</a></td></tr>
				     <tr><td class="annotationsLink"><b class="greyLink">Annotations Manager</b></td></tr>
				     <tr><td class="uuidLink"><b class="greyLink">UUID Manager</b></td></tr>
			   	</table>
			   </div>
			</div>

         <div class="navlink right
			<%
				if (aboutTheDataMenuItems.contains(request.getServletPath()) || dataReports) {
					out.print(" navselected ");
				}
			%>
			hoverMenu">
			   <a href="<% out.print(host); %>/tcga/tcgaAbout.jsp">About the Data</a>
			   <div class="subNavMenu">
			   	<table>
				     <tr><td><a href="<% out.print(host); %>/tcga/tcgaAbout.jsp">About the Data</a></td></tr>
				     <tr><td><a href="<% out.print(host); %>/tcga/tcgaDataType.jsp">Data Levels and Data Types</a></td></tr>
				     <tr><td><a href="<% out.print(host); %>/tcga/tcgaPlatformDesign.jsp">Platform Design</a></td></tr>
				     <tr><td><a href="<% out.print(host); %>/tcga/tcgaAccessTiers.jsp">Access Tiers</a></td></tr>
				     <tr><td><a href="<% out.print(host); %>/datareports/">Reports</a></td></tr>
				     <tr><td><a href="<% out.print(host); %>/tcga/tcgaAnnouncements.jsp">Announcements</a></td></tr>
				     <tr><td><a href="<% out.print(host); %>/tcga/tcgaHelp.jsp">User Guides and Help</a></td></tr>
			   	</table>
			   </div>
			</div>
         <!-- End Navigation Menu -->
    </div>
</div>
