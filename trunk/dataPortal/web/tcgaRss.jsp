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
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <span class="trailDest">RSS Feeds</span></div>

			<div class="stdTitle">RSS Feeds</div>
			
			<div class="paragraph">
				The RSS feeds on the TCGA Data Portal are published through a service called FeedBurner. 1 FeedBurner provides you with the choice to subscribe to the RSS feeds with Web-based readers or to get updates delivered by e-mail. TCGA does not recommend or endorse any reader in particular. <a target="newWin" href="http://www.nih.gov/news/rss.htm">Read more about RSS and RSS Readers.</a> <div class="newWinIcon">&nbsp;</div>
			</div>
			
			<div class="stdSecondaryTitle">Feeds by Cancer Type</div>
			<div class="paragraph indent">
				<a class="rssIcon" href="#">Acute myeloid leukemia</a><br/>
				<a class="rssIcon" href="#">Colon adenocarcinoma</a><br/>
				<a class="rssIcon" href="#">Glioblastoma multiforme</a><br/>
				<a class="rssIcon" href="#">Kidney renal papillary cell carcinoma</a><br/>
				<a class="rssIcon" href="#">Lung adenocarcinoma</a><br/>
				<a class="rssIcon" href="#">Lung squamous cell carcinoma</a><br/>
				<a class="rssIcon" href="#">Ovarian serous cystadenocarcinoma</a><br/>
				<a class="rssIcon" href="#">Rectum adenocarcinoma</a>
			</div>
			
			<div class="stdSecondaryTitle">Feeds by Data Type:</div>
			<div class="paragraph indent">
				<a class="rssIcon" href="#">Expression Gene</a><br/>
				<a class="rssIcon" href="#">Expression Exon</a><br/>
				<a class="rssIcon" href="#">Expression miRNA</a><br/>
				<a class="rssIcon" href="#">Copy Number</a><br/>
				<a class="rssIcon" href="#">Methylation</a><br/>
				<a class="rssIcon" href="#">SNP</a><br/>
				<a class="rssIcon" href="#">Traces</a><br/>
				<a class="rssIcon" href="#">Somatic Mutations</a><br/>
				<a class="rssIcon" href="#">Clinical Data</a><br/>
			</div>
		</div>
		
		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMoreTcgaInformation.jsp" />
		</div>		
</div>

<jsp:include page="jspCommon/footer.jsp" />
