/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.io.File;
import java.util.Map;

/**
 * Subclass of maf file dispatcher which calls the correct maf file processor based on the maf file spec version.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class MafFileProcessorDispatcher extends AbstractMafFileVersionDispatcher<Archive> {
    @Override
    protected Archive getReturnValue(final Map<File, Archive> results, final QcContext context) {
        return context.getArchive();
    }

    @Override
    protected Archive getDefaultReturnValue(final Archive archive) {
        return archive;
    }

    public String getName() {
        return "MAF file processor";
    }
}
