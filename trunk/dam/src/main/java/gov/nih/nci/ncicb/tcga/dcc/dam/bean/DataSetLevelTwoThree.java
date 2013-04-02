/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

/**
 * @user: HickeyE
 * @version: $id$
 */
public class DataSetLevelTwoThree extends DataSet { // TODO: name should reflect Level 2 and 3 usage
    private String centerName;
    private String platformName;
    private String depositBaseName;
    private int depositBatch;
    private int dataRevision;
    private int experimentID;   // TODO: we now have both DCC and DP internal IDs in the same class.  This will get confusing.  Maybe we should stop storing internal IDs in the Code and use the names instead.

    public void setCenterName( final String c ) {
        centerName = c;
    }

    public void setDataDepositBaseName( final String n ) {
        depositBaseName = n;
    }

    public void setDataDepositBatch( final int b ) {
        depositBatch = b;
    }

    public void setDataRevision( final int r ) {
        dataRevision = r;
    }

    public String getCenterName() {
        return centerName;
    }

    public String getDepositBaseName() {
        return depositBaseName;
    }

    public int getDepositBatch() {
        return depositBatch;
    }

    public int getDataRevision() {
        return dataRevision;
    }

    public void setExperimentID( final int eID ) {
        experimentID = eID;
    }

    public int getExperimentID() {
        return experimentID;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName( final String platformName ) {
        this.platformName = platformName;
    }
}
