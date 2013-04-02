/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

/**
 * This class defined a Job process for the DAM Webservice
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "job-process")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobProcess {

    @XmlElement private UUID ticket; //This is the ticket UUID of the job and is unique
    @XmlElement(name = "submission-time") private Date submissionTime;
    @XmlElement(name = "estimated-size") private Long estimatedSize;
    @XmlElement(name = "status-check-url") private String statusCheckUrl;
    @XmlElement(name = "job-status")  private DAMWebServiceJobStatus jobStatus;

    public UUID getTicket() {
        return ticket;
    }

    public void setTicket(UUID ticket) {
        this.ticket = ticket;
    }

    public Date getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(Date submissionTime) {
        this.submissionTime = submissionTime;
    }

    public Long getEstimatedSize() {
        return estimatedSize;
    }

    public void setEstimatedSize(Long estimatedSize) {
        this.estimatedSize = estimatedSize;
    }

    public String getStatusCheckUrl() {
        return statusCheckUrl;
    }

    public void setStatusCheckUrl(String statusCheckUrl) {
        this.statusCheckUrl = statusCheckUrl;
    }

    public DAMWebServiceJobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(DAMWebServiceJobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }
}//End of Class
