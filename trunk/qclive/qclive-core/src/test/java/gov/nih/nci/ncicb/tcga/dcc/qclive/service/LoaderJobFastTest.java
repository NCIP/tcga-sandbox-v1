package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.LoaderArchive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.DummyFileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.Loader;

import java.io.File;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Class to test auto loader jobs
 * 
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */
public class LoaderJobFastTest {
	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String SAMPLE_ARCHIVE = SAMPLE_DIR + "qclive"
			+ File.separator + "intgen.org_GBM.minbio.1.0.0";

	private final Mockery context = new JUnit4Mockery();
	private final Job job = context.mock(Job.class);
	private final String JOB_NAME = "job";
	private final String JOB_GROUP_NAME = "group";

	private JobExecutionContext jobExecutionContext;
	private LoaderRunnerI mockLoaderRunner;
	private Scheduler mockScheduler;
	private Level2DataCacheEnqueuerI mockLevel2DataCacheEnqueuer;
	private LoaderJob loaderJob;

	@Before
	public void setup() throws Exception {
		mockScheduler = context.mock(Scheduler.class);
		mockLevel2DataCacheEnqueuer = context
				.mock(Level2DataCacheEnqueuerI.class);
		mockLoaderRunner = context.mock(LoaderRunnerI.class);

		loaderJob = new LoaderJob() {
			protected void initFields() {
				level2DataCacheEnqueuer = mockLevel2DataCacheEnqueuer;
			}
		};

		jobExecutionContext = getJobExecutionContext();
	}

	@Test
	public void runJob() throws Exception {
		final String[] jobNames = new String[] { "Job1", "Job2", "Job3" };
		final Loader loader = getLoader();

		jobExecutionContext.getJobDetail().getJobDataMap()
				.put("loaderRunner", mockLoaderRunner);

		context.checking(new Expectations() {
			{
				one(mockLoaderRunner).runJob();
				one(mockScheduler).getJobNames(JOB_GROUP_NAME);
				will(returnValue(jobNames));
				one(mockLoaderRunner).getLoader();
				will(returnValue(loader));
				one(mockLevel2DataCacheEnqueuer).addJob(
						with(validateDataFilterBean(loader)));

			}
		});

		loaderJob.executeInternal(jobExecutionContext);
	}

	@Test
	public void runLastJob() throws Exception {
		final String[] jobNames = new String[] { "Job3" };
		final Loader loader = getLoader();
		final JobDetail jobDetail = getJobDetail("Job3");
		jobExecutionContext.getJobDetail().getJobDataMap()
				.put("loaderRunner", mockLoaderRunner);

		context.checking(new Expectations() {
			{
				one(mockLoaderRunner).runJob();
				one(mockScheduler).getJobNames(JOB_GROUP_NAME);
				will(returnValue(jobNames));
				one(mockLoaderRunner).getLoader();
				will(returnValue(loader));
				one(mockLevel2DataCacheEnqueuer).addJob(
						with(validateDataFilterBean(loader)));
				will(returnValue(jobDetail));
				one(mockLevel2DataCacheEnqueuer).scheduleTrigger(jobDetail);

			}
		});

		loaderJob.executeInternal(jobExecutionContext);
	}

	private Matcher<Level2DataFilterBean> validateDataFilterBean(
			final Loader loader) {

		return new TypeSafeMatcher<Level2DataFilterBean>() {

			@Override
			public boolean matchesSafely(
					final Level2DataFilterBean level2DataFilterBean) {

				return level2DataFilterBean.getPlatformName().equals(
						loader.getArchive().getPlatform())
						&& level2DataFilterBean.getDiseaseAbbreviation()
								.equals(loader.getArchive().getDisease())
						&& level2DataFilterBean.getCenterDomainName().equals(
								loader.getArchive().getCenter())
						&& level2DataFilterBean.getExperimentIdList().contains(
								loader.getArchive().getExperimentId());
			}

			public void describeTo(final Description description) {
				description.appendText("Valid match");
			}
		};
	}

	private JobExecutionContext getJobExecutionContext() {
		final JobDetail jobDetail = getJobDetail(JOB_NAME);
		final Trigger trigger = new SimpleTrigger();
		final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(
				jobDetail, trigger, null, false, null, null, null, null);
		return new JobExecutionContext(mockScheduler, triggerFiredBundle, job);
	}

	private Loader getLoader() throws Exception {
		final Loader loader = new Loader();
		final LoaderArchive archive = new LoaderArchive(SAMPLE_ARCHIVE,
				new DummyFileTypeLookup());
		archive.setExperimentId(1);
		loader.setArchive(archive);
		return loader;
	}

	private JobDetail getJobDetail(final String jobName) {
		final JobDetail jobDetail = new JobDetail();
		jobDetail.setName(jobName);
		jobDetail.setGroup(JOB_GROUP_NAME);
		return jobDetail;
	}

}
