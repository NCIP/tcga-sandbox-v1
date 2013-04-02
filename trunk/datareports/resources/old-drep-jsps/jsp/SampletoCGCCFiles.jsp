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
        <h1>&nbsp;Sample to CGCC File&nbsp;</h1>
    </div>

    <div id="results_table">
        <ec:table
                items="${count}"
                var="gencount"
                title="Biospecimen Barcode to CGCC Data File Association Matrix "
                filterable="false"
                action="${pageContext.request.contextPath}/SampletoCGCCFiles.htm"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column style="vertical-align: top" property="entity" title="Biospecimen Barcode"/>
                <ec:column style="vertical-align: top" property="condition" title="SDRF File"/>
                <ec:column style="vertical-align: top" property="condition2" title="Archive Name"/>
                <ec:column style="vertical-align: top" property="condition3" title="Array Data File">
                    <c:if test='${gencount.condition3 == null}'> - </c:if>
                </ec:column>
                <ec:column style="vertical-align: top" property="condition4" title="Array Data Matrix File">
                    <c:if test='${gencount.condition4 == null}'> - </c:if>
                </ec:column>
                <ec:column style="vertical-align: top" property="condition5" title="Derived Array Data File">
                    <c:if test='${gencount.condition5 == null}'> - </c:if>
                </ec:column>
                <ec:column style="vertical-align: top" property="condition6" title="Derived Array Data Matrix File">
                    <c:if test='${gencount.condition6 == null}'> - </c:if>
                </ec:column>
            </ec:row>
        </ec:table>
        <!--
         <ec:table
             items="${count[1]}"
             var="gencount"
             title="Biospecimen Barcode to CGCC Data File Association Matrix "
             filterable="false"
             sortable="false"
             rowsDisplayed="1"
             showPagination="false"
             >
             <ec:row highlightRow="true">
                 <ec:column property="entity" title="Biospecimen Barcode"/>
                 <ec:column property="condition" title="sdrf file"/>
                 <ec:column property="condition2" title="archive_name"/>
                 <ec:column property="condition3" title="data_file"/>
                 <ec:column property="condition4" title="file_col_name"/>
             </ec:row>
         </ec:table>
         -->

    </div>
</div>
<%@ include file="../../footer.jsp" %>