package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DAMJobStatus;

/**
 * Interface for service object that gets job status information.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface DAMJobStatusService {

    /**
     * Gets the job status information for the given job key.  If the job key is invalid, an object will still be
     * returned, but with details indicating the status is Unknown.
     *
     * @param jobKey the key for looking up the job
     * @return the job status bean
     */
    public DAMJobStatus getJobStatusForJobKey(String jobKey);

}
