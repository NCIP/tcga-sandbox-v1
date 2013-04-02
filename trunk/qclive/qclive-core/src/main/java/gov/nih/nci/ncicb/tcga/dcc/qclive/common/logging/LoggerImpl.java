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

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Logger interface, which allows for logging to multiple destinations.  To use,
 * instantiate one or more LoggerDestination objects and add them to the LoggerImpl using
 * addDestination or setDestinations.  When the log method is called, all destinations will
 * be passed the log message. 
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class LoggerImpl implements Logger {

    private final List<LoggerDestination> destinations = new ArrayList<LoggerDestination>();

    /**
     * Logs the message to all destinations, passing on the level.
     *
     * @param loggingLevel the level of this log message
     * @param message      the message
     */
    public void log( final Level loggingLevel, final String message ) {
        for(final LoggerDestination destination : destinations) {
            try {
                destination.logToDestination( loggingLevel, message );
            }
            catch(LoggerDestination.LoggerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Logs the exception to all destinations, using level FATAL.  The complete stack
     * trace of the exception is logged.
     * 
     * @param e the exception to log
     */
    public void log( final Exception e ) {
        final StackTraceElement[] stack = e.getStackTrace();
        final StringBuilder buff = new StringBuilder( 2048 );
        buff.append( e.toString() ).append( '\n' );
        for(final StackTraceElement aStack : stack) {
            buff.append( aStack.toString() ).append( '\n' );
        }
        log( Level.FATAL, buff.toString() );
    }

    /**
     * Adds a destination to this logger.
     *
     * @param destination the LoggerDestination to add
     */
    public void addDestination( final LoggerDestination destination ) {
        destinations.add( destination );
    }

    /**
     * Set the destinations for this logger to be the given list of destinations.
     * 
     * @param destinations the list of destinations
     */
    public void setDestinations( final List<LoggerDestination> destinations ) {
        this.destinations.clear();
        this.destinations.addAll( destinations );
    }

    /**
     * Gets the destinations
     * @return list of destinations      
     */
    public List<LoggerDestination> getDestinations() {
        return destinations;
    }
}
