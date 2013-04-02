/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ProjectCase;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Service interface for the Project case dashboard
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ProjectCaseDashboardService {

    /**
     * get All project case counts
     *
     * @return list of ProjectCase
     */
    public List<ProjectCase> getAllProjectCaseCounts();

    /**
     * get the filter ProjectCase list
     *
     * @param list
     * @param disease
     * @return list of ProjectCase
     */
    public List<ProjectCase> getFilteredProjectCaseList(List<ProjectCase> list, List<String> disease);

    /**
     * creates a list of distinct possible values of a given filter for the whole aliquot report
     *
     * @param getterString method to call on the Aliquot object to get the filter
     * @return a list of AliquotFilter
     */
    public List<ExtJsFilter> getProjectCaseFilterDistinctValues(String getterString);


    /**
     * get the Project Case comparator map
     *
     * @return a ProjectCase comparator map
     */
    public Map<String, Comparator> getProjectCaseComparator();


    /**
     * refresh project case database procedure
     */
    public void refreshProjectCaseDashboardProcedure();

    /**
     * empty all project case Cache keys
     */
    public void emptyPCODCache();
}
