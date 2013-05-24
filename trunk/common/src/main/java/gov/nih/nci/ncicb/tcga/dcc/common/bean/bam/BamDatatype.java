package gov.nih.nci.ncicb.tcga.dcc.common.bean.bam;

/**
 * Bam datatype bean.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BamDatatype {

    private Long bamDatatypeId;
    private String bamDatatype;
    private String generalDatatype;

    public Long getBamDatatypeId() {
        return bamDatatypeId;
    }

    public void setBamDatatypeId(Long bamDatatypeId) {
        this.bamDatatypeId = bamDatatypeId;
    }

    public String getBamDatatype() {
        return bamDatatype;
    }

    public void setBamDatatype(String bamDatatype) {
        this.bamDatatype = bamDatatype;
    }

    public String getGeneralDatatype() {
        return generalDatatype;
    }

    public void setGeneralDatatype(String generalDatatype) {
        this.generalDatatype = generalDatatype;
    }
}//End of class
