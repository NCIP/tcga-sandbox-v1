/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for HomePageController
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class HomePageControllerFastTest {

    private HomePageController controller;

    @Before
    public void setup() {
        controller = new HomePageController();
    }

    @Test
    public void testGetUUIDManagerHomeScreen() throws UUIDException {
        String viewName = controller.getUUIDManagerHomeScreen();
        assertNotNull(viewName);
        assertEquals("uuidManagerHome", viewName);
    }

}
