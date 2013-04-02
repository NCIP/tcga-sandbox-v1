/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.qclive.BaseQCLiveFastTest;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.DummyFileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;

import java.io.IOException;

import org.junit.Test;

/**
 * @author nassaud
 * @version $Rev$
 * @since 2.0
 */
public class AutoLoaderArchiveQCLiveFastTest extends BaseQCLiveFastTest {

    public AutoLoaderArchiveQCLiveFastTest() throws IOException {
    }

    @Test
    public void testArchive() throws LoaderException {
        final String ARCHIVE_DIR = getAutoLoaderSamplesPath() + "/anonymous/broad.mit.edu_GBM.HT_HG-U133A.mage-tab.1.1002.0";
        LoaderArchive autoLoaderArchive = new LoaderArchive(ARCHIVE_DIR, new DummyFileTypeLookup());
        assertNotNull(autoLoaderArchive.getSDRFFile());
        assertEquals(autoLoaderArchive.getPlatform(), "HT_HG-U133A");
        assertEquals(autoLoaderArchive.getCenter(), "broad.mit.edu");
        assertEquals(autoLoaderArchive.getBatch(), 1);
        assertEquals(autoLoaderArchive.getRevision(), 1002);
        assertEquals(autoLoaderArchive.getDisease(), "GBM");
    }
}
