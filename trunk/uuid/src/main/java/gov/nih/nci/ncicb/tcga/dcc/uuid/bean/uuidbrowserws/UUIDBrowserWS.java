/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean class to represent a fuller uuid browser for the complex view web service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "tcgaElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class UUIDBrowserWS {

    private String uuid;
    private List<BarcodeUUIDWS> barcodes;
    private String elementType;
    private Boolean redacted;
    private String lastUpdate;
    private DiseaseUUIDWS disease;
    private String batch;
    private TSSUUIDWS tss;
    private BCRUUIDWS bcr;
    private ParticipantUUIDWS participant;
    private DrugUUIDWS drug;
    private ExaminationUUIDWS examination;
    private SurgeryUUIDWS surgery;
    private RadiationUUIDWS radiation;
    private List<SampleUUIDWS> sample;
    private List<PortionUUIDWS> portion;
    private List<ShippedPortionUUIDWS> shippedPortion;
    private List<AnalyteUUIDWS> analyte;
    private List<SlideUUIDWS> slide;
    private List<AliquotUUIDWS> aliquot;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public DiseaseUUIDWS getDisease() {
        return disease;
    }

    public void setDisease(DiseaseUUIDWS disease) {
        this.disease = disease;
    }

    public List<BarcodeUUIDWS> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<BarcodeUUIDWS> barcodes) {
        this.barcodes = barcodes;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public TSSUUIDWS getTss() {
        return tss;
    }

    public void setTss(TSSUUIDWS tss) {
        this.tss = tss;
    }

    public BCRUUIDWS getBcr() {
        return bcr;
    }

    public void setBcr(BCRUUIDWS bcr) {
        this.bcr = bcr;
    }

    public ParticipantUUIDWS getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantUUIDWS participant) {
        this.participant = participant;
    }

    public DrugUUIDWS getDrug() {
        return drug;
    }

    public void setDrug(DrugUUIDWS drug) {
        this.drug = drug;
    }

    public ExaminationUUIDWS getExamination() {
        return examination;
    }

    public void setExamination(ExaminationUUIDWS examination) {
        this.examination = examination;
    }

    public SurgeryUUIDWS getSurgery() {
        return surgery;
    }

    public void setSurgery(SurgeryUUIDWS surgery) {
        this.surgery = surgery;
    }

    public RadiationUUIDWS getRadiation() {
        return radiation;
    }

    public void setRadiation(RadiationUUIDWS radiation) {
        this.radiation = radiation;
    }

    public List<SampleUUIDWS> getSample() {
        return sample;
    }

    public void setSample(List<SampleUUIDWS> sample) {
        this.sample = sample;
    }

    public List<PortionUUIDWS> getPortion() {
        return portion;
    }

    public void setPortion(List<PortionUUIDWS> portion) {
        this.portion = portion;
    }

    public List<AnalyteUUIDWS> getAnalyte() {
        return analyte;
    }

    public void setAnalyte(List<AnalyteUUIDWS> analyte) {
        this.analyte = analyte;
    }

    public List<SlideUUIDWS> getSlide() {
        return slide;
    }

    public void setSlide(List<SlideUUIDWS> slide) {
        this.slide = slide;
    }

    public List<AliquotUUIDWS> getAliquot() {
        return aliquot;
    }

    public void setAliquot(List<AliquotUUIDWS> aliquot) {
        this.aliquot = aliquot;
    }

    public List<ShippedPortionUUIDWS> getShippedPortion() {
        return shippedPortion;
    }

    public void setShippedPortion(final List<ShippedPortionUUIDWS> shippedPortion) {
        this.shippedPortion = shippedPortion;
    }

    public Boolean getRedacted() {
        return redacted;
    }

    public void setRedacted(final Boolean redacted) {
        this.redacted = redacted;
    }
}//End of Class
