/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for maf validator dispatcher
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class MafFileValidatorDispatcherFastTest {
    private Mockery context = new JUnit4Mockery();
    private MafFileValidatorDispatcher validator;
    private Processor<File, Boolean> mafFileValidator1;
    private Processor<File, Boolean>  mafFileValidator2;
    private Processor<File, Boolean>  mafFileValidator24;
    private Archive archive;
    private QcContext qcContext;
    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    @Before
    public void setup() {
        validator = new MafFileValidatorDispatcher();
        mafFileValidator1 = (Processor<File, Boolean>) context.mock(
                Processor.class, "maf 1 validator");
        mafFileValidator2 = (Processor<File, Boolean>) context.mock(
                Processor.class, "maf 2 validator");

        mafFileValidator24 = (Processor<File, Boolean>) context.mock(
                Processor.class, "maf 2.4 validator");

        validator.setDefaultSpecVersion("1.0");
        validator.addMafHandler(mafFileValidator1, "1.0");
        validator.addMafHandler(mafFileValidator2, "2.3");
        validator.addMafHandler(mafFileValidator24, "2.4");
        validator.setSupportedVersions("1.0,2.3,2.4");
        archive = new Archive();
        archive.setArchiveType(Archive.TYPE_LEVEL_2);
        archive.setExperimentType(Experiment.TYPE_GSC);
        qcContext = new QcContext();
        qcContext.setArchive(archive);
    }

    @Test
    public void testDefaultVersion() throws Processor.ProcessorException {
        // this archive maf file has no #version indicator, so the default
        // validator will be used
        archive.setDeployLocation(SAMPLES_DIR + "qclive/mafFileValidator/good"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        context.checking(new Expectations() {
            {
                atLeast(1).of(mafFileValidator1).execute(with(any(File.class)),
                        with(qcContext));
                will(returnValue(true));
            }
        });

        boolean valid = validator.execute(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), valid);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(1, qcContext.getWarningCount());
        assertEquals("No MAF version header found; validating against 1.0 spec", qcContext.getWarnings().get(0));
    }

    @Test
    public void testLatestVersion() throws Processor.ProcessorException {
        // this archive maf has #version 2.3, which is one of the registered
        // validator versions
        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/mafFileValidator/goodMaf2"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        context.checking(new Expectations() {
            {
                atLeast(1).of(mafFileValidator2).execute(with(any(File.class)),
                        with(qcContext));
                will(returnValue(true));
            }
        });

        boolean valid = validator.execute(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), valid);
    }
    @Test
    public void testVersion2_4() throws Processor.ProcessorException {
        // this archive maf has #version 2.3, which is one of the registered
        // validator versions

        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/mafFileValidator/mafV2_4/validArchive"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        context.checking(new Expectations() {
            {
                atLeast(1).of(mafFileValidator24).execute(with(any(File.class)),
                        with(qcContext));
                will(returnValue(true));
            }
        });

        boolean valid = validator.execute(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), valid);
    }

    @Test
    public void testUnsupportedVersion() {
        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/mafFileValidator/badVersion"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        try {
            validator.execute(archive, qcContext);
        } catch (Processor.ProcessorException e) {
            assertEquals("MAF spec version 'squirrel' is not supported",
                    e.getMessage());
        }
    }

    @Test
    public void testBadlyFormattedVersion() {
        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/mafFileValidator/badVersionFormat"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        try {
            validator.execute(archive, qcContext);
        } catch (Processor.ProcessorException e) {
            assertEquals(
                    "MAF spec version must be specified in the first line of the file with the format '#version X' where X is the version designation",
                    e.getMessage());
        }
    }

    @Test(expected = Processor.ProcessorException.class)
    public void testWrongLevel() throws Processor.ProcessorException {
        archive.setDeployLocation(SAMPLES_DIR + "qclive/mafFileValidator/good"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setArchiveType(Archive.TYPE_LEVEL_1);

        validator.execute(archive, qcContext);
    }
}
