package gov.nih.nci.ncicb.tcga.dcc.dam.processors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.FilePackagerEnqueuerI;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test class for FilePackagerFactory
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class FilePackagerFactoryFastTest {

    private static final String TEST_DATA_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String SCRATCH_FOLDER = TEST_DATA_FOLDER
            + "filePackager" + File.separator
            + "scratch" + File.separator;
    private static final String appContextFile = "samples/applicationContext-unittest.xml";
    private static final String DISEASE = "GBM";
    private static final String EMAIL = "test@test.com";
    private static final String PROTECTED_PHYSICAL_PATH = "/protected/";
    private static final String PROTECTED_LOGICAL_PATH = "/protected/logical/";
    private static final String NOT_PROTECTED_PHYSICAL_PATH = "/not_protected/";
    private static final String NOT_PROTECTED_LOGICAL_PATH = "/not_protected/logical/";
    private static final int MAX_FILES = 5;

    private final ApplicationContext appContext = new ClassPathXmlApplicationContext(appContextFile);
    private Mockery mocker = new JUnit4Mockery();
    private FilePackagerEnqueuerI mockFilePackagerEnqueuer;
    private MailSender mockMailSender;
    private FilePackagerFactory fpFactory;
    private List<DataFile> downloadFiles;

    @Before
    public void setup() {
        fpFactory = (FilePackagerFactory) appContext.getBean("filePackagerFactory");
        fpFactory.setNotProtectedArchiveLogicalPath(NOT_PROTECTED_LOGICAL_PATH);
        fpFactory.setProtectedArchiveLogicalPath(PROTECTED_LOGICAL_PATH);
        fpFactory.setNotProtectedArchivePhysicalPath(NOT_PROTECTED_PHYSICAL_PATH);
        fpFactory.setProtectedArchivePhysicalPath(PROTECTED_PHYSICAL_PATH);
        loadFileInfoLists();
        mockFilePackagerEnqueuer = mocker.mock(FilePackagerEnqueuerI.class);
        fpFactory.setFilePackagerEnqueuer(mockFilePackagerEnqueuer);
        mockMailSender = mocker.mock(MailSender.class);
        fpFactory.setMailSender(mockMailSender);
    }

    @Test
    public void createFilePackagerBean() {

        final UUID key = UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00");
        final FilePackagerBean filePackagerBean = fpFactory.createFilePackagerBean(DISEASE, downloadFiles, EMAIL, false, false,
                key, null);

        assertNotNull(filePackagerBean);
        assertTrue(filePackagerBean.getArchivePhysicalName().contains(NOT_PROTECTED_PHYSICAL_PATH));
        assertTrue(filePackagerBean.getArchiveLogicalName().contains(NOT_PROTECTED_LOGICAL_PATH));
        assertEquals(key, filePackagerBean.getKey());
        assertEquals(DISEASE, filePackagerBean.getDisease());
        assertEquals(EMAIL, filePackagerBean.getEmail());
    }

    @Test
    public void enqueueFilePackagerBean() throws Exception {

        final FilePackagerBean filePackagerBean = getTestFilePackagerBean();

        mocker.checking(new Expectations() {{
            one(mockFilePackagerEnqueuer).queueFilePackagerJob(filePackagerBean);
            will(getFilePackagerBeanAction(filePackagerBean));

            one(mockMailSender).send(filePackagerBean.getEmail(), null, "Download Requested",
                    "Your archive request has been submitted to the DCC. You will receive another email when the job is " +
                            "complete, along with a link to download the archive. To check on the status of your request, " +
                            "please use this link: " + filePackagerBean.getStatusCheckUrl(),
                    false);
        }});

        fpFactory.enqueueFilePackagerBean(filePackagerBean);
        assertNotNull(fpFactory.getQuartzJobHistory(filePackagerBean.getKey()));
    }

    @Test
    public void getFilePackagerBean() {

        final FilePackagerBean filePackagerBean = getTestFilePackagerBean();
        initQuartzJobHistory(filePackagerBean);
        fpFactory.putQuartzJobHistory(filePackagerBean.getKey(), filePackagerBean.getUpdatedQuartzJobHistory());

        final QuartzJobHistory testQuartzJobHistory = fpFactory.getQuartzJobHistory(filePackagerBean.getKey());
        assertNotNull(testQuartzJobHistory);
        assertEquals(filePackagerBean.getKey(), testQuartzJobHistory.getKey());
    }

    @Test
    public void getInvalidFilePackagerBean() {
        final QuartzJobHistory testQuartzJobHistory = fpFactory.getQuartzJobHistory(UUID.fromString("00000000-0000-0000-0000-00000000000"));
        assertNull(testQuartzJobHistory);
    }

    @Test
    public void removeFilePackagerBean() {

        final FilePackagerBean filePackagerBean = getTestFilePackagerBean();
        initQuartzJobHistory(filePackagerBean);
        fpFactory.putQuartzJobHistory(filePackagerBean.getKey(), filePackagerBean.getUpdatedQuartzJobHistory());

        QuartzJobHistory testQuartzJobHistory = fpFactory.getQuartzJobHistory(filePackagerBean.getKey());
        assertNotNull(testQuartzJobHistory);

        fpFactory.removeFilePackagerBean(filePackagerBean.getKey());

        testQuartzJobHistory = fpFactory.getQuartzJobHistory(filePackagerBean.getKey());
        assertNull(testQuartzJobHistory);

    }


    private void loadFileInfoLists() {
        emptyScratchFolder();
        downloadFiles = new ArrayList<DataFile>();
        for (int i = 0; i < MAX_FILES; i++) {
            DataFile fi = new DataFileLevelOne();
            fi.setFileId("f" + i);
            fi.setFileName("file" + i);
            fi.setProtected(false);
            fi.setPlatformTypeId("platform" + i);
            fi.setCenterId("center" + i);
            fi.setBarcodes(new ArrayList<String>());
            downloadFiles.add(fi);
        }
    }

    private void emptyScratchFolder() {
        File scratch = new File(SCRATCH_FOLDER);
        if (!scratch.exists()) {
            scratch.mkdir();
        }
        File[] existingFiles = scratch.listFiles();
        for (final File existingFile : existingFiles) {
            existingFile.delete();
        }
    }

    private FilePackagerBean getTestFilePackagerBean() {

        final FilePackagerBean filePackagerBean = new FilePackagerBean();
        filePackagerBean.setKey(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff01"));
        filePackagerBean.setArchivePhysicalPathPrefix(NOT_PROTECTED_PHYSICAL_PATH);
        filePackagerBean.setArchiveLinkSite("https://tcga-data.nci.nih.gov/tcga/blah/blahblah/");
        filePackagerBean.setArchiveLogicalName("test-archive");
        filePackagerBean.setJobWSSubmissionDate(new Date());
        filePackagerBean.setEstimatedUncompressedSize(999L);
        filePackagerBean.setSelectedFiles(downloadFiles);
        filePackagerBean.setStatusCheckUrl("http://whereismydata/jobStatus.htm");
        filePackagerBean.setEmail("test@test");

        return filePackagerBean;
    }

    /**
     * Return an <code>Action</code> that will simulate what the implementation of FilePackagerEnqueuer.queueFilePackagerJob()
     * is doing on the FilePackagerBean
     *
     * @param filePackagerBean the <code>FilePackagerBean</code>
     * @return an <code>Action</code> that will simulate what the implementation of FilePackagerEnqueuer.queueFilePackagerJob() on the FilePackagerBean
     */
    private Action getFilePackagerBeanAction(final FilePackagerBean filePackagerBean) {

        return new CustomAction("FilePackagerBeanAction") {

                @Override
                public Object invoke(Invocation invocation) throws Throwable {

                    final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
                    quartzJobHistory.setJobName(filePackagerBean.getKey().toString());

                    filePackagerBean.setQuartzJobHistory(quartzJobHistory);

                    return null;
                }
            };
    }

    /**
     * Initialize the quartzJobHistory from the given <code>FilePackagerBean</code>
     *
     * @param filePackagerBean the <code>FilePackagerBean</code> to initialize
     */
    public void initQuartzJobHistory(final FilePackagerBean filePackagerBean) {

        final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
        quartzJobHistory.setJobName(filePackagerBean.getKey().toString());

        filePackagerBean.setQuartzJobHistory(quartzJobHistory);
    }
}
