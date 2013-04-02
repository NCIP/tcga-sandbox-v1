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
        <h1>&nbsp;Biospecimen List&nbsp;</h1>
        <h4>&nbsp;<a href="dccDataReport.htm">Back to Main Report Page</a>&nbsp;</h4>
    </div>
    <div id="blue_line" align="center">
        <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
    </div>
    <div id="results_table">
        <ec:table
                items="${biospecimenList}"
                var="gencount"
                title="Biospecimen"
                filterable="false"
                action="${pageContext.request.contextPath}/biospecimenList.htm"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="BCR Sample Barcode"/>
                <ec:column property="count" title="BCR Batch"/>
                <ec:column property="condition" title="Sent to Center"/>
                <ec:column property="condition2" title="Center/Platform Data submit to DCC">
                    <c:if test='${gencount.condition2 == null}'>
                        <font color="red">0</font>
                    </c:if>
                </ec:column>
                <ec:column property="condition3" title="Level 1 Data">
                    <c:if test='${gencount.condition3 == null}'>
                        <font color="red">0</font>
                    </c:if>
                </ec:column>
                <ec:column property="condition4" title="Level 2 Data">
                    <c:if test='${gencount.condition4 == null}'>
                        <font color="red">0</font>
                    </c:if>
                </ec:column>
                <ec:column property="condition5" title="Level 3 Data">
                    <c:if test='${gencount.condition5 == null}'>
                        <font color="red">0</font>
                    </c:if>
                </ec:column>

            </ec:row>
        </ec:table>
    </div>
</div>
<%@ include file="../../footer.jsp" %>

