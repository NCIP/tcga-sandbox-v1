<%@ page import="gov.nih.nci.ncicb.tcga.dcc.qclive.util.GenerateWeeklyReportImpl" %>
<%@ include file="../../header.jsp" %>
<%--
  ~ Software License, Version 1.0 Copyright 2009 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the ÒcaBIGª
  ~ SoftwareÓ).
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  --%>

<div id="middle" align="center">
    <div id="content">
        <h1>&nbsp;DCC Data Summary&nbsp;</h1>
        <h4>&nbsp;<a href="dccDataReport.htm">Back to Main Report Page</a>&nbsp;</h4>
    </div>

    <div id="results_table">
        <h4>&nbsp;Data as of <c:out value="${dataSummaryList[2][0].lastRefresh}"/><br/>
            <!--
            <a href="dataSummaryRealTime.htm">Click to get real time data</a> (<font color="red">This page will take more than 20 minutes to display</font>)&nbsp;
            -->
        </h4>
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
                <ec:column property="tumorType" title="Cancer Type"/>
                <ec:column property="centerName" title="Center"/>
                <ec:column property="portionAnalyte" title="Analyte"/>
                <ec:column property="platform" title="Platform"/>
                <ec:column property="samplesFromBcrToCenter" title="Total Sample IDs BCR sent to Center">
                    <c:if test='${datasummary.samplesFromBcrToCenter != null}'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=${datasummary.centerName}&portion=${datasummary.portionAnalyte}&platform=${datasummary.platform}&data=SamplesBCRSent" target="_blank">
                            <c:out value="${datasummary.samplesFromBcrToCenter}"/></a><br>
                    </c:if>
                </ec:column>
                <ec:column property="samplesReportedOnByCenter" title="Total Sample IDs Received by DCC">
                    <c:if test='${datasummary.samplesReportedOnByCenter == null}'> 0 </c:if>
                    <c:if test='${datasummary.samplesReportedOnByCenter != null}'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=${datasummary.centerName}&portion=${datasummary.portionAnalyte}&platform=${datasummary.platform}&data=SamplesDCCReceived" target="_blank">
                            <c:out value="${datasummary.samplesReportedOnByCenter}"/></a><br>
                    </c:if>
                </ec:column>
                <ec:column property="unaccountedForSampleCount" title="Total Samples Unaccounted for">
                    <c:if test='${datasummary.unaccountedForSampleCount != null}'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=${datasummary.centerName}&portion=${datasummary.portionAnalyte}&platform=${datasummary.platform}&data=SamplesUnaccountedFor" target="_blank">
                            <c:out value="${datasummary.unaccountedForSampleCount}"/>
                        </a><br>
                    </c:if>
                    <c:if test='${datasummary.samplesFromBcrToCenter == datasummary.samplesReportedOnByCenter}'> 0 </c:if>
                    <c:if test='${datasummary.samplesFromBcrToCenter - datasummary.samplesReportedOnByCenter < 0}'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=${datasummary.centerName}&portion=${datasummary.portionAnalyte}&platform=${datasummary.platform}&data=SamplesNotSentFromBCR" target="_blank">
                            <c:out value="${datasummary.samplesFromBcrToCenter - datasummary.samplesReportedOnByCenter}"/>
                        </a><br>
                    </c:if>
                </ec:column>
                <ec:column property="level1ResultsReportedByCenter" title="Sample IDs with Level 1 Data">
                    <c:if test='${datasummary.level1ResultsReportedByCenter == null}'> n/a </c:if>
                    <c:if test='${datasummary.level1ResultsReportedByCenter != null }'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=${datasummary.centerName}&portion=${datasummary.portionAnalyte}&platform=${datasummary.platform}&data=SamplesWithLevel1Data" target="_blank">
                            <c:out value="${datasummary.level1ResultsReportedByCenter}"/></a><br>
                    </c:if>
                </ec:column>
                <ec:column property="level2ResultsReportedByCenter" title="Sample IDs with Level 2 Data">
                    <c:if test='${datasummary.level2ResultsReportedByCenter == null}'> n/a </c:if>
                    <c:if test='${datasummary.level2ResultsReportedByCenter != null }'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=${datasummary.centerName}&portion=${datasummary.portionAnalyte}&platform=${datasummary.platform}&data=SamplesWithLevel2Data" target="_blank">
                            <c:out value="${datasummary.level2ResultsReportedByCenter}"/></a><br>
                    </c:if>
                </ec:column>
                <ec:column property="level3ResultsReportedByCenter" title="Sample IDs with Level 3 Data">
                    <c:if test='${datasummary.level3ResultsReportedByCenter == null}'> n/a </c:if>
                    <c:if test='${datasummary.level3ResultsReportedByCenter != null }'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=${datasummary.centerName}&portion=${datasummary.portionAnalyte}&platform=${datasummary.platform}&data=SamplesWithLevel3Data" target="_blank">
                            <c:out value="${datasummary.level3ResultsReportedByCenter}"/></a><br>
                    </c:if>
                </ec:column>
                <ec:column property="areLevel4ReportsReportedByCenter" title="Level 4 submitted (Y/N)">
                    <c:if test='${datasummary.areLevel4ReportsReportedByCenter == null}'> N </c:if>
                        <c:if test='${datasummary.areLevel4ReportsReportedByCenter != null}'>
                            <c:if test='${datasummary.areLevel4ReportsReportedByCenter == "Y" }'>
                                <span style="color: red; "> Y* </span>
                            </c:if>
                        </c:if>
                </ec:column>
                <ec:column property="percentageOflevel1ResultsReportedByCenter" title="% Level 1 Complete">
                    <c:if test='${datasummary.level1ResultsReportedByCenter == null}'> n/a </c:if>
                </ec:column>
                <ec:column property="percentageOflevel2ResultsReportedByCenter" title="% Level 2 Complete">
                    <c:if test='${datasummary.level2ResultsReportedByCenter == null}'> n/a </c:if>
                </ec:column>
                <ec:column property="percentageOflevel3ResultsReportedByCenter" title="% Level 3 Complete">
                    <c:if test='${datasummary.level3ResultsReportedByCenter == null}'> n/a </c:if>
                </ec:column>
            </ec:row>
        </ec:table>
        <div id="content" align="left">
            <h5>
                <span style="color: red; ">* not in current archives</span><br/>
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
<!-- TODO REMAP THE DATA SUMMARY FIELDS TO SOMETHING MORE REASONABLE -->
            <ec:row highlightRow="true">
                <ec:column property="tumorType" title="Cancer Type"/>
                <ec:column property="centerName" title="BCR Center"/>
                <ec:column property="totalPatients" title="No. of patients">
                    <c:if test='${datasummary.totalPatients != null}'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=BCR&data=TotalPatients" target="_blank">
                            <c:out value="${datasummary.totalPatients}"/></a><br>
                    </c:if>
                </ec:column>
                <ec:column property="totalSampleIdsReceivedByDcc" title="Total Sample IDs Received by DCC">
                    <c:if test='${datasummary.totalSampleIdsReceivedByDcc != null}'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=BCR&data=TotalSampleIDs" target="_blank">
                            <c:out value="${datasummary.totalSampleIdsReceivedByDcc}"/></a><br>
                    </c:if>
                </ec:column>
                <ec:column property="totalAnalyteIdsReceivedByDcc" title="Total Analyte IDs Received by DCC">
                    <c:if test='${datasummary.totalAnalyteIdsReceivedByDcc != null }'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=BCR&data=TotalAnalyteIDs" target="_blank">
                            <c:out value="${datasummary.totalAnalyteIdsReceivedByDcc}"/></a><br>
                    </c:if>
                </ec:column>
                <ec:column property="totalAliqoutIdsReceivedByDcc" title="Total Aliquot IDs Received by DCC">
                    <c:if test='${datasummary.totalAliqoutIdsReceivedByDcc != null}'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=BCR&data=TotalAliquotIDs" target="_blank">
                            <c:out value="${datasummary.totalAliqoutIdsReceivedByDcc}"/></a><br>
                    </c:if>
                </ec:column>
                <ec:column property="fieldCompletionSummary" title="Field completion summary">
                    <c:if test='${datasummary.fieldCompletionSummary == null}'> - </c:if>
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

