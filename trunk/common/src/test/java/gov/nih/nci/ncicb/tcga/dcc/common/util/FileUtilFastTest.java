/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.md5.MD5ChecksumCreator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for FileUtil
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileUtilFastTest {
	
    private static final String SAMPLE_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    
    private static final String TEST_FILE = SAMPLE_DIR + "testReadWriteContentToFile.txt";
    private static final String TEST_ARCHIVES_DIR = SAMPLE_DIR + "testArchives" + File.separator;
    private static final String TEST_FILE_TO_COPY = "test_file_to_copy.tar.gz";
    private static final String BACKUP = ".bak";
    private static final String TEST_TAR_GZ_ARCHIVE_NAME = "genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.44.1001.0.tar.gz";
    private static final String TEST_TAR_GZ_ARCHIVE = TEST_ARCHIVES_DIR + TEST_TAR_GZ_ARCHIVE_NAME;
    private static final String TEST_TAR_ARCHIVE_NAME = "genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.44.1001.0.tar";
    private static final String TEST_TAR_ARCHIVE = TEST_ARCHIVES_DIR + TEST_TAR_ARCHIVE_NAME;

    @Test
    public void testGetFormattedFileSizeForKB() {
        long fileSize = 1024;
        String formattedStr = FileUtil.getFormattedFileSize(fileSize);
        assertNotNull(formattedStr);
        assertEquals("1.0 KiB", formattedStr);
    }

    @Test
    public void testGetFormattedFileSizeForMB() {
        long fileSize = 1024 * 1024;
        String formattedStr = FileUtil.getFormattedFileSize(fileSize);
        assertNotNull(formattedStr);
        assertEquals("1.0 MiB", formattedStr);
    }

    @Test
    public void testGetFormattedFileSizeForGB() {
        long fileSize = 1024 * 1024 * 1024;
        String formattedStr = FileUtil.getFormattedFileSize(fileSize);
        assertNotNull(formattedStr);
        assertEquals("1.0 GiB", formattedStr);
    }

    @Test
    public void testReadFile() throws IOException {

        final File file = new File(TEST_FILE);
        final String content = FileUtil.readFile(file, true);
        final String expectedContent = "this is a test.\nEOF";
        assertEquals("Files contents don't match: ", expectedContent, content);
    }

    @Test
    public void testWriteContentToFile() throws IOException {

        final String content = "this is a test.\nEOF";
        final File actualFile = new File(SAMPLE_DIR + "testReadWriteContentToFileActual.txt");
        try {
            FileUtil.writeContentToFile(content, actualFile);
        } catch (IOException x) {
            fail("IOException encountered: " + x.getMessage());
        }

        assertEquals("File content doesn't match: ", content, FileUtil.readFile(actualFile, true));

        if (actualFile.exists()) {
            actualFile.delete();
        }

    }

    @Test
    public void testCopy() throws IOException {
        String fromFile = SAMPLE_DIR + TEST_FILE_TO_COPY;
        String toFile = fromFile + BACKUP;

        FileUtil.copy(fromFile, toFile);

        File copiedFile = new File(toFile);
        assertTrue(copiedFile.exists());
        copiedFile.delete();
    }

    @Test
    public void testCopyFile() throws Exception {
        String fromFile = SAMPLE_DIR + TEST_FILE_TO_COPY;
        String toFile = fromFile + BACKUP;
        MD5ChecksumCreator mdc = new MD5ChecksumCreator();
        FileUtil.copyFile(fromFile, toFile);
        File copiedFile = new File(toFile);
        assertTrue(copiedFile.exists());
        assertEquals(MD5ChecksumCreator.convertStringToHex(mdc.generate(new File(fromFile))),
                MD5ChecksumCreator.convertStringToHex(mdc.generate(copiedFile)));
        copiedFile.delete();
    }

    @Test
    public void testMove() throws IOException {
        String fromFile = SAMPLE_DIR + TEST_FILE_TO_COPY;
        String toFile = fromFile + BACKUP;

        FileUtil.move(fromFile, toFile);

        File movedFile = new File(toFile);
        assertTrue(movedFile.exists());
        FileUtil.move(toFile, fromFile);

    }

    @Test
    public void getFilenameWithoutExtension() throws IOException {
        final String filename = "test.tar.gz";
        final String filenameWithoutExtension = FileUtil.getFilenameWithoutExtension(filename, ".tar.gz");
        assertEquals("test", filenameWithoutExtension);

    }

    @Test
    public void makeDir() throws IOException {
        final String newDirPath = SAMPLE_DIR + "new_dir_test";
        FileUtil.makeDir(newDirPath);
        final File newDir = new File(newDirPath);
        assertTrue(newDir.exists());

        //cleanup
        FileUtil.deleteDir(newDir);

    }

    @Test
    public void createCompressedFile() throws IOException {
        final String fileToBeCompressedPathName = SAMPLE_DIR + "file.txt";
        final String compressedFilePathName = SAMPLE_DIR + "file.tar.gz";
        final String data = "data to be compressed";

        File fileToBeCompressed = new File(fileToBeCompressedPathName);
        FileUtil.writeContentToFile(data, fileToBeCompressed);
        FileUtil.createCompressedFile(fileToBeCompressedPathName, compressedFilePathName);
        File compressedFile = new File(compressedFilePathName);
        assertTrue(compressedFile.exists());

        FileUtil.deleteDir(fileToBeCompressed);
        FileUtil.deleteDir(compressedFile);

    }

    @Test
    public void createCompressedFiles() throws IOException {
        final String fileName0 = SAMPLE_DIR + "file_0.txt";
        final String fileName1 = SAMPLE_DIR + "file_1.txt";
        final String compressedFileName = SAMPLE_DIR + "file.tar.gz";
        final String data = "data to be compressed";

        File file0 = new File(fileName0);
        FileUtil.writeContentToFile(data, file0);

        File file1 = new File(fileName1);
        FileUtil.writeContentToFile(data, file1);

        final List<String> fileNamesToBeCompressed = new ArrayList<String>();
        fileNamesToBeCompressed.add(fileName0);
        fileNamesToBeCompressed.add(fileName1);

        FileUtil.createCompressedFiles(fileNamesToBeCompressed, compressedFileName);
        File compressedFile = new File(compressedFileName);
        assertTrue(compressedFile.exists());

        file0.delete();
        file1.delete();
        compressedFile.delete();

    }

    @Test
    public void testExplodeTarOrTarGzForTarGzArchiveGood() throws Exception {

        final String testArchiveName = TEST_TAR_GZ_ARCHIVE;
        assertTrue(testArchiveName.endsWith(".tar.gz"));

        final File expandDir = FileUtil.makeDir(TEST_ARCHIVES_DIR + "testResults");
        assertTrue(expandDir.exists());
        assertEquals(0, expandDir.listFiles().length);

        FileUtil.explodeTarOrTarGz(expandDir, testArchiveName);
        assertEquals(2, expandDir.listFiles().length);

        final boolean successfullyDeleted = FileUtil.deleteDir(expandDir);
        assertTrue(successfullyDeleted);
    }

    @Test
    public void testExplodeTarOrTarGzForTarArchiveGood() throws Exception {

        final String testArchiveName = TEST_TAR_ARCHIVE;
        assertTrue(testArchiveName.endsWith(".tar"));

        final File expandDir = FileUtil.makeDir(TEST_ARCHIVES_DIR + "testResults");
        assertTrue(expandDir.exists());
        assertEquals(0, expandDir.listFiles().length);

        FileUtil.explodeTarOrTarGz(expandDir, testArchiveName);
        assertEquals(2, expandDir.listFiles().length);

        final boolean successfullyDeleted = FileUtil.deleteDir(expandDir);
        assertTrue(successfullyDeleted);
    }
}
