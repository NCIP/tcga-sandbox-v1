/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.service;


import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.LoaderArchive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.Loader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.Serializable;

import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;
/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: May 13, 2009
 * Time: 6:01:56 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Quartz job that starts the LoaderRunner.
 */
public class LoaderJob extends QuartzJobBean implements Serializable {
    private final Log logger = LogFactory.getLog(getClass());
    protected Level2DataCacheEnqueuerI level2DataCacheEnqueuer;

    public LoaderJob() {
        initFields();
    }

    protected void executeInternal(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LoaderRunnerI runner = (LoaderRunnerI) jobExecutionContext.getJobDetail().getJobDataMap().get("loaderRunner");
        try {
            runner.runJob();
        } catch (LoaderException e) {
            e.printStackTrace();  //TODO change this to accommodate a better error reporting model.  -- RSS
        } finally {
            try {
                // check whether all the loader jobs for this experiment are completed
                final String groupName = jobExecutionContext.getJobDetail().getGroup();
                String[] jobNames = jobExecutionContext.getScheduler().getJobNames(groupName);
                boolean isLastJob = (jobNames.length == 1) ? true : false;
                refreshCache(runner.getLoader(), isLastJob);
            } catch (Throwable e) {
                logger.error(e.toString(), e);
                throw new JobExecutionException(e);
            }
        }
    }

    protected void initFields() {
        level2DataCacheEnqueuer = (Level2DataCacheEnqueuer) SpringApplicationContext.getObject(ConstantValues.LEVEL2_CACHE_ENQUEUER_SPRING_BEAN_NAME);
    }

    private void refreshCache(final Loader loader, final boolean isLastJob) throws SchedulerException {
        final LoaderArchive archive = loader.getArchive();
        final Level2DataFilterBean level2DataFilterBean = new Level2DataFilterBean();
        level2DataFilterBean.setCenterDomainName(archive.getCenter());
        level2DataFilterBean.setDiseaseAbbreviation(archive.getDisease());
        level2DataFilterBean.setPlatformName(archive.getPlatform());
        level2DataFilterBean.addExperimentId(archive.getExperimentId());

        JobDetail jobDetail = level2DataCacheEnqueuer.addJob(level2DataFilterBean);
        logger.info(new StringBuilder("Added job to generate level2data cache files for ")
                .append(archive)
                .toString());
        // If this is the last job for this experiment, schedule trigger for the job to generate cache files
        if (isLastJob) {
            level2DataCacheEnqueuer.scheduleTrigger(jobDetail);
            logger.info(new StringBuilder("Scheduled job to generate level2data cache files ")
                    .append(jobDetail.getName())
                    .append("-")
                    .append(jobDetail.getGroup())
                    .toString());
        }

    }
}
