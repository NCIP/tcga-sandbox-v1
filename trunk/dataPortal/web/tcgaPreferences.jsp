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
			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaTools.jsp">Tools</a> > <span class="trailDest">Preferences</span></div>

			<div class="stdTitle" style="margin-bottom: 15px;">Preferences</div>

			<div class="paragraph">
				Setting preferences can streamline your work with the Data Portal.
			</div>

			<div class="boxcomplete corners15 paragraph">
				<div class="stdSecondaryTitle">Default Cancer Type</div>
				
					<select size="1" name="diseaseType" class="paragraph indent">
                    <option value="BRCA">
                        BRCA - Breast invasive carcinoma
                    </option>
                    
                    <option value="COAD">
                        COAD - Colon adenocarcinoma
                    </option>
                    
                    <option selected="selected" value="GBM">
                        GBM - Glioblastoma multiforme
                    </option>
                    
                    <option value="KIRP">
                        KIRP - Kidney renal papillary cell carcinoma
                    </option>
                    
                    <option value="LUAD">
                        LUAD - Lung adenocarcinoma
                    </option>
                    
                    <option value="LUSC">
                        LUSC - Lung squamous cell carcinoma
                    </option>
                    
                    <option value="OV">
                        OV - Serous cystadenocarcinoma
                    </option>
                    
                    <option value="READ">
                        READ - Rectum adenocarcinoma
                    </option>
                    
                    <option value="UCEC">
                        UCEC - Uterine Corpus Endometrioid Carcinoma
                    </option>
                </select>
			</div>	

			<div class="boxcomplete corners15 paragraph">
				<div class="stdSecondaryTitle">Preferred Scale for Copy Number and Gene Expression Data</div>
				
				<div class="paragraph">
					<div class="preferencesStarter">&nbsp;</div>
					<div class="preferencesSelection">Log 2</div>
					<div class="preferencesSeparator">&nbsp;</div>
					<div class="preferencesSelection">Fold Number</div><br/>
					<div class="preferencesStarter">&nbsp;</div>
					<div class="preferencesSelection"><input name="log2" type="radio"></div>
					<div class="preferencesSeparator">&nbsp;</div>
					<div class="preferencesSelection"><input name="foldNumber" type="radio" checked="checked"></div>
				</div>	
			</div>	

			<div class="boxcomplete corners15 paragraph">
				<div class="stdSecondaryTitle">Default Query Type</div>
				
				<div class="paragraph">
					<div class="preferencesStarter">&nbsp;</div>
					<div class="preferencesSelection">Query Template</div>
					<div class="preferencesSeparator">&nbsp;</div>
					<div class="preferencesSelection">Saved Query</div>
					<div class="preferencesSeparator">&nbsp;</div>
					<div class="preferencesSelection">New Query</div><br/>
					<div class="preferencesStarter">&nbsp;</div>
					<div class="preferencesSelection"><input name="queryTemplate" type="radio"></div>
					<div class="preferencesSeparator">&nbsp;</div>
					<div class="preferencesSelection"><input name="savedQuery" type="radio" checked="checked"></div>
					<div class="preferencesSeparator">&nbsp;</div>
					<div class="preferencesSelection"><input name="newQuery" type="radio"></div>
				</div>	
			</div>	

			<div class="boxcomplete corners15 paragraph">
				<div class="stdSecondaryTitle" style="width: 470px;display:inline-block;">Your Saved Lists</div>
				<div class="button blueButtonFill">Add New List</div><br/>
				
				<div class="paragraph">
					<div class="savedQuery">GBM gene list</div><div class="button blueButtonFill" style="margin-right: 50px;">Edit</div><div class="button blueButtonFill">Remove</div><br/>
					<div class="savedQuery">GBM patient list</div><div class="button blueButtonFill" style="margin-right: 50px;">Edit</div><div class="button blueButtonFill">Remove</div><br/>
					<div class="savedQuery">OV gene list</div><div class="button blueButtonFill" style="margin-right: 50px;">Edit</div><div class="button blueButtonFill">Remove</div><br/>
					<div class="savedQuery">OV patient list</div><div class="button blueButtonFill" style="margin-right: 50px;">Edit</div><div class="button blueButtonFill">Remove</div><br/>
				</div>
			</div>	

			<div class="boxcomplete corners15 paragraph">
				<div class="stdSecondaryTitle" style="width: 470px;display:inline-block;">Your Saved Queries</div>
				
				<div class="button blueButtonFill">Add New List</div><br/>
				
				<div class="paragraph">
					<div class="savedQuery">GBM pathway query</div><div class="button blueButtonFill" style="margin-right: 50px;">Edit</div><div class="button blueButtonFill">Remove</div><br/>
					<div class="savedQuery">GBM CN query</div><div class="button blueButtonFill" style="margin-right: 50px;">Edit</div><div class="button blueButtonFill">Remove</div><br/>
					<div class="savedQuery">OV CN query</div><div class="button blueButtonFill" style="margin-right: 50px;">Edit</div><div class="button blueButtonFill">Remove</div><br/>
				</div>
			</div>	
		</div>

		<div class="rightColumn">
			<jsp:include page="jspCommon/rightMenuTools.jsp" />

			<jsp:include page="jspCommon/rightMoreTcgaInformation.jsp" />
		</div>

	</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
