/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

import java.util.Date;

/**
 * bean class representing a bam telemetry object
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BamTelemetry {

    private String bamFile;
    private Date dateReceived;
    private String center;
    private String disease;
    private String molecule;
    private String dataType;
    private String aliquotId;
    private String participantId;
    private String sampleId;
    private Long fileSize;
    private String aliquotUUID;

    public String getAliquotUUID() {
        return aliquotUUID;
    }

    public void setAliquotUUID(String aliquotUUID) {
        this.aliquotUUID = aliquotUUID;
    }

    public String getBamFile() {
        return bamFile;
    }

    public void setBamFile(String bamFile) {
        this.bamFile = bamFile;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getMolecule() {
        return molecule;
    }

    public void setMolecule(String molecule) {
        this.molecule = molecule;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAliquotId() {
        return aliquotId;
    }

    public void setAliquotId(String aliquotId) {
        this.aliquotId = aliquotId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }


}//End of Class
