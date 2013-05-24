
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="shortcut icon" href="/tcga/images/general/tcga.a" />
        <script type="text/javascript" src="/tcga/scripts/main.js?vers=20111011-0829"></script>

<!-- site css -->
        <link rel="stylesheet" href="/tcga/styles/main.css?vers=20111003" media="all" />

        <script type="text/javascript">
            var checkForIe6 = function() {
                var ua = navigator.userAgent;
                var re  = new RegExp("MSIE ([0-9]{1,}[\\.0-9]{0,})");
                if (re.exec(ua) != null) {
                    var rv = parseFloat( RegExp.$1 );
                    if ( rv < 7) {
                        document.getElementById('browserWarning').style.display = 'block';
                    }
                }
            }

            var checkForIe7 = function() {
                var ua = navigator.userAgent;
                var re  = new RegExp("MSIE ([0-9]{1,}[\\.0-9]{0,})");
                if (re.exec(ua) != null) {
                    var rv = parseFloat( RegExp.$1 );
                    if ( rv == 7) {
                        return true;
                    }
                }

                 return false;
            }

            $(document).ready(function() {
                 checkForIe6();
            });
        </script>






    <title>TCGA Data Portal: Publications</title>
</head>

    <body>
        <div id="browserWarning" style="display:none;">
			<div id="browserWarningClose"
				onclick="document.getElementById('browserWarning').style.display='none'">

				<img src="images/common/x_red.png" alt="Close">
	
			</div>
	
			<div id="browserWarningTitle" class="stdTitle">
				Warning:
			</div>
			<div id="browserWarningMessage">
				Internet Explorer 6 is no longer supported by the TCGA
				Data Portal. Please use one of these supported browsers:
				<a href="http://www.microsoft.com/windows/internet-explorer/">Internet
				Explorer 7+</a>, <a href="http://getfirefox.com/">Firefox</a>,
				<a href="http://www.google.com/chrome">Google Chrome</a>, or
				<a href="http://www.apple.com/safari/download/">Safari</a>.
			</div>

		</div>
        <!-- NCI Banner (please keep all code on one line for browsers spacing issue) -->
<div id="nci-banner">
<ul id="nci-banner-list"><li><a href="http://www.cancer.gov" target="_blank"><img src="http://tcga-data.nci.nih.gov/tcga/images/general/nci-banner.gif" width="446" height="36" alt="National Cancer Institute" /></a></li><li><a href="http://www.genome.gov/" target="_blank"><img src="http://tcga-data.nci.nih.gov/tcga/images/general/nhgri-banner.gif" width="527" height="36" alt="The National Human Genome Research Institute" /></a></li></ul>
</div>

<!-- END NCI Banner -->
        <!-- Masthead (Logo, utility links, search) -->
<div id="masthead">
	<!--[if lt IE 7]>
<style type="text/css">
#logo {
{ behavior: url(htc/iepngfix.htc);
}
</style>
<![endif]-->
	<a href="http://tcga-data.nci.nih.gov/tcga"><img src="http://tcga-data.nci.nih.gov/tcga/images/general/tcga-dp_logo.png" width="502" height="54" alt="The Cancer Genome Atlas logo" id="logo" /></a>
	<ol id="utility-links">
		<li><a href="http://cancergenome.nih.gov" target="_blank">TCGA Home</a></li>
		<li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaContact.jsp">Contact Us</a></li>

		<li class="last"><a href="http://cancergenome.nih.gov/newsevents/forthemedia" target="_blank">For the Media</a></li>
	</ol>
</div>

<!-- END Masthead (Logo, utility links, search) -->
        <!-- Main Navigation -->
