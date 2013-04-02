package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AssertElement;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AssertionType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean to map between uuid and barcode.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlRootElement(name = "uuidMapping")
@XmlAccessorType(XmlAccessType.FIELD)
public class UuidBarcodeMapping {
	
    @AssertElement(AssertionType.BARCODE)
    @XmlElement
    private String barcode;

    @AssertElement(AssertionType.UUID)
    @XmlElement
    private String uuid;

    public UuidBarcodeMapping() {
    }

    public UuidBarcodeMapping(String barcode, String uuid) {
        this.barcode = barcode;
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(final String barcode) {
        this.barcode = barcode;
    }
}
