/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

/**
 * Author: David Nassau
 * <p/>
 * View items used for monitoring the queue.
 */
public class QueueMonitorItem {

    public enum JobType {
        ArchiveCreation, FileDeletion
    }

    public enum QueueType {
        Bigjob, Smalljob
    }

    public enum RunningState {
        Waiting, Running
    }

    int index;
    JobType jobType;
    String archiveName;
    long totalSize;
    QueueType queueType;
    RunningState runningState;

    public int getIndex() {
        return index;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(final JobType jobType) {
        this.jobType = jobType;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(final String archiveName) {
        this.archiveName = archiveName;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(final long totalSize) {
        this.totalSize = totalSize;
    }

    public QueueType getQueueType() {
        return queueType;
    }

    public void setQueueType(final QueueType queue) {
        this.queueType = queue;
    }

    public RunningState getRunningState() {
        return runningState;
    }

    public void setRunningState(final RunningState runningState) {
        this.runningState = runningState;
    }
}
