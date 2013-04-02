package gov.nih.nci.ncicb.tcga.dcc.common.bean;

/**
 * Bean representing Archive Type.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveType {
    private Integer archiveTypeId;
    private String archiveType;
    private Integer dataLevel; // may be null

    public Integer getArchiveTypeId() {
        return archiveTypeId;
    }

    public void setArchiveTypeId(final Integer archiveTypeId) {
        this.archiveTypeId = archiveTypeId;
    }

    public String getArchiveType() {
        return archiveType;
    }

    public void setArchiveType(final String archiveType) {
        this.archiveType = archiveType;
    }

    public Integer getDataLevel() {
        return dataLevel;
    }

    public void setDataLevel(final Integer dataLevel) {
        this.dataLevel = dataLevel;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ArchiveType)) {
            return false;
        } else {
            return (archiveTypeId == null && ((ArchiveType)o).getArchiveTypeId() == null) ||
                    (archiveTypeId.equals(((ArchiveType) o).getArchiveTypeId()));
        }
    }
}
