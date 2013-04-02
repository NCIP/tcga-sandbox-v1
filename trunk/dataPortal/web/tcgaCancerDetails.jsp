<%! String searchableDiseases = new String("GBM OV"); %>
<% String dbHost = new String("http://" + request.getServerName().replace("data", "portal")); %>

<jsp:include page="jspCommon/header.jsp" />

<jsp:include page="jspCommon/cssCommon.jsp" />
<jsp:include page="jspCommon/cssDataPortal.jsp" />

<jsp:include page="jspCommon/jsExt.jsp" />
<jsp:include page="jspCommon/jsRaphael.jsp" />
<jsp:include page="jspCommon/jsCommonUtilities.jsp" />
				<!-- Extra drawing utils -->
            <script type="text/javascript" src="scripts/extensions/drawingUtils.js"></script>
            <script type="text/javascript" src="scripts/extensions/raphaelShapes.js"></script>
            <script type="text/javascript" src="scripts/extensions/raphaelDepth.js"></script>
	<script type="text/javascript" src="scripts/util.js"></script> 
	<script type="text/javascript" src="scripts/newsDisplay.js"></script> 
	<script type="text/javascript" src="scripts/newsDisplayRightAnnouncements.js"></script> 
	<script type="text/javascript" src="scripts/detailsPageTable.js"></script> 
	<script type="text/javascript" src="scripts/detailsPageChart.js"></script> 

</head>
<body>

<jsp:include page="jspCommon/browserWarning.jsp" />

<jsp:include page="jspCommon/navMenu.jsp" />

<div class="bodyCentering">
	<div class="bodyContent">
		<div class="leftColumn">
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <span class="trailDest">Cancer Details</span></div>

			<div class="stdTitle"><%= request.getParameter("diseaseName")%>: Sample Counts and Findings</div>
			
			<div id="cancerDetailsSampleBlock">
				Target number of <%= request.getParameter("diseaseName")%> samples: <b>500</b> (number subject to change)
			</div>
			
			<% if (searchableDiseases.contains(request.getParameter("diseaseType"))) { %>
				<div id="cancerDetailsRunQuery" class="boxcomplete">
					<a href="<% out.print(dbHost); %>/tcga-portal/">Run Query on <%= request.getParameter("diseaseName")%></a>
				</div>
			<% } %>
			
			<br/>
			
			<div id="detailsPageTable" class="paragraph"></div>
			
			<div id="cancerDetailsChart" style="width: 560px;height: 300px;border: 2px solid #dddddd;margin: 10px 40px;">
			</div>

			<div class="paragraph">
				The TCGA data portal does not include lower levels of sequence data.  See
				<a href="http://www.ncbi.nlm.nih.gov/sites/entrez?db=genomeprj&cmd=Link&LinkName=genomeprj_sra&from_uid=41443" target="NewWin">NCBI's Sequence Read Archive</a> <div class="newWinIcon">&nbsp;</div>
				(SRA) for sequence-read data, and 
				<a href="http://www.ncbi.nlm.nih.gov/projects/gap/cgi-bin/study.cgi?study_id=phs000178.v3.p3" target="NewWin">NCBI's dbGAP</a> <div class="newWinIcon">&nbsp;</div>
				for BAM-alignment files and for a study overview.  Sequence trace files produced by older sequencing technologies are stored in
				<a href="http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?" target="NewWin">NCBI's Trace Archive</a>. <div class="newWinIcon">&nbsp;</div>
			</div>
			<div class="paragraph">
				<b>Findings:</b>
				<ul style="margin-top: 0px;">
					<li>Publications with <a href="http://tcga.cancer.gov/publications/scientific.asp" target="NewWin">findings based on TCGA data</a> <div class="newWinIcon">&nbsp;</div> can be found on the TCGA Web Site.</li>
				</ul>
			</div>
		</div>
		
		<div class="rightColumn">
			<jsp:include page="jspCommon/rightAnnouncements.jsp" />

			<jsp:include page="jspCommon/rightMoreTcgaInformation.jsp" />
		</div>		
</div>

<jsp:include page="jspCommon/footer.jsp" />
