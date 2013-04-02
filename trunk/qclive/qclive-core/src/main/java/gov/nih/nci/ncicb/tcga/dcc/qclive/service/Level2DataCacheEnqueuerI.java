package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

/**
 * Interface which provides APIs for scheduling Level2DataCacheGeneration jobs
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface Level2DataCacheEnqueuerI {

    public JobDetail addJob(final Level2DataFilterBean level2DataFilterBean) throws SchedulerException;

    public void scheduleTrigger(final JobDetail jobDetail) throws SchedulerException;

    public String getJobName(Level2DataFilterBean level2DataFilterBean);
}
