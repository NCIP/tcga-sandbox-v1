/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import gov.nih.nci.ncicb.tcga.dcc.dam.service.JobDelegate;
import org.apache.commons.io.IOUtils;
import org.quartz.JobDataMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;

/**
 * Bean for Quartz [SMALL|BIG]_QUE_JOB_DETAILS
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QuartzQueueJobDetails {

    private String jobName;

    private String jobGroup;

    private String description;

    private String jobClassName;

    private Boolean isDurable;

    private Boolean isVolatile;

    private Boolean isStateFul;

    private Boolean requestsRecovery;

    /**
     * Fields extracted from jobData
     */
    private Long estimatedUncompressedSize;
    private Date jobWSSubmissionDate;

    /**
     * Constructor
     */
    public QuartzQueueJobDetails(final String jobName,
                                 final String jobGroup,
                                 final String description,
                                 final String jobClassName,
                                 final boolean isDurable,
                                 final boolean isVolatile,
                                 final boolean isStateFul,
                                 final boolean requestsRecovery,
                                 final Blob jobData) {

        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.description = description;
        this.jobClassName = jobClassName;
        this.isDurable = isDurable;
        this.isVolatile = isVolatile;
        this.isStateFul = isStateFul;
        this.requestsRecovery = requestsRecovery;

        //Populate estimatedUncompressedSize and jobWSSubmissionDate
        extractData(jobData);
    }

    /**
     * Extract estimatedUncompressedSize and jobWSSubmissionDate from the given <code>Blob</code>
     * (which is expected to be a serialized <code>FilePackagerBean</code>) and populates this bean's properties
     *
     * @param jobData the serialized <code>FilePackagerBean</code> 
     */
    private void extractData(final Blob jobData) {

        // Default values that will be used in case an exception is raised
        Long estimatedUncompressedSize = null;
        Date jobWSSubmissionDate = null;

        if(jobData != null) {

            ObjectInputStream objectInputStream = null;
            try {
                //noinspection IOResourceOpenedButNotSafelyClosed
                objectInputStream = new ObjectInputStream(jobData.getBinaryStream());
                final JobDataMap jobDataMap = (JobDataMap) objectInputStream.readObject();
                final FilePackagerBean filePackagerBean = (FilePackagerBean) jobDataMap.get(JobDelegate.DATA_BEAN);;

                estimatedUncompressedSize = filePackagerBean.getEstimatedUncompressedSize();
                jobWSSubmissionDate = filePackagerBean.getJobWSSubmissionDate();

            } catch (final IOException e) {
            } catch (final SQLException e) {
            } catch (final ClassNotFoundException e) {
            } finally {
                IOUtils.closeQuietly(objectInputStream);
            }
        }

        // Set this bean's fields
        setEstimatedUncompressedSize(estimatedUncompressedSize);
        setJobWSSubmissionDate(jobWSSubmissionDate);
    }

    /*
     * Getters / Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(final String jobClassName) {
        this.jobClassName = jobClassName;
    }

    public Boolean getDurable() {
        return isDurable;
    }

    public void setDurable(final Boolean durable) {
        isDurable = durable;
    }

    public Boolean getVolatile() {
        return isVolatile;
    }

    public void setVolatile(final Boolean aVolatile) {
        isVolatile = aVolatile;
    }

    public Boolean getStateFul() {
        return isStateFul;
    }

    public void setStateFul(final Boolean stateFul) {
        isStateFul = stateFul;
    }

    public Boolean getRequestsRecovery() {
        return requestsRecovery;
    }

    public void setRequestsRecovery(final Boolean requestsRecovery) {
        this.requestsRecovery = requestsRecovery;
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
}
