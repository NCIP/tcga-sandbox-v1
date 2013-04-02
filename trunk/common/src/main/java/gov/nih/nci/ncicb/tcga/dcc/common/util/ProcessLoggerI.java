/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.apache.log4j.Level;

/**
 * Process logger interface, introduced for unit test
 *
 * @author David Nassau
 * @version $Rev$
 */
public interface ProcessLoggerI {
    void logToLogger( Level loggingLevel, String whatToLog );

    void logError( Throwable t );
}
