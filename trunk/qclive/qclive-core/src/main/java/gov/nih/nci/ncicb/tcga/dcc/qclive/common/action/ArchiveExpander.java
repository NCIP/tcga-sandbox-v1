/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This takes an Archive and expands it into a directory in the same location
 * as the archive.  The files will be put in a directory given the name of the archive.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ArchiveExpander extends AbstractProcessor<Archive, Archive> {

    /**
     * Expands the archive into a directory whose name is the archive name.  Puts the directory
     * in the same location as the archive.  Flattens the structure, so that all files are in the
     * same directory.  Will warn if there are nested or oddly-named directories, or if there is no
     * directory named after the archive, but will still succeed.
     *
     * @param archive the archive to expand
     * @return the same archive, which has been expanded
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an unrecoverable error during expansion
     */
    protected Archive doWork(final Archive archive, final QcContext context) throws ProcessorException {
        try {
            final File expandDir = makeArchiveDir(archive);
            final String archiveName = archive.getArchiveFile().getName();
            if (archiveName.endsWith(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION)
                    || archiveName.endsWith(ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION)) {
                expandTarOrTarGz(expandDir, archive, context);
            } else {
                throw new ProcessorException(new StringBuilder().append("Archive is not correct type: '").append(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION).append("' expected").toString());
            }
        } catch (IOException e) {
            throw new ProcessorException(new StringBuilder().append("Error expanding archive: ").append(e.getMessage()).toString(), e);
        }
        try {
            final File[] theFilesInDirectoryList = DirectoryListerImpl.getFilesInDir(archive.getExplodedArchiveDirectoryLocation());
            if ((theFilesInDirectoryList == null) || (theFilesInDirectoryList.length < 1)) {
                throw new ProcessorException("The expanded archive directory does not contain any files");
            }
            final File[] hiddenFiles = DirectoryListerImpl.getHiddenFilesInDir(archive.getExplodedArchiveDirectoryLocation());
            if (hiddenFiles != null && hiddenFiles.length > 0) {
                final StringBuilder errorMessage = new StringBuilder();
                errorMessage.append("Archives may not contain hidden files, but found: ");
                for (int i = 0; i < hiddenFiles.length; i++) {
                    errorMessage.append(hiddenFiles[i].getName());
                    if (i < hiddenFiles.length - 1) {
                        errorMessage.append(", ");
                    }
                }
                throw new ProcessorException(errorMessage.toString());
            }
        } catch (IOException e) {
            throw new ProcessorException(new StringBuilder().append("I/O error checking the expanded archive directory: ").append(e.getMessage()).toString(), e);
        }
        archive.setExpanded();
        return archive;
    }

    private File makeArchiveDir(final Archive archive) throws IOException, ProcessorException {
        File explodedDir = new File(archive.getExplodedArchiveDirectoryLocation());
        if (explodedDir.exists() && explodedDir.isDirectory()) {
            // clean up if exists
            boolean isDeleteSuccesfull = FileUtil.deleteDir(explodedDir);
            if (!isDeleteSuccesfull) {
                // something must be holding a lock , bail out
                throw new ProcessorException(new StringBuilder().append(" Unable to delete ").
                        append(archive.getExplodedArchiveDirectoryLocation()).append(" while expanding the archive: ")
                        .append(archive.getArchiveName()).toString());
            }
        }

        final File archiveDir = FileUtil.makeDir(archive.getExplodedArchiveDirectoryLocation());
        if (!archiveDir.exists()) {
            throw new ProcessorException(new StringBuilder().append("Error expanding '").append(archive.getArchiveFile().getName()).append("': could not create archive directory").toString());
        }
        if (!archiveDir.canWrite()) {
            throw new ProcessorException(new StringBuilder().append("Error expanding '").append(archive.getArchiveFile().getName()).append("': can't write to ").append(archiveDir.getAbsolutePath()).toString());
        }
        return archiveDir;
    }

    /**
     * Expands the given {@link Archive} provided that it is a tar or tar.gz archive.
     *
     * @param expandDir the directory under which to expand the archive
     * @param archive the archive
     * @param context the context
     * @throws IOException
     */
    private void expandTarOrTarGz(final File expandDir,
                                  final Archive archive,
                                  final QcContext context) throws IOException {

        try {
            FileUtil.explodeTarOrTarGz(expandDir, archive.fullArchivePathAndName());
        } catch (final IOException ie) {
            final StringTokenizer stk = new StringTokenizer(ie.getMessage(), "\n");
            while (stk.hasMoreTokens()) {
                context.addWarning(stk.nextToken());
            }
        }
    }

    /**
     * @return the name of this process step
     */
    public String getName() {
        return "archive expander";
    }

    private void expandZip(final File expandDir, final Archive archive, final QcContext context) throws IOException {

        ZipFile zipFile = null;
        BufferedOutputStream out = null;
        try {
            zipFile = new ZipFile(archive.fullArchivePathAndName());
            // write each entry to the archive directory
            final Enumeration zipEnum = zipFile.entries();
            boolean foundArchiveDir = false;
            while (zipEnum.hasMoreElements()) {
                final ZipEntry entry = (ZipEntry) zipEnum.nextElement();
                if (!entry.isDirectory()) {
                    String entryName = entry.getName();
                    final File entryFile = new File(entryName);
                    // extract out just the filename of the entry
                    if (entryFile.getCanonicalPath().lastIndexOf(File.separator) != -1) {
                        entryName = entryFile.getCanonicalPath().substring(entryFile.getCanonicalPath().lastIndexOf(File.separator));
                    }
                    // the file to write is the archive dir plus just the filename
                    final File archiveFile = new File(expandDir, entryName);
                    InputStream in = zipFile.getInputStream(entry);
                    FileOutputStream fout = new FileOutputStream(archiveFile);
                    //noinspection IOResourceOpenedButNotSafelyClosed
                    out = new BufferedOutputStream(fout);
                    copyInputStream(in, out);
                    in = null;
                    out = null;
                    fout.close();
                    fout = null;

                } else {
                    // if find a directory that isn't the archive name, add warning for weird archive structure
                    if (!entry.getName().equals(archive.getArchiveName()) && !entry.getName().equals(archive.getArchiveName() + "/")) {
                        context.addWarning(new StringBuilder().append("Archive '").append("' has a non-standard directory '").append(entry.getName()).append("'.").append("Archive files should be contained inside a single directory with the archive name as its name.").toString());
                    } else {
                        foundArchiveDir = true;
                    }
                }
            }
            if (!foundArchiveDir) {
                context.addWarning("Archive files should be contained inside a single directory with the archive name as its name.");
            }
        } finally {
            IOUtils.closeQuietly(out);

            if (zipFile != null) {
                zipFile.close();
            }
        }
    }

    /*
     * Copies what is read from in into out.
     */

    private void copyInputStream(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.flush();
        out.close();

    }

}
