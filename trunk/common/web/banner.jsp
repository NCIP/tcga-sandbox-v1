<%--
  ~ Software License, Version 1.0 Copyright 2010 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: chenjw
  Date: Jul 24, 2008
  Time: 10:36:38 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<div id="container" align="center">
    <div id="top_header">
        <img src="<c:url value="/images/layout/top_header.gif"/>" alt="" border="0" usemap="#top_header" class="view-images"/>
    </div>
    <div id="header">
        <img src="<c:url value="/images/layout/header.jpg"/>" alt="" border="0" usemap="#header" class="view-images"/>
    </div>
    <div id="nav">

        <!-- Start Navigation Menu -->
        <!-- Tab 1 -->

        <a href="http://tcga.cancer.gov/dataportal/" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('about','','<c:url value="/images/layout/nav_about_tcga_data_ovr.gif"/>',1)">
            <img src="<c:url value="/images/layout/nav_about_tcga_data.gif"/>" alt="About TCGA Data" name="about" id="about" class="view-images menu_image" width="152"/></a>

        <!-- Tab 2 -->
        <a href="http://tcga.cancer.gov/dataportal/help/" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('help','','<c:url value="/images/layout/nav_portal_help_ovr.gif"/>',1)">
            <img src="<c:url value="/images/layout/nav_portal_help.gif"/>" alt="Portal Help" name="help" id="help" class="view-images menu_image"/></a>

        <!-- Tab 3 -->

        <a href="http://tcga.cancer.gov/dataportal/data/access/" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('access','','<c:url value="/images/layout/nav_data_access_ovr.gif"/>',1)">
            <img src="<c:url value="/images/layout/nav_data_access.gif"/>" alt="Data Access" name="access" id="access" class="view-images menu_image"/></a>

        <!-- Tab 4 -->

        <a href="/" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('browse','','<c:url value="/images/layout/nav_browse_data_ovr.gif"/>',1)">
            <img src="<c:url value="/images/layout/nav_browse_data_ovr.gif"/>" alt="Browse Data" name="browse" id="browse" class="view-images menu_image"/></a>

        <!-- Tab 5 -->

        <a href="http://tcga.cancer.gov/dataportal/data/cma/" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('cma','','<c:url value="/images/layout/nav_analyse_data_in_cma_ovr.gif"/>',1)">
            <img src="<c:url value="/images/layout/nav_analyse_data_in_cma.gif"/>" alt="Analyze Data in CMA" name="cma" id="cma" class="view-images menu_image"/></a>

        <!-- End Navigation Menu -->

    </div>
</div>