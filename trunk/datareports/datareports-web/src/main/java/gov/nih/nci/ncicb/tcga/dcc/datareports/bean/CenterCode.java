/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;

/**
 * Class that defines a center for the code reports
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class CenterCode extends Center {

    private String code;
    private String shortName;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }
}//End of Class
