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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Test class for ArchiveFileSaver
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArchiveFileSaverFastTest {

    private Mockery context = new JUnit4Mockery();
    private ArchiveFileSaver saver;
    private FileInfoQueries mockFileInfoQueries;
    private FileInfoQueries mockDiseaseFileInfoQueries;
    private FileArchiveQueries mockFileArchiveQueries;
    private FileArchiveQueries mockDiseaseFileArchiveQueries;
    private Archive archive;
    private QcContext qcContext;
    private final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private final String archiveLocation = SAMPLES_DIR
            + "qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.0"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;

    @Before
    public void setup() {
        mockFileInfoQueries = context.mock(FileInfoQueries.class,
                "fileInfo_one");
        mockDiseaseFileInfoQueries = context.mock(FileInfoQueries.class,
                "fileInfo_two");
        mockFileArchiveQueries = context.mock(FileArchiveQueries.class,
                "fileArchive_one");
        mockDiseaseFileArchiveQueries = context.mock(FileArchiveQueries.class,
                "fileArchive_two'");
        saver = new ArchiveFileSaver();
        saver.setCommonFileInfoQueries(mockFileInfoQueries);
        saver.setDiseaseFileInfoQueries(mockDiseaseFileInfoQueries);
        saver.setFileArchiveQueries(mockFileArchiveQueries);
        saver.setDiseaseFileArchiveQueries(mockDiseaseFileArchiveQueries);
        saver.setAdditionalFiles("manifest.txt,description.txt,changes_dcc.txt,readme_dcc.txt,dcc_altered_files.txt");
        archive = new Archive();
        archive.setDataLevel(2);
        archive.setDeployLocation(archiveLocation);
        archive.setArchiveType("Level_1");
        archive.setSerialIndex("1");

        qcContext = new QcContext();
        mockQcContext(qcContext);
    }

    @Test
    public void test() throws Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                atLeast(1).of(mockFileInfoQueries).getFileId(
                        with(any(String.class)), with(any(Long.class)));
                will(returnValue(null));
                atLeast(1).of(mockFileInfoQueries).addFile(
                        with(expectedFileInfo()));
                will(returnValue(1L));
                atLeast(1).of(mockDiseaseFileInfoQueries).addFile(
                        with(expectedFileInfo()), with(any(Boolean.class)));
                will(returnValue(1L));
                atLeast(1).of(mockFileArchiveQueries)
                        .addFileToArchiveAssociation(with(any(FileInfo.class)),
                                with(any(Archive.class)));
                atLeast(1)
                        .of(mockDiseaseFileArchiveQueries)
                        .addFileToArchiveAssociation(with(any(FileInfo.class)),
                                with(any(Archive.class)),
                                with(any(Boolean.class)), with(any(Long.class)));
            }
        });

        saver.execute(archive, qcContext);
        assertTrue(qcContext.getErrorCount() == 0);
    }

    @Test(expected = Processor.ProcessorException.class)
    public void testDatabaseException() throws Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                atLeast(1).of(mockFileInfoQueries).getFileId(
                        with(any(String.class)), with(any(Long.class)));
                will(returnValue(null));
                atLeast(1).of(mockFileInfoQueries).addFile(
                        with(expectedFileInfo()));
                // noinspection ThrowableInstanceNeverThrown
                will(throwException(new DataIntegrityViolationException("oops")));
            }
        });

        saver.execute(archive, qcContext);
    }

    private void mockQcContext(final QcContext qcContext) {
        Experiment experiment = new Experiment();
        final Archive previousArchive = new Archive();
        previousArchive.setId(9L);
        previousArchive.setRealName("");
        previousArchive.setArchiveTypeId(1);
        previousArchive.setArchiveType("Level_1");
        previousArchive.setSerialIndex("1");
        experiment.addArchive(previousArchive);
        experiment.addArchive(archive);
        experiment.addPreviousArchive(previousArchive);
        qcContext.setExperiment(experiment);
        qcContext.setArchive(archive);
    }

    public static TypeSafeMatcher<FileInfo> expectedFileInfo() {
        return new TypeSafeMatcher<FileInfo>() {

            @Override
            public boolean matchesSafely(final FileInfo fileInfo) {
                return fileInfo != null && fileInfo.getFileName() != null
                        && fileInfo.getDataLevel() != null
                        && fileInfo.getFileSize() != null
                        && fileInfo.getFileMD5() != null;
            }

            public void describeTo(final Description description) {
                description
                        .appendText("a file_info object with name, data level, size and MD5 set");
            }
        };
    }
}
