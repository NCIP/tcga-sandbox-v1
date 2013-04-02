/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.secure.ClideCrypt;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.secure.ClideKeyGen;
import gov.nih.nci.ncicb.tcga.dcc.common.util.md5.MD5ChecksumCreator;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the secure components of CLIDE including key generation, file
 * encryption and decryption.
 * 
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ClideEncryptionFastTest {
	
	private static final String SAMPLES_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

	/* This is the directory that the keys and encrypted files are placed in */
	private static final String KEY_DIR = SAMPLES_DIR + "keys";

	/* This is the file we'll encrypt */
	private static final String TARGET_FILE = SAMPLES_DIR + "original_files/z_my_pokemans.jpg";

	private MD5ChecksumCreator md5Creator = new MD5ChecksumCreator();
	private ClideKeyGen keyGen = null;
	private File keyDir = null;
	private File publicKey = null;
	private File privateKey = null;

	public File getFileInKeyDir(final String name) throws IOException {
		return new File(keyDir.getCanonicalPath() + File.separator + name);
	}

	public void report(final long start, final File target) {
		long size = target.length();
		float seconds = ((System.currentTimeMillis() - start) / 1000f);
		System.out.println(size / 1000000f + " MB crypto'd in " + seconds
				+ " seconds");
	}

	public void clearDir(final String dir) {
		File trans = new File(dir);
		File[] contents = trans.listFiles(new ClideConstants.ArchiveFilter());
		for (int i = contents.length - 1; i >= 0; --i) {
			File file = contents[i];
			boolean success = file.delete();
			if (!success) {
				fail("Unable to delete " + file.getName());
			}
		}
	}

	@Before
	public void setUp() throws NoSuchAlgorithmException, IOException {
		keyGen = new ClideKeyGen();
		clearDir(KEY_DIR);
		keyDir = new File(KEY_DIR);
		publicKey = getFileInKeyDir("public.key");
		privateKey = getFileInKeyDir("private.key");
		keyGen.generateNewPairOfKeys(publicKey, privateKey);
	}

	@After
	public void tearDown() {
		clearDir(KEY_DIR);
	}

	@Test
	public void testKeyGeneration() throws NoSuchAlgorithmException,
			IOException {
		assertTrue("No public key?", publicKey.exists());
		assertTrue("No private key?", privateKey.exists());
	}

	@Test
	public void testThatKeysWork() throws GeneralSecurityException,
			IOException, ClassNotFoundException {
		ClideCrypt crypt = new ClideCrypt();
		File targetFile = new File(TARGET_FILE);
		File encryptedFile = getFileInKeyDir(targetFile.getName() + ".enc");
		long start = System.currentTimeMillis();
		crypt.encrypt(targetFile, encryptedFile, publicKey);
		report(start, targetFile);

		assertMD5DigestsNotEqual(targetFile, encryptedFile);
		File decryptedFile = getFileInKeyDir(targetFile.getName() + ".dec");
		crypt.decrypt(encryptedFile, decryptedFile, privateKey);
		assertMD5DigestsEqual(targetFile, decryptedFile);
	}

	@Test
	public void testThatPublicKeyCanNotDecrypt()
			throws GeneralSecurityException, IOException,
			ClassNotFoundException {
		ClideCrypt crypt = new ClideCrypt();
		File targetFile = new File(TARGET_FILE);
		File encryptedFile = getFileInKeyDir(targetFile.getName() + ".enc");
		crypt.encrypt(targetFile, encryptedFile, publicKey);
		assertMD5DigestsNotEqual(targetFile, encryptedFile);
		File decryptedFile = getFileInKeyDir(targetFile.getName() + ".dec");
		try {
			crypt.decrypt(encryptedFile, decryptedFile, publicKey); // NOTE I
																	// used the
																	// wrong
																	// key!!!
			fail("No error when decrypting attempted with public key");
		} catch (InvalidKeyException ikx) {
			// success
		}
	}

	@Test
	public void testThatOnlyOurPrivateKeyDecrypts()
			throws GeneralSecurityException, IOException,
			ClassNotFoundException {
		ClideCrypt crypt = new ClideCrypt();
		File targetFile = new File(TARGET_FILE);
		File encryptedFile = getFileInKeyDir(targetFile.getName() + ".enc");
		long start = System.currentTimeMillis();
		crypt.encrypt(targetFile, encryptedFile, publicKey);
		report(start, targetFile);
		File publicKey2 = getFileInKeyDir("public.key2");
		File privateKey2 = getFileInKeyDir("private.key2");
		keyGen.generateNewPairOfKeys(publicKey2, privateKey2);
		// attempt to decrypt the file with privateKey2
		File decryptedFile = getFileInKeyDir(targetFile.getName() + ".dec");
		try {
			crypt.decrypt(encryptedFile, decryptedFile, privateKey2);
			fail("Second generated key decrypted file!");

		} catch (InvalidKeyException ikx) {
			// success
		}
		// now use the correct key
		crypt.decrypt(encryptedFile, decryptedFile, privateKey);
		assertMD5DigestsEqual(targetFile, decryptedFile);

	}

	@Test
	public void testThatFilesEncryptedWithDifferentKeysAreDifferent()
			throws GeneralSecurityException, IOException,
			ClassNotFoundException {
		ClideCrypt crypt = new ClideCrypt();
		File targetFile = new File(TARGET_FILE);
		File encryptedFile = getFileInKeyDir(targetFile.getName() + ".enc");
		long start = System.currentTimeMillis();
		crypt.encrypt(targetFile, encryptedFile, publicKey);
		report(start, targetFile);
		File publicKey2 = getFileInKeyDir("public.key2");
		File privateKey2 = getFileInKeyDir("private.key2");
		keyGen.generateNewPairOfKeys(publicKey2, privateKey2);
		File encryptedFile2 = getFileInKeyDir(targetFile.getName() + ".enc2");
		crypt.encrypt(targetFile, encryptedFile2, publicKey2);
		assertMD5DigestsNotEqual(encryptedFile, encryptedFile2);

	}

	public void assertMD5DigestsEqual(final File originalFile,
			final File decryptedFile) throws NoSuchAlgorithmException,
			IOException {
		byte[] originalMD5 = md5Creator.generate(originalFile);
		byte[] transMD5 = md5Creator.generate(decryptedFile);
		String explanation = "MD5 checksums not equal.  original: "
				+ MD5ChecksumCreator.convertStringToHex(originalMD5)
				+ " decryptedFile:"
				+ MD5ChecksumCreator.convertStringToHex(transMD5);

		assertTrue(explanation, Arrays.equals(originalMD5, transMD5));
	}

	public void assertMD5DigestsNotEqual(final File originalFile,
			final File encryptedFile) throws NoSuchAlgorithmException,
			IOException {
		byte[] originalMD5 = md5Creator.generate(originalFile);
		byte[] transMD5 = md5Creator.generate(encryptedFile);
		String explanation = "MD5 checksums equal.  original: "
				+ MD5ChecksumCreator.convertStringToHex(originalMD5)
				+ " encryptedFile:"
				+ MD5ChecksumCreator.convertStringToHex(transMD5);

		assertFalse(explanation, Arrays.equals(originalMD5, transMD5));
	}

}
