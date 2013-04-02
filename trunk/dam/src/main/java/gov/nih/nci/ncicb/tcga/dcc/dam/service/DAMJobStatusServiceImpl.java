package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DAMJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;

/**
 * Service object that uses QuartzJobHistoryService to get information about the status of a job, and then
 * constructs a bean with the appropriate status and message detail.
 *
 * @see DAMJobStatus
 * @see QuartzJobHistoryService
 * @see gov.nih.nci.ncicb.tcga.dcc.dam.web.DataMatrixStatusRequestController
 *
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMJobStatusServiceImpl implements DAMJobStatusService {

    private QuartzJobHistoryService quartzJobHistoryService;
    private FilePackagerEnqueuerI filePackagerEnqueuer;
    private String supportEmailAddress;

    /**
     * Gets the job status information for the given job key.  If the job key is invalid, an object will still be
     * returned, but with details indicating the status is Unknown.
     *
     * @param jobKey the key for looking up the job
     * @return the job status bean
     */
    @Override
    public DAMJobStatus getJobStatusForJobKey(final String jobKey) {
        QuartzJobHistory quartzJobHistory = quartzJobHistoryService.getQuartzJobHistory(jobKey, FilePackagerEnqueuer.JOB_GROUP_FILE_PACKAGER);
        if (quartzJobHistory == null) {
            quartzJobHistory = new QuartzJobHistory();
            quartzJobHistory.setStatus(QuartzJobStatus.Unknown);
        }
        final DAMJobStatus jobStatus = new DAMJobStatus();
        final QuartzJobStatus quartzJobStatus = quartzJobHistory.getStatus();

        String message = null;
        String status = quartzJobStatus.getValue();

        if (quartzJobStatus.equals(QuartzJobStatus.Queued)) {
            final Integer queuePosition = quartzJobHistoryService.getPositionInQueue(quartzJobHistory);
            if (queuePosition < 1) {
                message = "Your job is first in the queue and will begin processing soon.";
            } else if (queuePosition == 1) {
                message = "There is 1 job in the queue ahead of you.";
            } else {
                message = "There are " + queuePosition + " jobs in the queue ahead of you.";
            }
        } else if (quartzJobStatus.equals(QuartzJobStatus.Failed)) {
             message = "There was an error creating your archive: " + quartzJobHistory.getLinkText() + ". Please contact " + getSupportEmailAddress() + " if you need assistance.";
        } else if (quartzJobStatus.equals(QuartzJobStatus.Started)) {
            message = "Your job is currently being processed.";
            status = "Running";

        } else if (quartzJobStatus.equals(QuartzJobStatus.Succeeded)) {
            message = "The archive you created is available at <a href=\"" + quartzJobHistory.getLinkText() + "\">" + quartzJobHistory.getLinkText() + "</a>";
            jobStatus.setDownloadUrl(quartzJobHistory.getLinkText());
            status = "Complete";

        } else if (quartzJobStatus.equals(QuartzJobStatus.Accepted)) {
            message = "Your archive request has been submitted.";
            status = "Submitted";

        } else if (quartzJobStatus.equals(QuartzJobStatus.Unknown)) {
            status = "Not Found";
            message = "The job requested was not found. Archives are removed after " +
                    filePackagerEnqueuer.getHoursTillDeletion() +
                    " hours from the time they're finished processing. If you have reached this status in error please " +
                    "contact " + getSupportEmailAddress() + " for more information.";
        }

        jobStatus.setStatus(status);
        jobStatus.setMessage(message);

        return jobStatus;
    }

    public void setQuartzJobHistoryService(final QuartzJobHistoryService quartzJobHistoryService) {
        this.quartzJobHistoryService = quartzJobHistoryService;
    }

    public void setFilePackagerEnqueuer(final FilePackagerEnqueuerI filePackagerEnqueuer) {
        this.filePackagerEnqueuer = filePackagerEnqueuer;
    }

    public String getSupportEmailAddress() {
        return supportEmailAddress;
    }

    public void setSupportEmailAddress(final String supportEmailAddress) {
        this.supportEmailAddress = supportEmailAddress;
    }
}
