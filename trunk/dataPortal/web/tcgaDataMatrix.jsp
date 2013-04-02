<jsp:include page="jspCommon/header.jsp" />

<jsp:include page="jspCommon/cssGwt.jsp" />
<jsp:include page="jspCommon/cssCommon.jsp" />
<jsp:include page="jspCommon/cssDataPortal.jsp" />
	<link rel="stylesheet" type="text/css" href="styles/dataMatrix.css">

<jsp:include page="jspCommon/jsExt.jsp" />
<jsp:include page="jspCommon/jsCommonUtilities.jsp" />

</head>
<body>

<jsp:include page="jspCommon/browserWarning.jsp" />

<jsp:include page="jspCommon/navMenu.jsp" />

<div class="bodyCentering">
	<div class="bodyContent">
		<div class="trail"><a href="tcgaHome2.jsp">Home</a> > <a href="tcgaDownload.jsp">Download Data</a> > <span class="trailDest">Download Using the Data Matrix</span></div>

		<div class="stdTitle">Download Using the Data Matrix</div>

<select size="1" name="diseaseType" class="paragraph">
                    
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

<div id="filterContainer">
<div id="filter" style="display:block" class="yui-skin-sam">

Select initial matrix filter settings.  To view all data, click <span style="text-decoration: underline;cursor:pointer;" onclick="location.href='tcgaDataMatrixResult.jsp'">here</span> or click "Apply" without choosing any settings. (Note: unfiltered matrix is large and can take some time to load.)

<div id="filterHead">
    Filter Settings
</div>
<div id="filterBody">

<div id="filterPanel1" class="filterPanel">

    
    <div class="filterSection">
        <div class="filterName">Data Type:</div>
        <div class="filterWidget">
            <select multiple size="4" name="platformType">
                <option value=""> All </option>

                
                
                <option value="-999">Clinical
                </option>
                
                <option value="8">Copy Number Results
                </option>
                
                <option value="6">DNA Methylation
                </option>
                
                <option value="4">Expression-Exon
                </option>
                
                <option value="3">Expression-Genes
                </option>
                
                <option value="5">Expression-miRNA
                </option>

                
                <option value="7">SNP
                </option>
                
                <option value="9">Somatic Mutations
                </option>
                
                <option value="10">Trace-Sample Relationship
                </option>
                
            </select>
        </div>
    </div>

    
    <div class="filterSection">

        <div class="filterName">Limit by Batch Number:</div>
        <div class="filterWidget">
            <select multiple size="4" name="batch">
                <option value="">All</option>

                
                
                <option value="Batch 1">Batch 1
                </option>
                
                <option value="Batch 2">Batch 2
                </option>

                
                <option value="Batch 3">Batch 3
                </option>
                
                <option value="Batch 4">Batch 4
                </option>
                
                <option value="Batch 5">Batch 5
                </option>
                
                <option value="Batch 6">Batch 6
                </option>
                
                <option value="Batch 7">Batch 7
                </option>
                
                <option value="Batch 8">Batch 8
                </option>

                
                <option value="Batch 10">Batch 10
                </option>
                
                <option value="Batch 16">Batch 16
                </option>
                
                <option value="Batch 20">Batch 20
                </option>
                
                <option value="Batch 26">Batch 26
                </option>
                
                <option value="Batch 38">Batch 38
                </option>
                
                <option value="Unclassified">Unclassified
                </option>

                
            </select>
        </div>
    </div>

    
    <div class="filterSection">
        <div class="filterName">Limit by Data Level:</div>
        <div class="filterWidget">

            
            <input type="checkbox"  id="level1" name="level"
                   value="1"> <label for="level1">Level 1
        </label><br/>

            
            <input type="checkbox"  id="level2" name="level"
                   value="2"> <label for="level2">Level 2
        </label><br/>
            
            <input type="checkbox"  id="level3" name="level"
                   value="3"> <label for="level3">Level 3
        </label><br/>
            
        </div>
    </div>

    
    <div class="filterSection">
        <div class="filterName">Limit by Availability:</div>

        <div class="filterWidget">
            <input type="checkbox"  id="availabilityAvailable"
                   name="availability" value="A">
            <label for="availabilityAvailable">Available </label><br/>
            <input type="checkbox"  id="availabilityPending"
                   name="availability" value="P">
            <label for="availabilityPending">Pending </label><br/>
            <input type="checkbox"  id="availabilityNotAvailable"
                   name="availability" value="N">
            <label for="availabilityNotAvailable">Not Available</label><br/>

        </div>
    </div>


</div>



