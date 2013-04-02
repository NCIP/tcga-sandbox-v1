/*
 * The caBIG Software License, Version 1.0 Copyright 2009 TCGA DCC/Portal Project ("Cancer Center")
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.service;

import java.io.Serializable;

/**
 * Callback whereby the Loader reports its current status back to the caller.
 * When first created, the status is null. When injected into the Loader, it becomes Queued.
 * When starts executed, becomes Started, and when done either Succeeded or Failed.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

public interface StatusCallback extends Serializable {

    public enum Status {

        Queued, Started, Succeeded, Failed
    }

    public void sendStatus(Status status);
}
