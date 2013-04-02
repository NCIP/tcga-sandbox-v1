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
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaAbout.jsp">About the Data</a> > <span class="trailDest">Data Levels and Data Types</span></div>

			<div style="margin-top: 10px;display: inline-block;width: 300px;">
				<div class="stdTitle">Data Levels and Data Types</div>
				
				<div class="stdSecondaryTitle">Data Levels</div>
			</div>
			
			<div id="dataLevelsDataTypeRedirect" class="boxcomplete">
				Also on this page:<br/>
				<a href="#dataLevelsDataTypes">Relationship of Data Levels to Data Types</a>
			</div>
			
			<table class="gradientRoundedTable topAlign paragraph" cellspacing="0" cellpadding="3px">
				<tr>
					<th>Data Level</th>
					<th>Description</th>
					<th>Example</th>
				</tr>
				<tr>
					<td>Level 1<br/><br/>Raw</td>
					<td>Low-level data for a single sample, not normalized across samples, and not interpreted for the presence or absence of specific molecular abnormalities.</td>
					<td>Sequence trace file; Affymetrix CEL file</td>
				</tr>
				<tr>
					<td>Level 2<br/><br/>Processed</td>
					<td>Data for a single sample that has been normalized and interpreted for the presence or absence of specific molecular abnormalities.</td>
					<td>Mutation call for a single sample; amplification/deletion/LOH call for a probed locus in a sample; expression of a splice variant.</td>
				</tr>
				<tr>
					<td>Level 3<br/><br/>Segmented/ Interpreted</td>
					<td>For genomic copy-number assays, segmented data is processed data for a single sample that has been further analyzed to aggregate individual loci into larger contiguous regions.</td>
					<td>Amplification/deletion/LOH call for a region in a sample.</td>
				</tr>
				<tr>
					<td>Level 4<br/><br/>Summary Finding</td>
					<td>A quantified association, across classes of samples, among two or more specific molecular abnormalities, sample characteristics, or clinical variables.</td>
					<td>A finding that a particular genomic region (a "region of interest") is amplified in 10% of TCGA glioma samples.</td>
				</tr>
			</table>

			<a name="dataLevelsDataTypes"></a>

			<div class="stdSecondaryTitle">Relationship of Data Levels to Data Types</div>
			
			<table class="gradientRoundedTable topAlign paragraph" cellspacing="0" cellpadding="3px">
				<tr>
					<th>Data Type (Base-Specific)</th>
					<th>Level 1 (Raw&nbsp;Data)</th>
					<th>Level 2 (Normalized/ Processed)</th>
					<th>Level 3 (Segmented/ Interpreted)</th>
					<th>Level 4 (Summary Finding/ROI)</th>
				</tr>
				<tr>
					<td>Clinical-Complete Set</td>
					<td>Clinical data for 1 patient</td>
					<td>NA</td>
					<td>NA</td>
					<td>NA</td>
				</tr>
				<tr>
					<td>Clinical-Minimal Set</td>
					<td>Clinical data for 1 patient</td>
					<td>NA</td>
					<td>NA</td>
					<td>NA</td>
				</tr>
				<tr>
					<td>Copy Number Results-CGH</td>
					<td>Raw signals per probe</td>
					<td>Normalized signals for copy number alterations of aggregated regions, per probe or probe set</td>
					<td>Copy number alterations for aggregated/ segmented regions, per sample</td>
					<td>Regions with statistically significant copy number changes across samples</td>
				</tr>
				<tr>
					<td>Copy Number Results-SNP</td>
					<td>NA</td>
					<td>Copy number alterations per probe or probe set</td>
					<td>Copy number alterations for aggregated regions, per sample</td>
					<td>Regions with statistically significant copy number changes across samples</td>
				</tr>
				<tr>
					<td>LOH-SNP</td>
					<td>NA</td>
					<td>LOH Calls per probe set</td>
					<td>Aggregation of regions of LOH per sample</td>
					<td>Statistically significant LOH across samples</td>
				</tr>
				<tr>
					<td>SNP</td>
					<td>Raw signals per probe</td>
					<td>Normalized signals per probe or probe set</td>
					<td>NA</td>
					<td>NA</td>
				</tr>
				<tr>
					<td>DNA Methylation</td>
					<td>Raw signals per probe</td>
					<td>Normalized signals per probe or probe set and allele calls</td>
					<td>Methylated sites/genes per sample</td>
					<td>Statistically significant methylated sites/genes across samples</td>
				</tr>
				<tr>
					<td>Expression-Exon</td>
					<td>Raw signals per probe</td>
					<td>Normalized signals per probe set</td>
					<td>Expression calls for Exons/Variants per sample</td>
					<td>Genes with statiscally significant alternative splicing across samples</td>
				</tr>
				<tr>
					<td>Expression-Gene</td>
					<td>Raw signals per probe</td>
					<td>Normalized signals per probe or probe set</td>
					<td>Expression calls for Genes per sample</td>
					<td>Genes of interest across samples</td>
				</tr>
				<tr>
					<td>Expression-miRNA</td>
					<td>Raw signals per probe</td>
					<td>Normalized signals per probe or probe set</td>
					<td>Expression calls for miRNAs per sample</td>
					<td>miRNAs of interest across samples</td>
				</tr>
				<tr>
					<td>Trace-Gene- Sample Relationship</td>
					<td>Trace file</td>
					<td>NA</td>
					<td>NA</td>
					<td>NA</td>
				</tr>
				<tr>
					<td>Mutations</td>
					<td>NA</td>
					<td>Putative mutations</td>
					<td>Validated somatic mutations</td>
					<td>Statistically significant mutations across samples</td>
				</tr>
			</table>
		</div>

		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMenuAbout.jsp" />
		</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
