package gov.nih.nci.ncicb.tcga.dcc.common.bean;

/**
 * Bean to hold batch_number_assignment table data
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BatchNumberAssignment {
    Integer batchId;
    Integer diseaseId;
    Integer centerId;
    String disease;
    String centerDomainName;

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
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

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getCenterDomainName() {
        return centerDomainName;
    }

    public void setCenterDomainName(String centerDomainName) {
        this.centerDomainName = centerDomainName;
    }
}
