/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ArchiveNameValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentDAO;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.LiveI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import org.quartz.StatefulJob;
import org.quartz.impl.jdbcjobstore.QueueJobStore;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * Job for running checks on experiments that have been uploaded.
 * This is defined as stateful job so that the same experiment jobs won't run in parallel
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr ,rraman$
 * @version $Rev: 3419 $
 */
public class ExperimentCheckerJob implements StatefulJob {
    protected ExperimentDAO experimentDAO;
    protected LiveI live;

    private final Log logger = LogFactory.getLog(getClass());

    public ExperimentCheckerJob() {
        initFields();
    }

    public void execute(final JobExecutionContext context) throws JobExecutionException {
        
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String experimentName = (String) jobDataMap.get(LiveScheduler.EXPERIMENT_NAME);
        String experimentType = (String) jobDataMap.get(LiveScheduler.EXPERIMENT_TYPE);
        final String archiveName = (String) jobDataMap.get(LiveScheduler.ARCHIVE_NAME);

        logger.info("ExperimentCheckerJob for " + experimentName + "-" + archiveName + " started");
        Boolean interruptedJob = (Boolean) jobDataMap.get(QueueJobStore.RECOVERED_INTERRUPTED_JOB);
        
        QcLiveStateBean stateContext = (QcLiveStateBean)jobDataMap.get(LiveScheduler.STATE_CONTEXT);
        
        try {
            // set the disease context
            setDiseaseContext(experimentName);
            // do the cleanup if the job got interrupted and not completed in its previous execution
            if (interruptedJob != null &&
                    interruptedJob) {
                cleanup(archiveName);
            }
            // check whether the job is the legacy job. Legacy jobs group name is set to 'QCLive'. If it is a legacy job
            // just run that job.
            boolean isLegacyJob = "QCLive".equals(context.getJobDetail().getGroup())?true:false;

            // check whether more triggers exists for this job
            boolean isLastTrigger = (context.getScheduler().getTriggerNames(context.getJobDetail().getGroup()).length > 1) ? false:true;
            // Experiment trigger is scheduled for each archive. So multiple triggers get scheduled for the same experiment. As experiment
            // is based on multiple archives if there are multiple triggers associated with the same experiment, execute the experiment job
            // only in the last trigger execution to avoid running the same experiment job multiple times.
            if(isLegacyJob || isLastTrigger){
                if(Experiment.TYPE_BCR.equals(experimentType)) {
                    live.checkExperiment(experimentName, experimentType, archiveName,stateContext);
                } else {
                    live.checkExperiment(experimentName, experimentType,stateContext);
                }
                logger.info("ExperimentCheckerJob for " + experimentName + "-" + archiveName + " completed");
            }else{
                logger.warn( (context.getScheduler().getTriggerNames(context.getJobDetail().getGroup()).length -1) + " triggers already scheduled for this experiment. Do not run ExperimentCheckerJob now. " );
            }
                
            
        } catch (Throwable e) {
            logger.error("ExperimentCheckerJob failed for " + experimentName + "-" + archiveName, e);
            throw new JobExecutionException(e);
        }
    }

    protected void cleanup(final String archiveName) throws IOException {
        // no archives to recover
        if (archiveName == null || archiveName.length() == 0)
            return;


        Archive archive = new Archive(archiveName);
        archive = experimentDAO.getArchiveByName(archive.getArchiveName());
        archive.setArchiveFile(new File(archiveName));
        // get deploy dir
        final File deployDir = experimentDAO.getDeployDirectoryPath(archive);
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
        // create exploded dir
        File explodedDir = FileUtil.makeDir(archive.getExplodedArchiveDirectoryLocation());
        // explode tar or tar.gz file
        FileUtil.explodeTarOrTarGz(explodedDir, archive.fullArchivePathAndName());
        // update the status to be uploaded
        archive.setDeployStatus(Archive.STATUS_UPLOADED);
        experimentDAO.updateArchiveStatus(archive);
    }

    protected void initFields() {
        experimentDAO = (ExperimentDAO) SpringApplicationContext.getObject(ConstantValues.EXPERIMENT_DAO_OBJECT_SPRING_BEAN_NAME);
        live = (LiveI) SpringApplicationContext.getObject(ConstantValues.LIVE_OBJECT_SPRING_BEAN_NAME);
    }


    public void setDiseaseContext(final String experimentName) {
        Matcher nameMatcher = ArchiveNameValidator.EXPERIMENT_NAME_PATTERN.matcher(experimentName);
        if (nameMatcher.find()) {
            DiseaseContextHolder.setDisease(nameMatcher.group(ArchiveNameValidator.INDEX_IN_ARCHIVE_NAME_TUMOR_TYPE));
        }
    }
}
