package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

/**
 * Exception class for unsupported files
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class UnsupportedFileException extends Exception {

    public UnsupportedFileException(final String message){
        super(message);
    }

    public UnsupportedFileException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
