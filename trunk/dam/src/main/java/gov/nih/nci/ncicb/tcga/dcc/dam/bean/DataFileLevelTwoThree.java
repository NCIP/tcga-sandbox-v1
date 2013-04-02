/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import java.util.Collection;
import java.util.TreeSet;

/**
 * @author HickeyE
 * @version $id$
 */
public abstract class DataFileLevelTwoThree extends DataFile {
    // note: will keep data set ids sorted
    private Collection<Integer> dataSetsDP = new TreeSet<Integer>(); // NOT DAM DataSet but DP DATA_SET
    private Integer experimentId;
    private String sourceFileType;
    private String accessLevel;
    private String centerName;
    private String platformName;
    private Collection<Long> hybRefIds;
    private boolean isConsolidated = true;

    /**
     * Makes the appropriate data file for the level.
     * @param dataLevel the data level
     * @return a DataFileLevelTwo or DataFileLevelThree object
     */
    public static DataFileLevelTwoThree makeInstance(int dataLevel) {
        if (dataLevel == 3) {
            return new DataFileLevelThree();
        } else if (dataLevel == 2){
            return new DataFileLevelTwo();
        } else {
            return null;
        }
    }

    public Collection<Long> getHybRefIds() {
        return hybRefIds;
    }

    public void setHybRefIds(final Collection<Long> hybRefIds) {
        this.hybRefIds = hybRefIds;
    }

    public Collection<Integer> getDataSetsDP() {
        return dataSetsDP;
    }

    public void setDataSetsDP(Collection<Integer> datasetIds) {
        dataSetsDP = datasetIds;
    }

    public String getSourceFileType() {
        return sourceFileType;
    }

    public void setSourceFileType(final String sourceFileType) {
        this.sourceFileType = sourceFileType;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(final String accessLevel) {
        this.accessLevel = accessLevel;
        if (accessLevel.equalsIgnoreCase("PUBLIC")) {
            super.setProtected(false);
        } else {
            super.setProtected(true);
        }
    }

    public void addDataSetID(final int id) {
        dataSetsDP.add(id);
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(final String centerName) {
        this.centerName = centerName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(final String platformName) {
        this.platformName = platformName;
    }

    @Override
    public String getDisplayBarcodes() {
        if (isConsolidated()) {
            return "selected_barcodes";          
        } else {
            return super.getDisplayBarcodes();
        }
    }

    @Override
    public boolean mayPossiblyGenerateCacheFile() {
        return true;
    }

    public boolean isConsolidated() {
        return isConsolidated;
    }

    public void setConsolidated(final boolean consolidated) {
        isConsolidated = consolidated;
    }

    public Integer getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(final Integer experimentId) {
        this.experimentId = experimentId;
    }
}
