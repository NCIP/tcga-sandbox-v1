/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide;

import gov.nih.nci.ncicb.tcga.dcc.clide.common.secure.ClideKeyGen;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ClideServer;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Exercise the public static void main()s of the application
 * 
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class CommandLineArgumentsFastTest {

	protected static final String CLIDE_SAMPLES_PATH = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
	
	private File publicKey = new File(CLIDE_SAMPLES_PATH + "keys" + File.separator + "publicKeyText.txt");

	private File privateKey = new File(CLIDE_SAMPLES_PATH + "keys" + File.separator + "privateKeyText.txt");

	@After
	public void tearDown() throws IOException {
		if (ClideServer.getCommandLineInstance() != null) {
			ClideServer.getCommandLineInstance().stop();

		}
		deleteOrFail(publicKey);
		deleteOrFail(privateKey);

	}

	private void deleteOrFail(final File file) throws IOException {
		if (file.exists()) {
			if (!file.delete()) {
				fail("Cannot delete " + file.getCanonicalPath());
			}
		}
	}

	@Test
	public void testClideKeyGenGoodArguments() {
		try {
			ClideKeyGen.main(new String[] { publicKey.getCanonicalPath(),
					privateKey.getCanonicalPath() });
		} catch (IOException e) {
			fail("Choked on valid files");
		} catch (NoSuchAlgorithmException e) {
			fail("No such algorithm?  What version of Java are you using??!?");
		}
		assertTrue("Public key was not created", publicKey.exists());
		assertTrue("Private key was not created", privateKey.exists());
		assertTrue("Public key is somehow larger than private?",
				publicKey.length() < privateKey.length());

	}

	@Test(expected = IOException.class)
	public void testClideKeyGenBadPublicKeyPath()
			throws NoSuchAlgorithmException, IOException {
		File pub = new File(CLIDE_SAMPLES_PATH + "MORDORNUMBERONEONEONEELEVEN/privateKeyText.txt");
		ClideKeyGen.main(new String[] { pub.getCanonicalPath(),
				privateKey.getCanonicalPath() });
		fail("ClideKeyGen didn't stop when it hit a bad file");

	}

	@Test(expected = IOException.class)
	public void testClideKeyGenBadPrivateKeyPath()
			throws NoSuchAlgorithmException, IOException {
		File priv = new File(CLIDE_SAMPLES_PATH + "WATCHOUTFORWALKINGTREES/privateKeyText.txt");
		ClideKeyGen.main(new String[] { publicKey.getCanonicalPath(),
				priv.getCanonicalPath() });
		fail("ClideKeyGen didn't stop when it hit a bad file");

	}

	// @Test
	// public void testClientGoodArguments() {
	// try {
	// ClideClientImpl.main(new String[]{System.getProperty("user.dir") +
	// "/clide/clide.properties"});
	//
	// } catch (IllegalArgumentException iax) {
	// fail(iax.getMessage());
	//
	// } catch (URISyntaxException usx) {
	// fail(usx.getMessage());
	// }
	// // The client was initialized correctly and attempted to start, but since
	// the server is not running the client
	// // then quits (and this test doesn't hang.)
	// }
	//
	// @Test
	// public void testServerGoodArguments() {
	// try {
	// ClideServer.main(new String[]{System.getProperty("user.dir") +
	// "/clide/clide.properties"});
	//
	// } catch (IllegalArgumentException iax) {
	// fail(iax.getMessage());
	//
	// } catch (URISyntaxException usx) {
	// fail(usx.getMessage());
	//
	// }
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testClientTooManyArguments() throws URISyntaxException {
	// ClideClientImpl.main(new String[]{"file.1", "extraArg.2"});
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testServerTooManyArguments() throws URISyntaxException {
	// ClideServer.main(new String[]{"Squirrel.arg", "Doc.ument"});
	//
	// }
	//
	// @Test (expected = NullPointerException.class)
	// public void testClientWrongFile() throws URISyntaxException {
	// ClideClientImpl.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/outcomes.private.key"
	// });
	// }
	//
	// @Test (expected = NullPointerException.class)
	// public void testServerWrongFile() throws URISyntaxException {
	// ClideServer.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/outcomes.private.key"
	// });
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testClientPropertyFileDoesntExist() throws URISyntaxException
	// {
	// ClideClientImpl.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/frodo/bilbo/gandalf/derp.derp88"
	// });
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testServerPropertyFileDoesntExist() throws URISyntaxException
	// {
	// ClideServer.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/frodo/bilbo/gandalf/derp.derp88"
	// });
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testClientBadDestinationProperty() throws URISyntaxException
	// {
	// ClideClientImpl.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/destinationDirDoesntExist.properties"
	// });
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testClientBadProcessedProperty() throws URISyntaxException {
	// ClideClientImpl.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/processedDirDoesntExist.properties"
	// });
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testServerBadDestinationProperty() throws URISyntaxException
	// {
	// ClideServer.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/sourceDirDoesntExist.properties"
	// });
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testServerBadWorkingProperty() throws URISyntaxException {
	// ClideServer.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/workingDirIsFile.properties"
	// });
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testServerBadSentProperty() throws URISyntaxException {
	// ClideServer.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/sentDirIsFile.properties"
	// });
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testClientBadPrivateKeyProperty() throws URISyntaxException {
	// ClideClientImpl.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/privateKeyDoesntExist.properties"
	// });
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testServerBadPrivateKeyProperty() throws URISyntaxException {
	// ClideServer.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/publicKeyDoesntExist.properties"
	// });
	// }
	//
	// @Test (expected = IllegalArgumentException.class)
	// public void testMissingEncryptionValue() throws URISyntaxException {
	// ClideClientImpl.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/badEncryptionValue.properties"
	// });
	// }
	//
	// @Test
	// public void testClientBadTimeoutProperty() throws URISyntaxException {
	// boolean illegalArg = false;
	// IllegalArgumentException iae = null;
	//
	// try {
	// ClideClientImpl.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/badTimeout.properties"
	// });
	//
	// } catch (IllegalArgumentException iax) {
	// illegalArg = true;
	// iae = iax;
	//
	// } catch (URISyntaxException usx) {
	// fail(usx.getMessage());
	// }
	// assertTrue("Should have received IllegalArgumentException", illegalArg);
	// assertTrue("Wrong messagein exception",
	// iae.getMessage().contains("timeoutInSeconds"));
	//
	// }
	//
	// @Test
	// public void testServerBadTimeoutProperty() throws URISyntaxException {
	// boolean illegalArg = false;
	// IllegalArgumentException iae = null;
	// try {
	// ClideServer.main(new String[]{
	// System.getProperty("user.dir") +
	// "/clide/src/test/resources/samples/badTimeout.properties"
	// });
	//
	// } catch (IllegalArgumentException iax) {
	// illegalArg = true;
	// iae = iax;
	//
	// } catch (URISyntaxException usx) {
	// fail(usx.getMessage());
	// }
	// assertTrue("Should have received IllegalArgumentException", illegalArg);
	// assertTrue("Wrong message in exception",
	// iae.getMessage().contains("timeoutInSeconds"));
	// }

}
