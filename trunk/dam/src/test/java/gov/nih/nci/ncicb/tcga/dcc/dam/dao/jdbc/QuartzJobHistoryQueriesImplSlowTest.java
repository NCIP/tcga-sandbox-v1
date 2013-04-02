/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

/**
 * QuartzJobHistoryQueriesImpl Unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QuartzJobHistoryQueriesImplSlowTest extends DBUnitTestCase {

    private static final String PATH_TO_DB_PROPERTIES = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String DB_PROPERTIES_FILE = "quartz.unittest.properties";

    private static final String DATA_FILE = "portal" + File.separator + "dao" + File.separator + "QuartzJobHistoryQueries_data.xml";

    /**
     * Query to update data in <b>qrtz_job_history.job_data</b>. This can't be done directly through a static dataset file
     * since dbunit does not handle the XMLType in a dataset.
     */
    private static final String UPDATE_JOB_DATA_QUERY = "update " + QuartzJobHistoryQueriesImpl.QRTZ_JOB_HISTORY
            + " set " + QuartzJobHistoryQueriesImpl.JOB_DATA + "=? "
            + "where " + QuartzJobHistoryQueriesImpl.JOB_NAME + "=? "
            + "and " + QuartzJobHistoryQueriesImpl.JOB_GROUP+ "=?";

    private QuartzJobHistoryQueriesImpl quartzJobHistoryQueries;

    private int initialRowCount;

    public QuartzJobHistoryQueriesImplSlowTest() {
        super(PATH_TO_DB_PROPERTIES, DATA_FILE, DB_PROPERTIES_FILE);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();

        quartzJobHistoryQueries = new QuartzJobHistoryQueriesImpl();
        quartzJobHistoryQueries.setDataSource(getDataSource());

        setInitialRowCount(getQuartzJobHistoryRowCount());

        initJobData();
    }

    @Override
    public DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testPersistSucceeded() {
        testPersistOK(QuartzJobStatus.Succeeded);
    }

    @Test
    public void testPersistFailed() {
        testPersistOK(QuartzJobStatus.Failed);
    }

    @Test
    public void testPersistQueued() {
        testPersistOK(QuartzJobStatus.Queued);
    }

    @Test
    public void testPersistStarted() {
        testPersistOK(QuartzJobStatus.Started);
    }

    @Test
    public void testPersistExisting() {
        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
        quartzJobHistory.setJobName("test_job_name_1");
        quartzJobHistory.setJobGroup("test_job_group_1");
        // status in DB initially is queued
        quartzJobHistory.setStatus(QuartzJobStatus.Failed);
        quartzJobHistory.setEnqueueDate(new Date());
        quartzJobHistory.setFireTime(new Date());
        quartzJobHistory.setLinkText(null);
        quartzJobHistory.setEstimatedUncompressedSize(1234L);

        final int rowsUpdated = quartzJobHistoryQueries.persist(quartzJobHistory);
        assertEquals(1, rowsUpdated);
    }

    @Test
    public void testGetAllQuartzJobHistory() throws Exception {

        final List<QuartzJobHistory> quartzJobHistoryList = quartzJobHistoryQueries.getAllQuartzJobHistory();
        assertEquals(3, quartzJobHistoryList.size());

        boolean job1Found = false;
        boolean job2Found = false;
        boolean job3Found = false;

        /**
         * Check that each QuartzJobHistory as the expected values according to what is in the dataset file 
         * and what has been initialized in the setup
         */
        for(final QuartzJobHistory quartzJobHistory : quartzJobHistoryList) {

            if("test_job_name_1".equals(quartzJobHistory.getJobName())) {

                job1Found = true;
                checkQuartzJobHistoryForExpectedValues(quartzJobHistory, 1, "Queued");

            } else if("test_job_name_2".equals(quartzJobHistory.getJobName())) {

                job2Found = true;
                checkQuartzJobHistoryForExpectedValues(quartzJobHistory, 2, "Started");

            } else if("test_job_name_3".equals(quartzJobHistory.getJobName())) {

                job3Found = true;
                checkQuartzJobHistoryForExpectedValues(quartzJobHistory, 3, "Succeeded");
            }
        }

        assertTrue("Job 1 not found!", job1Found);
        assertTrue("Job 2 not found!", job2Found);
        assertTrue("Job 3 not found!", job3Found);
    }

    @Test
    public void testDeleteExistingRow() throws Exception {

        final QuartzJobHistory quartzJobHistory = getTestQuartzJobHistory(QuartzJobStatus.Started);
        quartzJobHistory.setJobName("test_job_name_1");
        quartzJobHistory.setJobGroup("test_job_group_1");
        int updatedRows = quartzJobHistoryQueries.delete(quartzJobHistory);

        assertEquals("Unexpected number of updated rows: ", 1, updatedRows);
        assertEquals(getInitialRowCount() - 1, getQuartzJobHistoryRowCount());
    }

    @Test
    public void testDeleteNonExistingRow() throws Exception {

        final QuartzJobHistory quartzJobHistory = getTestQuartzJobHistory(QuartzJobStatus.Started);
        quartzJobHistory.setJobName("test_job_name_4");
        quartzJobHistory.setJobGroup("test_job_group_4");
        int updatedRows = quartzJobHistoryQueries.delete(quartzJobHistory);

        assertEquals("Unexpected number of updated rows: ", 0, updatedRows);
        assertEquals(getInitialRowCount(), getQuartzJobHistoryRowCount());
    }

    @Test
    public void testGetQuartzJobHistoryExists() {

        final String jobName = "test_job_name_1";
        final String jobGroup = "test_job_group_1";
        final QuartzJobHistory quartzJobHistory = quartzJobHistoryQueries.getQuartzJobHistory(jobName, jobGroup);

        final String expectedFireTime = "2010-11-01";
        final String expectedStatus = QuartzJobStatus.Queued.toString();
        final String expectedLastUpdated = "2010-12-01";
        final String expectedLinkText = "http://archive1.tar.gz";
        final Long expectedEstimatedUncompressedSize = 1L;
        final long expectedJobWSSubmissionDate = 1234567890L + 1L;

        assertNotNull(quartzJobHistory);
        assertEquals("Unexpected job name", jobName, quartzJobHistory.getJobName());

        checkQuartzJobHistoryForExpectedValues(quartzJobHistory,
                jobGroup,
                expectedFireTime,
                expectedStatus,
                expectedLastUpdated,
                expectedLinkText,
                expectedEstimatedUncompressedSize,
                expectedJobWSSubmissionDate);
    }

    @Test
    public void testGetQuartzJobHistoryDoesNotExist() {

        final String jobName = "test_job_name_0";
        final String jobGroup = "test_job_group_0";
        final QuartzJobHistory quartzJobHistory = quartzJobHistoryQueries.getQuartzJobHistory(jobName, jobGroup);

        assertNull(quartzJobHistory);
    }

    public void testGetPositionInQueue() {
        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
        quartzJobHistory.setQueueName("test");
        quartzJobHistory.setEnqueueDate(new Date());

        assertEquals(new Integer(2), quartzJobHistoryQueries.getPositionInQueue(quartzJobHistory));
    }

    /**
     * JOB_DATA values must be inserted at set up since it is not handled by dbunit in a dataset file
     */
    private void initJobData() {

        final Long date = 1234567890L;

        insertXmlAsXMLType("test_job_name_1", "test_job_group_1", getJobDataFor("http://archive1.tar.gz", 1L, date + 1));
        insertXmlAsXMLType("test_job_name_2", "test_job_group_2", getJobDataFor("http://archive2.tar.gz", 2L, date + 2));
        insertXmlAsXMLType("test_job_name_3", "test_job_group_3", getJobDataFor("http://archive3.tar.gz", 3L, date + 3));
    }

    /**
     * Return an XML document (as String) for the job_data column with the given parameters
     *
     * @param linkText the link to the archive
     * @param estimatedUncompressedSize the estimated uncompressed size of the archive
     * @param jobWSSubmissionDateMilliseconds the job web service submission date in milliseconds
     *
     * @return an XML document (as String) for the job_data column with the given parameters
     */
    private String getJobDataFor(final String linkText,
                                 final Long estimatedUncompressedSize,
                                 final Long jobWSSubmissionDateMilliseconds) {

        return new StringBuilder("<jobData><linkText>")
                .append(linkText)
                .append("</linkText><estimatedUncompressedSize>")
                .append(estimatedUncompressedSize)
                .append("</estimatedUncompressedSize><jobWSSubmissionDate>")
                .append(jobWSSubmissionDateMilliseconds)
                .append("</jobWSSubmissionDate></jobData>")
                .toString();
    }

    /**
     * Insert the given xml into the job_data column for a given job name and group
     *
     * @param job_name the job name
     * @param job_group the job group
     * @param xmlAsString the job data (xml) for that job name and group
     */
    private void insertXmlAsXMLType(final String job_name, final String job_group, final String xmlAsString) {

        final Object[] params = new Object[] {
                xmlAsString,
                job_name,
                job_group
        };

        getSimpleJdbcTemplate().update(UPDATE_JOB_DATA_QUERY, params);
    }

    /**
     * Check a <code>QuartzJobHistory</code> for expected values depending on its row number in the dataset
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to check
     * @param rowNumber the row number in the dataset
     * @param expectedStatus the expected status
     */
    private void checkQuartzJobHistoryForExpectedValues(final QuartzJobHistory quartzJobHistory,
                                                        final long rowNumber,
                                                        final String expectedStatus) {

        checkQuartzJobHistoryForExpectedValues(quartzJobHistory,
                        "test_job_group_" + rowNumber,
                        "2010-11-0" + rowNumber,
                        expectedStatus,
                        "2010-12-0" + rowNumber,
                        "http://archive" + rowNumber + ".tar.gz",
                        rowNumber,
                        1234567890L + rowNumber);
    }

    /**
     * Check a <code>QuartzJobHistory</code> for expected values
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to check
     * @param expectedJobGroup the expected job group
     * @param expectedFireTime the expected fire time
     * @param expectedStatus the expected status
     * @param expectedLastUpdated the expected last updated
     * @param expectedLinkText the expected link text
     * @param expectedEstimatedUncompressedSize the expected estimated uncompressed size
     * @param expectedJobWSSubmissionDate the expected job web service submission date
     */
    private void checkQuartzJobHistoryForExpectedValues(final QuartzJobHistory quartzJobHistory,
                                                        final String expectedJobGroup,
                                                        final String expectedFireTime,
                                                        final String expectedStatus,
                                                        final String expectedLastUpdated,
                                                        final String expectedLinkText,
                                                        final Long expectedEstimatedUncompressedSize,
                                                        final long expectedJobWSSubmissionDate) {

        assertEquals("Unexpected job group: ", expectedJobGroup, quartzJobHistory.getJobGroup());
        assertEquals("Unexpected fire time: ", expectedFireTime, quartzJobHistory.getFireTime().toString());
        assertEquals("Unexpected status: ", expectedStatus, quartzJobHistory.getStatus().toString());
        assertEquals("Unexpected last updated: ", expectedLastUpdated, quartzJobHistory.getLastUpdated().toString());
        assertEquals("Unexpected text link: ", expectedLinkText, quartzJobHistory.getLinkText());
        assertEquals("Unexpected estimated uncompressed size: ", expectedEstimatedUncompressedSize, quartzJobHistory.getEstimatedUncompressedSize());
        assertEquals("Unexpected job web service submission date in milliseconds", expectedJobWSSubmissionDate, quartzJobHistory.getJobWSSubmissionDate().getTime());
    }

    /**
     * Test the persistence of a <code>QuartzJobHistory</code> with the given Status
     * (expected values are Succeeded and Failed)
     *
     * @param status the Status of the <code>QuartzJobHistory</code> to persist
     */
    private void testPersistOK(final QuartzJobStatus status) {

        try {
            assertRowCount(getInitialRowCount());

            final QuartzJobHistory quartzJobHistory = getTestQuartzJobHistory(status);
            int updatedRows = quartzJobHistoryQueries.persist(quartzJobHistory);

            assertEquals("Unexpected number of updated rows: ", 1, updatedRows);
            assertRowCount(getInitialRowCount() + 1);

            final Map<String, Object> savedData = getSimpleJdbcTemplate().queryForMap("select * from qrtz_job_history where job_name=?", quartzJobHistory.getJobName());
            assertEquals(quartzJobHistory.getQueueName(), savedData.get("queue_name"));
            assertNotNull(savedData.get("time_enqueued"));
            if (quartzJobHistory.isComplete()) {
                assertEquals("1", savedData.get("is_complete").toString());
            }


        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /**
     * Return an instance of a <code>QuartzJobHistory</code> with the given Status
     *
     * @param status the status
     * @return an instance of a <code>QuartzJobHistory</code> with the given Status
     */
    private QuartzJobHistory getTestQuartzJobHistory(final QuartzJobStatus status) {

        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();

        quartzJobHistory.setStatus(status);
        quartzJobHistory.setJobName("test_job_name");
        quartzJobHistory.setJobGroup("test_job_group");
        quartzJobHistory.setFireTime(new Date());
        quartzJobHistory.setLinkText("http");
        quartzJobHistory.setEstimatedUncompressedSize(1979L);
        quartzJobHistory.setJobWSSubmissionDate(new Date());
        quartzJobHistory.setEnqueueDate(new Date());
        quartzJobHistory.setQueueName("test");

        return quartzJobHistory;
    }

    /**
     * Return the row count of the QRTZ_JOB_HISTORY table
     *
     * @return the row count of the QRTZ_JOB_HISTORY table
     * @throws Exception
     */
    private int getQuartzJobHistoryRowCount() throws Exception {
        return getSimpleJdbcTemplate().queryForInt("select count(*) from QRTZ_JOB_HISTORY");
    }

    /**
     * Assert that the QRTZ_JOB_HISTORY table has the given row count
     *
     * @param rowCount the expected row count for the QRTZ_JOB_HISTORY tbale
     * @throws Exception
     */
    private void assertRowCount(final int rowCount) throws Exception {
        assertEquals("Unexpected row count: ", rowCount, getQuartzJobHistoryRowCount());
    }

    /*
     * Getter / Setter
     */

    public int getInitialRowCount() {
        return initialRowCount;
    }

    public void setInitialRowCount(final int initialRowCount) {
        this.initialRowCount = initialRowCount;
    }
}
