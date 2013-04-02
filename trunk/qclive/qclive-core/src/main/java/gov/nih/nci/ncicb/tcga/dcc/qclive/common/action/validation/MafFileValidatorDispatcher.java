/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractMafFileVersionDispatcher;

import java.io.File;
import java.util.Map;

/**
 * Subclass of maf file dispatcher which calls the correct maf file validator based on the maf spec version.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class MafFileValidatorDispatcher extends AbstractMafFileVersionDispatcher<Boolean> {
    @Override
    protected Boolean getReturnValue(final Map<File, Boolean> results, final QcContext context) {
        return context.getErrorCount() == 0 && !results.containsValue(false);
    }

    @Override
    protected Boolean getDefaultReturnValue(final Archive archive) {
        return true;
    }

    public String getName() {
        return "MAF file validator";
    }
}
