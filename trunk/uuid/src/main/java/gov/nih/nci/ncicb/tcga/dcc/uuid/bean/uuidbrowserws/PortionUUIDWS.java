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
 * portion bean class to be used for the uuid browser view web service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "portion")
@XmlAccessorType(XmlAccessType.FIELD)
public class PortionUUIDWS {

    @XmlAttribute(name = "href")
    private String portion;
    private String code;

    public PortionUUIDWS() {
    }

    public PortionUUIDWS(String portion) {
        this.portion = portion;
    }

    public PortionUUIDWS(String portion, String code) {
        this.portion = portion;
        this.code = code;
    }

    public String getPortion() {
        return portion;
    }

    public void setPortion(String portion) {
        this.portion = portion;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}//End of Class
