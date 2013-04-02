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
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc.ProjectCaseDashboardDAOImpl;

import java.io.File;
import java.util.List;

/**
 * Test class for the project case dashboard dao layer
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ProjectCaseDashboardDAOImplSlowTest extends DatareportDBUnitConfig {

    protected static final String JSON_PATH =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    ProjectCaseDashboardDAOImpl impl = new ProjectCaseDashboardDAOImpl();
    DatareportsProperties properties = new DatareportsProperties();

    public void testGetAllProjectCasesCounts() throws Exception {
        properties.setPipelineReportJsonFilesPath(JSON_PATH);
        impl.setDataSource(getDataSource());
        List<ProjectCase> res = impl.getAllProjectCasesCounts();
        assertNotNull(res);
        assertEquals(3, res.size());
        assertEquals("COAD", res.get(0).getDisease());
        assertEquals("601/350", res.get(1).getMutationGSC());
        assertEquals("502/500", res.get(2).getCopyNumberSNPCGCC());
    }

    public void testGetCompleteCasesByDisease() throws Exception {
        impl.setDataSource(getDataSource());
        Integer res = impl.getCompleteCasesByDisease("GBM");
        assertNotNull(res);
        assertEquals(2, res.intValue());
    }

}//End of Class
