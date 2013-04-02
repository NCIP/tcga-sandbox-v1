/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 *  Copyright Notice.  The software subject to this notice and license includes both human
 *  readable source code form and machine readable, binary, object code form (the "caBIG
 *  Software").
 *
 *  Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link GscIdfValidator}
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class GscIdfValidatorFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    private static final String GSC_TEST_DIR = SAMPLES_DIR + "qclive" + File.separator
            + "idf" + File.separator
            + "gsc" + File.separator;

    private static final String ARCHIVE_DOMAIN_NAME = "jhu-usc.edu";
    private static final String ARCHIVE_PLATFORM = "IlluminaDNAMethylation_OMA003_CPI";

    private GscIdfValidator gscIdfValidator;
    private QcContext context;

    @Before
    public void setUp() {

        gscIdfValidator = new GscIdfValidator();
        context = new QcContext();
    }

    @Test
    public void testValidIdf() throws Processor.ProcessorException {

        final Archive archive = makeTestArchive("valid");
        context.setArchive(archive);

        final boolean valid = gscIdfValidator.doWork(archive, context);
        assertTrue(valid);
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testIdfHeadersNotAllowed() throws Processor.ProcessorException {

        final Archive archive = makeTestArchive("headersNotAllowed");
        context.setArchive(archive);

        final boolean valid = gscIdfValidator.doWork(archive, context);
        assertFalse(valid);
        assertEquals(8, context.getErrorCount());
        assertTrue(context.getErrors().contains("Experimental Factor Type Term Source REF is not a valid row header"));
        assertTrue(context.getErrors().contains("Person Phone is not a valid row header"));
        assertTrue(context.getErrors().contains("Quality Control Types is not a valid row header"));
        assertTrue(context.getErrors().contains("Quality Control Types Term Source REF is not a valid row header"));
        assertTrue(context.getErrors().contains("Replicate Type is not a valid row header"));
        assertTrue(context.getErrors().contains("Replicate Type Term Source REF is not a valid row header"));
        assertTrue(context.getErrors().contains("Date of Experiment is not a valid row header"));
        assertTrue(context.getErrors().contains("Public Release Date is not a valid row header"));
    }

    @Test
    public void testRequiredIdfHeadersMissing() throws Processor.ProcessorException {

        final Archive archive = makeTestArchive("headersMissing");
        context.setArchive(archive);

        final boolean valid = gscIdfValidator.doWork(archive, context);
        assertFalse(valid);

        final List<String> expectedErrors = new ArrayList<String>();
        expectedErrors.add("Investigation Title header is missing from the IDF");
        expectedErrors.add("Experimental Design header is missing from the IDF");
        expectedErrors.add("Experimental Design Term Source REF header is missing from the IDF");
        expectedErrors.add("Experimental Factor Name header is missing from the IDF");
        expectedErrors.add("Experimental Factor Type header is missing from the IDF");
        expectedErrors.add("Person Last Name header is missing from the IDF");
        expectedErrors.add("Person First Name header is missing from the IDF");
        expectedErrors.add("Person Email header is missing from the IDF");
        expectedErrors.add("Person Affiliation header is missing from the IDF");
        expectedErrors.add("Person Roles header is missing from the IDF");
        expectedErrors.add("Experiment Description header is missing from the IDF");
        expectedErrors.add("Protocol Name header is missing from the IDF");
        expectedErrors.add("Protocol Type header is missing from the IDF");
        expectedErrors.add("Protocol Description header is missing from the IDF");
        expectedErrors.add("Protocol Parameters header is missing from the IDF");
        expectedErrors.add("Protocol Term Source REF header is missing from the IDF");
        expectedErrors.add("SDRF Files header is missing from the IDF");
        expectedErrors.add("Term Source File header is missing from the IDF");
        expectedErrors.add("Term Source Version header is missing from the IDF");

        assertEquals(expectedErrors.size(), context.getErrorCount());

        for (final String expectedError : expectedErrors) {
            assertTrue("Did not find expected error: " + expectedError, context.getErrors().contains(expectedError));
        }
    }

    /**
     * Return a test {@link Archive}.
     *
     * @return a test {@link Archive}
     * @param archiveName the archive name
     */
    private Archive makeTestArchive(final String archiveName) {

        final File file = new File(GSC_TEST_DIR + archiveName + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        final Archive archive = new Archive();
        archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        archive.setArchiveFile(file);
        archive.setDomainName(ARCHIVE_DOMAIN_NAME);
        archive.setPlatform(ARCHIVE_PLATFORM);

        return archive;
    }
}
