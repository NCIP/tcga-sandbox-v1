package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLoggerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveDeletionBean;

import java.io.File;

import org.apache.log4j.Level;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;


/**
 * Test class for ArchiveDeletionJob
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class ArchiveDeletionJobFastTest {

    private Mockery context = new JUnit4Mockery();
    private final ProcessLoggerI mockLogger = context.mock(ProcessLoggerI.class, "logger");
    private static final String ARCHIVE_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples/archives").getPath() + File.separator;

    private static final String VALID_ARCHIVE_NAME = "delete_archive.tar.gz";
    private static final String INVALID_ARCHIVE_NAME = "do_not_exist.tar.gz";
    private static final String BACKUP = ".bak";
    private ArchiveDeletionJob archiveDeletionJob;

    @Before
    public void setup() {
        archiveDeletionJob = new ArchiveDeletionJob();
        archiveDeletionJob.setLogger(mockLogger);
    }


    @Test
    public void deleteValidArchive() throws Exception {
        final ArchiveDeletionBean bean = new ArchiveDeletionBean();
        final String validArchive = ARCHIVE_FOLDER + VALID_ARCHIVE_NAME;
        final String validArchiveBackup = validArchive + BACKUP;
        FileUtil.copy(validArchive, validArchiveBackup);
        bean.setArchiveName(ARCHIVE_FOLDER + VALID_ARCHIVE_NAME);
        context.checking(new Expectations() {{
            one(mockLogger).logToLogger(with(Level.INFO), with(getExpectedValidLog(true)));
        }});

        archiveDeletionJob.run(bean);
        FileUtil.move(validArchiveBackup, validArchive);

    }

    @Test
    public void deleteInvalidArchive() throws Exception {
        final ArchiveDeletionBean bean = new ArchiveDeletionBean();
        bean.setArchiveName(ARCHIVE_FOLDER + INVALID_ARCHIVE_NAME);

        context.checking(new Expectations() {{
            one(mockLogger).logToLogger(with(Level.INFO), with(getExpectedValidLog(false)));
        }});

        archiveDeletionJob.run(bean);
    }

    /**
     * Return a <code>Matcher</code> for the expected log
     *
     * @param successfullyDeleted <code>true</code> if the archive is expected to be successfully deleted, <code>false</code> otherwise
     * @return a <code>Matcher</code> for the expected log
     */
    private Matcher<String> getExpectedValidLog(final boolean successfullyDeleted) {
        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String s) {
                return s.contains(new StringBuilder("Archive ").append(!successfullyDeleted?"un":"").append("successfully deleted"))
                        || s.contains("File does not exist");
            }

            public void describeTo(final Description description) {
                description.appendText("Expected log");
            }
        };
    }

}
