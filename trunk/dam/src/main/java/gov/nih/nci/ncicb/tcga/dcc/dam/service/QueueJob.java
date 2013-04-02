package gov.nih.nci.ncicb.tcga.dcc.dam.service;

/**
 * The interface to be implemented by classes which gets invoked by scheduler
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface QueueJob<J> {

    /**
     * Run the given job
     *
     * @param job the job to run
     * @throws Exception
     */
    public void run(final J job) throws Exception;
}
