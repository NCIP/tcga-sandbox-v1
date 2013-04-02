/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

public class DataSetMutation extends DataSet {

    private String mutationBarcode;
    private String matchedNormalBarcode;
    private String platformDisplayName;
    private String centerName;

    public String getMutationBarcode() {
        return mutationBarcode;
    }

    public void setMutationBarcode( final String mutationBarcode ) {
        this.mutationBarcode = mutationBarcode;
    }

    public String getMatchedNormalBarcode() {
        return matchedNormalBarcode;
    }

    public void setMatchedNormalBarcode( final String matchedNormalBarcode ) {
        this.matchedNormalBarcode = matchedNormalBarcode;
    }

    public String getPlatformDisplayName() {
        return platformDisplayName;
    }

    public void setPlatformDisplayName(final String platformDisplayName) {
        this.platformDisplayName = platformDisplayName;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }
}
