package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * Delegates job to the corresponding job action class.
 * To use this DATA_BEAN and JOB_BEAN_NAME parameters should be added in Scheduler JobDetail->JobDataMap object
 * The JOB_BEAN_NAME should be defined in spring application context file
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class JobDelegate extends QuartzJobBean {

    public static final String DATA_BEAN = "DataBean";
    public static final String JOB_BEAN_NAME = "JobBeanName";
    protected final Log logger = LogFactory.getLog(getClass());

    protected void executeInternal(final JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            final Object dataBean = jobExecutionContext.getJobDetail().getJobDataMap().get(DATA_BEAN);
            final String jobBeanName = (String) jobExecutionContext.getJobDetail().getJobDataMap().get(JOB_BEAN_NAME);
            final QueueJob queueJob = (QueueJob) SpringApplicationContext.getObject(jobBeanName);

            logger.info("Started JOB for" + jobBeanName + "[" + jobExecutionContext.getJobDetail().getName() + "] at: " + new Date());
            queueJob.run(dataBean);
            logger.info("Completed JOB for" + jobBeanName + "[" + jobExecutionContext.getJobDetail().getName() + "] at: " + new Date());

        } catch (final Exception e) {

            //Log and re-throw as a JobExecutionException in order to be reported appropriately by PersistJobHistoryQuartzPlugin
            final StringBuilder errorMessage = new StringBuilder("Error: [")
                    .append(jobExecutionContext.getJobDetail().getName())
                    .append("]")
                    .append(e.toString());

            logger.error(errorMessage, e);

            throw new JobExecutionException(errorMessage.toString(), e);
        }
    }

}
