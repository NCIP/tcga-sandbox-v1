/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Test for ZipArchiveExpander class.
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveExpanderFastTest {

	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
    private static final String TEST_ARCHIVE_DIR = SAMPLES_DIR + File.separator
            + "qclive" + File.separator;
    private static final String TEST_TAR_GZ_ARCHIVE_DIR = TEST_ARCHIVE_DIR + "tarGz";
    private final static String GOOD_TAR_GZ_ARCHIVE_FILE = TEST_TAR_GZ_ARCHIVE_DIR
            + File.separator + "good.tar.gz";
    private static final String TEST_TAR_ARCHIVE_DIR = TEST_ARCHIVE_DIR + "tar";
    private final static String GOOD_TAR_ARCHIVE_FILE = TEST_TAR_ARCHIVE_DIR
            + File.separator + "good.tar";
	private final static String WEIRD_ARCHIVE_FILE = TEST_TAR_GZ_ARCHIVE_DIR
			+ File.separator + "weird.tar.gz";
	private final static String NODIR_ARCHIVE_FILE = TEST_TAR_GZ_ARCHIVE_DIR
			+ File.separator + "nodir.tar.gz";
	private final static String BROAD_ARCHIVE_FILE = TEST_TAR_GZ_ARCHIVE_DIR
			+ File.separator
			+ "broad.mit.edu_BRCA.Genome_Wide_SNP_6.mage-tab.1.1001.0.tar.gz";
    private final static String HIDDEN_ARCHIVE_FILE = TEST_TAR_GZ_ARCHIVE_DIR + File.separator + "hidden.tar.gz";

	private List<File> createdFiles = new ArrayList<File>();
	private ArchiveExpander expander;
	private QcContext qcContext;
	private Archive archive;

	@Before
	public void setup() {
		expander = new ArchiveExpander();
		qcContext = new QcContext();
	}

	private File runArchive(final String archiveFile)
			throws Processor.ProcessorException, IOException {
		archive = new Archive(archiveFile);
		qcContext.setArchive(archive);
		expander.execute(archive, qcContext);
		File archiveDir = new File(
				archive.getExplodedArchiveDirectoryLocation());
		assertTrue(archiveDir.exists());
		return archiveDir;
	}

	/**
	 * Test the archive 'good'. The good archive has a directory called 'good'
	 * which contains 2 files, file1.txt and file2.txt. The ArchiveExpander
	 * should expand this with no errors or warnings.
	 * 
	 * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
	 *             in case of error
	 * @throws IOException
	 *             in case of error
	 */
	@Test
	public void testGoodTarGzArchive()
            throws Processor.ProcessorException, IOException {

        final String archiveName = GOOD_TAR_GZ_ARCHIVE_FILE;
        assertTrue(archiveName.endsWith(".tar.gz"));

		final File archiveDir = runArchive(archiveName);
		final File file1 = new File(archiveDir, "file1.txt");
		assertTrue(file1.exists());

        final File file2 = new File(archiveDir, "file2.txt");
		assertTrue(file2.exists());
		assertEquals("Had Warnings: " + qcContext.getWarnings().toString(), 0,
				qcContext.getWarningCount());
		assertEquals("Had Errors: " + qcContext.getErrors().toString(), 0,
				qcContext.getErrorCount());

		createdFiles.add(file1);
		createdFiles.add(file2);
		createdFiles.add(archiveDir);
	}

    @Test
    public void testGoodTarArchive()
            throws ProcessorException, IOException {

        final String archiveName = GOOD_TAR_ARCHIVE_FILE;
        assertTrue(archiveName.endsWith(".tar"));
        
        final File archiveDir = runArchive(archiveName);
        final File file1 = new File(archiveDir, "file1.txt");
        assertTrue(file1.exists());

        final File file2 = new File(archiveDir, "file2.txt");
        assertTrue(file2.exists());
        assertEquals("Had Warnings: " + qcContext.getWarnings().toString(), 0,
                qcContext.getWarningCount());
        assertEquals("Had Errors: " + qcContext.getErrors().toString(), 0,
                qcContext.getErrorCount());

        createdFiles.add(file1);
        createdFiles.add(file2);
        createdFiles.add(archiveDir);
    }

    @Test
    public void testArchiveWithHiddenFile() {
        try {
            runArchive(HIDDEN_ARCHIVE_FILE);
            fail("exception was not thrown for hidden file");
        } catch (ProcessorException e) {
            assertEquals("Archives may not contain hidden files, but found: .surpriseFile", e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            try {
                File archiveDir = new File(archive.getExplodedArchiveDirectoryLocation());
                createdFiles.add(new File(archiveDir, "test.txt"));
                createdFiles.add(new File(archiveDir, ".surpriseFile"));
                createdFiles.add(new File(archiveDir, "hidden"));
                createdFiles.add(archiveDir);
            } catch (IOException e) {
                fail("error cleaning up test files");
            }

        }
    }

	@After
	public void cleanup() {
		for (File file : createdFiles) {
			if (file.exists()) {
				boolean success = file.delete();
				if (!success) {
					System.out.println("Error cleaning up file "
							+ file.getName());
				}
			}
		}
	}

	/**
	 * Tests the archive 'weird.zip' which, although the archive name is
	 * 'weird', contains a directory called 'mongoose'. The archive expansion
	 * should succeed with a warning that the zip file is not made correctly.
	 * The files inside the weird directory should still be inside a dir called
	 * 'weird' and there should not be a 'mongoose' directory.
	 * 
	 * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
	 *             if something is wrong
	 * @throws IOException
	 *             if something is even more wrong
	 */
	@Test
	public void testWeirdArchive() throws Processor.ProcessorException,
			IOException {
		File archiveDir = runArchive(WEIRD_ARCHIVE_FILE);
		assertEquals("Expander should have 2 warnings", 2,
				qcContext.getWarningCount());
		// and contains one file
		File afile = new File(archiveDir, "afile.txt");
		assertTrue(afile.exists());
		// and there is no mongoose directory
		assertFalse(new File(SAMPLES_DIR + "qclive/zip/mongoose/").exists());
		// verify a warning was added
		assertTrue(qcContext.getWarningCount() > 0);
		createdFiles.add(afile);
		createdFiles.add(archiveDir);
	}

	@Test (expected=ProcessorException.class)
	public void testLockedDirectory() throws ProcessorException, IOException{
		
		File archiveDir = new File(TEST_TAR_GZ_ARCHIVE_DIR + "/goodTestDir" );
		File testFile = new File(TEST_TAR_GZ_ARCHIVE_DIR + "/goodTestDir/testFile1.txt" );
		FileLock lock = null;
        RandomAccessFile randomAccessFile = null;
		FileChannel channel = null;
		try{
			archiveDir.mkdir();	
			testFile.createNewFile();

            randomAccessFile = new RandomAccessFile(testFile, "rw");
			channel = randomAccessFile.getChannel();
			lock = channel.lock();		
			
			archiveDir = runArchive(TEST_TAR_GZ_ARCHIVE_DIR + "/goodTestDir.tar.gz" );
		}finally{
			if (archiveDir != null && lock != null){				
				lock.release();
                randomAccessFile.close();
				channel.close();
				// recursevely delete the dir
				FileUtil.deleteDir(archiveDir);
			}
		}		
	}
	
	/**
	 * Tests nodir.zip, which has a file that is not in a directory. Check that
	 * the expander creates the correct archive directory anyway and puts the
	 * file in there.
	 * 
	 * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
	 *             if something is wrong
	 * @throws IOException
	 *             if something is even more wrong
	 */
	@Test
	public void testNoDirArchive() throws Processor.ProcessorException,
			IOException {
		File archiveDir = runArchive(NODIR_ARCHIVE_FILE);
		assertEquals("Expander should have a warning", 1,
				qcContext.getWarningCount());
		File afile = new File(archiveDir, "afile.txt");
		assertTrue(afile.exists());
		createdFiles.add(afile);
		createdFiles.add(archiveDir);
	}

	@Test
	public void testBroadArchive() throws Processor.ProcessorException,
			IOException {
		File archiveDir = runArchive(BROAD_ARCHIVE_FILE);
		File idfFile = new File(archiveDir,
				"broad.mit.edu_BRCA.Genome_Wide_SNP_6.idf.txt");
		File sdrfFile = new File(archiveDir,
				"broad.mit.edu_BRCA.Genome_Wide_SNP_6.sdrf.txt");
		File descriptionFile = new File(archiveDir, "DESCRIPTION.txt");
		File manifestFile = new File(archiveDir, "MANIFEST.txt");
		createdFiles.add(idfFile);
		createdFiles.add(sdrfFile);
		createdFiles.add(descriptionFile);
		createdFiles.add(manifestFile);
		createdFiles.add(archiveDir);

		assertEquals(0, qcContext.getWarningCount());
		assertTrue(idfFile.exists());
		assertTrue(sdrfFile.exists());
		assertTrue(descriptionFile.exists());
		assertTrue(manifestFile.exists());

	}	
}
