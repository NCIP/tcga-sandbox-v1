package gov.nih.nci.ncicb.tcga.dcc.common.generation;

/**
 * Exception class for FileGenerator
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileGeneratorException extends Exception{

    public FileGeneratorException() {
	    super();
    }


    public FileGeneratorException(String message) {
    	super(message);
    }

    public FileGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileGeneratorException(Throwable cause) {
        super(cause);
    }
}
