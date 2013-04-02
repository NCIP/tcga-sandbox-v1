/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Class that holds a static method for making a copy of a file.
 *
 * @author Jessica Chen Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class FileCopier {

    /**
     * Copies the contents of a file into a new file in the given directory.  The new file will have the same name as
     * the original file.
     *
     * @param fromFile the file to copy
     * @param deployDirectory the directory in which to copy the file -- if this is not a directory, the new file will
     * be created with this name
     *
     * @return the File that was created
     *
     * @throws IOException if there are I/O errors during reading or writing
     */
    public static File copy(final File fromFile, final File deployDirectory) throws IOException {
        // if not a directory, then just use deployDirectory as the filename for the copy
        File destination = deployDirectory;
        if (deployDirectory.isDirectory()) {
            // otherwise use original file's name
            destination = new File(deployDirectory, fromFile.getName());
        }

        FileInputStream fileInputStream = null;
        FileChannel sourceChannel = null;
        FileOutputStream fileOutputStream = null;
        FileChannel destinationChannel = null;

        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileInputStream = new FileInputStream(fromFile);
            //noinspection ChannelOpenedButNotSafelyClosed
            sourceChannel = fileInputStream.getChannel();

            //noinspection IOResourceOpenedButNotSafelyClosed
            fileOutputStream = new FileOutputStream(destination);
            //noinspection ChannelOpenedButNotSafelyClosed
            destinationChannel = fileOutputStream.getChannel();
            final int maxCount = (64 * 1024 * 1024) - (32 * 1024);
            final long size = sourceChannel.size();
            long position = 0;
            while (position < size) {
                position += sourceChannel.transferTo(position, maxCount, destinationChannel);
            }
        } catch (IOException ie) {
            throw new IOException("Failed to copy " + fromFile + " to " + deployDirectory + ".\n Error Details: " + ie.toString());
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(sourceChannel);
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(destinationChannel);
        }

        return destination;
    }

    public static File move(final File fromFile, final File deployDirectory) throws IOException {
        File destination = copy(fromFile, deployDirectory);
        if (!fromFile.delete()) {
            throw new IOException("Failed to delete " + fromFile);
        }
        return destination;
    }

    /**
     * Copy the given source file or directory to the given destination, keeping the same creation date as the source.
     *
     * @param sourceFileOrDirectory      the source file or directory
     * @param destinationFileOrDirectory the destination file or directory
     * @throws IOException if an IO error occurs during the copy
     */
    public static void copyFileOrDirectory(final File sourceFileOrDirectory, final File destinationFileOrDirectory) throws IOException {

        final File parentDirectory = destinationFileOrDirectory.getParentFile();
        if (!parentDirectory.exists()) {
            boolean success = parentDirectory.mkdirs();

            if (!success) {
                throw new IOException("Directory '" + parentDirectory + "' was not successfully created.");
            }
        }

        if (sourceFileOrDirectory.isDirectory()) {
            FileUtils.copyDirectory(sourceFileOrDirectory, destinationFileOrDirectory);
        } else {
            FileUtils.copyFile(sourceFileOrDirectory, destinationFileOrDirectory);
        }
    }
}
