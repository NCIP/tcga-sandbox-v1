/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.common;

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;

import java.io.File;
import java.io.IOException;

/**
 * interface defining ClideUtils allowing for use of Mocks or swap implementation
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ClideUtils {

	ClientContext setUpDirectories(ClientContext clientContext);

    void checkDiskSpace(ClientContext clientContext);

    void cleanUpDirectory(String downloadedDirectory);

    boolean checkClientFreeSpace(long FileSize,ClientContext clientContext);

    int validateTimeout(String timeoutInSeconds);

    File validateClientDownloadedDirectory(String downloadedDirectory);

    File validateClientProcessedDirectory(String downloadedDirectory, String processedDirectory);

    boolean validateEncryption(String encrypt);

    boolean validateForceValidate(String validate);

    File validatePrivateKey(String privateKey);

    File validatePublicKey(String publicKey);

    boolean validateClientInternalLogging(String clientInternalLogging);

    boolean validateServerInternalLogging(String serverInternalLogging);

    File validateServerArchiveDirectory(String archiveDirectory);

    File validateServerSentDirectory(String sentDirectory, String archiveDirectory);

    File validateServerWorkingDirectory(String workingDirectory, String archiveDirectory);

    boolean validateServerOnWindows(String serverOnWinows);

    void copyFilesFromTo(File fromDir, File toDir) throws IOException;

    void moveAllFilesIfNecessary(File fromDir, File toDir) throws IOException;

    String getFormattedAsSeconds(final long milliseconds);

    String getFormattedThroughput(final long totalBytes, final long durationInMillis);

}
