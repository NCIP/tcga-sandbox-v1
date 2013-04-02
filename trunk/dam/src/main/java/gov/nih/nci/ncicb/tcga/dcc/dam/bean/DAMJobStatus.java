package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

/**
 * Bean holding basic status information about DAM file packager jobs.
 *
 * @see gov.nih.nci.ncicb.tcga.dcc.dam.web.DataMatrixStatusRequestController
 * @see gov.nih.nci.ncicb.tcga.dcc.dam.web.DataAccessFileProcessingController
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMJobStatus {
    private String status;
    private String message;
    private String downloadUrl;

    /**
     * Gets a string representing the status of this job.
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * Gets a string representing additional information about the job status.
     *
     * @return status detail message
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(final String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
