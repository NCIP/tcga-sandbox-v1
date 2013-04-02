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
 * class tht defines a bcr batch for code reports
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class BcrBatchCode {

    private String bcrBatch;
    private String studyCode;
    private String studyName;
    private String bcr;

    public String getBcrBatch() {
        return bcrBatch;
    }

    public void setBcrBatch(final String bcrBatch) {
        this.bcrBatch = bcrBatch;
    }

    public String getStudyCode() {
        return studyCode;
    }

    public void setStudyCode(final String studyCode) {
        this.studyCode = studyCode;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(final String studyName) {
        this.studyName = studyName;
    }

    public String getBcr() {
        return bcr;
    }

    public void setBcr(final String bcr) {
        this.bcr = bcr;
    }
}//End of class
