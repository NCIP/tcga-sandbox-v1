package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;

import java.io.File;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Class to test Level2DataCacheEnqueuer
 * 
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class Level2DataCacheEnqueuerFastTest {
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String appContextFile = "samples/applicationContext-dbunit.xml";

	private Mockery context = new JUnit4Mockery();
	private ApplicationContext appContext;
	private Scheduler scheduler;
	private Level2DataCacheEnqueuer level2DataCacheEnqueuer;

	@Before
	public void setup() {

		appContext = new ClassPathXmlApplicationContext(appContextFile);
		scheduler = context.mock(Scheduler.class, "level2DataScheduler");
		level2DataCacheEnqueuer = (Level2DataCacheEnqueuer) appContext
				.getBean("level2DataCacheEnqueuer");
		level2DataCacheEnqueuer.setScheduler(scheduler);
	}

	@Test
	public void testGetJobName() {
		final Level2DataFilterBean level2DataFilterBean = getLevel2DataFilterBean();
		final String jobName = level2DataFilterBean.getDiseaseAbbreviation()
				+ "_" + level2DataFilterBean.getCenterDomainName() + "_"
				+ level2DataFilterBean.getPlatformName();

		assertTrue(jobName.equals(level2DataCacheEnqueuer
				.getJobName(level2DataFilterBean)));
	}

	private Level2DataFilterBean getLevel2DataFilterBean() {
		final Level2DataFilterBean level2DataFilterBean = new Level2DataFilterBean();
		level2DataFilterBean.setDiseaseAbbreviation("GBM");
		level2DataFilterBean.setCenterDomainName("jhu-usc.edu");
		level2DataFilterBean.setPlatformName("HumanMethylation27");
		return level2DataFilterBean;
	}

	private JobDetail getExistingJobDetail(final long experimentId) {
		final Level2DataFilterBean level2DataFilterBean = getLevel2DataFilterBean();
		level2DataFilterBean.addExperimentId(experimentId);

		final JobDetail existingJobDetail = new JobDetail();
		existingJobDetail.setName(level2DataCacheEnqueuer
				.getJobName(level2DataFilterBean));
		existingJobDetail.setGroup(Level2DataCacheEnqueuer.getJobGroupName());
		existingJobDetail.getJobDataMap().put(ConstantValues.DATA_BEAN,
				level2DataFilterBean);
		return existingJobDetail;
	}

	private Matcher<JobDetail> validateJobDetail(
			final Level2DataFilterBean level2DataFilterBean) {

		return new TypeSafeMatcher<JobDetail>() {

			@Override
			public boolean matchesSafely(final JobDetail jobDetail) {
				Level2DataFilterBean jobDetailData = (Level2DataFilterBean) jobDetail
						.getJobDataMap().get(ConstantValues.DATA_BEAN);
				return level2DataCacheEnqueuer.getJobName(level2DataFilterBean)
						.equals(jobDetail.getName())
						&& Level2DataCacheEnqueuer.getJobGroupName().equals(
								jobDetail.getGroup())
						&& level2DataFilterBean.equals(jobDetailData);
			}

			public void describeTo(final Description description) {
				description.appendText("Valid match");
			}
		};
	}

	private Matcher<JobDetail> validateUpdatedJobDetail(
			final Level2DataFilterBean level2DataFilterBean,
			final long firstExperimentId) {

		return new TypeSafeMatcher<JobDetail>() {

			@Override
			public boolean matchesSafely(final JobDetail jobDetail) {
				Level2DataFilterBean jobDetailData = (Level2DataFilterBean) jobDetail
						.getJobDataMap().get(ConstantValues.DATA_BEAN);
				Set<Long> allExperimentIds = level2DataFilterBean
						.getExperimentIdList();
				allExperimentIds.add(firstExperimentId);

				return level2DataCacheEnqueuer.getJobName(level2DataFilterBean)
						.equals(jobDetail.getName())
						&& Level2DataCacheEnqueuer.getJobGroupName().equals(
								jobDetail.getGroup())
						&& level2DataFilterBean.getPlatformName().equals(
								jobDetailData.getPlatformName())
						&& level2DataFilterBean.getCenterDomainName().equals(
								jobDetailData.getCenterDomainName())
						&& level2DataFilterBean.getDiseaseAbbreviation()
								.equals(jobDetailData.getDiseaseAbbreviation())
						&& jobDetailData.getExperimentIdList().containsAll(
								allExperimentIds);
			}

			public void describeTo(final Description description) {
				description.appendText("Valid match");
			}
		};
	}

	private Matcher<Trigger> validateTrigger(final JobDetail jobDetail) {

		return new TypeSafeMatcher<Trigger>() {

			@Override
			public boolean matchesSafely(final Trigger trigger) {

				return trigger.getName().equals(jobDetail.getName())
						&& trigger.getGroup().equals(jobDetail.getGroup())
						&& trigger.getJobName().equals(jobDetail.getName());
			}

			public void describeTo(final Description description) {
				description.appendText("Valid match");
			}
		};
	}

}
