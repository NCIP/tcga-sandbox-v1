/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

/**
 * Bean representing a sample type.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SampleType {
    private String sampleTypeCode;
    private Boolean isTumor;
    private String definition;
    private String shortLetterCode;

    public void setSampleTypeCode(final String sampleTypeCode) {
        this.sampleTypeCode = sampleTypeCode;
    }

    public String getSampleTypeCode() {
        return sampleTypeCode;
    }

    public Boolean getIsTumor() {
        return isTumor;
    }

    public void setIsTumor(final Boolean tumor) {
        isTumor = tumor;
    }

    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }

    public String getShortLetterCode() {
        return shortLetterCode;
    }

    public void setShortLetterCode(String shortLetterCode) {
        this.shortLetterCode = shortLetterCode;
    }
}
