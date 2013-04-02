/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Robert S. Sfeir
 */
public class SDRFFastTest extends TestCase {

	private static final String SAMPLE_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private final String fileLocation = SAMPLE_DIR + "qclive";
    private final String fileName = "mskcc.org_DryRun.AgilentHGCGH244K.1.sdrf.txt";

    public void testGetSDRF() {
        final SDRF sdrf = new SDRF(fileLocation, fileName, new ProcessLogger());
        assertNotNull(sdrf.getSDRFList());
    }

    public void testGetSDRFColNames() throws IOException {
        final SDRF sdrf = new SDRF(fileLocation, fileName, new ProcessLogger());
        assertNotNull(sdrf.getSDRFColNames());
    }

    public void testGetSDRFRowNumber() throws IOException {
        final SDRF sdrf = new SDRF(fileLocation, fileName, new ProcessLogger());
        assertEquals(24, sdrf.getSDRFRowNumber(1).size());
    }

    public void testGetSDRFColNumber() throws IOException {
        final SDRF sdrf = new SDRF(fileLocation, fileName, new ProcessLogger());
        assertEquals("BCR", sdrf.getSDRFColNumberInRow(1, 11));
    }
}
