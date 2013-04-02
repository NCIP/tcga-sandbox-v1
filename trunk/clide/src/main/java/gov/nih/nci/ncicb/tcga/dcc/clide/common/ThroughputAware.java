/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.common;

/**
 * A simple contract for Channel Handlers to communicate how much data has been transferred
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public interface ThroughputAware {

    /**
     * While transferring a file, this number should increase as bytes are written.  When not transferring a file, this
     * method should return 0L
     *
     * @return
     */
    public long getTransferredBytes();

}
