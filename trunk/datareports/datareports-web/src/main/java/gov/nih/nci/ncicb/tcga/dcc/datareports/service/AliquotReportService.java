/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Interface for service method of the biospecimen report.
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

public interface AliquotReportService {

    /**
     * Return all aliquots
     *
     * @return a List of Aliquots
     */
    public List<Aliquot> getAllAliquot();

    /**
     * Return aliquot archive for a specific aliquot and level
     *
     * @param aliquotId
     * @param level of data
     *
     * @return a List of aliquot Archives
     */
    public List<AliquotArchive> getAllAliquotArchive(String aliquotId, int level);

    /**
     * creates a Aliquot filtered list according to filters
     *
     * @param list the list to be filtered
     * @param disease filter
     * @param center filter
     * @param platform filter
     * @param bcrBatch filter
     * @param levelOne filter
     * @param levelTwo filter
     * @param levelThree filter
     *
     * @return a Aliquot filtered list
     */
    public List<Aliquot> getFilteredAliquotList(
            List<Aliquot> list,
            String aliquotId,
            List<String> disease,
            List<String> center,
            List<String> platform,
            String bcrBatch,
            List<String> levelOne,
            List<String> levelTwo,
            List<String> levelThree);

    /**
     * creates a list of distinct possible values of a given filter for the whole aliquot report
     *
     * @param getterString method to call on the Aliquot object to get the filter
     *
     * @return a list of AliquotFilter
     */
    public List<ExtJsFilter> getAliquotFilterDistinctValues(String getterString);


    /**
     * get the aliquot comparator map
     *
     * @return a aliquot comparator map
     */
    public Map<String, Comparator> getAliquotComparator();

} //End of interface
