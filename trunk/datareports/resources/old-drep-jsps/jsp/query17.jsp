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
        <h1>&nbsp;Query 17 result&nbsp;</h1>

        <h3>to change the starting date, add "?since=mmddyyyy" at the end of url on your browser. </h3>
    </div>

    <div id="results_table">

        <ec:table
                items="${count[0]}"
                var="gencount"
                title="Count all GBM archives being processed by date"
                filterable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="count" title="Processing"/>
                <ec:column property="condition" title="Since"/>
            </ec:row>
        </ec:table>
        <div id="blue_line" align="center">
            <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
        </div>
        <ec:table
                items="${count[1]}"
                var="gencount"
                title="Count all OV archives being processed by date"
                filterable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="count" title="Processing"/>
                <ec:column property="condition" title="Since"/>
            </ec:row>
        </ec:table>
    </div>
</div>
<%@ include file="../../footer.jsp" %>