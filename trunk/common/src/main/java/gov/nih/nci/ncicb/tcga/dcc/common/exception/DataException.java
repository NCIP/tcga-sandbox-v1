package gov.nih.nci.ncicb.tcga.dcc.common.exception;

/**
 * Exception class for data generation errors
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataException extends Exception {
    public DataException(final String message) {
        super(message);
    }

    public DataException(final String message, final Throwable cause) {
        super(message, cause);

    }
}
