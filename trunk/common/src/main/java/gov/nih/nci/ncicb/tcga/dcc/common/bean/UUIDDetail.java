/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.GenerationMethod;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * Value Object for the UUID Details
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@XmlRootElement(name = "uuid-detail")
@XmlAccessorType(XmlAccessType.FIELD)
public class UUIDDetail {
    
    @XmlElement private String uuid;
    @XmlElement private Date creationDate;
    @XmlElement private GenerationMethod generationMethod;
    @XmlElement private Center center;
    @XmlElement private String createdBy;
    @XmlElement private String latestBarcode;
    @XmlElement private List<Barcode> barcodes;
    @XmlElement private String diseaseAbbrev;

    

    public UUIDDetail(){
    }

    public UUIDDetail(final String uuid, final Date creationDate, final GenerationMethod generationMethod,
                                    final Center center, final String createdBy) {
        this.uuid = uuid;
        this.creationDate = creationDate;
        this.generationMethod = generationMethod;
        this.center = center;
        this.createdBy = createdBy;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public GenerationMethod getGenerationMethod() {
        return generationMethod;
    }

    public void setGenerationMethod(final GenerationMethod generationMethod) {
        this.generationMethod = generationMethod;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(final Center center) {
        this.center = center;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLatestBarcode() {
        return latestBarcode;
    }

    public void setLatestBarcode(final String latestBarcode) {
        this.latestBarcode = latestBarcode;
    }

    public List<Barcode> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(final List<Barcode> barcodes) {
        this.barcodes = barcodes;
    }

    public String getDiseaseAbbrev() {
        return diseaseAbbrev;
    }

    public void setDiseaseAbbrev(final String diseaseAbbrev) {
        this.diseaseAbbrev = diseaseAbbrev;
    }

}
