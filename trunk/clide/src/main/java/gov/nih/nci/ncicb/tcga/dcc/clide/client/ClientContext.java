/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.client;

import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.DccCenter;

import java.io.File;
import java.net.URI;

import org.jboss.netty.bootstrap.ClientBootstrap;

/**
 * This class is currently a band aid to share values between client objects
 * that currently do not have direct couplings with each other
 * 
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ClientContext {

	private String host = null;
	private int port = 0;
	private URI uri = null;
	private File downloadDir = null;
	private File processedDir = null;
	private boolean encryptionEnabled = true;
	private boolean forceValidate = true;
	private File privateKey = null;
	private String requestedPath = null;
	private String expectedMD5 = null;
	private boolean internalLogging = false;
	private String noSpaceEmailTo;
	private String noSpaceEmailBcc;
	private String noSpaceEmailSubject;
	private String noSpaceEmailContent;
	private String diskSpaceThreshold;
	private String destinationPath;
	private ClientBootstrap bootsrap = null;
	private ClideClientPipelineFactory factory = null;
	private DccCenter center = null;

	public DccCenter getCenter() {
		return center;
	}

	public void setCenter(DccCenter center) {
		this.center = center;
	}

	public ClideClientPipelineFactory getFactory() {
		return factory;
	}

	public void setFactory(ClideClientPipelineFactory factory) {
		this.factory = factory;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public String getProcessedPath() {
		return processedPath;
	}

	public void setProcessedPath(String processedPath) {
		this.processedPath = processedPath;
	}

	private String processedPath;

	/**
	 * begin state variables *
	 */
	private boolean readingChunks = false;
	private int timeout;

	public boolean isEncryptionEnabled() {
		return encryptionEnabled;
	}

	public void setEncryptionEnabled(final boolean newEncryptionEnabled) {
		encryptionEnabled = newEncryptionEnabled;
	}

	public void setForceValidate(final boolean newForceValidate) {
		forceValidate = newForceValidate;
	}

	public boolean getForceValidate() {
		return forceValidate;
	}

	/**
	 * In the future this method will determine what to encrypt on a file by
	 * file basis. For now we just encrypt everything or not.
	 * 
	 * @param pathName
	 *            the name of the file on the remote server
	 * @return true iff the file should be encrypted.
	 */
	public boolean shouldEncryptFile(final String pathName) {
		// look at file name
		// look at host
		// check to see if it is enabled
		return isEncryptionEnabled();
	}

	public void setInternalLogging(final boolean bool) {
		internalLogging = bool;
	}

	public boolean getInternalLogging() {
		return internalLogging;

	}

	public String getExpectedMD5() {
		return expectedMD5;
	}

	public void setExpectedMD5(final String newExpectedMD5) {
		expectedMD5 = newExpectedMD5;
	}

	public void setRequestedPath(final String p) {
		requestedPath = p;

	}

	public String getRequestedPath() {
		return requestedPath;
	}

	public boolean isReadingChunks() {
		return readingChunks;
	}

	public void setReadingChunks(final boolean bool) {
		readingChunks = bool;
	}

	public String getHost() {
		return host;
	}

	public void setHost(final String newHost) {
		host = newHost;
	}

	public int getPort() {
		return port;
	}

	public void setPort(final int newPort) {
		port = newPort;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(final URI newUri) {
		uri = newUri;
	}

	public File getDownloadDir() {
		return downloadDir;
	}

	public void setDownloadDir(final File newDownloadDirectory) {
		downloadDir = newDownloadDirectory;
	}

	public File getProcessedDir() {
		return processedDir;
	}

	public void setProcessedDir(final File nwProcessedDir) {
		processedDir = nwProcessedDir;
	}

	public void setPrivateKey(final File key) {
		privateKey = key;
	}

	public File getPrivateKey() {
		return privateKey;
	}

	public void setTimeout(final int timeoutInSeconds) {
		timeout = timeoutInSeconds;
	}

	public int getTimeout() {
		return timeout;
	}

	public String getNoSpaceEmailTo() {
		return noSpaceEmailTo;
	}

	public void setNoSpaceEmailTo(String noSpaceEmailTo) {
		this.noSpaceEmailTo = noSpaceEmailTo;
	}

	public String getNoSpaceEmailBcc() {
		return noSpaceEmailBcc;
	}

	public void setNoSpaceEmailBcc(String noSpaceEmailBcc) {
		this.noSpaceEmailBcc = noSpaceEmailBcc;
	}

	public String getNoSpaceEmailSubject() {
		return noSpaceEmailSubject;
	}

	public void setNoSpaceEmailSubject(String noSpaceEmailSubject) {
		this.noSpaceEmailSubject = noSpaceEmailSubject;
	}

	public String getNoSpaceEmailContent() {
		return noSpaceEmailContent;
	}

	public void setNoSpaceEmailContent(String noSpaceEmailContent) {
		this.noSpaceEmailContent = noSpaceEmailContent;
	}

	public String getDiskSpaceThreshold() {
		return diskSpaceThreshold;
	}

	public void setDiskSpaceThreshold(String diskSpaceThreshold) {
		this.diskSpaceThreshold = diskSpaceThreshold;
	}

	public ClientBootstrap getBootsrap() {
		return bootsrap;
	}

	public void setBootsrap(ClientBootstrap bootsrap) {
		this.bootsrap = bootsrap;
	}
}