<!--[if lt IE 7]>
<style type="text/css">
#mainnav ul {
	margin-bottom: -7px;
}
</style>
<![endif]-->
<div id="mainnav">
	<ul id="mainnavlist" class="level1">
		<li class="level1-li nav0"><a class="level1-a" href="http://tcga-data.nci.nih.gov/tcga/tcgaHome2.jsp"><span>Home</span></a></li>

		<li class="level1-li nav2"><a class="level1-a" href="http://tcga-data.nci.nih.gov/tcga/tcgaDownload.jsp"><span>Download Data</span></a>
			<ul class="time">
                <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaDownload.jsp">Download Data</a></li>
                <li><a href="http://tcga-data.nci.nih.gov/tcga/dataAccessMatrix.htm">Data Matrix</a></li>
                <li><a href="http://tcga-data.nci.nih.gov/tcga/findArchives.htm">Bulk Download</a></li>

                <li><a href="http://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/">Open-Access HTTP Directory</a></li>
                <li><a class="last" href="https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/">Controlled-Access HTTP Directory</a></li>
			</ul>
		</li>
		<li class="level1-li nav3"><a class="level1-a" href="http://tcga-data.nci.nih.gov/tcga/tcgaTools.jsp"><span>Tools</span></a>
			<ul class="time">
				<li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaTools.jsp">Tools</a></li>
				<li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaAnalyticalTools.jsp">Analytical Tools</a></li>
				<li><a href="https://tcga-data.nci.nih.gov/annotations/">Annotations Manager</a></li>
				<li><a class="last" href="https://tcga-data.nci.nih.gov/uuid/uuidBrowser.htm">Biospecimen Metadata Browser</a></li>
			</ul>
		</li>
		<li class="level1-li nav4"><a class="level1-a" href="http://tcga-data.nci.nih.gov/tcga/tcgaAbout.jsp"><span>About the Data</span></a>

            <ul class="time">
				 <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaAbout.jsp">About the Data</a></li>
				 <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaDataType.jsp">Data Levels and Data Types</a></li>
				 <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaPlatformDesign.jsp">Platform Design</a></li>
				 <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaAccessTiers.jsp">Access Tiers</a></li>
				 <li><a href="http://tcga-data.nci.nih.gov/datareports/">Reports</a></li>

				 <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaAnnouncements.jsp">Announcements</a></li>
				 <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaHelp.jsp">User Guides and Help</a></li>
				 <li><a class="last" href="http://tcga-data.nci.nih.gov/docs/publications">Publications</a></li>
			</ul>
		</li>
	</ul>
</div>
<!-- END Main Navigation -->

        <!-- Container for Content and Sidebar -->

        <div id="container">
			<div id="content">
			   <div class="trail"><a href="http://tcga-data.nci.nih.gov/tcga/tcgaHome2.jsp">Home</a> > <span class="trailDest">Tumor Charts</span></div>
<div id="results">

</div>

	<div id="cancerDetailsNewChart"></div>

	<script src="/tcga/Charts/FusionCharts.js" type="text/javascript" ></script>
	<script>
<%
String matchedColor;
String unmatchedColor;
String tumorColor;
String roundedges;
String diseaseType;

if (request.getParameter("matchedColor") == null) {
	matchedColor = "999966";
	unmatchedColor = "336699";
	tumorColor = "993300";
	roundedges = "1";
	diseaseType= "GBM";
} else {
	matchedColor = request.getParameter("matchedColor");
	unmatchedColor = request.getParameter("unmatchedColor");
	tumorColor = request.getParameter("tumorColor");
	roundedges = request.getParameter("roundedges");
	diseaseType = request.getParameter("diseaseType");
}
%>    
$(document).ready(function(){

var jsonURL ="/tcga/damws/tumordetails/json?diseaseType=<%=diseaseType%>";


$.getJSON(jsonURL, function(json) {
   var datasetSettings = "[";

   var yAxisMaxValue = 0; 
   var yAxisMaxValueTemp = 0; 

   for(i=0; i<json.tumorSampleTypeCount.length;i++){
   	yAxisMaxValueTemp = 0; 
        if( i==0 ){
	  bgcolor = "<%=tumorColor%>";
   	}else if( i == 1 ){
	  datasetSettings +=  ",";
	  bgcolor = "<%=matchedColor%>";
	
   	} else if( i == 2 ){
	  datasetSettings +=  ",";
	  bgcolor = "<%=unmatchedColor%>";
	
   	} 

	datasetSettings +=  "{\"seriesname\":\"" + json.tumorSampleTypeCount[i].sampleType + "\",\"color\":\"" + bgcolor + "\",\"showvalues\":\"0\",";
	datasetSettings +=  "\"data\":[";
        datasetSettings +=  "{\"value\":\"" + json.tumorSampleTypeCount[i].copyNumber + "\"},";
	yAxisMaxValueTemp = (yAxisMaxValueTemp > json.tumorSampleTypeCount[i].copyNumber) ? yAxisMaxValueTemp : json.tumorSampleTypeCount[i].copyNumber;
        datasetSettings +=  "{\"value\":\"" + json.tumorSampleTypeCount[i].methylation + "\"},"
	yAxisMaxValueTemp = (yAxisMaxValueTemp > json.tumorSampleTypeCount[i].methylation) ? yAxisMaxValueTemp : json.tumorSampleTypeCount[i].methylation;
        datasetSettings +=  "{ \"value\":\"" + json.tumorSampleTypeCount[i].geneExpression + "\"},";
	yAxisMaxValueTemp = (yAxisMaxValueTemp > json.tumorSampleTypeCount[i].geneExpression) ? yAxisMaxValueTemp : json.tumorSampleTypeCount[i].geneExpression;
        datasetSettings +=  "{\"value\":\"" + json.tumorSampleTypeCount[i].miRnaExpression + "\"}";
	yAxisMaxValueTemp = (yAxisMaxValueTemp > json.tumorSampleTypeCount[i].miRnaExpression) ? yAxisMaxValueTemp : json.tumorSampleTypeCount[i].miRnaExpression;
        datasetSettings +=  "]}";  
  	yAxisMaxValue += yAxisMaxValueTemp;
   }
  datasetSettings += "]";

  		var chartSetting = '{"palette":"2","caption":"Data Type Summary","showlabels":"1","showvalues":"1","numberprefix":"","showsum":"1", "formatnumberscale":"0", "subcaption": "by Number of Samples","xaxisname": "Data Type","Yaxisname": "Number of Samples","useroundedges":"<%=roundedges%>", "legendborderalpha":"0"}';
		var categoriesSetting = '[{"category":[{"label":"Copy Number"}, {"label":"Methylation"},{"label":"Gene Expression"},{"label":"miRNA Expression" } ]}]';
		var strJSON = '{"chart":' + chartSetting + ',' + '"categories":' + categoriesSetting + ',' + '"dataset":' + datasetSettings + '}'; 
  var myChart = FusionCharts.render( "/tcga/Charts/StackedColumn2D.swf", "myChartId", "620", "500", "cancerDetailsNewChart", strJSON , "json" ); 

 });
});

	</script>
          </div>

	  <div id="sidebar">
				<div id="menuAbout" class="box boxMenu">
				    <h3>Chart Configuration</h3>
				    <ul class="boxbody">
