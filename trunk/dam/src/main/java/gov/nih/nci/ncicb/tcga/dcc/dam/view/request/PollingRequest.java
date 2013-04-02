/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view.request;

import java.util.UUID;

/**
 * Author: David Nassau
 * <p/>
 * Command class for DataAccessResultPollingController
 */
public class PollingRequest {

    private UUID filePackagerKey;

    public UUID getFilePackagerKey() {
        return filePackagerKey;
    }

    public void setFilePackagerKey(final UUID filePackagerKey) {
        this.filePackagerKey = filePackagerKey;
    }

    public void validate() throws IllegalArgumentException {
        if (filePackagerKey == null) {
            throw new IllegalArgumentException("filePackagerKey is incorrect");
        }
    }
}
