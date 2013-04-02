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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ArchiveLoader;
import org.quartz.SchedulerException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Interface used by qclive for scheduling jobs.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface JobScheduler {
    /**
     * Schedules an experiment check for the given experiment, to run at the given time.
     *
     * @param experimentName the name of the experiment (center_platform.disease)
     * @param experimentType the type of the experiment (CGCC, BCR, GSC)
     * @param whenToRun      when the check should be scheduled for
     * @throws org.quartz.SchedulerException if there is an error scheduling the job with the Quartz scheduler
     */
    public void scheduleExperimentCheck(final String experimentName,
                                        final String experimentType,
                                        final String archiveName,
                                        final Calendar whenToRun,
                                        final QcLiveStateBean stateContext,
                                        final String groupName) throws SchedulerException;

    /**
     * Schedules a call to cleanup the given archive after processing is complete (be it successfully or
     * unsuccessfully.)
     *
     * @param depositArchive deposited archive object
     * @param archiveFailed  did the archive processing fail or not
     * @throws org.quartz.SchedulerException if there is an error scheduling the cleanup job
     */
    public void scheduleArchiveCleanup(Archive depositArchive, boolean archiveFailed) throws SchedulerException;

    /**
     * Schedules a call to check an uploaded file.
     *
     * @param file      the uploaded file
     * @param whenToRun when the check should occur
     * @throws SchedulerException if the check could not be scheduled
     * @param stateContext to carry persistent information between scheduler executions
     * @throws IOException        if the file path cannot be determined
     */
    public void scheduleUploadCheck(File file, Calendar whenToRun,QcLiveStateBean stateContext) throws SchedulerException, IOException;

    /**
     * Re-Schedules a call to check md5 for the given file name
     *
     * @param fileName           uploaded file name
     * @param whenToRun          when the check should occur
     * @param validationAttempts number of validation attempts already made
     * @param stateContext to carry persistent information between scheduler executions
     * @throws SchedulerException scheduler exception
     */
    public void scheduleUploadCheck(String fileName, Calendar whenToRun, Integer validationAttempts,QcLiveStateBean stateContext) throws SchedulerException;

    /**
     * Schedules clinicalLoader execution
     *
     * @param archivesToLoad a list of archives to process
     * @param whenToRun      when the loading should take place
     * @param stateContext to carry persistent information between scheduler executions
     * @throws SchedulerException scheduler exception
     */
    public void scheduleClinicalLoader(List<Archive> archivesToLoad, Calendar whenToRun, String experimentName, QcLiveStateBean stateContext) throws SchedulerException;             
}
