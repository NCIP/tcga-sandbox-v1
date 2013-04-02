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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for ManifestValidator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ManifestValidatorFastTest {

    private Mockery context = new JUnit4Mockery();
    private final ManifestValidator manVal = new ManifestValidator(
            new ManifestParserImpl());
    private ArchiveQueries archiveQueries = context.mock(ArchiveQueries.class);
    private final Archive archive = new Archive();
    private final Archive prevArchive = new Archive();
    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    @Before
    public void setup() {
        manVal.setArchiveQueries(archiveQueries);
    }

    @Test
    public void testGood() throws Processor.ProcessorException {
        // note: the 1.0 archive has an "altered file" list, which has an
        // original MD5 for file2 listed that matches what is given in the 1.1
        // manifest
        // also note: file2.txt is listed in the 1.1 archive manifest but is not
        // in the archive, meaning it is supposed to be
        // copied from the previous 1.0 archive
        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/manifestValidator/center_disease.platform.level.1.1.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        prevArchive
                .setDeployLocation(SAMPLES_DIR
                        + "qclive/manifestValidator/center_disease.platform.level.1.0.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        prevArchive.setRealName("center_disease.platform.level.1.0.0");

        QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        context.checking(new Expectations() {
            {
                one(archiveQueries).getLatestVersionArchive(archive);
                will(returnValue(prevArchive));
            }
        });
        boolean valid = manVal.execute(archive, qcContext);
        assertTrue("Errors: " + qcContext.getErrors(), valid);
        assertEquals("Manifest Validator had errors: " + qcContext.getErrors(),
                0, qcContext.getErrorCount());
        assertEquals(
                "Manifest Validator had warnings: " + qcContext.getWarnings(),
                0, qcContext.getWarningCount());
        // make sure the manifest validator added the file to the current
        // context's altered file list
        assertTrue(qcContext.getAlteredFiles().containsKey("file2.txt"));
        assertEquals("11223344",
                qcContext.getAlteredFiles().get("file2.txt")[0]);
        assertEquals(
                "Was altered during processing of archive center_disease.platform.level.1.0.0",
                qcContext.getAlteredFiles().get("file2.txt")[1]);
    }

    @Test
    public void testMissing() throws Processor.ProcessorException {
        // test an archive whose manifest lists a file that isn't there
        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/manifestValidator/archive_with.bad.manifest.2.0.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        context.checking(new Expectations() {
            {
                one(archiveQueries).getLatestVersionArchive(archive);
                will(returnValue(null));
            }
        });
        boolean caught = false;
        try {
            manVal.execute(archive, qcContext);
        } catch (Processor.ProcessorException ex) {
            caught = true;
        }
        assertTrue(
                "Failed manifest validation should have thrown an exception",
                caught);
        assertEquals("Validation should have 1 error", 1,
                qcContext.getErrorCount());
    }

    @Test
    public void testBadMd5() throws Processor.ProcessorException {
        // test an archive whose manifest lists a bad MD5
        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/manifestValidator/archive_with.bad.md5.1.0.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        boolean caught = false;
        try {
            manVal.execute(archive, qcContext);
        } catch (Processor.ProcessorException ex) {
            caught = true;
        }
        assertTrue("Validation should have thrown an exception", caught);
        assertEquals("Validation should have 1 error", 1,
                qcContext.getErrorCount());
        assertEquals(
                "An error occurred while processing archive 'uninitialized archive': The MD5 listed in the manifest for file.txt does not match the actual MD5 for the file",
                qcContext.getErrors().get(0));
    }

    @Test
    public void testStandalone() throws Processor.ProcessorException {
        // don't set archive queries
        manVal.setArchiveQueries(null);
        // manifest is good except refers to files from previous version
        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/manifestValidator/center_disease.platform.level.1.1.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        // validation will pass with warnings
        assertTrue(manVal.execute(archive, qcContext));
        assertTrue("Warnings were expected but not found",
                qcContext.getWarningCount() > 0);
    }

    @Test
    public void testExtraFile() {
        // make sure validation fails if there is a file in the archive that
        // isn't in the manifest
        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/manifestValidator/archive_with.extra.file.1.0.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        boolean caught = false;
        try {
            manVal.execute(archive, qcContext);
        } catch (Processor.ProcessorException ex) {
            caught = true;
        }
        assertTrue(caught);
        assertTrue(qcContext
                .getErrors()
                .contains(
                        "An error occurred while processing archive 'uninitialized archive': File 'extra.txt' is present in the archive but is not listed in the manifest"));
    }
}
