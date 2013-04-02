package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BatchNumberAssignment;

import java.util.List;

/**
 * Interface for BatchNumberQueries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface BatchNumberQueries {

    /**
     * Gets the batch number assignment(s) for the given batch number.
     *
     * @param batchId the batch number
     * @return a list of BatchNumberAssignment objects for that batch, or null if batch not found
     */
    public List<BatchNumberAssignment> getBatchNumberAssignment(Integer batchId);

    /**
     * Checks if the combination of batch number, disease abbreviation, and center domain is valid.
     *
     * @param batchNumber the batch number
     * @param diseaseAbbreviation the disease abbreviation
     * @param centerDomain the center domain name
     * @return true if the batch is assigned to that center and disease, false if not
     */
    public boolean isValidBatchNumberAssignment(Integer batchNumber, String diseaseAbbreviation, String centerDomain);
}
