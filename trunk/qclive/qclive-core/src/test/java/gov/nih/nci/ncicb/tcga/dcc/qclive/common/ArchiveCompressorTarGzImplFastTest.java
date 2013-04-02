/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ArchiveCompressor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ArchiveCompressorTarGzImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.junit.Test;

/**
 * Test class for ArchiveCompressorTarGzImpl
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveCompressorTarGzImplFastTest {

	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	@Test
	public void testCompress() throws IOException {

        TarInputStream tin = null;

        try {
            String testDir = SAMPLE_DIR + "qclive/compression/tarGz/";
            File file1 = new File(testDir + "file1.txt");
            File file2 = new File(testDir + "file2.txt");
            File file3 = new File(testDir + "file3.txt");
            List<File> files = new ArrayList<File>();
            files.add(file1);
            files.add(file2);
            files.add(file3);
            // pass in a list of files and verify that a tar.gz file is made
            ArchiveCompressor compressor = new ArchiveCompressorTarGzImpl();
            String archiveName = "test";
            File createdFile = compressor.createArchive(files, archiveName, new File(
                    testDir),true);
            File testFile = new File(testDir + File.separator + archiveName
                    + compressor.getExtension());
            assertEquals(testFile.getCanonicalPath(),
                    createdFile.getCanonicalPath());
            assertTrue(testFile.exists());
            // look at tar entries and make sure expected files are there, with just
            // archive name as the path
            GZIPInputStream gzipStream = new GZIPInputStream(new FileInputStream(
                    testFile));
            //noinspection IOResourceOpenedButNotSafelyClosed
            tin = new TarInputStream(gzipStream);
            List<String> tarEntryNames = new ArrayList<String>();
            TarEntry tarEntry;
            while ((tarEntry = tin.getNextEntry()) != null) {
                tarEntryNames.add(tarEntry.getName());
            }
            tin.close();
            gzipStream.close();
            assertTrue(tarEntryNames.contains(archiveName + File.separator
                    + "file1.txt"));
            assertTrue(tarEntryNames.contains(archiveName + File.separator
                    + "file2.txt"));
            assertTrue(tarEntryNames.contains(archiveName + File.separator
                    + "file2.txt"));
            testFile.deleteOnExit();
        } finally {
            IOUtils.closeQuietly(tin);
        }
	}


    @Test
	public void testCreateArchive() throws IOException {

        TarInputStream tin = null;

        try {
            String testDir = SAMPLE_DIR + "qclive/compression/tar/";

            File file1 = new File(testDir + "file1.txt");
            File file2 = new File(testDir + "file2.txt");
            File file3 = new File(testDir + "file3.txt");
            List<File> files = new ArrayList<File>();
            files.add(file1);
            files.add(file2);
            files.add(file3);
            // pass in a list of files and verify that a tar.gz file is made
            ArchiveCompressor compressor = new ArchiveCompressorTarGzImpl();
            String archiveName = "test";
            File createdFile = compressor.createArchive(files, archiveName, new File(
                    testDir),false);

            File testFile = new File(testDir + File.separator + archiveName
                    + ".tar");
            assertEquals(testFile.getCanonicalPath(),
                    createdFile.getCanonicalPath());
            assertTrue(testFile.exists());
            // look at tar entries and make sure expected files are there, with just
            // archive name as the path
            FileInputStream inputStream = new FileInputStream(testFile);
            //noinspection IOResourceOpenedButNotSafelyClosed
            tin = new TarInputStream(inputStream);
            List<String> tarEntryNames = new ArrayList<String>();
            TarEntry tarEntry;
            while ((tarEntry = tin.getNextEntry()) != null) {
                tarEntryNames.add(tarEntry.getName());
            }
            tin.close();
            assertTrue(tarEntryNames.contains(archiveName + File.separator
                    + "file1.txt"));
            assertTrue(tarEntryNames.contains(archiveName + File.separator
                    + "file2.txt"));
            assertTrue(tarEntryNames.contains(archiveName + File.separator
                    + "file2.txt"));
            testFile.deleteOnExit();
        } finally {
            IOUtils.closeQuietly(tin);
        }
	}
}
