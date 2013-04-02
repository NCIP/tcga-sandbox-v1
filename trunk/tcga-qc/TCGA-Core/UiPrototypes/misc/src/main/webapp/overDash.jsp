<jsp:include page="jspCommon/header.jsp" />

<jsp:include page="jspCommon/cssCommon.jsp" />
<jsp:include page="jspCommon/cssDataPortal.jsp" />
	<link rel="stylesheet" type="text/css" href="styles/tcga_data_dash.css">

<jsp:include page="jspCommon/jsExt.jsp" />
<jsp:include page="jspCommon/jsRaphael.jsp" />
<jsp:include page="jspCommon/jsCommonUtilities.jsp" />
	<script type="text/javascript" src="scripts/dashProjectTable.js"></script> 
	<script type="text/javascript" src="scripts/dashNavigator.js"></script> 
	<script type="text/javascript" src="scripts/overDash.js"></script> 

</head>
<body>

<jsp:include page="jspCommon/browserWarning.jsp" />

<jsp:include page="jspCommon/navMenu.jsp" />

<div class="bodyCentering">
	<div class="bodyContent">
		<div class="trail"><span class="trailDest">Process Overview</span></div>

		<div class="stdTitle">TCGA Process Overview Dashboard</div>
		
		<div id="dashProjectExplanation" class="paragraph" style="width: 490px;display: inline-block;">
			This is an explanation of the OverDash, that almighty master of dashboards before
			which all other dashboards are but pale shadows.  We hold up the OverDash as the
			Platonic ideal to which the other dashboards aspire.  To find those pale shadows
			of other dashboards, please click on the titles in the OverDash.
		</div>

		<div id="dashNavigator" class="paragraph" style="width: 350px;display: inline-block;border: 4px double #9999ff;"></div>
	</div>
</div>

<div style="margin-left: 20px;" class="bodyCentering">
	<div id="dashProjectTable" class="paragraph"></div>
</div>

<div class="bodyCentering">
	<div class="bodyContent">
		<jsp:include page="jspCommon/footer.jsp" />
	</div>
</div>
