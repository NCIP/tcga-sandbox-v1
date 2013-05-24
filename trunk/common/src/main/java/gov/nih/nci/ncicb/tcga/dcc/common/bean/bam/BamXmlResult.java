/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.bean.bam;

import java.util.List;

/**
 * Bam telemetry xml CGHub data object.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BamXmlResult extends BamTelemetry {

    private String state;

    private List<BamXmlFileRef> bamXmlFileRefList;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<BamXmlFileRef> getBamXmlFileRefList() {
        return bamXmlFileRefList;
    }

    public void setBamXmlFileRefList(List<BamXmlFileRef> bamXmlFileRefList) {
        this.bamXmlFileRefList = bamXmlFileRefList;
    }

}//End of Class

