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
 * Class that represents a datatype for the code reports
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class DataType {

    private String centerType;
    private String displayName;
    private String ftpDisplay;
    private String available;

    public String getCenterType() {
        return centerType;
    }

    public void setCenterType(final String centerType) {
        this.centerType = centerType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getFtpDisplay() {
        return ftpDisplay;
    }

    public void setFtpDisplay(final String ftpDisplay) {
        this.ftpDisplay = ftpDisplay;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(final String available) {
        this.available = available;
    }
}//End of Class
