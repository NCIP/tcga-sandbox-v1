/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATAREPORTS_HOME_VIEW;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Test for the datareportHomeController
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

public class DatareportHomeControllerFastTest {

    private DatareportsBasicController controller;
    private MockHttpServletRequest request;

    @Before
    public void before() throws Exception {
        controller = new DatareportsBasicController();
        request = new MockHttpServletRequest();
    }
    
    @Test
    public void biospecimenReportFullHandler() throws Exception {
        request.setMethod("GET");
        final String viewName = controller.datareportsHomeHandler();
        assertTrue(viewName != null);
        assertEquals(DATAREPORTS_HOME_VIEW, viewName);
    }

} //End of Class
