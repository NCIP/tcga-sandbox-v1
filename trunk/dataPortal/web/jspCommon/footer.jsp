<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<%
    String host = new String("http://" + request.getServerName());
%>

<div id="footer" align="center">

	<a href="<% out.print(host); %>/tcga/tcgaHome2.jsp">TCGA Data Portal Home</a> |
	<a href="<% out.print(host); %>/tcga/tcgaSiteMap.jsp">Site Map</a> |
	<a href="<% out.print(host); %>/tcga/tcgaContact.jsp">Contact Us</a> |
	<a href="https://gforge.nci.nih.gov/tracker/?func=browse&group_id=265&atid=1843" target="_blank">Report a Problem</a> <!-- |
	<a class="rssIcon" href="/tcga/tcgaRss.jsp">RSS</a>
	-->
	<br/>
	<a href="http://tcga.cancer.gov/" target="_blank">TCGA Web Site Home</a> |
	<a href="http://tcga.cancer.gov/site_policies.asp" target="_blank">Web Site Policies</a> |
	<a href="http://tcga.cancer.gov/site_accessibility.asp" target="_blank">Accessibility</a>

	<br>
	<br>
	<a href="http://www.cancer.gov/" target="_blank"><img class="navImages" src="images/footer/footer_logo_nci.jpg" alt="National Cancer Institute"></a>
	<a href="http://www.genome.gov/" target="_blank"><img class="navImages" src="images/footer/logo_nhgri.gif" alt="National Human Genome Research Institute"></a>
	<a href="http://www.nih.gov/" target="_blank"><img class="navImages" src="images/footer/footer_logo_nih.jpg" alt="National Institutes of Health"></a>
	<a href="http://www.dhhs.gov/" target="_blank"><img class="navImages" src="images/footer/footer_logo_hhs.jpg" alt="Department of Health and Human Services"></a>
	<a href="http://www.firstgov.gov/" target="_blank"><img class="navImages" src="images/footer/footer_logo_firstgov.jpg" alt="FirstGov.gov"></a>
</div>

</body>
</html>