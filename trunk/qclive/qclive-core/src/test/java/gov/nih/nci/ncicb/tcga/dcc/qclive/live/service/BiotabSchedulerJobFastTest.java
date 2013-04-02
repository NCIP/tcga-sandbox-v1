package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataSourceMaker;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseRoutingDataSource;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;

import java.util.Calendar;

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
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;

@RunWith(JMock.class)
public class BiotabSchedulerJobFastTest {

	BiotabSchedulerJob buitabSchedulerJob;
	private final Mockery context = new JUnit4Mockery();
	private final Scheduler mockScheduler = context.mock(Scheduler.class);
	private final Job job = context.mock(Job.class);
	private final ApplicationContext mockCtx = context
			.mock(ApplicationContext.class);
	private final DataSourceMaker mockDsMaker = context
			.mock(DataSourceMaker.class);
	private final MailErrorHelper mockErrorHelper = context
			.mock(MailErrorHelper.class);

	
	private DiseaseRoutingDataSource diseaseDs;
	private SimpleTrigger simpleTrigger;
	private JobDetail jobDetail;
	private SchedulerContext quartCtx;

	@Before
	public void setUp() throws SchedulerException {
		new SpringApplicationContext().setApplicationContext(mockCtx);
		buitabSchedulerJob = new BiotabSchedulerJob();
		simpleTrigger = new SimpleTrigger();
		context.checking(new Expectations() {
			{
				one(mockDsMaker).makeDataSource("tcgaGBMDS");
				will(returnValue(null));
			}
		});
		diseaseDs = new DiseaseRoutingDataSource(mockDsMaker, "GBM:tcgaGBMDS");
		jobDetail = new JobDetail();
		jobDetail.setDurability(true);
		jobDetail.setVolatility(false);
		
		quartCtx = new SchedulerContext();
		quartCtx.put(ConstantValues.BIOTAB_GENERATOR_DELAY, "1");
	}

	@Test
	public void testExecute() throws Exception {
		Calendar whenToRun = Calendar.getInstance();
		whenToRun.add(Calendar.MINUTE, 1);
		
		simpleTrigger.setStartTime(whenToRun.getTime());
		simpleTrigger.setName("GBM_" + ConstantValues.BIOTAB_GENERATOR_TRIGGER);
		
		context.checking(new Expectations() {
			{
				one(mockCtx).getBean(ConstantValues.BIOTAB_SCHEDULER);
				will(returnValue(mockScheduler));				
				one(mockCtx).getBean(ConstantValues.DISEASE_ROUTING_DS);
				will(returnValue(diseaseDs));												
				one(mockScheduler).getContext();
				will(returnValue(quartCtx));
				one(mockScheduler).scheduleJob(with(any(JobDetail.class)),  with(any(SimpleTrigger.class)));

			}
		});
		buitabSchedulerJob.execute(getJobExecutionContext());
	}

	@Test
	public void testFailExecute() throws Exception {
		Calendar whenToRun = Calendar.getInstance();
		whenToRun.add(Calendar.MINUTE, 1);

		
		simpleTrigger.setStartTime(whenToRun.getTime());
		simpleTrigger.setName("GBM_" + ConstantValues.BIOTAB_GENERATOR_TRIGGER);
		
		context.checking(new Expectations() {
			{
				one(mockCtx).getBean(ConstantValues.BIOTAB_SCHEDULER);
				will(returnValue(mockScheduler));				
				one(mockCtx).getBean(ConstantValues.DISEASE_ROUTING_DS);
				will(returnValue(diseaseDs));				
				one(mockCtx).getBean(
						ConstantValues.MAIL_ERROR_HELPER_SPRING_BEAN_NAME);
				will(returnValue(mockErrorHelper));
				one(mockScheduler).getContext();
				will(returnValue(quartCtx));
				one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(SimpleTrigger.class)));
				will(throwException(new SchedulerException("something blew up")));																
				one(mockErrorHelper).send(with(any(String.class)),with(any(String.class)));
			}
		});
		buitabSchedulerJob.execute(getJobExecutionContext());
	}

	
	
	private JobExecutionContext getJobExecutionContext() {
		final JobDetail jobDetail = new JobDetail();
		final Trigger trigger = new SimpleTrigger();
		final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(
				jobDetail, trigger, null, false, null, null, null, null);
		return new JobExecutionContext(mockScheduler, triggerFiredBundle, job);
	}

}
