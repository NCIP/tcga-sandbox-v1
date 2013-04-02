/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

import java.util.Date;

public class DataFileClinical extends DataFile {
    private Date dateAdded;
    private String dynamicIdentifier;

    public DataFileClinical() {
        setLevel(DataAccessMatrixQueries.LEVEL_CLINICAL);
    }

    @Override
    public boolean mayPossiblyGenerateCacheFile() {
        return false;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(final Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDynamicIdentifier() {
        return dynamicIdentifier;
    }

    public void setDynamicIdentifier(final String dynamicIdentifier) {
        this.dynamicIdentifier = dynamicIdentifier;
    }

}
