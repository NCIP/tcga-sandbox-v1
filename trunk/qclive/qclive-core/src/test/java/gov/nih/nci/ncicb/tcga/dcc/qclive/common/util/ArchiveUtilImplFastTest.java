/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * ArchiveUtilImpl unit tests
 *
 * @author Julien Baboud Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveUtilImplFastTest {

    private static final String FILE_SEPARATOR = System
            .getProperty("file.separator");

    private static final String SAMPLE_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples/qclive").getPath()
            + File.separator;

    private static final String DUMMY_ARCHIVE_FILE = SAMPLE_DIR + "dummy"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;

    private static final String TEST_DIR = SAMPLE_DIR + "archiveUtilImpl"
            + FILE_SEPARATOR;

    private static final String MANIFEST_DEPLOY_DIR = TEST_DIR + "manifest";

    private static final String MANIFEST_DEPLOY_LOCATION = MANIFEST_DEPLOY_DIR
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;

    private static final String NO_MANIFEST_DEPLOY_DIR = TEST_DIR
            + "noManifest";

    private static final String NO_MANIFEST_DEPLOY_LOCATION = NO_MANIFEST_DEPLOY_DIR
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;

    private ArchiveUtilImpl archiveUtilImpl;
    private Archive archive;

    @Before
    public void setUp() {
        archiveUtilImpl = new ArchiveUtilImpl();
        archiveUtilImpl.setManifestParser(new ManifestParserImpl());

        archive = new Archive();
        archive.setArchiveFile(new File(DUMMY_ARCHIVE_FILE));
        archive.setId(1L);

        // Make sure the file system is setup correctly before each test
        resetTestFS(MANIFEST_DEPLOY_DIR, true);
    }

    @Test
    public void testGetArchiveManifestFileExists() {

        archive.setDeployLocation(MANIFEST_DEPLOY_LOCATION);

        File manifestFile;
        try {
            manifestFile = archiveUtilImpl.getArchiveManifestFile(archive);

            assertNotNull(manifestFile);
            assertTrue("The manifest file doesn't exist: ",
                    manifestFile.exists());

        } catch (ArchiveUtil.ArchiveUtilException e) {
            fail("ArchiveUtilException should not have been raised"
                    + e.getMessage());
        }
    }

    @Test
    public void testGetArchiveManifestFileDoesNotExist() {

        archive.setDeployLocation(NO_MANIFEST_DEPLOY_LOCATION);

        File manifestFile = null;
        try {
            manifestFile = archiveUtilImpl.getArchiveManifestFile(archive);
            fail("ArchiveUtilException should have been raised");

        } catch (ArchiveUtil.ArchiveUtilException expected) {

            assertNull(manifestFile);

            final String expectedErrorMsg = "Could not retrieve the manifest from the archive '"
                    + archive.getArchiveName()
                    + "' (Id: "
                    + archive.getId()
                    + ")";
            assertEquals("Error message unexpected: ", expectedErrorMsg,
                    expected.getMessage());
        }
    }

    @Test
    public void testAddContentIntoNewFileToArchiveManifestExists() {

        archive.setDeployLocation(MANIFEST_DEPLOY_LOCATION);

        final String content = "This is the file content.\nEOF";
        final String filename = "fileToAdd.txt";
        final String fileMD5 = "699ba6594039daf328e1d6fa113bec4d  " + filename
                + "\n";
        final QcContext qcContext = new QcContext();
        archiveUtilImpl.addContentIntoNewFileToArchive(content, filename,
                archive, qcContext);

        assertEquals("Error count unexpected: ", 0, qcContext.getErrorCount());
        final File manifest = new File(MANIFEST_DEPLOY_DIR,
                ManifestValidator.MANIFEST_FILE);
        assertTrue("The manifest file does not exist", manifest.exists());
        try {
            final String manifestContent = FileUtil.readFile(manifest, true);
            assertEquals("Manifest content unexpected: ", fileMD5,
                    manifestContent);
        } catch (IOException e) {
            fail("Could not read manifest file: " + e.getMessage());
        }

        resetTestFS(MANIFEST_DEPLOY_DIR, true);
    }

    @Test
    public void testAddContentIntoNewFileToArchiveManifestDoesNotExist() {

        archive.setDeployLocation(NO_MANIFEST_DEPLOY_LOCATION);

        String content = "This is the file content.\nEOF";
        String filename = "fileToAdd.txt";
        QcContext qcContext = new QcContext();
        archiveUtilImpl.addContentIntoNewFileToArchive(content, filename,
                archive, qcContext);

        assertEquals("Error count unexpected: ", 1, qcContext.getErrorCount());

        final String expectedErrorMsg = "An error occurred while processing archive '"
                + filename
                + "': Could not retrieve the manifest from the archive '"
                + archive.getArchiveName() + "' (Id: " + archive.getId() + ")";
        assertEquals("Error message unexpected: ", expectedErrorMsg, qcContext
                .getErrors().get(0));

        resetTestFS(NO_MANIFEST_DEPLOY_DIR, false);
    }

    /**
     * Reset the test file system: delete all files created during the unit test
     * and recreate an empty manifest
     *
     * @param directoryName     the directory to reset
     * @param createNewManifest <code>true</code> if a new Manifest needs to be created
     */
    private void resetTestFS(final String directoryName,
                             final boolean createNewManifest) {

        File directory = new File(directoryName);
        assertTrue("The file '" + directoryName + "' is not a directory: ",
                directory.isDirectory());

        // Delete all files
        for (final File fileToDelete : directory.listFiles()) {

            fileToDelete.delete();
        }

        // Recreate the manifest if it does not exist
        final File manifest = new File(directoryName,
                ManifestValidator.MANIFEST_FILE);
        try {
            if (createNewManifest && !manifest.exists()) {
                manifest.createNewFile();
                assertTrue("The manifest doe not exist: ", manifest.exists());
            }
        } catch (IOException e) {
            fail("The file '" + manifest.getPath() + manifest.getName()
                    + " could not be created: " + e.getMessage());
        }
    }
}