<div id="filterPanel2" class="filterPanel">

    
    <div class="filterSection">
        <div class="filterName">Limit by CGCC Center/Platform:</div>

        <div class="filterWidget">
            <select multiple size="4" name="center">
                <option value="">All</option>
                
                <option value="9.ABI">BCM (ABI)
                </option>
                
                <option value="9.ABI">BCM (ABI)
                </option>
                
                <option value="3.ABI">BI (ABI)
                </option>
                
                <option value="3.ABI">BI (ABI)
                </option>

                
                <option value="3.Genome_Wide_SNP_6">BI (Genome_Wide_SNP_6)
                </option>
                
                <option value="3.HT_HG-U133A">BI (HT_HG-U133A)
                </option>
                
                <option value="8.HumanHap550">HAIB (HumanHap550)
                </option>
                
                <option value="2.HG-CGH-244A">HMS (HG-CGH-244A)
                </option>
                
                <option value="2.HG-CGH-415K_G4124A">HMS (HG-CGH-415K_G4124A)
                </option>
                
                <option value="6.HumanMethylation27">JHU_USC (HumanMethylation27)
                </option>

                
                <option value="6.IlluminaDNAMethylation">JHU_USC (IlluminaDNAMethylation)
                </option>
                
                <option value="4.HuEx-1_0-st-v2">LBL (HuEx-1_0-st-v2)
                </option>
                
                <option value="7.HG-CGH-244A">MSKCC (HG-CGH-244A)
                </option>
                
                <option value="5.AgilentG4502A_07">UNC (AgilentG4502A_07)
                </option>
                
                <option value="5.H-miRNA_8x15K">UNC (H-miRNA_8x15K)
                </option>
                
                <option value="10.ABI">WUSM (ABI)
                </option>

                
                <option value="10.ABI">WUSM (ABI)
                </option>
                
            </select>
        </div>
    </div>

    <div class="filterSection">
        <div class="filterName">Limit by Sample: </div>
        <div class="filterWidget" id="sampleFilter">

            <table>
                <tr>
                    <td>ID Matches:
                    </td>

                    <td>
                        <div id="sampleWidgets">

                        </div>
                        <div>

                            <span onclick="addSampleWidget()"
                                  style="cursor:pointer;color:blue;text-decoration:underline;font-size:0.8em;">Add Row </span>
                        </div>

                    </td>
                <tr/>
                <tr>
                    <td>Paste Sample List:</td>
                    <td><textarea name="sampleList" rows=4 cols=30></textarea></td>

                </tr>
                <tr>
                    <td>Upload Sample List:</td>
                    <td><input name="sampleListFile" type="file"></td>
                </tr>
            </table>
        </div>
    </div>

</div>



<div id="filterPanel3" class="filterPanel">

      
    <div class="filterSection">
        <div class="filterName">Access Tier: </div>
        
        <div class="filterWidget">
            <input type="checkbox"  id="statusProtected"
                   name="protectedStatus" value="P">
            <label for="statusProtected">Protected
            </label><br/>

            <input type="checkbox"  id="statusUnprotected"
                   name="protectedStatus" value="N">
            <label for="statusUnprotected">Public
            </label><br/>
        </div>
    </div>

              
    <div class="filterSection">
        <div class="filterName">Limit by Tumor/Normal:</div>
        
        <div class="filterWidget">

            <input type="checkbox" 
                   id="matchedNormal" name="tumorNormal"
                   value="TN">
            <label for="matchedNormal">Tumor - matched</label><br/>

            <input type="checkbox" 
                   id="noMatchedNormal" name="tumorNormal"
                   value="T">
            <label for="noMatchedNormal">Tumor - unmatched</label><br/>

            <input type="checkbox" 
                   id="normalMatchedType" name="tumorNormal"
                   value="NT">
            <label for="normalMatchedType">Normal - matched</label><br/>

            <input type="checkbox" 
                   id="normalUnmatchedType" name="tumorNormal"
                   value="N">
            <label for="normalUnmatchedType">Normal - unmatched</label><br/>
        </div>
    </div>

              
    <div class="filterSection">
        <div class="filterName">Submitted Since (Date):</div>
        <div class="filterWidget">

            <div id="startDateCalContainer"></div>
            <input type="text" size="10" name="startDate" id="startDate" maxlength="19"
                   style="color:#D3D3D3"
                   onblur="blurred(this, 'mm/dd/yyyy');" onfocus="focused(this, 'mm/dd/yyyy');"
                   value="mm/dd/yyyy">
            <button type="button" id="startDateButton" name="Pick start date"
                    style="width:20px;padding-right:0;background-position:1% 50%;background-image:url(/tcga/images/matrix/buttons/calendar.png)">
                &nbsp;</button>
        </div>
    </div>

    <div class="filterSection">
        <div class="filterName">Submitted Up To (Date):</div>

        <div class="filterWidget">
            <div id="endDateCalContainer"></div>
            <input type="text" size="10" name="endDate" id="endDate" maxlength="19"
                   style="color:#D3D3D3"
                   onblur="blurred(this, 'mm/dd/yyyy');" onfocus="focused(this, 'mm/dd/yyyy');"
                   value="mm/dd/yyyy">
            <button type="button" id="endDateButton" name="Pick end date"
                    style="width:20px;padding-right:0;background-position:1% 50%;background-image:url(/tcga/images/matrix/buttons/calendar.png)">
                &nbsp;</button>
        </div>
    </div>
</div>


<div style="clear:both;text-align:right;">
    <button onclick="location.href='tcgaDataMatrixResult.jsp'" name="Filter" value="Apply" type="button" class="positiveButton"
            style="background-image:url(/tcga/images/matrix/buttons/check.png)"><span>Apply</span></button>
    <button type="button" name="Clear filter" onclick="clearFilterForm()">Clear</button>

        
</div>
</div>
<!-- end filterBody -->
</div>
<!-- end filter div -->
</div>
<!-- end filterContainer -->


	</div>
</div>

<jsp:include page="jspCommon/footer.jsp" />
