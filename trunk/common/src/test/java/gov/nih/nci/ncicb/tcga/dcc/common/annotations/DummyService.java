/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.annotations;

/**
 * Dummy service for testing the TCGAValue annotation
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

public class DummyService {

    @TCGAValue (key = "dom.name")
    private String name;

    private String function;

    private String type;

    public String getServiceString() {
        return name + " - " + function+" "+getType();
    }

    public void setName(final String name) {
        this.name = name;
    }

    @TCGAValue (key = "", defaultValue = "great function indeed")
    public void setFunction(final String function) {
        this.function = function;
    }

    @TCGAValue (key = "", defaultValue = "B")
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
    
}//End of Class
