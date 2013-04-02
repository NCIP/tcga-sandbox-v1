<?xml version="1.0"?>
<!-- $Id: dd.xsl 9733 2011-02-10 01:30:52Z jensenma $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:dd="http://tcga.nci.nih.gov/BCR/DataDictionary/1.0">
  <xsl:output method="html"/>

  <xsl:template match="/">
    <html>
      <head>
	<style type="text/css">
	  table {
	    border: 3px ridge black;
	  }
	  td {
	    border: 1px solid lightBlue;
	    padding: 2px;
	    max-width: 250px;
	  }
	  td.defn {
	  font-family : sans-serif;
	  font-size : smaller;
	  }
	  th {
	    border: 1px solid;
	    padding: 2px;
	  }
	</style>
      </head>
      <body>
	<h1><a href="https://tcga-data.nci.nih.gov/tcga/">The Cancer Genome Atlas</a></h1>
	<h1>Biospecimen Core Resource Data Dictionary</h1>
	<h2>version <xsl:value-of select="dd:dictionary/@version"/></h2>

	Click CDE ID link to visit the entry's NCI <a href="https://cdebrowser.nci.nih.gov/CDEBrowser/">CDE Browser</a> page.<br/>
	Mouse over dictionary entries to view definitions.<br/>
	Tumor-specific entries are annotated with their associated tumor types. Mouse over to see full names.<p/>
	
	<table>
	  <thead>
	    <th>CDE Public Id</th>
	    <th>CRF Question Text</th>
	    <th>Data Element</th>
	    <th>Definition</th>
	    <th><a href="http://tcga-data.nci.nih.gov/datareports/codeTablesReport.htm?codeTable=Disease" target="_blank">Tumor Types</a></th>
	  </thead>
	  <tbody>
	    <xsl:for-each select="dd:dictionary/dd:dictEntry">
	      <xsl:sort select="./@name"/>
	      <xsl:apply-templates select="."/>
	    </xsl:for-each>
	  </tbody>
	</table>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="dd:dictEntry">
    <tr>
      <td><a href="{./dd:caDSRInfo/@xlink:href}"><xsl:value-of select="dd:caDSRInfo/@public_id"/></a></td>
      <td><xsl:value-of select="dd:TCGAInfo/dd:CRFquestionText"/></td>
      <td><xsl:value-of select="@name"/></td>
      <!-- if TCGAadditionalExplanation is present, use this as the displayed definition.
           if not, then use the shorter of caDSRalternateDefinition and CRFcaBIGdefinition,
	   unless caDSRalternateDefinition is not present, then use CRFcaBIGdefinition. -->
      <xsl:variable name="defText1">
	<xsl:choose>
	  <xsl:when test="dd:TCGAInfo/dd:TCGAadditionalExplanation">
	    <xsl:value-of select="dd:TCGAInfo/dd:TCGAadditionalExplanation"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:choose>
	      <xsl:when test="dd:caDSRInfo/dd:caDSRalternateDefinition">
		<xsl:choose>
		  <xsl:when test="dd:TCGAInfo/dd:CRFcaBIGdefinition">
		    <xsl:choose>
		      <xsl:when test="string-length(dd:caDSRInfo/dd:caDSRalternateDefinition) &lt; string-length(dd:TCGAInfo/dd:CRFcaBIGdefinition)">
			<xsl:value-of select="dd:caDSRInfo/dd:caDSRalternateDefinition"/>
		      </xsl:when>
		      <xsl:otherwise>
			<xsl:value-of select="dd:TCGAInfo/dd:CRFcaBIGdefinition"/>
		      </xsl:otherwise>
		    </xsl:choose>
		  </xsl:when>
		  <xsl:otherwise>
		    <xsl:value-of select="dd:caDSRInfo/dd:caDSRalternateDefinition"/>
		  </xsl:otherwise>
		</xsl:choose>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:value-of select="dd:TCGAInfo/dd:CRFcaBIGdefinition"/>
	      </xsl:otherwise>
	    </xsl:choose>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:variable>
      <!-- however, sometimes the caDSRdefinition is the right one, or the only one -->
      <xsl:variable name="defText">
	<xsl:choose>
	  <xsl:when test="string-length($defText1) &gt; 0">
	    <xsl:choose>
	      <xsl:when test="string-length($defText1) &lt; string-length(dd:caDSRInfo/dd:caDSRdefinition)">
		<xsl:value-of select="$defText1"/>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:value-of select="dd:caDSRInfo/dd:caDSRdefinition"/>
	      </xsl:otherwise>
	    </xsl:choose>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="dd:caDSRInfo/dd:caDSRdefinition"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:variable>
      <xsl:choose>
	<xsl:when test="string-length($defText) &gt; 0">
	  <td class="defn" ><xsl:value-of select="$defText"/></td>
	</xsl:when>
	<xsl:otherwise>
	  <td class="defn"><xsl:text>Click CDE ID for more information</xsl:text></td>
	</xsl:otherwise>
      </xsl:choose>
      <td><xsl:apply-templates select="./dd:studies"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="dd:dictEntry/dd:studies">
    <xsl:for-each select="dd:study">
      <xsl:variable name="abbrev" select="."/>
      <xsl:choose>
	<xsl:when test="text()='LAML'">
	  <span title="Acute Myeloid Leukemia"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='LGG'">
	  <span title="Brain Lower Grade Glioma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when> 
	<xsl:when test="text()='BRCA'">
	  <span title="Breast invasive carcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='CESC'"> 
	  <span title="Cervical Squamous Cell Carcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='COAD'">
	  <span title="Colon adenocarcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='GBM'">
	  <span title="Glioblastoma multiforme"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='HNSC'">
	  <span title="Head and Neck squamous cell carcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='KIRC'">
	  <span title="Kidney renal clear cell carcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='KIRP'">
	  <span title="Kidney renal papillary cell carcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='LUAD'">
	  <span title="Lung adenocarcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='LUSC'">
	  <span title="Lung squamous cell carcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='OV'">
	  <span title="Ovarian serous cystadenocarcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='READ'">
	  <span title="Rectum adenocarcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='STAD'">
	  <span title="Stomach adenocarcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='THCA'">
	  <span title="Thyroid carcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:when test="text()='UCEC'">
	    <span title="Uterine Corpus Endometrioid Carcinoma"><xsl:value-of select="$abbrev"/></span>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="$abbrev"/>
	</xsl:otherwise>
      </xsl:choose>
      <xsl:text> </xsl:text>
    </xsl:for-each>
  </xsl:template>
  
  <xsl:template match="text()"/>

</xsl:stylesheet>
		