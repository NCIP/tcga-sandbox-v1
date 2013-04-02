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
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utils class for commonly used method for clide
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Scope("prototype")
@Component
public class ClideUtilsImpl implements ClideUtils {

    @Autowired
    MailSender mailSender;

    private static final Logger logger = Logger.getLogger(ClideUtilsImpl.class.getName());

    @Override
    public ClientContext setUpDirectories(final ClientContext clientContext) {    	    	
        clientContext.setDownloadDir(validateClientDownloadedDirectory(clientContext.getDestinationPath()));
        clientContext.setProcessedDir(validateClientProcessedDirectory(clientContext.getDestinationPath(),clientContext.getProcessedPath()));   
        return clientContext;
    }

    @Override
    public void checkDiskSpace(ClientContext clientContext) {
        long thresholdinBytes = validateDiskSpaceThreshold(clientContext);
        checkMountSpace(mailSender, "Download Directory", clientContext.getDownloadDir(),clientContext.getNoSpaceEmailTo(), clientContext.getNoSpaceEmailBcc(),
        		clientContext.getNoSpaceEmailSubject(), clientContext.getNoSpaceEmailContent(), thresholdinBytes);
        checkMountSpace(mailSender, "Processed Directory", clientContext.getProcessedDir(), clientContext.getNoSpaceEmailTo(), clientContext.getNoSpaceEmailBcc(),
        		clientContext.getNoSpaceEmailSubject(), clientContext.getNoSpaceEmailContent(), thresholdinBytes);
    }

