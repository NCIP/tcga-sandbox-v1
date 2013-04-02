package gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * bean describing a barcode to be used in jersey web services
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "barcode")
@XmlAccessorType(XmlAccessType.FIELD)
public class BarcodeWS {


    @XmlAttribute
    private String createdOn;
    @XmlAttribute
    private Boolean exists;
    private String barcode;

    public BarcodeWS() {
    }

    public BarcodeWS(String createdOn, String barcode) {
        this.createdOn = createdOn;
        this.barcode = barcode;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public Boolean isExists() {
        return exists;
    }
}
