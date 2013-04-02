/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ArchiveBase;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ArchiveLoader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ArchiveNameValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MD5Validator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.ArchiveLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.FileCopier;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentDAO;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.service.JobScheduler;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalLoaderException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.quartz.SchedulerException;
import org.springframework.dao.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * The main module for the QC Live process.  It calls all the other processes that are part of it.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: chenjw $
 * @version $Rev: 3441 $
 */
public class Live implements LiveI {

    private static Live instance;
    private static final int MINUTES_PER_HOUR = 60;
    public static final String QUARTZ_JOB_GROUP = "QCLive";
    private JobScheduler liveScheduler;
    private String serverUrl;

    protected Live() {
    }

    public static Live getInstance() {
        if (instance == null) {
            instance = new Live();
        }
        return instance;
    }

    private int waitHours; // hours to wait before another check when experiment is pending
    private int initialWaitMinutes;  // minutes to wait between upload and check for CGCC archives   
    private int clinicalLoaderWaitMinutes; // minutes to wait between processing done and clinical loader   
    private int md5RetryAttempts; // Number of allowed retry attempts for md5 check
    private int md5ValidationRetryPeriod; // wait time in milliseconds between retry attempts for md5 check
    private Processor<File, Archive> uploadChecker;
    private Map<String, Processor<String, Experiment>> experimentCheckers;
    private Map<String, Processor<Experiment, Boolean>> experimentValidators;
    private Map<String, Processor<Experiment, List<Archive>>> experimentDeployers;
    private Processor<List<Archive>, Boolean> autoloaderCaller;
    private ArchiveLoader clinicalLoaderCaller;
    private CenterQueries centerQueries;
    private PlatformQueries platformQueries;
    private ArchiveQueries commonArchiveQueries;
    private ArchiveQueries diseaseArchiveQueries;
    private ExperimentDAO experimentDAO;
    private MailSender mailSender;
    private MailErrorHelper errorMailSender;
    private String emailBcc;  // bcc address for success emails only
    private Logger logger;
    private boolean autoloaderEnabled;
    private String archiveOfflineRootPath;
    private String failedArchiveRootPath;
    private String validClinicalPlatforms;
    private ArchiveLogger archiveLogger;

    /**
     * This should be called by whatever process monitors for uploads.
     *
     * @param filename              the name (full path!) of the new upload
     * @param md5ValidationAttempts md5 Validation attempts already made
     */
    public void processUpload(final String filename, Integer md5ValidationAttempts, QcLiveStateBean stateContex) {
        boolean uploadSuccessful = false;
        logger.log(Level.INFO, "ProcessUpload called for " + filename);
        final QcContext context = setupContext(filename, stateContex);
        Archive newArchive = null;
        boolean failValidation = false;

        try {
            // check the upload
            newArchive = uploadChecker.execute(makeFile(filename), context);
            context.setArchive(newArchive);
            context.setCenterName(newArchive.getDomainName());
            context.setPlatformName(newArchive.getPlatform());

            // if ok, schedule a check for the archive's experiment for right now
            if (Archive.STATUS_UPLOADED.equals(newArchive.getDeployStatus())) {
                uploadSuccessful = true;
                final String groupName = (Experiment.TYPE_BCR.equals(newArchive.getExperimentType()) ? filename : newArchive.getExperimentName());
                scheduleCheck(newArchive.getExperimentName(),
                        newArchive.getExperimentType(),
                        filename,
                        initialWaitMinutes,
                        context.getStateContext(),
                        groupName);
                logger.log(Level.INFO, "Scheduled experiment check for " + newArchive.getExperimentName());
            }
        } catch (Processor.ProcessorException e) {
            uploadSuccessful = false;
            // context checks to make sure the error hasn't already been added before, so will not be a duplicate
            context.addError(MessageFormat.format(MessagePropertyType.GENERAL_VALIDATION_MESSAGE, e.getMessage()));
            logger.log(e);
        } catch (Exception e) {
            context.addError(MessageFormat.format(MessagePropertyType.GENERAL_VALIDATION_MESSAGE, e.toString()));
            logger.log(e);
            errorMailSender.send(new StringBuilder().append("Unexpected error during processing of upload ").append(filename).toString(), e.toString());
            uploadSuccessful = false;
        }

        if (!uploadSuccessful) {
            String md5Status = context.getMd5ValidationStatus();
            if (md5ValidationAttempts == null) {
                md5ValidationAttempts = 1;
            }
            if ((md5Status != null) && (md5Status.equals(MD5Validator.STATUS_PENDING))) {
                if (md5ValidationAttempts < getMd5RetryAttempts()) {
                    //add another quartz job for retrying md5 check
                    final Calendar whenToRun = calculateWhenJobShouldRun();
                    try {
                        logger.log(Level.INFO, "Retrying MD5 Check again.");
                        liveScheduler.scheduleUploadCheck(filename, whenToRun, md5ValidationAttempts + 1, context.getStateContext());
                    } catch (SchedulerException e) {
                        // not a critical error, so do not fail archive processing as per Jess
                        logger.log(Level.WARN, "Could not schedule job for retrying md5 Validation");
                        logger.log(e);
                    }
                } else {
                    // already done md5 check for maximum allowed times
                    failValidation = true;
                }
            } else {
                failValidation = true;
            }
            if (failValidation) {
                failMd5Validation(filename, context, newArchive);
            }
        }
    }

