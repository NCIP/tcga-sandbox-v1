/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Provides helper methods for <code>File</code> objects
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class FileUtil {
    private static final Log logger = LogFactory.getLog(FileUtil.class);
    private static final int BUFFER_SIZE = 1024;
    public static final String TAR = ".tar";
    public static final String TAR_GZ = ".tar.gz";
    public static final String MD5 = ".md5";

    //This way of copying is optimized and use zero-copy nio mechanism.
    //Please use when copying big files.
    public static boolean copyFile(final String sourceFilename, final String destFilename) {
        boolean success = false;
        FileChannel source = null;
        FileChannel destination = null;
        FileInputStream sourceStream = null;
        FileOutputStream destinationStream = null;
        final File destFile = new File(destFilename);
        final File sourceFile = new File(sourceFilename);
        try {
            final long size = sourceFile.length();
            sourceStream = new FileInputStream(sourceFile);
            destinationStream = new FileOutputStream(destFile);
            source = sourceStream.getChannel();
            destination = destinationStream.getChannel();
            long toTransfer = size;
            while (toTransfer > 0) {
                toTransfer -= source.transferTo(size - toTransfer, toTransfer, destination);
            }
            success = true;
        } catch (IOException iox) {
            try {
                logger.error("Unable to copy " + sourceFile.getCanonicalPath()
                        + " to " + destFile.getCanonicalPath(), iox);
            } catch (IOException iox2) {
                logger.error("Unable to copy " + sourceFile.getName() + " OR get the canonical name", iox2);
            }
        } finally {
            try {
                if (source != null) {
                    source.close();
                    source = null;
                }
                if (destination != null) {
                    destination.close();
                    destination = null;
                }
                if (sourceStream != null) {
                    sourceStream.close();
                    sourceStream = null;
                }
                if (destinationStream != null) {
                    destinationStream.close();
                    destinationStream = null;
                }
            } catch (IOException iox3) {
                logger.error("Unable to close stream?!?", iox3);
            }
        }
        return success;
    }

    /**
     * Returns the file content as String
     *
     * @param file                         the file which content is to return
     * @param normalizeToUnixLineSeparator <code>true</code> if the file content returned should have its line separators normalized to Unix standard, <code>false</code> otherwise
     * @return the file content as a String
     * @throws IOException
     */
    public static String readFile(final File file, final boolean normalizeToUnixLineSeparator) throws IOException {

        String result;

        final StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            final char[] buffer = new char[BUFFER_SIZE];
            int character;

            while ((character = reader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, character);
            }
        } finally {

            if (reader != null) {
                reader.close();
            }
        }

        result = stringBuilder.toString();

        if (normalizeToUnixLineSeparator) {
            result = replaceLineSeparators(result);
        }

        return result;
    }

    /**
     * Normalize line separator to Unix standard ("\n")
     *
     * @param input the String to normalize
     * @return the input with normalized new lines
     */
    public static String replaceLineSeparators(String input) {

        // Replace "\r\n" (Windows format) with "\n" (Unix format)
        input = input.replaceAll("\r\n", "\n");

        // Replace "\r" (Mac format) with "\n" (Unix format)
        input = input.replaceAll("\r", "\n");

        return input;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static Properties getProperties(final String filePath, final String fileName) throws IOException {

        Properties properties = new Properties();
        FileInputStream fileInputStream = null;

        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileInputStream = new FileInputStream(new File(filePath, fileName));
            properties.load(fileInputStream);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }

        return properties;
    }


    /**
     * Formats file size provided in bytes properly into Kilobytes, Megabytes and Gigabytes
     *
     * @param size size in bytes
     * @return formatted size
     */
    public static String getFormattedFileSize(final long size) {
        String estimate;
        float d;
        if (size < 1048576F) {
            d = size / 1024F;
            estimate = Math.round(d * 1000F) / 1000F + " KiB";
        } else if (size < 1073741824F) {
            d = size / 1048576F;
            estimate = Math.round(d * 1000F) / 1000F + " MiB";
        } else {
            d = size / 1073741824F;
            estimate = Math.round(d * 1000F) / 1000F + " GiB";
        }
        return estimate;
    }

    /**
     * Formats file size provided in bytes properly into Kilobytes, Megabytes and Gigabytes
     *
     * @param file File
     * @return formatted size
     */
    public static String getFormattedFileSize(final File file) {
        return getFormattedFileSize(file.length());
    }

    /**
     * Writes the given content in the given file
     *
     * @param content the content to add
     * @param file    the file into which to write the content
     * @throws IOException
     */
    public static void writeContentToFile(final String content, final File file) throws IOException {

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(content);

        } finally {

            if (fileWriter != null) {

                fileWriter.flush();
                fileWriter.close();
            }
        }
    }


    public static void copy(final String sourceFilename, final String destFilename) throws IOException {

        FileInputStream sourceFileInputStream = null;
        FileChannel sourceChannel = null;
        FileOutputStream destFileOutputStream = null;
        FileChannel destChannel = null;

        try {
            File destFile = new File(destFilename);
            final File sourceFile = new File(sourceFilename);
            if (destFile.isDirectory()) {
                // use original file's name
                destFile = new File(destFile, sourceFile.getName());
            }

            //noinspection IOResourceOpenedButNotSafelyClosed
            sourceFileInputStream = new FileInputStream(sourceFile);
            //noinspection ChannelOpenedButNotSafelyClosed
            sourceChannel = (sourceFileInputStream).getChannel();

            //noinspection IOResourceOpenedButNotSafelyClosed
            destFileOutputStream = new FileOutputStream(destFile);
            //noinspection ChannelOpenedButNotSafelyClosed
            destChannel = (destFileOutputStream).getChannel();

            final long size = sourceFile.length();
            long toTransfer = size;
            while (toTransfer > 0) {
                toTransfer -= sourceChannel.transferTo(size - toTransfer, toTransfer, destChannel);
            }

        } finally {
            IOUtils.closeQuietly(sourceFileInputStream);
            IOUtils.closeQuietly(sourceChannel);
            IOUtils.closeQuietly(destFileOutputStream);
            IOUtils.closeQuietly(destChannel);
        }
    }

    public static void move(final String sourceFilename, final String destFilename) throws IOException {

        copy(sourceFilename, destFilename);
        File sourceFile = new File(sourceFilename);
        if (!sourceFile.delete()) {
            logger.info("Failed to delete " + sourceFile);
        }
    }

    public static File makeDir(final String dirPath) {
        final File directory = new File(dirPath);
        // remove dir if already exists
        if (directory.exists()) {
            deleteDir(directory);
        }
        // create dir
        directory.mkdir();
        return directory;

    }

    public static String getFilenameWithoutExtension(final String filename, final String extension) throws IOException {
        final File file = new File(filename);
        return file.getCanonicalPath().substring(file.getCanonicalPath().lastIndexOf(File.separator) + 1, file.getCanonicalPath().lastIndexOf(extension));
    }

    /**
     * Expands a tar or tar.gz archive into the given directory
     *
     * @param expandDir the target directory
     * @param tarOrTarGzFilename the name of the archive to expand (must be tar or tar.gz)
     * @throws IOException
     */
    public static void explodeTarOrTarGz(final File expandDir,
                                         final String tarOrTarGzFilename) throws IOException {

        final File file = new File(tarOrTarGzFilename);
        final FileInputStream fStream = new FileInputStream(file);

        String filenameWithoutExtension;
        if(tarOrTarGzFilename.endsWith(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION)) {
            filenameWithoutExtension = getFilenameWithoutExtension(tarOrTarGzFilename, ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        } else {
            filenameWithoutExtension = getFilenameWithoutExtension(tarOrTarGzFilename, ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION);
        }

        GZIPInputStream gzipStream = null;
        TarInputStream tin = null;
        TarEntry tarEntry;
        final StringBuffer errorMsg = new StringBuffer();
        try {
            //noinspection IOResourceOpenedButNotSafelyClosed

            if(tarOrTarGzFilename.endsWith(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION)) {
                gzipStream = new GZIPInputStream(fStream);
                tin = new TarInputStream(gzipStream);
            } else {
                tin = new TarInputStream(fStream);
            }

            while ((tarEntry = tin.getNextEntry()) != null) {
                String entryName = tarEntry.getName();
                final File entryFile = new File(entryName);

                if (!tarEntry.isDirectory()) {
                    if (!entryName.startsWith(filenameWithoutExtension)) {
                        errorMsg.append("Archive files should be contained inside a single directory with the same name as the archive.\n");
                    }

                    // extract out just the filename of the entry
                    if (entryFile.getCanonicalPath().lastIndexOf(File.separator) != -1) {
                        entryName = entryFile.getCanonicalPath().substring(entryFile.getCanonicalPath().lastIndexOf(File.separator));
                    }
                    final File destPath = new File(expandDir, entryName);
                    FileOutputStream fout = new FileOutputStream(destPath);
                    try {
                        tin.copyEntryContents(fout);
                    } finally {
                        fout.flush();
                        fout.close();
                        fout = null;
                    }
                } else {
                    if (!entryName.equals(filenameWithoutExtension) && !entryName.equals(filenameWithoutExtension + File.separator)
                            && !entryName.equals(filenameWithoutExtension + "/")) {

                        String t = filenameWithoutExtension + File.separator;
                        errorMsg.append("Archive contains a non-standard directory '").
                                append(entryName).append("'.  Archive files should be contained inside a single directory with the same name as the archive.\n");
                    }
                }
            }
        } finally {
            IOUtils.closeQuietly(tin);
            IOUtils.closeQuietly(gzipStream);
            IOUtils.closeQuietly(fStream);
            if (errorMsg.length() > 0)
                throw new IOException(errorMsg.toString());
        }
    }

    /**
     * Creates compressed file for the given file
     *
     * @param fileNameToBeCompressed File to be compressed path name
     * @param compressedFileName     Compressed file path name
     * @throws IOException
     */
    public static void createCompressedFile(final String fileNameToBeCompressed, final String compressedFileName) throws IOException {
        final List<String> fileNamesList = new ArrayList();
        fileNamesList.add(fileNameToBeCompressed);
        createCompressedFiles(fileNamesList, compressedFileName);
    }

    public static void createCompressedFiles(final List<String> fileNamesToBeCompressed, final String compressedFileName) throws IOException {

        final File compressedFile = new File(compressedFileName);
        for (final String fileNameToBeCompressed : fileNamesToBeCompressed) {
            final File fileToBeCompressed = new File(fileNameToBeCompressed);

            if (!fileToBeCompressed.exists()) {
                throw new IOException("Cache File does not exist: " + fileToBeCompressed.getPath());
            }
        }
        final FileOutputStream outputFileStream = new FileOutputStream(compressedFile);
        final TarOutputStream tarStream = new TarOutputStream(new GZIPOutputStream(outputFileStream));
        try {
            for (final String fileNameToBeCompressed : fileNamesToBeCompressed) {
                final File fileToBeCompressed = new File(fileNameToBeCompressed);
                final String name = fileToBeCompressed.getName();
                final TarEntry tarAdd = new TarEntry(fileToBeCompressed);

                tarStream.setLongFileMode(TarOutputStream.LONGFILE_GNU);
                tarAdd.setModTime(fileToBeCompressed.lastModified());
                tarAdd.setName(name);
                tarStream.putNextEntry(tarAdd);

                FileInputStream inputStream = null;
                byte[] buffer = new byte[1024 * 64];
                try {

                    inputStream = new FileInputStream(fileToBeCompressed);
                    int nRead = inputStream.read(buffer, 0, buffer.length);
                    while (nRead >= 0) {
                        tarStream.write(buffer, 0, nRead);
                        nRead = inputStream.read(buffer, 0, buffer.length);
                    }

                    tarStream.closeEntry();
                } finally {
                    buffer = null;
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException ie) {
                        logger.error("Error closing I/O streams " + ie.toString());
                    }
                }

            }
        } finally {
            if (tarStream != null) {
                tarStream.close();
            }

        }
    }
}
