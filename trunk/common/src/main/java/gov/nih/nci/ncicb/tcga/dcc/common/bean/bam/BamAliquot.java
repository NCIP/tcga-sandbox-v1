package gov.nih.nci.ncicb.tcga.dcc.common.bean.bam;

/**
 * Bam aliquot bean.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BamAliquot {

    private Long aliquotId;
    private String uuid;

    public Long getAliquotId() {
        return aliquotId;
    }

    public void setAliquotId(Long aliquotId) {
        this.aliquotId = aliquotId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}//End of Class
