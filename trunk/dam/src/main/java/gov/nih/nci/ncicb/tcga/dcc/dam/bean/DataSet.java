/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import java.util.Date;
import java.util.List;

/**
 * Author: David Nassau
 * Instances are created by the DAO when the first user hits the site.
 * They are used to populate the DAMStaticModel then discarded.
 */
public class DataSet {

    private String sample, batch, platformTypeId, centerId, level, availability, platformId, tumorNormal;
    private boolean isProtected;
    private List<String> barcodes;
    private Date dateAdded;
    private int platformTypeSortOrder;
    private int archiveId; // the archive ID that contains the data for this dataset    
    private String platformAlias;
    private int batchNumber; //for sorting
    private String diseaseType; // disease abbreviation
    private List<DataFile> dataFiles; // data files representing this data set.  may be null!

    /**
     * id referring to a platform type - the actual display will come from an image
     *
     * @param value the platform type id
     */
    public void setPlatformTypeId( final String value ) {
        platformTypeId = value;
    }

    public String getPlatformTypeId() {
        return platformTypeId;
    }

    public int getPlatformTypeSortOrder() {
        return platformTypeSortOrder;
    }

    public void setPlatformTypeSortOrder( final int platformTypeSortOrder ) {
        this.platformTypeSortOrder = platformTypeSortOrder;
    }

    /**
     * id referring to a center. The display value will come from the "centers" application context object
     *
     * @param value the center id
     */
    public void setCenterId( final String value ) {
        centerId = value;
    }

    public String getCenterId() {
        return centerId;
    }

    /**
     * the level, exactly as it will appear in the UI, e.g. "1". For clinical, should be null.
     *
     * @param value the level
     */
    public void setLevel( final String value ) {
        level = value;
    }

    public String getLevel() {
        return level;
    }

    /**
     * @return batch number, exactly as it will appear in the UI
     */
    public String getBatch() {
        return batch;
    }

    public void setBatch( final String value ) {
        batch = value;
        if(batch.startsWith( "Batch " )) {
            batchNumber = Integer.parseInt( batch.substring( 6 ) );
        } else {
            batchNumber = -1;
        }
    }

    public int getBatchNumber() {
        return batchNumber;
    }

    /**
     * @param value sample number, exactly as it will appear in the UI
     */
    public void setSample( final String value ) {
        sample = value;
    }

    public String getSample() {
        return sample;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded( Date dateAdded ) {
        this.dateAdded = dateAdded;
    }

    /**
     * @return availability one-letter code, e.g. "A", "P", "N".  For the N/A case, leave null
     */
    public String getAvailability() {
        return availability;
    }

    public void setAvailability( final String value ) {
        availability = value;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected( final boolean value ) {
        isProtected = value;
    }

    /**
     * Full barcode. This won't appear in the UI at all, but we keep it as a useful ID
     * that we can pass back in to get the actual file names when needed.
     *
     * @return list of full barcodes
     */
    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes( List<String> value ) {
        barcodes = value;
    }

    /**
     * platform (not platform type) - used for display in some cases
     * (only when the same center uses two different platforms for the same platform type)
     *
     * @return platform id
     */
    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId( final String value ) {
        platformId = value;
    }

    public String getTumorNormal() {
        return tumorNormal;
    }

    public void setTumorNormal( String tumorNormal ) {
        this.tumorNormal = tumorNormal;
    }

    public int getArchiveId() {
        return archiveId;
    }

    public void setArchiveId( final int archiveId ) {
        this.archiveId = archiveId;
    }

    public String getPlatformAlias() {
        return platformAlias;
    }

    public void setPlatformAlias( final String platformAlias ) {
        this.platformAlias = platformAlias;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType( final String diseaseType ) {
        this.diseaseType = diseaseType;
    }

    public List<DataFile> getDataFiles() {
        return dataFiles;
    }

    public void setDataFiles(final List<DataFile> dataFiles) {
        this.dataFiles = dataFiles;
    }
}
