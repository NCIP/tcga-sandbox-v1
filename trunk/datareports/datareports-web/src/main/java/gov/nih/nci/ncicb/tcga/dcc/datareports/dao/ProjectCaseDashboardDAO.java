/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ProjectCase;

import javax.sql.DataSource;
import java.util.List;

/**
 * DAO layer of the project case dashboard
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ProjectCaseDashboardDAO {

    public void setDataSource(DataSource dataSource);

    public void refreshProjectCaseDashboardProcedure();

    /**
     * get all project cases counts
     *
     * @return list of ProjectCase
     */
    public List<ProjectCase> getAllProjectCasesCounts();

    /**
     * retrieve the counts of complete cases by disease
     *
     * @param disease
     * @return counts of complete cases
     */
    public Integer getCompleteCasesByDisease(final String disease);
}
