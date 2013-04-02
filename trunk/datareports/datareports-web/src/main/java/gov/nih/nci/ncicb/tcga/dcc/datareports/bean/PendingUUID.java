/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

import java.util.Date;

/**
 * Class used to represent pending barcode entity
 *
 * @author Stan Girshik Last updated by: $Author: $
 * @version $Rev: $
 */

public class PendingUUID {
    private String bcr;
    private String center;
    private Date shippedDate;
    private String plateId;
    private String batchNumber;
    private String plateCoordinate;
    private Date dccReceivedDate;
    private String uuid;
    private String bcrAliquotBarcode;
    private String sampleType;
    private String analyteType;
    private String portionNumber;
    private String vialNumber;
    private String itemType;

    /**
     * Return whether the UUID has been received by the DCC.
     *
     * @return <code>true</code> if the UUID has been received by the DCC, <code>false</code> otherwise
     */
    public boolean isReceivedByDcc() {
        return getDccReceivedDate() != null;
    }

    public String getBcr() {
        return bcr;
    }

    public void setBcr(String bcr) {
        this.bcr = bcr;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getPlateId() {
        return plateId;
    }

    public void setPlateId(String plateId) {
        this.plateId = plateId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getPlateCoordinate() {
        return plateCoordinate;
    }

    public void setPlateCoordinate(String plateCoordinate) {
        this.plateCoordinate = plateCoordinate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBcrAliquotBarcode() {
        return bcrAliquotBarcode;
    }

    public void setBcrAliquotBarcode(String bcrAliquotBarcode) {
        this.bcrAliquotBarcode = bcrAliquotBarcode;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public String getAnalyteType() {
        return analyteType;
    }

    public void setAnalyteType(String analyteType) {
        this.analyteType = analyteType;
    }

    public String getPortionNumber() {
        return portionNumber;
    }

    public void setPortionNumber(String portionNumber) {
        this.portionNumber = portionNumber;
    }

    public String getVialNumber() {
        return vialNumber;
    }

    public void setVialNumber(String vialNumber) {
        this.vialNumber = vialNumber;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Date getDccReceivedDate() {
        return dccReceivedDate;
    }

    public void setDccReceivedDate(final Date dccReceivedDate) {
        this.dccReceivedDate = dccReceivedDate;
    }
}