    private void failMd5Validation(
            final String filename, final QcContext context, Archive newArchive) {
        newArchive = (newArchive == null) ? new Archive() : newArchive;
        newArchive.setDepositLocation(filename);
        scheduleCleanup(newArchive, true);
        fail(filename, context);
    }

    protected Calendar calculateWhenJobShouldRun() {
        final Calendar whenToRun = Calendar.getInstance();
        whenToRun.setTimeInMillis(whenToRun.getTimeInMillis() + getMd5ValidationRetryPeriod());
        return whenToRun;
    }

    private String findCenterType(final String platformName) {
        Platform platform = platformQueries.getPlatformForName(platformName);
        return platform == null ? null : platform.getCenterType();
    }

    private QcContext setupContext(final String archiveOrExperimentName, QcLiveStateBean stateContext) {
        final QcContext context = makeContext();
        context.setStartTime(new Date(System.currentTimeMillis()));
        // parse the name to get out the center name in case of failures, so we can email them
        Matcher nameMatcher = ArchiveNameValidator.EXPERIMENT_NAME_PATTERN.matcher(archiveOrExperimentName);
        if (nameMatcher.find()) { // filename is expected to have extensions such as .tar.gz
            context.setCenterName(nameMatcher.group(ArchiveNameValidator.INDEX_IN_ARCHIVE_NAME_DOMAIN));
            context.setPlatformName(nameMatcher.group(ArchiveNameValidator.INDEX_IN_ARCHIVE_NAME_PLATFORM));
            DiseaseContextHolder.setDisease(nameMatcher.group(ArchiveNameValidator.INDEX_IN_ARCHIVE_NAME_TUMOR_TYPE));
            final Platform platform = platformQueries.getPlatformForName(context.getPlatformName());
            context.setCenterConvertedToUUID(centerQueries.isCenterConvertedToUUID(context.getCenterName(), platform.getCenterType()));
        }

        // state bean is used to pass information from uploadChecker to experiment validators and to the loaders
        if (stateContext == null) {
            stateContext = new QcLiveStateBean();
        }
        context.setStateContext(stateContext);
        return context;
    }

    protected QcContext makeContext() {
        return new QcContext();
    }

    protected File makeFile(final String path) {
        return new File(path);
    }

    @Override
    public void checkExperiment(final String experimentName, final String experimentType, final QcLiveStateBean stateContext) {
        checkExperiment(experimentName, experimentType, null, stateContext);
    }

    protected String getExtensionFromArchiveName(final String fileName) {
        String extension = "";
        if (fileName != null) {
            if (fileName.endsWith(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION)) {
                extension = ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
            } else if (fileName.endsWith(ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION)) {
                extension = ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION;
            }
        }
        return extension;
    }

