/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMHelper;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMHelperI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.FilterChoices;
import org.quartz.SchedulerContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.apache.log4j.Level;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.StaticMatrixModelFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;

/**
 * Description : Quartz job that refreshes DAM cache every night
 *
 * @author Namrata Rane Last updated by: $Author$
 * @version $Rev$
 */
public class DAMRefreshJob extends QuartzJobBean {

    private ProcessLogger logger;

    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            final SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
            logger = getLogger(schedulerContext);
            logger.logToLogger(Level.INFO, "Starting DAMRefreshJob");
            final DAMHelperI damHelper = getDAMHelper();
            final StaticMatrixModelFactoryI staticMatrixModelFactory = getStaticMatrixModelFactory(schedulerContext);
            staticMatrixModelFactory.refreshAll();
            damHelper.refreshTumorCenterPlatformInfoCache();
            FilterChoices.clearInstances();

            logger.logToLogger(Level.INFO, "Finished Executing DAMRefreshJob");
        } catch (SchedulerException e) {
            logger.logToLogger(Level.ERROR, "Exception while executing DAMRefreshJob" + e.toString());
            throw new JobExecutionException("DAMRefreshJob executeInternal exception " + e.toString());
        } catch (DataAccessMatrixQueries.DAMQueriesException e) {
            logger.logToLogger(Level.ERROR, "Exception while executing DAMRefreshJob" + e.toString());
            throw new JobExecutionException("DAMRefreshJob executeInternal exception " + e.toString());
        }
    }

    protected DAMHelperI getDAMHelper() {
        return (DAMHelper) SpringApplicationContext.getObject(ConstantValues.DAM_HELPER_SPRING_BEAN_NAME);
    }

    protected ProcessLogger getLogger(SchedulerContext schedulerContext) {
        return (ProcessLogger) schedulerContext.get("logger");
    }

    protected StaticMatrixModelFactoryI getStaticMatrixModelFactory(SchedulerContext schedulerContext) {
        return (StaticMatrixModelFactoryI) schedulerContext.get("staticMatrixModelFactory");
    }


}
