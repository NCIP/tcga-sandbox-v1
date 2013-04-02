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
 * TISSUE_SOURCE_SITE object representing a Tissue source site
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class TissueSourceSite extends CodeReport{

    private String bcr;
    private String studyName;

    public String getBcr() {
        return bcr;
    }

    public void setBcr(String bcr) {
        this.bcr = bcr;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(final String studyName) {
        this.studyName = studyName;
    }
}//End of Class
