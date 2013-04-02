package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util;

/**
 * Provide glue APIs to call restful webservices
 *
 * @author Deepak Srinivasan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface RestfulWebserviceClient {
    /**
     * Given the necessary parameters to use in executing a restful webservice,
     * executes a GET and returns the results
     * @param config
     * @return
     */
    public WebserviceOutput executeGet(final WebserviceInput config);
}
