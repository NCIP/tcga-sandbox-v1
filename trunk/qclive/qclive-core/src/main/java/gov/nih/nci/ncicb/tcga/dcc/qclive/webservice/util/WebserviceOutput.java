package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util;

import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.BarcodeListWS;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResults;

/**
 * Model used as input to the RestfulWebserviceClientImpl
 * Encapsulates commonly required parameters to execute a call to a restful webservice
 * via the Jersey API
 *
 * @author Deepak Srinivasan
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class WebserviceOutput {

    private int status;
    private BarcodeListWS barcodeListWS;
    private ValidationResults validationResults;

    public WebserviceOutput(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public BarcodeListWS getBarcodeListWS() {
        return barcodeListWS;
    }

    public void setBarcodeListWS(BarcodeListWS barcodeListWS) {
        this.barcodeListWS = barcodeListWS;
    }

    public ValidationResults getValidationResults() {
        return validationResults;
    }

    public void setValidationResults(final ValidationResults validationResults) {
        this.validationResults = validationResults;
    }
}
