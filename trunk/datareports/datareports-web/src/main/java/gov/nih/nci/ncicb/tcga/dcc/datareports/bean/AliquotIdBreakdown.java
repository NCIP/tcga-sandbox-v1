/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

/**
 * bean class defining the biospecimen breakdown report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class AliquotIdBreakdown {

    private String aliquotId;
    private String analyteId;
    private String sampleId;
    private String participantId;
    private String project;
    private String tissueSourceSite;
    private String participant;
    private String sampleType;
    private String vialId;
    private String portionId;
    private String portionAnalyte;
    private String plateId;
    private String centerId;
    private String valid;

    public String getAliquotId() {
        return aliquotId;
    }

    public void setAliquotId(final String aliquotId) {
        this.aliquotId = aliquotId;
    }

    public String getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(final String analyteId) {
        this.analyteId = analyteId;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(final String sampleId) {
        this.sampleId = sampleId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(final String participantId) {
        this.participantId = participantId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(final String project) {
        this.project = project;
    }

    public String getTissueSourceSite() {
        return tissueSourceSite;
    }

    public void setTissueSourceSite(final String tissueSourceSite) {
        this.tissueSourceSite = tissueSourceSite;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(final String participant) {
        this.participant = participant;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(final String sampleType) {
        this.sampleType = sampleType;
    }

    public String getVialId() {
        return vialId;
    }

    public void setVialId(final String vialId) {
        this.vialId = vialId;
    }

    public String getPortionId() {
        return portionId;
    }

    public void setPortionId(final String portionId) {
        this.portionId = portionId;
    }

    public String getPortionAnalyte() {
        return portionAnalyte;
    }

    public void setPortionAnalyte(final String portionAnalyte) {
        this.portionAnalyte = portionAnalyte;
    }

    public String getPlateId() {
        return plateId;
    }

    public void setPlateId(final String plateId) {
        this.plateId = plateId;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(final String centerId) {
        this.centerId = centerId;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(final String valid) {
        this.valid = valid;
    }
} //End of Class
