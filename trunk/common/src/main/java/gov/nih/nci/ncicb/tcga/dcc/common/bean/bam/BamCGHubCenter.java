package gov.nih.nci.ncicb.tcga.dcc.common.bean.bam;

/**
 * Bam CGHub center bean.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BamCGHubCenter {

    private String cGHubCenter;
    private Long centerId;

    public String getCGHubCenter() {
        return cGHubCenter;
    }

    public void setCGHubCenter(String cGHubCenter) {
        this.cGHubCenter = cGHubCenter;
    }

    public Long getCenterId() {
        return centerId;
    }

    public void setCenterId(Long centerId) {
        this.centerId = centerId;
    }
}//End of Class
