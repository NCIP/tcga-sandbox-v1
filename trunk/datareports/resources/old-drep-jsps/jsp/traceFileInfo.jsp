<%@ include file="../../header.jsp" %>

<div id="middle" align="center">
    <div id="content">
        <h1>&nbsp;Trace File Info&nbsp;</h1>
        <h4>&nbsp;<a href="dccDataReport.htm">Back to Main Report Page</a>&nbsp;</h4>
    </div>
    <div id="content" align="left">
        <h3>&nbsp; GSC-based Data: data as of <c:out value="${traceFileInfoList[0]}"/> </h3>
    </div>
    <div id="blue_line" align="center">
        <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
    </div>

    <div id="results_table">
        <ec:table
            items="${traceFileInfoList[1]}"
            var="gencount"
            title="Number of trace files by center and load date"
            filterable="false"
            sortable="false"
            rowsDisplayed="1"
            showPagination="false"
            >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="Center Name"/>
                <ec:column property="condition" title="Trace Load Date"/>
                <ec:column property="count" title="Count"/>
            </ec:row>
        </ec:table>
    </div>
</div>

<%@ include file="../../footer.jsp" %>