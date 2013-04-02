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
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaAbout.jsp">About the Data</a> > <span class="trailDest">Data Tracking Reports</span></div>

			<div class="stdTitle">Data Tracking Reports</div>

        <ul class="reportBullet" style="text-align:left">
        <li class="paragraph">
        <a href="sampleSummaryReport.jsp"><span class="stdSecondaryTitle">Experiment Sample-Counts Summary Report</span></a>
        <br/>The Experiment Sample-Counts Summary Report provides a count summary of Sample IDs by Disease Study, Center, Analyte Type, and Platform. [<a class="hand" onclick="tcga.detailPopup.show({title: 'Experiment Sample-Counts Summary Report', message: '<p>The Experiment Sample-Counts Summary Report provides a count summary of Sample IDs by Disease Study, Center, Analyte Type, and Platform. Sample counts are provided for Sample IDs a BCR Reported Sending to a Center, Sample IDs the DCC Received from a Center, unaccounted for BCR Sample IDs that a Center Reported, unaccounted for Center Sample IDs that a BCR Reported, and sample ID counts by data level. Links are provided for each positive count that resolve to list of Sample IDs for a specific count. This report is useful for determining how many and which Samples are in the TCGA pipeline. The counts are based on <i>reported</i> IDs either form a BCR or from a GSC/CGCC.</p>'})">more</a>]
        </li>
        <li class="paragraph">
        <a href="aliquotReport.jsp"><span class="stdSecondaryTitle">Experiment Aliquot Report</span></a>
        <span><br />The Experiment Aliquot Report provides a summary of experiment Aliquot IDs by Disease Study, BCR Batch number, Receiving Center, Platform, and data levels (1-3).</span> [<a class="hand" onclick="tcga.detailPopup.show({title: 'Experiment Aliquot Report', message: '<p>The Experiment Aliquot Report provides a summary of experiment Aliquot IDs by Disease Study, BCR Batch number, Receiving Center, Platform, and data levels (1-3). Aliquots are either described as <b>Submitted</b> or <b>Missing</b> for each data level. A value of <b>Missing</b> means that the DCC has not received data for an Aliquot ID for a particular data level. A value of <b>Submitted</b> means that the DCC has received data for an Aliquot ID for a particular data level. A <b>Submitted</b> value links to a list of files that represent the submitted data level for an aliquot ID. A filename may be clicked to download a file. This report is useful for determining which Aliquot IDs have outstanding data or locating the data for a specific Aliquot ID.</p>'})">more</a>]
        </li>
        <li class="paragraph">
        <a href="aliquotIdBreakdownReport.jsp"><span class="stdSecondaryTitle">Aliquot Id Breakdown Report</span></a>
        <span><br />The Aliquot ID Breakdown Report maps IDs for each step in the chain of provenance for each aliquot.</span> [<a class="hand" onclick="tcga.detailPopup.show({title: 'Aliquot Id Breakdown Report', message: '<p>The Aliquot ID Breakdown Report maps IDs for each step in the chain of provenance for each aliquot. Each row contains an Aliquot ID, mapped to its associated Analyte ID (from which the aliquot was made), Sample ID (from which the analyte was produced), Patient ID (from whom the sample was taken).</p> <p style=\'padding-top:5px;\'>The user can filter the table to a desired set of values; for example, to obtain all aliquot IDs for a particular analyte, click <b>Show Filters</b>, enter the Analyte ID in the corresponding text box, and click <b>Filter Now</b>. The resulting table can be exported to the user\'s machine by clicking <b>Export Data</b>, and selecting the desired format. More restrictive filters can be applied by entering further matching constraints in the <b>Filters Extended</b> pulldown.</p>'})">more</a>]
        </li>
        <li class="paragraph">
        <a href="orphanedAliquotReport.jsp"><span class="stdSecondaryTitle">Orphaned Aliquot Report</span></a>
        <span><br />The Orphaned Aliquot Report provides a list of biological IDs that failed linking between other data in TCGA.</span> [<a class="hand" onclick="tcga.detailPopup.show({title: 'Orphaned Aliquot Report', message: '<p>The Orphaned Aliquot Report provides a list of biological IDs that failed linking between other data in TCGA. For instance, an aliquot ID may have an incorrect format, or the ID may not be found in submitted clinical data. The ID\'s associated Archive Name, File Name, the Date the orphan was Reported On, the date the orphan was Repaired On, and the Failure Reason are also provided.</p><p style=\'padding-top:5px;\'>The report can be filtered by clicking on the Toggle Filters button and selecting the Failure Reason, Aliquot ID, or date range for Reported or Repaired. Clicking on a column header sorts the results by that column. Clicking on Export Data provides options for report download in different formats, or a URL that will recreate the report filter. This report is useful for identifying ID linkage breaks in a pipeline or for submitting centers to identify IDs and files that need triage.</p>'})">more</a>]
        </li>
        <li class="paragraph">
        <a href="latestArchiveReport.jsp"><span class="stdSecondaryTitle">Latest Archive Report</span></a>
        <span><br />The Latest Archive Report provides a list of archives that are the latest revision, the date the archive was added, and the archive type.</span> [<a class="hand" onclick="tcga.detailPopup.show({title: 'Latest Archive Report', message: '<p>The Latest Archive Report provides a list of archives that are the latest revision, the date the archive was added, and the archive type. If the submission is characterization-based (CGCC), then the associated sample-to-data relationship file (SDRF) is listed. If the submission is genomic sequencing-based, then the associated mutation (MAF) file is listed if the archive contains a MAF file. Archive Type reflects the aggregation of the data within the archive. A classic archive type can contain all levels and types of data, while all other types contain the specified type of data.</p> <p style=\'padding-top:5px;\'>Clicking on the blue text for Archive, Associated SDRF File, or Associated MAF file downloads the archive or file. The report can be filtered by clicking on the Toggle Filters button and selecting the archive type, or date range. Clicking on a column header sorts the results by that column. The report may be exported for other uses or a URL is provided to recreate the report filter. This report may be useful to those trying to mirror TCGA data. For example, a user could filter the report by date range to see what new data is available since the last download.</p>'})">more</a>]
        </li>
        <li class="paragraph">
        <a href="codeTablesReport.jsp"><span class="stdSecondaryTitle">Code Tables Report</span></a>
        <span><br />The Code Tables Report provides a set of tables associating metadata in English with standard short codes and abbreviations for that data.</span> [<a class="hand" onclick="tcga.detailPopup.show({title: 'Code Tables Report', message: '<p>The Code Tables Report provides a set of tables associating metadata in English with standard short codes and abbreviations for that data. The tables are organized by metadata type, e.g, the <b>Center</b> table is a list of TCGA participating centers and their abbreviations, while the <b>Tissue Source Site</b> table contains codes which map to kind of tissue, originating sample site, study abbreviation, and study name. Note that the tables themselves sometimes use codes or abbreviations in certain data columns; these codes can be translated using other code tables in this report.</p> <p style=\'padding-top:5px;\'>Browse the tables by using the scrollbar on the right of the panel, or by clicking the links on the left. Entire tables can be downloaded by clicking the <b>Export Data</b> pulldown at the top left of each table. Select the desired export format, and the download will begin automatically.</p>'})">more</a>]
        </li>
        </ul>

        <div class="paragraph">
        <div class="stdSecondaryTitle">Definitions</div>
        <ul>
        <li>
        An <i>Experiment</i> is defined in TCGA as the sum of the results of assays for a particular platform from a particular center for all Samples of a particular disease study.
        </li>
        </ul>
        </div>

		</div>

		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMenuAbout.jsp" />
		</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
