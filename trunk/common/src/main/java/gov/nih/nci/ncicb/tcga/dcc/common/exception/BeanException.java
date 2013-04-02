/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.exception;

/**
 * Exception class for data generation errors
 *
 * @author Deepak Srinivasan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BeanException extends Exception {
    public BeanException(final String message) {
        super(message);
    }

    public BeanException(final String message, final Throwable cause) {
        super(message, cause);

    }
}
