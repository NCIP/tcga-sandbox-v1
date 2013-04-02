package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import org.quartz.SchedulerException;

import java.util.Date;

/**
 * Interface for FilePackagerEnqueuer. Created for unit testing.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface FilePackagerEnqueuerI {

    /**
     * Schedule a FilePackager job with the given <code>FilePackagerBean</code>
     *
     * @param filePackagerBean the <code>FilePackagerBean</code> to run the FilePackager job with
     * @throws SchedulerException
     */
    public void queueFilePackagerJob(final FilePackagerBean filePackagerBean) throws SchedulerException;

    /**
     * Schedule a job to delete the given archive
     *
     * @param archiveName the archive name
     * @param immediate <code>true</code> if it should be scheduled immediately, <code>false</code> otherwise
     * @return the date at which the trigger will fire
     * @throws SchedulerException
     */
    public Date queueArchiveDeletionJob(final String archiveName, final boolean immediate) throws SchedulerException;

    /**
     * Schedule a job to delete the given <code>QuartzJobHistory</code>.
     * It should be scheduled at the same time the <code>FilePackagerBean</code> archive is scheduled for deletion.
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to delete
     * @param dateOfTrigger the Date at which the trigger should fire
     * @throws SchedulerException
     */
    public void queueQuartzJobHistoryDeletionJob(final QuartzJobHistory quartzJobHistory, final Date dateOfTrigger) throws SchedulerException;

    /**
     * Gets the number of hours the deletion job will be scheduled for, after the job is
     * finished processing.
     *
     * @return number of hours between end of file packager job and time deletion job is scheduled
     */
    public int getHoursTillDeletion();
}
