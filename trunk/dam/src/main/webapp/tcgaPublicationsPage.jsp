<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="shortcut icon" href="http://tcga-data.nci.nih.gov/tcga/images/general/tcga.a" />
<!-- site css -->
        <link rel="stylesheet" href="http://tcga-data.nci.nih.gov/tcga/styles/main.css" media="all" />

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





    <title>TCGA Data Portal: TCGA Publications</title>
        <div style="position:absolute;">
            <a href="#skip">
                <img src="http://cabig-ut.nci.nih.gov/skipnav.gif" border="0" height="1" width="1" alt="Skip Navigation" title="Skip Navigation" />
            </a>
        </div>
</head>

    <body>
    <a name="skip" id="skip"></a>
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

		<li class="level1-li nav1"><a class="level1-a" href="http://tcga-portal.nci.nih.gov/tcga-portal/"><span>Query the Data</span></a></li>
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
				 <li><a href="http://tcga-data.nci.nih.gov/docs/publications">TCGA Publications</a></li>
				 <li><a class="last" href="http://cancergenome.nih.gov/publications" target="_blank">Publications using TCGA Data</a></li>
			</ul>

		</li>
		<li class="level1-li nav5"><a class="level1-a" target="_blank" href="http://cancergenome.nih.gov/abouttcga/policies/publicationguidelines"><span>Publication Guidelines</span></a></li>
	</ul>
