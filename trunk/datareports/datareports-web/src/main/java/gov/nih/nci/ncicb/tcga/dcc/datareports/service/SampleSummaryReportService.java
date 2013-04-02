/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sample;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for retrieving the Sample Summary Reports from the DAO layer
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public interface SampleSummaryReportService {

    /**
     * Return Sample Summaries for a specific tumor type
     * @param tumorAbbr the abbreviation of the tumor type
     * @return a List of Sample Summaries
     */
    List<SampleSummary> getSampleSummaryReport(String tumorAbbr);

    /**
     * Return Sample Summaries for all tumor types
     * @return a List of Sample Summaries
     */
    List<SampleSummary> getSampleSummaryReport();

    /**
     * Return a centerName filtered Sample Summaries for all tumor types
     * @param centerName name of the center
     * @return a List of Sample Summaries
     */
    public List<SampleSummary> getFilteredSampleSummaryReport(String centerName);

    /**
     * answer center_name, center_type, email, display_name for each center
     * @return basic center info needed to generate and send a report
     */
    public List<Center> getCenters();


    /**
     * general method that return the list of sample according to parameters
     * @param sampleSummary
     * @param property is the name of ther JMESA column selected
     * @return a list of samples
     */
    public List<Sample> getDrillDown(SampleSummary sampleSummary,String property);


    /**
     * general method that return the list of sample according to parameters
     * @param summarySamples list of summarysamples
     * @param tumor tumor abbreviation
     * @param center
     * @param portionAnalyte portion analyte
     * @param platform
     *
     * @return a sample summary
     */
    public SampleSummary findSampleSummary(List<SampleSummary> summarySamples,
       String tumor,String center,String portionAnalyte,String platform);

    /**
     * get the SampleSummary comparator map
     *
     * @return a SampleSummary comparator map
     */
    public Map<String, Comparator> getSampleSummaryComparator();

    /**
     * get the Sample comparator map
     *
     * @param bcr
     * @return a Sample comparator map
     */
    public Map<String, Comparator> getSampleComparator(boolean bcr);

    /**
     * creates a filtered SampleSummary list according to filters
     *
     * @param list the list to be filtered
     * @param tumor filter
     * @param center filter
     * @param portionAnalyte filter
     * @param platform filter
     * @param levelFour filter
     *
     * @return a SampleSummary filtered list
     */
    public List<SampleSummary> getFilteredSampleSummaryList(
            List<SampleSummary> list,
            List<String> tumor,
            List<String> center,
            List<String> portionAnalyte,
            List<String> platform,
            List<String> levelFour);

    /**
     * creates a list of distinct possible values of a given filter for the whole SampleSummary report
     *
     * @param getterString method to call on the Biospecimen object to get the filter
     *
     * @return a list of SampleSummaryFilter
     */
    public List<ExtJsFilter> getSampleSummaryFilterDistinctValues(String getterString);

    /**
     * creates a filtered SampleSummary report for a given center 
     *
     * @param centerName
     *
     * @return a list of SampleSummary
     */
    public List<SampleSummary> processSampleSummary(final String centerName);

    /**
     * get the last date of refresh data from the sample summary report
     *
     * @param list of sample summary
     *
     * @return a date of last refreshed data
     */
    public Date getLatest(List<SampleSummary> list);

}


