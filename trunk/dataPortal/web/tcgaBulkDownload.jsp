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

			<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaDownload.jsp">Download Data</a> > <span class="trailDest">Bulk Download</span></div>
	
			<div class="stdTitle">Bulk Download from the Archives</div>

			<div class="paragraph">
				Use the form below to select data sets:
			</div>

            <table style="border: solid 1px #999999;" cellpadding="0" cellspacing="0" align="center" bgcolor="#F3F3F3">
                <tr>

                    <td align="left">
                        <table border=0 align="center" cellpadding=5 cellspacing=0 bgcolor="#F3F3F3">
                            <table border=0 cellpadding=3 cellspacing=3>
                                
                                    <tr>
                                        <td><strong><a
                                                href="http://cancergenome.nih.gov/dataportal/contact/tcga_portal_help.asp#Cancer%20Type"
                                                target="_help">Cancer Type</a></strong></td>
                                        <td>
                                            <select name="project" multiple="multiple"
                                                    size="5">
                                                <option value="-1" selected>All</option>

                                                
                                                    <option value="1">
                                                        Glioblastoma multiforme
                                                    </option>
                                                
                                                    <option value="3">
                                                        Ovarian serous cystadenocarcinoma
                                                    </option>
                                                
                                                    <option value="2">
                                                        Lung squamous cell carcinoma
                                                    </option>
                                                
                                                    <option value="4">

                                                        Lung adenocarcinoma
                                                    </option>
                                                
                                                    <option value="5">
                                                        Breast invasive carcinoma
                                                    </option>
                                                
                                                    <option value="6">
                                                        Colon adenocarcinoma
                                                    </option>
                                                
                                                    <option value="7">
                                                        Kidney renal clear cell carcinoma
                                                    </option>

                                                
                                                    <option value="8">
                                                        Kidney renal papillary cell carcinoma
                                                    </option>
                                                
                                                    <option value="9">
                                                        Stomach adenocarcinoma
                                                    </option>
                                                
                                                    <option value="10">
                                                        Head and Neck squamous cell carcinoma
                                                    </option>
                                                
                                                    <option value="11">

                                                        Liver hepatocellular carcinoma
                                                    </option>
                                                
                                                    <option value="12">
                                                        Cervical Squamous Cell Carcinoma
                                                    </option>
                                                
                                                    <option value="13">
                                                        Acute Myeloid Leukemia
                                                    </option>
                                                
                                                    <option value="14">
                                                        Chronic Lymphocytic Leukemia
                                                    </option>

                                                
                                                    <option value="15">
                                                        Cutaneous Melanoma
                                                    </option>
                                                
                                                    <option value="16">
                                                        Urothelial Carcinoma - Non-Papillary (NP)
                                                    </option>
                                                
                                                    <option value="17">
                                                        Urothelial Carcinoma - Papillary (P)
                                                    </option>
                                                
                                                    <option value="18">

                                                        Lymphoid Neoplasm Non-Hodgkins Lymphoma
                                                    </option>
                                                
                                                    <option value="20">
                                                        Thyroid carcinoma
                                                    </option>
                                                
                                                    <option value="21">
                                                        Brain Lower Grade Glioma
                                                    </option>
                                                
                                                    <option value="22">
                                                        Prostate adenocarcinoma
                                                    </option>

                                                
                                                    <option value="23">
                                                        Uterine Corpus Endometrioid Carcinoma
                                                    </option>
                                                
                                                    <option value="24">
                                                        Rectum adenocarcinoma
                                                    </option>
                                                
                                            </select></td>
                                    </tr>
                                
                                
                                    <tr>
                                        <td><strong><a
                                                href="http://cancergenome.nih.gov/dataportal/contact/tcga_portal_help.asp#Center"
                                                target="_help">Center</a></strong></td>

                                        <td><select name="center" multiple="multiple"
                                                    size="5">
                                            <option value="-1" selected>All</option>
                                            
                                                <option value="9">
                                                    Baylor College of Medicine
                                                </option>
                                            
                                                <option value="3">
                                                    Broad Institute of MIT and Harvard
                                                </option>
                                            
                                                <option value="2">

                                                    Harvard Medical School
                                                </option>
                                            
                                                <option value="1">
                                                    IGC Biospecimen Core Resource
                                                </option>
                                            
                                                <option value="6">
                                                    Johns Hopkins / University of Southern California
                                                </option>
                                            
                                                <option value="4">
                                                    Lawrence Berkeley National Laboratory
                                                </option>

                                            
                                                <option value="7">
                                                    Memorial Sloan-Kettering Cancer Center
                                                </option>
                                            
                                                <option value="5">
                                                    University of North Carolina
                                                </option>
                                            
                                                <option value="8">
                                                    HudsonAlpha Institute for Biotechnology
                                                </option>
                                            
                                                <option value="10">

                                                    Washington University School of Medicine
                                                </option>
                                            
                                                <option value="11">
                                                    Nationwide Children&#039;s Hospital
                                                </option>
                                            
                                                <option value="12">
                                                    Canada&#039;s Michael Smith Genome Sciences Centre
                                                </option>
                                            
                                        </select></td>

                                    </tr>
                                

                                
                                    <tr>
                                        <td><strong><a
                                                href="http://cancergenome.nih.gov/dataportal/contact/tcga_portal_help.asp#Platform"
                                                target="_help">Platform</a></strong></td>
                                        <td><select name="platform" multiple="multiple"
                                                    size="5">
                                            <option value="-1" selected>All</option>
                                            
                                                
                                                    <option value="3">
                                                        Affymetrix HT Human Genome U133 Array Plate Set
                                                    </option>

                                                
                                            
                                                
                                                    <option value="4">
                                                        Affymetrix Human Exon 1.0 ST Array
                                                    </option>
                                                
                                            
                                                
                                                    <option value="19">
                                                        Affymetrix Genome-Wide Human SNP Array 6.0
                                                    </option>
                                                
                                            
                                                
                                                    <option value="40">
                                                        Agilent Human Genome CGH Custom Microarray 2x415K
                                                    </option>
                                                
                                            
                                                
                                                    <option value="10">

                                                        Agilent Human Genome CGH Microarray 244A
                                                    </option>
                                                
                                            
                                                
                                                    <option value="13639">
                                                        Agilent 244K Custom Gene Expression G4502A-07-2
                                                    </option>
                                                
                                            
                                                
                                                    <option value="25">
                                                        Agilent 244K Custom Gene Expression G4502A-07-1
                                                    </option>
                                                
                                            
                                                
                                                    <option value="38">
                                                        Agilent Human miRNA Microarray Rel12.0
                                                    </option>

                                                
                                            
                                                
                                                    <option value="31">
                                                        Agilent 244K Custom Gene Expression G4502A-07-3
                                                    </option>
                                                
                                            
                                                
                                                    <option value="21">
                                                        Agilent 8 x 15K Human miRNA-specific microarray
                                                    </option>
                                                
                                            
                                                
                                                    <option value="37">
                                                        Agilent SurePrint G3 Human CGH Microarray Kit 1x1M
                                                    </option>
                                                
                                            
                                                
                                                    <option value="138187">

                                                        Illumina Infinium Human DNA Methylation 27
                                                    </option>
                                                
                                            
                                                
                                                    <option value="39">
                                                        Illumina Genome Analyzer mRNA Digital Gene Expression
                                                    </option>
                                                
                                            
                                                
                                                    <option value="14">
                                                        Illumina DNA Methylation OMA002 Cancer Panel I
                                                    </option>
                                                
                                            
                                                
                                                    <option value="130">
                                                        Illumina DNA Methylation OMA003 Cancer Panel I
                                                    </option>

                                                
                                            
                                                
                                                    <option value="36">
                                                        Illumina Human1M-Duo BeadChip
                                                    </option>
                                                
                                            
                                                
                                                    <option value="15">
                                                        Illumina 550K Infinium HumanHap550 SNP Chip
                                                    </option>
                                                
                                            
                                                
                                                    <option value="17">
                                                        Applied Biosystems Sequence data
                                                    </option>
                                                
                                            
                                        </select></td>

                                    </tr>
                                

                                
                                    <tr>
                                        <td><strong><a
                                                href="http://cancergenome.nih.gov/dataportal/contact/tcga_portal_help.asp#Data%20Type"
                                                target="_help">Data Type</a></strong></td>
                                        <td><select name="dataType" multiple="multiple"
                                                    size="5">
                                            <option value="-1" selected>All</option>
                                            
                                                <option value="3">
                                                    Expression-Genes
                                                </option>

                                            
                                                <option value="4">
                                                    Expression-Exon
                                                </option>
                                            
                                                <option value="5">
                                                    Expression-miRNA
                                                </option>
                                            
                                                <option value="8">
                                                    Copy Number Results
                                                </option>
                                            
                                                <option value="6">

                                                    DNA Methylation
                                                </option>
                                            
                                                <option value="7">
                                                    SNP
                                                </option>
                                            
                                                <option value="10">
                                                    Trace-Sample Relationship
                                                </option>
                                            
                                                <option value="9">
                                                    Somatic Mutations
                                                </option>

                                            
                                                <option value="1">
                                                    Complete Clinical Set
                                                </option>
                                            
                                                <option value="2">
                                                    Minimal Clinical Set
                                                </option>
                                            
                                                <option value="14">
                                                    Tissue Slide Images
                                                </option>
                                            
                                        </select></td>

                                    </tr>
                                
                                
                                    <tr>
                                        <td><strong>File Name</strong><br>(Full or Partial)</td>
                                        <td><input type="text" name="fileName" size="40"
                                                   maxlength="255"><br/>
                                            <span style="font-size:0.8em;color:gray;margin-top:-2px;">To locate the latest mutation files, enter "maf" here</span>
                                        </td>
                                    </tr>

                                

                                <tr>
                                    <td valign="top"><strong>Submission Date</strong></td>
                                    <td>
                                        <input type="text" name="dateStart" size="10"
                                               maxlength="10" value="1/1/07">
                                     -
                                        
                                        
                                            <input type="text" name="dateEnd" size="10"
                                                   maxlength="10"
                                                   value="6/11/10">
                                        <br>
                                                   On or After &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Before
                                    </td>

                                </tr>

                            </table>
                            <tr>
                                <td colspan=2 align=center><input type="reset" value="Reset">&nbsp;&nbsp;<input
                                        type="submit" value="Find"></td>
                            </tr>
                        </table>

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
