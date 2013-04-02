/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzQueueJobDetails;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.QuartzQueueJobDetailsQueries;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * QuartzQueueJobDetailsServiceImpl unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class QuartzQueueJobDetailsServiceImplFastTest {

    private Mockery context = new JUnit4Mockery();

    private QuartzQueueJobDetailsQueries mockQuartzQueueJobDetailsQueries;
    private QuartzQueueJobDetailsServiceImpl quartzQueueJobDetailsService;

    @Before
    public void setUp() {

        mockQuartzQueueJobDetailsQueries = context.mock(QuartzQueueJobDetailsQueries.class);
        quartzQueueJobDetailsService = new QuartzQueueJobDetailsServiceImpl();
        quartzQueueJobDetailsService.setQuartzQueueJobDetailsQueries(mockQuartzQueueJobDetailsQueries);
    }

    @Test
    public void testGetQuartzSmallQueueJobDetailsExists() {

        final String jobName = "small_job_1";
        final String jobGroup = "small_group_1";

        final QuartzQueueJobDetails pretendSmallQuartzQueueJobDetails = getPretendSmallQuartzQueueJobDetails();

        context.checking(new Expectations() {{
            one(mockQuartzQueueJobDetailsQueries).getQuartzSmallQueueJobDetails(jobName, jobGroup);
            will(returnValue(pretendSmallQuartzQueueJobDetails));
        }});

        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsService.getQuartzSmallQueueJobDetails(jobName, jobGroup);

        testQuartzQueueJobDetails(quartzQueueJobDetails,
                pretendSmallQuartzQueueJobDetails.getJobName(),
                pretendSmallQuartzQueueJobDetails.getJobGroup(),
                pretendSmallQuartzQueueJobDetails.getDescription(),
                pretendSmallQuartzQueueJobDetails.getJobClassName(),
                pretendSmallQuartzQueueJobDetails.getDurable(),
                pretendSmallQuartzQueueJobDetails.getVolatile(),
                pretendSmallQuartzQueueJobDetails.getStateFul(),
                pretendSmallQuartzQueueJobDetails.getRequestsRecovery());
    }

    @Test
    public void testGetQuartzSmallQueueJobDetailsDoesNotExist() {

        final String jobName = "job_0";
        final String jobGroup = "group_0";

        context.checking(new Expectations() {{
            one(mockQuartzQueueJobDetailsQueries).getQuartzSmallQueueJobDetails(jobName, jobGroup);
            will(returnValue(null));
        }});

        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsService.getQuartzSmallQueueJobDetails(jobName, jobGroup);

        assertNull(quartzQueueJobDetails);
    }

    @Test
    public void testGetQuartzBigQueueJobDetailsExists() {

        final String jobName = "big_job_1";
        final String jobGroup = "big_group_1";

        final QuartzQueueJobDetails pretendBigQuartzQueueJobDetails = getPretendBigQuartzQueueJobDetails();

        context.checking(new Expectations() {{
            one(mockQuartzQueueJobDetailsQueries).getQuartzBigQueueJobDetails(jobName, jobGroup);
            will(returnValue(pretendBigQuartzQueueJobDetails));
        }});

        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsService.getQuartzBigQueueJobDetails(jobName, jobGroup);

        testQuartzQueueJobDetails(quartzQueueJobDetails,
                pretendBigQuartzQueueJobDetails.getJobName(),
                pretendBigQuartzQueueJobDetails.getJobGroup(),
                pretendBigQuartzQueueJobDetails.getDescription(),
                pretendBigQuartzQueueJobDetails.getJobClassName(),
                pretendBigQuartzQueueJobDetails.getDurable(),
                pretendBigQuartzQueueJobDetails.getVolatile(),
                pretendBigQuartzQueueJobDetails.getStateFul(),
                pretendBigQuartzQueueJobDetails.getRequestsRecovery());
    }

    @Test
    public void testGetQuartzBigQueueJobDetailsDoesNotExist() {

        final String jobName = "job_0";
        final String jobGroup = "group_0";

        context.checking(new Expectations() {{
            one(mockQuartzQueueJobDetailsQueries).getQuartzBigQueueJobDetails(jobName, jobGroup);
            will(returnValue(null));
        }});

        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsService.getQuartzBigQueueJobDetails(jobName, jobGroup);

        assertNull(quartzQueueJobDetails);
    }

    @Test
    public void testGetQuartzSmallOrBigQueueJobDetailsExistsInSmallQueue() {

        final String jobName = "small_job_1";
        final String jobGroup = "small_group_1";

        final QuartzQueueJobDetails pretendSmallQuartzQueueJobDetails = getPretendSmallQuartzQueueJobDetails();

        context.checking(new Expectations() {{
            one(mockQuartzQueueJobDetailsQueries).getQuartzSmallQueueJobDetails(jobName, jobGroup);
            will(returnValue(pretendSmallQuartzQueueJobDetails));
        }});

        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsService.getQuartzSmallOrBigQueueJobDetails(jobName, jobGroup);

        testQuartzQueueJobDetails(quartzQueueJobDetails,
                pretendSmallQuartzQueueJobDetails.getJobName(),
                pretendSmallQuartzQueueJobDetails.getJobGroup(),
                pretendSmallQuartzQueueJobDetails.getDescription(),
                pretendSmallQuartzQueueJobDetails.getJobClassName(),
                pretendSmallQuartzQueueJobDetails.getDurable(),
                pretendSmallQuartzQueueJobDetails.getVolatile(),
                pretendSmallQuartzQueueJobDetails.getStateFul(),
                pretendSmallQuartzQueueJobDetails.getRequestsRecovery());
    }

    @Test
    public void testGetQuartzSmallOrBigQueueJobDetailsExistsInBigQueue() {

        final String jobName = "big_job_1";
        final String jobGroup = "big_group_1";

        final QuartzQueueJobDetails pretendBigQuartzQueueJobDetails = getPretendBigQuartzQueueJobDetails();

        context.checking(new Expectations() {{

            one(mockQuartzQueueJobDetailsQueries).getQuartzSmallQueueJobDetails(jobName, jobGroup);
            will(returnValue(null));

            one(mockQuartzQueueJobDetailsQueries).getQuartzBigQueueJobDetails(jobName, jobGroup);
            will(returnValue(pretendBigQuartzQueueJobDetails));
        }});

        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsService.getQuartzSmallOrBigQueueJobDetails(jobName, jobGroup);

        testQuartzQueueJobDetails(quartzQueueJobDetails,
                pretendBigQuartzQueueJobDetails.getJobName(),
                pretendBigQuartzQueueJobDetails.getJobGroup(),
                pretendBigQuartzQueueJobDetails.getDescription(),
                pretendBigQuartzQueueJobDetails.getJobClassName(),
                pretendBigQuartzQueueJobDetails.getDurable(),
                pretendBigQuartzQueueJobDetails.getVolatile(),
                pretendBigQuartzQueueJobDetails.getStateFul(),
                pretendBigQuartzQueueJobDetails.getRequestsRecovery());
    }

    @Test
    public void testGetQuartzSmallOrBigQueueJobDetailsDoesNotExist() {

        final String jobName = "job_0";
        final String jobGroup = "group_0";

        context.checking(new Expectations() {{

            one(mockQuartzQueueJobDetailsQueries).getQuartzSmallQueueJobDetails(jobName, jobGroup);
            will(returnValue(null));

            one(mockQuartzQueueJobDetailsQueries).getQuartzBigQueueJobDetails(jobName, jobGroup);
            will(returnValue(null));
        }});

        final QuartzQueueJobDetails quartzQueueJobDetails = quartzQueueJobDetailsService.getQuartzSmallOrBigQueueJobDetails(jobName, jobGroup);

        assertNull(quartzQueueJobDetails);
    }

    @Test
    public void testHasQuartzQueueJobDetailsExistsInSmallQueue() {

        final String jobName = "small_job_1";
        final String jobGroup = "small_group_1";

        context.checking(new Expectations() {{
            one(mockQuartzQueueJobDetailsQueries).hasQuartzQueueJobDetails(jobName, jobGroup);
            will(returnValue(true));
        }});

        final boolean hasQuartzQueueJobDetails = quartzQueueJobDetailsService.hasQuartzQueueJobDetails(jobName, jobGroup);

        assertTrue("Did not find job details in small queue", hasQuartzQueueJobDetails);
    }

    @Test
    public void testHasQuartzQueueJobDetailsExistsInBigQueue() {

        final String jobName = "big_job_1";
        final String jobGroup = "big_group_1";

        context.checking(new Expectations() {{
            one(mockQuartzQueueJobDetailsQueries).hasQuartzQueueJobDetails(jobName, jobGroup);
            will(returnValue(true));
        }});

        final boolean hasQuartzQueueJobDetails = quartzQueueJobDetailsService.hasQuartzQueueJobDetails(jobName, jobGroup);

        assertTrue("Did not find job details in big queue", hasQuartzQueueJobDetails);
    }

    @Test
    public void testHasQuartzQueueJobDetailsDoesNotExistInAnyQueue() {

        final String jobName = "big_job_0";
        final String jobGroup = "big_group_0";

        context.checking(new Expectations() {{
            one(mockQuartzQueueJobDetailsQueries).hasQuartzQueueJobDetails(jobName, jobGroup);
            will(returnValue(false));
        }});

        final boolean hasQuartzQueueJobDetails = quartzQueueJobDetailsService.hasQuartzQueueJobDetails(jobName, jobGroup);

        assertFalse("Unexpectedly found matching job details", hasQuartzQueueJobDetails);
    }

    /**
     * Return a <code>QuartzQueueJobDetails</code> for small job details
     *
     * @return a <code>QuartzQueueJobDetails</code> for small job details
     */
    private QuartzQueueJobDetails getPretendSmallQuartzQueueJobDetails() {

        final String jobName = "small_job_1";
        final String jobGroup = "small_group_1";
        final String description = "small description";
        final String jobClassName = "small.job.class.name";
        final Boolean isDurable = false;
        final Boolean isVolatile = true;
        final Boolean isStateful = false;
        final Boolean requestsRecovery = true;

        return new QuartzQueueJobDetails(
                jobName,
                jobGroup,
                description,
                jobClassName,
                isDurable,
                isVolatile,
                isStateful,
                requestsRecovery,
                null
        );
    }

    /**
     * Return a <code>QuartzQueueJobDetails</code> for big job details
     *
     * @return a <code>QuartzQueueJobDetails</code> for big job details
     */
    private QuartzQueueJobDetails getPretendBigQuartzQueueJobDetails() {

        final String jobName = "big_job_1";
        final String jobGroup = "big_group_1";
        final String description = "big description";
        final String jobClassName = "big.job.class.name";
        final Boolean isDurable = true;
        final Boolean isVolatile = false;
        final Boolean isStateful = true;
        final Boolean requestsRecovery = false;

        return new QuartzQueueJobDetails(
                jobName,
                jobGroup,
                description,
                jobClassName,
                isDurable,
                isVolatile,
                isStateful,
                requestsRecovery,
                null
        );
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
