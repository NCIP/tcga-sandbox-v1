package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Level2DataCacheGeneratorI;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Class to test Level2DataCacheGenerationJob
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class Level2DataCacheGenerationJobFastTest {
    private final String JOB_NAME = "job";
    private final String JOB_GROUP_NAME = "group";
    private final Mockery context = new JUnit4Mockery();
    private final Scheduler mockScheduler = context.mock(Scheduler.class);
    private final Job job = context.mock(Job.class);
    private Level2DataCacheGenerationJob level2DataCacheGenerationJob;
    private Level2DataCacheGeneratorI mockLevel2DataCacheGenerator;
    private JobExecutionContext jobExecutionContext;

    @Before
    public void setup() throws Exception {
        mockLevel2DataCacheGenerator = context.mock(Level2DataCacheGeneratorI.class);
        level2DataCacheGenerationJob = new Level2DataCacheGenerationJob() {
            protected void initFields() {
                level2DataCacheGenerator = mockLevel2DataCacheGenerator;
            }
        };

        jobExecutionContext = getJobExecutionContext();
    }

    @Test
    public void runNewJob() throws Exception {
        final Level2DataFilterBean level2DataFilterBean = getLevel2DataFilterBean();
        jobExecutionContext.getJobDetail().getJobDataMap().put(ConstantValues.DATA_BEAN, level2DataFilterBean);

        context.checking(new Expectations() {{
            one(mockLevel2DataCacheGenerator).generateCacheFiles(level2DataFilterBean);
            one(mockScheduler).deleteJob(JOB_NAME, JOB_GROUP_NAME);

        }});

        level2DataCacheGenerationJob.execute(jobExecutionContext);
    }

    private JobExecutionContext getJobExecutionContext() {
        final JobDetail jobDetail = new JobDetail();
        jobDetail.setName(JOB_NAME);
        jobDetail.setGroup(JOB_GROUP_NAME);
        final Trigger trigger = new SimpleTrigger();
        final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(jobDetail, trigger, null, false, null, null, null, null);
        return new JobExecutionContext(mockScheduler, triggerFiredBundle, job);
    }

    private Level2DataFilterBean getLevel2DataFilterBean() {
        final Level2DataFilterBean level2DataFilterBean = new Level2DataFilterBean();
        level2DataFilterBean.setDiseaseAbbreviation("GBM");
        level2DataFilterBean.setCenterDomainName("jhu-usc.edu");
        level2DataFilterBean.setPlatformName("HumanMethylation27");
        return level2DataFilterBean;
    }
}
