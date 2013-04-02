<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
      <head>
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                                   
            <title>TCGA - Raph Experiment 4</title>

            <!--jsp:include page="commonCssIncludes.jsp" /-->
            <link rel="stylesheet" type="text/css" href="styles/ext-all.css"/> 
            <link rel="stylesheet" type="text/css" href="styles/ext-overrides.css"/> 
            <!--link rel="stylesheet" type="text/css" href="styles/xtheme-gray.css"/--> 
            <link rel="stylesheet" type="text/css" href="styles/common.css"/> 
            <link rel="stylesheet" type="text/css" href="styles/tcga-data.css"/> 
			   <link rel="stylesheet" href="styles/tufte-graph.css" type="text/css" media="screen" charset="utf-8" />

            <!--jsp:include page="commonLibraryIncludes.jsp" /-->
            <script type="text/javascript" src="scripts/thirdParty/ext/ext-base-debug.js"></script>
            <script type="text/javascript" src="scripts/thirdParty/ext/ext-all-debug.js"></script>
            <script type="text/javascript" src="scripts/thirdParty/raphael/raphael.js"></script>
			   <script type="text/javascript" src="scripts/thirdParty/jquery-1.4.2.js"></script>
			   <script type="text/javascript" src="scripts/thirdParty/jquery.enumerable.js"></script>
			   <script type="text/javascript" src="scripts/thirdParty/jquery.tufte-graph.js"></script>
            <script type="text/javascript" src="scripts/extensions/FileUploadField.js"></script>
            <script type="text/javascript" src="scripts/extensions/configurableLookTabPanel.js"></script>
            <script type="text/javascript" src="scripts/util/commonDisplayUtilities.js"></script>
            <script type="text/javascript" src="scripts/util/overrides.js"></script>
            <script type="text/javascript" src="scripts/util/tabIndex.js"></script>
            <script type="text/javascript" src="scripts/util/commonData.js"></script>
            <script type="text/javascript" src="scripts/util/commonFormFields.js"></script>
            <script type="text/javascript" src="scripts/util/commonRenderers.js"></script>
            <script type="text/javascript" src="scripts/scripts.js"></script>
            <script type="text/javascript" src="scripts/graphWidgets.js"></script>
            <script type="text/javascript" src="scripts/graphPopups.js"></script>
            <script type="text/javascript" src="scripts/graph4raph.js"></script>
      </head>
		<body>

				<div id="container" align="center">
				    <div id="top_header">
				        <img 
				src="images/top_header.gif"
				 alt="" usemap="#top_header" class="view-images" border="0">
				    </div>
				    <div id="header">
				        <img 
				src="images/header.jpg" 
				alt="" usemap="#header" class="view-images" border="0">
				    </div>
				    <div id="nav">
				
				        <!-- Start Navigation Menu -->
				        <!-- Tab 1 -->
				        <a href="http://tcga.cancer.gov/dataportal/">
				            <img 
				src="images/nav_about_tcga_data.gif"
				 alt="About TCGA Data" name="about" id="about" class="view-images 
				menu_image" width="152"></a>
				
				        <!-- Tab 2 -->
				        <a href="http://tcga.cancer.gov/dataportal/help/">
				            <img 
				src="images/nav_portal_help.gif"
				 alt="Portal Help" name="help" id="help" class="view-images menu_image"></a>
				
				        <!-- Tab 3 -->
				
				        <a href="http://tcga.cancer.gov/dataportal/data/access/">
				            <img 
				src="images/nav_data_access.gif"
				 alt="Data Access" name="access" id="access" class="view-images 
				menu_image"></a>
				
				        <!-- Tab 4 -->
				
				        <a href="http://tcga-data.nci.nih.gov/">
				            <img 
				src="images/nav_browse_data_ovr.gif"
				 alt="Browse Data" name="browse" id="browse" class="view-images 
				menu_image"></a>
				
				        <!-- Tab 5 -->
				
				        <a href="http://tcga.cancer.gov/dataportal/data/cma/">
				            <img 
				src="images/nav_analyse_data_in_cma.gif"
				 alt="Analyze Data in CMA" name="cma" id="cma" class="view-images 
				menu_image"></a>
				
				        <!-- End Navigation Menu -->
				
				    </div>
				</div>
			</div>

			<div style="width: 100%;height: auto;">
			   <div style="width: 855px; margin: auto;padding: 15px 0 15px 0;">
					<div style="font-family: 'Lucida Grande', 'Lucida Sans Unicode', sans-serif;font-size: 24px; line-height: 24px; font-weight:bold; color: #00667C;margin: 0 0 10px 10px;">The Big Dashboard!</div>
					
					<div id="filterPanel"></div> 

	            <div id="raphgraph"></div>
				</div>					
			</div>					

			<div id="footer" align="center">
				<a href="http://tcga.cancer.gov/dataportal/">Home</a> |
				<a href="http://tcga.cancer.gov/dataportal/contact/">Contact Us</a> |
				<a href="http://tcga.cancer.gov/dataportal/policies/">Policies</a> |
				<a href="http://tcga.cancer.gov/dataportal/accessibility/">Accessibility</a> |
				<a href="http://tcga.cancer.gov/dataportal/sitemap/">Site Map</a>
				
				<br><br>
				<a href="http://www.cancer.gov/" target="_blank"><img src="images/footer_logo_nci.jpg" alt="National Cancer Institute" class="view-images" border="0" width="63" height="39"></a>
				<a href="http://www.genome.gov/" target="_blank"><img src="images/logo_nhgri.gif" alt="National Human Genome Research Institute" class="view-images" border="0" width="76" height="39"></a>
				<a href="http://www.nih.gov/" target="_blank"><img src="images/footer_logo_nih.jpg" alt="National Institutes of Health" class="view-images" border="0" width="48" height="39"></a>
				<img src="images/spacer.gif" alt="" class="view-images" width="6" height="39">
				<a href="http://www.dhhs.gov/" target="_blank"><img src="images/footer_logo_hhs.jpg" alt="Department of Health and Human Services" class="view-images" border="0" width="41" height="39"></a>
				<a href="http://www.firstgov.gov/" target="_blank"><img src="images/footer_logo_firstgov.jpg" alt="FirstGov.gov" class="view-images" border="0" width="101" height="39"></a>
			</div>
			
			
			<map id="top_header_map" name="top_header">
			    <area shape="rect" coords="6,0,223,27" href="http://www.cancer.gov/" target="_blank" alt="National Cancer Institute">
			    <area shape="rect" coords="519,2,794,28" href="http://www.genome.gov/" target="_blank" alt="National Human Genome Research Institute">
			</map>
			
			<map id="header_map" name="header">
			    <area shape="rect" coords="31,15,394,86" href="http://tcga.cancer.gov/dataportal/index.asp" alt="Data Portal Home">
			    <area shape="rect" coords="77,106,356,127" href="http://tcga.cancer.gov/" target="_blank" alt="Visit the Cancer Genome Atlas Home Site">
			</map>

      </body> 
</html>
