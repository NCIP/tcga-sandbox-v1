package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResults;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.ValidationWebServiceQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.RestfulWebserviceClient;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.WebserviceInput;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.WebserviceOutput;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Implementation class for querying validation web service
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ValidationWebServiceQueriesImpl implements ValidationWebServiceQueries {

    private String baseValidationWebServiceURL;

    private String validateUuidsAndBarcodesJsonURL;

    private RestfulWebserviceClient restfulWebserviceClient;

    private int webServiceBatchSize = ConstantValues.UUID_WS_BATCH_SIZE;

    @Override
    public ValidationResults validateUUIDs(final List<String> uuids) throws WebServiceException {

        ValidationResults result = null;

        if(uuids != null) {

            // Putting uuids in a set to remove duplicates
            final Set<String> uuidsSet = new HashSet<String>();
            uuidsSet.addAll(uuids);

            final List<String> uuidBatchList = new LinkedList<String>();
            final Iterator<String> uuidIterator = uuidsSet.iterator();
            String uuid;
            boolean lastBatch;

            while (uuidIterator.hasNext()) {

                uuid = uuidIterator.next();
                lastBatch = !uuidIterator.hasNext();

                // Add the barcodes to the list of barcodes to batch
                try {
                    // encode in case some are invalid with spaces etc
                    uuidBatchList.add(URLEncoder.encode(uuid, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                if (uuidBatchList.size() >= getWebServiceBatchSize() || lastBatch) { // Time to run query on batch

                    System.out.println("Validating " + uuidBatchList.size() + " uuid(s) ...");

                    final String queryParameters = StringUtil.convertListToDelimitedString(uuidBatchList, ConstantValues.WS_QUERY_PARAMETERS_DELIMITER);
                    final String uri = getBaseValidationWebServiceURL() + "/" + queryParameters;
                    final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_JSON_TYPE, uri);
                    config.setOutputEntityClassName(ValidationResults.class);
                    final WebserviceOutput output = getRestfulWebserviceClient().executeGet(config);

                    if (output.getStatus() == HttpStatusCode.OK) {

                        if(result == null) {
                            result = output.getValidationResults();
                        } else if(output.getValidationResults() != null) {
                            result.addValidationResult(output.getValidationResults().getValidationResult());
                        }

                    } else {
                        throw new WebServiceException("Error occurred while validating uuid(s). Status code : " + output.getStatus());
                    }

                    // Clear batch
                    uuidBatchList.clear();
                }
            }
        }

        return result;
    }

    @Override
    public ValidationResults batchValidateSampleUuidAndSampleTcgaBarcode(List<String[]> sampleUuidAndSampleTcgaBarcodePairs) throws WebServiceException {

        ValidationResults result = null;

        final List<String> uuidBatchList = new LinkedList<String>();
        final List<String> barcodeBatchList = new LinkedList<String>();
        boolean lastBatch;

        if(sampleUuidAndSampleTcgaBarcodePairs != null && sampleUuidAndSampleTcgaBarcodePairs.size() > 0) {

            final Iterator<String[]> uuidBarcodeIterator = sampleUuidAndSampleTcgaBarcodePairs.iterator();
            while (uuidBarcodeIterator.hasNext()) {
                final String[] uuidAndBarcodePair = uuidBarcodeIterator.next();
                lastBatch = !uuidBarcodeIterator.hasNext();

                if(uuidAndBarcodePair != null && uuidAndBarcodePair.length == 2) {

                    final String uuid = uuidAndBarcodePair[0];
                    final String barcode = uuidAndBarcodePair[1];

                    try {
                        uuidBatchList.add(URLEncoder.encode(uuid, "UTF-8"));
                        barcodeBatchList.add(URLEncoder.encode(barcode, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

                    if(uuidBatchList.size() + barcodeBatchList.size() >= getWebServiceBatchSize() || lastBatch) { // Time to run query on batch

                        System.out.println("Validating " + uuidBatchList.size() + " uuid(s) and " + barcodeBatchList.size() + " barcodes ...");

                        final String uuidQueryParameter = StringUtil.convertListToDelimitedString(uuidBatchList, ConstantValues.WS_QUERY_PARAMETERS_DELIMITER);
                        final String barcodeQueryParameter = StringUtil.convertListToDelimitedString(barcodeBatchList, ConstantValues.WS_QUERY_PARAMETERS_DELIMITER);
                        final String uri = getValidateUuidsAndBarcodesJsonURL() + "?uuids=" + uuidQueryParameter + "&barcodes=" + barcodeQueryParameter;

                        final WebserviceInput config = new WebserviceInput(MediaType.APPLICATION_JSON_TYPE, uri);
                        config.setOutputEntityClassName(ValidationResults.class);
                        final WebserviceOutput output = getRestfulWebserviceClient().executeGet(config);

                        if (output.getStatus() == HttpStatusCode.OK) {

                            if(result == null) {
                                result = output.getValidationResults();
                            } else if(output.getValidationResults() != null) {
                                result.addValidationResult(output.getValidationResults().getValidationResult());
                            }

                        } else {
                            throw new WebServiceException("Error occurred while validating uuid(s) and barcode(s). Status code : " + output.getStatus());
                        }

                        // Clear batch
                        uuidBatchList.clear();
                        barcodeBatchList.clear();
                    }
                }
            }
        }

        return result;
    }

    public String getBaseValidationWebServiceURL() {
        return baseValidationWebServiceURL;
    }

    public void setBaseValidationWebServiceURL(final String baseValidationWebServiceURL) {
        this.baseValidationWebServiceURL = baseValidationWebServiceURL;
    }

    public String getValidateUuidsAndBarcodesJsonURL() {
        return validateUuidsAndBarcodesJsonURL;
    }

    public void setValidateUuidsAndBarcodesJsonURL(String validateUuidsAndBarcodesJsonURL) {
        this.validateUuidsAndBarcodesJsonURL = validateUuidsAndBarcodesJsonURL;
    }

    public RestfulWebserviceClient getRestfulWebserviceClient() {
        return restfulWebserviceClient;
    }

    public void setRestfulWebserviceClient(final RestfulWebserviceClient restfulWebserviceClient) {
        this.restfulWebserviceClient = restfulWebserviceClient;
    }

    public int getWebServiceBatchSize() {
        return webServiceBatchSize;
    }

    public void setWebServiceBatchSize(int webServiceBatchSize) {
        this.webServiceBatchSize = webServiceBatchSize;
    }
}
