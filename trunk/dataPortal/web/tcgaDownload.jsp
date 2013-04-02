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
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <span class="trailDest">Download Data</span></div>


            <div style="margin-top: 10px;display: inline-block;width: 455px;">
			<div class="stdTitle">Download Data</div>

			<div class="paragraph">We provide 3 ways to download data:</div>
            </div>

            <div id="mutationsLink" class="boxcomplete">
                Open-Access Validated<br/>
                <a href="tcgaMutations.jsp">Somatic Mutation Data</a>
            </div>

			<table class="gradientRoundedTable topAlign" cellspacing="0" cellpadding="3px">
				<tr>
					<th width=270>Method</th>
					<th width=165>What it offers</th>
					<th width=165>When to use it</th>
				</tr>
				<tr>
					<td align="center">
						<div id="dataMatrixButton" class="button blueButtonFill" style="margin-top:20px;" onclick="location.href='dataAccessMatrix.htm';">
							Data Matrix
						</div>
					</td>
					<td>
						Select and download subsets of data by center, platform and data types.
						<br/>
						<br/>
						Includes: Level 1, 2 and 3 data
                        <br/>
                        <br/>
                        Access the <a href="faqs.jsp">FAQ</a>
					</td>
					<td>
						Use when:
						<ul class="downloadDataWhenToUse">
							<li>You want to download data in a table format</li>
							<li>You only want a subset of the data</li>
						</ul>
					</td>
				</tr>
				<tr>
					<td align="center">
						<div id="bulkDownloadButton" class="button blueButtonFill" style="margin-top:20px;" onclick="location.href='findArchives.htm';">
							Bulk Download
						</div>
					</td>
					<td>
						A form that helps you locate files in the data archives.
						<br/>
						<br/>
						Includes: Level 1, 2, 3 and limited level 4 data
					</td>
					<td>
						Use when:
						<ul style="position: relative;top: -10px;">
							<li>You want to download bulk datasets as provided by the research centers</li>
						</ul>
					</td>
				</tr>
				<tr>
					<td>
						<div class="stdSecondaryTitle" style="margin: 20px 0 20px 20px;">Access HTTP Directories</div>
						<ul class="downloadDataWhenToUse">
							<li><a href="/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/" target="NewWin">Open-access HTTP Directory</a><div class="newWinIcon">&nbsp;</div></li>
							<li><a href="/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/" target="NewWin"target="newWin">Controlled-Access HTTP Directory</a><div class="newWinIcon">&nbsp;</div></li>
						</ul>
					</td>
					<td>
						Direct access to the HTTP directories where the data archives are stored.
						<br/>
						<br/>
						Includes: Level 1, 2, 3 and limited level 4 data.
						<br/>
						<br/>
						Login is required for the Controlled-access HTTP Directory.  See <a href="http://tcga.cancer.gov/dataportal/data/access/closed/" target="newWin">controlled-access requirements</a> <div class="newWinIcon">&nbsp;</div>.
					</td>
					<td>
						Use when:
						<ul style="position: relative;top: -10px;">
							<li>You know how to use HTTP directories and you prefer to find files yourself rather than use the Bulk Download form</li>
						</ul>
					</td>
				</tr>
			</table>
		</div>

		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMenuDownloadData.jsp" />

			<jsp:include page="jspCommon/rightControlledAccessRequirements.jsp" />

			<jsp:include page="jspCommon/rightHelp.jsp" />
		</div>

	</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
