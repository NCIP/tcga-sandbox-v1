/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.io.File;

/**
 * Helper methods for <code>Archive</code> objects
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ArchiveUtil {

    /**
     * Creates a new file with the given filename and content and add it to the given archive.
     * Modify the archive's manifest accordingly.
     *
     * @param content the content of the file to create
     * @param filename the filename of the file to create
     * @param archive the archive into which to add the file
     * @param qcContext the context in which to log warnings and errors
     */
    public void addContentIntoNewFileToArchive(final String content, final String filename, final Archive archive, final QcContext qcContext);

    /**
     * Return the manifest from the given archive
     *
     * @param archive from which to retrieve the manifest
     * @return the manifest file
     * @throws ArchiveUtilException
     */
    public File getArchiveManifestFile(final Archive archive) throws ArchiveUtilException;

    /**
     * Exception for use by the ArchiveUtil objects.
     */
    public class ArchiveUtilException extends Exception {

        public ArchiveUtilException(final String message) {
            super(message);
        }
    }
}
