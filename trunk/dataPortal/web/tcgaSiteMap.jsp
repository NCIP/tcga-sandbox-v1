<jsp:include page="jspCommon/header.jsp" />

<jsp:include page="jspCommon/cssCommon.jsp" />
<jsp:include page="jspCommon/cssDataPortal.jsp" />

<jsp:include page="jspCommon/jsExt.jsp" />
<jsp:include page="jspCommon/jsCommonUtilities.jsp" />
	<script type="text/javascript" src="scripts/newsDisplay.js"></script>
	<script type="text/javascript" src="scripts/newsDisplayRightAnnouncements.js"></script> 

</head>
<body>

<jsp:include page="jspCommon/browserWarning.jsp" />

<jsp:include page="jspCommon/navMenu.jsp" />

<div class="bodyCentering">
	<div class="bodyContent">
		<div class="leftColumn">
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <span class="trailDest">Site Map</span></div>

			<div class="stdTitle">Data Portal Site Map</div>
			
			<div class="paragraph">
				<table class="siteMap">
					<tr>
						<td class="siteMapElement" rowspan=16><a href="tcgaHome2.jsp">Data Portal Home</a></td>
						<td class="siteMapElement">Cancer Detail Pages</td>
						<td class="siteMapEmpty"></td>
					</tr>
					<tr>
						<td class="siteMapElement"><a href="tcgaSearch.jsp">Query the Data</a></td>
						<td class="siteMapEmpty"></td>
					</tr>
					<tr>
						<td class="siteMapElement" rowspan=3><a href="tcgaDownload.jsp">Download Data</a></td>
						<td class="siteMapElement"><a href="/tcga/dataAccessMatrix.htm">Data Matrix</a></td>
					</tr>
					<tr>
						<td class="siteMapElement"><a href="/tcga/findArchives.htm">Bulk Download</a></td>
					</tr>
					<tr>
						<td class="siteMapElement"><a href="tcgaDownload.jsp">HTTP Directories</a></td>
					</tr>
					<tr>
						<td class="siteMapElement" rowspan=3><a href="tcgaTools.jsp">Tools</a></td>
						<td class="siteMapElement"><a href="tcgaAnalyticalTools.jsp">Analytical Tools</a></td>
					</tr>
					<tr>
						<td class="siteMapElement"><span class="annotationsLink"><span class="greyLink">Annotations Manager</span></span></td>
					</tr>
					<tr>
						<td class="siteMapElement"><span class="uuidLink"><span class="greyLink">UUID Manager</span></span></td>
					</tr>
					<tr>
						<td class="siteMapElement" rowspan=6><a href="tcgaAbout.jsp">About the Data</a></td>
						<td class="siteMapElement"><a href="tcgaDataType.jsp">Data Levels and Data Types</a></td>
					</tr>
					<tr>
						<td class="siteMapElement"><a href="tcgaPlatformDesign.jsp">Platform Design</a></td>
					</tr>
					<tr>
						<td class="siteMapElement"><a href="tcgaAccessTiers.jsp">Access Tiers</a></td>
					</tr>
					<tr>
						<td class="siteMapElement"><a href="/datareports/">Reports</a></td>
					</tr>
					<tr>
						<td class="siteMapElement"><a href="tcgaAnnouncements.jsp">Announcements</a></td>
					</tr>
					<tr>
						<td class="siteMapElement"><a href="tcgaHelp.jsp">User Guides and Help</a></td>
					</tr>
					<tr>
						<td class="siteMapElement"><a href="tcgaContact.jsp">Contact Us</a></td>
						<td class="siteMapEmpty"></td>
					</tr>
					<tr>
						<td class="siteMapElement"><a href="https://gforge.nci.nih.gov/tracker/?func=browse&group_id=265&atid=1843">Report a Problem</a> <div class="newWinIcon">&nbsp;</div></td>
						<td class="siteMapEmpty"></td>
					</tr>
					<!--
					<tr>
						<td class="siteMapElement"><a class="rssIcon" href="tcgaRss.jsp">RSS</a></td>
						<td class="siteMapEmpty"></td>
					</tr>
					-->
				</table>
			</div>
		</div>
		
		<div class="rightColumn">
			<jsp:include page="jspCommon/rightAnnouncements.jsp" />

			<jsp:include page="jspCommon/rightMoreTcgaInformation.jsp" />
		</div>		
</div>

<jsp:include page="jspCommon/footer.jsp" />
