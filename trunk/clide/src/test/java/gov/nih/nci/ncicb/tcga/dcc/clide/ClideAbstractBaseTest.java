/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide;

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClient;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.DccCenter;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtilsImpl;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ClideServer;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.md5.MD5ChecksumCreator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

/**
 * An abstract base class to put in the common methods and fields used in
 * testing our I/O operations
 * 
 * @author Jon Whitmore Last updated by: $Author$
 * @version $Rev$
 */
public abstract class ClideAbstractBaseTest {

	protected static final int PORT = 8080;
	protected static final String SERVER_ADDR = "localhost";
	protected static final String SERVER_URL = "http://" + SERVER_ADDR + ":"
			+ PORT;

	protected static final String CLIDE_SAMPLES_PATH = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

	/**
	 * This directory contains files that shouldn't be deleted as they are used
	 * for all tests *
	 */
	protected static final String ORIGINALS_DIR = CLIDE_SAMPLES_PATH + "original_files";

	/**
	 * This directory should be setup to contain the files the server will send
	 * *
	 */
	protected static final String FROM_DIR = CLIDE_SAMPLES_PATH + "to_send";

	/**
	 * This directory is used as a place for the server to move files that have
	 * been transferred *
	 */
	protected static final String SENT_DIR = CLIDE_SAMPLES_PATH + "sent";

	/**
	 * This directory is where the server will encrypt files. It should always
	 * be different that FROM_DIR. To test when the two are the same pass in
	 * FROM_DIR for both directory parameters
	 */
	protected static final String WORKING_DIR = CLIDE_SAMPLES_PATH
			+ "server_working";

	/**
	 * This directory is where the client will download to *
	 */
	protected static final String TO_DIR = CLIDE_SAMPLES_PATH + "received";

	/**
	 * This directory is where the client will copy decrypted, MD5-checked files
	 * for further processing *
	 */
	protected static final String PROCESSED_DIR = CLIDE_SAMPLES_PATH
			+ "processed";

	protected MD5ChecksumCreator md5Creator = new MD5ChecksumCreator();

	protected ClideConstants.ArchiveFilter archiveFilter = new ClideConstants.ArchiveFilter();

	protected ConfigurableApplicationContext ctx = new FileSystemXmlApplicationContext(
			"file:"+CLIDE_SAMPLES_PATH + "applicationContext-TESTClide.xml");
	protected ConfigurableApplicationContext ctx2 = new ClassPathXmlApplicationContext(
			new String[] {"samples/applicationContext-TESTClide.xml", "integrationContext-clide.xml" });
	
	protected ConfigurableApplicationContext ctx3 = new FileSystemXmlApplicationContext(
			"file:"+CLIDE_SAMPLES_PATH + "applicationContext-BADClide.xml");
	protected ClideClient client = null;
	protected ClideServer server = null;

	protected void clearDir(final String dir) throws IOException {
		File trans = new File(dir);
		File[] contents = trans
				.listFiles(new ClideConstants.NonHiddenFileFilter());
		for (int i = contents.length - 1; i >= 0; --i) {
			File file = contents[i];
			if (file.isDirectory()) {
				clearDir(file.getCanonicalPath());
			}
			boolean success = file.delete();
			assertTrue("Unable to delete " + file.getName(), success);
		}
		// }
	}

	/**
	 * remove all files from the directory we are transferring too
	 */
	protected void clearFilesUsed() throws IOException {
		clearDir(TO_DIR);
		clearDir(FROM_DIR);
		clearDir(SENT_DIR);
		clearDir(PROCESSED_DIR);
	}

	protected void putAllFilesIn(final String aDirectory) throws IOException {
		File originalDirectory = new File(ORIGINALS_DIR);
		File[] contents = originalDirectory.listFiles(archiveFilter);
		for (final File o : contents) {
			FileUtil.copyFile(o.getCanonicalPath(), aDirectory + File.separator
					+ o.getName());
		}
	}
	
	 protected ClideClient getTestClient() throws URISyntaxException {
	       ClideClient client = (ClideClient) ctx.getBean("clideClientImpl");

	        String clientProeprties =  CLIDE_SAMPLES_PATH + "workingDirIsFile.properties";

	       ClientContext clientContext = new ClientContext();
	       Properties properties = ClideUtilsImpl.getClideProperties(clientProeprties);

	        final URI uri = new URI(properties.getProperty("serverURI"));
	        final String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
	        final String host = uri.getHost() == null ? "localhost" : uri.getHost();
	        final int port = uri.getPort() == -1 ? 80 : uri.getPort();
	        clientContext.setUri(uri);
	        clientContext.setHost(host);
	        clientContext.setPort(port);

	        String dowloadDir = CLIDE_SAMPLES_PATH + "processed";

	         clientContext.setDownloadDir(new File(dowloadDir));


	       String processedPath = CLIDE_SAMPLES_PATH + "processed";
	        clientContext.setProcessedPath(processedPath);


	       String privateKeyFile = CLIDE_SAMPLES_PATH + "outcomes.private.key";

	       clientContext.setPrivateKey(new File(privateKeyFile));
	       clientContext.setEncryptionEnabled(new Boolean(properties.getProperty("clideEnableEncryption")));
	       clientContext.setTimeout(new Integer(properties.getProperty("timeoutInSeconds")));
	       clientContext.setForceValidate(new Boolean(properties.getProperty("forceValidate")));
	       clientContext.setDiskSpaceThreshold(properties.getProperty("diskSpaceThreshold"));
	       clientContext.setNoSpaceEmailTo(properties.getProperty("noSpaceEmailTo"));
	       clientContext.setNoSpaceEmailBcc(properties.getProperty("noSpaceEmailBcc"));
	       clientContext.setNoSpaceEmailContent(properties.getProperty("noSpaceEmailSubject"));
	       clientContext.setNoSpaceEmailContent(properties.getProperty("noSpaceEmailContent"));
	       clientContext.setInternalLogging(new Boolean(properties.getProperty("serverInternalLogging")));
	       clientContext.setCenter(DccCenter.UNC);
	       client.setClientContext(clientContext);
	       return client;

	    }

}
