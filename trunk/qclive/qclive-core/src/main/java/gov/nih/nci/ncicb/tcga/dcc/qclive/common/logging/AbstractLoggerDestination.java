/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging;

import org.apache.log4j.Level;

/**
 * Abstract parent for all LoggerDestination implementations.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: chenjw $
 * @version $Rev: 3441 $
 */
public abstract class AbstractLoggerDestination implements LoggerDestination {

    protected Level level = Level.INFO; // default

    public void setMinLevel( final Level level ) {
        this.level = level;
    }

    public void logToDestination( final Level messageLevel, final String message ) throws LoggerException {
        if(messageLevel.toInt() >= level.toInt()) {
            log( message );
        }
    }

    protected abstract void log( String message ) throws LoggerException;
}
