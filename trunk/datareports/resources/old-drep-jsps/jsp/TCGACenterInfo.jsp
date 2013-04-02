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
        <h1>&nbsp;TCGA Center Information&nbsp;</h1>
    </div>

    <div id="results_table">

        <ec:table
                items="${count}"
                var="gencount"
                title="Center"
                filterable="false"
                sortable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="DCC Center ID"/>
                <ec:column property="condition" title="Center Name"/>
                <ec:column property="condition2" title="Center Type"/>
                <ec:column property="condition3" title="Contact Email"/>
                <ec:column property="condition4" title="Display Name"/>
                <ec:column property="condition5" title="DCC Sort Order"/>
                <ec:column property="condition6" title="Short Name"/>
                <ec:column property="condition7" title="BCR Center ID"/>
            </ec:row>
        </ec:table>
    </div>
</div>
<%@ include file="../../footer.jsp" %>