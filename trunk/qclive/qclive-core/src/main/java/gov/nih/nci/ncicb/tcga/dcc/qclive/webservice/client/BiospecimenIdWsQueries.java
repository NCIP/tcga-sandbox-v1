package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client;

import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl.WebServiceException;

import java.util.List;

/**
 * @author Deepak Srinivasan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface BiospecimenIdWsQueries {
    /**
     * Given a valid barcode, determines whether the barcode exists in the database
     * @param barcode
     * @return <code>boolean</code> <code>true</code> if the barcode exists in the database,
     * <code>false</code> otherwise
     */
    boolean exists(final String barcode);

    /**
     * Given a list of valid barcodes, determines whether the barcodes exists in the database
     * @param barcodes
     * @return <code>List<String></code> list of barcodes that doesn't exist in the database
     */

    List<String> exists(final List<String> barcodes) throws WebServiceException;

     /**
     * sets up the base url to hit for the biospecimen metadata web service
     * @param baseBiospecimenJsonWs
     */
    public void setBaseBiospecimenJsonWs(final String baseBiospecimenJsonWs);
}
