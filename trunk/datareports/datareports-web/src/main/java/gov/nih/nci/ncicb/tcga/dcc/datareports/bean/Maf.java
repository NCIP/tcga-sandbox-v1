/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;

/**
 * Class that extends the common archive definition and add a member url for the url of the maf file
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class Maf extends Archive {

    private String mafUrl;

    public Maf() {
    }

    public String getMafUrl() {
        return mafUrl;
    }

    public void setMafUrl(final String mafUrl) {
        this.mafUrl = mafUrl;
    }
}//End of Class
