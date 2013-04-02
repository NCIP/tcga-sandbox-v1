package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentDAO;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.LiveI;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.jdbcjobstore.QueueJobStore;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Test class for experimentchecker job
 * 
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */

public class ExperimentCheckerJobFastTest {

	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples/qclive").getPath()
			+ File.separator;
	private final String ARCHIVE = SAMPLE_DIR + "test.org_GBM.bio.1.0.0.tar.gz";
	private final String BACKUP_ARCHIVE = SAMPLE_DIR
			+ "test.org_GBM.minbio.1.0.0.tar.gz.bak";
	private final String DEPLOY = SAMPLE_DIR + "deploy" + File.separator;
	private final String EXPERIMENT_NAME = "test.archive_GBM.bio";
	private final String EXPERIMENT_TYPE_CGCC = "CGCC";
	private final String EXPERIMENT_TYPE_BCR = "BCR";

	private final Mockery context = new JUnit4Mockery();
	private final LiveI mockLive = context.mock(LiveI.class);
	private final ExperimentDAO mockExperimentDAO = context
			.mock(ExperimentDAO.class);
	private final Scheduler mockScheduler = context.mock(Scheduler.class);
	private final Job job = context.mock(Job.class);
	private ExperimentCheckerJob experimentCheckerJob;
	private JobExecutionContext jobExecutionContext;

	@Before
	public void setup() throws Exception {
		experimentCheckerJob = new ExperimentCheckerJob() {
			protected void initFields() {
				experimentDAO = mockExperimentDAO;
				live = mockLive;
			}
		};

		jobExecutionContext = getJobExecutionContext();

		FileUtil.copy(ARCHIVE, BACKUP_ARCHIVE);
	}

	@Test
	public void runNewJobCGCC() throws SchedulerException {
        final String[] scheduledTriggers = new String[]{"trigger1"};
		jobExecutionContext.getMergedJobDataMap().put(
				LiveScheduler.EXPERIMENT_NAME, EXPERIMENT_NAME);
		jobExecutionContext.getMergedJobDataMap().put(
				LiveScheduler.EXPERIMENT_TYPE, EXPERIMENT_TYPE_CGCC);
		jobExecutionContext.getMergedJobDataMap().put(
				LiveScheduler.ARCHIVE_NAME, ARCHIVE);

		context.checking(new Expectations() {
			{
				one(mockLive).checkExperiment(
						(String) jobExecutionContext.getMergedJobDataMap().get(
								LiveScheduler.EXPERIMENT_NAME),
						(String) jobExecutionContext.getMergedJobDataMap().get(
								LiveScheduler.EXPERIMENT_TYPE), null);
                one(mockScheduler).getTriggerNames(EXPERIMENT_NAME);
                will(returnValue(scheduledTriggers));

			}
		});

		experimentCheckerJob.execute(jobExecutionContext);
	}

	@Test
	public void runNewJobBCR() throws SchedulerException {
        final String[] scheduledTriggers = new String[]{"trigger1"};
		jobExecutionContext.getMergedJobDataMap().put(
				LiveScheduler.EXPERIMENT_NAME, EXPERIMENT_NAME);
		jobExecutionContext.getMergedJobDataMap().put(
				LiveScheduler.EXPERIMENT_TYPE, EXPERIMENT_TYPE_BCR);
		jobExecutionContext.getMergedJobDataMap().put(
				LiveScheduler.ARCHIVE_NAME, ARCHIVE);

		context.checking(new Expectations() {
			{
				one(mockLive).checkExperiment(
						(String) jobExecutionContext.getMergedJobDataMap().get(
								LiveScheduler.EXPERIMENT_NAME),
						(String) jobExecutionContext.getMergedJobDataMap().get(
								LiveScheduler.EXPERIMENT_TYPE), ARCHIVE, null);
                one(mockScheduler).getTriggerNames(EXPERIMENT_NAME);
                will(returnValue(scheduledTriggers));

			}
		});

		experimentCheckerJob.execute(jobExecutionContext);
	}

	@Test
	public void reRunInterruptedJob() throws Exception {
        final String[] scheduledTriggers = new String[]{"trigger1"};
		final String deployedArchivepath = DEPLOY + getArchiveName();
		final File deployedArchiveDir = new File(deployedArchivepath);
		final Archive archive = new Archive();
		jobExecutionContext.getMergedJobDataMap().put(
				LiveScheduler.EXPERIMENT_NAME, EXPERIMENT_NAME);
		jobExecutionContext.getMergedJobDataMap().put(
				LiveScheduler.EXPERIMENT_TYPE, EXPERIMENT_TYPE_CGCC);
		jobExecutionContext.getMergedJobDataMap().put(
				LiveScheduler.ARCHIVE_NAME, ARCHIVE);
		jobExecutionContext.getMergedJobDataMap().put(
				QueueJobStore.RECOVERED_INTERRUPTED_JOB, true);

		context.checking(new Expectations() {
			{
				one(mockExperimentDAO).getArchiveByName(getArchiveName());
				will(returnValue(archive));
				one(mockExperimentDAO).getDeployDirectoryPath(archive);
				will(returnValue(deployedArchiveDir));
				one(mockExperimentDAO).updateArchiveStatus(archive);
				one(mockLive).checkExperiment(
						(String) jobExecutionContext.getMergedJobDataMap().get(
								LiveScheduler.EXPERIMENT_NAME),
						(String) jobExecutionContext.getMergedJobDataMap().get(
								LiveScheduler.EXPERIMENT_TYPE), null);
                one(mockScheduler).getTriggerNames(EXPERIMENT_NAME);
                will(returnValue(scheduledTriggers));


			}
		});

		experimentCheckerJob.execute(jobExecutionContext);
	}

