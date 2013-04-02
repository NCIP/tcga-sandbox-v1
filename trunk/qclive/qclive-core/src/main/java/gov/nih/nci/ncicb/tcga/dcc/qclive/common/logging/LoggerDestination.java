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
 * Interface for LoggerDestinations.  Implementations can log/print/save messages however they wish.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public interface LoggerDestination {

    public void setMinLevel( Level level );

    public abstract void logToDestination( Level messageLevel, String message ) throws LoggerException;

    public class LoggerException extends Exception {

        public LoggerException( final Throwable cause ) {
            super( cause );
        }

        public LoggerException( final String reason ) {
            super( reason );
        }
    }
}
