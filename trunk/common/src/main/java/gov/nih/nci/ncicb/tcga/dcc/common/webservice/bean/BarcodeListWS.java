package gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * bean describing a barcode list to be used in jersey web services
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "tcgaElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class BarcodeListWS implements Serializable {
    private List<BarcodeWS> barcodes;

    public List<BarcodeWS> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<BarcodeWS> barcodes) {
        this.barcodes = barcodes;
    }


}
