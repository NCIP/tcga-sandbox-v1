/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Pending UUID web service response bean
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "pending_metadata_response")
@XmlAccessorType(XmlAccessType.FIELD)
public class PendingUUIDResponse {

    @XmlElement(name = "response_message")
    private String responseMessage;

    @XmlElement(name = "error_messages")
    private List<String> errorMessages;

    public PendingUUIDResponse() {
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
