/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import java.util.Date;

/**
 * Class used to represent barcodes associated with UUIDs
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class Barcode {

    private long barcodeId;
    private String barcode;
    private String uuid;
    private Tumor disease;
    private Date effectiveDate;
    private Long itemTypeId;

    public Barcode() {
    }

    public long getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(final long barcodeId) {
        this.barcodeId = barcodeId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(final String barcode) {
        this.barcode = barcode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public Tumor getDisease() {
        return disease;
    }

    public void setDisease(final Tumor disease) {
        this.disease = disease;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(final Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Long getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(Long itemTypeId) {
        this.itemTypeId = itemTypeId;
    }
}
