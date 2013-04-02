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
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaTools.jsp">Tools</a> > <span class="trailDest">Analytical Tools</span></div>

			<div class="stdTitle">Analytical Tools</div>

			<ul class="paragraph">
				<li><a href="http://cma.nci.nih.gov"><b>NCI Cancer Molecular Analysis (CMA) Portal</b></a> <div class="newWinIcon">&nbsp;</div><br/>
					CMA allows researchers to integrate, visualize, and explore clinical and genomic characterization data from translational research studies.
				</li>
				<li><a href="http://cgwb.nci.nih.gov"><b>NCI Cancer Genome Workbench (CGWB)</b></a> <div class="newWinIcon">&nbsp;</div><br/>
					The Cancer Genome Workbench provides whole-genome and heatmap views of sample-level data.
				</li>
				<li><a href="http://www.broadinstitute.org/igv"><b>Broad Institute Integrative Genomics Viewer (IGV)</b></a> <div class="newWinIcon">&nbsp;</div><br/>
					The Integrative Genomics Viewer (IGV) is a high-performance visualization tool for interactive exploration of large, integrated datasets.
				</li>
				<li><a href="http://cbio.mskcc.org/cancergenomics-dataportal/index.do"><b>MSKCC Cancer Genomics Analysis</b></a> <div class="newWinIcon">&nbsp;</div><br/>
					The cBio Cancer Genomics Pathway Portal provides direct download and visualization of large-scale cancer genomics data sets.
				</li>
			</ul>
		</div>

		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMenuTools.jsp" />

			<jsp:include page="jspCommon/rightMoreTcgaInformation.jsp" />
		</div>

	</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