<!-- TODO REMAP THE DATA SUMMARY FIELDS TO SOMETHING MORE REASONABLE -->
            <ec:row highlightRow="true">
                <ec:column property="tumorType" title="Cancer Type"/>
                <ec:column property="centerName" title="GSC Center"/>
                <ec:column property="numberOfGenes" title="No. of Genes">
                    <c:if test='${datasummary.numberOfGenes != null}'>
                        <a href="detailedDataInfo.htm?tumor=${datasummary.tumorType}&center=${datasummary.centerName}&data=AllGenes" target="_blank">
                            <c:out value="${datasummary.numberOfGenes}"/></a><br>
                    </c:if>
                </ec:column>
                <ec:column property="totalTracesSubmittedToNcbi" title="Total traces submitted to NCBI **"/>
                <ec:column property="totalTraceIdsSubmittedToDcc" title="Total Trace IDs submitted to DCC"/>
                <ec:column property="percentageOfTraceIdsSubmittedToDcc" title="% submitted to DCC"/>
                <ec:column property="totalValidatedSomaticGenes" title="Total Validated Mutated Somatic Genes ***">
                    <span style="color: red; ">TODO</span>
                </ec:column>
                <ec:column property="totalUnknownMutatedSomaticGenes" title="Total Unknown Mutated Somatic Genes ****">
                    <span style="color: red; ">TODO</span>
                </ec:column>
            </ec:row>
        </ec:table>
        <div id="content" align="left">
            <h5>
                <span style="color: red; ">* link to detailed data information page could take long time to display!</span><br/>
                ** Number includes traces submitted to NCBI for all tumor type<br/>
                *** Number of unique Entrez IDs with Validation Status = Valid and Mutation Status = Somatic<br/>
                **** Number of unique Entrez IDs with Validation Status = Unknown and Mutation Status = Somatic
            </h5>
        </div>

        <p style="font-size:0.8em"><%=GenerateWeeklyReportImpl.REPORT_DISCLAIMER%></p>

    </div>
</div>
<div id="blue_line" align="center">
    <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
</div>
<%@ include file="../../footer.jsp" %>