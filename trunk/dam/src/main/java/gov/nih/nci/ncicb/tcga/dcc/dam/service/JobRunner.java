/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;

/**
 * Author: David Nassau
 * <p/>
 * Common interface for two types of quartz runner. This interface is referenced in the Spring
 * XML and allows us to use a single Enqueuer and Scheduler instance.
 */
public class JobRunner {   //Spring won't allow making it an abstract class?
    protected static ProcessLogger logger;
    private boolean isRunning; //so queue monitor can tell which ones are currently running

    public void setLogger(ProcessLogger logger) {
        JobRunner.logger = logger;
    }

    public void runJob() {
    }

    protected void setRunning(boolean b) {
        isRunning = b;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
