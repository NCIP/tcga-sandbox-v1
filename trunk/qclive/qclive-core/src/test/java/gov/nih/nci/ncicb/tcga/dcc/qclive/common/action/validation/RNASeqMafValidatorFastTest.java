/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

import java.io.File;

import org.junit.Test;

/**
 * Test class for RNASeqMafValidator
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RNASeqMafValidatorFastTest {
	private static final String SAMPLES_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_FILE_DIR = SAMPLES_DIR + "qclive/mafFileValidator/refSeq";

    @Test
    public void testExecute() throws Processor.ProcessorException {
        RNASeqMafValidator rnaSeqMafValidator = new RNASeqMafValidator();
        File refSeqMaf = new File(TEST_FILE_DIR + "/test.maf");
        boolean isValid = rnaSeqMafValidator.execute(refSeqMaf, new QcContext());
        assertTrue(isValid);
    }

    // Note: since the validator currently does nothing, we have no cases where anything should fail so there is no
    // failure cases to test.

}
