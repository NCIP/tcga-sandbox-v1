/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.system.applicationservice.ApplicationException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class for RemoteValidatorHelper
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RemoteValidatorHelperCaCoreImplSlowTest {

    private RemoteValidationHelperCaCoreImpl remoteValidationHelper;

    @Before
    public void setUp() throws Exception {
        remoteValidationHelper = new RemoteValidationHelperCaCoreImpl();
    }

    @Ignore
    @Test
    public void testCenterExists() throws ApplicationException {
        assertTrue( remoteValidationHelper.centerExists( "broad.mit.edu" ) );
        assertFalse( remoteValidationHelper.centerExists( "not a center" ));
    }

    @Ignore
    @Test
    public void testPlatformExists() throws ApplicationException {
        assertTrue( remoteValidationHelper.platformExists("Human1MDuo"));
        assertFalse( remoteValidationHelper.platformExists( "not a platform" ));
    }

    @Ignore
    @Test
    public void testArchiveExists() throws ApplicationException {
        assertTrue( remoteValidationHelper.archiveExists("intgen.org_GBM.bio.10.0.0"));
        assertFalse( remoteValidationHelper.archiveExists( "not an archive" ));
    }

    @Ignore
    @Test
    public void testDiseaseExists() throws ApplicationException {
        assertTrue( remoteValidationHelper.diseaseExists("GBM"));
        assertFalse( remoteValidationHelper.diseaseExists("not a disease" ));
    }

    @Ignore
    @Test
    public void testGetCenterTypeForPlatform() throws ApplicationException {
        assertEquals("CGCC", remoteValidationHelper.getCenterTypeForPlatform( "Human1MDuo" ));
        assertEquals( "GSC", remoteValidationHelper.getCenterTypeForPlatform( "ABI" ));
        assertEquals( null, remoteValidationHelper.getCenterTypeForPlatform( "not a platform" ));
    }

    @Ignore
    @Test
    public void testIsLatest() throws ApplicationException {
        assertTrue(remoteValidationHelper.isLatest( "genome.wustl.edu_GBM.ABI.197.0.0" ));
        assertFalse(remoteValidationHelper.isLatest( "not an archive" ));
    }

    @Ignore
    @Test
    public void testGetLatestArchive() throws ApplicationException {
        assertEquals( "genome.wustl.edu_GBM.ABI.197.0.0", remoteValidationHelper.getLatestArchive( "GBM", "genome.wustl.edu", "ABI", 197 ));
    }

    @Ignore
    @Test
    public void testFileExists() throws ApplicationException {
        assertTrue(remoteValidationHelper.fileExists( "genome.wustl.edu_GBM.ABI.197.tr", "genome.wustl.edu_GBM.ABI.197.0.0" ));
    }
}
