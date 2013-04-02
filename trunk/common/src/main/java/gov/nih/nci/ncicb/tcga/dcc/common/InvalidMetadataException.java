
/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common;

/**
 * Exception class for invalid meta data
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class InvalidMetadataException extends RuntimeException {
    public InvalidMetadataException(final String message) {
        super(message);
    }

    public InvalidMetadataException(final String message, final Throwable cause) {
        super(message, cause);

    }
}
