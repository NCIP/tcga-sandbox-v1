<%@ include file="../../header.jsp" %>
<div id="middle" align="center">
    <div id="content">
        <h1>&nbsp;DCC Data Summary&nbsp;</h1>
    </div>

    <div id="results_table">
        <ec:table
                items="${dataSummaryList[0]}"
                var="datasummary"
                title="Sample Summary"
                filterable="false"
                sortable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="cancerType" title="Cancer Type"/>
                <ec:column property="centerName" title="Center"/>
                <ec:column property="platform" title="Platform"/>
                <ec:column property="count1" title="Total Sample IDs Received by DCC"/>
                <ec:column property="count2" title="Sample IDs with Level 1 Data">
                    <c:if test='${datasummary.count2 == null}'> 0 </c:if>
                </ec:column>
                <ec:column property="count3" title="Sample IDs with Level 2 Data">
                    <c:if test='${datasummary.count3 == null}'> 0 </c:if>
                </ec:column>
                <ec:column property="count4" title="Sample IDs with Level 3 Data">
                    <c:if test='${datasummary.count4 == null}'> 0 </c:if>
                </ec:column>
                <ec:column property="count5" title="Level 4 submitted (Y/N)">
                    <c:if test='${datasummary.count5 == null}'> N </c:if>
                    <c:if test='${datasummary.centerName == "broad.mit.edu(CGCC)"}'>
                        <c:if test='${datasummary.count5 != null}'>
                            <c:if test='${datasummary.count5 == "Y" }'>
                                <font color="red"> Y </font>
                            </c:if>
                        </c:if>
                    </c:if>
                </ec:column>
                <ec:column property="count6" title="% Level 1 Complete">
                    <c:if test='${datasummary.count2 == null}'> 0 </c:if>
                </ec:column>
                <ec:column property="count7" title="% Level 2 Complete">
                    <c:if test='${datasummary.count3 == null}'> 0 </c:if>
                </ec:column>
                <ec:column property="count8" title="% Level 3 Complete">
                    <c:if test='${datasummary.count4 == null}'> 0 </c:if>
                </ec:column>
             </ec:row>
        </ec:table>
        <div id="content" align="left">
            <h5>
                <font color="red">* not in current archives</font><br/>
                %complete = #samples at data level/total samples received by DCC * 100
            </h5>
        </div>
        <ec:table
                items="${dataSummaryList[1]}"
                var="datasummary"
                title="BCR Summary"
                filterable="false"
                sortable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="cancerType" title="Cancer Type"/>
                <ec:column property="centerName" title="BCR Center"/>
                <ec:column property="count1" title="No. of patients"/>
                <ec:column property="count2" title="Total Sample IDs Received by DCC"/>
                <ec:column property="count3" title="Total Analyte IDs Received by DCC"/>
                <ec:column property="count4" title="Total Aliquot IDs Received by DCC"/>
                <ec:column property="count5" title="Field completion summary">
                    <c:if test='${datasummary.count5 == null}'> - </c:if>
                </ec:column>
             </ec:row>
        </ec:table>
        <ec:table
                items="${dataSummaryList[2]}"
                var="datasummary"
                title="GSC Summary"
                filterable="false"
                sortable="false"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="cancerType" title="Cancer Type"/>
                <ec:column property="centerName" title="GSC Center"/>
                <ec:column property="count1" title="No. of Genes"/>
                <ec:column property="count2" title="Total traces submitted to NCBI"/>
                <ec:column property="count3" title="Total Trace IDs submitted to DCC"/>
                <ec:column property="count4" title="% submitted to DCC"/>
                <ec:column property="count5" title="Total Validated Mutated Somatic Genes*">
                <font color="red">TODO</font>
                </ec:column>
                <ec:column property="count6" title="Total Unknown Mutated Somatic Genes**">
                <font color="red">TODO</font>
                </ec:column>
             </ec:row>
        </ec:table>
        <div id="content" align="left">
            <h5>
                * Number of unique Entrez IDs with Validation Status = Valid and Mutation Status = Somatic<br/>
                ** Number of unique Entrez IDs with Validation Status = Unknown and Mutation Status = Somatic
            </h5>
        </div>

    </div>
</div>
<div id="blue_line" align="center">
    <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
</div>
<%@ include file="../../footer.jsp" %>