package gov.nih.nci.ncicb.tcga.dcc.common.bean;

/**
 * Class which holds filetoarchive details
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileToArchive {
    private Long fileArchiveId;
    private Long archiveId;
    private String fileLocationURL;
    private FileInfo fileInfo;

    public FileToArchive() {
        fileInfo = new FileInfo();
    }

    public Long getFileId() {
        return fileInfo.getId();
    }

    public void setFileId(Long fileId) {
        fileInfo.setId(fileId);
    }

    public Long getFileArchiveId() {
        return fileArchiveId;
    }

    public void setFileArchiveId(Long fileArchiveId) {
        this.fileArchiveId = fileArchiveId;
    }

    public Long getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(Long archiveId) {
        this.archiveId = archiveId;
    }

    public String getFileLocationURL() {
        return fileLocationURL;
    }

    public void setFileLocationURL(String fileLocationURL) {
        this.fileLocationURL = fileLocationURL;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FileToArchive fileToArchive = (FileToArchive) o;

        if (getFileId() != null ? !getFileId().equals(fileToArchive.getFileId())
                : fileToArchive.getFileId() != null) {
            return false;
        }
        if (fileLocationURL != null ? !fileLocationURL.equals(fileToArchive.getFileLocationURL())
                : fileToArchive.getFileLocationURL() != null) {
            return false;
        }
        if (fileArchiveId != null ? !fileArchiveId.equals(fileToArchive.getFileArchiveId())
                : fileToArchive.getFileArchiveId() != null) {
            return false;
        }
        if (archiveId != null ? !archiveId.equals(fileToArchive.getArchiveId())
                : fileToArchive.getArchiveId() != null) {
            return false;
        }

        return true;
    }

}
