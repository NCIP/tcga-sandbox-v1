/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import static org.junit.Assert.fail;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * QuartzJobHistoryDeletionJob unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class QuartzJobHistoryDeletionJobFastTest {

    private Mockery context = new JUnit4Mockery();
    private QuartzJobHistoryDeletionJob quartzJobHistoryDeletionJob;
    private QuartzJobHistoryService mockQuartzJobHistoryService;

    @Before
    public void setup() {

        mockQuartzJobHistoryService = context.mock(QuartzJobHistoryService.class);
        quartzJobHistoryDeletionJob = new QuartzJobHistoryDeletionJob();
        quartzJobHistoryDeletionJob.setQuartzJobHistoryService(mockQuartzJobHistoryService);
    }

    @Test
    public void testRun() {

        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
        quartzJobHistory.setJobName("jobName");
        quartzJobHistory.setJobGroup("jobGroup");

        context.checking(new Expectations() {{
            one(mockQuartzJobHistoryService).delete(quartzJobHistory);
            will(returnValue(1));
        }});

        try {
            quartzJobHistoryDeletionJob.run(quartzJobHistory);
            // We can't really assert anything since the run method returns void,
            // but at least we can expect exactly one call to the mock QuartzJobHistoryService
            // (which is tested on its own)

        } catch (final Exception e) {
            fail("Unexpected exception");
        }
    }
}
