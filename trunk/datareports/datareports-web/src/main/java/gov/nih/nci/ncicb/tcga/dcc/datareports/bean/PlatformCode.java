/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;

/**
 * Class the defines a platform for the code reports
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class PlatformCode extends Platform {

    private String platformAlias;
    private String available;

    public String getPlatformAlias() {
        return platformAlias;
    }

    public void setPlatformAlias(final String platformAlias) {
        this.platformAlias = platformAlias;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(final String available) {
        this.available = available;
    }
}//End of Class