    private long validateDiskSpaceThreshold(ClientContext clientContext ) {
        int threshold;
        try {
            threshold = Integer.valueOf(clientContext.getDiskSpaceThreshold());
        } catch (NumberFormatException e) {
            final String msg = "Invalid value for diskSpaceThreshold";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg, e);
        }
        return threshold * 1048576L;
    }

    private void checkMountSpace(final MailSender mailSender, String mountStr, File mount, String to, String bcc,
                                 String subject, String body, long threshold) {
        if (mount.getFreeSpace() < threshold) {
            subject += ": " + mountStr;
            mailSender.send(to, bcc, subject, body, true);
            throw new RuntimeException(subject);
        }
    }

    @Override
    public boolean checkClientFreeSpace(final long FileSize,final ClientContext clientContext) {    	
    	
        String subject;
        long thresholdinBytes = validateDiskSpaceThreshold(clientContext);
        if ((clientContext.getDownloadDir().getFreeSpace() - thresholdinBytes) < FileSize) {
            subject = clientContext.getNoSpaceEmailSubject() + ": Download Directory";
            mailSender.send(clientContext.getNoSpaceEmailTo(), clientContext.getNoSpaceEmailBcc(), subject, clientContext.getNoSpaceEmailContent(), true);
            return false;
        } else if ((clientContext.getProcessedDir().getFreeSpace() - thresholdinBytes) < FileSize) {
            subject = clientContext.getNoSpaceEmailSubject() + ": Processed Directory";
            mailSender.send(clientContext.getNoSpaceEmailTo(), clientContext.getNoSpaceEmailBcc(), subject, clientContext.getNoSpaceEmailContent(), true);
            
            return false;
        } else return true;
    }

    @Override
    public int validateTimeout(final String timeoutInSeconds) {
        int timeout;
        try {
            if (timeoutInSeconds != null && timeoutInSeconds.length() > 0) {
                timeout = Integer.valueOf(timeoutInSeconds);
            } else {
                timeout = ClideConstants.DEFAULT_TIMEOUT;
            }
        } catch (NumberFormatException NFX) {
            final String msg = "Invalid value for timeoutInSeconds";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
        return timeout;
    }

    @Override
    public File validateClientDownloadedDirectory(final String downloadedDirectory) {
        final File downloadedDir = new File(downloadedDirectory);
        checkValidDirectory(downloadedDir);
        return downloadedDir;
    }

    @Override
    public File validateClientProcessedDirectory(final String processedDirectory, final String downloadedDirectory) {
        final File processedDir = new File(processedDirectory);
        final File downloadedDir = new File(downloadedDirectory);
        if (processedDirectory != null && processedDirectory.length() > 0) {
            checkValidDirectory(processedDir);
            return processedDir;
        } else {
            checkValidDirectory(downloadedDir);
            return downloadedDir;
        }
    }

    public void checkValidDirectory(File dir) {
        if (!dir.exists() || !dir.isDirectory() || !dir.canWrite()) {
            final String msg = "Unable to write files to " + dir.getAbsolutePath();
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public boolean validateEncryption(final String encrypt) {
        if (encrypt != null && encrypt.length() > 0) {
            return Boolean.parseBoolean(encrypt);
        } else {
            final String msg = "clideEnableEncryption property is not set.";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public boolean validateForceValidate(final String validate) {
        if (validate != null && validate.length() > 0) {
            return Boolean.parseBoolean(validate);
        } else {
            final String msg = "forceValidate property is not set.";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public File validatePrivateKey(final String privateKey) {
        File key = new File(privateKey);
        if (!key.exists() || key.isDirectory()) {
            final String msg = "No private key found for decryption";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);

        }
        return key;
    }

    @Override
    public File validatePublicKey(final String publicKey) {
        File key = new File(publicKey);
        if (!key.exists() || key.isDirectory()) {
            final String msg = "No public key found for decryption";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);

        }
        return key;
    }

    @Override
    public boolean validateClientInternalLogging(final String clientInternalLogging) {
        return Boolean.parseBoolean(clientInternalLogging);
    }

    @Override
    public boolean validateServerInternalLogging(final String serverInternalLogging) {
        return Boolean.parseBoolean(serverInternalLogging);
    }

    @Override
    public File validateServerArchiveDirectory(final String archiveDirectory) {
        final File archiveDir = new File(archiveDirectory);
        if (!archiveDir.exists() || !archiveDir.isDirectory() || !archiveDir.canRead()) {
            final String msg = "Directory " + archiveDirectory + " does not exist or cannot be read from.";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
        return archiveDir;
    }

    @Override
    public File validateServerSentDirectory(final String sentDirectory, final String archiveDirectory) {
        final File archiveDir = new File(archiveDirectory);
        File sentDir = new File(sentDirectory);
        if (sentDirectory == null || sentDirectory.length() == 0) {
            sentDir = archiveDir;
        }
        if (!sentDir.exists() || !sentDir.isDirectory() || !sentDir.canWrite()) {
            final String msg = "Directory " + sentDirectory + " does not exist or cannot be read or written to";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
        return sentDir;
    }

    @Override
    public File validateServerWorkingDirectory(final String workingDirectory, final String archiveDirectory) {
        final File archiveDir = new File(archiveDirectory);
        File workingDir = new File(workingDirectory);
        if (workingDirectory == null || workingDirectory.length() == 0) {
            workingDir = archiveDir;
        }
        if (!workingDir.exists() || !workingDir.isDirectory() || !workingDir.canRead() || !workingDir.canWrite()) {
            final String msg = "Directory " + workingDirectory + " does not exist or cannot be read or written to";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
        return workingDir;
    }

    @Override
    public boolean validateServerOnWindows(final String serverOnWindows) {
        return Boolean.parseBoolean(serverOnWindows);
    }

    @Override
    public void copyFilesFromTo(final File fromDir, final File toDir) throws IOException {
        if (fromDir == toDir) {
            // This is possible on the client and server side
            return;
        }
        boolean success;
        File[] contents = fromDir.listFiles(new ClideConstants.ArchiveFilter());
        for (final File file : contents) {
            success = FileUtil.copyFile(file.getCanonicalPath(), toDir.getCanonicalPath() + File.separator + file.getName());
            if (!success) {
                final String msg = "ERROR: " + file.getName() + " COULD NOT BE COPIED CORRECTLY TO " + toDir.getCanonicalPath();
                logger.error(msg);
                throw new RuntimeException(msg);
            }
        }
    }

    @Override
    public void moveAllFilesIfNecessary(File fromDir, File toDir) throws IOException {
        boolean success;
        if (!fromDir.equals(toDir)) {
            String sentDirPath = null;
            // Move files to new directory
            for (final File file : fromDir.listFiles()) {
                if (file.isFile()) {
                    sentDirPath = toDir.getCanonicalPath();
                    final boolean rename = file.renameTo(new File(sentDirPath + File.separator + file.getName()));
                    if (!rename) {
                        success = FileUtil.copyFile(file.getCanonicalPath(), sentDirPath + File.separator + file.getName());
                        success = success & file.delete();
                        if (!success) {
                            final String msg = "ERROR: " + file.getName() + " COULD NOT BE MOVED CORRECTLY TO " + sentDirPath;
                            logger.error(msg);
                            throw new RuntimeException(msg);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getFormattedAsSeconds(final long milliseconds) {
        return milliseconds / 1000 + " seconds";
    }

    /**
     * Formats bytes per millisecond provided into Kilobytes, Megabytes and Gigabytes per second
     *
     * @param totalBytes       interesting bytes over duration
     * @param durationInMillis the duration of time the bytes were interesting
     * @return e.g. 1MB/s 60MB/m 3600MB/h
     * @see gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil#getFormattedFileSize(long)
     */
    @Override
    public String getFormattedThroughput(final long totalBytes, final long durationInMillis) {
        if (durationInMillis == 0 || durationInMillis / 1000L == 0) {
            return FileUtil.getFormattedFileSize(totalBytes) + " in < 1s";
        }
        long seconds = (durationInMillis / 1000L);
        long bytesPerSecond = totalBytes / seconds;

        StringBuilder sb = new StringBuilder();
        String bps = FileUtil.getFormattedFileSize(bytesPerSecond);
        sb.append(bps + " in " + seconds + "s (");
        sb.append(FileUtil.getFormattedFileSize(bytesPerSecond));
        sb.append("/s, ");
        sb.append(FileUtil.getFormattedFileSize(bytesPerSecond * 60L));
        sb.append("/m, ");
        sb.append(FileUtil.getFormattedFileSize((bytesPerSecond * 3600L)));
        sb.append("/h)");
        return sb.toString();

    }


    /**
     * This method will check if a directory contain any files
     * and if it does,  will remove everything from the directory.
     *
     * @param downloadDirectory - download directory that is to be checked for partially downloaded files.
     */
    @Override
    public void cleanUpDirectory(String downloadDirectory) {

        if (StringUtils.isEmpty(downloadDirectory)) {
            throw new IllegalArgumentException(" The directory can't be empty. Check your arguments and try again");
        }

        final File dir = new File(downloadDirectory);

        // not making any assumptions, in case this method is called
        // by some other logic in the future
        if (!dir.exists()) {
            throw new IllegalArgumentException(" The directory does not exist. The directory path is : " + downloadDirectory);
        }
        File[] files = dir.listFiles();
        // files array can only be null if the dir doesn't exist and this case is covered above

        if (files.length > 0) {
            for (File fileToDelete : files) {
                logger.debug("Deleting partially processed file : " + fileToDelete.getName());
                fileToDelete.delete();
            }
        } else {
            logger.debug("The directory " + downloadDirectory + " is empty.");
        }
    }
    /**
     * This method reads in a property file. The method will first attempt to 
     * read the properties from the file path indicated by the clideProperties parameter
     * if not successful will try to read from classpath.
     * @param clideProperties property file to read
     * @return object containing properties
     */
    public static Properties getClideProperties(final String clideProperties) {

        if (StringUtils.isEmpty(clideProperties)){
            throw new IllegalArgumentException(" property file can't be null");
        }
    	Properties result = null;
        InputStream propertiesInputStream = null;
        File propertiesFile = new File(clideProperties);
        // attempt to open file
        if (propertiesFile.exists()){
            try{
                propertiesInputStream = new FileInputStream(propertiesFile);
                if (propertiesInputStream != null)
    	        {
    	            result = new Properties ();
    	            result.load (propertiesInputStream); // Can throw IOException
    	        }                                               
            }catch (FileNotFoundException e){
                // should never happen since I am checking the existence of the file above
                logger.error(e);
            }catch (IOException e){
        		logger.error(e);
        	}            
            finally{
            	if (propertiesInputStream != null){
            		try {
                        propertiesInputStream.close();
					} catch (IOException e) {						
						logger.error(e);
					}
            	}
            }
        }
        //	attempt to get it of classpath
        else if (ClassLoader.getSystemClassLoader ().getResourceAsStream (clideProperties) != null){
            propertiesInputStream = ClassLoader.getSystemClassLoader ().getResourceAsStream (clideProperties);
        	try{
	        	if (propertiesInputStream != null)
		        {
		            result = new Properties ();
		            result.load (propertiesInputStream); // Can throw IOException
		        }                                               	        
	        }catch (IOException e){
	    		logger.error(e);
	    	}            
	        finally{
	        	if (propertiesInputStream != null){
	        		try {
                        propertiesInputStream.close();
					} catch (IOException e) {						
						logger.error(e);
					}
	        	}
	                	
	        }	        
        }
        return result;
    }

	
}

