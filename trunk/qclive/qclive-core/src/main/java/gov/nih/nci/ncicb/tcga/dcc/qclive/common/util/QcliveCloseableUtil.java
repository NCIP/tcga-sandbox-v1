/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import org.apache.log4j.Level;

import java.io.Closeable;
import java.io.IOException;

/**
 * Util class to close resources, using a {@link Logger} to log {@link IOException}.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QcliveCloseableUtil {

    /**
     * Close a resource and log any {@link IOException} occurring while closing.
     *
     * @param closeable the resource to close
     * @param logger the logger to use
     */
    public static void close(final Closeable closeable, final Logger logger) {

        try{
            if(closeable != null) {
                closeable.close();
            }

        } catch (final IOException e) {

            if(logger != null) {
                logger.log(Level.WARN, e.getMessage());
            }
        }
    }
}
