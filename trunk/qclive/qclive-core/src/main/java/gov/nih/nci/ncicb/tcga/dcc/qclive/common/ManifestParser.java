/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Interface for ManifestParser
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public interface ManifestParser {

    /**
     * Parses the given file.  It expects each line to have an MD5 sum, some whitespace, and then a filename.
     *
     * @param manifest the file to parse
     * @return a map, where keys are filenames and values are MD5 hashes
     * @throws java.io.IOException      if the file is not found or cannot be read
     * @throws java.text.ParseException if the file isn't formatted correctly
     */
    public Map<String, String> parseManifest(File manifest) throws IOException, ParseException;

    /**
     * Adds the given file to the manifest.
     *
     * @param fileToAdd file to add to the manifest
     * @param manifest  the manifest to add to
     * @throws IOException              of there is an error reading or writing the manifest
     * @throws NoSuchAlgorithmException if there is an error getting the MD5 checksum of the added file
     * @throws ParseException           if the given manifest isn't formatted correctly
     */
    public void addFileToManifest(File fileToAdd, File manifest)
            throws IOException, NoSuchAlgorithmException, ParseException;

    /**
     * Adds the given list of file to the manifest.
     *
     * @param filesToAdd list of files to add to the manifest
     * @param manifest   the manifest to add to
     * @throws IOException              of there is an error reading or writing the manifest
     * @throws NoSuchAlgorithmException if there is an error getting the MD5 checksum of the added file
     * @throws ParseException           if the given manifest isn't formatted correctly
     */
    public void addFilesToManifest(List<File> filesToAdd, File manifest)
            throws IOException, NoSuchAlgorithmException, ParseException;

    /**
     * Updates the manifest with the given file lists.
     *
     * @param filesToAdd
     * @param filesToRemove
     * @param manifest
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ParseException
     */
    public void updateManifest(List<File> filesToAdd, List<File> filesToRemove, File manifest)
            throws IOException, NoSuchAlgorithmException, ParseException;
}
