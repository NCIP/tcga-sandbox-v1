<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

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

        <div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaDownload.jsp">Download Data</a> > <span class="trailDest">Somatic Mutation Data</span></div>

<div class="stdTitle">Open-Access Validated Somatic Mutation Data</div>

<div class="stdSecondaryTitle">Data tables used for identifying significantly mutated genes described in the <a target="newWin" href="/docs/publications/gbm_2008/">2008 GBM manuscript</a> <div class="newWinIcon">&nbsp;</div></div>
<ul>
<li>
<a target="newWin" href="/docs/publications/gbm_2008/TCGA_GBM_Level3_All_Somatic_Mutations_DataFreeze2.maf">Level 3 - Somatic Mutations</a> <div class="newWinIcon">&nbsp;</div>: Somatic mutations summarized from all centers using the <a href="/docs/TCGA_GBM08_Publication_Data_Freeze.txt">09/09/08 TCGA GBM Publication Data Freeze</a> <div class="newWinIcon">&nbsp;</div>. Non-silent mutations were validated by orthogonal methods (genotype assay or alternative sequencing method) or verified by independent PCR amplification and sequencing. Silent mutations were validated, verified or manually reviewed.
<b>Download:</b> [<a target="newWin" href="/docs/publications/gbm_2008/TCGA_GBM_Level3_All_Somatic_Mutations_DataFreeze2.maf">.maf</a>] [<a target="newWin" href="/docs/publications/gbm_2008/TCGA_GBM_Level3_All_Somatic_Mutations_DataFreeze2.xls">.xls</a>] <div class="newWinIcon">&nbsp;</div>
</li>

<li>
<a target="newWin" href="/docs/publications/gbm_2008/TCGA_GBM_Level4_Significant_Genes_by_Mutations_DataFreeze2.xls">Level 4 - Significance Across Samples</a> <div class="newWinIcon">&nbsp;</div>: statistical significance for each gene WRT somatic mutation counts in Level 3 data (represented as p- and q-values).  <b>Download:</b> [<a href="/docs/publications/gbm_2008/TCGA_GBM_Level4_Significant_Genes_by_Mutations_DataFreeze2.xls">.xls</a>] <div class="newWinIcon">&nbsp;</div>
</li>
</ul>

<div class="stdSecondaryTitle">Latest summarized somatic mutation data</div>
<ul>
<li>
<a target="newWin" href="/docs/somatic_mutations/TCGA_GBM_Level3_Somatic_Mutations_08.28.2008.maf">Level 3 - Somatic Mutations as of 08/28/2008</a> <div class="newWinIcon">&nbsp;</div>: all validated somatic mutations summarized for all centers up to 08/28/2008. Duplicate entries from the same sample assayed by multiple centers were removed. <b>Download:</b> [<a href="/docs/somatic_mutations/TCGA_GBM_Level3_Somatic_Mutations_08.28.2008.maf">.maf</a>] [<a href="/docs/somatic_mutations/TCGA_GBM_Level3_Somatic_Mutations_08.28.2008.xls">.xls</a>] <div class="newWinIcon">&nbsp;</div></li>
</ul>

<div class="stdSecondaryTitle">Additional Information</div>
<ul>
<li>Current somatic mutation data can be downloaded from the Data Access Matrix: <a href="/tcga/dataAccessMatrix.htm?mode=ApplyFilter&platformType=9&diseaseType=GBM">GBM Somatic Mutations</a>, <a href="/tcga/dataAccessMatrix.htm?mode=ApplyFilter&platformType=9&diseaseType=OV">OV Somatic Mutations</a>
<li>All GBM Publication Mutations: <a target="newWin" href="http://tcga.cancer.gov/dataportal/data/access/closed/">Controlled-access</a> <div class="newWinIcon">&nbsp;</div> mutations that include germ-line, somatic, and LOH: [<a target="newWin" href="/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/gbm/cgcc/broad.mit.edu/abi/tracerel/broad.mit.edu_GBM.ABI.1.21.0/broad.mit.edu_GBM.ABI.1.maf">Broad</a>] [<a target="newWin" href="/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/gbm/gsc/hgsc.bcm.edu/abi/tracerel/hgsc.bcm.edu_GBM.ABI.1.19.0/hgsc.bcm.edu_GBM.ABI.1.maf">Baylor</a>] [<a target="newWin" href="/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/gbm/gsc/genome.wustl.edu/abi/tracerel/genome.wustl.edu_GBM.ABI.319.0.0/genome.wustl.edu_GBM.ABI.319.maf">WashU</a>] <div class="newWinIcon">&nbsp;</div></li>
<li>The latest <a target="newWin" href="http://tcga.cancer.gov/dataportal/data/access/closed/">controlled-access</a> <div class="newWinIcon">&nbsp;</div> mutations can be found by entering 'maf' in the File Name field on the <a href="/tcga/findArchives.htm">Find Archives search page</a></li>
<li>Descriptions of TCGA data levels are provided in the <a href="/docs/TCGA_Data_Primer.pdf">TCGA Data Primer</a> <div class="pdfIcon">&nbsp;</div> <div class="newWinIcon">&nbsp;</div></li>
<li><a target="newWin" href="/docs/somatic_mutations/MutationAnnotationFormatDescription.pdf">Descriptions of column headers</a> <div class="pdfIcon">&nbsp;</div> <div class="newWinIcon">&nbsp;</div></li>
</ul>

<p style="font-size:0.9em">Note: Some Windows machines may attempt to use Microsoft Access to open maf files.  If this happens to you, please right-click the maf file and choose "Open With..." to select the appropriate application to use for opening the file.</p>

	</div>

        <div class="rightColumn">
            <jsp:include page="jspCommon/rightMenuDownloadData.jsp" />
	</div>

      </div>
    </div>

<jsp:include page="jspCommon/footer.jsp" />
