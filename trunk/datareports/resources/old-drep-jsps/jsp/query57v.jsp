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
        <h1>&nbsp;Query 57v result&nbsp;</h1>
    </div>

    <div id="results_table">

        <ec:table
                items="${count}"
                title="Number of distinct genes submitted to the NCBI by each GSC (Data as of ${count[0].condition2})<br/>
              <a href=\"query57.htm\">Click to get real time data</a> (<font color=\"red\">Warning: This page may take 10 minutes or more to display</font>)"
                var="gencount"
                filterable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="GSC Center"/>
                <ec:column property="count" title="No. of Genes"/>
            </ec:row>
        </ec:table>

    </div>
</div>
<%@ include file="../../footer.jsp" %>