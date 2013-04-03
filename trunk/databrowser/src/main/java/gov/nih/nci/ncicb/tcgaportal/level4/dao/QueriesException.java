/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.dao;

/**
 * Exception used to wrap all exceptions generated in DAO
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class QueriesException extends Exception {
    public QueriesException(String message) {
        super(message);
    }

    public QueriesException(Throwable cause) {
        super(cause);
    }

    public QueriesException(String message, Throwable cause) {
        super(message, cause);
    }
}
