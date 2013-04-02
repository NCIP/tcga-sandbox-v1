package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl;

/**
 * TODO: class documentation
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class WebServiceException extends Exception{

    public WebServiceException() {
	    super();
    }


    public WebServiceException(String message) {
    	super(message);
    }

    public WebServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServiceException(Throwable cause) {
        super(cause);
    }
}