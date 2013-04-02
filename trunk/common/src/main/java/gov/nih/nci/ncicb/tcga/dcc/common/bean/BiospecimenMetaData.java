/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * UUID bean for use with the UUID browser
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "tcgaElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class BiospecimenMetaData {
    @XmlAttribute(name = "href")
    @NotNull
    private String uuid;
    private String parentUUID;
    @NotNull
    private String uuidType;
    private Boolean redacted;
    @NotNull
    private String disease;
    private String barcode;
    @NotNull
    private String tissueSourceSite;
    @NotNull
    private String participantId;
    private String sampleType;
    private String analyteType;
    @NotNull
    private String bcr;
    @NotNull
    private String batch;
    private String centerCode;
    private String receivingCenter;
    private String platform;
    private String plateId;
    private String vialId;
    private String portionId;
    private String slide;
    private String slideLayer;
    private Date updateDate;
    private Date createDate;
    private Date shippedDate;
    private Boolean shipped;

    /**
     * Default constructor
     */
    public BiospecimenMetaData() {
    }

    /**
     * Constructor that creates an instance of a {@link BiospecimenMetaData} object with a specific
     * UUID string.
     *
     * @param uuid - a string representing a UUID
     */
    public BiospecimenMetaData(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Simple static factory method for copying instances of {@link BiospecimenMetaData}.
     *
     * @param source - an instance of {@link BiospecimenMetaData} to copy
     */
    public static BiospecimenMetaData newInstance(final BiospecimenMetaData source) {

        final BiospecimenMetaData copy = new BiospecimenMetaData();

        if (source != null) {
            PropertyDescriptor sourceDescriptor;
            PropertyDescriptor copyDescriptor;
            final Field[] fields = BiospecimenMetaData.class.getDeclaredFields();
            for (final Field field : fields) {
                try {
                    sourceDescriptor = new PropertyDescriptor(field.getName(), BiospecimenMetaData.class);
                    copyDescriptor = new PropertyDescriptor(field.getName(), BiospecimenMetaData.class);
                    copyDescriptor.getWriteMethod().invoke(copy, sourceDescriptor.getReadMethod().invoke(source));
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        return copy;
    }

    /**
     * Overridden {@link Object#toString()} method that returns a human-readable
     * XML representation of a {@link BiospecimenMetaData} instance and its contents.
     */
    @Override
    public String toString() {

        JAXBContext context;
        Marshaller marshaller;
        final StringWriter stringWriter = new StringWriter();

        try {
            context = JAXBContext.newInstance(this.getClass());
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(this, stringWriter);
        } catch (JAXBException je) {
            throw new RuntimeException("Marshalling bean instance failed. Reason: " + je.getMessage());
        }

        return stringWriter.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (!(obj instanceof BiospecimenMetaData))) {
            return false;
        }
        BiospecimenMetaData biospecimenMetaData = (BiospecimenMetaData) obj;
        if (this == biospecimenMetaData) {
            return true;
        }

        if (!((this.getUuidType() == null) ? (biospecimenMetaData.getUuidType() == null) :
                (biospecimenMetaData.getUuidType() != null) ? (this.getUuidType().equals(biospecimenMetaData.getUuidType())) : false)) {
            return false;
        }

        if (!((this.getDisease() == null) ? (biospecimenMetaData.getDisease() == null) :
                (biospecimenMetaData.getDisease() != null) ? (this.getDisease().equals(biospecimenMetaData.getDisease())) : false)) {
            return false;
        }

        if (!((this.getParticipantId() == null) ? (biospecimenMetaData.getParticipantId() == null) :
                (biospecimenMetaData.getParticipantId() != null) ? (this.getParticipantId().equals(biospecimenMetaData.getParticipantId())) : false)) {
            return false;
        }

        if (!((this.getBarcode() == null) ? (biospecimenMetaData.getBarcode() == null) :
                (biospecimenMetaData.getBarcode() != null) ? (this.getBarcode().equals(biospecimenMetaData.getBarcode())) : false)) {
            return false;
        }
        if (!((this.getTissueSourceSite() == null) ? (biospecimenMetaData.getTissueSourceSite() == null) :
                (biospecimenMetaData.getTissueSourceSite() != null) ? (this.getTissueSourceSite().equals(biospecimenMetaData.getTissueSourceSite())) : false)) {
            return false;
        }

        if (!((this.getSampleType() == null) ? (biospecimenMetaData.getSampleType() == null) :
                (biospecimenMetaData.getSampleType() != null) ? (this.getSampleType().equals(biospecimenMetaData.getSampleType())) : false)) {
            return false;
        }

        if (!((this.getVialId() == null) ? (biospecimenMetaData.getVialId() == null) :
                (biospecimenMetaData.getVialId() != null) ? (this.getVialId().equals(biospecimenMetaData.getVialId())) : false)) {
            return false;
        }
        if (!((this.getPortionId() == null) ? (biospecimenMetaData.getPortionId() == null) :
                (biospecimenMetaData.getPortionId() != null) ? (this.getPortionId().equals(biospecimenMetaData.getPortionId())) : false)) {
            return false;
        }

        if (!((this.getPlateId() == null) ? (biospecimenMetaData.getPlateId() == null) :
                (biospecimenMetaData.getPlateId() != null) ? (this.getPlateId().equals(biospecimenMetaData.getPlateId())) : false)) {
            return false;
        }

        if (!((this.getCenterCode() == null) ? (biospecimenMetaData.getCenterCode() == null) :
                (biospecimenMetaData.getCenterCode() != null) ? (this.getCenterCode().equals(biospecimenMetaData.getCenterCode())) : false)) {
            return false;
        }

        if (!((this.getAnalyteType() == null) ? (biospecimenMetaData.getAnalyteType() == null) :
                (biospecimenMetaData.getAnalyteType() != null) ? (this.getAnalyteType().equals(biospecimenMetaData.getAnalyteType())) : false)) {
            return false;
        }

        if (!((this.getSlide() == null) ? (biospecimenMetaData.getSlide() == null) :
                (biospecimenMetaData.getSlide() != null) ? (this.getSlide().equals(biospecimenMetaData.getSlide())) : false)) {
            return false;
        }

        if (!((this.getSlideLayer() == null) ? (biospecimenMetaData.getSlideLayer() == null) :
                (biospecimenMetaData.getSlideLayer() != null) ? (this.getSlideLayer().equals(biospecimenMetaData.getSlideLayer())) : false)) {
            return false;
        }
        return true;
    }


    @Override
    public int hashCode() {
        final StringBuilder hashString = new StringBuilder();
        hashString.append((getUuidType() != null) ? getUuidType().hashCode() : "")
                .append((getDisease() != null) ? getDisease().hashCode() : "")
                .append((getBarcode() != null) ? getBarcode().hashCode() : "")
                .append((getTissueSourceSite() != null) ? getTissueSourceSite().hashCode() : "")
                .append((getParticipantId() != null) ? getParticipantId().hashCode() : "")
                .append((getSampleType() != null) ? getSampleType().hashCode() : "")
                .append((getAnalyteType() != null) ? getAnalyteType().hashCode() : "")
                .append((getCenterCode() != null) ? getCenterCode().hashCode() : "")
                .append((getPlateId() != null) ? getPlateId().hashCode() : "")
                .append((getVialId() != null) ? getVialId().hashCode() : "")
                .append((getPortionId() != null) ? getPortionId().hashCode() : "")
                .append((getSlide() != null) ? getSlide().hashCode() : "")
                .append((getSlideLayer() != null) ? getSlideLayer().hashCode() : "");

        return hashString.toString().hashCode();
    }

    public String getMetaDataString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("[");
        if (getBarcode() != null) {
            sb.append("Barcode: '")
                    .append(getBarcode())
                    .append("'");
        }
        if (getTissueSourceSite() != null) {
            sb.append(", TSS: '")
                    .append(getTissueSourceSite())
                    .append("'");
        }
        if (getParticipantId() != null) {
            sb.append(", Participant Id: '")
                    .append(getParticipantId())
                    .append("'");
        }
        if (getSampleType() != null) {
            sb.append(", Sample Type: '")
                    .append(getSampleType())
                    .append("'");
        }
        if (getVialId() != null) {
            sb.append(", Vial Number: '")
                    .append(getVialId());
        }
        if (getPortionId() != null) {
            sb.append("', Portion Id: '")
                    .append(getPortionId())
                    .append("'");
        }
        if (getAnalyteType() != null) {
            sb.append(", Analyte Type Id: '")
                    .append(getAnalyteType())
                    .append("'");
        }
        if (getPlateId() != null) {
            sb.append(", Plate Id: '")
                    .append(getPlateId())
                    .append("'");
        }
        if (getCenterCode() != null) {
            sb.append(", Center Code: '")
                    .append(getCenterCode())
                    .append("'");
        }
        if (getSlide() != null) {
            sb.append(", Slide: '")
                    .append(getSlide())
                    .append("'");
        }
        if (getSlideLayer() != null) {
            sb.append(", Section Location: '")
                    .append(getSlideLayer())
                    .append("'");
        }
        sb.append("]");
        return sb.toString();


    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getParentUUID() {
        return parentUUID;
    }

    public void setParentUUID(String parentUUID) {
        this.parentUUID = parentUUID;
    }

    public String getUuidType() {
        return uuidType;
    }

    public void setUuidType(String uuidType) {
        this.uuidType = uuidType;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getTissueSourceSite() {
        return tissueSourceSite;
    }

    public void setTissueSourceSite(String tissueSourceSite) {
        this.tissueSourceSite = tissueSourceSite;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
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

    public String getReceivingCenter() {
        return receivingCenter;
    }

    public void setReceivingCenter(String receivingCenter) {
        this.receivingCenter = receivingCenter;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlateId() {
        return plateId;
    }

    public void setPlateId(String plateId) {
        this.plateId = plateId;
    }

    public String getVialId() {
        return vialId;
    }

    public void setVialId(String vialId) {
        this.vialId = vialId;
    }

    public String getPortionId() {
        return portionId;
    }

    public void setPortionId(String portionId) {
        this.portionId = portionId;
    }

    public String getSlide() {
        return slide;
    }

    public void setSlide(String slide) {
        this.slide = slide;
    }

    public String getSlideLayer() {
        return slideLayer;
    }

    public void setSlideLayer(String slideLayer) {
        this.slideLayer = slideLayer;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getBcr() {
        return bcr;
    }

    public void setBcr(String bcr) {
        this.bcr = bcr;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Boolean getRedacted() {
        return redacted;
    }

    public void setRedacted(final Boolean redacted) {
        this.redacted = redacted;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Boolean getShipped() {
        return shipped;
    }

    public void setShipped(Boolean shipped) {
        this.shipped = shipped;
    }

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

}//End of Class
