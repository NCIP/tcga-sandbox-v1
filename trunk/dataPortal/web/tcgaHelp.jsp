<jsp:include page="jspCommon/header.jsp" />

<jsp:include page="jspCommon/cssCommon.jsp" />
<jsp:include page="jspCommon/cssDataPortal.jsp" />

<jsp:include page="jspCommon/jsExt.jsp" />
<jsp:include page="jspCommon/jsCommonUtilities.jsp" />

</head>
<body>

<jsp:include page="jspCommon/browserWarning.jsp" />

<jsp:include page="jspCommon/navMenu.jsp" />

<div class="bodyCentering">
	<div class="bodyContent">
		<div class="leftColumn">
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaAbout.jsp">About the Data</a> > <span class="trailDest">User Guides and Help</span></div>

			<div class="stdTitle">User Guides and Help</div>
			
			<div class="paragraph">
				<b>User Guides</b>
			</div>
			
			<div class="paragraph">
				<ul class="triangle">
					<li><a target="newWin" href="http://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/docs/tcga_DataBrowser_UserGuide.pdf">Query the Data User's Guide</a> <div class="pdfIcon">&nbsp;</div> <div class="newWinIcon">&nbsp;</div><br/>Describes how to perform a search for cancer genes submitted to The Cancer Genome Atlas and for patients and biological pathways associated with those genes.</li>
					<li><a target="newWin" href="http://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/docs/tcga_DataAccessMatrix_UserGuide.pdf">Data Matrix User's Guide</a> <div class="pdfIcon">&nbsp;</div> <div class="newWinIcon">&nbsp;</div><br/>This "search by sample" feature allows users to select results of individual samples from multiple centers and platforms and create a customized archive of the selected data.</li>
					<li><a target="newWin" href="http://cancergenome.nih.gov/objects/pdfs/Data_Portal_Help.pdf">Bulk Download User's Guide</a> <div class="pdfIcon">&nbsp;</div> <div class="newWinIcon">&nbsp;</div><br/>Explains how to search for and download complete data archives as submitted by TCGA Research Centers.</li>
					<li><a target="newWin" href="http://tcga-data.nci.nih.gov/docs/TCGA_Data_Primer.pdf">TCGA Data Guide</a> <div class="pdfIcon">&nbsp;</div> <div class="newWinIcon">&nbsp;</div><br/>A detailed guide to understanding TCGA Data</li>
				</ul>
			</div>

			<div class="paragraph">
				<b>Help for Research Centers Providing TCGA Data</b>
			</div>
			
			<div class="paragraph">
				<ul class="triangle">
					<li><a href="https://gforge.nci.nih.gov/docman/view.php/265/5004/Data_Preparation_and_Transfer_SOP.zip" target="newWin">Standard Operating Procedures</a> <div class="newWinIcon">&nbsp;</div> (SOP) for Preparation and Transfer of Data to the TCGA Data Coordinating Center</li>
				</ul>
			</div>

			<div class="paragraph">
				<b>Other Resources</b>
			</div>
			
			<div class="paragraph">
				<ul class="triangle">
					<li><a href="https://list.nih.gov/archives/tcga-data-l.html" target="newWin">TCGA Listserv</a> <div class="newWinIcon">&nbsp;</div><br/>Provides a daily update of new data available on the TCGA Data Portal</li>
				</ul>
			</div>

			<div class="stdTitle">Report a Problem</div>

			<div class="paragraph">
				The <a href="https://gforge.nci.nih.gov/tracker/?func=browse&group_id=265&atid=1843" target="newWin">Data Issue Tracker</a> <div class="newWinIcon">&nbsp;</div> provides a list of known issues with TCGA data.  If you find an issue that is not list, please submit it.
			</div>

			<jsp:include page="jspCommon/contactBlock.jsp" />

		</div>
		
		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMenuAbout.jsp" />

			<jsp:include page="jspCommon/rightReportProblem.jsp" />
		</div>		
</div>

<jsp:include page="jspCommon/footer.jsp" />
