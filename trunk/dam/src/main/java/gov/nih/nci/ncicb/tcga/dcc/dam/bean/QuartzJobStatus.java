/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import java.io.Serializable;

/**
 * An enum for the different statuses of a Quartz job
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public enum QuartzJobStatus implements Serializable {

    Queued ("Queued"),
    Started ("Started"),
    Accepted("Accepted"), // Queued or Started
    Succeeded ("Succeeded"),
    Failed ("Failed"),
    Unknown("Unknown");

    /**
     * The human readable value of this enum
     */
    private final String value;

    private QuartzJobStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return getValue();
    }
}
