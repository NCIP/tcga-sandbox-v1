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

/**
 * Interface for directory lister.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface DirectoryLister {

    /**
     * Get all files in the directory.
     * @param dirName the directory in which to look
     * @return array of File objects
     */
    public File[] getFilesInDirectory( final String dirName );

    /**
     * Get all files in the directory that end with the given extension.
     * @param dirName the directory in which to look
     * @param extension the extension the files must have
     * @return array of File objects
     */
    public File[] getFilesInDirectoryByExtension( final String dirName, final String extension );
}
