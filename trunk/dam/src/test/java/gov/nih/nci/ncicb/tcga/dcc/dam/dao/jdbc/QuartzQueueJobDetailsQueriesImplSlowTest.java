/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzQueueJobDetails;

import java.io.File;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

/**
 * QuartzQueueJobDetailsQueriesImpl unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QuartzQueueJobDetailsQueriesImplSlowTest extends DBUnitTestCase {

    private static final String PATH_TO_DB_PROPERTIES = Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String DB_PROPERTIES_FILE = "quartz.unittest.properties";

    private static final String DATA_FILE = "portal" + File.separator + "dao" + File.separator + "QuartzQueueJobDetailsQueries_data.xml";

    private QuartzQueueJobDetailsQueriesImpl quartzQueueJobDetailsQueries;

    public QuartzQueueJobDetailsQueriesImplSlowTest() {
        super(PATH_TO_DB_PROPERTIES, DATA_FILE, DB_PROPERTIES_FILE);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();

        quartzQueueJobDetailsQueries = new QuartzQueueJobDetailsQueriesImpl();
        quartzQueueJobDetailsQueries.setDataSource(getDataSource());
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
    public void testGetQuartzSmallQueueJobDetailsExists() {

        final String jobName = "small_job_1";
        final String jobGroup = "small_group_1";

        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsQueries.getQuartzSmallQueueJobDetails(jobName, jobGroup);

        final String description = "small description";
        final String jobClassName = "small.job.class.name";
        final Boolean isDurable = false;
        final Boolean isVolatile = true;
        final Boolean isStateful = false;
        final Boolean requestsRecovery = true;

        testQuartzQueueJobDetails(quartzQueueJobDetails,
                jobName,
                jobGroup,
                description,
                jobClassName,
                isDurable,
                isVolatile,
                isStateful,
                requestsRecovery);
    }

    @Test
    public void testGetQuartzSmallQueueJobDetailsDoesNotExist() {

        final String jobName = "job_0";
        final String jobGroup = "group_0";

        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsQueries.getQuartzSmallQueueJobDetails(jobName, jobGroup);

        assertNull(quartzQueueJobDetails);
    }

    @Test
    public void testGetQuartzBigQueueJobDetailsExists() {

        final String jobName = "big_job_1";
        final String jobGroup = "big_group_1";

        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsQueries.getQuartzBigQueueJobDetails(jobName, jobGroup);

        final String description = "big description";
        final String jobClassName = "big.job.class.name";
        final Boolean isDurable = true;
        final Boolean isVolatile = false;
        final Boolean isStateful = true;
        final Boolean requestsRecovery = false;

        testQuartzQueueJobDetails(quartzQueueJobDetails,
                jobName,
                jobGroup,
                description,
                jobClassName,
                isDurable,
                isVolatile,
                isStateful,
                requestsRecovery);
    }

    @Test
    public void testGetQuartzBigQueueJobDetailsDoesNotExist() {

        final String jobName = "big_job_0";
        final String jobGroup = "big_group_0";
        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsQueries.getQuartzBigQueueJobDetails(jobName, jobGroup);

        assertNull(quartzQueueJobDetails);
    }

    @Test
    public void testHasQuartzQueueJobDetailsExistsInSmallQueue() {

        final String jobName = "small_job_1";
        final String jobGroup = "small_group_1";
        final boolean hasQuartzQueueJobDetails = quartzQueueJobDetailsQueries.hasQuartzQueueJobDetails(jobName, jobGroup);

        assertTrue("Did not find job details in small queue", hasQuartzQueueJobDetails);
    }

    @Test
    public void testHasQuartzQueueJobDetailsExistsInBigQueue() {

        final String jobName = "big_job_1";
        final String jobGroup = "big_group_1";
        final boolean hasQuartzQueueJobDetails = quartzQueueJobDetailsQueries.hasQuartzQueueJobDetails(jobName, jobGroup);

        assertTrue("Did not find job details in big queue", hasQuartzQueueJobDetails);
    }

    @Test
    public void testHasQuartzQueueJobDetailsDoesNotExistInAnyQueue() {

        final String jobName = "big_job_0";
        final String jobGroup = "big_group_0";
        final boolean hasQuartzQueueJobDetails = quartzQueueJobDetailsQueries.hasQuartzQueueJobDetails(jobName, jobGroup);

        assertFalse("Unexpectedly found matching job details", hasQuartzQueueJobDetails);
    }

    /**
     * Test the given <code>QuartzQueueJobDetails</code> against expected values
     *
     * @param quartzQueueJobDetails the <code>QuartzQueueJobDetails</code> to test
     * @param jobName the expected job name
     * @param jobGroup the expected job group
     * @param description the expected description
     * @param jobClassName the expected job class name
     * @param isDurable the expected durable value
     * @param isVolatile the expected volatile value
     * @param isStateful the expected stateful value
     * @param requestsRecovery the expected requests recovery value
     */
    private void testQuartzQueueJobDetails(final QuartzQueueJobDetails quartzQueueJobDetails,
                                           final String jobName,
                                           final String jobGroup,
                                           final String description,
                                           final String jobClassName,
                                           final Boolean isDurable,
                                           final Boolean isVolatile,
                                           final Boolean isStateful,
                                           final Boolean requestsRecovery) {

        assertNotNull(quartzQueueJobDetails);
        assertEquals("Unexpected job name", jobName, quartzQueueJobDetails.getJobName());
        assertEquals("Unexpected job group", jobGroup, quartzQueueJobDetails.getJobGroup());
        assertEquals("Unexpected description", description, quartzQueueJobDetails.getDescription());
        assertEquals("Unexpected job class name", jobClassName, quartzQueueJobDetails.getJobClassName());
        assertEquals("Unexpected durable value", isDurable, quartzQueueJobDetails.getDurable());
        assertEquals("Unexpected volatile value", isVolatile, quartzQueueJobDetails.getVolatile());
        assertEquals("Unexpected stateful value", isStateful, quartzQueueJobDetails.getStateFul());
        assertEquals("Unexpected requests recovery value", requestsRecovery, quartzQueueJobDetails.getRequestsRecovery());
    }
}
