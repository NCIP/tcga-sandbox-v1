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
 * Tissue Source Site bean class
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TissueSourceSite {

    private String tissueSourceSiteId;
    private String name;

    public String getTissueSourceSiteId() {
        return tissueSourceSiteId;
    }

    public void setTissueSourceSiteId(String tissueSourceSiteId) {
        this.tissueSourceSiteId = tissueSourceSiteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}//End of Class
