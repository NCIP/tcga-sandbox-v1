/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * ControlArchiveValidator unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ControlArchiveValidatorFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    private static final String TEST_DIR = SAMPLES_DIR
            + "qclive" + File.separator
            + "controlArchiveValidator" + File.separator;

    private static final String GOOD_CNTL = "intgen.org_CNTL.bio.Level_1.0.4.0";
    private static final String BAD_CNTL_NO_CONTROL_FILES = "intgen.org_CNTL.bio.Level_1.0.4.1";
    private static final String BAD_CNTL_INVALID_PLATFORM_NAME = "intgen.org_CNTL.diagnostic_images.Level_1.0.4.0";

    private ControlArchiveValidator controlArchiveValidator;
    private QcContext qcContext;

    @Before
    public void setUp() {
        controlArchiveValidator = new ControlArchiveValidator();
        qcContext = new QcContext();
    }

    @Test
    public void testGetName() {

        assertNotNull(controlArchiveValidator);
        assertEquals("control archive validation", controlArchiveValidator.getName());
    }

    @Test
    public void testDoWorkNonCNTLArchive() throws Processor.ProcessorException {

        final Archive archive = new Archive();
        archive.setTumorType("NonCNTL");

        final Boolean valid = controlArchiveValidator.doWork(archive, new QcContext());
        assertNotNull(valid);
        assertTrue(valid);
    }

    @Test
    public void testDoWorkGoodCNTLArchive() throws Processor.ProcessorException {

        final String deployLocation = TEST_DIR + GOOD_CNTL + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
        final Archive archive = new Archive();
        archive.setTumorType("CNTL");
        archive.setDeployLocation(deployLocation);
        archive.setPlatform("bio");

        final Boolean valid = controlArchiveValidator.doWork(archive, qcContext);
        assertNotNull(valid);
        assertTrue(valid);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testDoWorkCNTLArchiveNoControlFiles() throws Processor.ProcessorException {

        final String deployLocation = TEST_DIR + BAD_CNTL_NO_CONTROL_FILES + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
        final Archive archive = new Archive();
        archive.setTumorType("CNTL");
        archive.setDeployLocation(deployLocation);
        archive.setRealName(BAD_CNTL_NO_CONTROL_FILES);
        archive.setPlatform("bio");

        final Boolean valid = controlArchiveValidator.doWork(archive, qcContext);
        assertNotNull(valid);
        assertFalse(valid);
        assertEquals(2, qcContext.getErrorCount());

        final List<String> errors = qcContext.getErrors();
        assertNotNull(errors);
        assertEquals("An error occurred while processing archive '" + BAD_CNTL_NO_CONTROL_FILES + "': " +
                "The archive is a control archive and should contain at least 1 control file.", errors.get(0));

        final String errorMessage2 = errors.get(1);
        final String errorMessagePrefix = new StringBuilder("An error occurred while processing archive '")
                .append(BAD_CNTL_NO_CONTROL_FILES).append("': ")
                .append("The archive is a control archive and should only contain control files. Found: ").toString();
        final String fileName1 = "intgen.org_biospecimen.TCGA-AB-1234.xml";
        final String fileName2 = "intgen.org_clinical.TCGA-AB-1234.xml";
        final String errorMessagePermutation1 = new StringBuilder(errorMessagePrefix).append(fileName1).append(",").append(fileName2).toString();
        final String errorMessagePermutation2 = new StringBuilder(errorMessagePrefix).append(fileName2).append(",").append(fileName1).toString();
        final boolean foundPermutation1 = errorMessagePermutation1.equals(errorMessage2);
        final boolean foundPermutation2 = errorMessagePermutation2.equals(errorMessage2);

        assertTrue(foundPermutation1 || foundPermutation2);
    }

    @Test
    public void testDoWorkCNTLArchiveBadPlatformName() throws Processor.ProcessorException {

        final String deployLocation = TEST_DIR + BAD_CNTL_INVALID_PLATFORM_NAME + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
        final Archive archive = new Archive();
        archive.setTumorType("CNTL");
        archive.setDeployLocation(deployLocation);
        archive.setRealName(BAD_CNTL_INVALID_PLATFORM_NAME);
        archive.setPlatform("diagnostic_images");

        final Boolean valid = controlArchiveValidator.doWork(archive, qcContext);
        assertNotNull(valid);
        assertFalse(valid);
        assertEquals(1, qcContext.getErrorCount());

        final List<String> errors = qcContext.getErrors();
        assertNotNull(errors);

        final String errorMessage = errors.get(0);
        final String expectedErrorMessage = new StringBuilder("An error occurred while processing archive '")
                .append(BAD_CNTL_INVALID_PLATFORM_NAME)
                .append("': Expecting platform name for Control archive to be 'bio', but found 'diagnostic_images'")
                .toString();
        assertEquals(expectedErrorMessage, errorMessage);
    }
}
