<jsp:include page="jspCommon/header.jsp" />

<jsp:include page="jspCommon/cssExt.jsp" />
<jsp:include page="jspCommon/cssCommon.jsp" />
<jsp:include page="jspCommon/cssDataPortal.jsp" />
	<link rel="stylesheet" type="text/css" href="styles/tcga_platforms.css">
	
<jsp:include page="jspCommon/jsCommonUtilities.jsp" />
<jsp:include page="jspCommon/jsExt.jsp" />
	<script type="text/javascript" src="scripts/platforms.js"></script> 

</head>
<body>

<jsp:include page="jspCommon/browserWarning.jsp" />

<jsp:include page="jspCommon/navMenu.jsp" />

<div class="bodyCentering">
	<div class="bodyContent">
		<div class="leftColumn">
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaAbout.jsp">About the Data</a> > <span class="trailDest">TCGA Platform Design</span></div>

			<div class="stdTitle">Platform Design</div>

			<div class="paragraph"><div class="newWinIcon">&nbsp;</div> Please note: All links in the table will open in a new window.</div>

			<div id="platforms" class="paragraph">
			</div>

			<div id="platformsInformation">
			            <ul>
			              <li class="paragraph"><b>Center</b> - provides the Center Name (domain) for the center that is submitting data for a particular platform. A <a href="/datareports/codeTablesReport.htm?codeTable=center">report</a> is available that provides a relationship between Center Name (domain), Center Type, Center Display Name, and BCR Center ID (Data Generating Center).</li>
			              <li class="paragraph"><b>TCGA Platform Code</b> - provides the code used in TCGA to represent a platform. The code is used in archive and file names and file contents. A <a href="/datareports/codeTablesReport.htm?codeTable=platform">report</a> is available that provides the relationship between TCGA Platform Code and Platform Name.</li>
			              <li class="paragraph"><b>Platform Name</b> - provides the vendor's name of a particular platform. The Platform Name links to the best approximation of a direct link to the platform's main information page. On the venor's page you may find links to the vendor's annotation files and other supporting materials. The DCC does not collect or track vendor annotations. If you have trouble finding the vendor's supporting materials, we reccomend you contact the vendor directly. A <a href="/datareports/codeTablesReport.htm?codeTable=platform">report</a> is available that provides the relationship between TCGA Platform Code and Platform Name.</li>
			              <li class="paragraph"><b>Sequence Download</b> - The links provided in this column either download the FASTA file that was submitted by the related center and used to create the ADF file, or in the case of sequence-based platforms, take you to the NCBI Trace or Short Read Archives where you can download or search the metadata to get the primers used in the assay. Values of <em>Unavailable mean that the FASTA file has not yet been posted by the related center, or in the case of sequence-based plaforms, the data is not yet available at NCBI. Values of <i>NA</i> mean that probe or primer sequence files are not applicable to the related platform and that those sequences may be available in a result file (<i>e.g.</i> <span style="letter-spacing: 0px;">IlluminaGA_mRNA_DGE</span> sequences are available in the archive result files).</em></li>
			<em>              </em><li><em><b>TCGA ADF Download</b> - TCGA uses a modified format of the standard MAGE ADF file. TCGA ADFs are used in the anaysis of the resulting data. The links provided download the TCGA ADF. Values of <i>Unavailable</i> mean that the ADF file has not yet been posted by the related center, or in the case of sequence-based plaforms, the data is not yet available at NCBI. Values of <i>NA</i> mean that ADF files are not applicable to the related platform  (<i>e.g.</i> <span style="letter-spacing: 0px;">IlluminaGA_mRNA_DGE</span> is not an array-based platform and so has no ADF).</em></li>
			<em>            </em></ul>
			<em>        </em></div>
			
					</div>
			
			</div>

<jsp:include page="jspCommon/footer.jsp" />
