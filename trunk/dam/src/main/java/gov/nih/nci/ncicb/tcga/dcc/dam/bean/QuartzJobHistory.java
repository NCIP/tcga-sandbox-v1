/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * This class stores the status of a job along with the additional information
 * needed to build the <code>JobProcess</code> object that will be sent back to the web service user.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QuartzJobHistory implements Serializable {

    private static final long serialVersionUID = -7243285150151891403L;

    private String jobName;
    private String jobGroup;
    private Date fireTime;
    private QuartzJobStatus status;
    private Date lastUpdated;
    private String queueName;
    private Date enqueueDate;

    /**
     * Fields needed to build jobData XML String
     */
    private String linkText;
    private Long estimatedUncompressedSize;
    private Date jobWSSubmissionDate; // This value only makes sense for jobs submitted through the web service. It will be null otherwise.

    /**
     * Tag names used to build the XML returned by <code>getJobData()</code>, which will be stored in the DB
     */
    public static final String JOB_DATA = "jobData";
    public static final String LINK_TEXT = "linkText";
    public static final String ESTIMATED_UNCOMPRESSED_SIZE = "estimatedUncompressedSize";
    public static final String JOB_WS_SUBMISSION_DATE = "jobWSSubmissionDate";

    /*
     * Constructors
     */

    public QuartzJobHistory() {
    }

    /**
     * Create a <code>QuartzJobHistory</code> with the given fields.
     *
     * @param jobName the Quartz job name
     * @param jobGroup the Quartz job group
     * @param fireTime the Quartz trigger <code>Date</code>
     * @param status the Quartz job status
     * @param lastUpdated the <code>Date</code> at which this object was last updated
     * @param linkText the link to the archive, as text
     * @param estimatedUncompressedSize the estimated uncompressed size of the archive
     * @param jobWSSubmissionDate the job web service submission <code>date</code>
     */
    public QuartzJobHistory(final String jobName,
                            final String jobGroup,
                            final Date fireTime,
                            final QuartzJobStatus status,
                            final Date lastUpdated,
                            final String linkText,
                            final Long estimatedUncompressedSize,
                            final Date jobWSSubmissionDate) {

        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.fireTime = fireTime;
        this.status = status;
        this.lastUpdated = lastUpdated;
        this.linkText = linkText;
        this.estimatedUncompressedSize = estimatedUncompressedSize;
        this.jobWSSubmissionDate = jobWSSubmissionDate;
    }

    /**
     * Create a <code>QuartzJobHistory</code> with the given <code>UUID</code> ticket as the job name
     *
     * @param ticket the <code>UUID</code> ticket to use for the job name
     */
    public QuartzJobHistory(final UUID ticket) {
        this.jobName = ticket.toString();
    }

    /**
     * Return the XML document (as a <code>String</code>) for the job data
     *
     * @return the XML document (as a <code>String</code>) for the job data
     */
    public String getJobData() {

        // Storing Date with milliseconds precision
        Long jobWSSubmissionDateAsLong = null;
        if(getJobWSSubmissionDate() != null) {
            jobWSSubmissionDateAsLong = getJobWSSubmissionDate().getTime();
        }

        return new StringBuilder("<" + JOB_DATA + ">")
                .append(getXML(LINK_TEXT, getLinkText()))
                .append(getXML(ESTIMATED_UNCOMPRESSED_SIZE, getEstimatedUncompressedSize()))
                .append(getXML(JOB_WS_SUBMISSION_DATE, jobWSSubmissionDateAsLong))
                .append("</" + JOB_DATA + ">")
                .toString();
    }

    /**
     * Return an XML node (as a <code>StringBuilder</code>) out of the given tag name and value
     *
     * @param tagName tag name to use for the XML node
     * @param value the value to use for the XML node
     *
     * @return an XML node (as a <code>StringBuilder</code>) out of the given tag name and value
     */
    private StringBuilder getXML(final String tagName, final Object value) {

        return new StringBuilder("<")
                .append(tagName)
                .append(">")
                .append((value != null)?value.toString():"")
                .append("</")
                .append(tagName)
                .append(">")
                ;
    }

    /**
     * Return the <code>UUID</code> made of the job name
     *
     * @return the <code>UUID</code> made of the job name
     */
    public UUID getKey() {
        return UUID.fromString(getJobName());
    }

    /**
     * Return <code>true</code> if the job status is <code>Succeeded</code>, <code>false</code> otherwise
     *
     * @return <code>true</code> if the job status is <code>Succeeded</code>, <code>false</code> otherwise
     */
    public boolean isSucceeded() {
        return getStatus() == QuartzJobStatus.Succeeded;
    }

    /**
     * Return <code>true</code> if the job status is <code>Failed</code>, <code>false</code> otherwise
     *
     * @return <code>true</code> if the job status is <code>Failed</code>, <code>false</code> otherwise
     */
    public boolean isFailed() {
        return getStatus() == QuartzJobStatus.Failed;
    }

    /**
     * Return <code>true</code> if the job status is <code>Accepted</code>, <code>false</code> otherwise
     *
     * @return <code>true</code> if the job status is <code>Accepted</code>, <code>false</code> otherwise
     */
    public boolean isAccepted() {
        return getStatus() == QuartzJobStatus.Accepted;
    }

    /**
     * Return the status as a String
     *
     * @return the status as a String
     */
    public String getStatusAsString() {
        return getStatus().toString();
    }

    /*
     * Getter / Setter
     */

    public String getJobName() {
        return jobName;
    }

    public void setJobName(final String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(final String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public Date getFireTime() {
        return fireTime;
    }

    public void setFireTime(final Date fireTime) {
        this.fireTime = fireTime;
    }

    public QuartzJobStatus getStatus() {
        return status;
    }

    public void setStatus(final QuartzJobStatus status) {
        this.status = status;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(final String linkText) {
        this.linkText = linkText;
    }

    public Long getEstimatedUncompressedSize() {
        return estimatedUncompressedSize;
    }

    public void setEstimatedUncompressedSize(final Long estimatedUncompressedSize) {
        this.estimatedUncompressedSize = estimatedUncompressedSize;
    }

    public Date getJobWSSubmissionDate() {
        return jobWSSubmissionDate;
    }

    public void setJobWSSubmissionDate(final Date jobWSSubmissionDate) {
        this.jobWSSubmissionDate = jobWSSubmissionDate;
    }

    public void setQueueName(final String queueName) {
        this.queueName = queueName;
    }

    public Date getEnqueueDate() {
        return enqueueDate;
    }

    public void setEnqueueDate(final Date enqueueDate) {
        this.enqueueDate = enqueueDate;
    }

    public String getQueueName() {
        return queueName;
    }

    public boolean isComplete() {
        return isSucceeded() || isFailed();
    }
}
