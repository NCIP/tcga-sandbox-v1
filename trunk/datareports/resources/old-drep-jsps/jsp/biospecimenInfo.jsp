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
        <h1>&nbsp;Biospecimen Info&nbsp;</h1>
        <h4>&nbsp;<a href="dccDataReport.htm">Back to Main Report Page</a>&nbsp;</h4>
    </div>
    <div id="blue_line" align="center">
        <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
    </div>

    <div id="results_table">
        <ec:table
                items="${biospecimenInfo[4]}"
                var="gencount"
                title="Center, shipdate, batch, analyte type, analyte count"
                filterable="false"
                action="${pageContext.request.contextPath}/biospecimenInfo.htm"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="condition7" title="Center"/>
                <ec:column property="condition8" title="Shipdate"/>
                <ec:column property="condition9" title="Batch"/>
                <ec:column property="condition10" title="Analyte Type"/>
                <ec:column property="condition11" title="Analyte Count"/>
            </ec:row>
        </ec:table>

        <ec:table
                items="${biospecimenInfo[2]}"
                var="gencount"
                title="From ${biospecimenInfo[0]} to ${biospecimenInfo[1]} (To change the starting date, add '?since=mmddyyyy' at the end of url on your browser.)"
                filterable="false"
                sortable="false"
                rowsDisplayed="10"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="Classification"/>
                <ec:column property="count" title="Count"/>
                <ec:column property="condition2" title="Example"/>
                <ec:column property="condition3" title="Definition"/>
            </ec:row>
        </ec:table>

        <ec:table
                items="${biospecimenInfo[3]}"
                var="gencount"
                title="Overall"
                filterable="false"
                sortable="false"
                rowsDisplayed="10"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="Classification"/>
                <ec:column property="count" title="Count"/>
                <ec:column property="condition2" title="Example"/>
                <ec:column property="condition3" title="Definition"/>
            </ec:row>
        </ec:table>

    </div>
</div>
<%@ include file="../../footer.jsp" %>

