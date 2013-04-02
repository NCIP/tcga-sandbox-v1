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
        <h1>&nbsp;Query 61v result&nbsp;</h1>
    </div>

    <div id="results_table">
        <h4>&nbsp;Data as of <c:out value="${count[0][0].condition2}"/><br/>
            <a href="query61.htm">Click to get real time data</a> (<font color="red">Warning: This page may take 10
                                                                                     minutes or more to display</font>)&nbsp;
        </h4>

        <ec:table
                items="${count[0]}"
                var="gencount"
                title="Number of distinct GBM sample barcodes received by DCC for each GSC"
                filterable="false"
                rowsDisplayed="100"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="Center"/>
                <ec:column property="condition" title="Platform"/>
                <ec:column property="condition3" title="Example"> TCGA-02-0001-01 </ec:column>
                <ec:column property="count" title="Number of distinct sample barcodes"/>
            </ec:row>
        </ec:table>
        <div id="blue_line" align="center">
            <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
        </div>
        <ec:table
                items="${count[1]}"
                var="gencount"
                title="Number of distinct OV sample barcodes received by DCC for each GSC"
                filterable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="Center"/>
                <ec:column property="condition" title="Platform"/>
                <ec:column property="condition3" title="Example"> TCGA-02-0001-01 </ec:column>
                <ec:column property="count" title="Number of distinct sample barcodes"/>
            </ec:row>
        </ec:table>

    </div>
</div>
<%@ include file="../../footer.jsp" %>