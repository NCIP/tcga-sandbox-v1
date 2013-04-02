/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.qclive.BaseQCLiveFastTest;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * @author David Nassau
 * @version $Rev$
 */
public class AutoLoaderSDRFFastTest extends BaseQCLiveFastTest {

    public AutoLoaderSDRFFastTest() throws IOException {
    }

    @Test
    public void testSDRF() throws Exception {
        String[] headers = new String[]{"Extract Name", "Hybridization Name"};
        LoaderSDRF autoLoaderSdrf = new LoaderSDRF(new File(getAutoLoaderSamplesPath() + File.separator + "test.sdrf.txt"), headers);
        assertEquals("Extract Name", autoLoaderSdrf.getColumnHeader(0));
        assertEquals("Hybridization Name", autoLoaderSdrf.getColumnHeader(1));
        String first = autoLoaderSdrf.getColumnData("Extract Name", 1);
        String second = autoLoaderSdrf.getColumnData("Hybridization Name", 1);
        assertTrue(first.startsWith("TCGA-"));
        assertEquals(first.length(), 28);
        assertTrue(second.startsWith("0"));
        assertEquals(second.length(), 4);
    }
}
