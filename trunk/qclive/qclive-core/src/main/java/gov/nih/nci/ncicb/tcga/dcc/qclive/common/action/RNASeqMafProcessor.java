/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.io.File;

/**
 * Processor for RNASeq maf files.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RNASeqMafProcessor extends AbstractMafFileHandler<File> {
    /**
     * Does nothing for now.
     *
     * @param input the maf file to process
     * @param context the context for this QC call
     * @return the same input file
     * @throws ProcessorException
     */
    protected File doWork(final File input, final QcContext context) throws ProcessorException {
        return input;
    }

    public String getName() {
        return "maf file processor (RNASeq)";
    }
}
