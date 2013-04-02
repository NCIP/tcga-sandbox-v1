/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

/**
 * bean class representing a portion analyte
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class PortionAnalyte {

    private String portionAnalyteCode;
    private String definition;

    public PortionAnalyte() {
    }

    public PortionAnalyte(String portionAnalyteCode, String definition) {
        this.portionAnalyteCode = portionAnalyteCode;
        this.definition = definition;
    }

    public String getPortionAnalyteCode() {
        return portionAnalyteCode;
    }

    public void setPortionAnalyteCode(String portionAnalyteCode) {
        this.portionAnalyteCode = portionAnalyteCode;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}//End of Class
