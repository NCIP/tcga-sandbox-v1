/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import org.apache.log4j.Level;
import org.quartz.SchedulerException;
import org.sadun.util.polling.BasePollManager;
import org.sadun.util.polling.CycleStartEvent;
import org.sadun.util.polling.FileFoundEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Robert S. Sfeir
 */
public class TCGAPollManagerService extends BasePollManager {

    private File[] theDirs = null;
    private String extensionToExclude = null;
    private long delayBeforeStartProcess = 0L;
    private JobScheduler jobScheduler;
    private MailErrorHelper mailErrorHelper;
    private int timeBetweenFileDoneCheck = 1000;
    private long timeToWaitForMd5;
    private Logger logger;
    private String bulkReceivedWorkingDirectory;

    public void setMillisecondsToWaitInMoveLoop(final int millisecondsToWaitInMoveLoop) {
        this.millisecondsToWaitInMoveLoop = millisecondsToWaitInMoveLoop;
    }

    private int millisecondsToWaitInMoveLoop = 30000;

    public TCGAPollManagerService() {
    }

    String getExtensionToExclude() {
        return extensionToExclude;
    }

    public void setExtensionToExclude(final String extensionToExclude) {
        this.extensionToExclude = extensionToExclude;
    }

    long getDelayBeforeStartProcess() {
        return delayBeforeStartProcess;
    }

    public void setDelayBeforeStartProcess(final long delayBeforeStartProcess) {
        this.delayBeforeStartProcess = delayBeforeStartProcess;
    }

    public int getTimeBetweenFileDoneCheck() {
        return timeBetweenFileDoneCheck;
    }

    public void setTimeBetweenFileDoneCheck(final int timeBetweenFileDoneCheck) {
        this.timeBetweenFileDoneCheck = timeBetweenFileDoneCheck;
    }

    public long getTimeToWaitForMd5() {
        return timeToWaitForMd5;
    }

