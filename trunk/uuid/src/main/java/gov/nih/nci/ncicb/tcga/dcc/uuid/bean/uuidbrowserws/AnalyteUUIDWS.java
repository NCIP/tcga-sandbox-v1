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
 * analyte bean class to be used for the uuid browser view web service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "analyte")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnalyteUUIDWS {

    @XmlAttribute(name = "href")
    private String analyte;
    private String analyteType;
    private String description;

    public AnalyteUUIDWS() {
    }

    public AnalyteUUIDWS(String analyte) {
        this.analyte = analyte;
    }

    public AnalyteUUIDWS(String analyteType, String description) {
        this.analyteType = analyteType;
        this.description = description;
    }

    public String getAnalyte() {
        return analyte;
    }

    public void setAnalyte(String analyte) {
        this.analyte = analyte;
    }

    public String getAnalyteType() {
        return analyteType;
    }

    public void setAnalyteType(String analyteType) {
        this.analyteType = analyteType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}//End of Class
