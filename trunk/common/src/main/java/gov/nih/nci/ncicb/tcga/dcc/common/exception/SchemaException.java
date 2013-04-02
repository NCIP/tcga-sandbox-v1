package gov.nih.nci.ncicb.tcga.dcc.common.exception;

/**
 * Indicates that some sort of schema validation error occured.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SchemaException  extends Exception {

    public SchemaException( final String message ) {
        super( message );
    }

    public SchemaException( final String message, final Throwable cause) {
        super( message, cause );
    }

}
