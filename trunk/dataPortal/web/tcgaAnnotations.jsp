<jsp:include page="jspCommon/header.jsp" />

<jsp:include page="jspCommon/cssExt.jsp" />
<jsp:include page="jspCommon/cssGwt.jsp" />
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
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaTools.jsp">Tools</a> > <span class="trailDest">UUID Manager</span></div>

			<div class="stdTitle">Annotations Manager</div>

	<div class="paragraph">
		The Annotations Manager offers a simple interface to add, edit and search annotations to TCGA data.  All TCGA Data Portal visitors can use the tool to search for annotations.  Authenticated TCGA project members can use it to add or edit annotations to their data.
	</div>

	<div style="width: 100%;" id="center">
      <div id="main"><div id="annotationApplicationPanel" class=" x-panel"><div class="x-panel-bwrap" id="ext-gen15"><div class="x-panel-body x-panel-body-noheader x-border-layout-ct" id="ext-gen16" style="height: 398px;"><div id="ext-comp-1049" class=" x-panel x-border-panel" style="left: 0px; top: 0px; width: 100%;"><div class="x-panel-header x-unselectable" id="ext-gen18" style="-moz-user-select: none;"><span class="x-panel-header-text" id="ext-gen21">TCGA Annotations</span></div><div class="x-panel-bwrap" id="ext-gen19"><div class="x-panel-body" id="ext-gen20" style="height: 26px; width: 100%;"><div class="x-toolbar x-small-editor x-toolbar-layout-ct" id="ext-comp-1050" style="width: 100%; height: 21px;"><table cellspacing="0" class="x-toolbar-ct"><tbody><tr><td align="left" class="x-toolbar-left"><table cellspacing="0"><tbody><tr class="x-toolbar-left-row"><td class="x-toolbar-cell" id="ext-gen27"><table cellspacing="0" class="x-btn x-btn-text-icon  x-btn-text-icon x-item-disabled" id="addNewAnnotationsButton" style="width: auto;"><tbody class="x-btn-small x-btn-icon-small-left"><tr><td class="x-btn-tl"><i>&nbsp;</i></td><td class="x-btn-tc"></td><td class="x-btn-tr"><i>&nbsp;</i></td></tr><tr><td class="x-btn-ml"><i>&nbsp;</i></td><td class="x-btn-mc"><em unselectable="on" class=""><button type="button" id="ext-gen28" style="background-image: url(&quot;images/icons/add.png&quot;);" class=" x-btn-text">Add New Annotation</button></em></td><td class="x-btn-mr"><i>&nbsp;</i></td></tr><tr><td class="x-btn-bl"><i>&nbsp;</i></td><td class="x-btn-bc"></td><td class="x-btn-br"><i>&nbsp;</i></td></tr></tbody></table></td><td class="x-toolbar-cell" id="ext-gen29"><table cellspacing="0" class="x-btn x-btn-text-icon  x-btn-text-icon" id="ext-comp-1051" style="width: auto;"><tbody class="x-btn-small x-btn-icon-small-left"><tr><td class="x-btn-tl"><i>&nbsp;</i></td><td class="x-btn-tc"></td><td class="x-btn-tr"><i>&nbsp;</i></td></tr><tr><td class="x-btn-ml"><i>&nbsp;</i></td><td class="x-btn-mc"><em unselectable="on" class=""><button type="button" id="ext-gen30" style="background-image: url(&quot;images/icons/find.png&quot;);" class=" x-btn-text">Search Annotations</button></em></td><td class="x-btn-mr"><i>&nbsp;</i></td></tr><tr><td class="x-btn-bl"><i>&nbsp;</i></td><td class="x-btn-bc"></td><td class="x-btn-br"><i>&nbsp;</i></td></tr></tbody></table></td></tr></tbody></table></td><td align="right" class="x-toolbar-right"><table cellspacing="0" class="x-toolbar-right-ct"><tbody><tr><td><table cellspacing="0"><tbody><tr class="x-toolbar-right-row"><td class="x-toolbar-cell x-hide-display" id="ext-gen31"><table cellspacing="0" class="x-btn   x-btn-noicon" id="logoutButton"><tbody class="x-btn-small x-btn-icon-small-left"><tr><td class="x-btn-tl"><i>&nbsp;</i></td><td class="x-btn-tc"></td><td class="x-btn-tr"><i>&nbsp;</i></td></tr><tr><td class="x-btn-ml"><i>&nbsp;</i></td><td class="x-btn-mc"><em unselectable="on" class="x-btn-arrow"><button type="button" id="ext-gen32" class=" x-btn-text">&nbsp;</button></em></td><td class="x-btn-mr"><i>&nbsp;</i></td></tr><tr><td class="x-btn-bl"><i>&nbsp;</i></td><td class="x-btn-bc"></td><td class="x-btn-br"><i>&nbsp;</i></td></tr></tbody></table></td><td class="x-toolbar-cell" id="ext-gen33"><table cellspacing="0" class="x-btn   x-btn-noicon" id="loginButton" style="width: auto;"><tbody class="x-btn-small x-btn-icon-small-left"><tr><td class="x-btn-tl"><i>&nbsp;</i></td><td class="x-btn-tc"></td><td class="x-btn-tr"><i>&nbsp;</i></td></tr><tr><td class="x-btn-ml"><i>&nbsp;</i></td><td class="x-btn-mc"><em unselectable="on" class=""><button type="button" id="ext-gen34" class=" x-btn-text">Login</button></em></td><td class="x-btn-mr"><i>&nbsp;</i></td></tr><tr><td class="x-btn-bl"><i>&nbsp;</i></td><td class="x-btn-bc"></td><td class="x-btn-br"><i>&nbsp;</i></td></tr></tbody></table></td></tr></tbody></table></td><td><table cellspacing="0"><tbody><tr class="x-toolbar-extras-row"></tr></tbody></table></td></tr></tbody></table></td></tr></tbody></table></div></div></div></div><div id="annotationMain" class=" x-panel x-panel-noborder x-border-panel" style="left: 0px; top: 53px; width: 100%;"><div class="x-panel-bwrap" id="ext-gen23"><div class="x-panel-body x-panel-body-noheader x-panel-body-noborder x-box-layout-ct" id="ext-gen24" style="width: 100%; height: 345px;"><div class="x-box-inner" id="ext-gen35" style="width: 838px; height: 345px;"><div id="mainText" style="padding: 10px; width: 100%; left: 0px; top: 0px;" class=" x-box-item">Welcome to TCGA Annotations!<br><br>Please log in to get access to restricted operations such as adding and editing notes (You will only be able to edit your own notes).</div></div></div></div></div></div></div></div></div>
   </div>

	</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
