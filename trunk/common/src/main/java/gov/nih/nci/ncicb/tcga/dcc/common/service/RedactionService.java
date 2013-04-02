package gov.nih.nci.ncicb.tcga.dcc.common.service;

/**
 * Service interface for redactions and recissions
 *
 * @author Shelley Alonso
 *         Last updated by: $Shelley Alonso$
 * @version $Rev$
 */

public interface RedactionService {
    /**
    * Redacts all children of a redacted item
    * @param redactedItem the barcode or uuid of the participant being redacted
     */
    public void redact(final String redactedItem);

    /**
     * Rescinds the redaction for all children of a redacted participant
     * @param rescindedItem the barcode or uuid of the participant being rescinded
     */
     public void rescind(final String rescindedItem);

}
