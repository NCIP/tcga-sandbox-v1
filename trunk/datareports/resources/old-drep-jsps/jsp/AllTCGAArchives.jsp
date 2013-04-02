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
        <h1>&nbsp;All TCGA Archives Submit to DCC&nbsp;</h1>
    </div>

    <div id="results_table">

        <ec:table
                items="${count}"
                var="gencount"
                title="Archive"
                filterable="false"
                action="${pageContext.request.contextPath}/AllTCGAArchives.htm"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="Archive Name"/>
                <ec:column property="condition" title="Date Added"/>
                <ec:column property="condition2" title="Deploy Status"/>
                <ec:column property="condition3" title="Deploy Location"/>
            </ec:row>
        </ec:table>
    </div>
</div>
<%@ include file="../../footer.jsp" %>