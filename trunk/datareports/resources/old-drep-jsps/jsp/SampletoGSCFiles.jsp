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
        <h1>&nbsp;Sample to GSC File&nbsp;</h1>
    </div>

    <div id="results_table">
        <ec:table
                items="${count}"
                var="gencount"
                title="Biospecimen Barcode to GSC Data File Association Matrix "
                filterable="false"
                action="${pageContext.request.contextPath}/SampletoGSCFiles.htm"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column style="vertical-align: top" property="entity" title="Biospecimen Barcode"/>
                <ec:column property="condition" title="Trace Data File"/>
                <ec:column style="vertical-align: top" property="condition2" title="Sample Type"/>
                <ec:column style="vertical-align: top" property="condition3" title="Gene"/>
                <ec:column style="vertical-align: top" property="condition4" title="Mutation Data File"/>
            </ec:row>
        </ec:table>
        <!--
        <div id="blue_line" align="center">
            <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
        </div>
        <ec:table
            items="${count[1]}"
            var="gencount"
            title="Biospecimen Barcode to GSC Data File Association Matrix "
            filterable="false"
            sortable="false"
            rowsDisplayed="1"
            showPagination="false"
            >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="Biospecimen Barcode"/>
                <ec:column property="condition" title="Biospecimen Barcode"/>
                <ec:column property="condition2" title="Mutation Data File"/>
                <ec:column property="condition3" title="Gene"/>
            </ec:row>
        </ec:table>
        -->
    </div>
</div>
<%@ include file="../../footer.jsp" %>