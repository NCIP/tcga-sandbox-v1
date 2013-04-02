/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

/**
 * Created by IntelliJ IDEA.
 * User: fengla
 * Date: Jun 30, 2008
 * Time: 10:31:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class TraceFileInfo {

    private String centerName;
    private String loadDate;
    private String totalCount;

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName( final String centerName ) {
        this.centerName = centerName;
    }

    public String getLoadDate() {
        return loadDate;
    }

    public void setLoadDate( final String loadDate ) {
        this.loadDate = loadDate;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount( final String totalCount ) {
        this.totalCount = totalCount;
    }
}
