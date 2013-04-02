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
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <span class="trailDest">About TCGA Data</span></div>

			<div class="stdTitle">About TCGA Data</div>

			<div class="paragraph">
				TCGA collects and analyzes high-quality tumor samples and makes the following data available 
				on the Data Portal:
				<ul>
					<li>clinical information about patients participating in the program</li>
					<li>metadata about their samples (for example, the weight of a sample portion)</li>
					<li>histopathology slide images from sample portions</li>
					<li>molecular information derived from the samples</li>
				</ul>
				Clinical data and sample metadata is provided by the Biospecimen Core Resources (BCRs) in an 
				XML format. Both the original XML as well as a tab-delimited tabular format are available for 
				download from the TCGA Portal. Slide images provided by the BCRs are also available for 
				download. The BCRs extract DNA and RNA analytes from the samples and send these analytes 
				to Genome Characterization Centers (GCCs) and Sequencing Centers (GSCs), where the molecular 
				data is generated and then submitted to the Data Coordinating Center (DCC).
				<br/>
				<br/>
				In addition to collecting and analyzing high-quality tumor samples, the TCGA is also attempting 
				to include high-quality non-tumor samples in some assays. The goal is to analyze germline DNA 
				for every participant to establish which abnormalities detected in a tumor sample are peculiar 
				to the oncogenic process. 
				<ul>
					<li>For most tumor types, TCGA will be able to collect and analyze normal blood samples for the majority of participants with that disease.</li>
					<li>Sometimes a matching normal blood sample is not available; in this case, a normal tissue sample from the same participant may be used as the germline control in DNA assays.</li>
					<li>In the case of RNA assays, normal blood is not a suitable control since the RNA profile of a blood sample would be expected to differ from the RNA profile of tissue from an organ such as brain, breast, lung, or ovary regardless of whether that tissue were normal or cancerous. For this reason, TCGA attempts to collect some number of normal tissue samples matched to the anatomic site of the tumor but usually not matched to the participant. RNA measurements from these normal samples can be pooled and used to analyze how RNA expression in a tumor differs from RNA expression in normal tissue of the same anatomic origin.</li>
				</ul>
			</div>

			<div class="stdSecondaryTitle">Data Availability</div>
			
			<div class="paragraph">
				All TCGA data is available through the TCGA Portal except for lower levels of sequence data
				(trace files and aligned reads). Trace files produced by older sequencing techologies are
				stored in NCBI's <a target="newWin" href="http://www.ncbi.nlm.nih.gov/Traces/trace.cgi">Trace Archive</a> <div class="newWinIcon">&nbsp;</div>, while the
				aligned reads from newer sequencing technologies are available from NCBI's 
				<a target="newWin" href="http://www.ncbi.nlm.nih.gov/projects/gap/cgi-bin/study.cgi?study_id=phs000178">dbGAP</a> <div class="newWinIcon">&nbsp;</div>.
				Higher level sequence data (variation calls and abundance measures) are available at 
				the TCGA Portal.
			</div>

			<div class="stdSecondaryTitle">Gradual Accumulation of TCGA Data</div>
			
			<div class="paragraph">
				TCGA processes samples as they become available. For some tumor types, the supply of samples 
				is abundant and the targeted number of samples can be collected and processed within a year. 
				For other tumor types, the supply of samples is limited and the collection and processing of 
				samples may extend over several years. The DCC receives data continuously: data available on 
				the Portal may change from week to week, and even from one day to the next. Moreover, since 
				the generation of different data types may require different amounts of time, the DCC may not 
				receive all types of data for a given sample at the same time. Therefore, it is to be expected 
				that at any point in time there will be "holes" in the data available at the Portal.
			</div>

			<div class="stdSecondaryTitle">Changing Technologies</div>
			
			<div class="paragraph">
				The GCCs and GSCs run various assays to produce the molecular data. The assay technologies 
				("platforms") used by the centers have changed over the course of the project. With one 
				exception (the Affymetrix 6.0), none of the microarray platforms that were used in the first 
				year of the TCGA pilot project is still being used in the production phase of the project. In 
				some cases, lower-resolution array platforms are being replaced with higher-resolution array 
				platforms; in other cases, array-based assays are being replaced with sequencing. It is 
				expected that the inventory of TCGA platforms will continue to change.
			</div>
		</div>

		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMenuAbout.jsp" />
		</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
