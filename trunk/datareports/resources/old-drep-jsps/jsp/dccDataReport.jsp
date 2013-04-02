<%@ include file="../../header.jsp" %>
<%--
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  --%>

<div id="middle" align="center">
    <div id="content">
        <h1>&nbsp;DCC Data Summary Report - GBM&nbsp;</h1>
    </div>
    <div id="content" align="left">
        <a href="dataSummary.htm">Data Summary</a> <br/>
        <a href="archiveList.htm?deployStatus=Available">Archive List</a> <br/>
        <a href="archiveInfo.htm">Archive Info</a> <br/>
        <a href="biospecimenList.htm">Biospecimen List</a> (<font color="red">This page may take a few minutes to
                                                                              display</font>)<br/>
        <a href="biospecimenInfo.htm">Biospecimen Info</a> <br/>
        <a href="traceFileInfo.htm">Trace File Info</a> (<font color="red">This page may take a few minutes to
                                                                           display</font>)<br/>
    </div>
    <div id="blue_line" align="center">
        <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
    </div>
    <div id="content">
        <h1>&nbsp;DCC Data Summary Report - OV&nbsp;</h1>
    </div>
    <div id="content" align="left">
        <a href="dataSummary.htm?tumorType=OV">Data Summary</a> <br/>
        <a href="archiveList.htm?tumorType=OV&deployStatus=Available">Archive List</a> <br/>
        <a href="archiveInfo.htm?tumorType=OV">Archive Info</a> <br/>
        <a href="biospecimenList.htm?tumorType=OV">Biospecimen List</a> (<font color="red">This page may take a few
                                                                                           minutes to
                                                                                           display</font>)<br/>
        <a href="biospecimenInfo.htm?tumorType=OV">Biospecimen Info</a> <br/>
        <a href="traceFileInfo.htm?tumorType=OV">Trace File Info</a> (<font color="red">This page may take a few minutes
                                                                                        to display</font>)<br/>
    </div>
    <div id="blue_line" align="center">
        <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
    </div>
</div>
<%@ include file="../../footer.jsp" %>