<form method="post" target="">
<label style="width:100px;">diseaseType: </label><input type="text" name ="diseaseType" value="<%=diseaseType%>"/><br />
<label style="width:100px;">unmatched color: </label><input type="text" name ="unmatchedColor" value="<%=unmatchedColor%>"/><br />
<label style="width:100px;">matched color: </label><input type="text" name ="matchedColor" value="<%=matchedColor%>"/><br />
<label style="width:100px;">tumor color: </label><input type="text" name ="tumorColor" value="<%=tumorColor%>"/><br />
<label style="width:100px;">roundedges: </label><input type="text" name ="roundedges" value="<%=roundedges%>"/><br /><br />
<input type="submit" value="Submit" />
</form>
				    </ul>

				</div>
			</div>


        </div>
        <!-- Footer -->
<div id="footer">
  <div id="footer-text">
      <ul>
        <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaHome2.jsp">TCGA Data Portal Home</a></li>
        <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaSiteMap.jsp">Site Map</a></li>

        <li class="last"><a href="http://tcga-data.nci.nih.gov/tcga/tcgaHelp.jsp#ncicbSupport">Report a Problem</a></li>
      </ul>
      <ul>
        <li><a href="http://cancergenome.nih.gov/" target="_blank">TCGA Home</a></li>
        <li><a href="http://cancergenome.nih.gov/abouttcga/peoplecontacts/tcgacontacts" target="_blank">Contact Us</a></li>
        <li><a href="http://cancergenome.nih.gov/global/policies" target="_blank">Web Site Policies</a></li>
        <li><a href="http://cancergenome.nih.gov/global" target="_blank">Accessibility</a></li>

        <li class="last"><a href="http://cancergenome.nih.gov/rss" target="_blank">RSS</a></li>
      </ul>
  </div>
  <ul id="footer-icons">
    <li><a href="http://www.cancer.gov" target="_blank"><img src="http://tcga-data.nci.nih.gov/tcga/images/general/footer-nci.gif" width="44" height="32" alt="National Cancer Institute" /></a></li>
    <li><a href="http://www.genome.gov/" target="_blank"><img src="http://tcga-data.nci.nih.gov/tcga/images/general/footer-nhgri.gif" width="63" height="32" alt="The National Human Genome Research Institute" /></a></li>
    <li><a href="http://www.nih.gov" target="_blank"><img src="http://tcga-data.nci.nih.gov/tcga/images/general/footer-nih.gif" width="29" height="32" alt="National Institutes of Health" /></a></li>
    <li><a href="http://www.dhhs.gov/" target="_blank"><img src="http://tcga-data.nci.nih.gov/tcga/images/general/footer-hhs.gif" width="31" height="32" alt="United States Department of Health and Human Services" /></a></li>

    <li class="last"><a href="http://www.usa.gov/" target="_blank"><img src="http://tcga-data.nci.nih.gov/tcga/images/general/footer-usagov.gif" width="83" height="32" alt="USA.gov: The U.S. Government's Official Web Portal" /></a></li>
  </ul>
</div>
    </body>
</html>
