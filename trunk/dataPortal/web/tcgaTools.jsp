<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

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
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <span class="trailDest">Tools</span></div>

			<div class="stdTitle">Tools</div>

			<div class="indent">
				<div class="stdSecondaryTitle">Analytical Tools</div>
				<div class="paragraph">Links to genomic viewers and other analytical tools</div>
	
				<div class="stdSecondaryTitle">Data Portal Tools</div>
				<ul>
					<li><b><span class="annotationsLink"><span class="greyLink">Annotations Manager</span></span></b><br/>A tool to add, edit or search for annotations to TCGA data by barcode or Universally Unique Identifier (UUID).</li>
					<li><b><span class="uuidLink"><span class="greyLink">UUID Manager</span></span></b><br/>TCGA project members use this tool to reserve blocks of UUIDs.  Anyone can use it to find UUIDs associated with legacy barcodes.</li>
				</ul>
			</div>
		</div>

		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMenuTools.jsp" />

			<jsp:include page="jspCommon/rightMoreTcgaInformation.jsp" />
		</div>

	</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
