<jsp:include page="jspCommon/header.jsp" />

<jsp:include page="jspCommon/cssCommon.jsp" />
<jsp:include page="jspCommon/cssDataPortal.jsp" />

<jsp:include page="jspCommon/jsExt.jsp" />
<jsp:include page="jspCommon/jsCommonUtilities.jsp" />
	<script type="text/javascript" src="scripts/newsDisplay.js"></script> 
	<script type="text/javascript" src="scripts/news.js"></script> 

</head>
<body>

<jsp:include page="jspCommon/browserWarning.jsp" />

<jsp:include page="jspCommon/navMenu.jsp" />

<div class="bodyCentering">
	<div class="bodyContent">
		<div class="leftColumn">
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaAbout.jsp">About the Data</a> > <span class="trailDest">Announcements</span></div>

			<div class="stdTitle">Announcements</div>

         <div id="newsNewsArticles">
         </div>
      </div>


		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMenuAbout.jsp" />
		</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
