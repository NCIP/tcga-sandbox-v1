/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage;

/**
 * TODO: INFO ABOUT CLASS
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class UsageLoggerException extends Exception {

    public UsageLoggerException( Throwable throwable ) {
        super( throwable );
    }

    public UsageLoggerException( String message ) {
        super( message );
    }

    public UsageLoggerException( String message, Throwable cause ) {
        super( message, cause );
    }
}
