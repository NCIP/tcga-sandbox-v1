/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.*;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sdrf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * interface describing the service layer for the latest sdrf,archive,maf reports
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public interface LatestGenericReportService {

    /**
     * Return all latest sdrf
     *
     * @return a List of Sdrfs
     */
    public List<Sdrf> getLatestSdrfWS();

    /**
     * Return all latest archive
     *
     * @return a List of Archives
     */
    public List<Archive> getLatestArchiveWS();

    /**
     * Return all latest archive by type
     *
     * @param archiveType
     *
     * @return a List of Archives
     */
    public List<Archive> getLatestArchiveWSByType(String archiveType);


    /**
     * Return all latest maf
     *
     * @return a List of Maf
     */
    public List<Maf> getLatestMafWS();

    /**
     * Return all latest archive,sdrf,maf combined
     *
     * @return a List of LatestArchive
     */
    public List<LatestArchive> getLatestArchive();

    /**
     * creates a LatestArchive filtered list according to filters
     *
     * @param list the list to be filtered
     * @param archiveType
     * @param dateFrom filter
     * @param dateTo filter
     *
     * @return a LatestArchive filtered list
     */
    public List<LatestArchive> getFilteredLatestArchiveList(
            List<LatestArchive> list,
            List<String> archiveType,
            String dateFrom,
            String dateTo);

    /**
     * get the LatestArchive comparator map
     *
     * @return a LatestArchive comparator map
     */
    public Map<String, Comparator> getLatestArchiveComparator();

    /**
     * creates a list of distinct possible values of a given filter for the whole latest archive report
     *
     * @param getterString method to call on the LatestArchvie object to get the filter
     *
     * @return a list of LatestArchiveFilter
     */
    public List<ExtJsFilter> getLatestArchiveFilterDistinctValues(String getterString);

}