</div>
<!-- END Main Navigation -->

        <!-- Container for Content and Sidebar -->

        <div id="container">
			<div id="content">
			   <div class="trail"><a href="/tcga/tcgaHome2.jsp">Home</a> > <span class="trailDest">TCGA Publications</span></div>

				<h2>The Cancer Genome Atlas Research Network Publications</h2>
		        <h3>
				  <!--Holding place for link to publication and DOI -->

			    2011</h3>
		        <ul>
		          <li><em><strong>Integrated Genomic Analyses of Ovarian Carcinom</strong></em></li>
		            <ul>

		              <li><a href="http://www.nature.com/nature/journal/v474/n7353/full/nature10166.html">Nature, Volume 474 Number 7353, June 30, 2011 [doi:10.1038/nature10166]</a> </li>
	                  <li><a href="http://tcga-data.nci.nih.gov/docs/publications/ov_2011/">Portal Publication Site and Associated Data Files</a></li>

		            </ul>
	          </ul>
		        <h3>2010</h3>
		        <ul>

		            <li><em><strong>An integrated genomic analysis identifies clinically relevant subtypes of glioblastoma characterized by abnormalities in PDGFRA, IDH1, EGFR and NF1</strong>	                </em></li>
		          <ul>
		            <li><a href="http://www.sciencedirect.com/science?_ob=ArticleURL&amp;_udi=B6WWK-4Y6CBS9-C&amp;_user=10843&amp;_coverDate=01%2F19%2F2010&amp;_rdoc=13&amp;_fmt=high&amp;_orig=browse&amp;_srch=doc-info%28%23toc%237133%232010%23999829998%231609092%23FLA%23display%23Volume%29&amp;_cdi=7133&amp;_sort=d&amp;_docanchor=&amp;_ct=13&amp;_acct=C000000150&amp;_version=1&amp;_urlVersion=0&amp;_userid=10843&amp;md5=1a2f39e3d0070ee3b80ca5480865ec38">Cancer Cell, Volume 17, Issue 1, 19 January 2010, Pages 98-110 [doi:10.1016/j.ccr.2009.12.020]</a></li>

                    <li><a href="http://tcga-data.nci.nih.gov/docs/publications/gbm_exp/">Portal Publication Site and Associated Data Files</a></li>
                    </ul>
	          </ul>

	          <h3>2008</h3>
	            <ul>
                <li> <strong><i>Comprehensive genomic characterization defines human glioblastoma genes and core pathways</i></strong>

                    <ul>
                      <li><a href="http://www.nature.com/nature/journal/vaop/ncurrent/full/nature07385.html">Nature, Volume 455 Number 7209, September 4, 2008 [doi:10.1038/nature07385]</a></li>
                      <li><a href="http://tcga-data.nci.nih.gov/docs/publications/gbm_2008/">Portal Publication Site and Associated Data Files</a></li>

                    </ul>
                </li>
              </ul>
              <br /><br />
              <h3>TCGA Related Publications</h3>

              <ul>
                <li><a href="http://cancergenome.nih.gov/publications">Publications from the TCGA Research Network and from other investigators who have successfully leveraged the TCGA data for their own work.</a></li>

              </ul>
              <p><em><strong>Disclaimer</strong>: the results provided by the links below are not necessarily complete or TCGA-specific.</em>
              </p>
              <ul>
                <li><a href="http://www.ncbi.nlm.nih.gov/pubmed?term=cancer+genome+atlas+TCGA">PubMed</a></li>

                <li><a href="http://scholar.google.com/scholar?hl=en&amp;q=cancer+genome+atlas+TCGA&amp;spell=1">Google Scholar</a></li>

              </ul>
              <br /><br />
              <h3>Views of the Data</h3>
              <ul>
                <li><a href="http://tcga-data.nci.nih.gov/tcga/dataAccessMatrix.htm">Data Access Matrix</a>: a tool for creating customized archives of TCGA data</li>
                <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaAnalyticalTools.jsp">Analytical Tools</a></li>

              </ul>
              <br /><br />
              <h3>Additional Information</h3>
              <ul>
                <li><a href="https://wiki.nci.nih.gov/display/TCGA">TCGA Wiki</a>
                  <ul>
                    <li>Descriptions of TCGA data are provided in the <a href="https://wiki.nci.nih.gov/display/TCGA/TCGA+Data+Primer">TCGA Data Primer</a></li>

                  </ul>
                </li>
                <li><a href="http://tcga-data.nci.nih.gov/datareports/">TCGA Data Coordinating Center Reports</a></li>
                <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaPlatformDesign.jsp">Platform Designs</a></li>
                <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaHelp.jsp">User Guides and Help</a></li>
              </ul>
          </div>

	  <div id="sidebar">
				<div id="menuAbout" class="box boxMenu">
				    <h3>In This Section</h3>
				    <ul class="boxbody">
				        <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaAbout.jsp">About the Data</a></li>
				        <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaDataType.jsp">Data Levels and Data Types</a></li>
				        <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaPlatformDesign.jsp">Platform Design</a></li>

				        <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaAccessTiers.jsp">Access Tiers</a></li>
				        <li><a href="http://tcga-data.nci.nih.gov/datareports/">Reports</a></li>
				        <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaAnnouncements.jsp">Announcements</a></li>
				        <li><a href="http://tcga-data.nci.nih.gov/tcga/tcgaHelp.jsp">User Guides and Help</a></li>
                        <li class="last"><a href="http://tcga-data.nci.nih.gov/docs/publications">TCGA Publications</a></li>
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

        <li class="last"><a href="/tcga/tcgaHelp.jsp#ncicbSupport">Report a Problem</a></li>
      </ul>
      <ul>
        <li><a href="http://cancergenome.nih.gov/" target="_blank">TCGA Home</a></li>
        <li><a href="http://cancergenome.nih.gov/abouttcga/peoplecontacts/tcgacontacts" target="_blank">Contact Us</a></li>

        <li><a href="http://cancergenome.nih.gov/global/policies" target="_blank">Web Site Policies</a></li>
        <li><a href="http://cancergenome.nih.gov/global" target="_blank">Accessibility</a></li>

        <li><a href="http://cancergenome.nih.gov/rss" target="_blank">RSS</a></li>
        <li class="last"><a href="http://cancergenome.nih.gov/abouttcga/policies/publicationguidelines" target="_blank">Publication Guidelines</a></li>
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
