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
 * @author HickeyE
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BCRID {

    // all Strings because of the leading zeros
    private Integer id;
    private String fullID;
    private String projectName;
    private String siteID;
    private String patientID;
    private String sampleID;
    private String portionID;
    private String sampleTypeCode;
    private String sampleNumberCode;
    private String portionNumber;
    private String portionTypeCode;
    private String plateId;
    private String bcrCenterId;
    private String shippingDate;
    private long archiveId;
    private int isValid;
    private int isViewable;
    private String uuid;
    private Integer batchNumber;

    public String getUUID() {
        return uuid;
    }

    public void setUUID(final String uuid) {
        if (uuid != null) {
            this.uuid = uuid.toLowerCase();
        }
    }



    /**
     * The format of the BCRID is assumed to be like
     * TCGA.02.0006.01B.01R including the length
     * of each component.
     * <p/>
     * This class will return the codes, as seen in the
     * example above, and not the meaning of the codes.
     * Perhaps an enhancement would be a database lookup
     * of these codes in a static or singleton manner.
     * <p/>
     * See the individual get methods for the current (Oct 2007)
     * meaning of the codes.
     * <p/>
     */
    public BCRID() {
    }

    /**
     * returns the unique ID of this object from DB
     *
     * @return the ID of the BCR Barcode object
     */
    public Integer getId() {
        return id;
    }

    public void setId( final Integer id ) {
        this.id = id;
    }

    public String getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate( final String shippingDate ) {
        this.shippingDate = shippingDate;
    }

    public String getFullID() {
        return fullID;
    }

    public String getProjectName() {
        return projectName;
    }

    /*
    code:meaning
    02:MD Anderson
    03:Lung Center Tissue Bank of CALGB
    04:Gynecologic Oncology Group
    06:Henry Ford Hospital
    07:Cell Lines
     */
    public String getSiteID() {
        return siteID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getSampleID() {
        return sampleID;
    }

    public String getPortionID() {
        return portionID;
    }

    /*
    code:meaning
    01:solid tumor
    10:normal blood
    11:normal tissue
    12:buccal smear
     */
    public String getSampleTypeCode() {
        return sampleTypeCode;
    }

    /*
    code:meaning
    A:first
    B:second
    etc.
     */
    public String getSampleNumberCode() {
        return sampleNumberCode;
    }

    public String getPortionNumber() {
        return portionNumber;
    }

    /*
    code:meaning
    D:DNA
    R:RNA
     */
    public String getPortionTypeCode() {
        return portionTypeCode;
    }

    public void setFullID( final String fullID ) {
        this.fullID = fullID;
    }

    public void setProjectName( final String projectName ) {
        this.projectName = projectName;
    }

    public void setSiteID( final String siteID ) {
        this.siteID = siteID;
    }

    public void setPatientID( final String patientID ) {
        this.patientID = patientID;
    }

    public void setSampleID( final String sampleID ) {
        this.sampleID = sampleID;
    }

    public void setPortionID( final String portionID ) {
        this.portionID = portionID;
    }

    public void setSampleTypeCode( final String sampleTypeCode ) {
        this.sampleTypeCode = sampleTypeCode;
    }

    public void setSampleNumberCode( final String sampleNumberCode ) {
        this.sampleNumberCode = sampleNumberCode;
    }

    public void setPortionNumber( final String portionNumber ) {
        this.portionNumber = portionNumber;
    }

    public void setPortionTypeCode( final String portionTypeCode ) {
        this.portionTypeCode = portionTypeCode;
    }

    public String getPlateId() {
        return plateId;
    }

    public void setPlateId( final String plateId ) {
        this.plateId = plateId;
    }

    public String getBcrCenterId() {
        return bcrCenterId;
    }

    public void setBcrCenterId( final String bcrCenterId ) {
        this.bcrCenterId = bcrCenterId;
    }

    public void setArchiveId( final Long id ) {
        this.archiveId = id;
    }

    public Long getArchiveId() {
        return archiveId;
    }

    public int getValid() {
        return isValid;
    }

    public void setValid( int valid ) {
        isValid = valid;
    }

    public int getViewable() {
        return isViewable;
    }

    public void setViewable( int viewable ) {
        isViewable = viewable;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BCRID bcrID = (BCRID) o;
        if (fullID == null || bcrID.getFullID() == null) {
            // without both barcode fields set, we don't know, so just see if they are the same object
            return super.equals(o);
        } else {
            // they are equal if the barcodes are equal
            return fullID.equals(bcrID.getFullID());
        }
    }

    public int hashCode() {
        if (fullID == null) {
            return super.hashCode();
        } else {
            int result = id == null ? 0 : id;
            result = 31 * result + fullID.hashCode();
            return result;
        }
    }

    public void setBatchNumber(final Integer batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Integer getBatchNumber() {
        return batchNumber;
    }
}