/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamTelemetry;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Service layer of the bam telemetry report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface BamTelemetryReportService {

    /**
     * Return all BamTelemetry
     *
     * @return a List of BamTelemetry
     */
    public List<BamTelemetry> getAllBamTelemetry();

    /**
     * creates a BamTelemetry filtered list according to filters
     *
     * @param list
     * @param aliquotUUID
     * @param aliquotId
     * @param dateFrom
     * @param dateTo
     * @param disease
     * @param center
     * @param dataType
     * @param analyteCode
     * @param libraryStrategy
     * @return a List of BamTelemetry
     */
    public List<BamTelemetry> getFilteredBamTelemetryList(
            List<BamTelemetry> list,
            String aliquotUUID,
            String aliquotId,
            String dateFrom,
            String dateTo,
            List<String> disease,
            List<String> center,
            List<String> dataType,
            List<String> analyteCode,
            List<String> libraryStrategy);

    /**
     * creates a list of distinct possible values of a given filter for the whole BamTelemetry report
     *
     * @param getterString method to call on the BamTelemetry object to get the filter
     * @return a list of BamTelemetryFilter
     */
    public List<ExtJsFilter> getBamTelemetryFilterDistinctValues(String getterString);


    /**
     * get the BamTelemetry comparator map
     *
     * @return a BamTelemetry comparator map
     */
    public Map<String, Comparator> getBamTelemetryComparator();


}
