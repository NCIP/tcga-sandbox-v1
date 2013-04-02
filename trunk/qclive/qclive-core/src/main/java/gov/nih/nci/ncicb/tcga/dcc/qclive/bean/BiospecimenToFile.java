package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

/**
 * Bean class for BIOSPECIMEN_TO_FILE table
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BiospecimenToFile {
    private Integer biospecimenFileId;
    private Integer biospecimenId;
    private Long fileId;
    private Long oldFileId;
    private String  fileColName;


    public Integer getBiospecimenFileId() {
        return biospecimenFileId;
    }

    public void setBiospecimenFileId(final Integer biospecimenFileId) {
        this.biospecimenFileId = biospecimenFileId;
    }

    public Integer getBiospecimenId() {
        return biospecimenId;
    }

    public void setBiospecimenId(final Integer biospecimenId) {
        this.biospecimenId = biospecimenId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(final Long fileId) {
        this.fileId = fileId;
    }

    public String getFileColName() {
        return fileColName;
    }

    public void setFileColName(final String fileColName) {
        this.fileColName = fileColName;
    }

    public Long getOldFileId() {
        return oldFileId;
    }

    public void setOldFileId(Long oldFileId) {
        this.oldFileId = oldFileId;
    }
}
