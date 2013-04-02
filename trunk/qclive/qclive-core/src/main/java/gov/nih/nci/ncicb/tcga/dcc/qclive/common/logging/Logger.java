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

import java.util.List;

/**
 * Logger interface.  A Logger can log messages to whichever "LoggerDestinations" are registered to it.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public interface Logger {

    /**
     * Log the given message at the given level.
     *
     * @param loggingLevel the level of the message
     * @param message      the message to log
     */
    public void log( Level loggingLevel, String message );

    /**
     * Log the given exception.
     *
     * @param e the exception to log.
     */
    public void log( Exception e );

    /**
     * Add a destination to the destination list
     *
     * @param destination the LoggerDestination to add
     */
    public void addDestination( LoggerDestination destination );

    /**
     * Set the destination list; will overwrite anything already in the list.
     *
     * @param destinations the list of destinations
     */
    public void setDestinations( List<LoggerDestination> destinations );

    /**
     * @return the list of destinations associated with this logger
     */
    public List<LoggerDestination> getDestinations();
}