    @Override
    public void checkExperiment(final String experimentName, final String experimentType, final String archiveFileName, QcLiveStateBean stateContext) {

        final QcContext context = setupContext(experimentName, stateContext);
        try {
            if (Experiment.TYPE_BCR.equals(experimentType)) {
                // Set the 1 archive that will be included in that experiment
                final String extension = getExtensionFromArchiveName(archiveFileName);
                final String archiveFileNameNoExtension = FileUtil.getFilenameWithoutExtension(archiveFileName, extension);
                context.setExperimentArchiveNameFilter(archiveFileNameNoExtension);
            }

            final Experiment experiment = experimentCheckers.get(experimentType).execute(experimentName, context);
            context.setExperiment(experiment);
            if (Experiment.STATUS_CHECKED.equals(experiment.getStatus())) {
                // experiment is complete, move to validation
                validateExperiment(experiment, context);
            } else if (Experiment.STATUS_PENDING.equals(experiment.getStatus())) {
                // pending means the checker is saying "give them more time"
                // send an email to the center with a status update
                StringBuilder message = new StringBuilder("Processing of archives for " + experimentName + " is on hold for the following reason(s):\n");
                for (final String error : context.getErrors()) {
                    message.append("- ");
                    message.append(error);
                    message.append("\n\n");
                }
                emailCenter(experiment.getCenterName(), context.getPlatformName(), experimentName + " processing is waiting for archives", message.toString());
                scheduleCheck(experimentName, experimentType, waitHours * MINUTES_PER_HOUR, context.getStateContext(), experimentName);
            } else {
                // either no more time left or there is an error that needs resubmit to fix
                // we cannot move forward with this set of submissions
                failExperiment(experiment, context);
            }
        } catch (Processor.ProcessorException e) {
            final Experiment experiment = context.getExperiment();
            if (experiment != null) {
                if (Experiment.STATUS_UP_TO_DATE.equals(experiment.getStatus())) {
                    // this isn't really a failure, just log it
                    logger.log(Level.INFO, "Experiment " + experiment.getName() + " has no new uploaded archives");
                } else {
                    failExperiment(experiment, context);
                }
            } else {
                fail(experimentName, context);
            }

        } catch (Throwable throwable) {
            logger.log(new Exception(throwable));
            errorMailSender.send(new StringBuilder().append("Unexpected error during checking of experiment ").append(experimentName).toString(), throwable.toString());
            if (context.getExperiment() != null) {
                failExperiment(context.getExperiment(), context);
            } else {
                fail(experimentName, context);
            }
        }
    }

    /**
     * Removes archives deployed in the distro dir
     *
     * @param experimentName
     * @param qcContext
     */
    protected void cleanupNewlyDeployedArchives(final String experimentName, final QcContext qcContext) {
        //get the archives used in this experiment
        final List<Archive> experimentArchives = qcContext.getArchivesToBeProcessedInTheExperiment();
        if (experimentArchives != null) {
            logger.log(Level.INFO, experimentName + " experiment deployment failed. The following archives are deleted from distro dir :");
            for (final Archive experimentArchive : experimentArchives) {
                final Archive archive = commonArchiveQueries.getArchive(experimentArchive.getArchiveName());
                // remove the archives that are not in available status from distro dir
                if (archive.getDeployStatus() != Archive.STATUS_AVAILABLE) {
                    // get deploy dir
                    final File deployDir = experimentDAO.getDeployDirectoryPath(experimentArchive);
                    logger.log(Level.INFO, " Archive :" + deployDir);
                    // remove deployed dir if exists
                    FileUtil.deleteDir(deployDir);
                    // remove tar file if exists
                    final File deployedTarFile = new File(deployDir + FileUtil.TAR);
                    deployedTarFile.delete();
                    // remove tar.gz file if exists
                    final File deployedTarGzFile = new File(deployDir + FileUtil.TAR_GZ);
                    deployedTarGzFile.delete();
                    // remove md5 file if exists
                    final File deployedTarGzMd5File = new File(deployDir + FileUtil.TAR_GZ + FileUtil.MD5);
                    deployedTarGzMd5File.delete();
                }
            }
        }
    }

