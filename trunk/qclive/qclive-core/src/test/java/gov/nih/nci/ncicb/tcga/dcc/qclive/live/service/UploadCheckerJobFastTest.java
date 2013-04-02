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
import org.junit.Before;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.jdbcjobstore.QueueJobStore;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Test class for uploadchecker job
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class UploadCheckerJobFastTest {

    private static final String SAMPLE_DIR =
    	Thread.currentThread().getContextClassLoader().getResource("samples/qclive").getPath() + File.separator;
    private final String ARCHIVE = SAMPLE_DIR + "test.archive_GBM.bio.1.3.0.tar.gz";
    private final Mockery context = new JUnit4Mockery();
    private final LiveI mockLive = context.mock(LiveI.class);
    private final ExperimentDAO mockExperimentDAO = context.mock(ExperimentDAO.class);
    private final Scheduler mockScheduler = context.mock(Scheduler.class);
    private TriggerFiredBundle triggerBundle;
    private final Job job = context.mock(Job.class);
    private UploadCheckerJob uploadCheckerJob;
    private JobExecutionContext jobExecutionContext;

    @Before
    public void setup() throws Exception {
        uploadCheckerJob = new UploadCheckerJob() {
            protected void initFields() {
                experimentDAO = mockExperimentDAO;
                live = mockLive;
            }
        };

        jobExecutionContext = getJobExecutionContext();

    }

    @Test
    public void runNewJob() throws JobExecutionException {

        jobExecutionContext.getMergedJobDataMap().put("file", ARCHIVE);
        jobExecutionContext.getMergedJobDataMap().put("md5ValidationAttempts", 0);

        context.checking(new Expectations() {{
            one(mockLive).processUpload((String) jobExecutionContext.getMergedJobDataMap().get("file"),
                    (Integer) jobExecutionContext.getMergedJobDataMap().get("md5ValidationAttempts"),null);

        }});

        uploadCheckerJob.execute(jobExecutionContext);
    }

    @Test
    public void reRunInterruptedJob() throws Exception {
        final Archive archive = new Archive();
        jobExecutionContext.getMergedJobDataMap().put("file", ARCHIVE);
        jobExecutionContext.getMergedJobDataMap().put("md5ValidationAttempts", 0);
        jobExecutionContext.getMergedJobDataMap().put(QueueJobStore.RECOVERED_INTERRUPTED_JOB, true);

        context.checking(new Expectations() {{
            one(mockExperimentDAO).getArchiveByName(getArchiveName());
            will(returnValue(archive));
            one(mockExperimentDAO).updateArchiveStatus(archive);
            one(mockLive).processUpload((String) jobExecutionContext.getMergedJobDataMap().get("file"),
                    (Integer) jobExecutionContext.getMergedJobDataMap().get("md5ValidationAttempts"),null);

        }});

        uploadCheckerJob.execute(jobExecutionContext);
    }

    @Test
    public void cleanup() throws Exception {
        final String explodedDirpath = SAMPLE_DIR + getArchiveName();
        FileUtil.makeDir(explodedDirpath);
        final Archive archive = new Archive();

        context.checking(new Expectations() {{
            one(mockExperimentDAO).getArchiveByName(getArchiveName());
            will(returnValue(archive));
            one(mockExperimentDAO).updateArchiveStatus(archive);

        }});

        uploadCheckerJob.cleanup(ARCHIVE);
        File explodedDir = new File(explodedDirpath);
        assertTrue(!explodedDir.exists());
    }
    @Test
    public void cleanupNoArchive() throws Exception {
        final String explodedDirpath = SAMPLE_DIR + getArchiveName();
        FileUtil.makeDir(explodedDirpath);
        final Archive archive = new Archive();

        context.checking(new Expectations() {{
            one(mockExperimentDAO).getArchiveByName(getArchiveName());
            will(returnValue(null));
            one(mockExperimentDAO).updateArchiveStatus(archive);

        }});

        uploadCheckerJob.cleanup(ARCHIVE);
        File explodedDir = new File(explodedDirpath);
        assertTrue(!explodedDir.exists());
    }


    private JobExecutionContext getJobExecutionContext() {
        final JobDetail jobDetail = new JobDetail();
        final Trigger trigger = new SimpleTrigger();
        final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(jobDetail, trigger, null, false, null, null, null, null);
        return new JobExecutionContext(mockScheduler, triggerFiredBundle, job);
    }

    private String getArchiveName() throws Exception {
        return FileUtil.getFilenameWithoutExtension(ARCHIVE, FileUtil.TAR_GZ);
    }

}
