/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.bamloader;

import java.util.Date;

/**
 * Bean class representing the BAM file table
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BAMFile {

    private Long fileBAMId;
    private String fileNameBAM;
    private Integer diseaseId;
    private Integer centerId;
    private Long fileSizeBAM;
    private Date dateReceived;
    private Integer datatypeBAMId;
    private Long biospecimenId;
    private String analysisId;
    private String analyteCode;
    private String libraryStrategy;
    private Date dccDateReceived;

    public Long getFileBAMId() {
        return fileBAMId;
    }

    public void setFileBAMId(Long fileBAMId) {
        this.fileBAMId = fileBAMId;
    }

    public String getFileNameBAM() {
        return fileNameBAM;
    }

    public void setFileNameBAM(String fileNameBAM) {
        this.fileNameBAM = fileNameBAM;
    }

    public Integer getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(Integer diseaseId) {
        this.diseaseId = diseaseId;
    }

    public Integer getCenterId() {
        return centerId;
    }

    public void setCenterId(Integer centerId) {
        this.centerId = centerId;
    }

    public Long getFileSizeBAM() {
        return fileSizeBAM;
    }

    public void setFileSizeBAM(Long fileSizeBAM) {
        this.fileSizeBAM = fileSizeBAM;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public Integer getDatatypeBAMId() {
        return datatypeBAMId;
    }

    public void setDatatypeBAMId(Integer datatypeBAMId) {
        this.datatypeBAMId = datatypeBAMId;
    }

    public Long getBiospecimenId() {
        return biospecimenId;
    }

    public void setBiospecimenId(Long biospecimenId) {
        this.biospecimenId = biospecimenId;
    }

    @Override
    public String toString() {
        return "BAMFile{" +
                "fileBAMId=" + fileBAMId +
                ", fileNameBAM='" + fileNameBAM + '\'' +
                ", diseaseId=" + diseaseId +
                ", centerId=" + centerId +
                ", fileSizeBAM=" + fileSizeBAM +
                ", dateReceived=" + dateReceived +
                ", datatypeBAMId=" + datatypeBAMId +
                ", biospecimenId=" + biospecimenId +
                '}';
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public String getAnalyteCode() {
        return analyteCode;
    }

    public void setAnalyteCode(String analyteCode) {
        this.analyteCode = analyteCode;
    }

    public String getLibraryStrategy() {
        return libraryStrategy;
    }

    public void setLibraryStrategy(String libraryStrategy) {
        this.libraryStrategy = libraryStrategy;
    }

    public Date getDccDateReceived() {
        return dccDateReceived;
    }

    public void setDccDateReceived(Date dccDateReceived) {
        this.dccDateReceived = dccDateReceived;
    }
}//End of Class
