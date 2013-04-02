/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertSame;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.io.File;

import org.junit.Test;

/**
 * Test class for RNASeqMafProcessor.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RNASeqMafProcessorFastTest {
    @Test
    public void testExecute() throws Processor.ProcessorException {
        RNASeqMafProcessor rnaSeqMafProcessor = new RNASeqMafProcessor();
        File pretendFile = new File("hello");
        File returnFile = rnaSeqMafProcessor.execute(pretendFile, new QcContext());
        assertSame(pretendFile, returnFile);
    }

    // Class does nothing for now, is just a placeholder, which is why the test is so thin.
}
