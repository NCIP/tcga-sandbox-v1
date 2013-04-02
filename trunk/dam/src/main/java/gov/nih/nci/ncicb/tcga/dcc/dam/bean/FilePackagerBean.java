package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Bean to hold file packager details
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FilePackagerBean implements Serializable {

    private static final long serialVersionUID = 2896136258122743912L;

    private boolean flatten;
    private String archiveLogicalName;
    private String archivePhysicalName;
    private UUID key;
    private long estimatedUncompressedSize;
    private long priorityAdjustedEstimatedUncompressedSize;
    private List<DataFile> selectedFiles;
    private Exception exception;
    private long creationTime;
    private String disease;
    private Date jobWSSubmissionDate;
    private FilterRequestI filterRequest;
    private QuartzJobStatus status;
    private String archivePhysicalPathPrefix;
    private String archiveLinkSite;
    private String email;

    /**
     * A lightweight bean that will store just enough information from this class
     * to be able to build the response for the Web Service user.
     */
    private QuartzJobHistory quartzJobHistory;
    private String statusCheckUrl;

    public FilePackagerBean() {
        init();
    }

    public void init() {
        setCreationTime(System.currentTimeMillis());
        status = QuartzJobStatus.Queued;
    }

    /**
     * Create and return a lightweight bean that will store just enough information from this class
     * and set this class <code>quartzJobHistory</code> property to be able to build the response
     * for the Web Service user
     *
     * @param jobDetail the <code>JobDetail</code> the Quartz job associated to this <code>FilePackagerBean</code>
     * @param trigger the <code>Trigger</code> the Quartz trigger associated to this <code>FilePackagerBean</code>
     *
     * @return a lightweight bean that will store just enough information from this class
     */
    public QuartzJobHistory createQuartzJobHistory(final JobDetail jobDetail, final Trigger trigger) {

        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory(
                jobDetail.getName(),
                jobDetail.getGroup(),
                trigger.getStartTime(),
                getStatus(),
                new Date(),
                getLinkText(),
                getEstimatedUncompressedSize(),
                getJobWSSubmissionDate()
        );

        setQuartzJobHistory(quartzJobHistory);

        return quartzJobHistory;
    }

    /**
     * Return the lightweight version of this FilePackagerBean as a <code>QuartzJobHistory</code>
     *
     * @return the lightweight version of this FilePackagerBean as a <code>QuartzJobHistory</code>
     */
    public QuartzJobHistory getUpdatedQuartzJobHistory() {

        //Update QuartzJobHistory
        this.quartzJobHistory.setStatus(this.getStatus());
        this.quartzJobHistory.setLinkText(this.getLinkText());
        this.quartzJobHistory.setEstimatedUncompressedSize(this.getEstimatedUncompressedSize());
        this.quartzJobHistory.setJobWSSubmissionDate(this.getJobWSSubmissionDate());
        this.quartzJobHistory.setLastUpdated(new Date());

        return quartzJobHistory;
    }

    public QuartzJobStatus getStatus() {
        return status;
    }

    public void setStatus(QuartzJobStatus status) {
        this.status = status;
    }

    //get Job submissiotn date fro the DAM Web Service

    public Date getJobWSSubmissionDate() {
        return jobWSSubmissionDate;
    }

    /**
     * Set Job submission data for DAM Webservice
     *
     * @param jobWSSubmissionDate the job submission <code>Date</code>
     */
    public void setJobWSSubmissionDate(final Date jobWSSubmissionDate) {
        this.jobWSSubmissionDate = jobWSSubmissionDate;
    }

    public void setArchivePhysicalPathPrefix(final String archivePhysicalPathPrefix) {
        this.archivePhysicalPathPrefix = archivePhysicalPathPrefix;
    }

    public void setArchiveLinkSite(final String archiveLinkSite) {
        this.archiveLinkSite = archiveLinkSite;
    }

    public String getArchivePhysicalPathPrefix() {
        return archivePhysicalPathPrefix;
    }

    public String getArchiveLinkSite() {
        return archiveLinkSite;
    }


    public long getEstimatedUncompressedSize() {
        return estimatedUncompressedSize;
    }

    // used only in unit test case

    public void setEstimatedUncompressedSize(final long estimatedUncompressedSize) {
        this.estimatedUncompressedSize = estimatedUncompressedSize;
    }

    /**
     * used by enqueuer to set priorities for queue. Cache files are not included in this number,
     * so it's more likely to be put in the "fast lane"
     *
     * @return the get priority adjusted estimated uncompressed size
     */
    public long getPriorityAdjustedEstimatedUncompressedSize() {
        return priorityAdjustedEstimatedUncompressedSize;
    }

    public void setPriorityAdjustedEstimatedUncompressedSize(long priorityAdjustedEstimatedUncompressedSize) {
        this.priorityAdjustedEstimatedUncompressedSize = priorityAdjustedEstimatedUncompressedSize;
    }

    /**
     * @param selectedFileInfo List of files to be included in the archive.
     */
    public void setSelectedFiles(final List<DataFile> selectedFileInfo) {
        this.selectedFiles = selectedFileInfo;

        //for cache files, make sure the file exists; if not, we will generate it for this archive
        for (final DataFile df : selectedFiles) {
            df.decideWhetherToGenerateCacheFile();
        }

        calculateEstimatedUncompressedSize();
    }

    public List<DataFile> getSelectedFiles() {
        return selectedFiles;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(final String disease) {
        this.disease = disease;
    }

    /**
     * @return returns file URL to be downloaded
     */
    public String getLinkText() {
        String ret = null;

        if (isDone()) {
            if (getArchiveLinkSite() != null && getArchiveLinkSite().length() > 0) {
                ret = getArchiveLinkSite();
            } else {
                ret = "";
            }
            ret += (getArchiveLogicalName() + ".tar.gz");
        } else if (isFailed()) {
            String msg = "";
            if (getException() != null && (msg = getException().getMessage()) == null) {
                msg = getException().getClass().toString();
            }
            ret = "Error: " + msg;
        }

        return ret;
    }


    private void calculateEstimatedUncompressedSize() {
        long total = 0;
        long total2 = 0; //priority-adjusted
        if (selectedFiles != null) {
            for (final DataFile df : selectedFiles) {
                total += df.getSize();
                if (df.isPermanentFile()) {
                //permanent file - don't add size so the queue is determined by the size to generate from DB
                } else {
                    total2 += df.getSize();
                }
            }
        }
        estimatedUncompressedSize = total;
        priorityAdjustedEstimatedUncompressedSize = total2;
    }

    /**
     * @return Relative path to the archive, used to constuct a web link to the file.
     */
    public String getArchiveLogicalName() {
        return archiveLogicalName;
    }

    public String getArchivePhysicalName() {
        return archivePhysicalName;
    }

    public void setArchiveLogicalName(String archiveLogicalName) {
        this.archiveLogicalName = archiveLogicalName;
    }

    public void setArchivePhysicalName(String archivePhysicalName) {
        this.archivePhysicalName = archivePhysicalName;
    }

    /**
     * @return true when the archive has been written or when an error has occurred.
     */
    public boolean isDone() {
        return (getStatus() == QuartzJobStatus.Succeeded);
    }

    /**
     * @return true if execution failed for any reason.
     */
    public boolean isFailed() {
        return (getStatus() == QuartzJobStatus.Failed);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFilterRequest(final FilterRequestI filterRequest) {
        this.filterRequest = filterRequest;
    }

    public FilterRequestI getFilterRequest() {
        return filterRequest;
    }

    public boolean isFlatten() {
        return flatten;
    }

    public void setFlatten(boolean flatten) {
        this.flatten = flatten;
    }

    public UUID getKey() {
        return key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }


    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public QuartzJobHistory getQuartzJobHistory() {
        return quartzJobHistory;
    }

    public void setQuartzJobHistory(final QuartzJobHistory quartzJobHistory) {
        this.quartzJobHistory = quartzJobHistory;
    }


    public String getStatusCheckUrl() {
        return statusCheckUrl;
    }

    public void setStatusCheckUrl(final String statusCheckUrl) {
        this.statusCheckUrl = statusCheckUrl;
    }
}
