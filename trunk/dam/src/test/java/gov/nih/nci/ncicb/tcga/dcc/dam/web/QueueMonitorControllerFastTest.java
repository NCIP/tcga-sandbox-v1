/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveDeletionBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.JobDelegate;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.QueueMonitorItem;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Test class for QueueMonitorController
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class QueueMonitorControllerFastTest {
    private Mockery context = new JUnit4Mockery();
    private Scheduler mockBigJobScheduler, mockSmallJobScheduler;
    private QueueMonitorController queueMonitorController;

    @Before
    public void setUp() {
        mockBigJobScheduler = context.mock(Scheduler.class, "bigJobScheduler");
        mockSmallJobScheduler = context.mock(Scheduler.class, "smallJobScheduler");
        queueMonitorController = new QueueMonitorController();
        queueMonitorController.setBigJobScheduler(mockBigJobScheduler);
        queueMonitorController.setSmallJobScheduler(mockSmallJobScheduler);
    }

    @Test
    public void test() throws SchedulerException {
        // jobs 1 and 2 are in the big job queue, 3 and 4 are in the small job queue

        final JobDetail job1Detail = makeJobDetail("archive1_creation", true);
        final JobDetail job2Detail = makeJobDetail("archive2_creation", true);
        final JobDetail job3Detail = makeJobDetail("archive3_deletion", false);
        final JobDetail job4Detail = makeJobDetail("archive4_creation", true);

        context.checking(new Expectations() {{
            one(mockBigJobScheduler).getJobGroupNames();
            will(returnValue(new String[]{"test"}));
            one(mockSmallJobScheduler).getJobGroupNames();
            will(returnValue(new String[]{"test"}));
            one(mockBigJobScheduler).getJobNames("test");
            will(returnValue(new String[]{"job1", "job2"}));
            one(mockSmallJobScheduler).getJobNames("test");
            will(returnValue(new String[]{"job3", "job4"}));
            one(mockBigJobScheduler).getJobDetail("job1", "test");
            will(returnValue(job1Detail));
            one(mockBigJobScheduler).getJobDetail("job2", "test");
            will(returnValue(job2Detail));
            one(mockSmallJobScheduler).getJobDetail("job3", "test");
            will(returnValue(job3Detail));
            one(mockSmallJobScheduler).getJobDetail("job4", "test");
            will(returnValue(job4Detail));
        }});

        ModelAndView modelAndView = queueMonitorController.handleRequest(null, null);
        assertTrue(modelAndView.getModelMap().containsKey("QueueItems"));
        List<QueueMonitorItem> queueItems = (List<QueueMonitorItem>) modelAndView.getModelMap().get("QueueItems");
        assertEquals("archive1_creation", queueItems.get(0).getArchiveName());
        assertEquals(QueueMonitorItem.JobType.ArchiveCreation, queueItems.get(0).getJobType());
        assertEquals(QueueMonitorItem.QueueType.Bigjob, queueItems.get(0).getQueueType());

        assertEquals("archive2_creation", queueItems.get(1).getArchiveName());
        assertEquals(QueueMonitorItem.JobType.ArchiveCreation, queueItems.get(1).getJobType());
        assertEquals(QueueMonitorItem.QueueType.Bigjob, queueItems.get(1).getQueueType());

        // creation jobs go before deletion, so 4 is in list before 3...

        assertEquals("archive4_creation", queueItems.get(2).getArchiveName());
        assertEquals(QueueMonitorItem.JobType.ArchiveCreation, queueItems.get(2).getJobType());
        assertEquals(QueueMonitorItem.QueueType.Smalljob, queueItems.get(2).getQueueType());

        assertEquals("archive3_deletion", queueItems.get(3).getArchiveName());
        assertEquals(QueueMonitorItem.JobType.FileDeletion, queueItems.get(3).getJobType());
        assertEquals(QueueMonitorItem.QueueType.Smalljob, queueItems.get(3).getQueueType());


    }

    private JobDetail makeJobDetail(final String archiveName, final boolean creation) {
        final JobDetail jobDetail = new JobDetail();
        JobDataMap jobDataMap = new JobDataMap();
        Object runner;
        if (creation) {
            FilePackagerBean filePackagerBean = new FilePackagerBean();
            filePackagerBean.setArchiveLogicalName(archiveName);
            jobDataMap.put(JobDelegate.DATA_BEAN, filePackagerBean);
        } else {
            ArchiveDeletionBean archiveDeletionBean = new ArchiveDeletionBean();
            archiveDeletionBean.setArchiveName(archiveName);
            jobDataMap.put(JobDelegate.DATA_BEAN, archiveDeletionBean);
        }

        jobDetail.setJobDataMap(jobDataMap);

        return jobDetail;
    }
}
