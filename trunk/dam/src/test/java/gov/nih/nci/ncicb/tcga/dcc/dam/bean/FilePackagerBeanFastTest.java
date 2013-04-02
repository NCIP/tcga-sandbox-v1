/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;

/**
 * FilePackagerBean unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FilePackagerBeanFastTest {

    /**
     * Fields to hold expect values
     */
    private FilePackagerBean filePackagerBean;
    private JobDetail jobDetail;
    private SimpleTrigger simpleTrigger;
    private Date triggerDate;
    private Date jobWSSubmissionDate;
    private Date quartzJobHistoryLastUpdatedDate;
    private Long estimatedUncompressedSize;
    private String archiveLinkSite;
    private String archiveLogicalName;
    private Exception exception;
    private QuartzJobStatus status;

    @Before
    public void setUp() {

        setTriggerDate(new Date(1000));
        setJobWSSubmissionDate(new Date(2000));
        setQuartzJobHistoryLastUpdatedDate(new Date(3000));
        setEstimatedUncompressedSize(4000L);
        setArchiveLinkSite("http://localhost/");
        setArchiveLogicalName("testArchive");
        setException(new Exception("Test Exception"));
        setStatus(QuartzJobStatus.Queued);

        this.filePackagerBean = getFilePackagerBeanForSetUp();
        this.jobDetail = getJobDetailForSetUp();
        this.simpleTrigger = getSimpleTriggerForSetUp();
    }

    /**
     * Return a <code>FilePackagerBean</code> for the setup
     *
     * @return a <code>FilePackagerBean</code> for the setup
     */
    private FilePackagerBean getFilePackagerBeanForSetUp() {

        final FilePackagerBean result = new FilePackagerBean();

        result.setStatus(getStatus());
        result.setEstimatedUncompressedSize(getEstimatedUncompressedSize());
        result.setJobWSSubmissionDate(getJobWSSubmissionDate());

        //Info for being able to build linkText
        result.setArchiveLinkSite(getArchiveLinkSite());
        result.setArchiveLogicalName(getArchiveLogicalName());
        result.setException(getException());

        return result;
    }

    /**
     * Return a <code>JobDetail</code> for the setup
     *
     * @return a <code>JobDetail</code> for the setup
     */
    private JobDetail getJobDetailForSetUp() {

        final JobDetail result = new JobDetail();

        result.setName("testJobName");
        result.setGroup("testJobGroup");

        return result;
    }

    private SimpleTrigger getSimpleTriggerForSetUp() {

        this.triggerDate = new Date();

        final SimpleTrigger result = new SimpleTrigger();
        result.setStartTime(triggerDate);

        return result;
    }

    @Test
    public void testCreateQuartzJobHistorySucceededStatus() {

        setStatus(QuartzJobStatus.Succeeded);
        getFilePackagerBean().setStatus(getStatus());

        final QuartzJobHistory quartzJobHistory = getFilePackagerBean().createQuartzJobHistory(getJobDetail(), getSimpleTrigger());
        checkQuartzJobHistory(
                quartzJobHistory,
                getJobDetail().getName(),
                getJobDetail().getGroup(),
                getStatus(),
                getEstimatedUncompressedSize(),
                getJobWSSubmissionDate(),
                getArchiveLinkSite(),
                getArchiveLogicalName(),
                getException(),
                new Date().getTime()
        );
    }

    @Test
    public void testCreateQuartzJobHistoryFailedStatus() {

        setStatus(QuartzJobStatus.Failed);
        getFilePackagerBean().setStatus(getStatus());

        final QuartzJobHistory quartzJobHistory = getFilePackagerBean().createQuartzJobHistory(getJobDetail(), getSimpleTrigger());
        checkQuartzJobHistory(
                quartzJobHistory,
                getJobDetail().getName(),
                getJobDetail().getGroup(),
                getStatus(),
                getEstimatedUncompressedSize(),
                getJobWSSubmissionDate(),
                getArchiveLinkSite(),
                getArchiveLogicalName(),
                getException(),
                new Date().getTime()
        );
    }

    @Test
    public void testGetQuartzJobHistory() {

        //Create the initial QuartzJobHistory for the FilePackagerBean
        final String jobName = "job name";
        final String jobGroup = "job group";
        final QuartzJobStatus status = QuartzJobStatus.Succeeded;
        final Long estimatedUncompressedSize = 100L;
        final Date jobWSSubmissionDate = new Date(9000);
        final String archiveLinkSite = "http://testsite/";
        final String archiveLogicalName = "archiveName";
        final Date lastUpdated = new Date(1);
        final Exception exception = null;

        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
        quartzJobHistory.setJobName(jobName);
        quartzJobHistory.setJobGroup(jobGroup);
        quartzJobHistory.setStatus(status);
        quartzJobHistory.setEstimatedUncompressedSize(estimatedUncompressedSize);
        quartzJobHistory.setJobWSSubmissionDate(jobWSSubmissionDate);
        quartzJobHistory.setLinkText(new StringBuilder(archiveLinkSite).append(archiveLogicalName).append(".tar.gz").toString());
        quartzJobHistory.setLastUpdated(lastUpdated);

        //Let's just make sure we set up the initial QuartzJobHistory correctly
        checkQuartzJobHistory(
                quartzJobHistory,
                jobName,
                jobGroup,
                status,
                estimatedUncompressedSize,
                jobWSSubmissionDate,
                archiveLinkSite,
                archiveLogicalName,
                exception,
                new Date().getTime()
                );

        //Set the FilePackagerBean with that initial QuartzJobHistory
        getFilePackagerBean().setQuartzJobHistory(quartzJobHistory);

        //Update the FilePackagerBean fields to simulate a change
        final QuartzJobStatus newStatus = QuartzJobStatus.Failed;
        final Long newEstimatedUncompressedSize = 2000L;
        final Date newJobWSSubmissionDate = new Date(18000);
        final String newArchiveLinkSite = null;
        final String newArchiveLogicalName = null;
        final Exception newException = new Exception("Another Exception");

        getFilePackagerBean().setStatus(newStatus);
        getFilePackagerBean().setEstimatedUncompressedSize(newEstimatedUncompressedSize);
        getFilePackagerBean().setJobWSSubmissionDate(newJobWSSubmissionDate);
        getFilePackagerBean().setArchiveLinkSite(newArchiveLinkSite);
        getFilePackagerBean().setArchiveLogicalName(newArchiveLogicalName);
        getFilePackagerBean().setException(newException);

        //Get the updated QuartzJobHistory
        final QuartzJobHistory updatedQuartzJobHistory = getFilePackagerBean().getUpdatedQuartzJobHistory();

        checkQuartzJobHistory(
                updatedQuartzJobHistory,
                jobName,
                jobGroup,
                newStatus,
                newEstimatedUncompressedSize,
                newJobWSSubmissionDate,
                newArchiveLinkSite,
                newArchiveLogicalName,
                newException,
                new Date().getTime()
                );
    }

    /**
     * Check the given <code>QuartzJobHistory</code> for expected values
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to check
     * @param expectedJobName the expected job name
     * @param expectedJobGroup the expected job group
     * @param expectedStatus the expected status
     * @param expectedEstimatedUncompressedSize the expected estimated uncompressed size
     * @param expectedJobWSSubmissionDate the expected job web service submission date
     * @param expectedArchiveLinkSite the expected archive link site
     * @param expectedArchiveLogicalName the expected archive logical name
     * @param expectedException the expected exception
     * @param expectedLastUpdatedUpperLimitInMilliseconds the expected last updated upper limit in milliseconds
     */
    private void checkQuartzJobHistory(final QuartzJobHistory quartzJobHistory,
                                       final String expectedJobName,
                                       final String expectedJobGroup,
                                       final QuartzJobStatus expectedStatus,
                                       final Long expectedEstimatedUncompressedSize,
                                       final Date expectedJobWSSubmissionDate,
                                       final String expectedArchiveLinkSite,
                                       final String expectedArchiveLogicalName,
                                       final Exception expectedException,
                                       final Long expectedLastUpdatedUpperLimitInMilliseconds) {

        String expectedLinkText = null;
        if(expectedStatus == QuartzJobStatus.Succeeded) {
            expectedLinkText = new StringBuilder(expectedArchiveLinkSite).append(expectedArchiveLogicalName).append(".tar.gz").toString();

        } else if(expectedStatus == QuartzJobStatus.Failed) {
            expectedLinkText = new StringBuilder("Error: ").append(expectedException.getMessage()).toString();
        }

        assertEquals("Unexpected job name: ", expectedJobName, quartzJobHistory.getJobName());
        assertEquals("Unexpected job name: ", expectedJobGroup, quartzJobHistory.getJobGroup());
        assertEquals("Unexpected status: ", expectedStatus, quartzJobHistory.getStatus());
        assertEquals("Unexpected estimated uncompressed size: ", expectedEstimatedUncompressedSize, quartzJobHistory.getEstimatedUncompressedSize());
        assertEquals("Unexpected job web service submission date: ", expectedJobWSSubmissionDate, quartzJobHistory.getJobWSSubmissionDate());
        assertEquals("Unexpected link text: ", expectedLinkText, quartzJobHistory.getLinkText());
        assertTrue("Unexpected last updated date: ", quartzJobHistory.getLastUpdated().getTime() > 0);
        assertTrue("Unexpected last updated date: ", quartzJobHistory.getLastUpdated().getTime() <= expectedLastUpdatedUpperLimitInMilliseconds);
    }

    /*
     * Getter / Setter
     */

    public FilePackagerBean getFilePackagerBean() {
        return filePackagerBean;
    }

    public void setFilePackagerBean(final FilePackagerBean filePackagerBean) {
        this.filePackagerBean = filePackagerBean;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public void setJobDetail(final JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    public SimpleTrigger getSimpleTrigger() {
        return simpleTrigger;
    }

    public void setSimpleTrigger(final SimpleTrigger simpleTrigger) {
        this.simpleTrigger = simpleTrigger;
    }

    public Date getTriggerDate() {
        return triggerDate;
    }

    public void setTriggerDate(final Date triggerDate) {
        this.triggerDate = triggerDate;
    }

    public Date getJobWSSubmissionDate() {
        return jobWSSubmissionDate;
    }

    public void setJobWSSubmissionDate(final Date jobWSSubmissionDate) {
        this.jobWSSubmissionDate = jobWSSubmissionDate;
    }

    public Date getQuartzJobHistoryLastUpdatedDate() {
        return quartzJobHistoryLastUpdatedDate;
    }

    public void setQuartzJobHistoryLastUpdatedDate(final Date quartzJobHistoryLastUpdatedDate) {
        this.quartzJobHistoryLastUpdatedDate = quartzJobHistoryLastUpdatedDate;
    }

    public Long getEstimatedUncompressedSize() {
        return estimatedUncompressedSize;
    }

    public void setEstimatedUncompressedSize(final Long estimatedUncompressedSize) {
        this.estimatedUncompressedSize = estimatedUncompressedSize;
    }

    public String getArchiveLinkSite() {
        return archiveLinkSite;
    }

    public void setArchiveLinkSite(final String archiveLinkSite) {
        this.archiveLinkSite = archiveLinkSite;
    }

    public String getArchiveLogicalName() {
        return archiveLogicalName;
    }

    public void setArchiveLogicalName(final String archiveLogicalName) {
        this.archiveLogicalName = archiveLogicalName;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(final Exception exception) {
        this.exception = exception;
    }

    public QuartzJobStatus getStatus() {
        return status;
    }

    public void setStatus(final QuartzJobStatus status) {
        this.status = status;
    }
}
