/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLoggerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerI;
import org.apache.log4j.Level;

import java.io.File;
import java.util.Date;

/**
 * Executes file packaging JOB
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class FilePackagerJob implements QueueJob<FilePackagerBean> {

    private FilePackagerFactoryI filePackagerFactory;
    private FilePackagerFactoryI wsFilePackagerFactory;
    private FilePackagerI filePackager;
    private FilePackagerEnqueuerI filePackagerEnqueuer;
    private ProcessLoggerI logger;

    public FilePackagerFactoryI getFilePackagerFactory() {
        return filePackagerFactory;
    }

    public void setFilePackagerFactory(final FilePackagerFactoryI filePackagerFactory) {
        this.filePackagerFactory = filePackagerFactory;
    }

    public FilePackagerI getFilePackager() {
        return filePackager;
    }

    public void setFilePackager(final FilePackagerI filePackager) {
        this.filePackager = filePackager;
    }

    public FilePackagerEnqueuerI getFilePackagerEnqueuer() {
        return filePackagerEnqueuer;
    }

    public void setFilePackagerEnqueuer(final FilePackagerEnqueuerI filePackagerEnqueuer) {
        this.filePackagerEnqueuer = filePackagerEnqueuer;
    }

    public ProcessLoggerI getLogger() {
        return logger;
    }

    public void setLogger(final ProcessLoggerI logger) {
        this.logger = logger;
    }

    public FilePackagerFactoryI getWsFilePackagerFactory() {
        return wsFilePackagerFactory;
    }

    public void setWsFilePackagerFactory(final FilePackagerFactoryI wsFilePackagerFactory) {
        this.wsFilePackagerFactory = wsFilePackagerFactory;
    }

    /**
     * Run the job and throw any exception that might arise so that JobDelegate can re-throw it
     *
     * @param filePackagerBean a <code>FilePackagerBean</code> for which to run the job
     * @throws Exception
     */
    public void run(final FilePackagerBean filePackagerBean) throws Exception {

        String archiveName = "";

        try {
            DiseaseContextHolder.setDisease(filePackagerBean.getDisease());
            filePackagerBean.setStatus(QuartzJobStatus.Started);
            archiveName = File.separator + filePackagerBean.getArchivePhysicalPathPrefix() + filePackagerBean.getArchivePhysicalName();
            logger.logToLogger(Level.INFO, "Started Job for " + archiveName + " at: " + new Date());

            // Update the QuartzJobHistory Maps before running the job
            updateQuartzJobHistoryMaps(filePackagerBean);

            filePackager.runJob(filePackagerBean);

            // Update the QuartzJobHistory Maps after running the job
            updateQuartzJobHistoryMaps(filePackagerBean);

            // Queued the ArchiveDeletionJob and retrieve it's trigger date
            // so that we can trigger the QuartzJobHistoryDeletionJob at the same time
            final Date triggerDate = filePackagerEnqueuer.queueArchiveDeletionJob(archiveName + ".tar.gz", filePackagerBean.isFailed());
            filePackagerEnqueuer.queueQuartzJobHistoryDeletionJob(filePackagerBean.getUpdatedQuartzJobHistory(), triggerDate);

            logger.logToLogger(Level.INFO, "Completed Job for " + archiveName + " at: " + new Date());
        }
        catch (Exception ex) {
            //Exception might be a wrapper for another kind of exception,
            // so pull that out and record it
            Exception origExp = ex;
            if (ex.getCause() != null && ex.getCause() instanceof Exception) {
                origExp = (Exception) ex.getCause();
            }
            logger.logToLogger(Level.ERROR, archiveName + " Job Failed at: " + new Date() + ProcessLogger.stackTracePrinter(origExp));

            //throw the unwrapped exception
            throw origExp;
        }
        finally {
            DiseaseContextHolder.clearDisease();
        }
    }

    /**
     * Update the <code>QuartzJobHistory</code> Maps with the latest from the given <code>FilePackagerBean</code>
     *
     * @param filePackagerBean the <code>FilePackagerBean</code> that holds the <code>QuartzJobHistory</code>
     */
    private void updateQuartzJobHistoryMaps(final FilePackagerBean filePackagerBean) {

        filePackagerFactory.putQuartzJobHistory(filePackagerBean.getKey(), filePackagerBean.getUpdatedQuartzJobHistory());
        wsFilePackagerFactory.putQuartzJobHistory(filePackagerBean.getKey(), filePackagerBean.getUpdatedQuartzJobHistory());
    }
}
