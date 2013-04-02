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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test for MD5 Validator class
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class MD5ValidatorFastTest {
    private MD5Validator md5Validator;
    private File testFile;
    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    @Before
    public void setup() {
        md5Validator = new MD5Validator();
        testFile = new File(
                SAMPLES_DIR
                        + "qclive/uploadChecker/broad.mit.edu_GBM.Genome_Wide_SNP_6.Level_1.1.0.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
    }

    @Test
    public void test() throws Processor.ProcessorException {
        assertTrue(md5Validator.execute(testFile, new QcContext()));
        assertEquals(
                "MD5 validation on broad.mit.edu_GBM.Genome_Wide_SNP_6.Level_1.1.0.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION,
                md5Validator.getDescription(testFile));
    }

    @Test(expected = Processor.ProcessorException.class)
    public void testInvalidFile() throws Processor.ProcessorException {
        md5Validator.execute(new File("does not exist"), new QcContext());
    }

    @Test(expected = Processor.ProcessorException.class)
    public void testExceptionMd5() throws Processor.ProcessorException {
        // override this to force it to throw an exception to make sure that
        // case is handled correctly
        md5Validator = new MD5Validator() {
            @Override
            protected String getChecksum(final File input) throws IOException {
                throw new IOException("testing");
            }
        };

        md5Validator.execute(testFile, new QcContext());
    }
}
