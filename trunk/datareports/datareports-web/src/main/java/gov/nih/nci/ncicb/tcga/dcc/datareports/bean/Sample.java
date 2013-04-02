/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

/**
 * This class defines a sample
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class Sample {

    private String name;
    private String sampleDate;

    //only useful for the mock object creation
    public Sample(String name){
        this.name = name;
    }

    public Sample(){}

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }
}//End of Class
