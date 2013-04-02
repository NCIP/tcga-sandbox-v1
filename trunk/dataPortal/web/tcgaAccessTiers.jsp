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
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaAbout.jsp">About the Data</a> > <span class="trailDest">Access Tiers</span></div>

			<div class="stdTitle">Access Tiers</div>

			<div>
				There are two data access tiers:
				<ul>
					<li>Open Access data tier</li>
					<li>Controlled Access data tier</li>
				</ul>
			</div>

			<div class="stdSecondaryTitle">
				<b>Open Access Data Tier</b>
			</div>

			<div class="paragraph">				
				The Open Access data tier is a publically tier of data that cannot be aggregated to generate a dataset unique to an individual.  The Open Access data tier does not require user certification for data access. 
			</div>
			
			<div class="paragraph">
				Data within the Open Access data tier are available in public databases, such as TCGA Data Portal and the NCBI Trace Repository.  These data types may include:
				
				<ul>
					<li>Tissue pathology data</li>
					<li>Health Insurance Portability and Accountability Act of 1996 (HIPAA) de-identified clinical data</li>
					<li>Gene expression data</li>
					<li>Copy-number alterations for non-genetic platforms</li>
					<li>Epigenetic data</li>
					<li>Data summaries, such as genotype frequencies</li>
					<li>DNA sequence data of single amplicons</li>
				</ul>
			</div>

			<div class="stdSecondaryTitle">
				<b>Controlled Access Data Tier</b>
			</div>
			
			<div class="paragraph">
				The Controlled Access data tier requires user certification for data access.  It contains clinical data and individually unique information such as demographic and clinical data
				up to the level of detail permitted by HIPAA Limited Data Set regulations, genome-wide genotypes, and information linking all sequence traces to a single donor whose associated
				data has been stripped of direct identifiers.
			</div>
			
			<div class="paragraph">
				Access to this data is available to researchers who:
				
				<ul>
					<li>Agree to restrict their use of the information to biomedical research purposes only</li>
					<li>Agree with the statements within <a target="newWin" href="http://dbgap.ncbi.nlm.nih.gov/aa/wga.cgi?page=DUC&view_pdf&stacc=phs000178.v1.p1">TCGA Data Use Certification (DUC)</a> <div class="pdfIcon">&nbsp;</div> <div class="newWinIcon">&nbsp;</div></li>
					<li>Have their institutions certifiably agree to the statements within TCGA DUC</li>
					<li>
						Complete the <a target="newWin" href="http://www.ncbi.nlm.nih.gov/projects/gap/cgi-bin/study.cgi?study_id=phs000178.v1.p1">Data Access Request</a> <div class="newWinIcon">&nbsp;</div> (DAR) form and submit it to the Data Access Committee to be a TCGA Approvbed User.
						This <a target="newWin" href="https://dbgap.ncbi.nlm.nih.gov/aa/wga.cgi?login=&page=login">form is available electronically</a> <div class="newWinIcon">&nbsp;</div> through the Database of Genotype and PhenoType (dbGaP).
					</li>
				</ul>
			</div>
			
			<div>
				Questions regarding Data Access Policies and Procedures can be sent to the TCGA Data Access Committee at <a href="mailto:tcga@mail.nih.gov">tcga@mail.nih.gov</a>
			</div>
		</div>

		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMenuAbout.jsp" />
		</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
