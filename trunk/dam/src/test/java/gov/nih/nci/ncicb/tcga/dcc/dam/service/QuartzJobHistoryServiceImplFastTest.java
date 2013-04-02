/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.QuartzJobHistoryQueries;

import java.util.LinkedList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * QuartzJobHistoryServiceImpl unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class QuartzJobHistoryServiceImplFastTest {

    private Mockery context = new JUnit4Mockery();
    private QuartzJobHistoryQueries mockQuartzJobHistoryQueries;
    private QuartzJobHistoryServiceImpl quartzJobHistoryService;

    @Before
    public void setUp() {

        mockQuartzJobHistoryQueries = context.mock(QuartzJobHistoryQueries.class);
        quartzJobHistoryService = new QuartzJobHistoryServiceImpl();
        quartzJobHistoryService.setQuartzJobHistoryQueries(mockQuartzJobHistoryQueries);
    }

    @Test
    public void testDelete() {

        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
        final int expectedAffectedRows = 1;

        context.checking(new Expectations() {{
            allowing(mockQuartzJobHistoryQueries).delete(quartzJobHistory);
            will(returnValue(expectedAffectedRows));
        }});

        int affectedRows = quartzJobHistoryService.delete(quartzJobHistory);

        assertEquals("Unexpected affected rows: ", expectedAffectedRows, affectedRows);
    }

    @Test
    public void testPersist() {

        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
        final int expectedAffectedRows = 1;

        context.checking(new Expectations() {{
            allowing(mockQuartzJobHistoryQueries).persist(quartzJobHistory);
            will(returnValue(expectedAffectedRows));
        }});

        int affectedRows = quartzJobHistoryService.persist(quartzJobHistory);

        assertEquals("Unexpected affected rows: ", expectedAffectedRows, affectedRows);
    }

    @Test
    public void testGetAllQuartzJobHistory() {

        final List<QuartzJobHistory> expectedQuartzJobHistoryList = new LinkedList<QuartzJobHistory>();
        expectedQuartzJobHistoryList.add(new QuartzJobHistory());
        expectedQuartzJobHistoryList.add(new QuartzJobHistory());
        expectedQuartzJobHistoryList.add(new QuartzJobHistory());

        context.checking(new Expectations() {{
            allowing(mockQuartzJobHistoryQueries).getAllQuartzJobHistory();
            will(returnValue(expectedQuartzJobHistoryList));
        }});

        final List<QuartzJobHistory> actualQuartzJobHistoryList = quartzJobHistoryService.getAllQuartzJobHistory();

        assertEquals("Unexpected List of QuartzJobHistory: ", expectedQuartzJobHistoryList, actualQuartzJobHistoryList);
    }

    @Test
    public void testGetQuartzJobHistoryExists() {

        final String jobName = "job_name";
        final String jobGroup = "job_group";

        final QuartzJobHistory expectedQuartzJobHistory = new QuartzJobHistory();
        expectedQuartzJobHistory.setJobName(jobName);
        expectedQuartzJobHistory.setJobGroup(jobGroup);

        context.checking(new Expectations() {{
            allowing(mockQuartzJobHistoryQueries).getQuartzJobHistory(jobName, jobGroup);
            will(returnValue(expectedQuartzJobHistory));
        }});

        final QuartzJobHistory quartzJobHistory = quartzJobHistoryService.getQuartzJobHistory(jobName, jobGroup);

        assertNotNull(quartzJobHistory);
        assertEquals("Unexpected job name", expectedQuartzJobHistory.getJobName(), quartzJobHistory.getJobName());
        assertEquals("Unexpected job group", expectedQuartzJobHistory.getJobGroup(), quartzJobHistory.getJobGroup());
    }

    @Test
    public void testGetQuartzJobHistoryDoesNotExist() {

        final String jobName = "job_name";
        final String jobGroup = "job_group";

        context.checking(new Expectations() {{
            allowing(mockQuartzJobHistoryQueries).getQuartzJobHistory(jobName, jobGroup);
            will(returnValue(null));
        }});

        final QuartzJobHistory quartzJobHistory = quartzJobHistoryService.getQuartzJobHistory(jobName, jobGroup);

        assertNull(quartzJobHistory);
    }

    @Test
    public void testGetPositionInQueue() {
        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();

        context.checking(new Expectations() {{
            one(mockQuartzJobHistoryQueries).getPositionInQueue(quartzJobHistory);
            will(returnValue(1234));
        }});
        final Integer position = quartzJobHistoryService.getPositionInQueue(quartzJobHistory);
        assertEquals(new Integer(1234), position);

    }
}
