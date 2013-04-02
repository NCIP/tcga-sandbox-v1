/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;

/**
 * extension of common bean center to add the short name used in BAM files
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class CenterShort extends Center {

    public CenterShort() {
    }

    public CenterShort(Integer centerId, String centerType, String shortName) {
        this.setCenterId(centerId);
        this.setCenterType(centerType);
        this.shortName = shortName;
    }

    private String shortName;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
