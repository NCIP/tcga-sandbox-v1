/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip implementation of FileCompressor.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ArchiveCompressorZipImpl implements ArchiveCompressor {

    public static final String ZIP_EXTENSION = ".zip";

    /**
     * Compress the given files into an archive with the given name, plus the file extension.  Put the compressed
     * archive into the given directory.
     *
     * @param files                the files to include in the archive
     * @param archiveName          the name of the archive, minus extension
     * @param destinationDirectory the location to put the new compressed archive
     * @param compress              flag to compress the archive
     * @return the File representing the created compressed archive
     * @throws IOException if it needs to
     */
    public File createArchive( final List<File> files, final String archiveName,
                               final File destinationDirectory,
                               final Boolean compress) throws IOException {
        final File archiveFile = new File( destinationDirectory, archiveName + ZIP_EXTENSION );

        ZipOutputStream out = null;
        FileInputStream in = null;

        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            out = new ZipOutputStream( new FileOutputStream( archiveFile ) );
            final byte[] buf = new byte[1024];
            for(final File file : files) {
                try {
                    //noinspection IOResourceOpenedButNotSafelyClosed
                    in = new FileInputStream( file );
                    out.putNextEntry( new ZipEntry( archiveName + File.separator + file.getName() ) );
                    int len;
                    while(( len = in.read( buf ) ) > 0) {
                        out.write( buf, 0, len );
                    }
                    out.closeEntry();
                    in.close();
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
        return archiveFile;
    }

    public String getExtension() {
        return ZIP_EXTENSION;
    }
}
