/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ServerContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.SoundcheckRun;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Exercise the wrapper around Soundcheck and see if it's working
 *
 * @author Jon Whitmore
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RunSoundcheckSlowTest extends ClideAbstractBaseTest {

    @Before
    public void setUp() throws IOException {
        clearFilesUsed();
        ServerContext.setWorkingDir(new File(WORKING_DIR));
    }

    @After
    public void tearDown() throws IOException {
        clearFilesUsed();
    }

    /**
     * remove all files from the directory we are transferring too
     */

  protected void clearFilesUsed() throws IOException {
        clearDir(WORKING_DIR);
    }

    /**
     * Run the validator on an incomplete set of files to ensure failure
     */
  	/* 
	 * TODO: This test will not work until the test files are recovered that used to be available from 
	 * http://tcga-data-dev.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirp/clideTesting
	 */
	@Ignore
	@Test
    public void testWithoutMageTabAndFailValidation() throws IOException {
        putAllFilesIn(WORKING_DIR);
        File levelOne = new File(WORKING_DIR, "jhu-usc.edu_KIRP.HumanMethylation27.mage-tab.1.1.0.tar.gz");
        if (!levelOne.delete()) {
            fail("Can't delete one archive to ensure failure: " + levelOne.getCanonicalPath());
        }
        SoundcheckRun run = new SoundcheckRun();
        new Thread(run).start();
        while (!run.isDone()) {
            // wait for the thread to finish before asserting anything
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                fail("InterruptedException waiting for SoundcheckRun to finish.");
            }
        }
        assertTrue(!run.wasSuccessful());
        run.cleanUp(ServerContext.getWorkingDir(), false);
        List<String> errors = run.getErrors();
        assertTrue(errors.contains("MAGE-TAB archive not found	[archive jhu-usc.edu_KIRP.HumanMethylation27.Level_3.1.0.0]"));
        assertTrue("Some files not correctly removed.", onlyContainsArchivesAndMD5s(WORKING_DIR));
    }

    /**
     * Run the validator on an incomplete set of files to ensure failure
     */
	 /*
    @Test
    public void testWithoutLevelOneAndFailValidation() throws IOException {
        putAllFilesIn(WORKING_DIR);
        File levelOne = new File(WORKING_DIR, "jhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0.tar.gz");
        if (!levelOne.delete()) {
            fail("Can't delete one archive to ensure failure: " + levelOne.getCanonicalPath());
        }
        SoundcheckRun run = new SoundcheckRun();
        new Thread(run).start();
        while (!run.isDone()) {
            // wait for the thread to finish before asserting anything
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                fail("InterruptedException waiting for SoundcheckRun to finish.");
            }
        }
        assertTrue(!run.wasSuccessful());
        run.cleanUp(ServerContext.getWorkingDir(), false);
        String errors = run.getErrors();
        assertTrue(errors.contains("- Archive 'jhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0' is listed in the SDRF but either has not yet been uploaded or is not the latest available archive for that type and serial index\t[archive jhu-usc.edu_KIRP.HumanMethylation27.mage-tab.1.0.0]"));
        assertTrue(errors.contains("- Archive 'jhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0' is referenced in the SDRF but is not one of the latest or uploaded archives\t[archive jhu-usc.edu_KIRP.HumanMethylation27.mage-tab.1.0.0]"));
        assertTrue("Some files not correctly removed.", onlyContainsArchivesAndMD5s(WORKING_DIR));
    }

    @Test
    public void testWithAllFiles() throws IOException {
        putAllFilesIn(WORKING_DIR);
        SoundcheckRun run = new SoundcheckRun();
        new Thread(run).start();
        while (!run.isDone()) {
            // wait for the thread to finish before asserting anything
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                fail("InterruptedException waiting for SoundcheckRun to finish.");
            }
        }
        assertTrue("Run should have been successful!", run.wasSuccessful());
        run.cleanUp(ServerContext.getWorkingDir(), false);
        assertTrue("Some files not correctly removed.", onlyContainsArchivesAndMD5s(WORKING_DIR));
    }
*/
    public boolean onlyContainsArchivesAndMD5s(final String directoryPath) {
        File dir = new File(directoryPath);
        File[] contents = dir.listFiles(new ClideConstants.NonHiddenFileFilter());
        for (int i = 0; i < contents.length; i++) {
            if (!contents[i].getName().endsWith(ConstantValues.ARCHIVE_EXTENSION)
                    && !contents[i].getName().endsWith(ConstantValues.ARCHIVE_EXTENSION + ".md5")) {
                return false;
            }
        }
        return true;
    }

}
