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
 * LoggerDestination implementation that logs to a Log4J logger.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class Log4JLoggerDestination implements LoggerDestination {

    private final org.apache.log4j.Logger LOGGER;

    public Log4JLoggerDestination( String loggerName ) {
        LOGGER = org.apache.log4j.Logger.getLogger( loggerName );
    }

    public void setMinLevel( final Level level ) {
        LOGGER.setLevel( level );
    }

    public void logToDestination( final Level messageLevel, final String message ) {
        LOGGER.log( messageLevel, message );
    }
}
