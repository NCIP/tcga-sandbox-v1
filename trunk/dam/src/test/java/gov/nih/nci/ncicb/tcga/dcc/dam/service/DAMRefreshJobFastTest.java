package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMHelperI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.StaticMatrixModelFactoryI;

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
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Test class for DAMRefreshJob
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DAMRefreshJobFastTest {
    private Mockery context;
    private JobExecutionContext jobExecutionContext;
    private Scheduler mockScheduler;
    private Job job;
    private DAMRefreshJob damRefreshJob;
    private DAMHelperI damHelper;
    private StaticMatrixModelFactoryI staticMatrixModelFactoryI;

    @Before
    public void setup() {
        context = new JUnit4Mockery();
        mockScheduler = context.mock(Scheduler.class);
        damHelper = context.mock(DAMHelperI.class);
        staticMatrixModelFactoryI = context.mock(StaticMatrixModelFactoryI.class);
        job = context.mock(Job.class);
        jobExecutionContext = getJobExecutionContext();

        damRefreshJob = new DAMRefreshJob() {
            protected DAMHelperI getDAMHelper() {
                return damHelper;
            }

            protected ProcessLogger getLogger(SchedulerContext schedulerContext) {
                return new ProcessLogger();
            }

            protected StaticMatrixModelFactoryI getStaticMatrixModelFactory(SchedulerContext schedulerContext) {
                return staticMatrixModelFactoryI;
            }
        };

    }

    @Test
    public void runRefreshJob() throws JobExecutionException, DataAccessMatrixQueries.DAMQueriesException, SchedulerException {
        context.checking(new Expectations() {{
            one(mockScheduler).getContext();
            one(staticMatrixModelFactoryI).refreshAll();
            one(damHelper).refreshTumorCenterPlatformInfoCache();
        }});
        damRefreshJob.executeInternal(jobExecutionContext);

    }

    private JobExecutionContext getJobExecutionContext() {
        final JobDetail jobDetail = new JobDetail();
        final Trigger trigger = new SimpleTrigger();
        final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(jobDetail, trigger, null, false, null, null, null, null);
        return new JobExecutionContext(mockScheduler, triggerFiredBundle, job);
    }
}
