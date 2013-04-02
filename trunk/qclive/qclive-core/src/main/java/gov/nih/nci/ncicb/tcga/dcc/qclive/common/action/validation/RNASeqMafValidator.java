/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractMafFileHandler;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

import java.io.File;

/**
 * Validator for RNASeq maf files
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RNASeqMafValidator extends AbstractMafFileHandler<Boolean> {

    /**
     * For now this always returns true!  It's not a very strict validator...
     *
     * @param mafFile the maf file to validate
     * @param context the context for this QC call
     * @return true, always
     * @throws ProcessorException
     */
    protected Boolean doWork(final File mafFile, final QcContext context) throws ProcessorException {
        return true;
    }

    public String getName() {
        return "maf file validator (RNASeq)";
    }
}
