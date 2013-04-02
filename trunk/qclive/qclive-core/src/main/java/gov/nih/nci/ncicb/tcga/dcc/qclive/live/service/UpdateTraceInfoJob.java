/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * User: fengla
 * Date: Sep 22, 2008
 */
public class UpdateTraceInfoJob extends QuartzJobBean {

    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException {
        UpdateTraceInfoRunner runner = (UpdateTraceInfoRunner) jobExecutionContext.getJobDetail().getJobDataMap().get( "updateTraceInfoRunner" );
        try {
            runner.runJob();
        }
        catch(Exception e) {
            //shouldn't happen - we should catch anything earlier, but just in case this is our last chance
            throw new JobExecutionException( "UpdateTraceInfoJob executeInternal e " + e.toString() );
        }
    }
}