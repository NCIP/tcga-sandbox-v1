<jsp:include page="jspCommon/header.jsp" />

<jsp:include page="jspCommon/cssCommon.jsp" />
<jsp:include page="jspCommon/cssDataPortal.jsp" />

<jsp:include page="jspCommon/jsExt.jsp" />
<jsp:include page="jspCommon/jsCommonUtilities.jsp" />
	<script type="text/javascript" src="scripts/newsDisplay.js"></script> 
	<script type="text/javascript" src="scripts/newsDisplayRightAnnouncements.js"></script> 
	<script type="text/javascript" src="scripts/homePageTable.js"></script> 

</head>
<body>

<jsp:include page="jspCommon/browserWarning.jsp" />

<jsp:include page="jspCommon/navMenu.jsp" />

<div class="bodyCentering">
	<div class="bodyContent">
		<div class="leftColumn">
			<div class="trail"><span class="trailDest">Home</span></div>

			<div class="stdTitle">TCGA Data Portal Overview</div>
			
			<div class="paragraph">
				The Cancer Genome Atlas (TCGA) Data Portal provides a platform for researchers to search, download, and analyze data sets
				generated by TCGA.  It contains clinical information, genomic characterization data, and
				high-throughput sequencing analysis of the tumor genomes.
			</div>
			
			<div id="homePageButtons" class="paragraph">
				<div id="homePageQueryDataButton" class="button blueButtonFill" onclick="location.href=tcgaDbHost + '/tcga-portal/';">
					Query the Data
				</div>
				<div id="homePageDownloadDataButton" class="button blueButtonFill" onclick="location.href='tcgaDownload.jsp';">
					Download Data
				</div>
				<br/>
				<div class="dataButtonDescription">
					Search summarized data for genes, patients and pathways
				</div>
				<div class="dataButtonDescription">
					Choose from three ways to download data
				</div>
			</div>
			
			<div id="homePageTable" class="paragraph"></div>

			<div class="paragraph">
				<a href="http://tcga.cancer.gov/" target="NewWin">List of all cancer data planned for TCGA</a> <div class="newWinIcon">&nbsp;</div>.
			</div>
			
			<div class="paragraph">
				The TCGA data portal does not include lower levels of sequence data.  See
				<a href="http://www.ncbi.nlm.nih.gov/sites/entrez?db=genomeprj&cmd=Link&LinkName=genomeprj_sra&from_uid=41443" target="NewWin">NCBI's Sequence Read Archive</a> <div class="newWinIcon">&nbsp;</div>
				(SRA) for sequence-read data, and 
				<a href="http://www.ncbi.nlm.nih.gov/projects/gap/cgi-bin/study.cgi?study_id=phs000178.v3.p3" target="NewWin">NCBI's dbGAP</a> <div class="newWinIcon">&nbsp;</div>
				for BAM-alignment files and for a study overview.  Sequence trace files produced by older sequencing technologies are stored in
				<a href="http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?" target="NewWin">NCBI's Trace Archive</a>. <div class="newWinIcon">&nbsp;</div>
			</div>
		</div>
		
		<div class="rightColumn">
			<jsp:include page="jspCommon/rightAnnouncements.jsp" />

			<jsp:include page="jspCommon/rightMoreTcgaInformation.jsp" />
		</div>		
</div>

<jsp:include page="jspCommon/footer.jsp" />
