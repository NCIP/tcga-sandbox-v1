/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * participant bean class to be used for the uuid browser view web service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "participant")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParticipantUUIDWS {

    @XmlAttribute(name = "href")
    private String participant;
    private String id;

    public ParticipantUUIDWS() {
    }

    public ParticipantUUIDWS(String participant) {
        this.participant = participant;
    }

    public ParticipantUUIDWS(String participant, String id) {
        this.participant = participant;
        this.id = id;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}//End of Class
