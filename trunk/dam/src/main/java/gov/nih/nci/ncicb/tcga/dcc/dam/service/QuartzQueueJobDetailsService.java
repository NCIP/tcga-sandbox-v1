/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzQueueJobDetails;

/**
 * QuartzQueueJobDetails Service
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface QuartzQueueJobDetailsService {

    /**
     * Return Quartz small queue job details with the given job name and job group
     *
     * @param jobName the job name
     * @param jobGroup the job group
     * @return Quartz small queue job details with the given job name and job group
     */
    public QuartzQueueJobDetails getQuartzSmallQueueJobDetails(final String jobName, final String jobGroup);

    /**
     * Return Quartz big queue job details with the given job name and job group
     *
     * @param jobName the job name
     * @param jobGroup the job group
     * @return Quartz big queue job details with the given job name and job group
     */
    public QuartzQueueJobDetails getQuartzBigQueueJobDetails(final String jobName, final String jobGroup);

    /**
     * Return Quartz small or big queue job details with the given job name and job group
     *
     * @param jobName the job name
     * @param jobGroup the job group
     * @return Quartz small or big queue job details with the given job name and job group
     */
    public QuartzQueueJobDetails getQuartzSmallOrBigQueueJobDetails(final String jobName, final String jobGroup);

    /**
     * Return <code>true</code> if the job with the given name and group can be found in the small queue or the big queue
     *
     * @param jobName the job name
     * @param jobGroup the job group
     * @return <code>true</code> if the job with the given name and group can be found in the small queue or the big queue
     */
    public boolean hasQuartzQueueJobDetails(final String jobName, final String jobGroup);
}
