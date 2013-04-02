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
        <h1>&nbsp;BCR Biospecimen Barcode&nbsp;</h1>
        <h4> Biospecimen Barcode = The complete analyte barcode
            <br/> Batch Number = The batch number that the analyte was sent in
            <br/> Revision = The revision of the archive a barcode came from
            <br/> Shipping Date = The shipping date of an analyte
            <br/> Project = The project the ID is associated with - currently only TCGA
            <br/> Collection Center = BCR sample collection site (e.g. 02=MD Anderson)
            <br/> Patient = Patient ID
            <br/> Sample Type = e.g. solid tumor (01) or normal blood (10)
            <br/> Sample Sequence = The number of samples from the same patient for a sample type (e.g. 'A' is first
             sample)
            <br/> Portion Sequence = The number of portions from a sample
            <br/> Portion Analyte = The type of analyte (D=DNA, R=RNA, T=total RNA, W=whole genome amplified)
            <br/> Plate ID = The plate that an aliquot or a portion was placed on
            <br/> Data Generating Center ID = The center that a plate was sent to
        </h4>
    </div>

    <div id="results_table">

        <ec:table
                items="${count}"
                var="gencount"
                title="All BCR Biospecimen Barcode Submitted to DCC"
                filterable="false"
                action="${pageContext.request.contextPath}/BCRSamples.htm"
                rowsDisplayed="1"
                showPagination="false"
                >
            <ec:row highlightRow="true">
                <ec:column property="entity" title="Biospecimen Barcode"/>
                <ec:column property="condition" title="Batch Number"/>
                <ec:column property="condition2" title="Revision"/>
                <ec:column property="condition3" title="Shipping Date"/>
                <ec:column property="condition4" title="Project"/>
                <ec:column property="condition5" title="Collection Center"/>
                <ec:column property="condition6" title="Patient"/>
                <ec:column property="condition7" title="Sample Type"/>
                <ec:column property="condition8" title="Sample Sequence"/>
                <ec:column property="condition9" title="Portion Sequence"/>
                <ec:column property="condition10" title="Portion Analyte"/>
                <ec:column property="condition11" title="Plate ID"/>
                <ec:column property="condition12" title="Data Generating Center ID"/>
            </ec:row>
        </ec:table>

    </div>
</div>
<%@ include file="../../footer.jsp" %>