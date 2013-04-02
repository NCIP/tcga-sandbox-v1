/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;

import java.io.File;

/**
 * Implementation of QcStatsLogger.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class QcStatsLoggerImpl implements QcStatsLogger {

    private ArchiveQueries archiveQueries;

    public void logIncomingArchive( final Archive archive ) {
        long size = Math.round( archive.getArchiveFile().length() / (long) 1024 );
        archiveQueries.setArchiveInitialSize( archive.getId(), size );
    }

    public void logDeployedArchive( final Archive archive ) {
        long size = Math.round( new File( archive.getDeployLocation() ).length() / (long) 1024 );
        archiveQueries.setArchiveFinalSize( archive.getId(), size );
    }

    public void setArchiveQueries( final ArchiveQueries archiveQueries ) {
        this.archiveQueries = archiveQueries;
    }
}
