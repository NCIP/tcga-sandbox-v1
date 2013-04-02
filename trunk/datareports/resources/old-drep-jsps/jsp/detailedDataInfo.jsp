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
        <h1>&nbsp;DCC Data Detail&nbsp;</h1>
        <h4>&nbsp;Tumor: <c:out value="${dataList[0]}"/><br/>
            &nbsp;Center: <c:out value="${dataList[1]}"/><br/>
            <c:if test='${dataList[2] != null}'>
                &nbsp;Portion Analyte: <c:out value="${dataList[2]}"/><br/>
            </c:if>
            <c:if test='${dataList[3] != null}'>
                &nbsp;Platform: <c:out value="${dataList[3]}"/><br/>
            </c:if>
        </h4>

    </div>

    <div id="results_table">
        <ec:table
                items="${dataList[5]}"
                var="gencount"
                title="${dataList[4]}"
                filterable="false"
                sortable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="${dataList[4]}"/>
            </ec:row>
        </ec:table>
    </div>
</div>
<div id="blue_line" align="center">
    <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
</div>
<%@ include file="../../footer.jsp" %>