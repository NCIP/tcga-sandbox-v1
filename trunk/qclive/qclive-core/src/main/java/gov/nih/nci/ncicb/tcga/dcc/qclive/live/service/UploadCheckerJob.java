/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.LiveI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.impl.jdbcjobstore.QueueJobStore;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;

/**
 * Runner for upload checker.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class UploadCheckerJob implements StatefulJob {
    private final Log logger = LogFactory.getLog(getClass());
    protected ExperimentDAO experimentDAO;
    protected LiveI live;

    public UploadCheckerJob() {
        initFields();
    }

    public void execute(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String filename = (String) jobDataMap.get("file");
        Integer md5ValidationAttempts = (Integer) jobDataMap.get("md5ValidationAttempts");        
        QcLiveStateBean stateContext =  (QcLiveStateBean)jobDataMap.get(LiveScheduler.STATE_CONTEXT);
        Boolean interruptedJob = (Boolean) jobDataMap.get(QueueJobStore.RECOVERED_INTERRUPTED_JOB);
        logger.debug("UploadChecker " + filename + " started.");
        // do the cleanup if the job got interrupted and not completed in its previous execution
        if (interruptedJob != null &&
                interruptedJob) {
            cleanup(filename);
        }

        live.processUpload(filename, md5ValidationAttempts,stateContext);
        logger.debug("UploadChecker " + filename + " completed.");
    }

    protected void initFields() {
        experimentDAO = (ExperimentDAO) SpringApplicationContext.getObject(ConstantValues.EXPERIMENT_DAO_OBJECT_SPRING_BEAN_NAME);
        live = (LiveI) SpringApplicationContext.getObject(ConstantValues.LIVE_OBJECT_SPRING_BEAN_NAME);
    }


    protected void cleanup(final String filepath) {
    	  logger.debug("UploadChecker clean " + filepath);
          try {
              String explodedDirectoryLocation = filepath;
              // remove the expanded dir if exists
              if (filepath.endsWith(FileUtil.TAR_GZ)) {
                  explodedDirectoryLocation = filepath.substring(0, filepath.length() - FileUtil.TAR_GZ.length());
                  final File explodedDirectory = new File(explodedDirectoryLocation);
                  FileUtil.deleteDir(explodedDirectory);
              }
              File fileName = new File(explodedDirectoryLocation);            
              if (fileName != null && fileName.exists() ){
              	final String filename = new File(explodedDirectoryLocation).getName();
  	            Archive archive = experimentDAO.getArchiveByName(filename);
  	            if (archive != null){
  		            archive.setDeployStatus(Archive.STATUS_INTERRUPTED);
  		            //update archive status
  		            experimentDAO.updateArchiveStatus(archive);
  	            }
              }
          } catch (Exception e) {
              logger.error("Cleanup Failed: " + e.toString(), e);

          }
    }
}

