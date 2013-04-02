package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client;

import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResults;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl.WebServiceException;

import java.util.List;

/**
 * Interface for querying validation web service
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ValidationWebServiceQueries {

    /**
     * Validate the given list of UUIDs and return a {@link ValidationResults} object that contains the individual results.
     *
     * @param uuids the UUIDs to validate
     * @return a {@link ValidationResults} object that contains the individual results
     */
    public ValidationResults validateUUIDs(final List<String> uuids) throws WebServiceException;

    /**
     * Validate the given list of SampleUUID/SampleTCGABarcode pairs
     * and return a {@link ValidationResults} object that contains the individual results of the validation.
     *
     * @param sampleUuidAndSampleTcgaBarcodePairs a list of SampleUUID/SampleTCGABarcode pairs
     * @return a {@link ValidationResults} object that contains the individual results of the validation
     */
    public ValidationResults batchValidateSampleUuidAndSampleTcgaBarcode(final List<String[]> sampleUuidAndSampleTcgaBarcodePairs) throws WebServiceException;
}
