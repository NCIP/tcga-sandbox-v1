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
 * Date: Jul 7, 2008
 * Time: 11:21:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class Biospecimen {

    private String CenterName;
    private String Shipdate;
    private String Batch;
    private String AnalyteType;
    private String AnalyteCount;

    public String getCenterName() {
        return CenterName;
    }

    public void setCenterName( final String center ) {
        CenterName = center;
    }

    public String getShipdate() {
        return Shipdate;
    }

    public void setShipdate( final String shipdate ) {
        Shipdate = shipdate;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch( final String batch ) {
        Batch = batch;
    }

    public String getAnalyteType() {
        return AnalyteType;
    }

    public void setAnalyteType( final String analyteType ) {
        AnalyteType = analyteType;
    }

    public String getAnalyteCount() {
        return AnalyteCount;
    }

    public void setAnalyteCount( final String analyteCount ) {
        AnalyteCount = analyteCount;
    }
}
