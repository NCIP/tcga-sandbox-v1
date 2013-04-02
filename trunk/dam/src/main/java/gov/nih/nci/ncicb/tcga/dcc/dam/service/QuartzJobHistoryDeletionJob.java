/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Run a Quartz Job to remove old QuartzJobHistory entries from the QRTZ_JOB_HISTORY table.
 * It should be run at the same time as the associated archive has been deleted
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QuartzJobHistoryDeletionJob implements QueueJob<QuartzJobHistory> {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private QuartzJobHistoryService quartzJobHistoryService;

    /**
     * Run the job and throw any exception that might arise so that JobDelegate can re-throw it
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to be deleted
     * @throws Exception
     */
    @Override
    public void run(final QuartzJobHistory quartzJobHistory) throws Exception {
        getQuartzJobHistoryService().delete(quartzJobHistory);
    }

    /*
     * Getter / Setter
     */

    public Log getLog() {
        return log;
    }

    public QuartzJobHistoryService getQuartzJobHistoryService() {
        return quartzJobHistoryService;
    }

    public void setQuartzJobHistoryService(final QuartzJobHistoryService quartzJobHistoryService) {
        this.quartzJobHistoryService = quartzJobHistoryService;
    }
}
