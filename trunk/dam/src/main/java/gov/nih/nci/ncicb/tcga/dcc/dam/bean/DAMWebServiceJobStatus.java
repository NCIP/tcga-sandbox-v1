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

/**
 * This bean class represent the status of job submitted byt he dam web service
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "job-status")
@XmlAccessorType(XmlAccessType.FIELD)
public class DAMWebServiceJobStatus {

    @XmlElement(name = "status-code") private String statusCode;
    @XmlElement(name = "status-message") private String statusMessage;
     @XmlElement(name = "archive-url") private String archiveUrl;

    public DAMWebServiceJobStatus(String statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public DAMWebServiceJobStatus() {
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getArchiveUrl() {
        return archiveUrl;
    }

    public void setArchiveUrl(String archiveUrl) {
        this.archiveUrl = archiveUrl;
    }
    
} //End of Class