    private void scheduleCheck(final String experimentName,
                               final String experimentType,
                               final int waitMinutes,
                               final QcLiveStateBean stateContext,
                               final String groupName) throws SchedulerException {
        scheduleCheck(experimentName, experimentType, null, waitMinutes, stateContext, groupName);
    }

    private void scheduleCheck(final String experimentName,
                               final String experimentType,
                               final String archive,
                               final int waitMinutes,
                               final QcLiveStateBean stateContext,
                               final String groupName) throws SchedulerException {
        // calculate when job will be run
        Calendar whenToRun = Calendar.getInstance();
        whenToRun.add(Calendar.MINUTE, waitMinutes);
        liveScheduler.scheduleExperimentCheck(experimentName, experimentType, archive, whenToRun, stateContext, groupName);
    }

    private void validateExperiment(
            final Experiment experiment,
            QcContext context) throws Processor.ProcessorException {
        if (context == null) {
            context = setupContext(experiment.getName(), null);
        }
        if (experimentValidators.get(experiment.getType()).execute(experiment, context)) {
            // is valid, so deploy it!
            final List<Archive> deployedArchives = experimentDeployers.get(experiment.getType()).execute(experiment, context);
            if (experiment.getStatus().equals(Experiment.STATUS_DEPLOYED)) {
                processingDone(deployedArchives, experiment, context);
            } else {
                // if deployment fails, want to fail all parts of experiment
                failExperiment(experiment, context);
            }
        } else {
            failExperiment(experiment, context);
        }
    }

    public int getWaitHours() {
        return waitHours;
    }

    public String getValidClinicalPlatforms() {
        return validClinicalPlatforms;
    }

    public void setValidClinicalPlatforms(String validClinicalPlatforms) {
        this.validClinicalPlatforms = validClinicalPlatforms;
    }

    public void setWaitHours(final int waitHours) {
        this.waitHours = waitHours;
    }

