/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ProjectCase;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Fast test class for the PCOD dao class
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class projectCaseDashboardDAOFastTest {

    private ProjectCaseDashboardDAOImpl pcodDAO;

    @Before
    public void before() throws Exception {
        pcodDAO = new ProjectCaseDashboardDAOImpl();
    }

    @Test
    public void testGetRatio() {
        final Float res = pcodDAO.getRatio("404/500");
        assertNotNull(res);
        assertEquals(0.808f, res);
    }

    @Test
    public void testGetRatioOver1() {
        final Float res = pcodDAO.getRatio("504/500");
        assertNotNull(res);
        assertEquals(1f, res);
    }

    @Test
    public void testProcessOverallProgress() throws Exception {
        final ProjectCase pc = new ProjectCase();
        pc.setProjectedCaseBCR("500");
        pc.setLowPassGCC("30/500");
        pc.setExomeGSC("505/500");
        pc.setExpressionArrayCGCC("324/500");
        final String res = pcodDAO.processOverallProgress(pc);
        assertNotNull(res);
        assertEquals("71/500", res);
    }
}
