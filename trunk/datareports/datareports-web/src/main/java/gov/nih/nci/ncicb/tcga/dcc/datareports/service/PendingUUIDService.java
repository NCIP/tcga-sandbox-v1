package gov.nih.nci.ncicb.tcga.dcc.datareports.service;


import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import org.springframework.security.access.annotation.Secured;

import java.util.List;

/**
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface PendingUUIDService {


    /**
     * Parses and validates the incoming pending UUID message
     * Parses the incoming message into JSON ( checking whether the message is in valid JSON)
     * Validates the incoming message according to pending UUID validation rules.
     * If the validation fails , call getErrors to get a list of errors associated with the validation
     *
     * @param incomingPendingUUIDMsg pending UUID in JSON format
     * @return true if JSON message is valid, false otherwise
     */
    public boolean parseAndValidatePendingUUIDJson(final String incomingPendingUUIDMsg);


    /**
     * parse and convert a valid json message into a list of PendingUUID domain bean
     *
     * @param incomingJson
     * @return list of PendingUUID
     */
    public List<PendingUUID> getPendingUUIDsFromJson(final String incomingJson);


    /**
     * persist the validated list of PendingUUIDs to the database with calls to the dao
     *
     * @param pendingUUIDList
     */
    @Secured("ROLE_PENDING_METADATA_WS_USER")
    public void persistPendingUUIDs(final List<PendingUUID> pendingUUIDList);

    /**
     * Returns a list of errors if present
     *
     * @return a list of errors, empty list otherwise
     */
    public List<String> getErrors();

}
