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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * disease bean class to be used for the uuid browser view web service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "disease")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiseaseUUIDWS {

    private String abbreviation;
    private String description;

    public DiseaseUUIDWS() {
    }

    public DiseaseUUIDWS(String abbreviation, String description) {
        this.abbreviation = abbreviation;
        this.description = description;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}//End of class
