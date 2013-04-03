/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.constants;

import com.google.gwt.core.client.GWT;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;

/**
 * Tests the DataBrowserConstants interface
 *
 * @author David Nassau
 * @version $Rev$
 */
public class DataBrowserConstantsFastTest extends AnomalySearchGWTTestCase {

    public void testConstants() {
        DataBrowserConstants constants = GWT.create(DataBrowserConstants.class);
        assertEquals("Genes", constants.genes());
        assertEquals("Participants", constants.patients());
        assertEquals("Pathways", constants.pathways());
    }

}
