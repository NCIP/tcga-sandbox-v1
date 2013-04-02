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
        <h1>&nbsp;Query 59v result&nbsp;</h1>
    </div>

    <div id="results_table">
        <h4>&nbsp;Data as of <c:out value="${count[0][0].condition2}"/><br/>
            <a href="query59.htm">Click to get real time data</a> (<font color="red">Warning: This page may take 5
                                                                                     minutes or more to display</font>)&nbsp;
        </h4>
        <ec:table
                items="${count[0]}"
                title="Number of distinct GBM traces submitted to the DCC by each GSC"
                var="gencount"
                filterable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="GSC Center"/>
                <ec:column property="count" title="Total Trace IDs submitted to DCC"/>
            </ec:row>
        </ec:table>
        <div id="blue_line" align="center">
            <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
        </div>
        <ec:table
                items="${count[1]}"
                var="gencount"
                title="Number of distinct OV traces submitted to the DCC by each GSC"
                filterable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="GSC Center"/>
                <ec:column property="count" title="Total Trace IDs submitted to DCC"/>
            </ec:row>
        </ec:table>
    </div>
</div>
<%@ include file="../../footer.jsp" %>