    public void setMailSender(final MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setCenterQueries(final CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    public void setPlatformQueries(final PlatformQueries platformQueries) {
        this.platformQueries = platformQueries;
    }

    public void setEmailBcc(final String emailBcc) {
        this.emailBcc = emailBcc;
    }

    public String getEmailBcc() {
        return emailBcc;
    }

    public void setErrorMailSender(final MailErrorHelper errorMailSender) {
        this.errorMailSender = errorMailSender;
    }

    protected void failExperiment(final Experiment experiment, final QcContext context) {

        cleanupNewlyDeployedArchives(experiment.getName(), context);

        StringBuilder archiveList = new StringBuilder("The following archives were part of this submission:\n");
        // update the status of the experiment's archives
        for (final Archive archive : experiment.getArchives()) {
            // first, set all archives in this group to Invalid,
            // except those that are already marked as Available
            if (archive.getDeployStatus().equals(Archive.STATUS_UPLOADED) ||
                    archive.getDeployStatus().equals(Archive.STATUS_VALIDATED)) {
                archive.setDeployStatus(Archive.STATUS_INVALID);
            }
            // save status of all archives in case any changed without saving yet
            if (commonArchiveQueries != null) {
                commonArchiveQueries.updateArchiveStatus(archive);
                diseaseArchiveQueries.updateArchiveStatus(archive);
            }
            // now all unavailable archives are added to list of failed and also scheduled for cleanup
            if (!archive.getDeployStatus().equals(Archive.STATUS_AVAILABLE)) {
                archiveList.append("\t- ").append(archive.getRealName()).append("\n");
                archiveLogger.endTransaction(archive.getArchiveName(), false);
                // schedule job to cleanup failed archive
                scheduleCleanup(archive, true);
            }


        }
        fail(experiment.getName(), context, archiveList.toString());
    }

    private synchronized void scheduleCleanup(final Archive archive, final boolean isFailed) {
        try {
            liveScheduler.scheduleArchiveCleanup(archive, isFailed);
        } catch (SchedulerException e) {
            logger.log(Level.WARN, "Could not schedule job for cleaning up failed archive");
            logger.log(e);
        }
    }

    protected void fail(final String name, final QcContext context) {
        fail(name, context, null);
    }

    protected void fail(final String name, final QcContext context, final String detail) {
        // build the error string
        String archiveName = name;
        if (context.getArchive() != null && StringUtils.isNotEmpty(context.getArchive().getArchiveName())) {
            archiveName = context.getArchive().getArchiveName();
        } else {
            // for md5 validator the archive does not exist yet, so if there is a failure, get archive name from the file name
            archiveName = (new File(name)).getName();
            // take out .tar.gz at the end
            if (archiveName.endsWith(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION)) {
                archiveName = archiveName.substring(0, archiveName.length() - ConstantValues.COMPRESSED_ARCHIVE_EXTENSION.length());
            } else if (archiveName.endsWith(ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION)) {
                archiveName = archiveName.substring(0, archiveName.length() - ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION.length());
            }
        }


        final StringBuilder error = new StringBuilder("Processing failed for ").append(archiveName).append(".");
        error.append("\n\nPlease address any errors shown below before resubmitting the archive(s).  ").
                append("If you have any questions, please contact the DCC team at TCGA-DCC-BINF-L@LIST.NIH.GOV.");
        if (context.getErrorCount() > 0) {
            error.append("\n\nError(s) recorded:\n");
            for (final String errorStr : context.getErrors()) {
                error.append("- ");
                error.append(errorStr);
                error.append("\n\n");
            }
        } else {
            error.append("\n\nNo specific errors were recorded.  Please contact the DCC for assistance.");
        }
        if (context.getWarningCount() > 0) {
            error.append("\n\nWarning(s) recorded:\n");
            for (final String warning : context.getWarnings()) {
                error.append("- ");
                error.append(warning);
                error.append("\n\n");
            }
        }
        if (detail != null) {
            error.append(detail);
        }
        emailCenter(context.getCenterName(), context.getPlatformName(), new StringBuilder().append("Processing failed for ").append(archiveName).toString(), error.toString());
        logger.log(Level.WARN, new StringBuilder()
                .append(context.getCenterName())
                .append(" ")
                .append(context.getPlatformName())
                .append(" ")
                .append(name)
                .append(": ")
                .append(error.toString()).toString());

        archiveLogger.addErrorMessage(context.getStateContext().getTransactionId(), archiveName, error.toString());
        archiveLogger.endTransaction(context.getStateContext().getTransactionId(), false);
    }

    protected void emailCenter(
            final String centerName, final String platformName, final String subject, final String body) {
        Center center = null;
        try {
            if (centerName != null && platformName != null) {
                String centerType = findCenterType(platformName);
                final Integer id = centerQueries.findCenterId(centerName, centerType);
                center = centerQueries.getCenterById(id);
            }
        } catch (DataAccessException e) {
            // that means not found, ignore
        }
        if (center != null) {
            if (mailSender.isMailEnabled()) {
                mailSender.send(center.getCommaSeparatedEmailList(), null, subject, body, false);
            } else {
                logger.log(Level.DEBUG, "-- EMAIL --");
                logger.log(Level.DEBUG, "To: " + center.getCommaSeparatedEmailList());
                logger.log(Level.DEBUG, "Subject: " + subject);
                logger.log(Level.DEBUG, "Message: ");
                logger.log(Level.DEBUG, body);
            }
        } else {
            errorMailSender.send("Unable to get email for center " + centerName + " and platform " + platformName + ".  Message to be sent was:\n\n" + body, "");
        }
    }

    protected void processingDone(
            final List<Archive> deployedArchives, final Experiment experiment,
            final QcContext context) {
        if (deployedArchives.size() < 1) {
            return;  // not sure this will ever happen?
        }
        final boolean isClinical = isClinical(deployedArchives);

        // create an email for the submitting center, telling them processing is complete
        final StringBuilder centerMailBody = new StringBuilder().append("Processing of your submission for '").
                append(experiment.getName()).append("' has completed successfully.  The following archive(s) were deployed:\n\n");

        for (final Archive archive : deployedArchives) {
            scheduleCleanup(archive, false);
            archive.setDeployStatus(Archive.STATUS_AVAILABLE);
            commonArchiveQueries.updateArchiveStatus(archive);
            commonArchiveQueries.setToLatest(archive);
            diseaseArchiveQueries.updateArchiveStatus(archive);
            diseaseArchiveQueries.setToLatest(archive);

            // append name to center email body
            centerMailBody.append("\t").append(archive.getArchiveName()).append("\n");
            // and send out a "new archive available" email for each deployed archive to everyone
            final String subject = "New Archive Available - " + archive.getArchiveName();
            long archiveSize = commonArchiveQueries.getArchiveSize(archive.getId()) * 1024;
            final String body = new StringBuilder().append("A new archive is available.\n\n").
                    append("Archive Name\t").
                    append(archive.getArchiveName()).append("\n\n").
                    append("Archive Size\t").
                    append(FileUtil.getFormattedFileSize(archiveSize)).append("\n\n").
                    append("Browse Contents\t").append(getServerUrl()).append("/tcga/showFiles.htm?archiveId=").
                    append(archive.getId()).append("\n\n").
                    append("Download\t").append(getServerUrl()).
                    append(archive.getDeployLocation().replace('\\', '/')).
                    append("\n\n").
                    append("#This is a tab delimited message").toString();
            if (mailSender.isMailEnabled()) {
                mailSender.send(archive.getTheCenter().getCommaSeparatedEmailList(), emailBcc, subject, body, false);
            } else {
                logger.log(Level.DEBUG, "-- EMAIL --");
                logger.log(Level.DEBUG, "To: " + archive.getTheCenter().getCommaSeparatedEmailList());
                logger.log(Level.DEBUG, "Subject: " + subject);
                logger.log(Level.DEBUG, "Message: ");
                logger.log(Level.DEBUG, body);
            }

            // last step update archiveLog to indicate the archive has completed successfuly
            archiveLogger.endTransaction(archive.getArchiveName(), true);

        }
        // if there were any warnings, append them to the message that goes to the submitting center
        if (context.getWarningCount() > 0) {
            centerMailBody.append("\n\nWarning(s) recorded:\n");
            for (final String warning : context.getWarnings()) {
                centerMailBody.append("- ");
                centerMailBody.append(warning);
                centerMailBody.append("\n\n");
            }
        }
        emailCenter(context.getCenterName(), context.getPlatformName(),
                new StringBuilder().append("Processing of ").append(experiment.getName()).append(" was successful").toString(),
                centerMailBody.toString());

        if (isClinical) {
            Calendar whenToRun = Calendar.getInstance();
            whenToRun.add(Calendar.MINUTE, clinicalLoaderWaitMinutes);
            // for clinical archives update the archive status after loading the data
            for (final Archive archive : deployedArchives) {
                scheduleCleanup(archive, false);
                archive.setDeployedWarningMessages(context.getWarnings());
            }

            // clinical loader scheduling
            PrintWriter printWriter = null;
            try {
                liveScheduler.scheduleClinicalLoader(deployedArchives, whenToRun, experiment.getName(), context.getStateContext());

            } catch (SchedulerException e) {
                logger.log(Level.ERROR, "Unable to schedule clinical data loading job " + e.getMessage());
                // email the full stack trace
                final Writer result = new StringWriter();
                //noinspection IOResourceOpenedButNotSafelyClosed
                printWriter = new PrintWriter(result);
                e.printStackTrace(printWriter);
                errorMailSender.send("Clinical Loader Error while scheduling clinical loader", result.toString());
                // last step update archiveLog to indicate the archive has completed unsuccessfuly
                archiveLogger.endTransaction(context.getStateContext().getTransactionId(), false);
            } finally {
                IOUtils.closeQuietly(printWriter);
            }
        }
    }

    public boolean isClinical(List<Archive> deployedArchives) {
        boolean isClinical = false;
        if (deployedArchives != null &&
                deployedArchives.size() > 0) {

            // find the valid platforms for clinical archives
            String[] validPlatforms = validClinicalPlatforms.split(",");
            List validList = Arrays.asList(validPlatforms);
            // check if there is at least one clinical archive
            for (Archive archive : deployedArchives) {
                if (validList.contains(archive.getPlatform().toLowerCase())) {
                    isClinical = true;
                    break;
                }
            }
        }
        return isClinical;
    }

    /**
     * Loads deployed clinical archives to the db
     * This method is invoked by the scheduler
     *
     * @param deployedArchives
     */
    public void loadClinicalData(List<Archive> deployedArchives, QcLiveStateBean stateContext) throws ClinicalLoaderException {
        clinicalLoaderCaller.load(deployedArchives, stateContext);
    }


    /**
     * Called by cleanup job which is scheduled by Quartz via the JobScheduler.  The expanded directory for the archive
     * will be deleted.  If the archive failed, it will be moved into a "failed" directory.
     *
     * @param archiveBase archive to clean up
     * @param isFailed    if the processing for the archive failed
     */
    public void cleanupArchive(ArchiveBase archiveBase, boolean isFailed) {

        if (archiveBase != null &&
                archiveBase.getDepositLocation() != null) {
            final String archiveFile = archiveBase.getDepositLocation();
            File archiveToMove = new File(archiveFile);
            File archiveMd5ToMove = new File(archiveFile + FileUtil.MD5);

            if (archiveToMove.exists()) {
                String archiveDestRootPath = (isFailed) ? getFailedArchiveRootPath() : getArchiveOfflineRootPath();
                try {
                    File archiveDestRootDir = makeDestinationDirs(archiveDestRootPath, archiveBase);
                    // move file to offline/failed location
                    FileCopier.move(archiveToMove, archiveDestRootDir);
                    // move md5file to offline/failed location
                    FileCopier.move(archiveMd5ToMove, archiveDestRootDir);
                    logger.log(Level.DEBUG, "Moved Archives " + archiveToMove + "," + archiveMd5ToMove + " to: "
                            + archiveDestRootPath);
                } catch (IOException ie) {
                    logger.log(Level.WARN, "Failed to move archive  " + archiveToMove + "\nError: " + ie.toString());
                }

                // now deleted expanded directory, if it is there, for all archives
                if (archiveFile.endsWith(FileUtil.TAR_GZ) || archiveFile.endsWith(FileUtil.TAR)) {
                    final int index = (archiveFile.indexOf(FileUtil.TAR_GZ) != -1) ?
                            archiveFile.indexOf(FileUtil.TAR_GZ) : archiveFile.indexOf(FileUtil.TAR);
                    final String expandedArchiveDirname = archiveFile.substring(0, index);
                    final File expandedArchiveDir = new File(expandedArchiveDirname);
                    if (expandedArchiveDir.exists()) {
                        if (!FileUtil.deleteDir(expandedArchiveDir)) {
                            StringBuilder errorMessage = new StringBuilder("Failed to delete expanded directory "
                                    + expandedArchiveDirname);
                            logger.log(Level.ERROR, errorMessage.toString());
                            errorMailSender.send(errorMessage.toString(), "");
                        }
                    }
                }
            } else {
                logger.log(Level.WARN, "Archive cleanup could not complete, because archive file '"
                        + archiveFile + "' not found");
            }
        }
    }

    /**
     * Creates offline dir. The file path should be rootdir/centername/centertype/. If centername or centertype is not
     * defined, create unknown dir
     *
     * @param rootDir
     * @param archiveBase
     * @return
     * @throws IOException
     */

    private File makeDestinationDirs(final String rootDir, final ArchiveBase archiveBase) throws IOException {
        StringBuilder archiveDestPath = new StringBuilder();

        archiveDestPath.append(new File(rootDir).getCanonicalPath()); // offline root path
        archiveDestPath.append(File.separator);
        if (archiveBase.getDomainName() != null &&
                archiveBase.getDomainName().length() > 0) {
            archiveDestPath.append(archiveBase.getDomainName());//  add center name
            archiveDestPath.append(File.separator);
            if (archiveBase.getExperimentType() != null &&
                    archiveBase.getExperimentType().length() > 0) {
                archiveDestPath.append(archiveBase.getExperimentType()); //  add center type
            } else {
                archiveDestPath.append(ConstantValues.DIR_UNKNOWN);
            }

        } else {
            archiveDestPath.append(ConstantValues.DIR_UNKNOWN);
        }
        archiveDestPath.append(File.separator);

        File archiveDestDir = new File(archiveDestPath.toString());
        if (!archiveDestDir.exists()) {
            if (!archiveDestDir.mkdirs()) {
                throw new IOException("Failed to create offline dir " + archiveDestPath.toString());
            }
        }

        return archiveDestDir;

    }

    public void setClinicalLoaderCaller(ArchiveLoader clinicalLoaderCaller) {
        this.clinicalLoaderCaller = clinicalLoaderCaller;
    }

    public void setUploadChecker(final Processor<File, Archive> uploadChecker) {
        this.uploadChecker = uploadChecker;
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    public void setCommonArchiveQueries(final ArchiveQueries commonArchiveQueries) {
        this.commonArchiveQueries = commonArchiveQueries;
    }

    public void setDiseaseArchiveQueries(final ArchiveQueries diseaseArchiveQueries) {
        this.diseaseArchiveQueries = diseaseArchiveQueries;
    }

    public void setAutoloaderCaller(final Processor<List<Archive>, Boolean> autoloaderCaller) {
        this.autoloaderCaller = autoloaderCaller;
    }

    public void setAutoloaderEnabled(final boolean autoloaderEnabled) {
        this.autoloaderEnabled = autoloaderEnabled;
    }

    /**
     * Sets the time to wait between a successful upload of an archive and the call to checkExperiment.  Wait is needed
     * because often deposit several at once, and this limits the number of "waiting for archives" emails that get sent
     * out for archives that are already deposited.
     *
     * @param initialWaitMinutes number of minutes to wait
     */
    public void setInitialWaitMinutes(final int initialWaitMinutes) {
        this.initialWaitMinutes = initialWaitMinutes;
    }

    public void setClinicalLoaderWaitMinutes(final int clinicalLoaderWaitMinutes) {
        this.clinicalLoaderWaitMinutes = clinicalLoaderWaitMinutes;
    }

    public void setExperimentCheckers(final Map<String, Processor<String, Experiment>> experimentCheckers) {
        this.experimentCheckers = experimentCheckers;
    }


    public void setExperimentValidators(final Map<String, Processor<Experiment, Boolean>> experimentValidators) {
        this.experimentValidators = experimentValidators;
    }

    public void setExperimentDeployers(final Map<String, Processor<Experiment, List<Archive>>> experimentDeployers) {
        this.experimentDeployers = experimentDeployers;
    }

    public void setJobScheduler(final JobScheduler scheduler) {
        this.liveScheduler = scheduler;
    }

    public JobScheduler getEnqueuer() {
        return liveScheduler;
    }

    public String getArchiveOfflineRootPath() {
        return archiveOfflineRootPath;
    }

    public void setArchiveOfflineRootPath(final String archiveOfflineRootPath) {
        this.archiveOfflineRootPath = archiveOfflineRootPath;
    }

    public String getFailedArchiveRootPath() {
        return failedArchiveRootPath;
    }

    public void setFailedArchiveRootPath(final String failedArchiveRootPath) {
        this.failedArchiveRootPath = failedArchiveRootPath;
    }

    public int getMd5ValidationRetryPeriod() {
        return md5ValidationRetryPeriod;
    }

    public void setMd5ValidationRetryPeriod(int md5ValidationRetryPeriod) {
        this.md5ValidationRetryPeriod = md5ValidationRetryPeriod;
    }

    public int getMd5RetryAttempts() {
        return md5RetryAttempts;
    }

    public void setMd5RetryAttempts(int md5RetryAttempts) {
        this.md5RetryAttempts = md5RetryAttempts;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setArchiveLogger(ArchiveLogger archiveLogger) {
        this.archiveLogger = archiveLogger;
    }

    public ExperimentDAO getExperimentDAO() {
        return experimentDAO;
    }

    public void setExperimentDAO(ExperimentDAO experimentDAO) {
        this.experimentDAO = experimentDAO;
    }
}
