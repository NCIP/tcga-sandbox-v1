/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveDeletionBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.JobDelegate;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.QueueMonitorItem;
import org.apache.log4j.Level;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: David Nassau
 * <p/>
 * Prepares a table view for monitoring the queue
 */
public class QueueMonitorController implements Controller {

    private Scheduler bigJobScheduler;
    private Scheduler smallJobScheduler;
    private final ProcessLogger logger = new ProcessLogger();


    public ModelAndView handleRequest(final HttpServletRequest httpServletRequest,
                                      final HttpServletResponse httpServletResponse) {

        ModelAndView modelAndView;
        try {
            final List<QueueMonitorItem> creationJobs = new ArrayList<QueueMonitorItem>();
            final List<QueueMonitorItem> deletionJobs = new ArrayList<QueueMonitorItem>();
            getJobsFromQueue(bigJobScheduler, creationJobs, deletionJobs);
            getJobsFromQueue(smallJobScheduler, creationJobs, deletionJobs);

            // put all in one list, with creation jobs first
            final List<QueueMonitorItem> qitems = new ArrayList<QueueMonitorItem>();
            qitems.addAll(creationJobs);
            qitems.addAll(deletionJobs);
            modelAndView = new ModelAndView("queueMonitor", "QueueItems", qitems);
        }
        catch (SchedulerException e) {
            modelAndView = new ModelAndView("dataAccessMatrixError", "ErrorInfo", new ErrorInfo(e));
        }
        return modelAndView;
    }

    private void getJobsFromQueue(final Scheduler scheduler, final List<QueueMonitorItem> creationJobs,
                                  final List<QueueMonitorItem> deletionJobs) throws SchedulerException {
        int i = 0;
        for (final String group : scheduler.getJobGroupNames()) {
            for (final String jobname : scheduler.getJobNames(group)) {
                logger.logToLogger(Level.DEBUG,
                        new StringBuilder().append("QueueMonitorController: about to get detail for job '").
                                append(jobname).append("' from group '").append(group).append("'").toString());
                final JobDetail jobdetail = scheduler.getJobDetail(jobname, group);
                if (jobdetail != null) {
                    final Object dataBean = jobdetail.getJobDataMap().get(JobDelegate.DATA_BEAN);
                    if (dataBean instanceof FilePackagerBean) {
                        final FilePackagerBean filePackagerBean = (FilePackagerBean) dataBean;
                        final QueueMonitorItem qitem = createMonitorItem(filePackagerBean.getArchiveLogicalName(),
                                filePackagerBean.getEstimatedUncompressedSize(),
                                i++,
                                QueueMonitorItem.JobType.ArchiveCreation,
                                scheduler);
                        creationJobs.add(qitem);
                    } else if (dataBean instanceof ArchiveDeletionBean) {
                        final ArchiveDeletionBean archiveDeletionBean = (ArchiveDeletionBean) dataBean;
                        final QueueMonitorItem qitem = createMonitorItem(archiveDeletionBean.getArchiveName(),
                                -1,
                                i++,
                                QueueMonitorItem.JobType.FileDeletion,
                                scheduler);
                        deletionJobs.add(qitem);

                    }

                }
            }
        }
    }

    private QueueMonitorItem createMonitorItem(final String archiveName, final long totalSize, final int index,
                                               final QueueMonitorItem.JobType jobType, final Scheduler scheduler) {
        final QueueMonitorItem qitem = new QueueMonitorItem();
        qitem.setJobType(jobType);
        qitem.setArchiveName(archiveName);
        qitem.setTotalSize(totalSize);
        if (scheduler.equals(smallJobScheduler)) {
            qitem.setQueueType(QueueMonitorItem.QueueType.Smalljob);
        } else {
            qitem.setQueueType(QueueMonitorItem.QueueType.Bigjob);
        }
        qitem.setIndex(index);
        return qitem;
    }

    public void setBigJobScheduler(final Scheduler bigJobScheduler) {
        this.bigJobScheduler = bigJobScheduler;
    }

    public void setSmallJobScheduler(final Scheduler smallJobScheduler) {
        this.smallJobScheduler = smallJobScheduler;
    }
}
