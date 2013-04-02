/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Robert S. Sfeir
 */
@XmlRootElement(name = "disease")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tumor implements Serializable{

    @XmlElement(name = "diseaseId")
    private Integer tumorId = null;

    @XmlElement(name = "abbreviation")
    private String tumorName = null;

    @XmlElement(name = "description")
    private String tumorDescription;

    private String tumorDisplayText;

    public Tumor() {
    }

    public Tumor(Integer tumorId, String tumorName) {
        this.tumorId = tumorId;
        this.tumorName = tumorName;
    }

    public Integer getTumorId() {
        return tumorId;
    }

    public void setTumorId( final Integer tumorId ) {
        this.tumorId = tumorId;
    }

    public String getTumorName() {
        return tumorName;
    }

    public void setTumorName( final String tumorName ) {
        this.tumorName = tumorName;
    }

    public String getTumorDisplayText() {
        return tumorName + " - " + tumorDescription;
    }

    public void setTumorDisplayText(String tumorDisplayText) {
        this.tumorDisplayText = tumorDisplayText;
    }

    public boolean equals( final Object o ) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final Tumor tumor = (Tumor) o;
        if(tumorId != null ? !tumorId.equals( tumor.tumorId ) : tumor.tumorId != null) {
            return false;
        }
        if(!tumorName.equals( tumor.tumorName )) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result;
        result = ( tumorId != null ? tumorId.hashCode() : 0 );
        result = 31 * result + tumorName.hashCode();
        return result;
    }

    public String toString() {
        return tumorName;
    }

    public String getTumorDescription() {
        return tumorDescription;
    }

    public void setTumorDescription(final String tumorDescription) {
        this.tumorDescription = tumorDescription;
    }
}
