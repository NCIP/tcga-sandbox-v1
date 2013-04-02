/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLoggerI;
import org.apache.log4j.Level;

/**
 * In the interest of time, a primitive logger that prints to stdout and doesn't require configuring log4j
 * It prints the thread name so you can piece together the story when multiple threads are printing.
 * @author David Nassau
 * @version $Rev$
 */
public class BareBonesLogger implements ProcessLoggerI {
    public void logToLogger(final Level loggingLevel, final String whatToLog) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(loggingLevel.toString()).append("]").append('\t')
                .append(Thread.currentThread().getName()).append('\t')
                .append(whatToLog);
        System.out.println(sb);
    }

    public synchronized void logError(final Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ERROR]\t").append(Thread.currentThread().getName()).append('\t').append(t.getMessage()).append('\t');
        System.err.print(sb);
        t.printStackTrace(System.err);
    }
}
