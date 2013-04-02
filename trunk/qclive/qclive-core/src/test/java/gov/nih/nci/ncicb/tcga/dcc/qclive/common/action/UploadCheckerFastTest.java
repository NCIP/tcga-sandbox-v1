/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for UploadChecker
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class UploadCheckerFastTest {

    private Mockery context = new JUnit4Mockery();
    private UploadChecker uploadChecker;
    @SuppressWarnings("unchecked")
    private Processor<File, Boolean> mockMD5Val = (Processor<File, Boolean>) context
            .mock(Processor.class, "md5Validator");
    @SuppressWarnings("unchecked")
    private Processor<Archive, Boolean> mockNameVal = (Processor<Archive, Boolean>) context
            .mock(Processor.class, "nameValidator");
    @SuppressWarnings("unchecked")
    private Processor<Archive, Archive> mockExpander = (Processor<Archive, Archive>) context
            .mock(Processor.class, "archiveExpander");
    @SuppressWarnings("unchecked")
    private Processor<Archive, Archive> mockSaver = (Processor<Archive, Archive>) context
            .mock(Processor.class, "archiveSaver");
    private String archiveFilename = "broad.mit.edu_GBM.Genome_Wide_SNP_6.Level_1.1.0.0"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
    private static final String SAMPLE_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private String directory = SAMPLE_DIR + "qclive/uploadChecker";
    private Archive archive = new Archive();
    private File archiveFile = new File(directory, archiveFilename);

    @Before
    public void setUp() {
        uploadChecker = new TestableUploadChecker(archive, archiveFile);// mockMD5Val,
        // mockNameVal,
        // null,
        // postSteps,
        // archive,
        // archiveFile);
        uploadChecker.addInputValidator(mockMD5Val);
        uploadChecker.addOutputValidator(mockNameVal);
        uploadChecker.addPostProcessor(mockExpander);
        uploadChecker.addPostProcessor(mockSaver);
        context.checking(new Expectations() {
            {
                allowing(mockMD5Val).getName();
                will(returnValue("MD5"));
                allowing(mockNameVal).getName();
                will(returnValue("archive name"));
            }
        });
    }

    @Test
    public void test() throws Processor.ProcessorException {
        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        context.checking(new Expectations() {
            {
                one(mockMD5Val).execute(archiveFile, qcContext);
                will(returnValue(true));
                one(mockNameVal).execute(archive, qcContext);
                will(returnValue(true));
                one(mockExpander).execute(archive, qcContext);
                will(setExpanded());
                one(mockSaver).execute(archive, qcContext);
                will(setArchiveId(42));
            }
        });
        Archive archive = uploadChecker.execute(archiveFile, qcContext);
        assertEquals(archive, this.archive);
        assertEquals("Uploaded", archive.getDeployStatus());
        assertNotNull(archive.getDateAdded());
        assertTrue(archive.isExpanded());
        assertTrue("Archive ID is 0, which means it wasn't saved",
                archive.getId() != 0);
        assertEquals((Long) 42L, archive.getId());

        assertNotNull(archive.getDepositLocation());
        assertEquals(archive.getDeployLocation(), archive.getDepositLocation());
    }

    @Test
    public void testSuccessMessage() {
        String emailBody = uploadChecker.buildEmailBody(new File(
                "testArchiveName"), new QcContext());
        String expectedEmailBody = "Archive testArchiveName was uploaded successfully.  MD5 and archive name checks passed.  You will receive another email when processing is complete.";
        assertEquals(expectedEmailBody, emailBody);
    }

    class TestableUploadChecker extends UploadChecker {

        private Archive testArchive;

        public TestableUploadChecker(Archive archive, File file) {
            super();
            testArchive = archive;
            testArchive.setArchiveFile(file);
        }

        protected Archive makeArchive(File file) {
            return testArchive;
        }
    }

    public static Action setExpanded() {
        return new SetExpandedAction();
    }

    public static Action setArchiveId(int id) {
        return new SetArchiveIdAction(id);
    }

    public static class SetExpandedAction implements Action {

        public void describeTo(final Description description) {
            description.appendText("calls setExpanded on an archive");
        }

        /*
           * This sets expanded and then returns the archive object
           */
        public Object invoke(final Invocation invocation) throws Throwable {
            Archive archive = (Archive) invocation.getParameter(0);
            archive.setExpanded();
            return archive;
        }
    }

    public static class SetArchiveIdAction implements Action {

        private long id;

        public SetArchiveIdAction(long id) {
            this.id = id;
        }

        public void describeTo(final Description description) {
            description.appendText("sets the archive ID to ").appendText(
                    String.valueOf(id));
        }

        public Object invoke(final Invocation invocation) throws Throwable {
            Archive archive = (Archive) invocation.getParameter(0);
            archive.setId(id);
            return archive;
        }
    }
}
