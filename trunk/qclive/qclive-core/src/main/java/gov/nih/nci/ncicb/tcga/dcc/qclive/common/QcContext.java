/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MD5Validator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Holds context about a specific run of QC checker.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class QcContext {
    public static final Integer MSGS_LIMIT = 250;
    public static final String WARNING_MSG = "Too many warnings. New warning messages will not be recorded";
    private static final String DEFAULT_NAME = "Default";
    private Experiment experiment;
    private Archive archive;
    private TabDelimitedContent sdrf;
    private File file; // current file being worked on, if any
    private final Map<String,Map<String,Set<String>>> errorsByProcessNameByArchiveName = new LinkedHashMap<String,Map<String,Set<String>>>();
    private final Map<String,Map<String,Set<String>>> warningsByProcessNameByArchiveName = new LinkedHashMap<String,Map<String,Set<String>>>();
    private Date startTime;
    private Date stopTime;
    private String centerName;
    private boolean noRemote;
    private Logger logger;
    private Map<String, String[]> alteredFiles = new HashMap<String, String[]>();
    private String md5ValidationStatus;
    private String platformName;
    private List<Exception> exceptionsToLog = new ArrayList<Exception>();
    private Map<String,  Map<String,File>> splitDataMatrixFilesByOriginalDataMatrixFileByBarcode;
    private Archive archiveInProgress;
    private String  currentProcessName;
    private Boolean isStandaloneValidator = false;
    // used to carry stateful information across QcContext instantiations
    private QcLiveStateBean stateContext;	   
    private List<Archive> archivesToBeProcessedInTheExperiment;
    private Boolean isCenterConvertedToUUID;

	/**
     * Filter to be used to exclude archives that are not matching the filter out of the experiment
     */
    private String experimentArchiveNameFilter;

    // keeps track of files moved forward
    private final List<String> filesCopiedFromPreviousArchive = new ArrayList<String>();
    private boolean experimentRequiresMageTab = false;

    public String toString() {
        if (experiment != null) {
            return "Context for " + experiment.getName();
        } else if (archive != null) {
            return "Context for " + archive.getRealName();
        } else {
            return "Context";
        }
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(final Experiment experiment) {
        this.experiment = experiment;
    }

    public Archive getArchive() {
        return archive;
    }

    public String getCenterEmail() {
        if (archive != null && archive.getTheCenter() != null) {
            return archive.getTheCenter().getCommaSeparatedEmailList();
        } else if (experiment != null) {
            for (final Archive archive : experiment.getArchives()) {
                if (archive.getTheCenter() != null) {
                    return archive.getTheCenter().getCommaSeparatedEmailList();
                }
            }
        }
        return null;
    }

    public void setArchive(final Archive archive) {
        this.archive = archive;
    }

    /**
     * Stores errors based on the archive name and process name
     * @param errMsg
     */

    public void addError(final String errMsg) {
        StringBuilder message = new StringBuilder();
        if (errMsg != null) {
            message.append(errMsg);
            if (archive != null && archive.getRealName() != null) {
                message.append("\t[archive ").append(archive.getRealName()).append("]");
            }
        } else {
            message.append("Unknown error");
        }
        if(getErrorCount() > MSGS_LIMIT){
            final String errMessage = "Too many errors. Stopped processing the archive "+getArchiveInProgress();
            addMessage(errorsByProcessNameByArchiveName, errMessage);
            throw new RuntimeException(errMessage);
        }
        addMessage(errorsByProcessNameByArchiveName, message.toString());
    }

    public void addErrors(final List<String> errors) {
        for (final String error : errors) {
            addError(error);
        }
    }

    /**
     * Add warnings based on archive name and process name
     * @param warnMsg
     */

    public void addWarning(final String warnMsg) {
        StringBuilder message = new StringBuilder(warnMsg);
        if (archive != null && archive.getRealName() != null) {
            message.append("\t[").append(archive.getRealName()).append("]");
        } else if (experiment != null) {
            message.append("\t[").append(experiment.getName()).append("]");
        }
        if(getWarningCount() > MSGS_LIMIT){
            if(!getWarnings().contains(WARNING_MSG)){
                addMessage(warningsByProcessNameByArchiveName, WARNING_MSG);
            }
        }else{
            addMessage(warningsByProcessNameByArchiveName, message.toString());
        }

    }

    /**
     * @return the number of errors recorded during the last execute
     */
    public int getErrorCount() {
        return getMessageCount(errorsByProcessNameByArchiveName);
    }

    /**
     * @return the number of warnings recorded during the last execute
     */
    public int getWarningCount() {
        return getMessageCount(warningsByProcessNameByArchiveName);
    }

    /**
     * @return a list of error messages generated during the last execute
     */
    public List<String> getErrors() {
        return convertMapToList(errorsByProcessNameByArchiveName) ;
    }

    public List<String> getErrorsByArchiveName(final Archive archive) {
        return getMessagesByArchiveName(errorsByProcessNameByArchiveName,archive);
    }

    public List<String> getWarningsByArchiveName(final Archive archive) {
        return getMessagesByArchiveName(warningsByProcessNameByArchiveName,archive);
    }

    private List<String> getMessagesByArchiveName(final Map<String,Map<String,Set<String>>> msgsByProcessNameByArchiveName,
                                                  final Archive archive){
        final List<String> messageList = new ArrayList<String>();
        if(archive != null){
            final Map<String,Set<String>> messagesByProcessName = msgsByProcessNameByArchiveName.get(archive.toString());
            if(messagesByProcessName != null){
                for (final Set messages: messagesByProcessName.values()){
                    messageList.addAll(messages);
                }
            }
        }
        return messageList;

    }

    public List<String> getErrorsByProcessName(final Archive archive, final String processName){
        return getMessagesByProcessName(errorsByProcessNameByArchiveName,archive,processName);
    }

    public List<String> getWarningsByProcessName(final Archive archive, final String processName){
        return getMessagesByProcessName(warningsByProcessNameByArchiveName,archive,processName);
    }


    private List<String> getMessagesByProcessName(final Map<String,Map<String,Set<String>>> msgsByProcessNameByArchiveName,
                                                  final Archive archive,
                                                  final String processName){

       final List<String> messageList = new ArrayList<String>();
        if(archive != null){
            final Map<String,Set<String>> messagesByProcessName = msgsByProcessNameByArchiveName.get(archive.toString());
            if(messagesByProcessName != null){
                final Set<String> messages = messagesByProcessName.get(processName);
                if(messages != null){
                    messageList.addAll(messages);
                }
            }
        }
        return messageList;
    }
    /**
     * @return a List of warning messages generated during the last execute
     */
    public List<String> getWarnings() {
        return convertMapToList(warningsByProcessNameByArchiveName) ;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(final Date stopTime) {
        this.stopTime = stopTime;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setCenterName(final String centerName) {
        this.centerName = centerName;
    }

    public String getCenterName() {
        return centerName;
    }

    public TabDelimitedContent getSdrf() {
        return sdrf;
    }

    public void setSdrf(final TabDelimitedContent sdrf) {
        this.sdrf = sdrf;
    }

    public boolean isNoRemote() {
        return noRemote;
    }

    public void setNoRemote(final boolean noRemote) {
        this.noRemote = noRemote;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    public Map<String, String[]> getAlteredFiles() {
        return alteredFiles;
    }

    public void aboutToChangeFile(final File file, final String reasonForChange) throws IOException, NoSuchAlgorithmException {
        // get the MD5 of the file and add it to the map
        final String fileChecksum = MD5Validator.getFileMD5(file);
        alteredFiles.put(file.getName(), new String[]{fileChecksum, reasonForChange});
    }

    public String getMd5ValidationStatus() {
        return md5ValidationStatus;
    }

    public void setMd5ValidationStatus(final String md5ValidationStatus) {
        this.md5ValidationStatus = md5ValidationStatus;
    }

    public void setPlatformName(final String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformName() {
        return platformName;
    }

    /**
     * Add this exception to the list of ones to log
     *
     * @param e the exception to log
     */
    public void addExceptionToLog(final Exception e) {
        exceptionsToLog.add(e);
    }

    public List<Exception> getExceptionsToLog() {
        return exceptionsToLog;
    }
      
    public String getExperimentArchiveNameFilter() {
        return experimentArchiveNameFilter;
    }

    public void setExperimentArchiveNameFilter(final String experimentArchiveNameFilter) {
        this.experimentArchiveNameFilter = experimentArchiveNameFilter;
    }
        
    public List<String> getFilesCopiedFromPreviousArchive() {
        return filesCopiedFromPreviousArchive;
    }

    public Map<String, Map<String,File>> getSplitDataMatrixFiles() {
        return splitDataMatrixFilesByOriginalDataMatrixFileByBarcode;
    }

            
    public QcLiveStateBean getStateContext() {
		return stateContext;
	}

	public void setStateContext(QcLiveStateBean stateContext) {
		this.stateContext = stateContext;
	}
    
    public void addSplitDataMatrixFiles(final Map<String, File> dataMatrixFiles, final String originalDataMatrixFile ) {
        if (splitDataMatrixFilesByOriginalDataMatrixFileByBarcode == null) {
            splitDataMatrixFilesByOriginalDataMatrixFileByBarcode = new HashMap<String,Map<String,File>>();
        }
        for(final String aliquotBarcode: dataMatrixFiles.keySet()){
            Map<String,File> splitDataMatrixFilesByOriginalDataMatrixFile = splitDataMatrixFilesByOriginalDataMatrixFileByBarcode.get(aliquotBarcode);
            if(splitDataMatrixFilesByOriginalDataMatrixFile == null){
                splitDataMatrixFilesByOriginalDataMatrixFile = new HashMap<String,File>();
                splitDataMatrixFilesByOriginalDataMatrixFileByBarcode.put(aliquotBarcode, splitDataMatrixFilesByOriginalDataMatrixFile);
            }
            splitDataMatrixFilesByOriginalDataMatrixFile.put(originalDataMatrixFile,dataMatrixFiles.get(aliquotBarcode));
        }
    }

    public String getArchiveInProgressName(){
        String inProgressArchiveName = DEFAULT_NAME;

        try{
            inProgressArchiveName = getArchiveInProgress().toString();
        }catch(Exception e){

        }
        return inProgressArchiveName;
    }

    public Archive getArchiveInProgress() {
        if( archiveInProgress == null){
            if(archive == null){
                return new Archive();
            }
            return archive;
        }
        return archiveInProgress;
    }

    public void setArchiveInProgress(Archive archiveInProgress) {
        this.archiveInProgress = archiveInProgress;
    }

    public void setItemInProgress(Object itemInProgress){
        if(itemInProgress != null && itemInProgress instanceof Archive){
            setArchiveInProgress((Archive)itemInProgress);
        }else{
            setArchiveInProgress(null);
        }
    }

    public Boolean isStandaloneValidator() {
        return isStandaloneValidator;
    }

    public void setStandaloneValidator(Boolean standaloneValidator) {
        isStandaloneValidator = standaloneValidator;
    }

    public String getCurrentProcessName() {
        return (((currentProcessName == null) || currentProcessName.isEmpty())?DEFAULT_NAME :currentProcessName);
    }

    public void setCurrentProcessName(String currentProcessName) {
        this.currentProcessName = currentProcessName;
    }

    private List<String> convertMapToList(final Map<String,Map<String,Set<String>>> msgByProcessNameByArchiveName){
        final List<String> msgList = new ArrayList<String>();
        for(final Map<String,Set<String>> dataByProcessName: msgByProcessNameByArchiveName.values()){
            for (final Set msgs: dataByProcessName.values()){
                msgList.addAll(msgs);
            }
        }
        return msgList;
    }

    private int getMessageCount(final Map<String,Map<String,Set<String>>> msgByProcessNameByArchiveName){
        int msgCount = 0;
        for(final Map<String,Set<String>> msgsByProcessName: msgByProcessNameByArchiveName.values()){
            for (final Set warnings: msgsByProcessName.values()){
                msgCount  += warnings.size();
            }
        }
        return msgCount;

    }

    private void addMessage(final Map<String,Map<String,Set<String>>> msgsByProcessNameByArchiveName, final String message){
        final String inProgressArchiveName = getArchiveInProgressName();
        Map<String,Set<String>> msgsByProcessName = msgsByProcessNameByArchiveName.get(inProgressArchiveName);

        if(msgsByProcessName == null){
            msgsByProcessName = new LinkedHashMap<String,Set<String>>();
            msgsByProcessNameByArchiveName.put(inProgressArchiveName, msgsByProcessName);
        }

        Set<String> messages = msgsByProcessName.get(getCurrentProcessName());
        if(messages == null){
            messages = new LinkedHashSet<String>();
            msgsByProcessName.put(getCurrentProcessName(), messages);
        }
        messages.add(message.toString());

    }

    /**
     * Gets the list of archives that are getting processed in the experiment
     * @return
     */
    public List<Archive> getArchivesToBeProcessedInTheExperiment() {
        return archivesToBeProcessedInTheExperiment;
    }

    /**
     * Sets the archives thast are getting processed in the experiment
     * @param archivesToBeProcessedInTheExperiment
     */
    public void setArchivesToBeProcessedInTheExperiment(List<Archive> archivesToBeProcessedInTheExperiment) {
        this.archivesToBeProcessedInTheExperiment = archivesToBeProcessedInTheExperiment;
    }

    public Boolean isCenterConvertedToUUID() {
        return (isCenterConvertedToUUID == null)?false:isCenterConvertedToUUID;
    }

    public void setCenterConvertedToUUID(Boolean centerConvertedToUUID) {
        isCenterConvertedToUUID = centerConvertedToUUID;
    }

    public void setExperimentRequiresMageTab(final boolean experimentRequiresMageTab) {
        this.experimentRequiresMageTab = experimentRequiresMageTab;
    }

    public boolean experimentRequiresMageTab() {
        return experimentRequiresMageTab;
    }
}