	@Test
	public void cleanup() throws Exception {
		final File deployDir = createDeployDir();
		final String deployedArchivepath = DEPLOY + getArchiveName();
		final File deployedArchiveDir = new File(deployedArchivepath);
		final Archive archive = new Archive();
		final String explodedDirpath = SAMPLE_DIR + getArchiveName();

		FileUtil.makeDir(deployedArchivepath);
		context.checking(new Expectations() {
			{
				one(mockExperimentDAO).getArchiveByName(getArchiveName());
				will(returnValue(archive));
				one(mockExperimentDAO).getDeployDirectoryPath(archive);
				will(returnValue(deployedArchiveDir));
				one(mockExperimentDAO).updateArchiveStatus(archive);

			}
		});

		experimentCheckerJob.cleanup(ARCHIVE);
		assertTrue(!deployedArchiveDir.exists());
		final File explodedDir = new File(explodedDirpath);
		assertTrue(explodedDir.exists());
		// cleanup
		FileUtil.deleteDir(deployDir);
		FileUtil.deleteDir(explodedDir);
	}


    @Test
    public void runJobWithMultipleTriggers() throws  SchedulerException {
        final String[] scheduledTriggers = new String[]{"trigger1", "trigger2"};
        jobExecutionContext.getMergedJobDataMap().put(
                LiveScheduler.EXPERIMENT_NAME, EXPERIMENT_NAME);
        jobExecutionContext.getMergedJobDataMap().put(
                LiveScheduler.EXPERIMENT_TYPE, EXPERIMENT_TYPE_CGCC);
        jobExecutionContext.getMergedJobDataMap().put(
                LiveScheduler.ARCHIVE_NAME, ARCHIVE);

        context.checking(new Expectations() {
            {
                exactly(2).of(mockScheduler).getTriggerNames(EXPERIMENT_NAME);
                will(returnValue(scheduledTriggers));

            }
        });

        experimentCheckerJob.execute(jobExecutionContext);
    }


    @Test
    public void runJobWithQCLiveGroupName() throws SchedulerException {
        final String[] scheduledTriggers = new String[]{"trigger1", "trigger2"};
        jobExecutionContext = getJobExecutionContext("QCLive");
        jobExecutionContext.getMergedJobDataMap().put(
                LiveScheduler.EXPERIMENT_NAME, EXPERIMENT_NAME);
        jobExecutionContext.getMergedJobDataMap().put(
                LiveScheduler.EXPERIMENT_TYPE, EXPERIMENT_TYPE_CGCC);
        jobExecutionContext.getMergedJobDataMap().put(
                LiveScheduler.ARCHIVE_NAME, ARCHIVE);

        context.checking(new Expectations() {
            {
                exactly(2).of(mockScheduler).getTriggerNames("QCLive");
                will(returnValue(scheduledTriggers));
                one(mockLive).checkExperiment(
                        (String) jobExecutionContext.getMergedJobDataMap().get(
                                LiveScheduler.EXPERIMENT_NAME),
                        (String) jobExecutionContext.getMergedJobDataMap().get(
                                LiveScheduler.EXPERIMENT_TYPE),  null);

            }
        });

        experimentCheckerJob.execute(jobExecutionContext);
    }


    private JobExecutionContext getJobExecutionContext() {
        return getJobExecutionContext(EXPERIMENT_NAME);
    }

	private JobExecutionContext getJobExecutionContext(final String groupName) {
		final JobDetail jobDetail = new JobDetail();
        jobDetail.setGroup(groupName);
		final Trigger trigger = new SimpleTrigger();
		final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(
				jobDetail, trigger, null, false, null, null, null, null);
		return new JobExecutionContext(mockScheduler, triggerFiredBundle, job);
	}

	private String getArchiveName() throws Exception {
		return FileUtil.getFilenameWithoutExtension(ARCHIVE, FileUtil.TAR_GZ);
	}

	private File createDeployDir() {
		File deployDir = new File(DEPLOY);
		deployDir.mkdir();
		return deployDir;
	}

	@After
	public void tearDown() throws Exception {
		FileUtil.move(BACKUP_ARCHIVE, ARCHIVE);

	}

}
