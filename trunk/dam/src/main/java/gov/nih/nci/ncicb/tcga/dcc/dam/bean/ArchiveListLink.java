package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import java.util.Date;

/**
 * Bean holding a URL and name for browsing archive information.
 *
 * @see ArchiveListInfo
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveListLink {
    private String url;
    private String displayName;
    private Long fileSizeInBytes;
    private Date deployDate;

    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL for this link.  May be relative or absolute.
     *
     * @param url the url string
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public Long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public void setFileSizeInBytes(final Long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public Date getDeployDate() {
        return deployDate;
    }

    public void setDeployDate(final Date deployDate) {
        this.deployDate = deployDate;
    }
}
