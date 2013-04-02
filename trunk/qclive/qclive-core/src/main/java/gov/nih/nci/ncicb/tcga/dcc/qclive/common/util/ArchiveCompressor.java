/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface for file compressor.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public interface ArchiveCompressor {

    /**
     * Compress the given files into an archive with the given name, plus the file extension.  Put the compressed
     * archive into the given directory.
     *
     * @param files                the files to include in the archive
     * @param archiveName          the name of the archive, minus extension
     * @param destinationDirectory the location to put the new compressed archive
     * @return the File representing the created compressed archive
     * @throws IOException if it needs to
     */
    public File createArchive( List<File> files, String archiveName, File destinationDirectory, Boolean compress ) throws IOException;

    /**
     * @return the extension used by this ArchiveCompressor
     */
    public String getExtension();
}
