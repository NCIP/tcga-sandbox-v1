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
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.QuartzJobHistoryQueries;

import java.util.List;

/**
 * Service implementation for <code>QuartzJobHistory</code>
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QuartzJobHistoryServiceImpl implements QuartzJobHistoryService {

    private QuartzJobHistoryQueries quartzJobHistoryQueries;

    /**
     * Persist a <code>QuartzJobHistory</code>.
     * Return the numbers of rows affected by the update.
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to persist
     * @return the numbers of rows affected by the update
     */
    @Override
    public int persist(final QuartzJobHistory quartzJobHistory) {
        return getQuartzJobHistoryQueries().persist(quartzJobHistory);
    }

    /**
     * Retrieve all <code>QuartzJobHistory</code>
     *
     * @return a <code>List</code> of <code>QuartzJobHistory</code>
     */
    @Override
    public List<QuartzJobHistory> getAllQuartzJobHistory() {
        return getQuartzJobHistoryQueries().getAllQuartzJobHistory();
    }

    /**
     * Delete the given <code>QuartzJobHistory</code>.
     * Return the numbers of rows affected by the update.
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to delete
     * @return the numbers of rows affected by the update
     */
    @Override
    public int delete(final QuartzJobHistory quartzJobHistory) {
        return getQuartzJobHistoryQueries().delete(quartzJobHistory);
    }

    /**
     * Return the <code>QuartzJobHistory</code> with the given job name and job group, or <code>null</code> if no match is found
     * <p/>
     * Note: the job name is the same as the <code>UUID</code> from the <code>FilePackagerBean</code> the <code>QuartzJobHistory</code> is derived from.
     *
     * @param jobName  the job name
     * @param jobGroup the job group
     * @return the <code>QuartzJobHistory</code> with the given job name and job group, or <code>null</code> if no match is found
     */
    @Override
    public QuartzJobHistory getQuartzJobHistory(final String jobName, final String jobGroup) {
        return getQuartzJobHistoryQueries().getQuartzJobHistory(jobName, jobGroup);
    }

     /**
     * Finds the number of incomplete jobs for this job's queue that were enqueued before this job.  This
     * is the number of jobs ahead of this job in the queue, including those that are running.
     *
     * @param quartzJobHistory bean representing the job whose position you are looking for
     * @return the number of incomplete jobs in the same queue that were added earlier
     */
    @Override
    public Integer getPositionInQueue(final QuartzJobHistory quartzJobHistory) {
        return getQuartzJobHistoryQueries().getPositionInQueue(quartzJobHistory);
    }

    /*
     * Getter / Setter
     */

    public QuartzJobHistoryQueries getQuartzJobHistoryQueries() {
        return quartzJobHistoryQueries;
    }

    public void setQuartzJobHistoryQueries(final QuartzJobHistoryQueries quartzJobHistoryQueries) {
        this.quartzJobHistoryQueries = quartzJobHistoryQueries;
    }
}
