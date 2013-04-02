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
        <h1>&nbsp;Frequently Used DCC Queries&nbsp;</h1>
    </div>
    <div id="content" align="left">
        <a href="query1.htm">Count all archives being tracked</a> (<font color="black">Query 1</font>)<br/>
        <a href="query2.htm">Count all available archives</a> (<font color="black">Query 2</font>)<br/>
        <a href="query3.htm">Count all archives by date (default set to past 30 days)</a> (<font color="black">Query
                                                                                                               3</font>)<br/>
        <a href="query4.htm">Count all available archives by date (default set to past 30 days)</a>
                                                                  (<font color="black">Query 4</font>)<br/>
        <a href="query5.htm">Count all tracked archvies by Center</a> (<font color="black">Query 5</font>)<br/>
        <a href="query5b.htm">Count all tracked archvies broad/GSC</a> (<font color="black">Query 5b</font>)<br/>
        <a href="query6.htm">Count all available archives by Center</a> (<font color="black">Query 6</font>)<br/>
        <a href="query6b.htm">Count all available archives by Center broad/GSC</a> (<font color="black">Query 6b</font>)<br/>
        <a href="query7.htm">Count all archives submit to DCC by date (default set to past 30 days) by center </a>
                                                                  (<font color="black">Query 7</font>)<br/>
        <a href="query7b.htm">Count all archives submit to DCC by date (default set to past 30 days) broad/GSC</a>
                                                                  (<font color="black">Query 7b</font>)<br/>
        <a href="query8.htm">Count all available archives submit to DCC by center and date (default set to past 30
                             days)</a> (<font color="black">Query 8</font>)<br/>
        <a href="query8b.htm">Count all available archives submit to DCC by date ( broad/GSC, default set to past 30
                              days)</a> (<font color="black">Query 8b</font>)<br/>
        <a href="query11.htm">Count all revised archives (double counts archive if revised twice, etc)</a>
                                                                  (<font color="black">Query 11</font>)<br/>
        <a href="query12.htm">Count all revised archives by date (double counts archive if revised twice, etc. default
                              set to past 30 days)</a> (<font color="black">Query 12</font>)<br/>
        <a href="query13.htm">Count all revised archives by Center (double counts archive if revised twice, etc)</a>
                                                                  (<font color="black">Query 13</font>)<br/>
        <a href="query13b.htm">Count all revised archives by Center (broad/GSC)</a> (<font color="black">Query
                                                                                                         13b</font>)<br/>
        <a href="query14.htm">Count all revised archives by Date and Center (double counts archive if revised twice, etc
                              (default set to past 30 days))</a> (<font color="black">Query 14</font>)<br/>
        <a href="query14b.htm">Count all revised archives by Date and Center (broad/GSC (default set to past 30
                               days))</a> (<font color="black">Query 14b</font>)<br/>
        <a href="query15.htm">Count all archives being processed</a> (<font color="black">Query 15</font>)<br/>
        <a href="query16.htm">Count all archives being processed by center</a> (<font color="black">Query
                                                                                                    16</font>)<br/>
        <a href="query16b.htm">Count all archives being processed by center (broad/GSC)</a> (<font color="black">Query
                                                                                                                 16b</font>)<br/>
        <a href="query17.htm">Count all archives being processed by date (default set to past 30 days)</a>
                                                                  (<font color="black">Query 17</font>)<br/>
        <a href="query18.htm">Count all archives being processed by center and date (default set to past 30 days)</a>
                                                                  (<font color="black">Query 18</font>)<br/>
        <a href="query18b.htm">Count all archives being processed by center and date (broad/GSC, default set to past 30
                               days)</a> (<font color="black">Query 18b</font>)<br/>
        <a href="query21.htm">All platform types in use by Center</a> (<font color="black">Query 21</font>)<br/>
        <a href="query22.htm">All platform types in use by Center and Date (default set to past 30 days) </a>
                                                                  (<font color="black">Query 22</font>)<br/>
        <a href="query27.htm">All datatypes in use by Center</a> (<font color="black">Query 27</font>)<br/>
        <a href="query28.htm">All datatypes in use by Date and Center (default set to past 30 days)</a>
                                                                  (<font color="black">Query 28</font>)<br/>
        <a href="query33.htm">Date of last archive submitted by Center</a> (<font color="black">Query 33</font>)<br/>
        <a href="query33b.htm">Date of last archive submitted by Center (broad/GSC)</a> (<font color="black">Query
                                                                                                             33b</font>)<br/>
        <a href="query33c.htm">Date of last archive submitted by Center (broad/CGCC)</a> (<font color="black">Query
                                                                                                              33c</font>)<br/>
        <a href="query34.htm">Date of last archive submitted by Center and Date (default set to past 30 days)</a>
                                                                  (<font color="black">Query 34</font>)<br/>
        <a href="query34b.htm">Date of last archive submitted by Center and Date (broad/GSC)</a> (<font color="black">Query
                                                                                                                      34b</font>)<br/>
        <a href="query34c.htm">Date of last archive submitted by Center and Date (broad/CGCC)</a> (<font color="black">Query
                                                                                                                       34c</font>)<br/>
        <a href="query36.htm">Number of distinct aliquot barcodes</a> (<font color="black">Query 36</font>)<br/>
        <a href="query37.htm">Number of distinct analyte barcodes</a> (<font color="black">Query 37</font>)<br/>
        <a href="query38.htm">Number of distinct patient barcodes</a> (<font color="black">Query 38</font>)<br/>
        <a href="query39.htm">Count distinct sample types (those in use only)</a> (<font color="black">Query
                                                                                                       39</font>)<br/>
        <a href="query40.htm">Count distinct collection centers (in use only)</a> (<font color="black">Query
                                                                                                       40</font>)<br/>
        <a href="query41.htm">Count total types of analyes (in use only)</a> (<font color="black">Query 41</font>)<br/>
        <a href="query42.htm">Number of distinct sample barcodes in use</a> (<font color="black">Query 42</font>)<br/>
        <a href="query43.htm">Number of distinct aliquot barcodes by date </a> (<font color="black">Query
                                                                                                    43</font>)<br/>
        <a href="query44.htm">Number of distinct analyte barcodes by date (default set to past 30 days)</a>
                                                                  (<font color="black">Query 44</font>)<br/>
        <a href="query45.htm">Number of distinct patient barcodes by date (default set to past 30 days)</a>
                                                                  (<font color="black">Query 45</font>)<br/>
        <a href="query46.htm">Number of distinct sample barcodes in use by date (default set to past 30 days)</a>
                                                                  (<font color="black">Query 46</font>)<br/>
        <a href="query47.htm">Count total types of analytes (in use only) by date (default set to past 30 days)</a>
                                                                  (<font color="black">Query 47</font>)<br/>
        <a href="query48.htm">Count distinct sample types (those in use only) by date (default set to past 30 days)</a>
                                                                  (<font color="black">Query 48</font>)<br/>
        <a href="query49.htm">Count distinct collection centers (in use only) by date (default set to past 30 days)</a>
                                                                  (<font color="black">Query 49</font>)<br/>
        <a href="query50.htm">Center, Shipdate, Batch, Analyte type, Analyte count</a> (<font color="black">Query
                                                                                                            50</font>)<br/>
        <a href="query51.htm">Number of trace files by center and load date</a> (<font color="black">Query 51</font>
        <font color="red">This page may take a few minutes to display</font>)<br/>
        <a href="query57v.htm">Number of distinct genes submitted to the NCBI by each GSC</a> (<font color="black">Query
                                                                                                                   57v</font>)<br/>
        <a href="query58v.htm">Number of distinct traces submitted to the NCBI by each GSC</a> (<font color="black">Query
                                                                                                                    58v</font>)<br/>
        <a href="query59v.htm">Number of distinct traces submitted to the DCC by each GSC</a> (<font color="black">Query
                                                                                                                   59v</font>)<br/>
        <a href="query60.htm">Number of distinct sample barcodes received by DCC for each CGCC</a> (<font color="black">Query
                                                                                                                        60</font>)<br/>
        <a href="query61v.htm">Number of distinct sample barcodes received by DCC for each GSC</a> (<font color="black">Query
                                                                                                                        61v</font>)<br/>
        <a href="query62.htm">Number of distinct Level 1 sample barcodes received by DCC for each CGCC</a>
                                                                  (<font color="black">Query 62</font>)<br/>
        <a href="query63.htm">Number of distinct Level 2 sample barcodes received by DCC for each CGCC</a>
                                                                  (<font color="black">Query 63</font>)<br/>
        <a href="query64.htm">Number of distinct Level 3 sample barcodes received by DCC for each CGCC</a>
                                                                  (<font color="black">Query 64</font>)<br/>
        <a href="query65.htm">Current CGCC archives</a> (<font color="black">Query 65</font>)<br/>
        <a href="query66.htm">Current GSC archives</a> (<font color="black">Query 66</font>)<br/>
        <a href="query67.htm">Current BCR archives</a> (<font color="black">Query 67</font>)<br/>
        <a href="query68.htm">Level 4 data submitted to the DCC by center</a> (<font color="black">Query 68</font>)<br/>
    </div>
    <div id="content">
        <h1>&nbsp;Other adhoc DCC Queries&nbsp;</h1>
    </div>
    <div id="content" align="left">
        <a href="BCRSamples.htm">BCR Biospecimen Barcode</a><br/>
        <a href="SampletoCGCCFiles.htm">Sample to CGCC File</a><br/>
        <a href="SampletoGSCFiles.htm">Sample to GSC File</a><br/>
        <a href="latestArchives.htm?deployStatus=Available">All TCGA Archives Available for Download</a><br/>
        <a href="AllTCGAArchives.htm">All TCGA Archives Submit to DCC</a><br/>
        <a href="AllTCGAPlatforms.htm">TCGA Platforms</a><br/>
        <a href="AllTCGACollectionCenters.htm">TCGA Collection Centers</a><br/>
        <a href="OrphanedBiospecimenBarcode.htm">Orphaned Biospecimen Barcode Submit to DCC</a><br/>
        <a href="AllTCGAPortionAnalytes.htm">TCGA Portion Analyte Definition</a><br/>
        <a href="AllTCGASampleTypes.htm">TCGA Sample Types</a><br/>
        <a href="AllTCGATumorTypes.htm">TCGA Tumor Types</a><br/>
        <a href="AllTCGACenters.htm">TCGA Centers</a><br/>
        <a href="TCGACenterInfo.htm">All TCGA Center Information</a><br/>
    </div>
</div>
<div id="blue_line" align="center">
    <img src="<c:url value="/images/layout/blue_line.gif"/>" alt="separator">
</div>
<%@ include file="../../footer.jsp" %>