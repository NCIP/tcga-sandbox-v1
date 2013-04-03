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
 * sample bean class to be used for the uuid browser view web service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "sample")
@XmlAccessorType(XmlAccessType.FIELD)
public class SampleUUIDWS {

    @XmlAttribute(name = "href")
    private String sample;
    private String sampleType;
    private String description;
    private String shortLetterCode;
    private String vial;

    public SampleUUIDWS() {
    }

    public SampleUUIDWS(String sample) {
        this.sample = sample;
    }

    public SampleUUIDWS(String sampleType, String description, String shortLetterCode, String vial) {
        this.sampleType = sampleType;
        this.description = description;
        this.shortLetterCode = shortLetterCode;
        this.vial = vial;
    }

    public String getShortLetterCode() {
        return shortLetterCode;
    }

    public void setShortLetterCode(String shortLetterCode) {
        this.shortLetterCode = shortLetterCode;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVial() {
        return vial;
    }

    public void setVial(String vial) {
        this.vial = vial;
    }
}//End of Class