    public void setTimeToWaitForMd5(final long timeToWaitForMd5) {
        this.timeToWaitForMd5 = timeToWaitForMd5;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Checks whether a directory to poll exists for each poll cycle.
     */
    @Override
    public void cycleStarted(final CycleStartEvent evt) {   
    	final List<String> notificationList = new ArrayList<String>();
    	boolean sendNotification = false;
    	
    	if (evt != null && evt.getPoller().getDirectories() != null && evt.getPoller().getDirectories().length > 0 ){
    		for (File dir:evt.getPoller().getDirectories()){
    			if (!dir.exists() || !dir.canRead() || !dir.canWrite()){
    				sendNotification = true;
    				notificationList.add(dir.getPath());    				
    			}
    		}
    	}else{
    		String errorMessage = " QClive Directory Poller is unable to poll. There are no directories specified in QCLive properties. ";    				
			getLogger().log(Level.ERROR, errorMessage);
			getMailErrorHelper().send(errorMessage,"");
    	}
    	if (sendNotification){
    		
    		final StringBuilder messageToSend = new StringBuilder();
    		messageToSend.append(" QClive Directory Poller is unable to poll: ");    		    		
    		for (String directory:notificationList){
    			messageToSend.append(directory + " ");
    		}
    		messageToSend.append(" Make sure the directory exists with correct read/write permissions");
    		getLogger().log(Level.ERROR, messageToSend.toString());
    		getMailErrorHelper().send(messageToSend.toString(),"");
    	}    	    	
    }
    
    public void fileMoved(final File movedFile) {
        String fullFilePath = null;
        final String fileMovedName = movedFile.getName();
        getLogger().log(Level.DEBUG, "[FILE MOVED] File moved was called for " + fileMovedName);
        try {
            fullFilePath = movedFile.getCanonicalPath();
            getLogger().log(Level.DEBUG, "[FILE MOVED] A file was just moved to received: " + movedFile.getName());
            if (!fullFilePath.endsWith(extensionToExclude)) {
                getLogger().log(Level.DEBUG, "[FILE MOVED] Starting process to schedule file: " + movedFile.getName());
                //check to see if the file is finished copying here as well
                //it is possible that the rename process might take longer than expected
                //and trigger this method early.  That is causing the md5 check to fail.
                //In this case we check the file size because the date of the upload doesn't change.
                long lastSize = movedFile.length();
                try {
                    //We sleep 30 seconds to give the next block a chance to check for the file size
                    //Otherwise that happens too fast and the thread fails.
                    getLogger().log(Level.DEBUG, "[FILE MOVED] Waiting 30 seconds for archive to finish copying: " + movedFile.getName());
                    Thread.sleep(millisecondsToWaitInMoveLoop);
                } catch (InterruptedException e) {
                	logAndEmail("[FILE MOVED] For some reason the fileFound wait thread got interrupted!", e);                	
                }
                while (movedFile.length() != lastSize) {
                    //Check file size every 30 seconds.  If change didn't happen, file is done copying.
                    lastSize = movedFile.length();
                    Thread.sleep(millisecondsToWaitInMoveLoop);
                    getLogger().log(Level.DEBUG, "[FILE MOVED] File being moved... waiting for " + movedFile.getName());
                }
                //File modified check is done we now proceed forward.
                getLogger().log(Level.DEBUG, "[FILE MOVED] File finished copy for: " + movedFile.getName());
                final Calendar whenToRun = calculateWhenJobShouldRun();
                getLogger().log(Level.INFO, "Scheduled upload check for: " + movedFile.getName());
                jobScheduler.scheduleUploadCheck(movedFile, whenToRun,null);

            }
        } catch (SchedulerException e) {
            getMailErrorHelper().send("There was a problem scheduling the job for " + fullFilePath, ProcessLogger.stackTracePrinter(e));
            logger.log(e);
        } catch (IOException e) {
            getMailErrorHelper().send("There was a problem with the file while scheduling a job for: " + fullFilePath, ProcessLogger.stackTracePrinter(e));
            logger.log(e);
        } catch (InterruptedException e) {
            getMailErrorHelper().send("The thread which checks for file moved was interrupted", ProcessLogger.stackTracePrinter(e));
            logger.log(e);
        }
    }

    protected void logAndEmail(String message, Exception e){    	
    	logger.log(Level.ERROR, message );
    	logger.log(e);
        getMailErrorHelper().send(message, ProcessLogger.stackTracePrinter(e));
    }

    protected Calendar calculateWhenJobShouldRun() {
        final Calendar whenToRun = Calendar.getInstance();
        whenToRun.setTimeInMillis(whenToRun.getTimeInMillis() + getTimeToWaitForMd5());
        return whenToRun;
    }

    /**
     * When a file is uploaded to a watched directory an event is triggered firing off this method.
     * In this method we now check to see if the file is still being uploaded.  The change would be the last time the file
     * was modified.  When a file is moved out of the queue it's modified time registers as 0.  So we need to check to make sure
     * that the fileFound even is not being triggered because of an old notification.
     *
     * @param theFileWeFound The file that was uploaded.
     */
    public void fileFound(final FileFoundEvent theFileWeFound) {
        handleFileFound(theFileWeFound.getFile());
    }

    protected void handleFileFound(final File theFileWeFound) {
        long lastModified = theFileWeFound.lastModified();
        getLogger().log(Level.DEBUG, "[FILE FOUND] last modified for " + theFileWeFound.getName() + " is " + lastModified);
        if (lastModified > 0) {
            getLogger().log(Level.DEBUG, "[FILE FOUND] last modified for " + theFileWeFound.getName() + " is " + lastModified);
            try {
                //We sleep 15 seconds to give the next block a chance to check for the file modification time
                //Otherwise that happens too fast and we don't see a mod time.
                getLogger().log(Level.DEBUG, "[FILE FOUND] sleeping initial 15 seconds");
                Thread.sleep(15000);
                getLogger().log(Level.DEBUG, "[FILE FOUND] waking from initial 15 seconds");
            } catch (InterruptedException e) {
                getMailErrorHelper().send("For some reason the fileFound wait thread got interrupted!", ProcessLogger.stackTracePrinter(e));
            }
            while (theFileWeFound.lastModified() != lastModified) {
                getLogger().log(Level.DEBUG, "File being uploaded... waiting for " + theFileWeFound.getName());
                lastModified = theFileWeFound.lastModified();
                try {
                    //We need to slow down checking for the file's modified time in case there is a timeout on the upload speed.
                    //That value is defaulted to 1 second but check the spring xml context file to see what the real value is.
                    Thread.sleep(getTimeBetweenFileDoneCheck());
                } catch (InterruptedException e) {
                	logAndEmail ("For some reason the fileFound wait thread got interrupted!",e);
                }
            }
            try {
                String location;
                if (bulkReceivedWorkingDirectory != null && bulkReceivedWorkingDirectory.trim().length() > 0) {
                    location = bulkReceivedWorkingDirectory;
                } else {
                    location = theFileWeFound.getCanonicalPath().substring(0, theFileWeFound.getCanonicalPath().lastIndexOf(File.separator)) + File.separator + "received";
                }
                final File receivedDir = new File(location);
                if (!receivedDir.exists() && !receivedDir.mkdir()) {
                    throw new IOException("Received directory does not exist and could not be created at " + location);
                }
                final File theFile = new File(location + File.separator + theFileWeFound.getName());
                
                FileUtil.move(theFileWeFound.getCanonicalPath(), theFile.getCanonicalPath());
                getLogger().log(Level.INFO, "[FILE FOUND] File Rename successful, file should be in Received." + theFile);
                
                fileMoved(theFile);                
            } catch (IOException e) {            
            	logAndEmail("There was a problem with reading or writing the file for the job: " + theFileWeFound.getName(), e);
            }

        }
    }  

    public void setDirsToPoll(String dirsToPoll) {
        if (dirsToPoll == null) {
            dirsToPoll = "";
        }
        final StringTokenizer directoryNameTokenizer = new StringTokenizer(dirsToPoll, ",");
        int i = 0;
        if (theDirs == null || theDirs.length != directoryNameTokenizer.countTokens()) {
            theDirs = new File[directoryNameTokenizer.countTokens()];
        }
        while (directoryNameTokenizer.hasMoreTokens()) {
            theDirs[i] = new File(directoryNameTokenizer.nextToken());
            i++;
        }
    }

    public File[] getTheDirs() {
        return theDirs;
    }

    public void setTheDirs(final File[] theDirs) {
        this.theDirs = theDirs;
    }

    public MailErrorHelper getMailErrorHelper() {
        return mailErrorHelper;
    }

    public void setMailErrorHelper(final MailErrorHelper mailErrorHelper) {
        this.mailErrorHelper = mailErrorHelper;
    }

    public void setJobScheduler(final JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    public void setBulkReceivedWorkingDirectory(final String bulkReceivedWorkingDirectory) {
        this.bulkReceivedWorkingDirectory = bulkReceivedWorkingDirectory;
    }
}
