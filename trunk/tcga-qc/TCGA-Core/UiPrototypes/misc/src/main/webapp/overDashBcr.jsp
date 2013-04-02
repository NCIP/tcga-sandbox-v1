<jsp:include page="jspCommon/header.jsp" />

<jsp:include page="jspCommon/cssCommon.jsp" />
<jsp:include page="jspCommon/cssDataPortal.jsp" />
	<link rel="stylesheet" type="text/css" href="styles/tcga_data_dash.css">

<jsp:include page="jspCommon/jsExt.jsp" />
<jsp:include page="jspCommon/jsRaphael.jsp" />
<jsp:include page="jspCommon/jsCommonUtilities.jsp" />
	<script type="text/javascript" src="scripts/dashProjectTable.js"></script> 
	<script type="text/javascript" src="scripts/dashNavigator.js"></script> 
	<script type="text/javascript" src="scripts/overDashBcr.js"></script> 

</head>
<body>

<jsp:include page="jspCommon/browserWarning.jsp" />

<jsp:include page="jspCommon/navMenu.jsp" />

<div class="bodyCentering">
	<div class="bodyContent">
		<div class="trail"><span class="trailDest">Process Overview</span></div>

		<div class="stdTitle">TCGA Case Accrual through BCRs Dashboard</div>
		
		<div id="dashProjectExplanation" class="paragraph" style="width: 490px;height: 106px;display: inline-block;vertical-align:top;border: 1px dashed black;">
			Explain the BCR dashboard goes here.
		</div>

		<div id="dashNavigator" class="paragraph" style="width: 350px;display: inline-block;border: 4px double #9999ff;"></div>

		<div id="dashProjectTable" class="paragraph" style="border: 1px dashed black;height: 200px;vertical-align: top;">
			The dashboard for the tissue details goes here.
		</div>
	</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />