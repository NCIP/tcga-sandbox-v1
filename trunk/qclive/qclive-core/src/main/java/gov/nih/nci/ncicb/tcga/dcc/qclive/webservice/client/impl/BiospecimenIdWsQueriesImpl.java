package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.BarcodeListWS;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.BarcodeWS;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.BiospecimenIdWsQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.RestfulWebserviceClient;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.WebserviceInput;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.WebserviceOutput;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Deepak Srinivasan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BiospecimenIdWsQueriesImpl implements BiospecimenIdWsQueries {

    private String baseBiospecimenJsonWs;
    private RestfulWebserviceClient client;

    private int webServiceBatchSize = ConstantValues.WS_BATCH_SIZE;

    /**
     * Given a barcode, calls the biospecimen metadata webservice and determines
     * if the provided barcode exists in the database
     * @param barcode
     * @return <code>boolean</code> <code>true</code> if the provided barcode exists in the database,
     * <code>false</code> otherwise
     */
    @Override
    public boolean exists(final String barcode) {
        final String uri;
        try {
            uri = baseBiospecimenJsonWs + "/" + URLEncoder.encode(barcode, "UTF-8");

            final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_JSON_TYPE,  uri);
            final WebserviceOutput output = client.executeGet(config);
            return output.getStatus() == 200;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> exists(final List<String> barcodes) throws WebServiceException {

        final List<String> result = new ArrayList<String>();

        // Putting barcodes in a set to remove duplicates
        final Set<String> barcodesSet = new HashSet<String>();
        barcodesSet.addAll(barcodes);

        final List<String> barcodeBatchList = new LinkedList<String>();
        final Iterator<String> barcodeIterator = barcodesSet.iterator();
        String barcode;
        boolean lastBatch;

        while (barcodeIterator.hasNext()) {

            barcode = barcodeIterator.next();
            lastBatch = !barcodeIterator.hasNext();

            // Add the barcodes to the list of barcodes to batch
            barcodeBatchList.add(barcode);

            if (barcodeBatchList.size() >= getWebServiceBatchSize() || lastBatch) { // Time to run query on batch

                System.out.println("Validating " + barcodeBatchList.size() + " barcode(s) ...");

                final String uri;
                try {
                    uri = baseBiospecimenJsonWs + "/" + getDelimitedString(barcodeBatchList, ConstantValues.WS_BARCODE_DELIMITER);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_JSON_TYPE, uri);
                config.setOutputEntityClassName(BarcodeListWS.class);
                final WebserviceOutput output = client.executeGet(config);

                if (output.getStatus() == 200) {
                    final BarcodeListWS barcodeListWS = output.getBarcodeListWS();
                    if (barcodeListWS != null) {
                        for (BarcodeWS barcodeWS : barcodeListWS.getBarcodes()) {
                            if (!barcodeWS.isExists()) {
                                result.add(barcodeWS.getBarcode());
                            }
                        }
                    }
                } else {
                    throw new WebServiceException(" Error occurred while validating barcodes. Status code : " + output.getStatus());
                }

                // Clear batch
                barcodeBatchList.clear();
            }
        }

        return result;
    }

    private String getDelimitedString(final List<String> data, final String delimiter) throws UnsupportedEncodingException {
        final StringBuffer sb = new StringBuffer();
        for(final String str : data){
            sb.append(URLEncoder.encode(str, "UTF-8"))
            .append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    /**
     * Sets up the WebServiceClient to use
     * @param client
     */
    public void setClient(final RestfulWebserviceClient client) {
        this.client = client;
    }
    public RestfulWebserviceClient getClient() {
        return client;
    }

    /**
     * sets up the base url to hit for the biospecimen metadata web service
     * @param baseBiospecimenJsonWs
     */
    public void setBaseBiospecimenJsonWs(final String baseBiospecimenJsonWs) {
        this.baseBiospecimenJsonWs = baseBiospecimenJsonWs;
    }
    public String getBaseBiospecimenJsonWs() {
        return baseBiospecimenJsonWs;
    }

    public int getWebServiceBatchSize() {
        return webServiceBatchSize;
    }

    public void setWebServiceBatchSize(final int webServiceBatchSize) {
        this.webServiceBatchSize = webServiceBatchSize;
    }
}
