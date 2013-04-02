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

/**
 * Interface for Qc stats logger.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public interface QcStatsLogger {

    public void logIncomingArchive( Archive archive );

    public void logDeployedArchive( Archive archive );
}
