/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;

import java.util.List;

/**
 * Interface for <code>QuartzJobHistory</code> queries
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface QuartzJobHistoryQueries {

    /**
     * Persist a <code>QuartzJobHistory</code> in the DB.
     * Return the numbers of rows affected by the update
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to persist
     * @return the numbers of rows affected by the update
     */
    public int persist(final QuartzJobHistory quartzJobHistory);

    /**
     * Retrieve all <code>QuartzJobHistory</code> from the DB
     *
     * @return a <code>List</code> of <code>QuartzJobHistory</code>
     */
    public List<QuartzJobHistory> getAllQuartzJobHistory();

    /**
     * Delete the given <code>QuartzJobHistory</code> from the DB.
     * Return the numbers of rows affected by the update
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to delete
     * @return the numbers of rows affected by the update
     */
    public int delete(QuartzJobHistory quartzJobHistory);

    /**
     * Return the <code>QuartzJobHistory</code> with the given job name and job group, or <code>null</code> if no match is found
     * <p/>
     * Note: the job name is the same as the <code>UUID</code> from the <code>FilePackagerBean</code> the <code>QuartzJobHistory</code> is derived from.
     *
     * @param jobName  the job name
     * @param jobGroup the job group
     * @return the <code>QuartzJobHistory</code> with the given job name and job group, or <code>null</code> if no match is found
     */
    public QuartzJobHistory getQuartzJobHistory(final String jobName, final String jobGroup);

     /**
     * Queries to find the number of incomplete jobs for this job's queue that were enqueued before this job.  This
     * is the number of jobs ahead of this job in the queue.
     *
     * @param quartzJobHistory bean representing the job whose position you are looking for
     * @return the number of incomplete jobs in the same queue that were added earlier
     */
    public Integer getPositionInQueue(QuartzJobHistory quartzJobHistory);
}
