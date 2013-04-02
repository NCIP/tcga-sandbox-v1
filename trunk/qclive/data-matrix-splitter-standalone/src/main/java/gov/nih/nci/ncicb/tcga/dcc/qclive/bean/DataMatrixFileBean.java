package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

/**
 * Bean to hold data matrix file details
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataMatrixFileBean {
    private Long archiveId;
    private Long fileId;
    private Long oldFileId;
    private Long dataTypeId;
    private Long fileSize;
    private Integer levelNumber;
    private String archiveName;
    private String archiveDeployLocation;
    private String fileName;
    private String MD5;
    private String aliquotBarcode;

    public Long getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(Long archiveId) {
        this.archiveId = archiveId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getArchiveDeployLocation() {
        return archiveDeployLocation;
    }

    public void setArchiveDeployLocation(String archiveDeployLocation) {
        this.archiveDeployLocation = archiveDeployLocation;
    }

    public Long getOldFileId() {
        return oldFileId;
    }

    public void setOldFileId(Long oldFileId) {
        this.oldFileId = oldFileId;
    }

    public Long getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(Long dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getAliquotBarcode() {
        return aliquotBarcode;
    }

    public void setAliquotBarcode(String aliquotBarcode) {
        this.aliquotBarcode = aliquotBarcode;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
