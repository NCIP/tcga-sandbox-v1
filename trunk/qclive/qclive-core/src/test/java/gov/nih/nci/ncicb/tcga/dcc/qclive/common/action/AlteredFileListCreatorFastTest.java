/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for AlteredFileListCreator.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class AlteredFileListCreatorFastTest {
    private static final String ARCHIVE_LOCATION = Thread
            .currentThread()
            .getContextClassLoader()
            .getResource(
                    "samples/qclive/alteredFileListCreator/archiveLocation")
            .getPath();

    private static final String FILE1_LOCATION = ARCHIVE_LOCATION
            + File.separator + "file1.txt";
    private static final String FILE2_LOCATION = ARCHIVE_LOCATION
            + File.separator + "file2.txt";
    private static final String EXPECTED_ALTERED_FILE_LOCATION = ARCHIVE_LOCATION
            + File.separator + AlteredFileListCreator.DCC_ALTERED_FILES_NAME;
    private static final String MANIFEST_LOCATION = ARCHIVE_LOCATION
            + File.separator + "MANIFEST.txt";

    @Test
    public void test() throws IOException, NoSuchAlgorithmException,
            Processor.ProcessorException, InterruptedException {
        File alteredFileList = null;
        // create a temporary manifest
        File manifest = new File(MANIFEST_LOCATION);
        // noinspection ResultOfMethodCallIgnored
        manifest.createNewFile();
        BufferedReader reader1 = null;
        BufferedReader reader2 = null;
        try {
            AlteredFileListCreator alteredFileListCreator = new AlteredFileListCreator();
            alteredFileListCreator.setManifestParser(new ManifestParserImpl());
            Archive archive = new Archive();
            archive.setDeployLocation(ARCHIVE_LOCATION
                    + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
            QcContext qcContext = new QcContext();
            qcContext.aboutToChangeFile(new File(FILE1_LOCATION),
                    "Because I feel like it");
            qcContext.aboutToChangeFile(new File(FILE2_LOCATION),
                    "Because it keeps trying to take over the world");

            alteredFileListCreator.doWork(archive, qcContext);

            // the altered file list should be in the archive location
            alteredFileList = new File(EXPECTED_ALTERED_FILE_LOCATION);
            assertTrue(alteredFileList.exists());
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader1 = new BufferedReader(new FileReader(
                    alteredFileList));
            String header1 = reader1.readLine();
            assertEquals(
                    "#This file lists the files in this archive that were altered by the DCC after receipt from the submitting center",
                    header1);
            String header2 = reader1.readLine();
            assertEquals("#Original MD5 Checksum\tFilename\tReason for change",
                    header2);
            String line1 = reader1.readLine();
            assertEquals(
                    "9890b55b8b982f3c48fa34b3d0142d4c\tfile1.txt\tBecause I feel like it",
                    line1);
            String line2 = reader1.readLine();
            assertEquals(
                    "a328c997de03abab23333f19516c5412\tfile2.txt\tBecause it keeps trying to take over the world",
                    line2);
            assertNull(reader1.readLine());

            //noinspection IOResourceOpenedButNotSafelyClosed
            reader2 = new BufferedReader(new FileReader(new File(
                    MANIFEST_LOCATION)));
            String line = reader2.readLine();
            assertTrue(line
                    .contains(AlteredFileListCreator.DCC_ALTERED_FILES_NAME));
        } finally {
            // delete the altered file
            if (alteredFileList != null && alteredFileList.exists()) {
                alteredFileList.deleteOnExit();
            }
            // delete the temp manifest
            manifest.deleteOnExit();
            IOUtils.closeQuietly(reader1);
            IOUtils.closeQuietly(reader2);
        }
    }

    @Test
    public void testAlteredFileListMap() throws IOException {
        final String alteredFiles = "a328c997de03abab23333f19516c5412\tfile1.txt \n"
                + "bcd8c997de03abab23333f19516c5412\tfile2.txt \n";
        File alteredFile = createAlteredFile(alteredFiles);

        Archive archive = new Archive();
        archive.setDeployLocation(ARCHIVE_LOCATION
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        Map<String, String> alteredFileList = AlteredFileListCreator
                .getAlteredFileListMap(archive);
        final String[] alteredFileNames = alteredFileList.keySet().toArray(
                new String[1]);
        assertTrue(alteredFiles.contains(alteredFileNames[0]));
        assertTrue(alteredFiles.contains(alteredFileNames[1]));
        alteredFile.delete();
    }

    @Test
    public void testEmptyLineAlteredFileListMap() throws IOException {
        final String alteredFiles = "a328c997de03abab23333f19516c5412\tfile1.txt \n"
                + "bcd8c997de03abab23333f19516c5412\tfile2.txt \n" + "\n ";

        File alteredFile = createAlteredFile(alteredFiles);
        Archive archive = new Archive();
        archive.setDeployLocation(ARCHIVE_LOCATION
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        try {
            Map<String, String> alteredFileList = AlteredFileListCreator
                    .getAlteredFileListMap(archive);
        } catch (Exception exp) {
            fail("Error handling empty data " + exp.getMessage());
        }
        alteredFile.delete();
    }

    private File createAlteredFile(final String data) throws IOException {
        BufferedWriter writer = null;
        final File alteredFile = new File(EXPECTED_ALTERED_FILE_LOCATION);
        try {

            writer = new BufferedWriter(new FileWriter(alteredFile));
            writer.write(data);

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return alteredFile;
    }
}
