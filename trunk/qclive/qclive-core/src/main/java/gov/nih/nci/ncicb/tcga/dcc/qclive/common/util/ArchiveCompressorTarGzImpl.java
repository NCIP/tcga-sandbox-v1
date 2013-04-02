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
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Archive compressor that uses tar and gzip to create the archive file.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ArchiveCompressorTarGzImpl implements ArchiveCompressor {

    public static final String TAR_EXTENSION = ".tar";
    public static final String GZIP_EXTENSION = ".gz";
    public static final String TAR_GZIP_EXTENSION = TAR_EXTENSION + GZIP_EXTENSION;

    /**
     * Compress the given files into an archive with the given name, plus the file extension.  Put the compressed
     * archive into the given directory.  (So if you pass in /test/ as the destinationDirectory and 'anArchive' as
     * the archiveName, the compressed archives will be /test/anArchive.tar.gz)
     *
     * @param files                the files to include in the archive
     * @param archiveName          the name of the archive, minus extension
     * @param destinationDirectory the location to put the new compressed archive
     * @param compress             flag to compress the archive
     * @return the File representing the created compressed archive
     * @throws IOException if it needs to
     */
    public File createArchive( final List<File> files, final String archiveName,
                          final File destinationDirectory,
                          final Boolean compress) throws IOException {
        File archive = null;
        TarOutputStream tarOutputStream = null;
        FileInputStream in = null;
        GZIPOutputStream gzipOutputStream = null;

        try {
            // first make a tar archive with all the given files
            final File tarFile = new File( destinationDirectory, archiveName + TAR_EXTENSION );
            //noinspection IOResourceOpenedButNotSafelyClosed
            tarOutputStream = new TarOutputStream( new FileOutputStream( tarFile ) );
            tarOutputStream.setLongFileMode( TarOutputStream.LONGFILE_GNU );
            final byte[] buf = new byte[1024];
            for(final File file : files) {
                try {
                    //noinspection IOResourceOpenedButNotSafelyClosed
                    in = new FileInputStream( file );
                    // name of entry should be archiveName/fileName
                    final TarEntry tarEntry = new TarEntry( file );
                    tarEntry.setName( archiveName + File.separator + file.getName() );
                    tarOutputStream.putNextEntry( tarEntry );
                    int len;
                    while(( len = in.read( buf ) ) > 0) {
                        tarOutputStream.write( buf, 0, len );
                    }
                    tarOutputStream.closeEntry();
                    in.close();
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
            tarOutputStream.close();

            if(compress){
                final File outputFile = new File( destinationDirectory, archiveName + TAR_GZIP_EXTENSION );
                // then compress it using gzip
                //noinspection IOResourceOpenedButNotSafelyClosed
                gzipOutputStream = new GZIPOutputStream( new FileOutputStream( outputFile ) );
                //noinspection IOResourceOpenedButNotSafelyClosed
                in = new FileInputStream( tarFile );
                int len;
                while(( len = in.read( buf ) ) > 0) {
                    gzipOutputStream.write( buf, 0, len );
                }
                // this was a temp file so delete it
                //noinspection ResultOfMethodCallIgnored
                tarFile.delete();
                archive = outputFile;
            }else{
                archive = tarFile;
            }
        } finally {
            IOUtils.closeQuietly(tarOutputStream);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(gzipOutputStream);
        }

        return archive;
    }

    public String getExtension() {
        return TAR_GZIP_EXTENSION;
    }
}
