package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import java.io.Serializable;

/**
 * Bean to store Archive deletion details
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveDeletionBean implements Serializable {
    private static final long serialVersionUID = -7006719010834595867L;
    private String archiveName;

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }
}
