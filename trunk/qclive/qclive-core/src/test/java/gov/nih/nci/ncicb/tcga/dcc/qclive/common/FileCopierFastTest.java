/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.FileCopier;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for FileCopier.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileCopierFastTest {
	private static final String SAMPLE_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String FILE_COPIER_DIR = SAMPLE_DIR + "qclive" + File.separator + "fileCopier" + File.separator;

    @Test
    public void testCopy() throws IOException {

        BufferedReader reader1 = null;
        BufferedReader reader2 = null;

        try {
            File toCopy = new File(SAMPLE_DIR + "qclive" + File.separator + "dataMatrix" + File.separator + "Test.sdrf" );
            File copy = FileCopier.copy( toCopy, new File(SAMPLE_DIR + "qclive" + File.separator + "fileCopier" ) );
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader1 = new BufferedReader( new FileReader( toCopy ) );
            StringBuffer origText = new StringBuffer();
            String line = reader1.readLine();
            while(line != null) {
                origText.append( line );
                line = reader1.readLine();
            }
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader2 = new BufferedReader( new FileReader( copy ) );
            StringBuffer copyText = new StringBuffer();
            line = reader2.readLine();
            while(line != null) {
                copyText.append( line );
                line = reader2.readLine();
            }
            reader2.close();
            assertEquals( origText.toString(), copyText.toString() );
            //noinspection ResultOfMethodCallIgnored
            copy.delete();
        } finally {
            IOUtils.closeQuietly(reader1);
            IOUtils.closeQuietly(reader2);
        }
    }

    @Test
    public void testCopyFileOrDirectoryWhenSourceIsDirectory() {

        try {
            final String childFilename = "fromFile.txt";

            final File sourceDirectory = new File(FILE_COPIER_DIR + "fromDir");
            assertTrue(sourceDirectory.exists());
            assertTrue(sourceDirectory.isDirectory());

            final File[] sourceChildren = sourceDirectory.listFiles();
            assertEquals(1, sourceChildren.length);

            final File sourceChild = sourceChildren[0];
            assertNotNull(sourceChild);
            assertEquals(childFilename, sourceChild.getName());

            final File destinationDirectory = new File(FILE_COPIER_DIR + "toDir");
            assertFalse(destinationDirectory.exists());
            FileCopier.copyFileOrDirectory(sourceDirectory, destinationDirectory);

            assertTrue(destinationDirectory.exists());
            assertTrue(destinationDirectory.isDirectory());

            final File[] destinationChildren = destinationDirectory.listFiles();
            assertNotNull(destinationChildren);
            assertEquals(1, destinationChildren.length);

            final File destinationChild = destinationChildren[0];
            assertNotNull(destinationChild);
            assertEquals(childFilename, sourceChild.getName());

            // Comparing source and destination files content
            final String sourceChildContent = FileUtil.readFile(sourceChild, true);
            final String destinationChildContent = FileUtil.readFile(sourceChild, true);
            assertEquals(sourceChildContent, destinationChildContent);

            // Cleanup
            final boolean deletedDestinationChildWithSuccess = destinationChild.delete();
            assertTrue(deletedDestinationChildWithSuccess);

            final boolean deletedDestinationDirectoryWithSuccess = destinationDirectory.delete();
            assertTrue(deletedDestinationDirectoryWithSuccess);

        } catch (final IOException e) {
            fail("Unexpected IOException: " + e.getMessage());
        }
    }

    @Test
    public void testCopyFileOrDirectoryWhenSourceDoesNotExist() {

        final File sourceDirectory = new File(FILE_COPIER_DIR + "doesNotExist");
        final File destinationDirectory = new File(FILE_COPIER_DIR + "toDir");

        try {
            FileCopier.copyFileOrDirectory(sourceDirectory, destinationDirectory);
            fail("IOException was not thrown.");

        } catch (final IOException e) {
            assertTrue(e.getMessage().contains("samples/qclive/fileCopier/doesNotExist"));
        }
    }
}
