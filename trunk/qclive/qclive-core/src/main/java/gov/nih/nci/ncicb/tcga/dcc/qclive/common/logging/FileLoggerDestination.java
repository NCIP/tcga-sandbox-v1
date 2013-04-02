/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging;

import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcliveCloseableUtil.close;

/**
 * Logs information to a file.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class FileLoggerDestination extends AbstractLoggerDestination {

    private final String filename; // filename for logging
    private final boolean append;
    private Writer writer;

    public FileLoggerDestination( final String filename, final boolean append ) {
        this.filename = filename;
        this.append = append;
    }

    protected void log( final String message ) throws LoggerException {
        try {
            if(this.writer == null) {
                //noinspection IOResourceOpenedButNotSafelyClosed
                this.writer = new PrintWriter( new BufferedWriter( new FileWriter( filename, append ) ) );
            }
            writer.append( message );
            writer.flush();
        }
        catch(IOException ioe) {
            throw new LoggerException( ioe );
        } finally {
            IOUtils.closeQuietly(writer);
            this.writer = null;
        }
    }
}
