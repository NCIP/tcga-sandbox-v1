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
 * Exception class for UUID Manager
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class UUIDException extends Exception {

    private ErrorInfo errorInfo;

    public UUIDException( final String message ) {
        super( message );
    }

    public UUIDException( final String message, final Throwable cause) {
        super( message, cause );
        this.errorInfo = new ErrorInfo(cause);
    }

    // used in the error view (to get properly formatted stacktrace)
    // even though IDEA says it is not used
    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(final ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }
}

