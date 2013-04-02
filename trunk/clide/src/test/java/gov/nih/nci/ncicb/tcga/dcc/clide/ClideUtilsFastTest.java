/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtilsImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test of the ClideUtils class
 * 
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class ClideUtilsFastTest extends ClideAbstractBaseTest {

	public class NoHiddenFileFilter implements FilenameFilter {

		public boolean accept(File file, String s) {
			return file.isHidden();
		}
	}

	private final Mockery context = new JUnit4Mockery();
	private MailSender mailSender;
	private ClideUtilsImpl clideUtils;
	private static String userDir = System.getProperty("user.dir");
	private String tmpDir = System.getProperty("java.io.tmpdir");
	private ClientContext clientContext = null;

	@Before
	public void before() throws Exception {

		clideUtils = new ClideUtilsImpl();
		mailSender = context.mock(MailSender.class);

		// We use reflection to access the private field
		Field f = clideUtils.getClass().getDeclaredField("mailSender");
		f.setAccessible(true);
		f.set(clideUtils, mailSender);

		clientContext = new ClientContext();

		clientContext.setDestinationPath(userDir);
		clientContext.setProcessedPath(userDir);
		clientContext.setDiskSpaceThreshold("10");
		clientContext.setNoSpaceEmailTo("o@o.com");
		clientContext.setNoSpaceEmailBcc("i@i.com");
		clientContext.setNoSpaceEmailContent("blah blah blah content email");
		clientContext.setNoSpaceEmailSubject("blah blah blah subject email");
		clideUtils.setUpDirectories(clientContext);
		ClideContextHolder.setClientContext(clientContext);
	}

	private void setUpFakeFilesForTest(final long size, final String dir)
			throws NoSuchFieldException, IllegalAccessException {
		File file = new File(userDir) {
			@Override
			public long getFreeSpace() {
				return size;
			}
		};
		ClideContextHolder.getClientContext().setDownloadDir(file);
	}

	@Test
	public void testCheckDiskSpace() throws Exception {
		setUpFakeFilesForTest(1024000000L, "downloadDir");
		clideUtils.checkDiskSpace(clientContext);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckDiskSpaceBadThreshold() throws Exception {
		ClideContextHolder.getClientContext().setDiskSpaceThreshold("a");
		clideUtils.checkDiskSpace(clientContext);
	}

	@Test(expected = RuntimeException.class)
	public void testCheckDiskSpaceNoSpace() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(mailSender).send("o@o.com", "i@i.com",
						"blah blah blah subject email: Download Directory",
						"blah blah blah content email", true);
				will(returnValue(null));
			}
		});
		setUpFakeFilesForTest(1L, "downloadDir");
		clideUtils.checkDiskSpace(clientContext);
	}

	@Test
	public void testCheckClientFreeSpace() throws Exception {
		setUpFakeFilesForTest(1024000000L, "downloadDir");
		assertTrue(clideUtils.checkClientFreeSpace(100, clientContext));
	}

	@Test
	public void testCheckClientNOFreeSpaceForDownloadDir() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(mailSender).send("o@o.com", "i@i.com",
						"blah blah blah subject email: Download Directory",
						"blah blah blah content email", true);
				will(returnValue(null));
			}
		});
		setUpFakeFilesForTest(1024L, "downloadDir");
		assertFalse(clideUtils.checkClientFreeSpace(2048, clientContext));
	}

	@Test
	public void testCheckClientNOFreeSpaceForProcessedDir() throws Exception {
		// ClideContextHolder.getClientContext().setDiskSpaceThreshold();
		context.checking(new Expectations() {
			{
				allowing(mailSender).send("o@o.com", "i@i.com",
						"blah blah blah subject email: Download Directory",
						"blah blah blah content email", true);
				will(returnValue(null));
			}
		});
		setUpFakeFilesForTest(1024L, "processedDir");
		assertFalse(clideUtils.checkClientFreeSpace(2048, clientContext));
	}

	@Test
	public void testValidateTimeout() throws Exception {
		assertEquals(550, clideUtils.validateTimeout("550"));
	}

	@Test
	public void testValidateTimeoutNull() throws Exception {
		assertEquals(ClideConstants.DEFAULT_TIMEOUT,
				clideUtils.validateTimeout(""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateTimeoutBad() throws Exception {
		clideUtils.validateTimeout("blah");
	}

	@Test
	public void testValidateClientDownloadedDirectory() throws Exception {
		assertEquals(new File(userDir),
				clideUtils.validateClientDownloadedDirectory(userDir));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateClientDownloadedDirectoryBad() throws Exception {
		clideUtils
				.validateClientDownloadedDirectory("by definition, oblivion is not a correct path");
	}

	@Test
	public void testValidateClientProcessedDirectory() throws Exception {
		assertEquals(new File(tmpDir),
				clideUtils.validateClientProcessedDirectory(tmpDir, userDir));
	}

	@Test
	public void testValidateClientProcessedDirectoryEmpty() throws Exception {
		assertEquals(new File(userDir),
				clideUtils.validateClientProcessedDirectory("", userDir));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateClientProcessedDirectoryBad() throws Exception {
		clideUtils.validateClientProcessedDirectory(
				"bottomless pit of darkness", userDir);
	}

	@Test
	public void testValidateEncryption() throws Exception {
		assertTrue(clideUtils.validateEncryption("True"));
		assertTrue(clideUtils.validateEncryption("true"));
		assertTrue(clideUtils.validateEncryption("TRUE"));
		assertFalse(clideUtils.validateEncryption("False"));
		assertFalse(clideUtils.validateEncryption("false"));
		assertFalse(clideUtils.validateEncryption("FALSE"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateEncryptionBad() throws Exception {
		clideUtils.validateEncryption("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateEncryptionEmpty() throws Exception {
		clideUtils.validateEncryption("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidatePrivateKeyBad() throws Exception {
		clideUtils.validatePrivateKey(userDir);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidatePrivateKeyBad2() throws Exception {
		clideUtils.validatePrivateKey("blah");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidatePublicKeyBad() throws Exception {
		clideUtils.validatePublicKey(userDir);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidatePublicKeyBad2() throws Exception {
		clideUtils.validatePublicKey("blah");
	}

	@Test
	public void testValidateClientInternalLogging() throws Exception {
		assertTrue(clideUtils.validateClientInternalLogging("True"));
		assertTrue(clideUtils.validateClientInternalLogging("true"));
		assertTrue(clideUtils.validateClientInternalLogging("TRUE"));
		assertFalse(clideUtils.validateClientInternalLogging("False"));
		assertFalse(clideUtils.validateClientInternalLogging("false"));
		assertFalse(clideUtils.validateClientInternalLogging("FALSE"));
		assertFalse(clideUtils.validateClientInternalLogging("blah"));
	}

	@Test
	public void testValidateServerInternalLogging() throws Exception {
		assertTrue(clideUtils.validateServerInternalLogging("True"));
		assertTrue(clideUtils.validateServerInternalLogging("true"));
		assertTrue(clideUtils.validateServerInternalLogging("TRUE"));
		assertFalse(clideUtils.validateServerInternalLogging("False"));
		assertFalse(clideUtils.validateServerInternalLogging("false"));
		assertFalse(clideUtils.validateServerInternalLogging("FALSE"));
		assertFalse(clideUtils.validateServerInternalLogging("blah"));
	}

	@Test
	public void testValidateServerArchiveDirectory() throws Exception {
		assertEquals(userDir.toString(), clideUtils
				.validateServerArchiveDirectory(userDir).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateServerArchiveDirectoryBad() throws Exception {
		clideUtils.validateServerArchiveDirectory("THE ISLAND");
	}

	@Test
	public void testValidateServerSentDirectory() throws Exception {
		assertEquals(new File(tmpDir).getPath(), clideUtils
				.validateServerSentDirectory(tmpDir, userDir).getPath());
	}

	@Test
	public void testValidateServerSentDirectoryEmpty() throws Exception {
		assertEquals(userDir.toString(), clideUtils
				.validateServerSentDirectory("", userDir).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateServerSentDirectoryBad() throws Exception {
		clideUtils.validateServerSentDirectory("wonderland", userDir);
	}

	@Test
	public void testValidateServerWorkingDirectory() throws Exception {
		assertEquals(new File(tmpDir).getPath(), clideUtils
				.validateServerWorkingDirectory(tmpDir, userDir).getPath());
	}

	@Test
	public void testValidateServerWorkingDirectoryEmpty() throws Exception {
		assertEquals(userDir.toString(), clideUtils
				.validateServerWorkingDirectory("", userDir).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateServerWorkingDirectoryBad() throws Exception {
		clideUtils.validateServerWorkingDirectory("far far away kingdom",
				userDir);
	}
	
	/* 
	 * TODO: This test will not work until the test files are recovered that used to be available from 
	 * http://tcga-data-dev.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirp/clideTesting
	 */
	@Ignore
	@Test
	public void testCopyFile() throws IOException, NoSuchAlgorithmException {
		clearDir(WORKING_DIR);
		File archive = new File(ORIGINALS_DIR + File.separator
				+ "jhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0.tar.gz");
		File newArchive = new File(WORKING_DIR + File.separator
				+ "jhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0.tar.gz");

		byte[] md5Before = md5Creator.generate(archive);
		FileUtil.copyFile(archive.getAbsolutePath(),
				newArchive.getAbsolutePath());
		byte[] md5After = md5Creator.generate(newArchive);

		assertEquals("Files are not the same size", archive.length(),
				newArchive.length());
		assertTrue("The md5s don't match", Arrays.equals(md5Before, md5After));
		clearDir(WORKING_DIR);

	}

	@Test
	public void testCopyFilesFromTo() throws IOException {
		clearDir(WORKING_DIR);
		File originals = new File(ORIGINALS_DIR);
		File working = new File(WORKING_DIR);
		String[] before = originals.list(new NoHiddenFileFilter());
		clideUtils.copyFilesFromTo(originals, working);
		String[] after = working.list(new NoHiddenFileFilter());
		assertTrue("File lists are not the same", Arrays.equals(before, after));
		clearDir(WORKING_DIR);
	}

	@Test
	public void testCleanUpDownloadDirectory() throws IOException {

		File downloadDir = new File(WORKING_DIR + File.separator
				+ "downloadDir");
		File testFile = new File(downloadDir.getCanonicalPath()
				+ File.separator + "testFile");
		try {

			downloadDir.mkdir();
			testFile.createNewFile();
			clideUtils.cleanUpDirectory(downloadDir.getCanonicalPath());

			// assert the directory is clean
			assertTrue(downloadDir.listFiles().length == 0);

		} finally {
			if (testFile != null) {
				testFile.delete();
			}
			if (downloadDir != null) {
				downloadDir.delete();
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCleanUpDownloadDirectoryBadDirectoryName()
			throws IOException {
		clideUtils.cleanUpDirectory("badDir");
		Assert.fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCleanUpDownloadDirectoryNullDirectoryName()
			throws IOException {
		clideUtils.cleanUpDirectory(null);
		Assert.fail();
	}

	/**
	 * Test file copying multiple times and assert that NIO is faster more than
	 * half the time
	 * 
	 * @throws IOException
	 */
	@Ignore
	@Test
	public void testNIOCopySpeed() throws IOException {
		int nRuns = 10;
		int faster = 0;
		for (int i = 0; i < nRuns; ++i) {
			if (isNioFaster()) {
				faster++;
			}
		}
		assertTrue("Nio isn't faster?", faster > nRuns / 2);

	}

	public boolean isNioFaster() throws IOException {
		clearDir(WORKING_DIR);
		File archive = new File(ORIGINALS_DIR + File.separator
				+ "jhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0.tar.gz");
		File newArchive = new File(WORKING_DIR + File.separator
				+ "jhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0.tar.gz");

		long start_nio = System.currentTimeMillis();
		FileUtil.copyFile(archive.getAbsolutePath(),
				newArchive.getAbsolutePath());
		long end_nio = System.currentTimeMillis();

		clearDir(WORKING_DIR);

		long start_io = System.currentTimeMillis();
		oldCopyFile(archive.getAbsolutePath(), newArchive.getAbsolutePath());
		long end_io = System.currentTimeMillis();

		long nio_time = end_nio - start_nio;
		long io_time = end_io - start_io;

		System.out.println("Nio: " + nio_time + "  io: " + io_time + "\n");
		clearDir(WORKING_DIR);
		return nio_time < io_time;
	}

	public boolean oldCopyFile(final String sourceFile,
			final String destinationFile) throws IOException {
		boolean success = false;
		File source = new File(sourceFile);
		File destination = new File(destinationFile);
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(destination);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			success = true;

		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
			in = null;
			if (out != null) {
				out.close();
				out = null;
			}
		}
		return success;
	}

	@Test
	public void testGetFormattedAsSeconds() {
		String formatted = clideUtils.getFormattedAsSeconds(42000);
		assertEquals("Formatted String was incorrect", "42 seconds", formatted);
	}

	@Test
	public void testGetFormattedThroughput() {
		String formatted = clideUtils.getFormattedThroughput(1024 * 1024, 1000);
		assertEquals("Formatted String was incorrect",
				"1.0 MiB in 1s (1.0 MiB/s, 60.0 MiB/m, 3.516 GiB/h)", formatted);
	}

}// End of Class
