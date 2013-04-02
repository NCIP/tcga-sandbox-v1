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
        <h1>&nbsp;Query 62 result&nbsp;</h1>
    </div>

    <div id="results_table">

        <ec:table
                items="${count[0]}"
                var="gencount"
                title="Number of distinct Level 1 GBM sample barcodes received by DCC for each CGCC"
                filterable="false"
                rowsDisplayed="100"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="Center"/>
                <ec:column property="condition" title="Platform"/>
                <ec:column property="condition2" title="Example"> TCGA-02-0001-01 </ec:column>
                <ec:column property="count" title="Number of distinct Level 1 sample barcodes"/>
            </ec:row>
        </ec:table>
        <div id="blue_line" align="center">
            <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
        </div>
        <ec:table
                items="${count[1]}"
                var="gencount"
                title="Number of distinct Level 1 OV sample barcodes received by DCC for each CGCC"
                filterable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="Center"/>
                <ec:column property="condition" title="Platform"/>
                <ec:column property="condition2" title="Example"> TCGA-02-0001-01 </ec:column>
                <ec:column property="count" title="Number of distinct Level 1 sample barcodes"/>
            </ec:row>
        </ec:table>

    </div>
</div>
<%@ include file="../../footer.jsp" %>